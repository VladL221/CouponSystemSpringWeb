package com.example.demo.Login;

import java.util.Map;


import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.stereotype.Service;


import com.example.demo.facade.AdminFacade;
import com.example.demo.facade.ClientFacade;
import com.example.demo.facade.CompanyFacade;
import com.example.demo.facade.CustomerFacade;

@Service
public class LoginManager {

	private ConfigurableApplicationContext ctx;

	private Map<String, SessionInfo> sessions;

	private AdminFacade admin;

	private CustomerFacade customer;

	private CompanyFacade company;

	

	public LoginManager(ConfigurableApplicationContext ctx, Map<String, SessionInfo> sessions, AdminFacade admin,
			CustomerFacade customer, CompanyFacade company) {
		super();
		this.ctx = ctx;
		this.sessions = sessions;
		this.admin = admin;
		this.customer = customer;
		this.company = company;
	}


	public ClientFacade login(String email, String password, LoginType ct)  {

		switch (ct) {
		case admin:
			AdminFacade admin = ctx.getBean(AdminFacade.class);
			if (admin.login(email, password) && ct.equals(LoginType.admin))
				return admin;

		case customer:
			CustomerFacade customer = ctx.getBean(CustomerFacade.class);
			if (customer.login(email, password) && ct.equals(LoginType.customer))
				return customer;

		case company:
			CompanyFacade company = ctx.getBean(CompanyFacade.class);
			if (company.login(email, password) && ct.equals(LoginType.company))
				return company;
			else
				return null;

		default:
			return null;
		}

	}
	
	


	public AdminFacade getAdmin() {
		return admin;
	}


	public CustomerFacade getCustomer() {
		return customer;
	}


	public CompanyFacade getCompany() {
		return company;
	}


	public Map<String, SessionInfo> getSessions() {
		return sessions;
	}


	public void setSessions(Map<String, SessionInfo> sessions) {
		this.sessions = sessions;
	}

	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((admin == null) ? 0 : admin.hashCode());
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginManager other = (LoginManager) obj;
		if (admin == null) {
			if (other.admin != null)
				return false;
		} else if (!admin.equals(other.admin))
			return false;
		if (company == null) {
			if (other.company != null)
				return false;
		} else if (!company.equals(other.company))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "LoginManager [sessions=" + sessions + ", admin=" + admin + ", customer=" + customer + ", company="
				+ company + "]";
	}
	
	
	


}
