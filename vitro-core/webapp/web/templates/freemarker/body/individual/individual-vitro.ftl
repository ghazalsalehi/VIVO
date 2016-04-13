<#--
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->


<#if !labelCount??>
    <#assign labelCount = 0 >
</#if>

<#--
<#assign email = "" />
<#assign phone = "" />
<#assign fax = "" />
<#assign maillingAdrr = "" />
-->

<#assign individualMostSpecificTypeName = "" >
<#list individual.mostSpecificTypes as msType>
  <#assign individualMostSpecificTypeName = "${msType}" >
</#list>  


<#--
CR:028
-->
<#assign MainType = "" />
<#assign MainTypeURL = "" />
<#assign subTypeURL = "" />

<#assign rdfUrl = individual.rdfUrl>
<#if rdfUrl??>
	<script>
		var individualRdfUrl = "";
		individualRdfUrl = '${rdfUrl}';
	</script>
</#if>

<#-- Store title in a variable to display person's title CR:020-->
<#assign contactInfo = propertyGroups.getProperty("${qutCore}contactInfo")!>
<#if contactInfo?has_content>
	<#assign personTitle = "">
	<#list contactInfo.statements as statement>
		<#if statement.freeTextValue1??>
			<#assign personTitle = "${statement.freeTextValue1}" />
		</#if>
	</#list>
</#if>

<#assign dateRecordModified = propertyGroups.getProperty("${qutCore}dateRecordModified")!>
<#if dateRecordModified?has_content>
	<#assign recordModifiedDate = "">
	<#list dateRecordModified.statements as statement>
		<#if statement.freeTextValue1??>
			<#assign recordModifiedDate = "${statement.freeTextValue1}" />
		</#if>
	</#list>
</#if>

<#--
	TODO: Get this information from the server.
-->
<#if individualMostSpecificTypeName == "Group">
	<#assign MainType = "People & Groups" >
	<#assign MainTypeURL = "people" >
	<#assign subTypeURL = "people#http://www.qut.edu.au/ontologies/vivoqut#group" />
<#elseif individualMostSpecificTypeName == "Researcher">
	<#assign MainType = "People & Groups" >
	<#assign MainTypeURL = "people" >
	<#assign subTypeURL = "people#http://www.qut.edu.au/ontologies/vivoqut#researcher" />
<#elseif individualMostSpecificTypeName == "Administrative position">
	<#assign MainType = "People & Groups" >
	<#assign MainTypeURL = "people" >
	<#assign subTypeURL = "people#http://www.qut.edu.au/ontologies/vivoqut#administrativePosition" />
<#elseif individualMostSpecificTypeName == "Project">
	<#assign MainType = "Projects" >
	<#assign MainTypeURL = "activities" >
	<#assign subTypeURL = "activities#http://www.qut.edu.au/ontologies/vivoqut#project" />
<#elseif individualMostSpecificTypeName == "Research data set">
	<#assign MainType = "Data Collections" >
	<#assign MainTypeURL = "dataCollections" >
	<#assign subTypeURL = "dataCollections#http://www.qut.edu.au/ontologies/vivoqut#researchDataSet" />
<#elseif individualMostSpecificTypeName == "Repository">
	<#assign MainType = "Data Collections" >
	<#assign MainTypeURL = "dataCollections" >
	<#assign subTypeURL = "dataCollections#http://www.qut.edu.au/ontologies/vivoqut#repository" />
<#elseif individualMostSpecificTypeName == "Registry">
	<#assign MainType = "Data Collections" >
	<#assign MainTypeURL = "dataCollections" >
	<#assign subTypeURL = "dataCollections#http://www.qut.edu.au/ontologies/vivoqut#registry" />
<#elseif individualMostSpecificTypeName == "Collection">
	<#assign MainType = "Data Collections" >
	<#assign MainTypeURL = "dataCollections" >
	<#assign subTypeURL = "dataCollections#http://www.qut.edu.au/ontologies/vivoqut#collection" />
<#elseif individualMostSpecificTypeName == "Catalogue or index">
	<#assign MainType = "Data Collections" >
	<#assign MainTypeURL = "dataCollections" >
	<#assign subTypeURL = "dataCollections#http://www.qut.edu.au/ontologies/vivoqut#catalogueOrIndex" />
<#elseif individualMostSpecificTypeName == "Service">
	<#assign MainType = "Equipment & Services" >
	<#assign MainTypeURL = "services" >
	<#assign subTypeURL = "services#http://www.qut.edu.au/ontologies/vivoqut#create" />
<#elseif individualMostSpecificTypeName == "Equipment">
	<#assign MainType = "Equipment & Services" >
	<#assign MainTypeURL = "services" >
	<#assign subTypeURL = "services#http://www.qut.edu.au/ontologies/vivoqut#assemble" />
<#elseif individualMostSpecificTypeName == "Source code">
	<#assign MainType = "Software and Code" >
	<#assign MainTypeURL = "softwareAndCode" >
	<#assign subTypeURL = "softwareAndCode#http://www.qut.edu.au/ontologies/vivoqut#code" />
<#elseif individualMostSpecificTypeName == "Binaries">
	<#assign MainType = "Software and Code" >
	<#assign MainTypeURL = "softwareAndCode" >
	<#assign subTypeURL = "softwareAndCode#http://www.qut.edu.au/ontologies/vivoqut#software" />
