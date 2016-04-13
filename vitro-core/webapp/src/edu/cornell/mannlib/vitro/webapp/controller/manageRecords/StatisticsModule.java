package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.manageRecords.ManageMyWorkspaceModule.RecordsState;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import net.sf.json.JSONObject;


public class StatisticsModule extends ManageRecordsModule{
	
	@Override
	void processRequest(VitroRequest vreq, HttpServletResponse response) {
		String action = vreq.getParameter(Utils.MRS_MODULE_PARAM_ACTION);
		
		if (action != null ){
			// everyone
			if (action.equals("getRecordModifiedDate")){
				String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);	// ex: /display/q8
				String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
				String URL = defaultNamespace + key; 
				
				OntModel ontModel = getOntoModel(vreq);
				Property pFreeTextValue1 = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
                Property pDateRecordModified = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "dateRecordModified");
               
                String dateRecorModified =  Utils.getPropertyDataValue(vreq, ontModel, URL, pDateRecordModified, pFreeTextValue1);
                
                JSONObject json = new JSONObject();
                json.put("recorModifiedDate", dateRecorModified);
                json.put("MRS_ERROR_CODE", Utils.MRS_SUCESS);
				json.put("MRS_ERROR_MSG", Utils.MRS_ERROR_MSG_SUCESS);
				
				doAjaxResponse(vreq, response, json);
			}
		}else{
			doJSPResponse(vreq, response, Controllers.MRS_STATISTICS_JSP);
		}
	}
}
