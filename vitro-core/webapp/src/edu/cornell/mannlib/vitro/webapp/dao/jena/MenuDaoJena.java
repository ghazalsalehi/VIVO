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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.dao.MenuDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.menu.MainMenu;

public class MenuDaoJena extends JenaBaseDao implements MenuDao {

    private static final Log log = LogFactory.getLog(MenuDaoJena.class);
        
    static final String prefixes = 
        "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n" +
        "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#> \n" +
        "PREFIX display: <" + VitroVocabulary.DISPLAY +"> \n";
    
    
    static final protected String menuQueryString = 
        prefixes + "\n" +
        "SELECT ?menuItem ?linkText ?urlMapping  WHERE {\n" +
//        "  GRAPH ?g{\n"+
        "    ?menu rdf:type display:MainMenu .\n"+
        "    ?menu display:hasElement ?menuItem .  \n"+       
        "    ?menuItem display:linkText ?linkText .\n"+
        "    OPTIONAL { ?menuItem display:menuPosition ?menuPosition }.\n"+
        "    OPTIONAL { ?menuItem display:toPage ?page . }\n"+        
        "    OPTIONAL { ?page display:urlMapping ?urlMapping . }\n"+        
//        "  }\n"+
        "} \n" +
        "ORDER BY ?menuPosition ?menuItemText \n";        
    
    static{
        try{    
            menuQuery=QueryFactory.create(menuQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for menuQueryString " + th.getMessage());
            log.error(menuQueryString);
        }                 
    }
    
    static protected Query menuQuery;     
    
    public MenuDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }
    
    @Override
    public MainMenu getMainMenu( String url ) {
        return getMenu( getOntModelSelector().getDisplayModel(), url );         
    }
            
    
    protected MainMenu getMenu(Model displayModel, String url){    
        //setup query parameters
        QuerySolutionMap initialBindings = new QuerySolutionMap();        
        
        //run SPARQL query to get menu and menu items        
        QueryExecution qexec = QueryExecutionFactory.create(menuQuery, displayModel, initialBindings );
        try{
            // ryounes Seems suspicious that a dao is creating a template model object. What's this all about?
            MainMenu menu = new MainMenu();
            
            /* bdc34: currently there is no good way to decide which url to show
             * on the menu when a page has multiple urls. */
            Set <String>seenMenuItems = new HashSet<String>();    
            
            ResultSet results =qexec.execSelect();
            for( ; results.hasNext();){
                QuerySolution soln = results.nextSolution();
                Literal itemText = soln.getLiteral("linkText");
                Literal itemLink = soln.getLiteral("urlMapping");                
                RDFNode menuItem = soln.getResource("menuItem");
                
                String text = itemText != null ? itemText.getLexicalForm():"(undefined text)";
                String link = itemLink != null ? itemLink.getLexicalForm():"undefinedLink";                
                String menuItemUri = PageDaoJena.nodeToString(menuItem);
                
                if( !seenMenuItems.contains(menuItemUri) ){
                    menu.addItem(text,link, isActive( url, link ));
                    seenMenuItems.add( menuItemUri );
                }
            }
            return menu;
        }catch(Throwable th){
            log.error(th,th);
            return new MainMenu();
        }
    }

 
    // CR:036
    protected boolean isActive(String url, String link){
        if( link.equals("/")) {
        	if (link.equals(url)){	// rdf home page.
        		return true;
        	}else if (url.equals("/scf") || url.equals("/softCodeFinder") || url.equals("/spatial")){	// scf or spatial home page.)
        		return true;
        	}else{
        		return false;
        	}
        }else if (url.equals("/softCodeFinderAbout") || url.equals("/spatialDataFinderAbout")){
        	return false;
        }else
            return url.startsWith(link);
    }
    
    /*protected boolean isActive(String url, String link){
        if( "/".equals(link) )                    
            return "/".equals(url);
        else
            return url.startsWith(link);
    }*/
}
