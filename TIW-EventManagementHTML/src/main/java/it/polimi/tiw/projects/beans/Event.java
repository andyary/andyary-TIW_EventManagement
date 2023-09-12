package it.polimi.tiw.projects.beans;

import java.sql.Time;
import java.util.Date;

public class Event {
	private int idevent;
	private String Title;
	private String Description;
	private Time Time;
	private Date Date;
	private String Location;	
	private int maxattendees;
	private int owner;
	
	public int getId() {
		return idevent;
	}

	public void setId(int id) {
		this.idevent = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
			this.Description = Description;	
	}
	
	public Time getTime() {
		return Time;
	}
	
	public void setTime(Time time) {
		this.Time = time;
	}
	
	public Date getDate() {
		return Date;
	}
	
	public void setDate(Date date) {
		this.Date = date;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String Location) {
		this.Location = Location;
	}
	
	public int getMaxAttendees() {
		return maxattendees;
	}

	public void setMaxAttendees(int maxattendees) {
		this.maxattendees = maxattendees;
	}
	
	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
	
}
