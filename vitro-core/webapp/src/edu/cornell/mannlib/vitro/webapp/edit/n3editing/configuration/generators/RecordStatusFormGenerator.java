
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

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
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


public class RecordStatusFormGenerator extends VivoBaseGenerator implements EditConfigurationGenerator{     

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	final static String descriptionTypePred = vivoCore + "freeTextValue1";
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
		 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("recordStatusWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList(n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( descriptionTypeAssertion ));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtDescriptionType"));
        
        conf.addSparqlForExistingLiteral("txtDescriptionType", descriptionTypeQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtDescriptionType").
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
	
	final static String descriptionTypeQuery  =  
            "SELECT ?existingDescriptionType WHERE {\n"+
            "?obj <"+ descriptionTypePred +"> ?existingDescriptionType . }";
	
	private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:publishRecord  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    final static String descriptionTypeAssertion  =      
            "?obj <"+ descriptionTypePred +"> ?txtDescriptionType .";
    
    //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("selectionTypes", Utils.getFormSpecificData("[QUESTION_TYPE_RECORD_STATUS]"));
		formSpecificData.put("displayString", "Publish Record");
		formSpecificData.put("textAreaSize", "input");
		formSpecificData.put("propertyName", "publishRecord");
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
