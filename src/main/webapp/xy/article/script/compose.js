//合成多标题使用
var compose_art = {
		op:1, //op为1新建多标题稿件，op为2修改多标题稿件
		type : 1,
		editorcontent : "",
		colDialog : null,//主栏目对话框
		refDialog : null,//关联栏目对话框
		addArtDialog : null,
		isOpenCol : false,//用来标识主栏目对话框是否代开
		isOpenRef : false,//用来标识关联栏目对话框是否打开
		DocIDs : "",//用来保存合成标题的现有稿件，用于判断添加的稿件是否重复，以免造成重复
		currentColID : 0,//用来记录是在主栏目中还是关联栏目中打开多标题
		init : function() {
            var iTop = (window.screen.height-600)/2;
            var iLeft = (window.screen.width-800)/2;
			window.resizeTo(800,600);
            window.moveTo(iLeft,iTop);
            
			compose_art.setContent();
			
			//初始化主栏目的信息
			$("#colFrm").val(colName);
			$("#colFrmId").val(colID);
			
			$('#colSelect').click(compose_art.colSelect);
			$('#refSelect').click(compose_art.refSelect);
			$('#artAdd').click(compose_art.artAdd);
			$('#doSave').click(compose_art.doSave);
			$('#doPub').click(compose_art.doPub);
			$('#doCancel').click(compose_art.doCancel);
			if(compose_art.op == 2){
				$(".frmColumn").hide();
			}

		},
		setContent : function() {
			if (typeof UE == "undefined") return;
			
			var editorName = (compose_art.type == 0) ? "editor" : "simpleEditor";
			
			var editor = UE.getEditor(editorName);
			editor.ready(function() {
				var content = compose_art.editorcontent;
				
				content = content.replace(/&lt;/g, "<");
				content = content.replace(/&gt;/g, ">");
				//content = content.replace(/&quot;/g, "\"");
				content = content.replace(/&#034;/g, "\"");
				content = content.replace(/&nbsp;/g, " ");
				
				editor.setContent(content);
			});
		},
		//读编辑器的内容
		getContent : function() {
			if (typeof UE == "undefined") return "";
			
			var editorName = (compose_art.type == 0) ? "editor" : "simpleEditor";
			
			var editor = UE.getEditor(editorName);
			return editor.getContent();
		},
		//打开主栏目选择对话框
		colSelect: function(){
			var dataUrl = "xy/column/ColumnCheck.jsp?type=" + "radio" + "&siteID="
			+ siteID + "&ids=" + $("#colFrmId").val() + "&ch=" + ch;
			var pos = compose_art._getDialogPos(document.getElementById("colFrm"));
			dataUrl = compose_art.dealUrl(dataUrl);

			compose_art.colDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "400px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			compose_art.colDialog.show();
			compose_art.isOpenCol = true;
		},
		//打开关联栏目选择对话框
		refSelect: function(){
			var dataUrl = "xy/column/ColumnCheck.jsp?type=" + "op" + "&siteID="
			+ siteID + "&ids=" + $("#refFrmId").val() + "&ch=" + ch;
			var pos = compose_art._getDialogPos(document.getElementById("refFrm"));
			dataUrl = compose_art.dealUrl(dataUrl);

			compose_art.refDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "400px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			compose_art.refDialog.show();
			compose_art.isOpenRef = true;
		},
		//栏目选择对话框关闭事件处理
		columnClose : function(colIds,colNames){
			if(compose_art.isOpenCol){
				$("#colFrm").val(colNames);
				$("#colFrmId").val(colIds);
				compose_art.colDialog.close();
				compose_art.isOpenCol = false;
			}
			if(compose_art.isOpenRef){
				$("#refFrm").val(colNames);
				$("#refFrmId").val(colIds);
				compose_art.refDialog.close();
				compose_art.isOpenRef = false;
			}
			
		},
		//栏目选择对话框关闭事件处理
		columnCancel : function(){
			if(compose_art.isOpenCol){
				compose_art.colDialog.close();
				compose_art.isOpenCol = false;
			}
			if(compose_art.isOpenRef){
				compose_art.refDialog.close();
				compose_art.isOpenRef = false;
			}

		},
		getArticleSuccess : function(opnions){
			var content = compose_art.getContent();
			content = content + opnions;
			compose_art.editorcontent = content;
			compose_art.setContent();
		},
		//打开添加稿件的对话框
		artAdd : function(){
			var dataUrl = "xy/MainArticle.do?siteID=" + siteID + "&colID="
					+ colID + "&type=" + 0 + "&ch=" + ch;
			// 顶点位置
			var pos = {left : "0px",top : "30px",width : "800px",height : "450px"};
			dataUrl = compose_art.dealUrl(dataUrl);

			compose_art.addArtDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "800px",
				height : "450px",
				pos : pos,
				resizable : false
			});
			compose_art.addArtDialog.show();
		},
		//获取对话框显示的位置
		_getDialogPos : function(el) {
			function Pos (x, y) {
				this.x = x;
				this.y = y;
			}
			function getPos(el) {
				var r = new Pos(el.offsetLeft, el.offsetTop);
				if (el.offsetParent) {
					var tmp = getPos(el.offsetParent);
					r.x += tmp.x;
					r.y += tmp.y;
				}
				return r;
			}
			var p = getPos(el);
			
			//决定弹出窗口的高度和宽度
			var dWidth = 400;
			var dHeight = 300;

			var sWidth = document.body.clientWidth; //窗口的宽和高
			var sHeight = document.body.clientHeight;
			
			if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
			if (dHeight + 30 > sHeight) dHeight = sHeight - 30;
			
			//顶点位置
			var pos = {left : p.x +"px", 
				top : (p.y + el.offsetHeight - 1)+"px",
				width : dWidth,
				height : dHeight
				};
			if (pos.left + dWidth > sWidth)
				pos.left = sWidth - dWidth;
			if (pos.top + dHeight > sHeight)
				pos.top = sHeight - dHeight;
			
			return pos;
		},
			
		//处理Url的路径深度
		dealUrl : function(url) {
			var pathPrefix = "../../";
			return pathPrefix + url;
		},
		getNewDocID : function(){
			var newdocID = 0;
			$.ajax({				
				url : "../../xy/article/getNewDocID.do",
				type : 'POST',
				data : {
					"DocLibID" : DocLibID
				},
				dataType : 'html',
				async : false,
				success:function(msg, status){	
				if (status == "success") {
					if (msg.substr(0, 7) == "success") {//保存成功
						newdocID = msg.substr(7);
					} 
				} 
			}});
			return newdocID;
		},
		//处理保存按钮
		doSave : function(){
			var isTransfer = false;
			
			var newdocID = 0;
			if(compose_art.op == 1){
				newdocID = compose_art.getNewDocID();
			}else {
				newdocID = compose_art.DocIDs;
			}
			
			if(newdocID==0){
				alert("获取稿件ID发生未知错误");
				return;
			}
			var content = compose_art.replaceTag(compose_art.getContent());
			if(content == ''){
				alert("标题不应为空");
				return;
			}
			$.ajax({			
				url : "../../xy/article/dealComposeArt.do",
				type : 'POST',
				data : {
					"linkTitle" : $.trim(content),
					"siteID" : siteID,
					"docIDs" : newdocID,
					"isTransfer" : isTransfer,
					"DocLibID" : DocLibID,
					"mainColId" : $("#colFrmId").val(),
					"mainColName" : $("#colFrm").val(),
					"refColIds" : $("#refFrmId").val(),
					"refColNames" : $("#refFrm").val(),
					"currentColID" : compose_art.currentColID
				},
				dataType : 'html',
				success  : function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//保存成功
							compose_art.saveSuccess(msg.substr(7),newdocID);
						} else {
							alert(msg);
						}
					} 
			}});
		},
		//处理保发布按钮
		doPub : function(){
			var isTransfer = true;
			
			var newdocID = 0;
			if(compose_art.op == 1){
				newdocID = compose_art.getNewDocID();
			}else {
				newdocID = compose_art.DocIDs;
			}
			
			if(newdocID==0){
				alert("获取稿件ID发生未知错误");
				return;
			}
			var content = compose_art.replaceTag(compose_art.getContent());
			if(content == ''){
				alert("标题不应为空");
				return;
			}
			$.ajax({			
				url : "../../xy/article/dealComposeArt.do",
				type : 'POST',
				data : {
					"linkTitle" : $.trim(content),
					"siteID" : siteID,
					"docIDs" : newdocID,
					"isTransfer" : isTransfer,
					"DocLibID" : DocLibID,
					"mainColId" : $("#colFrmId").val(),
					"mainColName" : $("#colFrm").val(),
					"refColIds" : $("#refFrmId").val(),
					"refColNames" : $("#refFrm").val(),
					"currentColID" : compose_art.currentColID
				},
				dataType : 'html',
				success:function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//发布成功
							compose_art.pubSuccess(msg.substr(7),newdocID);
						} else {
							alert(msg);
						}
					} 
			}});
		},
		//处理取消按钮
		doCancel : function(){
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmCompose").attr("src", url);
		},
		saveSuccess : function(opnions,newdocID){
			var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + newdocID
			+ "&DocLibID=" + DocLibID +"&Opinion="
			+ encodeU(opnions);
			$("#frmCompose").attr("src", url);
		},
		pubSuccess : function(opnions,newdocID){
			var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + newdocID
			+ "&DocLibID=" + DocLibID +"&Opinion="
			+ encodeU(opnions);
			$("#frmCompose").attr("src", url);
		},
		replaceTag : function(content){
			content = content.replace("<p>","");
			content = content.replace("</p>","");
			content = content.replace("<P>","");
			content = content.replace("</P>","");
			return content;
		}
		
};
$(function() {
	compose_art.init();
});

