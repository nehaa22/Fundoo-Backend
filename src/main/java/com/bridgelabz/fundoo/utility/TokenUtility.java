package com.bridgelabz.fundoo.utility;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.Verification;

@Component
public class TokenUtility {
	public final String TOKEN_SECRET = "sd5745FAHFW";

	//generate token
	
	public String genetateToken(Long id) {
		try {
			// to set algorithmCreates a new Algorithm instance using HmacSHA256. Tokens specify this as "HS256".
			Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
			// payload
			String token = JWT.create().withClaim("userId", id).sign(algorithm);
			return token;
		} catch (JWTCreationException exception) {
			exception.printStackTrace();
			// log Token Signing Failed
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	// decode token
	
	public Long decodeToken(String token) {
		Long userid;
		// for verification algorithm
		Verification verification = null;
		try {
			verification = JWT.require(Algorithm.HMAC256(TOKEN_SECRET));
		} catch (IllegalArgumentException e) {
	
			e.printStackTrace();
		}
		JWTVerifier jwtverifier = verification.build();
		// to decode token
		DecodedJWT decodedjwt = jwtverifier.verify(token);

		Claim claim = decodedjwt.getClaim("userId");
		userid = claim.asLong();
		return userid;

	}

}
