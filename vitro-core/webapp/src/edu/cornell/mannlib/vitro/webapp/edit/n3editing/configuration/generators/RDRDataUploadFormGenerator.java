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

public class RDRDataUploadFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	final static String rdrPackageTypePred = vivoCore + "infoType1"; 
	final static String rdrPackageURLPred = vivoCore + "freeTextValue1";	// http://libweb01-dev.qut.edu.au/dataset/7c5f6fb5-d0a3-44ae-91c8-aadc4456e898
	final static String rdrPackageNamePred = vivoCore + "freeTextValue2";	// test-dataset-name
	final static String rdrPackageIDPred = vivoCore + "freeTextValue3";		// 7c5f6fb5-d0a3-44ae-91c8-aadc4456e898
	final static String rdrPackageTitlePred = vivoCore + "freeTextValue4";	// test dataset name
	
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;

    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("RDRUploadDataWebForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("obj");
                
        conf.setN3Required( Arrays.asList( n3ForNewObject ) );
        conf.setN3Optional(Arrays.asList( RDRPackageTypeAssertion, RDRPackageTitleAssertion, RDRPackageNameAssertion, RDRPackageIDAssertion, RDRPackageURLAssertion));
       
        conf.addNewResource("obj", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtRDRPackageType", "txtRDRPackageTitle", "txtRDRPackageName", "txtRDRPackageID", "txtRDRPackageURL"));
        
        conf.addSparqlForExistingLiteral("txtRDRPackageType", RDRPackageTypeQuery);
        conf.addSparqlForExistingLiteral("txtRDRPackageTitle", RDRPackageTitleQuery);
        conf.addSparqlForExistingLiteral("txtRDRPackageName", RDRPackageNameQuery);
        conf.addSparqlForExistingLiteral("txtRDRPackageID", RDRPackageIDQuery);
        conf.addSparqlForExistingLiteral("txtRDRPackageURL", RDRPackageURLQuery);
        
        
        conf.addField( new FieldVTwo().
                setName("txtRDRPackageType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
        
        conf.addField( new FieldVTwo().
                setName("txtRDRPackageTitle").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
        
        conf.addField( new FieldVTwo().
                setName("txtRDRPackageName").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
        
        conf.addField( new FieldVTwo().
                setName("txtRDRPackageID").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
       
        conf.addField( new FieldVTwo().
                setName("txtRDRPackageURL").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
 
        conf.addValidator(new AntiXssValidation());
        
        //Adding additional data, specifically edit mode
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }
    
    final static String RDRPackageTypeQuery  =  
            "SELECT ?existingRDRDataType WHERE {\n"+
            "?obj <"+ rdrPackageTypePred +"> ?existingRDRDataType . }";
    
    final static String RDRPackageTitleQuery  =  
            "SELECT ?existingRDRDataTitle WHERE {\n"+
            "?obj <"+ rdrPackageTitlePred +"> ?existingRDRDataTitle . }";
    
    final static String RDRPackageNameQuery  =  
            "SELECT ?existingRDRDataName WHERE {\n"+
            "?obj <"+ rdrPackageNamePred +"> ?existingRDRDataName . }";
    
    final static String RDRPackageIDQuery  =  
            "SELECT ?existingRDRDataID WHERE {\n"+
            "?obj <"+ rdrPackageIDPred +"> ?existingRDRDataID . }";
    
    final static String RDRPackageURLQuery  =  
            "SELECT ?existingRDRDataURL WHERE {\n"+
            "?obj <"+ rdrPackageURLPred +"> ?existingRDRDataURL . }";
    
    private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:RDRDataUpload  ?obj .\n"+            
            "?obj  a core:customProperty ";
    
    final static String RDRPackageTypeAssertion =
            "?obj <" + rdrPackageTypePred + "> ?txtRDRPackageType .";
      
    final static String RDRPackageTitleAssertion =
            "?obj <" + rdrPackageTitlePred + "> ?txtRDRPackageTitle .";
    
    final static String RDRPackageNameAssertion =
            "?obj <" + rdrPackageNamePred + "> ?txtRDRPackageName .";
    
    final static String RDRPackageIDAssertion =
            "?obj <" + rdrPackageIDPred + "> ?txtRDRPackageID .";
    
    final static String RDRPackageURLAssertion =
            "?obj <" + rdrPackageURLPred + "> ?txtRDRPackageURL .";
    
    //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("displayString", "Browse and upload");
		formSpecificData.put("propertyName", "RDRDataUpload");
		formSpecificData.put("selectionTypes", Utils.getFormSpecificData("[ELECTRONIC_ADDRESS_TYPE]"));
		
		String individualSubjectURL = "";	// Ex: http://www.localhost.com/individual/n3161
		if( editConfiguration.getSubjectUri() == null){
			individualSubjectURL = EditConfigurationUtils.getSubjectUri(vreq);
		}else{
			individualSubjectURL = editConfiguration.getSubjectUri();
		}
		
		String individualDisplayURL = individualSubjectURL.replace("individual", "display");
		//formSpecificData.put("individualSubjectURL", individualSubjectURL);
		formSpecificData.put("individualDisplayURL", individualDisplayURL);
		
		String title = Utils.getIndividualTitle(individualSubjectURL, vreq);
		formSpecificData.put("IndividualTitle", title);
		
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
