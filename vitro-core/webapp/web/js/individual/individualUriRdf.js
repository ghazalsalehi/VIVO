				  
/*
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
*/

$(document).ready(function(){
    // This function creates and styles the "qTip" tooltip that displays the resource uri and the rdf link when the user clicks the uri/rdf icon.
    $('#uriIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                prerender: true, // We need this for the .click() event listener on 'a.close'
                text: '<h5>share the URI for this profile</h5> <input id="uriLink" type="text" value="' + $('#uriIcon').attr('title') + '" /><h5><a class ="rdf-url" href="' + individualRdfUrl + '">view profile in RDF format</a></h5><a class="close" href="#">close</a>'
            },
            position: {
                corner: {
                    target: 'bottomLeft',
                    tooltip: 'topLeft'
                }
            },
            show: {
                when: {event: 'click'}
            },
            hide: {
                fixed: true, // Make it fixed so it can be hovered over and interacted with
                when: {
                    target: $('a.close'),
                    event: 'click'
                }
            },
            style: {
                padding: '1em',
                width: 400,
                backgroundColor: '#f1f2ee'
            }
        });
    });

    // Prevent close link for URI qTip from requesting bogus '#' href
    $('a.close').click(function() {
        $('#uriIcon').qtip("hide");
        return false;
    });
		
	$(".property-group").hide();
	
	if (window.location.hash == "" || window.location.hash == "#label" || window.location.hash == "#verbosePropertySwitch") {
		$("#overview").show();
		$('#browse-classes a[href$="#overview"]').addClass("selected");
	} else {
		var tId = $(window.location.hash).parents(".property-group").attr("id");
		$("#"+tId).show();
		$('#browse-classes a[href$="#'+tId+'"]').addClass("selected");
	}
	// Tabing
	$("#browse-classes li a").click(function(e) {
		var a = $(this);
		$(".property-group:visible").slideUp(300, function() { // slideUp ==> fadeOut
			// fadeIn
		});
		$(a.attr("href")).slideDown(300);
		$("#browse-classes a").removeClass("selected")
		e.preventDefault();
		a.addClass("selected");
	});
	
	// LIBRDF-118 
	// When user click back button the page won't reload. Manually reload the page if it is older by checking 
	// the individual's  "Date record modified" field.
	// --------------------------------------------------------------------------------------------------
	var indLocalName = $("#wrapper-content").find(".individualLocalName");
	if (indLocalName.length > 0){
		$.post( "/manageRecords?module=statistics&action=getRecordModifiedDate", { key:indLocalName.text() }, function( data ) {
			var errorCode = data.MRS_ERROR_CODE;
			if (errorCode == "MRS_001"){	//success.
				if (data.recorModifiedDate){
					var lastRecordModifiedDate = data.recorModifiedDate;
					
					if (lastRecordModifiedDate != ''){	// Cannot happen
						var oldRecordModified = $("#wrapper-content").find(".recordModifiedDate")
						if (oldRecordModified.length > 0){
							var oldRecordModifiedDate = oldRecordModified.text();
							
							if (oldRecordModifiedDate != ''){
								var oldDate = dateFromString(oldRecordModifiedDate);
								var lastDate = dateFromString(lastRecordModifiedDate);
								
								if (oldDate && lastDate){
									if (oldDate.getTime() < lastDate.getTime()){
										location.reload(true);
									}
								}
							}
						}
					}
				}
			}
		}, "json");
	}
	
	function dateFromString(dateString){
        var values = dateString.split(/[T\-:]/);		// ex: 2009-04-19T16:11:05
        
        if (values.length == 6){
        	var year       = values[0];
            var month      = values[1];
            var day = values[2];        
            var hours = values[3];
            var minutes = values[4];
            var seconds = values[5];
            
            return new Date(year, month, day, hours, minutes, seconds, "0"); 
        }else{
        	return null;
        }
	}
	
	// LIBRDF-69
	// -----------------------------------------------------------------------------------------------
	
	// ---------------------------------------------------------------------------------------------------
	// Remove this code lines after admin people manually enter values into the new schema.
	var hash = window.location.hash;
	if (hash == "#hidden_properties"){
		
		$("#individual-intro").find("#admin-on-top").hide();
		$("#individual-intro").find("#navigation-content").hide();
		$("#individual-intro").find("#navigation-tabs").hide();
		$("#individual-intro").find(".navigation-pagination").hide();
		
		$("#individual-intro").find("#hidden_properties").show();
	}else{
		$("#individual-intro").find("#admin-on-top").show();
		$("#individual-intro").find("#navigation-content").show();
		$("#individual-intro").find("#navigation-tabs").show();
		$("#individual-intro").find(".navigation-pagination").show();
		$("#individual-intro").find("#hidden_properties").hide();
	}
	
	// ---------------------------------------------------------------------------------------------------
	
	//nextRecordsState:Draft
	//nextRecordsState:UnderReview
	//nextRecordsState:PublishedOpenAccess
	//nextRecordsState:PublishedQUTAccess
	// LIBRDF-83
	$("#individual-intro").find(".navigation-pagination").find(".send-for-review").click(function(e){
		
		var btn = $(this);
		if (!btn.hasClass('disabled')) {
			var nextRecordsState = "UnderReview";
			var individualLocalName = $("#wrapper-content").find(".individualLocalName").text();
			var currentModule = btn.find('.currentModule').html();
			
			
			Messi.ask('Are you SURE you want to send this record for review? If in doubt, CANCEL.', function(val) { 
				if (val==='Y') {
					$("#individual-intro").find(".navigation-pagination").find(".send-for-review").addClass('disabled');
					
					$.post( "/manageRecords?module=workspace&action=moveToNextState&nextRecordsState=" + nextRecordsState + "&currentRecordsState=" + currentModule + "&reponseInfoLevel=0", { key:individualLocalName }, function( data ) {
						var errorCode = data.MRS_ERROR_CODE;
						if (errorCode == "MRS_001"){	//success.
							
							$("#individual-intro").find("#publishRecord-editor").siblings('li').find('span').text("Under review");
							
							if (data.emailErrorMessage){
								$.growl.error({ title: "", message: data.emailErrorMessage });
							}
							$.growl.notice({ title: "", message: "Successfully sent this record for review." });

						}else if (errorCode == "MRS_002"){	//failed
							btn.removeClass('disabled');
							$.growl.error({ title: "", message: "Failed to send the record to next level." +  data.MRS_ERROR_MSG});
						}
					}, "json");	
				}
			});
		}
	});
	
	$("#individual-intro").find(".navigation-pagination").find(".publish-for-open").click(function(e){
		
		var btn = $(this);
		if (!btn.hasClass('disabled')) {
			var nextRecordsState = "PublishedOpenAccess";
			var individualLocalName = $("#wrapper-content").find(".individualLocalName").text();
			var currentModule = btn.find('.currentModule').html();
			
			Messi.ask('Are you SURE you want to publish this record for Open Access? If in doubt, CANCEL.', function(val) { 
				if (val==='Y') {
					$("#individual-intro").find(".navigation-pagination").find(".publish-for-open").addClass('disabled');
					$.post( "/manageRecords?module=workspace&action=moveToNextState&nextRecordsState=" + nextRecordsState + "&currentRecordsState=" + currentModule + "&reponseInfoLevel=0", { key:individualLocalName }, function( data ) {
						var errorCode = data.MRS_ERROR_CODE;
						if (errorCode == "MRS_001"){	//success.
							
							$("#individual-intro").find("#publishRecord-editor").siblings('li').find('span').text("Published - Open Access");
							
							if (data.emailErrorMessage){
								$.growl.error({ title: "", message: data.emailErrorMessage });
							}
							$.growl.notice({ title: "", message: "Successfully publish this record for open access." });
							
							$("#individual-intro").find(".navigation-pagination").find(".publish-for-qut").removeClass('disabled');
							
						}else if (errorCode == "MRS_002"){	//failed
							btn.removeClass('disabled');
							$.growl.error({ title: "", message: "Failed to publish this record." +  data.MRS_ERROR_MSG});
						}
					}, "json");	
				}
			});
		}
	});
	
	$("#individual-intro").find(".navigation-pagination").find(".publish-for-qut").click(function(e){
		var btn = $(this);
		if (!btn.hasClass('disabled')) {
			var nextRecordsState = "PublishedQUTAccess";
			var individualLocalName = $("#wrapper-content").find(".individualLocalName").text();
			var currentModule = btn.find('.currentModule').html();
			
			Messi.ask('Are you SURE you want to publish this record for QUT Access? If in doubt, CANCEL..', function(val) { 
				if (val==='Y') {
					$("#individual-intro").find(".navigation-pagination").find(".publish-for-qut").addClass('disabled');
					$.post( "/manageRecords?module=workspace&action=moveToNextState&nextRecordsState=" + nextRecordsState + "&currentRecordsState=" + currentModule + "&reponseInfoLevel=0", { key:individualLocalName }, function( data ) {
						var errorCode = data.MRS_ERROR_CODE;
						if (errorCode == "MRS_001"){	//success.
								
							$("#individual-intro").find("#publishRecord-editor").siblings('li').find('span').text("Published - QUT Access") ;
							
							if (data.emailErrorMessage){
								$.growl.error({ title: "", message: data.emailErrorMessage });
							}
							$.growl.notice({ title: "", message: "Successfully publish this record for qut access." });
							
							$("#individual-intro").find(".navigation-pagination").find(".publish-for-open").removeClass('disabled');
							
						}else if (errorCode == "MRS_002"){	//failed
							btn.removeClass('disabled');
							$.growl.error({ title: "", message: "Failed to publish this record." +  data.MRS_ERROR_MSG});
						}
					}, "json");	
				}
			});
		}
	});
	
	var i=1;
	$("#individual-intro").find("#navigation-content").find("article").each(function(e){
		var article = $(this);
		var title = i+". " + article.find("h3 span").html();
		article.find("h3 span").html(title);
		i++;
	});
	
	$("#individual-intro").find("#admin-on-top").find(".edit-label").click(function(e) {
		
		var editLabelItem = $(this);
		var postRequestStr = editLabelItem.attr("href");
		var parentDiv = editLabelItem.parents("#admin-on-top");
	
		$.post( postRequestStr, function( data ) {
			var labelEditor = parentDiv.find("#label-editor");
			var labelDisplay = parentDiv.find("#label-display");
			
			labelEditor.html(data);
			labelDisplay.hide();
			labelEditor.show();
			
			initLabelEditHTML(labelEditor);
		});
		
		e.preventDefault();
	});
	
	function initLabelEditHTML(labelEditor){
		addEventListenersToHelpBtn(labelEditor);
		
		labelEditor.find('.button_wrapper .submit').click(function(e) {
			//Find the parent with form
			var submitBtn = $(this);
			if (!submitBtn.hasClass('disabled')) {
				var parentForm = submitBtn.parents('form');
				var action = parentForm.attr('action');
			
				submitBtn.addClass("disabled");
				// Post data to the action URL here and get the response
				$.post( action, parentForm.serialize(),  function( data ) { 
					/*slideUpContainer(labelEditor, function() {
						labelEditor.addClass('hide');
						var labelDisplay = labelEditor.next( "#label-display" );
						labelDisplay.find('h2').remove();
						labelDisplay.append(data);
						
						//initPropertyListItemHTML(propertyListContainer);
					});*/
					
					// TODO : stop reloading the page when edit label.
					// Please see PostEditCleanupController.doPostEditRedirect()
					window.location.reload();
				});
			}
			e.preventDefault();
		});
		
		labelEditor.find('.button_wrapper .cancel').click(function(e) {
			slideUpContainer(labelEditor, function() {
				var labelDisplayContainer = labelEditor.next( "#label-display" );
				labelDisplayContainer.show();
				labelEditor.hide();
			
			});
			e.preventDefault();
		});
	}
	 
	$("#individual-intro .property h3 a").click(function(e) {	// Add
		var propertyEditorDivID = $(this).parent().attr("id");
		
		var postRequestStr = $(this).attr("href");
		$.post( postRequestStr, function( data ) {
			var itemEditor = $("#" + propertyEditorDivID + "-editor");

			itemEditor.html(data);
			itemEditor.removeClass("hide");
			var propertyListContainer = itemEditor.parent( ".property-list" );
			propertyListContainer.prepend(itemEditor);

			if (propertyListContainer.find("li[role='listitem']").length == 0){	// if empty
				itemEditor.find(".cancel").hide();
			}
			
			initAddEditPropertyItemHTML(propertyEditorDivID);
			
			itemEditor.hide().slideDown(500);
		});
		
		e.preventDefault();
	});
	
	$("#individual-intro .property .property-list .edit-individual").click(function(e) {	// Edit
		editPropertyItem($(this));
		e.preventDefault();
	});
	
	$("#individual-intro .property .property-list .delete-individual").click(function(e) { // Delete
		deletePropertyItem($(this));
		e.preventDefault();
	});
	
	function editPropertyItem(itemDiv){
		
		var propertyEditorDivID = itemDiv.parent().attr("property-id");
		var postRequestStr = itemDiv.parent().attr("href");
		var listItem = itemDiv.parents('li[role="listitem"]'); // return only the immediate parent li
		
		
		$.post( postRequestStr, function( data ) {
				listItem.addClass('hide');
				var itemEditor = $("#" + propertyEditorDivID + "-editor");
				itemEditor.html(data);
				itemEditor.removeClass('hide');
				listItem.before(itemEditor);
				initAddEditPropertyItemHTML(propertyEditorDivID);
				itemEditor.hide().slideDown(500);
		});
	}
	
	function deletePropertyItem(itemDiv){
		var propertyEditorDivID = itemDiv.parent().attr("property-id");
		var postRequestStr = itemDiv.parent().attr("href");
		var listItem = itemDiv.parents('li[role="listitem"]'); // return only the immediate parent li
		
		$.post( postRequestStr, function( data ) {
			var confirmMsgForm = $(data);
			confirmMsgForm.find('p.submit').addClass('hide');
			// LIBRDF-103
			if (propertyEditorDivID == "RDRDataUpload"){
				Messi.ask($("<p>Please confirm you wish to delete all of your data files.</p>"), function(val) { 	
					if (val === 'Y') {
						deleteRDRPackge(propertyEditorDivID, listItem, confirmMsgForm);
					}
				});
			}else{
				Messi.ask(confirmMsgForm, function(val) { 	
					if (val === 'Y') {
						deleteRDFProperty(propertyEditorDivID, listItem, confirmMsgForm);
					}
				});
			}
		});
	}
	
	function deleteRDFProperty(propertyEditorDivID, listItem, confirmMsgForm){
		var action = confirmMsgForm.attr('action');
		listItem.slideUp(500, function(e){
			listItem.remove();
			
			var itemEditor = $("#" + propertyEditorDivID + "-editor");
			var propertyList = itemEditor.parent(".property-list");
			if (propertyList.find("li[role='listitem']").length == 0){	// if empty
				propertyList.parent(".property").find("h3").find("a").trigger( "click" );
			}
		});
		
		$.post( action, confirmMsgForm.serialize());
	}
	
	// LIBRDF-103
	function deleteRDRPackge(propertyEditorDivID, listItem, confirmMsgForm){
		var packageID = (listItem.find('#packageID').text()).trim();
		
		if (packageID != '') {
			listItem.css({"opacity":"0.5"});
			$.post("/systemIntegration?module=rdr&action=deleteRDRPackage", {packageID:packageID }, function(data) {
				var errorCode = data.SYSINT_ERROR_CODE;
				if (errorCode == "SYSINT_001"){	//success.
					deleteRDFProperty(propertyEditorDivID, listItem, confirmMsgForm);
					$.growl.notice({ title: "", message: data.SYSINT_ERROR_MSG});
				}else if (errorCode == "SYSINT_002"){	//failed
					new Messi('Failed to delete the dataset. ' + data.SYSINT_ERROR_MSG, {title: 'Error', titleClass: 'anim error', buttons: [{id: 0, label: 'Close', val: 'X'}]});
					listItem.css({"opacity":""});
				}
			}, "json");
		}else{
			new Messi('Failed to delete the dataset. Dataset ID cannot be empty.', {title: 'Error', titleClass: 'anim error', buttons: [{id: 0, label: 'Close', val: 'X'}]});
		}
	}
	

	function initAddEditPropertyItemHTML(propertyEditorDivID){ // When click add or edit
		
		var propertyEditorDivName = "#" + propertyEditorDivID + "-editor";
		var propertyEditorDiv = $(propertyEditorDivName);
		
		var ckEditorID = propertyEditorDivID + "-ckeditor";
		if (propertyEditorDiv.find("#" + ckEditorID).length > 0){
			CKEDITOR.replace(ckEditorID);
		}
		
		// ANDS ORCID  widget
		var orcidWidget = propertyEditorDivID + "-orcidWidget";
		if (propertyEditorDiv.find("#" + orcidWidget).length > 0){
			var orcid = propertyEditorDiv.find("#" + orcidWidget);
			orcid.orcid_widget({
				pre_lookup: false,
				pre_open_search:false,
				lookup_text: 'Lookup',
				search_text: 'Search'
			});
		}
		
		// Jquery date_picker
		propertyEditorDiv.find("#dateFromCalander").datepicker({ dateFormat: "yy-mm-dd" });
		propertyEditorDiv.find("#dateToCalander").datepicker({ dateFormat: "yy-mm-dd" });
		propertyEditorDiv.find("#dateCalander").datepicker({ dateFormat: "yy-mm-dd" });
		
		
		// ANDS Location capture widget
		if (propertyEditorDiv.find("#locationCaptureWidget").length > 0){
			var locationCaptureWidget = propertyEditorDiv.find("#locationCaptureWidget");
			var coverageType = propertyEditorDiv.find("#coverageType");
			var spatialValue = propertyEditorDiv.find("#spatialValue");
			
			if ( coverageType.val() == "kmlPolyCoords"){
				locationCaptureWidget.ands_location_widget('init', {target:'mapCordinates'});
		 	}
			
			if (coverageType.val() == "iso19139dcmiBox"){
				coverageType.val("kmlPolyCoords");
				
				var iso19139dcmiBoxCordinates = spatialValue.val();
		 		var myRegexp = /northlimit=\s*(.+);\s*southlimit=\s*(.+);\s*westlimit=\s*(.+);\s*eastlimit=\s*(.+);\s*projection=.*/gi;

		 		var match = myRegexp.exec(iso19139dcmiBoxCordinates);
		 		if ((match != null) && (match.length == 5)){	// match[0] has the full string.
		 			var northlimit = match[1];
		 			var southlimit = match[2];
		 			var westlimit = match[3];
		 			var eastLimit = match[4];
			 		
			 		var polyCoords = eastLimit+","+northlimit+" "+eastLimit+","+southlimit+" "+westlimit+","+southlimit+" "+westlimit+","+northlimit+" "+eastLimit+","+northlimit;
			 		locationCaptureWidget.val(polyCoords);
		 		}
			}
			
			var spatialCoverageMapDiv = propertyEditorDiv.find("#spatialCoverageMapDiv");
			var coverageTextValueDiv = propertyEditorDiv.find("#coverageTextValueDiv");
			
			if ( coverageType.val() == "kmlPolyCoords"){
				spatialCoverageMapDiv.show();
				coverageTextValueDiv.hide();
		 	} else {
		 		spatialCoverageMapDiv.hide();
		 		coverageTextValueDiv.show();
		 	}
			
			coverageType.change(function(){
				if (coverageType.val() == 'kmlPolyCoords'){
					locationCaptureWidget.ands_location_widget('init', {target:'mapCordinates'});	// if widget is not initialized. This happens only in edit mode. Map is loaded only user has selected "map".
					spatialCoverageMapDiv.show();
					coverageTextValueDiv.hide();
				}else if(coverageType.val() == 'text'){
					coverageTextValueDiv.show();
					spatialCoverageMapDiv.hide();
				}
			});
		}
		
		// Licence 
		if (propertyEditorDiv.find('#licenceType').length > 0){
			onChangeLicenceField();
		}
		propertyEditorDiv.find('#licenceType').change(function(){
			onChangeLicenceField();
		});
		
		function onChangeLicenceField(){
			var licenceType = propertyEditorDiv.find('#licenceType');
			var licenceValueDiv = propertyEditorDiv.find('#licenceValueDiv');
			var rightsUriDiv = propertyEditorDiv.find('#rightsUriDiv');
			
			if (licenceType.find(":selected").val() == "Other"){
				licenceValueDiv.show();
				rightsUriDiv.hide();
			}else{
				licenceValueDiv.hide();
				rightsUriDiv.hide();
			}
		}
		
		// Research Areas
		if (propertyEditorDiv.find('#researchAreasInfoType').length > 0){
			onChangeResearchAreasField();
		}
		propertyEditorDiv.find('#researchAreasInfoType').change(function(){
			onChangeResearchAreasField();
		});
		
		function onChangeResearchAreasField(){
			var researchAreasInfoType = propertyEditorDiv.find('#researchAreasInfoType');
			var researchAreasFORTypeDiv = propertyEditorDiv.find('#researchAreasFORTypeDiv');
			var researchAreasValueDiv = propertyEditorDiv.find('#researchAreasValueDiv');
			
			
			if (researchAreasInfoType.find(":selected").val() == "local"){
				researchAreasValueDiv.show();
				researchAreasFORTypeDiv.hide();
			}else{
				researchAreasFORTypeDiv.show();
				researchAreasValueDiv.hide();
			}
		}
		
		// Citation Metadata
		if (propertyEditorDiv.find('#mainform-dataCitation').length > 0){
			initCitationMetaDataWebForm(propertyEditorDiv);
		}
		
		// Related Objects - if load autoCompleteObjectPropForm.ftl
		if (propertyEditorDiv.find('#relatedObject').length > 0){
			customFormData.typeName = "by typing name...";
			(new CustomForm()).onLoad();

			// When user click change selection..
			propertyEditorDiv.find('.changeSelection').click(function(e) {
				propertyEditorDiv.find('.acSelection').hide();
				propertyEditorDiv.find('#relatedObject').find('div').show();
				
				e.preventDefault();
			});
			
		}
		
		addEventListenersToHelpBtn(propertyEditorDiv);
		addEventListenersToSubmitBtn(propertyEditorDiv);
		addEventListenersToCancelBtn(propertyEditorDiv);
		addEventListenersToInputValidation(propertyEditorDiv);
	
		
		function addEventListenersToSubmitBtn(container) {
			container.find('.button_wrapper .submit').click(function(e) {
				//Find the parent with form
				var submitBtn = $(this);
				if (!submitBtn.hasClass('disabled')) {
					
					var parentForm = submitBtn.parents('form');
					var action = parentForm.attr('action');
					
					if (propertyEditorDiv.find('#mainform-dataCitation').length > 0){
						parentForm = propertyEditorDiv.find('#mainform-dataCitation');
						action = parentForm.attr('action');
					}
					
					
					// Before serialize the form check for any CKEDITOR instances
					for ( instance in CKEDITOR.instances ) {
			            CKEDITOR.instances[instance].updateElement();
			        }
					
					var errors = validateMainForm(parentForm, propertyEditorDivID);
					if (errors.length === 0) {
						// LIBRDF-103
						if (propertyEditorDiv.find('#mainform-RDRDataUpload').length > 0){
							createRDRPackage(container, propertyEditorDiv, submitBtn, action, parentForm);
						}else{
							submitForm(container, propertyEditorDiv, submitBtn, action, parentForm);
						}
					} else {
						$.each(errors, function( i, elem ) {
							elem.addClass("required_filed");
						});
						new Messi('Please complete the required fields.', {title: 'Error', titleClass: 'anim error', buttons: [{id: 0, label: 'Close', val: 'X'}]});
					}
				}
				
				e.preventDefault();
			});
		}
		
		function addEventListenersToCancelBtn(container) {
			container.find('.button_wrapper .cancel').click(function(e) {
				slideUpContainer(container, function() {
					var propertyListContainer = propertyEditorDiv.parent(".property-list");
					propertyListContainer.find('li[role="listitem"].hide').removeClass('hide');
					propertyEditorDiv.addClass('hide');
				
				});
				e.preventDefault();
			});
		}
		
		function addEventListenersToInputValidation(container) {
			container.find('input, select, textarea').change(function(e){
				$(this).removeClass("required_filed");
			});
		}
		
	}
	
	function addEventListenersToHelpBtn(container) {
		var helpButton = container.find(".help_btn");
		var helpDiv =  container.find(".div_help");
		
		helpButton.click(function() {
			var btn = $(this);
			var text = btn.siblings('.div_help');
			if (text.is(":visible")) {
				text.slideUp(300);
				btn.removeClass('help_shown');
				btn.addClass('help_hidden');
			} else {
				text.slideDown(300);
				btn.removeClass('help_hidden');
				btn.addClass('help_shown');
			}
		});
		helpDiv.show();
		
		if (container.find('.help_heading').find('.help_hidden').length >0 ){
			helpDiv.hide();
		}
	}
	
	function slideUpContainer(element, callback) {
		element.animate({height: 0}, 500, function(e1){
			element.empty();
			element.css('height', 'auto');
			callback();
		});
	}
	
	function submitForm(container, propertyEditorDiv, submitBtn, action, parentForm, callback){
		submitBtn.addClass("disabled");
		// Post data to the action URL here and get the response
		$.post( action, parentForm.serialize(),  function( data ) {
			slideUpContainer(container, function() {
				container.addClass('hide');
				var propertyListContainer = propertyEditorDiv.parent( ".property-list" );
				propertyListContainer.find('li[role="listitem"]').remove();
				propertyListContainer.append(data);
				
				initPropertyListItemHTML(propertyListContainer);
				if (callback) callback();
			});
			
		});
	}
	
	// LIBRDF-103
	function createRDRPackage(container, propertyEditorDiv, submitBtn, action, parentForm){
		var packageTitleElem = parentForm.find('#RDRPackageTitle');
		var individualURLElem = parentForm.find('#individualURL');
		var packageNameElem = parentForm.find('#RDRPackageName');
		var packageIDElem = parentForm.find('#RDRPackageID');
		var packageURLElem = parentForm.find('#RDRPackageURL');
				
		if ((packageTitleElem.val() != '') && (individualURLElem.text() != '')){
			
			var leftColumn = propertyEditorDiv.find('.left_column');
			leftColumn.prepend('<img class="ajax-loader" src="/edit/forms/qut_libs/common/images/ajax-loader.gif" style="margin:1px;">');

			$.post("/systemIntegration?module=rdr&action=createRDRPackage", {packageTitle:packageTitleElem.val(), individualURL:individualURLElem.text() }, function(data) {
				var errorCode = data.SYSINT_ERROR_CODE;
				if (errorCode == "SYSINT_001"){	//success.
					var responseValue = data.SYSINT_ERROR_MSG;
					packageNameElem.val(responseValue.PACKAGE_NAME);
					packageIDElem.val(responseValue.PACKAGE_ID);
					packageURLElem.val(responseValue.PACKAGE_URL);
					
					packageTitleElem.prop('disabled', false);
					
					var propertyListContainer = propertyEditorDiv.parent( ".property-list" );
					submitForm(container, propertyEditorDiv, submitBtn, action, parentForm, function() {
						propertyListContainer.css({"opacity":"0"});
						propertyListContainer.find('.upload-RDRDataUpload').attr('href', responseValue.RESOURCE_UPLOAD_URL);
						
						/*var htmlMsgElem = $("<div style='padding-top: 10px;color: green;'>Please wait...</div>");
						propertyListContainer.prepend(htmlMsgElem);
						htmlMsgElem.hide().fadeIn('fast');
						htmlMsgElem.delay(1000).fadeOut('normal', function() {
					        $(this).remove();
					    });*/
						
						location.href = responseValue.RESOURCE_UPLOAD_URL;
					});
					
				}else if (errorCode == "SYSINT_002"){	//failed
					leftColumn.find("img.ajax-loader").remove();
					leftColumn.css({"opacity":""});
					packageTitleElem.prop('disabled', false);
					new Messi('Failed to create the dataset. ' + data.SYSINT_ERROR_MSG, {title: 'Error', titleClass: 'anim error', buttons: [{id: 0, label: 'Close', val: 'X'}]});
				}
			}, "json");
		}
	}
	
	function initPropertyListItemHTML(propertyListContainer){	// list container
		
		propertyListContainer.find("li[role='listitem'] a .edit-individual").click(function(e1) {
			editPropertyItem($(this));
			e1.preventDefault();
		});
		
		propertyListContainer.find("li[role='listitem'] a .delete-individual").click(function(e1) {
			deletePropertyItem($(this));
			e1.preventDefault();
		});
		
		initLocationCaptureWidget(propertyListContainer);
	}
	
	// Init after submiting data
	function initLocationCaptureWidget(propertyListContainer){
		var cnt = 0;
		propertyListContainer.find('.sptialStatContainerSelector').each(function(e) {
			var spatialCoverageType = $(this).children('.spatialCoverageType').html();
			var spatialCoverageValue = $(this).children('.spatialCoverageValue').html();
			
			if (spatialCoverageType == "iso19139dcmiBox"){
				var myRegexp = /northlimit=\s*(.+);\s*southlimit=\s*(.+);\s*westlimit=\s*(.+);\s*eastlimit=\s*(.+);\s*projection=.*/gi;
				var match = myRegexp.exec(spatialCoverageValue);
		 		
				if ((match != null) && (match.length == 5)){	// match[0] has the full string.
					var northlimit = match[1];
					var southlimit = match[2];
					var westlimit = match[3];
					var eastLimit = match[4];
					
					var polyCoords = eastLimit+","+northlimit+" "+eastLimit+","+southlimit+" "+westlimit+","+southlimit+" "+westlimit+","+northlimit+" "+eastLimit+","+northlimit;
					$(this).html("");
					$(this).ands_location_widget('init', {lonLat:polyCoords, target:"geoLocation"+cnt});
					$(this).find('.alw_toolbar').hide();
					$(this).height(320);
				}
			}else if (spatialCoverageType == "kmlPolyCoords"){
				$(this).html("");
				$(this).ands_location_widget('init', {lonLat:spatialCoverageValue, target:"geoLocation"+cnt});
				$(this).find('.alw_toolbar').hide();
				$(this).height(320);
			}else if (spatialCoverageType == "text"){
				var textStr = spatialCoverageValue;
				$(this).html(textStr);
			}else{
			}
			cnt++;
		});
	}
	
	$("#individual-intro .property .property-list").each(function( e ) {
		var propertyList = $(this);
		
		if (propertyList.find("li[role='listitem']").length == 0){
			var property = propertyList.parent( ".property" );
			property.find("h3").find("a").trigger( "click" );
			
		}
	});

	$("#individual-intro .property h3.allow-single").find('img[class=add-individual]').hide();
	
	// LIRDF-103
	// Set RDR dataset resource upload URL after page is loaded.
	$("#individual-intro .property .property-list").find(".upload-RDRDataUpload").each(function(e){
		var itemDiv = $(this);
		var listItem = itemDiv.parents('li[role="listitem"]'); // return only the immediate parent li
		var packageID = (listItem.find('#packageID').text()).trim();
		
		$.post("/systemIntegration?module=rdr&action=getRDRUploadNewResourceURL", {packageID:packageID },  function(data) {
			var errorCode = data.SYSINT_ERROR_CODE;
			if (errorCode == "SYSINT_001"){	//success.
				var responseValue = data.SYSINT_ERROR_MSG;
				itemDiv.attr('href', responseValue.RESOURCE_UPLOAD_URL);
			}
		}, "json");	
	});
	
	$("#navigation-pagination").find('.next').click(function(e){
		var currentTabName = $("#navigation-tabs .current a").attr('name');
	});
	
	
	function validateMainForm(parentForm, propertyEditorDivID){
		var errorElements = [];
		
		var parentFormID = parentForm.attr("id");
		
		switch(parentFormID) {
			case "mainform-biography":
				return validateBiography(parentForm, errorElements);
			case "mainform-researchAreas":
				return validateResearchAreas(parentForm, errorElements);
			case "mainform-qutEPrints":
			case "mainform-relatedInformation":
			case "mainform-predecessorOrganisation":
			case "mainform-successorOrganisation":
			case "mainform-collaborator":
			case "mainform-partnerInstitution":
				return validateQUTEPrints(parentForm, errorElements);
			case "mainform-linkToexternalRecords":
				return validateLinkToexternalRecords(parentForm, errorElements);
			case "mainform-position":
				return validatePosition(parentForm, errorElements);
			case "mainform-licence":
				return validateLicence(parentForm, errorElements);
			case "mainform-thomsonReutersResearcherID":
			case "mainform-scopusAuthorID":
			case "mainform-locationOfDigitalData":
			case "mainform-otherIdentifierType":
				return validateTwoFieldWebForm(parentForm, errorElements);
			case "mainform-fundingScheme":
			case "mainform-grantor":
			case "mainform-dataFileTypes":
			case "mainform-accessRightsInfo":
			case "mainform-AOUName":
			case "mainform-accessPolicyURL":
				return validateOneFreeTextWebForm(parentForm, errorElements);
			case "mainform-dateRecordModified":
			case "mainform-dateRecordCreated":
				return validateDateCalanderField(parentForm, errorElements);
			case "mainform-ordcIdentifier":
				return validateORCDIdentifier(parentForm, errorElements);
			case "mainform-spatialCoverage":
				return validateSpatialCoverage(parentForm, errorElements);
			case "mainform-temporalCoverage":
				return validateTemporalCoverage(parentForm, errorElements);
			case "mainform-recordInitiallyCreatedBy":
				return validateRecordInitiallyCreatedBy(parentForm, errorElements);
			case "mainform-citationStyle":
				return validateCitationStyle(parentForm, errorElements);
			case "mainform-dataCitation":
				return validateDataCitation(parentForm, errorElements);
			case "mainform-contactInfo":
				return validateContactInfo(parentForm, errorElements);
			case "mainform-publishRecord":
				return validateRecordStatus(parentForm, errorElements);
			case "mainform-copyrightInfo":
				return validateCopyrightInfo(parentForm, errorElements);
			// Select from related objects - no need to validate
			case "customForm-" + propertyEditorDivID:	 // AutoComplete related objects
				return validateAutoCompleteRelatedObject(parentForm, errorElements);
			case "editForm-" + propertyEditorDivID:	 // Drop down related objects
				return validateRelatedObjectFromDropDown(parentForm, errorElements);
			case "mainform-RDRDataUpload":
				return validateRDRDataUpload(parentForm, errorElements);
			default:
				break;
		}
		
		return errorElements;
	}
	
	function validateBiography(parentForm, errorElements){
		var biography = parentForm.find('#biography-ckeditor');
		if (biography.val() == ""){
			errorElements.push(biography);
		}
		return errorElements;
	}
	
	function validateResearchAreas(parentForm, errorElements){
		
		var researchAreasInfoType = parentForm.find('#researchAreasInfoType');
		var researchAreasValue = parentForm.find('#researchAreasValue');
		var researchAreasFORType = parentForm.find('#researchAreasFORType');
			
		var researchAreasInfoTypeValue = researchAreasInfoType.find(":selected").val();
		if (researchAreasInfoTypeValue == 'local'){
			if (researchAreasValue.val() == ''){
				errorElements.push(researchAreasValue);
				return errorElements;
			}
			researchAreasFORType.val("");
		}else{
			if ( researchAreasFORType.find(":selected").val() == ""){
				errorElements.push(researchAreasFORType);
				return errorElements;
			}
			researchAreasValue.val('');
		}
		
		return errorElements;
	}
	

	function validateQUTEPrints(parentForm, errorElements){
		var identifierType = parentForm.find('#identifierType');
		var idenValue = parentForm.find('#idenValue');
		var description = parentForm.find('#title');
		
		if (identifierType.find(":selected").val() == ""){
			errorElements.push(identifierType);
		}
		
		if (idenValue.val() == ''){
			errorElements.push(idenValue);
		}
		
		if (description.val() == ''){
			errorElements.push(description);
		}
		
		return errorElements;
	}
	
	function validateLinkToexternalRecords(parentForm, errorElements){
		var relationshipType = parentForm.find('#relationshipType');
		
		if (relationshipType.find(":selected").val() == ""){
			errorElements.push(relationshipType);
		}else{
			var relationshipKey = parentForm.find('#relationshipKey');
			var relationshipURL = parentForm.find('#relationshipURL');
			var relatedObjectDisplayName = parentForm.find('#relatedObjectDisplayName');
			
			if (relationshipKey.val() == ''){
				errorElements.push(relationshipKey);
			}
			
			if (relationshipURL.val() == ''){
				errorElements.push(relationshipURL);
			}
			
			if (relatedObjectDisplayName.val() == ''){
				errorElements.push(relatedObjectDisplayName);
			}
		}
		return errorElements;
	}
	
	function validatePosition(parentForm, errorElements){
		var position = parentForm.find('#position');
		var divfac = parentForm.find('#divfac');
		var school = parentForm.find('#school');
		
		if (position.val() == ''){
			errorElements.push(position);
		}
		
		if (divfac.val() == ''){
			errorElements.push(divfac);
		}
		
		if (school.val() == ''){
			errorElements.push(school);
		}
		
		return errorElements;
	}
	
	function validateTwoFieldWebForm(parentForm, errorElements){
		var description = parentForm.find('#description');
		
		if (description.val() == ''){
			errorElements.push(description);
		}
		return errorElements;
	}
	
	function validateOneFreeTextWebForm(parentForm, errorElements){
		var freeTextValue = parentForm.find('.freeTextValue');
		
		if (freeTextValue.val() == ''){
			errorElements.push(freeTextValue);
		}
		return errorElements;
		
	}
	
	function validateORCDIdentifier(parentForm, errorElements){
		var orcidWidget = parentForm.find('#ordcIdentifier-orcidWidget');
		
		if (orcidWidget.val() == ''){
			errorElements.push(orcidWidget);
		}
		return errorElements;
	}
	
	function validateRecordInitiallyCreatedBy(parentForm, errorElements){
		var recordCreatedByName = parentForm.find('#recordCreatedByName');
		
		if (recordCreatedByName.val() == ''){
			errorElements.push(recordCreatedByName);
		}
		return errorElements;
	}
	
	function validateSpatialCoverage(parentForm, errorElements){
		var coverageType = parentForm.find('#coverageType');
		var mapCordinates = parentForm.find('#mapCordinates');
		var coverageTextValue = parentForm.find('#coverageTextValue');
		
 		if ((coverageType.val() == "kmlPolyCoords")){
 			if (mapCordinates.val() == ""){
 				errorElements.push(mapCordinates);
 			}else{
		 		$("#spatialValue").val("");
				$("#spatialValue").val($('#mapCordinates').val());
			}
 		}else if(coverageType.val() == "text"){
 			if (coverageTextValue.val() == ""){
 				errorElements.push(coverageTextValue);
 			}else{
 				$("#spatialValue").val("");
				$("#spatialValue").val($('#coverageTextValue').val());
 			}
		}else{
			errorElements.push(coverageType);
		}
 		
 		return errorElements;
	}
	
	function validateTemporalCoverage(parentForm, errorElements){
		var dateFrom = parentForm.find('#dateFromCalander');
		var dateTo = parentForm.find('#dateToCalander');
		
		if (dateFrom.val() == ''){
			errorElements.push(dateFrom);
		}
		
		if (dateTo.val() == ''){
			errorElements.push(dateTo);
		}
		return errorElements;
	}
	
	function validateLicence(parentForm, errorElements){
		var licenceType = parentForm.find('#licenceType');
		var licenceValue = parentForm.find('#licenceValue');
		var rightsUri = parentForm.find('#rightsUri');
		
		
		licenceTypeValue = licenceType.find(":selected").val();

		if (licenceTypeValue == ""){
			errorElements.push(licenceType);
		}else{
			if (licenceTypeValue == 'CC-BY'){
				licenceValue.val("Creative Commons Attribution 3.0");
				rightsUri.val("http://creativecommons.org/licenses/by/4.0/");
			}else if(licenceTypeValue == 'CC-BY-SA'){
				licenceValue.val("Creative Commons Attribution-Share Alike 3.0");
				rightsUri.val("http://creativecommons.org/licenses/by-sa/4.0/");
			}else if(licenceTypeValue == 'CC-BY-NC'){
				licenceValue.val("Creative Commons Attribution-NonCommercial 3.0");
				rightsUri.val("http://creativecommons.org/licenses/by-nc/4.0/");
			}else if(licenceTypeValue == 'CC-BY-NC-SA'){
				licenceValue.val("Creative Commons Attribution-NonCommercial-Share Alike 3.0");
				rightsUri.val("http://creativecommons.org/licenses/by-nc-sa/4.0/");
			}else if(licenceTypeValue == 'CC-BY-ND'){
				licenceValue.val("Creative Commons Attribution-No Derivatives 3.0");
				rightsUri.val("http://creativecommons.org/licenses/by-nd/4.0/");
			}else if(licenceTypeValue == 'CC-BY-NC-ND'){
				licenceValue.val("Creative Commons Attribution-NonCommercial-No Derivatives 3.0");
				rightsUri.val("http://creativecommons.org/licenses/by-nc-nd/4.0/");
			}else if(licenceTypeValue == 'GPL'){
				licenceValue.val("GNU General Public License (GPL)");
				rightsUri.val("http://www.gnu.org/licenses/gpl.html");
			}else if(licenceTypeValue == 'AusGOALRestrictive'){
				licenceValue.val("AusGOAL Restrictive Licence");
				rightsUri.val("http://www.ausgoal.gov.au/restrictive-licence-template");	
			}else if(licenceTypeValue == 'NoLicense'){
				licenceValue.val("");
				rightsUri.val("");
			}else if(licenceTypeValue == 'Other'){
				if (licenceValue.val() == ""){
					errorElements.push(licenceValue);
				}
			}
		}
		
		return errorElements;
	}
	
	function validateCitationStyle(parentForm, errorElements){
		var styleType = parentForm.find('#styleType');
		var styleValue = parentForm.find('#styleValue');
		
		if (styleValue.val() == ''){
			errorElements.push(styleValue);
		}
		
		return errorElements;
	}
	
	function validateDateCalanderField(parentForm, errorElements){
		var date = parentForm.find('#dateCalander');
		
		if (date.val() == ''){
			errorElements.push(date);
		}
		return errorElements;
	}
	
	function validateContactInfo(parentForm, errorElements){
		
		var firstname = parentForm.find('#firstname');
		var surname = parentForm.find('#surname');
		var email = parentForm.find('#email');
		
		
		if (firstname.val() == ''){
			errorElements.push(firstname);
		}
		
		if (surname.val() == ''){
			errorElements.push(surname);
		}
		
		if (email.val() == ''){
			errorElements.push(email);
		}
		
		return errorElements;
	}
	
	function validateRecordStatus(parentForm, errorElements){
		var recordStatus = parentForm.find('#recordStatus');
		
		if ( recordStatus.find(":selected").val() == ""){
			errorElements.push(recordStatus);
		}
		
		return errorElements;
	}
	
	function validateAutoCompleteRelatedObject(parentForm, errorElements){
		var relatedObject = parentForm.find('#object');
		if ((relatedObject.val() == '') || (relatedObject.val() == 'Start typing for records to appear...')){
			errorElements.push(relatedObject);
		}
		
		return errorElements;
	}
	
	function validateRelatedObjectFromDropDown(parentForm, errorElements){
		var relatedObject = parentForm.find('#objectVar');
		
		if ( relatedObject.find(":selected").val() == ""){
			errorElements.push(relatedObject);
		}
		
		return errorElements;
	}
	
	function validateCopyrightInfo(parentForm, errorElements){
		var copyright = parentForm.find('#copyright');
		if ((copyright.val() == '')){
			errorElements.push(copyright);
		}
		
		return errorElements;
	}
	
	function validateRDRDataUpload(parentForm, errorElements){
		var dataStorageTitle = parentForm.find('#RDRDataTitle');
		if ((dataStorageTitle.val() == '')){
			errorElements.push(dataStorageTitle);
		}
		
		return errorElements;
	}
	
	function validateDataCitation(parentForm, errorElements){
		var subFormDiv = parentForm.next(); // #subform-datacitation
		errorElements = validateDataCitationRequiredFields(subFormDiv);
		if (errorElements.length === 0) {
			var arr = {};
			var singles = new Array;
			var mutiples = new Array;
			var contributor = "";
			
			var idenType = subFormDiv.find('#txtIdenType').val();
			if ((idenType == "doi")){
				if (user_role == "CURATOR" || user_role == "DB_ADMIN" || user_role == "SELF" || user_role == "ROOT"){
					subFormDiv.find("#txtDOIStatus").val("NO_CHANGE");
				}else{
					subFormDiv.find("#txtDOIStatus").val("MINT_OR_UPDATE_DOI");
				}
				
			}else{
				subFormDiv.find("#txtDOIStatus").val("NO_CHANGE");
			}
		
			//singles
			var $inputs = subFormDiv.find(':input');			
			$inputs.each(function(e) {
				var elem = $(this);
				if ((elem.parents('.multiple').length == 0) && (elem.parents('.field_set').length == 0)){
					singles[singles.length] =  {"name":elem.attr('name'), "value":elem.val()};
				}	
			});
			arr['singles'] = singles;
			
			//multiples
			var conType =  subFormDiv.find('#txtContributorType').val();			
			mutiples[0] = {"name":"txtContributorType", "value":conType};
			if (conType == "Author/creator") {
				
				 subFormDiv.find(".form_tab_1").find(".field_set").each(function() {
					var contributors = new Array;
					$(this).find('input, textarea, select').each(function() {
						var elem = $(this);
						contributors[contributors.length] = {"name":elem.attr('name'), "value":elem.val()};
					});	
					mutiples[mutiples.length] = contributors;
				});
				
				var count = 0;	//LIBRDF-44
				subFormDiv.find(".form_tab_1").find(".field_set").each(function() {
					var fieldSet = $(this);
					if (count > 0){
						contributor += "; ";
					}
					contributor += fieldSet.find('input[name="txtFamillyName"]').val() + "," + fieldSet.find('input[name="txtGivenName"]').val();
					count = count + 1;
				});
				
			} else if (conType == "Group/research institution"){
				subFormDiv.find(".form_tab_2").find(".field_set").each(function() {
					var contributors = new Array;
					$(this).find('input, textarea, select').each(function() {
						var elem = $(this);
						contributors[contributors.length] = {"name":elem.attr('name'), "value":elem.val()};
					});	
					mutiples[mutiples.length] = contributors;		
				});
				
				var count = 0; //LIBRDF-44
				subFormDiv.find(".form_tab_2").find(".field_set").each(function() {
					var fieldSet = $(this);
					if (count > 0){
						contributor += "; ";
					}
					contributor += fieldSet.find('input[name="txtGroupName"]').val() ;
					count = count + 1;
				});
				
				//contributor = $('input[name="txtGroupName"]').val();	// get first group.	
			}
			arr['multiples'] = mutiples;
			
			var rowData = JSON.stringify(arr);	
			parentForm.find("#citationFormValues").val("");
			parentForm.find("#citationFormValues").val(rowData);
			
			var idenValue =  subFormDiv.find('#txtIdenValue').val();
			var title = subFormDiv.find('input[name="txtTitle"]').val();
			var publisher = subFormDiv.find('input[name="txtPublisher"]').val();
			var publicationYear = subFormDiv.find('input[name="txtPublicationYear"]').val();
			var DOIStatus =  subFormDiv.find('#txtDOIStatus').val();
			
			var citationDisplayStr = contributor + ". (" + publicationYear + "). " + title + ". [" + publisher + "]. ";
			if (idenType == "doi"){
				if (DOIStatus == "MINT_OR_UPDATE_DOI"){
					citationDisplayStr = "mint/update doi action is requied for this collection";
				}else{
					var doiLink = "http://doi.org/" + idenValue;
					var idenDisplayStr =  " <a href='" + doiLink + "'>doi:" + idenValue + "<a/>";
					citationDisplayStr += idenDisplayStr;
				}				
			}else if(idenType == "url"){
				var idenDisplayStr =  " <a href='" + idenValue + "'>" + idenValue + "<a/>";
				citationDisplayStr += idenDisplayStr;
			}
			parentForm.find("#citationDisplayStr").val("");
			parentForm.find("#citationDisplayStr").val(citationDisplayStr);
			
			parentForm.find("#citationIdenValue").val("");
			parentForm.find("#citationIdenValue").val(idenValue);
		}
		
		return errorElements;
	}
	
	// Citation Metadata
	function initCitationMetaDataWebForm(propertyEditorDiv){
		if (user_mode == "edit"){
			populateSingles(propertyEditorDiv);
			populateMultiples(propertyEditorDiv);
			
			var identifierType = $('#txtIdenType').val();
			if (identifierType == "doi") {
				propertyEditorDiv.find("#txtIdenType").attr('disabled','disabled');
				propertyEditorDiv.find("#txtIdenValue").attr('readonly','readonly');
				if (user_role == "CURATOR" || user_role == "DB_ADMIN" || user_role == "SELF" || user_role == "ROOT"){
					propertyEditorDiv.find(".mint_doi").show();
					propertyEditorDiv.find(".submit").attr('disabled','disabled').css('opacity', 0.5);
				}else{
					propertyEditorDiv.find(".mint_doi").hide();
					propertyEditorDiv.find(".submit").removeAttr('disabled').css('opacity', 1);
				}
				//refreshForm();
			}else {
				propertyEditorDiv.find("#txtIdenType").attr('disabled','disabled');
				propertyEditorDiv.find("#txtIdenValue").removeAttr('readonly');
				propertyEditorDiv.find(".submit").removeAttr('disabled').css('opacity', 1);
				propertyEditorDiv.find(".mint_doi").hide();
				//refreshForm();
			}
		}
		
		
		var ajustVisibility = function(e) {
			var conType = propertyEditorDiv.find('#txtContributorType').val();
			
			if (conType == "Author/creator") {
				propertyEditorDiv.find(".form_tab_1").show();
				propertyEditorDiv.find(".form_tab_2").hide();
			} else if (conType == "Group/research institution"){
				propertyEditorDiv.find(".form_tab_2").show();
				propertyEditorDiv.find(".form_tab_1").hide();
			}else{
				propertyEditorDiv.find(".form_tab_2").hide();
				propertyEditorDiv.find(".form_tab_1").hide();
			}
			
		}
		propertyEditorDiv.find('#txtContributorType').change(ajustVisibility);
		
		propertyEditorDiv.find('#txtIdenType').change(function() {
			var identifierType = propertyEditorDiv.find('#txtIdenType').val();
				
			if (identifierType == "doi") {
				propertyEditorDiv.find("#txtIdenValue").attr('readonly','readonly');
				propertyEditorDiv.find("#txtIdenValue").val("");
				if (user_role == "CURATOR" || user_role == "DB_ADMIN" || user_role == "SELF" || user_role == "ROOT"){
					propertyEditorDiv.find(".mint_doi").show();
					propertyEditorDiv.find(".submit").attr('disabled','disabled').css('opacity', 0.5);
				}else{
					propertyEditorDiv.find(".mint_doi").hide();
					propertyEditorDiv.find(".submit").removeAttr('disabled').css('opacity', 1);
				}
				//refreshForm();
			}else {
				propertyEditorDiv.find("#txtIdenValue").removeAttr('readonly')
				propertyEditorDiv.find("#txtIdenValue").val("");
				propertyEditorDiv.find(".submit").removeAttr('disabled').css('opacity', 1);
				propertyEditorDiv.find(".mint_doi").hide();
				//refreshForm();
			}
		});
		
		propertyEditorDiv.find('.add_new_child').click(function(e) {
			var form = $(this).parents('.additional_row').siblings('.field_set:last');
			var clone = form.clone(true);
			clone.find('input, textarea').val('');
			clone.find('.inner_row:first').siblings('.inner_row').remove();
			clone.find('.txtDateValue').datepicker( "destroy" );
			clone.find('.txtDateValue').datepicker({ dateFormat: "yy-mm-dd" });

			form.after(clone);
			
			clone.hide().slideDown(500);
			e.preventDefault();
	    });
		
		propertyEditorDiv.find('.form_contributor_del_btn').click(function(e){
			var forms = $(this).parents('.form_sub_tab').find('.field_set');

			if (forms.length <= 1) {
				Messi.alert('Cannot delete last item!');
			} else {
				var parent = $(this).parent();
				parent.slideUp(500, function(){
					parent.remove();
				}); 
			}
			e.preventDefault();
	    });
		
		
		propertyEditorDiv.find('#txtPublicationYear').blur(function() {
		       var match = /^\d{4}$/.exec(this.value);
		       if (!match) {
		           Messi.alert('Invalid year of publication. Format is {yyyy}.');
		       }
		}); 
		
		propertyEditorDiv.find('.mint_doi').click(function(e){
			var errors = validateDataCitationRequiredFields(propertyEditorDiv);
			if (errors.length === 0){
				propertyEditorDiv.find("#ajax_wait").modal({
					opacity:70,
					overlayCss: {backgroundColor:"#000"},
					escClose: false
				})
				
				var idenType = propertyEditorDiv.find('#txtIdenType').val();
				var idenValue = '';
				if ((idenType == "doi")){
					idenValue = propertyEditorDiv.find('#txtIdenValue').val();
				}
				var isSuccess = 'false';
				
				$.post("/mintDOI", $("#subform-dataCitation").serialize(),
					function(data) {
						if (idenValue == ""){	// mint doi 
						   if (data.ErrCode == "MT001"){
							   propertyEditorDiv.find("#txtIdenValue").val("");
							   propertyEditorDiv.find("#txtIdenValue").val(data.Value);
								isSuccess = 'true';
								finaliseDOIMinting(propertyEditorDiv, isSuccess);
								$.modal.close();
								Messi.alert("DOI was minted successfully.");
						   }else{
								finaliseDOIMinting(propertyEditorDiv, isSuccess);
								$.modal.close();
								Messi.alert("Failed to mint the DOI. Error (" + data.ErrCode + ":" + data.Value + ")");
						   }
						}else{	// update doi 
							if (data.ErrCode == "MT002"){
								isSuccess = 'true';
								finaliseDOIMinting(propertyEditorDiv, isSuccess);
								$.modal.close();
								Messi.alert("DOI was updated successfully.");
							}else{
								finaliseDOIMinting(propertyEditorDiv, isSuccess);
								$.modal.close();
								Messi.alert("Failed to update the DOI. Error (" + data.ErrCode + ":" + data.Value + ")");
							}
						}
				});
			}else {
				$.each(errors, function( i, elem ) {
					elem.addClass("required_filed");
				});
				new Messi('Please complete the required fields.', {title: 'Error', titleClass: 'anim error', buttons: [{id: 0, label: 'Close', val: 'X'}]});
			}
			e.preventDefault();
	    });

	}
	
	function finaliseDOIMinting(propertyEditorDiv, isSuccess){
		if (isSuccess == 'true'){
			propertyEditorDiv.find(".submit").removeAttr('disabled').css('opacity', 1);
			propertyEditorDiv.find("#txtIdenType").attr('disabled','disabled');
			propertyEditorDiv.find("#txtIdenValue").attr('readonly','readonly');
		}else{
			propertyEditorDiv.find(".mint_doi").show();
		}
	}
	
		
	function populateSingles(propertyEditorDiv){
		var singles = citationFormValues.singles;
		var length = singles.length;

	    var element = null;
		for (var i = 0; i < length; i++) {
			element = singles[i];
			
			if (element.name == "txtIdenValue"){
				propertyEditorDiv.find("#identifier").find('input[name="txtIdenValue"]').val(element.value);
			}else if (element.name == "txtIdenType"){
				propertyEditorDiv.find("#identifier").find('select[name="txtIdenType"]').val(element.value);
			}else if (element.name == "txtDataUrl"){
				propertyEditorDiv.find("#dataURL").find('input[name="txtDataUrl"]').val(element.value);
			}else if (element.name == "txtTitle"){
				propertyEditorDiv.find("#title").find('input[name="txtTitle"]').val(element.value);
			}else if (element.name == "txtPublisher"){
				propertyEditorDiv.find("#publisher").find('input[name="txtPublisher"]').val(element.value);
			}else if (element.name == "txtPublicationYear"){
				propertyEditorDiv.find("#publicationYear").find('input[name="txtPublicationYear"]').val(element.value);
			}else if (element.name == "txtVersion"){
				propertyEditorDiv.find("#version").find('input[name="txtVersion"]').val(element.value);
			} else if (element.name == "txtDOIStatus"){		
				propertyEditorDiv.find("#doi_status").find('select[name="txtDOIStatus"]').val(element.value);
			}
		}
	}
	
	function populateMultiples(){
		var multiples = citationFormValues.multiples;
			
		var length = multiples.length;
		if (length > 1){
			var conType = multiples[0];
			$("#contributors").find('select[name="txtContributorType"]').val(conType.value);

			var contributors = null;
			if (conType.value == "Author/creator"){
				for (var i = 1; i < length; i++) {
					contributors = multiples[i];	//{"name:txtFamillyName", "value:Gawri"}
					
					if (i == 1){
						$(".form_tab_1").show();
						$(".form_tab_2").hide();
							
						for (var j = 0; j < contributors.length; j++) {
							if (contributors[j].name == "txtFamillyName"){
								$("#form_tab_1").find('input[name="txtFamillyName"]').val(contributors[j].value);
							}else if (contributors[j].name == "txtGivenName"){
								$("#form_tab_1").find('input[name="txtGivenName"]').val(contributors[j].value);
							}
						}
					}else{
						var form = $("#form_tab_1").find('.add_new_child').parents('.additional_row').siblings('.field_set:last');
						var clone = form.clone(true);
						clone.attr('style', '');
						clone.find('input, textarea').val('');
						clone.find('.inner_row:first').siblings('.inner_row').remove();
						
						for (var j = 0; j < contributors.length; j++) {
							if (contributors[j].name == "txtFamillyName"){
								clone.find('input[name="txtFamillyName"]').val(contributors[j].value);
							}else if (contributors[j].name == "txtGivenName"){
								clone.find('input[name="txtGivenName"]').val(contributors[j].value);
							}
						}
					
						form.after(clone);
					}
				}
			}else if (conType.value  == "Group/research institution"){
				for (var i = 1; i < length; i++) {
					contributors = multiples[i];
					
					if (i == 1){
						$(".form_tab_2").show();
						$(".form_tab_1").hide();
						
						if (contributors.length == 1){
							if (contributors[0].name == "txtGroupName"){
								$("#form_tab_2").find('input[name="txtGroupName"]').val(contributors[0].value);
							}
						}
					}else{
						var form = $("#form_tab_2").find('.add_new_child').parents('.additional_row').siblings('.field_set:last');
						var clone = form.clone(true);
						clone.attr('style', '');
						clone.find('input, textarea').val('');
						clone.find('.inner_row:first').siblings('.inner_row').remove();
		
						if (contributors.length == 1){
							if (contributors[0].name == "txtGroupName"){
								clone.find('input[name="txtGroupName"]').val(contributors[0].value);
							}
						}
						
						form.after(clone);
					}
				}
			}
		}
	}
	
	function validateDataCitationRequiredFields(propertyEditorDiv){
		
		var errorElements = [];
		
		var $inputs = propertyEditorDiv.find(':input');
		
		var conType = propertyEditorDiv.find('#txtContributorType');
		if (conType.val() == ""){
			errorElements.push(conType);
		}
		
		var idenType = propertyEditorDiv.find('#txtIdenType');

		$inputs.each(function(e) {
			var item = $(this);
			var itemValue = item.val();
			var itemName = item.attr('name');

			if (itemName == "txtIdenType"){
				if (itemValue == ""){
					errorElements.push(item);
				}
			}else if(itemName == "txtTitle"){
				if (itemValue == ""){	
					errorElements.push(item);
				}
			}else if(itemName == "txtPublisher"){
				if (itemValue == ""){
					errorElements.push(item);
				}
			}else if(itemName == "txtPublicationYear"){
				if (itemValue == ""){
					errorElements.push(item);
				}else{
					var match = /^\d{4}$/.exec(itemValue);
					if (!match) {
						errorElements.push(item);
					}
				}
			}else if(itemName == "txtVersion"){
				if (itemValue == ""){
					errorElements.push(item);
				}
			}
			
			if (idenType.val() != "doi"){
				if(itemName == "txtIdenValue"){
					if (itemValue == ""){
						errorElements.push(item);
					}
				}
			}
			
			if (conType.val() == "Author/creator"){
				
				if(itemName == "txtFamillyName"){
					if (itemValue == ""){
						errorElements.push(item);
					}
				}
				else if(itemName == "txtGivenName"){
					if (itemValue == ""){
						errorElements.push(item);
					}
				}
			}else if (conType.val() == "Group/research institution"){
				if(itemName == "txtGroupName"){
					if (itemValue == ""){
						errorElements.push(item);
					}
				}
			}
		});
		
		return errorElements;
	}

	// -----------------------------------------------------------------------------------------------
});



/*if (!String.prototype.encodeHTML) {
	  String.prototype.encodeHTML = function () {
	    return this.replace(/&/g, '&amp;')
	               .replace(/</g, '&lt;')
	               .replace(/>/g, '&gt;')
	               .replace(/"/g, '&quot;');
	  };
}

if (!String.prototype.decodeHTML) {
	  String.prototype.decodeHTML = function () {
	    return this.replace(/&quot;/g, '"')
	               .replace(/&gt;/g, '>')
	               .replace(/&lt;/g, '<')
	               .replace(/&amp;/g, '&');
	  };
}

var test = "<note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";
var enc = "&lt;note&gt;&lt;to&gt;Tove&lt;/to&gt;&lt;from&gt;Jani&lt;/from&gt;&lt;heading&gt;Reminder&lt;/heading&gt;&lt;body&gt;Don&apos;t forget me this weekend!&lt;/body&gt;&lt;/note&gt;";
alert(test.encodeHTML());
alert(enc.decodeHTML());*/

