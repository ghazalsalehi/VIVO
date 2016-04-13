/*
 * jQuery Tabs-Navigation Plugin 1.0
 * Copyright 2015, Gawri Edussuriya
 */

function resetTabs(){
    $("#navigation-content > div").hide(); //Hide all content
    $("#navigation-tabs .current").removeClass('current'); //Reset id's      
}

(function(){
	resetTabs();
    
	// Tab Pagination
	var paginationDiv = $(".navigation-pagination");
	
	//ex url: http://www.localhost.com/display/n3161?displayMode=edit#dataStorage
	// LIBRDF-103
	if(location.hash == "#otherT"){
		var isReadyToGoNext = validateNextStep("#tab1");
		if (isReadyToGoNext){
			$("#navigation-tabs li:nth-child(2)").addClass("current"); // Activate nth tab
		    $("#navigation-content > div:nth-child(2)").fadeIn(); // Show nth tab content
		}else{
			showFirstTab(paginationDiv);
		}
	}else if(location.hash == "#storageT"){	
		var isReadyToGoNext = validateNextStep("#tab1");
		if (isReadyToGoNext){
			$("#navigation-tabs li:nth-child(3)").addClass("current"); // Activate nth tab
		    $("#navigation-content > div:nth-child(3)").fadeIn(); // Show nth tab content
		}else{
			showFirstTab(paginationDiv);
		}
	}else if(location.hash == "#submitT"){
		var isReadyToGoNext = validateNextStep("#tab1");
		if (isReadyToGoNext){
			$("#navigation-tabs li").last().addClass("current"); // Activate last tab
			paginationDiv.find('.next').addClass("disabled");
			paginationDiv.find('.manageRecordAction').removeClass('hide');
			paginationDiv.find('.next').addClass('hide');
		    $("#navigation-content > div:nth-child(4)").fadeIn(); // Show nth tab content
		}else{
			showFirstTab(paginationDiv);
		}
	}else if(location.hash == "#mandatoryT"){
		showFirstTab(paginationDiv);
	}else{
		showFirstTab(paginationDiv);
	}
	
    /* Enable click event if need.
	$("#navigation-tabs a").click(function(e) { 
        e.preventDefault();
        if ($(this).parent().hasClass("current")){ //detection for current tab
         return;      
        }
        else{             
			resetTabs();
			$(this).parent().addClass("current"); // Activate this
			$($(this).attr('name')).fadeIn(); // Show content for current tab
        }
    });*/
	
	paginationDiv.find('.next').click(function(e){
		var currentTab = $("#navigation-tabs .current");
		var isReadyToGoNext = true;
		
		if (currentTab.find("a").attr("name") == "#tab3"){
			var itemEditor = $("#RDRDataUpload-editor");
			
			var propertyListContainer = itemEditor.parent( ".property-list" );
			var listItem = propertyListContainer.find("li[role='listitem']");

			if (listItem.length != 0){	// if RDRDataUpload has a package.
				var packageID = (listItem.find('#packageID').text()).trim();
				
				// Check whether the package has at least one data file attached to it.
				$.post("/systemIntegration?module=rdr&action=getPackageResourceCount", {packageID:packageID },  function(data) {
					var errorCode = data.SYSINT_ERROR_CODE;
					if (errorCode == "SYSINT_001"){	//success.
						var responseValue = data.SYSINT_ERROR_MSG;
						var resourceCount = responseValue.PACKAGE_RESOURCE_COUNT;
						
						if (parseInt(resourceCount) === 0){
							Messi.ask('You have not uploaded any data. The dataset URL will be deleted if you proceed to the next screen. Do you wish to continue?', function(val) { 
								if (val==='Y') {
									var postRequestStr = listItem.find("a.delete-RDRDataUpload").attr("href");
									$.post( postRequestStr, function( data ) {
										var confirmMsgForm = $(data);
										$.post("/systemIntegration?module=rdr&action=deleteRDRPackage", {packageID:packageID }, function(data) { // Delete from RDR
											var errorCode = data.SYSINT_ERROR_CODE;
											if (errorCode == "SYSINT_001"){	//success.
												var action = confirmMsgForm.attr('action');
												
												listItem.slideUp(500, function(e){
													listItem.remove();
													
													var propertyList = itemEditor.parent(".property-list");
													if (propertyList.find("li[role='listitem']").length == 0){	// if empty
														propertyList.parent(".property").find("h3").find("a").trigger( "click" );
													}
												});
												
												$.post( action, confirmMsgForm.serialize());	// Delete from RDF
												goNext(isReadyToGoNext, currentTab, "");
											}else{
												$.growl.notice({ title: "", message: data.SYSINT_ERROR_MSG});
											}
										}, "json");
									});
									
								}else if(val==='N') {
									var RDRDataUploadURL = listItem.find("a.upload-RDRDataUpload").attr("href");
									location.href = RDRDataUploadURL;
								}
							});
						}else{
							goNext(isReadyToGoNext, currentTab, "");
						}
					}else{
						$.growl.notice({ title: "", message: data.SYSINT_ERROR_MSG});
					}
				}, "json");
			}else{
				goNext(isReadyToGoNext, currentTab, "");
			}
		}else{
			if (currentTab.find("a").attr("name") == "#tab1"){
				isReadyToGoNext = validateNextStep("#tab1");
			}
			
			var errMsg = "Please complete the mandatory fields.";
			goNext(isReadyToGoNext, currentTab, errMsg);
		}
		e.preventDefault();
	});
	
	function goNext(isReadyToGoNext, currentTab, errMsg){
		if (isReadyToGoNext){
			if (currentTab.next().length > 0){
				var nextTab = currentTab.next();
				resetTabs();
				nextTab.addClass("current");
				$(nextTab.find('a').attr('name')).fadeIn(); // Show content for current tab
				
				setWindowLocationHash(nextTab);
		
				paginationDiv.find('.previous').removeClass("disabled");
					
				if (nextTab.next().length == 0){ // Check for last item
					paginationDiv.find('.next').addClass("disabled");
					paginationDiv.find('.manageRecordAction').removeClass('hide');
					paginationDiv.find('.next').addClass('hide');
				}else{
					paginationDiv.find('.manageRecordAction').addClass('hide');
				}				
			}
		}else{
			new Messi(errMsg, {title: 'Error', titleClass: 'anim error', buttons: [{id: 0, label: 'Close', val: 'X'}]});
		}
	}
	
	paginationDiv.find('.previous').click(function(e){
		var currentTab = $("#navigation-tabs .current");
		if (currentTab.prev().length > 0){
			var prevTab = currentTab.prev();
			resetTabs();
			prevTab.addClass("current");
			$(prevTab.find('a').attr('name')).fadeIn(); // Show content for current tab
			
			setWindowLocationHash(prevTab);
			
			paginationDiv.find('.next').removeClass("disabled");
			paginationDiv.find('.next').removeClass('hide');
			
			if (prevTab.prev().length == 0){ // Check for last item
				paginationDiv.find('.previous').addClass("disabled");
			}else{
				paginationDiv.find('.manageRecordAction').addClass('hide');
			}
		}
	});	
	
	function validateNextStep(pageID){
		var errors = [];
		var page = $("#navigation-content").find(pageID);

		// LIBRDF-48 TO BE REMOVED AFTER DATA MIGRATION - RIF-CS LITE
		if (userEmailAdress == "researchdatafinder@qut.edu.au" || userEmailAdress == "gawri.edussuriya@qut.edu.au" || userEmailAdress == "p.frame@qut.edu.au" || userEmailAdress == "jodie.vaughan@qut.edu.au"){
			return true;
		}
		
		page.find("article").each(function(e){
			var propertyItem = $(this).find(".property-list").find("li[role=listitem]");
			if (propertyItem.length === 0){
				errors.push(propertyItem);
			}
		});
		
		if (errors.length > 0){return false;}
		else{return true;}
	}
	
	function showFirstTab(paginationDiv){
		$("#navigation-tabs li:first").addClass("current"); // Activate first tab
	    $("#navigation-content > div:first").fadeIn(); // Show first tab content
	 
		paginationDiv.find('.previous').addClass("disabled");
	}
	
	function setWindowLocationHash(tab){
		if (tab.find('a').attr('name') == "#tab2"){
			window.location.hash = "#otherT";
		}else if (tab.find('a').attr('name') == "#tab3"){
			window.location.hash = "#storageT";
		}else if (tab.find('a').attr('name') == "#tab4"){
			window.location.hash = "#submitT";
		}else if (tab.find('a').attr('name') == "#tab1"){
			window.location.hash = "mandatoryT";
		}
	}
	
})()


/* Example Usage :
<div id="navigation-tabs">
	<ul>
		<li><div class="arrow-left"></div><a href="#" name="#tab1">1. Mandatory</a><div class="arrow-right"></div></li>
		<li><div class="arrow-left"></div><a href="#" name="#tab2">2. Optional</a><div class="arrow-right"></div></li>
		<li><div class="arrow-left"></div><a href="#" name="#tab3">3. Other</a><div class="arrow-right"></div></li>
	</ul>
</div>
<div id="navigation-content">
	<div id="tab1" style="display: block;">
	</div>
	<div id="tab2" style="display: none;">
	</div>
	<div id="tab3" style="display: none;">
	</div>
</div>
<div id="navigation-pagination">
	<div class="next">Next</div>
	<div class="previous">Previous</div>
</div>
*/