<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign nameTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtNameType") />
<#assign namePartTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtNamePartType") />
<#assign namePartValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtNamePartValue") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign nameTypes = editConfiguration.pageData.nameTypes />
<#assign namePartTypes = editConfiguration.pageData.namePartsTypes />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">
<#assign bTypeHidden = "false">

<#assign labelNameType = "Name Type">
<#assign labelNamePartType = "Name Part Type">
<#assign labelNamePartValue = "Name Part Value">

<#if propertyName == "abbreviation">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Include abbreviated name of group.">
		</#if>
	</#if>

	<#assign labelNamePartValue = "Abbreviation">
	<#assign nameTypeValue = "abbreviated" >
	<#assign namePartTypeValue = "initial">
	<#assign bTypeHidden = "true">
<#elseif propertyName == "firstName">
	<#if recordType == "people">
		<#if recordSubType != "group">
			<#assign helptext = "Include full first name.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "Include full first name of the primary contact.">
	<#elseif recordType == "dataCollections">
		<#assign helptext = "Include full first name of the primary contact.">
	<#elseif recordType == "service">
		<#assign helptext = "Include full first name of the primary contact.">
	</#if>
	<#assign labelNamePartValue = "First name">
	<#assign nameTypeValue = "primary">
	<#assign namePartTypeValue = "given" >
	<#assign bTypeHidden = "true">
<#elseif propertyName == "supressFirstName">
	<#assign helptext = "Include full first name (primary contact for groups only).">
	<#assign labelNamePartValue = "First name">
	<#assign nameTypeValue = "primary">
	<#assign namePartTypeValue = "given" >
	<#assign bTypeHidden = "true">
<#elseif propertyName == "surname">
	<#if recordType == "people">
		<#if recordSubType != "group">
			<#assign helptext = "Include full surname.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "Include full surname of the primary contact.">
	<#elseif recordType == "dataCollections">
		<#assign helptext = "Include full surname of the primary contact.">
	<#elseif recordType == "service">
		<#assign helptext = "Include full surname of the primary contact.">
	</#if>
	<#assign labelNamePartValue = "Surname">
	<#assign nameTypeValue = "primary">
	<#assign namePartTypeValue = "family" >
	<#assign bTypeHidden = "true">
<#elseif propertyName == "supressSurname">
	<#assign helptext = "Include full first name (primary contact for groups only).">
	<#assign labelNamePartValue = "Surname">
	<#assign nameTypeValue = "primary">
	<#assign namePartTypeValue = "family" >
	<#assign bTypeHidden = "true">
<#elseif propertyName == "prefefredTitleText">
	<#assign helptext = "Specify the primary contact's title.">
	<#assign labelNamePartValue = "Title">
	<#assign nameTypeValue = "primary">
	<#assign namePartTypeValue = "title" >
	<#assign bTypeHidden = "true">
<#elseif propertyName == "supressTitle">
	<#assign helptext = "Specify the primary contact's title (primary contact for groups only).">	
	<#assign labelNamePartValue = "Title">
	<#assign nameTypeValue = "primary">
	<#assign namePartTypeValue = "title" >
	<#assign bTypeHidden = "true">
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div id="nameType" class="form_row" <#if bTypeHidden == "true">style="display:none"</#if>>
				<div class="form_label"><span class='requiredHint'>* </span>${labelNameType}:</div> 
				<div class="form_value">
					<select name="txtNameType" id="txtNameType">
						<#if bTypeHidden == "false">
							<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						</#if>
						
						<#list nameTypes as optionType1>
							<option value="${optionType1}" <#if optionType1 == nameTypeValue>selected="selected"</#if> >${optionType1}</option>
						</#list>
					</select>
				</div> 
			</div>
	
			<div id="namePartType" class="form_row" <#if bTypeHidden == "true">style="display:none"</#if>>
				<div class="form_label"><span class='requiredHint'>* </span>${labelNamePartType}:</div> 
				<div class="form_value">
					<select name="txtNamePartType" id="txtNamePartType">
						<#if bTypeHidden == "false">
							<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						</#if>
						<#list namePartTypes as optionType2>
						<option value="${optionType2}" <#if optionType2 == namePartTypeValue>selected="selected"</#if> >${optionType2}</option>
						</#list>
					</select>
				</div> 
			</div>
		
			<div id="idenValue" class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>${labelNamePartValue}:</div> 
				<div class="form_value"><input type="text" name="txtNamePartValue" value="${namePartValue}"/></div> 
			</div>
			<div class="button_wrapper">
				<div class="property-button cancel">Cancel</div>
				<div class="property-button submit">
					<input type="hidden" name="userMode" value="${editMode}"/>
					Submit
				</div>
			</div>
		</div>
		<div class="right_column">
			<div class="help_heading">
				<h4>QUT Guidance</h4>
				<div class="help_btn help_shown" title="Click here for help (It will not navigate you away)"></div>
				<div class="div_help"">
					<p class="help_text">${helptext}</p>
				</div>
			</div>			
		</div>
	</div>
</form>
 
