package com.sna.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sna.exception.ResourceNotFoundException;
import com.sna.model.CustomerToken;
import com.sna.repository.CustomerTokenRepository;

@RestController
@RequestMapping("/api/customer/tokens")
public class CustomerTokenController {

	private static final Logger log = LogManager.getLogger(CustomerTokenController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CustomerTokenRepository customerTokenRepository;

	@GetMapping
	public List<CustomerToken> getAllCustomerTokens() {
		return customerTokenRepository.findAll();
	}

	@PostMapping
	public List<CustomerToken> createCustomerToken(@RequestBody List<CustomerToken> customerTokens) {
		return customerTokenRepository.saveAll(customerTokens);
	}

	@GetMapping("/{id}")
	public CustomerToken getCustomerTokenById(@PathVariable(value = "id") Long customerTokenId) {
		return customerTokenRepository.findById(customerTokenId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer Token Not Found ..."));
	}
	
	@GetMapping("/")
	public List<CustomerToken> getCustomerRecords() {
		return customerTokenRepository.findAll();	}

	@PutMapping("/updateRecord/{id}")
	public CustomerToken updateCustomerToken(@PathVariable(value = "id") Long customerTokenId,
			@RequestBody CustomerToken customerToken) {

		CustomerToken customerTokens = customerTokenRepository.findById(customerTokenId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer Token Not Found ..."));

		customerTokens.setCustomerName(customerToken.getCustomerName());
		customerTokens.setCustomerTokenId(customerToken.getCustomerTokenId());
		customerTokens.setExpiryDate(customerToken.getExpiryDate());
		customerTokens.setIssueDate(customerToken.getIssueDate());
		customerTokens.setStatus(customerToken.getStatus());
		customerTokens.setTokenType(customerToken.getTokenType());
		CustomerToken updatedCustomerToken = customerTokenRepository.save(customerTokens);

		return updatedCustomerToken;
	}
	
	@PutMapping("/")
	public HashMap<String, Integer> expireCustomerToken(@RequestHeader(value = "excludeAmbassadors") Boolean excludeAmbassadors) {

	int totalRecords = customerTokenRepository.getTotalRecords();
	int ambassadorRecords = customerTokenRepository.getTotalAmbassadorsRecords();
	int currentExpiredRecords = customerTokenRepository.getTotalExpiredRecords();
		if(!excludeAmbassadors) {
			customerTokenRepository.expireTokens();
		}else {
			customerTokenRepository.skipAmbassadorAndExpireTokens();
		}
		
	int totalExpiredRecords = customerTokenRepository.getTotalExpiredRecords();
	int recordsUpdated = totalExpiredRecords - currentExpiredRecords;
	
	HashMap<String, Integer> Records=new HashMap<String, Integer>();   
	Records.put("Total Records Read", totalRecords);
	Records.put("Total Number of Ambassadors", ambassadorRecords); 
	Records.put("Total SNA Expired", recordsUpdated); 
	Records.put("Total Customers EXP history", currentExpiredRecords); 
	
	ResponseEntity<String> result = restTemplate.postForEntity("http://customer-email-service/api/customer/emails",
			Records, String.class);
	
	return Records;
	
	}

}
