package com.example.demo.model;

import com.example.demo.enums.PolicyStatus;

public class TermLifePolicy extends Policy {

	
	private int entryAge;
    private int coverageTermYears;
    private boolean returnOfPremium;
    
    
	public TermLifePolicy(String policyNumber, String holderName, double sumAssured, PolicyStatus status, int entryAge,
			int coverageTermYears, boolean returnOfPremium) {
		super(policyNumber, holderName, sumAssured, status);
		this.entryAge = entryAge;
		this.coverageTermYears = coverageTermYears;
		this.returnOfPremium = returnOfPremium;
	}


	public int getEntryAge() {
		return entryAge;
	}


	public void setEntryAge(int entryAge) {
		this.entryAge = entryAge;
	}


	public int getCoverageTermYears() {
		return coverageTermYears;
	}


	public void setCoverageTermYears(int coverageTermYears) {
		this.coverageTermYears = coverageTermYears;
	}


	public boolean isReturnOfPremium() {
		return returnOfPremium;
	}


	public void setReturnOfPremium(boolean returnOfPremium) {
		this.returnOfPremium = returnOfPremium;
	}
    
	


   
}
