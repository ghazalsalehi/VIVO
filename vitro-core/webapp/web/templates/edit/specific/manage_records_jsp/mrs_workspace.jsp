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
						<!-- <li class="page"><a href="/manageRecords?module=dashboard">Dashboard</a></li> -->
						<li class="page"><a href="/manageRecords?module=downloads">Downloads</a></li>
						<li class="page active"><a href="/manageRecords?module=workspace">Workspace</a></li>
						<li class="page"><a href="/manageRecords?module=statistics">Statistics</a></li>
						<!-- <li class="page"><a href="/manageRecords?module=siteAdmin">Settings</a></li> -->
					</ul>
					</div>
				</div> 
				<div id="mr-workspace-body">
					<div role="navigation" id="left-col">
						<div id="navigation">
							<p class="section-heading">My records</p>
							<ul id="review-status">
								<li id="Draft" class="selected"><a title="Browse all draft records in this class" >Draft <span class="count-classes">(<%=request.getAttribute("draftRecordsCount")%>)</span></a></li>
								<li id="UnderReview"><a title="Browse all Under review records in this class" >Under review <span class="count-classes">(<%=request.getAttribute("underReviewRecordsCount")%>)</span></a></li>
								<li id="PublishedOpenAccess"><a title="Browse all Publish - open access records in this class" >Published - Open access <span class="count-classes">(<%=request.getAttribute("publishedOpenAccessRecordsCount")%>)</span></a></li>
								<li id="PublishedQUTAccess"><a title="Browse all Publish - qut access records in this class" >Published - QUT access <span class="count-classes">(<%=request.getAttribute("publishedQUTAccessRecordsCount")%>)</span></a></li>
							</ul>
						</div>
						<div id="addNewRecord">
							<p class="section-heading">Add new record</p>
							<div class="quick-guide"><a href='/manageRecords?module=downloads&key=quickGuide'>Download Quick Guide</a></div>
							<% Map<String, Object> dataInput = (Map<String, Object>)request.getAttribute("dataInput"); %>	
												
							<form id="addIndividualClass" action="<%=dataInput.get("formAction")%>" method="get">
								<div id="optionList">
									<select id="VClassURI" name="typeOfNew" class="form-item long-options" role="select" onChange="runEffect(this)">
										<%
											LinkedHashMap<String, List<Option>> orderedClassGroups = (LinkedHashMap<String, List<Option>>)dataInput.get("groupedClassOptions");
											Iterator entries = orderedClassGroups.entrySet().iterator();
											while (entries.hasNext()) {
												Map.Entry entry = (Map.Entry) entries.next();
												String key = (String)entry.getKey();
												
												if (key.equals("Services")){%>
													<optgroup label="Equipment & Services">
												<%}else if (key.equals("People")){%>
													<optgroup label="People and Groups">
												<%}else if (key.equals("Data Collections")){%>
													<optgroup label="Data">
												<%}else if (key.equals("Activities") || key.equals("Projects")){%>
													<optgroup label="Projects">
												<%}else if (key.equals("Software and Code") || key.equals("Binaries and Source code")){%>
													<optgroup label="Software Finder">
												<%}else if (key.equals("Spatial Data")){%>
													<optgroup label="Spatial Data Finder">
												<%}
												List<Option> options = (List<Option>)entry.getValue();
												for(Option opt : options){%>
													<option value=<%=opt.getValue()%> <%if(opt.getSelected()){%>selected="selected"><%}%>><%=opt.getBody()%></option>
												<%}%>
												</optgroup>
										<%	}
										%>	
									</select>
								</div>
								<div id="submitBtn">
									<input type="hidden" name="editForm" value="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.NewIndividualFormGenerator" role="input" />
									<input type="submit" id="submit" value="Add" role="button" />
								</div>
								
								<div class="toggler">
								  <div id="effect" class="create-newrecord-helptext">
										<p>A researcher is a human being; or an identity (or role) assumed by one or more human beings. Example: <a href='http://researchdata.ands.org.au/professor-martin-sillence' target='_blank'>Professor Martin Sillence</a></p>
								  </div>
								</div>
							</form>
						</div>
						<%
						String loggedInuserRoleLevel = (String)request.getAttribute("loggedInuserRoleLevel");
						if ((loggedInuserRoleLevel.equals("siteAdmin")) || (loggedInuserRoleLevel.equals("self")) ){%>
							<div id="admin_navigation">
								<p class="section-heading">My jobs</p>
								<ul id="review-status">
									<li id="ReadyToReview"><a title="Browse all individuals in this class" >New <span class="count-classes">(<%=request.getAttribute("readyToReviewRecordsCount")%>)</span></a></li>
									<li id="BeingReviewd"><a title="Browse all individuals in this class" >Records for review <span class="count-classes">(<%=request.getAttribute("beingReviewedByAdminRecordsCount")%>)</span></a></li>
									<li id="PublishedByMe"><a title="Browse all individuals in this class" >Published by me <span class="count-classes">(<%=request.getAttribute("publishedByAdminRecordsCount")%>)</span></a></li> 
									<li id="AssignedByMe"><a title="Browse all individuals in this class" >Records I have delegated <span class="count-classes">(<%=request.getAttribute("assignedByAdminRecordsCount")%>)</span></a></li>
								</ul>
							</div>
						<%}else if (loggedInuserRoleLevel.equals("curator")){%>
							<div id="admin_navigation">
								<p class="section-heading">My jobs</p>
									<ul id="review-status">
									<li id="BeingReviewd"><a title="Browse all individuals in this class" >Records for review <span class="count-classes">(<%=request.getAttribute("beingReviewedByAdminRecordsCount")%>)</span></a></li>
									<li id="PublishedByMe"><a title="Browse all individuals in this class" >Published by me <span class="count-classes">(<%=request.getAttribute("publishedByAdminRecordsCount")%>)</span></a></li>
									<li id="AssignedByMe"><a title="Browse all individuals in this class" >Records I have delegated <span class="count-classes">(<%=request.getAttribute("assignedByAdminRecordsCount")%>)</span></a></li>
								</ul>
							</div>
						<%}%>
					</div>
					
					<div id="right-col">
						<p class="individuals-heading">${individuals_heading}</p>
						<ul id="individuals-list" role="region">
							<!-- populate using AJAX request -->
						</ul>
						<div class="mrs-pagination">
							
						</div>
					</div>
				</div>
            
            ${ftl_footer}

    </body>
</html>

<script type="text/javascript" src="/edit/forms/qut_libs/common/js/jquery-2.0.2.min.js"></script>
<script type="text/javascript" src="/edit/forms/qut_libs/messi/js/messi.min.js"></script>
<script type="text/javascript" src="/edit/forms/qut_libs/growl/js/jquery.growl.js"></script>
<script type="text/javascript" src="/edit/forms/qut_libs/manage_records_libs/js/mrs_workspace.js"></script>
<script type="text/javascript" src="/edit/forms/qut_libs/rdfballoon/js/jquery-rdfballoon.js"></script>

<link rel="stylesheet" href="/edit/forms/qut_libs/rdfballoon/css/rdfballoon.css" type="text/css">
<link rel="stylesheet" href="/edit/forms/qut_libs/messi/css/messi.min.css" type="text/css">
<link rel="stylesheet" href="/edit/forms/qut_libs/growl/css/jquery.growl.css" type="text/css">
<link rel="stylesheet" href="/edit/forms/qut_libs/manage_records_libs/css/manage_records.css" type="text/css">
