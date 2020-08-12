package com.example.demo.facade;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.example.demo.Exceptions.ExistsException;
import com.example.demo.Exceptions.NotExistsException;
import com.example.demo.Exceptions.WrongLoginInput;
import com.example.demo.beans.Category;
import com.example.demo.beans.Coupon;
import com.example.demo.beans.Customer;
import com.example.demo.db.CouponRepository;
import com.example.demo.db.CustomerRepository;

@Service
@Scope("prototype")
public class CustomerFacade implements ClientFacade {
	
	private CustomerRepository customerRepo;
	
	private CouponRepository couponRepo;
	
	private CustomerFacade facade;
	


	public CustomerFacade(CustomerRepository customerRepo, CouponRepository couponRepo) {
		super();
		this.customerRepo = customerRepo;
		this.couponRepo = couponRepo;
	}

	private Customer custLogin;

	//purchases the coupons
	public void purchaseCoupon(Coupon coupon) throws NotExistsException, ExistsException {
		Customer custard = customerRepo.findById(custLogin.getCustomerID()).orElse(null);
		Coupon coup = couponRepo.findById(coupon.getCouponID()).orElse(null);
		if (coup != null) {
			//checks if this custiner akready has the coupon and if the amount is bigger than 0 and that the end date is after now
			if (!custard.getCoupons().contains(coup) && coup.getAmount() > 0
					&& coup.getEndDate().after(new Date(System.currentTimeMillis()))) {
				custard.getCoupons().add(coup);
				coup.setAmount(coup.getAmount() - 1);
				couponRepo.save(coup);
				customerRepo.save(custard);
			} else {
				throw new ExistsException(coupon.getTitle(), coupon.getCouponID(),
						"this coupon is either already purchased or its expired or the stock is 0");
			}

		} else {
			throw new NotExistsException(coupon.getTitle(), coupon.getCouponID(), "this coupon does not exist");
		}

	}
	//gets all customer coupons
	public List<Coupon> getAllCustomerCoupons() throws NotExistsException {
		Customer custard = customerRepo.findById(custLogin.getCustomerID()).orElse(null);
		if (!custard.getCoupons().isEmpty()) {
			return custard.getCoupons();
		} else {
			throw new NotExistsException("", 0, "This customer does not own any coupons");
		}
	}

	// returns coupons by category
	public List<Coupon> getCouponsByCategory(Category category) throws NotExistsException {
		Customer custard = customerRepo.findById(custLogin.getCustomerID()).orElse(null);
		List<Coupon> coupons = couponRepo.findByCategory(category);
		coupons.retainAll(custard.getCoupons());
		if (!coupons.isEmpty()) {
			return coupons;
		} else {
			throw new NotExistsException("", 0, "This customer does not own any coupons of this type");
		}
	}

	// returns coupons by max val
	public List<Coupon> getCouponsByMaxVal(double maxVal) throws NotExistsException {
		Customer custard = customerRepo.findById(custLogin.getCustomerID()).orElse(null);
		List<Coupon> coupons = couponRepo.findByPriceLessThanEqual(maxVal);
		// removes coupons which do not belong to this customer
		coupons.retainAll(custard.getCoupons());
		if (!coupons.isEmpty()) {
			return coupons;
		} else {
			throw new NotExistsException("", 0, "This customer does not own any coupons up to this price");
		}
	}

	// get customer details
	public Customer getCustomerDetails() {
		Customer custard = customerRepo.findById(custLogin.getCustomerID()).orElse(null);
		return custard;
	}


	@Override
	public boolean login(String email, String password) {
		List<Customer> customers = customerRepo.findAll();
		for (Customer custard : customers) {
			if (custard.getEmail().contentEquals(email) && custard.getPassword().contentEquals(password)) {
				custLogin = custard;
				return true;
			}
		}
		return false;
	}


	

}
