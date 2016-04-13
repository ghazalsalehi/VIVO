$(function(){
		
	$('#Draft').click(function(e){
		currentModule = "Draft";
		$(this).addClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryDraftRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#Draft').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
		}, "json");
	});
	
	$('#UnderReview').click(function(e){
		currentModule = "UnderReview";
		$(this).addClass("selected");
		$('#Draft').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryUnderReviewRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#UnderReview').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
		}, "json");
	});
	
	$('#PublishedOpenAccess').click(function(e){
		currentModule = "PublishedOpenAccess";
		$(this).addClass("selected");
		$('#Draft').removeClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryPublishedOpenAccessRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#PublishedOpenAccess').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
			
		}, "json");
	});	

	$('#PublishedQUTAccess').click(function(e){
		currentModule = "PublishedQUTAccess";
		$(this).addClass("selected");
		$('#Draft').removeClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryPublishedQUTAccessRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#PublishedQUTAccess').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
			
		}, "json");
	});	
	
	$('#ReadyToReview').click(function(e){
		currentModule = "ReadyToReview";
		$(this).addClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		$('#Draft').removeClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryReadyToReviewRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#ReadyToReview').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
			
		}, "json");
	});
	
	$('#BeingReviewd').click(function(e){
		currentModule = "BeingReviewd";
		$(this).addClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		$('#Draft').removeClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryBeingReviewedByRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#BeingReviewd').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
			
		}, "json");
	});

	$('#PublishedByMe').click(function(e){
		currentModule = "PublishedByMe";
		$(this).addClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#AssignedByMe').removeClass("selected");
		$('#Draft').removeClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryPublishedByRecords", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#PublishedByMe').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
			
		}, "json");
	});
	
	$('#AssignedByMe').click(function(e){
		currentModule = "AssignedByMe";
		$(this).addClass("selected");
		$('#ReadyToReview').removeClass("selected");
		$('#BeingReviewd').removeClass("selected");
		$('#PublishedByMe').removeClass("selected");
		$('#Draft').removeClass("selected");
		$('#UnderReview').removeClass("selected");
		$('#PublishedOpenAccess').removeClass("selected");
		$('#PublishedQUTAccess').removeClass("selected");
		
		$.post( "manageRecords?module=workspace&action=queryAssignedByAdmin", { page: pageNo }, function( data ) {
			$('#individuals-list').html(data.html);
			$('.individuals-heading').html(data.individuals_heading);
			$('#AssignedByMe').find('.count-classes').html('(' + data.recordsCount + ')');
			
			doPagination(pageNo, data.recordsCount);
			
		}, "json");
	});
	
	//---------------------------------------------------------------------------------------- Take this from a file.
	var VClassURIName = $('#VClassURI').val();
	if (VClassURIName == "http://www.qut.edu.au/ontologies/vivoqut#researchDataSet"){
		var des = "A collection of physical or digital objects generated by research activities. Example: <a href='http://researchdata.ands.org.au/coral-community-data-of-coastal-fringing-reefs-pilot-study' target='_blank'>Coral community data</a>";
		$( "#effect p" ).html(des);
	}
	//----------------------------------------------------------------------------------------
	
	balloon = new RDFBalloon({id:"assignToReviewBalloon", width:470, ok: okFuncAssignToReview, cancel: cancelFunc});//target:".test",
	balloonNotifyUserAndSendBack = new RDFBalloon({id:"assignToReviewBalloon", width:470, ok: okNotifyUserAndSendBack, cancel: cancelFuncNotifyUserAndSendBack});//target:".test",
	$( "#Draft" ).trigger("click");
});


