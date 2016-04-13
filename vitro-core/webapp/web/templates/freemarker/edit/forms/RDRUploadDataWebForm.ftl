<#--
Copyright (c) 2015, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign packageTitle = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRDRPackageTitle") />
<#assign packageType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRDRPackageType") />
<#assign packageName = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRDRPackageName") />
<#assign packageID = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRDRPackageID") />
<#assign packageURL = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRDRPackageURL") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign selectionTypes = editConfiguration.pageData.selectionTypes />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign individualURL = editConfiguration.pageData.individualDisplayURL />
<#assign individualTitle = editConfiguration.pageData.IndividualTitle />

<#assign helptext = "Upload your data to Research Data Finder. All data uploaded to this storage area must be open access. If access to the data is via mediation, list the email address of a contact person in Q.9.">

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row" style='display: none;'>
				<div class="form_label"><span class='requiredHint'>* </span>Title:</div> 
				<div class="form_value">
					<#if editMode == "edit">
						<input type="text" name="txtRDRPackageTitle" id="RDRPackageTitle" class="freeTextValue" value="${packageTitle}"/>
					<#else>
						<input type="text" name="txtRDRPackageTitle" id="RDRPackageTitle" class="freeTextValue" value="${individualTitle}"/>
					</#if>
				</div>
			</div>
			
			<div class="form_row" style="display:none;">
				<div class="form_label"><span class='requiredHint'>* </span>Type:</div> 
				<div class="form_value">
					<select name="txtRDRPackageType" id="RDRDatasetType" id="type">
						<#list selectionTypes as optionType>
							<#if editMode == "edit">
								<option value="${optionType}" <#if optionType == packageType>selected="selected"</#if> >${optionType}</option>
							<#else>
								<option value="${optionType}" <#if optionType == "url">selected="selected"</#if>>${optionType}</option>
							</#if>
						</#list>
					</select>
				</div>
			</div>
			
			<div class="form_row" style="display:none;">
				<div class="form_label"><span class='requiredHint'>* </span>Name:</div> 
				<div class="form_value">
					<input type="text" name="txtRDRPackageName" id="RDRPackageName"  class="freeTextValue" value="${packageName}"/>
				</div>
			</div>
			
			<div class="form_row" style="display:none;">
				<div class="form_label"><span class='requiredHint'>* </span>ID:</div> 
				<div class="form_value">
					<input type="text" name="txtRDRPackageID" id="RDRPackageID"  class="freeTextValue" value="${packageID}"/>
				</div>
			</div>
			
			<div class="form_row" style="display:none;">
				<div class="form_label"><span class='requiredHint'>* </span>URL:</div> 
				<div class="form_value">
					<input type="text" name="txtRDRPackageURL" id="RDRPackageURL"  class="freeTextValue" value="${packageURL}"/>
				</div>
			</div>

			<span id='individualURL' style='display: none;'>${individualURL}</span>
			
			<div class="button_wrapper">
				<div class="property-button cancel">No</div>
				<div class="property-button submit" style='float: left;'>
						<input type="hidden" name="editKey" value="${editKey}" />
						Upload
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

<div id="ajax_wait" style=" width: 400px; display: none">
	<div class="ajaxBg"></div>
	<div class="ajaxText" style="text-align:center; color: #FFFFFF; float: left; width: 400px;">Data is being submitted. Please wait...</div>
</div>      