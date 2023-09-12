package it.polimi.tiw.events.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.lang.String;

//import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.events.beans.Event;
import it.polimi.tiw.events.beans.User;
import it.polimi.tiw.events.dao.BookingDAO;
import it.polimi.tiw.events.dao.EventDAO;
import it.polimi.tiw.events.utils.ConnectionHandler;


@WebServlet("/GetEventDetailsData")
@MultipartConfig
public class GetEventDetailsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetEventDetailsData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get and check params
		Integer idevent = null;
		try {
			idevent = Integer.parseInt(request.getParameter("idevent"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		// If a event with that ID exists for that USER,
		// obtain the event details
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		EventDAO eventDAO = new EventDAO(connection);
		Event event = null;
		try {
			event = eventDAO.findEventById(idevent);
			if (event == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Not possible query to get event details from DB");
			return;
		}

		boolean booked = false;
		BookingDAO bookingDAO = new BookingDAO(connection);

		try {
			booked = bookingDAO.checkBooking(idevent, user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Not possible query to check if booking already exist");
			return;
		}

		int cbookings = 99999;
		boolean bookable = false;
		try {
			cbookings = bookingDAO.countBookingByEvent(idevent);
			if (cbookings >= eventDAO.findEventById(idevent).getMaxAttendees())
				bookable = false;
			else {
				bookable = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Not possible query to count existing bookings");
			return;
		}

//		// Redirect to the Home page and add missions to the parameters
//		String path = "/WEB-INF/Event.html";
//		ServletContext servletContext = getServletContext();
//		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
//		ctx.setVariable("event", event);
//		ctx.setVariable("booked", booked);
//		ctx.setVariable("bookable", bookable);
//		ctx.setVariable("cbookings", cbookings);
//		templateEngine.process(path, ctx, response.getWriter());
//	}
		// Redirect to the Home page and add missions to the parameters
		String json = new Gson().toJson(event);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
