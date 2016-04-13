<#--
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<#--Assign variables from editConfig-->
<#assign rangeOptions = editConfiguration.pageData.objectVar />
<#assign rangeOptionsExist = false />

<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign propertyName = editConfiguration.pageData.propertyName />
<#assign helptext = "">

<#if (rangeOptions?keys?size > 0)>
	<#assign rangeOptionsExist = true/>
</#if>

<#assign TitleText = "Select an entry for '${editConfiguration.formTitle}'">

<#if editConfiguration.formTitle == "Has association with">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "List all groups, people, related datasets and equipment or services records related to this research group. You may need to create People & Group, Data Collection, Project or Equipment and Services records to Research Data Finder before they can be added here. Prior to creating records, search the registry to see if People or Project records already exist.">
		<#else>	
			<#assign helptext = "List all groups, people (other than the Principal Investigator), related datasets and equipment or services records related to this researcher. You may need to create People & Group, Data Collection, Project or Equipment and Services records to Research Data Finder before they can be added here.  Prior to creating records, search the registry to see if People or Project records already exist.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "List all groups, people, related datasets and equipment or services records related to this research project. You may need to create People & Group, Data Collection, Project or Equipment and Services records to Research Data Finder before they can be added here. Prior to creating records, search the registry to see if People or Project records already exist.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "List all groups, people, related datasets and equipment or services records related to this data collection. You may need to create People & Group, Data Collection, Project or Equipment and Services records to Research Data Finder before they can be added here. Prior to creating records, search the registry to see if People or Project records already exist.">
	<#elseif recordType == "service">
		<#assign helptext = "List all groups, people, related datasets and equipment or services records related to this equipment or service. You may need to create People & Group, Data Collection, Project or Equipment and Services records to Research Data Finder before they can be added here. Prior to creating records, search the registry to see if People or Project records already exist.">
	</#if> 
<#elseif editConfiguration.formTitle == "Has member">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Select the researcher/s that are members of the research group.">
		</#if>
	</#if>
<#elseif editConfiguration.formTitle == "Has part">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Select all people/groups that contain the group being described.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "Select all activities that are contained by the activity being described.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections that are contained within the data collection being described.">
	<#elseif recordType == "service">
		<#assign helptext = "Select all services (all sub types) that contain the service being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is collector of">
	<#if recordType == "people">
		<#assign helptext = "Select all data collections aggregated by the person/group being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is funded by">
	<#if recordType == "people">
		<#assign helptext = "Select all related people/groups that receive monetary or in-kind aid from the person/group being described.">
	<#elseif recordType == "activities">
		<#assign helptext = "Select all related people/groups or activities that receive monetary or in-kind aid from the activity (program only) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is funder of">
	<#if recordType == "people">
		<#assign helptext = "Select all related people/groups that provide monetary or in-kind aid to the person/group being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is managed by">
	<#if recordType == "people">
		<#assign helptext = "Select all people/groups, activities, data collections and services that oversee, maintain or make accessible the person/group being described.">
	<#elseif recordType == "activities">
		<#assign helptext = "Select all people/groups that oversee, maintain or make accessible the activity being described.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all people/groups that oversee, maintain or make accessible the data collection being described.">
	<#elseif recordType == "service">
		<#assign helptext = "Select all people/groups that oversee, maintain or make accessible the service being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is manager of">
	<#if recordType == "people">
		<#assign helptext = "Select all people/group, data collections and/or services that are overseen by the person/group being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is member of">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Select all people/groups enrolled in the group being described.">
		</#if>
	</#if>
<#elseif editConfiguration.formTitle == "Is owned by">
	<#if recordType == "people">
		<#assign helptext = "Select all people/groups, activities, data collections and/or services that are legally owned by the person/group being described.">
	<#elseif recordType == "activities">
		<#assign helptext = "Select all people/groups that legally own the activity being described.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all people/groups that legally own the data collection being described.">
	<#elseif recordType == "service">
		<#assign helptext = "Select all people/groups that legally own the service being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is owner of">
	<#if recordType == "people">
		<#assign helptext = "Select all groups, activities, data collections and/or services that are legally possessed by the group being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is part of">
	<#if recordType == "people">
		<#if recordSubType == "group">
			<#assign helptext = "Select all people/groups that are contained within the group being described.">
		</#if>
	<#elseif recordType == "activities">
		<#assign helptext = "Select all activities that are contained within the activity being described.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections (all sub types) that contain the data collection being described.">
	<#elseif recordType == "service">
		<#assign helptext = "Select all services that are contained within the service being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is participant in">
	<#if recordType == "people">
		<#assign helptext = "Select all activities that the person/group being described were enrolled in.">
	</#if>
<#elseif editConfiguration.formTitle == "Is chief investigator of">
	<#if recordType == "people">
		<#assign helptext = "Select all data collections associated with this researcher.">
	</#if>
<#elseif editConfiguration.formTitle == "Enriches">
	<#if recordType == "people">
		<#assign helptext = "Select all data collections that the people/group being described have added additional value to.">
	</#if>
<#elseif editConfiguration.formTitle == "Has output">
	<#if recordType == "activities">
		<#assign helptext = "Select all data collections that are an output of the project being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Has participant">
	<#if recordType == "activities">
		<#assign helptext = "Select all people/groups that participated in the activity being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Has Chief Investigator">
	<#if recordType == "activities">
		<#assign helptext = "Specify the Chief Investigator. People records must be first created before they can be added here.">
	<#elseif recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the Chief Investigator. People records must be first created before they can be added here.">
	</#if>
