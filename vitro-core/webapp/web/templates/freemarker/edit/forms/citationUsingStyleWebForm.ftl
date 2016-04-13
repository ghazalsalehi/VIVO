<#--
Copyright (c) 2013, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign citationStyleType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCitationStyleType") />
<#assign citationStyleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCitationStyleValue") />
<#assign citationStyleLink = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCitationStyleLink") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign textAreaSize = editConfiguration.pageData.displayString />
<#assign selectionTypes = editConfiguration.pageData.selectionTypes />
<#assign propertyName = editConfiguration.pageData.propertyName />

<#assign helptext = "If a citation exists, specify the citation style and include the full citation in this field.  If no citation exists, use the 'Citation generator' field to create a citation. If you would like a <a href=\"http://www.ands.org.au/guides/doi.html\" target=\"_blank\">DOI</a> (Digital Object Identifier), request one by completing the 'Citation generator' field.">


<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>Type:</div> 
				<div class="form_value">
					<select name="txtCitationStyleType" id="styleType" >
						<#list selectionTypes as optionType>
							<#if optionType == "Datacite">
								<option value="${optionType}" <#if editMode != "edit">selected="selected"<#elseif optionType == citationStyleType>selected="selected"</#if> >${optionType}</option>
							<#else>
								<option value="${optionType}" <#if optionType == citationStyleType>selected="selected"</#if> >${optionType}</option>
							</#if>
						</#list>
					</select>
				</div> 
			</div>
			
			<div class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>Citation:</div> 
				<div class="form_value">
					<textarea class="${textAreaSize}" type="text" name="txtCitationStyleValue" id="styleValue" role="input">${citationStyleValue}</textarea>
				</div> 
			</div>
			
			<div class="form_row top_row">
				<div class="form_label">URL or DOI:</div> 
				<div class="form_value"><input type="text" name="txtCitationStyleLink" id="styleLink" value="${citationStyleLink}"/></div> 
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
