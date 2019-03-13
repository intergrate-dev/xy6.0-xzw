//给定制的表单自动添加分组ID
var group_form = {
	init : function() {
		var field = e5_form.getParam("groupField");
		var groupID = e5_form.getParam("groupID");
		$("#" + field).val(groupID);
		
		var field = e5_form.getParam("siteField");
		var siteID = e5_form.getParam("siteID");
		$("#" + field).val(siteID);
	}
}
//查是否重名时，增加站点、组条件
e5_form._duplicateUrl = function(field) {
	var siteID = e5_form.getParam("siteID");
	var groupID = e5_form.getParam("groupID");
	
	var theURL = "xy/Duplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&siteID=" + siteID
		+ "&value=" + e5_form.encode(field.val());
	if (groupID)
		theURL += "&groupID=" + groupID;
	
	return theURL;
}
$(function(){
	group_form.init();
});