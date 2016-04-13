
<#-- Template for adding/editing time values -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign txtCitationFormValues = "" />
<#assign txtCitationDisplayStr = "" />
<#assign txtCitationFormValues = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCitationFormValues") />
<#assign txtCitationDisplayStr = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCitationDisplayStr") />
<#assign CitationIdenType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtIdenType") />
<#assign CitationIdenValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtCitationIdenValue") />
<#assign CitationContributorType = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtContributorType") />
<#assign CitationDOIStatus = lvf.getFormFieldValue(editSubmission, editConfiguration, "txtDOIStatus") />

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#assign citationIdentifierTypes = editConfiguration.pageData.citationIdentifierTypes />
<#assign namePartTypes = editConfiguration.pageData.namePartTypes />
<#assign citationDatesTypes = editConfiguration.pageData.citationDatesTypes />
<#assign creatorTypes = editConfiguration.pageData.creatorTypes />
<#assign doiStatusTypes = editConfiguration.pageData.doiStatusTypes />
<#assign currentUserRole = editConfiguration.pageData.currentUserRole />
<#assign individualSubjectURL = editConfiguration.pageData.individualSubjectURL />
<#assign individualTitle = editConfiguration.pageData.IndividualTitle />
<#assign publisher = editConfiguration.pageData.publisher />
<#assign year = editConfiguration.pageData.year />
<#assign propertyName = editConfiguration.pageData.propertyName />

<#assign helptext = "Use the web form to create a citation for the data being described.  Complete all mandatory fields (marked by an *) then click on 'Create citation' to generate a citation.">

<script type="text/javascript">	
	var user_role = "${currentUserRole}";
	var user_mode = "${editMode}";
	<#if editMode == "edit"> 
		citationFormValues = ${txtCitationFormValues};
		citationDisplayStr = "${txtCitationDisplayStr}";
	</#if>
</script>

<form id="mainform-${propertyName}" action=${submitUrl} method="get" style="display:none">
	<p>Citation identifier value:<input type="text" id ="citationIdenValue" name="txtCitationIdenValue" value="${CitationIdenValue}"/></p>
	<p>Citation:<input type="text" id ="citationFormValues" name="txtCitationFormValues" value="${txtCitationFormValues}"/></p>
	<p>Citation Display:<input type="text" id ="citationDisplayStr" name="txtCitationDisplayStr" value="${txtCitationDisplayStr}"/></p>
	<input type="hidden" name="editKey" value="${editKey}" />
</form>

<form id="subform-${propertyName}" action="${submitUrl} method="get">
	<div class="form_wrapper">
		<div class="left_column">
				<div id="identifier" class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Identifier :</div> 
					<div class="form_value">
						<div class="field_set_iden">
							<div class="form_column form_column_mid">
									<div>Type:</div>
									<select name="txtIdenType" id="txtIdenType">
										<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
										<#list citationIdentifierTypes as optionType>
											<option value="${optionType}" <#if optionType == CitationIdenType>selected="selected"</#if> >${optionType}</option>
										</#list>
									</select>
							</div>
							<div class="form_column form_column_mid">
									<div>Value:</div>
									<input type="text" name="txtIdenValue" id="txtIdenValue" value=""/>
							</div>
							<div class="form_column_mid">
								<button class="property-button mint_doi" style="display:none;">Create/Update DOI</button>
							</div>
						</div>
					</div>
				</div>				
				<div id="contributors" class="form_row multiple">
				<div class="form_label"><span class='requiredHint'>* </span>Author(s) :</div> 
					<div class="form_value">
						<div class="form_column form_column_mid">
								<div>Type:</div>
								<select name="txtContributorType" id="txtContributorType" >
									<option value="" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
									<#list creatorTypes as optionType>
										<option value="${optionType}" <#if optionType == CitationContributorType>selected="selected"</#if> >${optionType}</option>
									</#list>
								</select>
						</div>
						<div class="form_tab_1" id="form_tab_1" style="display:none">
							<div class="form_sub_tab">
								<div class="field_set">
									<div class="form_contributor_del_btn" title="Remove this Contributor"></div>
									<div class="top_row two_column">
										<div class="form_column">
											<div><span class='requiredHint'>* </span>Family Name:</div>
											<input type="text" name="txtFamillyName" id="txtFamillyName" value=""/>
										</div>
										<div class="form_column">
											<div><span class='requiredHint'>* </span>Given Name:</div>
											<input type="text" name="txtGivenName" id="txtGivenName" value=""/>
										</div>
									</div>
								</div>
								<div class="additional_row">
									<button class="add_new_child">Add new Author(s)/Creator(s)</button>
								</div>
							</div>
						</div>
						<div class="form_tab_2" id="form_tab_2" style="display:none">
							<div class="form_sub_tab">
								<div class="field_set">
									<div class="form_contributor_del_btn" title="Remove this Contributor"></div>
									<div class="form_column form_column_mid">
											<div><span class='requiredHint'>* </span>Group:</div>
											<input type="text" name="txtGroupName" id="txtGroupName" value=""/>
									</div>
								</div>
								<div class="additional_row">
									<button class="add_new_child">Add new Author(s)/Creator(s)</button>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div id="dataURL" class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Data URL:</div> 
					<div class="form_value"><input type="text" name="txtDataUrl" id="txtDataUrl" value="${individualSubjectURL}"/></div>
				</div>
				<div id="title" class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Title:</div> 
					<div class="form_value"><input type="text" name="txtTitle" id="txtTitle" value="${individualTitle}"/></div>
				</div>
				<div id="publisher" class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Publisher:</div> 
					<div class="form_value"><input type="text" name="txtPublisher" id="txtPublisher" value="${publisher}"/></div>
				</div>
				<div id="publicationYear" class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Year of publication:</div> 
					<div class="form_value"><input type="text" name="txtPublicationYear" id="txtPublicationYear" value="${year}"/></div>
				</div>
				<div id="version" class="form_row">
					<div class="form_label"><span class='requiredHint'>* </span>Version:</div> 
					<div class="form_value"><input type="text" name="txtVersion" id="txtVersion" value="1"/></div>
				</div>
				<div id="doi_status" class="form_row" style="display:none">
					<div class="form_label">Status:</div> 
					<div class="form_value">
						<select name="txtDOIStatus" id="txtDOIStatus" >
							<option value="abc" <#if editMode != "edit">selected="selected"</#if>>Select Type</option>
							<#list doiStatusTypes as optionType>
								<option value="${optionType}" <#if optionType == CitationDOIStatus>selected="selected"</#if> >${optionType}</option>
							</#list>
						</select>
					</div>
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
	
	<div id="ajax_wait" style=" width: 400px; display: none">
		<div class="ajaxBg"></div>
		<div class="ajaxText" style="text-align:center; color: #FFFFFF; float: left; width: 400px;">Data is being submitted. Please wait...</div>
	</div>
     
   