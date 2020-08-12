package com.example.demo.facade;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.example.demo.Exceptions.CustomException;
import com.example.demo.Exceptions.ExistsException;
import com.example.demo.Exceptions.NotExistsException;
import com.example.demo.beans.Company;
import com.example.demo.beans.Coupon;
import com.example.demo.beans.Customer;
import com.example.demo.db.CompanyRepository;
import com.example.demo.db.CouponRepository;
import com.example.demo.db.CustomerRepository;

@Service
@Scope("prototype")
public class AdminFacade implements ClientFacade {

	
	private AdminFacade facade;
	
	private CompanyRepository companyRepo;
	
	private CustomerRepository customerRepo;

	private CouponRepository couponRepo;
	
	


	public AdminFacade(CompanyRepository companyRepo, CustomerRepository customerRepo, CouponRepository couponRepo) {
		super();
		this.companyRepo = companyRepo;
		this.customerRepo = customerRepo;
		this.couponRepo = couponRepo;
	}

	// adds the company all the validators are on the bean class.
	public void addCompany(Company company) throws ExistsException, CustomException {
		if (companyRepo.findByName(company.getName()) == null) {
			companyRepo.save(emailCheckComp(company));
		} else {
			throw new ExistsException(company.getName(), company.getCompanyID(),
					" The company that you tried to add already exists");
		}
	}

	// updates only either the email or password. validators are on the bean class
	public void updateCompany(Company company) throws NotExistsException, CustomException {
		Company comp2 = companyRepo.findByName(company.getName());
		System.out.println(comp2);
		if (comp2 != null) {
			
			comp2.setEmail(company.getEmail());
			comp2.setPassword(company.getPassword());
			companyRepo.save(emailCheckComp(comp2));

		} else {
			throw new NotExistsException(company.getName(), company.getCompanyID(),
					"this company does not exist please try again");
		}
		
	}


	//deletes the comany as well as the coupons and unlinks the coupons from the customers
	public void deleteCompany(int id) throws NotExistsException {
		Company company = companyRepo.findById(id).orElse(null);
		List<Customer> customers = customerRepo.findAll();
		List<Coupon> coups = couponRepo.findAll();
		coups.retainAll(company.getCoupons());
		boolean exist = false;
		if (company != null) {
			exist = true;
			//unlinks all of the coupons from the customers
			for (Coupon coupon : company.getCoupons()) {
				for (Customer customer : customers) {
					if (customer.getCoupons().contains(coupon)) {
						customer.getCoupons().remove(coupon);
						customerRepo.save(customer);
					}
				}
				//unlinks the coupons from the company
				coupon.setCompany(null);
				couponRepo.save(coupon);
			}
			//deletes the company and unlinks the company from the coupons
			company.getCoupons().clear();
			companyRepo.save(company);
			companyRepo.delete(company);
			couponRepo.deleteAll(coups);
		}

		if (!exist) {
			throw new NotExistsException(null, id, "this company does not exist");
		}
		//and deletes the coupons which are not linked to any company which is this one.

	}
	
	//gets all the companies
	public List<Company> getAllCompanies() throws NotExistsException {
		if (!companyRepo.findAll().isEmpty()) {
			return companyRepo.findAll();
		} else {
			throw new NotExistsException("", 0, " there is no companies added please add a company");
		}
	}
	//gets company by id 
	public Company getCompany(int id) throws CustomException {
		if(companyRepo.findById(id).orElse(null) == null)
			throw new CustomException("There is no such company!");
		return companyRepo.findById(id).orElse(null);
	}

	// *************************** CUSTOMER SECTION *****************************

	//adds a customer. validators are on the bean class
	public void addCustomer(Customer customer) throws CustomException {
		customerRepo.save(emailCheckCust(customer));
	}

	// deletes the cutomer as well as the purchase history of the customer and increases the amount of the coupons by 1
	public void deleteCustomer(int id) throws NotExistsException {
		Customer customer = customerRepo.findById(id).orElse(null);
		if (customer != null) {
			List<Coupon> coupons = customer.getCoupons();
			for (Coupon coupon : coupons) {
				coupon.setAmount(coupon.getAmount() + 1);
				couponRepo.save(coupon);
			}
			customer.getCoupons().clear();
			customerRepo.save(customer);
			customerRepo.delete(customer);
		} else {
			throw new NotExistsException("", id, "this customer does not exist");
		}

	}


	//updates the email or password. validators are on the bean class.
	public void updateCustomer(Customer customer) throws NotExistsException, CustomException {
		Customer custard = customerRepo.findById(customer.getCustomerID()).orElse(null);
		if (custard != null) {
				custard.setPassword(customer.getPassword());
				custard.setEmail(customer.getEmail());
		} else {
			throw new NotExistsException(customer.getFirstName(), customer.getCustomerID(),
					"This customer does not exist");
		}
		customerRepo.save(emailCheckCust(custard));
	}

	//gets all the customers
	public List<Customer> getAllCustomers() throws CustomException {
		List<Customer> customers = customerRepo.findAll();
		if (!customers.isEmpty()) {
			return customers;
		} else {
			throw new CustomException("there is no customers in the database");
		}
	}

	//gets the customer by id
	public Customer getCustomer(int id) throws CustomException {
		if(customerRepo.findById(id).orElse(null) == null)
			throw new CustomException("There is no such customer!");
		return customerRepo.findById(id).orElse(null);
	}

	@Override
	public boolean login(String email, String password) {
		if (email.equals("admin@admin.com") && password.equals("admin")) {
			return true;
		}
		return false;

	}

	
	public Customer emailCheckCust(Customer customer) throws CustomException {
		List<Customer> customers = customerRepo.findAll();
		for (Customer cust : customers) {
			if(cust.getEmail().equals(customer.getEmail())) {
				throw new CustomException("Email already exist!");
			}
		}
		return customer;	
	}
	
	public Company emailCheckComp(Company company) throws CustomException {
		List<Company> companies = companyRepo.findAll();
		for (Company comp : companies) {
			if(comp.getEmail().equals(company.getEmail())) {
				throw new CustomException("Email already exist!");
			}
		}
		return company;	
	}
	
	
	

}
