package com.recharge.app.service;

import com.recharge.app.exception.AccessDeniedException;
import com.recharge.app.exception.EmailValidationException;
import com.recharge.app.exception.InvalidLoginException;
import com.recharge.app.exception.MobileNumberValidationException;
import com.recharge.app.exception.NotFoundException;
import com.recharge.app.exception.UsernameValidationException;
import com.recharge.app.model.Log;
import com.recharge.app.model.User;
import com.recharge.app.model.enums.Role;
import com.recharge.app.repository.LogRepository;
import com.recharge.app.repository.UserRepository;
import com.recharge.app.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private TwilioService twilioService;

	private final Random random = new Random();
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// Register User
	public ResponseEntity<Map<String, String>> registerUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new EmailValidationException("Email already registered");
		}

		if (!isValidMobileNumber(user.getPhoneNumber())) {
			throw new MobileNumberValidationException("Invalid mobile number");
		}

		if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
			throw new MobileNumberValidationException("Phone number already in use. Please select another.");
		}

		user.setRole(Role.USER);
		userRepository.save(user);

		return ResponseEntity.ok(Map.of("message", "User Successfully Registered"));
	}

	// User Login
	public ResponseEntity<Map<String, String>> loginUser(String phoneNumber) {
		if (!isValidMobileNumber(phoneNumber)) {
			throw new MobileNumberValidationException("Invalid mobile number");
		}

		User user = userRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(() -> new NotFoundException("User not found"));

		String role = user.getRole().name();
		String token = jwtUtil.generateToken(phoneNumber, role);

		Optional<Log> lastLog = logRepository.findTopByUserOrderByLogIdDesc(user);
		lastLog.ifPresent(log -> {
			if (log.getLogOut() == null) {
				log.setLogOut(new Timestamp(System.currentTimeMillis()));
				logRepository.save(log);
			}
		});
		
		String message = user.getName() + " You SuccessFully Login in MobiComm";
		
		twilioService.sendSms("+91" + phoneNumber, message);

		// Save new login log
		Log log = new Log();
		log.setUser(user);
		logRepository.save(log);

		return ResponseEntity.ok(Map.of("message", "Login Successful", "token", token));
	}

	// Generate 6 Unique Random Mobile Numbers
	public List<String> generateRandomMobileNumbers() {
		Set<String> numbers = new HashSet<>();

		while (numbers.size() < 6) {
			String number = (6 + random.nextInt(4)) + String.valueOf(100000000 + random.nextInt(900000000));

			if (!userRepository.existsByPhoneNumber(number)) {
				numbers.add(number);
			}
		}
		return new ArrayList<>(numbers);
	}

	// Validate Mobile Number
	public boolean isValidMobileNumber(String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches("^[6-9]\\d{9}$");
	}

	// Register Admin
	public ResponseEntity<Map<String, String>> registerAdmin(User admin) {
		if (userRepository.existsByUsernameAndRole(admin.getUsername(), Role.ADMIN)) {
			throw new UsernameValidationException("Username already taken by another Admin");
		}

		if (userRepository.existsByEmailAndRole(admin.getEmail(), Role.ADMIN)) {
			throw new EmailValidationException("Email already registered in Admin circle");
		}

		if (userRepository.existsByPhoneNumberAndRole(admin.getPhoneNumber(), Role.ADMIN)) {
			throw new MobileNumberValidationException("Phone number already in use by another Admin");
		}

		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		admin.setRole(Role.ADMIN);
		admin.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		userRepository.save(admin);

		return ResponseEntity.ok(Map.of("message", "Admin Registered Successfully"));
	}

	// Admin Login (Username & Password)
	public ResponseEntity<Map<String, String>> adminLogin(String username, String password) {
		User admin = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Admin user not found"));

		if (!admin.getRole().equals(Role.ADMIN)) {
			throw new AccessDeniedException("Access Denied: Not an Admin");
		}

		if (!passwordEncoder.matches(password, admin.getPassword())) {
			throw new InvalidLoginException("Invalid Password");
		}

		// Generate JWT Token for Admin
		String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");
		
		Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getRole() == Role.ADMIN) {
            User user = userOpt.get();
            Log log = new Log();
            log.setUser(user);
            log.setLogIn(new Timestamp(System.currentTimeMillis())); // Store login time
            logRepository.save(log);
        }

		return ResponseEntity.ok(Map.of("message", "Login Successful", "token", token));
	}

	public ResponseEntity<String> logout(String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body("Invalid Token");
		}

		token = token.substring(7); // Remove "Bearer " prefix
		String phoneNumber = jwtUtil.extractPhoneNumber(token);

		if (phoneNumber != null) {
			userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
				logRepository.findTopByUserOrderByLogIdDesc(user).ifPresent(log -> {
					if (log.getLogOut() == null) {
						log.setLogOut(new Timestamp(System.currentTimeMillis())); // Set logout time
						logRepository.save(log);
					}
				});
			});

			// Invalidate authentication in the security context
			SecurityContextHolder.clearContext();
			return ResponseEntity.ok("Logout successful");
		}

		return ResponseEntity.badRequest().body("Invalid Token");
	}
	
	
	public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User updateUserProfile(Long userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setEmail(updatedUser.getEmail());
            user.setDob(updatedUser.getDob());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
