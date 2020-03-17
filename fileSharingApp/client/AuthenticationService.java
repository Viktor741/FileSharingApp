package com.google.gwt.fileSharingApp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("login")
public interface AuthenticationService extends RemoteService {
  String login(String username, String password) throws IllegalArgumentException;
}
