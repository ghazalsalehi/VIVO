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
/**
 * 
 */
package edu.cornell.mannlib.vitro.webapp.search.indexing;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.search.beans.StatementToURIsToUpdate;

/**
 * @author bdc34
 *
 */
public class AdditionalURIsForClassGroupChangesTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link edu.cornell.mannlib.vitro.webapp.search.indexing.AdditionalURIsForClassGroupChanges#findAdditionalURIsToIndex(com.hp.hpl.jena.rdf.model.Statement)}.
     */
    @Test
    public void testFindAdditionalURIsToIndex() {
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3ForPresentationClass), null,  "N3");
        
        StatementToURIsToUpdate uriFinder = new AdditionalURIsForClassGroupChanges( model );
        List<String> uris = uriFinder.findAdditionalURIsToIndex( 
                ResourceFactory.createStatement(
                        ResourceFactory.createResource("http://vivoweb.org/ontology/core#Presentation"),
                        ResourceFactory.createProperty(VitroVocabulary.IN_CLASSGROUP),
                        ResourceFactory.createResource("http://example.com/someClassGroup")));
        
        Assert.assertNotNull(uris);
        Assert.assertTrue("uris list is empty", uris.size() > 0 );
        
        Assert.assertTrue(uris.contains("http://vivo.scripps.edu/individual/n400"));
        Assert.assertTrue(uris.contains("http://vivo.scripps.edu/individual/n12400"));
        Assert.assertTrue(uris.contains("http://vivo.scripps.edu/individual/n210"));
        Assert.assertTrue(uris.contains("http://vivo.scripps.edu/individual/n264"));
        Assert.assertTrue(uris.contains("http://vivo.scripps.edu/individual/n25031"));
        Assert.assertTrue(uris.contains("http://vivo.scripps.edu/individual/n2486"));
        
        Assert.assertTrue("uris list should not contain n9999",!uris.contains("http://vivo.scripps.edu/individual/n9999"));
        Assert.assertTrue("uris list should not contain n9998",!uris.contains("http://vivo.scripps.edu/individual/n9998"));
        
//        Assert.assertTrue("uris didn't not contain test:onions", uris.contains(testNS+"onions"));
//        Assert.assertTrue("uris didn't not contain test:cheese", uris.contains(testNS+"cheese"));
//        Assert.assertTrue("uris didn't not contain test:icecream", uris.contains(testNS+"icecream"));
//        
//        Assert.assertTrue("uris contained test:Person", !uris.contains(testNS+"Person"));
//        Assert.assertTrue("uris contained owl:Thing", !uris.contains( OWL.Thing.getURI() ));        
    }
String n3ForPresentationClass = 
    "@prefix dc:      <http://purl.org/dc/elements/1.1/> . \n" +
    "@prefix pvs:     <http://vivoweb.org/ontology/provenance-support#> . \n" +
    "@prefix geo:     <http://aims.fao.org/aos/geopolitical.owl#> . \n" +
    "@prefix foaf:    <http://xmlns.com/foaf/0.1/> . \n" +
    "@prefix scires:  <http://vivoweb.org/ontology/scientific-research#> . \n" +
    "@prefix scripps:  <http://vivo.scripps.edu/> . \n" +
    "@prefix dcterms:  <http://purl.org/dc/terms/> . \n" +
    "@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> . \n" +
    "@prefix swrl:    <http://www.w3.org/2003/11/swrl#> . \n" +
    "@prefix vitro:   <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> . \n" +
    "@prefix event:   <http://purl.org/NET/c4dm/event.owl#> . \n" +
    "@prefix bibo:    <http://purl.org/ontology/bibo/> . \n" +
    "@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> . \n" +
    "@prefix owl:     <http://www.w3.org/2002/07/owl#> . \n" +
    "@prefix swrlb:   <http://www.w3.org/2003/11/swrlb#> . \n" +
    "@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n" +
    "@prefix core:    <http://vivoweb.org/ontology/core#> . \n" +
    "@prefix skos:    <http://www.w3.org/2004/02/skos/core#> . \n" +
    "@prefix vivo:    <http://vivo.library.cornell.edu/ns/0.1#> . \n" +
    "@prefix dcelem:  <http://purl.org/dc/elements/1.1/> . \n" +
    "@prefix ero:     <http://purl.obolibrary.org/obo/> . \n" +
    " \n" +
    "core:Presentation \n" +
    "      a       owl:Class ; \n" +
    "      rdfs:label \"Presentation\"@en-US ; \n" +
    "      rdfs:subClassOf event:Event , owl:Thing ; \n" +
    "      vitro:displayLimitAnnot \n" +
    "              \"-1\"^^xsd:int ; \n" +
    "      vitro:displayRankAnnot \n" +
    "              \"-1\"^^xsd:int ; \n" +
    "      vitro:hiddenFromDisplayBelowRoleLevelAnnot \n" +
    "              <http://vitro.mannlib.cornell.edu/ns/vitro/role#public> ; \n" +
    "      vitro:inClassGroup <http://vivoweb.org/ontology#vitroClassGroupevents> ; \n" +
    "      vitro:prohibitedFromUpdateBelowRoleLevelAnnot \n" +
    "              <http://vitro.mannlib.cornell.edu/ns/vitro/role#public> ; \n" +
    "      vitro:shortDef \"Encompasses talk, speech, lecture, slide lecture, conference presentation\"^^xsd:string ; \n" +
    "      owl:equivalentClass core:Presentation . \n" +
    " \n" +
    " \n" +
    "core:Presentation \n" +
    "      owl:equivalentClass core:Presentation . \n" +
    " \n" +
    "<http://vivo.scripps.edu/individual/n400> \n" +
    "      a       core:Presentation . \n" +
    " \n" +
    "<http://vivo.scripps.edu/individual/n12400> \n" +
    "      a       core:Presentation . \n" +
    " \n" +
    "<http://vivo.scripps.edu/individual/n210> \n" +
    "      a       core:Presentation . \n" +
    " \n" +
    "<http://vivo.scripps.edu/individual/n264> \n" +
    "      a       core:Presentation ; \n" +
    "      vitro:mostSpecificType \n" +
    "              core:Presentation . \n" +
    " \n" +
    "<http://vivo.scripps.edu/individual/n25031> \n" +
    "      a       core:Presentation ; \n" +
    "      vitro:mostSpecificType \n" +
    "              core:Presentation . \n" +
    " \n" +
    "<http://vivo.scripps.edu/individual/n2486> \n" +
    "      a       core:Presentation ; \n" +
    "      vitro:mostSpecificType \n" +
    "              core:Presentation . \n" +
    " \n " + 
    "<http://vivo.scripps.edu/individual/n9998> \n" +
    "      a       core:BogusClass . \n" +   
    "<http://vivo.scripps.edu/individual/n9999> \n" +
    "      a       core:BogusClass . \n" +
    " \n" +    
    "core:InvitedTalk \n" +
    "      rdfs:subClassOf core:Presentation . \n" +
    " \n" ;
 
   
  
}