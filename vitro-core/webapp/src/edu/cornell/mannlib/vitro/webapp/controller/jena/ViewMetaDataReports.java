

package edu.cornell.mannlib.vitro.webapp.controller.jena;

import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;

public class ViewMetaDataReports extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ManageQUTResearchData.class.getName());

    @Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) {

    	if (!isAuthorizedToDisplayPage(request, response, SimplePermission.QUT_RESEARCH_DATA_PAGE.ACTIONS)) {
    		return;
    	}

        VitroRequest vreq = new VitroRequest(request);        

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/specific/viewMetaData_reports.jsp");
        request.setAttribute("title","View Metadata Reports");
        
        ServletContext context = getServletContext();
    	//Dataset dataset = DatasetFactory.create(ModelContext.getJenaOntModel(context));
        OntModel model = ModelContext.getJenaOntModel(context);
        
        String poepleCount = getCountOfIndividualsInVClass("people", model);
        String adminCount = getCountOfIndividualsInVClass("administrativePosition", model);
        String researcherCount = getCountOfIndividualsInVClass("researcher", model);
        String groupCount = getCountOfIndividualsInVClass("group", model);

        String activitiesCount = getCountOfIndividualsInVClass("activities", model);
        String projectsCount = getCountOfIndividualsInVClass("project", model);
        String awardsCount = getCountOfIndividualsInVClass("award", model);
        String coursesCount = getCountOfIndividualsInVClass("course", model);
        String eventsCount = getCountOfIndividualsInVClass("event", model);
        String programsCount = getCountOfIndividualsInVClass("program", model);
        
        String dataCollectionsCount = getCountOfIndividualsInVClass("dataCollections", model);
        String collectionsCount = getCountOfIndividualsInVClass("collection", model);
        String research_datasetsCount = getCountOfIndividualsInVClass("researchDataSet", model);
        String catalogueOrIndexCount = getCountOfIndividualsInVClass("catalogueOrIndex", model);
        String registryCount = getCountOfIndividualsInVClass("registry", model);
        String repositoryCount = getCountOfIndividualsInVClass("repository",model);
        
        String serviceCount = getCountOfIndividualsInVClass("service",model);
        String createCount = getCountOfIndividualsInVClass("create", model);
        String generateCount = getCountOfIndividualsInVClass("generate", model);
        String reportCount = getCountOfIndividualsInVClass("report", model);
        String annotateCount = getCountOfIndividualsInVClass("annotate", model);
        String transformCount = getCountOfIndividualsInVClass("transform", model);
        String assembleCount = getCountOfIndividualsInVClass("assemble", model);
        String harvestOAIPMHCount = getCountOfIndividualsInVClass("harvestOAIPMH", model);
        String search_HTTPCount = getCountOfIndividualsInVClass("searchHTTP", model);
        String searchOpenSearchCount = getCountOfIndividualsInVClass("searchOpenSearch", model);
        String searchSRUCount = getCountOfIndividualsInVClass("searchSRU", model);
        String searchSRWCount = getCountOfIndividualsInVClass("searchSRW", model);
        String searchZ39_50Count = getCountOfIndividualsInVClass("SearchZ3950", model);
        String syndicateATOMCount = getCountOfIndividualsInVClass("syndicateATOM", model);
        String syndicateRSSCount = getCountOfIndividualsInVClass("syndicateRSS", model);
        
        request.setAttribute("total_people", poepleCount);
        request.setAttribute("total_adminPositions", adminCount);
        request.setAttribute("total_researchers", researcherCount);
        request.setAttribute("total_groups", groupCount);
        
        request.setAttribute("total_activities", activitiesCount);
        request.setAttribute("total_projects", projectsCount);
        request.setAttribute("total_awards", awardsCount);
        request.setAttribute("total_courses", coursesCount);
        request.setAttribute("total_events", eventsCount);
        request.setAttribute("total_programs", programsCount);
        
        request.setAttribute("total_dataCollections", dataCollectionsCount);
        request.setAttribute("total_collections", collectionsCount);
        request.setAttribute("total_research_datasets", research_datasetsCount);
        request.setAttribute("total_catalogueOrIndex", catalogueOrIndexCount);
        request.setAttribute("total_registry", registryCount);
        request.setAttribute("total_repository", repositoryCount);
        
        request.setAttribute("total_services", serviceCount);
        request.setAttribute("total_create", createCount);
        request.setAttribute("total_generate", generateCount);
        request.setAttribute("total_report", reportCount);
        request.setAttribute("total_annotate", annotateCount);
        request.setAttribute("total_transform", transformCount);
        request.setAttribute("total_assemble", assembleCount);
        request.setAttribute("total_harvest_OAI-PMH", harvestOAIPMHCount);
        request.setAttribute("total_search_HTTP", search_HTTPCount);
        request.setAttribute("total_searchOpenSearch", searchOpenSearchCount);
        request.setAttribute("total_searchSRU", searchSRUCount);
        request.setAttribute("total_searchSRW", searchSRWCount);
        request.setAttribute("total_searchZ39_50", searchZ39_50Count);
        request.setAttribute("total_syndicateATOM", syndicateATOMCount);
        request.setAttribute("total_syndicateRSS", syndicateRSSCount);
        
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + vreq.getAppBean().getThemeDir()+"css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error(this.getClass().getName()+" could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }
    
    /*public String getCountOfIndividuals(String className, ServletContext context){
    	long count = 0;
    	String vclassURI = "<http://www.qut.edu.au/ontologies/vivoqut#" + className + ">";
    	 String facetOnField = VitroSearchTermNames.RDFTYPE;
    	
    	SolrQuery query = new SolrQuery( ).
                setRows(0).
                setQuery(VitroSearchTermNames.CLASSGROUP_URI + ":" + vclassURI ).        
                setFacet(true). //facet on type to get counts for classes in classgroup
                addFacetField( facetOnField ).
                setFacetMinCount(0);
     	try {    	              
             SolrServer solr = SolrSetup.getSolrServer(context);
             QueryResponse response = null;      
             response = solr.query(query);            
             count = response.getResults().getNumFound();                        
     	} catch(Exception ex) {
     		log.error("An error occured in retrieving individual count", ex);
     	}
    	
     	 String sCount = String.format ("%d", count);
     	 
    	return sCount;
    }*/
    
    public String getCountOfIndividualsInVClass(String className, OntModel model){
    	String vclassURI = "<http://www.qut.edu.au/ontologies/vivoqut#" + className + ">";
        int count = 0;
        
        String queryString = "SELECT ?obj ?label WHERE { \n" +
    			"?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "+ vclassURI +  " \n" +
    			"OPTIONAL { ?obj <http://www.w3.org/2000/01/rdf-schema#label> ?label   } \n" +
    			"}";
        
        Query sparqlQuery = QueryFactory.create( queryString, Syntax.syntaxARQ);
        model.getLock().enterCriticalSection(Lock.READ);
        QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, model);
 	    
		try {
			ResultSet results = qExec.execSelect();
			for ( ; results.hasNext() ; ){
				QuerySolution soln= results.nextSolution() ;
				count++;
			}
		}finally {
			model.getLock().leaveCriticalSection();
			qExec.close() ;
		}
        
        String sCount = String.format ("%d", count);
        
        return sCount;
    }

    @Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }
}
