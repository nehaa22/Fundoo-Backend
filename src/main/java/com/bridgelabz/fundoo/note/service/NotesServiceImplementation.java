package com.bridgelabz.fundoo.note.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

//import com.bridgelabz.fundoo.elasticsearch.ElasticSearchimpl;
import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.repository.NoteRepository;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.user.model.EmailInfo;
import com.bridgelabz.fundoo.user.model.User;
import com.bridgelabz.fundoo.user.repository.UserRepository;
import com.bridgelabz.fundoo.utility.MailServiceUtility;
import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.bridgelabz.fundoo.utility.SenderQueue;
import com.bridgelabz.fundoo.utility.TokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("NotesService")
@PropertySource("classpath:ExceptinMessages.properties")
public class NotesServiceImplementation implements NoteService {

	@Autowired
	private TokenUtility tokenUtility;

//	@Autowired
//	private RestHighLevelClient client;

	@Autowired
	private SenderQueue senderQueue;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private MailServiceUtility mailServiceUtility;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public static String Key = "user";

//	ElasticSearchimpl elasticSearchimpl = new ElasticSearchimpl();
	
	@Override
	public Response createNewNoteForUser(NoteDTO noteDTO, String emailID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);

		if (noteDTO.getTitle().isEmpty() || noteDTO.getDescription().isEmpty()) {
			throw new UserException(404, environment.getProperty("status.note.emptyTitleOrDisc"));
		}
		Note note = modelMapper.map(noteDTO, Note.class);
		Optional<User> user = userRepository.findById(userID);

		note.setCreated(LocalDateTime.now());
		note.setModified(LocalDateTime.now());
		user.get().getNotes().add(note);
		Note enote=new Note();
		enote=noteRepository.save(note);
		
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> dnote = mapper.convertValue(enote,Map.class);

		String index_name = "fundoo";
		String type_name = "note";
		
