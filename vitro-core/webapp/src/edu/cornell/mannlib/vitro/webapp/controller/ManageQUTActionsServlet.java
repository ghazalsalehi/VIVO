

package edu.cornell.mannlib.vitro.webapp.controller;

import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import groovy.xml.XmlUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;

import qut.crmm.model.CModel;
import qut.crmm.model.transform.spec.Java2RIF;
import qut.crmm.model.transform.spec.Jena2Java;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListQueryResults;
import edu.cornell.mannlib.vitro.webapp.controller.manageRecords.ManageRecordsEmailMessage;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.NewURIMakerVitro;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexBuilder;
import edu.cornell.mannlib.vitro.webapp.utils.SparqlQueryUtils;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import edu.cornell.mannlib.vitro.webapp.dao.jena.IndividualDaoJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditSubmissionUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.NewURIMaker;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.ModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.StandardModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

import qut.crmm.utils.VivoProps;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


public class ManageQUTActionsServlet extends BaseEditController {

	private static final Log log = LogFactory.getLog(ManageQUTActionsServlet.class.getName());
	
	public String RMFilelocation = "";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		VitroRequest vreq = new VitroRequest(request);		
		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		String crmmConfFile = contextPath + "/qut_data/conf/config.conf";
		
		Lock lock = null;
		String action = request.getParameter("action");
		if (action == null){
			return;
		}
		
