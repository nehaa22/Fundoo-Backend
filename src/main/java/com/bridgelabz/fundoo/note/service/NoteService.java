package com.bridgelabz.fundoo.note.service;

import java.util.List;
import java.util.Set;

import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.user.model.User;

public interface NoteService {

	
	/**
	 * Purpose :To create new note
	 * @param notedto
	 * @param emailID
	 * @return
	 */
	public Response createNewNoteForUser(NoteDTO notedto, String emailID);
	
	/** 
	 * Purpose :To Update Note
	 * @param notedto
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response userNoteUpdate(NoteDTO notedto, String emailID, long noteID);	
	/**
	 * Purpose : Retrieve user Note
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response userNoteRetrieve(String emailID, long noteID);
	/**
	 * Purpose :To Add to trash
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response noteAddToTrash(String token, long noteID);
	/**
	 * Purpose :To Delete note permanently
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response userNotedelete(String emailID, long noteID);
	/**
	 * To set note pin or unpin 
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response pinUnpinNote(String emailID, long noteID);
	/**
	 * @param emailID
	 * @return
	 */
	public List<Note> getTrashedNotesOfUser(String emailID);
	/**
	 * To set note archived or unarchived
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response archivedUnarchivedNote(String emailID, long noteID);
	/**
	 * To Set Color to note
	 * @param emailID
	 * @param noteID
	 * @param color
	 * @return
	 */
	public Response setColourToNote(String emailID, long noteID, String color);
	/**
	 * To getting all pined notes of particular users
	 * @param emailID
	 * @return
	 */
	public List<Note> getPinnedNoteOfUser(String emailID);
	/**
	 * To getting all Archived notes of particular users 
	 * @param emailID
	 * @return
	 */
	public List<Note> getArchievedNoteList(String emailID);
	/**
	 * To find particular note from user
	 * @param emailID
	 * @param title
	 * @param description
	 * @return
	 */
	public Note findNoteFromUser(String emailID, String title, String description);
	/**
	 * To Set reminder to particular note
	 * @param emailID
	 * @param noteID
	 * @param time
	 * @return
	 */
	public Response setReminderToNote(String emailID, long noteID, String time);
	/**
	 * To Delete reminder
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response closeReminder(String emailID, long noteID);
	/**
	 * To getting all Collaborated users of particular note
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	Set<User> getAllCollaboratedUserOfNode(String emailID, long noteID);
	/**
	 * To Add Collaborator to note
	 * @param emailID
	 * @param collaboratorEmail
	 * @param noteID
	 * @return
	 */
	Response collaborateNoteToUser(String emailID, String collaboratorEmail, long noteID);
	/**
	 * To remove Collaborator from note
	 * @param emailID
	 * @param collaboratorEmail
	 * @param noteID
	 * @return
	 */
	Response removeCollaboratorFromNote(String emailID, String collaboratorEmail, long noteID);
	/**
	 * To getting all collaborated notes 
	 * @param emailID
	 * @return
	 */
	Set<Note> getAllCollaboratedNotes(String emailID);
	/**
	 * To getting all notes
	 * @param emailID
	 * @return
	 */
	List<Note> getAllNotesOfUser(String emailID);
	/**
	 * Purpose :To getting all sorted notes by title
	 * @param emailID
	 * @return
	 */
	public List<Note> getSortedNotesByTitle(String emailID);
	/**
	 * Purpose :To getting all sorted notes by date
	 * @param emailID
	 * @return
	 */
	public List<Note> getSortedNotesByDate(String emailID);

	//void cacheNotesDetails(boolean checkFlag) throws Exception;
	
	
	
}