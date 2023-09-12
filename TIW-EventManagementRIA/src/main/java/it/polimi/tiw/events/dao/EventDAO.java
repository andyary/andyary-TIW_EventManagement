package it.polimi.tiw.events.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.events.beans.Event;


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
	
	public List<Event> findBookedEventByUser(int iduser) throws SQLException {
		List<Event> events = new ArrayList<Event>();
		String query = "SELECT * from event as e JOIN booking as b where ((e.date > CURDATE() OR (e.date = CURDATE() AND e.time > CURTIME())) AND b.idattendee = ? AND e.idevent = b.idevent) ORDER BY date, time DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, iduser);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Event event = new Event();
					event.setId(result.getInt("e.idevent"));
					event.setTitle(result.getString("e.title"));
					event.setDescription(result.getString("e.description"));
					event.setTime(result.getTime("e.time"));
					event.setDate(result.getDate("e.date"));
					event.setLocation(result.getString("e.location"));
					event.setMaxAttendees(result.getInt("e.maxattendees"));
					event.setOwner(result.getInt("e.owner"));
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

	
	public int createEvent(String title, String description, Time time, Date date, String location, int maxattendees, int owner) throws SQLException {

		String query = "INSERT into event (title, description, time, date, location, maxattendees, owner) VALUES(?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

			pstatement.setString(1, title);
			pstatement.setString(2, description);
			pstatement.setTime(3, time);
			pstatement.setDate(4, (java.sql.Date) date);
			pstatement.setString(5, location);
			pstatement.setInt(6, maxattendees);
			pstatement.setInt(7, owner);
			pstatement.executeUpdate();
// https://stackoverflow.com/questions/19873190/statement-getgeneratedkeys-method
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			} else {
				throw new SQLException("Creating user failed, no ID obtained.");
			}
		}
	}

}

