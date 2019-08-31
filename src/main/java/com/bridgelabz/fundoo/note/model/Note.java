package com.bridgelabz.fundoo.note.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import com.bridgelabz.fundoo.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Entity
public class Note implements Serializable {

	private static final long serialVersionUID = 3891749725421598901L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long noteId;
	
	@NotNull(message = "title should not be null")
	@NotEmpty(message = "title should not be empty")
	private String title;
	
	@NotNull(message = "description should not be null")
	@NotEmpty(message = "description should not be empty")
	private String description;
	
	private String colour;
	private LocalDateTime created;
	private LocalDateTime modified;
	private boolean isPin;
	private boolean isArchieve;
	private LocalDateTime remainder;

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL , mappedBy = "Notes")
	private List<Label> listLabel;

	public List<Label> getListLabel() {
		return listLabel;
	}

	public void setListLabel(List<Label> listLabel) {
		this.listLabel = listLabel;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<User> collaboratedUsers;
	
	public Set<User> getCollaboratedUsers() {
		return collaboratedUsers;
	}

	public void setCollaboratedUsers(Set<User> collaboratedUsers) {
		this.collaboratedUsers = collaboratedUsers;
	}
	
	public LocalDateTime getRemainder() {
		return remainder;
	}

	public void setRemainder(LocalDateTime remainder) {
		this.remainder = remainder;
	}

	public boolean isArchieve() {
		return isArchieve;
	}

	public void setArchieve(boolean isArchieve) {
		this.isArchieve = isArchieve;
	}

	public boolean isPin() {
		return isPin;
	}

	public void setPin(boolean isPin) {
		this.isPin = isPin;
	}

	private boolean isTrash;

	public boolean isTrash() {
		return isTrash;
	}

	public void setTrash(boolean isTrash) {
		this.isTrash = isTrash;
	}

	public long getNoteId() {
		return noteId;
	}

	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}

	@Override
	public String toString() {
		return "Note [noteId=" + noteId + ", title=" + title + ", description=" + description + ", colour=" + colour
				+ ", created=" + created + ", modified=" + modified + ", isPin=" + isPin + ", isArchieve=" + isArchieve
				+ ", remainder=" + remainder + ", isTrash=" + isTrash + ", listLabel=" + listLabel
				+ ", collaboratedUsers=" + collaboratedUsers + "]";
	}
}
