package com.example.demo.service;

import com.example.demo.enums.PolicyStatus;
import com.example.demo.exception.InvalidPolicyTermException;
import com.example.demo.exception.PolicyLapsedException;
import com.example.demo.model.TermLifePolicy;

public class TermLifeService extends AbstractPolicyService<TermLifePolicy> {


	
	    @Override
	    public double calculateAnnualPremium(TermLifePolicy policy) {
	        validatePolicyEligibility(policy);

	        // 1. Base mortality rate driven by entryAge
	        double ratePerThousand;
	        int age = policy.getEntryAge();
	        if (age < 30) {
	            ratePerThousand = 1.20;
	        } else if (age <= 45) {
	            ratePerThousand = 2.50;
	        } else {
	            ratePerThousand = 5.00;
	        }

	        double annualPremium = (policy.getSumAssured() / 1000.0) * ratePerThousand;

	        // 2. Term duration loading driven by coverageTermYears
	        if (policy.getCoverageTermYears() > 25) {
	            annualPremium *= 1.20; // 20% loading for long-horizon risk
	        } else if (policy.getCoverageTermYears() > 15) {
	            annualPremium *= 1.10; // 10% loading for medium-horizon risk
	        }

	        // 3. Return of Premium (TROP) adjustment driven by returnOfPremium
	        if (policy.isReturnOfPremium()) {
	            annualPremium *= 1.50; // TROP plans carry ~50% higher premium
	        }

	        return Math.round(annualPremium * 100.0) / 100.0;
	    }

	    @Override
	    public double calculateSettlementAmount(TermLifePolicy policy) throws PolicyLapsedException {
	        // Inherited check from AbstractPolicyService
	        validateActiveForPayout(policy);

	        // Maturity payout depends on returnOfPremium flag
	        if (policy.getStatus() == PolicyStatus.MATURED) {
	            if (policy.isReturnOfPremium()) {
	                return calculateAnnualPremium(policy) * policy.getCoverageTermYears();
	            }
	            return 0.0; // Pure term expires with zero maturity value
	        }

	        // Death claim payout for ACTIVE / GRACE_PERIOD policies
	        return policy.getSumAssured();
	    }

	    // Overloaded method to demonstrate Compile-Time Polymorphism
	    public double calculateInstallmentPremium(TermLifePolicy policy, int paymentsPerYear) {
	        double annual = calculateAnnualPremium(policy);
	        // Add a 3% modal loading factor for non-annual installments
	        double modalFactor = (paymentsPerYear > 1) ? 1.03 : 1.0;
	        return Math.round(((annual * modalFactor) / paymentsPerYear) * 100.0) / 100.0;
	    }

	    private void validatePolicyEligibility(TermLifePolicy policy) {
	        if (policy.getEntryAge() < 18 || policy.getEntryAge() > 65) {
	            throw new InvalidPolicyTermException(
	                "Entry age " + policy.getEntryAge() + " is outside eligible bounds (18–65)."
	            );
	        }
	        if ((policy.getEntryAge() + policy.getCoverageTermYears()) > 80) {
	            throw new InvalidPolicyTermException(
	                "Coverage maturity age (" + (policy.getEntryAge() + policy.getCoverageTermYears()) +
	                ") cannot exceed 80 years."
	            );
	        }
	    }
	}


