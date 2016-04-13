<#--
Copyright (c) 2015, QUT University
All rights reserved.
-->

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>
<#assign subjectUri = individual.controlPanelUrl()?split("=") >

<#if recordStatus ??>
<#else>
	<#assign recordStatus = "Draft" >
</#if>

<div id="navigation-tabs">
	<#if MainType == "People & Groups" || MainType == "Projects">
		<ul>
			<li><div class="arrow-left"></div><a href="#" name="#tab1" class="three-tabs">1. Mandatory fields</a><div class="arrow-right"></div></li>
			<li><div class="arrow-left"></div><a href="#" name="#tab2" class="three-tabs">2. Other fields</a><div class="arrow-right"></div></li>
			<li><div class="arrow-left"></div><a href="#" name="#tab4" class="three-tabs">3. Submit</a><div class="arrow-right"></div></li>
		</ul>
	<#else>
		<ul>
			<li><div class="arrow-left"></div><a href="#" name="#tab1" class="four-tabs">1. Mandatory fields</a><div class="arrow-right"></div></li>
			<li><div class="arrow-left"></div><a href="#" name="#tab2" class="four-tabs">2. Other fields</a><div class="arrow-right"></div></li>
			<li><div class="arrow-left"></div><a href="#" name="#tab3" class="four-tabs">3. Data source</a><div class="arrow-right"></div></li>
			<li><div class="arrow-left"></div><a href="#" name="#tab4" class="four-tabs">4. Submit</a><div class="arrow-right"></div></li>
		</ul>
	</#if>
	
	<div class="navigation-pagination">
		<#if currentUserRole ??>
			<#if currentUserRole == "EDITOR">
				<#if recordStatus == "UnderReview">
					<div class="pagination-button send-for-review manageRecordAction disabled hide">
				<#else>
					<div class="pagination-button send-for-review manageRecordAction hide">
				</#if>
						<span class="currentModule hide">${recordStatus}</span>
						Send for review
					</div>
			</#if>
		</#if>
		<#if hasPermisionToPublishRecord>
			<#if recordStatus == "PublishedOpenAccess">
				<div class="pagination-button publish-for-open manageRecordAction disabled hide">
			<#else>
				<div class="pagination-button publish-for-open manageRecordAction hide">
			</#if>
					<span class="currentModule hide">${recordStatus}</span>
					Publish (Open)
				</div>
			
			<#if recordStatus == "PublishedQUTAccess">
				<div class="pagination-button publish-for-qut manageRecordAction disabled hide">
			<#else>
				<div class="pagination-button publish-for-qut manageRecordAction hide">
			</#if>
				<span class="currentModule hide">${recordStatus}</span>
				Publish (QUT only)
			</div>
		</#if>	
		
		<div class="pagination-button next">Next ></div>
		<div class="pagination-button previous">< Previous</div>
	</div>
</div>
		
<div id="navigation-content">
	<div id="tab1" style="display: block;" class="mandatory">
		<#list propertyGroups.all as group>		
			<#list group.properties as property>
				<#if property.localName == "mainImage">
				<#else>
					<#if property.propertyType??>
						<#if property.propertyType == "mandatory">
							<article class="property" role="article">
								<#if property.allowMultipleIndividualsForProperty == true>
									<h3 id="${property.localName}" class="toggle-title allow-multiple"><span>${property.name}</span> <@p.addLink property editable /></h3>
								<#else> 
									<h3 id="${property.localName}" class="toggle-title allow-single"><span>${property.name}</span> <@p.addLink property editable /></h3>
								</#if>
					
								<ul class="property-list" role="list" id="${property.localName}List">
									<li id="${property.localName}-editor" style="overflow:hidden;" class="hide"></li>
									<#-- List the statements for each property -->
									<#if property.type == "data">	
										<@p.dataPropertyList property editable />	<#-- data property -->
									<#else>
										<@p.objectProperty property editable />		<#-- object property -->
									</#if>
								</ul>
							</article>
					</#if>
				</#if>
				</#if>
			</#list>
		</#list>
	</div>
	<div id="tab2" style="display: none;" class="optional">
		<#list propertyGroups.all as group>		
			<#list group.properties as property>
				<#if property.localName == "mainImage">
				<#else>
					<#if property.propertyType??>
						<#if property.propertyType == "optional">
							<article class="property" role="article">
								<div class="border">
									<#if property.allowMultipleIndividualsForProperty == true>
										<h3 id="${property.localName}" class="toggle-title allow-multiple"><span>${property.name}</span> <@p.addLink property editable /></h3>
									<#else> 
										<h3 id="${property.localName}" class="toggle-title allow-single"><span>${property.name}</span> <@p.addLink property editable /></h3>
									</#if>
								</div>
					
								<ul class="property-list" role="list" id="${property.localName}List">
									<li id="${property.localName}-editor" style="overflow:hidden;" class="hide"></li>
									<#-- List the statements for each property -->
									<#if property.type == "data">	
										<@p.dataPropertyList property editable />	<#-- data property -->
									<#else>
										<@p.objectProperty property editable />		<#-- object property -->
									</#if>
								</ul>
							</article>
					</#if>
				</#if>
				</#if>
			</#list>
		</#list>		
	</div>
	<div id="tab3" style="display: none;" class="storage">
		<#list propertyGroups.all as group>		
			<#list group.properties as property>
				<#if property.localName == "mainImage">
				<#else>
					<#if property.propertyType??>
						<#if property.propertyType == "storage">
							<article class="property" role="article">
								<div class="border">
									<#if property.allowMultipleIndividualsForProperty == true>
										<h3 id="${property.localName}" class="toggle-title allow-multiple"><span>${property.name}</span> <@p.addLink property editable /></h3>
									<#else> 
										<h3 id="${property.localName}" class="toggle-title allow-single"><span>${property.name}</span> <@p.addLink property editable /></h3>
									</#if>
								</div>
					
								<ul class="property-list" role="list" id="${property.localName}List">
									<li id="${property.localName}-editor" style="overflow:hidden;" class="hide"></li>
									<#-- List the statements for each property -->
									<#if property.type == "data">	
										<@p.dataPropertyList property editable />	<#-- data property -->
									<#else>
										<@p.objectProperty property editable />		<#-- object property -->
									</#if>
								</ul>
							</article>
						</#if>
					</#if>
				</#if>
			</#list>
		</#list>	
	</div>
	<div id="tab4" style="display: none;" class="other">
		<#list propertyGroups.all as group>		
			<#list group.properties as property>
				<#if property.localName == "mainImage">
				<#else>
					<#if property.propertyType??>
						<#if property.propertyType == "other">
							<article class="property" role="article">
								<div class="border">
									<#if property.allowMultipleIndividualsForProperty == true>
										<h3 id="${property.localName}" class="toggle-title allow-multiple"><span>${property.name}</span> <@p.addLink property editable /></h3>
									<#else> 
										<h3 id="${property.localName}" class="toggle-title allow-single"><span>${property.name}</span> <@p.addLink property editable /></h3>
									</#if>
								</div>
					
								<ul class="property-list" role="list" id="${property.localName}List">
									<li id="${property.localName}-editor" style="overflow:hidden;" class="hide"></li>
									<#-- List the statements for each property -->
									<#if property.type == "data">	
										<@p.dataPropertyList property editable />	<#-- data property -->
									<#else>
										<@p.objectProperty property editable />		<#-- object property -->
									</#if>
								</ul>
							</article>
					</#if>
				</#if>
				</#if>
			</#list>
		</#list>	 
	</div>
