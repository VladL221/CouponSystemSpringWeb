package com.example.demo.beans;

import java.sql.Date;

import org.springframework.stereotype.Component;

//logger class to be used by the aspect and the logger thread
@Component
public class Logger {

	private int id;

	private int clientId;

	private String clientType;

	private Date executionDate;

	private String description;

	private double amount;

	public Logger() {
		super();
	}

	public Logger(int clientId, String clientType, Date executionDate, String description, double amount) {
		super();
		this.clientId = clientId;
		this.clientType = clientType;
		this.executionDate = executionDate;
		this.description = description;
		this.amount = amount;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

}
