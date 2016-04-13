<#--
Copyright (c) 2013, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->
<#assign positionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtPosition") />
<#assign divfacValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDivfac") />
<#assign schoolValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtSchool") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign DisplayString = "Position(s)" />
<#assign textAreaSize = "input" />
<#assign helptext = "This field may be pre-filled from the Academic Profiles system if a researcher is listed there, and is updated whenever the researcher's profile is modified.  You can add information to this field if none exists.">
<#assign propertyName = editConfiguration.pageData.propertyName />

<form id="mainform-${propertyName}" action=${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Position:</div> 
				<div class="form_value"><input type="text" name="txtPosition" id="position" value="${positionValue}"/></div> 
			</div>
					
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Division/Faculty:</div> 
				<div class="form_value"><input type="text" name="txtDivfac" id="divfac" value="${divfacValue}"/></div> 
			</div>	
			<div class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>School:</div> 
				<div class="form_value"><input type="text" name="txtSchool" id="school" value="${schoolValue}"/></div> 
			</div>
			<div class="button_wrapper">
				<div class="property-button cancel">Cancel</div>
				<div class="property-button submit">
					<input type="hidden" name="editKey" value="${editKey}" />
					Submit
				</div>
			</div>
		</div>
		<div class="right_column">
			<div class="help_heading">
				<h4>QUT Guidance</h4>
				<div class="help_btn help_shown" title="Click here for help (It will not navigate you away)"></div>
				<div class="div_help">
					<p class="help_text">${helptext}</p>
				</div>
			</div>
		</div>
	</div>
</form>
          
