/*
Copyright (c) 2015, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.systemIntegration;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public abstract class SystemIntegrationModule {
	
	private static final Log log = LogFactory.getLog(SystemIntegrationModule.class.getName());
	
	abstract void processRequest(VitroRequest vreq, HttpServletResponse response) throws Exception;
	
	protected void doJSPResponse(VitroRequest vreq, HttpServletResponse response, String template){
		 
		RequestDispatcher rd = vreq.getRequestDispatcher(template);
		//vreq.setAttribute("title","Manage Records");
		//vreq.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + vreq.getAppBean().getThemeDir() + "css/edit.css\"/>");
		
		try {
			rd.forward(vreq, response);
	    } catch (Exception e) {
	    	log.error(this.getClass().getName() + " could not forward to view.");
	        log.error(e.getMessage());
	        log.error(e.getStackTrace());
	    }
	}
	
	protected void doAjaxResponse(VitroRequest vreq, HttpServletResponse response, JSONObject json) {
		try {
			response.getWriter().print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected RoleLevel getRoleLevel(VitroRequest vreq) {
		 //UserAccount loggedInUser = LoginStatusBean.getCurrentUser(vreq);
         RoleLevel role = RoleLevel.getRoleFromLoginStatus(vreq);
         
         return role;
	}
}
