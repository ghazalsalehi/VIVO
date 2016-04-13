<%--
Copyright (c) 2014, QUT University
All rights reserved.
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@page language="java" import="java.util.*" %>
<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.*" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vedit.beans.Option" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet" %>
<% request.setAttribute("requestedActions", SimplePermission.SEE_QUT_MANAGE_RECORDS_PAGE.ACTION); %>
<vitro:confirmAuthorization />

<%  /***********************************************
         Display a single Page  in the most basic fashion.
         The html <HEAD> is generated followed by the banners and menu.
         After that the result of the jsp in the attribute bodyJsp is inserted.
         Finally comes the footer.
         
         request.attributes:                    
            "bodyJsp" - jsp of the body of this page.
            "title" - title of page
            "css" - optional additional css for page
            "scripts" - optional name of file containing <script> elements to be included in the page
            "bodyAttr" - optional attributes for the <body> tag, e.g. 'onload': use leading space
            
          Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output
          for debugging info.
                 
         bdc34 2006-02-03 created
        **********************************************/
        /*
        String e = "";
        if (request.getAttribute("bodyJsp") == null){
            e+="basicPage.jsp expects that request parameter 'bodyJsp' be set to the jsp to display as the page body.\n";
        }
        if (request.getAttribute("title") == null){
            e+="basicPage.jsp expects that request parameter 'title' be set to the title to use for page.\n";
        }
        if (request.getAttribute("css") == null){
            e+="basicPage.jsp expects that request parameter 'css' be set to css to include in page.\n";
        }
        if( request.getAttribute("appBean") == null){
            e+="basicPage.jsp expects that request attribute 'appBean' be set.\n";
        }
        if( e.length() > 0 ){
            throw new JspException(e);
        }
        */
%>


<% /* Prepare Freemarker components to allow .ftl templates to be included from jsp */
    FreemarkerHttpServlet.getFreemarkerComponentsForJsp(request);
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        ${ftl_head}
        
        <c:if test="${!empty scripts}"><jsp:include page="${scripts}"/></c:if>
    </head>
    
    <body ${requestScope.bodyAttr}>
            ${ftl_identity}
            
            ${ftl_menu}
			
				<div class="content-header">
					<div class="mr-menu">
						<ul class="page-controls">
						<!--<li class="page active"><a href="/manageRecords?module=dashboard">Dashboard</a></li>-->
						<li class="page"><a href="/manageRecords?module=downloads">Downloads</a></li>
						<li class="page"><a href="/manageRecords?module=workspace">Workspace</a></li>
						<li class="page"><a href="/manageRecords?module=statistics">Statistics</a></li>
						<!-- <li class="page"><a href="/manageRecords?module=siteAdmin">Settings</a></li> -->
					</ul>
					</div>
				</div> 
				<div id="mr-dashboard-body">
					
					<div id="right-col" style="width:100%">
						<p class="individuals-heading" style="color:#FFF;padding-left:8px;margin:0px;font-size: 12px;text-align: center;">Dashboard</p>
						<ul id="individuals-list" role="region" style="border-bottom: 2px solid #CCC;
border-left: 1px solid #CCC;
border-right: 1px solid #CCC;
overflow: hidden;">
							<li class="individual" role="listitem" style="height: 30px;
padding-top: 5px;
padding-left: 5px;text-align: center;">
								This feature will available soon.
							</li>
						</ul>
					</div>
				</div>
            
            ${ftl_footer}

    </body>
</html>

<script type="text/javascript" src="/edit/forms/qut_libs/manage_records_libs/js/mrs_workspace.js"></script>
<script type="text/javascript" src="/edit/forms/qut_libs/rdfballoon/js/jquery-rdfballoon.js"></script>
<script type="text/javascript" src="/edit/forms/qut_libs/common/js/jquery-2.0.2.min.js"></script>

<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.9.1.js"></script>
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<link rel="stylesheet" href="/resources/demos/style.css">

<link rel="stylesheet" href="/edit/forms/qut_libs/rdfballoon/css/rdfballoon.css" type="text/css">
<link rel="stylesheet" href="/edit/forms/qut_libs/manage_records_libs/css/manage_records.css" type="text/css">
