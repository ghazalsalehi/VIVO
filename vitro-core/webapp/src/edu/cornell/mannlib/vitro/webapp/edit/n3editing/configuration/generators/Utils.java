package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.apache.solr.client.solrj.SolrQuery;

import qut.crmm.utils.VivoProps;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.admin.UserAccountsListPage;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQuery;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListQueryResults;

public class Utils {
	//final static String CONFIG_FILE =  "C:\\Apps\\VIVO_HOME_1.5\\apache-tomcat-7.0.29\\webapps\\vivo\\qut_data\\conf\\DropDownOptionsForFields.conf";
	final static String CONFIG_FILE =  "/usr/local/qut-rdf/apache-tomcat/webapps/vivo/qut_data/conf/DropDownOptionsForFields.conf";
	public static final String RDF_HOME =  "/usr/local/qut-rdf/"; // LIBRDF-57
	
	public static DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static DateFormat xsdSimpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat xsdDateTimeFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
	
	public static final String RDF_WEBSITE_EMAIL_ADDRESS = "researchdatafinder@qut.edu.au";
	
	public static final String VCLASS_PEOPLE =  "http://www.qut.edu.au/ontologies/vivoqut#people";
	public static final String VCLASS_RESEARCHER =  "http://www.qut.edu.au/ontologies/vivoqut#researcher";
	public static final String VCLASS_ADMINISTRATIVE_POSITION =  "http://www.qut.edu.au/ontologies/vivoqut#administrativePosition";
	public static final String VCLASS_GROUP =  "http://www.qut.edu.au/ontologies/vivoqut#group";
	
	public static final String VCLASS_ACTIVITIES  = "http://www.qut.edu.au/ontologies/vivoqut#activities";
	
	public static final String VCLASS_DATA_COLLECTIONS  = "http://www.qut.edu.au/ontologies/vivoqut#dataCollections";
	public static final String VCLASS_CATALOGUE_OR_INDEX  = "http://www.qut.edu.au/ontologies/vivoqut#catalogueOrIndex";
	public static final String VCLASS_REPOSITORY  = "http://www.qut.edu.au/ontologies/vivoqut#repository";
	public static final String VCLASS_REGISTRY  = "http://www.qut.edu.au/ontologies/vivoqut#registry";
	public static final String VCLASS_COLLECTION  = "http://www.qut.edu.au/ontologies/vivoqut#collection";
	public static final String VCLASS_RESEARCH_DATA_SET  = "http://www.qut.edu.au/ontologies/vivoqut#researchDataSet";
	
	public static final String VCLASS_SERVICE  = "http://www.qut.edu.au/ontologies/vivoqut#service";
	public static final String VCLASS_SERVICE_annotate  = "http://www.qut.edu.au/ontologies/vivoqut#annotate";
	public static final String VCLASS_SERVICE_report  = "http://www.qut.edu.au/ontologies/vivoqut#report";
	public static final String VCLASS_SERVICE_syndicateRSS  = "http://www.qut.edu.au/ontologies/vivoqut#syndicateRSS";
	public static final String VCLASS_SERVICE_syndicateATOM  = "http://www.qut.edu.au/ontologies/vivoqut#syndicateATOM";
	public static final String VCLASS_SERVICE_harvestOAIPMH  = "http://www.qut.edu.au/ontologies/vivoqut#harvestOAIPMH";
	public static final String VCLASS_SERVICE_SearchZ3950  = "http://www.qut.edu.au/ontologies/vivoqut#SearchZ3950";
	public static final String VCLASS_SERVICE_searchSRU  = "http://www.qut.edu.au/ontologies/vivoqut#searchSRU";
	public static final String VCLASS_SERVICE_searchSRW  = "http://www.qut.edu.au/ontologies/vivoqut#searchSRW";
	public static final String VCLASS_SERVICE_create  = "http://www.qut.edu.au/ontologies/vivoqut#create";
	public static final String VCLASS_SERVICE_transform  = "http://www.qut.edu.au/ontologies/vivoqut#transform";
	public static final String VCLASS_SERVICE_searchOpenSearch  = "http://www.qut.edu.au/ontologies/vivoqut#searchOpenSearch";
	public static final String VCLASS_SERVICE_assemble  = "http://www.qut.edu.au/ontologies/vivoqut#assemble";
	public static final String VCLASS_SERVICE_searchHTTP  = "http://www.qut.edu.au/ontologies/vivoqut#searchHTTP";
	public static final String VCLASS_SERVICE_generate  = "http://www.qut.edu.au/ontologies/vivoqut#generate";

	
	public static final String VCLASS_SOFTWARE_AND_CODE =  "http://www.qut.edu.au/ontologies/vivoqut#softwareAndCode";
	public static final String VCLASS_SOFTWARE =  "http://www.qut.edu.au/ontologies/vivoqut#software";
	public static final String VCLASS_CODE =  "http://www.qut.edu.au/ontologies/vivoqut#code";
	
	
	// -------------------------------------SPATIAL DATA------------------------------------------------------------------------------------------
	
	public static final String VCLASS_SPATIAL_DATA =  "http://www.qut.edu.au/ontologies/vivoqut#spatialData";
	
