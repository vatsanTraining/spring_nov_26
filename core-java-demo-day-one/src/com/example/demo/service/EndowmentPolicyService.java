package com.example.demo.service;

import com.example.demo.exception.PolicyLapsedException;
import com.example.demo.model.EndowmentPolicy;

public class EndowmentPolicyService extends AbstractPolicyService<EndowmentPolicy> {

	@Override
    public double calculateAnnualPremium(EndowmentPolicy policy) {
        return (policy.getSumAssured() * 0.04) + 1200.0;
    }

    @Override
    public double calculateSettlementAmount(EndowmentPolicy policy) throws PolicyLapsedException {
        validateActiveForPayout(policy);
        return policy.getSumAssured() + policy.getAccruedBonus();
    }
}
