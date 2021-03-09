package com.example.demo.aspects;

import java.sql.Date;
import java.util.concurrent.CompletableFuture;

import com.example.demo.beans.*;
import com.example.demo.db.CompanyRepository;
import com.example.demo.db.CustomerRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.demo.db.CouponRepository;
import com.example.demo.thread.LoggerUpdateThread;
import org.springframework.web.client.RestTemplate;


// MINE ******************************************

@Aspect
@Component
public class LoggerAspect {
	
	//this aspect builds the loggerupdatethread and activates it to use the resttemplates on the micro service
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CompanyRepository companyRepository;

	private String adminUrl = "http://localhost:8082/adminLogger/addLog";

	private final String RestPack = "com.example.demo.Rest";
	private Thread t;

	
	//executes after return of the advice which if only the response entity response code
	@AfterReturning(pointcut = "execution(* " + RestPack
			+ ".CompanyController.addCoupon(..))", returning = "result")
	public void addCouponPayment(JoinPoint joinPoint, ResponseEntity<?> result) {
		if (result.getStatusCode().is2xxSuccessful()) {
			//build the thread
			LoggerUpdateThread it = new LoggerUpdateThread(joinPoint.getArgs()[0].toString(),
					new Date(System.currentTimeMillis()), LoggerDescriptions.couponCreation.getValue(), 100.0);
			//tihs build the bean that is outside of the current application I.E the microservice
			context.getAutowireCapableBeanFactory().autowireBean(it);
			t = new Thread(it);
			t.start();
		}
	}
	//executes after return of the advice which if only the response entity response code
	@AfterReturning(pointcut = "execution(* " + RestPack
			+ ".CompanyController.updateCoupon(..))", returning = "result")
	public void updateCouponPayment(JoinPoint joinPoint, ResponseEntity<?> result) {
		if (result.getStatusCode().is2xxSuccessful()) {
			//build the thread
			LoggerUpdateThread it = new LoggerUpdateThread(joinPoint.getArgs()[0].toString(),
					new Date(System.currentTimeMillis()), LoggerDescriptions.couponUpdate.getValue(), 10.0);
			context.getAutowireCapableBeanFactory().autowireBean(it);
			t = new Thread(it);
			t.start();
		}
	}
	//executes after return of the advice which if only the response entity response code
	@AfterReturning(pointcut = "execution(* " + RestPack
			+ ".CustomerController.purchaseCoupon(..))", returning = "result")
	public void purchaseCouponPayment(JoinPoint joinPoint, ResponseEntity<?> result) {
		if (result.getStatusCode().is2xxSuccessful()) {
			double price = 0;

			Coupon coupon = couponRepository.findById(((Coupon) joinPoint.getArgs()[1]).getCouponID()).orElse(null);
			if (coupon != null) {
				price = coupon.getPrice();
				//build the thread
				LoggerUpdateThread it = new LoggerUpdateThread(joinPoint.getArgs()[0].toString(),
						new Date(System.currentTimeMillis()), LoggerDescriptions.couponPurchase.getValue(), price);
				context.getAutowireCapableBeanFactory().autowireBean(it);
				t = new Thread(it);
				t.start();
			} else {
				//logger here 
			}
		}
	}

//	@Column
//	private String methodType;
//	@Column
//	private Date executionDate;
//	@Column
//	private int statusCode;
//	@Enumerated(EnumType.STRING)
//	private ClientType type;
//	@Column(nullable = false)
//	private int clientId;


	@Before("execution(* "+RestPack + ".AdminController.delete*(..))")
	public CompletableFuture<?> adminDeleteLogger(JoinPoint joinPoint){
		Logger logger;
		String name = methodNameCheck(joinPoint.getSignature().getName());
		if(name.matches(("(.*)Customer(.*)"))){
			Customer customer = customerRepository.findById(((Integer) joinPoint.getArgs()[1])).orElse(null);
			logger = new Logger(name, new Date(System.currentTimeMillis()),200, "customer",customer.getCustomerID());
			return sendAdminLog(logger).thenApply(ResponseEntity::ok);
		}else if(name.matches(("(.*)Company(.*)"))){
			Company company = companyRepository.findById(((Integer) joinPoint.getArgs()[1])).orElse(null);
			logger = new Logger(name, new Date(System.currentTimeMillis()),200, "company",company.getCompanyID());
			return sendAdminLog(logger).thenApply(ResponseEntity::ok);
		}
		return null;
	}

	@AfterReturning(pointcut = "execution(* "+RestPack + ".AdminController.add*(..)) ||" +
			"execution(* "+RestPack + ".AdminController.update*(..))",returning = "result")
	public CompletableFuture<?> adminLogger(JoinPoint joinPoint, ResponseEntity<?> result){
		Logger logger;
		String name = methodNameCheck(joinPoint.getSignature().getName());
		if(name.matches(("(.*)Customer(.*)"))){
			Customer customer = customerRepository.findById(((Customer) joinPoint.getArgs()[1]).getCustomerID()).orElse(null);
			logger = new Logger(name, new Date(System.currentTimeMillis()),result.getStatusCodeValue(), "customer",customer.getCustomerID());
			return sendAdminLog(logger).thenApply(ResponseEntity::ok);
		}else if(name.matches(("(.*)Company(.*)"))){
			Company company = companyRepository.findById(((Company) joinPoint.getArgs()[1]).getCompanyID()).orElse(null);
			logger = new Logger(name, new Date(System.currentTimeMillis()),result.getStatusCodeValue(), "company",company.getCompanyID());
			return sendAdminLog(logger).thenApply(ResponseEntity::ok);
		}
		return null;
	}


	@Async
	private CompletableFuture<Logger> sendAdminLog(Logger logger){
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Logger> responseEntity = restTemplate.postForEntity(adminUrl,logger,Logger.class);
		return CompletableFuture.completedFuture(logger);
	}


	private String methodNameCheck(String methodName){
		switch (methodName){
			case "deleteCustomer":
				return LoggerDescriptions.deleteCustomer.getValue();
			case "deleteCompany":
				return LoggerDescriptions.deleteCompany.getValue();
			case "addCustomer":
				return LoggerDescriptions.addCustomer.getValue();
			case "addCompany":
				return LoggerDescriptions.addCompany.getValue();
			case "updateCustomer":
				return LoggerDescriptions.updateCustomer.getValue();
			case "updateCompany":
				return LoggerDescriptions.updateCompany.getValue();
		}
		return null;
	}




}
