<section id="individual-intro" class="vcard" role="region">           
		<div id="admin-on-top">
			<div id="label-editor">
			</div>
			<div id="label-display">
					<#if (individualMostSpecificTypeName == "Researcher") || (individualMostSpecificTypeName == "Administrative position")>	
				<#if ( individualImage?contains('<img class="individual-photo"') )>
					<div class="scf-image-container">
						<div id="photo-wrapper">${individualImage}</div>
					</div>
				<#else>
					<div class="scf-image-container">
						<div id="photo-wrapper"><img class="individual-photo" width="160" alt="placeholder image" title="no image" src="/images/placeholders/thumbnail.jpg"></div>
					</div>
				</#if>	
			</#if>
			
			<h2 class="selected-class">
				<#if (individualMostSpecificTypeName == "Researcher") || (individualMostSpecificTypeName == "Administrative position")>	
					<#if personTitle??>
						<span id="personPrefTitle" style="font-weight: bold;"><#if personTitle != "">${personTitle}</#if></span>
					</#if>
				</#if>
				
				<@p.label individual editable labelCount /> 
				<a href="${individualURL}" target="_blank" style="text-decoration: none;">
					<button  class="property-button preview" >Preview</button>
					<#-- <input type="hidden" name="displayMode" value="finish">-->
				</a>
				
				<#-- LIBRDF-48 TO BE REMOVED AFTER DATA MIGRATION - RIF-CS LITE -->
				<#if currentUserEmailAdress ??>
					<#if currentUserEmailAdress == "researchdatafinder@qut.edu.au" || currentUserEmailAdress == "gawri.edussuriya@qut.edu.au" || currentUserEmailAdress == "p.broadley@qut.edu.au" || currentUserEmailAdress == "jodie.vaughan@qut.edu.au" || currentUserEmailAdress == "siobhann.mccafferty@qut.edu.au">
						<a href="${individualURL}?displayMode=edit#hidden_properties" target="_blank" style="text-decoration: none;">
							<button  class="property-button preview">Show hidden fields</button>
						</a>
					</#if>
				</#if>
				<#-- 
				<#if currentUserRole ??>
					<#if currentUserRole == "ROOT" || currentUserRole == "SELF">
						<img id="uriIcon" title="${individual.uri}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon"/>
					</#if>
				</#if>
				-->
			</h2>
			
			</div>

		</div>

		<#include "individual-properties-editMode.ftl"> 
</section> <!-- individual-intro -->

<#if currentUserRole ??>
	<#if currentUserRole == "ROOT" || currentUserRole == "SELF">
		<#if individual.showAdminPanel>
			<div class="multi-container">
				<section id="admin">
					<h2>Admin Panel</h2>
					<ul class="panel">
						<li>
							<a href="${individual.controlPanelUrl()}" title="edit this individual">Edit this individual</a>
						</li>
						<li>
							<section id = "verbose-mode">
								<#if verbosePropertySwitch?has_content>
									<#assign anchorId = "verbosePropertySwitch">
									<#assign currentValue = verbosePropertySwitch.currentValue?string("on", "off")>
									<#assign newValue = verbosePropertySwitch.currentValue?string("off", "on")>
									<p>Verbose display is <b>${currentValue}</b> | 
									<a id="${anchorId}" class="verbose-toggle small" href="${verbosePropertySwitch.url}#${anchorId}" title="verbose control">Turn ${newValue}</a></p>
								</#if> 		
							</section>
						</li>
						<li>
							<a href="${individual.uri}" target="_blank" title="resource uri">${individual.uri}</a>
						</li>
					</ul>
				</section>
			</div>
		</#if>
	</#if>
</#if>

