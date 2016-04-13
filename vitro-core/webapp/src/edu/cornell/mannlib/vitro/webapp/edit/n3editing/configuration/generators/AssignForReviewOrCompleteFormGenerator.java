
/*
Copyright (c) 2012, QUT University
*/

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
import edu.cornell.mannlib.vitro.webapp.utils.SparqlQueryRunner;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;


public class AssignForReviewOrCompleteFormGenerator extends VivoBaseGenerator implements EditConfigurationGenerator{     

	private static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	
	final static String assignForReviewUserIDPred = vivoCore + "infoType1";	
	final static String assignByUserIDPred = vivoCore + "infoType2";
	final static String assignByUsercommentsPred = vivoCore + "freeTextValue1";
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
		 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("assignForReviewOrCompleteWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
       
        conf.setN3Optional(Arrays.asList( assignForReviewUserIDAssertion,  assignByUserIDAssertion, assignByUsercommentsAssertion));
       
        
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtAssignForReviewUserID", "txtAssignByUserID", "txtAssignByUsercomments"));
        
        conf.addSparqlForExistingLiteral("txtAssignForReviewUserID", assignForReviewUserIDQuery);
        conf.addSparqlForExistingLiteral("txtAssignByUserID", assignByUserIDQuery);
        conf.addSparqlForExistingLiteral("txtAssignByUsercomments", assignByUsercommentsQuery);
       
     
        
        conf.addField( new FieldVTwo().
                setName("txtAssignForReviewUserID").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
          
        conf.addField( new FieldVTwo().
                setName("txtAssignByUserID").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
        
        conf.addField( new FieldVTwo().
                setName("txtAssignByUsercomments").
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
	
	final static String assignForReviewUserIDQuery  =  
            "SELECT ?existingAssignForReviewUserID WHERE {\n"+
            "?obj <"+ assignForReviewUserIDPred +"> ?existingAssignForReviewUserID . }";
	
	final static String assignByUsercommentsQuery  =  
            "SELECT ?existingAssignByUsercomments WHERE {\n"+
            "?obj <"+ assignByUsercommentsPred +"> ?existingAssignByUsercomments . }";
	
	final static String assignByUserIDQuery  =  
            "SELECT ?existingAssignByUserID WHERE {\n"+
            "?obj <"+ assignByUserIDPred +"> ?existingAssignByUserID . }";
	
			
    private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:assignForReviewOrComplete  ?obj .\n"+            
            "?obj  a core:customProperty ";
		
    final static String assignForReviewUserIDAssertion =
            "?obj <" + assignForReviewUserIDPred + "> ?txtAssignForReviewUserID .";
    
    final static String assignByUsercommentsAssertion =
            "?obj <" + assignByUsercommentsPred + "> ?txtAssignByUsercomments .";
    
 
    final static String assignByUserIDAssertion =
            "?obj <" + assignByUserIDPred + "> ?txtAssignByUserID .";
    
    
    public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("selectionTypes", Utils.getRegisteredAdminAndCuratorList(vreq));
		formSpecificData.put("assignedByusersList", Utils.getRegisteredAdminAndCuratorList(vreq));
		formSpecificData.put("displayString", "Assign for review/complete");
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
