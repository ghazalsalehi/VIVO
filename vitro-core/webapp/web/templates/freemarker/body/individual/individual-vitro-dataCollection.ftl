
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
		<#elseif property.localName == "citationStyle">
			<#assign citationStyle>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "dataCitation">
			<#assign dataCitation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "relatedInformation">
			<#assign relatedInformation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "copyrightInfo">
			<#assign copyrightInfo>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "accessRightsInfo">
			<#assign accessRightsInfo>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "licence">
			<#assign licence>
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
		<#elseif property.localName == "locationOfDigitalData">
			<#assign locationOfDigitalData>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "offlineLocation">
			<#assign offlineLocation>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "RDRDataUpload">
			<#assign RDRDataUpload>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "temporalCoverage">
			<#assign temporalCoverage>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasAssociationWith">
			<#assign hasAssociationWith>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasPrincipalInvestigator">
			<#assign hasPrincipalInvestigator>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isAvailableThrough">
			<#assign isAvailableThrough>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isDescribedBy">
			<#assign isDescribedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isManagedBy">
			<#assign isManagedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isOperatedOnBy">
			<#assign isOperatedOnBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isOutputOf">
			<#assign isOutputOf>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isOwnedBy">
			<#assign isOwnedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isPresentedBy">
			<#assign isPresentedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isProducedBy">
			<#assign isProducedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isSupportedBy">
			<#assign isSupportedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "supports">
			<#assign supports>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "hasCollector">
			<#assign hasCollector>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "linkToexternalRecords">
			<#assign linkToexternalRecords>
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
		
		<#if (accessRightsInfo?? && accessRightsInfo != "")>
			<#if accessRightsInfo?? && accessRightsInfo != "">
				<h4>Access rights</h4>
				${accessRightsInfo}
			</#if>
		</#if>
		
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
		
		<#if (citationStyle?? && citationStyle != "") || (dataCitation?? && dataCitation!="")>
			<div class="scf-left-row">
				<h4>Cite this collection</h4>
				<#if citationStyle?? && citationStyle != "">
					${citationStyle}
				</#if>

				<#if dataCitation?? && dataCitation != "">
					${dataCitation}
				</#if>				
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
	</div>
	<div id="scf-right-border"></div>
	<div id="scf-right-col">
		<!--
		<div class="scf-right-top-row">
			<h4>Identifiers</h4>
			<div class="scf-right-row-description">
				<#if localKey?? && localKey != "">
					${localKey}
				</#if>
			</div>
		</div>
		-->
		<!-- <div id="small-line">DOI: 10.4225/01/513D576D36128</div>-->
		
		<#if (locationOfDigitalData?? && locationOfDigitalData != "") || (offlineLocation?? && offlineLocation != "") || (RDRDataUpload?? && RDRDataUpload != "")>
			<div class="scf-right-row seperate-border">
				<h4>Access the data</h4>
				<div class="scf-right-row-description">
					<#if locationOfDigitalData?? >
						${locationOfDigitalData}
					</#if>
					
					<#if offlineLocation?? >
						${offlineLocation}
					</#if>
					
					<#if RDRDataUpload?? >
						${RDRDataUpload}
					</#if>
					
				</div>
			</div>
		</#if>
		
		<#if (copyrightInfo?? && copyrightInfo != "") || (licence?? && licence != "")>
			<div class="scf-right-row seperate-border">
				<div class="scf-right-row-description">
					<#if licence?? && licence != "">
						<h4>Licence</h4>
						${licence}
					</#if>
					<#if copyrightInfo?? && copyrightInfo != "">
						<h4>Copyright</h4>
						${copyrightInfo}
					</#if>
				</div>
			</div>
		</#if>
		
		<#if temporalCoverage?? && temporalCoverage != "">
			<div class="scf-right-row seperate-border">
				<h4>Dates of data collection</h4>
				<div class="scf-right-row-description">
					${temporalCoverage}
				<div>
			</div>
		</#if>
		
		<#if (hasAssociationWith?? && hasAssociationWith != "") || (hasPrincipalInvestigator?? && hasPrincipalInvestigator != "") || (isAvailableThrough?? && isAvailableThrough != "") || (isDescribedBy?? && isDescribedBy != "") || (isManagedBy?? && isManagedBy != "")
			|| (isOperatedOnBy?? && isOperatedOnBy != "") || (isOutputOf?? && isOutputOf != "") || (isOwnedBy?? && isOwnedBy != "") || (isPresentedBy?? && isPresentedBy != "") || (isProducedBy?? && isProducedBy != "") 
			|| (isSupportedBy?? && isSupportedBy != "") || (supports?? && supports != "") || (hasCollector?? && hasCollector != "") || (linkToexternalRecords?? && linkToexternalRecords != "")>
			<div class="scf-right-row seperate-border">
				<h4>Connections</h4>
				<#if hasAssociationWith?? && hasAssociationWith != "">
					<div class="scf-right-row-description">
						<h5>Has association with</h5>
						${hasAssociationWith}
					</div>
				</#if>
				<#if hasPrincipalInvestigator?? && hasPrincipalInvestigator != "">
					<div class="scf-right-row-description">
						<h5>Has principal investigator</h5>
						${hasPrincipalInvestigator}
					</div>
				</#if>
				<#if isAvailableThrough?? && isAvailableThrough != "">
					<div class="scf-right-row-description">
						<h5>Is available through</h5>
						${isAvailableThrough}
					</div>
				</#if>
				<#if isDescribedBy?? && isDescribedBy != "">
					<div class="scf-right-row-description">
						<h5>Is described by</h5>
						${isDescribedBy}
					</div>
				</#if>
				<#if isManagedBy?? && isManagedBy != "">
					<div class="scf-right-row-description">
						<h5>Is managed by</h5>
						${isManagedBy}
					</div>
				</#if>
				<#if isOperatedOnBy?? && isOperatedOnBy != "">
					<div class="scf-right-row-description">
						<h5>Is operated on by</h5>
						${isOperatedOnBy}
					</div>
				</#if>
				<#if isOutputOf?? && isOutputOf != "">
					<div class="scf-right-row-description">
						<h5>Is output of</h5>
						${isOutputOf}
					</div>
				</#if>
				<#if isOwnedBy?? && isOwnedBy != "">
					<div class="scf-right-row-description">
						<h5>Is owned by</h5>
						${isOwnedBy}
					</div>
				</#if>
				<#if isPresentedBy?? && isPresentedBy != "">
					<div class="scf-right-row-description">
						<h5>Is presented by</h5>
						${isPresentedBy}
					</div>
				</#if>
				<#if isProducedBy?? && isProducedBy != "">
					<div class="scf-right-row-description">
						<h5>Is produced by</h5>
						${isProducedBy}
					</div>
				</#if>
				<#if isSupportedBy?? && isSupportedBy != "">
					<div class="scf-right-row-description">
						<h5>Is supported by</h5>
						${isSupportedBy}
					</div>
				</#if>
				<#if supports?? && supports != "">
					<div class="scf-right-row-description">
						<h5>Supports</h5>
						${supports}
					</div>
				</#if>
				<#if hasCollector?? && hasCollector != "">
					<div class="scf-right-row-description">
						<h5>Was collected by</h5>
						${hasCollector}
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
		
		<#if (dataTypeInfo?? && dataTypeInfo != "") || (embargoDate?? && embargoDate != "") || (retentionPeriod?? && retentionPeriod != "") || (dateOfDisposal?? && dateOfDisposal != "") || 
			(dateRecordCreated?? && dateRecordCreated != "") || (dateRecordModified?? && dateRecordModified != "") || (publishRecord?? && publishRecord != "")>
			<div class="scf-right-row seperate-border">
				<h4>Other</h4>
				<div class="scf-right-row-description">
					<#if dataTypeInfo?? && dataTypeInfo != "">
						<div id="normal-line">
							${dataTypeInfo}
						</div>
					</#if>
					<#if embargoDate?? && embargoDate != "">
						<div id="normal-line">
							${embargoDate}
						</div>
					</#if>
					<#if retentionPeriod?? && retentionPeriod != "">
						<div id="normal-line">
							${retentionPeriod}
						</div>
					</#if>
					<#if dateOfDisposal?? && dateOfDisposal != "">
						<div id="normal-line">
							${dateOfDisposal}
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

