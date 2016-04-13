/*
Copyright (c) 2014, QUT
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.freemarker; 

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

public class SpatialDataFinderAbout extends FreemarkerHttpServlet {
	
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SpatialDataFinderAbout.class);
    private static final String TEMPLATE_DEFAULT = "spatialDataFinderAbout.ftl";
    
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        ApplicationBean application = vreq.getAppBean();
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("aboutText", application.getAboutText());
        body.put("acknowledgeText", application.getAcknowledgeText());
        
        return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
    }

    @Override
    protected String getTitle(String siteName, VitroRequest vreq) {
    	return "About " + siteName;
    }

}
