package com.example.demo;

//public class CurrencyConverter implements Converter<Double> {
//
//	@Override
//	public Double convert(Double t) {
//		return  t* 90.00;
//	}
//
//}
//Converter<Double> inrToUsd = new CurrencyConverter();
//
//System.out.println(inrToUsd.convert(100.00));


public class JavaEightDemo {

	public static void main(String[] args) {

		
		Converter<Double> curConv = (t) -> t*90.00;
		
		System.out.println(curConv.convert(100.00));
	}

}