function doPagination(requestedPageNo, recordsCount) {
	var maxRecodsPerPage = 10;
	var remainder = recordsCount % maxRecodsPerPage;
	
	var pageNumbers = 0;
	if (remainder > 0){
		pageNumbers = (recordsCount - remainder)/maxRecodsPerPage;
		pageNumbers++;
	}else{
		pageNumbers = recordsCount/maxRecodsPerPage;
	}

	var maxPageCount = 15;

	if (pageNumbers > 1){
		var paginationHtml = "<ul>";
		var startingPageNo = 1;
		if (requestedPageNo > maxPageCount){ //ex: when user hit the next button,requestedPageNo:31,32,33
			startingPageNo = requestedPageNo - maxPageCount + 1;
			paginationHtml += "<li class='round' role='listitem'><a href='#' title='View more results' data-page='" + (requestedPageNo - maxPageCount)  + "'>Previous</a></li>";
		}
		
		for (var  i = startingPageNo ; i <= pageNumbers; i++ ){
			
			if (i == maxPageCount + startingPageNo){	// Next
				paginationHtml += "<li class='round' role='listitem'><a href='#' title='View more results' data-page='" + i + "'>Next</a></li>";
				break;
			}
			
			if (i == requestedPageNo){
				paginationHtml += "<li class='round selected' role='listitem'>" + i + "</li>";
			}else{
				paginationHtml += "<li class='round' role='listitem'><a href='#' title='View page " + i + " of the results' data-page='" + i + "'>" + i + "</a></li>";
			}
		}

		paginationHtml += "</ul>";
		
		$( ".mrs-pagination" ).show();
		$( ".mrs-pagination" ).html(paginationHtml);

		$('.mrs-pagination').find('a').click(function(e){
			pageNo = $(this).data("page");
			$( "#" + currentModule ).trigger("click");
		});
	}else{
		$( ".mrs-pagination" ).html('');
		$( ".mrs-pagination" ).hide();
	}
	
	pageNo = 1;
}

/*
	$.growl({ title: "Growl", message: "The kitten is awake!" });
	$.growl.error({ message: "The kitten is attacking!" });
	$.growl.notice({ message: "The kitten is cute!" });
	$.growl.warning({ message: "The kitten is ugly!" });
*/

//currentModule:Draft
//currentModule:UnderReview
//currentModule:PublishedOpenAccess
//currentModule:PublishedQUTAccess

//currentModule:ReadyToReview
//currentModule:BeingReviewd
//currentModule:PublishedByMe
//currentModule:AssignedByMe
var pageNo = 1;
var balloon;
var currentModule = "Draft";
var deleteRecord=function(e){
	Messi.ask('Are you SURE you want to delete this record? If in doubt, CANCEL.', function(val) { 	
		if (val === 'Y') {
			var parent = $(e).parents('.individual');
			var individualLocalName = parent.find('.individualLocalName').html();
			
			//current page number.
			pageNo = 1;
			var selectedPageNo = $('.mrs-pagination').find('li.selected').html();
			if (selectedPageNo){
				pageNo = selectedPageNo;
			}

			var subActionStr = "queryDraftRecords";
			if (currentModule == "Draft"){
				subActionStr = "queryDraftRecords";
			}else if (currentModule == "UnderReview"){
				subActionStr = "queryUnderReviewRecords";
			}else if (currentModule == "PublishedOpenAccess"){
				subActionStr = "queryPublishedOpenAccessRecords";
			}else if (currentModule == "PublishedQUTAccess"){
				subActionStr = "queryPublishedQUTAcessRecords";
			}
			var child = $(e).children("div");
			var currentClassName = child.attr('class');
			child.removeClass();
			child.html('<img src="/edit/forms/qut_libs/common/images/ajax-loader-circle.gif" style="margin:1px;">');
			child.css('background-image', 'none');
			parent.addClass("setColourDeleteRecord");
			$.post( "manageRecords?module=workspace&action=delete&recordsState=" + currentModule, { page: pageNo, key:individualLocalName }, function( data ) {
				var errorCode = data.MRS_ERROR_CODE;
				if (errorCode == "MRS_001"){	//success.
					parent.slideToggle(500, function() {
						$(this).remove();
						$('#individuals-list').html(data.html);
						$('#individuals-list li:last').addClass("setColourNextAvailableRecord");
						$('#individuals-list li:last').hide().slideToggle(1000, function(){
							$(this).removeClass("setColourNextAvailableRecord");
						});
						
						var recordCount = data.recordsCount;

						if (currentModule == "Draft"){$('#Draft').find('.count-classes').html('(' + recordCount + ')');}
						else if (currentModule == "UnderReview"){$('#UnderReview').find('.count-classes').html('(' + recordCount + ')');}
						else if (currentModule == "PublishedOpenAccess"){$('#PublishedOpenAccess').find('.count-classes').html('(' + recordCount + ')');}
						else if (currentModule == "PublishedQUTAccess"){$('#PublishedQUTAccess').find('.count-classes').html('(' + recordCount + ')');}
					});
					
					// Pagination
					if (data.individualCountsInPage === 0){
						if (pageNo > 1){
							pageNo = --pageNo;
							$( "#" + currentModule ).trigger("click");
						}
					}else{
						doPagination(pageNo, data.recordsCount);
					}
					
					$.growl.notice({ title: "", message: "Successfully deleted the record." });
				}else if (errorCode == "MRS_002"){	//failed
					child.addClass(currentClassName);
					child.html('');
					child.css('background-image', '');
					parent.removeClass("setColourDeleteRecord");
					$.growl.error({ title: "", message: data.MRS_ERROR_MSG});
				}
			}, "json");
			
			pageNo = 1;
			
			$('.myElement').smallipop();
		}
	});
}

