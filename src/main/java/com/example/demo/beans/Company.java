package com.example.demo.beans;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.demo.Exceptions.CustomException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "companies")
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int CompanyID;
	@Column(nullable = false, unique = true)
	private String name;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "company")
	private List<Coupon> coupons;

	public Company() {

	}

	public Company(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	public int getCompanyID() {
		return CompanyID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + CompanyID;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Company))
			return false;
		Company other = (Company) obj;
		if (CompanyID != other.CompanyID)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Company [CompanyID=" + CompanyID + ", name=" + name + ", email=" + email + ", password=" + password
				+ "]";
	}

}
