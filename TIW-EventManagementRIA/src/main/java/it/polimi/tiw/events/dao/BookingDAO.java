package it.polimi.tiw.events.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.events.beans.Booking;

public class BookingDAO {
	private Connection connection;

	public BookingDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Booking> findBookingbyEvent(int idevent) throws SQLException {
		List<Booking> bookings = new ArrayList<Booking>();

		String query = "SELECT * FROM booking WHERE idevent = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					Booking booking = new Booking(result.getInt("idevent"), result.getInt("idattendee"));
					bookings.add(booking);
				}
			}
		}
		return bookings;
	}

	public void addBooking(int idevent, int idattendee) throws SQLException {
		String query = "INSERT into booking (idevent, idattendee)   VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, idattendee);
			pstatement.executeUpdate();
		}
	}

	public void delBooking(int idevent, int idattendee) throws SQLException {
		;
		String query = "DELETE FROM booking WHERE (idevent = ? AND idattendee = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, idattendee);
			pstatement.executeUpdate();
		}
	}

	
	
	public Booking findBooking(int idevent, int idattendee) throws SQLException {
		String query = "SELECT  * FROM booking  WHERE idevent = ? AND idattendee =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, idattendee);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Booking booking = new Booking(result.getInt("idevent"), result.getInt("idattendee"));
					return booking;
				}
			}
		}
	}

	public boolean checkBooking(int idevent, int idattendee) throws SQLException {
		String query = "SELECT  * FROM booking  WHERE idevent = ? AND idattendee =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, idattendee);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return false;
				else {
					return true;
				}
			}
		}
	}

	public int countBookingByEvent(int idevent) throws SQLException {
		String query = "SELECT  COUNT(*) as count FROM booking WHERE idevent = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				return result.getInt("count");

			}
		}
	}
}
