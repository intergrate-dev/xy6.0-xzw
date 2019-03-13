var ruleSelect_form = {
		dialog : null,
		inputid : null,//显示发布路径的那个input的ID,为了显示路径  例如:页面区块的    b_dir
		dirID : null,//例如:页面区块的    b_dir_ID
		init : function() {
			$(".btnAdded0").click(ruleSelect_form.save);//选择按钮点击事件
		},
		save : function(evt) {
			var siteID = e5_form.getParam("siteID");
			if (!siteID) siteID = 1;
			var inputid = $(evt.target).attr("inputid");
			var url = "../../xy/site/DomaindirSelect.jsp?siteID=" + siteID + "&inputid=" + inputid;
			
			ruleSelect_form.inputid = document.getElementById(inputid);
			var inputID = inputid + "_ID";
			ruleSelect_form.dirID= document.getElementById(inputID);
			var pos = e5_form.event._getDialogPos(document.getElementById(inputid));
			
			ruleSelect_form.dialog = e5.dialog({type:"iframe", value:url}, 
					{title:"选择目录", width:"400px", height:"450px", pos:pos,resizable:false});
			ruleSelect_form.dialog.show();
			
			//隐藏input目录框上的验证信息
			$("#" + inputid).validationEngine("hide");
		}
}

$(function(){
	ruleSelect_form.init();
});