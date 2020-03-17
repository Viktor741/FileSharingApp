package com.google.gwt.fileSharingApp.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginController extends HttpServlet {
	private static final Logger logger = Logger.getLogger(LoginController.class.getSimpleName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// final String requestBody =
		// request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		Map<String, String[]> requestBody = request.getParameterMap();
		final String username = requestBody.get("username")[0];
		final String password = requestBody.get("password")[0];

		PrintWriter responseBody = response.getWriter();
		SessionStore.deleteSessionIdsForUser(username);
		if (UserStore.isValidCredentials(username, password)) {
			// user is authenticated
			// no session expiration/timeouts considered in this prototype
			// implementation
			// store new authentication token
			final String sessionId = SessionStore.getNewSessionId();
			SessionStore.addUserSessionId(username, sessionId);
			addAuthSuccessCookie(response, sessionId);
			responseBody.print("Login success");
			LoginController.logger.log(Level.INFO, String.format("User %s authentication success", username));
		} else {
			// user authentication failed
			LoginController.logger.log(Level.INFO, String.format("User %s authentication failure", username));
			addAuthFailureCookie(response);
			responseBody.print("Login failure");
		}

		responseBody.flush();
		// response.setContentType("text/plain");
		// response.setCharacterEncoding("UTF-8");
		// response.setStatus(HttpServletResponse.SC_OK);
	}

	private void addAuthSuccessCookie(final HttpServletResponse response, final String sessionId) {
		final Cookie sessionIdCookie = new Cookie(Constants.SESSION_ID_COOKIE, sessionId);
		response.addCookie(sessionIdCookie);
	}

	private void addAuthFailureCookie(final HttpServletResponse response) {
		final Cookie sessionIdCookie = new Cookie(Constants.SESSION_ID_COOKIE, null);
		sessionIdCookie.setMaxAge(0);
		response.addCookie(sessionIdCookie);
	}

}
