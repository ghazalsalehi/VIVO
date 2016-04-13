package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;

import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.edit.listing.OntologiesListingController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vedit.beans.Option;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListQueryResults;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.pfunction.library.concat;
import com.hp.hpl.jena.sparql.pfunction.library.str;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.NewURIMakerVitro;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.IndividualFiltering;
import edu.cornell.mannlib.vitro.webapp.dao.jena.DependentResourceDeleteJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.IndividualDaoJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexBuilder;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexingEventListener;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexingEventListener.EventTypes;
import edu.cornell.mannlib.vitro.webapp.sparql.GetObjectClasses;

public class ManageMyWorkspaceModule extends ManageRecordsModule implements IndexingEventListener {
	
	private static final Log log = LogFactory.getLog(ManageMyWorkspaceModule.class.getName());
	
	public enum RecordsState { 
		 DRAFT("Draft", "Draft records"),
         UNDER_REVIEW("UnderReview", "Under review records"),
         PUBLISHED_OPEN_ACCESS("PublishedOpenAccess", "Published - Open access records"),
		 PUBLISHED_QUT_ACCESS("PublishedQUTAccess", "Published - QUT access records"),
		 READY_TO_REVIEW("ReadyToReview", "Ready to review"),
		 BEING_REVIEWED_BY_ADMIN("BeingReviewd", "Records being reviewed"),
		 PUBLISHED_BY_ADMIN("PublishedByMe", "Published by records"),
		 ASSIGNED_BY_ADMIN("AssignedByMe", "Assigned to records");
		 
		 private final String value;
	     private final String displayName;
		 
	     RecordsState(String value, String displayName){
			 this.value = value;
			 this.displayName = displayName;
		 }
	     
	     public String getValue(){
	    	 return value;
	     }
	     
	     public String getDisplayName(){
	    	 return displayName;
	     }
	}
	
	// REMOVED(No longer using) : Approved status from the process.
	
	public enum EmailType { 
		SEND_BACK_TO_DRAFT("SendBackToDraft", Utils.MRS_EMAIL_SEND_BACK_TO_DRAFT_DISPLAY_NAME, Utils.MRS_EMAIL_SEND_BACK_TO_DRAFT_DESCRIPTION),
		SEND_FOR_REVIEW("SendForReview", Utils.MRS_EMAIL_SEND_FOR_REVIEW_DISPLAY_NAME, Utils.MRS_EMAIL_SEND_FOR_REVIEW_DESCRIPTION),
        RECORD_PUBLISH_OPEN_ACCESS("RecordPublishOpen", Utils.MRS_EMAIL_RECORD_PUBLISH_OPEN_ACCESS_DISPLAY_NAME, Utils.MRS_EMAIL_RECORD_PUBLISH_OPEN_ACCESS_DESCRIPTION),
        RECORD_PUBLISH_QUT_ACCESS("RecordPublishQUT", Utils.MRS_EMAIL_RECORD_PUBLISH_QUT_ACCESS_DISPLAY_NAME, Utils.MRS_EMAIL_RECORD_PUBLISH_QUT_ACCESS_DESCRIPTION),
        RECORD_ASSIGN_TO_REVIEW("RecordAssignToReview", Utils.MRS_EMAIL_RECORD_ASSIGN_TO_REVIEW_DISPLAY_NAME, Utils.MRS_EMAIL_RECORD_ASSIGN_TO_REVIEW_DESCRIPTION),
		RECORD_NOTIFY_USER_AND_SEND_BACK("RecordRejected", Utils.MRS_EMAIL_RECORD_NOTIFY_USER_AND_SEND_BACK_DISPLAY_NAME,  Utils.MRS_EMAIL_RECORD_NOTIFY_USER_AND_SEND_BACK_DESCRIPTION);

		private final String value;
	    private final String emailDisplayName;
	    private final String emailDescription;
		 
	    EmailType(String value, String emailDisplayName, String emailDescription){
			 this.value = value;
			 this.emailDisplayName = emailDisplayName;
			 this.emailDescription = emailDescription;
		 }
	     
	     public String getValue(){
	    	 return value;
	     }
	     
	     public String getEmailDisplayName(){
	    	 return emailDisplayName;
	     }
	     
	     public String getEmailDescription(){
	    	 return emailDescription;
	     }
	}
	
	private RecordsState recordState;
	private RecordsState nextRecordState;
	private boolean isIndexUpdated;
	private String userID;
	RoleLevel loggedInUserRole;
	UserAccount loggedInUser;
	private DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public VitroRequest vreq;
	private NewURIMakerVitro newURIMaker;
	private String emailErrorMessage;
	
	public ManageMyWorkspaceModule(){
		recordState = RecordsState.DRAFT;
		nextRecordState = RecordsState.DRAFT;
		isIndexUpdated = false;
		userID = "";
		loggedInUserRole = null;
		loggedInUser = null;
		newURIMaker = null;
		vreq = null;
		this.emailErrorMessage = null;
	}
	 
	@Override
	void processRequest(VitroRequest vreq, HttpServletResponse response) throws Exception{
		String action = vreq.getParameter(Utils.MRS_MODULE_PARAM_ACTION);
		
		loggedInUser = LoginStatusBean.getCurrentUser(vreq);
		if (loggedInUser != null) {
        	if (loggedInUser.isRootUser()){
        		userID = loggedInUser.getFirstName();
        	}else{
        		userID = loggedInUser.getExternalAuthId();
        	}
		}
		
		loggedInUserRole = RoleLevel.getRoleFromLoginStatus(vreq);
		this.vreq = vreq;
		
		if (action != null ){
			
			String page = vreq.getParameter(Utils.MRS_MODULE_PARAM_PAGE);
			int pageNo = 1;
			if (page != null){
				pageNo = Integer.parseInt(page);
			}
			
			// everyone
			if (action.equals("queryDraftRecords")){	//Draft
				List<Individual> individuals = getDraftRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
				recordState = RecordsState.DRAFT;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}else if (action.equals("queryUnderReviewRecords")){	//Under review
				List<Individual> individuals = getUnderReviewRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
				recordState = RecordsState.UNDER_REVIEW;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}else if (action.equals("queryPublishedOpenAccessRecords")){ //Published Open access
				List<Individual> individuals = getPublishedForOpenAccessRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
				recordState = RecordsState.PUBLISHED_OPEN_ACCESS;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}else if (action.equals("queryPublishedQUTAccessRecords")){ //Published QUT access
				List<Individual> individuals = getPublishedForQUTAcessRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
				recordState = RecordsState.PUBLISHED_QUT_ACCESS;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}
			
			// only for siteAdmin/root
			else if (action.equals("queryReadyToReviewRecords")){	// New
				List<Individual> individuals = getReadyToReviewRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);	//siteAdmin
				recordState = RecordsState.READY_TO_REVIEW;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}
			else if (action.equals("queryBeingReviewedByRecords")){ // Records being reviewed 
				List<Individual> individuals = getBeingReviewedByRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);	//siteAdmin/curator/root
				recordState = RecordsState.BEING_REVIEWED_BY_ADMIN;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}else if (action.equals("queryPublishedByRecords")){
				List<Individual> individuals = getPublishedByRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
				recordState = RecordsState.PUBLISHED_BY_ADMIN;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}else if (action.equals("getAdminAndCuratorList")){	//Admin = siteAdmin/root
				String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);	// ex: /display/q8
				String currentRecordsState = vreq.getParameter(Utils.MRS_MODULE_CURRENT_RECORDS_STATE);
				
				setRecordsState(currentRecordsState);	//ex: draft
				
				List<UserAccount> accounts = Utils.getRegisteredAdminAndCuratorUserAccounts(vreq);
				String assignToUserID = null;
				
