package it.polimi.tiw.events.beans;

public class Like {

	private int idevent;
	private int idliker;


	
	public Like(int idevent, int iduser) {
		this.idevent = idevent;
		this.idliker = iduser;
	}
	
	public int getIdEvent() {
		return idevent;
	}

	public void setIdEvent(int id) {
		this.idevent = id;
	}

	public int getIdLiker() {
		return idliker;
	}

	public void setIdLiker(int id) {
		this.idliker = id;
	}
	
}