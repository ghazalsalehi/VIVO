<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTION); %>
<vitro:confirmAuthorization />

<div class="staticPageBackground">

<h2>Download AP party records</h2>

<% 
if (request.getAttribute("error") != null) {
%>
	<div class='manage_qut_rd_error'>Following error occured while processing your request. <br/><br/> ${error}</div>
<%
} else {
%>
	<div class='manage_qut_rd_success'>${msg}</div>
<% 
} 
%>
</div>
