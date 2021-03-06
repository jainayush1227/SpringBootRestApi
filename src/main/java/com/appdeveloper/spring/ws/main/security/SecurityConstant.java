package com.appdeveloper.spring.ws.main.security;

import com.appdeveloper.spring.ws.main.SpringApplicationContext;

public class SecurityConstant {

	public static final long EXPIRATION_TIME = 864000000;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";
	public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
	
	public static String getTokenSecret() {
		AppProperties appProperties=(AppProperties)SpringApplicationContext.getBean("AppProperties");
		return appProperties.getTokenSecret();
	}

}
