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

<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign propertyName = editConfiguration.pageData.propertyName />

<#assign helptext = "Data Librarian to change value to 'Published' to publish record.">

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>${DisplayString}:</div> 
				<div class="form_value">
					<select name="txtDescriptionType" id="recordStatus">
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
              
              
              
              
              
              
              