//nextRecordsState:Draft
//nextRecordsState:UnderReview
//nextRecordsState:PublishedOpenAccess
//nextRecordsState:PublishedQUTAccess

var moveRecordToNextLevel=function(e, nextRecordsState){
	Messi.ask('Are you SURE you want to move this record to the next level? If in doubt, CANCEL.', function(val) { 
		if (val==='Y') {
			var parent = $(e).parents('.individual');
			var individualLocalName = parent.find('.individualLocalName').html();
			
			//current page number.
			pageNo = 1;
			var selectedPageNo = $('.mrs-pagination').find('li.selected').html();
			if (selectedPageNo){
				pageNo = selectedPageNo;
			}
			
			var child = $(e).children("div");
			var currentClassName = child.attr('class');
			child.removeClass();
			child.html('<img src="/edit/forms/qut_libs/common/images/ajax-loader-circle.gif" style="margin:1px;">');
			child.css('background-image', 'none');
			parent.addClass("addGreenColour");
			$.post( "manageRecords?module=workspace&action=moveToNextState&nextRecordsState=" + nextRecordsState + "&currentRecordsState=" + currentModule, { page: pageNo, key:individualLocalName }, function( data ) {
				var errorCode = data.MRS_ERROR_CODE;
				if (errorCode == "MRS_001"){	//success.
					parent.slideToggle(500, function() {
						$(this).remove();
						$('#individuals-list').html(data.html);
						$('#individuals-list li:last').addClass("setColourNextAvailableRecord");
						$('#individuals-list li:last').hide().slideToggle(1000, function(){
							$(this).removeClass("setColourNextAvailableRecord");
						});

						if (currentModule == "Draft"){$('#Draft').find('.count-classes').html('(' + data.recordsCount + ')');}
						else if (currentModule == "UnderReview"){$('#UnderReview').find('.count-classes').html('(' + data.recordsCount + ')');}
						else if (currentModule == "PublishedQUTAccess"){$('#PublishedQUTAccess').find('.count-classes').html('(' + data.recordsCount + ')');}
						else if (currentModule == "PublishedOpenAccess"){$('#PublishedOpenAccess').find('.count-classes').html('(' + data.recordsCount + ')');}
						else if (currentModule == "BeingReviewd"){
							$('#BeingReviewd').find('.count-classes').html('(' + data.recordsCount + ')');
							$('#PublishedByMe').find('.count-classes').html('(' + data.publishedByAdminRecordsCount + ')');
							$('#AssignedByMe').find('.count-classes').html('(' + data.assignedByAdminRecordsCount + ')');
						}
						
						if (nextRecordsState == "Draft"){
							$('#Draft').find('.count-classes').html('(' + data.nextStateRecordsCount + ')');
						}else if (nextRecordsState == "UnderReview"){
							$('#UnderReview').find('.count-classes').html('(' + data.nextStateRecordsCount + ')');
						}else if ((currentModule != "BeingReviewd") && (nextRecordsState == "PublishedQUTAccess")){
							$('#PublishedQUTAccess').find('.count-classes').html('(' + data.nextStateRecordsCount + ')');
						}else if ((currentModule != "BeingReviewd") && (nextRecordsState == "PublishedOpenAccess")){
							$('#PublishedOpenAccess').find('.count-classes').html('(' + data.nextStateRecordsCount + ')');
						}
					});
					
					// Pagination
					if (data.individualCountsInPage === 0){
						if (pageNo > 1){
							pageNo = --pageNo;
							$( "#" + currentModule ).trigger("click");
						}
					}else{
						doPagination(pageNo, data.recordsCount);
					}
		
					if (data.emailErrorMessage){
						$.growl.error({ title: "", message: data.emailErrorMessage });
					}
					$.growl.notice({ title: "", message: "Successfully move this record to the next level." });
				}else if (errorCode == "MRS_002"){	//failed
					child.addClass(currentClassName);
					child.html('');
					$.growl.error({ title: "", message: "Failed to send the record to next level." +  data.MRS_ERROR_MSG});
					parent.removeClass("addGreenColour");
				}
			}, "json");
		}
	});
}

