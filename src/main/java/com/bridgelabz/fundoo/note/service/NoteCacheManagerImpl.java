package com.bridgelabz.fundoo.note.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.user.model.User;
import com.bridgelabz.fundoo.utility.RedisUtil;

@Configuration
public class NoteCacheManagerImpl implements NotesCacheManager {

	public static final String TABLE_NOTE = "TABLE_note";

	public static final String NOTE = "note_";

	private RedisUtil<User> redisUtilNotes;

	@Autowired
	public NoteCacheManagerImpl(RedisUtil<User> redisUtilNotes) {

		this.redisUtilNotes = redisUtilNotes;

	}

	@Override
	public void cacheNoteDetails(User note) {

		System.out.println("inside ");
		redisUtilNotes.putMap(TABLE_NOTE, NOTE + note.getUserId(), note);
		System.out.println("inside 1");

		redisUtilNotes.setExpire(TABLE_NOTE, 1, TimeUnit.DAYS);
		System.out.println("inside 2");

	}
}