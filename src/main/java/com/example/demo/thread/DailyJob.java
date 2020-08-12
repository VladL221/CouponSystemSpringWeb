package com.example.demo.thread;

import java.sql.Date;

import java.util.Calendar;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.beans.Company;
import com.example.demo.beans.Coupon;
import com.example.demo.beans.Customer;
import com.example.demo.db.CompanyRepository;
import com.example.demo.db.CouponRepository;
import com.example.demo.db.CustomerRepository;

@Component
public class DailyJob {

	private CouponRepository couponRepo;
	private CompanyRepository compRepo;
	private CustomerRepository custRepo;

	public DailyJob(CouponRepository couponRepo, CompanyRepository compRepo, CustomerRepository custRepo) {
		super();
		this.couponRepo = couponRepo;
		this.compRepo = compRepo;
		this.custRepo = custRepo;
	}

	//finds the expired coupons and deletes them
	@Transactional
	@Scheduled(fixedRate = (1000 * 60 * 60 * 24))
	public void task() {
		// calls for all the coupons with expired enddate
		List<Coupon> coupons = couponRepo.findByEndDateBefore(new Date(Calendar.getInstance().getTimeInMillis()));
		if (!coupons.isEmpty()) {
			// removes all the expired coupons from the companies
			for (Company company : compRepo.findAll()) {
				company.getCoupons().removeAll(coupons);
				compRepo.save(company);
			}
			// removes all the expired coupons from customers
			for (Customer customer : custRepo.findAll()) {
				customer.getCoupons().removeAll(coupons);
				custRepo.save(customer);
			}
			couponRepo.deleteAll(coupons);
		}

	}

}
