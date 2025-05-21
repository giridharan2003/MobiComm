package com.recharge.app.repository;

import com.recharge.app.model.Plan;
import com.recharge.app.model.enums.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

	Plan findByPlanId(Long planId);

	List<Plan> findByStatus(Status active);

	List<Plan> findByOtt(String ottName);

	List<Plan> findByPrice(double price);

	List<Plan> findByBadgeBadgeNameAndStatus(String badgeName, Status status);

	List<Plan> findByBadgeBadgeName(String badgeName);

	List<Plan> findByCategoryCategoryId(Long categoryId);

	List<Plan> findByCategoryCategoryName(String categoryName);

	List<Plan> findByCategoryCategoryNameAndStatus(String categoryName, Status status);

	@Query("SELECT COUNT(p) FROM Plan p WHERE p.category.id = :categoryId")
	Long countPlansByCategoryId(Long categoryId);

}