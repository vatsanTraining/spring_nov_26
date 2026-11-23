package com.example.demo;

public class InsuranceAgent extends Agent {

	private String category;

	
	public InsuranceAgent() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public InsuranceAgent(byte id, String name, double commission, String category) {
		super(id, name, commission);
		this.category = category;
	}


	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
