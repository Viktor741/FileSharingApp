package com.google.gwt.fileSharingApp.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;
import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONObject;

@SuppressWarnings("serial")
public class UserFilesController extends HttpServlet {
	private static final Logger logger = Logger.getLogger(UserFilesController.class.getSimpleName());
	private static final String serviceName = "getUserFiles";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Get user files: Server side call invoked");
		PrintWriter responseBody = response.getWriter();
		if(isAuthenticated(request)) {
			logger.log(Level.INFO, "Get user files: User authenticated");

//			FileItem uploadItem = getFileItem(request);
//			if (uploadItem == null) {
//				responseBody.print("Get user files: No file data retrieved");
//			} else {
//				final byte[] fileContents = uploadItem.get();
//				final String filePath = Constants.FILE_UPLOAD_DIR + fileName;
//				// Try with resources
//				// https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
//				try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//					outputStream.write(fileContents);
//					responseBody.print("File upload success");
//				} catch (IOException e) {
//					logger.log(Level.INFO, "File upload: Failed to write file " + filePath);
//					responseBody.print("File upload failure");
//				}
//			}
			String userFiles = "";
			try {
				
				userFiles = new JSONObject()
						.put("yourFiles", new JSONArray().
								put("notes.txt").
								put("gia_document_pk_PII_v1.pkr"))
						.put("sharedFiles", new JSONArray().
								put("notes.txt").
								put("gia_document_pk_PII_v1.pkr"))
						.put("publicFiles", new JSONArray().
								put("notes.txt").
								put("gia_document_pk_PII_v1.pkr")).toString();
			} catch (Exception e) {
				
			}
			
			responseBody.print(userFiles);
		} else {
			logger.log(Level.INFO, "Get user files: User NOT authenticated");
			responseBody.print("Reading user files failed - user not authenticated");
		}
		responseBody.flush(); 
//	    response.setContentType("text/plain");
//	    response.setCharacterEncoding("UTF-8");
//		response.setStatus(HttpServletResponse.SC_OK);
	}



	public String getServiceName() {
		return serviceName;
	}
	
	private boolean isAuthenticated(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie: cookies) {
			if (Constants.SESSION_ID_COOKIE.equals(cookie.getName())) {
				final String sessionId = cookie.getValue();
				if ( SessionStore.isValidSession(sessionId)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
