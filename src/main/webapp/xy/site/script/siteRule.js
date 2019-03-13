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
var siteRule_form = {
		init : function() {
			//站点ID赋值
			var siteID = e5_form.getParam("siteID");
			$("#rule_siteID").val(siteID);	
			
			$('#rule_column_dir').attr('readonly','true');
			$('#rule_article_dir').attr('readonly','true');
			$('#rule_photo_dir').attr('readonly','true');
			$('#rule_attach_dir').attr('readonly','true');	
		}
}

$(function(){
	siteRule_form.init();
});