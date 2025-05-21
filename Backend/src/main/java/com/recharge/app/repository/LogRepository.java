package com.recharge.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recharge.app.model.Log;
import com.recharge.app.model.User;

import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    
    Optional<Log> findTopByUserOrderByLogIdDesc(User user);
}
