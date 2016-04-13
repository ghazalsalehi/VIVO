<%--
Copyright (c) 2013, QUT University
All rights reserved.
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.QUT_RESEARCH_DATA_PAGE.ACTION); %>
<vitro:confirmAuthorization />

<div class="staticPageBackground">

<h2>Ingest RM records</h2>

<form action="admin/manageQUTActions?action=ingestRMData" method="post"  enctype="multipart/form-data" >

    <input name="file1" type="file" size="60"/> <br/>
	<input name="file2" type="file" size="60"/> <br/>
	<input name="file3" type="file" size="60"/>  <br/>
	<input name="file4" type="file" size="60"/>  <br/>
	<input name="file5" type="file" size="60"/>  <br/>
	
    <p><input id="submit" type="submit" name="Submit" value="Ingest research data" /></p>     
</form>

<h2>Generate RIF-CS Feed</h2>

<form action="admin/manageQUTActions?action=createFeed" method="post" >
    <p><input id="submit" type="submit" name="Submit" value="Create RIF-CS feed" /></p>     
</form>

<h2>Generate Sitemap</h2>

<form action="admin/manageQUTActions?action=createSitemap" method="post" >
    <p><input id="submit" type="submit" name="Submit" value="Create Site Map" /></p>     
</form>

</div>
