package it.polimi.tiw.events.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.events.beans.Event;
import it.polimi.tiw.events.beans.User;
import it.polimi.tiw.events.dao.EventDAO;
import it.polimi.tiw.events.utils.ConnectionHandler;

@WebServlet("/GetEventsData")
@MultipartConfig
public class GetEventsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetEventsData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		EventDAO eventDAO = new EventDAO(connection);
		List<Event> events = new ArrayList<Event>();

		Integer tableform = null;
		try {
			tableform = Integer.parseInt(request.getParameter("tableform"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		try {
			if (tableform == 2) {
				events = eventDAO.findNotOwnEventByUser(user.getId());
			    System.out.println("Finding not own events");}
			if (tableform == 3) {
				events = eventDAO.findBookedEventByUser(user.getId());
			    System.out.println("Finding booked events");}
			if (tableform == 1) {
				events = eventDAO.findOwnEventByUser(user.getId());
			    System.out.println("Finding own events");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover missions");
			return;
		}


		
		// Redirect to the Home page and add missions to the parameters
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(events);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
