<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign dateFromType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDateFromType") />
<#assign dateToType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDateToType") />
<#assign dateFromValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDateFromValue") />
<#assign dateToValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDateToValue") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign dateFormats = editConfiguration.pageData.dateFormats />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#assign dateFromType = "W3CDTF">
<#assign dateToType = "W3CDTF">

<#if propertyName == "existenceDates">
	<#if recordType == "activities">
		<#assign helptext = "List start and finish dates for the related activity.">
	</#if>
<#elseif propertyName == "temporalCoverage">
	<#if recordType == "activities">
		<#assign helptext = "Include a time period during which the data was collected/operated on or observations made, or a time period that the activity is linked to intellectually or thematically e.g. 1997 to 1998; 01/01/10 to 31/12/11; the 18th century.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Include a time period during which the data was collected or operated on or observations made e.g. 1997 to 1998; 01/01/10 to 31/12/11.  If data collection is ongoing, indicate this in the description field.">
	<#elseif recordType == "service">
		<#assign helptext = "Using the map, specify the geographical area where the data was collected or operated on by the related service.  Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
	</#if>	
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>Date From:</div> 
				<div class="form_value">
					<input type="text" name="txtDateFromValue" id = "dateFromCalander" value="${dateFromValue}"/>
				</div> 
			</div>
			<div class="form_row" style="display:none">
				<div class="form_label">Date From Type:</div> 
				<div class="form_value">
					<select name="txtDateFromType" id="dateFromType">
						<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						<#list dateFormats as optionType1>
						<option value="${optionType1}" <#if optionType1 == dateFromType>selected="selected"</#if> >${optionType1}</option>
						</#list>
					</select>
				</div> 
			</div>
			<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Date To:</div> 
					<div class="form_value"><input type="text" name="txtDateToValue" id="dateToCalander" value="${dateToValue}"/></div> 
			</div>
			<div class="form_row" style="display:none">
				<div class="form_label">Date To Type:</div> 
				<div class="form_value">
					<select name="txtDateToType" id="dateToType">
						<#-- <option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option> -->
						<#list dateFormats as optionType2>
							<option value="${optionType2}" <#if optionType2 == dateToType>selected="selected"</#if> >${optionType2}</option>
						</#list>
					</select>
					</div> 
			</div>
			<div class="button_wrapper">
				<div class="property-button cancel">Cancel</div>
				<div class="property-button submit">
					<input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
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

          