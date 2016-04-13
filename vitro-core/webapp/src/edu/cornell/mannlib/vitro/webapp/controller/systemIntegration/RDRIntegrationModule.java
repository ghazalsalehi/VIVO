/*
Copyright (c) 2015, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.systemIntegration;

import java.io.OutputStream;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;


public class RDRIntegrationModule extends SystemIntegrationModule{
	
	private static final Log log = LogFactory.getLog(RDRIntegrationModule.class.getName());
	
	UserAccount loggedInUser;
	private String userID;
	public VitroRequest vreq;
	private String dataRepositoryAPIActionURL;
	
	private String RDR_URL;
	private String RDR_ADMIN_API_KEY;
	private String RDR_API_VERSION;

	public RDRIntegrationModule(){
		loggedInUser = null;
		userID = "";
		vreq = null;
		dataRepositoryAPIActionURL = "";
		RDR_URL = "";
		RDR_ADMIN_API_KEY = "";
		RDR_API_VERSION = "";
		
	}
	
	@Override
	void processRequest(VitroRequest vreq, HttpServletResponse response) throws Exception {
		
		String action = vreq.getParameter(Utils.SYSTEM_INTEGRATION_MODULE_ACTION);
		
		loggedInUser = LoginStatusBean.getCurrentUser(vreq);
		if (loggedInUser != null) {
        	if (loggedInUser.isRootUser()){
        		userID = loggedInUser.getFirstName();
        	}else{
        		userID = loggedInUser.getExternalAuthId();
        	}
		}
		
		this.vreq = vreq;
		
		try{
			RDR_URL = ConfigurationProperties.getBean(vreq).getProperty("QUT_SYSINT_RDR.url"); 
	    	if (RDR_URL == null) {
	    		throw new Exception(Utils.SYSINT_RDR_ERROR_FAILED_TO_RETRIVE_RDR_URL);
	    	}
	    	
			RDR_ADMIN_API_KEY = ConfigurationProperties.getBean(vreq).getProperty("QUT_SYSINT_RDR.adminApiKey"); 
	    	if (RDR_ADMIN_API_KEY == null) {
	    		throw new Exception(Utils.SYSINT_RDR_ERROR_FAILED_TO_RETRIVE_ADMIN_API_KEY);
	    	}
	    	
	    	RDR_API_VERSION = ConfigurationProperties.getBean(vreq).getProperty("QUT_SYSINT_RDR.apiVersion"); 
	    	if (RDR_API_VERSION == null) {
	    		throw new Exception(Utils.SYSINT_RDR_ERROR_FAILED_TO_RETRIVE_API_VERSION);
	    	}
	    	
	    	dataRepositoryAPIActionURL = RDR_URL + "api/" + RDR_API_VERSION + "/action/";
	    	
	    	if (action != null ){
				if (action.equals("createRDRPackage")){	
					String packageTitle = vreq.getParameter(Utils.SYSINT_MODULE_PARAM_PACKAGE_TITLE);
					String individualURL = vreq.getParameter(Utils.SYSINT_MODULE_PARAM_RDF_INDIVIDUAL_URL);
					
					if (packageTitle == null || individualURL == null){
						throw new Exception(Utils.SYSINT_RDR_ERROR_CREATE_PACKAGE_INVALID_PARAMS);
					}
						
					JSONObject json = processCreateRDRPackageRequest(packageTitle, individualURL);
					doAjaxResponse(vreq, response, json);
				}else if (action.equals("deleteRDRPackage")){	
					String packageID = vreq.getParameter(Utils.SYSINT_MODULE_PARAM_PACKAGE_ID);
					
					if (packageID == null || packageID.equals("")){
						throw new Exception(Utils.SYSINT_RDR_ERROR_PACKAGE_INVALID_PARAMS);
					}
					
					JSONObject json = processDeleteRDRPackageRequest(packageID);
					doAjaxResponse(vreq, response, json);
				}else if (action.equals("getRDRUploadNewResourceURL")){
					String packageID = vreq.getParameter(Utils.SYSINT_MODULE_PARAM_PACKAGE_ID);
					
					if (packageID == null || packageID.equals("")){
						throw new Exception(Utils.SYSINT_RDR_ERROR_PACKAGE_INVALID_PARAMS);
					}
					
					JSONObject json = new JSONObject();
					
					json.put("SYSINT_ERROR_CODE", Utils.SYSINT_SUCCESS);
					
					JSONObject jsonResponseMsg = new JSONObject();	
					String resourceUploadURL = RDR_URL + "uploadNewResource/" + packageID;
	        		jsonResponseMsg.put("RESOURCE_UPLOAD_URL", resourceUploadURL);
	        		
	        		json.put("SYSINT_ERROR_MSG", jsonResponseMsg);
	        		
					doAjaxResponse(vreq, response, json);
					
				}else if (action.equals("getPackageResourceCount")){
					String packageID = vreq.getParameter(Utils.SYSINT_MODULE_PARAM_PACKAGE_ID);
					
					if (packageID == null || packageID.equals("")){
						throw new Exception(Utils.SYSINT_RDR_ERROR_PACKAGE_INVALID_PARAMS);
					}
					
					JSONObject json = new JSONObject();
					
					json.put("SYSINT_ERROR_CODE", Utils.SYSINT_SUCCESS);
					
					int iResCount = getPackageResourceCountFromRDR(packageID);
					JSONObject jsonResponseMsg = new JSONObject();
					
					jsonResponseMsg.put("PACKAGE_RESOURCE_COUNT", iResCount);
					
					json.put("SYSINT_ERROR_MSG", jsonResponseMsg);
					
					doAjaxResponse(vreq, response, json);
				}else{
					throw new Exception(Utils.SYSINT_RDR_ERROR_INVALID_ACTION);
				}
			}else{
				throw new Exception(Utils.SYSINT_RDR_ERROR_ACTION_NOT_FOUND);
			}
		}catch(Exception e){
			notifyUserWithError(vreq, response, Utils.SYSINT_ERROR_MSG_FAILED_TO_CREATE_DATASET, e.getMessage());
		}
	}
	
	public void notifyUserWithError(VitroRequest vreq, HttpServletResponse response, String errorMsg, String systemErr){
		JSONObject json = new JSONObject();
		json.put("SYSINT_ERROR_CODE", Utils.SYSINT_FAILED);
		json.put("SYSINT_ERROR_MSG", errorMsg + ": " + systemErr);
		if (! systemErr.equals("")){
			log.error(systemErr);
		}
		doAjaxResponse(vreq, response, json);
	}
	
	private JSONObject processDeleteRDRPackageRequest(String packageID) throws Exception{
		try{
			String userApiKey = GetLoggedInUserAPIKeyFromRDR();
			if (userApiKey == null || userApiKey.equals("")){
				throw new Exception(Utils.SYSINT_RDR_ERROR_INVALID_USER_API_KEY);
			}
			
			String POST_URL = dataRepositoryAPIActionURL +  "package_delete";
			
			JSONObject POST_PARAMS = new JSONObject();
			POST_PARAMS.put("id", packageID);
			
			HttpURLConnection con = getHttpURLConnection(POST_URL, userApiKey);
				
		    // For POST only - START
		    con.setDoOutput(true);
		    OutputStream os = con.getOutputStream();
		    os.write(POST_PARAMS.toString().getBytes());
		    os.flush();
		    os.close();
		    // For POST only - END
		        
		    String response = readHttpURLConnectionResponse(con);
		    
		    JSONObject json = new JSONObject();
		    JSONObject jObject  = JSONObject.fromObject(response);
        	boolean packageDeleted = (Boolean) jObject.get("success");
        	if (packageDeleted){	
        		json.put("SYSINT_ERROR_CODE", Utils.SYSINT_SUCCESS);
        		json.put("SYSINT_ERROR_MSG", "Dataset has been deleted successfully.");
        	}else{ 
        		json.put("SYSINT_ERROR_CODE", Utils.SYSINT_FAILED);
        		JSONObject jObjectErr = (JSONObject) jObject.get("error");
    			json.put("SYSINT_ERROR_MSG", jObjectErr.get("name"));
        	}
		
			return json;
		}catch(Exception e){
			JSONObject json = new JSONObject();
			json.put("SYSINT_ERROR_CODE", Utils.SYSINT_FAILED);
			json.put("SYSINT_ERROR_MSG", e.getMessage());
			log.error(e.getMessage());
			
			return json;
		}		
	}
	
	private JSONObject processCreateRDRPackageRequest(String packageTitle, String individualURL) throws Exception{
		try{
			packageTitle = packageTitle.trim();
			
			// For the package name we take first three words of the title
			String[] parts = packageTitle.split(" ");
			String uniqueName = "";
			int i;
			for (i = 0; i < parts.length; i++){
				if (i >= 3){
					break;
				}else{
					uniqueName += parts[i] + " ";
				}
			}
			
			uniqueName = uniqueName.trim().toLowerCase().replaceAll("[^A-Za-z0-9]", "-"); // RDR package name cannot have capital letters and non alpha numeric characters.
			
			int suffix = 0;
			String uniquePackageName = uniqueName;
			while ( packageURLAlreadyInUse(uniquePackageName)){
				suffix++;
				uniquePackageName = uniqueName + suffix ;
			}
		
			String userApiKey = GetLoggedInUserAPIKeyFromRDR();
			if (userApiKey == null || userApiKey.equals("")){
    			throw new Exception(Utils.SYSINT_RDR_ERROR_INVALID_USER_API_KEY);
    		}
			
			String POST_URL = dataRepositoryAPIActionURL +  "package_create";
	
		    JSONObject POST_PARAMS = new JSONObject();
		    POST_PARAMS.put("name", uniquePackageName);
		    POST_PARAMS.put("title", packageTitle);
		    POST_PARAMS.put("rdf_metadata_url", individualURL);
		    
		    HttpURLConnection con = getHttpURLConnection(POST_URL, userApiKey);
			
	        // For POST only - START
	        con.setDoOutput(true);
	        OutputStream os = con.getOutputStream();
	        os.write(POST_PARAMS.toString().getBytes());
	        os.flush();
	        os.close();
	        // For POST only - END
	        
	        String response = readHttpURLConnectionResponse(con);
            
	        JSONObject json = new JSONObject();
	        
	        JSONObject jObject  = JSONObject.fromObject(response);
        	boolean packageCreated = (Boolean) jObject.get("success");
        	if (packageCreated){	
        		JSONObject packageInfo = (JSONObject) jObject.get("result");
        		String packageName = (String) packageInfo.get("name");
        		String packageID = (String) packageInfo.get("id");
        		
        		json.put("SYSINT_ERROR_CODE", Utils.SYSINT_SUCCESS);
        		
        		JSONObject jsonResponseMsg = new JSONObject();	        		
        		jsonResponseMsg.put("PACKAGE_NAME", packageName);
        		jsonResponseMsg.put("PACKAGE_ID", packageID);
        		String datasetURL = RDR_URL + "dataset/" + packageName;	// This is for display purpose. Users cannot edit the name through CKAN since edit/manage is hidden from CKAN front end.
        		jsonResponseMsg.put("PACKAGE_URL", datasetURL);
        		String resourceUploadURL = RDR_URL + "uploadNewResource/" + packageID;
        		jsonResponseMsg.put("RESOURCE_UPLOAD_URL", resourceUploadURL);
        		
        		json.put("SYSINT_ERROR_MSG", jsonResponseMsg);
        	}else{ 
        		// Example error message response RDR
        		// {"help": "http://ckan-dev.qut.localhost.com/api/3/action/help_show?name=package_create", "success": false, "error": {"__type": "Validation Error", "name": ["Must be purely lowercase alphanumeric (ascii) characters and these symbols: -_"]}}
        		
        		json.put("SYSINT_ERROR_CODE", Utils.SYSINT_FAILED);
        		JSONObject jObjectErr = (JSONObject) jObject.get("error");
        		
        		String errType = (String) jObjectErr.get("__type");
        		if (errType.equals("Validation Error")){
        			// Since we convert the title to lower case, modify the error message sent by RDR 
        			json.put("SYSINT_ERROR_MSG", "Title must be purely alphanumeric (ascii) characters and these symbols: -_");
        		}else{
        			json.put("SYSINT_ERROR_MSG", jObjectErr.get("name"));
        		}
        	} 
		
			return json;
		}catch(Exception e){
			JSONObject json = new JSONObject();
			json.put("SYSINT_ERROR_CODE", Utils.SYSINT_FAILED);
			json.put("SYSINT_ERROR_MSG", e.getMessage());
			log.error(e.getMessage());
			
			return json;
		}
	}
	
	private String GetLoggedInUserAPIKeyFromRDR() throws Exception{
		String apikey = "";
		
		if (loggedInUser.getExternalAuthId().equals(""))
			throw new Exception("Please use your QUT login to deposit data.");
		else{
			String fullname = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();
			String email = loggedInUser.getEmailAddress();
			
			if (email.equals("") || fullname.equals("")){
				throw new Exception("Incorrect login details.");
			}
			
			String POST_URL = dataRepositoryAPIActionURL + "user_show_or_create_by_openid";
			JSONObject POST_PARAMS = new JSONObject();
			POST_PARAMS.put("openid", userID);	
			//POST_PARAMS.put("openid", "root"); // Test in localhost
		    POST_PARAMS.put("email", email);
		    POST_PARAMS.put("fullname", fullname);
		    
			HttpURLConnection con = getHttpURLConnection(POST_URL, RDR_ADMIN_API_KEY);
			
			// For POST only - START
	        con.setDoOutput(true);
	        OutputStream os = con.getOutputStream();
	        os.write(POST_PARAMS.toString().getBytes());
	        os.flush();
	        os.close();
	        // For POST only - END
	        
	        String response = readHttpURLConnectionResponse(con);
	        
	        JSONObject jObject  = JSONObject.fromObject(response);
        	boolean userIn = (Boolean) jObject.get("success");
        	if (userIn){
        		JSONObject userInfo = (JSONObject) jObject.get("result");
        		apikey = (String) userInfo.get("apikey");
        	}else{ // User not found and cannot be created
        		JSONObject jObjectErr = (JSONObject) jObject.get("error");
        		throw new Exception((String) jObjectErr.get("name"));
        	}
		}
	        
		return apikey;
	}
	
	private boolean packageURLAlreadyInUse(String uniquePackageName) throws Exception{
		String POST_URL = dataRepositoryAPIActionURL + "package_show?id=" + uniquePackageName;
		    
		HttpURLConnection con = getHttpURLConnection(POST_URL, RDR_ADMIN_API_KEY);
		
		String response = readHttpURLConnectionResponse(con); 
	
		JSONObject jObject  = JSONObject.fromObject(response);
    	boolean packageInUse = (Boolean) jObject.get("success");
    	if (packageInUse){	
    		return true;
    	}else{ 
    		return false;
    	}
	}
	
	private int getPackageResourceCountFromRDR(String packageID) throws Exception{
		String POST_URL = dataRepositoryAPIActionURL + "package_show?id=" + packageID;
		
		HttpURLConnection con = getHttpURLConnection(POST_URL, RDR_ADMIN_API_KEY);
		
		String response = readHttpURLConnectionResponse(con); 
	
		JSONObject jObject  = JSONObject.fromObject(response);
    	boolean packageInUse = (Boolean) jObject.get("success");
    	if (packageInUse){
    		JSONObject pkgInfo = (JSONObject) jObject.get("result");
    		JSONArray resources = (JSONArray) pkgInfo.get("resources");
    		return resources.size();
    	}else{ 
    		throw new Exception("Pakcge not found.");
    	}
	}
	
	private String readHttpURLConnectionResponse(HttpURLConnection con) throws Exception{
		
		StringBuffer responseBuff = new StringBuffer();
		
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(
	                con.getInputStream()));
			
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	        	responseBuff.append(inputLine);
	        }
	        in.close();
		}catch (IOException ioe) {	// when response status is not 200
			InputStream error = con.getErrorStream();
			
        	try {
        		int data;
        		while ((data = error.read()) != -1) {
        			responseBuff.append((char)data);
     	        }
                error.close();
            } catch (Exception e) {
            	if (error != null) {
                    error.close();
                }
            	
            	throw new Exception(e.getMessage());
            }
		}
		
		if (responseBuff.equals("")){
			throw new Exception("Invalid response from the server.");
		}
	
		return responseBuff.toString();
	}
	
	private HttpURLConnection getHttpURLConnection(String POST_URL, String API_KEY) throws Exception{
		String USER_AGENT = "Mozilla/5.0";
		URL obj = new URL(POST_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Authorization", API_KEY);
        
		return con;
	}
}
