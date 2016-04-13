/*
Copyright (c) 2012, QUT University
*/

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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




public class BiographyFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	final static String descriptionTypePred = vivoCore + "infoType1";
	final static String descriptionPred = vivoCore + "freeTextValue1";
	final static String moreInfoURLPred = vivoCore + "freeTextValue2";
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	
	//final static String doiForDataSet = vivoCore + "dataSetForDoi" ; //predicate
	 
    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("biographyFieldsWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( 
        		descriptionAssertion, descriptionTypeAssertion, moreInfoAssertion));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtDescriptionType","txtDescription", "txtMoreInfoURL"));
        
        conf.addSparqlForExistingLiteral("txtDescription", descriptionQuery);
        conf.addSparqlForExistingLiteral("txtDescriptionType", descriptionTypeQuery);
        conf.addSparqlForExistingLiteral("txtMoreInfoURL", moreInfoURLQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtDescriptionType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
       conf.addField( new FieldVTwo().
                setName("txtDescription").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
       conf.addField( new FieldVTwo().
               setName("txtMoreInfoURL").
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
    
    final static String descriptionQuery  =  
            "SELECT ?existingDescription WHERE {\n"+
            "?obj <"+ descriptionPred +"> ?existingDescription . }";
    
    final static String moreInfoURLQuery  =  
            "SELECT ?existingMoreInfo WHERE {\n"+
            "?obj <"+ moreInfoURLPred +"> ?existingMoreInfo . }";
    
    final static String descriptionTypeQuery  =  
            "SELECT ?existingDescriptionType WHERE {\n"+
            "?obj <"+ descriptionTypePred +"> ?existingDescriptionType . }";
    
    final static String descriptionAssertion =
            "?obj <" + descriptionPred + "> ?txtDescription .";
    
    final static String moreInfoAssertion =
            "?obj <" + moreInfoURLPred + "> ?txtMoreInfoURL .";
    
    final static String descriptionTypeAssertion  =      
            "?obj <"+ descriptionTypePred +"> ?txtDescriptionType .";
    
    private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:biography  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("selectionTypes", getFormSpecificData());
		formSpecificData.put("displayString", "Biography");
		formSpecificData.put("propertyName", "biography");
		formSpecificData.put("textAreaSize", "bigger");
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
	
	public List<String> getFormSpecificData(){
		
		ArrayList wordList = new ArrayList();
     	  
		try {
		    BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
		    String str;
		    String regex = "\\[.*\\]";
		    boolean bIsFound = false;
		    while ((str = in.readLine()) != null) {
		    	if (str.equals("[BIOGRAPHY_TYPE]")){
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
}
