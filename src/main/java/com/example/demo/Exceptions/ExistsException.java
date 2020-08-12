package com.example.demo.Exceptions;

@SuppressWarnings("serial")
public class ExistsException extends Exception {

	private String text;
	private int id;

	public ExistsException(String text, int id, String msg) {
		super(msg);
		setText(text);
		setId(id);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
