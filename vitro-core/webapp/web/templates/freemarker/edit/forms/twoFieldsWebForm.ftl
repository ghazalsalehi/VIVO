<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign descriptionType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDescriptionType") />
<#assign descriptionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDescription") />



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
<#assign labelType = "Type">
<#assign labelValue = "Value">

<#assign bTypeHidden = "false">

<#if propertyName == "mediafluxIdentifier">
	<#assign helptext = "A Mediaflux identifier is a unique record identifier.   This field is pre-populated for all Mediaflux records.">
	<#assign descriptionType = "mediaflux">
	<#assign bTypeHidden = "true">
<#elseif propertyName == "nlaPartyIdentifier">
	<#if recordType == "people">
		<#assign helptext = "Include NLA Party Identifier e.g. <a target=\"_blank\" href=\"http://nla.gov.au/nla.party-1486383\">http://nla.gov.au/nla.party-1486383</a>.">
		<#assign descriptionType = "AU-ANL:PEAU">
		<#assign bTypeHidden = "true">
	</#if>
<#elseif propertyName == "scopusAuthorID">
	<#if recordType == "people">
		<#assign helptext = "Include Scopus Author ID e.g. <a target=\"_blank\" href=\"http://www.scopus.com/authid/detail.url?authorId=7203071164\">http://www.scopus.com/authid/detail.url?authorId=7203071164</a>.">
		<#assign descriptionType = "scopus">
		<#assign bTypeHidden = "true">
	</#if>
<#elseif propertyName == "thomsonReutersResearcherID">
	<#if recordType == "people">
		<#assign helptext = "Include ResearcherID e.g. <a target=\"_blank\" href=\"http://www.researcherid.com/rid/A-3052-2011\">http://www.researcherid.com/rid/A-3052-2011</a>.">
		<#assign descriptionType = "thomsonreuters">
		<#assign bTypeHidden = "true">
	</#if>
<#elseif propertyName == "ordcIdentifier">
	<#if recordType == "people">
		<#assign helptext = "Go to <a href=\"http://orcid.org/\" target=\"_blank\">http://orcid.org/</a> to obtain your Open Researcher Contributor ID (ORCID) and list your ORCID similar to the following examples:<br/>Kirsty Kitto <a href=\"http://orcid.org/0000-0001-7642-7121\" target=\"_blank\">http://orcid.org/0000-0001-7642-7121</a> or Kirsty 0000-0001-7642-7121. <br/>Email the Library Research Support team at <a href=\"mailto:library.research@qut.edu.au\" target=\"_top\">library.research@qut.edu.au</a> for assistance with this question.">
		<#assign descriptionType = "orcid">
		<#assign bTypeHidden = "true">
	</#if>
<#elseif propertyName == "otherIdentifierType">
	<#assign descriptionType = "local">
	<#assign bTypeHidden = "true">
<#elseif propertyName == "localKey">
	<#assign helptext = "A local key is a unique record identifier.   This field is pre-populated for every record.">
	<#assign bTypeHidden = "true">
<#elseif propertyName == "emailAddress">
	<#if recordType == "people">
		<#assign helptext = "List only a QUT email address (no personal emails are to be included).  For groups, list an organisational email address or, if preferred, list a web address for a contact person.">
	<#elseif recordType == "activities">
		<#assign helptext = "List only a QUT email address (no personal emails are to be included).  For groups, list an organisational email address or, if preferred, list a web address for a contact person.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List only a QUT email address for the primary contact (no personal emails are to be included). For groups, list an organisational email address or, if preferred, list a web address for a contact person.">
	<#elseif recordType == "service">
		<#assign helptext = "List only a QUT email address (no personal emails are to be included).  For groups, list an organisational email address or, if preferred, list a web address for a contact person">
	</#if>
	
	<#assign labelValue = "Email">
	<#assign descriptionType = "email">
	<#assign bTypeHidden = "false">
<#elseif propertyName == "academicProfile">
	<#if recordType == "people">
		<#assign helptext = "Include URL for the QUT Academic Profile web page of the person being described.">
	<#elseif recordType == "activities">
		<#assign helptext = "Include URL for the QUT Academic Profile web page of the primary contact.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Include URL for the QUT Academic Profile web page of the primary contact.">
	<#elseif recordType == "service">
		<#assign helptext = "Include URL for the QUT Academic Profile web page of the primary contact.">
	</#if>
	
	<#assign labelValue = "Link">
	<#assign descriptionType = "url">
	<#assign bTypeHidden = "true">
