/*
Copyright (c) 2014, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;

public class ManageRecordsController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ManageRecordsController.class.getName());

	public static final Actions REQUIRED_ACTIONS = SimplePermission.SEE_QUT_MANAGE_RECORDS_PAGE.ACTIONS;
	
	protected Actions requiredActions(VitroRequest vreq) {
    	return REQUIRED_ACTIONS;
	}
	
    @Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) {

	    if (!isAuthorizedToDisplayPage(request, response, SimplePermission.SEE_QUT_MANAGE_RECORDS_PAGE.ACTIONS)) {
			return;
		}

	    VitroRequest vreq = new VitroRequest(request); 
	    String module = request.getParameter(Utils.MANAGE_RECORDS_MODULE_PARAMETER);
	    if (module != null){
	    	if (module.equals(Utils.MRS_MODULE_DASHBOARD_VALUE)){
	    	    ManageRecordsModule dashBoardModule = new DashBoardModule();
	    	    try {
					dashBoardModule.processRequest(vreq, response);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
	    	}else if (module.equals(Utils.MRS_MODULE_WROKSPACE_VALUE)){
	    		ManageRecordsModule workspaceModule = new ManageMyWorkspaceModule();
	    		try {
					workspaceModule.processRequest(vreq, response);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
	    	}else if (module.equals(Utils.MRS_MODULE_STATISTICS_VALUE)){
	    		ManageRecordsModule statisticModule = new StatisticsModule();
	    	    try {
	    	    	statisticModule.processRequest(vreq, response);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
	    	}else if (module.equals(Utils.MRS_MODULE_DOWNLOADS_VALUE)){
	    		ManageRecordsModule statisticModule = new DownloadsModule();
	    	    try {
	    	    	statisticModule.processRequest(vreq, response);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
	    	}
	    }else{
	    	sendErrorResponse(request, response, "Please check the URL is correct or click the Back button to try another link.");
	    }
    }

    @Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }
    
    public void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, String errorMsg){
    	RequestDispatcher rd = request.getRequestDispatcher(Controllers.MRS_ERROR_JSP);
    	request.setAttribute("MRS_error_msg", errorMsg);
    	try {
            rd.forward(request, response);
    	} catch (Exception e) {
    		log.error(this.getClass().getName() + " could not forward to view.");
    		log.error(e.getMessage());
    	}
    }
}
