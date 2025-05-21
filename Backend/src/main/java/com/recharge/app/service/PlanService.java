package com.recharge.app.service;

import com.recharge.app.exception.PlanNotFoundException;
import com.recharge.app.model.Category;
import com.recharge.app.model.Plan;
import com.recharge.app.model.enums.Status;
import com.recharge.app.repository.CategoryRepository;
import com.recharge.app.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	public Plan getPlanId(Long planId) {
		return planRepository.findByPlanId(planId);
	}

	public List<Plan> getAllActivePlans() {
		return planRepository.findByStatus(Status.ACTIVE);
	}

	public List<Plan> getAllInactivePlans() {
		return planRepository.findByStatus(Status.INACTIVE);
	}

	public List<Plan> getActivePlansByBadgeName(String badgeName) {
		return planRepository.findByBadgeBadgeNameAndStatus(badgeName, Status.ACTIVE);
	}

	public List<Plan> getPlansByOttName(String ottName) {
		return planRepository.findByOtt(ottName);
	}

	public List<Plan> getPlansByPrice(double price) {
		return planRepository.findByPrice(price);
	}

	public List<Plan> getActivePlansByCategoryName(String categoryName) {
		return planRepository.findByCategoryCategoryNameAndStatus(categoryName, Status.ACTIVE);
	}

	// Update Plan Status
	public Plan updatePlanStatus(Long planId, Status status) {
		Plan existingPlan = planRepository.findByPlanId(planId);
		if (existingPlan == null) {
			throw new PlanNotFoundException("Plan not found with id: " + planId);
		}
		existingPlan.setStatus(status);
		return planRepository.save(existingPlan);
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	public String getEnabledFields(long categoryId) {
		Category category = categoryRepository.findById(categoryId).orElse(null);
		return (category != null) ? category.getEnabledFields() : "";
	}

	public Plan addPlan(Plan plan, Long categoryId) {
		Category category = categoryRepository.findByCategoryId(categoryId);

		plan.setCategory(category);
		plan.setBadge(null);

		return planRepository.save(plan);
	}
}