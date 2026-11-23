package com.example.demo;

public class Agent {

	private byte id;
	private String name;
	private double commission;
	
	public Agent() {
		super();
	}

	// this()  overloaded constructor
	// this.   refer to varibale
	
	public Agent(String name, double commission) {
		this((byte)0,name,commission);
		this.name = name;
		this.commission = commission;
	}


	public Agent(byte id, String name, double commission) {
		super();
		if(id<1 || id>127) {
			throw new RuntimeException("Invalid value -should be 1 to 127");
		} else {
		this.id = id;
		}
		this.name = name;
		this.commission = commission;
	}

	public byte getId() {
		return id;
	}

	public void setId(byte id) throws Exception {
		
		
		if(id<1 || id>127) {
			throw new Exception("Invalid value -should be 1 to 127");
		} else {
		this.id = id;
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	@Override
	public String toString() {
		return this.id +","+this.name+","+this.commission;
		
	}

	
	
	
}
