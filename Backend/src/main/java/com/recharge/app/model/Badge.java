package com.recharge.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "badge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Badge {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
	private long badgeId;
	
	@Column(name = "badge_name", nullable = false, unique = true)
	private String badgeName;
}
