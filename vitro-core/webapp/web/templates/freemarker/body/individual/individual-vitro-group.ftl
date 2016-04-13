<#list propertyGroups.all as group>
	 <#list group.properties as property>
		<#if property.localName == "biography">
			<#assign biography>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "researchAreas">
			<#assign researchAreas>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "predecessorOrganisation">
			<#assign predecessorOrganisation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "successorOrganisation">
			<#assign successorOrganisation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "qutEPrints">
			<#assign qutEPrints>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "publisherOfQut">
			<#assign publisherOfQut>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "relatedInformation">
			<#assign relatedInformation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "collaborator">
			<#assign collaborator>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "currentRelInfoMemberOf">
			<#assign currentRelInfoMemberOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "organizationWithin">
			<#assign organizationWithin>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "collectionOrSeriesEditorOf">
			<#assign collectionOrSeriesEditorOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "reviewerOf">
			<#assign reviewerOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "linkToexternalRecords">
			<#assign linkToexternalRecords>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "localKey">
			<#assign localKey>
				<@p.objectPropertyPublicView property false />
			</#assign>	
		<#elseif property.localName == "nlaPartyIdentifier">
			<#assign nlaPartyIdentifier>
				<@p.objectPropertyPublicView property false />
			</#assign>		
		<#elseif property.localName == "ordcIdentifier">
			<#assign ordcIdentifier>
				<@p.objectPropertyPublicView property false />
			</#assign>	
		<#elseif property.localName == "scopusAuthorID">
			<#assign scopusAuthorID>
				<@p.objectPropertyPublicView property false />
			</#assign>	
		<#elseif property.localName == "thomsonReutersResearcherID">
			<#assign thomsonReutersResearcherID>
				<@p.objectPropertyPublicView property false />
			</#assign>				
		<#elseif property.localName == "fellowOn">
			<#assign fellowOn>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "principleInvestigatorContactFor">
			<#assign principleInvestigatorContactFor>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasAssociationWith">
			<#assign hasAssociationWith>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasMember">
			<#assign hasMember>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isCollectorOf">
			<#assign isCollectorOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isFundedBy">
			<#assign isFundedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isFunderOf">
			<#assign isFunderOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isMemberOf">
			<#assign isMemberOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isOwnerOf">
			<#assign isOwnerOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isManagedBy">
			<#assign isManagedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isManagerOf">
			<#assign isManagerOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isParticipantIn">
			<#assign isParticipantIn>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isPrincipalInvestigatorOf">
			<#assign isPrincipalInvestigatorOf>
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
		
		<#if qutEPrints?? && qutEPrints != "">
			<div class="scf-left-row">
				<h4>Publications</h4>
					<div class="scf-right-row-description">
						${qutEPrints}
					</div>
			</div>
		</#if>
		
		<#if publisherOfQut?? && publisherOfQut != "">
			<div class="scf-left-row">
				<h4>Publisher of</h4>
					<div class="scf-right-row-description">
						${publisherOfQut}
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
		
		<#if predecessorOrganisation?? && predecessorOrganisation != "">
			<div class="scf-left-row">
				<h4>Predecessor organisation</h4>
				${predecessorOrganisation}
			</div>
		</#if>
		
		<#if successorOrganisation?? && successorOrganisation != "">
			<div class="scf-left-row">
				<h4>Successor organisation</h4>
				${successorOrganisation}
			</div>
		</#if>
		
		<#if relatedInformation?? && relatedInformation != "">
			<div class="scf-left-row">
				<h4>Related information</h4>
				${relatedInformation}
			</div>
		</#if>
		
		<#if collectionOrSeriesEditorOf?? && collectionOrSeriesEditorOf != "">
			<div class="scf-left-row">
				<h4>Collection or series editor of</h4>
				${collectionOrSeriesEditorOf}
			</div>
		</#if>
		
		<#if reviewerOf?? && reviewerOf != "">
			<div class="scf-left-row">
				<h4>Reviewer of</h4>
				${reviewerOf}
			</div>
		</#if>
		
	</div>
	<div id="scf-right-border"></div>
	<div id="scf-right-col">
		<#if (nlaPartyIdentifier?? && nlaPartyIdentifier != "") || (ordcIdentifier?? && ordcIdentifier != "") || (scopusAuthorID?? && scopusAuthorID != "") ||
			(thomsonReutersResearcherID?? && thomsonReutersResearcherID != "")>
			<div class="scf-right-row seperate-border">
				<h4>Identifiers</h4>
				<div class="scf-right-row-description">
					<#if nlaPartyIdentifier?? && nlaPartyIdentifier != "">
						${nlaPartyIdentifier}
					</#if>
					<#if ordcIdentifier?? && ordcIdentifier != "">
						${ordcIdentifier}
					</#if>
					<#if scopusAuthorID?? && scopusAuthorID != "">
						${scopusAuthorID}
					</#if>
					<#if thomsonReutersResearcherID?? && thomsonReutersResearcherID != "">
						${thomsonReutersResearcherID}
					</#if>
					
					<!-- <div id="small-line">DOI: 10.4225/01/513D576D36128</div> -->
				</div>
			</div>
		</#if>
		
		<#if (fellowOn?? && fellowOn != "") || (principleInvestigatorContactFor?? && principleInvestigatorContactFor != "") || (hasAssociationWith?? && hasAssociationWith != "") ||(hasMember?? && hasMember != "") || (isCollectorOf?? && isCollectorOf != "") 
			|| (isFundedBy?? && isFundedBy != "") || (isFunderOf?? && isFunderOf != "") || (isOwnerOf?? && isOwnerOf != "") || (isManagedBy?? && isManagedBy != "") || (isManagerOf?? && isManagerOf != "") || (isMemberOf?? && isMemberOf != "") || (isParticipantIn?? && isParticipantIn != "") ||
			(isPrincipalInvestigatorOf?? && isPrincipalInvestigatorOf != "") || (linkToexternalRecords?? && linkToexternalRecords != "")>
			<div class="scf-right-row seperate-border">
				<h4>Connections</h4>
				<#if fellowOn?? && fellowOn != "">
					<div class="scf-right-row-description">
						<h5>Fellow working on</h5>
						${fellowOn}
					</div>
				</#if>
				<#if principleInvestigatorContactFor?? && principleInvestigatorContactFor != "">
					<div class="scf-right-row-description">
						<h5>Principal Investigator/Industry contact for</h5>
						${principleInvestigatorContactFor}
					</div>
				</#if>
				<#if hasAssociationWith?? && hasAssociationWith != "">
					<div class="scf-right-row-description">
						<h5>Has association with</h5>
						${hasAssociationWith}
					</div>
				</#if>
				<#if hasMember?? && hasMember != "">
					<div class="scf-right-row-description">
						<h5>Has member</h5>
						${hasMember}
					</div>
				</#if>
				<#if isCollectorOf?? && isCollectorOf != "">
					<div class="scf-right-row-description">
						<h5>Is collector of</h5>
						${isCollectorOf}
					</div>
				</#if>
				<#if isFundedBy?? && isFundedBy != "">
					<div class="scf-right-row-description">
						<h5>Is funded by</h5>
						${isFundedBy}
					</div>
				</#if>
				<#if isFunderOf?? && isFunderOf != "">
					<div class="scf-right-row-description">
						<h5>Is funder of</h5>
						${isFunderOf}
					</div>
				</#if>
				<#if isOwnerOf?? && isOwnerOf != "">
					<div class="scf-right-row-description">
						<h5>Is owner of</h5>
						${isOwnerOf}
					</div>
				</#if>
				<#if isManagedBy?? && isManagedBy != "">
					<div class="scf-right-row-description">
						<h5>Is managed by</h5>
						${isManagedBy}
					</div>
				</#if>
				<#if isManagerOf?? && isManagerOf != "">
					<div class="scf-right-row-description">
						<h5>Is member of</h5>
						${isManagerOf}
					</div>
				</#if>
				<#if isMemberOf?? && isMemberOf != "">
					<div class="scf-right-row-description">
						<h5>Is manager of</h5>
						${isMemberOf}
					</div>
				</#if>
				<#if isParticipantIn?? && isParticipantIn != "">
					<div class="scf-right-row-description">
						<h5>Is participant in</h5>
						${isParticipantIn}
					</div>
				</#if>
				<#if isPrincipalInvestigatorOf?? && isPrincipalInvestigatorOf != "">
					<div class="scf-right-row-description">
						<h5>Is principal investigator of</h5>
						${isPrincipalInvestigatorOf}
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
		
		<#if collaborator?? && collaborator != "">
			<div class="scf-right-row seperate-border">
				<h4>Collaborators/Industry partners</h4>
				${collaborator}
			</div>
		</#if>
		
		<#if currentRelInfoMemberOf?? && currentRelInfoMemberOf != "">
			<div class="scf-right-row seperate-border">
				<h4>Current member of</h4>
				${currentRelInfoMemberOf}
			</div>
		</#if>
		
		<#if organizationWithin?? && organizationWithin != "">
			<div class="scf-right-row seperate-border">
				<h4>Organisation within</h4>
				${organizationWithin}
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
		
		<#if (dateRecordCreated?? && dateRecordCreated != "") || (dateRecordModified?? && dateRecordModified != "") || (publishRecord?? && publishRecord != "")>
			<div class="scf-right-row seperate-border">
				<h4>Other</h4>
				<div class="scf-right-row-description">
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