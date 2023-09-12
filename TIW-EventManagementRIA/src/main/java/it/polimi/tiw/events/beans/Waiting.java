package it.polimi.tiw.events.beans;

import java.sql.Time;
import java.util.Date;

public class Waiting {

	private int idevent;
	private int idwaiter;
	private Time Time;
	private Date Date;


	public Waiting(int idevent, int idattendee) {
		this.idevent = idevent;
		this.idwaiter = idattendee;
	}
	
	public int getIdEvent() {
		return idevent;
	}

	public void setIdEvent(int id) {
		this.idevent = id;
	}

	public int getIdWaiter() {
		return idwaiter;
	}

	public void setIdWaiter(int id) {
		this.idwaiter = id;
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

}