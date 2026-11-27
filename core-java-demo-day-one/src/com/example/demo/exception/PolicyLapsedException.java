package com.example.demo.exception;

public class PolicyLapsedException extends Exception {

	    private static final long serialVersionUID = 1L;

		public PolicyLapsedException(String policyNumber) {
	        super("Calculation aborted: Policy " + policyNumber + " is LAPSED.");
	    }
	}


