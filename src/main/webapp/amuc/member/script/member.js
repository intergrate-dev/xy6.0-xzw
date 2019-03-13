var member_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#m_siteID").val(siteID);
		$("#mStatus").val(1);
	}
}
$(function(){
	member_form.init();
});