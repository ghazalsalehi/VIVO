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

<#-- Template for browsing individuals in class groups for menupages -->

<#import "lib-string.ftl" as str>
<noscript>
<p style="padding: 20px 20px 20px 20px;background-color:#f8ffb7">This browse page requires javascript, but your browser is set to disable javascript. Either enable javascript or use the <a href="http://researchdatafinder.qut.edu.au/browse" title="index page">index page</a> to browse for information.</p>
</noscript>
<#assign urlName = "" />

<#if page.title == "People & Groups">
	<#assign urlName = "people" >
<#elseif page.title == "Projects">
	<#assign urlName = "activities" >
<#elseif page.title == "Data Collections">
	<#assign urlName = "dataCollections" >
<#elseif page.title == "Equipment & Services">
	<#assign urlName = "services" >
<#elseif page.title == "Software and Code">
	<#assign urlName = "softwareAndCode" >
<#elseif page.title == "Spatial Data">
	<#assign urlName = "spatialData" >
</#if>

<div id = "breadcrumb">
	<p class="hide">You are here:</p>
	<ul>
		<li class="home">
			<a title="Go to homepage" href=<#if env=="scf">"/scf"<#elseif env=="spatial">"/spatial"<#else>"/"</#if>>
				<span class="hide">Homepage</span>
			</a>
		</li>
		<li>
			<a href="/${urlName}">${page.title}</a>
		</li>
	</ul>
</div>




<section id="noJavascriptContainer" class="hidden">
<section id="browse-by" role="region">
    <nav role="navigation" id="left-col">
		<p class="section-heading">${page.title}</p>
        <ul id="browse-classes">
            <#list vClassGroup as vClass>
                <#------------------------------------------------------------
                Need to replace vClassCamel with full URL that allows function
                to degrade gracefully in absence of JavaScript. Something
                similar to what Brian had setup with widget-browse.ftl
                ------------------------------------------------------------->
                <#assign vClassCamel = str.camelCase(vClass.name) />
                <#-- Only display vClasses with individuals -->
                
				<#if (vClass.entityCount > 0)>
                    <#-- <li id="${vClassCamel}"><a href="#${vClassCamel}" title="Browse all individuals in this class" data-uri="${vClass.URI}">${vClass.name} <span class="count-classes">(${vClass.entityCount})</span></a></li> -->
					<#if vClass.classLevel != -1>
						<li id="${vClassCamel}"><a href="#${vClassCamel}" title="Browse all individuals in this class" data-uri="${vClass.URI}">${vClass.name} <span class="count-classes">(${vClass.entityCount})</span></a>
							<ul style="display:none;">
								<#if vClass.subClassList ??>
									<#list vClass.subClassList as vSubClass>
										<#if (vSubClass.entityCount > 0)>
											<#assign vSubClassCamel = str.camelCase(vSubClass.name) />
											<li><a href="#${vSubClassCamel}" title="Browse all individuals in this class" data-uri="${vSubClass.URI}" class="no-expand">${vSubClass.name} <span class="count-classes">(${vSubClass.entityCount})</span></a></li>
										</#if>
										
									</#list>
								</#if>
							</ul>
						</li>
					</#if>
                </#if>
            </#list>
        </ul>

    </nav>
	
	<div id="right-col">
		<h3 class="selected-class"></h3>
	    <nav id="alpha-browse-container" role="navigation">
            
            <#assign alphabet = ["A", "B", "C", "D", "E", "F", "G" "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"] />
            <ul id="alpha-browse-individuals">
                <li><a href="#" class="selected" data-alpha="all" title="select all">All</a></li>
                <#list alphabet as letter>
                    <li><a href="#" data-alpha="${letter?lower_case}" title="Browse all individuals whose name starts with ${letter}">${letter}</a></li>
                </#list>
            </ul>
        </nav>
		
		<section id="individuals-in-class" role="region">
			<ul role="list">

				<#-- Will be populated dynamically via AJAX request view-browse-default.ftl-->
			</ul>
		</section>
	</div>
    
   
</section>
</section>
<script type="text/javascript">
    $('section#noJavascriptContainer').removeClass('hidden');
</script>