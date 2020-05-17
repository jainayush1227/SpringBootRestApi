package com.appdeveloper.spring.ws.main.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.appdeveloper.spring.ws.main.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>{
	
	UserEntity findByEmail(String email);

	UserEntity findByUserId(String userId);
	
	UserEntity findUserByEmailVerificationToken( String token);
	
	

}
