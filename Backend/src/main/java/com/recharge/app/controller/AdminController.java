package com.recharge.app.controller;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recharge.app.model.Category;
import com.recharge.app.model.Plan;
import com.recharge.app.model.enums.Calls;
import com.recharge.app.model.enums.Status;
import com.recharge.app.service.PlanService;

@RestController
@CrossOrigin("*")
@RequestMapping("/admin/plans")
public class AdminController {

    @Autowired
    private PlanService planService;
    
    @PutMapping("/active/{planId}")
    public ResponseEntity<Plan> activatePlan(@PathVariable Long planId) {
        Plan updatedPlan = planService.updatePlanStatus(planId, Status.ACTIVE);
        return ResponseEntity.ok(updatedPlan);
    }

    @PutMapping("/inactive/{planId}")
    public ResponseEntity<Plan> deactivatePlan(@PathVariable Long planId) {
        Plan updatedPlan = planService.updatePlanStatus(planId, Status.INACTIVE);
        return ResponseEntity.ok(updatedPlan);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = planService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @PostMapping("/newCategory")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        return ResponseEntity.ok(planService.saveCategory(category));
    }

    @GetMapping("/categories/{categoryId}/enabled-fields")
    public ResponseEntity<String> getEnabledFieldsForCategory(@PathVariable long categoryId) {
        String enabledFields = planService.getEnabledFields(categoryId);
        return ResponseEntity.ok(enabledFields);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addPlan(@RequestBody Map<String, Object> requestBody) {
        try {
            Long categoryId = Long.parseLong(requestBody.get("categoryId").toString());

            Plan plan = new Plan();
            plan.setPrice(requestBody.get("price") != null ? Double.valueOf(requestBody.get("price").toString()) : null);
            plan.setDataLimit(requestBody.get("dataLimit") != null ? requestBody.get("dataLimit").toString() : null);
            plan.setValidityDays(requestBody.get("validityDays") != null ? Integer.parseInt(requestBody.get("validityDays").toString()) : null);
            plan.setCall(requestBody.get("call") != null ? Calls.valueOf(requestBody.get("call").toString()) : null);
            plan.setSms(requestBody.get("sms") != null ? Integer.parseInt(requestBody.get("sms").toString()) : null);
            plan.setOtt(requestBody.get("ott") != null ? requestBody.get("ott").toString() : null);
            plan.setStatus(requestBody.get("status") != null ? Status.valueOf(requestBody.get("status").toString()) : Status.ACTIVE);
            plan.setAdditionalFeatures(requestBody.get("additionalFeatures") != null ? requestBody.get("additionalFeatures").toString() : null);

            Plan savedPlan = planService.addPlan(plan, categoryId);
            return ResponseEntity.ok(savedPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Error adding plan: " + e.getMessage());
        }
    }

}