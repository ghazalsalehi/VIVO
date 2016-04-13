<#--
Copyright (c) 2015, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign titleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtTitleValue") />
<#assign firstnameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtFirstnameValue") />
<#assign surnameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtSurnameValue") />
<#assign emailValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtEmailValue") />
<#assign URLValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtURLValue") />
<#assign phoneValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtPhoneValue") />
<#assign faxValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtFaxValue") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#if recordType == "people">
	<#if recordSubType == "group">
		<#assign helptext = "List a contact person for the group. If the group is not at QUT anymore, list a long term contact such as an institute or faculty data manager.">
	<#else>
		<#assign helptext = "List the researcher&#39;s full name and email address. Include other contact information as appropriate.">
	</#if>
<#elseif recordType == "activities">
	<#assign helptext  = "List the details of the primary contact for the project. If the primary contact is not at QUT anymore, list a long term contact such as an institute or faculty data manager.">
<#elseif recordType == "service">
	<#assign helptext  = "List the details of the primary contact for the equipment or service. If the primary contact is not at QUT anymore, list a long term contact such as an institute or faculty data manager.">
<#else>
	<#assign helptext = "List the details of the primary contact for the data. If the primary contact is not at QUT anymore, list a long term contact such as an institute or faculty data manager.">
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row">
				<div class="form_label">Title</div> 
				<div class="form_value">
					<input type="text" name="txtTitleValue" id="title" value="${titleValue}"/>
				</div> 
			</div>
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>First name</div> 
				<div class="form_value">
					<input type="text" name="txtFirstnameValue" id="firstname" value="${firstnameValue}"/>
				</div> 
			</div>
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Surname</div> 
				<div class="form_value">
					<input type="text" name="txtSurnameValue" id="surname" value="${surnameValue}"/>
				</div> 
			</div>
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Email</div> 
				<div class="form_value">
					<input type="text" name="txtEmailValue" id="email" value="${emailValue}"/>
				</div> 
			</div>
			<div class="form_row">
				<div class="form_label">URL</div> 
				<div class="form_value">
					<input type="text" name="txtURLValue" id="URL" value="${URLValue}"/>
				</div> 
			</div>
			<div class="form_row">
				<div class="form_label">Phone</div> 
				<div class="form_value">
					<input type="text" name="txtPhoneValue" id="phone" value="${phoneValue}"/>
				</div> 
			</div>
			<div class="form_row">
				<div class="form_label">Fax</div> 
				<div class="form_value">
					<input type="text" name="txtFaxValue" id="fax" value="${faxValue}"/>
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
              
              
              