//处理添加稿件对话框的关闭事件
function articleCancel(){
	compose_art.addArtDialog.close();	
}

//处理添加稿件对话框的关闭事件
function articleClose(docLibID,docIDs){
	//在此需要判断添加的稿件是否已经存在，暂时没有好的办法，待主体功能晚上后在想办法处理
	var theURL = "../../xy/article/getArtLink.do?DocIDs=" + docIDs
					+ "&DocLibID=" + docLibID;

	$.ajax({url:theURL, async:false, success:function(msg, status){	
		if (status == "success") {
			if (msg.substr(0, 7) == "success") {//推送成功
				compose_art.getArticleSuccess(msg.substr(7));
			} else {
				alert(msg);
			}
		} 
	}});
	compose_art.addArtDialog.close();	
}
/** 对特殊字符和中文编码 */
function encodeU(param1) {
	if (!param1)
		return "";
	var res = "";
	for ( var i = 0; i < param1.length; i++) {
		switch (param1.charCodeAt(i)) {
		case 0x20://space
		case 0x3f://?
		case 0x23://#
		case 0x26://&
		case 0x22://"
		case 0x27://'
		case 0x2a://*
		case 0x3d://=
		case 0x5c:// \
		case 0x2f:// /
		case 0x2e:// .
		case 0x25:// .
			res += escape(param1.charAt(i));
			break;
		case 0x2b:
			res += "%2b";
			break;
		default:
			res += encodeURI(param1.charAt(i));
			break;
		}
	}
	return res;
}