package edu.cornell.mannlib.vitro.webapp.controller;


import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.controller.jena.APPartyRecord;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditN3Utils;

import qut.crmm.model.FORCode;
import qut.crmm.model.Funding;
import qut.crmm.model.People;
import qut.crmm.model.Project;
import qut.crmm.model.Publication;
import qut.crmm.utils.VivoProps;

import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qut.crmm.model.transform.spec.*;


public class RMCSVReports2Jena {
	
	private static final Log log = LogFactory.getLog(RMCSVReports2Jena.class.getName());
	
	private long updatedRecordsCount;
	private long newRecodsCount;
	private VivoProps vp;
	private long localKey;
	private long resourceKey;
	private RMCSV2Java oRMCSV2Java;
	private String RMdataFolder;
	private OntModel aBoxModel;
	private String  configFile;
	private String  defaultNameSpace;
	private Model changes;
	
	private static Model literalCreationModel;
	private DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	   
    static{
        literalCreationModel = ModelFactory.createDefaultModel();
    }
	
	
	public RMCSVReports2Jena( String defaultNameSpace, OntModel aBoxModel, String crmmConfFile){
		localKey = 0;
		updatedRecordsCount = 0;
		newRecodsCount = 0;
		this.defaultNameSpace = defaultNameSpace;
		this.configFile = crmmConfFile;
		this.aBoxModel = aBoxModel;
		this.RMdataFolder = "";
	}
	
	public void init() throws Exception{
		changes = ModelFactory.createDefaultModel();
		vp = new VivoProps(changes, configFile);
		localKey = Utils.loadLocalKey(aBoxModel, defaultNameSpace, vp);
		resourceKey = Utils.loadResourceKey(aBoxModel, defaultNameSpace, vp);
	}
	
	public void saveFinalKeys() throws Exception{
		Utils.saveFinalLocalKey(aBoxModel, defaultNameSpace, localKey, vp);
		Utils.saveFinalResourceKey(aBoxModel, defaultNameSpace, resourceKey, vp);
	}
	
	public void process(String RMdataFolder) throws Exception {
		this.RMdataFolder = RMdataFolder;
		
		try{
			oRMCSV2Java = new RMCSV2Java(vp, RMdataFolder, localKey);
			oRMCSV2Java.execute();
			
			// get last incremented keys
			localKey = oRMCSV2Java.getLocalKey();	
		}catch (Exception e) {
			throw e;
		}
		
		try{
			log.info("RESEARCH_MASTER Converting .csv file to Jena.");
			ConvertRMCSV2Jena();
			log.info("RESEARCH_MASTER Finish converting into jena model.");
			//emit("D:\\testData\\RM_1.rdf", changes);
		}catch (Exception e) {
			throw e;
		}
	}
	
	public Model getChanges() {
		return changes;
	}

	public void setChanges(Model changes) {
		this.changes = changes;
	}

	public void emit(String fileName, Model changes) {
		try {
			PrintStream p= new PrintStream(fileName);
			changes.write(p);
			p.close();
		}
		catch (FileNotFoundException e) { System.out.println(e); }
	}
	
	public void addResearchAreas(Project oProj, Resource r) throws Exception{
		
		String keyWrods = (oProj.getProjectKeywords()).trim();
		if (! keyWrods.equals("")){
			
			String uri = getNewURI("rm");
			Resource keyWordsResource = changes.createResource(uri);
			
			keyWordsResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
			keyWordsResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
			Literal litrl1 = createLiteral("local");
			keyWordsResource.addProperty(vp.props.get("infoType1"), litrl1);
			//keyWordsResource.addProperty(vp.props.infoType2, "---"); one proj can have several for codes.
			Literal litrl2 = createLiteral(keyWrods);
			keyWordsResource.addProperty(vp.props.get("freeTextValue1"), litrl2);
			
			r.addProperty(vp.props.get("researchAreas"), keyWordsResource);
		}
		
		List<FORCode> lstFORCodes = oProj.getLstFORCode();
		Iterator itr = lstFORCodes.iterator();
		while(itr.hasNext()) {
		   FORCode oFORCOde = (FORCode) itr.next();
		   
		   String uri = getNewURI("rm");
		   Resource FORCodeResource = changes.createResource(uri);
		   FORCodeResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		   FORCodeResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		   Literal litrl3 = createLiteral("anzsrc-for");
		   FORCodeResource.addProperty(vp.props.get("infoType1"), litrl3);
		   
		   String forCodeName = (oFORCOde.getFORCode()).trim() + " " + (oFORCOde.getFORName()).trim();
		   Literal litrl4 = createLiteral(forCodeName);
		   FORCodeResource.addProperty(vp.props.get("infoType2"), litrl4);
		   
		   r.addProperty(vp.props.get("researchAreas"), FORCodeResource);
		}
	}
	