var assignRecordToReview=function(e){	// e == this	
	balloon.showBalloon($(e));
	balloon.showWait();
	
	var parent = $(e).parents('.individual');
	var individualLocalName = parent.find('.individualLocalName').html();
	
	$.post( "manageRecords?module=workspace&action=getAdminAndCuratorList&currentRecordsState=" + currentModule, {key:individualLocalName}, function( data ) {
		balloon.setContent(data.html);
	}, "json");
}

var notifyUserAndSendRecordBack=function(e){	
	balloonNotifyUserAndSendBack.showBalloon($(e));
	balloonNotifyUserAndSendBack.showWait();
	
	var parent = $(e).parents('.individual');
	var individualLocalName = parent.find('.individualLocalName').html();
	
	$.post( "manageRecords?module=workspace&action=getNotifyUserAndSendBackBody", {}, function( data ) {
		balloonNotifyUserAndSendBack.setContent(data.html);
	}, "json");
}

var okFuncAssignToReview = function(target){
	var selectedUserID = balloon.getBaseDiv().find('.assignToReview').val();
	var assignByUsercomments = balloon.getBaseDiv().find('.rdf-balloon-comments').val();
	
	if (selectedUserID == ""){
			Messi.alert('Please select a person form the list.');
	}else{
		var parent = $(target).parents('.individual');
		var individualLocalName = parent.find('.individualLocalName').html();
		
		//current page number.
		pageNo = 1;
		var selectedPageNo = $('.mrs-pagination').find('li.selected').html();
		if (selectedPageNo){
			pageNo = selectedPageNo;
		}

		balloon.hide();
			
		var child = $(target).children("div");
		var currentClassName = child.attr('class');
		child.removeClass();
		child.html('<img src="/edit/forms/qut_libs/common/images/ajax-loader-circle.gif" style="margin:1px;">');
		child.css('background-image', 'none');
		parent.addClass("addGreenColour");
		$.post( "manageRecords?module=workspace&action=assignRecordToReview&currentRecordsState=" + currentModule , { assignForReviewUserID:selectedUserID, comments:assignByUsercomments, page: pageNo, key:individualLocalName }, function( data ) {
			var errorCode = data.MRS_ERROR_CODE;
			if (errorCode == "MRS_001"){
				parent.slideToggle(500, function() {
					$(this).remove();
					$('#individuals-list').html(data.html);
					$('#individuals-list li:last').addClass("setColourNextAvailableRecord");
					$('#individuals-list li:last').hide().slideToggle(1000, function(){
						$(this).removeClass("setColourNextAvailableRecord");
					});
					
					if (currentModule == "Draft"){$('#Draft').find('.count-classes').html('(' + data.recordsCount + ')');}
					
					if (data.underReviewRecordsCount === undefined){}	// only when admin,curator assign someone to review their records goes to the Under review.
					else {
						$('#UnderReview').find('.count-classes').html('(' + data.underReviewRecordsCount + ')');
					}
					
					if (data.readyToReviewRecordsCount === undefined){}	// only when admin assign record to review from New.
					else {
						$('#ReadyToReview').find('.count-classes').html('(' + data.readyToReviewRecordsCount + ')');
					}
					
					$('#BeingReviewd').find('.count-classes').html('(' + data.beingReviewedByAdminRecordsCount + ')');
					$('#AssignedByMe').find('.count-classes').html('(' + data.assignedByAdminRecordsCount + ')');
				});
				
				// Pagination
				if (data.individualCountsInPage === 0){
					if (pageNo > 1){
						pageNo = --pageNo;
						$( "#" + currentModule ).trigger("click");
					}
				}else{
					doPagination(pageNo, data.recordsCount);
				}	
				
				if (data.emailErrorMessage){
					$.growl.error({ title: "", message: data.emailErrorMessage });
				}
				$.growl.notice({ title: "", message: "Successfully assign record to review."});
			}else{	//(errorCode == "MRS_002")
				child.addClass(currentClassName);
				child.html('');
				$.growl.error({ title: "", message: "Failed to assign record for review."});
				parent.removeClass("addGreenColour");
			}
		}, "json");	
		
		pageNo = 1;
	}
}

