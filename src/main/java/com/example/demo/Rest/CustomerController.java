package com.example.demo.Rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Login.LoginManager;
import com.example.demo.Login.SessionInfo;
import com.example.demo.beans.Category;
import com.example.demo.beans.Coupon;
import com.example.demo.facade.CustomerFacade;

@RestController
@RequestMapping("customer")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {
	
	
	
	private CustomerFacade facade;
	
	private LoginManager manager;

	public CustomerController(CustomerFacade facade, LoginManager manager) {
		super();
		this.facade = facade;
		this.manager = manager;
	}
	
	//validates token
	public CustomerFacade tokenCheck(String token) {
		SessionInfo session = manager.getSessions().get(token);
		if (manager.getSessions().containsKey(token) && session.getFacade() instanceof CustomerFacade) {
			long idleCheck = System.currentTimeMillis();
			if (idleCheck - session.getTimeStamp() > 1800000) {
				manager.getSessions().remove(token);
				return null;
			}
			session.setTimeStamp(System.currentTimeMillis());
			return (CustomerFacade) session.getFacade();
		} else {
			return null;
		}
	}
//purchases coupon
	@PostMapping("purchase/{token}")
	public ResponseEntity<?> purchaseCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.purchaseCoupon(coupon);
				return ResponseEntity.ok("Coupon purchased successfully");
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	//finds all coupons
	@GetMapping("find/all/coupons/{token}")
	public ResponseEntity<?> getCustomerCoupons(@PathVariable String token) {
		try {
			facade  = tokenCheck(token);
			if (facade != null)
				return ResponseEntity.ok(facade.getAllCustomerCoupons());
			else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("find/all/coupons/cat/{category}/{token}")
	public ResponseEntity<?> getCustomerCouponsByCategory(@PathVariable String category,@PathVariable String token) {
		
			facade = tokenCheck(token);
			System.out.println(category);
			System.out.println(token);
			if (facade != null) {
				try {
					List<Coupon> coupons = facade.getCouponsByCategory(Category.valueOf(category));
					System.out.println(coupons);
				return ResponseEntity.ok(coupons);
				
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
				}
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
			}
	}

	@GetMapping("find/all/coupons/price/{maxPrice}/{token}")
	public ResponseEntity<?> getCustomerCouponsByPrice(@PathVariable String token, @PathVariable double maxPrice) {
		try {
			facade = tokenCheck(token);
			if (facade != null)
				return ResponseEntity.ok(facade.getCouponsByMaxVal(maxPrice));
			else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("info/{token}")
	public ResponseEntity<?> getCustomerDetails(@PathVariable String token) {
		facade = tokenCheck(token);
		if (facade != null)
			return ResponseEntity.ok(facade.getCustomerDetails());
		else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
	}
	
	
	
	

}
