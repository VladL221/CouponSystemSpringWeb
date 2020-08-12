package com.example.demo.facade;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.example.demo.Exceptions.CustomException;
import com.example.demo.Exceptions.ExistsException;
import com.example.demo.Exceptions.NotExistsException;
import com.example.demo.beans.Category;
import com.example.demo.beans.Company;
import com.example.demo.beans.Coupon;
import com.example.demo.beans.Customer;
import com.example.demo.db.CompanyRepository;
import com.example.demo.db.CouponRepository;
import com.example.demo.db.CustomerRepository;

@Service
@Scope("prototype")
public class CompanyFacade implements ClientFacade {
	
	private CompanyFacade facade;
	
	private CompanyRepository companyRepo;
	
	private CouponRepository couponRepo;

	private CustomerRepository customerRepo;

	private Company compLogin;
	
	
	



	public CompanyFacade(CompanyRepository companyRepo, CouponRepository couponRepo, CustomerRepository customerRepo) {
		super();
		this.companyRepo = companyRepo;
		this.couponRepo = couponRepo;
		this.customerRepo = customerRepo;
	}

	// checks the login validators
	@Override
	public boolean login(String email, String password)  {
		List<Company> company = companyRepo.findAll();
		// checks if any companies exist or not so that admin will know to add.
		if (company.isEmpty())
			 return false;
		for (Company company2 : company) {
			// checks if company2 is equal to the logging in company
			if (company2.getEmail().equalsIgnoreCase(email) && company2.getPassword().equalsIgnoreCase(password)) {
				compLogin = company2;
				return true;
			}
		}
		return false;

	}

	//adds a coupons and validates that the company does not own a coupon of the same name
	//validators are on the bean class of coupon
	public void addCoupon(Coupon coupon) throws ExistsException, CustomException {
		Coupon coup =(Coupon) couponCheck(coupon);
		coupon.setCompany(compLogin);
		couponRepo.save(coup);
		companyRepo.save(compLogin);
		this.compLogin = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		
			
	}

	// updates the coupon with the right values if they were updated also checks the
	// title validator so no duplicates
	// other validators are in the bean class
	public void updateCoupon(Coupon coupon) throws CustomException {
		Company comp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		Coupon coup = (Coupon) couponCheck(coupon) ;
		if (comp.getCoupons().contains(coup)) {
			coup.setAmount(coupon.getAmount());
			coup.setCategory(coupon.getCategory());
			coup.setDescription(coupon.getDescription());
			coup.setEndDate(coupon.getEndDate());
			coup.setImage(coupon.getImage());
			coup.setPrice(coupon.getPrice());
			coup.setStartDate(coupon.getStartDate());
			coup.setTitle(coupon.getTitle());
			couponRepo.save(coup);
			this.compLogin = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
				} else {
			throw new CustomException("This coupon does not belong to this company!");
				}

	}

	//deletes the coupons and unlinks them from the customers that bought it
	public void deleteCoupon(int CouponID) throws NotExistsException {
		Company comp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		Coupon coupon = couponRepo.findById(CouponID).orElse(null);
		if (coupon != null) {
			if (comp.getCoupons().contains(coupon)) {
				for (Customer customer2 : customerRepo.findAll()) {
					if (customer2.getCoupons().contains(coupon)) {
						customer2.getCoupons().remove(coupon);
						customerRepo.save(customer2);
					}
				}
				comp.getCoupons().remove(coupon);
				companyRepo.save(comp);
				couponRepo.delete(coupon);
			} else {
				throw new NotExistsException(coupon.getTitle(), CouponID, "Company does not own this coupon");
			}
		} else {
			throw new NotExistsException("", CouponID, "coupon does not exist please try another coupon");
		}

	}
	
	// returns the coupons if company owns it
	public Coupon getCoupon(int id) throws CustomException {
		Company comp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		Coupon coup = couponRepo.findById(id).orElse(null);
		if (comp.getCoupons().contains(coup)) {
			return coup;
		} else {
			throw new CustomException("This company does not own this coupon!");
		}

	}
	
	//returns all company coupons
	
	public List<Coupon> getAllCompanyCoupons() throws NotExistsException {
		Company tempComp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		if (!tempComp.getCoupons().isEmpty()) {
			System.out.println(tempComp.getCoupons());
			return tempComp.getCoupons();
		} else {
			throw new NotExistsException(compLogin.getName(), compLogin.getCompanyID(),
					"This company does not own any coupons");

		}

	}

	// returns coupons by category
	public List<Coupon> getCouponsByCategory(Category category) throws NotExistsException {
		Company comp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		// gets all the coupons of this category
		List<Coupon> coupons = couponRepo.findByCategory(category);
		// gets all coupons of this company
		// filters the coupons that do not belong to this company
		coupons.retainAll(comp.getCoupons());
		if (!coupons.isEmpty()) {
			// returns the filtered coupons
			return coupons;

		}
		throw new NotExistsException("", 0, "This company does not own any coupons by this category");

	}

	// returns the coupons by its max value input
	public List<Coupon> getCouponsByMaxVal(double maxVal) throws NotExistsException {
		Company comp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		// gets all coupons by max value
		List<Coupon> coupons = couponRepo.findByPriceLessThanEqual(maxVal);
		// filters the coupons that do not belong to this company
		coupons.retainAll(comp.getCoupons());

		if (!coupons.isEmpty()) {
			// returns the filtered coupons
			return coupons;
		} else {
			throw new NotExistsException("", 0, "This company does not own any coupons by this max value");
		}
	}
	// returns the company details
	public Company getCompanyDetails() {
		Company comp = companyRepo.findById(compLogin.getCompanyID()).orElse(null);
		return comp;

	}
	
	
	// needs to validate that the coupon end date is not after start date and that end date is not after current time,
	// that company doesnt own a coupon with same title
	
	public Coupon couponCheck(Coupon coupon) throws CustomException {
		List<Coupon> coupons = couponRepo.findAll();
		coupons.retainAll(companyRepo.findById(compLogin.getCompanyID()).orElse(null).getCoupons());
		for (Coupon coup : coupons) {
			if(coup.getTitle().equals(coupon.getTitle()))
				throw new CustomException("You already own a coupon with this title");
		}
		if(coupon.getStartDate().after(coupon.getEndDate()) || coupon.getEndDate().before(new Date(System.currentTimeMillis())))
			throw new CustomException("End date must be after current date! and be after start date!");
		else if(coupon.getAmount() < 1 || coupon.getCategory() == null || coupon.getDescription().length() < 3 || coupon.getImage() == null || coupon.getPrice()<1 || coupon.getTitle().length() <3)
		throw new CustomException("Please make sure that all of the fields are filled properly!");
		
		return coupon;
	}

	
	

}