var okNotifyUserAndSendBack = function(target){
	var notifyUserAndSendBackComments = balloonNotifyUserAndSendBack.getBaseDiv().find('.rdf-balloon-comments').val();

	if (notifyUserAndSendBackComments == ""){
			Messi.alert('Please provide a comment.');
	}else{
		var parent = $(target).parents('.individual');
		var individualLocalName = parent.find('.individualLocalName').html();
		
		//current page number.
		pageNo = 1;
		var selectedPageNo = $('.mrs-pagination').find('li.selected').html();
		if (selectedPageNo){
			pageNo = selectedPageNo;
		}

		balloonNotifyUserAndSendBack.hide();
		var child = $(target).children("div");
		var currentClassName = child.attr('class');
		child.removeClass();
		child.html('<img src="/edit/forms/qut_libs/common/images/ajax-loader-circle.gif" style="margin:1px;">');
		child.css('background-image', 'none');
		parent.addClass("addGreenColour");
		
		$.post( "manageRecords?module=workspace&action=notifyUserAndSendRecordBack&currentRecordsState=" + currentModule , { comments:notifyUserAndSendBackComments, page: pageNo, key:individualLocalName }, function( data ) {
			var errorCode = data.MRS_ERROR_CODE;
			if (errorCode == "MRS_001"){
				parent.slideToggle(500, function() {
					$(this).remove();
					$('#individuals-list').html(data.html);
					$('#individuals-list li:last').addClass("setColourNextAvailableRecord");
					$('#individuals-list li:last').hide().slideToggle(1000, function(){
						$(this).removeClass("setColourNextAvailableRecord");
					});
					
					
					if (data.readyToReviewRecordsCount === undefined){}	// only for admin
					else {
						$('#ReadyToReview').find('.count-classes').html('(' + data.readyToReviewRecordsCount + ')');
					}
						
					$('#BeingReviewd').find('.count-classes').html('(' + data.beingReviewedByAdminRecordsCount + ')');
					$('#AssignedByMe').find('.count-classes').html('(' + data.assignedByAdminRecordsCount + ')');
					$('#Draft').find('.count-classes').html('(' + data.draftRecordsCount + ')');
					$('#UnderReview').find('.count-classes').html('(' + data.underReviewRecordsCount + ')');					
				});
				
				// Pagination
				if (data.individualCountsInPage === 0){
					if (pageNo > 1){
						pageNo = --pageNo;
						$( "#" + currentModule ).trigger("click");
					}
				}else{
					doPagination(pageNo, data.recordsCount);
				}
				
				if (data.emailErrorMessage){
					$.growl.error({ title: "", message: data.emailErrorMessage });
				}
				$.growl.notice({ title: "", message: "Successfully sent this record back to the user."});
				
			}else{	//(errorCode == "MRS_002")
				child.addClass(currentClassName);
				child.html('');
				$.growl.error({ title: "", message: "Failed to send record back to user."});
				parent.removeClass("addGreenColour");
			}
		}, "json");	
		
		pageNo = 1;
	}
}

var cancelFunc = function(target){
	balloon.hide();
}

var cancelFuncNotifyUserAndSendBack = function(target){
	balloonNotifyUserAndSendBack.hide();
}

