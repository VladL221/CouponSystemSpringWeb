package com.example.demo.beans;
//logger descprition enum wanted to implement remove as well but short on time
public enum LoggerDescriptions {
	
	couponPurchase("Coupon purchased"),
	couponCreation("Coupon created"),
	couponUpdate("Coupon updated"),
	couponRemove("Coupon Removed");
	
	
	private String value;
	
	private LoggerDescriptions(String option) {
		value = option;
	}
	
	public String getValue() {
		return value;
	}

}
