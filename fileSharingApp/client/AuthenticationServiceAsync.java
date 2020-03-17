package com.google.gwt.fileSharingApp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>LoginService</code>.
 */
public interface AuthenticationServiceAsync {
  void login(String username, String password, AsyncCallback<String> callback) throws IllegalArgumentException;
}
