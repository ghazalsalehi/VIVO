/*
Copyright (c) 2013, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.jena;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class ManageQUTResearchData extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ManageQUTResearchData.class.getName());

    @Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) {

	    if (!isAuthorizedToDisplayPage(request, response, SimplePermission.QUT_RESEARCH_DATA_PAGE.ACTIONS)) {
			return;
		}

        VitroRequest vreq = new VitroRequest(request);        

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/specific/manageQUT_data.jsp");
        request.setAttribute("title","Manage QUT Research Data");
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\""+vreq.getAppBean().getThemeDir()+"css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error(this.getClass().getName() + " could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }
    }

    @Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }
}
