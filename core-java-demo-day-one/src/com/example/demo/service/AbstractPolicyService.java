package com.example.demo.service;

import com.example.demo.enums.PolicyStatus;
import com.example.demo.exception.PolicyLapsedException;
import com.example.demo.model.Policy;

public abstract class AbstractPolicyService<T extends Policy> {

	protected void validateActiveForPayout(T policy) throws PolicyLapsedException {
        if (policy.getStatus() == PolicyStatus.LAPSED) {
            throw new PolicyLapsedException(policy.getPolicyNumber());
        }
    }

    public abstract double calculateAnnualPremium(T policy);
    public abstract double calculateSettlementAmount(T policy) throws PolicyLapsedException;
}
