package com.recharge.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recharge.app.model.RechargeHistory;
import com.recharge.app.model.User;
import com.recharge.app.repository.UserRepository;
import com.recharge.app.security.JwtUtil;
import com.recharge.app.service.RechargeHistoryService;
import com.recharge.app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RechargeHistoryService rechargeHistoryService;

	// Get suggested random numbers
	@GetMapping("/register/suggest-numbers")
	public List<String> getSuggestedNumbers() {
		return userService.generateRandomMobileNumbers();
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
		return userService.registerUser(user);
	}

	@PostMapping("/login/{phoneNumber}")
	public ResponseEntity<Map<String, String>> validateNumber(@PathVariable String phoneNumber) {
		return userService.loginUser(phoneNumber);
	}

	@PostMapping("/admin/register")
	public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody User admin) {
		return userService.registerAdmin(admin);
	}

	@PostMapping("/admin/login")
	public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Map<String, String> loginRequest) {
		String username = loginRequest.get("username");
		String password = loginRequest.get("password");
		return userService.adminLogin(username, password);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
		return userService.logout(token);
	}

	@GetMapping("/validate-token")
	public ResponseEntity<?> validateToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(401).body("Token is missing or invalid");
		}

		String token = authHeader.substring(7);

		String phoneNumber = jwtUtil.extractPhoneNumber(token);

		if (jwtUtil.isTokenExpired(token) || !userRepository.existsByPhoneNumber(phoneNumber)) {
			return ResponseEntity.status(401).body("Token is expired");
		}

		return ResponseEntity.ok("Token is valid");
	}

	// Update user profile (only email & DOB)
	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUserProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
		User user = userService.updateUserProfile(userId, updatedUser);
		return ResponseEntity.ok(user);
	}

	// Get recharge history of a user
	@GetMapping("/{userId}/recharge-history")
	public ResponseEntity<List<RechargeHistory>> getUserRechargeHistory(@PathVariable Long userId) {
		List<RechargeHistory> history = rechargeHistoryService.getRechargeHistoryByUserId(userId);
		return ResponseEntity.ok(history);
	}

}
