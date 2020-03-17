package com.google.gwt.fileSharingApp.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import static com.google.gwt.query.client.GQuery.*;

import com.google.gwt.fileSharingApp.shared.Constants;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.apache.http.HttpStatus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FileSharingApp implements EntryPoint {
	private static Logger logger = Logger.getLogger(FileSharingApp.class.getName());
	private static final String homeLinkId = "homeLink";
	private static final String loginLinkId = "loginLink";
	private static final String uploadFilesLinkId = "uploadFilesLink";
	private static final String shareFilesLinkId = "shareFilesLink";
	private static final String downloadFilesLinkId = "downloadFilesLink";

	private static final String homeContainerId = "homeContainer";
	private static final String loginContainerId = "loginContainer";
	private static final String uploadFilesContainerId = "uploadFilesContainer";
	private static final String uploadFilesFormContainerId = "uploadFilesFormContainer";
	private static final String shareFilesContainerId = "shareFilesContainer";
	private static final String downloadFilesContainerId = "downloadFilesContainer";

	private String authenticatedUser = null;

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	//private final FileUploadServiceAsync fileUploadService = GWT.create(FileUploadService.class);

	/**
	 * Create a remote service proxy to talk to the server-side Login service.
	 */
	private final AuthenticationServiceAsync authenticationService = GWT.create(AuthenticationService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Set initial tab and configure click event handlers so that we can navigate to other tabs
		this.configureMenu();
	}

	private void configureMenu() {
		// Activate home page content container & menu link
		this.navigateToTab(FileSharingApp.homeLinkId, FileSharingApp.homeContainerId);

		// Attache click event handlers for the top navigation menu
		Anchor.wrap(RootPanel.get(FileSharingApp.homeLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Home page link clicked");
				navigateToTab(FileSharingApp.homeLinkId, FileSharingApp.homeContainerId);
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.loginLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Login page link clicked");
				navigateToTab(FileSharingApp.loginLinkId, FileSharingApp.loginContainerId);
				configureLoginContainerContent();
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.uploadFilesLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Upload files page link clicked");
				navigateToTab(FileSharingApp.uploadFilesLinkId, FileSharingApp.uploadFilesContainerId);
				configureFileUploadContainerContent();			
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.shareFilesLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Share files page link clicked");
				navigateToTab(FileSharingApp.shareFilesLinkId, FileSharingApp.shareFilesContainerId);
				configureShareFilesContainerContent();
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.downloadFilesLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Download files page link clicked");
				navigateToTab(FileSharingApp.downloadFilesLinkId, FileSharingApp.downloadFilesContainerId);
				configureDownloadFilesContainerContent();
			}
		});
	}

	private void navigateToTab(final String tabLink, final String tabContainer) {
		// hide all content tabs
		$(".tab-container").addClass("hidden-container");
		// deactivate activate navigation menu link
		$(".tab-link.active").removeClass("active");

		RootPanel.get(tabLink).addStyleName("active");
		RootPanel.get(tabContainer).removeStyleName("hidden-container");
	}
	
	private void configureLoginContainerContent() {
		if ( $("#loginContainer table").get().getLength() == 0 ) {
			// Create a panel to hold all of the form widgets.
			final FormPanel form = new FormPanel();
			form.setAction(getDefaultFileUploadFormAction());
			form.setEncoding(FormPanel.ENCODING_URLENCODED);
			form.setMethod(FormPanel.METHOD_POST);
			VerticalPanel panel = new VerticalPanel();
			form.setWidget(panel);
			final Label usernameLabel = new Label();
			usernameLabel.setText("Please enter your username");
			panel.add(usernameLabel);
			final TextBox usernameTextBox = new TextBox();
			usernameTextBox.setName("username");
			usernameTextBox.setFocus(true);
			usernameTextBox.setWidth("200px");
			panel.add(usernameTextBox);
			final Label passwordLabel = new Label();
			passwordLabel.setText("Please enter your password");
			panel.add(passwordLabel);
			final PasswordTextBox passwordTextBox = new PasswordTextBox();
			passwordTextBox.setName("password");
			passwordTextBox.setWidth("200px");
			panel.add(passwordTextBox);
			panel.setSpacing(3);
			panel.setTitle("User name and password table");
			Button submitBtn = new Button();
			submitBtn.setText("Submit");
			submitBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					form.submit();
				}
			});
			panel.add(submitBtn);
			panel.setCellHorizontalAlignment(submitBtn, HasHorizontalAlignment.ALIGN_CENTER);

			Label statusLabel = new Label();
			statusLabel.setText("");
			panel.add(statusLabel);
			panel.setCellHorizontalAlignment(statusLabel, HasHorizontalAlignment.ALIGN_CENTER);

			// Add an event handler to the form.
			form.addSubmitHandler(new FormPanel.SubmitHandler() {
				public void onSubmit(SubmitEvent event) {
					// This event is fired just before the form is submitted. We
					// can take
					// this opportunity to perform validation.
					form.setAction(getDefaultLoginFormAction());
					if (usernameTextBox.getText().isEmpty() || passwordTextBox.getText().isEmpty()) {
						Window.alert("Username or password missing");
						event.cancel();
					}
				}
			});
			form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
				public void onSubmitComplete(SubmitCompleteEvent event) {
					// if("login success".equalsIgnoreCase(event.getResults()))
					// {
					if (event.getResults().toLowerCase().contains("login success")) {
						updateLoginStatus(usernameTextBox.getText());
						disableLoginForm(usernameTextBox, passwordTextBox, submitBtn);
						statusLabel.setText("Login success");
					} else {
						statusLabel.setText("Login failure");
					}
					clearLoginForm(usernameTextBox, passwordTextBox);
					logger.log(Level.INFO, "Login request response received: " + event.getResults());
				}
			});

			RootPanel.get(FileSharingApp.loginContainerId).add(form);
		}
	}
	
	
	private void configureFileUploadContainerContent() {
		if (!isAuthenticated()) {
			$("#uploadFilesContainer .authenticated-user").hide();
			$("#uploadFilesContainer .anonymous-user").show();
		} else {
			$("#uploadFilesContainer .anonymous-user").hide();
			$("#uploadFilesContainer .authenticated-user").show();
			if ( $("#uploadFilesContainer table").get().getLength() == 0 ) {
				final FormPanel form = new FormPanel();
				form.setAction(getDefaultFileUploadFormAction());
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				VerticalPanel panel = new VerticalPanel();
				form.setWidget(panel);
				FileUpload upload = new FileUpload();
				upload.setName("uploadFormElement");
				panel.add(upload);
				Button submitBtn = new Button();
				submitBtn.setText("Submit");
				submitBtn.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						form.submit();        
					}
				});
				panel.add(submitBtn);	    
				panel.setCellHorizontalAlignment(submitBtn, HasHorizontalAlignment.ALIGN_CENTER);

				Label statusLabel = new Label();
				statusLabel.setText("");
				panel.add(statusLabel);
				panel.setCellHorizontalAlignment(statusLabel, HasHorizontalAlignment.ALIGN_CENTER);

				// Add an event handler to the form.
				form.addSubmitHandler(new FormPanel.SubmitHandler() {
					public void onSubmit(SubmitEvent event) {
						// This event is fired just before the form is submitted. We can take
						// this opportunity to perform validation.
						final String fileName = getFileName(upload);
						form.setAction(getFileUploadFormAction(fileName));
						if (upload.getFilename().isEmpty()) {
							Window.alert("No file selected yet");
							event.cancel();
						}
					}
				});
				form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
					public void onSubmitComplete(SubmitCompleteEvent event) {
						// When the form submission is successfully completed, this event is
						// fired. Assuming the service returned a response of type text/html,
						// we can get the result text here (see the FormPanel documentation for
						// further explanation).
						// Window.alert(event.getResults());
						//if ("file upload success".equalsIgnoreCase(event.getResults())) {
						if (event.getResults().toLowerCase().contains("file upload success")) {
							statusLabel.setText("File upload success");
						} else {
							statusLabel.setText("File upload failure");
						}
						upload.getElement().setPropertyString("value", "");
						logger.log(Level.INFO, "File upload request response received: " + event.getResults());
					}
				});
				RootPanel.get(FileSharingApp.uploadFilesFormContainerId).add(form);
			}
		}
	}
	
	private void configureShareFilesContainerContent() {
		if (!isAuthenticated()) {
			$("#shareFilesContainer .authenticated-user").hide();
			$("#shareFilesContainer .anonymous-user").show();
		} else {
			$("#shareFilesContainer .anonymous-user").hide();
			$("#shareFilesContainer .authenticated-user").show();

		}
	}

	private void configureDownloadFilesContainerContent() {
		if (!isAuthenticated()) {
			$("#downloadFilesContainer .authenticated-user").hide();
			$("#downloadFilesContainer .anonymous-user").show();
		} else {
			$("#downloadFilesContainer .anonymous-user").hide();
			$("#downloadFilesContainer .authenticated-user").show();
			// remove whatever content we may have rendered with the element with id="yourFilesToDownload"
			$("#yourFilesToDownload").children().remove();
			// remove whatever content we may have rendered with the element with id="sharedFilesToDownload"
			$("#sharedFilesToDownload").children().remove();
			// remove whatever content we may have rendered with the element with id="publicFilesToDownload"
			$("#publicFilesToDownload").children().remove();
			// render a table with link to download user's own files
			
            try {
                String requestData = null;
                String userFilesEndpoint = "/filesharingapp/filesharingapp/userFiles";

                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(userFilesEndpoint));
                builder.sendRequest(requestData, new RequestCallback() {    
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                    	logger.log(Level.INFO, "/userFiles: received response " + response.getText());
                        if (HttpStatus.SC_OK == response.getStatusCode()) {
                        	try {
                        		// Build tables with links to download individual files
                        		// links would be such as one below:
                        		// http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=notes.txt

                        		JSONValue jsonValue = JSONParser.parseStrict(response.getText());
                        		JSONObject responseAsJSON = jsonValue.isObject();
                        		
                        		JSONArray publicFiles = responseAsJSON.get("publicFiles").isArray();
                        		VerticalPanel publicFilesLinksPanel = new VerticalPanel();
                        		for (int i=0; i< publicFiles.size(); i++) {
                        			final String fileName = publicFiles.get(i).isString().toString().replace("\"", "");
                        			String fileDownloaUrl = 
                        				"http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=" + 
                        						fileName;
                        			Anchor link = new Anchor(fileName, fileDownloaUrl);
                        			link.setTarget("_blank");
                        			publicFilesLinksPanel.add(link);
                          		}
                        		RootPanel.get("publicFilesToDownload").add(publicFilesLinksPanel);
                        		
                        		JSONArray yourFiles = responseAsJSON.get("yourFiles").isArray();
                        		VerticalPanel yourFilesLinksPanel = new VerticalPanel();
                        		for (int i=0; i< yourFiles.size(); i++) {
                        			final String fileName = yourFiles.get(i).isString().toString().replace("\"", "");
                        			String fileDownloaUrl = 
                            				"http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=" + 
                            						fileName;
                            			Anchor link = new Anchor(fileName, fileDownloaUrl);
                            			link.setTarget("_blank");
                            			yourFilesLinksPanel.add(link);
                        		}                        		                        	
                        		RootPanel.get("yourFilesToDownload").add(yourFilesLinksPanel);
                        		
                        		JSONArray sharedFiles = responseAsJSON.get("sharedFiles").isArray();
                        		VerticalPanel sharedFilesLinksPanel = new VerticalPanel();
                        		for (int i=0; i< sharedFiles.size(); i++) {
                        			final String fileName = sharedFiles.get(i).isString().toString().replace("\"", "");
                        			String fileDownloaUrl = 
                            				"http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=" + 
                            						fileName;
                            			Anchor link = new Anchor(fileName, fileDownloaUrl);
                            			link.setTarget("_blank");
                            			sharedFilesLinksPanel.add(link);
                        		}   
                        		RootPanel.get("sharedFilesToDownload").add(sharedFilesLinksPanel);
                        	} catch (Exception e) {
                        		
                        	}
                        } else {
                        	
                        }                         
                    }
                     
                    @Override
                    public void onError(Request request, Throwable exception) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
			
		}
	}
	
	private void updateLoginStatus(final String username) {
		if (username == null || username.isEmpty()) {
			return;
		}
		this.authenticatedUser = username;
		final Label authenticatedUserLabel = new Label();
		authenticatedUserLabel.setText("Logged in as " + this.authenticatedUser);
		RootPanel.get("loginStatus").add(authenticatedUserLabel);
	}
	
	private String getFileUploadFormAction(String fileName) {
		final String action = "/" + Constants.APP_NAME + 
				"/" + Constants.MODULE_NAME +
				"/" + Constants.FILE_UPLOAD_SERVICE + 
				"?fileName=" + fileName;
		return action;
	}
	
	private String getDefaultFileUploadFormAction() {
		final String action = "/" + Constants.APP_NAME + 
				"/" + Constants.MODULE_NAME +
				"/" + Constants.FILE_UPLOAD_SERVICE;
		return action;
	}
	
	private String getDefaultLoginFormAction() {
		final String action = "/" + Constants.APP_NAME + 
				"/" + Constants.MODULE_NAME +
				"/" + Constants.LOGIN_SERVICE;
		return action;
	}
	
	private String getFileName(FileUpload fileUpload) {
		final String fileName = fileUpload.getFilename().substring(Constants.FILE_UPLOAD_NAME_FAKE_PATH.length());
		return fileName;
	}
	
	
	private void clearLoginForm(TextBox username, TextBox password) {
		if (username != null) {
			username.setValue("");
		}
		if (password != null) {
			password.setValue("");
		}
	}
	
	private void disableLoginForm(TextBox username, TextBox password, Button loginBtn) {
		if (username != null) {
			username.setEnabled(false);
		}
		if (password != null) {
			password.setEnabled(false);
		}
		if (loginBtn!= null) {
			loginBtn.setEnabled(false);
		}
	}
	
	private boolean isAuthenticated() {
		return !(this.authenticatedUser == null || this.authenticatedUser.isEmpty());
	}
}
