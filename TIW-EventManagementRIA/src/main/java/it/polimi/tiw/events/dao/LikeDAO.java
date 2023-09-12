package it.polimi.tiw.events.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.events.beans.Like;

public class LikeDAO {
	private Connection connection;

	public LikeDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Like> findLikebyEvent(int idevent) throws SQLException {
		List<Like> likes = new ArrayList<Like>();

		String query = "SELECT * FROM like WHERE idevent = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					Like like = new Like(result.getInt("idevent"), result.getInt("idliker"));
					likes.add(like);
				}
			}
		}
		return likes;
	}

	public void addLike(int idevent, int iduser) throws SQLException {
		String query = "INSERT into like (idevent, idliker)   VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, iduser);
			pstatement.executeUpdate();
		}
	}

	public void delLike(int idevent, int idliker) throws SQLException {
		;
		String query = "DELETE FROM like WHERE (idevent = ? AND idliker = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, idliker);
			pstatement.executeUpdate();
		}
	}

	
	
	public Like findLike(int idevent, int idliker) throws SQLException {
		String query = "SELECT  * FROM like  WHERE idevent = ? AND idliker =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, idliker);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Like like = new Like(result.getInt("idevent"), result.getInt("idliker"));
					return like;
				}
			}
		}
	}

	public boolean checkLike(int idevent, int iduser) throws SQLException {
		String query = "SELECT * FROM like  WHERE idevent = ? AND idliker =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			pstatement.setInt(2, iduser);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return false;
				else {
					return true;
				}
			}
		}
	}

	public int countLikeByEvent(int idevent) throws SQLException {
		String query = "SELECT COUNT(*) as count FROM like WHERE idevent = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idevent);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				return result.getInt("count");

			}
		}
	}
}
