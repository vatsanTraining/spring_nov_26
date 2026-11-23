package com.example.demo.model;

import com.example.demo.enums.PolicyStatus;

public abstract class Policy {
 private String policyNumber;
 private String holderName;
 private double sumAssured;
 private PolicyStatus status;

 protected Policy(String policyNumber, String holderName, double sumAssured, PolicyStatus status) {
     this.policyNumber = policyNumber;
     this.holderName = holderName;
     setSumAssured(sumAssured); // Encapsulated validation
     this.status = status;
 }

 public void setSumAssured(double sumAssured) {
     if (sumAssured <= 0) {
         throw new IllegalArgumentException("Sum assured must be greater than zero.");
     }
     this.sumAssured = sumAssured;
 }

 public String getPolicyNumber() { return policyNumber; }
 public String getHolderName() { return holderName; }
 public double getSumAssured() { return sumAssured; }
 public PolicyStatus getStatus() { return status; }
 public void setStatus(PolicyStatus status) { this.status = status; }
}

