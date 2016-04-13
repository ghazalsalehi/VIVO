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

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign addrLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLabel") />
<#assign addressTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addressType") />
<#assign addrLineOneValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineOne") />
<#assign addrLineTwoValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineTwo") />
<#assign addrLineThreeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineThree") />
<#assign addrLineFourValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineFour") />
<#assign cityValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "city") />
<#assign stateValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "state") />
<#assign postalCodeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "postalCode") />
<#assign countryValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "country") />

<#assign helptext = "">


<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">    
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Mailing Address">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Mailing Address">
        <#assign disabledVal=""/>
</#if>

<#assign TitleText = "${titleVerb}&nbsp;mailing address for ${editConfiguration.subjectName}">
<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--Checking if any required fields are empty-->
         <#if lvf.submissionErrorExists(editSubmission, "country")>
 	        Please select a country.<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "addrLineOne")>
 	        Please enter a value in the Address Line 1 field.<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "city")>
 	        Please enter a value in the City field.<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "postalCode")>
 	        Please enter a value in the Postal Code field.
        </#if>
        
        </p>
    </section>
</#if>
<@lvf.unsupportedBrowser urls.base /> 

<style type="text/css">
.form_row {
	position: relative;
}
</style>
<section id="personHasMailingAddress" role="region">        
    
    <form id="personHasMailingAddress" class="" action="${submitUrl}"  role="add/edit educational training">
		<div class="title_wrapper">
			<p class="text">${TitleText}</p>
		</div>
	<div class="form_wrapper">
		<div id="country_id" class="form_row top_row">
		<div class="form_label">${requiredHint} Country:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">For example, 'Australia.'</p>
				</div>
				<#assign countryOpts = editConfiguration.pageData.country />
				<select id="country" name="country" >
				    <#list countryOpts?keys as key>  
			 	    <#assign countryName = countryOpts[key]?uncap_first?replace("the ", "")?cap_first >
				    <option value="${countryName}" <#if countryName == countryValue>selected</#if>>
						${countryName} 
					</option>            
					</#list>
				</select>
				<input type="hidden" id="countryEditMode" name="countryEditMode" value="${countryValue}" />
			</div>
		</div>

		<div id="addressDetails1" class="form_row">
		<div class="form_label">${requiredHint} Address line 1:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">Do not include person/organisation name here (instead, use 'First Name' and 'Surname' fields).  Include first line of mailing address for primary contact/group. For example, 'c/o Institute of Health and Biomedical innovation (IHBI).'</p>
				</div>
				<input  size="50"  type="text" id="addrLineOne" name="addrLineOne" value="${addrLineOneValue}" />
			</div>
		</div>
		
		<div id="addressDetails2" class="form_row">
		<div class="form_label">Address line 2:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">Include second line of mailing address for contact person/group.  For example, '60 Musk Avenue.'</p>
				</div>
				<input  size="50"  type="text" id="addrLineTwo" name="addrLineTwo" value="${addrLineTwoValue}" />
			</div>
		</div>
		
		<div id="addressDetails3" class="form_row">
		<div class="form_label">Address line 3:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">Include third line of mailing address for contact person/group.  For example, 'Kelvin Grove Urban Village.'</p>
				</div>
				<input  size="50"  type="text" id="addrLineThree" name="addrLineThree" value="${addrLineThreeValue}" />
			</div>
		</div>
		
		<div id="addressDetails4" class="form_row">
		<div class="form_label">Address line 4:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">For example, 'Kelvin Grove Urban Village.'</p>
				</div>
				<input  size="50"  type="text" id="addrLineFour" name="addrLineFour" value="${addrLineFourValue}" />
			</div>
		</div>
		
		<div id="addressDetails5" class="form_row">
		<div class="form_label">${requiredHint} Suburb or city:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">For example, 'Kelvin Grove.'</p>
				</div>
				<input  size="40"  type="text" id="city" name="city" value="${cityValue}" />
			</div>
		</div>
		
		<div id="addressDetails6" class="form_row">
			<div class="form_label">${requiredHint} State or Territory:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">For example, 'Queensland' or 'QLD.'</p>
				</div>
				<input  size="40"  type="text" id="state" name="state" value="${stateValue}" />
			</div>
		</div>

		<div id="addressDetails7" class="form_row">
		<div class="form_label">${requiredHint} Post code:</div> 
			<div class="form_value">
				<div class="help_btn help_hidden" title="Click here for help (It will not navigate you away)"></div>
				<div class="inline_help div_help">
					<p class="inline_help_text help_text">For example, '4059.'</p>
				</div>
				<input  size="8"  type="text" id="postalCode" name="postalCode" value="${postalCodeValue}" />
			</div>
		</div>

		<input type="hidden" id="addrLabel" name="addrLabel" value="${addrLabelValue}" />
		<input type="hidden" id="addressType" name="addressType" value="${addressTypeValue}" />
		<input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

	</div>
		
		<div class="button_wrapper">

			<p class="submit">
				<input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span>
				<a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
			</p>

			<p id="requiredLegend" class="requiredHint">* required fields</p>
		</div>

</form>

</section>


<script type="text/javascript">
/*$(document).ready(function(){
    mailingAddressUtils.onLoad("${editMode}","${countryValue}");
});*/
</script>

 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/doi/css/styles_help.css" />', '<link rel="stylesheet" href="${urls.base}/edit/forms/doi/css/styles.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/mailingAddressUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>')}
			 
${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/doi/js/data_help.js"></script>')}


