/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService.ResultFormat;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;

/**
 * Adds all labels to name fields, not just the one returned by Indivdiual.getName().
 */
public class NameFields implements DocumentModifier {
	RDFServiceFactory rsf;
	
	public static final VitroSearchTermNames term = new VitroSearchTermNames();
	public static final Log log = LogFactory.getLog(NameFields.class.getName());
	
	public NameFields( RDFServiceFactory rsf){
		this.rsf = rsf; 
	}
	
	@Override
	public void modifyDocument(Individual ind, SolrInputDocument doc,
			StringBuffer addUri) throws SkipIndividualException {
		if( ind == null || ind.getURI() == null ){
			return;
		}
		
		//also run SPARQL query to get rdfs:label values		
		String query = 
			"SELECT ?label WHERE {  " +
			"<" + ind.getURI() + "> " +
			"<http://www.w3.org/2000/01/rdf-schema#label> ?label  }";

		try {
			RDFService rdfService = rsf.getRDFService();
			BufferedReader stream = 
				new BufferedReader(new InputStreamReader(rdfService.sparqlSelectQuery(query, ResultFormat.CSV)));
			
			StringBuffer buffer = new StringBuffer();
			String line;

			//throw out first line since it is just a header
			stream.readLine();
			
			while( (line = stream.readLine()) != null ){
				buffer.append(line).append(' ');
			}
			
			log.debug("Adding labels for " + ind.getURI() + " \"" + buffer.toString() + "\"");
			//doc.addField(term.NAME_RAW, buffer.toString());
			
			// CR:031 - QUT
			// comments : WHY adding label field twice into NAME_RAW.?????
			String rdfsLabelVal = buffer.toString();
			if (ind.isVClass(Utils.VCLASS_RESEARCHER) || ind.isVClass(Utils.VCLASS_ADMINISTRATIVE_POSITION)){
	    	   String[] parts = rdfsLabelVal.split(" ");
	    	   String personValue = "";
	    	   for (int i = (parts.length - 1); i >= 0; i--){
	    		   personValue += parts[i] + " ";
	    	   }
	    	   doc.addField(term.NAME_RAW, personValue.trim());
	        }else{
	        	
	        	// CR:032
	        	final Pattern ThePattern = Pattern.compile("^\\s*The\\s+[0-9]*\\s*(\\w+.*)", Pattern.CASE_INSENSITIVE);
	        	final Pattern APattern = Pattern.compile("^\\s*A\\s+[0-9]*\\s*(\\w+.*)", Pattern.CASE_INSENSITIVE);
	        	final Pattern AnPattern = Pattern.compile("^\\s*An\\s+[0-9]*\\s*(\\w+.*)", Pattern.CASE_INSENSITIVE);
	        	final Pattern DigitPattern = Pattern.compile("^\\s*[0-9]+\\s*(\\w+.*)", Pattern.CASE_INSENSITIVE);
	    		
	        	final Matcher TheMatcher = ThePattern.matcher(rdfsLabelVal);
	    		final Matcher AMatcher = APattern.matcher(rdfsLabelVal);
	    		final Matcher AnMatcher = AnPattern.matcher(rdfsLabelVal);
	    		final Matcher DigitMatcher = DigitPattern.matcher(rdfsLabelVal);
	    		
	    		if (TheMatcher.find()){
	    			rdfsLabelVal = TheMatcher.group(1);
	    		}else if (AMatcher.find()){
	    			rdfsLabelVal = AMatcher.group(1);
	    		}else if (AnMatcher.find()){
	    			rdfsLabelVal = AnMatcher.group(1);
	    		}else if (DigitMatcher.find()){
	    			rdfsLabelVal = DigitMatcher.group(1);
	    		}
	    		
	        	doc.addField(term.NAME_RAW, rdfsLabelVal);
	        }
			 
		} catch (RDFServiceException e) {
			log.error("could not get the rdfs:label for " + ind.getURI(), e);
		} catch (IOException e) {
			log.error("could not get the rdfs:label for " + ind.getURI(), e);
		}

	}
	
	@Override
	public void shutdown() { /*nothing to do */ }
}