var runEffect=function(e){
	var description = "";
	var selectedValue = $(e).val();
	if (selectedValue === "http://www.qut.edu.au/ontologies/vivoqut#researcher"){
		description = "A researcher is a human being; or an identity (or role) assumed by one or more human beings. Example: <a href='http://researchdata.ands.org.au/professor-martin-sillence' target='_blank'>Professor Martin Sillence</a>";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#group"){
		description = "A group is one or more persons acting as a family, group, association, partnership, corporation, institution or agency.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#administrativePosition"){
		description = "Administrative position is a kind of party where the position name and contact information are present but the identity of the party filling the role is not specified.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#project"){
		description = "A piece of work that is undertaken or attempted, with a start and end date and defined objectives.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#catalogueOrIndex"){
		description = "A collection of resource descriptions describing the content of one or more repositories or collective works at the item level.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#collection"){
		description = "A compiled content created as separate and independent works and assembled into a collective whole for distribution and use.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#registry"){
		description = "A collection of registry objects compiled to support the business of a given community. Example: <a href='http://researchdata.ands.org.au/emelbourne' target='_blank'>eMelbourne</a>";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#repository"){
		description = "A collection of physical or digital objects compiled for information and documentation purposes and/or for storage and safekeeping.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#researchDataSet"){
		description = "A collection of physical or digital objects generated by research activities. Example: <a href='http://researchdata.ands.org.au/coral-community-data-of-coastal-fringing-reefs-pilot-study' target='_blank'>Coral community data</a>";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#annotate"){
		description = "This links an annotation to a data object, or part thereof. Example: <a href='http://researchdata.ands.org.au/environmental-metabolomics-information-system-emis' target='_blank'>Environmental Metabolomics Information System</a>";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#assemble"){
		description = "This builds a new data object instance composed of existing data objects. A survey generation tool creates a survey form out of user input and templates.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#create"){
		description = "This produces a new data object representing existing phenomena in the world, including physical reality and user input. An instrument creates data.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#generate"){
		description = "This produces a new data object out of mathematical formulae and parameters, rather than capturing and representing existing data in the world.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#harvestOAIPMH"){
		description = "A harvester is a client application that issues an Open Archives Initiative Protocol for Metadata Harvesting (OAI-PMH) requests.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#report"){
		description = "This presents existing data in a summary form. A visualisation reports on data. Example: <a href='http://researchdata.ands.org.au/mquter-environmental-workbench' target='_blank'>MQUTeR Environmental Workbench</a>";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#searchHTTP"){
		description = "Search service over HTTP (websites). Example: <a href='http://researchdata.ands.org.au/classic-texts-in-australian-and-international-taxation-law-website' target='_blank'>Classic Texts in Australian and International Taxation Law Website</a>";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#searchOpenSearch"){
		description = "Open Search search is a collection of technologies that allow publishing of search results in a format suitable for syndication and aggregation. See also Wikipedia";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#searchSRU"){
		description = "SRU search-SRU is a standard XML-focused search protocol for Internet search queries based on Z39.50 semantics.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#searchSRW"){
		description = "Search- SRW (formerly 'SRU via HTTP SOAP’) is being deployed as the search API for the DSpace initiative. It is being considered as the standard search API by a number of communities.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#SearchZ3950"){
		description = "\"Z39.50\" refers to the International Standard, ISO 23950: \"Information Retrieval (Z39.50): Application Service Definition and Protocol Specification\", and to ANSI/NISO Z39.50.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#syndicateATOM"){
		description = "ATOM syndication is an XML-based Web content and metadata syndication format. http://tools.ietf.org/html/rfc4287.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#syndicateRSS"){
		description = "RSS feed is a family of web feed formats that are specified using XML.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#transform"){
		description = "RSS feed is a family of web feed formats that are specified using XML.";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#code"){
		description = "Non-human readable computer instructions eg .war, .jar, .exe, .msi";
	}else if (selectedValue == "http://www.qut.edu.au/ontologies/vivoqut#software"){
		description = "Human-readable computer instructions, e.g text.";
	}
	$( "#effect p" ).html(description);
	$( "#effect" ).css("left",-270);
	$( "#effect" ).animate({left: "0"}, 500);
};






















































