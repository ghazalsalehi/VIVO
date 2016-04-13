<#--
Copyright (c) 2014, QUT University
-->

 
<#if statement.infoType1??>
	<#if statement.freeTextValue1??>
		<#if statement.freeTextValue2?? && statement.freeTextValue3??>
			<a target="_blank" href="${statement.freeTextValue2}">${statement.freeTextValue3}</a>
		</#if>
	<#else>
		UNKNOWN
	</#if>
<#else>
	UNKNOWN
</#if>
