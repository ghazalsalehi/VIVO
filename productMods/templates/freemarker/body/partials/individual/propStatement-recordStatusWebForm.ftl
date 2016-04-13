<#--
Copyright (c) 2014, QUT University
All rights reserved.
-->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dataCiteDOI. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#if statement.freeTextValue1??>
	<#assign type = statement.freeTextValue1 />
	
	<#if type == "Draft">
		Draft
	<#elseif type == "UnderReview">
		Under review
	<#elseif type == "Approved">
		Approved
	<#elseif type == "PublishedQUTAccess">
		Published - QUT Access
	<#elseif type == "PublishedOpenAccess">
		Published - Open Access
	<#else>
		${statement.freeTextValue1}
	</#if>
<#else>
	UNKNOWN
</#if>