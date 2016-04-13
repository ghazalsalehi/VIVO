<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign licenceType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtlicenceType") />
<#assign licenceValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtlicenceValue") />
<#assign rightsUri = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRightsUri") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign licenceUriTypes = editConfiguration.pageData.licenceUriTypes />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#if propertyName == "licence">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "If the data is already licenced, select the relevant licence from the drop down menu. If no licence has been applied, see the <a target=\"_blank\" href=\"http://creativecommons.org.au/learn/licences/\">Creative Commons website</a> for examples of appropriate licences.">
	</#if>	
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>Licence URL:</div> 
				<div class="form_value">
					<select name="txtlicenceType" id="licenceType" onchange="javascript: showfield();">
						<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						<#list licenceUriTypes as optionType>
							<#if optionType == "CC-BY">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Creative Commons Attribution 4.0 (CC-BY)</option>
							<#elseif optionType == "CC-BY-SA">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Creative Commons Attribution-Share Alike 4.0 (CC-BY-SA)</option>
							<#elseif optionType == "CC-BY-NC">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Creative Commons Attribution-NonCommercial 4.0 (CC-BY-NC)</option>
							<#elseif optionType == "CC-BY-NC-SA">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Creative Commons Attribution-NonCommercial-Share Alike 4.0 (CC-BY-NC-SA)</option>
							<#elseif optionType == "CC-BY-ND">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Creative Commons Attribution-No Derivatives 4.0 (CC-BY-ND)</option>
							<#elseif optionType == "CC-BY-NC-ND">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Creative Commons Attribution-NonCommercial-No Derivatives 4.0 (CC-BY-NC-ND)</option>
							<#elseif optionType == "GPL">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >GNU General Public License (GPL)</option>
							<#elseif optionType == "AusGOALRestrictive">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >AusGOAL Restrictive Licence</option>
							<#elseif optionType == "NoLicense">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >No Licence</option>
							<#elseif optionType == "Other">
								<option value="${optionType}" <#if optionType == licenceType>selected="selected"</#if> >Other</option>
							</#if>
						</#list>
					</select>
				</div> 
			</div>
			<div id="licenceValueDiv" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Value:</div> 
				<div class="form_value"><input type="text" name="txtlicenceValue" id="licenceValue" value="${licenceValue}"/></div> 
			</div>
			<div id="rightsUriDiv" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Rights URL:</div> 
				<div class="form_value"><input type="text" name="txtRightsUri" id="rightsUri" value="${rightsUri}"/></div> 
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
              
              
              
              
              