
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


public class LinksToExternalRecordsWebFormGenerator extends VivoBaseGenerator implements EditConfigurationGenerator{          

	private static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	final static String relationshipType = vivoCore + "infoType1";
	final static String relationshipKey = vivoCore + "freeTextValue1";
	final static String relationshipURL = vivoCore + "freeTextValue2";
	final static String relatedObjectDisplayName = vivoCore + "freeTextValue3";
	

	 
    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("linksToExternalRecordsWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( 
        		relationshipTypeAssertion, relationshipKeyAssertion, relationshipURLAssertion, relatedObjectDisplayNameAssertion ));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtRelationshipType", "txtRelationshipKey", "txtRelationshipURL", "txtRelatedObjectDisplayName"));
        
        conf.addSparqlForExistingLiteral("txtRelationshipType", relationshipTypeQuery);
        conf.addSparqlForExistingLiteral("txtRelationshipKey", relationshipKeyQuery);
        conf.addSparqlForExistingLiteral("txtRelationshipURL", relationshipURLQuery);
        conf.addSparqlForExistingLiteral("txtRelatedObjectDisplayName", relatedObjectDisplayNameQuery);
        
                
       conf.addField( new FieldVTwo().
                setName("txtRelationshipType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
      
       conf.addField( new FieldVTwo().
               setName("txtRelationshipKey").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtRelationshipURL").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtRelatedObjectDisplayName").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));

        //Add validator
        //conf.addValidator(new DateTimeIntervalValidationVTwo("startField","endField"));
        conf.addValidator(new AntiXssValidation());
        
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }
    
    final static String relationshipTypeQuery  =  
            "SELECT ?existingrelationshipType WHERE {\n"+
            "?obj <"+ relationshipType +"> ?existingrelationshipType . }";
    
    final static String relationshipKeyQuery  =  
            "SELECT ?existingrelationshipKey WHERE {\n"+
            "?obj <"+ relationshipKey +"> ?existingrelationshipKey . }";
    
    final static String relationshipURLQuery  =  
            "SELECT ?existingrelationshipURL WHERE {\n"+
            "?obj <"+ relationshipURL +"> ?existingrelationshipURL . }";
    
    final static String relatedObjectDisplayNameQuery  =  
            "SELECT ?existingrelatedObjectDisplayName WHERE {\n"+
            "?obj <"+ relatedObjectDisplayName +"> ?existingrelatedObjectDisplayName . }";
    
    private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:linkToexternalRecords  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    final static String relationshipTypeAssertion =
            "?obj <" + relationshipType + "> ?txtRelationshipType .";
    
    final static String relationshipKeyAssertion  =      
            "?obj <"+ relationshipKey +"> ?txtRelationshipKey .";
    
    final static String relationshipURLAssertion  =      
            "?obj <"+ relationshipURL +"> ?txtRelationshipURL .";
    
    final static String relatedObjectDisplayNameAssertion  =      
            "?obj <"+ relatedObjectDisplayName +"> ?txtRelatedObjectDisplayName .";
    
  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("displayString", "Link to external records");
		formSpecificData.put("propertyName", "linkToexternalRecords");
		formSpecificData.put("selectionTypes", Utils.getFormSpecificData("[EXTERNAL_AFFILIATIONS_RELATIONSHIP_TYPE]"));
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	/*public EditMode getEditMode(VitroRequest vreq) {
		List<String> predicates = new ArrayList<String>();
		predicates.add(doiForDataSet);
		return EditModeUtils.getEditMode(vreq, predicates);
	}*/
	
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
