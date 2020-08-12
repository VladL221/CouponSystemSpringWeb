package com.example.demo.Rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Exceptions.NotExistsException;
import com.example.demo.Login.LoginManager;
import com.example.demo.Login.SessionInfo;
import com.example.demo.beans.Category;
import com.example.demo.beans.Coupon;
import com.example.demo.facade.CompanyFacade;

@RestController
@RequestMapping("company")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

	private LoginManager manager;

	private CompanyFacade facade;
	
	private RestTemplate restTemplate;


	
	

	public CompanyController(LoginManager manager, CompanyFacade facade) {
		super();
		this.manager = manager;
		this.facade = facade;
	}

	// Token Checker:
	// First check if the token exists and if it matches the client facade.
	// Second check the last activity of the logged in user.
	public CompanyFacade tokenCheck(String token) {
		SessionInfo sessionInfo = manager.getSessions().get(token);
		if (manager.getSessions().containsKey(token) && sessionInfo.getFacade() instanceof CompanyFacade) {
			long endTime = System.currentTimeMillis(); // Get another time to compare last activity.
			if (endTime - sessionInfo.getTimeStamp() > 1800000) { // Check if time gap was 30 minutes.
				manager.getSessions().remove(token);

				return null;
			}

			else

				sessionInfo.setTimeStamp(System.currentTimeMillis());
			return (CompanyFacade) sessionInfo.getFacade();
		} else {
			return null;
		}
	}

	// Create coupon.
	// If it fails, throw an exception from CompanyFacade addCoupon().
	@PostMapping("create/coupon/{token}")
	public ResponseEntity<?> addCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.addCoupon(coupon);
				return ResponseEntity.ok(coupon);
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	// Update coupon.
	// If it fails, throw an exception from CompanyFacade updateCoupon().
	@PutMapping("update/coupon/{token}")
	public ResponseEntity<String> updateCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.updateCoupon(coupon);
				return ResponseEntity.ok("Coupon updated successfully");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Delete coupon.
	// If it fails, throw an exception from CompanyFacade deleteCoupon().
	@DeleteMapping("delete/coupon/{id}/{token}")
	public ResponseEntity<String> deleteCoupon(@PathVariable String token, @PathVariable int id) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.deleteCoupon(id);
				return ResponseEntity.ok("Coupon deleted successfully");
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Return all the company's coupons through CompanyFacade getCompanyCoupons().
	// Added throw because we work with an object that may throw null exception.
	
	@GetMapping("find/all/coupons/{token}")
	public ResponseEntity<?> getAllCoupons(@PathVariable String token) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				
				System.out.println(facade.getAllCompanyCoupons());
				return ResponseEntity.ok(facade.getAllCompanyCoupons());
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Get one coupon by ID.
	// if it fails, throw an exception from CompanyFacade getOneCompanyCoupon().
	@GetMapping("find/one/coupon/{id}/{token}")
	public ResponseEntity<?> getOneCoupon(@PathVariable String token, @PathVariable int id) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				return ResponseEntity.ok(facade.getCoupon(id));
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Get company coupons by category.
	// No need to enter company because it is taken care of by the facade and login.
	@GetMapping("find/all/coupons/category/{category}/{token}")
	public ResponseEntity<?> getAllCouponsbyCategory(@PathVariable String token, @PathVariable Category category) {
		facade = tokenCheck(token);
		if (facade != null)
			try {
				return ResponseEntity.ok(facade.getCouponsByCategory(Category.valueOf(category.toString())));
			} catch (NotExistsException e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
	}

	// Get company coupons by max price.
	// No need to enter company because it is taken care of by the facade and login.
	@GetMapping("find/all/coupons/price/{maxPrice}/{token}")
	public ResponseEntity<?> getAllCouponsByPrice(@PathVariable String token, @PathVariable double maxPrice) {
		facade = tokenCheck(token);
		if (facade != null)
			try {
				return ResponseEntity.ok(facade.getCouponsByMaxVal(maxPrice));
			} catch (NotExistsException e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
	}
	
	//need to add to angular!
	
	@GetMapping("/getPaymentsHistory/{token}")
	public ResponseEntity<?> getPaymentsHistory(@PathVariable("token") String token){
		facade = tokenCheck(token);
		if(facade != null) {
			restTemplate = new RestTemplate();
			try {
				ResponseEntity<?> responseEntity = restTemplate.getForEntity("http://localhost:8383/" + "/getLoggsByClientIdAndClientType/" + facade.getCompanyDetails().getCompanyID() + "/company", List.class);
				if(responseEntity.getBody() != null) {
					return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
				}else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad");
				}
			} catch (RestClientException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("something went wrong please contact us");
			} catch (NullPointerException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Company does not exist please check your request");
			}
		}else {
			return new ResponseEntity<String>("Wrong login!", HttpStatus.UNAUTHORIZED);
		}
	}
	

	@GetMapping("info/{token}")
	public ResponseEntity<?> getCompanyDetails(@PathVariable String token) {
		facade = tokenCheck(token);
		if (facade != null)
			return ResponseEntity.ok(facade.getCompanyDetails());
		else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
	}
	


}
