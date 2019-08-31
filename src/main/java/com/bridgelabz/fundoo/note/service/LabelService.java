package com.bridgelabz.fundoo.note.service;

import java.util.List;

import com.bridgelabz.fundoo.note.dto.LabelDTO;
import com.bridgelabz.fundoo.note.dto.NoteDTO;
import com.bridgelabz.fundoo.response.Response;

public interface LabelService {
	
	/**
	 * Purpose :To create new label for user
	 * @param labelDto
	 * @param emailID
	 * @return
	 */
	public Response createNewLabelForUser(LabelDTO labelDto , String emailID);

	/**
	 * Purpose :To  update existence label
	 * @param labelDTO
	 * @param emailID
	 * @param labelID
	 * @return
	 */
	public Response updateExistenceLabel(LabelDTO labelDTO, String emailID, long labelID);

	/**
	 * Purpose :To delete user label 
	 * @param emailID
	 * @param labeID
	 * @return
	 */
	public Response deleteUserLabel(String emailID, long labeID);

	/**
	 * Purpose :To getting all labels of user
	 * @param emailID
	 * @return
	 */
	public List<LabelDTO> getAllLabelsOfUser(String emailID);

	/**
	 * Purpose :To remove label of particular note
	 * @param labelID
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response removeLabelFromNote(long labelID , String emailID ,long noteID);

	/**
	 * Purpose :To Add label to particular note
	 * @param labelID
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public Response addLabelToNote(long labelID, String emailID, long noteID);

	/**
	 * Purpose :To getting all labels of particular note
	 * @param emailID
	 * @param noteID
	 * @return
	 */
	public List<LabelDTO> getLabelsOfNote(String emailID, long noteID);

	/**
	 * Purpose :To getting all notes of particular label
	 * Purpose :To create new note
	 * @param emailID
	 * @param labelID
	 * @return
	 */
	public List<NoteDTO> getNotesOfLabel(String emailID, long labelID);
}
