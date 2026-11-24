package com.example.demo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalDemo {

	    public static void main(String[] args) {

	    	BigDecimal amount = new BigDecimal("87456123490.126");

	        BigDecimal twoDecimals = amount.setScale(2, RoundingMode.HALF_UP); 
	        
	        System.out.println("Two Decimals =>"+ twoDecimals);
	        
	  
	        BigDecimal fourDecimals = twoDecimals.setScale(4, RoundingMode.UNNECESSARY); 
	  
	        System.out.println("Four Decimals =>"+ fourDecimals);
	  	  
	        
	        
		     BigDecimal currency = amount.setScale(2, RoundingMode.HALF_EVEN); 
		   
		     System.out.println(currency.toPlainString()); 

		     
		    	BigDecimal scivalue = new BigDecimal("0.000012347");

		    	       
		     BigDecimal constrained = scivalue.round(new MathContext(4, RoundingMode.HALF_UP));
		     
		     System.out.println("Constrained ===>"+constrained.toPlainString());

		     
		        int decimal = 13;
		        int binaryNum = 0b1101;   
		        int octalNum = 015;       
		        int hexNum = 0xD;         

		        System.out.println(decimal);
		        System.out.println(binaryNum); 
		        System.out.println(octalNum);  
		        System.out.println(hexNum);    
		        
		       System.out.println(Integer.toBinaryString(decimal));
		   
	    }
	}



