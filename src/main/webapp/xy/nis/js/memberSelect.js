//会员选择
var xy_memSelect = {
	dialog : null,
	
	//选择按钮
	memSelect : function(event) {
		var url = "../../xy/SimpleSelect.do?type=1&siteID=" + article.siteID;
		var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
		xy_memSelect.dialog = e5.dialog({type : "iframe", value : url}, {
			showTitle : true,
			title: "会员",
			width : "1000px",
			height : "500px",
			pos : pos,
			resizable : false
		});
		xy_memSelect.dialog.show();
	},
	//选择窗口：选定后
	groupSelectOK: function(docLibID, docID) {
		xy_memSelect.dialog.close();
		$.get("../../xy/nis/findMember.do", {docLibID:docLibID, docIDs:docID}, function(data){
			if (!data[0].mNickname) {
				alert("所选人没有昵称！");
				return;
			}
			$("#a_answererID").val(data[0].mID) ; //会员ID
			$("#a_answerer").val(data[0].mNickname) ; //会员昵称
			$("#a_answererIcon").val(data[0].mHead) ; //会员头像地址
		});
	},
	//选择窗口：取消后
	groupSelectCancel : function() {
		xy_memSelect.dialog.close();
	}
}
//回调函数
function groupSelectOK(docLibID, docID){
	xy_memSelect.groupSelectOK(docLibID, docID);
}

function groupSelectCancel(){
	xy_memSelect.groupSelectCancel();
}
