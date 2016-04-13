<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign inputValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtValue") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#if propertyName == "RMPersonCode">
	<#if recordType == "people">
		<#assign helptext = "A ResearchMaster person code is a unique code assigned to people/group records that have been ingested from ResearchMaster.">
	</#if> 
<#elseif propertyName == "phone">
	<#assign helptext = "Insert phone number for primary contact/group, including country and local area code.  For example, '+61 7 3138 2000.'">
<#elseif propertyName == "fax">
	<#assign helptext = "Insert fax number for primary contact/group, including country and local area code.  For example, '+61 7 3138 2001.'">
<#elseif propertyName == "geographicFocus">
	<#if recordType != "service">
		<#assign helptext = "List the regions/countries that form the focus of the research relative to the data collection being described.">
	</#if>
<#elseif propertyName == "fundingScheme">
	<#if recordType == "activities">
		<#assign helptext = "List the scheme by which the project being described was funded. For example, <a href=\"https://www.nhmrc.gov.au/grants-funding/apply-funding/research-fellowships\" target=\"_blank\">NHMRC Research Fellowship</a> or <a href=\"https://www.nhmrc.gov.au/grants-funding/apply-funding/research-fellowships\" target=\"_blank\">ARC Discovery</a>">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List the funding scheme that the data collection was created/collected with the aid of.   For example, NHMRC Research Fellowship or Queensland Teachers' Union (QTU).">
	<#elseif recordType == "service">
		<#assign helptext = "List the funding scheme that the service was created/developed under.   For example, NHMRC Research Fellowship or Queensland Teachers' Union (QTU).">
	</#if>	
<#elseif propertyName == "grantor">
	<#if recordType == "activities">
		<#assign helptext = "List the grantor(s) and grant ID number(s) where funding was provided towards the project. For example, National Health and Medical Research Council (NHMRC) 415006 or National Institutes of Health (NIH) 5 R01 HL123451-01A2.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List the grantor(s) and grant ID number(s) where  funding was provided towards the creation/collection of the data being described.  For example, National Health and Medical Research Council (NHMRC) 415006 or National Institutes of Health (NIH) 5 R01 HL123451-01A2.">
	<#elseif recordType == "service">
		<#assign helptext = "List the grantor(s) and grant ID number(s) where  funding was provided towards the creation/development of the service being described.  For example, National Health and Medical Research Council (NHMRC) 415006 or National Institutes of Health (NIH) 5 R01 HL123451-01A2.">
	</#if>	
<#elseif propertyName == "RMProjectCode">
	<#if recordType == "activities">
		<#assign helptext = "A ResearchMaster project code is a unique code assigned to activity records that have been ingested from ResearchMaster.">
	</#if>
<#elseif propertyName == "projectStatus">
	<#if recordType == "activities">
		<#assign helptext = "For activity records ingested from ResearchMaster, this field will be pre-filled.  Manually created activity records published with associated data should all have a project status of 'Approved.'">
	</#if>
<#elseif propertyName == "AOUName">
	<#if recordType == "activities">
		<#assign helptext = "List the Academic Organisational Unit the activity being described is affiliated with e.g. Institute of Health and Biomedical Innovation (IHBI) or School of Management. For automatically created records, this field will be pre-filled from ResearchMaster.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Please list all relevant QUT schools, faculties, centres and institutes.">
	</#if>
<#elseif propertyName == "version">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the the most recent version of the collection, data set, catalogue, index, registry or repository e.g. Version 4.1.">
	</#if>
<#elseif propertyName == "offlineLocation">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Provide an email address for an authorised person who can provide access to the hardcopy data.">
	<#elseif recordType == "service">
		<#assign helptext = "Provide an email address for an authorised person who can provide access to the hardcopy data.  ">
	</#if>
<#elseif propertyName == "copyrightInfo">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Include a statement about the intellectual property rights held over a collection, or a statement about access rights e.g. © Queensland University of Technology, 2005.">
	</#if>
<#elseif propertyName == "accessRightsInfo">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Include a description of access rights and constraints, including who may access the data, when access may occur (including any embargo), and uses that may be made of the collection. For example, 'The owner of this data collection reserves the right to provide access to the data by negotiation' or 'You could be required to indicate your intended use of the data and to fulfil other terms and conditions as determined by the data owners.'">
	</#if>
<#elseif propertyName == "retentionPeriod">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Referring to the <a target=\"_blank\" href=\"http://www.nhmrc.gov.au/guidelines/publications/r39\">Australian Code for the Responsible Conduct of Research</a> for the minimum retention period requirements for research data, specify how many years the data must be retained for e.g. 15 years.">
	</#if>
<#elseif propertyName == "accessPolicyURL">
	<#if recordType == "service">
		<#assign helptext = "Enter the URL that points to the access policy for this resource. This could be a web site, or, for example, an XACML resource. XACML is eXtensible Access Control Markup Language, a declarative access control policy language. The access policy may be, but need not be, machine-readable.">
	</#if>
<#elseif propertyName == "AOUNameCode">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Name codes (or Academic Organisational Unit codes) are pre-filled from ResearchMaster. Do not edit this field if empty.">
		</#if>
	</#if>
<#elseif propertyName == "primaryContact">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Provide the first name and surname of the primary contact for the associated data collection.">
	<#elseif recordType == "activities">
		<#assign helptext = "Provide the first name and surname of the primary contact for the associated activity.">
	<#elseif recordType == "service">
		<#assign helptext = "Provide the first name and surname of the primary contact for the associated service.">
	</#if>
<#elseif propertyName == "dataFileTypes">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the file format of the data e.g. .xlsx. Where appropriate, list proprietary software required to access the data e.g. Excel spreadsheet requires Microsoft Excel 2001. For physical data, list the format they take e.g. 200 paper survey responses.">
	</#if>
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row">
				<div class="form_full_value">
					<#if textAreaSize == "input">
						<input type="text" name="txtValue" class="freeTextValue" value="${inputValue}"/>
					<#else>
						<textarea class="${textAreaSize} freeTextValue" type="text" name="txtValue" role="input">${inputValue}</textarea>
					</#if>
				</div> 
			</div>
			<div class="button_wrapper">
					<div class="property-button cancel">Cancel</div>
					<div class="property-button submit">
						<input type="hidden" name="editKey" value="${editKey}" />
						Submit
					</div>
			</div>
		</div>
		<div class="right_column">
			<div class="help_heading">
				<h4>QUT Guidance</h4>
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="div_help">
					<p class="help_text">${helptext}</p>
				</div>
			</div>
		</div>
	</div>
</form>
              
              
              