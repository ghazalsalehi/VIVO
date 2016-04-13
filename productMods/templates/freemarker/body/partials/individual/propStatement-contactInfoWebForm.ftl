<#--
Copyright (c) 2015, QUT University
All rights reserved.
-->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dataCiteDOI. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
 
<#if statement.freeTextValue1?? && statement.freeTextValue2?? && statement.freeTextValue3??>
	<div id="small-line">Name: ${statement.freeTextValue1} ${statement.freeTextValue2} ${statement.freeTextValue3}</div>
<#elseif statement.freeTextValue2?? && statement.freeTextValue3??>
	<div id="small-line">Name: ${statement.freeTextValue2} ${statement.freeTextValue3}</div>
<#elseif statement.freeTextValue3??>
	<div id="small-line">Name: ${statement.freeTextValue3}</div>
</#if>

<#if statement.freeTextValue4??>
    <div id="small-line">Email: <a href="mailto:${statement.freeTextValue4}" target="_top">${statement.freeTextValue4}</a></div>
</#if>
<#if statement.freeTextValue6??>
	<div id="small-line">Phone: ${statement.freeTextValue6}</div>
</#if>
<#if statement.freeTextValue7??>
	<div id="small-line">Fax: ${statement.freeTextValue7}</div>
</#if>
<#if statement.freeTextValue5??>
	<div id="small-line">More: <a href="${statement.freeTextValue5}" target="_blank">${statement.freeTextValue5}</a></div>
</#if>