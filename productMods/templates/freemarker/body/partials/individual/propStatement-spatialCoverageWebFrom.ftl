<#--
Copyright (c) 2014, QUT University
-->
		
<#if statement.infoType1??>
	<#assign type = "${statement.infoType1}">        
	<#if statement.freeTextValue1??>
	 	<#assign value = "${statement.freeTextValue1}">       
	<#else>
		<#assign value = "">       
	</#if>
<#else>
	<#assign type = "">       
	<#assign value = "">
</#if>


<div class="sptialStatContainerSelector" style="margin-bottom: 10px;">
	<div class="spatialCoverageType">${type}</div>
	<div class="spatialCoverageValue">${value}</div>
</div>


