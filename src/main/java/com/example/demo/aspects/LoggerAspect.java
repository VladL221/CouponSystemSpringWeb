package com.example.demo.aspects;

import java.sql.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.demo.beans.Coupon;
import com.example.demo.beans.LoggerDescriptions;
import com.example.demo.db.CouponRepository;
import com.example.demo.thread.LoggerUpdateThread;


// MINE ******************************************

@Aspect
@Component
public class LoggerAspect {
	
	//this aspect builds the loggerupdatethread and activates it to use the resttemplates on the micro service
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private CouponRepository couponRepository;

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
	
	
}
