package com.example.demo.thread;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Login.LoginManager;
import com.example.demo.Login.SessionInfo;
import com.example.demo.beans.Logger;
import com.example.demo.facade.ClientFacade;
import com.example.demo.facade.CompanyFacade;
import com.example.demo.facade.CustomerFacade;

public class LoggerUpdateThread implements Runnable{

	@Autowired
	private LoginManager instance;
	
	private ClientFacade facade = null;

	private String token;
	private int clientId;
	private Date executionDate;
	private String description;
	private double amount;
	
	public LoggerUpdateThread() {}

	public LoggerUpdateThread(String token, Date executionDate, String description, double amount) {
		this.token = token;
		this.executionDate = executionDate;
		this.description = description;
		this.amount = amount;
	}
	//the thread that activates and send the rest template requests to the micro servcie
	@Override
	public void run() {
		//validates token
		facade = getFacadeByToken(token);
		if (facade != null) {
			RestTemplate restTemplate;
			Logger logger;
			if (facade instanceof CompanyFacade) {
				CompanyFacade companyFacade = (CompanyFacade) facade;
				try {
					//builds the thread constructor attributes
					clientId = companyFacade.getCompanyDetails().getCompanyID();
					logger = new Logger(clientId, "company", executionDate, description, amount);
					restTemplate = new RestTemplate();
					//if aspects advice returned 200 activates this rest whole thread and sends te built thread to the micro service
					ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8383/storeLogger", logger, String.class);
					if(responseEntity.getStatusCodeValue() == 20) {
						// logger file here
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					// suppose to send to admin if user might get coupon but not pay
				}
			} else if (facade instanceof CustomerFacade) {
				CustomerFacade customerFacade = (CustomerFacade) facade;
				try {
					clientId = customerFacade.getCustomerDetails().getCustomerID();
					logger = new Logger(clientId, "customer", executionDate, description, amount);
					restTemplate = new RestTemplate();
					ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8383/storeLogger", logger, String.class);
					if(responseEntity.getStatusCodeValue() == 20) {
						// logger file here
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					// suppose to send to admin if user might get coupon but not pay
				}
			}else {
				// suppose to send to admin if user might get coupon but not pay
			}
		} else {
			// suppose to send to admin if user might get coupon but not pay
		}
	}

	private ClientFacade getFacadeByToken(String token) {
		SessionInfo sessionInfo = instance.getSessions().get(token);
		if (sessionInfo == null) {
			instance.getSessions().remove(token);
			return null;
		} else {
			return sessionInfo.getFacade();
		}
	}
	
}
