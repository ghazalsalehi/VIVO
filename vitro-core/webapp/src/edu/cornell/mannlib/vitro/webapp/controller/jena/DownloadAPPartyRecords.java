package edu.cornell.mannlib.vitro.webapp.controller.jena;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.util.EntityUtils;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.NewURIMakerVitro;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.NewURIMaker;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.ModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.StandardModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;

import java.io.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import qut.crmm.utils.VivoProps;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;

public class DownloadAPPartyRecords extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(DownloadAPPartyRecords.class.getName());
	
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String action = request.getParameter("action");
		boolean isDaillyUpdate = false;
		if (action == null) {
			isDaillyUpdate = false;
			
			if (!isAuthorizedToDisplayPage(request, response,
					SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTIONS)) {
			    	return;
			 }
			
			 request.setAttribute("bodyJsp","/templates/edit/specific/downloadAPPartyRecords_output.jsp");
		     request.setAttribute("title","download AP party records");
		}else{
			if (action.equals("daillyUpdate")){isDaillyUpdate = true;}
		}
         
		RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
		
		VitroRequest vreq = new VitroRequest(request);	
		ServletContext servletContext = getServletContext();
		//OntModel ontModel = ModelContext.getJenaOntModel(servletContext);
		OntModel aboxModel = Utils.getABoxModel(vreq.getSession(), servletContext);

        String  defaultNameSpace = vreq.getWebappDaoFactory().getDefaultNamespace();     
        String contextPath = servletContext.getRealPath(File.separator);
		String crmmConfFile = contextPath + "/qut_data/conf/config.conf";
		
		/*PrintStream p3= new PrintStream("C:\\Apps\\rdf\\ABoxFromAPDownlaod_AUTOMATIC.rdf");
		aboxModel.write(p3);
		p3.close();*/
		
		if (isDaillyUpdate){
			log.info("REGULAR_UPDATE STARTED.");
		}
		
		long updatedRecordsCount = 0;
		long newRecodsCount = 0;
		APRequest2Jena AP2jena= null;
		int i = 1;
		
		try{
			
			while(true){
				HttpClient client = new DefaultHttpClient();
				//String reqStr = "http://searching.qut.edu.au/search/search.cgi?collection=staffprof&query=!padrenull&form=xml&num_ranks=" + 350 + "&start_rank=" + i;
				String reqStr = "http://searching.qut.edu.au/search/search.cgi?collection=staffprof&query=!padrenull&form=xml&num_ranks=" + 100 + "&start_rank=" + i;
				//String reqStr = "http://searching.qut.edu.au/search/search.cgi?collection=staffprof&query=urquhaa&form=xml";
				
				
				HttpGet req = new HttpGet(reqStr);
				HttpResponse res = client.execute(req);
				
				String resultStr = "";
				
				BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));		    
				String line = "";
				while ((line = br.readLine()) != null) {
					resultStr += line;
				} 
	
				resultStr = resultStr.replaceAll("<p><table.*>.*</table></p>", "");
				
				Lock lock = null;
				Document doc = null;
				try{
					
					doc = stringToDom(resultStr);
					
					if (! isEmptyResult(doc)){
						
						lock =  aboxModel.getLock();
				        lock.enterCriticalSection(Lock.WRITE);
				        
				        log.info("RECORDS STARTED: " + i + " -> 100"); 
				        
				        AP2jena = new APRequest2Jena(aboxModel, defaultNameSpace, crmmConfFile);
				        AP2jena.init();
						AP2jena.processRequest(doc);
						AP2jena.saveFinalKeys();
						
						Model changes = AP2jena.getChanges();
						if (changes != null){
							aboxModel.add(changes);
						}
						
						lock.leaveCriticalSection();
						
						updatedRecordsCount += AP2jena.getUpdatedRecordsCount();
						newRecodsCount += AP2jena.getNewRecodsCount();
						AP2jena = null;
						
						System.gc();
						
						log.info("RECORDS FINISHED:" + (i+100));
						
					}else{
						break;
					}
			
                    i += 100;
                    
				}catch(Exception e){
					if (lock != null){
						lock.leaveCriticalSection();
					}
					throw new Exception(e.getMessage());
				}
			}
			
			String msg = String.valueOf(updatedRecordsCount) + " records updated. " + String.valueOf(newRecodsCount) + " new records created.";
			
			if (isDaillyUpdate){
				String successMsg = "REGULAR_UPDATE WAS SUCCSESS. [" + msg + "]";
				response.getWriter().print(successMsg);
				log.info(successMsg);
			}else{
				String successMsg = "MANUAL_UPDATE WAS SUCCSESS. [" + msg + "] <br/><br/> <a href='/SearchIndex?rebuild=Rebuild'>Click here to rebuild the search index</a>";
				request.setAttribute("msg", successMsg);
				rd.forward(request, response);
				log.info(successMsg);
			}
		}catch(Exception e){
        	 
        	if (isDaillyUpdate){
        		response.getWriter().print("REGULAR_UPDATE FAILED : " + e.getMessage());
        		log.error("REGULAR_UPDATE FAILED: " + e.getMessage());
			}else{
				request.setAttribute("error", e.getMessage());
				rd.forward(request, response);
				log.error("MANUAL_UPDATE FAILED: " + e.getMessage());
			}
        	
        	// sender : researchdatafinder@qut.edu.au
        	// send an email to relevant person.
        }
	}
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request,response);
    }
	
	public boolean isEmptyResult(Document doc) throws Exception{
		
		if (doc == null){
			throw new Exception("Invalid xml response recived from the AP web server. document is null.");
		}
		String rootNodeName = doc.getDocumentElement().getNodeName();
		if (rootNodeName.equals("results")){
			NodeList nResultNodesList = doc.getElementsByTagName("result");
			if (nResultNodesList.getLength() < 1){
				return true;
			}else{
				return false;
			}
		}else{
			throw new Exception("Invalid xml response recived from the AP web server. root node <results> not found.");
		}
	}

	public Document stringToDom(String xmlSource) throws Exception{
		try{
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		     DocumentBuilder builder = factory.newDocumentBuilder();
		     return builder.parse(new InputSource(new StringReader(xmlSource)));
		}catch(Exception e){
			throw new Exception("Failed to create the xml document.");
		}       
	}
}
