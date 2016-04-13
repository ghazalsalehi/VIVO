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

public abstract class RelatedInfoWebFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	
	final static String relatedInfoTypePred = vivoCore + "infoType1";
	final static String identifierTypePred = vivoCore + "infoType2";
	final static String identifierValuePred = vivoCore + "freeTextValue1";
	final static String titleValuePred = vivoCore + "freeTextValue2";
	final static String noteValuePred = vivoCore + "freeTextValue3";
	
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	//final static String doiForDataSet = vivoCore + "dataSetForDoi" ; //predicate
	
	abstract String getn3ForNewObject();
	//abstract List<String> getFormSpecificData(String sSection);
	abstract String getDisplayString();
	abstract String getPropertyName();
	abstract String getTextAreaSize();
	 
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("relatedInfoWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( getn3ForNewObject() ) );
        conf.setN3Optional(Arrays.asList( 
        		relatedInfoTypeAssertion, identifierTypeAssertion, identifierValueAssertion, titleValueAssertion, noteValueAssertion ));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtRelatedInfoType","txtIdentifierType", "txtIdentifierValue", "txtTitleValue", "txtNoteValue"));
        
        conf.addSparqlForExistingLiteral("txtRelatedInfoType", relatedInfoTypeQuery);
        conf.addSparqlForExistingLiteral("txtIdentifierType", identifierTypeQuery);
        conf.addSparqlForExistingLiteral("txtIdentifierValue", identifierValueQuery);
        conf.addSparqlForExistingLiteral("txtTitleValue", titleValueQuery);
        conf.addSparqlForExistingLiteral("txtNoteValue", noteValueQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtRelatedInfoType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
       conf.addField( new FieldVTwo().
                setName("txtIdentifierType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtIdentifierValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtTitleValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtNoteValue").
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
    
    final static String relatedInfoTypeQuery  =  
            "SELECT ?existingRelatedInfoType WHERE {\n"+
            "?obj <"+ relatedInfoTypePred +"> ?existingRelatedInfoType . }";
    
    final static String identifierTypeQuery  =  
            "SELECT ?existingIdentifierType WHERE {\n"+
            "?obj <"+ identifierTypePred +"> ?existingIdentifierType . }";
    
    final static String identifierValueQuery  =  
            "SELECT ?existingIdentifierValue WHERE {\n"+
            "?obj <"+ identifierValuePred +"> ?existingIdentifierValue . }";
    
    final static String titleValueQuery  =  
            "SELECT ?existingTitleValue WHERE {\n"+
            "?obj <"+ titleValuePred +"> ?existingTitleValue . }";
    
    final static String noteValueQuery  =  
            "SELECT ?existingNoteValue WHERE {\n"+
            "?obj <"+ noteValuePred +"> ?existingNoteValue . }";
	
    final static String relatedInfoTypeAssertion =
            "?obj <" + relatedInfoTypePred + "> ?txtRelatedInfoType .";
    
    final static String identifierTypeAssertion  =      
            "?obj <"+ identifierTypePred +"> ?txtIdentifierType .";
    
    final static String identifierValueAssertion  =      
            "?obj <"+ identifierValuePred +"> ?txtIdentifierValue .";
    
    final static String titleValueAssertion  =      
            "?obj <"+ titleValuePred +"> ?txtTitleValue .";
    
    final static String noteValueAssertion  =      
            "?obj <"+ noteValuePred +"> ?txtNoteValue .";
    
  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("relatedInfoTypes", Utils.getFormSpecificData("[RELATEDINFO_TYPE]"));
		formSpecificData.put("identifierTypes", Utils.getFormSpecificData("[CITATION_IDENTIFIER_TYPE]"));
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
