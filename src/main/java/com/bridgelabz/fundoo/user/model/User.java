package com.bridgelabz.fundoo.user.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import com.bridgelabz.fundoo.note.model.Label;
import com.bridgelabz.fundoo.note.model.Note;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Component
public class User  implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userId")
	private Long userId;
	
	@NotNull(message = "first name should not be null")
	@NotEmpty(message = "first name should not be empty")
	private String firstName;
	
	@NotNull(message = "last name should not be null")
	@NotEmpty(message = "last name should not be empty")
	private String lastName;
	
	@NotNull(message = "email ID should not be null")
	@NotEmpty(message = "email ID should not be empty")
	@javax.validation.constraints.Email
	private String emailId;
	
	@NotNull(message = "password should not be null")
	@NotEmpty(message = "password should not be empty")
	private String password;
	
	@Size(min=10,max=10,message = "mobile number must be 10 degits")
	@Pattern(regexp="(^$|[0-9]{10})",message = "mobile number must be in degits")
	private String mobileNum;
	
	private boolean isVerify;
	
	private LocalDateTime registerDate = LocalDateTime.now();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	private List<Note> notes;

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	private List<Label> labels;

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabel(List<Label> labels) {
		this.labels = labels;
	}

	@JsonIgnore
	@ManyToMany(mappedBy = "collaboratedUsers", cascade = CascadeType.ALL)
	private Set<Note> collaboratedNotes;

	public Set<Note> getCollaboratedNotes() {
		return collaboratedNotes;
	}

	public void setCollaboratedNotes(Set<Note> collaboratedNotes) {
		this.collaboratedNotes = collaboratedNotes;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public boolean isVerify() {
		return isVerify;
	}

	public void setVerify(boolean isVerify) {
		this.isVerify = isVerify;
	}

	public LocalDateTime getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDateTime registerDate) {
		this.registerDate = registerDate;
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId
				+ ", password=" + password + ", mobileNum=" + mobileNum + ", isVerify=" + isVerify + ", registerDate="
				+ registerDate + ", notes=" + notes + ", labels=" + labels + ", collaboratedNotes=" + collaboratedNotes
				+ "]";
	}
	
}