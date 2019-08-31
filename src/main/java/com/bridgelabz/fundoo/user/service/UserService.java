package com.bridgelabz.fundoo.user.service;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.response.TokenResponse;
import com.bridgelabz.fundoo.user.dto.LoginDTO;
import com.bridgelabz.fundoo.user.dto.UserDTO;

@Service
public interface UserService {

	/**
	 * Purpose :To user login
	 * @param loginDto
	 * @return
	 * @throws UserException
	 * @throws UnsupportedEncodingException
	 */
	TokenResponse existenceUserLogin(LoginDTO loginDto) throws UserException, UnsupportedEncodingException;

	/**
	 * Purpose :To validate new user Email ID
	 * @param token
	 * @return
	 */
	Response validationOfEmailId(String token);

	/**
	 * Purpose :To Reset password
	 * @param emailID
	 * @param password
	 * @return
	 */
	Response resetUserPassword(String emailID, String password);

	/**
	 * Purpose :To send reset password link
	 * @param emailId
	 * @return
	 * @throws UserException
	 * @throws UnsupportedEncodingException
	 */
	Response forgetPasswordLink(String emailId) throws UserException, UnsupportedEncodingException;

	/**
	 * Purpose :To Register New User
	 * @param userDto
	 * @return 
	 */
	Response newUserRegistration(UserDTO userDto);

}
