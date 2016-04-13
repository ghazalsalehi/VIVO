<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign descriptionTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDescriptionType") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign selectionTypes = editConfiguration.pageData.selectionTypes />
<#assign DisplayString = editConfiguration.pageData.displayString />

<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">


<#if propertyName == "internalOrExternal">
	<#if recordType == "activities">
		<#assign helptext = "Select funding type from menu.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select funding type from menu.">
	<#elseif recordType == "service">
		<#assign helptext = "Select funding type from menu.">
	</#if> 
<#elseif propertyName == "isApprovedbyLibrary">
	<#assign helptext = "Liaison Librarian to change value to 'Yes' once record is complete and ready for checking by the Office of Research.">
<#elseif propertyName == "isApprovedByOoR">
	<#assign helptext = "Office of Research to change value to 'Yes' once record has been reviewed and is ready for publication.">
<#elseif propertyName == "publishRecord">
	<#assign helptext = "Data Librarian to change value to 'Published' to publish record.">
<#elseif propertyName == "dataTypeInfo">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "If the data identifies people or groups i.e. adults and children, specify the extent to which individuals can be identified.">
	</#if> 
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Save changes">
		<#assign TitleText = "Save changes for '${editConfiguration.formTitle}'">
        <#assign disabledVal="disabled">
       
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create entry">
		<#assign TitleText = "Create entry for '${editConfiguration.formTitle}'">
        <#assign disabledVal=""/>
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row">
				<div class="form_label">${DisplayString}:</div> 
				<div class="form_value">
					<select name="txtDescriptionType" id="desType">
						<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						<#list selectionTypes as optionType>
							<option value="${optionType}" <#if optionType == descriptionTypeValue>selected="selected"</#if> >${optionType}</option>
						</#list>
					</select>
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
              
              
              
              
              
              