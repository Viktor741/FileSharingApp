package com.google.gwt.fileSharingApp.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONException;
import com.google.gwt.thirdparty.json.JSONObject;

public class FileStore {
	  private static final Logger logger = Logger.getLogger(FileStore.class.getSimpleName());
	  /*
	   * 		sample JSON Object describing access allowed to a file uploaded by a user, e.g. user1
	   * 		{
	   * 			"filename": "file.txt",
	   * 			"public": false,
	   * 			"sharedWith": [
	   * 				"user2",
	   * 				"user3"
	   * 			]
	   *        }
	   */
	  private static Map<String, ArrayList<UserFile>> fileStoreDB;
	  static {
		  fileStoreDB = new HashMap<String, ArrayList<UserFile>>();
	  } 
	  
	  public static Set<String> getUsersStoringFiles() {
		  return fileStoreDB.keySet();
	  }
	  
	  public static boolean isValidCredentials(final String username, final String password) {
		  return fileStoreDB.containsKey(username) && fileStoreDB.get(username).equals(password);
	  }
	  
	  public static List<String> getUserFiles(final String userName) {
		  ArrayList<String> fileNames = new ArrayList<String>();
		  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
		  if (userFiles != null) {
			  // user does have some files store/uploaded already
			  userFiles.stream().forEach( userFile -> {
				  fileNames.add(userFile.getFileName());
			  });
		  } 
		  return fileNames;
	  }
	  
	  public static List<String> getFilesSharedWithUser(final String userName) {
		  ArrayList<String> fileNames = new ArrayList<String>();
		  // Get all users that have their files uploaded in the system
		  Set<String> users = fileStoreDB.keySet();
		  if (!users.isEmpty()) {
			  users.forEach( user -> {
				  ArrayList<UserFile> userFiles = fileStoreDB.get(user);
				  if (userFiles != null) {
					  // user does have some files store/uploaded already
					  userFiles.stream().forEach( userFile -> {
						  if (userFile.isSharedWithUser(userName)) {
							  fileNames.add(userFile.getFileName());
						  }
					  });
				  } 
			  });
		  }
		  return fileNames;
	  }
	  
	  public static List<String> getPublicFiles() {
		  ArrayList<String> fileNames = new ArrayList<String>();
		  // Get all users that have their files uploaded in the system
		  Set<String> users = fileStoreDB.keySet();
		  if (!users.isEmpty()) {
			  users.forEach( userName -> {
				  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
				  if (userFiles != null) {
					  // user does have some files store/uploaded already
					  userFiles.stream().forEach( userFile -> {
						  if (userFile.isPublic()) {
							  fileNames.add(userFile.getFileName());
						  }
					  });
				  } 
			  });
			  

		  }
		  return fileNames;
	  }  
	  
	  public void addFileForUser(final String userName, final String fileName) {
		  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
		  if (userFiles != null) {
			  // user does have some files store/uploaded already
			  addNextFileForUser(userFiles, userName, fileName);
		  } else  {
			  // user does NOT have any files stored/uploaded
			  addFirstFileForUser(userName, fileName);
		  }
	  }
	  
	  private void addFirstFileForUser(final String userName, final String fileName) {
		  UserFile userFile = new UserFile();
		  userFile.setFileName(fileName);
		  userFile.setPublic(false);
		  ArrayList<UserFile> userFiles = new ArrayList<UserFile>();
		  userFiles.add(userFile);
		  fileStoreDB.put(userName, userFiles);
	  }
	  
	  private void addNextFileForUser(final ArrayList<UserFile> userFiles, 
			  final String userName, final String fileName) {
		  Optional<UserFile> userFileOfGivenName = userFiles.stream().filter(userFile ->  
		  	userFile.getFileName().equals(fileName) ).findFirst();
		  if (userFileOfGivenName.isPresent()) {
			  // user uploaded the file earlier already 
			  return;
		  } else {
			  UserFile userFile = new UserFile();
			  userFile.setFileName(fileName);
			  userFile.setPublic(false);
			  fileStoreDB.get(userName).add(userFile);
		  }
	  }
	  
	  private class UserFile extends JSONObject {
		  public UserFile() {
			  super();
			  try {
				this.put("filename", "");
				this.put("public", false);
				this.put("sharedWith", new JSONArray());
			  } catch (JSONException e) {
				// TODO
			  }
		  }
		  
		  public void setFileName(final String filename) {
			  try {
				this.put("filename", filename);
			  } catch (JSONException e) {
				// TODO
			  }
		  }
		  
		  public String getFileName() {
			  String filename = "";
			  try {
				filename = this.getString("filename");
			  } catch (JSONException e) {
				// TODO
			  }
			  return filename;
		  }
		  
		  public void setPublic(final boolean sharedWithEveryone) {
			  try {
				this.put("public", sharedWithEveryone);
			  } catch (JSONException e) {
				// TODO
			  }
		  }
		  
		  public boolean isPublic() {
			  boolean sharedWithEveryone = false;
			  try {
				  sharedWithEveryone = this.getBoolean("public");
			  } catch (JSONException e) {
				// TODO
			  }
			  return sharedWithEveryone;
		  }
		  
		  public void shareWithUser(final String username) {
			  try {
				  if (username != null && !username.isEmpty()) {
					  this.getJSONArray("sharedWith").put(username);
				  }
			  } catch (JSONException e) {
				// TODO
			  }
		  }
		  
		  public boolean isSharedWithUser(final String username) {
			  boolean isShared = false;
			  try {
				  JSONArray sharedWithUsers = this.getJSONArray("sharedWith");
				  
				  for (int i = 0; i < sharedWithUsers.length(); i++) {
					  isShared = sharedWithUsers.get(i).equals(username);
					  if (isShared) {
						  break;
					  }
				  }
			  } catch (JSONException e) {
				// TODO
			  }
			  
			  return isShared;
		  }
	  }
	  
}
