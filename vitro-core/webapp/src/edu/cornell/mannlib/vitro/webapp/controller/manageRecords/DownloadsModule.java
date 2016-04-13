package edu.cornell.mannlib.vitro.webapp.controller.manageRecords;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;


public class DownloadsModule extends ManageRecordsModule{
	
	private static final Log log = LogFactory.getLog(DownloadsModule.class.getName());
	
	public DownloadsModule(){
		
	}
	
	@Override
	void processRequest(VitroRequest vreq, HttpServletResponse response) throws IOException {
		String key = vreq.getParameter(Utils.MRS_MODULE_PARAM_KEY);
		String type = vreq.getParameter(Utils.MRS_MODULE_PARAM_TYPE); //inline or attachment
	
		if (key != null ){
			// LIBRDF-94
			if (key.equals("quickGuide")){
				download(Utils.MRS_MODULE_DOWNLOADS_QUICK_GUIDE_DOC, type, vreq, response, "msword");
			}
		}
		
		doJSPResponse(vreq, response, Controllers.MRS_DOWNLOADS_JSP);
	}
	
	public void download(String fileName, String downloadType,  VitroRequest vreq, HttpServletResponse response, String format) throws IOException{
		String relativeFilePath = Utils.MRS_MODULE_DOWNLOADS_FILE_PATH + fileName;
		ServletContext context = vreq.getSession().getServletContext();
		String contextPath = context.getRealPath(File.separator);
		File docFile = new File(contextPath + relativeFilePath);

		response.setContentType("application/" + format);
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentLength((int) docFile.length());

		FileInputStream fileInputStream = new FileInputStream(docFile);
		OutputStream responseOutputStream = response.getOutputStream();
		int bytes;
		while ((bytes = fileInputStream.read()) != -1) {
			responseOutputStream.write(bytes);
		}
		
		fileInputStream.close();
		responseOutputStream.close();
	}
}
