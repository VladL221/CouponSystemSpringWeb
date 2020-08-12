package com.example.demo.Exceptions;

import java.sql.Date;

import com.example.demo.beans.Category;

@SuppressWarnings("serial")
public class InputMisMatchException extends Exception {

	private int id;
	private String name;
	private String password;
	private String email;
	private Date startDate;
	private Date endDate;
	private int amount;
	private Category type;
	private String desc;
	private double price;
	private String image;

	public InputMisMatchException(int id, String name, String password, String email, String msg) {
		super(msg);
		setId(id);
		setName(name);
		setPassword(password);
		setEmail(email);
	}

	public InputMisMatchException(int id, String name, Date startDate, Date endDate, int amount, Category type,
			String desc, double price, String image, String msg) {
		super(msg);
		setId(id);
		setName(name);
		setStartDate(startDate);
		setEndDate(endDate);
		setAmount(amount);
		setType(type);
		setDesc(desc);
		setPrice(price);
		setImage(image);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Category getType() {
		return type;
	}

	public void setType(Category type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
