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

<#assign actionText = "Add new" />
<#if editConfiguration.dataPropertyStatement?has_content>
    <#assign actionText = "Edit"/>
</#if>
<#assign submitLabel>${actionText} label</#assign>

<#assign literalValues = "${editConfiguration.dataLiteralValuesAsString}" />
<#assign recordType = editConfiguration.pageData.recordType />
<#assign recordSubType = editConfiguration.pageData.recordSubType />
<#assign helptext = "">

<#if recordType == "people">
	<#assign helptext = "Use first name and then surname (information should reflect that on the individuals' Academic Profile webpage).  For groups, provide full names with no abbreviations.">
<#elseif recordType == "activities">
	<#assign helptext = "Provide the official name for the project, award, course, event or program.">
<#elseif recordType == "dataCollections">
	<#assign helptext = "Provide a descriptive name which clearly identifies the data e.g. Pharmacokinetic data for twelve therapeutic equine medications.">
<#elseif recordType == "service">
	<#assign helptext = "Provide a descriptive name which clearly identifies the service e.g. K-tree: a tree structured clustering algorithm.">
</#if> 

<#--<h2>${actionText} <em>label</em> for ${editConfiguration.subjectName}</h2>
-->

<form class="editForm" action = "${submitUrl}" method="post">
    <div class="form_wrapper label_form">
		<div class="left_column">
			<div id="idenValue" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Name:</div> 
				<div class="form_value">
					<textarea rows="1" cols="120" type="text" name="${editConfiguration.varNameForObject}" id="label" role="input">${literalValues}</textarea>
				</div> 
			</div>
			<div class="button_wrapper">
				<div class="property-button cancel">Cancel</div>
				<div class="property-button submit">
					<input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input"/>
					<input type="hidden" name="vitroNsProp" value="true" role="input"/>
					Submit
				</div>
			</div>
		</div>
		<div class="right_column">
			<div class="help_heading">
				<h4>QUT Guidance</h4>
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="div_help"">
					<p class="help_text">${helptext}</p>
				</div>
			</div>			
		</div>
	</div>		
</form>

<#--The original jsp included a delete form for deleting rdfs label.  
If required, deletion can be supported but it does not appear that is required currently. 
-->