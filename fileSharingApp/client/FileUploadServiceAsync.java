package com.google.gwt.fileSharingApp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>FileUploadService</code>.
 */
public interface FileUploadServiceAsync {
  void uploadFile(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
}
