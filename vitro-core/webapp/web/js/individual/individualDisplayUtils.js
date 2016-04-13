$(document).ready(function() {
	var cnt = 0;
	$('.sptialStatContainerSelector').each(function(e) {
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
			}
		}else if (spatialCoverageType == "kmlPolyCoords"){
			$(this).html("");
			$(this).ands_location_widget('init', {lonLat:spatialCoverageValue, target:"geoLocation"+cnt});
			$(this).find('.alw_toolbar').hide();
		}else if (spatialCoverageType == "text"){
			var textStr = spatialCoverageValue;
			$(this).html(textStr);
		}else{
		}
		cnt++;
	});
  });
