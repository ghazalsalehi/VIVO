/*
Copyright (c) 2015, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.jena;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.IndividualDaoJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaModelUtils;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;


import qut.crmm.utils.VivoProps;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditN3Utils;

// LIBRDF-48
public class DataMigrationRIFCSLite extends JenaIngestController {
     
   private static final Log log = LogFactory.getLog(DataMigrationRIFCSLite.class.getName());

   Model literalCreationModel = null;
   Model changes = null;
   VitroRequest vreq = null;
   VivoProps vp = null;
   int count = 0;
   private long resourceKey = 0;
   String  defaultNameSpace = "" ;
   
    @Override
	public void doPost (HttpServletRequest request, HttpServletResponse response){

		if (!isAuthorizedToDisplayPage(request, response,
				SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTIONS)) {
    		return;
    	}

        vreq = new VitroRequest(request); 
		ServletContext servletContext = getServletContext();
		OntModel model = Utils.getABoxModel(vreq.getSession(), servletContext);
		
	    String contextPath = servletContext.getRealPath(File.separator);
		String crmmConfFile = contextPath + "/qut_data/conf/config.conf";

		literalCreationModel = ModelFactory.createDefaultModel();
		vp  = new VivoProps(literalCreationModel, crmmConfFile);
		
		changes = ModelFactory.createDefaultModel();
		
		defaultNameSpace = vreq.getWebappDaoFactory().getDefaultNamespace();
		try {
			resourceKey = Utils.loadResourceKey(model, defaultNameSpace, vp);
		} catch (Exception e1) {
			log.error("DATA_MIGRATION FAILED: " + e1.getMessage());
			return;
		}
		
        log.info("DATA_MIGRATION STARTED.");
        
        try{
        	process(model);
        }catch(Exception e){
			log.error("DATA_MIGRATION FAILED: " + e.getMessage());
			return;
		}
        
        try {
			saveFinalKeys(model);
		} catch (Exception e2) {
			log.error("DATA_MIGRATION FAILED: " + e2.getMessage());
			return;
		}
        
        if (changes != null){
        	model.add(changes);
		}
        
        log.info("DATA_MIGRATION COMPLETED.");
    }

    @Override
	public void doGet (HttpServletRequest request, HttpServletResponse response){
        doPost(request,response);
    }
    
    public void saveFinalKeys(OntModel model) throws Exception{
		Utils.saveFinalResourceKey(model, defaultNameSpace, resourceKey, vp);
	}
    
    public void process(OntModel model) throws Exception{
    	
    	count = 0;
    	String vclassURI_researcher = "<http://www.qut.edu.au/ontologies/vivoqut#researcher>";
    	String vclassURI_adminPos = "<http://www.qut.edu.au/ontologies/vivoqut#administrativePosition>";
    	String vclassURI_group = "<http://www.qut.edu.au/ontologies/vivoqut#group>";
    	startDataMigration(model, vclassURI_researcher, true);
    	startDataMigration(model, vclassURI_adminPos, true);
    	startDataMigration(model, vclassURI_group, false);
    	log.info("TOTAL party  RECORD PROCESSSED IS : " + count);
    	
    	count = 0;
    	String vclassURI_project = "<http://www.qut.edu.au/ontologies/vivoqut#project>";
    	startDataMigration(model, vclassURI_project, false);
    	log.info("TOTAL project  RECORD PROCESSSED IS : " + count);
    	
    	count = 0;
    	String vclassURI_repository = "<http://www.qut.edu.au/ontologies/vivoqut#repository>";
    	String vclassURI_registry = "<http://www.qut.edu.au/ontologies/vivoqut#registry>";
    	String vclassURI_catalogueOrIndex = "<http://www.qut.edu.au/ontologies/vivoqut#catalogueOrIndex>";
    	String vclassURI_collection = "<http://www.qut.edu.au/ontologies/vivoqut#collection>";
    	String vclassURI_researchDataSet = "<http://www.qut.edu.au/ontologies/vivoqut#researchDataSet>";
    	startDataMigration(model, vclassURI_repository, false);
    	startDataMigration(model, vclassURI_registry, false);
    	startDataMigration(model, vclassURI_catalogueOrIndex, false);
    	startDataMigration(model, vclassURI_collection, false);
    	startDataMigration(model, vclassURI_researchDataSet, false);
    	log.info("TOTAL collection  RECORD PROCESSSED IS : " + count);
    	
    	count = 0;
    	String vclassURI_create = "<http://www.qut.edu.au/ontologies/vivoqut#create>";
    	String vclassURI_report = "<http://www.qut.edu.au/ontologies/vivoqut#report>";
    	startDataMigration(model, vclassURI_create, false);
    	startDataMigration(model, vclassURI_report, false);
    	log.info("TOTAL service  RECORD PROCESSSED IS : " + count);
    	
    	count = 0;
    	String vclassURI_software = "<http://www.qut.edu.au/ontologies/vivoqut#software>";
    	String vclassURI_code = "<http://www.qut.edu.au/ontologies/vivoqut#code>";
    	startDataMigration(model, vclassURI_software, false);
    	startDataMigration(model, vclassURI_code, false);
    	log.info("TOTAL software and code  RECORD PROCESSSED IS : " + count);
    	
    	
    	count = 0;
    	String vclassURI_economy = "<http://www.qut.edu.au/ontologies/vivoqut#economy>";
    	String vclassURI_health = "<http://www.qut.edu.au/ontologies/vivoqut#health>";
    	String vclassURI_farming = "<http://www.qut.edu.au/ontologies/vivoqut#farming>";
    	String vclassURI_biota = "<http://www.qut.edu.au/ontologies/vivoqut#biota>";
    	String vclassURI_geoscientificInformation = "<http://www.qut.edu.au/ontologies/vivoqut#geoscientificInformation>";
    	String vclassURI_environment = "<http://www.qut.edu.au/ontologies/vivoqut#environment>";
    	String vclassURI_climate = "<http://www.qut.edu.au/ontologies/vivoqut#climate>";
    	String vclassURI_energyandUtilities = "<http://www.qut.edu.au/ontologies/vivoqut#energyandUtilities>";
    	String vclassURI_intelligenceAndMilitary = "<http://www.qut.edu.au/ontologies/vivoqut#intelligenceAndMilitary>";
    	String vclassURI_structure = "<http://www.qut.edu.au/ontologies/vivoqut#structure>";
    	String vclassURI_transportation = "<http://www.qut.edu.au/ontologies/vivoqut#transportation>";
    	String vclassURI_boundaries = "<http://www.qut.edu.au/ontologies/vivoqut#boundaries>";
    	String vclassURI_location = "<http://www.qut.edu.au/ontologies/vivoqut#location>";
    	String vclassURI_planningCadastres = "<http://www.qut.edu.au/ontologies/vivoqut#planningCadastre>";
    	String vclassURI_societyandCulture = "<http://www.qut.edu.au/ontologies/vivoqut#societyandCulture>";
    	String vclassURI_imageryBaseMapsEarthCover = "<http://www.qut.edu.au/ontologies/vivoqut#imageryBaseMapsEarthCover>";
    	String vclassURI_elevation = "<http://www.qut.edu.au/ontologies/vivoqut#elevation>";
    	String vclassURI_oceans = "<http://www.qut.edu.au/ontologies/vivoqut#oceans>";
    	String vclassURI_inlandWaters = "<http://www.qut.edu.au/ontologies/vivoqut#inlandWaters>";
    	
    	startDataMigration(model, vclassURI_economy, false);
    	startDataMigration(model, vclassURI_health, false);
    	startDataMigration(model, vclassURI_farming, false);
    	startDataMigration(model, vclassURI_biota, false);
    	startDataMigration(model, vclassURI_geoscientificInformation, false);
    	startDataMigration(model, vclassURI_environment, false);
    	startDataMigration(model, vclassURI_climate, false);
    	startDataMigration(model, vclassURI_energyandUtilities, false);
    	startDataMigration(model, vclassURI_intelligenceAndMilitary, false);
    	startDataMigration(model, vclassURI_structure, false);
    	startDataMigration(model, vclassURI_transportation, false);
    	startDataMigration(model, vclassURI_boundaries, false);
    	startDataMigration(model, vclassURI_location, false);
    	startDataMigration(model, vclassURI_planningCadastres, false);
    	startDataMigration(model, vclassURI_societyandCulture, false);
    	startDataMigration(model, vclassURI_imageryBaseMapsEarthCover, false);
    	startDataMigration(model, vclassURI_elevation, false);
    	startDataMigration(model, vclassURI_oceans, false);
    	startDataMigration(model, vclassURI_inlandWaters, false);
    	
    	log.info("TOTAL spatial data  RECORD PROCESSSED IS : " + count);
    	
    	
    }
    
    public void startDataMigration(OntModel model, String vclassURI, boolean isPartyRecord) throws Exception{
    	String queryString = "SELECT ?obj WHERE { \n" +
    			"?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " + vclassURI + " }";
    	
    	Query sparqlQuery = QueryFactory.create( queryString, Syntax.syntaxARQ);
		QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, model);
		
    	try {
    		ResultSet results = qExec.execSelect();

    		for ( ; results.hasNext() ; ){
    			QuerySolution soln = results.nextSolution();
    			Resource r = soln.getResource("obj");  // Get a result variable -must be a resource
    			
    			String personTitle = "";
    	    	String firstname = "";
    	    	String surname = "";
    	    	String email = "";
    	    	String url = "";
    	    	String phone = "";
    	    	String fax = "";
    	    	
    	    	
    			if (isPartyRecord){
    				personTitle = getFreeTextBoxValue(r, vp.props.get("supressTitle"), vp.props.get("freeTextValue1"));
    				firstname = getFreeTextBoxValue(r, vp.props.get("supressFirstName"), vp.props.get("freeTextValue1"));
    				surname = getFreeTextBoxValue(r, vp.props.get("supressSurname"), vp.props.get("freeTextValue1"));
    				url = getFreeTextBoxValue(r, vp.props.get("academicProfile"), vp.props.get("freeTextValue1"));
    			}else{
    				firstname = getFreeTextBoxValue(r, vp.props.get("primaryContact"), vp.props.get("freeTextValue1"));
    				url = getFreeTextBoxValue(r, vp.props.get("website"), vp.props.get("freeTextValue1"));
    			}
    			
    			email = getFreeTextBoxValue(r, vp.props.get("emailAddress"), vp.props.get("freeTextValue1"));
    			phone = getFreeTextBoxValue(r, vp.props.get("phone"), vp.props.get("freeTextValue1"));
    			fax = getFreeTextBoxValue(r, vp.props.get("fax"), vp.props.get("freeTextValue1"));	
    			
    			try{
    				
    				if (personTitle.equals("") && firstname.equals("") && surname.equals("") && email.equals("") &&
    		    			url.equals("") && phone.equals("") && fax.equals("")){
    		    	}else{
    		    		updateContactInfo(model, r, vp.props.get("contactInfo"), personTitle, firstname, surname, email, url, phone, fax);
        				
        				if (isPartyRecord){
        					deleteAllPropertyValues(r, vp.props.get("supressTitle"));
        					deleteAllPropertyValues(r, vp.props.get("supressFirstName"));
        					deleteAllPropertyValues(r, vp.props.get("supressSurname"));
        					deleteAllPropertyValues(r, vp.props.get("academicProfile"));
        				}else{
        					deleteAllPropertyValues(r, vp.props.get("primaryContact"));
        					deleteAllPropertyValues(r, vp.props.get("website"));
        				}
        				
        				deleteAllPropertyValues(r, vp.props.get("emailAddress"));
        				deleteAllPropertyValues(r, vp.props.get("phone"));
        				deleteAllPropertyValues(r, vp.props.get("fax"));
        				
        				updateDateRecordModified(r, vp.props.get("dateRecordModified"));
    		    	}
    				
    			}catch(Exception e){
    				log.error("DATA_MIGRATION FAILED FOR: " + r.getURI() +" [ " + e.getMessage() + " ]");
    				throw e;
    			}

    			count++;
    		}
    		
    		log.info("DATA_MIGRATION finished for : " + vclassURI);
    	}catch(Exception e){
			   qExec.close();
			   throw e;
		}finally {
		   qExec.close();
		}
	}
    
    public Literal createLiteral(String value){
	    String v = EditN3Utils.stripInvalidXMLChars(value);
	    return literalCreationModel.createTypedLiteral(v, "http://www.w3.org/2001/XMLSchema#string");
	}
    
    public String getNewURI(String prefix, OntModel model) throws Exception{
    	String uri = "";
    	resourceKey += 1;
    	uri = defaultNameSpace + prefix + String.valueOf(resourceKey);
    	
    	return uri;
    }
    
    public String getFreeTextBoxValue(Resource r, Property pro, Property subPro){
    	Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null) {
			return "";
		}
    	
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			return "";
		}
		
		Statement value = robj.getProperty(subPro);	
		if (value == null){
			return "";	
		}else{
			return value.getString();
		}
    }
    
    public void updateContactInfo(OntModel model, Resource r, Property pro, String personTitle, String firstname, String surname, 
    		String email, String url, String phone, String fax) throws Exception{
    	
    	Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null) {
			addContactInfo(model, r, pro, personTitle, firstname, surname, email, url, phone, fax);
			return;
		}
		
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			addContactInfo(model, r, pro, personTitle, firstname, surname, email, url, phone, fax);
			return;
		}
	
		if (! personTitle.equals("")){
			Statement value1 = robj.getProperty(vp.props.get("freeTextValue1"));	
			if ((value1 == null) || (! value1.getString().equals(personTitle))){
				robj.removeAll(vp.props.get("freeTextValue1"));
				Literal litrl1 = createLiteral(personTitle);
				robj.addProperty(vp.props.get("freeTextValue1"), litrl1);			
			}
		}
		
		if (! firstname.equals("")){
			Statement value2 = robj.getProperty(vp.props.get("freeTextValue2"));	
			if ((value2 == null) || (! value2.getString().equals(firstname))){
				robj.removeAll(vp.props.get("freeTextValue2"));
				Literal litrl2 = createLiteral(firstname);
				robj.addProperty(vp.props.get("freeTextValue2"), litrl2);			
			}
		}
		
		if (! surname.equals("")){
			Statement value3 = robj.getProperty(vp.props.get("freeTextValue3"));	
			if ((value3 == null) || (! value3.getString().equals(surname))){
				robj.removeAll(vp.props.get("freeTextValue3"));
				Literal litrl3 = createLiteral(surname);
				robj.addProperty(vp.props.get("freeTextValue3"), litrl3);			
			}
		}
		
		if (! email.equals("")){
			Statement value4 = robj.getProperty(vp.props.get("freeTextValue4"));	
			if ((value4 == null) || (! value4.getString().equals(email))){
				robj.removeAll(vp.props.get("freeTextValue4"));
				Literal litrl4 = createLiteral(email);
				robj.addProperty(vp.props.get("freeTextValue4"), litrl4);			
			}
		}
		
		if (! url.equals("")){
			Statement value5 = robj.getProperty(vp.props.get("freeTextValue5"));	
			if ((value5 == null) || (! value5.getString().equals(url))){
				robj.removeAll(vp.props.get("freeTextValue5"));
				Literal litrl5 = createLiteral(url);
				robj.addProperty(vp.props.get("freeTextValue5"), litrl5);			
			}
		}
		
		if (! phone.equals("")){
			Statement value6 = robj.getProperty(vp.props.get("freeTextValue6"));	
			if ((value6 == null) || (! value6.getString().equals(phone))){
				robj.removeAll(vp.props.get("freeTextValue6"));
				Literal litrl6 = createLiteral(phone);
				robj.addProperty(vp.props.get("freeTextValue6"), litrl6);			
			}
		}
		
		if (! fax.equals("")){
			Statement value7 = robj.getProperty(vp.props.get("freeTextValue7"));	
			if ((value7 == null) || (! value7.getString().equals(fax))){
				robj.removeAll(vp.props.get("freeTextValue7"));
				Literal litrl7 = createLiteral(fax);
				robj.addProperty(vp.props.get("freeTextValue7"), litrl7);			
			}
		}
    }
    
    public void addContactInfo(OntModel model, Resource r, Property pro, String personTitle, String firstname, String surname, 
    		String email, String url, String phone, String fax) throws Exception{
    	
    	String uri = getNewURI("dm", model);	// data migration - dm
    	Resource contactInfo = changes.createResource(uri);
    	
    	contactInfo.addProperty(vp.props.get("type"), vp.classes.get("thing"));
    	contactInfo.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
    	
    	if (! personTitle.equals("")){
    		Literal litrlTitle = createLiteral(personTitle);
        	contactInfo.addProperty(vp.props.get("freeTextValue1"), litrlTitle);
    	}
    	if (! firstname.equals("")){
    		Literal litrlFirstname = createLiteral(firstname);
        	contactInfo.addProperty(vp.props.get("freeTextValue2"), litrlFirstname);
    	}
    	if (! surname.equals("")){
    		Literal litrlSurname = createLiteral(surname);
        	contactInfo.addProperty(vp.props.get("freeTextValue3"), litrlSurname);
    	}
    	if (! email.equals("")){
    		Literal litrlEmail = createLiteral(email);
        	contactInfo.addProperty(vp.props.get("freeTextValue4"), litrlEmail);
    	}
    	if (! url.equals("")){
    		Literal litrlURL = createLiteral(url);
        	contactInfo.addProperty(vp.props.get("freeTextValue5"), litrlURL);
    	}
    	if (! phone.equals("")){
    		Literal litrlPhone = createLiteral(phone);
        	contactInfo.addProperty(vp.props.get("freeTextValue6"), litrlPhone);
    	}
    	if (! fax.equals("")){
    		Literal litrlFax = createLiteral(fax);
        	contactInfo.addProperty(vp.props.get("freeTextValue7"), litrlFax);
    	}
    
    	r.addProperty(pro, contactInfo);	
    }
    
    public void deleteAllPropertyValues(Resource r, Property pro) throws Exception{
    	
    	WebappDaoFactoryJena wdf = new WebappDaoFactoryJena(vreq.getOntModelSelector());
		if (wdf == null){throw new Exception("WebappDaoFactoryJena is null");}
		
		IndividualDaoJena ida = (IndividualDaoJena)wdf.getIndividualDao();
		if (ida == null){throw new Exception("IndividualDaoJena is null");}
		
    	Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null) {
			return;
		}
    	
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			return;
		}

		robj.removeAll(vp.props.get("infoType1"));
		robj.removeAll(vp.props.get("infoType2"));
		robj.removeAll(vp.props.get("freeTextValue1"));
		
		String uri = robj.getURI();
		if (ida.deleteIndividual(uri) != 0){throw new Exception("Failed to delete this individual " + uri );}
    }
    
    public void updateDateRecordModified(Resource r, Property pro) throws Exception{
    	
		Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null){
			//addDateRecordModified(r, pro);
			return;
		}
		
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			//addDateRecordModified(r, pro);
			return;
		}
		
		robj.removeAll(vp.props.get("freeTextValue1"));
		     
		DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
		
		Literal litrl = createLiteral(formattedDateStr);
		robj.addProperty(vp.props.get("freeTextValue1"), litrl);
	}
	
}


































