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
