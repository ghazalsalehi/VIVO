<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign descriptionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDescription") />
<#assign descriptionTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDescriptionType") />
<#assign moreInfoURLValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtMoreInfoURL") />


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
<#assign descriptionTypeValue="full"/>

<#if propertyName == "biography">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "A description of a group, either formal or informal. Include important keywords and write for an audience with general familiarity of a research area, not specialist knowledge. Limit the use of acronyms and jargon.">
		<#else>
			<#assign helptext = "Include a short biography for the researcher. For display purposes, this field has a 300 character limit. You will not be able to submit if the maximum limit has been reached. Include a URL for the researcher&#39;s Academic Profile e.g. <a href=\"http://staff.qut.edu.au/staff/bettsm/\" target=\"_blank\">http://staff.qut.edu.au/staff/bettsm/</a> in the 'Additional info URL' field.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "A description of a research project or activity. Include important keywords and write for an audience with general familiarity of a research area, not specialist knowledge. Limit the use of acronyms and jargon.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "A description of a data collection or dataset. Include important keywords and write for an audience with general familiarity of a research area, not specialist knowledge. Limit the use of acronyms and jargon.">
	<#elseif recordType == "service">
		<#assign helptext = "A plain text description of a person, group, activity, data collection or service.  Include important keywords and write for an audience with general familiarity of a research area, not specialist knowledge.  Limit  the use of acronyms and jargon.  Include dates of establishment/incorporation and/or dissolution for groups.  If a logo is to be displayed, select 'logo' from the drop down menu and include a URL for logo in the free text box.">
	</#if> 
</#if>
	
<form id="mainform-${propertyName}" action=${submitUrl} method="get">	   
	<div class="form_wrapper">
				<div class="left_column">
						<div id="description" class="form_row" >
							<div class="form_full_value">
								<textarea style="width: 595px; height: 450px" type="text" name="txtDescription"  id="${propertyName}-ckeditor" role="input">${descriptionValue}</textarea>
							</div> 
						</div>
						<div class="form_row" style="display:none;">
							<div class="form_label">* Type:</div> 
							<div class="form_value">
								<select name="txtDescriptionType" id="descriptionType" >
									<#-- <option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option> -->
									<#list selectionTypes as optionType>
									<option value="${optionType}" <#if optionType == descriptionTypeValue>selected="selected"</#if> >${optionType}</option>
									</#list>
								</select>
							</div> 
						</div>
						
						<#if recordType == "people">
							<#if recordSubType != "group">
								<div id="moreInfoURL" class="form_row">
								<div class="form_label">Additional info URL:</div> 
								<div class="form_value">
									<input type="text" name="txtMoreInfoURL" value="${moreInfoURLValue}"/>
								</div> 
								</div>
							</#if>
						</#if>	
					
						<div class="button_wrapper">
							<div class="property-button cancel">Cancel<#--<a href="${editConfiguration.cancelUrl}" title="Cancel">Cancel</a>--></div>
							<div class="property-button submit">
								<input type="hidden" name="editKey" value="${editKey}" />
								Submit
								<#--<input type="submit" value="Save" role="button" />-->
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
</form>
    




              
              
              
              
              
              
              