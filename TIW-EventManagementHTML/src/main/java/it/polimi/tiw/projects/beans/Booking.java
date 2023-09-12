package it.polimi.tiw.projects.beans;

public class Booking {

	private int idevent;
	private int idattendee;


	
	public Booking(int idevent, int idattendee) {
		this.idevent = idevent;
		this.idattendee = idattendee;
	}
	
	public int getIdEvent() {
		return idevent;
	}

	public void setIdEvent(int id) {
		this.idevent = id;
	}

	public int getIdAttendee() {
		return idattendee;
	}

	public void setIdAttendee(int id) {
		this.idattendee = id;
	}
	
}