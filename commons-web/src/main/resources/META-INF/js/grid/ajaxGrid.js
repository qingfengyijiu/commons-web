(function(factory) {
	"use strict";
	if(typeof define === "function" && define.amd && require) {
		require.config({
			paths : {
				"jquery" : "../external/js/jquery.min",
				"jquery-ui" : "../external/js/jquery-ui.min",
				"jqGrid" : "../external/js/grid/jqgrid/jquery.jqGrid.min"
			}
		});
		define(["jquery", "jquery-ui", "jqGrid"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
"use strict";
//module begin
$.fn.ajaxGrid = function() {
	return this.each(function() {
		var $obj = $(this);
		gridInit($obj);
	});
}
return $;
function gridInit($obj) {
	var url,
		mtype,
		datatype = "json",
		colModel,
		viewrecords,
		width,
		height,
		rowNum = 10,
		pager;
	url = $obj.attr("data-url");
	mtype = $obj.attr("method");
	width = $obj.attr("width");
	height = $obj.attr("height");
	pager = "#" + $obj.attr("pager");
	colModel = new Array();
	$("thead tr:first th", $obj).each(function() {
		var tempColModel = new Object(),
			_this = $(this);
		tempColModel.label = _this.html();
		tempColModel.name = _this.attr("data");
		tempColModel.width = _this.attr("width") != null ? parseInt(_this.attr("width")) : 100;
		tempColModel.key = _this.attr("key") != null ? true : false;
		colModel.push(tempColModel);
	});
	$obj.jqGrid({
		url : url,
		mtype : mtype,
		datatype : datatype,
		colModel : colModel,
		viewrecords : viewrecords,
		width : width,
		height : height,
		rowNum : rowNum,
		pager : pager
	});
}
}))