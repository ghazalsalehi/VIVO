<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign relatedInfoTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtRelatedInfoType") />
<#assign identifierTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtIdentifierType") />
<#assign identifierValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtIdentifierValue") />
<#assign titleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtTitleValue") />
<#-- <#assign noteValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtNoteValue") /> -->


<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign relatedInfoTypes = editConfiguration.pageData.relatedInfoTypes />
<#assign identifierTypes = editConfiguration.pageData.identifierTypes />

<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign textAreaSize = editConfiguration.pageData.textAreaSize />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#assign relatedInfoTypeText = "Link to">
<#assign identifierTypeText = "URL or DOI">
<#assign identifierValueText = "Link">
<#assign titleText = "Text to display">
<#assign noteText = "Note">

<#if propertyName == "predecessorOrganisation">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Name/s of predecessor organisation/s e.g. Queensland Institute of Technology would be listed for the Queensland University of Technology.">
		</#if>
	</#if> 
<#elseif propertyName == "successorOrganisation">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "If the organisation has undergone a name change since the related research activity was completed, enter new name here e.g. Institute for Future Environments (IFE) instead of Institute for Sustainable Resources (ISR).">
		</#if>
	</#if> 
<#elseif propertyName == "organizationWithin">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "List the name of the organisation that the group operates within e.g. Institute of Health and Biomedical Innovation is an organisation within the Queensland University of Technology.">
		</#if>
	</#if> 
<#elseif propertyName == "affiliatedInstitution">
	<#if recordType == "people">
		<#assign helptext = "Specify the institutions affiliated with the person/group being described e.g. Queensland University of Technology and Queensland Health.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the institutions affiliated with the collection of the research data being described e.g Queensland University of Technology and Queensland Health.">
	</#if> 
<#elseif propertyName == "currentRelInfoMemberOf">
	<#if recordType == "people">
		<#assign helptext = "List and link to all professional groups or associations the person/group is a member of e.g Queensland Bar Association.">
	</#if> 
<#elseif propertyName == "headOf">
	<#if recordType == "people">
		<#assign helptext = "List the school/department/faculty/group/organisation the person being described has responsibility for.">
	</#if> 
<#elseif propertyName == "collaborator">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Link to all affiliated partners who are associated with this research group.">
		<#else>			
			<#assign helptext = "Link to all affiliated partners who are associated with this person&#39;s research.">
		</#if>
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List and link to all affiliated partners who contributed to the collection/creation of the related research data e.g. If the Electoral Commission Queensland covered the cost of data collection activities, list the Commission in this field. ">
	</#if>
<#elseif propertyName == "clinicalActivities">
	<#if recordType == "people">
		<#assign helptext = "If relevant, list all clinical activities undertaken by the person/group being described, during the course of the research activity that led to the collection/creation of the related data. ">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "If relevant, list all clinical activities undertaken during the course of the research activity that led to the collection/creation of the related data.">
	</#if>
<#elseif propertyName == "qutEPrints">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Provide the URL for any related publications, including those in <a href=\"http://eprints.qut.edu.au/\" target=\"_blank\">QUT ePrints</a>">
		<#else>
			<#assign helptext = "Provide the URL for the researcher&#39;s QUT ePrints author webpage e.g. <a href= 'http://eprints.qut.edu.au/view/person/Frost,_Ray.html' target='_balnk'>Frost,_Ray</a>">
		</#if>
		<#if titleValue == "">
			<#assign titleValue = "Author publications, as listed on QUT ePrints">	
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "Include URLs for all publications that are related to or resulting from the completed research activity.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Include URLs for all publications that are related to or resulting from the creation/collection/use of the associated data.">
	<#elseif recordType == "service">
		<#assign helptext = "Include URLs for all publications that are related to or resulting from the use of the equipment or service.">
	</#if> 
<#elseif propertyName == "publisherOfQut">
	<#if recordType == "people">
		<#assign helptext = "Provide links to works published by the person/group being described e.g. The group 'Queensland University of Technology' is a publisher of the Journal of Learning Design.">
	</#if> 
