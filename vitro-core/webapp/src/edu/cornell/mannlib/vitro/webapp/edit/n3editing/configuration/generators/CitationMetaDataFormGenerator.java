
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

public class CitationMetaDataFormGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{            

	final static String vivoCore = "http://www.qut.edu.au/ontologies/vivoqut#";
	final static String citationFormValues = vivoCore + "freeTextValue1" ;
	final static String citationDisplayStr = vivoCore + "freeTextValue2" ;
	
	final static String citationIdenType = vivoCore + "infoType1" ;
	final static String citataionIdenValue = vivoCore + "freeTextValue3" ;	
	final static String contributorType = vivoCore + "infoType2" ;
	final static String DOIStatus = vivoCore + "infoType3" ;
	
	final static String CONFIG_FILE =  Utils.CONFIG_FILE;
	 
    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
 
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
                
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("citationMetaDataForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("citation");
                
        conf.setN3Required( Arrays.asList( n3ForNewCitation ) );
        conf.setN3Optional(Arrays.asList( citationAssertion , citationStrAssertion, idenTypeAssertion, citationIdenValueAssertion, ContributorTypeAssertion, DOIStatusAssertion));
       
        conf.addNewResource("citation", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(list ());
        conf.setLiteralsOnForm( Arrays.asList("txtCitationFormValues", "txtCitationDisplayStr", "txtIdenType", "txtCitationIdenValue", "txtContributorType", "txtDOIStatus"));
        
        conf.addSparqlForExistingLiteral("txtCitationFormValues", citationQuery);
		conf.addSparqlForExistingLiteral("txtCitationDisplayStr", citationStrQuery);
		conf.addSparqlForExistingLiteral("txtIdenType", idenTypeQuery);
		conf.addSparqlForExistingLiteral("txtCitationIdenValue", citationIdenValueQuery);
		conf.addSparqlForExistingLiteral("txtContributorType", contributorTypeQuery);
		conf.addSparqlForExistingLiteral("txtDOIStatus", DOIStatusQuery);
        
        conf.addField( new FieldVTwo().
                setName("txtCitationFormValues").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
				
		conf.addField( new FieldVTwo().
                setName("txtCitationDisplayStr").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
		
		conf.addField( new FieldVTwo().
                setName("txtIdenType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
		
		conf.addField( new FieldVTwo().
                setName("txtCitationIdenValue").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
		
		conf.addField( new FieldVTwo().
                setName("txtContributorType").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));
		
		conf.addField( new FieldVTwo().
                setName("txtDOIStatus").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));

        conf.addValidator(new AntiXssValidation());
        
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }
    
    final static String citationQuery  =  
            "SELECT ?existingCitation WHERE {\n"+
            "?citation <"+ citationFormValues +"> ?existingCitation . }";
			
	final static String citationStrQuery  =  
            "SELECT ?existingCitationStr WHERE {\n"+
            "?citation <"+ citationDisplayStr +"> ?existingCitationStr . }";
	
	final static String idenTypeQuery  =  
            "SELECT ?existingIdenType WHERE {\n"+
            "?citation <"+ citationIdenType +"> ?existingIdenType . }";
	
	final static String citationIdenValueQuery  =  
            "SELECT ?existingIdenValue WHERE {\n"+
            "?citation <"+ citataionIdenValue +"> ?existingIdenValue . }";
	
	final static String contributorTypeQuery  =  
            "SELECT ?existingContributorType WHERE {\n"+
            "?citation <"+ contributorType +"> ?existingContributorType . }";
	
	final static String DOIStatusQuery  =  
            "SELECT ?existingDOIStatus WHERE {\n"+
            "?citation <"+ DOIStatus +"> ?existingDOIStatus . }";
    
    final static String n3ForNewCitation =       
            "@prefix core: <"+ vivoCore +"> .\n"+
            "?subject core:dataCitation  ?citation .\n"+            
            "?citation  a core:customProperty ";
			
    final static String citationAssertion =
            "?citation <" + citationFormValues + "> ?txtCitationFormValues .";
			
	final static String citationStrAssertion =
            "?citation <" + citationDisplayStr + "> ?txtCitationDisplayStr .";
	
	final static String idenTypeAssertion =
            "?citation <" + citationIdenType + "> ?txtIdenType .";
	
	final static String citationIdenValueAssertion =	
            "?citation <" + citataionIdenValue + "> ?txtCitationIdenValue .";
	
	final static String ContributorTypeAssertion =
            "?citation <" + contributorType + "> ?txtContributorType .";
	
	final static String DOIStatusAssertion =
            "?citation <" + DOIStatus + "> ?txtDOIStatus .";
 
 
    
  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		formSpecificData.put("propertyName", "dataCitation");
		formSpecificData.put("citationIdentifierTypes",  getFormSpecificData("[CITATION_METADATA_IDENTIFIER_TYPE]"));
		formSpecificData.put("namePartTypes",  getFormSpecificData("[NAME_PART_TYPE]"));
		formSpecificData.put("citationDatesTypes",  getFormSpecificData("[CITATION_METADATA_DATES_TYPE]"));
		
		ArrayList creatorTypeslist = new ArrayList();
		creatorTypeslist.add("Author/creator");
		creatorTypeslist.add("Group/research institution");
		formSpecificData.put("creatorTypes",  creatorTypeslist);
		
		ArrayList doiStatusTypesList = new ArrayList();
		doiStatusTypesList.add("MINT_OR_UPDATE_DOI");
		doiStatusTypesList.add("NO_CHANGE");
		formSpecificData.put("doiStatusTypes",  doiStatusTypesList);

		RoleLevel role = RoleLevel.getRoleFromLoginStatus(vreq);
		formSpecificData.put("currentUserRole", Utils.getCurrentUserRoleAsString(role));
		
		String individualSubjectURL = "";
		if( editConfiguration.getSubjectUri() == null){
			individualSubjectURL = EditConfigurationUtils.getSubjectUri(vreq);
		}else{
			individualSubjectURL = editConfiguration.getSubjectUri();
		}
		formSpecificData.put("individualSubjectURL", individualSubjectURL);
		
		String title = Utils.getIndividualTitle(individualSubjectURL, vreq);
		formSpecificData.put("IndividualTitle", title);
		
		//LIBRDF-43
		int year = Calendar.getInstance().get(Calendar.YEAR);
		formSpecificData.put("publisher", "Queensland University of Technology");
		formSpecificData.put("year", String.valueOf(year));

		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	List<String> getFormSpecificData(String sSection){
		
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
