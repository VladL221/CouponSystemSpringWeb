package com.example.demo.facade;


import com.example.demo.Exceptions.CustomException;
import com.example.demo.Exceptions.WrongLoginInput;
public interface ClientFacade {

	public boolean login(String email, String password);
}
