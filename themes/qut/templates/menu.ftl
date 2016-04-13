<#--
Copyright (c) 2011, Cornell University
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

<div id="menu-wrapper">

<div id="menu">

    <ul>
        <#list menu.items as item>
			<#if env?? && env == "scf">
				<#if item.linkText == "Software and Code" || item.linkText == "Home">
					<li class="menu">
				<#else>
					<li class="menu" style="display:none">
				</#if>
					<#if item.linkText == "Home">
						<a class="menu-link <#if item.active>current</#if>" href="/scf" >
					<#else>
						<a class="menu-link <#if item.active>current</#if>" href="${item.url}" >
					</#if>
							<#if item.linkText == "Software and Code">
								<span style="border-right:none;">${item.linkText}</span>
							<#else>
								<span>${item.linkText}</span>
							</#if>
						</a>
					</li>
			<#elseif env?? && env == "spatial">
				<#if item.linkText == "Spatial Data" || item.linkText == "Home">
					<li class="menu">
				<#else>
					<li class="menu" style="display:none">
				</#if>
				
				<#if item.linkText == "Home">
					<a class="menu-link <#if item.active>current</#if>" href="/spatial" >
				<#else>
					<a class="menu-link <#if item.active>current</#if>" href="${item.url}" >
				</#if>
							<#if item.linkText == "Spatial Data">
								<span style="border-right:none;">${item.linkText}</span>
							<#else>
								<span>${item.linkText}</span>
							</#if>
						</a>
					</li>
			<#else>
				<#if item.linkText == "Software and Code" || item.linkText == "Spatial Data">
					<li class="menu" style="display:none">
				<#else>
					<li class="menu">
				</#if>
						<a class="menu-link <#if item.active>current</#if>" href="${item.url}" >
							<#if item.linkText == "Equipment & Services">
								<span style="border-right:none;border-left:1px dotted #fff;">${item.linkText}</span>
							<#else>
								<span>${item.linkText}</span>
							</#if>
						</a>
					</li>
			</#if>
	
		<#--
			<li class="menu">
				<a class="menu-link <#if item.active>current</#if>" href="${item.url}" >
					<span>${item.linkText}</span>
				</a>
			</li>
		-->
		
        </#list>
	</ul>

	
	<ul>
	
		<#if env?? && env == "scf">	
			<li class="menu" style="float:right;">
				<#--<a class="menu-link" href="/softCodeFinderContact">-->
				<a class="menu-link" href="/contact">
				<span style="border-right:none;border-left:1px dotted #fff;">Contact</span>
				</a>
			</li>
			<li class="menu" style="float:right;">
				<a class="menu-link" href="/softCodeFinderAbout">
				<span style="border-left:0px;">About</span>
				</a>
			</li>
		<#elseif env?? && env == "spatial">	
			<li class="menu" style="float:right;">
				<#--<a class="menu-link" href="/spatialDataFinderContact">-->
				<a class="menu-link" href="/contact">
				<span style="border-right:none;border-left:1px dotted #fff;">Contact</span>
				</a>
			</li>
			<li class="menu" style="float:right;">
				<a class="menu-link" href="/spatialDataFinderAbout">
				<span style="border-left:0px;">About</span>
				</a>
			</li>
		<#else>
			<li class="menu" style="float:right;">
				<a class="menu-link" href="/contact">
				<span style="border-right:none;border-left:1px dotted #fff;">Contact</span>
				</a>
			</li>
			<li class="menu" style="float:right;">
				<a class="menu-link" href="/about">
				<span style="border-left:0px;">About</span>
				</a>
			</li>
		</#if>
	</ul>
</div>	
</div>

<div id="background-wrapper">
<div id="wrapper-content" role="main">        
    <#if flash?has_content>
        <#if flash?starts_with("Welcome") >
            <section  id="welcome-msg-container" role="container">
                <section  id="welcome-message" role="alert">${flash}</section>
            </section>
        <#else>
            <section id="flash-message" role="alert">
                ${flash}
            </section>
        </#if>
    </#if>
    
    <!--[if lte IE 8]>
    <noscript>
        <p class="ie-alert">This site uses HTML elements that are not recognized by Internet Explorer 8 and below in the absence of JavaScript. As a result, the site will not be rendered appropriately. To correct this, please either enable JavaScript, upgrade to Internet Explorer 9, or use another browser. Here are the <a href="http://www.enable-javascript.com">instructions for enabling JavaScript in your web browser</a>.</p>
    </noscript>
    <![endif]-->