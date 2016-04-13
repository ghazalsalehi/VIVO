<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign copyrightValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCopyrightValue") />
<#assign rightsUri = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRightsUri") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#if propertyName == "copyrightInfo">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "State the copyright ownership for the data using the format &copy; Copyright Owner, Year of copyright.  For example, &copy; Queensland University of Technology, 2005. Any intellectual property created by staff members in the course of their employment is owned by QUT.  Students personally own the intellectual property that they generate, unless assigned otherwise.">
	</#if>
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
		<div class="form_wrapper">
			<div class="left_column">
				<div id="copyrightDiv" class="form_row top_row">
					<div class="form_label"><span class='requiredHint'>* </span>${editConfiguration.formTitle}:</div> 
					<div class="form_value">
						<#if copyrightValue == "">
							<#assign copyrightValue = "&copy;">
						</#if>
						<textarea class="${textAreaSize}" type="text" name="txtCopyrightValue" id="copyright" role="input">${copyrightValue}</textarea>
					</div>  
				</div>
				<div id="rightsUri" class="form_row" style="display:none;">
					<div class="form_label">Rights URL:</div> 
					<div class="form_value"><input type="text" name="txtRightsUri" id="rightsUriValue" value="${rightsUri}"/></div>
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
					<div class="div_help"">
						<p class="help_text">${helptext}</p>
					</div>
				</div>			
			</div>
		</div>
</form>
              
              
              