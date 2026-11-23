package com.example.demo;

public class CurrencyConverter implements Converter<Double> {

	@Override
	public Double convert(Double t) {
		return  t* 90.00;
	}

}
