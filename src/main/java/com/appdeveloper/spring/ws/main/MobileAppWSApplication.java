package com.appdeveloper.spring.ws.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appdeveloper.spring.ws.main.security.AppProperties;

@SpringBootApplication
public class MobileAppWSApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MobileAppWSApplication.class, args);
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
	
	@Bean(name="AppProperties")
	public AppProperties appProperties() {
		return new AppProperties();
	}

}
