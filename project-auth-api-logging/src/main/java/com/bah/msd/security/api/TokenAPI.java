package com.bah.msd.security.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bah.msd.security.domain.Customer;
import com.bah.msd.security.domain.CustomerFactory;
import com.bah.msd.security.domain.Token;
import com.bah.msd.security.util.JWTHelper;


@RestController
@RequestMapping("/token")
public class TokenAPI {

	//private static Key key = AuthFilter.key;	
	public static Token appUserToken;
	
	@GetMapping
	public String getAll() {
		System.out.println("getAll");
		return "jwt-fake-token-asdfasdfasfa".toString();
	}
	
	@PostMapping
//	 public ResponseEntity<?> createTokenForCustomer(@RequestBody Customer customer, HttpRequest request, UriComponentsBuilder uri) {
	public ResponseEntity<?> createTokenForCustomer(@RequestBody Customer customer) {
		String username = customer.getName();
		String password = customer.getPassword();
		
		
		if (username != null && username.length() > 0 && password != null && password.length() > 0 && checkPassword(username, password)) {
			Token token = createToken(username);
			ResponseEntity<?> response = ResponseEntity.ok(token);
			System.out.println("create token for customer: " + customer);
			return response;			
		}
		// bad request
		
		// System.out.println("username is " + username);
		// System.out.println("password is " + password);
		
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
	}
	
	// this method should be calling the method service
	
	private boolean checkPassword(String username, String password) { 
		System.out.println("username: " + username + " and password: " + password);
		// special case for application user
		if(username.equals("ApiClientApp") && password.equals("secret")) {
			return true;
		}
		// make call to customer service 
		Customer cust = getCustomerByNameFromCustomerAPI(username);
		
		// compare name and password
		if(cust != null && cust.getName().equals(username) && cust.getPassword().equals(password)) {
			return true;				
		}	
		RestTemplate restClient;
		return false;
		
		// local version of the above code, gets customer from repository
		
//		Iterator<Customer> customers = repo.findAll().iterator();
//		while(customers.hasNext()) {
//			Customer cust = customers.next();
//			if(cust.getName().equals(username) && cust.getPassword().equals(password)) {
//				return true;				
//			}
//		}
		
		

	}
	
	public static Token getAppUserToken() {
		if(appUserToken == null || appUserToken.getToken() == null || appUserToken.getToken().length() == 0) {
			appUserToken = createToken("ApiClientApp");
		}
		// System.out.println("app user token = " + appUserToken);
		return appUserToken;
	}
	
    private static Token createToken(String username) {
    	String scopes = "com.bah.msd.customerapi.api";
    	// special case for application user
    	if( username.equalsIgnoreCase("ApiClientApp")) {
    		scopes = "com.bah.msd.customerapi.api";
    	}
    	Token token = (Token) JWTHelper.createToken(scopes);
    	
		/*
		 * long fiveHoursInMillis = 1000 * 60 *60 * 5;
		 * 
		 * String token_string = Jwts.builder() .setSubject(username)
		 * .setIssuer("me@me.com") .claim("scopes",scopes) .setExpiration(new
		 * Date(System.currentTimeMillis() + fiveHoursInMillis)) .signWith(key)
		 * .compact();
		 */
    	// System.out.println("token created!!! token: " + token.toString());
    	return token;
    }
    
    
	private Customer getCustomerByNameFromCustomerAPI(String username) {
		try {
			
		// Get the value of the API_Host from the Environment
			String apiHost = System.getenv("API_HOST");
			
			// default to generic localhost:8080
			if(apiHost == null) {
				apiHost = "localhost:8080";
			}

		// Dynamic insertion of apiHost value	
			URL url = new URL("http://" + apiHost + "/api/customers/byname/" + username);	
			
			
		//	System.out.println("Breadcrumbs for getCustomerByNameFromCustomerAPI");
			System.out.println("Generated URL is " + url);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			Token token = getAppUserToken();
			
		//	System.out.println("Generated token is " + token);
			
			conn.setRequestProperty("authorization", "Bearer " + token.getToken());
			
		//	System.out.println("Created connection is " + conn);
		//	System.out.println("Response code for connection is " + conn.getResponseCode());


			if (conn.getResponseCode() != 200) {
				return null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output = "";
				String out = "";
				while ((out = br.readLine()) != null) {
					output += out;
				}
				conn.disconnect();
		//		System.out.println("Security API was able to get customer name from Customer API!!!");
				return CustomerFactory.getCustomer(output);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;

		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}

	}  	

}    

