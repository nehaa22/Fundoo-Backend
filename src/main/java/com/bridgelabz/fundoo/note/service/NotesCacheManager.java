package com.bridgelabz.fundoo.note.service;

import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.user.model.User;

public interface NotesCacheManager {
	void cacheNoteDetails(User user);

	// void cacheNoteDetails1(User user);
}