	public static final String VCLASS_SPATIAL_BUILD_ENVIRONMENT =  "http://www.qut.edu.au/ontologies/vivoqut#builtEnvironment";
	public static final String VCLASS_SPATIAL_INTELLIGENCE_MILITARY =  "http://www.qut.edu.au/ontologies/vivoqut#intelligenceAndMilitary";
	public static final String VCLASS_SPATIAL_TRANSPORTATION =  "http://www.qut.edu.au/ontologies/vivoqut#transportation";
	public static final String VCLASS_SPATIAL_STRUCTURE =  "http://www.qut.edu.au/ontologies/vivoqut#structure";
	
	public static final String VCLASS_SPATIAL_CLIMATE =  "http://www.qut.edu.au/ontologies/vivoqut#climate";
	
	public static final String VCLASS_SPATIAL_ECONOMY_AND_HEALTH =  "http://www.qut.edu.au/ontologies/vivoqut#economyandHealth";
	public static final String VCLASS_SPATIAL_ECONOMY =  "http://www.qut.edu.au/ontologies/vivoqut#health";
	public static final String VCLASS_SPATIAL_HEALTH =  "http://www.qut.edu.au/ontologies/vivoqut#economy";
	
	public static final String VCLASS_SPATIAL_ELEVATION_AND_BASEMAPS =  "http://www.qut.edu.au/ontologies/vivoqut#elevationandBaseMaps";
	public static final String VCLASS_SPATIAL_ELEVATION =  "http://www.qut.edu.au/ontologies/vivoqut#elevation";
	public static final String VCLASS_SPATIAL_BASEMAPS =  "http://www.qut.edu.au/ontologies/vivoqut#imageryBaseMapsEarthCover";
	
	public static final String VCLASS_SPATIAL_ENERGY_AND_UTILITES =  "http://www.qut.edu.au/ontologies/vivoqut#energyAndUtilities";
	
	public static final String VCLASS_SPATIAL_FARMING =  "http://www.qut.edu.au/ontologies/vivoqut#farming";
	
	public static final String VCLASS_SPATIAL_NATURAL_ENVIRONMENT =  "http://www.qut.edu.au/ontologies/vivoqut#naturalEnvironment";
	public static final String VCLASS_SPATIAL_ENVIRONMENT =  "http://www.qut.edu.au/ontologies/vivoqut#environment";
	public static final String VCLASS_SPATIAL_GEOSCIENTIFIC_INFORMATION =  "http://www.qut.edu.au/ontologies/vivoqut#geoscientificInformation";
	public static final String VCLASS_SPATIAL_BIOTA =  "http://www.qut.edu.au/ontologies/vivoqut#biota";
	
	public static final String VCLASS_SPATIAL_POSITION_AND_BOUNDARIES =  "http://www.qut.edu.au/ontologies/vivoqut#positionandBoundaries";
	public static final String VCLASS_SPATIAL_NATURAL_BOUNDARIES =  "http://www.qut.edu.au/ontologies/vivoqut#boundaries";
	public static final String VCLASS_SPATIAL_PLANNINGCADASTRE =  "http://www.qut.edu.au/ontologies/vivoqut#planningCadastre";
	public static final String VCLASS_SPATIAL_LOCATION =  "http://www.qut.edu.au/ontologies/vivoqut#location";
	
	public static final String VCLASS_SPATIAL_SOCIETRY_AND_CULTURE =  "http://www.qut.edu.au/ontologies/vivoqut#societyandCulture";
	
	public static final String VCLASS_SPATIAL_WATER_AND_COASTAL =  "http://www.qut.edu.au/ontologies/vivoqut#waterandCoastal";
	public static final String VCLASS_SPATIAL_OCEANS =  "http://www.qut.edu.au/ontologies/vivoqut#oceans";
	public static final String VCLASS_SPATIAL_INLAND_WATERS =  "http://www.qut.edu.au/ontologies/vivoqut#inlandWaters";

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	public static final String PROPERTY_RECORD_STATUS = "http://www.qut.edu.au/ontologies/vivoqut#publishRecord";
	public static final String PROPERTY_CONTACT_INFO = "http://www.qut.edu.au/ontologies/vivoqut#contactInfo";
	public static final String PROPERTY_RECORD_OWNERSHIP_INFO = "http://www.qut.edu.au/ontologies/vivoqut#recordInitiallyCreatedBy";
	public static final String PROPERTY_RECORD_ASSIGN_TO_REVIEW = "http://www.qut.edu.au/ontologies/vivoqut#assignForReviewOrComplete";
	public static final String PROPERTY_RECORD_OTHER_INFO = "http://www.qut.edu.au/ontologies/vivoqut#recordOtherInfo";
	public static final String PROPERTY_RECORD_PUBLISHED_BY_INFO =  "http://www.qut.edu.au/ontologies/vivoqut#recordPublishedByInfo";
	public static final String PROPERTY_RECORD_MODIFIED_DATE =  "http://www.qut.edu.au/ontologies/vivoqut#dateRecordModified";
	
	public static final String PROPERTY_RDF_TYPE =  "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String PROPERTY_RDF_LABEL =  "http://www.w3.org/2000/01/rdf-schema#label";
	
