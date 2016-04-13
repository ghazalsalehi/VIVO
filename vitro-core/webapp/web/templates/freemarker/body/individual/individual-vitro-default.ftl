
<section id="individual-intro" class="vcard" role="region">
	<nav role="navigation" id="left-col">
		<#--<#assign TitleHeading = individual.mostSpecificTypes?replace("[", "") />
		<p class="section-heading">${TitleHeading?replace("]", "")}</p> -->
		
		<p class="section-heading">${individualMostSpecificTypeName}</p>
		
		<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

		<#if (propertyGroups.all)??>
		<#assign groups = propertyGroups.all>
    
		<#if groups?has_content>
			<#if (groups?size > 1) || (groups?first).getName(nameForOtherGroup)?has_content> 
					
						<ul id="browse-classes">
							<#list groups as group>
								<#assign groupname = group.getName(nameForOtherGroup)>                        
								<#if groupname?has_content>
									<#--create property group html id is the function that will replace all spaces with underscore to make a valid id-->
									<#assign groupnameHtmlId = p.createPropertyGroupHtmlId(groupname) >
									<#-- capitalize will capitalize each word in the name; cap_first only the first. We may need a custom
									function to capitalize all except function words. -->
									<li role="listitem"><a href="#${groupnameHtmlId}" title="group name">${groupname?capitalize}</a></li>
								</#if>
							</#list>
						</ul>
					
				</#if> 
			</#if>
		</#if>
		
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
				
	</nav>
	
	<div id="content">                
		<div id="admin-on-top">
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
				<span id="supPrefTitle" style="font-weight: bold;"></span>
				<@p.label individual editable labelCount /> 
				<#if currentUserRole ??>
					<#if currentUserRole == "ROOT" || currentUserRole == "SELF">
						<img id="uriIcon" title="${individual.uri}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon"/>
					</#if>
				</#if>
			</h2>
			
			<#--
			<#if currentUserRole?? && currentUserRole != "">
				<#if currentUserRole == "CURATOR" || currentUserRole == "DB_ADMIN" || currentUserRole == "ROOT" || currentUserRole == "SELF">
					<div class="scf-right-row-description">
						<form action=${individualURL} method="post">
							<div id="small-admin-button"><button type="submit" id="btn-edit_delete_action">Preview</button></div>
							<input type="hidden" name="displayMode" value="finish"></input>
						</form>
						 <div id="small-line-button"><button type="submit" id="btn-edit_delete_action">Delete this record</button></div> 
					</div>
				</#if>
			</#if>
			-->
			
			
			<div class="scf-right-row-description">
				<form action=${individualURL} method="post">
					<div id="small-admin-button"><button type="submit" id="btn-edit_delete_action">Preview</button></div>
					<input type="hidden" name="displayMode" value="finish"></input>
				</form>
			</div>

		</div>

		<div id="middle-col" class="id-9485">
			<#-- Ontology properties -->
			<#include "individual-properties.ftl">

			<#-- gawri CR:020 -->
			<#assign title = propertyGroups.getProperty("${qutCore}supressTitle")!>
			<#if title?has_content>
				<#assign supTitle = "">
				<#list title.statements as statement>
					<#if statement.freeTextValue1??>
						<#assign supTitle = "${statement.freeTextValue1}" />
					</#if>
				</#list>

				<#if supTitle != "">
					<script type="text/javascript">
						var title = '${supTitle}';
						var myVal = document.getElementById("supPrefTitle");
						myVal.innerHTML =  title;
					</script>
				</#if>
			</#if>
		</div>
	</div>
</section> <!-- individual-intro -->


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />', '<link rel="stylesheet" href="${urls.base}/css/individual/individual-display-style.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>')}