<#elseif editConfiguration.formTitle == "Describes">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections that are a catalogue for, or index of, items in the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Was collected by">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all related people/groups who have aggregated the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is described by">
	<#if recordType == "dataCollections">
		<#assign helptext = "Select all data collections that catalogue or index the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is located in">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections that hold the repository being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is location for">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections that are held by the repository being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is output of">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Specify the project that has produced the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Supports">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all services (all sub types) that the data collection being described can contribute to, and/or be accessed or used through.">
	</#if>
<#elseif editConfiguration.formTitle == "Is enriched by">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all people/groups that provide additional value to the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is derived from">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections that the data collection being described is a derivative of e.g. through analysis.">
	</#if>
<#elseif editConfiguration.formTitle == "Has derived collection">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all data collections that are derivatives of the data collection being described e.g. through analysis.">
	</#if>
<#elseif editConfiguration.formTitle == "Is available through">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all services (sub types Harvest, Search and Syndicate) that the data collection being described is available through.">
	</#if>
<#elseif editConfiguration.formTitle == "Is produced by">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all services (sub types Create, Generate, Assemble and Transform output) that produce the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is presented by">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all services (sub type Report) that present the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Has value added by">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all services (sub types Annotate and Classify) that add value to the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is operated on by">
	<#if recordType == "dataCollections" || recordType == "softwareAndCode" || recordType == "spatialData">
		<#assign helptext = "Select all services (sub type Transform input) that operate on the data collection being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Is supported by">
	<#if recordType == "service">
		<#assign helptext = "Select all data collections that contribute, provide access to and promote use of the service (all sub types) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Makes available">
	<#if recordType == "service">
		<#assign helptext = "Select all data collections (all sub types) that are made available by the service (sub types Harvest, Search and Syndicate) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Produces">
	<#if recordType == "service">
		<#assign helptext = "Select all data collections (all sub types) that are produced by the service (sub types Create, Generate, Assemble and Transform output) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Presents">
	<#if recordType == "service">
		<#assign helptext = "Select all data collections (all sub types) that are presented by the service (sub type Report) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Operates on">
	<#if recordType == "service">
		<#assign helptext = "Select all data collections (all sub types) that are operated on by the service (sub type Transform input) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Adds value to">
	<#if recordType == "service">
		<#assign helptext = "Select all data collections (all sub types) that add value to the service (sub types Annotate and Classify) being described.">
	</#if>
<#elseif editConfiguration.formTitle == "Partner Investigator working on">
	<#if recordType == "people">
		<#assign helptext = "Select all activity records for which the person being described is the Partner Investigator.">
	</#if>
<#elseif editConfiguration.formTitle == "Associate Investigator working on">
	<#if recordType == "people">
		<#assign helptext = "Select all activity records for which the person being described is the Associate Investigator.">
	</#if>
<#elseif editConfiguration.formTitle == "Fellow working on">
	<#if recordType == "people">
		<#assign helptext = "Select all activity records for which the person being described is a Fellow investigator e.g. NHMRC Fellow.">
	</#if>
<#elseif editConfiguration.formTitle == "Australian Professorial Fellowship holder working on">
	<#if recordType == "people">
		<#assign helptext = "Select all activity records for which the person being described is an Australian Professorial Fellowship holder.">
	</#if>
<#elseif editConfiguration.formTitle == "Australian Research Council Postdoctoral Research Fellow (APDI) working on">
	<#if recordType == "people">
		<#assign helptext = "Select all activity records for which the person being described is an Australian Research Council Postdoctoral Research Fellow (APDI).">
	</#if>
<#elseif editConfiguration.formTitle == "Principal Investigator/Industry contact for">
	<#if recordType == "people">
		<#assign helptext = "Select all activities for which the person/group being described is the Principal Investigator/Industry contact.">
	</#if>
<#elseif editConfiguration.formTitle == "Primary contact for">
	<#if recordType == "people">
		<#assign helptext = "Select all records that the person/group being described is the primary contact for.">
	</#if>
</#if>

<#if editConfiguration.propertySelectFromExisting = true>
    <#if rangeOptionsExist  = true >
        <#assign rangeOptionKeys = rangeOptions?keys />
        <form class="editForm" action = "${submitUrl}" id="editForm-${propertyName}">
			<div class="form_wrapper">
				<div class="left_column">
					<div id="relationship" class="form_row">
						<div class="form_label"><span class='requiredHint'>* </span>Select an entry :</div>
						<div class="form_value">
							<select id="objectVar" name="objectVar" role="select">
								<#if ! editConfiguration.objectUri?has_content>
									<option value="" role="option">Start typing for records to appear...</option>
								</#if>
								
								<#list rangeOptionKeys as key>
									<option value="${key}" <#if editConfiguration.objectUri?has_content && editConfiguration.objectUri = key>selected</#if> role="option">${rangeOptions[key]}</option>
								</#list>
							</select>
						</div>
					</div>
					
					<div class="button_wrapper">
						<div class="property-button cancel">Cancel</div>
						<div class="property-button submit">
							<input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
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
    <#else>
        <p> There are no entries in the system from which to select. </p>  
    </#if>
</#if>


<#if editConfiguration.propertySelectFromExisting = false && editConfiguration.propertyOfferCreateNewOption = false>
	<p>This property is currently configured to prohibit editing. </p>
</#if>

<#--
<#if editConfiguration.propertyOfferCreateNewOption = true>
	<#include "defaultOfferCreateNewOptionForm.ftl">
</#if>

<#if editConfiguration.includeDeletionForm = true>
	<#include "defaultDeletePropertyForm.ftl">
</#if>
-->
