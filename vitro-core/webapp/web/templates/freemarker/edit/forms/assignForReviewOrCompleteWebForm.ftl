<#--
Copyright (c) 2012, QUT University
-->

<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve variables needed-->



<#assign txtAssignForReviewUserID = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtAssignForReviewUserID") />
<#assign txtAssignByUserID = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtAssignByUserID") />
<#assign txtAssignByUsercomments = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtAssignByUsercomments") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign selectionTypes = editConfiguration.pageData.selectionTypes />
<#assign DisplayString = editConfiguration.pageData.displayString />
<#assign assignedByusersList = editConfiguration.pageData.assignedByusersList />



<#assign helptext = "">

<form id="mainform" action=${submitUrl} method="get">	
	<div class="form_wrapper">
		<div class="left_column">
			<div id="assignForReviewUserID" class="form_row top_row">
				<div class="form_label"><span class='requiredHint'>* </span>Assign to review(user ID):</div> 
				<div class="form_value">
					<select name="txtAssignForReviewUserID" id="txtAssignForReviewUserID" >
						<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						<#list selectionTypes?keys as key >  
							<option value="${key}" <#if key == txtAssignForReviewUserID>selected="selected"</#if> >${selectionTypes[key]}</option>
						</#list>
					</select>
				</div> 
			</div>
			<div id="assignByUserID" class="form_row">
				<div class="form_label"><span class='requiredHint'>* </span>Assign by user ID:</div> 
				<div class="form_value">
					<select name="txtAssignByUserID" id="txtAssignByUserID" >
						<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
						<#list assignedByusersList?keys as key >  
							<option value="${key}" <#if key == txtAssignByUserID>selected="selected"</#if> >${assignedByusersList[key]}</option>
						</#list>
					</select>
				</div> 
			</div>
				
			<div id="assignByUsercomments" class="form_row">
				<div class="form_label">Assign by user comments:</div> 
				<div class="form_value"><textarea type="text" name="txtAssignByUsercomments" id="txtAssignByUsercomments">${txtAssignByUsercomments}</textarea></div> 
			</div>
			<div class="button_wrapper">
				<div class="property-button cancel">Cancel</div>
				<div class="property-button submit">
					<input type="hidden" name="userMode" value="${editMode}"/>
					Submit
				</div>
			</div>
		</div>
		<div class="right_column">
			<div class="help_heading">
				<h4>QUT Guidance</h4>
				<div class="help_btn help_shown" title="Click here for help (It will not navigate you away)"></div>
				<div class="div_help"">
					<p class="help_text">${helptext}</p>
				</div>
			</div>			
		</div>
	</div>
</form>
              
              
              
              
              
              
              