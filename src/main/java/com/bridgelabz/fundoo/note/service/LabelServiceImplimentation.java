package com.bridgelabz.fundoo.note.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.note.dto.LabelDTO;
import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.note.model.Label;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.repository.LabelRepository;
import com.bridgelabz.fundoo.note.repository.NoteRepository;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.user.model.User;
import com.bridgelabz.fundoo.user.repository.UserRepository;
import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.bridgelabz.fundoo.utility.TokenUtility;

@Service("LableService")
@PropertySource("classpath:ExceptinMessages.properties")
public class LabelServiceImplimentation implements LabelService {

	@Autowired
	private TokenUtility tokenUtility;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private NoteRepository noteRepository;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public static String Key = "user";

	public Response createNewLabelForUser(LabelDTO labelDTO, String emailID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
			new UserException(environment.getProperty("status.user.notExist")));
	
		Optional<Label> label1 = user.getLabels().stream().filter(data -> data.getLabelName().equals(labelDTO.getLabelName())).findFirst();
		
		if(label1.isPresent()) {
			throw new UserException(environment.getProperty("status.label.exist"));
		}

		Label label = modelMapper.map(labelDTO, Label.class);

		label.setLabelName(labelDTO.getLabelName());
		label.setCreated(LocalDateTime.now());
		label.setModified(LocalDateTime.now());

		user.getLabels().add(label);

		labelRepository.save(label);
		userRepository.save(user);

		Response response = ResponseHelper.sendError(201, environment.getProperty("status.label.created"));
		return response;
	}

	@Override
	public Response updateExistenceLabel(LabelDTO labelDTO, String emailID, long labelID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
			new UserException(environment.getProperty("status.user.notExist")));
		
		Label label = user.getLabels().stream().filter(data -> data.getLabelId()==labelID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.label.notexist")));

		Optional<Label>labelPresent = user.getLabels().stream().filter(data -> data.getLabelName()
				.equals(labelDTO.getLabelName())).findFirst();

		if (labelPresent.isPresent()) {
			throw new UserException(environment.getProperty("status.label.exist"));
		}

		label.setLabelName(labelDTO.getLabelName());
		label.setModified(LocalDateTime.now());
		labelRepository.save(label);

		Response response = ResponseHelper.sendError(202, environment.getProperty("status.label.updated"));
		return response;

	}

	@Override
	public Response deleteUserLabel(String emailID, long labelID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
		new UserException(404,environment.getProperty("status.user.notExist")));
	
		Label label = user.getLabels().stream().filter(data -> data.getLabelId()==labelID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.label.notexist")));

		user.getLabels().remove(label);
		label.getNotes().clear();
		
		userRepository.save(user);
		labelRepository.save(label);
		labelRepository.delete(label);
		Response response = ResponseHelper.sendError(200, environment.getProperty("status.label.deleted"));
		return response;
	}

	public List<LabelDTO> getAllLabelsOfUser(String emailID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
		new UserException(environment.getProperty("status.user.notExist")));
	
		List<Label>lables = user.getLabels();

		List<LabelDTO> listLabels = new ArrayList<>();
		
		for (Label noteLabel : lables) {
			LabelDTO labelDto = modelMapper.map(noteLabel, LabelDTO.class);
			listLabels.add(labelDto);
		}
		return listLabels;
	}

	@Override
	public Response addLabelToNote(long labelID, String emailID, long noteID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
		new UserException(environment.getProperty("status.user.notExist")));
	
		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst().
				orElseThrow(() -> new UserException(environment.getProperty("status.note.notExist")));
		
		Label label = user.getLabels().stream().filter(data -> data.getLabelId()==labelID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.label.notexist")));

		note.getListLabel().add(label);
		note.setModified(LocalDateTime.now());
		label.getNotes().add(note);
		label.setModified(LocalDateTime.now());
		noteRepository.save(note);
		labelRepository.save(label);

		Response response = ResponseHelper.sendError(200, environment.getProperty("status.label.added"));
		return response;
	}

	public List<LabelDTO> getLabelsOfNote(String emailID, long noteID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
		new UserException(environment.getProperty("status.user.notExist")));
	
		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst().
				orElseThrow(() -> new UserException(environment.getProperty("status.note.notExist")));
		
		List<Label> label = note.getListLabel();
		List<LabelDTO> listLabels = new ArrayList<>();
		for (Label noteLabel : label) {
			LabelDTO labelDto = modelMapper.map(noteLabel, LabelDTO.class);
			listLabels.add(labelDto);
		}
		return listLabels;
	}

	@Override
	public Response removeLabelFromNote(long labelID, String emailID, long noteID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
		new UserException(environment.getProperty("status.user.notExist")));

		Label label = user.getLabels().stream().filter(data -> data.getLabelId()==labelID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.label.notexist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst().
				orElseThrow(() -> new UserException(environment.getProperty("status.note.notExist")));
		label.getNotes().remove(note);
		//note.getListLabel().remove(label);
		note.setModified(LocalDateTime.now());
		noteRepository.save(note);
		labelRepository.save(label);
		Response response = ResponseHelper.sendError(100, environment.getProperty("status.label.removed"));
		return response;
	}

	@Override
	public List<NoteDTO> getNotesOfLabel(String emailID, long labelID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID).orElseThrow(() -> 
		new UserException(environment.getProperty("status.user.notExist")));
	
		Label label = user.getLabels().stream().filter(data -> data.getLabelId()==labelID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.label.notexist")));
		
		List<Note> notes = label.getNotes();

		List<NoteDTO> noteList = new ArrayList<>();

		for (Note note : notes) {
			NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);
			noteList.add(noteDto);
		}
		return noteList;
	}
}
