
<script type="text/javascript">
  $(document).ready(function() {
	$("#mapContainer").ands_location_widget({
		lonLat:"151.503462,-33.251348 150.558638,-33.039778 149.767623,-34.137978 150.844283,-34.754050 151.283736,-33.937689 151.503462,-33.251348"
	});
  });
</script>

<#list propertyGroups.all as group>
	 <#list group.properties as property>
		<#if property.localName == "biography">
			<#assign biography>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "qutEPrints">
			<#assign qutEPrints>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "researchAreas">
			<#assign researchAreas>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "relatedInformation">
			<#assign relatedInformation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "AOUName">
			<#assign AOUName>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "existenceDates">
			<#assign existenceDates>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "temporalCoverage">
			<#assign temporalCoverage>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "localKey">
			<#assign localKey>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "spatialCoverage">
			<#assign spatialCoverage>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "otherIdentifierType">
			<#assign otherIdentifierType>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasAssociationWith">
			<#assign hasAssociationWith>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasOutput">
			<#assign hasOutput>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasParticipant">
			<#assign hasParticipant>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasPrincipalInvestigator">
			<#assign hasPrincipalInvestigator>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isFundedBy">
			<#assign isFundedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "linkToexternalRecords">
			<#assign linkToexternalRecords>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "website">
			<#assign website>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "prefefredTitleText">
			<#assign prefefredTitleText>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "dataTypeInfo">
			<#assign dataTypeInfo>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "embargoDate">
			<#assign embargoDate>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "partnerInstitution">
			<#assign partnerInstitution>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "retentionPeriod">
			<#assign retentionPeriod>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "dateOfDisposal">
			<#assign dateOfDisposal>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "projectStatus">
			<#assign projectStatus>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "dateRecordCreated">
			<#assign dateRecordCreated>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "dateRecordModified">
			<#assign dateRecordModified>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "internalOrExternal">
			<#assign internalOrExternal>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "fundingScheme">
			<#assign fundingScheme>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "grantor">
			<#assign grantor>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "publishRecord">
			<#assign publishRecord>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "contactInfo">
			<#assign contactInfo>
				<@p.objectPropertyPublicView property false />
			</#assign>
		</#if>

	 </#list>
</#list>

