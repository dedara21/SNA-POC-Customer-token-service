package com.sna.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.sna.model.CustomerToken;

public interface CustomerTokenRepository extends JpaRepository<CustomerToken, Long>{
	
	
	@Modifying
	@Transactional
	@Query(value = "update customer_token set status = '6' where status = '1' and token_type = 'SNA'", nativeQuery = true)
	void expireTokens();
	
	@Modifying
	@Transactional
	@Query(value = "update customer_token set status = '6' where status = '1' and token_type = 'SNA' and level_id = 'R'", nativeQuery = true)
	void skipAmbassadorAndExpireTokens();
	
	
	@Query(value = "select count(status) from customer_token", nativeQuery = true)
	int getTotalRecords();
	
	@Query(value = "select count(status) from customer_token where status = '6'", nativeQuery = true)
	int getTotalExpiredRecords();
	
	@Query(value = "select count(*) from customer_token where level_id = 'A'", nativeQuery = true)
	int getTotalAmbassadorsRecords();
}
