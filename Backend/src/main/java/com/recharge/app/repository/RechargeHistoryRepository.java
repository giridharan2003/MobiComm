package com.recharge.app.repository;

import com.recharge.app.model.RechargeHistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RechargeHistoryRepository extends JpaRepository<RechargeHistory, Long> {

	List<RechargeHistory> findByUserUserId(Long userId);
}
