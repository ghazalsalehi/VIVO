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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

//Returns the appropriate n3 based on data getter
public abstract class ProcessDataGetterAbstract implements ProcessDataGetterN3 {
	
	public ProcessDataGetterAbstract(){
		
	}
	
    //placeholder so need "?" in front of the variable
    public String getDataGetterVar(int counter) {
    	return "?" + getDataGetterVarName(counter);
    }
    
    //Just the var name, no "?"
    public String getDataGetterVarName(int counter) {
    	return "dataGetter" + counter;
    }
    
    public String getPrefixes() {
    	return "@prefix display: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#> . \n" + 
    			"@prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#> . \n";
    }
  
   public String getVarName(String base, int counter) {
	   return base + counter;
   }
   
   //For use within n3 strings, need a "?"
   public String getN3VarName(String base, int counter) {
	   return "?" + getVarName(base, counter);
   }
   
   //Return name of new resources
   public List<String> getNewResources(int counter) {
	   //Each data getter requires a new resource
	   List<String> newResources = new ArrayList<String>();
	   newResources.add("dataGetter" + counter);
	   return newResources;
   }
   
   protected String getSparqlPrefix() {
		  return  "PREFIX display: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#> \n" + 
				  "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
   
   }
   
   //For existing values
   protected  Map<String, List<Literal>> existingLiteralValues = new HashMap<String, List<Literal>>();
   protected Map<String, List<String>> existingUriValues = new HashMap<String, List<String>>();
   public Map<String, List<Literal>> retrieveExistingLiteralValues() {
	  return existingLiteralValues;
   }
   public Map<String, List<String>> retrieveExistingUriValues() {
	  return existingUriValues;
   }
	   
	//Data getter var needs to be included in uris in scope
   public void populateExistingDataGetterURI(String dataGetterURI, int counter) {
	   existingUriValues.put(this.getVarName("dataGetter", counter), new ArrayList<String>(Arrays.asList(dataGetterURI)));

   }
   

 
}


