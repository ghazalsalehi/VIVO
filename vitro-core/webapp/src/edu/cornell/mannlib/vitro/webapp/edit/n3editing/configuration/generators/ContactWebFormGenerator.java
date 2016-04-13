/*
Copyright (c) 2015, QUT University
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

public class ContactWebFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	
	final static String titleValuePred = vivoCore + "freeTextValue1";
	final static String firstnameValuePred = vivoCore + "freeTextValue2";
	final static String surnameValuePred = vivoCore + "freeTextValue3";
	final static String emailValuePred = vivoCore + "freeTextValue4";
	final static String URLValuePred = vivoCore + "freeTextValue5";
	final static String phoneValuePred = vivoCore + "freeTextValue6";
	final static String faxValuePred = vivoCore + "freeTextValue7";
	
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
		
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("contactInfoWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( 
        		titleValueAssertion, firstnameValueAssertion, surnameValueAssertion, emailValueAssertion, 
        		URLValueAssertion, phoneValueAssertion,  faxValueAssertion));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtTitleValue","txtFirstnameValue", "txtSurnameValue", "txtEmailValue", 
        		"txtURLValue", "txtPhoneValue", "txtFaxValue"));
        
        conf.addSparqlForExistingLiteral("txtTitleValue", titleValueQuery);
        conf.addSparqlForExistingLiteral("txtFirstnameValue", firstnameValueQuery);
        conf.addSparqlForExistingLiteral("txtSurnameValue", surnameValueQuery);
        conf.addSparqlForExistingLiteral("txtEmailValue", emailValueQuery);
        conf.addSparqlForExistingLiteral("txtURLValue", URLValueQuery);
        conf.addSparqlForExistingLiteral("txtPhoneValue",  phoneValueQuery);
        conf.addSparqlForExistingLiteral("txtFaxValue", faxValueQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtTitleValue").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
       conf.addField( new FieldVTwo().
                setName("txtFirstnameValue").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtSurnameValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtEmailValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtURLValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtPhoneValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtFaxValue").
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
    
    final static String titleValueQuery  =  
            "SELECT ?existingTitleValue WHERE {\n"+
            "?obj <"+ titleValuePred +"> ?existingTitleValue . }";
    
    final static String firstnameValueQuery  =  
            "SELECT ?existingFirstnameValue WHERE {\n"+
            "?obj <"+ firstnameValuePred +"> ?existingFirstnameValue . }";
    
    final static String surnameValueQuery  =  
            "SELECT ?existingSurnameValue WHERE {\n"+
            "?obj <"+ surnameValuePred +"> ?existingSurnameValue . }";
    
    final static String emailValueQuery  =  
            "SELECT ?existingEmailValue WHERE {\n"+
            "?obj <"+ emailValuePred +"> ?existingEmailValue . }";
    
    final static String URLValueQuery  =  
            "SELECT ?existingURLValue WHERE {\n"+
            "?obj <"+ URLValuePred +"> ?existingURLValue . }";
    
    final static String phoneValueQuery  =  
            "SELECT ?existingPhoneValue WHERE {\n"+
            "?obj <"+ phoneValuePred +"> ?existingPhoneValue . }";
    
    final static String faxValueQuery  =  
            "SELECT ?existingFaxValue WHERE {\n"+
            "?obj <"+ faxValuePred +"> ?existingFaxValue . }";
    
    final static String titleValueAssertion =
            "?obj <" + titleValuePred + "> ?txtTitleValue .";
    
    final static String firstnameValueAssertion  =      
            "?obj <"+ firstnameValuePred +"> ?txtFirstnameValue .";
    
    final static String surnameValueAssertion  =      
            "?obj <"+ surnameValuePred +"> ?txtSurnameValue .";
    
    final static String emailValueAssertion  =      
            "?obj <"+ emailValuePred +"> ?txtEmailValue .";
    
    final static String URLValueAssertion  =      
            "?obj <"+ URLValuePred +"> ?txtURLValue .";
    
    final static String phoneValueAssertion  =      
            "?obj <"+ phoneValuePred +"> ?txtPhoneValue .";
    
    final static String faxValueAssertion  =      
            "?obj <"+ faxValuePred +"> ?txtFaxValue .";
    
    private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:contactInfo  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("displayString", "Contact");
		formSpecificData.put("propertyName", "contactInfo");
		formSpecificData.put("textAreaSize", "input");
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
