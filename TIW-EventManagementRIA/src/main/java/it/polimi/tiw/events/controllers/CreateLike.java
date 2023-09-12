package it.polimi.tiw.events.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.events.beans.Event;
import it.polimi.tiw.events.beans.User;
import it.polimi.tiw.events.dao.LikeDAO;
import it.polimi.tiw.events.dao.EventDAO;
import it.polimi.tiw.events.utils.ConnectionHandler;

@WebServlet("/CreateLike")
@MultipartConfig

public class CreateLike extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateLike() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
	
	
	@Override
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
			System.out.println("CreateLike- Launching Check 1");
			System.out.println("CreateLike- idevent:" + idevent);
			System.out.println("CreateLike- iduser:" + iduser);
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
		LikeDAO likeDAO = new LikeDAO(connection);
		EventDAO eventDao = new EventDAO(connection);
		Event event = null;

		try {
			event = eventDao.findEventById(idevent);

			System.out.println("CreateLike- Launching Check 2");

			if (event == null) {
				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
				response.getWriter().println("Event not found");
				return;
			}

			// Check that the like is not already present

			System.out.println("CreateLike- Launching Check 3");

			if (likeDAO.checkLike(idevent, iduser) == true) {
				response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
				response.getWriter().println("Like already submitted");
				return;
			}

			System.out.println("CreateLike- Trying to add like");
			likeDAO.addLike(idevent, iduser);

		} catch (SQLException e2) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to add like");
			return;
		}

		// Return idevent
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(idevent);
	}

//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		doPost(request, response);
//	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}