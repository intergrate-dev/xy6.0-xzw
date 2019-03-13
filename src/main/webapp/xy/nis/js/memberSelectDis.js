//会员选择
var xy_memSelect = {
	dialog : null,
	//专题模板设计按钮
	memSelect : function(event) {
		var url = "../../xy/SimpleSelect.do?type=1";
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
	groupSelectOK: function(docLibID, docIDs) {
		xy_memSelect.dialog.close();
		window.resizeTo(600, 180);
		console.info(docIDs);
		console.info(docLibID);
		$.get("../../xy/nis/FindMember.do", {docLibID:docLibID, docIDs:docIDs}, function(data){
			console.info(data);
			var mID=data[0].mID;
			var mName=data[0].mName;
			for ( var i = 1; i < data.length; i++) {
				mID=mID+","+data[i].mID; //会员ID
				mName=mName+","+data[i].mName ; //会员昵称
				
			}
			
			$("#userID").val(mID) ; //会员头像地址
			$("#userName").val(mName) ; //会员头像地址
		});
	},
	//选择窗口：取消后
	groupSelectCancel : function() {
		xy_memSelect.dialog.close();

		window.resizeTo(600, 180);
	}
}