		if (action.equals("ingestRMData")) {
			
			String validFileNames[] = {"People.csv", "Projects.csv", "FOR.csv", "Funding.csv", "Publications.csv"};
			
			try {
				
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> fields = upload.parseRequest(request);
				
				for (String fileName: validFileNames) {
					int apperence = 0;
					for (FileItem t : fields) {
						if (!t.isFormField()) {
							if (t.getName().equals(fileName))
								apperence++;
						} 
					}
					
					if (apperence != 1)
						throw new Exception("Duplicate or missing file " + fileName);
				}
				
				if (createFileLocation(contextPath)){	// file created successfully.
					//request.setAttribute("path", RMFilelocation);
					for (FileItem t : fields) {
						if (!t.isFormField() && !t.getName().equals("")) {
							String tofilePath = RMFilelocation + "/" +  t.getName();					
							t.write(new File(tofilePath));
						}
					}
				}else{
					throw new Exception("Failed to create directotry. " + RMFilelocation);
				}
				
				// important: better we take mysql backup before doing ingest
				
		        String  defaultNameSpace = vreq.getWebappDaoFactory().getDefaultNamespace();
		        OntModel aboxModel = Utils.getABoxModel(vreq.getSession(), servletContext);
		        
		        log.info("RESEARCH_MASTER INGEST IS STARTED.");
		        
	            lock =  aboxModel.getLock();
		        lock.enterCriticalSection(Lock.WRITE);
	            
		        RMCSVReports2Jena oRMcsv2Jena = new RMCSVReports2Jena(defaultNameSpace, aboxModel, crmmConfFile);
		        oRMcsv2Jena.init();
		        log.info("RESEARCH_MASTER Started processing RM data files.");
				oRMcsv2Jena.process(RMFilelocation);
				oRMcsv2Jena.saveFinalKeys();
				log.info("RESEARCH_MASTER Finished processing RM data files.");
				
				log.info("RESEARCH_MASTER Started RM data ingest into Abox model..");
				
				Model changes = oRMcsv2Jena.getChanges();
				if (changes != null){
					aboxModel.add(changes);
				}
				
	            lock.leaveCriticalSection();
	            
	            log.info("RESEARCH_MASTER Finished RM data ingest into Abox model..");
	            
	            long newRecodsCount = oRMcsv2Jena.getNewRecodsCount();
				long updatedRecordsCount = oRMcsv2Jena.getUpdatedRecordsCount();
				
				String msg = String.valueOf(updatedRecordsCount) + " records updated. " + String.valueOf(newRecodsCount) + " new records created.\n";
				log.info("RESEARCH_MASTER INGEST WAS SUCCSESS. " + msg);
				
				String successMsg = msg + "Please rebuild the search index to complete your request. <br/><br/> <a target='_blank' href='../SearchIndex?rebuild=Rebuild'>Click here to rebuild the search index</a>"; 
				request.setAttribute("msg", successMsg);
				
			}catch (Exception e){
				if (lock != null){
					lock.leaveCriticalSection();
				}
				
				log.error("RESEARCH_MASTER INGEST WAS  FAILED: " + e.getMessage());
				request.setAttribute("error", e.getMessage());
			}

		}else if (action.equals("createFeed") || action.equals("createAutoFeed")) {
			
			try{
				 //Dataset dataset = DatasetFactory.create(ModelContext.getJenaOntModel(servletContext));
		         OntModel model = ModelContext.getJenaOntModel(servletContext);
		         
		         // CR: 54
		         String strLastHarvestedDate = Utils.loadLastHarvestedDate(model, vreq.getWebappDaoFactory().getDefaultNamespace());
		         Date dateLastHarvested = null;
		         try{
		        	 dateLastHarvested =  Utils.xsdDateTimeFormat.parse(strLastHarvestedDate);
	        	 }catch(Exception e){
	        		 throw new Exception("cannot convert time to a valid date.");
	        	 }
		         
		         String relationTypesFile = contextPath + "/qut_data/conf/relationTypes.txt";
		         CModel cModel = new CModel();
		         Jena2Java jena2Java = new Jena2Java();
		         jena2Java.init(crmmConfFile, model, cModel, relationTypesFile, dateLastHarvested);
		         jena2Java.process();
		         
		         Date currentDate = Calendar.getInstance().getTime(); // Process until this time.
     		     String strFinalHarvestedDate = (currentDate == null) ? null : Utils.xsdDateTimeFormat.format(currentDate);
		         
		         String propertyFile = contextPath + "/qut_data/conf/properties.yaml";
		         
		         String feedLocation = contextPath;
		         String array[] = feedLocation.split("vivo");
		         if (array.length == 2)
		         {
		        	feedLocation = array[0] + "/oaicat/WEB-INF/META/records.xml";
		        	Java2RIF java2RIF = new Java2RIF(cModel);
			        java2RIF.readProperties(propertyFile);
			     
			        //-------------------------------------------------------------------------------
			        //This is for generating XML RIF-CS  feed
					/*java2RIF.initForXMLRIF();
					java2RIF.processObjectsForXMLRIF();*/
			        
			        //This is for generating RIF-CS feed with OAICAT
			        java2RIF.init();
			        java2RIF.processObjects(); 
			        java2RIF.complete();
			        //-------------------------------------------------------------------------------
			    
			         String feedStr = java2RIF.getFeedXMLAsString();
			         try{
			        	 saveRIFCSFeed(feedStr, feedLocation);
			         }catch (Exception e){
							throw e;
					 }
			         
			         // LIBRDF-57
			         Date value = Calendar.getInstance().getTime();
         		     String formattedDateStr = (value == null) ? "" : Utils.xsdDateTimeFormat1.format(value);
			         String feedBackupLocation = Utils.RDF_HOME + "backups/harvester/" + formattedDateStr +  "-records.xml";
			         
			         try{
			        	 saveRIFCSFeed(feedStr, feedBackupLocation);
			         }catch (Exception e){
							throw e;
					 }
			         
			         // LIBRDF-55
			         try{
			        	 reloadOAICAT(vreq);
			         }catch (Exception e){
						 throw e;
					 }
			         
			         String successMsg  = "Successfully processed your request. feed is ready to harvest. <br/><br/> <a target='_blank' href='http://researchdatafinder.qut.edu.au/oaicat/OAIHandler?verb=ListRecords&metadataPrefix=rif'>Click here to see the feed</a>";
			         if (action.equals("createAutoFeed")){
			        	 response.getWriter().print("[ManageQUTResearchData]: " + successMsg);
			         }else{
				         request.setAttribute("msg", successMsg);
			         }
			         
			         // CR: 54
			         Utils.SaveNextRDAHarvestedInfo(vreq, strLastHarvestedDate, strFinalHarvestedDate);
			         log.info("RIF_CS Feed is created successfully and ready to harvest");

		         }else{
		        	 throw new Exception("Failed to find the feed localtion in vivo.");
		         }

			}catch (Exception e){
				
				 if (action.equals("createAutoFeed")){
					response.getWriter().print("Error creating the RIF_CS feed: " + e.getMessage());
		         }else{
		        	request.setAttribute("error", e.getMessage());
		         }

				log.error("Error creating the RIF_CS feed: " + e.getMessage());
				
				// LIBRDF-55
				// Send email to rdf team
				List<String> errors = new ArrayList<String>();
				errors.add(e.getMessage());
				
				Map<String, Object> emailContent = new HashMap<String, Object>();
				emailContent.put("errors", errors);
				emailContent.put("subject", "ERROR: RDF developer/administrator immediate attention required.");
				emailContent.put("recipientName", "Admin");
				emailContent.put("paragraph_1", "Folowing errors have been found by the RDF harvester.");
				emailContent.put("errorMsg", "RDF  developer/administrator atttention required.");
				
				ManageRecordsEmailMessage emailMessgae = new ManageRecordsEmailMessage(emailContent);
				emailMessgae.setTemplate(Utils.TEMPLATE_RDF_DEFAULT_EMAIL_ERROR);
				emailMessgae.addRecipients(Utils.RDF_WEBSITE_EMAIL_ADDRESS);
				
				try{
					emailMessgae.sendEmail(vreq);
				}catch(Exception e1){
					if (action.equals("createAutoFeed")){
						response.getWriter().print("Error Sending the email: " + e1.getMessage());
					}else{
						request.setAttribute("error", e1.getMessage());
					}
				}
			}
		}else if (action.equals("createSitemap") || action.equals("createAutoSitemap")) {
			
			List<String> errors = new ArrayList<String>();
			
			try{
				String baseUrl = "http://" + vreq.getServerName();	//http://researchdatafinder.qut.edu.au
				//WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, new File(contextPath));
				WebSitemapGenerator wsg = WebSitemapGenerator.builder(baseUrl, new File(contextPath)).gzip(true).build();
				String startTime = Utils.xsdDateTimeFormat.format(new Date());
				
				int pageSize = 10000;
				int page = 1;
				while(true){
					List<Individual> individuals = Utils.getPublishedOpenAccessRecords(vreq, page, pageSize);
					if ((individuals == null) || (individuals.isEmpty())){
						break;
					}else{
						for (int i = 0; i < individuals.size(); i++){
							Individual ind = individuals.get(i);
							String loc = baseUrl + "/display/" + ind.getLocalName();
							String strDateRecordModified = Utils.getRecordObjectPropertyValue(ind, Utils.PROPERTY_RECORD_MODIFIED_DATE, Utils.PROPERTY_FREE_TEXT_VALUE_1); // ex:2014-07-10T11:20:51
							
							try{
								Date dateRecModified  = null;
								if (strDateRecordModified.matches("\\d{4}-\\d{2}-\\d{2}")) {
									dateRecModified = Utils.xsdSimpleDateTimeFormat.parse(strDateRecordModified);	// "yyyy-MM-dd
								}else{
									dateRecModified = Utils.xsdDateTimeFormat.parse(strDateRecordModified);	// "yyyy-MM-dd'T'HH:mm:ss"
								}
								
								// this will configure the URL with lastmod=now, priority=1.0, changefreq=hourly
								WebSitemapUrl url = new WebSitemapUrl.Options(loc).lastMod(dateRecModified).priority(1.0).changeFreq(ChangeFreq.MONTHLY).build();
								wsg.addUrl(url);
								
								//throw new Exception("Error test");
								
							}catch (Exception ex){	
								errors.add("Invalid Date record modified : " + loc + ": " + strDateRecordModified);
							}
						}
						page++;
					}
				}
				
				String endTime = Utils.xsdDateTimeFormat.format(new Date());
				
				// Stats : [Time differences for 68  records : (2014-07-18T14:37:53 : 2014-07-18T14:37:56)]
				
				wsg.write();
				
				int recordsCount = Utils.getPublishedForOpenAcessRecordsCount(vreq);
				if ( recordsCount > 50000){
					wsg.writeSitemapsWithIndex(); // generate the sitemap_index.xml
				}
				
				String errorMsg = "";
	        	for (int j=0; j < errors.size(); j++){
	        		errorMsg += errors.get(j) + "\n";
	        	}
	        	 
				String successMsg  = "Successfully processed your request. sitemap is ready to crawl.\n [Records count : " + recordsCount + " (" +  startTime + " : " + endTime + ")]";
		        if (action.equals("createAutoSitemap")){
		        	 response.getWriter().print("[ManageQUTResearchData]: " + successMsg);
		        	 if (!errorMsg.equals("")){response.getWriter().print("\n" + errorMsg);}	 
		        }else{
		        	 if (!errorMsg.equals("")){successMsg = successMsg + "\n" + errorMsg;}
			         request.setAttribute("msg", successMsg);
		        }
		         
				log.info("Sitemap is created successfully and ready to crawl.");
			}catch (Exception e){
				
				 if (action.equals("createAutoSitemap")){
					response.getWriter().print("Error creating the site map : " + e.getMessage());
		         }else{
		        	request.setAttribute("error", e.getMessage());
		         }

				 errors.add("Error creating the site map: " + e.getMessage());
				 log.error("Error creating the site map: " + e.getMessage());
			}
			
			// Send email to RDF if there is an error.
			if (!errors.isEmpty()){
				Map<String, Object> emailContent = new HashMap<String, Object>();
				
				emailContent.put("errors", errors);
				emailContent.put("subject", "ERROR: RDF administrator/developer attention required.");
			   	emailContent.put("recipientName", "Admin");
			   	emailContent.put("paragraph_1", "Folowing errors found when generating the sitemap.");
			   	emailContent.put("errorMsg", "RDF administrator/Developer atttention required.");
			   	
			   	ManageRecordsEmailMessage emailMessgae = new ManageRecordsEmailMessage(emailContent);
			   	
				emailMessgae.setTemplate(Utils.TEMPLATE_RDF_DEFAULT_EMAIL_ERROR);
				emailMessgae.addRecipients(Utils.RDF_WEBSITE_EMAIL_ADDRESS);
				
				try{
					emailMessgae.sendEmail(vreq);
				}catch(Exception e){
					if (action.equals("createAutoSitemap")){
						response.getWriter().print("Error Sending the email: " + e.getMessage());
			        }else{
			        	request.setAttribute("error", e.getMessage());
			        }
				}
			}
		}
		//this is for changing the record status automatically. 
		//CR: 051 this has to be executed to change the record status from 'Publish - QUT Access' to 'PublishQUTAccess'
		/*else if (action.equals("ChangedToPublishedOpenAccess")) {
			//this will change record status from 'Publish - QUT Access' to 'PublishQUTAccess' and 'Published - QUT Access' to 'PublishedQUTAccess'
			
			//this has a bug. results.getIndividuals() only returns 10 individuals. but results.getHitCounts() returns the correct value.
			// so we have to execute several time until individuals.size() is zero.
			List<Individual> individuals = getPublishedForOpenAccessRecords(vreq);	
			log.info("Found Published - Open Access : " +  individuals.size());
			for (int i = 0; i < individuals.size(); i++) {
				Individual ind = individuals.get(i);
				try{
					changeRecordStatusTo(vreq, ind, Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE);
				}catch(Exception e){
					log.error(e.getMessage());
				}
			}
			log.info("FINISHED Published - Open Access : " +  individuals.size());
		}else if (action.equals("ChangedToPublishedQUTAccess")) {
			//this will change record status from 'Publish - QUT Access' to 'PublishQUTAccess' and 'Published - QUT Access' to 'PublishedQUTAccess'
			List<Individual> individuals = getPublishedForQUTAcessRecords(vreq); //this has a bug. results.getIndividuals() only returns 10 individuals. but results.getHitCounts() returns the correct value.
			log.info("Found Published - QUT Access : " +  individuals.size());
			for (int i = 0; i < individuals.size(); i++) {
				Individual ind = individuals.get(i);
				try{
					changeRecordStatusTo(vreq, ind, Utils.RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE);
				}catch(Exception e){
					log.error(e.getMessage());
				}
			}
			log.info("FINISHED Published - QUT Access : " +  individuals.size());
		}*/

