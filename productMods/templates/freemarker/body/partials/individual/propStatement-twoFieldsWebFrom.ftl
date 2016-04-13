<#--
Copyright (c) 2015, QUT University
All rights reserved.
-->
 
<#if statement.infoType1??>
	<#if statement.infoType1 == "uri" || statement.infoType1 == "AU-ANL:PEAU" || statement.infoType1 == "url" || statement.infoType1 == "scopus" || statement.infoType1 == "thomsonreuters">
		<#if statement.freeTextValue1??>
			<a target="_blank" href="${statement.freeTextValue1}">${statement.freeTextValue1}</a>
		</#if>
	<#elseif statement.infoType1 == "orcid">
		<#if statement.freeTextValue1??>
			<a target="_blank" href="http://orcid.org/${statement.freeTextValue1}">http://orcid.org/${statement.freeTextValue1}</a>
		</#if>
	<#elseif statement.infoType1 == "email">
		<#if statement.freeTextValue1??>
			<a href="mailto:${statement.freeTextValue1}" target="_top">${statement.freeTextValue1}</a>
		</#if>
	<#elseif statement.infoType1 == "Internal" || statement.infoType1 == "External">
		${statement.infoType1}
	<#else>
		<#if statement.freeTextValue1??>
			${statement.freeTextValue1}
		<#else>
		</#if>
	</#if>
<#else>
	UNKNOWN
</#if>
