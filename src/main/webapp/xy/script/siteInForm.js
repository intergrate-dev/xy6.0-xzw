//通用的js：表单自动填写siteID、查重时补充siteID条件
var site_in_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#wxa_siteID").val(siteID);
		$("#wba_siteID").val(siteID);
		$("#corp_siteID").val(siteID);
		$("#bm_siteID").val(siteID);
	}
}

//查是否重名时，增加站点条件
e5_form._duplicateUrl = function(field) {
	var siteID = e5_form.getParam("siteID");
	var theURL = "xy/Duplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&siteID=" + siteID
		+ "&value=" + e5_form.encode(field.val());
	return theURL;
}

$(function() {
	site_in_form.init();
});