

package edu.cornell.mannlib.vitro.webapp.controller.jena;

import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.ModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.StandardModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;

import qut.crmm.utils.VivoProps;

import java.io.File;

public class InsertInitialKeys extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ManageQUTResearchData.class.getName());

    @Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) {

		if (!isAuthorizedToDisplayPage(request, response,
				SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTIONS)) {
    		return;
    	}
		
		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		String crmmConfFile = contextPath + "/qut_data/conf/config.conf";

        VitroRequest vreq = new VitroRequest(request);        

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/specific/insertInitialKeys_output.jsp");
        request.setAttribute("title","insert Initials Keys");
		
		try{
			
			//ModelSelector writeModelSelector = StandardModelSelector.selector;
	        //Model writeModel = writeModelSelector.getModel(vreq, getServletContext());
	        OntModel aboxModel = Utils.getABoxModel(vreq.getSession(), servletContext);
	        
	        Model changes = ModelFactory.createDefaultModel();
	        VivoProps vp = new VivoProps(changes, crmmConfFile);
	        
	        //String lkcUri = vp.appProps.get("baseNS") + "lkc";
	    	//String ruric = vp.appProps.get("baseNS") + "ruric";
	    	String RDAHarvestedInfo = vp.appProps.get("baseNS") + "RDAHarvestedInfo";
	        
	    	//Uncomment these if you want to reset..
	        /*Resource r1localKey = aboxModel.getResource(lkcUri);	// localkey. this method behave same as createResource
			if (r1localKey != null){
				r1localKey.removeProperties();
				r1localKey.addProperty(vp.props.get("localKeyCounter"), "13450");
			}else{
				throw new Exception("cannot create local key resource. "+ lkcUri);
			}	
			
			Resource rResourceKey = aboxModel.getResource(ruric);	// resource key. this method behave same as createResource
			if (rResourceKey != null){
				rResourceKey.removeProperties();
				rResourceKey.addProperty(vp.props.get("resourceURICounter"), "1");
			}else{
				throw new Exception("cannot create local key resource. "+ ruric);
			}*/
	    	
	    	Resource rRDAHarvestedInfo = aboxModel.getResource(RDAHarvestedInfo);	
			if (rRDAHarvestedInfo != null){
				rRDAHarvestedInfo.removeProperties(); 	
				rRDAHarvestedInfo.addProperty(vp.props.get("lastHarvestedDate"), "2014-03-06T16:43:51"); // yyyy-MM-dd'T'HH:mm:ss : 2014-03-06T16:43:51
				rRDAHarvestedInfo.addProperty(vp.props.get("previousHarvestedDate"), "2013-11-06T16:43:51");
				rRDAHarvestedInfo.addProperty(vp.props.get("freeTextValue1"), "2013-04-05T12:43:41");	// Previous Previous Harvested Date. not important but we keep this info.
			}else{
				throw new Exception("Cannot craete RDA Harvested Info. "+ RDAHarvestedInfo);
			}

			//String successMsg = "Successfully processed your request. <br/> <a href='" + lkcUri + "'>" + lkcUri + "</a><br/> <a href='" + ruric + "'>" + ruric + "</a>"; 
			String successMsg = "Successfully processed your request. <br/> <a href='" + RDAHarvestedInfo + "'>" + RDAHarvestedInfo + "</a>"; 
			request.setAttribute("msg", successMsg);
				
		}catch (Exception e){
				request.setAttribute("error", e.getMessage());
		}
        
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\""+vreq.getAppBean().getThemeDir()+"css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error(this.getClass().getName()+" could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }
    
    @Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }
}
