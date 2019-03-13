//共查找稿件使用
var search_art = {	
		colDialog : null,//主栏目对话框
		type : 0,		//0:多标题合成；1：页面区块内容选取稿件
    	docIDRel : 0,		//查找相关稿件时使用
		init : function() {
			$('#colSelect').click(search_art.colSelect);
			$('#search').click(search_art.Search);
			$('#reset').click(search_art.Reset);
			if(search_art.type == 0 || search_art.type == 2 || search_art.type == 3 || search_art.type == 1 || search_art.type == 4||search_art.type == 6){
				$("#colFrmId").val(colID);
				$("#colFrm").val(colName);
			}
		},
		colSelect: function(){
			//var dataUrl = "../xy/column/ColumnCheck.jsp?type=" + "radio" + "&siteID="
			//+ siteID + "&ids=" + $("#colFrmId").val()+ "&ch=" + ch;
			var dataUrl = "../xy/column/ColumnCheck.jsp?type=op&siteID="+ siteID
			+ "&ids=" + $("#colFrmId").val() + "&ch=" + ch;
			
			var pos = search_art._getDialogPos(document.getElementById("colFrm"));
			
			search_art.colDialog = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "400px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			search_art.colDialog.show();
		},
		columnClose : function(colIds,colNames){
			$("#colFrm").val(colNames);
			$("#colFrmId").val(colIds);
			search_art.colDialog.close();
		},
		columnCancel : function(){
			search_art.colDialog.close();
		},
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
		Search : function() {
			var colId = $("#colFrmId").val();
			var title = $("#title").val();
			var typeselect = $("#typeselect").val();
			var sys_id = $("#sys_id").val();
			
			e5.mods["workspace.search"].searchClick(colId,title,typeselect,sys_id);
		},
		Reset : function(){
            localStorage.removeItem('relateArticle');
			$("#title").val("");
			$("#colFrm").val("");
			$("#colFrmId").val("");
			$("#typeselect").val("");
			$("#sys_id").val("");
		}
};
$(function() {
	search_art.init();
});