<#elseif propertyName == "relatedMetadata">
	<#if recordType == "people">
		<#assign helptext = "Provide links to richer metadata formats used to describe the person/group e.g. Ecological Markup Language (EML).">
	<#elseif recordType == "activities">
		<#assign helptext = "Provide links to richer metadata formats used to describe the activity e.g. Ecological Markup Language (EML).">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Provide links to richer metadata formats used to describe the data collection e.g. Ecological Markup Language (EML).">
	<#elseif recordType == "service">
		<#assign helptext = "Provide links to richer metadata formats used to describe the service e.g. Ecological Markup Language (EML).">
	</#if>
<#elseif propertyName == "relatedInformation">
	<#if recordType == "people">
		<#assign helptext = "Provide links to any related information e.g. online news article, industry partner website, collaborators' website, publications (use 'Publications' field for publications listed in QUT ePrints or elsewhere), data dictionaries, methodology, software or software configuration, models, workflows, etc.">
	<#elseif recordType == "activities">
		<#assign helptext = "Provide links to any related information e.g. online news article, industry partner website, collaborators' website, publications (use 'Publications' field for publications listed in QUT ePrints or elsewhere), data dictionaries, methodology, software or software configuration, models, workflows, etc.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Provide links to any related information e.g. online news article, industry partner website, collaborators' website, publications (use 'Publications' field for publications listed in QUT ePrints or elsewhere), data dictionaries, methodology, software or software configuration, models, workflows, etc.">
	<#elseif recordType == "service">
		<#assign helptext = "Provide links to any related information e.g. online news article, industry partner website, collaborators' website, publications (use 'Publications' field for publications listed in QUT ePrints or elsewhere), data dictionaries, methodology, software or software configuration, models, workflows, etc.">
	</#if>
<#elseif propertyName == "partnerInstitution">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Enter the website URL for any partnering institution involved in the creation or collection of the data being described.">
	<#elseif recordType == "activities">
		<#assign helptext = "Enter the website URL for any partnering institution involved in this research project or activity.">
	<#elseif recordType == "softwareAndCode">
		<#assign helptext = "Enter the website URL for any partnering institution involved in the creation or collection of the data being described.">
	</#if>
<#elseif propertyName == "facultyOrDepartmentName">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List and link to the website for the faculty, school or department within the affiliated institution that is connected to the data collection being described.">
	</#if>
<#elseif propertyName == "partOf">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List and link to any research scheme or program that the data collection being described is a part of e.g. The Australian Election Study (ANU).">
	</#if>
<#elseif propertyName == "collectionOrSeriesEditorOf">
	<#if recordType == "people">
		<#assign helptext = "List and link to the collections or series that the person/group being described is an editor of.">
	</#if>
<#elseif propertyName == "organiserOf">
	<#if recordType == "people">
		<#assign helptext = "List and link to the events/activities the person/group being described has organised.">
	</#if>
<#elseif propertyName == "reviewerOf">
	<#if recordType == "people">
		<#assign helptext = "List and link to the publications the person/group being described was a reviewer for.">
	</#if>
<#elseif propertyName == "serviceToTheProfession">
	<#if recordType == "people">
		<#assign helptext = "List any awards for service to their profession awarded to the person/group being described.">
	</#if>
<#elseif propertyName == "outreachService">
	<#if recordType == "people">
		<#assign helptext = "List and link to any outreach and community service activities the person/group being described was/is involved in.">
	</#if>