<#else>
	<#assign MainType = "Spatial Data" >
	<#assign MainTypeURL = "spatialData" >
	<#assign subTypeURL = "spatialData#http://www.qut.edu.au/ontologies/vivoqut#builtEnvironment" />
</#if>

<div id = "breadcrumb">
	<p class="hide">You are here:</p>
	<ul>
		<li class="home">
			<a title="Go to homepage" href=<#if env=="scf">"/scf"<#elseif env=="spatial">"/spatial"<#else>"/"</#if>>
				<span class="hide">Homepage</span>
			</a>
		</li>
		<li>
			<a href="/${MainTypeURL}">${MainType}</a>
		</li>
		<li>
			<a href="/${subTypeURL}">${individualMostSpecificTypeName}</a>
		</li>
	</ul>
</div>

<#--	
<#assign individualMostSpecificTypeName>
	${individual.mostSpecificTypes}
</#assign>
-->

<#-- 
<#assign individualImage>
	<@p.image individual=individual 
			propertyGroups=propertyGroups 
 		    namespaces=namespaces 
			editable=editable 
			showPlaceholder="with_add_link" />
</#assign>
-->

<#if recordModifiedDate??>
	<span class="recordModifiedDate hide">${recordModifiedDate}</span>
</#if>
<span class="individualLocalName hide">${individual.localName}</span>

<#if individualMostSpecificTypeName == "Binaries" || individualMostSpecificTypeName == "Source code">
	<#if displayMode?? && (displayMode == "edit")>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#include "individual-vitro-software-and-code.ftl">
	</#if>
<#elseif individualMostSpecificTypeName == "Researcher" || individualMostSpecificTypeName == "Administrative position">
	<#if displayMode?? && (displayMode == "edit")>
		<#assign individualImage>
			<@p.image individual=individual 
					propertyGroups=propertyGroups 
					namespaces=namespaces 
					editable=editable 
					showPlaceholder="with_add_link" />
		</#assign>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#assign individualImage>
			<@p.image individual=individual 
					propertyGroups=propertyGroups 
					namespaces=namespaces 
					editable=false 
					showPlaceholder="with_add_link" />
		</#assign>
		<#include "individual-vitro-people.ftl">
	</#if>
<#elseif individualMostSpecificTypeName == "Group">
	<#if displayMode?? && (displayMode == "edit")>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#include "individual-vitro-group.ftl">
	</#if>
<#elseif individualMostSpecificTypeName == "Project">
	<#if displayMode?? && (displayMode == "edit")>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#include "individual-vitro-project.ftl">
	</#if>
<#elseif individualMostSpecificTypeName == "Repository" || individualMostSpecificTypeName == "Registry" || individualMostSpecificTypeName == "Catalogue or index" || individualMostSpecificTypeName == "Collection" || individualMostSpecificTypeName == "Research data set">
	<#if displayMode?? && (displayMode == "edit")>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#include "individual-vitro-dataCollection.ftl">
	</#if>
<#elseif individualMostSpecificTypeName == "Annotate" || individualMostSpecificTypeName == "Report" || individualMostSpecificTypeName == "Syndicate RSS" || individualMostSpecificTypeName == "Syndicate ATOM" || individualMostSpecificTypeName == "Harvest OAI-PMH" || 
		individualMostSpecificTypeName == "Search Z39.50" || individualMostSpecificTypeName == "Search SRU" || individualMostSpecificTypeName == "Search SRW" || individualMostSpecificTypeName == "Service" || individualMostSpecificTypeName == "Transform" ||
		individualMostSpecificTypeName == "Search open search" || individualMostSpecificTypeName == "Equipment" || individualMostSpecificTypeName == "Search HTTP" || individualMostSpecificTypeName == "Generate">
	<#if displayMode?? && (displayMode == "edit")>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#include "individual-vitro-softwareAndEquipment.ftl">
	</#if>
<#elseif individualMostSpecificTypeName == "Farming" || individualMostSpecificTypeName == "Economy" || individualMostSpecificTypeName == "Health" || individualMostSpecificTypeName == "Biota" || individualMostSpecificTypeName == "Geoscientific information" || 
		individualMostSpecificTypeName == "Environment" || individualMostSpecificTypeName == "Climate" || individualMostSpecificTypeName == "Energy/Utilities" || individualMostSpecificTypeName == "Intelligence/Military" || individualMostSpecificTypeName == "Structure" ||
		individualMostSpecificTypeName == "Transportation" || individualMostSpecificTypeName == "Boundaries" || individualMostSpecificTypeName == "Location" || individualMostSpecificTypeName == "PlanningCadastre" || individualMostSpecificTypeName == "Base maps" || individualMostSpecificTypeName == "Elevation" ||
		individualMostSpecificTypeName == "Inland waters" || individualMostSpecificTypeName == "Oceans"	|| individualMostSpecificTypeName == "Society and Culture">
	<#if displayMode?? && (displayMode == "edit")>
		<#include "individual-vitro-editMode.ftl">
	<#else>
		<#include "individual-vitro-spatialData.ftl">
	</#if>
<#else>
	<#include "individual-vitro-editMode.ftl">
</#if>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualDisplayUtils.js"></script>')}