	//	IndexRequest indexRequest = new IndexRequest(index_name,type_name,String.valueOf(enote.getNoteId())).source(dnote);
//		System.out.println(indexRequest);
//		try {
//			client.index(indexRequest, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
		Response response = ResponseHelper.sendError(201, environment.getProperty("status.notes.createdSuccessfull"));
		return response;

	}

	@Override
	public Response userNoteUpdate(NoteDTO noteDTO, String emailID, long noteID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		if (noteDTO.getTitle().isEmpty() || noteDTO.getDescription().isEmpty()) {
			throw new UserException(environment.getProperty("status.note.emptyTitleOrDisc"));
		}

		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(environment.getProperty("status.user.notExist")));
		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.note.notExist")));
		user.getNotes().remove(note);
		note.setTitle(noteDTO.getTitle());
		note.setDescription(noteDTO.getDescription());
		note.setModified(LocalDateTime.now());
		user.getNotes().add(note);
		userRepository.save(user);
		noteRepository.save(note);
		Response response = ResponseHelper.sendError(301, environment.getProperty("status.note.trashed"));
		//elasticSearchimpl.updateNote(note);
		return response;
	}

	@Override
	public Response userNoteRetrieve(String emailID, long noteID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(environment.getProperty("status.user.notExist")));
		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.note.notExist")));

		String title = note.getTitle();
		System.out.println(title);

		String description = note.getDescription();
		System.out.println(description);

		Response response = ResponseHelper.sendError(200, environment.getProperty("status.note.retrive"));
		return response;
	}

	public Response noteAddToTrash(String emailID, long noteID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);

		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		if (note.isTrash() == false) {
			note.setTrash(true);
			note.setModified(LocalDateTime.now());
			noteRepository.save(note);
			Response response = ResponseHelper.sendError(301, environment.getProperty("status.note.trashed"));
			return response;
		}

		Response response = ResponseHelper.sendError(304, environment.getProperty("status.note.trashError"));
		return response;
	}

	public Response userNotedelete(String emailID, long noteID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));
		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

	//	if (note.isTrash()) {
			user.getNotes().remove(note);
			userRepository.save(user);
			noteRepository.delete(note);
			Response response = ResponseHelper.sendError(200, environment.getProperty("status.note.deleted"));
			//return response;
		//} else {
			//elasticSearchimpl.deleteNote(note);
			return response;
		
	}

	@Override
	public Response pinUnpinNote(String emailID, long noteID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		if (!note.isPin()) {
			note.setPin(true);
			noteRepository.save(note);

			Response response = ResponseHelper.sendError(301, environment.getProperty("status.note.pinned"));
			return response;
		} else {
			note.setPin(false);
			noteRepository.save(note);
			Response response = ResponseHelper.sendError(301, environment.getProperty("status.note.unpinned"));
			return response;
		}
	}

	@Override
	public Response archivedUnarchivedNote(String emailID, long noteID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		if (note.isArchieve() == false) {
			note.setArchieve(true);
			noteRepository.save(note);

			Response response = ResponseHelper.sendError(200, environment.getProperty("status.note.archieved"));
			return response;
		} else {
			note.setArchieve(false);
			noteRepository.save(note);

			Response response = ResponseHelper.sendError(200, environment.getProperty("status.note.unarchieved"));
			return response;
		}

	}

	@Override
	public Response setColourToNote(String emailID, long noteID, String color) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		note.setColour(color);
		noteRepository.save(note);

		Response response = ResponseHelper.sendError(200, environment.getProperty("status.note.color"));
		return response;
	}

	@Override
	public List<Note> getTrashedNotesOfUser(String emailID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		List<Note> userNotes = user.getNotes().stream().filter(data -> (data.isTrash() == true))
				.collect(Collectors.toList());

		return userNotes;
	}

	@Override
	public List<Note> getPinnedNoteOfUser(String emailID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);

		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		List<Note> pinedNotes = user.getNotes().stream().filter(data -> (data.isPin() == true))
				.collect(Collectors.toList());

		return pinedNotes;

	}

	@Override
	public List<Note> getAllNotesOfUser(String emailID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);

		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		List<Note> allNotes = user.getNotes();

		return allNotes;

	}

	@Override
	public List<Note> getArchievedNoteList(String emailID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		
		long userID = tokenUtility.decodeToken(token);

		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		List<Note> archieveNotes = user.getNotes().stream().filter(data -> (data.isArchieve() == true))
				.collect(Collectors.toList());

		return archieveNotes;

	}

	@Override
	public Note findNoteFromUser(String emailID, String title, String description) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userId = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream()
				.filter(data -> data.getDescription().equals(description) && data.getTitle().equals(title)).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		return note;
	}

	@Override
	public Response setReminderToNote(String emailID, long noteID, String time) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userID = tokenUtility.decodeToken(token);

		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTime = LocalDateTime.parse(time, datetimeFormatter);
		LocalDateTime CurrentDateAndTime = LocalDateTime.now();
		if (CurrentDateAndTime.compareTo(localDateTime) < 0) {

			note.setRemainder(localDateTime);
			noteRepository.save(note);
			Response response = ResponseHelper.sendError(200, environment.getProperty("note.status.remainder"));
			return response;

		}

		Response response = ResponseHelper.sendError(304, environment.getProperty("note.status.remainderfail"));

		return response;
	}

	@Override
	public Response closeReminder(String emailID, long noteID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		note.setRemainder(null);
		noteRepository.save(note);

		Response response = ResponseHelper.sendError(200, environment.getProperty("status.note.deleteRemainder"));

		return response;

	}

	@Override
	public Response collaborateNoteToUser(String emailID, String email, long noteID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		EmailInfo collaboratorEmail = new EmailInfo();
		long userID = tokenUtility.decodeToken(token);

		User owner = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		User user = userRepository.findAll().stream().filter(data -> data.getEmailId().equals(email)).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.collaborator.NoUserFound")));

		Note note = owner.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		if (user.getCollaboratedNotes().contains(note))
			throw new UserException(environment.getProperty("status.collaborator.noteIsCollaborated"));

		user.getCollaboratedNotes().add(note);
		note.getCollaboratedUsers().add(user);

		userRepository.save(user);
		noteRepository.save(note);

		collaboratorEmail.setTo(email);
		collaboratorEmail.setSubject("Note collaboration ");
		collaboratorEmail.setBody("Note from " + owner.getEmailId() + " collaborated to you\nTitle : " + note.getTitle()
				+ "\nDescription : " + note.getDescription());
		try {
			collaboratorEmail.setBody(mailServiceUtility.getLink("http://localhost:9090/user/", owner.getUserId()));
		} catch (IllegalArgumentException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			//senderQueue.produce(collaboratorEmail);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// mailServiceUtility.send(collaboratorEmail);

		Response response = ResponseHelper.sendError(201, environment.getProperty("status.collaborator.create"));
		return response;
	}

	@Override
	public Response removeCollaboratorFromNote(String emailID, String collaboratorEmail, long noteID) {
	
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userID = tokenUtility.decodeToken(token);
		User owner = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(environment.getProperty("status.user.notExist")));

		User user = userRepository.findAll().stream().filter(data -> data.getEmailId().equals(collaboratorEmail)).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.user.notExist")));

		Note note = owner.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(404, environment.getProperty("status.note.notExist")));

		user.getCollaboratedNotes().remove(note);
		note.getCollaboratedUsers().remove(user);

		userRepository.save(user);
		noteRepository.save(note);

		Response response = ResponseHelper.sendError(200, environment.getProperty("status.collaborator.deleted"));
		return response;
	}

	@Override
	public Set<Note> getAllCollaboratedNotes(String emailID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(environment.getProperty("status.user.notExist")));

		Set<Note> collaboratedNotes = user.getCollaboratedNotes();

		return collaboratedNotes;
	}

	@Override
	public Set<User> getAllCollaboratedUserOfNode(String emailID, long noteID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userID = tokenUtility.decodeToken(token);
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new UserException(environment.getProperty("status.user.notExist")));
		// Note note = noteRepository.findById(noteID).orElseThrow(() -> new
		// UserException(environment.getProperty("status.note.notExist")));
		Note note = user.getNotes().stream().filter(data -> data.getNoteId() == noteID).findFirst()
				.orElseThrow(() -> new UserException(environment.getProperty("status.note.notExist")));
		Set<User> collaboratedUsers = note.getCollaboratedUsers();
		return collaboratedUsers;
	}

	public List<Note> getSortedNotesByTitle(String emailID) {

		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userId = tokenUtility.decodeToken(token);
		List<Note> sortedNotes = userRepository.findById(userId).get().getNotes();
		sortedNotes.sort(Comparator.comparing(Note::getTitle));
		return sortedNotes;

	}

	@Override
	public List<Note> getSortedNotesByDate(String emailID) {
		
		String token = (String) redisTemplate.opsForHash().get(Key, emailID);
		long userId = tokenUtility.decodeToken(token);
		List<Note> sortedNotes = userRepository.findById(userId).get().getNotes();
		sortedNotes.sort(Comparator.comparing(Note::getCreated));
		return sortedNotes;
	}

}