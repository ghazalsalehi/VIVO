
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
		<#elseif property.localName == "hasAssociationWith">
			<#assign hasAssociationWith>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isSupportedBy">
			<#assign isSupportedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "isManagedBy">
			<#assign isManagedBy>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "produces">
			<#assign produces>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "presents">
			<#assign presents>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "supports">
			<#assign supports>
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
		<#elseif property.localName == "website">
			<#assign website>
				<@p.objectPropertyPublicView property false />
			</#assign>
		<#elseif property.localName == "accessPolicyURL">
			<#assign accessPolicyURL>
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
		<!-- <div id="small-line">DOI: 10.4225/01/513D576D36128</div> -->
		
		<#if website?? && website != "">
			<div class="scf-right-row seperate-border">
				<h4>Website</h4>
				${website}
			</div>
		</#if>
		<#if accessPolicyURL?? && accessPolicyURL != "">
			<div class="scf-right-row seperate-border">
				<h4>Access policy URL</h4>
				${accessPolicyURL}
			</div>
		</#if>
		
		<#if (locationOfDigitalData?? && locationOfDigitalData != "") || (offlineLocation?? && offlineLocation != "")>
			<div class="scf-right-row seperate-border">
				<h4>Access the data</h4>
				<div class="scf-right-row-description">
					<#if locationOfDigitalData?? >
						${locationOfDigitalData}
					</#if>
					
					<#if offlineLocation??>
						${offlineLocation}
					</#if>
				</div>
			</div>
		</#if>
		
		<#if (hasAssociationWith?? && hasAssociationWith != "") || (isSupportedBy?? && isSupportedBy != "") || (produces?? && produces != "") || (isManagedBy?? && isManagedBy != "") || (presents?? && presents != "") || (supports?? && supports != "")
			|| (linkToexternalRecords?? && linkToexternalRecords != "")>
			<div class="scf-right-row seperate-border">
				<h4>Connections</h4>
				<#if hasAssociationWith?? && hasAssociationWith != "">
					<div class="scf-right-row-description">
						<h5>Has association with</h5>
						${hasAssociationWith}
					</div>
				</#if>
				
				<#if isSupportedBy?? && isSupportedBy != "">
					<div class="scf-right-row-description">
						<h5>Is supported by</h5>
						${isSupportedBy}
					</div>
				</#if>
				
				<#if produces?? && produces != "">
					<div class="scf-right-row-description">
						<h5>Produces</h5>
						${produces}
					</div>
				</#if>
				
				<#if presents?? && presents != "">
					<div class="scf-right-row-description">
						<h5>Presents</h5>
						${presents}
					</div>
				</#if>
				
				<#if supports?? && supports != "">
					<div class="scf-right-row-description">
						<h5>Supports</h5>
						${supports}
					</div>
				</#if>

				<#if isManagedBy?? && isManagedBy != "">
					<div class="scf-right-row-description">
						<h5>Is managed by</h5>
						${isManagedBy}
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
