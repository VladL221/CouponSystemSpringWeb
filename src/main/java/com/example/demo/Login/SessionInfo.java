package com.example.demo.Login;

import org.springframework.stereotype.Component;

import com.example.demo.facade.ClientFacade;

@Component
public class SessionInfo {
	
	private ClientFacade facade;
	
	private long timeStamp;

	public SessionInfo(ClientFacade facade, long timeStamp) {
		super();
		this.facade = facade;
		this.timeStamp = timeStamp;
	}
	public SessionInfo() {
		
	}

	public ClientFacade getFacade() {
		return facade;
	}

	public void setFacade(ClientFacade facade) {
		this.facade = facade;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
	
	@Override
	public String toString() {
		return "SessionInfo [facade=" + facade + ", timeStamp=" + timeStamp + "]";
	}
	
	
	
	
	

}
