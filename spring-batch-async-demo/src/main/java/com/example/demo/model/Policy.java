package com.example.demo.model;

import com.example.demo.enums.PolicyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "policy_2026")
public class Policy {

	
	
	@Id
	@Column(name = "policy_number")
	private long policyNumber;
	
	@Column(name = "policy_holder")
	private String policyHolder;
	
	@Column(name = "policy_type")
	@Enumerated(EnumType.STRING)
	private PolicyType policyType;
	
	@Column(name = "premium_amount")
	private double premiumAmount;
	
	public Policy() {
		super();
	
	}

	public Policy(long policyNumber, String policyHolder, PolicyType policyType, double premiumAmount) {
		super();
		this.policyNumber = policyNumber;
		this.policyHolder = policyHolder;
		this.policyType = policyType;
		this.premiumAmount = premiumAmount;
	}

	public long getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(long policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getPolicyHolder() {
		return policyHolder;
	}

	public void setPolicyHolder(String policyHolder) {
		this.policyHolder = policyHolder;
	}

	public PolicyType getPolicyType() {
		return policyType;
	}

	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}

	public double getPremiumAmount() {
		return premiumAmount;
	}

	public void setPremiumAmount(double premiumAmount) {
		this.premiumAmount = premiumAmount;
	}

	@Override
	public String toString() {
		return this.policyNumber + ","+this.policyHolder+","+this.premiumAmount+","+this.policyType;
	}
	
	
}
