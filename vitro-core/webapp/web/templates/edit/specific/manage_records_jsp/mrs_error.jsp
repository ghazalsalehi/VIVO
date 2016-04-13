<%--
Copyright (c) 2014, QUT University
All rights reserved.
--%>


<%@page language="java" import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet" %>


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
			
			<div id="heading-container" style="width: 100%; float: left; border-bottom: 1px dotted #B9BFC5; border-bottom: 1px dotted #B9BFC5;">
				<h2 class="selected-class">
					Invalid request.		 
				</h2>
			</div>

			<div style="margin-top:20px;float: left;">
				${MRS_error_msg}
			</div> 
            ${ftl_footer}

    </body>
</html>
