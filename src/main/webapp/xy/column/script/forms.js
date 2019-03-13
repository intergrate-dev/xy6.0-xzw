var col_forms = {
	init : function() {
		$("li").click(col_forms.skip);
		$("li").first().click();
	},
	skip : function(evt) {
		var type = $(evt.target).attr("type");
		
		var url = "about:blank";
		if (type == "0") {
			url = "../../e5workspace/manoeuvre/Form.do?code=formColumn"
					+ "&DocLibID=" + libID
					+ "&DocIDs=" + colID
					+ "&siteID=" + siteID;
		} else if (type == "1") {
			url = "../../e5workspace/manoeuvre/Form.do?code=formColumnExt"
					+ "&DocLibID=" + libID
					+ "&DocIDs=" + colID
					+ "&siteID=" + siteID;
		}
		window.frames["frmBase"].location.href = url;
	}
}
$(function(){
	col_forms.init();
});