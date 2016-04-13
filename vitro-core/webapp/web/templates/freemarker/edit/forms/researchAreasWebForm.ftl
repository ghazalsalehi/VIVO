<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign researchAreasInfoTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtResearchAreasInfoType") />
<#assign researchAreasFORTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtResearchAreasFORType") />
<#assign researchAreasValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtResearchAreasValue") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign researchAreasInfoTypes = editConfiguration.pageData.researchAreasInfoTypes />
<#assign researchAreasFORTypes = editConfiguration.pageData.researchAreasFORTypes />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">


<#if propertyName == "researchAreas">
	<#if recordType == "people">
		<#assign helptext = "Australian New Zealand Standard Research Classification (ANZSRC) <a href=\"http://www.abs.gov.au/ausstats/abs@.nsf/0/4AE1B46AE2048A28CA25741800044242?opendocument\" target=\"_blank\">Field of Research codes</a>, as well as keywords may be entered here. Choose from two, four or six digit ANZSRC Field of Research Codes.">
	<#elseif recordType == "activities">
		<#assign helptext = "Australian New Zealand Standard Research Classification (ANZSRC) <a href=\"http://www.abs.gov.au/ausstats/abs@.nsf/0/4AE1B46AE2048A28CA25741800044242?opendocument\" target=\"_blank\">Field of Research codes</a>, as well as keywords may be entered here. Choose from two, four or six digit ANZSRC Field of Research Codes.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Australian New Zealand Standard Research Classification (ANZSRC) <a href=\"http://www.abs.gov.au/ausstats/abs@.nsf/0/4AE1B46AE2048A28CA25741800044242?opendocument\" target=\"_blank\">Field of Research codes</a>, as well as keywords may be entered here. Choose from two, four or six digit ANZSRC Field of Research Codes.">
	<#elseif recordType == "service">
		<#assign helptext = "Australian New Zealand Standard Research Classification (ANZSRC) <a href=\"http://www.abs.gov.au/ausstats/abs@.nsf/0/4AE1B46AE2048A28CA25741800044242?opendocument\" target=\"_blank\">Field of Research codes</a>, as well as keywords may be entered here. Choose from two, four or six digit ANZSRC Field of Research Codes.">
	</#if> 
</#if>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
					<div class="form_row">
						<div class="form_label"><span class='requiredHint'>* </span>Type:</div> 
						<div class="form_value">
							<select name="txtResearchAreasInfoType" id="researchAreasInfoType">
								<#-- <option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option> -->
								<#list researchAreasInfoTypes as optionType1>
									<#if optionType1 == "local">
										<option value="${optionType1}" <#if editMode != "edit">selected="selected"<#elseif optionType1 == researchAreasInfoTypeValue>selected="selected"</#if> >Keyword</option>
									<#elseif optionType1 == "anzsrc-for">
										<option value="${optionType1}" <#if optionType1 == researchAreasInfoTypeValue>selected="selected"</#if> >Field of Research Code (ANZSRC)</option>
									<#else>
										<#-- LIBRDF-85 hide unwanted types from dropdown since old records has got anzsrc, amzsrc-toa, anzsrc-seo values -->
										<#if editMode == "edit">
											<#if optionType1 == researchAreasInfoTypeValue>
												<option value="${optionType1}" selected="selected">${optionType1}</option>
											</#if>
										</#if>
									</#if>	
								</#list>
							</select>
						</div> 
					</div>
					<div class="form_row" id="researchAreasFORTypeDiv">
						<div class="form_label"><span class='requiredHint'>* </span>FoR Code:</div> 
						<div class="form_value">
							<select name="txtResearchAreasFORType" id="researchAreasFORType">
								<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
								<#list researchAreasFORTypes as optionType2>
									<option value="${optionType2}" <#if optionType2 == researchAreasFORTypeValue>selected="selected"</#if> >${optionType2}</option>
								</#list>
							</select>
						</div> 
					</div>
					<div class="form_row" style="" id="researchAreasValueDiv">
							<div class="form_label"><span class='requiredHint'>* </span>Value:</div> 
							<div class="form_value"><input type="text" name="txtResearchAreasValue" id="researchAreasValue" value="${researchAreasValue}"/></div> 
					</div>
					
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
						<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
						<div class="div_help">
							<p class="help_text">${helptext}</p>
						</div>
					</div>
		</div>
	</div>
</form>
