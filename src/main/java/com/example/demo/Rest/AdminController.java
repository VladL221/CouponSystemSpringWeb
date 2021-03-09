package com.example.demo.Rest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.demo.beans.ClientType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Exceptions.CustomException;
import com.example.demo.Exceptions.NotExistsException;
import com.example.demo.Login.LoginManager;
import com.example.demo.Login.SessionInfo;
import com.example.demo.beans.Company;
import com.example.demo.beans.Customer;
import com.example.demo.facade.AdminFacade;


@RestController
@RequestMapping("admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

	private LoginManager manager;

	private AdminFacade facade;
	
	private RestTemplate restTemplate;

	

	public AdminController(LoginManager manager, AdminFacade facade) {
		super();
		this.manager = manager;
		this.facade = facade;
	}

	// Token Checker:
	// First check if the token exists and if it matches the client facade.
	// Second check the last activity of the logged in user.
	private AdminFacade tokenCheck(String token) {
		SessionInfo sessionInfo = manager.getSessions().get(token);
		if (manager.getSessions().containsKey(token) && sessionInfo.getFacade() instanceof AdminFacade) {
			long endTime = System.currentTimeMillis(); // Get another time to compare last activity.
			if (endTime - sessionInfo.getTimeStamp() > 1800000) { // Check if time gap was 30 minutes.
				manager.getSessions().remove(token);
				return null;
			}

			else

				sessionInfo.setTimeStamp(System.currentTimeMillis());
			return (AdminFacade) sessionInfo.getFacade();
		} else {
			return null;
		}	
	}

	// Create company.
	// If it fails, throw an exception from AdminFacade addCompany().
	@PostMapping("create/company/{token}")
	public ResponseEntity<?> addCompany(@PathVariable String token, @RequestBody Company company) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.addCompany(company);
				return ResponseEntity.status(HttpStatus.CREATED).build();
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Update company.
	// If it fails, throw an exception from the AdminFacade updateCompany().
	@PutMapping("update/company/{token}")
	public ResponseEntity<?> updateCompany(@PathVariable String token, @RequestBody Company company) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.updateCompany(company);
				return  ResponseEntity.status(HttpStatus.OK).body("Updated");
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Delete company.
	// If it fails, throw an exception from AdminFacade deleteCompany().
	@DeleteMapping("delete/company/{id}/{token}")
	public ResponseEntity<String> deleteCompany(@PathVariable String token, @PathVariable int id) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				String name = facade.getCompany(id).getName().toString();
				facade.deleteCompany(id);
				
				return ResponseEntity.ok("Company " + name.toString() + " deleted successfully");
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Return all the companies through AdminFacade getAllCompanies().
	@GetMapping("find/all/companies/{token}")
	public ResponseEntity<?> getAllCompanies(@PathVariable String token) {
		facade = tokenCheck(token);
		if (facade != null)
			try {
				return ResponseEntity.ok(facade.getAllCompanies());
			} catch (NotExistsException e) {
				// TODO Auto-generated catch block
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
	}

	// Return one company.
	// If it fails, throw an exception from AdminFacade getOneCompany().
	@GetMapping("find/one/company/{id}/{token}")
	public ResponseEntity<?> getOneCompany(@PathVariable int id, @PathVariable String token) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				return ResponseEntity.ok(facade.getCompany(id));
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}

	// Create customer.
	// If it fails, throw an exception from AdminFacade addCustomer().
	@PostMapping("create/customer/{token}")
	public ResponseEntity<?> addCustomer(@PathVariable String token, @RequestBody Customer customer) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.addCustomer(customer);
				return ResponseEntity.status(HttpStatus.CREATED).build();
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Update customer.
	// If it fails, throw an exception from the AdminFacade updateCustomer().
	@PutMapping("update/customer/{token}")
	public ResponseEntity<?> updateCustomer(@PathVariable String token, @RequestBody Customer customer) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				facade.updateCustomer(customer);
				return ResponseEntity.status(200).body("Updated");
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Delete customer.
	// If it fails, throw an exception from AdminFacade deleteCustomer().
	@DeleteMapping("delete/customer/{id}/{token}")
	public ResponseEntity<String> deleteCustomer(@PathVariable String token, @PathVariable int id) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				String name =	facade.getCustomer(id).getFirstName().toString();
				facade.deleteCustomer(id);
				return ResponseEntity.ok("Customer " + name.toString() + " deleted successfully");
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Return all the customers through AdminFacade getAllCustomers().
	@GetMapping("find/all/customers/{token}")
	public ResponseEntity<?> getAllCustomers(@PathVariable String token) {
		facade = tokenCheck(token);
		if (facade != null)
			try {
				return ResponseEntity.ok(facade.getAllCustomers());
			} catch (CustomException e) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		else
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
	}

	// Return one customer.
	// If it fails, throw an exception from AdminFacade getOneCustomer().
	@GetMapping("find/one/customer/{id}/{token}")
	public ResponseEntity<?> getOneCustomer(@PathVariable String token, @PathVariable int id) {
		try {
			facade = tokenCheck(token);
			if (facade != null) {
				return ResponseEntity.ok(facade.getCustomer(id));
			} else
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user!");
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}
	

	// added
	
	@GetMapping("/getAllLoggers/{token}")
	public ResponseEntity<?> getAllLoggs(@PathVariable("token") String token) {
		facade = tokenCheck(token);
		if (facade != null) {
			restTemplate = new RestTemplate();
			ResponseEntity<?> responseEntity = restTemplate.getForEntity("http://localhost:8383/" + "getAllLoggs", List.class);
			if (responseEntity.getBody() != null) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad");
			}
		} else {
			return new ResponseEntity<String>("Wrong login!", HttpStatus.UNAUTHORIZED);
		}
	}
	//added
	@GetMapping("/getAllCompaniesLoggs/{token}")
	public ResponseEntity<?> getAllCompaniesLoggs(@PathVariable("token") String token) {
		facade = tokenCheck(token);
		if (facade != null) {
			restTemplate = new RestTemplate();
			ResponseEntity<?> responseEntity = restTemplate
					.getForEntity("http://localhost:8383/" + "getLoggsByClientType/company", List.class);
			if (responseEntity.getBody() != null) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad");
			}
		} else {
			return new ResponseEntity<String>("Wrong login!", HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/getAllCustomersLoggs/{token}")
	public ResponseEntity<?> getAllCustomersLoggs(@PathVariable("token") String token) {
		facade = tokenCheck(token);
		if (facade != null) {
			restTemplate = new RestTemplate();
			ResponseEntity<?> responseEntity = restTemplate
					.getForEntity("http://localhost:8383/" + "getLoggsByClientType/customer", List.class);
			if (responseEntity.getBody() != null) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad");
			}
		} else {
			return new ResponseEntity<String>("Wrong login!", HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/getAllSpecificClientLoggs/{token}/{id}/{type}")
	public ResponseEntity<?> getAllCustomersLoggs(@PathVariable("token") String token, @PathVariable("id") int id,
			@PathVariable("type") String type) {
		facade = tokenCheck(token);
		if (facade != null) {
			if(id < 1 || type.length() < 1) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
			}
			restTemplate = new RestTemplate();
			ResponseEntity<?> responseEntity = restTemplate
					.getForEntity("http://localhost:8383/" + "getLoggsByClientIdAndClientType/" + id + "/" + type, List.class);
			if (responseEntity.getBody() != null) {
				return ResponseEntity.status(HttpStatus.OK).body(responseEntity.getBody());
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad");
			}
		} else {
			return new ResponseEntity<String>("Wrong login!", HttpStatus.UNAUTHORIZED);
		}
	}

	@Async
	@GetMapping("/getAllAdminLogs/{token}")
	CompletableFuture<?> getAllAdminLogs(@PathVariable String token){
		facade = tokenCheck(token);
		if(facade != null){
			RestTemplate restTemplate = new RestTemplate();
			return CompletableFuture.completedFuture(restTemplate.getForEntity("http://localhost:8082/adminLogger/getAllLogs",List.class)).thenApply(body -> body.getBody());
		}
		return null;
	}

	@Async
	@GetMapping("/getAllAdminLogsByType/{token}/{type}")
	CompletableFuture<?> getAllAdminLogsByType(@PathVariable String token, @PathVariable ClientType type){
		facade = tokenCheck(token);
		RestTemplate restTemplate;
		if(facade != null){
			restTemplate = new RestTemplate();
//			ResponseEntity<?> entity = restTemplate.getForEntity(String.format("http://localhost:8082/adminLogger/%s",type.getValue()),List.class);
			return CompletableFuture.completedFuture(restTemplate.getForEntity(String.format("http://localhost:8082/adminLogger/%s",type.getValue()),List.class)).thenApply(body -> body.getBody());
		}
		return null;
	}

}
