
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


public class LicenceURIFormGenerator extends oneFreeTextBoxWebFormGenerator{            

	private static String vivoCore1 = "http://www.qut.edu.au/ontologies/vivoqut#";
	
	private static String n3ForNewObject =       
            "@prefix core: <"+ vivoCore1 +"> .\n"+
            "?subject core:licenceURI  ?obj .\n"+            
            "?obj  a core:customProperty ";
	
	@Override
	String getn3ForNewObject() {
		return n3ForNewObject;
	}
	
	@Override
	String getDisplayString(){
		return "Licence URI";
	}
	
	@Override
	String getPropertyName(){
		return "licenceURI";
	}
	
	@Override
	String getTextAreaSize(){	// empty for text box, normal or bigger
		return "normal";
	}
}
