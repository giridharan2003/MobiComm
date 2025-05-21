package com.recharge.app.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recharge.app.exception.PlanNotFoundException;
import com.recharge.app.model.Category;
import com.recharge.app.model.Plan;
import com.recharge.app.model.User;
import com.recharge.app.repository.UserRepository;
import com.recharge.app.security.JwtUtil;
import com.recharge.app.service.PlanService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/plans")
@CrossOrigin("*")
public class PlanController {

	@Autowired
	private PlanService planService;
	
	@Autowired
    private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/{planId}")
	public Plan getPlanId(@PathVariable long planId) {
		return planService.getPlanId(planId);
	}

	@GetMapping("/active")
	public List<Plan> getAllActivePlans() {
		return planService.getAllActivePlans();
	}

	@GetMapping("/inactive")
	public List<Plan> getAllInactivePlans() {
		return planService.getAllInactivePlans();
	}

	@GetMapping("/badge/{badgeName}")
	public ResponseEntity<List<Plan>> getPlansByBadgeName(@PathVariable String badgeName) {
		List<Plan> plans = planService.getActivePlansByBadgeName(badgeName);
		if (plans.isEmpty()) {
			throw new PlanNotFoundException("Plans not found for badge: " + badgeName);
		}
		return ResponseEntity.ok(plans);
	}

	@GetMapping("/ott/{ottName}")
	public ResponseEntity<List<Plan>> getPlansByOttName(@PathVariable String ottName) {
		List<Plan> plans = planService.getPlansByOttName(ottName);
		if (plans.isEmpty()) {
			throw new PlanNotFoundException("Plans not found for OTT: " + ottName);
		}
		return ResponseEntity.ok(plans);
	}

	@GetMapping("/price/{price}")
	public ResponseEntity<List<Plan>> getPlansByPrice(@PathVariable double price) {
		List<Plan> plans = planService.getPlansByPrice(price);
		if (plans.isEmpty()) {
			throw new PlanNotFoundException("Plans not found for price: " + price);
		}
		return ResponseEntity.ok(plans);
	}

	@GetMapping("/categories")
	public ResponseEntity<Set<Category>> getAvailableCategories() {
		List<Plan> plans = planService.getAllActivePlans();
		Set<Category> categories = plans.stream().map(Plan::getCategory).collect(Collectors.toSet());
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/category/{categoryName}")
	public ResponseEntity<List<Plan>> getPlansByCategoryName(@PathVariable String categoryName) {
		List<Plan> plans = planService.getActivePlansByCategoryName(categoryName);
		if (plans.isEmpty()) {
			throw new PlanNotFoundException("Plans not found for category: " + categoryName);
		}
		return ResponseEntity.ok(plans);
	}
	
	@GetMapping("/user-details")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token is missing or invalid");
        }

        String token = authHeader.substring(7);

        if (jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(401).body("Token is expired");
        }

        String phoneNumber = jwtUtil.extractPhoneNumber(token);
        
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        return ResponseEntity.ok(user);
    }

}
