/*
 * jQuery RDFBalloon Plugin 1.0
 * Copyright 2014, Gawri Edussuriya
 */

function RDFBalloon(options) {
	var root = this; // Don't use 'this' to refer RDFBalloon, instead use 'root'
	
	this.vars = {
		id: "balloon",
		background: "yellow",
		width:525
	};
	
	this.baseDiv = null;
	this.elemContent = null;
	this.buttonOk = null;
	this.buttonCancel = null;
	this.arrow = null;

	this.construct = function(options) {
		$.extend(root.vars, options);
		
		//alert(root.vars.id + " " + root.vars.target);
		
		if (root.vars.target) { // If a target is provided set the target
			$(root.vars.target).click(function() {
				root.showBalloon($(this));
			});
		}
	}
	
	this.showBalloon = function(target) {
		if (root.baseDiv == null) { // Create dialog
			var elem = $("<div></div>");
			
			root.baseDiv = elem;
			
			elem.attr("id", root.vars.id);
			elem.addClass("rdfballoon-body");
			root.baseDiv.css("width", root.vars.width);
			
			root.elemContent = $("<div class='rdfballoon-content'></div>");
			root.elemButtonPanel = $("<div class='rdfballoon-row last'></div>");
			root.buttonOk = $("<div class='rdfballoon-button'>OK</div>");
			root.buttonCancel = $("<div class='rdfballoon-button'>Cancel</div>");
			
			root.elemButtonPanel.append(root.buttonCancel);
			root.elemButtonPanel.append(root.buttonOk);
			elem.append(root.elemContent);
			elem.append(root.elemButtonPanel);
			
			root.arrow = $("<div></div>");
			root.arrow.addClass("rdfballoon-balloon-arrow");
			root.arrow.appendTo("body");
			
			elem.appendTo("body");
			elem.hide();
			
			root.buttonOk.click(function(){root.vars.ok(root.currentTarget)});
			root.buttonCancel.click(function(){root.vars.cancel(root.currentTarget)});
			
			$(document).mouseup(function (e)
			{
				if (!root.baseDiv.is(e.target) // if the target of the click isn't the container...
					&& root.baseDiv.has(e.target).length === 0) // ... nor a descendant of the container
				{
					root.hide();
				}
			});
		}
		
		var elem = root.baseDiv;
		
		var offset = target.offset();
		
		// Replace elem.width() by the input width parameter value
		var pageEdgeX = $("#wrapper-content").offset().left + $("#wrapper-content").width();
		var balloonEdgeX = offset.left + (elem.width()/2);//left - width/2 + width
		var ballonX = 0;		
		
		if (pageEdgeX < balloonEdgeX) { // Adjust the balloon if it is near the edge of the page
			balloonX = pageEdgeX - elem.width();
		} else {
			balloonX = offset.left - (elem.width()/2);
		}
				
		elem.offset({ top: offset.top + target.height() + 34, left: balloonX});
		root.arrow.offset({ top: offset.top + target.height() - 4, left: offset.left - (root.arrow.width()/2) + target.height()/2 + 4});
		
		elem.css('visibility', 'visible');
		root.arrow.css('visibility', 'visible');
		root.arrow.slideDown(300);
		elem.slideDown(300);
		
		root.currentTarget = target;
	};
	
	this.showWait = function(){
		root.setContent("<div class='rdfballoon-waiting'><div class='rdfballoon-waiting-img'></div></div>");
		root.elemButtonPanel.hide();
	}
	
	this.setContent = function(content){
		root.elemContent.html(content);
		root.elemButtonPanel.show();
	}
	
	this.getContent = function(){
		return root.elemContent;
	}
	
	this.getBaseDiv = function(){
		return root.baseDiv;
	}
	
	this.hide = function(){
		root.baseDiv.css('visibility', 'hidden');
		root.arrow.css('visibility', 'hidden');
	}
	
	this.construct(options);
}


/* Usage :
$(function() {
	
	var balloon = new RDFBalloon({id:"assignTo",  ok: okFunc, cancel: cancelFunc});//target:".test",
	$('.test').click(function(){
		balloon.showBalloon($(this));
	});
}); */