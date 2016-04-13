
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

public class LicenceWebFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	final static String licenceType = vivoCore + "infoType1";
	final static String licenceValue = vivoCore + "freeTextValue1";
	final static String rightsUri = vivoCore + "freeTextValue2";
		
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;

    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("licenceWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( licenceTypeAssertion, licenceValueAssertion, rightsUriAssertion ));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtlicenceType", "txtlicenceValue", "txtRightsUri"));
        
        conf.addSparqlForExistingLiteral("txtlicenceType", licenceTypeQuery);
        conf.addSparqlForExistingLiteral("txtlicenceValue", licenceValueQuery);
        conf.addSparqlForExistingLiteral("txtRightsUri", rightsUriQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtlicenceType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
                
        conf.addField( new FieldVTwo().
                setName("txtlicenceValue").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
        conf.addField( new FieldVTwo().
               setName("txtRightsUri").
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
    
    final static String licenceTypeQuery  =  
            "SELECT ?existingLicenceType WHERE {\n"+
            "?obj <"+ licenceType +"> ?existingLicenceType . }";
    
    final static String licenceValueQuery  =  
            "SELECT ?existinglicenceValue WHERE {\n"+
            "?obj <"+ licenceValue +"> ?existinglicenceValue . }";
    
    final static String rightsUriQuery  =  
            "SELECT ?existingrightsUri WHERE {\n"+
            "?obj <"+ rightsUri +"> ?existingrightsUri . }";
			
	final static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:licence  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    final static String licenceTypeAssertion =
            "?obj <" + licenceType + "> ?txtlicenceType .";
    
    final static String licenceValueAssertion  =      
            "?obj <"+ licenceValue +"> ?txtlicenceValue .";
    
    final static String rightsUriAssertion  =      
            "?obj <"+ rightsUri +"> ?txtRightsUri .";
    
  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("licenceUriTypes", Utils.getFormSpecificData("[LICENCE_TYPE]"));
		formSpecificData.put("propertyName", "licence");
		formSpecificData.put("textAreaSize", "input");
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	/*List<String> getFormSpecificData(String sSection){
		
		ArrayList wordList = new ArrayList();
		
		try {
			
		    BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
		    String str;
		    String regex = "\\[.*\\]";
		    boolean bIsFound = false;
		    while ((str = in.readLine()) != null) {
		    	
		    	if (str.equals(sSection)){
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
