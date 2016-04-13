/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qut.crmm.model.jena.JenaSDBConnection;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.DependentResourceDeleteJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfigurationConstants;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.controller.ProcessRdfFormController.Utilities;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexBuilder;


/**
 * The goal of this class is to provide processing from 
 * an EditConfiguration and an EditSubmission to produce
 * a set of additions and retractions.
 * 
 * When working with the default object property form or the 
 * default data property from, the way to avoid having 
 * any optional N3 is to originally configure the 
 * configuration.setN3Optional() to be empty. 
 */
public class ProcessRdfForm {               
       
    private NewURIMaker newURIMaker;
    private EditN3GeneratorVTwo populator;
    
    private Map<String,String> urisForNewResources = null; 
    
    private String defaultNameSpace = "";
    private Model writeModel = null;
    private String externalAuthID = "";
    private String fullName = "";
    private DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    OntModelSelector ontModelSelector = null;
    
   /**
     * Construct the ProcessRdfForm object. 
     */
    public ProcessRdfForm( EditConfigurationVTwo config, NewURIMaker newURIMaker){
        this.newURIMaker = newURIMaker;
        this.populator = config.getN3Generator();
    }
    
    public void setContextAndDefaultNS(String ns){ //CR 01: qut on 15//11/2012
    	defaultNameSpace = ns;
    }
    
    public void setExternalAuthId(String AuthID, String fullName){ //CR 01: qut on 15//11/2012
    	this.externalAuthID = AuthID;
    	this.fullName = fullName;
    }

	public void setWriteModel(Model writeModel) { //CR 01: qut on 15//11/2012
		this.writeModel = writeModel;
	}
	
	public void setOntModelSelector(OntModelSelector ontModelSelector) { //CR 01: qut on 17//03/2014
		this.ontModelSelector = ontModelSelector;
	}

