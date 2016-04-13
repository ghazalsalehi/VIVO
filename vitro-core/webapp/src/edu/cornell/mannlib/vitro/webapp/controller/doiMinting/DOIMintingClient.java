package edu.cornell.mannlib.vitro.webapp.controller.doiMinting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;


/*  ANDS JIRA:  SD-6136
 *  Production :
	Name = Queensland University of Technology
	appID = 9de2dd716eddd8315e5d455688ce8ce514d229ad
	IP address = 131.181.108.134
	DOI prefix = 10.4225/
	
	Testing:
	Name = TEST: Queensland University of Technology
	appID = e0b39ccb0f16d11768bf5ce0b4f81ee1e9a44e87
	IP address = 131.181.24.179-131.181.108.135
	DOI prefix = 10.5072/
 */


public class DOIMintingClient {
	
	private static final Log log = LogFactory.getLog(DOIMintingClient.class.getName());
	
	//String appID = "9de2dd716eddd8315e5d455688ce8ce514d229ad";  

    private boolean parseDoc(Document doc) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        try {
        // whether <response type="success"> was returned
        XPathExpression expr = xpath.compile("//title");
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
        	Node node = (Node) nodes.item(i);
        	System.out.println(node.getTextContent());
        }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        
        return false;
    }
    
    public String xmlDocAsString(Document d) {
    	String xml="";
    	
    	try {
	    	TransformerFactory factory = TransformerFactory.newInstance();
	    	Transformer transformer = factory.newTransformer();
	    	StringWriter writer = new StringWriter();
	    	Result result = new StreamResult(writer);
	    	Source source = new DOMSource(d);
	    	transformer.transform(source, result);
	    	writer.close();
	    	xml = writer.toString();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return xml;
    }
      
	
	public String MintDOI(Document xmlDoc, String dataURL){
		//String postURL = "https://services.ands.org.au/doi/1.1/mint.string/?app_id=" + Utils.ANDS_MINT_DOI_TEST_APP_ID + "&url=" + dataURL;	// test - DOI prefix = 10.5072/
		String postURL = "https://services.ands.org.au/doi/1.1/mint.string/?app_id=" + Utils.ANDS_MINT_DOI_APP_ID + "&url=" + dataURL;	// prod - DOI prefix = 10.4225/
		return postDOIRequest(xmlDoc, postURL);
	}
	
	public String updateDOI(Document xmlDoc, String dataURL, String doi){
		//String postURL = "https://services.ands.org.au/doi/1.1/update.string/?app_id=" + Utils.ANDS_MINT_DOI_TEST_APP_ID + "&doi=" + doi + "&url=" + dataURL; // test
		String postURL = "https://services.ands.org.au/doi/1.1/update.string/?app_id=" + Utils.ANDS_MINT_DOI_APP_ID + "&doi=" + doi + "&url=" + dataURL; // prod
		return postDOIRequest(xmlDoc, postURL);
	}
	
	public String postDOIRequest(Document xmlDoc, String postURL) {
		
		 HttpResponse response = null;
		 String xml = xmlDocAsString(xmlDoc);
		 
		 xml = xml.replaceAll(" xmlns=\"\"", "");
		    	     
	     HttpClient client = new DefaultHttpClient();
	     client.getParams().setParameter("http.useragent", "xxxx");
	     client.getParams().setParameter("Content-Encoding", "UTF-8");

	     log.info("creating http post for : " + postURL);
	     HttpPost httppost = new HttpPost(postURL);
	     String sRetValue = ""; 
	        
	     HttpParams params = new SyncBasicHttpParams();
	     
	      try {
	            HttpEntity entity = new StringEntity(xml, "UTF-8");            
	            client.getParams().setParameter("Content-Length", entity.getContentLength());
	            
	            log.info("XML request length : " + entity.getContentLength());         
	                        
	            List nameValuePairs = new ArrayList(1);
	            nameValuePairs.add(new BasicNameValuePair("xml", xml));
	          
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
	            log.info("HttpPost Excecute started."); 
	            response = client.execute(httppost);
	            log.info("HttpPost Excecute finished."); 
	            int statusCode = response.getStatusLine().getStatusCode();  
	            log.info("status code is: " + statusCode); 
	        } 
	        catch (Exception e) {        
	        	log.error("Error Executing Request :" + e.getMessage());

			    String sExecption = "ERROR_CONNECT|";
			    Writer writer = new StringWriter();
			    final PrintWriter printWriter = new PrintWriter(writer);
			    final Throwable throwable = new IllegalArgumentException("Error Executing Request : ");
			    throwable.printStackTrace(printWriter);
			    sExecption += writer.toString();
		       	
			    return sExecption;
	        }        
	     
	                
	        HttpEntity resEntity = response.getEntity();
	        
	        log.info("----------------------------------------"); 
	        log.info(response.getStatusLine()); 
	        
	        if (resEntity != null) {
	        	log.info("Response content length: " + resEntity.getContentLength());
	        	log.info("Chunked?: " + resEntity.isChunked());
	        	try {
	        		sRetValue = EntityUtils.toString(resEntity);
	        		System.out.println(sRetValue);
	        		log.info("Return Value: " + sRetValue);
	        	}
	        	catch(Exception e) {
	        		log.error("Error converting the response: " + e.getMessage());
	        	}
	        }
	        
	    
	        
	        String sResCode = "";
	        
	        if (!sRetValue.equals("")){
	        	String[] temp;
		    	String delimiter = " ";
		    	temp = sRetValue.split(delimiter);

				if (temp.length > 3){
		    		Pattern p = Pattern.compile("\\[(MT.*?)\\]");
		    		Matcher m = p.matcher(temp[0]);
		    		
		    		if(m.find()) {
		    			sResCode  = m.group(1);
		    			
		    			if (sResCode.equals("MT001")){
		    				sResCode += "|" + temp[2];	// MT001|10.5072/09/4FE936D87168C
		    			}else if(sResCode.equals("MT002")) {
		    				sResCode += "|Updated"; 	// MT001|Updated
		    			}
		    			else{
		    				String[] temp1;
		    		    	String delimiter1 = "<br />";
		    		    	temp1 = sRetValue.split(delimiter1);
		    		    	
		    		    	if (temp1.length >= 2){
		    		    		sResCode += "|" + temp1[1] ;
		    		    	}else{
		    		    		sResCode += "|Error";		// MT001|Error
		    		    	}
		    			}
		    		}
		    	}
	        }
	       
	        log.info("Sending response back to the user. response code is : " + sResCode);
	        return sResCode;
	}
}

