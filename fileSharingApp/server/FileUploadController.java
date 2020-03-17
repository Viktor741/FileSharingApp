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

@SuppressWarnings("serial")
public class FileUploadController extends HttpServlet {
	private static final Logger logger = Logger.getLogger(FileUploadController.class.getSimpleName());
	private static final String serviceName = "uploadFile";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "File upload: Server side call invoked");
		PrintWriter responseBody = response.getWriter();
		if(isAuthenticated(request)) {
			logger.log(Level.INFO, "File upload: User authenticated");
			final String queryString = request.getQueryString(); // e.g. fileName=obrazek.jpg
			logger.log(Level.INFO, "File upload: Request query string: " + queryString);
			final String fileName = queryString.split("=")[1]; // e.g. obrazek.jpg

			FileItem uploadItem = getFileItem(request);
			if (uploadItem == null) {
				responseBody.print("File upload: No file data retrieved");
			} else {
				final byte[] fileContents = uploadItem.get();
				final String filePath = Constants.FILE_UPLOAD_DIR + fileName;
				// Try with resources
				// https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
				try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
					outputStream.write(fileContents);
					responseBody.print("File upload success");
				} catch (IOException e) {
					logger.log(Level.INFO, "File upload: Failed to write file " + filePath);
					responseBody.print("File upload failure");
				}
			}
		} else {
			logger.log(Level.INFO, "File upload: User NOT authenticated");
			responseBody.print("File upload failure - user not authenticated");
		}
		responseBody.flush(); 
//	    response.setContentType("text/plain");
//	    response.setCharacterEncoding("UTF-8");
//		response.setStatus(HttpServletResponse.SC_OK);
	}

	private FileItem getFileItem(HttpServletRequest request) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField() && "uploadFormElement".equals(item.getFieldName())) {
					return item;
				}
			}
		} catch (FileUploadException e) {
			return null;
		}
		return null;
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
