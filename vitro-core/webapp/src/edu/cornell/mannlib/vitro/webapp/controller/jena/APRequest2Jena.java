package edu.cornell.mannlib.vitro.webapp.controller.jena;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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


public class APRequest2Jena {

	private static final Log log = LogFactory.getLog(APRequest2Jena.class.getName());
	
	private long updatedRecordsCount;
	private long newRecodsCount;
	private long localKey;
	private long resourceKey;
	private String crmmConfFile;
	private String defaultNameSpace;
	OntModel aBoxModel;
	
	Model changes = null;
	VivoProps vp = null;
	
	private static Model literalCreationModel;
	private DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	   
    static{
        literalCreationModel = ModelFactory.createDefaultModel();
    }
    
	public APRequest2Jena(OntModel aBoxModel, String defaultNameSpace, String crmmConfFile){
		this.updatedRecordsCount = 0;
		this.newRecodsCount = 0;
		this.localKey = 0;
		this.resourceKey = 0;	// here we start as "ap"
		this.crmmConfFile = crmmConfFile;
		this.defaultNameSpace = defaultNameSpace;
		this.aBoxModel = aBoxModel;
	}
	
	public void init() throws Exception{
		changes = ModelFactory.createDefaultModel();
		vp = new VivoProps(literalCreationModel, crmmConfFile);
		localKey = Utils.loadLocalKey(aBoxModel, defaultNameSpace, vp);
		resourceKey = Utils.loadResourceKey(aBoxModel, defaultNameSpace, vp);
	} 
	
	public void saveFinalKeys() throws Exception{
		Utils.saveFinalLocalKey(aBoxModel, defaultNameSpace, localKey, vp);
		Utils.saveFinalResourceKey(aBoxModel, defaultNameSpace, resourceKey, vp);
	}
	
	public void processRequest(Document doc) throws Exception{
		
		try{
			NodeList nResultNodesList = doc.getElementsByTagName("result");
			
			List<APPartyRecord> recordlist = new ArrayList<APPartyRecord>();
			
			for (int i = 0; i < nResultNodesList.getLength(); i++){
				Node nResultNode = nResultNodesList.item(i);
				if ( (nResultNode.getNodeType() == Node.ELEMENT_NODE) ){
					APPartyRecord record = new APPartyRecord();
					
					Element eElement = (Element) nResultNode;
					
					record.setAcadamicProfLink(eElement.getElementsByTagName("url").item(0).getTextContent().trim());
					record.setBiography(eElement.getElementsByTagName("description").item(0).getTextContent().trim());
					record.setKeywords(eElement.getElementsByTagName("keywords").item(0).getTextContent().trim());
					record.setEmail(eElement.getElementsByTagName("email").item(0).getTextContent().trim());
					record.setPhone(eElement.getElementsByTagName("phone").item(0).getTextContent().trim());
					record.setFax(eElement.getElementsByTagName("fax").item(0).getTextContent().trim());
					record.setPosition(eElement.getElementsByTagName("position").item(0).getTextContent().trim());
					record.setDivfac(eElement.getElementsByTagName("divfac").item(0).getTextContent().trim());
					record.setSchool(eElement.getElementsByTagName("school").item(0).getTextContent().trim());
					record.setPersonalTitle(eElement.getElementsByTagName("personaltitle").item(0).getTextContent().trim());
					record.setGivenName(eElement.getElementsByTagName("givenname").item(0).getTextContent().trim());
					record.setSurname(eElement.getElementsByTagName("surname").item(0).getTextContent().trim());
					record.setePrintsLink(eElement.getElementsByTagName("publications").item(0).getTextContent().trim());
					record.setUserName(eElement.getElementsByTagName("username").item(0).getTextContent().trim());
					record.setDiscipline(eElement.getElementsByTagName("discipline").item(0).getTextContent().trim());
					record.setMembership(eElement.getElementsByTagName("membership").item(0).getTextContent().trim());
					
					recordlist.add(record);
				}
			}
			
			log.info("Processing started for downloaded data.");
			executeDownload(recordlist);
			log.info("Processing finished for download data.");
            
		}catch(Exception e){
			throw e;
		}
	}
	
