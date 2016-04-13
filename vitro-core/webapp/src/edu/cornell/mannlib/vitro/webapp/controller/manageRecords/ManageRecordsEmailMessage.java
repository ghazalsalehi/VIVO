package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;


import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailFactory;
import edu.cornell.mannlib.vitro.webapp.services.freemarker.FreemarkerProcessingService.TemplateProcessingException;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;


public class ManageRecordsEmailMessage extends FreemarkerHttpServlet{
	
	private static final Log log = LogFactory.getLog(ManageRecordsEmailMessage.class.getName());
	
	private String subject;
	private String websiteName;
	private String websiteEmailAddress;
	private String comments;
	private String msgText;
	List<String> recipients;
	Map<String, Object> emailValues = new HashMap<String, Object>();
	private String template;
	
	public ManageRecordsEmailMessage(Map<String, Object> emailValues){
		this.websiteName = "Research Data Finder";
		this.websiteEmailAddress = Utils.RDF_WEBSITE_EMAIL_ADDRESS;
		this.recipients = new ArrayList<String>();
		this.emailValues = emailValues;
		this.msgText = "";
		this.template = Utils.TEMPLATE_MANAGE_RECORDS_EMAIL; 
	}
	
	public void setTemplate(String templateName){
		this.template = templateName;
	}
	
	public void sendEmail(VitroRequest vreq) throws Exception{
		msgText = "";
		
		ApplicationBean appBean = vreq.getAppBean();

		String statusMsg = "";
		try {
			 // ----------------- For test purpose is localhost.
			 /*String host = "localhost";
        	 Properties properties = System.getProperties();
   	      	 properties.setProperty("mail.smtp.host", host);
   	      	 Session session = Session.getDefaultInstance(properties);*/
   	      	 // -----------------

	         try{
	        	 msgText = processTemplateToString(template, emailValues, vreq.getHttpServletRequest());
	        	 Session session = FreemarkerEmailFactory.getEmailSession(vreq);
	        	 String subject = (String) emailValues.get("subject");
	         	 sendMessage(session, recipients, subject, msgText);
	         }catch(Exception e){
	        	 log.error(e.getMessage());
	        	 throw new Exception(e.getMessage());
	         }
        } catch (AddressException e) {
            statusMsg = "Please supply a valid email address." +  e.getMessage();
            throw new Exception(statusMsg);
        } catch (SendFailedException e) {
            statusMsg = "The system was unable to deliver your mail.  Please try again later.  [SEND FAILED]" + e.getMessage();
            throw new Exception(statusMsg);
        } catch (MessagingException e) {
            statusMsg = "The system was unable to deliver your mail.  Please try again later.  [MESSAGING]" + e.getMessage();
            throw new Exception(statusMsg);
        }
	}
	
	private void sendMessage(Session s, List<String> recipients, String deliveryfrom, String msgText) 
    		throws AddressException, SendFailedException, MessagingException, Exception {
		
		if (recipients.size() <= 0){
			throw new Exception("Recipient list is empty.");
		}
        // Construct the message
        MimeMessage msg = new MimeMessage( s );
        //System.out.println("trying to send message from servlet");

        // Set the from address
        try {
            //msg.setFrom( new InternetAddress( webuseremail, webusername ));
            msg.setFrom( new InternetAddress( websiteEmailAddress, websiteName ));
            
        } catch (UnsupportedEncodingException e) {
        	log.error("Can't set message sender with personal name " + websiteName + 
        			" due to UnsupportedEncodingException");
            msg.setFrom( new InternetAddress( websiteEmailAddress ) );
        }

        // Set the recipient address
        InternetAddress[] address=new InternetAddress[recipients.size()];
        for (int i = 0; i <recipients.size(); i++){
            address[i] = new InternetAddress(recipients.get(i));
        }
        msg.setRecipients( Message.RecipientType.TO, address );

        // Set the subject and text
        msg.setSubject( deliveryfrom );

        // add the multipart to the message
        //msg.setContent(msgText,"text/html");
        msg.setDataHandler(new DataHandler(new ByteArrayDataSource(msgText.toString(), "text/html")));

        // set the Date: header
        msg.setSentDate( new Date() );

        Transport.send( msg ); // try to send the message via smtp - catch error exceptions

    }
	
	public void addRecipients(String email){
		recipients.add(email);
	}
}
