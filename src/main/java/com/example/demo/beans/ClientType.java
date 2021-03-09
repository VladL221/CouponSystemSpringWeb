package com.example.demo.beans;

public enum ClientType {

    customer("customer"),
    company("company");

    private String value;

    private ClientType(String option) { value = option; }


    public String getValue() {
        return value;
    }

}