	public void executeDownload(List<APPartyRecord> recordlist) throws Exception{
		
		String vclassURI = "<http://www.qut.edu.au/ontologies/vivoqut#researcher>";
		String vclassObj = "<http://www.qut.edu.au/ontologies/vivoqut#RMPersonCode>";

		Iterator itr = recordlist.iterator();
		while(itr.hasNext()) {
			APPartyRecord record = (APPartyRecord) itr.next();
			
			String personUserName = record.getUserName();
			if (! personUserName.isEmpty()){
				
				/*if (personUserName.equals("bean") || personUserName.equals("barnetta") || personUserName.equals("gravesn") || personUserName.equals("millere2") ||
						personUserName.equals("buys") || personUserName.equals("page5") || personUserName.equals("page4") || personUserName.equals("sillence") ||
						personUserName.equals("mcgreej")  || personUserName.equals("geva")  || personUserName.equals("drennanj") || personUserName.equals("fitzgeam")){
					
					continue;
				}	*/// skip gold standard records.
				
				String value =  "\"" + personUserName + "\"^^<http://www.w3.org/2001/XMLSchema#string>";
				   
				String queryString = "SELECT ?obj WHERE { \n" +
				   "?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "+ vclassURI + " . \n" +
				   "?obj <http://www.qut.edu.au/ontologies/vivoqut#RMPersonCode> ?RMPersonCode . \n" +
				   "?RMPersonCode <http://www.qut.edu.au/ontologies/vivoqut#freeTextValue1> " + value + " }";
				   
				Query sparqlQuery = QueryFactory.create( queryString, Syntax.syntaxARQ);
				QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, aBoxModel);
				   
				try{
					ResultSet results = qExec.execSelect();	// assume username is unique.
					if (results.hasNext()){ // this is an update.
						   
						QuerySolution soln= results.nextSolution();
						Resource r = soln.getResource("obj");
						
						updateContactInfo(r, vp.props.get("contactInfo"), record);
						updatePosition(r, vp.props.get("position"), record);	//assume we get only one position.
						
						/*if (IsDraftRecord(r)){
							updateDescription(r, vp.props.get("biography"), record);
						}*/
						
						updatePublications(r, vp.props.get("qutEPrints"), record.getePrintsLink());
						updateKeywordsAndDescipline(r, record);
						updateDateRecordModified(r, vp.props.get("dateRecordModified"));
						
						updatedRecordsCount++;
					}else{  // creates a new record.
						  
						 localKey += 1;
						 String localKeyStr = "10378.3/8085/1018." + localKey;
						 record.setLocalKeyStr(localKeyStr);
						 
						 String uri = getNewURI("ap");
						 Resource r = changes.createResource(uri);
						  
						 r.addProperty(vp.props.get("type"), vp.classes.get("researcher"));
						   
						 String label = record.getGivenName() + " " + record.getSurname();
						 Literal litrl = createLiteral(label);
						 r.addProperty(vp.props.get("label"), litrl);
						   
						 addQuestionTypeProperty(r, vp.props.get("publishRecord"), Utils.RECORD_STATUS_DRAFT_VALUE);
						 addLocalKeyIdentifier(r, record.getLocalKeyStr());
						 addDateRecordCreated(r);
						 addDateRecordModified(r, vp.props.get("dateRecordModified"));
						 addUserName(r, record.getUserName());
						 addRecordCreatedBy(r);
						   
						 // other fields come from AP. 
						 addContactInfo(r, vp.props.get("contactInfo"), record);

						 String position = record.getPosition();
						 String divfac = record.getDivfac();
						 String school = record.getSchool();
						 if (! position.equals("") || ! divfac.equals("") || ! school.equals("")){
							 addPosition(r, vp.props.get("position"), record);
						 }

						 //String membership = record.getMembership();
						 //if (! membership.equals(""))
						 	//addMembership(r, vp.props.get("currentRelInfoMemberOf"), membership);
						 String keywords = record.getKeywords();
						 if (! keywords.equals(""))
							 addKeywrods(r, vp.props.get("researchAreas"), keywords, record);
						 String descipline = record.getDiscipline();
						 if (! descipline.equals(""))
						 	addDiscipline(r, vp.props.get("researchAreas"), descipline);
						 String publication = record.getePrintsLink();
						 if (! publication.equals(""))
							 addPublications(r, vp.props.get("qutEPrints"), publication);
						 addBiography(r, vp.props.get("biography"), record);
						
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
	
	public boolean IsDraftRecord(Resource r) throws Exception {
		Statement stmPublishRecord = r.getProperty(vp.props.get("publishRecord"));
		if (stmPublishRecord == null) return false;
		
		Resource rPubRecord = (stmPublishRecord.getObject()).asResource();
		if (rPubRecord == null) return false;
		
		Statement pubValue = rPubRecord.getProperty(vp.props.get("freeTextValue1"));
		if (pubValue == null) return false;

		if ((pubValue.getString()).equals("Draft")) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public void updateKeywordsAndDescipline(Resource r, APPartyRecord record)throws Exception{
		
		Property pResArea = vp.props.get("researchAreas");

		Map<String, UpdateInfo> mapKeywords = new HashMap<String, UpdateInfo>();	// value, UpdateInfo
		Map<String, UpdateInfo> mapDescipline = new HashMap<String, UpdateInfo>();	// value, UpdateInfo
		
		StmtIterator iter = r.listProperties(pResArea);
		while (iter.hasNext()) {
			 Statement stmt      = iter.nextStatement();
			 Resource robj = (stmt.getObject()).asResource();
			 
			 String sInforType = "";
			 String sValue = "";
			 Statement stmtInforType = robj.getProperty(vp.props.get("infoType1"));
			 if (stmtInforType != null){
				 sInforType = stmtInforType.getString();
				 
				 if (sInforType.equals("local")) {	//keywords.
					 Statement stmtValue = robj.getProperty(vp.props.get("freeTextValue1"));
					 if (stmtValue != null){
						 sValue = stmtValue.getString();
						 
						 UpdateInfo updateInfo = new UpdateInfo(stmt, sValue, "DELETE");;
						 mapKeywords.put(sValue, updateInfo);
					 }
				 }else if (sInforType.equals("anzsrc-for")){
					 Statement stmtValue = robj.getProperty(vp.props.get("infoType2"));
					 if (stmtValue != null){
						 sValue = stmtValue.getString();	// 0605 Microbiology
						 
						 String regex = "^[0-9]+\\s";
								 
						 String[] splits = sValue.split(regex);
						 if (splits.length == 2){	//EX: [,"Microbiology"]
							 String disValue = splits[1];
							 UpdateInfo updateInfo = new UpdateInfo(stmt, disValue, "DELETE");
							 // from AP has lower case and our file has mostly upper cases. so we compare string by converting into lower case.
							 mapDescipline.put(disValue.toLowerCase(), updateInfo); 
						 }
					 }	
				 }else{
					 robj.removeProperties();
					 stmt.remove();
				 }
			 }else{	// in case undeleted statement was there.
				 robj.removeProperties();
				 stmt.remove();
			 }
		}
			
		String keywords = record.getKeywords();
		if (! keywords.equals(""))
			_updateKeywrods(r, pResArea, keywords, record, mapKeywords);
		
		String discipline = record.getDiscipline();
		if (! discipline.equals(""))
			_updateDiscipline(r, pResArea, discipline, mapDescipline);
			
		// remove Keywords resource with "DELETE" action.
		Iterator iterator = mapKeywords.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			UpdateInfo updateInfo = (UpdateInfo)mapEntry.getValue();
			if (updateInfo.getAction().equals("DELETE")){
				Statement stmt = updateInfo.getStatement();
				Resource robj = (stmt.getObject()).asResource();
				robj.removeProperties();	//get object as resource and then remove the object properties. After that remove statements.
				stmt.remove();
			}
		}
			
		// remove Discipline resource with "DELETE" action.
		Iterator iterator1 = mapDescipline.entrySet().iterator();
		while (iterator1.hasNext()) {
			Map.Entry mapEntry1 = (Map.Entry) iterator1.next();
			UpdateInfo updateInfo = (UpdateInfo)mapEntry1.getValue();
			if (updateInfo.getAction().equals("DELETE")){
				Statement stmt = updateInfo.getStatement();
				Resource robj = (stmt.getObject()).asResource();
				robj.removeProperties();
				stmt.remove();
			}
		}
	}
	
	public void updateContactInfo(Resource r, Property pro, APPartyRecord record) throws Exception{
		String personTitle = record.getPersonalTitle();
    	String firstname = record.getGivenName();
    	String surname = record.getSurname();
    	String email = record.getEmail();
    	String url = record.getAcadamicProfLink();
    	String phone = record.getPhone();
    	String fax = record.getFax();
    	
    	if (firstname.equals("")){ // Only check empty for mandatory field
    		if (r.hasProperty(pro)){r.removeAll(pro);}
    		return;
    	}
    	
    	Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null) {
			addContactInfo(r, pro, record);
			return;
		}
    	
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			addContactInfo(r, pro, record);
			return;
		}
		
		Statement value1 = robj.getProperty(vp.props.get("freeTextValue1"));	
		if ((value1 == null) || (! value1.getString().equals(personTitle))){
			robj.removeAll(vp.props.get("freeTextValue1"));
			Literal litrl1 = createLiteral(personTitle);
			robj.addProperty(vp.props.get("freeTextValue1"), litrl1);			
		}
		
		Statement value2 = robj.getProperty(vp.props.get("freeTextValue2"));	
		if ((value2 == null) || (! value2.getString().equals(firstname))){
			robj.removeAll(vp.props.get("freeTextValue2"));
			Literal litrl2 = createLiteral(firstname);
			robj.addProperty(vp.props.get("freeTextValue2"), litrl2);			
		}
		
		Statement value3 = robj.getProperty(vp.props.get("freeTextValue3"));	
		if ((value3 == null) || (! value3.getString().equals(surname))){
			robj.removeAll(vp.props.get("freeTextValue3"));
			Literal litrl3 = createLiteral(surname);
			robj.addProperty(vp.props.get("freeTextValue3"), litrl3);			
		}
		
		Statement value4 = robj.getProperty(vp.props.get("freeTextValue4"));	
		if ((value4 == null) || (! value4.getString().equals(email))){
			robj.removeAll(vp.props.get("freeTextValue4"));
			Literal litrl4 = createLiteral(email);
			robj.addProperty(vp.props.get("freeTextValue4"), litrl4);			
		}
		
		Statement value5 = robj.getProperty(vp.props.get("freeTextValue5"));	
		if ((value5 == null) || (! value5.getString().equals(url))){
			robj.removeAll(vp.props.get("freeTextValue5"));
			Literal litrl5 = createLiteral(url);
			robj.addProperty(vp.props.get("freeTextValue5"), litrl5);			
		}
		
		Statement value6 = robj.getProperty(vp.props.get("freeTextValue6"));	
		if ((value6 == null) || (! value6.getString().equals(phone))){
			robj.removeAll(vp.props.get("freeTextValue6"));
			Literal litrl6 = createLiteral(phone);
			robj.addProperty(vp.props.get("freeTextValue6"), litrl6);			
		}
		
		Statement value7 = robj.getProperty(vp.props.get("freeTextValue7"));	
		if ((value7 == null) || (! value7.getString().equals(fax))){
			robj.removeAll(vp.props.get("freeTextValue7"));
			Literal litrl7 = createLiteral(fax);
			robj.addProperty(vp.props.get("freeTextValue7"), litrl7);			
		}
	}
	
	public void updateFreetextBox(Resource r, Property pro, String newValue) throws Exception{
		if (newValue.equals("")){
			if (r.hasProperty(pro)){r.removeAll(pro);}
			return;
		}
		
		Statement stmFreetextBox = r.getProperty(pro);
		if (stmFreetextBox == null) {
			addFreeTextbox(r, pro, newValue);
			return;
		}
		
		Resource robj = (stmFreetextBox.getObject()).asResource();
		if (robj == null) {
			addFreeTextbox(r, pro, newValue);
			return;
		}

		Statement freetextBoxValueValue = robj.getProperty(vp.props.get("freeTextValue1"));
		if ((freetextBoxValueValue == null) || (! (freetextBoxValueValue.getString()).equals(newValue))){
			robj.removeAll(vp.props.get("freeTextValue1"));
			Literal litrl = createLiteral(newValue);
			robj.addProperty(vp.props.get("freeTextValue1"), litrl);
		}
	}
	
	public void updateDescription(Resource r, Property pro, APPartyRecord record) throws Exception{
		String newValue = record.getBiography();
		if (newValue.equals("")){
			if (r.hasProperty(pro)){r.removeAll(pro);}
			return;
		}	
		
		Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null) {
			addBiography(r, pro, record);
			return;
		}
		
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			addBiography(r, pro, record);
			return;
		}
		
		Statement desValue = robj.getProperty(vp.props.get("freeTextValue1"));
		if ((desValue == null) || (! desValue.getString().equals(newValue))){
			robj.removeAll(vp.props.get("freeTextValue1"));
			Literal litrl1 = createLiteral(newValue);
			robj.addProperty(vp.props.get("freeTextValue1"), litrl1);
			robj.removeAll(vp.props.get("freeTextValue2"));
			Literal litrl2 = createLiteral(record.getAcadamicProfLink());
			robj.addProperty(vp.props.get("freeTextValue2"), litrl2);
		}
	}
	
