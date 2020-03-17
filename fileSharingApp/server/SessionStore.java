package com.google.gwt.fileSharingApp.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionStore {
	  private static Map<String, String> userSessionStore = new HashMap<String, String>();
	  public static void deleteSessionIdsForUser(final String userName) {
		  userSessionStore.remove(userName);
	  }
	  
	  public static void addUserSessionId(final String username, final String sessionId) {
		  deleteSessionIdsForUser(username);
		  userSessionStore.put(username, sessionId);
	  }
	  
	  public static String getNewSessionId() {
		  return UUID.randomUUID().toString();
	  }
	  
	  public static String getUserSessionId(final String username) {
		  return userSessionStore.get(username);
	  }
	  
	  public static boolean isValidSession(final String sessionId) {
		  return userSessionStore.containsValue(sessionId);
	  }
}
