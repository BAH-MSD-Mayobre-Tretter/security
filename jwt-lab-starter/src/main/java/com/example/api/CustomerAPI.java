package com.example.api;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerAPI {

	@GetMapping
	public String getAll() {
		String response = "[ ";
		for (int i = 0; i < list.size(); i++) {
			response += list.get(i).toJSON();
			if (i + 1 < list.size()) {
				response += ", ";
			}
		}
		response += " ]";
		return response;
	}

	@GetMapping("/{customerId}")
	public String getCustomerById(@PathVariable("customerId") long id) {
		String response = "{}";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == id) {
				response = list.get(i).toJSON();
			}
		}
		return response;
	}

	ArrayList<Customer> list = new ArrayList<Customer>();

	public CustomerAPI() {
		Customer c1 = new Customer(1, "Steve", "pass", "steve@abc.com");
		Customer c2 = new Customer(1, "Bob", "pass", "bob@abc.com");
		Customer c3 = new Customer(1, "Cindy", "pass", "cindy@abc.com");
		list.add(c1);
		list.add(c2);
		list.add(c3);
	}
	
}