</div>
<div class="navigation-pagination">
		<#if currentUserRole ??>
			<#if currentUserRole == "EDITOR">
				<#if recordStatus == "UnderReview">
					<div class="pagination-button send-for-review manageRecordAction disabled hide">
				<#else>
					<div class="pagination-button send-for-review manageRecordAction hide">
				</#if>
						<span class="currentModule hide">${recordStatus}</span>
						Send for review
					</div>
			</#if>
		</#if>
		<#if hasPermisionToPublishRecord>
			<#if recordStatus == "PublishedOpenAccess">
				<div class="pagination-button publish-for-open manageRecordAction disabled hide">
			<#else>
				<div class="pagination-button publish-for-open manageRecordAction hide">
			</#if>
					<span class="currentModule hide">${recordStatus}</span>
					Publish (Open)
				</div>
			
			<#if recordStatus == "PublishedQUTAccess">
				<div class="pagination-button publish-for-qut manageRecordAction disabled hide">
			<#else>
				<div class="pagination-button publish-for-qut manageRecordAction hide">
			</#if>
				<span class="currentModule hide">${recordStatus}</span>
				Publish (QUT only)
			</div>
		</#if>	
	<div class="pagination-button next">Next ></div>
	<div class="pagination-button previous">< Previous</div>
</div>

<#-- LIBRDF-48 TO BE REMOVED AFTER DATA MIGRATION - RIF-CS LITE -->
<script type="text/javascript">	
   var userEmailAdress = '';
    <#if currentUserEmailAdress ??>
        userEmailAdress  = '${currentUserEmailAdress}';
    </#if>
</script>
<#if currentUserEmailAdress ??>
	<#if currentUserEmailAdress == "researchdatafinder@qut.edu.au" || currentUserEmailAdress == "gawri.edussuriya@qut.edu.au" || currentUserEmailAdress == "p.broadley@qut.edu.au" || currentUserEmailAdress == "jodie.vaughan@qut.edu.au" || currentUserEmailAdress == "siobhann.mccafferty@qut.edu.au">
		<div id="hidden_properties">
			<div id="label-display"><h2 style="color: red;"> Please delete all entries from here, after migrating to the new schema.</h2> </div>
			<#list propertyGroups.all as group>		
				<#list group.properties as property>
					<#if property.localName == "mainImage">
					<#else>
							<#if property.propertyType??>
								<#if property.propertyType == "hidden">
									<article class="property" role="article">
										<div class="border">
											<#if property.allowMultipleIndividualsForProperty == true>
												<h3 id="${property.localName}" class="toggle-title allow-multiple"><span>${property.name}</span> <@p.addLink property editable /></h3>
											<#else> 
												<h3 id="${property.localName}" class="toggle-title allow-single"><span>${property.name}</span> <@p.addLink property editable /></h3>
											</#if>
										</div>
							
										<ul class="property-list" role="list" id="${property.localName}List">
											<li id="${property.localName}-editor" style="overflow:hidden;" class="hide"></li>
											<#-- List the statements for each property -->
											<#if property.type == "data">	
												<@p.dataPropertyList property editable />	<#-- data property -->
											<#else>
												<@p.objectProperty property editable />		<#-- object property -->
											</#if>
										</ul>
									</article>
								</#if>
							</#if>
					</#if>
				</#list>
			</#list>	 
		</div>
	</#if>
</#if>