	public static final String PROPERTY_FREE_TEXT_VALUE_1 =  "http://www.qut.edu.au/ontologies/vivoqut#freeTextValue1";
	public static final String PROPERTY_FREE_TEXT_VALUE_2 =  "http://www.qut.edu.au/ontologies/vivoqut#freeTextValue2";
	public static final String PROPERTY_FREE_TEXT_VALUE_3 =  "http://www.qut.edu.au/ontologies/vivoqut#freeTextValue3";
	public static final String PROPERTY_FREE_TEXT_VALUE_4 =  "http://www.qut.edu.au/ontologies/vivoqut#freeTextValue4";
	public static final String PROPERTY_INFO_TYPE_1 =  "http://www.qut.edu.au/ontologies/vivoqut#infoType1";
	public static final String PROPERTY_INFO_TYPE_2 =  "http://www.qut.edu.au/ontologies/vivoqut#infoType2";
	public static final String PROPERTY_INFO_TYPE_3 =  "http://www.qut.edu.au/ontologies/vivoqut#infoType3";
	
	public static final String VCLASSGROUP_PEOPLE_NAME = "People";
	public static final String VCLASSGROUP_SOFTWAREANDCODE_NAME = "SoftwareandCode";
	public static final String VCLASSGROUP_SPATIALDATA_NAME = "SpatialData";
	
	public static final String VCLASSGROUP_SPATIALDATA_DISPLAY_NAME = "Spatial Data";
	
	public static final String VCLASS_PEOPLE_TYPE = "people";
	public static final String VCLASS_ACTIVITIES_TYPE = "activities";
	public static final String VCLASS_DATA_COLLECTIONS_TYPE = "dataCollections";
	public static final String VCLASS_SERVICE_TYPE = "service";
	public static final String VCLASS_SOFTWARE_AND_CODE_TYPE = "softwareAndCode";
	public static final String VCLASS_SPATIAL_DATA_TYPE = "spatialData";
	
	public static final String ENV_SOFTWARE_CODE_FINDER = "scf";
	public static final String ENV_RESEARCH_DATA_FINDER = "rdf";
	public static final String ENV_SPATIAL_DATA_FINDER =  "spatial";
	
	public static final String RECORD_STATUS_DRAFT_VALUE = "Draft";
	public static final String RECORD_STATUS_UNDER_REVIEW_VALUE = "UnderReview";
	public static final String RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE = "PublishedOpenAccess";
	public static final String RECORD_STATUS_PUBLISH_QUT_ACCESS_VALUE = "PublishedQUTAccess";
	
	//Email templates
	//-----------------------------------------------------------------------------------
	public final static String TEMPLATE_MANAGE_RECORDS_EMAIL = "manageRecords-email.ftl";
	public final static String TEMPLATE_GENERATE_SITEMAP_EMAIL = "generateSitemap-email.ftl";
	public final static String TEMPLATE_RDF_DEFAULT_EMAIL_ERROR = "rdf-default-email-error.ftl";
	
	// Tomcat manager
	//-----------------------------------------------------------------------------------
	public final static String TOMCAT_MANAGER_USER_NAME = "rdftomcat";
	public final static String TOMCAT_MANAGER_PASSWORD = "qutrdftomcat123";
	
	// CR: 048
    // manage_records
	//-----------------------------------------------------------------------
	public static final String MANAGE_RECORDS_MODULE_PARAMETER = "module";
	public static final String MRS_MODULE_DASHBOARD_VALUE = "dashboard";
	public static final String MRS_MODULE_WROKSPACE_VALUE = "workspace";
	public static final String MRS_MODULE_STATISTICS_VALUE = "statistics";
	public static final String MRS_MODULE_DOWNLOADS_VALUE = "downloads";
	public static final String MRS_MODULE_PARAM_ACTION = "action";
	public static final String MRS_MODULE_PARAM_PAGE = "page";
	public static final String MRS_MODULE_PARAM_KEY = "key";
	public static final String MRS_MODULE_PARAM_SUB_ACTION = "subAction";
	public static final String MRS_MODULE_RECORDS_STATE = "recordsState";
	public static final String MRS_MODULE_CURRENT_RECORDS_STATE = "currentRecordsState";
	public static final String MRS_MODULE_NEXT_RECORD_STATE = "nextRecordsState";
	public static final String MRS_MODULE_PARAM_ASSIGN_FOR_REVIEW_USERID = "assignForReviewUserID";
	public static final String MRS_MODULE_PARAM_COOMMENTS = "comments";
	public static final String MRS_MODULE_PARAM_TYPE = "type"; 
	
	// LIBRDF-83
	public static final String MRS_MODULE_RESPONSE_INFO_LEVEL = "reponseInfoLevel";
	public static final int MRS_MODULE_RESPONSE_INFO_LEVEL_0 = 0;
	
	
	public static final String MRS_MODULE_ADMIN_COMMON_USERID= "admin";
	public static final String MRS_MODULE_ROOT_USERID= "root";
	
	public static final int MRS_MODULE_RECORDS_PER_PAGE = 10;
	
