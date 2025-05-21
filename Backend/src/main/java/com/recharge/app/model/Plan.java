package com.recharge.app.model;

import com.recharge.app.model.enums.Calls;
import com.recharge.app.model.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plan_id")
	private Integer planId;

	@Column(name = "plan_code", unique = true, updatable = false)
	private String planCode;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "badge_id")
	private Badge badge;

	@Column(name = "price", nullable = false)
	private Double price;

	@Column(name = "validity_days")
	private Integer validityDays;

	@Column(name = "data_limit")
	private String dataLimit;

	@Enumerated(EnumType.STRING)
	@Column(name = "calls")
	private Calls call;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.ACTIVE;

	@Column(name = "sms")
	private Integer sms;

	@Column(name = "additional_features")
	private String additionalFeatures;

	@Column(name = "ott")
	private String ott;
}
