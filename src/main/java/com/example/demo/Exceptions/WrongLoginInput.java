package com.example.demo.Exceptions;

@SuppressWarnings("serial")
public class WrongLoginInput extends Exception {

	private String userName;
	private String password;

	public WrongLoginInput(String userName, String password, String msg) {
		super(msg);
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
