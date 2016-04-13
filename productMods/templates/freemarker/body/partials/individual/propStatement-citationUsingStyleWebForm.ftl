<#--
Copyright (c) 2013, QUT University
All rights reserved.
-->
 
<#if statement.infoType1??>
	<#if statement.freeTextValue1??>
			${statement.freeTextValue1} 
			<#if statement.freeTextValue2??>
				<#assign str = statement.freeTextValue2>
				<#if  str?matches("http.*") || str?matches("www.*")>
					<a target="_blank" href="${statement.freeTextValue2}">${statement.freeTextValue2}</a>
				<#else>
					<a target="_blank" href="http://dx.doi.org/${statement.freeTextValue2}">${statement.freeTextValue2}</a>
				</#if>
			</#if>
	</#if>
<#else>
	UNKNOWN
</#if>
