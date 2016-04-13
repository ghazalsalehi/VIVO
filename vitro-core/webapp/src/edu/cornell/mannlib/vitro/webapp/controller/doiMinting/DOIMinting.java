/*
Copyright (c) 2014, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.doiMinting;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import net.sf.json.JSONObject;

/*import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;*/


/**
 * Servlet implementation class DOIMinting
 */
//@WebServlet("/mintDOI")
public class DOIMinting extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(DOIMinting.class.getName());
	
	String userMode;
	String dataUrl;
	String identifierType;  
	String identifierValue;
	String title;
	String publisher;
	String publicationYear;
	String contributorType;
	String version;
	
	String famillyNames[];
	String givenNames[];
	String groups[];
	Document doc;
	
    public DOIMinting() {
        super();
        userMode = "";
        dataUrl = "";
        identifierValue = "";
        doc = null;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		
		userMode = request.getParameter("userMode");
		dataUrl =  request.getParameter("txtDataUrl");
		
		identifierType = request.getParameter("txtIdenType"); // this return null since this field is disbled. if want to use this filed please create a hidden filed in js.
		identifierValue = request.getParameter("txtIdenValue");
		title = request.getParameter("txtTitle");
		publisher = request.getParameter("txtPublisher");
		publicationYear = request.getParameter("txtPublicationYear");
		contributorType = request.getParameter("txtContributorType");
		version = request.getParameter("txtVersion");
		
		if (contributorType.equals("Author/creator")){
			famillyNames = request.getParameterValues("txtFamillyName");
			givenNames = request.getParameterValues("txtGivenName");
		}else if (contributorType.equals("Group/research institution")){
			groups = request.getParameterValues("txtGroupName");
		}	
		
		log.info("mint/update doi request recevied.");
		
		boolean bIsMintDOI = false;
		
		if ((identifierValue.equals(""))){
			bIsMintDOI = true;
			log.info("request - Mint a doi.");
		}else{
			bIsMintDOI = false; //update
			log.info("request - Update the doi:" + identifierValue);
		}
		
		/*if (userMode.equals("add")){
			bIsMintDOI = true;
			log.info("MINT_DOI: request recevied.");
		}else{
			bIsMintDOI = false; //update
			log.info("UPDATE_DOI: request recevied. doi:" + identifierValue);
		}*/
		
		JSONObject json = new JSONObject();
		String sRetValue = ""; // MT001|10.5072/09/4FE936D87168C, MT002|Updated, ERRCODE|Error
		
		try {
			createDOIRequestXML(bIsMintDOI);
			DOIMintingClient doiClient = new DOIMintingClient();
	        if (bIsMintDOI){
	        	log.info("sending mint DOI request started.");
	        	sRetValue = doiClient.MintDOI(doc, dataUrl);
	        	log.info("completed minting DOI request.");
	        }else{
	        	log.info("sending update DOI request started.");
	        	sRetValue = doiClient.updateDOI(doc, dataUrl, identifierValue);
	        	log.info("completed update DOI request.");
	        }
	        
	        if (bIsMintDOI){
				String temp[] = sRetValue.split("\\|");
				if (temp.length == 2){
					if (temp[0].equals("ERROR_CONNECT")){
						json.put("ErrCode", "RDF_001");
						json.put("Value", temp[1]);
					}else{
						if (temp[0].equals("MT001")){
							identifierValue = temp[1];
						}
						json.put("ErrCode", temp[0]);
						json.put("Value", temp[1]);
					}
				}else{
					json.put("ErrCode", "RDF_001");
					json.put("Value", "Unknown Error");
				}
			}else {// bIsMintDOI = false , update;
				String[] temp = sRetValue.split("\\|");
				if (temp.length == 2){
					if (temp[0].equals("ERROR_CONNECT")){
						json.put("ErrCode", "RDF_001");
						json.put("Value", temp[1]);
					}else{
						json.put("ErrCode", temp[0]);
						json.put("Value", temp[1]);
					}
				}else{
					json.put("ErrCode", "RDF_001");
					json.put("Value", "Unknown Error");
				}
			}
	        
		} catch (Exception e) {
			log.info("Failed to mint/update DOI. " + e.getMessage());
			json.put("ErrCode", "RDF_002");
			json.put("Value", e.getMessage());
			response.getWriter().print(json);
		}

		log.info("sending mint/update DOI response to user.");
		response.getWriter().print(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public Document createDOIRequestXML(boolean bIsMintDOI) throws Exception{
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();
		
		if (doc == null){
			throw new Exception("Failed to create a document.");
		}
		
		Element root = doc.createElement("resource");
		root.setAttribute("xmlns", "http://datacite.org/schema/kernel-2.2");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation", "http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd");
		      
		doc.appendChild(root); 
		
		// identifier
        Element iden = doc.createElement("identifier");
        iden.setAttribute("identifierType", "DOI");
        
        if (! bIsMintDOI){	// Update
       	 	iden.appendChild(doc.createTextNode(identifierValue));
        }
        root.appendChild(iden);
    
        //Authors/Creators
      	Element creators = doc.createElement("creators");
      	if (contributorType.equals("Author/creator")){
      			// Creators
      		if (famillyNames.length == givenNames.length){
      			for(int i = 0; i < famillyNames.length; i++){
      				Element creator = doc.createElement("creator");
      				String fullName = famillyNames[i] + ", " + givenNames[i];
      				Element creatorName = doc.createElement("creatorName");
      				creatorName.appendChild(doc.createTextNode(fullName));
      				creator.appendChild(creatorName);
      				creators.appendChild(creator);
      			 }
      		}
      	}else if (contributorType.equals("Group/research institution")){
      		for(int i = 0; i < groups.length; i++){
      			Element creator = doc.createElement("creator");
      			String groupName = groups[i];
      			Element creatorName = doc.createElement("creatorName");
      			creatorName.appendChild(doc.createTextNode(groupName));
      			creator.appendChild(creatorName);
      			creators.appendChild(creator);
      		 }
      	}
      	root.appendChild(creators);
      		
        //Title
        Element titlesNode = doc.createElement("titles");
        Element titleNode = doc.createElement("title");
        titleNode.appendChild(doc.createTextNode(title));
        titlesNode.appendChild(titleNode);
        root.appendChild(titlesNode);
        
        //Publisher
        Element publisherNode = doc.createElement("publisher");
        publisherNode.appendChild(doc.createTextNode(publisher));
		root.appendChild(publisherNode);
        
        //Publication year
        Element publicationYearNode = doc.createElement("publicationYear");
        publicationYearNode.appendChild(doc.createTextNode(publicationYear));
		root.appendChild(publicationYearNode);
		
		// Version
		Element versionNode = doc.createElement("version");
		versionNode.appendChild(doc.createTextNode(version));
		root.appendChild(versionNode);
		
		if (doc == null){
			throw new Exception("Invalid document.");
		}
		
		/*TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\Apps\\doi_file.xml"));
		transformer.transform(source, result);*/
	
		return doc;
	}
}
