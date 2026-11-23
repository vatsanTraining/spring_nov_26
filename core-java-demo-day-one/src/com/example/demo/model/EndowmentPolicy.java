package com.example.demo.model;

import com.example.demo.enums.PolicyStatus;

public class EndowmentPolicy extends Policy {

	private double accruedBonus;

	 public EndowmentPolicy(String policyNumber, String holderName, double sumAssured,
	                        PolicyStatus status, double accruedBonus) {
	     super(policyNumber, holderName, sumAssured, status);
	     this.accruedBonus = accruedBonus;
	 }

	 public double getAccruedBonus() { return accruedBonus; }
	 public void setAccruedBonus(double accruedBonus) { this.accruedBonus = accruedBonus; }

}