	/**
     * This detects if this is an edit of an existing statement or an edit
     * to create a new statement or set of statements. Then the correct
     * method will be called to convert the EditConfiguration and EditSubmission
     * into a set of additions and retractions.
     * 
     * This will handle data property editing, object property editing 
     * and general editing.
     * 
     * The submission object will be modified to have its entityToReturnTo string
     * substituted with the values from the processing.
     * 
     * @throws Exception May throw an exception if Required N3 does not
     * parse correctly.
     */
    public AdditionsAndRetractions  process(
            EditConfigurationVTwo configuration,
            MultiValueEditSubmission submission) 
    throws Exception{  
        log.debug("configuration:\n" + configuration.toString());
        log.debug("submission:\n" + submission.toString());
        
        applyEditSubmissionPreprocessors( configuration, submission );
        
        AdditionsAndRetractions changes;
        if( configuration.isUpdate() ){ //edit an existing statement
           changes = editExistingStatements(configuration, submission); 
           //CR :050
           if ((writeModel != null) && (configuration.getPredicateUri() != null)){	// if predUri null means this is not a record update.
	           	Resource subjRes = writeModel.getResource(configuration.getSubjectUri());
	           	updateDateRecordModified(subjRes);
	
	           	if (ontModelSelector != null){	// if predicate has a inverse then change the inverse obj's dateRecordModified field
	           		OntModel tboxModel = ontModelSelector.getTBoxModel();
	               	Property pred = tboxModel.getProperty(configuration.getPredicateUri());
	               	OntProperty invPred = null;                        
	                if (pred.canAs(OntProperty.class)) {
	                	invPred = ((OntProperty)pred.as(OntProperty.class)).getInverse();
	                }
	                  
	                if (invPred != null) {
	             		Map<String, List<String>> currentUris = configuration.getUrisInScope(); // take the current object uri which is going to remove from scope.
	             		if (currentUris != null){
	             			List<String> values = currentUris.get(configuration.getVarNameForObject());  
	                		if ((values != null) && (values.size() >= 1)){
	                			String objUri = values.get(0);
	                			Resource objRes = writeModel.getResource(objUri);
	                			if (objRes != null){
	                 				updateDateRecordModified(objRes);
	                 			}
	                 		}
	             		}
	             		
	             		Map<String, List<String>> newUris = submission.getUrisFromForm(); // take the new object uri which is going to add from form.
              			if (newUris != null){
              				List<String> values = newUris.get(configuration.getVarNameForObject());  
                  			if ((values != null) && (values.size() >= 1)){
                  				String objUri = values.get(0);
                  				Resource objRes = writeModel.getResource(objUri);
                  				if (objRes != null){
                  					updateDateRecordModified(objRes);
                  				}
                  			}
              			}
	                }
	           	}
            }
        } else {
            changes = createNewStatements(configuration, submission );

            //CR 01: qut on 15//11/2012 - Implement draft functionality
            //--------------------------------------------------------
            
            String sTemplate = configuration.getTemplate(); // ex: newIndividualForm.ftl, oneFieldOptionsWebForm.ftl..etc
            if (sTemplate == null){}
            else if (sTemplate.equals("newIndividualForm.ftl")){	// add new individual - this is a hack.
            	 String entityUri = submission.getEntityToReturnTo(); // <http://localhost:8080/vivo/individual/n8079>
            	 entityUri = entityUri.replace("<", "");
            	 entityUri = entityUri.replace(">", "");
            	 Model addition = changes.getAdditions();
            	 Resource r = addition.getResource(entityUri.trim());
            	 
            	 if (r != null){

                     Resource thing = addition.getResource("http://www.w3.org/2002/07/owl#Thing");
                     Resource customProperty = addition.getResource("http://www.qut.edu.au/ontologies/vivoqut#customProperty");
                
                     Property pType = addition.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
                     Property pValue = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
                     Property pValue1 = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue2");
                     Property pInfoType = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "infoType1");
                     
                     Property plocalKeyCounter = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "localKeyCounter");
                     
                     Property plocalKey = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "localKey");
                     Property pdateRecordCreated = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "dateRecordCreated");
                     Property pdateRecordModified = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "dateRecordModified");
                     Property pPublishRecord = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "publishRecord");
                     //Property pisApprovedByOoR = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "isApprovedByOoR");
                     //Property pisApprovedbyLibrary = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "isApprovedbyLibrary");
                     Property precordCreatedBy = addition.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "recordInitiallyCreatedBy");
                     
                     
                     String uri = newURIMaker.getUnusedNewURI(defaultNameSpace);  
                     Resource draftResource = addition.createResource(uri);
                     draftResource.addProperty(pType, thing);
                     draftResource.addProperty(pType, customProperty);
                     Literal litrl1 = writeModel.createTypedLiteral(new String(Utils.RECORD_STATUS_DRAFT_VALUE));
                     draftResource.addProperty(pValue, litrl1);
                     r.addProperty(pPublishRecord, draftResource);

                     String nextlocalKey = getNextlocalKey(plocalKeyCounter);
                     if ((nextlocalKey != "") && (nextlocalKey != null)){
                    	 nextlocalKey = "10378.3/8085/1018." + nextlocalKey;
                    	 String uri1 = newURIMaker.getUnusedNewURI(defaultNameSpace);  
                         Resource localKeyResource = addition.createResource(uri1);
                         localKeyResource.addProperty(pType, thing);
                         localKeyResource.addProperty(pType, customProperty);
                         Literal litrl2 = writeModel.createTypedLiteral(new String("local"));
                         localKeyResource.addProperty(pInfoType, litrl2);
                         Literal litrl3 = writeModel.createTypedLiteral(new String(nextlocalKey));
                         localKeyResource.addProperty(pValue, litrl3);
                         
                         r.addProperty(plocalKey, localKeyResource); 
                     }else{
                    	 throw new Exception("Failed to find the local key.");
                     }
                     
         			 Date value = Calendar.getInstance().getTime();
         		     String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
         
         			 String uri4 = newURIMaker.getUnusedNewURI(defaultNameSpace);  
                     Resource dateCreatedResource = addition.createResource(uri4);
                     dateCreatedResource.addProperty(pType, thing);
                     dateCreatedResource.addProperty(pType, customProperty);
                     Literal litrl4 = writeModel.createTypedLiteral(new String(formattedDateStr));
                     dateCreatedResource.addProperty(pValue, litrl4);
                     r.addProperty(pdateRecordCreated, dateCreatedResource);
                     
                     String uri6 = newURIMaker.getUnusedNewURI(defaultNameSpace);  
                     Resource dateModifiedResource = addition.createResource(uri6);
                     dateModifiedResource.addProperty(pType, thing);
                     dateModifiedResource.addProperty(pType, customProperty);
                     dateModifiedResource.addProperty(pValue, litrl4);
                     r.addProperty(pdateRecordModified, dateModifiedResource);
                     
         			 /*String uri2 = newURIMaker.getUnusedNewURI(defaultNameSpace); 
         			 Resource OoRResource = addition.createResource(uri2);
         			 OoRResource.addProperty(pType, thing);
         			 OoRResource.addProperty(pType, customProperty);
         			 Literal litrl5 = writeModel.createTypedLiteral(new String("No"));
         			 OoRResource.addProperty(pValue, litrl5);
         			 r.addProperty(pisApprovedByOoR, OoRResource);
         			 
         			 String uri3 = newURIMaker.getUnusedNewURI(defaultNameSpace); 
        			 Resource LibraryResource = addition.createResource(uri3);
        			 LibraryResource.addProperty(pType, thing);
        			 LibraryResource.addProperty(pType, customProperty);
        			 Literal litrl6 = writeModel.createTypedLiteral(new String("No"));
        			 LibraryResource.addProperty(pValue, litrl6);
        			 r.addProperty(pisApprovedbyLibrary, LibraryResource);*/
        			 
        			 
        			 String uri5 = newURIMaker.getUnusedNewURI(defaultNameSpace); 
        			 Resource recordCreatedByResource = addition.createResource(uri5);
        			 recordCreatedByResource.addProperty(pType, thing);
        			 recordCreatedByResource.addProperty(pType, customProperty);
        			 
        			 Literal litrl7 = writeModel.createTypedLiteral(new String(fullName)); // full name
        			 recordCreatedByResource.addProperty(pValue, litrl7);
        			 
        			 Literal litrl8 = writeModel.createTypedLiteral(new String(externalAuthID)); // userID
        			 recordCreatedByResource.addProperty(pValue1, litrl8);

        			 r.addProperty(precordCreatedBy, recordCreatedByResource);
            	 }
             }else{ //add a new statement
            	 //CR :050
            	 if ((writeModel != null) && (configuration.getPredicateUri() != null)){
                	Resource subjRes = writeModel.getResource(configuration.getSubjectUri());
                	updateDateRecordModified(subjRes);
 	
                	if (ontModelSelector != null){	// if predicate has a inverse then change the inverse obj's dateRecordModified field
                		OntModel tboxModel = ontModelSelector.getTBoxModel();
                    	Property pred = tboxModel.getProperty(configuration.getPredicateUri());
                    	OntProperty invPred = null;                        
                        if (pred.canAs(OntProperty.class)) {
                        	invPred = ((OntProperty)pred.as(OntProperty.class)).getInverse();
                        }
                       
                  		if (invPred != null) {
                  			Map<String, List<String>> multiUris = submission.getUrisFromForm(); // when adding
                  			if (multiUris != null){
                  				List<String> values = multiUris.get(configuration.getVarNameForObject());  //ex: obj, objectVar
                      			if ((values != null) && (values.size() >= 1)){
                      				String objUri = values.get(0);
                      				Resource objRes = writeModel.getResource(objUri);
                      				if (objRes != null){
                      					updateDateRecordModified(objRes);
                      				}
                      			}
                  			}
                  		}
                	}
                 }
             }
            //---------------------------------------------------------
        }      
        
        changes = getMinimalChanges(changes);      
        logChanges( configuration, changes);        
        
        return changes;
    }
    
    //CR :050
    public void updateDateRecordModified(Resource subjRes) throws InsertException  {

    	if (subjRes == null) return;
    	
    	Resource thing = writeModel.getResource("http://www.w3.org/2002/07/owl#Thing");
        Resource customProperty = writeModel.getResource("http://www.qut.edu.au/ontologies/vivoqut#customProperty");
   
        Property pType = writeModel.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
        Property pValue = writeModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
        Property pdateRecordModified = writeModel.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "dateRecordModified");
	
        Lock lock = writeModel.getLock();
		lock.enterCriticalSection(Lock.WRITE);
		
		Date value = Calendar.getInstance().getTime();
	    String formattedDateStr = (value == null) ? null : xsdDateTimeFormat.format(value);
	    Literal litrl = writeModel.createTypedLiteral(new String(formattedDateStr));
        
        Statement stmnt1 = subjRes.getProperty(pdateRecordModified);
        Resource robj = null;
		if (stmnt1 != null){
			robj = (stmnt1.getObject()).asResource();
			if (robj != null) {	// edit
				robj.removeAll(pValue);
				robj.addProperty(pValue, litrl);
			}   	
		}
		
		/*if ((stmnt1 == null) || (robj == null)){	// add
			String uri = newURIMaker.getUnusedNewURI(defaultNameSpace);
			Resource dateModifiedResource = writeModel.createResource(uri);
			dateModifiedResource.addProperty(pType, thing);
			dateModifiedResource.addProperty(pType, customProperty);
	        dateModifiedResource.addProperty(pValue, litrl);
	        subjRes.addProperty(pdateRecordModified, dateModifiedResource);
		}*/
		
		lock.leaveCriticalSection();
    }
    
    private String getNextlocalKey(Property plocalKeyCounter){	//CR 01: qut on 15//11/2012	- change this method to acces Utils.java
    	String nextLocalKey = "";
    	
    	String lkcUri = defaultNameSpace + "lkc";
    	
    	if (writeModel == null){return nextLocalKey;}
    	else{
    		
    		Lock lock = writeModel.getLock();
    		lock.enterCriticalSection(Lock.WRITE);
    		
    		Resource r = writeModel.getResource(lkcUri);
    		if (r != null){
    	       	Statement stmnt1 = r.getProperty(plocalKeyCounter);
    	    	if (stmnt1 != null) {
    	    		String str = stmnt1.getString();
    	    		long nextLK = Long.valueOf(str);
    	    		nextLK++;
    	    		nextLocalKey =  String.valueOf(nextLK);
    	    	    
    	    		r.removeProperties();
    	    		r.addProperty(plocalKeyCounter, nextLocalKey);
    	    	}
    	    }
    		
    		lock.leaveCriticalSection();
    	}
    	
    	return nextLocalKey;
    }
    
    /** 
     * Processes an EditConfiguration for to create a new statement or a 
     * set of new statements.
     *  
     * This will handle data property editing, object property editing 
     * and general editing.
     * 
     * When working with the default object property form or the 
     * default data property from, the way to avoid having 
     * any optional N3 is to originally configure the 
     * configuration.setN3Optional() to be empty. 
     * 
     * @throws Exception May throw an exception if the required N3 
     * does not parse.
     */          
    private AdditionsAndRetractions createNewStatements(
            EditConfigurationVTwo configuration, 
            MultiValueEditSubmission submission) throws Exception {                
        log.debug("in createNewStatements()" );        
        
        //getN3Required and getN3Optional will return copies of the 
        //N3 String Lists so that this code will not modify the originals.
        List<String> requiredN3 = configuration.getN3Required();
        List<String> optionalN3 = configuration.getN3Optional();                        
        
        /* substitute in the form values and existing values */
        subInValuesToN3( configuration, submission, requiredN3, optionalN3, null , null);
                   
        /* parse N3 to RDF Models, No retractions since all of the statements are new. */         
        return parseN3ToChange(requiredN3, optionalN3, null, null);        
    }

    /* for a list of N3 strings, substitute in the subject, predicate and object URIs 
     * from the EditConfiguration. */
    protected void substituteInSubPredObjURIs(
            EditConfigurationVTwo configuration,
            List<String>... n3StrLists){                
        Map<String, String> valueMap = getSubPedObjVarMap(configuration);
        for (List<String> n3s : n3StrLists) {
            populator.subInUris(valueMap, n3s);
        }
    }

    /**
     * Process an EditConfiguration to edit a set of existing statements.
     * 
     * This will handle data property editing, object property editing and
     * general editing.
     * 
     * No longer checking if field has changed, because assertions and
     * retractions are mutually diff'ed before statements are added to or
     * removed from the model. The explicit change check can cause problems in
     * more complex setups, like the automatic form building in DataStaR.
     */
    protected AdditionsAndRetractions editExistingStatements(
            EditConfigurationVTwo editConfig,
            MultiValueEditSubmission submission) throws Exception {

        log.debug("editing an existing resource: " + editConfig.getObject() );        

        //getN3Required and getN3Optional will return copies of the 
        //N3 String Lists so that this code will not modify the originals.
        List<String> N3RequiredAssert = editConfig.getN3Required();        
        List<String> N3OptionalAssert = editConfig.getN3Optional();
        List<String> N3RequiredRetract = editConfig.getN3Required();        
        List<String> N3OptionalRetract = editConfig.getN3Optional();

        subInValuesToN3(editConfig, submission, 
                N3RequiredAssert, N3OptionalAssert, 
                N3RequiredRetract, N3OptionalRetract);
                                  
        return parseN3ToChange( 
                N3RequiredAssert,N3OptionalAssert, 
                N3RequiredRetract, N3OptionalRetract);        
    }    
    
    @SuppressWarnings("unchecked")
    protected void subInValuesToN3(
            EditConfigurationVTwo editConfig, MultiValueEditSubmission submission, 
            List<String> requiredAsserts, List<String> optionalAsserts,
            List<String> requiredRetracts, List<String> optionalRetracts ) throws InsertException{
        
        //need to substitute into the return to URL becase it may need new resource URIs
        List<String> URLToReturnTo = Arrays.asList(submission.getEntityToReturnTo());
        
        /* *********** Check if new resource needs to be forcibly created ******** */
        urisForNewResources = URIsForNewRsources(editConfig, newURIMaker);
        substituteInForcedNewURIs(urisForNewResources, submission.getUrisFromForm(), requiredAsserts, optionalAsserts, URLToReturnTo);
        logSubstitue( "Added form URIs that required new URIs", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);

        
        /* ********** Form submission URIs ********* */
        substituteInMultiURIs(submission.getUrisFromForm(), requiredAsserts, optionalAsserts, URLToReturnTo);
        logSubstitue( "Added form URIs", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);
        //Retractions does NOT get values from form.
        
        /* ******** Form submission Literals *********** */
        substituteInMultiLiterals( submission.getLiteralsFromForm(), requiredAsserts, optionalAsserts, URLToReturnTo);
        logSubstitue( "Added form Literals", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);
        //Retractions does NOT get values from form.                               
        
    
        
        /* *********** Add subject, object and predicate ******** */
        substituteInSubPredObjURIs(editConfig, requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts, URLToReturnTo);
        logSubstitue( "Added sub, pred and obj URIs", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);
        
     
        /* ********* Existing URIs and Literals ********** */
        substituteInMultiURIs(editConfig.getUrisInScope(), 
                requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts, URLToReturnTo);
        logSubstitue( "Added existing URIs", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);
        
        substituteInMultiLiterals(editConfig.getLiteralsInScope(), 
                requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts, URLToReturnTo);
        logSubstitue( "Added existing Literals", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);
        //Both Assertions and Retractions get existing values.
        
        /* ************  Edits may need new resources *********** */
        //moved this up?
        //urisForNewResources = URIsForNewRsources(editConfig, newURIMaker);
        substituteInURIs( urisForNewResources, requiredAsserts, optionalAsserts, URLToReturnTo);
        logSubstitue( "Added URIs for new Resources", requiredAsserts, optionalAsserts, requiredRetracts, optionalRetracts);
        // Only Assertions get new resources.       
        
        submission.setEntityToReturnTo(URLToReturnTo.get(0));
    }



	//TODO: maybe move this to utils or contorller?
    public static AdditionsAndRetractions addDependentDeletes( AdditionsAndRetractions changes, Model queryModel){
        //Add retractions for dependent resource delete if that is configured and 
        //if there are any dependent resources.                     
        Model depResRetractions = 
            DependentResourceDeleteJena
            .getDependentResourceDeleteForChange(changes.getAdditions(),changes.getRetractions(),queryModel);                

        changes.getRetractions().add(depResRetractions);        
        return changes; 
    }
       
    public static void applyChangesToWriteModel(
            AdditionsAndRetractions changes, 
            Model queryModel, Model writeModel, String editorUri) {                             
        //side effect: modify the write model with the changes   
   
        Lock lock = null;
        try{
            lock =  writeModel.getLock();
            lock.enterCriticalSection(Lock.WRITE);
            if( writeModel instanceof OntModel){
                ((OntModel)writeModel).getBaseModel().notifyEvent(new EditEvent(editorUri,true));    
            }               
            writeModel.add( changes.getAdditions() );
            writeModel.remove( changes.getRetractions() );
        }catch(Throwable t){
            log.error("error adding edit change n3required model to in memory model \n"+ t.getMessage() );
        }finally{
            if( writeModel instanceof OntModel){
                ((OntModel)writeModel).getBaseModel().notifyEvent(new EditEvent(editorUri,false));
            }
            lock.leaveCriticalSection();
        }          
    }
      
    // BUG:025
    public static void applyChangesToSolrIndex(EditConfigurationVTwo configuration, VitroRequest vreq) { 

    	String predicateUri = configuration.getPredicateUri();
    	if (predicateUri != null){
    		if ((predicateUri.equals(Utils.PROPERTY_RECORD_STATUS)) || (predicateUri.equals(Utils.PROPERTY_CONTACT_INFO)) || 
    				(predicateUri.equals(Utils.PROPERTY_RECORD_OWNERSHIP_INFO)) || (predicateUri.equals(Utils.PROPERTY_RECORD_ASSIGN_TO_REVIEW))){
    			String resource = configuration.getSubjectUri();
    			String obj = configuration.getObject();
    			
    			if (obj == null){
    				return; // this can happen when a new entry is created. it will happen only for "Utils.PROPERTY_RECORD_ASSIGN_TO_REVIEW" property.
    			}
    					
    			Model m = ModelFactory.createDefaultModel();
    			Resource r = m.createResource(resource);
    			Property p = m.createProperty(predicateUri);
    			Resource objRes = m.createProperty(obj);
    			Statement stmt = m.createStatement(r, p, objRes);
    					
    			if (stmt != null){
    				IndexBuilder builder = (IndexBuilder)vreq.getSession().getServletContext().getAttribute(IndexBuilder.class.getName()); 
    				if (builder != null){
    					builder.addToChanged(stmt);
    					builder.doUpdateIndex();
    				}
    			}
    		}
    	}
    }
    protected AdditionsAndRetractions parseN3ToChange( 
            List<String> requiredAdds, List<String> optionalAdds,
            List<String> requiredDels, List<String> optionalDels) throws Exception{
        
        List<Model> adds = parseN3ToRDF(requiredAdds, REQUIRED);
        adds.addAll( parseN3ToRDF(optionalAdds, OPTIONAL));
        
        List<Model> retracts = new ArrayList<Model>();
        if( requiredDels != null && optionalDels != null ){
            retracts.addAll( parseN3ToRDF(requiredDels, REQUIRED) );
            retracts.addAll( parseN3ToRDF(optionalDels, OPTIONAL) );
        }
        
        return new AdditionsAndRetractions(adds,retracts);
    }
    
    /**
     * Parse the n3Strings to a List of RDF Model objects.
     * 
     * @param n3Strings
     * @param parseType if OPTIONAL, then don't throw exceptions on errors
     * If REQUIRED, then throw exceptions on errors.
     * @throws Exception 
     */
    //protected static List<Model> parseN3ToRDF(
    public static List<Model> parseN3ToRDF(
            List<String> n3Strings, N3ParseType parseType ) throws Exception {
       List<String> errorMessages = new ArrayList<String>();
       
        List<Model> rdfModels = new ArrayList<Model>();
        for(String n3 : n3Strings){
            try{
                Model model = ModelFactory.createDefaultModel();
                StringReader reader = new StringReader(n3);
                model.read(reader, "", "N3");
                rdfModels.add( model );
            }catch(Throwable t){
                errorMessages.add(t.getMessage() + "\nN3: \n" + n3 + "\n");
            }
        }
        
        String errors = "";
        for( String errorMsg : errorMessages){
            errors += errorMsg + '\n';
        }
        
       if( !errorMessages.isEmpty() ){
           if( REQUIRED.equals(parseType)  ){        
               throw new Exception("Errors processing required N3. The EditConfiguration should " +
                    "be setup so that if a submission passes validation, there will not be errors " +
                    "in the required N3.\n" +  errors );
           }else if( OPTIONAL.equals(parseType) ){
               log.debug("Some Optional N3 did not parse, if a optional N3 does not parse it " +
                    "will be ignored.  This allows optional parts of a form submission to " +
                    "remain unfilled out and then the optional N3 does not get values subsituted in from" +
                    "the form submission values.  It may also be the case that there are unintentional " +
                    "syntax errors the optional N3." );
               log.debug( errors );                            
           }
       }
              
       return rdfModels;       
    }  

	/**
	 * TODO: bdc34: what does this check? Why?
	 */
	public static boolean isGenerateModelFromField(
	        String fieldName, 
	        EditConfigurationVTwo configuration, MultiValueEditSubmission submission) {
//		if(Utilities.isObjectProperty(configuration, vreq)) {
//			return true;
//		}
//		if(Utilities.isDataProperty(configuration, vreq)) {
//			if(Utilities.hasFieldChanged(fieldName, configuration, submission)) {
//				return true;
//			}
//		}
		return false;
	}	
		    
    protected void logSubstitue(String msg, List<String> requiredAsserts,
            List<String> optionalAsserts, List<String> requiredRetracts,
            List<String> optionalRetracts) {
        if( !log.isDebugEnabled() ) return;
        log.debug(msg);
        logSubstitueN3( msg, requiredAsserts, "required assertions");
        logSubstitueN3( msg, optionalAsserts, "optional assertions");
        logSubstitueN3( msg, requiredRetracts, "required retractions");
        logSubstitueN3( msg, optionalRetracts, "optional retractions");        
    }
    
    private void logSubstitueN3(String msg, List<String> n3, String label){
        if( n3 == null || n3.size() == 0) return;        
        String out = label + ":\n";
        for( String str : n3 ){
            out += "    " + str + "\n";
        }                      
        log.debug(out);
    }
    
	private static Map<String, String> getSubPedObjVarMap(
            EditConfigurationVTwo configuration) 
    {
        Map<String,String> varToValue = new HashMap<String,String>();
        
        String varNameForSub = configuration.getVarNameForSubject();
        if( varNameForSub != null && ! varNameForSub.isEmpty()){            
            varToValue.put( varNameForSub,configuration.getSubjectUri());                 
        }else{
            log.debug("no varNameForSubject found in configuration");
        }
        
        String varNameForPred = configuration.getVarNameForPredicate();
        if( varNameForPred != null && ! varNameForPred.isEmpty()){            
            varToValue.put( varNameForPred,configuration.getPredicateUri());
        }else{
            log.debug("no varNameForPredicate found in configuration");
        }
        String varNameForObj = configuration.getVarNameForObject();
        if( varNameForObj != null 
        		&& ! varNameForObj.isEmpty()){            
            varToValue.put( varNameForObj, configuration.getObject());
        }else{
            log.debug("no varNameForObject found in configuration");
        }        
        
        return varToValue;        
    }
    
	protected static AdditionsAndRetractions getMinimalChanges( AdditionsAndRetractions changes ){
        //make a model with all the assertions and a model with all the 
        //retractions, do a diff on those and then only add those to the jenaOntModel
        Model allPossibleAssertions = changes.getAdditions();
        Model allPossibleRetractions = changes.getRetractions();        
        
        //find the minimal change set
        Model assertions = allPossibleAssertions.difference( allPossibleRetractions );    
        Model retractions = allPossibleRetractions.difference( allPossibleAssertions );        
        return new AdditionsAndRetractions(assertions,retractions);
    }

   private void applyEditSubmissionPreprocessors(
            EditConfigurationVTwo configuration, MultiValueEditSubmission submission) {
        List<EditSubmissionVTwoPreprocessor> preprocessors = configuration.getEditSubmissionPreprocessors();
        if(preprocessors != null) {
            for(EditSubmissionVTwoPreprocessor p: preprocessors) {
                p.preprocess(submission);
            }
        }
    }

   
   //Note this would require more analysis in context of multiple URIs
   public Map<String,String> URIsForNewRsources(
           EditConfigurationVTwo configuration, NewURIMaker newURIMaker) 
           throws InsertException {       
       Map<String,String> newResources = configuration.getNewResources();
       
       HashMap<String,String> varToNewURIs = new HashMap<String,String>();       
       for (String key : newResources.keySet()) {
           String prefix = newResources.get(key);
           String uri = newURIMaker.getUnusedNewURI(prefix);                        
           varToNewURIs.put(key, uri);  
       }   
       log.debug( "URIs for new resources: " + varToNewURIs );
       return varToNewURIs;
   }

   private static void logChanges(EditConfigurationVTwo configuration,
           AdditionsAndRetractions changes) {
       if( log.isDebugEnabled() )
           log.debug("Changes for edit " + configuration.getEditKey() + 
                   "\n" + changes.toString());
   }
   
   //private static N3ParseType OPTIONAL = N3ParseType.OPTIONAL;
   //private static N3ParseType REQUIRED = N3ParseType.REQUIRED;
   
   public static N3ParseType OPTIONAL = N3ParseType.OPTIONAL;
   public static N3ParseType REQUIRED = N3ParseType.REQUIRED;
   
   private enum N3ParseType {
       /* indicates that the n3 is optional and that a parse error should not 
        * throw an exception */         
       OPTIONAL,  
       /* indicates that the N3 is required and that a parse error should 
        * stop the processing and throw an exception. */
       REQUIRED 
   };

   private void substituteInMultiLiterals(
           Map<String, List<Literal>> literalsFromForm,
           List<String> ... n3StrLists) {
       
       for( List<String> n3s : n3StrLists){
           populator.subInMultiLiterals(literalsFromForm, n3s);
       }
   }

   private  void substituteInMultiURIs(
           Map<String, List<String>> multiUris, List<String> ... n3StrLists) {
       
       for( List<String> n3s : n3StrLists){
           if( n3s != null ){
               populator.subInMultiUris(multiUris, n3s);
           }
       }                
   }

   private void substituteInURIs(
           Map<String, String> uris, List<String> ... n3StrLists) {        
       for( List<String> n3s : n3StrLists){
           if( n3s != null ){
               populator.subInUris(uris, n3s);
           }
       }                
   }    
   
   /*
    * In some situations, an object may have an existing URI but be left blank
    * when the desired behavior is that a new object be created to replace the existing URI
    * E.g. autocomplete forms that allow editing of autocomplete fields
    */
   @SuppressWarnings("unchecked")
   private void substituteInForcedNewURIs(
			Map<String, String> urisForNewResources, Map<String, List<String>> urisFromForm,
			List<String> requiredAsserts, List<String> optionalAsserts,
			List<String> uRLToReturnTo) {
	   Map<String, List<String>> newUris = new HashMap<String, List<String>>();
	   //Check if any values from the submission have the "force new uri" value
	   //TODO: Check how to handle multiple new resource values
	   Iterator<String> keyIterator = urisFromForm.keySet().iterator();
	   while(keyIterator.hasNext()) {
		   String key = keyIterator.next().toString();
		   if(urisFromForm.get(key).contains(EditConfigurationConstants.NEW_URI_SENTINEL)) {
			   String newUri = urisForNewResources.get(key);
			   List<String> newUrisForKey = new ArrayList<String>();
			   newUrisForKey.add(newUri);
			   newUris.put(key, newUrisForKey);
		   }
	   }
	   if(newUris.size() > 0) {
		   substituteInMultiURIs(newUris, requiredAsserts, optionalAsserts, uRLToReturnTo);
	   }
	   
	}
   
      
   private static Log log = LogFactory.getLog(ProcessRdfForm.class);
}