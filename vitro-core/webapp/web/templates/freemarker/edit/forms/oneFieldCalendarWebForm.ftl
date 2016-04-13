<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign dateValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDate") />


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

 
<#if propertyName == "dateMadePublicInRDF">
	<#assign helptext = "Data Librarian to enter date immediately prior to record publication.">
<#elseif propertyName == "dateRecordCreated">
	<#assign helptext = "This field is pre-filled upon automatic or manual record creation.">
<#elseif propertyName == "dateRecordModified">
	<#assign helptext = "This field is pre-filled upon record creation, and is updated whenever record is modified.">
<#elseif propertyName == "embargoDate">
	<#assign helptext = "Click in the box to choose a date that the data can be made available for re-use.   If the data is embargoed, do not specify an electronic location in the \"Location of digital data\" field.  An offline location may be specified.">
<#elseif propertyName == "dateOfDisposal">
	<#assign helptext = "Referring to the <a target=\"_blank\" href=\"http://www.nhmrc.gov.au/guidelines/publications/r39\">Australian Code for the Responsible Conduct of Research</a> for the minimum retention period requirements for research data, click in the box to choose a date that the data may be disposed of.">
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div id="dt" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Date:</div> 
				<div class="form_value">
					<input type="text" name="txtDate" id = "dateCalander" value="${dateValue}"/>
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
              

              			 
              
              
              