package com.appdeveloper.spring.ws.main.helper;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.appdeveloper.spring.ws.main.security.SecurityConstant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class Utils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return new String(returnValue);
	}

	public static boolean hasTokenExpired(String token) {

		Claims claims = Jwts.parser().setSigningKey(SecurityConstant.getTokenSecret()).parseClaimsJws(token).getBody();
		Date tokenExpirationDate = claims.getExpiration();
		Date today = new Date();

		return tokenExpirationDate.before(today);
	}
	
	public String generateEmailVerificationToken(String userId){
		String token=Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis()+SecurityConstant.EXPIRATION_TIME)).
				signWith(SignatureAlgorithm.HS512, SecurityConstant.getTokenSecret()).compact();
		return token;
		
	}

}
