/*
Copyright (c) 2012, QUT University
*/
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

public abstract class NamePartsWebFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	
	/*final static String relatedInfoTypePred = vivoCore + "infoType1";
	final static String identifierTypePred = vivoCore + "infoType2";
	final static String identifierValuePred = vivoCore + "freeTextValue1";*/
	
	final static String nameTypePred = vivoCore + "infoType1";
	final static String namePartTypePred = vivoCore + "infoType2";
	final static String namePartValuePred = vivoCore + "freeTextValue1";
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	//final static String doiForDataSet = vivoCore + "dataSetForDoi" ; //predicate
	
	abstract String getn3ForNewObject();
	abstract List<String> getFormSpecificData(String sSection);
	abstract String getDisplayString();
	abstract String getPropertyName();
	abstract String getTextAreaSize();
	
	 
    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("namePartsWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( getn3ForNewObject() ) );
        conf.setN3Optional(Arrays.asList( 
        		nameTypeAssertion, namePartTypeAssertion, identifierValueAssertion));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtNameType","txtNamePartType", "txtNamePartValue"));
        
        conf.addSparqlForExistingLiteral("txtNameType", nameTypeQuery);
        conf.addSparqlForExistingLiteral("txtNamePartType", namePartTypeQuery);
        conf.addSparqlForExistingLiteral("txtNamePartValue", namePartValueQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtNameType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
       conf.addField( new FieldVTwo().
                setName("txtNamePartType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtNamePartValue").
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
    
    final static String nameTypeQuery  =  
            "SELECT ?existingNameType WHERE {\n"+
            "?obj <"+ nameTypePred +"> ?existingNameType . }";
    
    final static String namePartTypeQuery  =  
            "SELECT ?existingNamePartType WHERE {\n"+
            "?obj <"+ namePartTypePred +"> ?existingNamePartType . }";
    
    final static String namePartValueQuery  =  
            "SELECT ?existingNamePartValue WHERE {\n"+
            "?obj <"+ namePartValuePred +"> ?existingNamePartValue . }";
    

    final static String nameTypeAssertion =
            "?obj <" + nameTypePred + "> ?txtNameType .";
    
    final static String namePartTypeAssertion  =      
            "?obj <"+ namePartTypePred +"> ?txtNamePartType .";
    
    final static String identifierValueAssertion  =      
            "?obj <"+ namePartValuePred +"> ?txtNamePartValue .";

  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("nameTypes", getFormSpecificData("[NAME_YPE]"));
		formSpecificData.put("namePartsTypes", getFormSpecificData("[NAME_PART_TYPE]"));
		formSpecificData.put("displayString", getDisplayString());
		formSpecificData.put("propertyName", getPropertyName());
		formSpecificData.put("textAreaSize", getTextAreaSize());
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
