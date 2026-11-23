package com.example.demo.exception;

public class PolicyLapsedException extends Exception {

	    public PolicyLapsedException(String policyNumber) {
	        super("Calculation aborted: Policy " + policyNumber + " is LAPSED.");
	    }
	}


