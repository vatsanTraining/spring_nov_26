package com.example.demo;
import com.example.demo.service.*;
import com.example.demo.enums.PolicyStatus;
import com.example.demo.exception.InvalidPolicyTermException;
import com.example.demo.exception.PolicyLapsedException;
import com.example.demo.model.EndowmentPolicy;
import com.example.demo.model.Policy;
import com.example.demo.model.TermLifePolicy;
import com.example.demo.processor.*;
import java.util.List;

public class Application {

	public static void main(String[] args) {
	
		        TermLifePolicy pureTerm = new TermLifePolicy(
		                "TL-1001", "Arjun Mehta", 5_000_000.0,
		                PolicyStatus.ACTIVE, 32, 20, false
		        );

		        TermLifePolicy tropTerm = new TermLifePolicy(
		                "TL-1002", "Priya Nair", 3_000_000.0,
		                PolicyStatus.MATURED, 42, 15, true
		        );

		        EndowmentPolicy endowment = new EndowmentPolicy(
		                "EN-2001", "Karthik Iyer", 1_000_000.0,
		                PolicyStatus.ACTIVE, 250_000.0
		        );

		        TermLifePolicy lapsedPolicy = new TermLifePolicy(
		                "TL-1003", "Ramesh Rao", 2_000_000.0,
		                PolicyStatus.LAPSED, 38, 25, false
		        );

		        // 2. Runtime Polymorphism: Process mixed policies uniformly in a batch
		        System.out.println("=== 1. BATCH BILLING (POLYMORPHISM) ===");
		        PolicyBillingProcessor billingProcessor = new PolicyBillingProcessor();
		        List<Policy> portfolio = List.of(pureTerm, tropTerm, endowment, lapsedPolicy);
		        billingProcessor.runBatchBilling(portfolio);

		        // 3. Compile-Time Polymorphism (Method Overloading) & Service Calculations
		        System.out.println("\n=== 2. INSTALLMENT & MATURITY SETTLEMENTS ===");
		        TermLifeService termService = new TermLifeService();
		        EndowmentPolicyService endowmentService = new EndowmentPolicyService();

		        double quarterlyInstallment = termService.calculateInstallmentPremium(pureTerm, 4);
		        System.out.printf("Policy [%s] Quarterly Installment: %.2f%n",
		                pureTerm.getPolicyNumber(), quarterlyInstallment);

		        try {
		            double tropPayout = termService.calculateSettlementAmount(tropTerm);
		            System.out.printf("Policy [%s] (TROP Matured) Settlement: %.2f%n",
		                    tropTerm.getPolicyNumber(), tropPayout);

		            double endowmentPayout = endowmentService.calculateSettlementAmount(endowment);
		            System.out.printf("Policy [%s] (Endowment) Settlement: %.2f%n",
		                    endowment.getPolicyNumber(), endowmentPayout);
		        } catch (PolicyLapsedException e) {
		            System.err.println("Unexpected error: " + e.getMessage());
		        }

		        		        System.out.println("\n=== 3. EXCEPTION HANDLING DEMO ===");
		        try {
		            System.out.println("Attempting settlement for lapsed policy " + lapsedPolicy.getPolicyNumber() + "...");
		            termService.calculateSettlementAmount(lapsedPolicy);
		        } catch (PolicyLapsedException e) {
		            System.out.println("Caught Checked Exception -> " + e.getMessage());
		        }

		        try {
		            System.out.println("Attempting premium calculation for ineligible age/term...");
		            TermLifePolicy invalidTerm = new TermLifePolicy(
		                    "TL-9999", "Vikram Sen", 1_000_000.0,
		                    PolicyStatus.ACTIVE, 60, 25, false // 60 + 25 = 85 > 80 max maturity age
		            );
		            termService.calculateAnnualPremium(invalidTerm);
		        } catch (InvalidPolicyTermException e) {
		            System.out.println("Caught Unchecked Exception -> " + e.getMessage());
		        }
		    }
	

	}


