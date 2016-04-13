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

package edu.cornell.mannlib.vitro.webapp.controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListController;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.utils.log.LogUtils;
import edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.PageDataGetterUtils;

/**
 * This servlet is for servicing requests for JSON objects/data.
 * It could be generalized to get other types of data ex. XML, HTML etc
 * @author bdc34
 *
 * Moved most of the logic into a group of JsonProducer classes. jeb228
 */
public class JsonServlet extends VitroHttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(JsonServlet.class);
    //private static final int INDIVIDUALS_PER_PAGE = 30;
    private static final int INDIVIDUALS_PER_PAGE = 15;
    public static final int REPLY_SIZE = 256;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        log.debug(LogUtils.formatRequestProperties(log, "debug", req));

        VitroRequest vreq = new VitroRequest(req);
        if (vreq.getParameter("getEntitiesByVClass") != null) {
            if( vreq.getParameter("resultKey") == null) {
                new GetEntitiesByVClass(vreq).process(resp);
            } else {
            	new GetEntitiesByVClassContinuation(vreq).process(resp);
            }
        }else if( vreq.getParameter("getN3EditOptionList") != null ){
        	throw new IllegalArgumentException("The call invoked deprecated classes " +
        			"and the parameter for this call appeared nowhere in the code base, " +
        			"so it was removed in May, 2012.");
        }else if( vreq.getParameter("getSolrIndividualsByVClass") != null ){
            new GetSolrIndividualsByVClass(vreq).process(resp);
        }else if( vreq.getParameter("getVClassesForVClassGroup") != null ){
            new GetVClassesForVClassGroup(vreq).process(resp);
        } else if( vreq.getParameter("getSolrIndividualsByVClasses") != null ){
        	log.debug("AJAX request to retrieve individuals by vclasses");
        	new	GetSolrIndividualsByVClasses(vreq).process(resp);
        } else if( vreq.getParameter("getDataForPage") != null ){
            new GetDataForPage(vreq).process(resp);
        }else if( vreq.getParameter("getRenderedSolrIndividualsByVClass") != null ){
            new GetRenderedSolrIndividualsByVClass(vreq).process(resp);
        }
    }
    
    
   public static JSONObject getSolrIndividualsByVClass(String vclassURI, HttpServletRequest req, ServletContext context) throws Exception {
        List<String> vclassURIs = Collections.singletonList(vclassURI);
        VitroRequest vreq = new VitroRequest(req);        
        
        Map<String, Object> map = getSolrVClassIntersectionResults(vclassURIs, vreq, context);
        //last parameter indicates single vclass instead of multiple vclasses
        
        return processVClassResults(map, vreq, context, false);                    
    }

   /* public static JSONObject getSolrIndividualsByVClass(String vclassURI, HttpServletRequest req, ServletContext context) throws Exception {
        List<String> vclassURIs = Collections.singletonList(vclassURI);
        VitroRequest vreq = new VitroRequest(req);        
        
        Map<String, Object> map = getSolrVClassIntersectionResults(vclassURIs, vreq, context);
        //last parameter indicates single vclass instead of multiple vclasses

        // CR - Implement draft functionality
        //---------------------------------------------------------------------------------------
        RoleLevel role = RoleLevel.getRoleFromLoginStatus(req);
        if (role == RoleLevel.PUBLIC || role == RoleLevel.QUT_GUEST){
       	 List<Individual> inds = (List<Individual>)map.get("entities");
            OntModel model = ModelContext.getJenaOntModel(context);
            
            List<Individual> filterInds = new ArrayList<Individual>();
            for(Individual ind : inds ){
           	String uri = ind.getURI();
           	Resource r = model.getResource(uri);
           	
           	Property pPublishRecord = model.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "publishRecord");
           	
           	StmtIterator iter = r.listProperties(pPublishRecord);
       		while (iter.hasNext()) {
       			Resource rPublishRecord = (iter.nextStatement().getObject()).asResource();
       			
       			if (rPublishRecord != null) {
       				Property pValue = model.getProperty("http://www.qut.edu.au/ontologies/vivoqut#", "freeTextValue1");
       				Statement value = rPublishRecord.getProperty(pValue);
       				if (value != null){
       					String sPublishRecord = value.getString();
       					
       					if (sPublishRecord.equals("yes")){
       						filterInds.add(ind);
       						break;
       					}
       				}
       			}
       		}
            }
            
            map.put("entities", filterInds);
       }

         // -------------------------------------
        return processVClassResults(map, vreq, context, false);                    
    }*/

    public static JSONObject getSolrIndividualsByVClasses(List<String> vclassURIs, HttpServletRequest req, ServletContext context) throws Exception {
   	 	VitroRequest vreq = new VitroRequest(req);   
   	 	log.debug("Retrieve solr results for vclasses" + vclassURIs.toString());
        Map<String, Object> map = getSolrVClassIntersectionResults(vclassURIs, vreq, context);
        log.debug("Results returned from Solr for " + vclassURIs.toString() + " are of size " + map.size());
        JSONObject rObj = processVClassResults(map, vreq, context, true);                    
        return rObj;     
   }
    
    //Including version for Solr query for Vclass Intersections
    private static Map<String,Object> getSolrVClassIntersectionResults(List<String> vclassURIs, VitroRequest vreq, ServletContext context){
        log.debug("Retrieving Solr intersection results for " + vclassURIs.toString());
    	String alpha = IndividualListController.getAlphaParameter(vreq);
        int page = IndividualListController.getPageParameter(vreq);
        log.debug("Alpha and page parameters are " + alpha + " and " + page);
        Map<String,Object> map = null;
        try {
	        /*map = IndividualListController.getResultsForVClassIntersections(
	                 vclassURIs, 
	                 page, INDIVIDUALS_PER_PAGE,
	                 alpha, 
	                 vreq.getWebappDaoFactory().getIndividualDao(), 
	                 context); */
        	
        	// CR01: implement draft records functionality - this is for home page
            RoleLevel role = RoleLevel.getRoleFromLoginStatus(vreq);
        	map = IndividualListController.getResultsForVClassIntersections(
	                 vclassURIs, 
	                 page, INDIVIDUALS_PER_PAGE,
	                 alpha, 
	                 vreq.getWebappDaoFactory().getIndividualDao(), 
	                 context, role); 
	         // pass context and RoleLevel
        } catch(Exception ex) {
        	log.error("Error in retrieval of search results for VClass " + vclassURIs.toString(), ex);
        }
            
        return map;
   }
 
    // Map given to process method includes the actual individuals returned from the search
    public static JSONObject processVClassResults(Map<String, Object> map, VitroRequest vreq, ServletContext context, boolean multipleVclasses) throws Exception{
         JSONObject rObj = PageDataGetterUtils.processVclassResultsJSON(map, vreq, multipleVclasses);
         return rObj;
    } 

    public static Collection<String> getMostSpecificTypes(Individual individual, WebappDaoFactory wdf) {
        ObjectPropertyStatementDao opsDao = wdf.getObjectPropertyStatementDao();
        Map<String, String> mostSpecificTypes = opsDao.getMostSpecificTypesInClassgroupsForIndividual(individual.getURI());  
        return mostSpecificTypes.values();
    }

    public static String getDataPropertyValue(Individual ind, DataProperty dp, WebappDaoFactory wdf){
        String value = ind.getDataValue(dp.getURI());
        if( value == null || value.isEmpty() )
            return "";
        else
            return value;            
    }
    
    public static String getObjectPropertyValue(Individual ind, ObjectProperty op, DataProperty dp, WebappDaoFactory wdf){	// CR:020.
    	Individual in = ind.getRelatedIndividual(op.getURI());
    	
    	if (in != null){
    		String value = in.getDataValue(dp.getURI());
        	
        	if( value == null || value.isEmpty() )
                return "";
            else
                return value;   
    	}else{
    		 return "";
    	}
    }
}
