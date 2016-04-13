
/*
Copyright (c) 2012, QUT University
*/

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.admin.UserAccountsListPage;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;


public class RecordCreatedByFormGenerator extends VivoBaseGenerator implements EditConfigurationGenerator{     

	private static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	
	final static String createdByNamePred = vivoCore + "freeTextValue1";
	final static String createdByUserIDPred = vivoCore + "freeTextValue2";
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
		 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("recordCreatedByWebForm.ftl");	// we hide userID from users. Only shows RoleLvel and Name.
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( 
        		createdByNameAssertion, createdByUserIDAssertion));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtRecordCreatedByName","txtRecordCreatedByUserID"));
        
        conf.addSparqlForExistingLiteral("txtCreatedByName", createdByNameQuery);
        conf.addSparqlForExistingLiteral("txtCreatedByUserID", createdByUserIDQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtCreatedByName").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
       conf.addField( new FieldVTwo().
                setName("txtCreatedByUserID").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
	   
        //Add validator
        //conf.addValidator(new DateTimeIntervalValidationVTwo("startField","endField"));
        conf.addValidator(new AntiXssValidation());
        
        //Adding additional data, specifically edit mode
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }
	
	final static String createdByNameQuery  =  
            "SELECT ?existingCreatedByName WHERE {\n"+
            "?obj <"+ createdByNamePred +"> ?existingCreatedByName . }";
    
    final static String createdByUserIDQuery  =  
            "SELECT ?existingCreatedByUserID WHERE {\n"+
            "?obj <"+ createdByUserIDPred +"> ?existingCreatedByUserID . }";
			
    private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:recordInitiallyCreatedBy  ?obj .\n"+            
            "?obj  a core:customProperty ";
		
	
    final static String createdByNameAssertion =
            "?obj <" + createdByNamePred + "> ?txtCreatedByName .";
    
    final static String createdByUserIDAssertion  =      
            "?obj <"+ createdByUserIDPred +"> ?txtCreatedByUserID .";
    

    
    public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("selectionTypes", Utils.getFormSpecificData("[USER_ROLE_LEVELS]"));
		formSpecificData.put("adminList", Utils.getRegisteredAdminAndCuratorList(vreq));
		formSpecificData.put("displayString", "Record created by");
		formSpecificData.put("propertyName", "recordInitiallyCreatedBy");
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	public EditMode getEditMode(VitroRequest vreq) {
		//In this case, the original jsp didn't rely on FrontEndEditingUtils
		//but instead relied on whether or not the object Uri existed
		String objectUri = EditConfigurationUtils.getObjectUri(vreq);
		EditMode editMode = FrontEndEditingUtils.EditMode.ADD;
		if(objectUri != null && !objectUri.isEmpty()) {
			editMode = FrontEndEditingUtils.EditMode.EDIT;
			
		}
		return editMode;
	}
}
