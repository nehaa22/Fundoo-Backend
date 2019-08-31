package com.bridgelabz.fundoo.user.controler;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.response.TokenResponse;
import com.bridgelabz.fundoo.user.dto.LoginDTO;
import com.bridgelabz.fundoo.user.dto.UserDTO;
import com.bridgelabz.fundoo.user.service.UserService;

@RequestMapping("/User")
@RestController
public class UserController {
	
	@Autowired()
	UserService userService;
	
//REST API for User registration
	
	@PostMapping("/Registretion")
	public ResponseEntity<Response> userRegistretion(@RequestBody UserDTO userDTO)
			throws UserException, UnsupportedEncodingException {

		Response response = userService.newUserRegistration(userDTO);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}

//REST API for User login
	
	@PostMapping("/Login")
	public ResponseEntity<TokenResponse> userLogin(@RequestBody LoginDTO loginDTO)
			throws UserException, UnsupportedEncodingException {

		TokenResponse response = userService.existenceUserLogin(loginDTO);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//REST API for User email validation
	
	@GetMapping(value = "/{token}")
	public ResponseEntity<Response> validateEmailID(@PathVariable String token) throws UserException {

		Response response = userService.validationOfEmailId(token);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

//REST API for User reset password link
	
	@GetMapping("/SendResetPasswordLink")
	public ResponseEntity<Response> forgotPassword(@RequestParam String emailID)
			throws UnsupportedEncodingException, UserException, MessagingException {
		
		Response status = userService.forgetPasswordLink(emailID);
		return new ResponseEntity<Response>(status, HttpStatus.OK);

	}

//REST API for reset password
	
	@PutMapping(value = "/PasswordReset{emailID}")
	public ResponseEntity<Response> resetPassword(@RequestParam String emailID, @RequestParam("password") String password)
			throws UserException {
		Response response = userService.resetUserPassword(emailID, password);
		return new ResponseEntity<Response>(response, HttpStatus.OK);

	}
	
}