		if (! action.equals("createAutoFeed")){
			RequestDispatcher rd = request
					.getRequestDispatcher(Controllers.BASIC_JSP);
			request.setAttribute("bodyJsp",
					"/templates/edit/specific/manageQUT_result.jsp");
			request.setAttribute("title", "Manage QUT Research Data");
			request.setAttribute("css",
					"<link rel=\"stylesheet\" type=\"text/css\" href=\""
							+ vreq.getAppBean().getThemeDir() + "css/edit.css\"/>");

			rd.forward(request, response);
		}
	}
	
	public void saveRIFCSFeed(String feed, String localtion) throws Exception {
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localtion), "UTF-8"));

		XmlUtil.serialize(feed, writer);
		
		writer.flush();
		writer.close();		
	}

	public boolean createFileLocation(String contextPath){
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
		Calendar cal = Calendar.getInstance();
		String folder = dateFormat.format(cal.getTime());
		
		RMFilelocation =  contextPath + "/qut_data/ResearchMaster_Data_Reports/"  + folder;
		
		File file = new File(RMFilelocation);
		if (!file.exists()) {
			if (file.mkdir()) {
				return true;
			}else{
				return false;
			}
		}
		
		return true;
	}

	//CR: 051 - to change db fileds automatically
	public void changeRecordStatusTo(VitroRequest vreq, Individual ind, String statusTo) throws Exception{
		WebappDaoFactoryJena wdf = new WebappDaoFactoryJena(vreq.getOntModelSelector());
		if (wdf == null){throw new Exception("WebappDaoFactoryJena is null");}
		
		IndividualDaoJena ida = (IndividualDaoJena)wdf.getIndividualDao();
		if (ida == null){throw new Exception("IndividualDaoJena is null");}
		
		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		String URL = defaultNamespace + ind.getLocalName(); 
		
		OntModelSelector ontModelSelector = wdf.getOntModelSelector();
		OntModel ontModel = ontModelSelector.getFullModel();
		
		IndexBuilder indexBuilder = IndexBuilder.getBuilder(vreq.getSession().getServletContext());
		Literal litrl = ontModel.createTypedLiteral(new String(statusTo));
    	
    	ontModel.enterCriticalSection(Lock.WRITE);
    	
    	try {
    		Resource res = ontModel.getResource(URL);
            	if (res == null){
            		ontModel.leaveCriticalSection();
            		throw new Exception("Resource " + URL + " is null");
            	}
            	
            	Property pValue = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
                Property precordStatus = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "publishRecord");
                
                
                if (litrl == null){
                	log.error("ManageQUTActionsServlet: next record status cannot be null.");
         			ontModel.leaveCriticalSection();
         			throw new Exception("next record status is null.");
                }
               
                Statement stmnt1 = res.getProperty(precordStatus);
                Resource robj = null;
             	if (stmnt1 != null){
             		robj = (stmnt1.getObject()).asResource();
             		if (robj != null) {	// edit existing recordStatus entry.
             			robj.removeAll(pValue);
             			robj.addProperty(pValue, litrl);
             			
        				indexBuilder.addToChanged(stmnt1);
        				indexBuilder.doUpdateIndex();
             			//ida.updateDateRecordModifiedTime(res, ontModel);
             		}else{
             			log.error("ManageQUTActionsServlet: record status object cannot be null.");
             			ontModel.leaveCriticalSection();
             			throw new Exception("Object for " + URL + " is null");
             		}
             	}else{
             		log.error("ManageQUTActionsServlet: record status stmt cannot be null.");
             		ontModel.leaveCriticalSection();
             		throw new Exception("Statement for " + URL + " is null");
             	}
        } finally {
        	ontModel.leaveCriticalSection();
        	log.info("CHANGED : " + ind.getURI());
        }
    }
	
	
	private void reloadOAICAT(VitroRequest vreq) throws Exception{
		String baseUrl = "http://" + vreq.getServerName();
		String reqStr = baseUrl + "/manager/html/reload?path=/oaicat";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet req = new HttpGet(reqStr);
		req.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(Utils.TOMCAT_MANAGER_USER_NAME, Utils.TOMCAT_MANAGER_PASSWORD),"UTF-8", false));
		HttpResponse res = client.execute(req);
		//String s = res.getStatusLine().toString();
		int resCode = res.getStatusLine().getStatusCode();
		
		if (resCode == 200){
			log.info("[ManageQUTResearchData]: oaicat was successfully reloaded.");
		}else{
			log.info("[ManageQUTResearchData]: Failed to reload the oaicat application. Response code: " + String.valueOf(resCode));
			throw new Exception("Failed to reload the oaicat application. Response code: " + String.valueOf(resCode));
		}
	}
	
	//this is for changing the record status automatically. 
	//CR: 051 this has to be executed to change the record status from 'Publish - QUT Access' to 'PublishQUTAccess'
	/*List<Individual> getPublishedForOpenAccessRecords(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			query.setFilterQueries(VitroSearchTermNames.PUBLISH_RECORD + ":\"Published - Open Access\""); // THis is for changing  purpose.
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			//this has a bug. results.getIndividuals() only returns 10 individuals. but results.getHitCounts() returns the correct value.
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}
	
	List<Individual> getPublishedForQUTAcessRecords(VitroRequest vreq){
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			query.setFilterQueries(VitroSearchTermNames.PUBLISH_RECORD + ":\"Published - QUT Access\"");
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
			
		}catch (Exception ex){
            log.error("getPublishedRecords: Could not make Solr query");
        }  
		
		return null;
	}*/
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
