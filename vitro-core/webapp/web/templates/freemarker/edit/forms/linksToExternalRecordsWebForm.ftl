<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign relationshipType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRelationshipType") />
<#assign relationshipKey = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRelationshipKey") />
<#assign relationshipURL = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRelationshipURL") />
<#assign relatedObjectDisplayName = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRelatedObjectDisplayName") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign selectionTypes = editConfiguration.pageData.selectionTypes />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign helptext = "If a record already exists in Research Data Australia, select its affiliation to the data collection being described, list its key and URL and copy the name of the record.">
	
<form id="mainform-${propertyName}" action=${submitUrl} method="get">	
	<div class="form_wrapper">
		<div class="left_column">	
			<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Affliation Type:</div> 
					<div class="form_value">
						<select name="txtRelationshipType" id="relationshipType" >
							<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
							<#list selectionTypes as optionType>
							<option value="${optionType}" <#if optionType == relationshipType>selected="selected"</#if> >${optionType}</option>
							</#list>
						</select>
					</div> 
			</div>
			<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Key:</div> 
					<div class="form_value">
							<input type="text" name="txtRelationshipKey" id="relationshipKey" value="${relationshipKey}"/>
					</div> 
			</div>
			<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>URL:</div> 
					<div class="form_value">
						<input type="text" name="txtRelationshipURL" id="relationshipURL" value="${relationshipURL}"/>
					</div> 
			</div>
			<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Display name:</div> 
					<div class="form_value">
						<input type="text" name="txtRelatedObjectDisplayName" id="relatedObjectDisplayName" value="${relatedObjectDisplayName}"/>
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
				<div class="div_help"">
					<p class="help_text">${helptext}</p>
				</div>
			</div>		
		</div>
	</div>	
</form>       
              
              
              
              
              