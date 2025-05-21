package com.recharge.app.repository;

import com.recharge.app.model.User;
import com.recharge.app.model.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Optional<User> findByPhoneNumber(String phoneNumber);

	boolean existsByEmailAndRole(String email, Role role);

	boolean existsByUsernameAndRole(String username, Role role);

	boolean existsByPhoneNumberAndRole(String phoneNumber, Role role);

	Optional<User> findByUsername(String username);
}
