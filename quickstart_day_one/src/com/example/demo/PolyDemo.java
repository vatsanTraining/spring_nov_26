package com.example.demo;

public class PolyDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Converter<Double> inrToUsd = new CurrencyConverter();
		
		 System.out.println(inrToUsd.convert(100.00));
		 
		 
		 
		Converter<String> teluToTamil = new TelguToTamilConverter();
		
		System.out.println(teluToTamil.convert("beeyam"));
		
		
	}

}
