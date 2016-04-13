<#--
Copyright (c) 2014, QUT University
-->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dataCiteDOI. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
 
<#if statement.freeTextValue1??>
	${statement.freeTextValue1}
	<#if statement.freeTextValue2??>
		<a target="_blank" href="${statement.freeTextValue2}">${statement.freeTextValue2}</a>
	</#if>
<#else>
	UNKNOWN
</#if>
