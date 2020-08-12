package com.example.demo.db;


import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.beans.Category;
import com.example.demo.beans.Coupon;
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
	
	//find the coupons by category
	List<Coupon> findByCategory(Category category);
	//finds the coupons that are equal or less than the price
	List<Coupon> findByPriceLessThanEqual(double price);
	//finds the coupons by end date
	List<Coupon> findByEndDateBefore(Date date);
	//finds coupons by amount greater than value
	List<Coupon> findByAmountGreaterThan(int amount);
}