</#if>

	<form id="mainform-${propertyName}" action=${submitUrl} method="get">
		<div class="form_wrapper">
			<div class="left_column">
			<#if propertyName == "relatedInformation" || propertyName == "collaborator" || propertyName == "predecessorOrganisation" || propertyName == "successorOrganisation" || propertyName == "partnerInstitution">
				<div class="form_row" style="display:none;">
			<#else>
				<div class="form_row">
			</#if>
					<div class="form_label"><span class='requiredHint'>* </span>${relatedInfoTypeText}:</div> 
					<div class="form_value">
						<#if propertyName == "relatedInformation" || propertyName == "collaborator" || propertyName == "predecessorOrganisation" || propertyName == "successorOrganisation" || propertyName == "partnerInstitution">
							<select name="txtRelatedInfoType" id="relatedInfoType">
								<#list relatedInfoTypes as optionType1>
									<#if optionType1 == "website">
										<option value="${optionType1}" <#if editMode != "edit">selected="selected"<#elseif optionType1 == relatedInfoTypeValue>selected="selected"</#if> >${optionType1}</option>
									<#else>
										<option value="${optionType1}" <#if optionType1 == relatedInfoTypeValue>selected="selected"</#if> >${optionType1}</option>
									</#if>
								</#list>
							</select>
						<#elseif propertyName == "publication">
							<select name="txtRelatedInfoType" id="relatedInfoType">
								<#list relatedInfoTypes as optionType1>
									<#if optionType1 == "publication">
										<option value="${optionType1}" <#if editMode != "edit">selected="selected"<#elseif optionType1 == relatedInfoTypeValue>selected="selected"</#if> >QUT ePrints</option>
									<#elseif optionType1 == "website">
										<option value="${optionType1}" <#if optionType1 == relatedInfoTypeValue>selected="selected"</#if> >Other</option>
									<#else>
										<option value="${optionType1}" <#if optionType1 == relatedInfoTypeValue>selected="selected"</#if> >${optionType1}</option>
									</#if>
								</#list>
							</select>
						<#else>
							<select name="txtRelatedInfoType" id="relatedInfoType">
								<#list relatedInfoTypes as optionType1>
									<#if optionType1 == "publication">
										<option value="${optionType1}" <#if editMode != "edit">selected="selected"<#elseif optionType1 == relatedInfoTypeValue>selected="selected"</#if> >QUT ePrints</option>
									<#elseif optionType1 == "website">
										<option value="${optionType1}" <#if optionType1 == relatedInfoTypeValue>selected="selected"</#if> >Other</option>
									<#else>
										<option value="${optionType1}" <#if optionType1 == relatedInfoTypeValue>selected="selected"</#if> >${optionType1}</option>
									</#if>
									
								</#list>
							</select>
						</#if>
					</div> 
				</div>
	
			<#if propertyName == "relatedInformation" || propertyName == "collaborator" || propertyName == "predecessorOrganisation" || propertyName == "successorOrganisation" || propertyName == "partnerInstitution">
				<div class="form_row" style="display:none;">
			<#else>
				<div class="form_row">
			</#if>
					<div class="form_label"><span class='requiredHint'>* </span>${identifierTypeText}:</div> 
					<div class="form_value">
						<select name="txtIdentifierType" id="identifierType">
							<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
							<#list identifierTypes as optionType2>
								<#if optionType2 == "url">
									<option value="${optionType2}" <#if editMode != "edit">selected="selected"<#elseif optionType2 == identifierTypeValue>selected="selected"</#if> >URL</option>
								<#elseif optionType2 == "doi">
									<option value="${optionType2}" <#if optionType2 == identifierTypeValue>selected="selected"</#if> >DOI</option>
								<#else>
									<option value="${optionType2}" <#if optionType2 == identifierTypeValue>selected="selected"</#if> >${optionType2}</option>
								</#if>								
							</#list>
						</select>
					</div> 
				</div>
		
				<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>${identifierValueText}:</div> 
					<div class="form_value"><input type="text" name="txtIdentifierValue" id="idenValue" value="${identifierValue}"/></div> 
				</div>
				
				<div class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>${titleText}:</div> 
					<div class="form_value"><input type="text" name="txtTitleValue" id="title" value="${titleValue}"/></div> 
				</div>
				<#--
				<div id="note" class="form_row">
					<div class="form_label">${noteText}:</div> 
					<div class="form_value"><input type="text" name="txtNoteValue" value="${noteValue}"/></div> 
				</div>
				-->
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
          