	public static final String MRS_SUCESS = "MRS_001";
	public static final String MRS_ERROR_MSG_SUCESS = "sucess";
	public static final String MRS_FAILED = "MRS_002";
	public static final String MRS_ERROR_MSG_FAILED_TO_DELETE_RECORD = "Failed to delete the record.";
	public static final String MRS_ERROR_MSG_FAILED_MOVE_TO_NEXT_LEVEL = "Failed to move to next record level.";
	public static final String MRS_ERROR_MSG_FAILED_TO_FIND_USER_ACCOUNT_LIST = "Failed to find user account list.";
	public static final String MRS_ERROR_MSG_FAILED_ASSIGN_RECORD_TO_REVIEW = "Failed to assign record for review.";
	public static final String MRS_ERROR_MSG_FAILED_TO_SEND_RECORD_BACK = "Failed to send record back to the user.";
	
	// manage_records Email Types
	//-----------------------------------------------------------------------
	public static final String MRS_EMAIL_SEND_BACK_TO_DRAFT_DISPLAY_NAME = "The following reacord is sent back to draft.";
	public static final String MRS_EMAIL_SEND_FOR_REVIEW_DISPLAY_NAME = "The following record is ready for review.";
	public static final String MRS_EMAIL_RECORD_PUBLISH_OPEN_ACCESS_DISPLAY_NAME = "The following record is published for open access.";
	public static final String MRS_EMAIL_RECORD_PUBLISH_QUT_ACCESS_DISPLAY_NAME = "The following record is published for QUT access.";
	public static final String MRS_EMAIL_RECORD_ASSIGN_TO_REVIEW_DISPLAY_NAME = "The following record has been assigned to you for review.";
	public static final String MRS_EMAIL_RECORD_NOTIFY_USER_AND_SEND_BACK_DISPLAY_NAME = "The following record requires revision.";
	
	public static final String MRS_EMAIL_SEND_BACK_TO_DRAFT_DESCRIPTION = "";
	public static final String MRS_EMAIL_SEND_FOR_REVIEW_DESCRIPTION = "The Research Data Finder team advises that the following record is ready for review.";
	public static final String MRS_EMAIL_RECORD_PUBLISH_OPEN_ACCESS_DESCRIPTION = "The Research Data Finder team advises that the following record has been published for open access.";
	public static final String MRS_EMAIL_RECORD_PUBLISH_QUT_ACCESS_DESCRIPTION = "The Research Data Finder team advises that the following record has been published for QUT access.";
	public static final String MRS_EMAIL_RECORD_ASSIGN_TO_REVIEW_DESCRIPTION = "The Research Data Finder team advises that the following record has been assigned to you for review.";
	public static final String MRS_EMAIL_RECORD_NOTIFY_USER_AND_SEND_BACK_DESCRIPTION = "The Research Data Finder team advises that the following record requires revision. Please see the comments below.";

	//---------------------------------------------------------------------------------------------------------------------
	
	public static final String SESSION_VAR_WINDOW_LOCATION_HREF = "windowLocation";
	public static final String ESOE_PROTECTED_URL = "/loginExternalAuthReturn";
	public static final String ESOE_REDIRECT_URL_PRAM_NAME = "redirectURL";
	
	public static final String ANDS_MINT_DOI_APP_ID = "9de2dd716eddd8315e5d455688ce8ce514d229ad";
	public static final String ANDS_MINT_DOI_TEST_APP_ID = "e0b39ccb0f16d11768bf5ce0b4f81ee1e9a44e87";
	
	//---------------------------------------------------------------------------------------------------------------------
	public static final String MRS_MODULE_DOWNLOADS_QUICK_GUIDE_DOC = "Research_Data_Finder_Quick Guide_V4.docx";
	public static final String MRS_MODULE_DOWNLOADS_FILE_PATH = "qut_data//docs//";
	
	// LIBRDF-103
	//---------------------------------------------------------------------------------------------------------------------
	
	public static final String SYSTEM_INTEGRATION_MODULE_PARAMETER = "module";
	public static final String SYSTEM_INTEGRATION_MODULE_ACTION = "action";
	public static final String SYSINT_MODULE_RESEARCH_DATA_REPOSITORY_VALUE = "rdr";
	public static final String SYSINT_MODULE_PARAM_RDF_INDIVIDUAL_URL = "individualURL";
	public static final String SYSINT_MODULE_PARAM_PACKAGE_TITLE = "packageTitle";
	public static final String SYSINT_MODULE_PARAM_PACKAGE_ID = "packageID";
	
	public static final String SYSINT_SUCCESS = "SYSINT_001";
	public static final String SYSINT_ERROR_MSG_SUCCESS = "sucess";
	public static final String SYSINT_FAILED = "SYSINT_002";
	public static final String SYSINT_ERROR_MSG_FAILED_TO_CREATE_DATASET = "Failed to create the dataset.";
	
	public static final String SYSINT_RDR_ERROR_FAILED_TO_RETRIVE_RDR_URL = "Data repository url not found.";
	public static final String SYSINT_RDR_ERROR_FAILED_TO_RETRIVE_ADMIN_API_KEY = "Data repository admin API key not found.";
	public static final String SYSINT_RDR_ERROR_FAILED_TO_RETRIVE_API_VERSION = "Data repository API version not found.";
	
