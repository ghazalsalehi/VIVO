package edu.cornell.mannlib.vitro.webapp.controller.jena;


import java.io.File;
import java.io.*;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.FileManager;


public class IngestGSYamlRecords extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(IngestGSYamlRecords.class.getName());
	
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		if (!isAuthorizedToDisplayPage(request, response,
				SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTIONS)) {
    		return;
    	}
		
		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		String GSRDFFile = contextPath + "/qut_data/GSRecords/GSYamlRecords.rdf";

        VitroRequest vreq = new VitroRequest(request);        

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/specific/ingestGSYamlRecords_output.jsp");
        request.setAttribute("title","ingest GS records");
        
        try{
        	OntModel aboxModel = Utils.getABoxModel(vreq.getSession(), servletContext);
        	
        	Lock lock =  aboxModel.getLock();
            lock.enterCriticalSection(Lock.WRITE);
            
            Model changes = ModelFactory.createDefaultModel();
            
            InputStream in = FileManager.get().open( GSRDFFile );
            if (in == null) {
            	lock.leaveCriticalSection();
                throw new IllegalArgumentException("File: " + GSRDFFile + " not found");
            }

            changes.read(in, null);
             
            String newFileName = GSRDFFile + ".ingested";
            File f = new File(GSRDFFile);
            File f1 = new File(newFileName);
            
            try {
            	f.renameTo(f1);
            } catch (Exception e) {
            	lock.leaveCriticalSection();
                throw new Exception("Failed to move"+ GSRDFFile +" file : " + e.getMessage());
            }
            
            aboxModel.add(changes);
            lock.leaveCriticalSection();
            
            String successMsg = "Successfully processed your request."; 
			request.setAttribute("msg", successMsg);
        	
        }catch (Exception e){
			request.setAttribute("error", e.getMessage());
        }
        
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\""+ vreq.getAppBean().getThemeDir() + "css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error(this.getClass().getName() + " could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }        
	}
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request,response);
    }
	

}
