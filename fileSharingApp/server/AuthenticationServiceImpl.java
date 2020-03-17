package com.google.gwt.fileSharingApp.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.fileSharingApp.client.AuthenticationService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService {
  private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class.getSimpleName());
	
  // configure dummy user store with sample user population
  private static Map<String, String> userStore;
  static {
	  AuthenticationServiceImpl.userStore = new HashMap<String, String>();
	  AuthenticationServiceImpl.userStore.put("user1", "Password01!");
	  AuthenticationServiceImpl.userStore.put("user2", "Password01!");
	  AuthenticationServiceImpl.userStore.put("user3", "Password01!");
	  AuthenticationServiceImpl.userStore.put("user4", "Password01!");
  } 
  
  private static Map<String, String> userSessionStore = new HashMap<String, String>();
  
  @Override
  public String login(String username, String password) throws IllegalArgumentException {
	// Remove user sessions if there are any
	userSessionStore.remove(username);	
	if (userStore.containsKey(username) && userStore.get(username).equals(password)) {
		// user is authenticated
		// no session expiration/timeouts considered in this prototype implementation
		// store new authentication token
		userSessionStore.put(username, UUID.randomUUID().toString());
		AuthenticationServiceImpl.logger.log(Level.INFO, 
				String.format("User %s authentication success", username));
	} else {
		// user authentication failed
		AuthenticationServiceImpl.logger.log(Level.INFO, 
				String.format("User %s authentication failure", username));		
	}
	AuthenticationServiceImpl.logger.log(Level.INFO, 
			String.format("User session store content is: %s", userSessionStore.toString()));		
	
	final String authToken = userSessionStore.get(username) != null ? userSessionStore.get(username) : "";
	
	// return user session id or empty string if user authentication failed, empty string indicating a failure
	return authToken;
  }

}
