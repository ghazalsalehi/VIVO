<#--
Copyright (c) 2015, QUT University
All rights reserved.
-->
 
<#if statement.freeTextValue1??>
	<#-- href attr is set after page is loaded.-->
	<a class="show-RDRDataUpload" target="_blank" href="${statement.freeTextValue1}">${statement.freeTextValue1}</a>
<#else>
	UNKNOWN
</#if>