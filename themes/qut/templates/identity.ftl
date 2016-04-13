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

<div id="header-wrapper">
	<a id="branding" href="http://researchdatafinder.qut.edu.au"></a>
	<div id="header">
	
		<#if env?? && env == "scf">
			<a id="logo_scf" href="/scf" title="Link to QUT SCF home page">QUT SCF home page</a>
		<#elseif env?? && env == "spatial">
			<a id="logo_spatial" href="/spatial" title="Link to QUT Spatial Data home page">QUT SDF home page</a>
		<#else>
			<a id="logo" href="/" title="Link to QUT RDF home page">QUT RDF home page</a>
		</#if>
		
		
		<#--<a id="logo" href="http://researchdatafinder.qut.edu.au" title="Link to QUT RDF home page">QUT RDF home page</a>-->
		
		<ul id="top-links">
			<li><a href="http://www.library.qut.edu.au/">Library</a></li>
			<#-- <li role="listitem"><a href="${urls.index}">Index</a></li> -->
            <#if user.loggedIn>
                <#if user.hasSiteAdminAccess>
                    <li role="listitem"><a href="${urls.siteAdmin}">Site Admin</a></li>
                </#if>
				<#-- <#if user.QUTManageRecordsPageAccess>
                    <li role="listitem"><a href="/manageRecords?module=workspace">Manage records</a></li>
                </#if> -->
				
                    <li>
                        <ul class="dropdown">
                            <li id="user-menu"><a class="no-border" href="#">${user.loginName}</a>
                                <ul class="sub_menu">
                                     <#if user.hasProfile>
                                         <li role="listitem"><a class="no-border" href="${user.profileUrl}">My profile</a></li>
                                     </#if>
									 <#if user.QUTManageRecordsPageAccess>
										<li role="listitem"><a class="no-border" href="/manageRecords?module=workspace">Manage records</a></li>
									</#if>
                                     <#if urls.myAccount??>
                                         <li role="listitem"><a class="no-border" href="${urls.myAccount}">My account</a></li>
                                     </#if>
                                     <li role="listitem"><a class="no-border" href="${urls.logout}">Log out</a></li>
                                </ul>
                            </li>
                         </ul>
                     </li>
                
                ${scripts.add('<script type="text/javascript" src="${urls.base}/js/userMenu/userMenuUtils.js"></script>')}
                
            <#else>
            <!--
                <li role="listitem"><a class="no-border" class="log-out" title="log in to manage this site" href="${urls.login}">QUT Login</a></li>
             -->
                <li role="listitem"><a class="no-border add-window-location" title="log in to manage this site" href="/loginExternalAuthReturn">QUT Login</a></li>
				${scripts.add('<script type="text/javascript" src="${urls.base}/js/login/loginExternalAuth.js"></script>')}
            </#if>
		</ul>    
		
		<form id="global-search-form" action="${urls.search}" name="search" role="search">
			 <!--  CR 038 -->
			<#if env?? && env == "scf">
				<input id="search-query" class="search-input" type="text" size="50" name="querytext" value="Search software and code" onblur="if(this.value == '') { this.value='Search rsoftware and code'}" onfocus="if (this.value == 'Search software and code') {this.value=''}" value="${querytext!}" autocapitalize="off" />
				<input type="hidden" name="env" value="scf">
			<#elseif env?? && env == "spatial">
				<input id="search-query" class="search-input" type="text" size="50" name="querytext" value="Search spatial data" onblur="if(this.value == '') { this.value='Search spatial data'}" onfocus="if (this.value == 'Search spatial data') {this.value=''}" value="${querytext!}" autocapitalize="off" />
				<input type="hidden" name="env" value="spatial">
			<#else>
				<input id="search-query" class="search-input" type="text" size="50" name="querytext" value="Search research data" onblur="if(this.value == '') { this.value='Search research data'}" onfocus="if (this.value == 'Search research data') {this.value=''}" value="${querytext!}" autocapitalize="off" />
				<input type="hidden" name="env" value="rdf">
			</#if>
			<input class="search-submit" value="Search" class="search" type="submit"/>
		</form>     <div id="blue-bar"></div>
	<div id="strapline">
		<img src="/themes/qut/images/strapline.png" alt="A university for the real world" />
	</div>

	<ul id="portal-links">
	<#--
		<#if env?? && env == "scf">
			<li><a id="rdf_button" href="/" style="width:152px">Research Data Finder</a></li>
			<li><a id="rdf_button" href="/spatial" style="width:152px">Spatial Data Finder</a></li>
		<#elseif env?? && env == "spatial">
			<li><a id="rdf_button" href="/scf" style="width:152px">Software Finder</a></li>
			<li><a id="rdf_button" href="/" style="width:152px">Research Data Finder</a></li>
		<#else>
			<li><a id="rdf_button" href="/scf" style="width:114px">Software Finder</a></li>
			<li><a id="rdf_button" href="/spatial" style="width:152px">Spatial Data Finder</a></li>
		</#if>
	-->
	 <li><a id="students" href="http://www.student.qut.edu.au/">Current students</a></li>
	 <li><a id="corpsite" href="http://www.qut.edu.au/">QUT website</a></li>
	 <li><a id="staff" href="http://www.intranet.qut.edu.au/">Current staff</a></li>
	</ul>
  </div><!-- #header -->
</div><!-- #header-wrapper -->

<!--
<header id="branding" role="banner">
    <h1 class="vivo-logo"><a title="QUT | Research Data Finder" href="${urls.home}"><span class="displace">${siteName}</span></a></h1>
    <#-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
    <#if siteTagline?has_content>
        <em>${siteTagline}</em>
    </#if>-->

    <nav role="navigation">
        <ul id="header-nav" role="list">
            <#-- <li role="listitem"><a href="${urls.index}">Index</a></li> -->
            <#if user.loggedIn>
                <#if user.hasSiteAdminAccess>
                    <li role="listitem"><a href="${urls.siteAdmin}">Site Admin</a></li>
                </#if>
                    <li>
                        <ul class="dropdown">
                            <li id="user-menu"><a href="#">${user.loginName}</a>
                                <ul class="sub_menu">
                                     <#if user.hasProfile>
                                         <li role="listitem"><a href="${user.profileUrl}">My profile</a></li>
                                     </#if>
                                     <#if urls.myAccount??>
                                         <li role="listitem"><a href="${urls.myAccount}">My account</a></li>
                                     </#if>
                                     <li role="listitem"><a href="${urls.logout}">Log out</a></li>
                                </ul>
                            </li>
                         </ul>
                     </li>
                
                ${scripts.add('<script type="text/javascript" src="${urls.base}/js/userMenu/userMenuUtils.js"></script>')}
                
            <#else>
                <li role="listitem"><a class="log-out" title="log in to manage this site" href="${urls.login}">QUT Login</a></li>
            </#if>
        </ul>
        
    </nav>
    
    <section id="search" role="region">
        <fieldset>
            <legend>Search form</legend>
			
            <form id="search-form" action="${urls.search}" name="search" role="search">
                <div id="search-field">
                    <input type="text" name="querytext" class="search-vivo" value="${querytext!}" autocapitalize="off" />
                    <input type="submit" value="Search" class="search">
                </div>
            </form>
        </fieldset>
    </section>
</header>
-->