<section id="individual-intro" class="vcard" role="region">
	<div id="scf-left-col">
		<div class="scf-individual-heading">
				<#if relatedSubject??>
					<h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
					<p><a href="${relatedSubject.url}" title="return to subject">&larr; return to ${relatedSubject.name}</a></p>                
				<#else> 
					<h2 class="selected-class">
						<span id="supPrefTitle" style="font-weight: bold;"></span>
						<@p.label individual false labelCount /> 
						<#if currentUserRole ??>
							<#if currentUserRole == "ROOT" || currentUserRole == "SELF">
								<img id="uriIcon" title="${individual.uri}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon"/>
							</#if>
						</#if>
					</h2>
				</#if>
		</div>
		
		<div class="scf-left-top-row">
			<#if biography?? && biography != "">
				${biography}
			</#if>
		</div>
		
		<#if spatialCoverage?? && spatialCoverage != "">
            <div class="scf-left-row">
                <h4>Geographical area of data collection</h4>
                ${spatialCoverage}
            </div>
         </#if>
		
		<#if qutEPrints?? && qutEPrints != "">
			<div class="scf-left-row">
				<h4>Publications</h4>
				<div class="scf-right-row-description">
					${qutEPrints}
				</div>
			</div>
		</#if>
		
		<#if researchAreas?? && researchAreas != "">
			<div class="scf-left-row">
				<h4>Research areas</h4>
				<div class="scf-left-row-description">
					${researchAreas}
				</div>
			</div>
		</#if>
		
		<#if relatedInformation?? && relatedInformation != "">
			<div class="scf-left-row">
				<h4>Related information</h4>
				${relatedInformation}
			</div>
		</#if>
		
		<#if partnerInstitution?? && partnerInstitution != "" >
			<div class="scf-left-row">
				<h4>Partner institution</h4>
				${partnerInstitution}
			</div>
		</#if>
		
		<#if (internalOrExternal?? && internalOrExternal != "") || (fundingScheme?? && fundingScheme != "") || (grantor?? && grantor != "") >
			<h4>Funding</h4>
				<#if internalOrExternal?? && internalOrExternal != "" >
					<div id = "multi_box">
						${internalOrExternal}
					</div>
				</#if>
			
				<#if fundingScheme?? && fundingScheme != "" >
					<div id = "multi_box">
						${fundingScheme}
					</div>
				</#if>
			
				<#if grantor?? && grantor != "" >
					<div id = "multi_box">
						${grantor}
					</div>
				</#if>
		</#if>
		
	</div>
	<div id="scf-right-border"></div>
	<div id="scf-right-col">
	
		<#if (otherIdentifierType?? && otherIdentifierType != "")>
			<div class="scf-right-row seperate-border">
				<h4>Identifiers</h4>
				<div class="scf-right-row-description">
					<#if otherIdentifierType?? && otherIdentifierType != "">
						${otherIdentifierType}
					</#if>
					<!-- <div id="small-line">DOI: 10.4225/01/513D576D36128</div> -->
				</div>
			</div>
		</#if>
		
		<#if website?? && website != "">
			<div class="scf-right-row seperate-border">
				<h4>Website</h4>
				${website}
			</div>
		</#if>
		
		<#if existenceDates?? && existenceDates != "">
			<div class="scf-right-row seperate-border">
				<h4>Duration of research activity</h4>
				<div class="scf-right-row-description">
					${existenceDates}
				<div>
			</div>
		</#if>
		
		<#if temporalCoverage?? && temporalCoverage != "">
			<div class="scf-right-row">
				<h4>Dates of data collection</h4>
				<div class="scf-right-row-description">
					${temporalCoverage}
				<div>
			</div>
		</#if>
		
		<#if (hasAssociationWith?? && hasAssociationWith != "") || (hasOutput?? && hasOutput != "") || (hasParticipant?? && hasParticipant != "") || (hasPrincipalInvestigator?? && hasPrincipalInvestigator != "") || (isFundedBy?? && isFundedBy != "")
			|| (linkToexternalRecords?? && linkToexternalRecords != "")>
			<div class="scf-right-row seperate-border">
				<h4>Connections</h4>
				<#if hasAssociationWith?? && hasAssociationWith != "">
					<div class="scf-right-row-description">
						<h5>Has association with</h5>
						${hasAssociationWith}
					</div>
				</#if>
				<#if hasOutput?? && hasOutput != "">
					<div class="scf-right-row-description">
						<h5>Has output</h5>
						${hasOutput}
					</div>
				</#if>
				<#if hasParticipant?? && hasParticipant != "">
					<div class="scf-right-row-description">
						<h5>Has participant</h5>
						${hasParticipant}
					</div>
				</#if>
				<#if hasPrincipalInvestigator?? && hasPrincipalInvestigator != "">
					<div class="scf-right-row-description">
						<h5>Has principal investigator</h5>
						${hasPrincipalInvestigator}
					</div>
				</#if>
				<#if isFundedBy?? && isFundedBy != "">
					<div class="scf-right-row-description">
						<h5>Is funded by</h5>
						${isFundedBy}
					</div>
				</#if>
				<#if linkToexternalRecords?? && linkToexternalRecords != "">
					<div class="scf-right-row-description">
						<h5>Other</h5>
						${linkToexternalRecords}
					</div>
				</#if>

			</div>
		</#if>
		
		<#if contactInfo?? && contactInfo != "">
			<div class="scf-right-row seperate-border">
				<h4>Contacts</h4>
				<div class="scf-right-row-description">
					${contactInfo}
				</div>
			</div>
		</#if>
		
		<#if (dateRecordCreated?? && dateRecordCreated != "") || (dateRecordModified?? && dateRecordModified != "") || (AOUName?? && AOUName != "") || (projectStatus?? && projectStatus != "") || (publishRecord?? && publishRecord != "")>
			<div class="scf-right-row seperate-border">
				<h4>Other</h4>
				<div class="scf-right-row-description">
					<#if AOUName?? && AOUName != "">
						<div id="normal-line">
							${AOUName}
						</div>
					</#if>
					<#if dateRecordCreated?? && dateRecordCreated != "">
						<div id="normal-line">
							${dateRecordCreated}
						</div>
					</#if>
					<#if dateRecordModified?? && dateRecordModified != "">
						<div id="normal-line">
							${dateRecordModified}
						</div>
					</#if>
					<#if projectStatus?? && projectStatus != "">
						<div id="normal-line">
							${projectStatus}
						</div>
					</#if>
					<#if publishRecord?? && publishRecord != "">
						<div id="normal-line">
							${publishRecord}
						</div>
					</#if>
				</div>
			</div>
		</#if>
		
		<#if currentUserRole?? && currentUserRole != "">
			<#if currentUserRole == "CURATOR" || currentUserRole == "DB_ADMIN" || currentUserRole == "ROOT" || currentUserRole == "SELF">
				<div class="scf-right-row seperate-top-bottom-border">
					<h4>Admin panel</h4>
					<div class="scf-right-row-description">
						<form action=${individualURL} method="get">
							<div id="small-line-button"><button type="submit" id="btn-edit_delete_action">Switch to Edit mode</button></div>
							<input type="hidden" name="displayMode" value="edit"></input>
						</form>
						<#-- <div id="small-line-button"><button type="submit" id="btn-edit_delete_action">Delete this record</button></div> -->
					</div>
				</div>
			</#if>
		</#if>
		
	</div>
</section> <!-- individual-intro -->
