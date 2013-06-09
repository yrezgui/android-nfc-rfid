package com.example.rfid;

public class Property {
	public static enum Type {
		String,
		Number,
		Boolean,
		Date;  
	};

	private String name;
	private Type type;
	private boolean required;

	public Property(String name, Property.Type type, boolean required){
		this.name = name;
		this.type = type;
		this.required = required;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Type getType() {
		return this.type;
	}

	public boolean isRequired() {
		return required;
	}
}
