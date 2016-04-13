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

public class DatesWebFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";

	final static String dateFromTypePred = vivoCore + "infoType1";
	final static String dateToTypePred = vivoCore + "infoType2";
	final static String dateFromValuePred = vivoCore + "freeTextValue1";
	final static String dateToValuePred = vivoCore + "freeTextValue2";
	
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	//final static String doiForDataSet = vivoCore + "dataSetForDoi" ; //predicate
	 
    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
		// underconstruction...
        conf.setTemplate("temporalOrExistenceDatesWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( 
        		dateFromTypeAssertion, dateToTypeAssertion, dateFromValueAssertion, dateToValueAssertion ));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtDateFromType","txtDateToType", "txtDateFromValue", "txtDateToValue"));
        
        conf.addSparqlForExistingLiteral("txtDateFromType", dateFromTypeQuery);
        conf.addSparqlForExistingLiteral("txtDateToType", dateToTypeQuery);
        conf.addSparqlForExistingLiteral("txtDateFromValue", dateFromValueQuery);
        conf.addSparqlForExistingLiteral("txtDateToValue", dateToValueQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtDateFromType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
       conf.addField( new FieldVTwo().
                setName("txtDateToType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtDateFromValue").
               setRangeDatatypeUri( XSD.xstring.toString() ).
               setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtDateToValue").
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
    
    final static String dateFromTypeQuery  =  
            "SELECT ?existingDateFromType WHERE {\n"+
            "?obj <"+ dateFromTypePred +"> ?existingDateFromType . }";
    
    final static String dateToTypeQuery  =  
            "SELECT ?existingDateToType WHERE {\n"+
            "?obj <"+ dateToTypePred +"> ?existingDateToType . }";
    
    final static String dateFromValueQuery  =  
            "SELECT ?existingDateFromValue WHERE {\n"+
            "?obj <"+ dateFromValuePred +"> ?existingDateFromValue . }";
    
    final static String dateToValueQuery  =  
            "SELECT ?existingDateToValue WHERE {\n"+
            "?obj <"+ dateToValuePred +"> ?existingDateToValue . }";
   
	
    final static String dateFromTypeAssertion =
            "?obj <" + dateFromTypePred + "> ?txtDateFromType .";
    
    final static String dateToTypeAssertion  =      
            "?obj <"+ dateToTypePred +"> ?txtDateToType .";
    
    final static String dateFromValueAssertion  =      
            "?obj <"+ dateFromValuePred +"> ?txtDateFromValue .";
    
    final static String dateToValueAssertion  =      
            "?obj <"+ dateToValuePred +"> ?txtDateToValue .";
			
			
	final static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:dates  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    
  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		
		ArrayList dateFromats = new ArrayList();
		//dateFromats.add("UTC");
		dateFromats.add("W3CDTF");
		
		formSpecificData.put("dateFormats", dateFromats);
		formSpecificData.put("displayString", "Coverage");
		formSpecificData.put("propertyName", "temporalCoverage");
		formSpecificData.put("textAreaSize", "input");
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
