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
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long categoryId;
    
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "enabled_fields", columnDefinition = "TEXT")
    private String enabledFields;
}