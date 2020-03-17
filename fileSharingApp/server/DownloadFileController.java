package com.google.gwt.fileSharingApp.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;
import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONObject;

@SuppressWarnings("serial")
public class DownloadFileController extends HttpServlet {
	private static final Logger logger = Logger.getLogger(DownloadFileController.class.getSimpleName());
	private static final String serviceName = "downloadFile";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Download file: Server side call invoked");
		
		if(isAuthenticated(request)) {
			logger.log(Level.INFO, "Download file: User authenticated");
			// the request processed by this servlet is like
			// http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=notes.txt
			// the line below is to get the fileName query string parameter value
			// (query string is fileName=notes.txt in this sample case)
			final String fileName = request.getParameter("fileName"); 	// e.g. obrazek.jpg
			try {
				final String filePath = Constants.FILE_UPLOAD_DIR + fileName;
				File file = new File(filePath);
				final byte[] fileContents = Files.readAllBytes(file.toPath());
				// Remember to set headers prior to
				response.setContentLength((int) file.length());
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				// writing response body !!!
				ServletOutputStream responseBody = response.getOutputStream();
				responseBody.write(fileContents);
				responseBody.flush(); 
				logger.log(Level.INFO, "Download file: Response built, server side call finalizing");
			} catch (Exception e) {
				// TODO - completed the code to report better should an exception be caught here
			}
			
			
		} else {
			logger.log(Level.INFO, "Download file: User NOT authenticated");
			// TODO - complete the code to report better when user ot authenticated
		}
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
