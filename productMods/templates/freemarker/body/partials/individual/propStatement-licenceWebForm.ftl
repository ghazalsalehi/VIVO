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

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dataCiteDOI. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
 
<#if statement.infoType1??>
	<#assign type = statement.infoType1 />
	<#if type == "Other">
		<#if statement.freeTextValue1??>
			${statement.freeTextValue1}
		<#else>
			UNKNOWN
		</#if>
	<#elseif type == "CC-BY">
		<img id="licence_logo" style="width:130px;height:auto" src="/themes/qut/images/icons/CC-BY.png" alt="CC-BY"><br/>
		Creative Commons Attribution 4.0 (CC-BY)<br/><a target="_blank" href="http://creativecommons.org/licenses/by/4.0/">http://creativecommons.org/licenses/by/4.0/</a>
	<#elseif type == "CC-BY-SA">
		<img id="licence_logo" style="width:130px;height:auto" src="/themes/qut/images/icons/CC-BY-SA.png" alt="CC-BY-SA"><br/>
		Creative Commons Attribution-Share Alike 4.0 (CC-BY-SA)<br/><a target="_blank" href="http://creativecommons.org/licenses/by-sa/4.0/">http://creativecommons.org/licenses/by-sa/4.0/</a>
	<#elseif type == "CC-BY-NC">
		<img id="licence_logo" style="width:130px;height:auto" src="/themes/qut/images/icons/CC-BY-NC.png" alt="CC-BY-NC"><br/>
		Creative Commons Attribution-NonCommercial 4.0 (CC-BY-NC)<br/><a target="_blank" href="http://creativecommons.org/licenses/by-nc/4.0/">http://creativecommons.org/licenses/by-nc/4.0/</a>
	<#elseif type == "CC-BY-NC-SA">
		<img id="licence_logo" style="width:130px;height:auto" src="/themes/qut/images/icons/CC-BY-NC-SA.png" alt="CC-BY-NC-SA"><br/>
		Creative Commons Attribution-NonCommercial-Share Alike 4.0 (CC-BY-NC-SA)<br/><a target="_blank" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">http://creativecommons.org/licenses/by-nc-sa/4.0/</a>
	<#elseif type == "CC-BY-ND">
		<img id="licence_logo" style="width:130px;height:auto" src="/themes/qut/images/icons/CC-BY-ND.png" alt="CC-BY-ND"><br/>
		Creative Commons Attribution-No Derivatives 4.0 (CC-BY-ND)<br/><a target="_blank" href="http://creativecommons.org/licenses/by-nd/4.0/">http://creativecommons.org/licenses/by-nd/4.0/</a>
	<#elseif type == "CC-BY-NC-ND">
		<img id="licence_logo" style="width:130px;height:auto" src="/themes/qut/images/icons/CC-BY-NC-ND.png" alt="CC-BY-NC-ND"><br/>
		Creative Commons Attribution-NonCommercial-No Derivatives 4.0 (CC-BY-NC-ND)<br/><a target="_blank" href="http://creativecommons.org/licenses/by-nc-nd/4.0/">http://creativecommons.org/licenses/by-nc-nd/4.0/</a>
	<#elseif type == "GPL">
		GNU General Public License (GPL)<br/>
		<a target="_blank" href="http://www.gnu.org/licenses/gpl.html">http://www.gnu.org/licenses/gpl.html</a>
	<#elseif type == "AusGOALRestrictive">
		AusGOAL Restrictive Licence<br/>
		<a target="_blank" href="http://www.ausgoal.gov.au/restrictive-licence-template">http://www.ausgoal.gov.au/restrictive-licence-template</a>
	<#elseif type == "NoLicense">
		No License
	<#else>
		${statement.infoType1}
	</#if>
<#else>
	UNKNOWN
</#if>