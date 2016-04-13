package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;

public abstract class ManageRecordsModule {
	
	private static final Log log = LogFactory.getLog(ManageRecordsModule.class.getName());
	
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
	
	protected OntModel getOntoModel(VitroRequest vreq){
			
		OntModelSelector ontModelSelector = vreq.getOntModelSelector();
		OntModel ontModel = ontModelSelector.getFullModel();
		
		return ontModel;
	}
}