	public void addQUTEPrints(Project oProj, Resource r) throws Exception{	// publications
		
		List<Publication> lstPublication = oProj.getLstPublication();
		Iterator itr = lstPublication.iterator();
		while(itr.hasNext()) {
			Publication oPublication = (Publication)itr.next();
			
			String ePrintsURL = oPublication.getePrintsUrl();
			
			if (ePrintsURL.equals("")){continue;}
			
			String uri = getNewURI("rm");
			Resource pubclicationRes = changes.createResource(uri);
			pubclicationRes.addProperty(vp.props.get("type"), vp.classes.get("thing"));
			pubclicationRes.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
			
			Literal litrl1 = createLiteral("website");
			pubclicationRes.addProperty(vp.props.get("infoType1"), litrl1);
			Literal litrl2 = createLiteral("url");
			pubclicationRes.addProperty(vp.props.get("infoType2"), litrl2);
			Literal litrl3 = createLiteral(ePrintsURL);
			pubclicationRes.addProperty(vp.props.get("freeTextValue1"), litrl3);
			
			r.addProperty(vp.props.get("qutEPrints"), pubclicationRes);
		}
	}
	
	public void addProjectExistanceDates(Project oProj, Resource r)throws Exception{
		
		String uri = getNewURI("rm");
		Resource existanceDatesResource = changes.createResource(uri);
		
		existanceDatesResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		existanceDatesResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		Literal litrl1 = createLiteral(oProj.getStartDate());
		existanceDatesResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		Literal litrl2 = createLiteral("W3CDTF");
		existanceDatesResource.addProperty(vp.props.get("infoType1"), litrl2);
		Literal litrl3 = createLiteral(oProj.getEndDate());
		existanceDatesResource.addProperty(vp.props.get("freeTextValue2"), litrl3);
		Literal litrl4 = createLiteral("W3CDTF");
		existanceDatesResource.addProperty(vp.props.get("infoType2"), litrl4);
		
		r.addProperty(vp.props.get("existenceDates"), existanceDatesResource);
	}
	
	public void AddProjectInternalOrExternalFunding(Funding oFun, Resource r) throws Exception{
		
		String uri = getNewURI("rm");
		Resource fundingResource = changes.createResource(uri);
		
		fundingResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		fundingResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		if (oFun.getExt() == "External"){
			Literal litrl1 = createLiteral("External");
			fundingResource.addProperty(vp.props.get("infoType1"), litrl1);
		}else{
			Literal litrl2 = createLiteral("Internal");
			fundingResource.addProperty(vp.props.get("infoType1"), litrl2);
		}
		
		r.addProperty(vp.props.get("internalorExternalFunding"), fundingResource);
	}
	
