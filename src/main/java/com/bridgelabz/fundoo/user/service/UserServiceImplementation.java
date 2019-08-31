package com.bridgelabz.fundoo.user.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.service.NotesCacheManager;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.response.TokenResponse;
import com.bridgelabz.fundoo.user.dto.LoginDTO;
import com.bridgelabz.fundoo.user.dto.UserDTO;
import com.bridgelabz.fundoo.user.model.EmailInfo;
import com.bridgelabz.fundoo.user.model.User;
import com.bridgelabz.fundoo.user.repository.UserRepository;
import com.bridgelabz.fundoo.utility.MailServiceUtility;
import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.bridgelabz.fundoo.utility.SenderQueue;
import com.bridgelabz.fundoo.utility.TokenUtility;

@PropertySource("classpath:ExceptinMessages.properties")
@Service("userService")
public class UserServiceImplementation implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private EmailInfo emailInfo;

	@Autowired
	private SenderQueue senderQueue;

	@Autowired
	private MailServiceUtility mailServiceUtility;

	@Autowired
	private Response response;

	@Autowired
	private Environment environment;

	@Autowired
	private TokenUtility tokenUtility;

	//private NotesCacheManager notesCacheManager;

	public static String Key = "user";

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

//	@Autowired
//	public UserServiceImplementation(NotesCacheManager redisCacheManager) {
//		this.notesCacheManager = redisCacheManager;
//	}

	@Override
	public Response newUserRegistration(UserDTO userDTO) {

		Optional<User> userIsPresent = userRepository.findAll().stream()
				.filter(data -> data.getEmailId().equals(userDTO.getEmailId())).findFirst();
		if (userIsPresent.isPresent()) {
			throw new UserException(201, environment.getProperty("status.register.emailExistError"));
			// System.out.println(userIsPresent.isPresent());
		}
		System.out.println(userIsPresent.isPresent());

		String password = passwordEncoder.encode(userDTO.getPassword());

		User user = modelMapper.map(userDTO, User.class);
		user.setPassword(password);
		user = userRepository.save(user);

		emailInfo.setTo(userDTO.getEmailId());
		emailInfo.setSubject("Email Verification ");

		try {
			emailInfo.setBody(mailServiceUtility.getLink("http://localhost:9090/User/", user.getUserId()));
		} catch (IllegalArgumentException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// mailServiceUtility.send(emailInfo);

		try {
				mailServiceUtility.send(emailInfo);
			//senderQueue.produce(emailInfo);
		} catch (java.lang.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response = ResponseHelper.sendError(201, environment.getProperty("status.register.success"));
		return response;

	}

	@Override
	public TokenResponse existenceUserLogin(LoginDTO loginDTO) throws UserException, UnsupportedEncodingException {

		User user = userRepository.findAll().stream().filter(data -> data.getEmailId().equals(loginDTO.getEmailId()))
				.findFirst().orElseThrow(() -> new UserException(environment.getProperty("status.login.invalidInput")));

		TokenResponse response = new TokenResponse();

		if (user.isVerify()) {
			boolean status = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());

			if (status) {

				// Optional<User> users = userRepository.findById(user.getUserId());
				// users.forEach(data -> notesCacheManager.cacheNoteDetails(data));

				String token = tokenUtility.genetateToken(user.getUserId());
				response.setToken(token);
				response.setStatusCode(200);
				response.setStatusMessage(environment.getProperty("status.login.success"));
				
				redisTemplate.opsForHash().put(Key, user.getEmailId(), token);
				//User user1 = (User) redisTemplate.opsForHash().get(Key, user.getUserId());
				//System.out.println("from Redis getting :" + user1.getFirstName() + "\t" + user1.getLastName());
				return response;
			}

			throw new UserException(401, environment.getProperty("user.login.password"));
		}

		throw new UserException(401, environment.getProperty("user.login.verification"));

	}

	@Override
	public Response validationOfEmailId(String token) {
		Long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));
		user.setVerify(true);
		userRepository.save(user);
		response = ResponseHelper.sendError(200, environment.getProperty("status.email.verified"));
		return response;
	}

	@Override
	public Response resetUserPassword(String emailID, String password) {
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		Long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("user.resetpassword.user")));
		String encodedpassword = passwordEncoder.encode(password);
		user.setPassword(encodedpassword);
		userRepository.save(user);
		return ResponseHelper.sendError(200, environment.getProperty("status.resetPassword.success"));

	}

	@Override
	public Response forgetPasswordLink(String emailID) throws UserException, UnsupportedEncodingException {
		User user = userRepository.findAll().stream().filter(data -> data.getEmailId().equals(emailID)).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.email.invalidMail")));

		emailInfo.setTo(emailID);
		emailInfo.setSubject("Forget Password Link ");

		try {
			emailInfo.setBody(
					mailServiceUtility.getLink("http://localhost:9090/user/PasswordReset{token}", user.getUserId()));
		} catch (IllegalArgumentException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		mailServiceUtility.send(emailInfo);

		return ResponseHelper.sendError(200, environment.getProperty("status.forgetPassword.success"));
	}

}
