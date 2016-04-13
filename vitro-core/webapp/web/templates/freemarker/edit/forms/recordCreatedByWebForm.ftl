<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign txtCreatedByName = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCreatedByName") />
<#assign txtRecordCreatedByUserID = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRecordCreatedByUserID") />
<#assign txtCreatedByRoleLevelType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCreatedByRoleLevelType") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign selectionTypes = editConfiguration.pageData.selectionTypes />
<#assign adminList = editConfiguration.pageData.adminList />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />

<#assign helptext = "This field is filled automatically when a record is created.">

<form id="mainform-${propertyName}" action=${submitUrl} method="get">	
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Person name:</div> 
				<div class="form_value">
					<input type="text" name="recordCreatedByName" id="recordCreatedByName" value="${txtCreatedByName}"/>
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