package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTimeComparator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Event;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.BookingDAO;
import it.polimi.tiw.projects.dao.EventDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateBooking")
public class CreateBooking extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateBooking() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		// Check params are present and correct
		Integer iduser = null;
		Integer idevent = null;

		try {
			idevent = Integer.parseInt(request.getParameter("idevent"));
			iduser = Integer.parseInt(request.getParameter("iduser"));
			if (idevent == null || iduser == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing param values");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		// Execute controller logic
		User user = (User) session.getAttribute("user");
		BookingDAO bookingDAO = new BookingDAO(connection);
		EventDAO eventDao = new EventDAO(connection);
		Event event = null;

		try {
			event = eventDao.findEventById(idevent);

			if (event == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Event not found");
				return;
			}

			if (iduser != user.getId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						"User not allowed to perform booking for other users");
				return;
			}

			// Check that the booking is not already present
			if (bookingDAO.checkBooking(idevent, iduser) == true) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Event already booked");
				return;
			}

			// Check that the booking does not exceed max attendees
			if (bookingDAO.countBookingByEvent(idevent) >= event.getMaxAttendees()) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
						"Booking not allowed, max attendees reached");
				return;
			}

			// Check that the event is not before current date
			Date current = new Date();
			if ((DateTimeComparator.getDateOnlyInstance().compare(event.getDate(), current) < 0)
					|| ((DateTimeComparator.getDateOnlyInstance().compare(event.getDate(), current) == 0)
							&& (DateTimeComparator.getTimeOnlyInstance().compare(event.getTime(), current) < 0))) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Event past not bookable");
				return;
			}

			bookingDAO.addBooking(idevent, iduser);

		} catch (SQLException e1) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add booking");
			return;
		}

		// Return view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetEventDetails?idevent=" + idevent;
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}