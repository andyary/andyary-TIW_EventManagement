package it.polimi.tiw.events.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTimeComparator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.events.beans.Event;
import it.polimi.tiw.events.beans.User;
import it.polimi.tiw.events.dao.BookingDAO;
import it.polimi.tiw.events.dao.EventDAO;
import it.polimi.tiw.events.utils.ConnectionHandler;

@WebServlet("/RemoveBooking")
@MultipartConfig

public class RemoveBooking extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public RemoveBooking() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		// Check params are present and correct
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer idevent = null;
		Integer iduser = null;
		
		try {
			idevent = Integer.parseInt(request.getParameter("idevent"));
			iduser = user.getId();
			if (idevent == null || iduser == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect param values");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}

		// Execute controller logic
		BookingDAO bookingDAO = new BookingDAO(connection);
		EventDAO eventDao = new EventDAO(connection);
		Event event = null;

		try {
			event = eventDao.findEventById(idevent);

			if (event == null) {
				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
				response.getWriter().println("Event not found");
				return;
			}

			if (iduser != user.getId()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed to remove booking for other users");
				return;
			}

			// Check that the booking is  present
			if (bookingDAO.checkBooking(idevent, iduser) == false) {
				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
				response.getWriter().println("Event not booked");
				return;
			}

			// Check that the booking does not exceed max attendees
//			if (bookingDAO.countBookingByEvent(idevent) >= event.getMaxAttendees()) {
//				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
//				response.getWriter().println("Booking not allowed, max attendees reached");
//				return;
//			}

			// Check that the event is not before current date
			Date current = new Date();
			if ((DateTimeComparator.getDateOnlyInstance().compare(event.getDate(), current) < 0)
					|| ((DateTimeComparator.getDateOnlyInstance().compare(event.getDate(), current) == 0)
							&& (DateTimeComparator.getTimeOnlyInstance().compare(event.getTime(), current) < 0))) {
				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
				response.getWriter().println("Event past not unbookable");
				return;
			}

			bookingDAO.delBooking(idevent, iduser);

		} catch (SQLException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
			response.getWriter().println("Not possible to remove booking");
			return;
		}

		// Return idevent
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(idevent);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}