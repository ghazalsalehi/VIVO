<#--
Copyright (c) 2013, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign RIFCSCoverageType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRIFCSCoverageType") />
<#assign spatialCoverageValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtSpatialCoverageValue") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign coverageTypes = editConfiguration.pageData.RIFCSCoverageTypes />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#if recordType == "activities">
	<#assign helptext = "Using the map, specify the geographical area associated with the data. Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
	<#assign helptext = "Using the map, specify the geographical area associated with the data. Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
<#elseif recordType == "service">
	<#assign helptext = "Using the map, specify the geographical area associated with the data. Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
</#if>	

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<input type="hidden" id="spatialValue" name="txtSpatialCoverageValue" value="${spatialCoverageValue}"/>
		<input type="hidden" id="mapCordinates" name="txtMapCordinates" value="<#if editMode == "edit"><#if RIFCSCoverageType == "kmlPolyCoords">${spatialCoverageValue}</#if></#if>"/>
		
		<div class="left_column">
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Type:</div> 
				<div class="form_value">
					<select name="txtRIFCSCoverageType" id="coverageType">
						<#list coverageTypes as optionType>
							<#if optionType == "kmlPolyCoords">
								<#if editMode != "edit">
									<option value="${optionType}" selected="selected">map</option>
								<#else>
									<option value="${optionType}" <#if optionType == RIFCSCoverageType>selected="selected"</#if> >map</option>
								</#if>	
							<#elseif optionType == "text">
								<option value="${optionType}" <#if optionType == RIFCSCoverageType>selected="selected"</#if> >text</option>
							</#if>
						</#list>
					</select>
				</div> 
			</div>
			
			<div id="coverageTextValueDiv" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Value:</div> 
				<div class="form_value">
					<textarea class="bigger" type="text" name="txtTextValue" id="coverageTextValue" role="input"><#if editMode == "edit"><#if RIFCSCoverageType == "text">${spatialCoverageValue}</#if></#if></textarea>
				</div> 
			</div>
			
			<div id="spatialCoverageMapDiv" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Select from map:</div> 
				<div id="locationCaptureWidget" class="form_value"></div>
			</div>
			<div class="button_wrapper" style="padding-top: 10px">
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
				<div class="help_btn help_shown" title="Click here for help (It will not navigate you away)"></div>
				<div class="div_help">
					<p class="help_text">${helptext}</p>
				</div>
			</div>
		</div>
	</div>
</form>
   
              
              
              
              
              
              
              