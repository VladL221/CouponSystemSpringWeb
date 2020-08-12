package com.example.demo.Rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Exceptions.CustomException;
import com.example.demo.Exceptions.WrongLoginInput;
import com.example.demo.Login.LoginManager;
import com.example.demo.Login.LoginType;
import com.example.demo.Login.SessionInfo;
import com.example.demo.beans.Coupon;
import com.example.demo.db.CouponRepository;
import com.example.demo.facade.AdminFacade;
import com.example.demo.facade.ClientFacade;
import com.example.demo.facade.CompanyFacade;
import com.example.demo.facade.CustomerFacade;

import springfox.documentation.spring.web.json.Json;

@RestController
@RequestMapping("login")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {
	
	
	private LoginManager manager;
	private CouponRepository coupRepo;

	// Constructor instead of "@Autowire" to avoid errors, safer.
	public LoginController(LoginManager manager,  CouponRepository coupRepo) {
		super();
		this.manager = manager;
		this.coupRepo = coupRepo;
	}

	// Login method tries to log in with email, password and the client type and
	// checks if it's actually someone in our data base.
	@PostMapping("{email}/{password}/{clientType}")
	public ResponseEntity<?> login(@PathVariable String email, @PathVariable String password,@PathVariable String clientType) {
		ClientFacade facade = null;
		
		try {
			facade = manager.login(email, password, LoginType.valueOf(clientType));
			if (facade != null) {
				SessionInfo ses = new SessionInfo();
				String token = UUID.randomUUID().toString();
				if (facade instanceof AdminFacade) {
					ses.setFacade((AdminFacade) facade);
					ses.setTimeStamp(System.currentTimeMillis());
				} else if (facade instanceof CompanyFacade) {
					ses.setFacade((CompanyFacade) facade);
					ses.setTimeStamp(System.currentTimeMillis());
				} else if (facade instanceof CustomerFacade) {
					ses.setFacade((CustomerFacade) facade);
					ses.setTimeStamp(System.currentTimeMillis());
				} else {
					return new ResponseEntity<String>("Bad login!",HttpStatus.BAD_REQUEST);
				}
				System.out.println(ses.getFacade());
				// Generate token - UUID is like a random generated unique ID
				// for the person logged in.
				System.out.println(facade);
				manager.getSessions().put(token, ses); // If succeeds then put the token and facade into the HashMap.
				return ResponseEntity.ok(token);
			} else {
				return new ResponseEntity<String>("Bad login!",HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("Bad login exception", HttpStatus.BAD_REQUEST);

		}
	}


	// Logout method.
	// Remove the token and send a message of logging out.
	@GetMapping("/logout/{token}")
	public ResponseEntity<?> logout(@PathVariable String token) {
		manager.getSessions().remove(token);
		return new ResponseEntity<String>("User logged out successfully!",HttpStatus.OK);
	}

	// Method to return and show all coupons on the web
	@GetMapping("coupons")
	public ResponseEntity<?> getAllCoupons() {
		try {
			return ResponseEntity.ok(coupRepo.findByAmountGreaterThan(0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); 
		}
		
	}

}