	public void AddFundingScheme(Funding oFun, Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource fundingSchemeResource = changes.createResource(uri);
		
		fundingSchemeResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		fundingSchemeResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral(oFun.getScheme());
		fundingSchemeResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("fundingScheme"), fundingSchemeResource);
	}
	
	public void AddGrantor(Funding oFun, Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource grantorResource = changes.createResource(uri);
		
		grantorResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		grantorResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral(oFun.getGrantor());
		grantorResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("grantor"), grantorResource);
	}
	
	public void addProjectDescription(Project oProj, Resource r) throws Exception{
		
		String uri = getNewURI("rm");
		Resource desResource = changes.createResource(uri);
		
		desResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		desResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral("full");
		desResource.addProperty(vp.props.get("infoType1"), litrl1);
		Literal litrl2 = createLiteral(oProj.getProjectDescription());
		desResource.addProperty(vp.props.get("freeTextValue1"), litrl2);
		
		r.addProperty(vp.props.get("biography"), desResource);
	}	

	public void addIsApprovedByOoR(Resource r) throws Exception{
		
		String uri = getNewURI("rm");
		Resource OoRResource = changes.createResource(uri);
		
		OoRResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		OoRResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		Literal litrl1 = createLiteral("No");
		OoRResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("isApprovedByOoR"), OoRResource);
	}
	
	public void addIsApprovedBylibrary(Resource r) throws Exception{
		
		String uri = getNewURI("rm");
		Resource LibraryResource = changes.createResource(uri);
		
		LibraryResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		LibraryResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		Literal litrl1 = createLiteral("No");
		LibraryResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("isApprovedbyLibrary"), LibraryResource);
	}
	
	public void addIsPublishRecord(Resource r) throws Exception{

		String uri = getNewURI("rm");
		Resource publishResource = changes.createResource(uri);
		
		publishResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		publishResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		Literal litrl1 = createLiteral(Utils.RECORD_STATUS_DRAFT_VALUE);
		publishResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("publishRecord"), publishResource);
	}
	
	public void addLocalKeyIdentifier(Resource r, String value) throws Exception{
		
		String uri = getNewURI("rm");
		Resource idenResource = changes.createResource(uri);
		
		idenResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		idenResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral("local");
		idenResource.addProperty(vp.props.get("infoType1"), litrl1);
		Literal litrl2 = createLiteral(value);
		idenResource.addProperty(vp.props.get("freeTextValue1"), litrl2);
		
		r.addProperty(vp.props.get("localKey"), idenResource);
	}
	
	public void AddRMProjectCode(Project oProj, Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource projCodeResource = changes.createResource(uri);
		
		projCodeResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		projCodeResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		String projectCode = String.valueOf(oProj.getProjectCode());
		Literal litrl1 = createLiteral(projectCode);
		projCodeResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("RMProjectCode"), projCodeResource);
	}
	
	public void AddRMProjectStatus(Project oProj, Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource projStatusResource = changes.createResource(uri);
		
		projStatusResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		projStatusResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral(oProj.getProjectStatus());
		projStatusResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("projectStatus"), projStatusResource);
	}
	
	public void AddRAOUName(Project oProj, Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource AOUNameResource = changes.createResource(uri);
		
		AOUNameResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		AOUNameResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral(oProj.getAOUName());
		AOUNameResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("AOUName"), AOUNameResource);
	}
	
	public void AddDateRecordCreated(Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource dateRecordCreatedResource = changes.createResource(uri);
		
		dateRecordCreatedResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		dateRecordCreatedResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
		Literal litrl1 = createLiteral(formattedDateStr);
		dateRecordCreatedResource.addProperty(vp.props.get("freeTextValue1"), litrl1);
		
		r.addProperty(vp.props.get("dateRecordCreated"), dateRecordCreatedResource);
	}
	
	public void addDateRecordModified(Resource r) throws Exception{
		String uri = getNewURI("rm");
		Resource dateRecordModifiedResource = changes.createResource(uri);
		
		dateRecordModifiedResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		dateRecordModifiedResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
		Literal litrl = createLiteral(formattedDateStr);
		dateRecordModifiedResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		r.addProperty(vp.props.get("dateRecordModified"), dateRecordModifiedResource);
	}
	
	public void addRecordCreatedBy(Resource r) throws Exception{
    	String uri = getNewURI("rm");
		Resource recordCreatedByResource = changes.createResource(uri);
		
		recordCreatedByResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		recordCreatedByResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl = createLiteral("Research Master");
		recordCreatedByResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		Literal litr2 = createLiteral("research_master");	// userID
		recordCreatedByResource.addProperty(vp.props.get("freeTextValue2"), litr2);

		r.addProperty(vp.props.get("recordInitiallyCreatedBy"), recordCreatedByResource);
    }
	
	
	public void SetHasAssociationWithRelationToPeople(Project oProj, Resource rFrom) throws Exception{
		String vclassURI = "<http://www.qut.edu.au/ontologies/vivoqut#researcher>";
		String vclassObj = "<http://www.qut.edu.au/ontologies/vivoqut#RMPersonCode>";
		
		List<People> lstPeople = oProj.getLstPeople();
		Iterator itr = lstPeople.iterator();
		while(itr.hasNext()) {
		   People oPeople = (People) itr.next();
		   
		   if (oPeople.getType().equals("External")){
			   continue;	// we only take Internal people only.
		   }
		   
		   String personUserName = oPeople.getNetworkLoginOrUsername();
		   personUserName = personUserName.toLowerCase();	// RM data report contain CAPITAL user names.
		   if (! personUserName.isEmpty()){
			   String value =  "\"" + personUserName + "\"^^<http://www.w3.org/2001/XMLSchema#string>";
			   
			   String queryString = "SELECT ?obj WHERE { \n" +
			   "?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "+ vclassURI + " . \n" +
			   "?obj <http://www.qut.edu.au/ontologies/vivoqut#RMPersonCode> ?RMPersonCode . \n" +
			   "?RMPersonCode <http://www.qut.edu.au/ontologies/vivoqut#freeTextValue1> " + value + " }";
			   
			   Query sparqlQuery = QueryFactory.create( queryString, Syntax.syntaxARQ);
			   QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, aBoxModel);
			   
			   try {
				   ResultSet results = qExec.execSelect();
				   		
				   for ( ; results.hasNext() ; ){  // we assume 'username' is unique.
					   QuerySolution soln= results.nextSolution();
					   Resource rTo = soln.getResource("obj");
					   
					   Statement s = changes.createStatement(rFrom, vp.relations.get("hasAssociationWith"), rTo);
					   changes.add(s);
				   }

			   }catch (Exception e) {
				   qExec.close();
				   throw new Exception("Failed to excecute and create relations to people.");
			   }finally {
				   qExec.close() ;
			   }
		   }
		}
	}
	
	public long getUpdatedRecordsCount() {
		return updatedRecordsCount;
	}

	public void setUpdatedRecordsCount(long updatedRecordsCount) {
		this.updatedRecordsCount = updatedRecordsCount;
	}

	public long getNewRecodsCount() {
		return newRecodsCount;
	}

	public void setNewRecodsCount(long newRecodsCount) {
		this.newRecodsCount = newRecodsCount;
	}

	public String getNewURI(String prefix){
    	String uri = "";
    	resourceKey += 1;
    	uri = defaultNameSpace + prefix + String.valueOf(resourceKey);
    	
    	return uri;
    }
	
	public Literal createLiteral(String value){
	    String v = EditN3Utils.stripInvalidXMLChars(value);
	    return literalCreationModel.createTypedLiteral(v, "http://www.w3.org/2001/XMLSchema#string");
	}
	
	public void ConvertRMCSV2Jena() throws Exception {
		Map<Long, Project> mapProjects = oRMCSV2Java.getAllProjects();
		
		String vclassURI = "<http://www.qut.edu.au/ontologies/vivoqut#project>";
		String vclassObj = "<http://www.qut.edu.au/ontologies/vivoqut#RMProjectCode>";
		
		Iterator iterator= mapProjects.entrySet().iterator();
		while(iterator.hasNext()){
			
			Map.Entry mapEntry = (Map.Entry)iterator.next();
			Project oProj = (Project) mapEntry.getValue();
			
			long projCode = oProj.getProjectCode();
			String sProjectCode = String.valueOf(projCode);
			
			if (! sProjectCode.equals("")){
				
				String value =  "\"" + sProjectCode + "\"^^<http://www.w3.org/2001/XMLSchema#string>";
				   
				String queryString = "SELECT ?obj WHERE { \n" +
				   "?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "+ vclassURI + " . \n" +
				   "?obj <http://www.qut.edu.au/ontologies/vivoqut#RMProjectCode> ?RMProjectCode . \n" +
				   "?RMProjectCode <http://www.qut.edu.au/ontologies/vivoqut#freeTextValue1> " + value + " }";
				
				Query sparqlQuery = QueryFactory.create( queryString, Syntax.syntaxARQ);
				QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, aBoxModel);
				
				try{
					ResultSet results = qExec.execSelect();	// assume project code is unique.
					if (results.hasNext()){ // this is an update.
						   
						QuerySolution soln = results.nextSolution();	
						Resource r = soln.getResource("obj");
						
						//// Currently we don't get the updated records. If we wants to have update functionality this is the place we need to modify.
						
						updatedRecordsCount++;
						
					}else{  // creates a new record.
						String uri = getNewURI("rm");
						oProj.setResdefKey(uri);
						
						Resource r = changes.createResource(uri);

						r.addProperty(vp.props.get("type"), vp.classes.get("project"));
						r.addProperty(vp.props.get("label"), oProj.getProjectTitle());
						
						if (! oProj.getProjectDescription().isEmpty()){
							addProjectDescription(oProj, r);
						}
						addResearchAreas(oProj, r);
						addProjectExistanceDates(oProj, r); //Duration of research activity
						addQUTEPrints(oProj, r);
						
						List<Funding> lstFunding = oProj.getLstFunding();
						Iterator itr = lstFunding.iterator();
						while(itr.hasNext()) {
						   Funding oFun = (Funding) itr.next();
						   
						   AddProjectInternalOrExternalFunding(oFun, r);
						   if (oFun.getScheme() != ""){AddFundingScheme(oFun, r);}
						   if (oFun.getGrantor() != ""){AddGrantor(oFun, r);}
						}
						
						//addIsApprovedByOoR(r);
						//addIsApprovedBylibrary(r);
						addIsPublishRecord(r);
						addRecordCreatedBy(r);
						
						AddRMProjectCode(oProj, r);
						AddRMProjectStatus(oProj, r);
						AddRAOUName(oProj, r);
						AddDateRecordCreated(r);
						addDateRecordModified(r);
						
						addLocalKeyIdentifier(r, oProj.getLocalKey());
					
						SetHasAssociationWithRelationToPeople(oProj, r);
						
						newRecodsCount++;
					}
					
				}catch(Exception e){
				   qExec.close();
				   throw e;
				}finally {
				   qExec.close();
				}
			}
		}
	}

	public static void main(String args[]) {

	}
}