	public static final String SYSINT_RDR_ERROR_INVALID_ACTION = "Invalid action.";
	public static final String SYSINT_RDR_ERROR_ACTION_NOT_FOUND = "Action not found.";
	public static final String SYSINT_RDR_ERROR_CREATE_PACKAGE_INVALID_PARAMS = "Dataset title or individual url cannot be null.";
	public static final String SYSINT_RDR_ERROR_INVALID_USER_API_KEY = "Invalid user authorization.";
	public static final String SYSINT_RDR_ERROR_DATASET_PACKAGE_URL_ALREADY_IN_USE = "This dataset title is already in use. Please choose a different name.";
	public static final String SYSINT_RDR_ERROR_PACKAGE_INVALID_PARAMS = "Dataset ID cannot be empty.";
	
	
	// IMPORTTANT: before calling these methods must take the write lock..
	public static void saveFinalLocalKey(Model writeModel, String defaultNameSpace, long localKey, VivoProps vp) throws Exception{

		String finalLocalKey =  String.valueOf(localKey);
		String lkcUri = defaultNameSpace + "lkc";
		
		//Lock lock = writeModel.getLock();
    	//lock.enterCriticalSection(Lock.WRITE);
    	
    	Resource r1localKey = writeModel.getResource(lkcUri);
    	if (r1localKey != null){
    		r1localKey.removeProperties();
    		r1localKey.addProperty(vp.props.get("localKeyCounter"), finalLocalKey);
    		  
    		//lock.leaveCriticalSection();
    	}else{
    		//lock.leaveCriticalSection();
        	throw new Exception("failed to save the final local key. [cannot find the local key resource. "+ lkcUri + "]");
	    }
	}
	
	public static long loadLocalKey(Model writeModel, String defaultNameSpace, VivoProps vp) throws Exception{	
		Property plocalKeyCounter = vp.props.get("localKeyCounter");
    	
	    String lkcUri = defaultNameSpace + "lkc";
	    long localKey = 0;
	    Resource r1localKey;
	    
	    if (writeModel == null){throw new Exception("couldn't find the write model.(null)");}
	    else{
	    	
	    	//Lock lock = writeModel.getLock();
	    	//lock.enterCriticalSection(Lock.WRITE);
	    	
	    	r1localKey = writeModel.getResource(lkcUri);
	    	if (r1localKey != null){
	           	Statement stmnt1 = r1localKey.getProperty(plocalKeyCounter);
	        	if (stmnt1 != null) {
	        		String str = stmnt1.getString();
	        		localKey = Long.valueOf(str);
	        	}else{
	        		//lock.leaveCriticalSection();
	            	throw new Exception("cannot find the local key statement. "+ r1localKey.toString());
	        	}
	        	
	        	//lock.leaveCriticalSection();
	    	}else{
	    		//lock.leaveCriticalSection();
            	throw new Exception("cannot find the local key resource. "+ r1localKey.toString());
    	    }
	    }
	    
	    if (localKey <= 0){throw new Exception("Invalid local key found." + String.valueOf(localKey));}
	    
	    return localKey;
	}
	
	public static long loadResourceKey(Model writeModel, String defaultNameSpace, VivoProps vp) throws Exception{
		Property pResourceKeyCounter = vp.props.get("resourceURICounter");
    	
		String ruric = defaultNameSpace + "ruric";
	    long resourceKey = 0;
	    Resource rResourceKey;
	    
	    if (writeModel == null){throw new Exception("couldn't find the write model.(null)");}
	    else{
	    	
	    	//Lock lock = writeModel.getLock();
	    	//lock.enterCriticalSection(Lock.WRITE);
	    	
	    	rResourceKey = writeModel.getResource(ruric);
	    	if (rResourceKey != null){
	           	Statement stmnt1 = rResourceKey.getProperty(pResourceKeyCounter);
	        	if (stmnt1 != null) {
	        		String str = stmnt1.getString();
	        		resourceKey = Long.valueOf(str);
	        	}else{
	        		//lock.leaveCriticalSection();
	            	throw new Exception("cannot find the local key statement. "+ rResourceKey.toString());
	        	}
	        	
	        	//lock.leaveCriticalSection();
	    	}else{
	    		//lock.leaveCriticalSection();
            	throw new Exception("cannot find the local key resource. "+ ruric);
    	    }
	    }
	    
	    if (resourceKey <= 0){throw new Exception("Invalid local key found." + String.valueOf(resourceKey));}
	    
	    return resourceKey;
	}
	
	public static void saveFinalResourceKey(Model writeModel, String defaultNameSpace, long resourceKey, VivoProps vp) throws Exception{

		String finalResourceKey =  String.valueOf(resourceKey);
		String ruric = defaultNameSpace + "ruric";
		
		//Lock lock = writeModel.getLock();
    	//lock.enterCriticalSection(Lock.WRITE);
    	
    	Resource rResourceKey = writeModel.getResource(ruric);
    	if (rResourceKey != null){
    		rResourceKey.removeProperties();
    		rResourceKey.addProperty(vp.props.get("resourceURICounter"), finalResourceKey);
    		  
    		//lock.leaveCriticalSection();
    	}else{
    		//lock.leaveCriticalSection();
        	throw new Exception("failed to save the final local key. [cannot find the local key resource. "+ ruric + "]");
	    }
	}
	
