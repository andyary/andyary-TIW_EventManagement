package it.polimi.tiw.events.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.events.beans.User;
import it.polimi.tiw.events.dao.EventDAO;
import it.polimi.tiw.events.utils.ConnectionHandler;

@WebServlet("/CreateEvent")
@MultipartConfig

public class CreateEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CreateEvent() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	private Date getMeYesterday() {
		return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get and parse all parameters from request
		boolean isBadRequest = false;
		String title = null;
		String description = null;
		Time time = null;
		Date date = null;
		String location = null;	
		Integer maxattendees = null;

		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");


		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
			time = (Time) stf.parse(request.getParameter("time"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = (Date) sdf.parse(request.getParameter("date"));
			location = StringEscapeUtils.escapeJava(request.getParameter("location"));
			maxattendees = Integer.parseInt(request.getParameter("maxattendees"));

			

			isBadRequest = maxattendees <= 0 || title.isEmpty() || description.isEmpty()
					|| getMeYesterday().after(date) || location.isEmpty();
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}

		// Create mission in DB
		EventDAO eventDAO = new EventDAO(connection);
		int newidevent;
		try {
			newidevent = eventDAO.createEvent(title, description, time, date, location, maxattendees, user.getId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to create event");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(newidevent);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