<#elseif propertyName == "organisationName">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "List the Academic Organisational Unit the person/group being described is affiliated with e.g. Institute of Health and Biomedical Innovation (IHBI).  For automatically created records, this field will be pre-filled from ResearchMaster.">
		</#if>
	</#if>
<#elseif propertyName == "website">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Include a URL for services that exist online.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "For activities that have a website, include the URL here.">
	<#elseif recordType == "service">
		<#assign helptext = "For services that exist online, include a URL to a host website.">
	</#if>
	
	<#assign labelValue = "Link">
	<#assign descriptionType = "url">
	<#assign bTypeHidden = "true">
<#elseif propertyName == "spatialCoverage">
	<#if recordType == "activities">
		<#assign helptext = "Using the map, specify the geographical area where the data was collected or operated on.  Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Using the map, specify the geographical area where the data was collected or operated on.  Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
	<#elseif recordType == "service">
		<#assign helptext = "Using the map, specify the geographical area where the data was collected or operated on.  Include geospatial coordinates for a point or area with latitude and longitude coordinates, or through the use of place or regional names e.g. Gippsland, Stradbroke Island or South Australia.">
	</#if>	
<#elseif propertyName == "dataLocationURL">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the electronic location of the data e.g. file name(s) and file path (if stored on the QUT network), URL (if stored in an external data repository) or other digital location.  For non digital data, use the \"Offline location\" field.">
	</#if>
<#elseif propertyName == "digitalObjectIdentifier">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List the DOI hypertext link minted for the data collection being described e.g. <a target=\"_blank\" href=\"http://dx.doi.org/10.4225/09/4FA9F76A2EF1D\">http://dx.doi.org/10.4225/09/4FA9F76A2EF1D</a>.">
		<#assign labelValue = "Digital Object Identifer (DOI)">
		<#assign descriptionType = "doi">
		<#assign bTypeHidden = "true">
	</#if>
<#elseif propertyName == "locationOfDigitalData">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the URL if data is stored in a repository or other online location.  If access to the data is via mediation, list the email address of a contact person.">
	<#elseif recordType == "service">
		<#assign helptext = "Specify the location of the equipment or service.">
	</#if>
</#if>

<script type='text/javascript'>

	function  showfield() {
	  if ($('#txtBiographyType').val() == 'select')
	    $('txtDescription').hide();
	  else
	    $('txtDescription').show();
	}
	
	function SelectChanged()  {  
		if ($('#txtDescriptionType').val() == 'email')
			$("#txtDescription .form_label").html("* Email");
		else if ($('#txtDescriptionType').val() == 'url')
			$("#txtDescription .form_label").html("* Link");
		else
			$("#txtDescription .form_label").html("* Value");
	}

</script>

<form id="mainform-${propertyName}" action=${submitUrl} method="get">	
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row" <#if bTypeHidden == "true">style="display:none"</#if>>
				<#if propertyName == "locationOfDigitalData">
					<div class="form_label optional"><span class='requiredHint'>* </span>${labelType}:</div> 
				<#else>
					<div class="form_label"><span class='requiredHint'>* </span>${labelType}:</div> 
				</#if>
								
				<div class="form_value">
					<select name="txtDescriptionType" id="txtDescriptionType" id="desType" onchange="SelectChanged();" >
						<#list selectionTypes as optionType>
							<#if propertyName == "locationOfDigitalData">
								<#if optionType != "other">
									<option value="${optionType}" <#if optionType == descriptionType>selected="selected"</#if> >${optionType}</option>
								</#if>
							<#else>
								<#if bTypeHidden == "false">
									<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
								</#if>
								<option value="${optionType}" <#if optionType == descriptionType>selected="selected"</#if> >${optionType}</option>
							</#if>
						</#list>
					</select>
				</div> 
			</div>
			<#if propertyName == "ordcIdentifier">
				<div class="form_row">
					<div class="form_full_value">
						<input type="text" name="txtDescription" id="ordcIdentifier-orcidWidget" value="${descriptionValue}" />
					</div>
				</div>
			<#else>	
				<div class="form_row">
					<#if propertyName == "locationOfDigitalData">
						<div class="form_label optional"><span class='requiredHint'>* </span>${labelValue}:</div> 
					<#else>
						<div class="form_label"><span class='requiredHint'>* </span>${labelValue}:</div> 
					</#if>
					<div class="form_value">
							<#if textAreaSize == "input">
								<input type="text" name="txtDescription" id="description" value="${descriptionValue}"/>
							<#else>
								<textarea class="${textAreaSize}" type="text" name="txtDescription" id="description" role="input">${descriptionValue}</textarea>
							</#if>
					
					</div> 
				</div>
			</#if>
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
    
              
              
              
              
              
              
              