	public void updatePosition(Resource r, Property pro, APPartyRecord record) throws Exception{
		String position = record.getPosition();
		String divfac = record.getDivfac();
		String school = record.getSchool();
		
		if (position.equals("") && divfac.equals("") && school.equals("")){
			if (r.hasProperty(pro)){r.removeAll(pro);}
			return;
		}
		
		Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null) {
			addPosition(r, pro, record);
			return;
		}
		
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			addPosition(r, pro, record);
			return;
		}
		
		Statement posValue = robj.getProperty(vp.props.get("freeTextValue1"));
		Statement divfacValue = robj.getProperty(vp.props.get("freeTextValue2"));
		Statement schoolValue = robj.getProperty(vp.props.get("freeTextValue3"));
		
		if ((posValue == null) || (! posValue.getString().equals(position))){
			robj.removeAll(vp.props.get("freeTextValue1"));
			Literal litrl1 = createLiteral(position);
			robj.addProperty(vp.props.get("freeTextValue1"), litrl1);
		}
		
		if ((divfacValue == null) || (! divfacValue.getString().equals(divfac))){
			robj.removeAll(vp.props.get("freeTextValue2"));
			Literal litrl2 = createLiteral(divfac);
			robj.addProperty(vp.props.get("freeTextValue2"), litrl2);
		}
		
		if ((schoolValue == null) || (! schoolValue.getString().equals(school))){
			robj.removeAll(vp.props.get("freeTextValue3"));
			Literal litrl3 = createLiteral(school);
			robj.addProperty(vp.props.get("freeTextValue3"), litrl3);
		}
	}
	
	public void updatePublications(Resource r, Property pro, String newValue)throws Exception{
		if (newValue.equals("")){
			if (r.hasProperty(pro)){r.removeAll(pro);}
			return;
		}

		Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null){
			addPublications(r, pro, newValue);
			return;
		}
		
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			addPublications(r, pro, newValue);
			return;
		}
		
		Statement pubValue = robj.getProperty(vp.props.get("freeTextValue1"));
		if ((pubValue == null) || (! pubValue.getString().equals(newValue))){
			robj.removeAll(vp.props.get("freeTextValue1"));
			Literal litrl = createLiteral(newValue);
			robj.addProperty(vp.props.get("freeTextValue1"), litrl);
		}
	}
	
	public void updateDateRecordModified(Resource r, Property pro) throws Exception{
    	
		Statement stmnt1 = r.getProperty(pro);
		if (stmnt1 == null){
			addDateRecordModified(r, pro);
			return;
		}
		
		Resource robj = (stmnt1.getObject()).asResource();
		if (robj == null) {
			addDateRecordModified(r, pro);
			return;
		}
		
		robj.removeAll(vp.props.get("freeTextValue1"));
		     
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//Calendar cal = Calendar.getInstance();
		//Literal litrl = createLiteral(dateFormat.format(cal.getTime()));
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
		Literal litrl = createLiteral(formattedDateStr);
		robj.addProperty(vp.props.get("freeTextValue1"), litrl);
	}
	
	public void addDateRecordModified(Resource r, Property pro) throws Exception{
		String uri = getNewURI("ap");
		Resource dateRecordModifiedResource = changes.createResource(uri);
		
		// TODO: Please remove thing from this class.
		// No need to add Thing. It will automatically add.
		dateRecordModifiedResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		dateRecordModifiedResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//Calendar cal = Calendar.getInstance();
		//Literal litrl = createLiteral(dateFormat.format(cal.getTime()));
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
		Literal litrl = createLiteral(formattedDateStr);
		dateRecordModifiedResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		r.addProperty(pro, dateRecordModifiedResource);
	}
	
	public void addQuestionTypeProperty(Resource r, Property p, String value) throws Exception{

		String uri = getNewURI("ap");
		Resource quesResource = changes.createResource(uri);
		
		quesResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		quesResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl = createLiteral(value);
		quesResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		r.addProperty(p, quesResource);
	}
	
    public void addLocalKeyIdentifier(Resource r, String value) throws Exception{
		
    	String uri = getNewURI("ap");
		Resource idenResource = changes.createResource(uri);
		
		idenResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		idenResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral("local");
		idenResource.addProperty(vp.props.get("infoType1"), litrl1);
		
		Literal litrl2 = createLiteral(value);
		idenResource.addProperty(vp.props.get("freeTextValue1"), litrl2);
		
		r.addProperty(vp.props.get("localKey"), idenResource);
	}
    
    public void addDateRecordCreated(Resource r) throws Exception{
    	String uri = getNewURI("ap");
		Resource dateRecordCreatedResource = changes.createResource(uri);
		
		dateRecordCreatedResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		dateRecordCreatedResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//Calendar cal = Calendar.getInstance();
		//Literal litrl = createLiteral(dateFormat.format(cal.getTime()));
		Date value = Calendar.getInstance().getTime();
		String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
		Literal litrl = createLiteral(formattedDateStr);
		dateRecordCreatedResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		r.addProperty(vp.props.get("dateRecordCreated"), dateRecordCreatedResource);
	}
    
    public void addUserName(Resource r, String value) throws Exception{
    	String uri = getNewURI("ap");
		Resource userNameResource = changes.createResource(uri);
		
		userNameResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		userNameResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl = createLiteral(value.toLowerCase());
		userNameResource.addProperty(vp.props.get("freeTextValue1"), litrl);

		r.addProperty(vp.props.get("RMPersonCode"), userNameResource);
    }
    
    public void addRecordCreatedBy(Resource r) throws Exception{
    	String uri = getNewURI("ap");
		Resource recordCreatedByResource = changes.createResource(uri);
		
		recordCreatedByResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		recordCreatedByResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl = createLiteral("Academic Profile"); // name
		recordCreatedByResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		Literal litr2 = createLiteral("academic_profile");	// userID
		recordCreatedByResource.addProperty(vp.props.get("freeTextValue2"), litr2);
		
		r.addProperty(vp.props.get("recordInitiallyCreatedBy"), recordCreatedByResource);
    }
    
    
    public void addFreeTextbox(Resource r, Property p, String value) throws Exception{
    	String uri = getNewURI("ap");
		Resource textBoxResource = changes.createResource(uri);
		
		textBoxResource.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		textBoxResource.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl = createLiteral(value);
		textBoxResource.addProperty(vp.props.get("freeTextValue1"), litrl);
		
		r.addProperty(p, textBoxResource);
    }
     
    public void addPosition(Resource r, Property p, APPartyRecord record) throws Exception{
    	String position = record.getPosition();
    	String divfac = record.getDivfac();
    	String school = record.getSchool();
    	
    	String uri = getNewURI("ap");
		Resource posRes = changes.createResource(uri);
		
		posRes.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		posRes.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		if (! position.equals("")){
			Literal litrl1 = createLiteral(position);
			posRes.addProperty(vp.props.get("freeTextValue1"), litrl1);
		}
		
		if (! divfac.equals("")){
			Literal litrl2 = createLiteral(divfac);
			posRes.addProperty(vp.props.get("freeTextValue2"), litrl2);
		}
		
		if (! school.equals("")){
			Literal litrl3 = createLiteral(school);
			posRes.addProperty(vp.props.get("freeTextValue3"), litrl3);
		}
		
		r.addProperty(p, posRes);	
    }
    
    // LIBRDF-77
    public void addContactInfo(Resource r, Property p, APPartyRecord record) throws Exception{
    	
    	String personTitle = record.getPersonalTitle();
    	String firstname = record.getGivenName();
    	String surname = record.getSurname();
    	String email = record.getEmail();
    	String url = record.getAcadamicProfLink();
    	String phone = record.getPhone();
    	String fax = record.getFax();
    	
    	if (firstname.equals("")){ // Mandatory field
    		return;
    	}
    	
    	String uri = getNewURI("ap");
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
    
    	r.addProperty(p, contactInfo);	
    }
    
   public void addKeywrods(Resource r, Property p, String value, APPartyRecord record) throws Exception{
    	String[] keywords = value.split(",");
    	int i;
    	for (i = 0; i <  keywords.length; i++){
			String value1 =  keywords[i];
			if (value1.equals("")){continue;}
			
			// AP sending person name as keyword. Ex: Aaron Urquhart.
			String fullName = record.getGivenName() + " " + record.getSurname();
			if (value1.equals(fullName)){continue;}
			
			String uri = getNewURI("ap");
			Resource keywordRes = changes.createResource(uri);
			
			keywordRes.addProperty(vp.props.get("type"), vp.classes.get("thing"));
			keywordRes.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
			
			Literal litrl1 = createLiteral("local");
			keywordRes.addProperty(vp.props.get("infoType1"), litrl1);
			Literal litrl2 = createLiteral(value1);
			keywordRes.addProperty(vp.props.get("freeTextValue1"), litrl2);
			
			r.addProperty(p, keywordRes);
		}
    }
    
    public void _updateKeywrods(Resource r, Property p, String value, APPartyRecord record, Map<String, UpdateInfo> mapKeywords) throws Exception{
   
    	String[] keywords = value.split(",");
    	int i;
    	for (i = 0; i <  keywords.length; i++){
			String value1 =  keywords[i];
			if (value1.equals("")){continue;}
			
			// AP sending person name as keyword. Ex: Aaron Urquhart.
			String fullName = record.getGivenName() + " " + record.getSurname();
			if (value1.equals(fullName)){continue;}
			
			if(mapKeywords.containsKey(value1)){	// no update. keep it as it is.
				UpdateInfo updateInfo = mapKeywords.get(value1);
				updateInfo.setAction("IDEL");
			}else{	// create new keyword.
				String uri = getNewURI("ap");
				Resource keywordRes = changes.createResource(uri);
				
				keywordRes.addProperty(vp.props.get("type"), vp.classes.get("thing"));
				keywordRes.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
				
				Literal litrl1 = createLiteral("local");
				keywordRes.addProperty(vp.props.get("infoType1"), litrl1);
				Literal litrl2 = createLiteral(value1);
				keywordRes.addProperty(vp.props.get("freeTextValue1"), litrl2);
				
				r.addProperty(p, keywordRes);
			}
		}
    }
     
    public void addDiscipline(Resource r, Property p, String value) throws Exception{
    	String[] discipline = value.split(";");
    	int i;
    	for (i = 0; i <  discipline.length; i++){
			String value1 =  discipline[i];
			if (value1.equals("")){continue;}
			
			String FoRValue = Utils.getFoRDropDownString(value1);
			if (FoRValue.equals("")){continue;}
			
			String uri = getNewURI("ap");
			Resource disciplineRes = changes.createResource(uri);
			
			disciplineRes.addProperty(vp.props.get("type"), vp.classes.get("thing"));
			disciplineRes.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
			
			Literal litrl1 = createLiteral("anzsrc-for");
			disciplineRes.addProperty(vp.props.get("infoType1"), litrl1);
			Literal litrl2 = createLiteral(FoRValue);
			disciplineRes.addProperty(vp.props.get("infoType2"), litrl2);
			//disciplineRes.addProperty(vp.props.get("freeTextValue1"), value1);
			
			r.addProperty(p, disciplineRes);
		}
    }
    
    public void _updateDiscipline(Resource r, Property p, String value, Map<String, UpdateInfo> mapDiscipline)throws Exception{
    	String[] discipline = value.split(";");
    	int i;
    	for (i = 0; i <  discipline.length; i++){
			
    		String value1 =  discipline[i];
			if (value1.equals("")){continue;}
			
			String lowerCaseValue = value1.toLowerCase();
			
			if(mapDiscipline.containsKey(lowerCaseValue)){	// no update. keep it as it is.
				UpdateInfo updateInfo = mapDiscipline.get(lowerCaseValue);
				updateInfo.setAction("IDEL");
			}else{	// create new keywords.
				
				String FoRValue = Utils.getFoRDropDownString(value1);
				if (FoRValue.equals("")){continue;}
				
				String uri = getNewURI("ap");
				Resource disciplineRes = changes.createResource(uri);
				
				disciplineRes.addProperty(vp.props.get("type"), vp.classes.get("thing"));
				disciplineRes.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
				
				Literal litrl1 = createLiteral("anzsrc-for");
				disciplineRes.addProperty(vp.props.get("infoType1"), litrl1);
				Literal litrl2 = createLiteral(FoRValue);
				disciplineRes.addProperty(vp.props.get("infoType2"), litrl2);
				//disciplineRes.addProperty(vp.props.get("freeTextValue1"), value1);
				
				r.addProperty(p, disciplineRes);
			}
		}
    }
    
    public void addBiography(Resource r, Property p, APPartyRecord record) throws Exception{
    	String uri = getNewURI("ap");
		Resource biography = changes.createResource(uri);
		
		String value = record.getBiography().trim();
		if (value.equals("")) return;
		
		biography.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		biography.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral("brief");
		biography.addProperty(vp.props.get("infoType1"), litrl1);
		Literal litrl2 = createLiteral(value);
		biography.addProperty(vp.props.get("freeTextValue1"), litrl2);
		Literal litrl3 = createLiteral(record.getAcadamicProfLink());
		biography.addProperty(vp.props.get("freeTextValue2"), litrl3);
		
		r.addProperty(p, biography);
    }
    
    public void addPublications(Resource r, Property p, String value) throws Exception{
    	String uri = getNewURI("ap");
		Resource publication = changes.createResource(uri);
		
		publication.addProperty(vp.props.get("type"), vp.classes.get("thing"));
		publication.addProperty(vp.props.get("type"), vp.classes.get("customProperty"));
		
		Literal litrl1 = createLiteral("publication");
		publication.addProperty(vp.props.get("infoType1"), litrl1);
		Literal litrl2 = createLiteral("url");
		publication.addProperty(vp.props.get("infoType2"), litrl2);
		Literal litrl3 = createLiteral(value);
		publication.addProperty(vp.props.get("freeTextValue1"), litrl3);
		
		r.addProperty(p, publication);
    }
    
    public String getNewURI(String prefix){
    	String uri = "";
    	resourceKey += 1;
    	uri = defaultNameSpace + prefix + String.valueOf(resourceKey);
    	
    	return uri;
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

	public long getLocalKey() {
		return localKey;
	}

	public void setLocalKey(long localKey) {
		this.localKey = localKey;
	}

	public Model getChanges() {
		return changes;
	}

	public void setChanges(Model changes) {
		this.changes = changes;
	}
	
	public Literal createLiteral(String value){
	    String v = EditN3Utils.stripInvalidXMLChars(value);
	    return literalCreationModel.createTypedLiteral(v, "http://www.w3.org/2001/XMLSchema#string");
	}
}

class UpdateInfo{
	public Statement stmt;
	public String valueToBeMatched;
	public String action;	// DELETE, IDEL
	
	UpdateInfo(Statement stmt, String valueToBeMatched, String action){
		this.stmt = stmt;
		this.valueToBeMatched = valueToBeMatched;
		this.action = action;
	}
	
	public Statement getStatement() {
		return stmt;
	}

	public void setStatement(Statement stmt) {
		this.stmt = stmt;
	}

	public String getValueToBeMatched() {
		return valueToBeMatched;
	}

	public void setValueToBeMatched(String valueToBeMatched) {
		this.valueToBeMatched = valueToBeMatched;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
