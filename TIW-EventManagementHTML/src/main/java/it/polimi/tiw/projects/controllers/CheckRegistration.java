package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.routines.EmailValidator;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CheckRegistration")
public class CheckRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CheckRegistration() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Obtain and escape params

		String usrn = null;
		String email = null;
		String pwd1 = null;
		String pwd2 = null;

		try {
			usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			pwd1 = StringEscapeUtils.escapeJava(request.getParameter("password1"));
			pwd2 = StringEscapeUtils.escapeJava(request.getParameter("password2"));

			// Check if credentials are missing or empty
			if (usrn == null || email == null || pwd1 == null || pwd2 == null || usrn.isBlank() || email.isBlank()
					|| pwd1.isBlank() || pwd2.isBlank()) {
				throw new Exception("Missing or empty credential value");
			}
		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		// Query DB to check if username or email already exist
		UserDAO userDao = new UserDAO(connection);
		User userByUsername = null;
		User userByEmail = null;

		try {
			userByEmail = userDao.GetIdByEmail(email);
			userByUsername = userDao.GetIdByUsername(usrn);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Not possible to check existing credentials on DB");
			return;
		}

		// Check if passwords match
		if (!pwd1.equals(pwd2)) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg2", "Password and Repeate Password are different");
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}

		// Check if email is valid method 1
		else if (!email.matches("[a-z0-9._]+@[a-z0-9.]+\\.[a-z]{2,4}$")) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg2", "Email format is not valid (method 1)");
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}

		// Check if email is valid method 2
		else if (!EmailValidator.getInstance().isValid(email)) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg2", "Email format is not valid (method 2)");
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}

		// If username or email already exist, show error
		else if (userByEmail != null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg2", "email already exist");
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}

		else if (userByUsername != null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg2", "Username already exist");
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}

		else {
			// Create new user in DB
			try {
				userDao.AddNewUser(usrn, email, pwd1);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating new user");
				return;
			}

			// Return success message
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("Msg", "User registration successful");
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
