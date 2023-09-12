package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Event;


public class EventDAO {
	private Connection connection;

	public EventDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Event> findOwnEventByUser(int iduser) throws SQLException {
		List<Event> events = new ArrayList<Event>();
		String query = "SELECT * from event where ((date > CURDATE() OR (date = CURDATE() AND time > CURTIME())) AND owner = ?) ORDER BY date, time DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, iduser);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Event event = new Event();
					event.setId(result.getInt("idevent"));
					event.setTitle(result.getString("title"));
					event.setDescription(result.getString("description"));
					event.setTime(result.getTime("time"));
					event.setDate(result.getDate("date"));
					event.setLocation(result.getString("location"));
					event.setMaxAttendees(result.getInt("maxattendees"));
					event.setOwner(result.getInt("owner"));
					events.add(event);
				}
			}
		}
		return events;
	}

	public List<Event> findNotOwnEventByUser(int iduser) throws SQLException {
		List<Event> events = new ArrayList<Event>();
		String query = "SELECT * from event where ((date > CURDATE() OR (date = CURDATE() AND time > CURTIME())) AND owner != ?) ORDER BY date, time DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, iduser);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Event event = new Event();
					event.setId(result.getInt("idevent"));
					event.setTitle(result.getString("title"));
					event.setDescription(result.getString("description"));
					event.setTime(result.getTime("time"));
					event.setDate(result.getDate("date"));
					event.setLocation(result.getString("location"));
					event.setMaxAttendees(result.getInt("maxattendees"));
					event.setOwner(result.getInt("owner"));
					events.add(event);
				}
			}
		}
		return events;
	}
	
	public Event findEventById(int idevent) throws SQLException {
		Event event = null;

		String query = "SELECT * FROM event WHERE idevent = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					event = new Event();
					event.setId(result.getInt("idevent"));
					event.setTitle(result.getString("title"));
					event.setDescription(result.getString("description"));
					event.setTime(result.getTime("time"));
					event.setDate(result.getDate("date"));
					event.setLocation(result.getString("location"));
					event.setMaxAttendees(result.getInt("maxattendees"));
					event.setOwner(result.getInt("owner"));
				}
			}
		}
		return event;
	}
	// aggiungere nome dell'owner ed elenco dei partecipanti tramite join	
	
	
}