				OntModel ontModel = getOntoModel(vreq);
				Property pAssignToReviewUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
                Property pAssignForReviewOrComplete = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "assignForReviewOrComplete");
                
                try{
                	ontModel.enterCriticalSection(Lock.WRITE);
    				assignToUserID = getPropertyDataValue(key, ontModel, pAssignForReviewOrComplete, pAssignToReviewUserID);
    				ontModel.leaveCriticalSection();
                }catch(Exception e){
                	ontModel.leaveCriticalSection();
                }
                
				JSONObject json = getJsonObjectForAssignToReviewAccountList(accounts, vreq, assignToUserID);
				doAjaxResponse(vreq, response, json);
			}else if (action.equals("getNotifyUserAndSendBackBody")){
				
				JSONObject json = new JSONObject();
				String html = "<div class='rdfballoon-row rdfballoon-top-row'><div class='rdfballoon-label'>Comments:</div><div class='rdfballoon-value'><textarea class='rdf-balloon-comments' name='txtNotifyUserAndSendBackComments' rows=6' cols='50'></textarea></div></div>";
				json.put("html", html);
				
				doAjaxResponse(vreq, response, json);
			}
			
			// only for curator/siteAdmin/root
			else if (action.equals("queryAssignedByAdmin")){
				List<Individual> individuals = getAssignedBySiteAdminToReviewRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
				recordState = RecordsState.ASSIGNED_BY_ADMIN;
				JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
				doAjaxResponse(vreq, response, json);
			}
			
			// everyone
			else if (action.equals("moveToNextState")){
				int responseLevel = 1; // default
				
				String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);	// ex: /display/q8
				String currentRecordsState = vreq.getParameter(Utils.MRS_MODULE_CURRENT_RECORDS_STATE);
				String nextRecordStateVal = vreq.getParameter(Utils.MRS_MODULE_NEXT_RECORD_STATE);
				String responseInfoLevel = vreq.getParameter(Utils.MRS_MODULE_RESPONSE_INFO_LEVEL); 
				if (responseInfoLevel != null){
					responseLevel = Integer.parseInt(responseInfoLevel);
				}
				
				try{
					setRecordsState(currentRecordsState);	//ex: draft
					setNextRecordsState(nextRecordStateVal); // ex: under review
					
					moveToNextState(vreq, key);
					
					if (responseLevel == Utils.MRS_MODULE_RESPONSE_INFO_LEVEL_0){ // LIBRDF-83
						JSONObject json = new JSONObject();
						json.put("MRS_ERROR_CODE", Utils.MRS_SUCESS);
						json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_SUCESS);
						
						if (emailErrorMessage != null){
							json.put("emailErrorMessage", emailErrorMessage);
						}
						doAjaxResponse(vreq, response, json);
					}else{
						List<Individual> individuals = getRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
						if (individuals != null){
							JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
							json.put("MRS_ERROR_CODE", Utils.MRS_SUCESS);
							json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_SUCESS);
							
							if (recordState == RecordsState.BEING_REVIEWED_BY_ADMIN){
								json.put("publishedByAdminRecordsCount", getPublishedByRecordsCount(vreq));
								json.put("assignedByAdminRecordsCount", getAssignedByAdminRecordsCount(vreq));
							}else{	
								json.put("nextStateRecordsCount", getNextStateRecordsCount(vreq));
							}
							
							if (emailErrorMessage != null){
								json.put("emailErrorMessage", emailErrorMessage);
							}
							doAjaxResponse(vreq, response, json);
						}else{
							throw new Exception("There are no records.");
						}
					}
				}catch(Exception e){
					JSONObject json = new JSONObject();
					json.put("MRS_ERROR_CODE", Utils.MRS_FAILED);
					json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_FAILED_MOVE_TO_NEXT_LEVEL);
					log.error(e.getMessage());
					doAjaxResponse(vreq, response, json);
				}
				
			}else if (action.equals("assignRecordToReview")){
				String assignForReviewUserID = vreq.getParameter(Utils.MRS_MODULE_PARAM_ASSIGN_FOR_REVIEW_USERID);
				String assignByUsercomments = vreq.getParameter(Utils.MRS_MODULE_PARAM_COOMMENTS);
				String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);	// ex: /display/q8
				String currentRecordsState = vreq.getParameter(Utils.MRS_MODULE_CURRENT_RECORDS_STATE);	//changed this to MRS_MODULE_CURRENT_RECORDS_STATE
				try{
					setRecordsState(currentRecordsState);
					assgnRecordToReview(vreq, assignForReviewUserID, key, assignByUsercomments);
					
					List<Individual> individuals = getRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
					if (individuals != null){
						JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
						json.put("MRS_ERROR_CODE", Utils.MRS_SUCESS);
						json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_SUCESS);

						if ((recordState == RecordsState.DRAFT) && (loggedInUserRole  != RoleLevel.EDITOR)){	//Curator, DB_ADMIN, Self
							json.put("underReviewRecordsCount", getUnderReviewRecordsCount(vreq));
						}
						
						if (recordState == RecordsState.READY_TO_REVIEW){
							json.put("readyToReviewRecordsCount", getReadyToReviewRecordsCount(vreq));
						}
						
						json.put("beingReviewedByAdminRecordsCount", getBeingReviewedByRecordsCount(vreq));
						json.put("assignedByAdminRecordsCount", getAssignedByAdminRecordsCount(vreq));
						
						if (emailErrorMessage != null){
							json.put("emailErrorMessage", emailErrorMessage);
						}
						
						doAjaxResponse(vreq, response, json);
					}else{
						throw new Exception("There are no records.");
					}
				}catch(Exception e){
					JSONObject json = new JSONObject();
					json.put("MRS_ERROR_CODE", Utils.MRS_FAILED);
					json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_FAILED_ASSIGN_RECORD_TO_REVIEW);
					log.error(e.getMessage());
					doAjaxResponse(vreq, response, json);
				}
			}else if (action.equals("notifyUserAndSendRecordBack")){
				String comments = vreq.getParameter(Utils.MRS_MODULE_PARAM_COOMMENTS);
				String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);	// ex: /display/q8
				String recordsState = vreq.getParameter(Utils.MRS_MODULE_CURRENT_RECORDS_STATE);
				
				try{
					setRecordsState(recordsState);
					notifyUserAndSendRecordBack(vreq, key, comments);
					
					List<Individual> individuals = getRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
					if (individuals != null){
						JSONObject json = getJsonObjectForIndividualList(individuals, vreq);
						json.put("MRS_ERROR_CODE", Utils.MRS_SUCESS);
						json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_SUCESS);
						
						if ((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF)){
							json.put("readyToReviewRecordsCount", getReadyToReviewRecordsCount(vreq));
						}
						
						json.put("beingReviewedByAdminRecordsCount", getBeingReviewedByRecordsCount(vreq));
						json.put("assignedByAdminRecordsCount", getAssignedByAdminRecordsCount(vreq));
						json.put("draftRecordsCount", getDraftRecordsCount(vreq));
						json.put("underReviewRecordsCount", getUnderReviewRecordsCount(vreq));
						
						if (emailErrorMessage != null){
							json.put("emailErrorMessage", emailErrorMessage);
						}
						
						doAjaxResponse(vreq, response, json);
					}else{
						throw new Exception("There are no records.");
					}
				}catch(Exception e){
					JSONObject json = new JSONObject();
					json.put("MRS_ERROR_CODE", Utils.MRS_FAILED);
					json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_FAILED_TO_SEND_RECORD_BACK);
					log.error(e.getMessage());
					doAjaxResponse(vreq, response, json);
				}
			}else if (action.equals("delete")){
				String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);	// ex: /display/q8
				String recordsState = vreq.getParameter(Utils.MRS_MODULE_RECORDS_STATE);	//changed this to MRS_MODULE_CURRENT_RECORDS_STATE
				
				try{
					setRecordsState(recordsState);
					deleteRecord(vreq, key);
					
					List<Individual> individuals = getRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
					if (individuals != null){
						JSONObject json = getJsonObjectForIndividualList(individuals, vreq);

						json.put("MRS_ERROR_CODE", Utils.MRS_SUCESS);
						json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_SUCESS);
						
						if (emailErrorMessage != null){
							json.put("emailErrorMessage", emailErrorMessage);
						}
						
						doAjaxResponse(vreq, response, json);
					}else{
						throw new Exception("No records found.");
					}
			
				}catch(Exception e){
					JSONObject json = new JSONObject();
					json.put("MRS_ERROR_CODE", Utils.MRS_FAILED);
					json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_FAILED_TO_DELETE_RECORD  + e.getMessage());
					log.error(e.getMessage());
					doAjaxResponse(vreq, response, json);
				}
			}
		}else{
			executeDefaultAction(vreq, response);
		}
			
		// RoleLevel role = RoleLevel.getRoleFromLoginStatus(vreq);
	}
	
	public void setRecordsState(String recordsStateValue){
		if (recordsStateValue.equals(RecordsState.DRAFT.getValue())){recordState = RecordsState.DRAFT;}
		else if (recordsStateValue.equals(RecordsState.UNDER_REVIEW.getValue())){recordState = RecordsState.UNDER_REVIEW;}
		else if (recordsStateValue.equals(RecordsState.PUBLISHED_OPEN_ACCESS.getValue())){recordState = RecordsState.PUBLISHED_OPEN_ACCESS;}
		else if (recordsStateValue.equals(RecordsState.PUBLISHED_QUT_ACCESS.getValue())){recordState = RecordsState.PUBLISHED_QUT_ACCESS;}
		else if (recordsStateValue.equals(RecordsState.READY_TO_REVIEW.getValue())){recordState = RecordsState.READY_TO_REVIEW;}
		else if (recordsStateValue.equals(RecordsState.BEING_REVIEWED_BY_ADMIN.getValue())){recordState = RecordsState.BEING_REVIEWED_BY_ADMIN;}
	}
	
	public void setNextRecordsState(String nextRecordsStateValue){
		if (nextRecordsStateValue.equals(RecordsState.DRAFT.getValue())){nextRecordState = RecordsState.DRAFT;}
		else if (nextRecordsStateValue.equals(RecordsState.UNDER_REVIEW.getValue())){
			nextRecordState = RecordsState.UNDER_REVIEW;
		}
		else if (nextRecordsStateValue.equals(RecordsState.PUBLISHED_OPEN_ACCESS.getValue())){nextRecordState = RecordsState.PUBLISHED_OPEN_ACCESS;}
		else if (nextRecordsStateValue.equals(RecordsState.PUBLISHED_QUT_ACCESS.getValue())){nextRecordState = RecordsState.PUBLISHED_QUT_ACCESS;}
	}
	
	protected void executeDefaultAction(VitroRequest vreq, HttpServletResponse response){
		vreq.setAttribute("dataInput", getDataInputData(vreq));
		vreq.setAttribute("draftRecordsCount", getDraftRecordsCount(vreq));
		vreq.setAttribute("underReviewRecordsCount", getUnderReviewRecordsCount(vreq));
		vreq.setAttribute("publishedOpenAccessRecordsCount", getPublishedForOpenAcessRecordsCount(vreq));
		vreq.setAttribute("publishedQUTAccessRecordsCount", getPublishedForQUTAcessRecordsCount(vreq));
		
		vreq.setAttribute("loggedInuserRoleLevel", loggedInUserRole.getShorthand());
		
		if (loggedInUserRole == RoleLevel.CURATOR){
			vreq.setAttribute("beingReviewedByAdminRecordsCount", getBeingReviewedByRecordsCount(vreq));
			vreq.setAttribute("publishedByAdminRecordsCount", getPublishedByRecordsCount(vreq));
			vreq.setAttribute("assignedByAdminRecordsCount", getAssignedByAdminRecordsCount(vreq));
		}else if (loggedInUserRole == RoleLevel.EDITOR){
		}else if((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF)){
			vreq.setAttribute("readyToReviewRecordsCount", getReadyToReviewRecordsCount(vreq));
			vreq.setAttribute("beingReviewedByAdminRecordsCount", getBeingReviewedByRecordsCount(vreq));
			vreq.setAttribute("publishedByAdminRecordsCount", getPublishedByRecordsCount(vreq));
			vreq.setAttribute("assignedByAdminRecordsCount", getAssignedByAdminRecordsCount(vreq));
		}
	
		vreq.setAttribute("individuals_heading", recordState.getDisplayName());
		doJSPResponse(vreq, response, Controllers.MRS_WORKSPACE_JSP);
	}
	
	private JSONObject getJsonObjectForIndividualList(List<Individual> individuals, VitroRequest vreq){
		JSONObject json = new JSONObject();
		json.put("individuals_heading", recordState.getDisplayName());
		json.put("individualCountsInPage", individuals.size());
		String html = "";
		
		if (individuals.size() == 0){
			html += "<li class=\"individual\" role=\"listitem\"><div class=\"label emptyList\">No records found.</div><li>";
			json.put("html", html);
			json.put("recordsCount", getRecordsCount(recordState, vreq));
			return json;
		}
		
		for (int i = 0; i < individuals.size(); i++) {
			Individual ind = individuals.get(i);
			
			html += "<li class=\"individual\" role=\"listitem\"><div class=\"label\">" + ind.getRdfsLabel() + "</div>" +
					"<div class=\"individualLocalName\">" + ind.getLocalName() + "</div>" +
					"<div class=\"toolbar\">";
			
			if (ind instanceof IndividualFiltering) {
	    		((IndividualFiltering)ind).setIsFillteredHiddenField(true);
	    	}
			
			String createdByName = "";
			if ((createdByName = getRecordObjectPropertyValue(ind, Utils.PROPERTY_RECORD_OWNERSHIP_INFO, Utils.PROPERTY_FREE_TEXT_VALUE_1)) != null){
				html += "<div class=\"record-created-by\"><b>Created by</b>: " + createdByName + "</div>" ;
			}
			
			if ((recordState == RecordsState.UNDER_REVIEW) || (recordState == RecordsState.ASSIGNED_BY_ADMIN)){
				String assgnToUserID  = getRecordObjectPropertyValue(ind, Utils.PROPERTY_RECORD_ASSIGN_TO_REVIEW, Utils.PROPERTY_INFO_TYPE_1);
				String assignToName = getUserName(assgnToUserID);
				if (assignToName != null){
					html += "<div class=\"record-assign-to\"><b>Assign to</b>: " + assignToName + "</div>";
				}

			}
			
			if ((recordState == RecordsState.PUBLISHED_OPEN_ACCESS) || (recordState == RecordsState.PUBLISHED_QUT_ACCESS)) {
				String publishedByUserID = getPublishedByUserID(ind);
				String publishedByName = getUserName(publishedByUserID);
				if (publishedByName != null){
					html += "<div class=\"record-approved-by\"><b>Published by</b>: " + publishedByName + "</div>";
				}
			}
		
			html += "<div class=\"manage-records-btn-group\">" + getButtonGroupHTML(vreq, ind) + "</div></div></li>";
		}
		
		json.put("html", html);
		json.put("recordsCount", getRecordsCount(recordState, vreq));	// cannot user individuals.size() since it is for particular page
		
		return json;
	}

	private String getUserName(String accountUserID){
		if (accountUserID == null){
			return null;
		}
		
		String assignToName = null;
		if (accountUserID.equals("admin")){assignToName = "Administrator";}
		if (accountUserID.equals("root")){assignToName = "Root user";}
		else{
			UserAccount userAccount = Authenticator.getInstance(vreq).getAccountForExternalAuth(accountUserID);
			if (userAccount != null){
				assignToName = userAccount.getFirstName() + " " + userAccount.getLastName();
			}
		}
		
		return assignToName;
	}
	
	private String getUserEmailAddress(String accountUserID){
		if (accountUserID == null){
			return null;
		}
		
		String userAccountEmail = null;
		
		UserAccount userAccount = Authenticator.getInstance(vreq).getAccountForExternalAuth(accountUserID);
		if (userAccount != null){
			userAccountEmail = userAccount.getEmailAddress();
		}

		return userAccountEmail;
	}
	
	private String getPublishedByUserID(Individual ind){
		
		List<ObjectPropertyStatement> listStmnts = ind.getObjectPropertyStatements(Utils.PROPERTY_RECORD_OTHER_INFO);
		for (int j =0; j < listStmnts.size(); j++){ // there can be one entry.
			ObjectPropertyStatement stmt = listStmnts.get(j);
			if (stmt != null){
				Individual indRecordOtherInfo = stmt.getObject();
				if (indRecordOtherInfo != null){
					return getRecordObjectPropertyValue(indRecordOtherInfo, Utils.PROPERTY_RECORD_PUBLISHED_BY_INFO, Utils.PROPERTY_FREE_TEXT_VALUE_1);
				}
			}
		}
		
		return null;
	}
	
	// This function is added to Utils.java file as static function. TODO: remove this from here.
	private String getRecordObjectPropertyValue(Individual ind, String propertyName, String fieldName){
		List<ObjectPropertyStatement> listStmnts = ind.getObjectPropertyStatements(propertyName);
		for (int j =0; j < listStmnts.size(); j++){ // there can be one entry
			ObjectPropertyStatement stmt = listStmnts.get(j);
			if (stmt != null){
				Individual indRecord = stmt.getObject();
				return indRecord.getDataValue(fieldName);
			}
		}
		
		return null;
	}
	
	private JSONObject getJsonObjectForAssignToReviewAccountList(List<UserAccount> userAccounts, VitroRequest vreq, String selectedUserID){
		
		JSONObject json = new JSONObject();
		String html = "<div class='rdfballoon-row rdfballoon-top-row'><div class='rdfballoon-label'>Name:</div><div class='rdfballoon-value'><select name='txtAssignToReview' class='assignToReview'><option value=''>Select Type</option>";
		
		if (userAccounts != null){
			for (int i = 0; i < userAccounts.size(); i++){
				
				
				UserAccount account  = userAccounts.get(i);
				String indUserID = "";
				if (account.isExternalAuthOnly()){
					indUserID = account.getExternalAuthId(); // Curators are authenticated by QUT single sign on.
				}else{	//root user or manually created accounts.
					indUserID = account.getFirstName();	// will be root for root user
				}
				
				if ((indUserID.equals(userID)) && (recordState == RecordsState.DRAFT)){	// For draft records, Cannot assign to themselves.
					continue;
				}
				
				String fullName = account.getFirstName() + " " + account.getLastName();
				if ((selectedUserID != null) && (selectedUserID.equals(indUserID))){
					html += "<option value='" + indUserID + "' selected='selected'>" + fullName + "</option>";
				}else{
					html += "<option value='" + indUserID + "'>" + fullName + "</option>";
				}
			}
		}
		
		// TEST CODE : should remove this lines
		//--------------------------------------------------------------------
		/*if ((selectedUserID != null) && (selectedUserID.equals("root"))){
			html += "<option value='root' selected='selected'>ROOT</option>";	
		}else{
			html += "<option value='root'>ROOT</option>";
		}*/
		//--------------------------------------------------------------------
		
		/*if ((selectedUserID != null) && (selectedUserID.equals("admin"))){	// no need to show the administrator
			html += "<option value='admin' selected='selected'>Administrator</option>";	
		}else{
			html += "<option value='admin'>Administrator</option>";
		}*/
		
		html += "</select></div></div><div class='rdfballoon-row'><div class='rdfballoon-label'>Comments:</div><div class='rdfballoon-value'><textarea class='rdf-balloon-comments' name='txtAssignByUsercomments' rows=6' cols='50'></textarea></div></div>";

		
		json.put("html", html);
		
		return json;
		
	}
	
	private String getButtonGroupHTMLForAction(VitroRequest vreq, Individual ind){
		return "";
	}
	private String getButtonGroupHTML(VitroRequest vreq, Individual ind){
		RoleLevel role = RoleLevel.getRoleFromLoginStatus(vreq);
		
		if (role == RoleLevel.DB_ADMIN){
			return getHtmlForAdmin(ind);
		}else if (role == RoleLevel.CURATOR){
			return getHtmlForCurator(ind);
		}else if (role == RoleLevel.EDITOR){
			return getHtmlForEditor(ind);
		}else{
			return getHtmlForAdmin(ind); // root and self
		}
	}
	
	
	
	List<Individual> getRecords(VitroRequest vreq, int pageNo, int pageSize){
		List<Individual> individuals = null;
		if (recordState.equals(RecordsState.DRAFT)){
			individuals = getDraftRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
		}else if (recordState.equals(RecordsState.UNDER_REVIEW)){
			individuals = getUnderReviewRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
		}else if (recordState.equals(RecordsState.PUBLISHED_OPEN_ACCESS)){
			individuals = getPublishedForOpenAccessRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
			recordState = RecordsState.PUBLISHED_OPEN_ACCESS;
		}else if (recordState.equals(RecordsState.PUBLISHED_QUT_ACCESS)){
			individuals = getPublishedForQUTAcessRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
		}else if (recordState.equals(RecordsState.READY_TO_REVIEW)){
			individuals = getReadyToReviewRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
		}else if (recordState.equals(RecordsState.BEING_REVIEWED_BY_ADMIN)){
			individuals = getBeingReviewedByRecords(vreq, pageNo, Utils.MRS_MODULE_RECORDS_PER_PAGE);
		}
		
		return individuals;
	}
	
	List<Individual> getDraftRecords(VitroRequest vreq, int page, int pageSize){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_DRAFT_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getDraftRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getUnderReviewRecords(VitroRequest vreq, int page, int pageSize){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			//query.setFilterQueries(VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getPublishedForOpenAccessRecords(VitroRequest vreq, int page, int pageSize){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			//query.setFilterQueries(VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);

			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getPublishedForQUTAcessRecords(VitroRequest vreq, int page, int pageSize){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			//query.setFilterQueries(VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	/*List<Individual> getBeingReviewedByRecords(VitroRequest vreq, int page, int pageSize){ // only for siteAdmin/root/curator
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			if (loggedInUserRole == null){throw new Exception("Logged in user is null.");}
			
			String filterQueryStr = "";
			if ((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF)){ // Query all the records that are assigned to siteAdministrators and root.
				filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND (";
				
				HashMap<String, String> adminList = Utils.getRegisteredAdminList(vreq);
				Iterator iter = adminList.keySet().iterator();
				while(iter.hasNext()) {
				    String key = (String)iter.next();
				    filterQueryStr += VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":"  + key;
				    if (iter.hasNext()){
				    	filterQueryStr += " OR ";
				    }
				}
			
				filterQueryStr += ")";
			}else if (loggedInUserRole == RoleLevel.CURATOR){	// Query only the records that are assigned to curators by a siteAdmin
				filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND " + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":" + userID ;
			}else{
				throw new Exception("Other users do not have admin rights.");
			}

			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}*/
	
	List<Individual> getReadyToReviewRecords(VitroRequest vreq, int page, int pageSize){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			if (loggedInUserRole == null){throw new Exception("Logged in user is null.");}
			
			String filterQueryStr = "";
			if ((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF)){ // Query all the records that are assigned to "admin".
				filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND (" + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":admin)";		
			}else{
				throw new Exception("Other users do not have admin rights.");
			}

			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getBeingReviewedByRecords(VitroRequest vreq, int page, int pageSize){ // only for siteAdmin/root/curator
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			if (loggedInUserRole == null){throw new Exception("Logged in user is null.");}
			
			String filterQueryStr = "";
			if ((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF) || (loggedInUserRole == RoleLevel.CURATOR)){	// Query only the records that are assigned to curators by a siteAdmin
				filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND " + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":" + userID ;
			}else{
				throw new Exception("Other users do not have admin rights.");
			}

			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getPublishedByRecords(VitroRequest vreq, int page, int pageSize){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			
			// "Assign to review" is the person who published the record. but better to get that info from otherRecordInfo.
			String filterQueryStr = "(" + VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE  + " OR " + 
					VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE + ") AND " +  VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":" + userID ;
			
			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getAssignedBySiteAdminToReviewRecords(VitroRequest vreq, int page, int pageSize){	//records assigned to curators by siteAdmin and root
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			//String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND " + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_BY_USERID + ":" + userID ;
			String filterQueryStr = "(" + VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE +  ") AND " + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	protected int getRecordsCount(RecordsState rs, VitroRequest vreq) {
		if (rs == RecordsState.DRAFT){
			return getDraftRecordsCount(vreq);
		}else if (rs == RecordsState.UNDER_REVIEW){
			return getUnderReviewRecordsCount(vreq);
		}else if (rs == RecordsState.PUBLISHED_OPEN_ACCESS){
			return getPublishedForOpenAcessRecordsCount(vreq);
		}else if (rs == RecordsState.PUBLISHED_QUT_ACCESS){
			return getPublishedForQUTAcessRecordsCount(vreq);
		}else if (rs == RecordsState.READY_TO_REVIEW){
			return getReadyToReviewRecordsCount(vreq);
		}else if (rs == RecordsState.BEING_REVIEWED_BY_ADMIN){
				return getBeingReviewedByRecordsCount(vreq);
		}else if (rs == RecordsState.PUBLISHED_BY_ADMIN){
			return getPublishedByRecordsCount(vreq);
		}else if (rs == RecordsState.ASSIGNED_BY_ADMIN){
			return getAssignedByAdminRecordsCount(vreq);
		}else{
			return 0;
		}
	}
	
	protected int getNextStateRecordsCount(VitroRequest vreq){
		if (nextRecordState == RecordsState.DRAFT){
			return getDraftRecordsCount(vreq);
		}else if (nextRecordState == RecordsState.UNDER_REVIEW){
			return getUnderReviewRecordsCount(vreq);
		}/*else if ((nextRecordState == RecordsState.APPROVED) && (recordState == RecordsState.BEING_REVIEWED_BY_ADMIN)){
			return getApprovedByRecordsCount(vreq);
		}else if (nextRecordState == RecordsState.APPROVED){
			return getApprovedRecordsCount(vreq);
		}*/else if (nextRecordState == RecordsState.PUBLISHED_OPEN_ACCESS){
			return getPublishedForOpenAcessRecordsCount(vreq);
		}else if (nextRecordState == RecordsState.PUBLISHED_QUT_ACCESS){
			return getPublishedForQUTAcessRecordsCount(vreq);
		}else{
			return 0;
		}
	}

	protected int getDraftRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			//query.setFilterQueries(VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_DRAFT_VALUE);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_DRAFT_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected int getUnderReviewRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected int getPublishedForOpenAcessRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected int getPublishedForQUTAcessRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE + " AND " + VitroSearchTermNames.RECORD_CREATED_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	
	protected int getReadyToReviewRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			
			if (loggedInUserRole == null){throw new Exception("Logged in user is null.");}
			
			String filterQueryStr = "";
			if ((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF)){ // Query all the records that are assigned to "admin".
				filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND (" + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":admin)";		
			}else{
				throw new Exception("Other users do not have admin rights.");
			}
			
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected int getBeingReviewedByRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			
			if (loggedInUserRole == null){throw new Exception("Logged in user is null.");}
			
			String filterQueryStr = "";
			if ((loggedInUserRole == RoleLevel.DB_ADMIN) || (loggedInUserRole == RoleLevel.SELF) || (loggedInUserRole == RoleLevel.CURATOR)){	// Query only the records that are assigned to themself.
				filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE + " AND " + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":" + userID ;
			}else{
				throw new Exception("Other users do not have admin rights.");
			}

			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected int getPublishedByRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);

			//String filterQueryStr = "(" + VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_APPROVED_VALUE + " OR " + VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE + 
				//	" OR " +  VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE + ") AND " +  VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":" + userID ;
			
			String filterQueryStr = "(" + VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE  + " OR " + 
					VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE + ") AND " +  VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_USERID + ":" + userID ;
			
			
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getDraftRecordsCount: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected int getAssignedByAdminRecordsCount(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			String filterQueryStr = "(" + VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_UNDER_REVIEW_VALUE +  ") AND " + VitroSearchTermNames.RECORD_ASSIGN_TO_REVIEW_BY_USERID + ":" + userID ;
			query.setFilterQueries(filterQueryStr);
			
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return 0;
	}
	
	protected Map<String, Object> getDataInputData(VitroRequest vreq) {
	    
        Map<String, Object> map = new HashMap<String, Object>();
        
		if (PolicyHelper.isAuthorizedForActions(vreq,
				SimplePermission.DO_BACK_END_EDITING.ACTIONS)) {

            map.put("formAction", UrlBuilder.getUrl("/editRequestDispatch"));
            
            WebappDaoFactory wadf = vreq.getFullWebappDaoFactory();
            
            // Create map for data input entry form options list
            List<VClassGroup> classGroups = wadf.getVClassGroupDao().getPublicGroupsWithVClasses(true,true,false); // order by displayRank, include uninstantiated classes, don't get the counts of individuals
    
            Set<String> seenGroupNames = new HashSet<String>();
            
            Iterator<VClassGroup> classGroupIt = classGroups.iterator();
            LinkedHashMap<String, List<Option>> orderedClassGroups = new LinkedHashMap<String, List<Option>>(classGroups.size());
            while (classGroupIt.hasNext()) {
                VClassGroup group = classGroupIt.next();            
                List<Option> opts = FormUtils.makeOptionListFromBeans(group.getVitroClassList(),"URI","PickListName",null,null,false);
                
                /*if ((loggedInUserRole == RoleLevel.EDITOR)){
                	
                	if (group.getPublicName().equals(Utils.VCLASSGROUP_PEOPLE_NAME)){
                		
                	}
                	
                	List<Option> optsToDelete = new ArrayList<Option>();
                	for (Option option : opts) {
                		if ((option.getValue().equals(Utils.VCLASS_RESEARCHER)) || (option.getValue().equals(Utils.VCLASS_ADMINISTRATIVE_POSITION))){
               		 		optsToDelete.add(option);
               		 	}
               		 	
                		if ((option.getValue().equals(Utils.VCLASS_CATALOGUE_OR_INDEX)) || (option.getValue().equals(Utils.VCLASS_REPOSITORY)) ||
               				(option.getValue().equals(Utils.VCLASS_REGISTRY)) || (option.getValue().equals(Utils.VCLASS_COLLECTION))){
                			optsToDelete.add(option);
            		 	}
       	 			}
                	
                	for (Option option1 : optsToDelete) {
                		opts.remove(option1);
                	}
                }*/
                
                // LIBRDF-48 Keep only create and assemble
                List<Option> optsToDelete = new ArrayList<Option>();
            	for (Option option : opts) {
            		if ((option.getValue().equals(Utils.VCLASS_SERVICE_annotate)) || (option.getValue().equals(Utils.VCLASS_SERVICE_report)) || (option.getValue().equals(Utils.VCLASS_SERVICE_syndicateRSS)) || (option.getValue().equals(Utils.VCLASS_SERVICE_syndicateATOM)) ||
            			(option.getValue().equals(Utils.VCLASS_SERVICE_harvestOAIPMH)) || (option.getValue().equals(Utils.VCLASS_SERVICE_SearchZ3950)) || (option.getValue().equals(Utils.VCLASS_SERVICE_searchSRU)) || (option.getValue().equals(Utils.VCLASS_SERVICE_searchSRW)) || 
            			(option.getValue().equals(Utils.VCLASS_SERVICE_transform)) || (option.getValue().equals(Utils.VCLASS_SERVICE_searchOpenSearch)) || (option.getValue().equals(Utils.VCLASS_SERVICE_searchHTTP)) || (option.getValue().equals(Utils.VCLASS_SERVICE_generate))){
            			
            			optsToDelete.add(option);
        		 	}
   	 			}
            	
            	for (Option option1 : optsToDelete) {
            		opts.remove(option1);
            	}
            	
                
                if (group.getPublicName().equals(Utils.VCLASSGROUP_SPATIALDATA_DISPLAY_NAME)){
                	 opts = filterSpatialDataOptionList(opts);
            	}

                if( seenGroupNames.contains(group.getPublicName() )){
                    //have a duplicate classgroup name, stick in the URI
                    orderedClassGroups.put(group.getPublicName() + " ("+group.getURI()+")", opts);
                }else if( group.getPublicName() == null ){
                    //have an unlabeled group, stick in the URI
                    orderedClassGroups.put("unnamed group ("+group.getURI()+")", opts);
                }else{
                    orderedClassGroups.put(group.getPublicName(),opts);
                    seenGroupNames.add(group.getPublicName());
                }             
            }
            
            map.put("groupedClassOptions", orderedClassGroups);
        }
        return map;
    }
	
	private List<Option> filterSpatialDataOptionList(List<Option> opts){
		List<Option> optsToDelete = new ArrayList<Option>();
    	for (Option option : opts) {
    		if ((option.getValue().equals(Utils.VCLASS_SPATIAL_BUILD_ENVIRONMENT)) || (option.getValue().equals(Utils.VCLASS_SPATIAL_ECONOMY_AND_HEALTH)) ||
   				(option.getValue().equals(Utils.VCLASS_SPATIAL_ELEVATION_AND_BASEMAPS)) || (option.getValue().equals(Utils.VCLASS_SPATIAL_NATURAL_ENVIRONMENT)) || (option.getValue().equals(Utils.VCLASS_SPATIAL_POSITION_AND_BOUNDARIES)) ||
   				(option.getValue().equals(Utils.VCLASS_SPATIAL_WATER_AND_COASTAL))){
    			optsToDelete.add(option);
		 	}
		}
    	
    	for (Option option1 : optsToDelete) {
    		opts.remove(option1);
    	}
    	
    	return opts;
	}
	
	private void deleteRecord(VitroRequest vreq, String key) throws Exception{
		
		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		String URL = defaultNamespace + key; 
		
		OntModel ontModel = getOntoModel(vreq);
		Resource res = ontModel.getResource(URL);
    	if (res == null){
    		throw new Exception("Resource " + URL + " is null");
    	}
		
		if (recordState == RecordsState.DRAFT){	
			// Don't delete if the record is published previously. This happens when user move record to Draft from Published and then delete it.
			// TODO: Think about how to delete the record from RDA.
			Property pRecordOtherInfo = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordOtherInfo");
			Property pRecordPublishedByInfo = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordPublishedByInfo");
			Property pFreeTextValue3 = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue3");
			try{
			        Statement stmntRecordOtherInfo = res.getProperty(pRecordOtherInfo);
			        Resource resobjRecordOtherInfo = null;
			     	if (stmntRecordOtherInfo != null){
			     		resobjRecordOtherInfo = (stmntRecordOtherInfo.getObject()).asResource();
			     		if (resobjRecordOtherInfo != null) {	//Resource
			     			String isRecordPublishedBefore = getPropertyFeild(resobjRecordOtherInfo, pRecordPublishedByInfo, pFreeTextValue3); //PublishedOpenAccess/PublishedQUTAccess
			     			if ((isRecordPublishedBefore != null) && (isRecordPublishedBefore.equals(Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE))){
			     				throw new Exception("This record is already a published record.");
			     			}
			     			
			     			if ((isRecordPublishedBefore != null) && (isRecordPublishedBefore.equals(Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE))){
			     				// Can delete. 
			     				// TODO: But think about it.
			     				throw new Exception("This record is already a published record.");
			     			}
			     		}
			     	}
			}catch(Exception e){
		       	throw new Exception(e.getMessage());
			}			
		}
		
		WebappDaoFactoryJena wdf = new WebappDaoFactoryJena(vreq.getOntModelSelector());
		if (wdf == null){throw new Exception("WebappDaoFactoryJena is null");}
		
		IndividualDaoJena ida = (IndividualDaoJena)wdf.getIndividualDao();
		if (ida == null){throw new Exception("IndividualDaoJena is null");}
		
		DeleteDependantResource(URL, ontModel, ida);
		
		if (ida.deleteIndividual(URL) != 0){throw new Exception(Utils.MRS_ERROR_MSG_FAILED_TO_DELETE_RECORD);}
		
		// TODO: CHECK whether we need to update solr index.
	}

	
/* res.listProperties(); ex res : http://www.localhost.com/individual/n4800
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#dateRecordModified, http://www.localhost.com/individual/n1578]
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#dateRecordCreated, http://www.localhost.com/individual/n820]
[http://www.localhost.com/individual/n4800, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.qut.edu.au/ontologies/vivoqut#administrativePosition]
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#localKey, http://www.localhost.com/individual/n5360]
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#recordInitiallyCreatedBy, http://www.localhost.com/individual/n150]
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#publishRecord, http://www.localhost.com/individual/n269]
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#assignForReviewOrComplete, http://www.localhost.com/individual/n10169]
[http://www.localhost.com/individual/n4800, http://www.w3.org/2000/01/rdf-schema#label, "Test admin position - gawri"^^http://www.w3.org/2001/XMLSchema#string]
[http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#hasAssociationWith, http://www.localhost.com/individual/q20]
*/
	private void DeleteDependantResource(String URL, OntModel ontModel, IndividualDaoJena ida) throws Exception{
		Resource res = ontModel.getResource(URL);
    	if (res == null){
    		throw new Exception("Resource " + URL + " is null");
    	}
    	
    	//EX parent res : http://www.localhost.com/individual/n4800
    	
		StmtIterator iter = res.listProperties();
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
	    	
		    String strPred = predicate.toString();
		    
		    if (strPred.equals(Utils.PROPERTY_RECORD_OTHER_INFO)){
		    	Resource childRes = object.asResource();
		    	DeleteDependantResource(childRes.toString(), ontModel, ida);
		    	if (ida.deleteIndividual(childRes.toString()) != 0){throw new Exception(Utils.MRS_ERROR_MSG_FAILED_TO_DELETE_RECORD );}
		    	continue;
		    }
		    
		    /* 
		     * [http://www.localhost.com/individual/n4800, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.qut.edu.au/ontologies/vivoqut#administrativePosition]
		     * [http://www.localhost.com/individual/n4800, http://www.w3.org/2000/01/rdf-schema#label, "This s Gawri Edussuriya"^^http://www.w3.org/2001/XMLSchema#string]
		     * Will be deleted when deleting the parent. 
		     * 
		     * */
		    if (strPred.equals(Utils.PROPERTY_RDF_TYPE) || strPred.equals(Utils.PROPERTY_RDF_LABEL)){
		    	continue;
		    }
		    
	    	if (object instanceof Resource) {
		    	Resource childRes = object.asResource();
		    	
		    	Property pLocalKey = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "localKey");
		    	Statement stmntLocalKey = childRes.getProperty(pLocalKey);
		    	// Ex: [http://www.localhost.com/individual/q20, http://www.qut.edu.au/ontologies/vivoqut#localKey, http://www.localhost.com/individual/b8771aaed6329c761c65c654]
		     	if (stmntLocalKey != null){	//If individual has a local key, then DO NOT delete.
		     		continue;
		     	}
		    	
		     	// Won't come here since we check the localkey before this steps. But in case..
		    	/* childRes : http://www.localhost.com/individual/q20
		    	 * 
		    	 * SELECTION FOR INVERSE RELATIONSHIP : http://www.localhost.com/individual/q20 null http://www.localhost.com/individual/n4800
		    	 * [http://www.localhost.com/individual/q20, http://www.qut.edu.au/ontologies/vivoqut#hasAssociationWith, http://www.localhost.com/individual/n4800]
		    	 * 
		    	 * Do not delete if the child has an inverse relationship
		    	 * */
		    	StmtIterator iterInverseRelationShip = ontModel.listStatements(childRes, null, res);
			    if ( iterInverseRelationShip.hasNext()){
			    	continue;
			    }
			     
			    /*
			     * SELECTION FOR OTHER RELATIONSHIP TO: null null http://www.localhost.com/individual/q20
			     * [http://www.localhost.com/individual/n4182, http://www.qut.edu.au/ontologies/vivoqut#hasAssociationWith, http://www.localhost.com/individual/q20]
			     * [http://www.localhost.com/individual/q4, http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOwnedBy, http://www.localhost.com/individual/q20]
			     * [http://www.localhost.com/individual/n3292, http://www.qut.edu.au/ontologies/vivoqut#hasAssociationWith, http://www.localhost.com/individual/q20]
			     * [http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#hasAssociationWith, http://www.localhost.com/individual/q20]
			     * 
			     * null null http://www.localhost.com/individual/n1578
			     * [http://www.localhost.com/individual/n4800, http://www.qut.edu.au/ontologies/vivoqut#dateRecordModified, http://www.localhost.com/individual/n1578]
			     * 
			     */	
			    boolean bIsLinkedToOtherIndividuals = false;
		    	StmtIterator iterIsLinkedToOthers = ontModel.listStatements(null, null, childRes);
				while( iterIsLinkedToOthers.hasNext()){
				   	Statement stmt2 = iterIsLinkedToOthers.nextStatement();
				   	Resource  subjLinkedTo   = stmt2.getSubject();
				   	String subjLinkedToURL = subjLinkedTo.toString();
				   	
				   	if (! subjLinkedToURL.equals(res.toString())){
				   		bIsLinkedToOtherIndividuals = true;
				   		break;
				   	}
				}
				
				if (bIsLinkedToOtherIndividuals == false){
					if (ida.deleteIndividual(childRes.toString()) != 0){throw new Exception(Utils.MRS_ERROR_MSG_FAILED_TO_DELETE_RECORD);}
				}
		    } else {
		        // object is a literal. Do nothing. Will be deleted when deleting the parent. 
		    }
		}
	}
	
	private String getPropertyFeild(Resource res, Property pParent, Property pField) throws Exception{
		// pParent = http://www.qut.edu.au/ontologies/vivoqut#recordPublishedByInfo
		// pField = http://www.qut.edu.au/ontologies/vivoqut#freeTextValue3
		
		String value = null;
		Statement stmnt1 = res.getProperty(pParent);
        Resource robj = null;
     	if (stmnt1 != null){
     		robj = (stmnt1.getObject()).asResource();
     		if (robj != null) {
     			Statement stmntVal = robj.getProperty(pField);
     			if (stmntVal != null){
          			value = stmntVal.getString();
          		}
     		}
     	}
     	
     	return value;
	}
	
	private void moveToNextState(VitroRequest vreq, String key) throws Exception{
		
		//Thread.sleep(4000);
		WebappDaoFactoryJena wdf = new WebappDaoFactoryJena(vreq.getOntModelSelector());
		if (wdf == null){throw new Exception("WebappDaoFactoryJena is null");}
		
		IndividualDaoJena ida = (IndividualDaoJena)wdf.getIndividualDao();
		if (ida == null){throw new Exception("IndividualDaoJena is null");}
		
		//String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		//String URL = defaultNamespace + key; 
		
		OntModelSelector ontModelSelector = wdf.getOntModelSelector();
		OntModel ontModel = ontModelSelector.getFullModel();
		
		IndexBuilder indexBuilder = IndexBuilder.getBuilder(vreq.getSession().getServletContext());
		changeRecordStatusTo( key, ontModel, ida, indexBuilder);
		
		synchronized(this){	// Here it is not necessary to use synchronized since this section is not used by multiple threads.  
            while(!isIndexUpdated){
				try {
					wait();
				} catch (Exception e) {
					throw new Exception("there is problem when updateing the index. please refresh." + e.getMessage());
				}
            }
        }
		
		isIndexUpdated = false;
	}
	
	private void deleteRecordOtherInfo(Resource res,  OntModel ontModel){
		Property pFreeTextValue1 = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
    	Property pFreeTextValue2 = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue2");
    	// Don't remove freeTextValue3 since it has the "RecordPublisedFor" information. When user going to delete a already published record then that record should be removed 
    	//from RDA as well. So that is why we keep "freetextValue3" info.
        Property pRecordOtherInfo = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordOtherInfo");
        Property pRecordPublishedByInfo = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordPublishedByInfo");
        
        try{
	        Statement stmntRecordOtherInfo = res.getProperty(pRecordOtherInfo);
	        Resource robjRecordOtherInfo = null;
	     	if (stmntRecordOtherInfo != null){
	     		robjRecordOtherInfo = (stmntRecordOtherInfo.getObject()).asResource();
	     		if (robjRecordOtherInfo != null) {
                	deletePropertyFeild(robjRecordOtherInfo, pRecordPublishedByInfo, pFreeTextValue1, ontModel); 
                	deletePropertyFeild(robjRecordOtherInfo, pRecordPublishedByInfo, pFreeTextValue2, ontModel);
	     		}
	     	}
        }catch(Exception e){
        	log.error("deleteRecordOtherInfo:error occured." + e.getMessage());
	    }
	}

	public void changeRecordStatusTo(String key, OntModel ontModel, IndividualDaoJena ida, IndexBuilder indexBuilder) throws Exception{
    	
		ManageRecordsEmailMessage emailMessgae = null;
    	ontModel.enterCriticalSection(Lock.WRITE);
    	
    	try {
	    		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
	    		String URL = defaultNamespace + key; 
	    		
            	Resource res = ontModel.getResource(URL);
            	if (res == null){
            		ontModel.leaveCriticalSection();
            		throw new Exception("Resource " + URL + " is null");
            	}
            	
            	Property pValue = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
                Property precordStatus = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "publishRecord");
                
                Literal litrl = null;
                if (nextRecordState == RecordsState.DRAFT){
                	litrl = ontModel.createTypedLiteral(new String(Utils.RECORD_STATUS_DRAFT_VALUE));
                	
                	Property pAssignToReviewUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
                	Property pAssignByUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType2");
                    Property pAssignForReviewOrComplete = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "assignForReviewOrComplete");
                	
                    if (res != null){
                    	deletePropertyFeild(res, pAssignForReviewOrComplete, pAssignToReviewUserID, ontModel); 
                    	deletePropertyFeild(res, pAssignForReviewOrComplete, pAssignByUserID, ontModel);
                    	
                    	deleteRecordOtherInfo(res, ontModel);
                    }
                    
                }else if (nextRecordState == RecordsState.UNDER_REVIEW){
                	litrl = ontModel.createTypedLiteral(new String(Utils.RECORD_STATUS_UNDER_REVIEW_VALUE));
                	
                	// IMPORTANT : assign record to admin if the user is editor otherwise assign themself and then they will be given permision to assign others to review their records..
                	Property pAssignToReviewUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
                    Property pAssignForReviewOrComplete = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "assignForReviewOrComplete");
                    
                    Literal litrlAssignTo = null;
                	if (loggedInUserRole == RoleLevel.EDITOR){
                		litrlAssignTo = ontModel.createTypedLiteral(new String(Utils.MRS_MODULE_ADMIN_COMMON_USERID));
                	}else{
                		litrlAssignTo = ontModel.createTypedLiteral(new String(userID));
                	}
                    
                    try{
        				updatePropertyFeild(res, litrlAssignTo, pAssignForReviewOrComplete, pAssignToReviewUserID, ontModel);	// assign to userID
        			}catch (Exception e){
        				ontModel.leaveCriticalSection();
        				throw e;
        			}
                    
                    if (loggedInUserRole == RoleLevel.EDITOR){	//create an email message.
                    	emailMessgae = ComposeEmailMessage(res, key, ontModel, EmailType.SEND_FOR_REVIEW, "", null);
                    }
                
        		}else if (nextRecordState == RecordsState.PUBLISHED_OPEN_ACCESS){
        			litrl = ontModel.createTypedLiteral(new String(Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE));
        		}else if (nextRecordState == RecordsState.PUBLISHED_QUT_ACCESS){
        			litrl = ontModel.createTypedLiteral(new String(Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE));
        		}
                
                
                // Store recordPublishedByInfo in "Record other info"(recordOtherInfo) field
                // NOTE : Assume that curator will publish the related objects.
                if ((nextRecordState == RecordsState.PUBLISHED_OPEN_ACCESS) || (nextRecordState == RecordsState.PUBLISHED_QUT_ACCESS)){
                	Property pRecordPublishedInfo = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordPublishedByInfo");
       			 	try{
       			 		updatePublishedByPropertyFeild(res, ontModel, pRecordPublishedInfo);
	        		}catch (Exception e){
	    				ontModel.leaveCriticalSection();
	    				throw e;
	    			}
       			 	
       			 	if (nextRecordState == RecordsState.PUBLISHED_OPEN_ACCESS){emailMessgae = ComposeEmailMessage(res, key, ontModel, EmailType.RECORD_PUBLISH_OPEN_ACCESS, "", null);}
       			 	else if (nextRecordState == RecordsState.PUBLISHED_QUT_ACCESS){emailMessgae = ComposeEmailMessage(res, key, ontModel, EmailType.RECORD_PUBLISH_QUT_ACCESS, "", null);}
                }
                
                if (litrl == null){
                	log.error("changeRecordStatusTo: next record status cannot be null.");
         			ontModel.leaveCriticalSection();
         			throw new Exception("next record status is null.");
                }
               
                Statement stmnt1 = res.getProperty(precordStatus);
                Resource robj = null;
             	if (stmnt1 != null){
             		robj = (stmnt1.getObject()).asResource();
             		if (robj != null) {	// edit existing recordStatus entry.
             			robj.removeAll(pValue);
             			robj.addProperty(pValue, litrl);
             			ida.updateDateRecordModifiedTime(res, ontModel);
             			updateSolrIndex(stmnt1, indexBuilder);
             		}else{
             			log.error("changeRecordStatusTo: record status object cannot be null.");
             			ontModel.leaveCriticalSection();
             			throw new Exception("Object for " + URL + " is null");
             		}
             	}else{
             		log.error("changeRecordStatusTo: record status stmt cannot be null.");
             		ontModel.leaveCriticalSection();
             		throw new Exception("Statement for " + URL + " is null");
             	}
             	
             	if (emailMessgae != null){
             		try{
             			emailMessgae.sendEmail(vreq);
             		}catch(Exception e){
             			emailErrorMessage = "Failed to send the email." + e.getMessage();
             		}
             	}
				
        } finally {
        	ontModel.leaveCriticalSection();
        }
    }
	
	public void changeRecordStatus(String key, OntModel ontModel, IndividualDaoJena ida, String recordStatus) throws Exception{
		
		ontModel.enterCriticalSection(Lock.WRITE);
    	
    	try {
	    		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
	    		String URL = defaultNamespace + key; 
	    		
	    		Literal litrl = ontModel.createTypedLiteral(new String(recordStatus));
          		if (litrl == null){
         			throw new Exception("Record status is null.");
                }
	    		
            	Resource res = ontModel.getResource(URL);
            	if (res == null){
            		throw new Exception("Resource " + URL + " is null");
            	}
            	
            	Property pValue = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
                Property precordStatus = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "publishRecord");
            	
            	Statement stmnt1 = res.getProperty(precordStatus);
                Resource robj = null;
              	if (stmnt1 != null){
              		robj = (stmnt1.getObject()).asResource();
             		if (robj != null) {	// edit existing recordStatus entry.
             			robj.removeAll(pValue);
             			robj.addProperty(pValue, litrl);
             			ida.updateDateRecordModifiedTime(res, ontModel);
             		}else{
             			throw new Exception("Object for " + URL + " is null");
             		}
              		
              	}else{
             		throw new Exception("Statement for " + URL + " is null");
             	}
                
    	}catch(Exception e){
    		ontModel.leaveCriticalSection();
    		throw new Exception(e.getMessage());
    	}finally {
        	ontModel.leaveCriticalSection();
        }
	}
	
	private String getRecordCreatedByUserID(String key, OntModel ontModel) throws Exception{
		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		String URL = defaultNamespace + key; 
		
		Property pFreeTextValue2 = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue2");
	    Property pRecordCreatedBy = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordInitiallyCreatedBy");
	    Property pRecordCreatedDate = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "dateRecordCreated");
	       
	    return getPropertyDataValue(key, ontModel, pRecordCreatedBy, pFreeTextValue2);
	}
	
	private Map<String, Object> getEmailMessageCommonValues(String key, OntModel ontModel, String subject, String recipientName, String paragraph_1, UserAccount recCreatedByUserAccount) throws Exception{
		
		if (recCreatedByUserAccount == null){
			throw new Exception("Cannot find the owner. User is not registerd.!");
			
		}
		Map<String, Object> emailContent = new HashMap<String, Object>();
	   	emailContent.put("subject", subject);
	   	emailContent.put("recipientName", recipientName);
	   	emailContent.put("paragraph_1", paragraph_1);
	   	 
	   	String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
	   	//webSiteUrl = webSiteUrl.replace("individual", "");	//http://researchdatafinder.qut.edu.au/
	   	//String URL = webSiteUrl + "manageRecords?module=workspace&action=goto&key=" + key;
	   	String URL = defaultNamespace + key;
	   	emailContent.put("recordURL", URL);

        Property pFreeTextValue1 = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
        Property pRecordCreatedDate = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "dateRecordCreated");
       
        String recordCreatedByName = recCreatedByUserAccount.getFirstName() + " " + recCreatedByUserAccount.getLastName();
        emailContent.put("recordCreatedByName", recordCreatedByName);
        emailContent.put("recordCreatedByEmail", recCreatedByUserAccount.getEmailAddress());
        
        String recordCreatedDate = getPropertyDataValue(key, ontModel, pRecordCreatedDate, pFreeTextValue1);
        if (recordCreatedDate != null){ 
        	if (recordCreatedDate.contains("T")){
        		String[] parts = recordCreatedDate.split("T");
        		emailContent.put("recordCreatedDate", parts[0] + " " + parts[1]);
        	}else{
        		emailContent.put("recordCreatedDate", recordCreatedDate);
        	}
        }

        return emailContent;
	}
	
	private ManageRecordsEmailMessage ComposeEmailMessage(Resource res, String key, OntModel ontModel, EmailType emailType, String comments, UserAccount recordAssignToUserAccount){
		 ManageRecordsEmailMessage emailMessgae = null;
	
		 try{
			 String recordCreatedByUserID = getRecordCreatedByUserID(key, ontModel);
		   	 if (recordCreatedByUserID == null){
		   		 throw new Exception("Failed to send the email. Failed to find the owner of the record.");
		   	 }else{
		   		UserAccount recordCreatedByUserAccount = null;
		   		if (recordCreatedByUserID.equals("root")){
		   			recordCreatedByUserAccount = Authenticator.getInstance(vreq).getAccountForInternalAuth("researchdatafinder@qut.edu.au");
		   		}else{
		   			recordCreatedByUserAccount = Authenticator.getInstance(vreq).getAccountForExternalAuth(recordCreatedByUserID);
		   		}
		   		
		   		String para1 = "";
		   		String recipientName = "";
		   		String errorMsg = "";
		   		List<String> recipentList = new ArrayList<String>();
		   		String rejectedByEmail = "";
		   		String rejectedByName = "";
		   		String publishedByName = "";
		   		String assignedByName = "";
		   		
		   		if (emailType == EmailType.SEND_FOR_REVIEW){
		   			para1 = emailType.getEmailDescription();
		   			recipientName = "Administrator";
		   			
		   			List<UserAccount> userAccountList = Utils.getRegisteredAdminUserAccountList(vreq);
		   			if ((userAccountList != null) && (userAccountList.size() > 0)){
		   				for (UserAccount account : userAccountList) {
	               		 	recipentList.add(account.getEmailAddress());
	       	 			}
		   			}else{
		   				errorMsg = "Failed to send the email. Cannot find registerd administraotrs in the system.";
				        recipentList.add("researchdatafinder@qut.edu.au");
		   			}

		   		}else if ((emailType == EmailType.RECORD_PUBLISH_OPEN_ACCESS) || (emailType == EmailType.RECORD_PUBLISH_QUT_ACCESS) ){ // records can be published by 
		   			para1 = emailType.getEmailDescription();
		   			
		   			List<UserAccount> userAccountList = Utils.getRegisteredAdminUserAccountList(vreq);
		   			if ((userAccountList != null) && (userAccountList.size() > 0)){
		   				for (UserAccount account : userAccountList) {
		   					recipentList.add(account.getEmailAddress());
	       	 			}	
		   			}
		   			
		   			if (! recipentList.contains(recordCreatedByUserAccount.getEmailAddress())){
		   				recipentList.add(recordCreatedByUserAccount.getEmailAddress());
	   				}
		   			
		   			recipientName = recordCreatedByUserAccount.getFirstName();
		   			publishedByName =  loggedInUser.getFirstName() + " " + loggedInUser.getLastName();
		   			
		   		}else if (emailType == EmailType.RECORD_ASSIGN_TO_REVIEW){
		   			para1 = emailType.getEmailDescription();
		   			recipientName = recordCreatedByUserAccount.getFirstName();
		   			
		   			if (recordAssignToUserAccount != null){
		   				recipientName = recordAssignToUserAccount.getFirstName();
		   				recipentList.add(recordAssignToUserAccount.getEmailAddress());
		   			}
		   			
		   			assignedByName = loggedInUser.getFirstName() + " " + loggedInUser.getLastName(); 
		   		}else if (emailType == EmailType.RECORD_NOTIFY_USER_AND_SEND_BACK){
		   			para1 = emailType.getEmailDescription();
		   			recipientName = recordCreatedByUserAccount.getFirstName();
		   			recipentList.add(recordCreatedByUserAccount.getEmailAddress());
		   			rejectedByEmail = loggedInUser.getEmailAddress();
			   		rejectedByName = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();

		   		}
		   	 
		   		Map<String, Object> emailContent = getEmailMessageCommonValues(key, ontModel, emailType.getEmailDisplayName(), recipientName, para1, recordCreatedByUserAccount);
		   		if ((emailContent == null) || emailContent.isEmpty()){
		   		 	throw new Exception("Failed to build an email message.");
		        }
		   		
		   		if (! errorMsg.equals("")){
		   			emailContent.put("errorMsg", errorMsg);
		   		}
		   		
		   		
		   		if (! assignedByName.equals("")){
		   			emailContent.put("assignedByName", assignedByName);
		   		}
		   		
		   		if (! publishedByName.equals("")){
		   			emailContent.put("publishedByName", publishedByName);
		   		}
		   		
		   		if (! comments.equals("")){
		   			emailContent.put("comments", comments);
		   		}
		   		
		   		if (! rejectedByName.equals("")){
		   			emailContent.put("rejectedByName", rejectedByName);
		   			if (! rejectedByEmail.equals("")){
		   				emailContent.put("rejectedByEmail", rejectedByEmail);
		   			}
		   		}
		   		
		   		emailMessgae = new ManageRecordsEmailMessage(emailContent);
		   		for (String recipentEmail : recipentList) {	
		   			emailMessgae.addRecipients(recipentEmail);
		   		}
		   		//emailMessgae.addRecipients("gawri.edussuriya@qut.edu.au");
		   	 }
		 }catch(Exception e){
   			 emailErrorMessage = "Failed to send the email." + e.getMessage();
   		 }
		 
		 return emailMessgae;
	}
	private void assgnRecordToReview(VitroRequest vreq, String assignForReviewUserID, String key, String assignByUsercomments) throws Exception{
		WebappDaoFactoryJena wdf = new WebappDaoFactoryJena(vreq.getOntModelSelector());
		if (wdf == null){throw new Exception("WebappDaoFactoryJena is null");}
		
		IndividualDaoJena ida = (IndividualDaoJena)wdf.getIndividualDao();
		if (ida == null){throw new Exception("IndividualDaoJena is null");}
		
		OntModelSelector ontModelSelector = wdf.getOntModelSelector();
		OntModel ontModel = ontModelSelector.getFullModel();
		
		if ((recordState == RecordsState.DRAFT) && (loggedInUserRole  != RoleLevel.EDITOR)){	//Curator, DB_ADMIN, Self
			 // First change the record status to UnderReview
			 String recordCreatedByUserID = getRecordCreatedByUserID(key, ontModel);
		   	 if (recordCreatedByUserID == null){
		   		 throw new Exception("Failed to send the email. Failed to find the owner of the record.");
		   	 }
		   	 
		   	 if (recordCreatedByUserID.equals(userID)){	// if the record owner assign someone to review their record. (combine UnderReview and AssignToReview in one step.)
		   		changeRecordStatus(key, ontModel, ida, Utils.RECORD_STATUS_UNDER_REVIEW_VALUE);
		   	 }
		}
		
		IndexBuilder indexBuilder = IndexBuilder.getBuilder(vreq.getSession().getServletContext());
		updateAssignForReviewOrCompleteFields( key, assignForReviewUserID, assignByUsercomments, ontModel, ida, indexBuilder);
		
		synchronized(this){	// Here it is not necessary to use synchronized since this section is not used by multiple threads.  
            while(!isIndexUpdated){
				try {
					wait();
				} catch (Exception e) {
					throw new Exception("there is problem when updateing the index. please refresh." + e.getMessage());
				}
            }
        }
		
		isIndexUpdated = false;
	}
	
	private void notifyUserAndSendRecordBack(VitroRequest vreq, String key, String comments) throws Exception{
		WebappDaoFactoryJena wdf = new WebappDaoFactoryJena(vreq.getOntModelSelector());
		if (wdf == null){throw new Exception("WebappDaoFactoryJena is null");}
		
		IndividualDaoJena ida = (IndividualDaoJena)wdf.getIndividualDao();
		if (ida == null){throw new Exception("IndividualDaoJena is null");}
		
		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		String URL = defaultNamespace + key; 
		
		OntModelSelector ontModelSelector = wdf.getOntModelSelector();
		OntModel ontModel = ontModelSelector.getFullModel();
		
		IndexBuilder indexBuilder = IndexBuilder.getBuilder(vreq.getSession().getServletContext());
		
		ontModel.enterCriticalSection(Lock.WRITE);
		try {
			Resource res = ontModel.getResource(URL);
            if (res == null){
            	ontModel.leaveCriticalSection();
            	throw new Exception("Resource " + URL + " is null");
            }
            
            Property pRecordStatusValue = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
            Property pRecordStatus  = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "publishRecord");
            
            Literal litrlRecordStatus = ontModel.createTypedLiteral(new String(Utils.RECORD_STATUS_DRAFT_VALUE));
            
            if (litrlRecordStatus == null){
               	log.error("notifyUserAndSendRecordBack: AssignForReviewUserID or pAssignByUserID cannot be null.");
         		ontModel.leaveCriticalSection();
         		throw new Exception("Record status is null.");
             }
            
            try{
            	
            	Property pAssignToReviewUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
            	Property pAssignByUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType2");
                Property pAssignForReviewOrComplete = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "assignForReviewOrComplete");
            	
                deletePropertyFeild(res, pAssignForReviewOrComplete, pAssignToReviewUserID, ontModel); 
            	deletePropertyFeild(res, pAssignForReviewOrComplete, pAssignByUserID, ontModel);
            	
				updatePropertyFeild(res, litrlRecordStatus, pRecordStatus, pRecordStatusValue, ontModel);
				Statement stmnt1 = res.getProperty(pRecordStatus);
				updateSolrIndex(stmnt1, indexBuilder);
				
				try{
					ManageRecordsEmailMessage emailMessgae = ComposeEmailMessage(res, key, ontModel, EmailType.RECORD_NOTIFY_USER_AND_SEND_BACK, comments, null);
					if (emailMessgae == null){
						throw new Exception("Failed to compse the email message.");
					}
					emailMessgae.sendEmail(vreq);
				}catch (Exception e){
					 emailErrorMessage = "Failed to send the email." + e.getMessage();
				}
            }catch (Exception e){
     			ontModel.leaveCriticalSection();
     			throw e;
     		}
            
		 }finally {
	        	ontModel.leaveCriticalSection();
	     }
		
		
		synchronized(this){	// Here it is not necessary to use synchronized since this section is not used by multiple threads.  
            while(!isIndexUpdated){
				try {
					wait();
				} catch (Exception e) {
					throw new Exception("there is problem when updateing the index. please refresh." + e.getMessage());
				}
            }
        }
		
		isIndexUpdated = false;
	}
	
	
	public void updateAssignForReviewOrCompleteFields(String key, String assignForReviewUserID, String assignByUsercomments, OntModel ontModel, IndividualDaoJena ida, IndexBuilder indexBuilder) throws Exception{
		
		ontModel.enterCriticalSection(Lock.WRITE);
    	try {
           
    		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
    		String URL = defaultNamespace + key; 
    		
            Resource res = ontModel.getResource(URL);
            if (res == null){
            	ontModel.leaveCriticalSection();
            	throw new Exception("Resource " + URL + " is null");
            }
            
            Property pAssignForReviewUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
            Property pAssignByUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType2");
            Property pAssignByUsercomments = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
            Property pAssignToReviewOrComplete = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "assignForReviewOrComplete");
               
            Literal litrlAssignForReviewUserID = ontModel.createTypedLiteral(new String(assignForReviewUserID));
            Literal litrlAssignByUserID = ontModel.createTypedLiteral(new String(userID));
            Literal litrlAssignByUsercomments = ontModel.createTypedLiteral(new String(assignByUsercomments));
                
                
             if (litrlAssignForReviewUserID == null || (litrlAssignByUserID == null)){
               	log.error("changeAssignForReviewOrCompleteField: AssignForReviewUserID or pAssignByUserID cannot be null.");
         		ontModel.leaveCriticalSection();
         		throw new Exception("user ID is null.");
             }
            	
			 try{
				updatePropertyFeild(res, litrlAssignForReviewUserID, pAssignToReviewOrComplete, pAssignForReviewUserID, ontModel); // assignForReviewUserID
				updatePropertyFeild(res, litrlAssignByUserID, pAssignToReviewOrComplete, pAssignByUserID, ontModel); // assignedByUserID
				updatePropertyFeild(res, litrlAssignByUsercomments, pAssignToReviewOrComplete, pAssignByUsercomments, ontModel); // assignByUsercomments
					
				Statement stmnt1 = res.getProperty(pAssignToReviewOrComplete);
				if (stmnt1 != null){
					updateSolrIndex(stmnt1, indexBuilder);	
				}else{
					log.error("updateAssignForReviewOrCompleteFields: record status stmt cannot be null.");
		         	ontModel.leaveCriticalSection();
		         	throw new Exception("Statement for " + URL + " is null");
				}
				
				try{
					
					UserAccount recordAssignToUserAccount = null;
			   		if (assignForReviewUserID.equals("root")){
			   			recordAssignToUserAccount = Authenticator.getInstance(vreq).getAccountForInternalAuth("researchdatafinder@qut.edu.au");
			   		}else{
			   			recordAssignToUserAccount = Authenticator.getInstance(vreq).getAccountForExternalAuth(assignForReviewUserID);
			   		}
			   		
					ManageRecordsEmailMessage emailMessgae = ComposeEmailMessage(res, key, ontModel, EmailType.RECORD_ASSIGN_TO_REVIEW, assignByUsercomments, recordAssignToUserAccount);
					if (emailMessgae == null){
						throw new Exception("Failed to compse the email message.");
					}
					emailMessgae.sendEmail(vreq);
				}catch (Exception e){
					 emailErrorMessage = "Failed to send the email." + e.getMessage();
				}
     		}catch (Exception e){
     			ontModel.leaveCriticalSection();
     			throw e;
     		}
        } finally {
        	ontModel.leaveCriticalSection();
        }
	}
	
	private void updatePublishedByPropertyFeild(Resource res, OntModel ontModel, Property pRecordPublishedByInfo) throws Exception{
		Property pRecordOtherInfo = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordOtherInfo");
		
		// For now recordOtherInfo property store recordPublishedByInfo.  
		Property pUserID = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");	// published by user ID
		Property pDate = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue2");
		Property pPublishedFor = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue3");	 //PublishedOpenAccess, PublishedQUTAccess
		
		Literal litrlUserID = ontModel.createTypedLiteral(new String(userID));
		
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
	    Literal litrlDate = ontModel.createTypedLiteral(new String(formattedDateStr));
	   
	    String publishedFor = nextRecordState.getValue();
	    Literal litrlPublishedFor = ontModel.createTypedLiteral(new String(publishedFor));
	    
        Resource customProperty = ontModel.getResource("http://www.qut.edu.au/ontologies/vivoqut#customProperty");
        Property pType = ontModel.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
        
 		//stmnt1 = [http://www.localhost.com/individual/q0, http://www.qut.edu.au/ontologies/vivoqut#recordOtherInfo, http://www.localhost.com/individual/n4206]
	    Statement stmnt1 = res.getProperty(pRecordOtherInfo);
	    Resource robj = null;
     	if (stmnt1 != null){
     		// Second time
     		robj = (stmnt1.getObject()).asResource(); 
     		if (robj != null) {	// edit existing pRecordOtherInfo entry.	// robj = http://www.localhost.com/individual/n4206
     			
     			// stmnt2 = [http://www.localhost.com/individual/n4206, http://www.qut.edu.au/ontologies/vivoqut#recordPublishedByInfo, http://www.localhost.com/individual/n6075]
     			Statement stmnt2 = robj.getProperty(pRecordPublishedByInfo);	
     			if (stmnt2 != null){	//edit existing statement
     				Resource robj1 = (stmnt2.getObject()).asResource();
     				robj1.removeAll(pUserID);
     				robj1.removeAll(pDate);
     				robj1.removeAll(pPublishedFor);
     				
     				robj1.addProperty(pUserID, litrlUserID);
     				robj1.addProperty(pDate, litrlDate);
     				robj1.addProperty(pPublishedFor, litrlPublishedFor);
     			}else{	// new
     				String newURI = getUnUsedURI();	
     		 		Resource newResource = ontModel.createResource(newURI);
     		 		newResource.addProperty(pType, customProperty);
     		 		newResource.addProperty(pUserID, litrlUserID);
     		 		newResource.addProperty(pDate, litrlDate);
     		 		newResource.addProperty(pPublishedFor, litrlPublishedFor);
     		 		
     				robj.addProperty(pRecordPublishedByInfo, newResource); 
     			}
     			// do not update solr index here since it will update later.
     		}else{
     			log.error("updatePropertyFeild: object cannot be null.");
     			throw new Exception("AssignRecordToADMIN:Object is null");
     		}
     	}else{
     		// First time
     		String newURI = getUnUsedURI();		// ex: http://www.localhost.com/individual/n6033
     		Resource newResource = ontModel.createResource(newURI);
     		newResource.addProperty(pType, customProperty);
     		newResource.addProperty(pUserID, litrlUserID);
     		newResource.addProperty(pDate, litrlDate);
     		newResource.addProperty(pPublishedFor, litrlPublishedFor);
     		
     		String newURI1 = getUnUsedURI();	
     		Resource newResource1 = ontModel.createResource(newURI1); // ex: http://www.localhost.com/individual/n4206
     		newResource1.addProperty(pType, customProperty);
     		// [http://www.localhost.com/individual/n4206, http://www.qut.edu.au/ontologies/vivoqut#recordPublishedByInfo, http://www.localhost.com/individual/n6075]
     		newResource1.addProperty(pRecordPublishedByInfo, newResource);
            res.addProperty(pRecordOtherInfo, newResource1); //  [http://www.localhost.com/individual/q0, http://www.qut.edu.au/ontologies/vivoqut#recordOtherInfo, http://www.localhost.com/individual/n4206]
     	}
	}
	
	private void deletePropertyFeild(Resource res, Property pParent, Property pField, OntModel ontModel) throws Exception{
		// pParent = http://www.qut.edu.au/ontologies/vivoqut#assignForReviewOrComplete
		// pField = http://www.qut.edu.au/ontologies/vivoqut#infoType1
		
		Statement stmnt1 = res.getProperty(pParent);
        Resource robj = null;
     	if (stmnt1 != null){
     		robj = (stmnt1.getObject()).asResource();
     		if (robj != null) {
     			robj.removeAll(pField);
     		}else{
     			log.error("updatePropertyFeild: object cannot be null.");
     			throw new Exception("AssignRecordToADMIN:Object is null");
     		}
     	}
	}
	
	private void updatePropertyFeild(Resource res, Literal litrl, Property pParent, Property pField, OntModel ontModel) throws Exception{
		// pParent = http://www.qut.edu.au/ontologies/vivoqut#assignForReviewOrComplete
		// pField = http://www.qut.edu.au/ontologies/vivoqut#infoType1
		
		Statement stmnt1 = res.getProperty(pParent);
        Resource robj = null;
     	if (stmnt1 != null){
     		robj = (stmnt1.getObject()).asResource();
     		if (robj != null) {	// edit existing recordStatus entry.
     			robj.removeAll(pField);
     			robj.addProperty(pField, litrl);
     			// do not update solr index here since it will update later.
     		}else{
     			log.error("updatePropertyFeild: object cannot be null.");
     			throw new Exception("AssignRecordToADMIN:Object is null");
     		}
     	}else{ // Create a new property. ex: assignForReviewOrComplete field can be null at the beginning.
            Resource customProperty = ontModel.getResource("http://www.qut.edu.au/ontologies/vivoqut#customProperty");
            Property pType = ontModel.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
            
     		String newURI = getUnUsedURI();
     		Resource newResource = ontModel.createResource(newURI);
     		newResource.addProperty(pType, customProperty);
            newResource.addProperty(pField, litrl);
            res.addProperty(pParent, newResource);
     	}
	}
	
	// TODO: This function was moved to Utils.java. Please remove from here. 
	private String getPropertyDataValue(String key, OntModel ontModel, Property pParent, Property pField) throws Exception{	
		// pParent = http://www.qut.edu.au/ontologies/vivoqut#assignForReviewOrComplete
		// pField = http://www.qut.edu.au/ontologies/vivoqut#infoType1
		
		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		String URL = defaultNamespace + key; 
		String value = null;
		
		try {
            Resource res = ontModel.getResource(URL);
           	if (res != null){
           	 Statement stmnt1 = res.getProperty(pParent);
             Resource robj = null;
             if (stmnt1 != null){
             	robj = (stmnt1.getObject()).asResource();
              	if (robj != null) {	// edit existing recordStatus entry.
              		Statement stmntVal = robj.getProperty(pField);
              		if (stmntVal != null){
              			value = stmntVal.getString();
              		}
              	}
              }
            }
		}catch (Exception e){
			throw new Exception(e.getMessage());
		}
		
		return value;
	}

	private String getUnUsedURI() throws Exception{
		if (newURIMaker == null){
			newURIMaker = new NewURIMakerVitro(vreq.getWebappDaoFactory());
		}
		
		String uri = newURIMaker.getUnusedNewURI(vreq.getWebappDaoFactory().getDefaultNamespace());  
		
		return uri;
	}
    public void updateSolrIndex(Statement stmt, IndexBuilder indexBuilder){
		if (stmt != null){
			if (indexBuilder != null){
				indexBuilder.addIndexBuilderListener(this);
				indexBuilder.addToChanged(stmt);
				indexBuilder.doUpdateIndex();
			}
		}
    }
    
    @Override
    public void notifyOfIndexingEvent(EventTypes event) {
    	
    	synchronized(this){	// Here it is not necessary to use synchronized since this section is not used by multiple threads.  
	        switch( event ){
	        	case START_FULL_REBUILD:
	            	break;
	            case FINISH_FULL_REBUILD: 
	            	break;
	            case START_UPDATE:
	            	break;
	            case FINISHED_UPDATE:
	            	isIndexUpdated = true;
	            	this.notifyAll();
	            	break;
	            default: 
	                log.debug("ignoring event type " + event.name());
	                break;    
	        } 
    	}
    }
    
    // <div class='record-created-by'><b>Created by</b>: Gawri Edussuriya</div>
    
    private String getHtmlForEditor(Individual ind){
		String btnGroupHtml = "";
		
		if (recordState == RecordsState.DRAFT){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'UnderReview');\"><div class='sprite-mr-review-icon' title='Send this record for review'></div></div>" +
					"<div class='manage-records-button' onclick=\"deleteRecord(this);\"><div class='sprite-mr-delete-icon' title='Delete this record'></div></div>";
		}else if (recordState == RecordsState.UNDER_REVIEW){
			btnGroupHtml =  "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>";
		}else if (recordState == RecordsState.PUBLISHED_OPEN_ACCESS){
			btnGroupHtml =  "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Draft');\"><div class='sprite-mr-send-to-draft-icon' title='Send this record back to Draft'></div></div>";
		}else if (recordState == RecordsState.PUBLISHED_QUT_ACCESS){
			btnGroupHtml =  "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Draft');\"><div class='sprite-mr-send-to-draft-icon' title='Send this record back to Draft'></div></div>";
		}
		
		return btnGroupHtml;
	}
	
    // Removed APPROVED status from the RecordState: "<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Approved');\"><div class='sprite-mr-approve-icon' title='Approve this record'></div></div>" +
	private String getHtmlForCurator(Individual ind){
		String btnGroupHtml = "";
		
		if (recordState == RecordsState.DRAFT){
			btnGroupHtml =  "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedOpenAccess');\"><div class='sprite-mr-publish-open-icon' title='Publish this record for open access'></div></div>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedQUTAccess');\"><div class='sprite-mr-publish-qut-icon' title='Publish this record for QUT access'></div></div>" +
					"<div class='manage-records-button' onclick=\"assignRecordToReview(this);\"><div class='sprite-mr-assign-icon' title='Assign record to review'></div></div>" +
					"<div class='manage-records-button' onclick=\"deleteRecord(this);\"><div class='sprite-mr-delete-icon' title='Delete this record'></div></div>";
		}else if (recordState == RecordsState.UNDER_REVIEW){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>";
		}else if (recordState == RecordsState.PUBLISHED_OPEN_ACCESS){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Draft');\"><div class='sprite-mr-send-to-draft-icon' title='Send this record back to Draft'></div></div>";
		}else if (recordState == RecordsState.PUBLISHED_QUT_ACCESS){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Draft');\"><div class='sprite-mr-send-to-draft-icon' title='Send this record back to Draft'></div></div>";
		}else if (recordState == RecordsState.BEING_REVIEWED_BY_ADMIN){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedOpenAccess');\"><div class='sprite-mr-publish-open-icon' title='Publish this record for open access'></div></div>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedQUTAccess');\"><div class='sprite-mr-publish-qut-icon' title='Publish this record for QUT access'></div></div>" +
					"<div class='manage-records-button' onclick=\"assignRecordToReview(this);\"><div class='sprite-mr-assign-icon' title='Assign record to review'></div></div>" +
					"<div class='manage-records-button' onclick=\"notifyUserAndSendRecordBack(this);\"><div class='sprite-mr-sendback-icon' title='Notify user and send back'></div></div>";
		}else if (recordState == RecordsState.PUBLISHED_BY_ADMIN){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" ;
		}else if (recordState == RecordsState.ASSIGNED_BY_ADMIN){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>";
		}
		
		return btnGroupHtml;
	}
	
	private String getHtmlForAdmin(Individual ind){
		String btnGroupHtml = "";
		
		if (recordState == RecordsState.DRAFT){
			btnGroupHtml =  "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedOpenAccess');\"><div class='sprite-mr-publish-open-icon' title='Publish this record for open access'></div></div>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedQUTAccess');\"><div class='sprite-mr-publish-qut-icon' title='Publish this record for QUT access'></div></div>" +
					"<div class='manage-records-button' onclick=\"assignRecordToReview(this);\"><div class='sprite-mr-assign-icon' title='Assign record to review'></div></div>" +
					"<div class='manage-records-button' onclick=\"deleteRecord(this);\"><div class='sprite-mr-delete-icon' title='Delete this record'></div></div>";
		}else if (recordState == RecordsState.UNDER_REVIEW){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>";
		}else if (recordState == RecordsState.PUBLISHED_OPEN_ACCESS){
			btnGroupHtml ="<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Draft');\"><div class='sprite-mr-send-to-draft-icon' title='Send this record back to Draft'></div></div>";
		}else if (recordState == RecordsState.PUBLISHED_QUT_ACCESS){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'Draft');\"><div class='sprite-mr-send-to-draft-icon' title='Send this record back to Draft'></div></div>";
		}else if (recordState == RecordsState.READY_TO_REVIEW){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<div class='manage-records-button' onclick=\"assignRecordToReview(this);\"><div class='sprite-mr-assign-icon' title='Assign record to review'></div></div>" +
					"<div class='manage-records-button' onclick=\"notifyUserAndSendRecordBack(this);\"><div class='sprite-mr-sendback-icon' title='Notify user and send back'></div></div>";
		}else if (recordState == RecordsState.BEING_REVIEWED_BY_ADMIN){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedOpenAccess');\"><div class='sprite-mr-publish-open-icon' title='Publish this record for open access'></div></div>" +
					"<div class='manage-records-button' onclick=\"moveRecordToNextLevel(this, 'PublishedQUTAccess');\"><div class='sprite-mr-publish-qut-icon' title='Publish this record for QUT access'></div></div>" +
					"<div class='manage-records-button' onclick=\"assignRecordToReview(this);\"><div class='sprite-mr-assign-icon' title='Assign record to review'></div></div>" +
					"<div class='manage-records-button' onclick=\"notifyUserAndSendRecordBack(this);\"><div class='sprite-mr-sendback-icon' title='Notify user and send back'></div></div>";
		}else if (recordState == RecordsState.PUBLISHED_BY_ADMIN){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>";
		}else if (recordState == RecordsState.ASSIGNED_BY_ADMIN){
			btnGroupHtml = "<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "\" target=\"_blank\"><div class='sprite-mr-view-icon' title='View record'></div></a>" +
					"<a class='manage-records-button' href=\"/display/" + ind.getLocalName() + "?displayMode=edit\" target=\"_blank\"><div class='sprite-mr-edit-icon' title='Edit record'></div></a>";
		}
		
		return btnGroupHtml;
	}
}
