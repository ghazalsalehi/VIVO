package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;

import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;


public class DashBoardModule extends ManageRecordsModule{
	
	@Override
	void processRequest(VitroRequest vreq, HttpServletResponse response) {
		
		
		doJSPResponse(vreq, response, Controllers.MRS_DASHBOARD_JSP);
	}
}
