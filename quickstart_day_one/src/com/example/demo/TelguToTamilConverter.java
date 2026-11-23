package com.example.demo;

public class TelguToTamilConverter implements Converter<String> {

	@Override
	public String convert(String key) {

		switch (key) {
		case "neelu": 
			return  "thanni";
		
		case "beeyam":
			return "arisi";
		default:
			throw new IllegalArgumentException("Unexpected value: " + key);
		}
		
		
	}

}