	// CR: 54
	public static String loadLastHarvestedDate(Model writeModel, String defaultNameSpace) throws Exception{
		
	    String RDAHarvestedInfo = defaultNameSpace + "RDAHarvestedInfo";
	    String strLastRDAHarvestedDate = "";
	    Resource rRDAHarvestedInfo = null;
	    
	    if (writeModel == null){throw new Exception("couldn't find the write model.(null)");}
	    else{
	    	Property plastRDAHarvestedDate = writeModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "lastHarvestedDate");
	    	
	    	rRDAHarvestedInfo = writeModel.getResource(RDAHarvestedInfo);
	    	if (rRDAHarvestedInfo != null){
	           	Statement stmnt1 = rRDAHarvestedInfo.getProperty(plastRDAHarvestedDate);
	        	if (stmnt1 != null) {
	        		strLastRDAHarvestedDate = stmnt1.getString();
	        		return strLastRDAHarvestedDate;
	        	}else{
	            	throw new Exception("cannot find the RDA harvest info statement. "+ rRDAHarvestedInfo.toString());
	        	}
	        	
	    	}else{
            	throw new Exception("cannot find the RDA harvest info resource. "+ rRDAHarvestedInfo.toString());
    	    }
	    }
	}
	
	public static void SaveNextRDAHarvestedInfo(VitroRequest vreq, String strLastHarvestedDate, String strFinalHarvestedDate) throws Exception{
		OntModelSelector ontModelSelector = vreq.getOntModelSelector();
		OntModel ontModel = ontModelSelector.getFullModel();
		
		String defaultNameSpace = vreq.getWebappDaoFactory().getDefaultNamespace();
		
		String RDAHarvestedInfo = defaultNameSpace + "RDAHarvestedInfo";
		if (ontModel == null){throw new Exception("couldn't find the write model.(null)");}
	    else{
	    	
	    	String strPreviousHarvestedDate = null;
	    	Property pLastHarvestedDate = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "lastHarvestedDate");
	    	Property pPreviousHarvestedDate = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "previousHarvestedDate");
	    	Property pPrevPrevHarvestedDate = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
	    	
	    	try{
	    		ontModel.enterCriticalSection(Lock.WRITE);
	    		
	    		Resource rRDAHarvestedInfo = ontModel.getResource(RDAHarvestedInfo);
		    	if (rRDAHarvestedInfo != null){
		    		Statement stmnt1 = rRDAHarvestedInfo.getProperty(pPreviousHarvestedDate);
		    		if (stmnt1 != null) {
		        		strPreviousHarvestedDate = stmnt1.getString();
		        	}

		    		Literal	litrlFinalHarvestedDate = ontModel.createTypedLiteral(new String(strFinalHarvestedDate));
		    		Literal	litrlLastHarvestedDate = ontModel.createTypedLiteral(new String(strLastHarvestedDate));
	              		
		    		rRDAHarvestedInfo.removeAll(pLastHarvestedDate);
		    		rRDAHarvestedInfo.addProperty(pLastHarvestedDate, litrlFinalHarvestedDate);
		    		
		    		rRDAHarvestedInfo.removeAll(pPreviousHarvestedDate);
		    		rRDAHarvestedInfo.addProperty(pPreviousHarvestedDate, litrlLastHarvestedDate);
		    		
		    		if (strPreviousHarvestedDate != null){
		    			Literal	litrlPreviousHarvestedDate = ontModel.createTypedLiteral(new String(strPreviousHarvestedDate));
		    			
		    			rRDAHarvestedInfo.removeAll(pPrevPrevHarvestedDate);
			    		rRDAHarvestedInfo.addProperty(pPrevPrevHarvestedDate, litrlPreviousHarvestedDate);
		    		}
		    	}
		    	
		    	ontModel.leaveCriticalSection();
	    	}catch(Exception ex){
	    		ontModel.leaveCriticalSection();
	    	}
	    }
	}

	
	public static String getFoRDropDownString(String FoRName) throws Exception{	
		String FoRStr = "";
		String sSection = "[RESEARCH_AREAS_FORCODE]";

		try {
			
		    BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
		    String str;
		    String regex = "\\[.*\\]";
		    boolean bIsFound = false;
		    while ((str = in.readLine()) != null) {
		    	if (str.equals(sSection)){
		    		bIsFound = true;
		    		continue;
		    	}
		    	
		    	if (bIsFound && str.matches(regex)){
		    		bIsFound = false;
		    	}
		    	
		    	if (bIsFound){
		    		String regex1 = "^[0-9]+\\s" + FoRName.trim() + "$";
		    		if (!str.equals("")){
		    			Pattern p = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
			    		Matcher m = p.matcher(str);
			    		if (m.matches()){
			    			FoRStr = str.trim();
			    			break;
			    		}
		    		}
		    	}
		    }
		    
		    in.close();
		}catch(Exception e){
			//throw e;
		}
		
		return FoRStr;
	}
	
	public static OntModel getABoxModel(HttpSession session, ServletContext ctx) {   
        if (session != null 
                && session.getAttribute("baseOntModelSelector")
                        instanceof OntModelSelector) {
            return ((OntModelSelector) 
                    session.getAttribute("baseOntModelSelector"))
                    .getABoxModel();   
        } else {
            return ((OntModelSelector) 
                    ctx.getAttribute("baseOntModelSelector")).getABoxModel();
        }
    }
	
	public static String getCurrentUserRoleAsString(RoleLevel role){
		String currentUserRole = "";
		
		if (role == RoleLevel.PUBLIC ){
			currentUserRole = "PUBLIC";
		}else if (role == RoleLevel.EDITOR){	// qut student and staff
			currentUserRole = "EDITOR";
		}else if (role == RoleLevel.CURATOR){	// qut data librarians
			currentUserRole = "CURATOR";
		}else if (role == RoleLevel.DB_ADMIN){	
			currentUserRole = "DB_ADMIN";
		}else if (role == RoleLevel.SELF){	
			currentUserRole = "SELF";
		}else if (role == RoleLevel.NOBODY){	
			currentUserRole = "ROOT";
		}else{
			currentUserRole = "";
		}
		
		return currentUserRole;
	}
	
	public static void setUserEnvironment(String env,  Map<String, Object> body){	// rdf or scf or spatial
		
        if ((env != null) && (env.equals(Utils.ENV_SOFTWARE_CODE_FINDER))){
        	 body.put("env", Utils.ENV_SOFTWARE_CODE_FINDER);
        }else if ((env != null) && (env.equals(Utils.ENV_SPATIAL_DATA_FINDER))){
        	body.put("env", Utils.ENV_SPATIAL_DATA_FINDER);
        }else{
        	 body.put("env", Utils.ENV_RESEARCH_DATA_FINDER);
        }
	}
	
	public static List<String> getFormSpecificData(String sSection){
		
		ArrayList wordList = new ArrayList();
		
		try {
			
		    BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
		    String str;
		    String regex = "\\[.*\\]";
		    boolean bIsFound = false;
		    while ((str = in.readLine()) != null) {
		    	
		    	if (str.equals(sSection)){
		    		bIsFound = true;
		    		continue;
		    	}
		    	
		    	if (bIsFound && str.matches(regex)){
		    		bIsFound = false;
		    	}
		    	
		    	if (bIsFound){
		    		if (!str.equals("")){
		    			wordList.add(str);
		    		}
		    	}
		    }
		    
		    in.close();
		} catch (IOException e) {
		}

		return wordList;
	}
	
	public static List<UserAccount> getRegisteredAdminUserAccountList(VitroRequest vreq){
		List<UserAccount> accounts = null;
  		UserAccountsListPage page = new UserAccountsListPage(vreq);
  		accounts = page.getAdminAccountList();
  		
  		return accounts;
	}
	public static HashMap<String, String> getRegisteredAdminList(VitroRequest vreq){
		List<UserAccount> accounts = null;
  		UserAccountsListPage page = new UserAccountsListPage(vreq);
  		accounts = page.getAdminAccountList();
  		
  		HashMap<String, String> mapAdminList = new HashMap<String, String>();
  		
  		if (accounts != null){
  			for (UserAccount account : accounts) {
  				String userID = "";
  				String name = account.getFirstName() + " " + account.getLastName();
  				if (account.isExternalAuthOnly()){
  					userID = account.getExternalAuthId();
  				}else{
  					userID = account.getFirstName();
  				}
  				
  				mapAdminList.put(userID, name);
  			}	
  		}
  		
  		//mapAdminList.put(Utils.MRS_MODULE_ROOT_USERID, "ROOT"); //for test purpose
  		mapAdminList.put(Utils.MRS_MODULE_ADMIN_COMMON_USERID, "Administrator");
  		
  		return mapAdminList;
	}
	
	public static HashMap<String, String> getRegisteredAdminAndCuratorList(VitroRequest vreq){
  		List<UserAccount> accounts = null;
  		UserAccountsListPage page = new UserAccountsListPage(vreq);
  		accounts = page.getAdminAndCuratorAccountList();
  		
  		HashMap<String, String> mapAdminCuratorList = new HashMap<String, String>();
  		
  		if (accounts != null){
  			for (UserAccount account : accounts) {
  				String userID = "";
  				String name = account.getFirstName() + " " + account.getLastName();
  				if (account.isExternalAuthOnly()){
  					userID = account.getExternalAuthId(); // Curators are authenticated by QUT single sign on.
  				}else{	//root user or manually created accounts.
  					userID = account.getFirstName();
  				}
  				
  				mapAdminCuratorList.put(userID, name);
  			}	
  		}
  		
  		//mapAdminCuratorList.put(Utils.MRS_MODULE_ROOT_USERID, "ROOT"); //for test purpose
  		mapAdminCuratorList.put(Utils.MRS_MODULE_ADMIN_COMMON_USERID, "Administrator");

  		return mapAdminCuratorList;
  	}
	
	public static List<UserAccount> getRegisteredAdminAndCuratorUserAccounts(VitroRequest vreq){
  		List<UserAccount> accounts = null;
  		UserAccountsListPage page = new UserAccountsListPage(vreq);
  		accounts = page.getAdminAndCuratorAccountList();
  		
  		return accounts;
	}
	
	public static List<Individual> getPublishedOpenAccessRecords(VitroRequest vreq, int page, int pageSize) throws Exception{
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			
			// "Assign to review" is the person who published the record. but better to get that info from otherRecordInfo.
			String filterQueryStr =  VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE ;
			
			query.setFilterQueries(filterQueryStr);
			
			int startRow = (page-1) * pageSize ;            
	        query.setStart( startRow ).setRows( pageSize );
	         
			query.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED, SolrQuery.ORDER.asc);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getIndividuals();
		}catch (Exception ex){
            throw ex;
        }  
	}
	
	public static int getPublishedForOpenAcessRecordsCount(VitroRequest vreq) throws Exception{
		try{
			String queryText = VitroSearchTermNames.RDFTYPE + ":\"http://www.qut.edu.au/ontologies/vivoqut#allRecordsType\" ";
			IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
			ServletContext context = vreq.getSession().getServletContext();
			
			SolrQuery query = new SolrQuery(queryText);
			String filterQueryStr = VitroSearchTermNames.PUBLISH_RECORD + ":" + Utils.RECORD_STATUS_PUBLISH_OPEN_ACCESS_VALUE;
			query.setFilterQueries(filterQueryStr);
			IndividualListQueryResults results = IndividualListQueryResults.runQuery(query, indDao, context);
			
			return results.getHitCount();
		}catch (Exception ex){
			throw ex;
        }  
	}
	
	public static String getRecordObjectPropertyValue(Individual ind, String propertyName, String fieldName){
		List<ObjectPropertyStatement> listStmnts = ind.getObjectPropertyStatements(propertyName);
		for (int j =0; j < listStmnts.size(); j++){ // there can be one entry
			ObjectPropertyStatement stmt = listStmnts.get(j);
			if (stmt != null){
				Individual indRecord = stmt.getObject();
				return indRecord.getDataValue(fieldName);
			}
		}
		
		return null;
	}
	
	// LIRDF-83
	public static String getPropertyDataValue(VitroRequest vreq, OntModel ontModel, String URL, Property pParent, Property pField){
		// pParent = http://www.qut.edu.au/ontologies/vivoqut#assignForReviewOrComplete
		// pField = http://www.qut.edu.au/ontologies/vivoqut#infoType1
			
		String value = null;

		try {
            Resource res = ontModel.getResource(URL);
           	if (res != null){
           	 Statement stmnt1 = res.getProperty(pParent);
             Resource robj = null;
             if (stmnt1 != null){
             	robj = (stmnt1.getObject()).asResource();
              	if (robj != null) {
              		Statement stmntVal = robj.getProperty(pField);
              		if (stmntVal != null){
              			value = stmntVal.getString();
              		}
              	}
              }
            }
		}catch (Exception e){
			return null;
		}
		
		return value;
	}
	
	// LIRDF-83
	public static boolean hasPermisionToPublishRecord(Individual ind, VitroRequest vreq){

		UserAccount u = LoginStatusBean.getCurrentUser(vreq);
		
		if (u == null){return false;}	// When no one logged in.
		if (u.isRootUser()) return true;
		
		String userID = u.getExternalAuthId();
		
		RoleLevel loggedInUserRole = RoleLevel.getRoleFromLoginStatus(vreq);
		if (loggedInUserRole  == RoleLevel.EDITOR){ 
			return false;
		}else{	// DB_ADMIN, CURATOR, ROOT
			String recordCreatedByUserID = getRecordObjectPropertyValue(ind, Utils.PROPERTY_RECORD_OWNERSHIP_INFO, Utils.PROPERTY_FREE_TEXT_VALUE_2);
			if (recordCreatedByUserID != null && userID.equals(recordCreatedByUserID)){
				return true;
			}
		
			OntModelSelector ontModelSelector = vreq.getOntModelSelector();
			OntModel ontModel = ontModelSelector.getFullModel();
			Property pParent = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "assignForReviewOrComplete");
			Property pField = ontModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
			
			String recordAssignedForReview = getPropertyDataValue(vreq, ontModel, ind.getURI(), pParent, pField);
			if (recordAssignedForReview != null && userID.equals(recordAssignedForReview)){
				return true;
			}
		}
		
		return false;
	}
	
	// LIBRDF-103
	public static String getIndividualTitle(String URL, VitroRequest vreq){

		ServletContext context = vreq.getSession().getServletContext();
		OntModel model = ModelContext.getJenaOntModel(context);
		
		Resource r = model.getResource(URL);
		if (r == null) return "";
		
		com.hp.hpl.jena.rdf.model.Property pLbl = model.getProperty("http://www.w3.org/2000/01/rdf-schema#", "label");
		com.hp.hpl.jena.rdf.model.Statement stmtTitle = r.getProperty(pLbl);
		RDFNode object = stmtTitle.getObject();
		
		String title = "";
		if (object == null){
			return title;
		}
		
		if (object.isLiteral()){
			Literal titleLiteral = object.asLiteral();
			title = titleLiteral.getString();
		}else{
			title = object.toString();
		}
		
		return title;
	}
}