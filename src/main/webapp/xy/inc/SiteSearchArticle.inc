<style>
	input{
		font-size: 12px;
	}
	#title,#sys_id{	
		font-size: 12px; 
		color:#666; 
		width:110px;
		margin-top: 10px;
		height: 21px;
  		padding: 0;
  		border-radius:0px;
  		padding-left: 10px;	
	}
	#siteSelect{
		font-family:'微软雅黑';
		font-size: 12px; 
		color:#666;
		width:80px; 
		border-radius:2px;
		margin-top: 10px;
		padding: 0;
  		height: 23px;
	}
	.colFrm{
		width:100px;
		font-size:12px;
		margin-top: 10px;
	}
	#colFrmId{
		display:none;
	}
	#colFrm{
		  padding: 0;
		  width:100px;
  		  height: 21px;
          margin-top: 10px;
  		  border-radius: 2px;
  		  font-size: 12px;
		  padding-left: 8px;
		  color: #646464;
		}
	.btngroup{
		float: right;
    	margin-right: 8px;
	}
</style>
<script>
var search_art = {	
	colDialog : null,
	init : function() {
		$('#colSelect').click(search_art.colSelect);
		$('#search').click(search_art.Search);
		$('#reset').click(search_art.Reset);
		$("#colFrmId").val(colID);
		$("#colFrm").val(colName);
		$("#title").keydown(function(event){
			if(event.keyCode == 13){
				search_art.Search();
			}
		});
		$('#siteSelect').change(function(){
		    $("#originalSiteID").val($("#siteSelect").val());
			search_art.Reset();
			search_art.Search();
		});
	},
	colSelect: function(){
		var dataUrl = "../xy/column/ColumnCheck.jsp?type=op&ids=" + $("#colFrmId").val() + "&ch=" + ch+ "&siteID=" + $("#siteSelect").val();
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
		var sys_id = $("#sys_id").val();
		e5.mods["workspace.search"].searchClick(colId,title,sys_id);
	},
	Reset : function(){
		$("#title").val("");
		$("#colFrm").val("");
		$("#colFrmId").val("");
		$("#sys_id").val("");
	}
};
$(function() {
	search_art.init();
});
e5.mod("workspace.search",function() {
	var api;
	var searchClick = function(colId, title, sys_id) {
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
			
		var rule = "";
		if (sys_id) rule += "_AND_sys_documentID_EQ_" + sys_id;
		//选择栏目
		if (colId){
			var array = new Array();
			array = colId.split(",");
			if(array.length==1){
				rule += "_AND_a_columnID_EQ_" + colId;
			}else{
				rule += "_AND_a_columnID in (";
				for(var i=0;i<array.length;i++){
					rule += array[i];
					if(i!=array.length-1){
						rule += ",";
					}else{
						rule += ")";
					}
				}
			}	
		} 
		rule += "_AND_a_type_EQ_0_AND_a_siteID_EQ_" + $("#siteSelect").val();
		searchparam.ruleFormula = rule.substring(5);
		
		//标题
		if (title != ""){
			searchparam.query = "@QUERYCODE@=qArticle"+"&" + "SYS_TOPIC" + "=" + e5.utils.encodeSpecialCode(title);
		}
		api.broadcast("searchTopic", searchparam);
	};
	var checkLoad = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var ready = !!statusReady&&statusReady();
		if (!ready) {
			setTimeout(checkLoad, 100);
			return;
		}
		
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
			
		api.broadcast("searchTopic", searchparam);
	}
	
	var init = function(sandbox) {
		api = sandbox;
	}
	var onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload: onload,
		searchClick : searchClick
	}
},{requires:[
"../e5workspace/script/Param.js",
"../e5script/e5.utils.js"
]});
</script>
<%@ page pageEncoding="UTF-8"%>
<div id="searchBtnArea">
	<div class="div">
		 站点：<select id="siteSelect" name="siteSelect">
				<c:forEach  var="site" items="${sites}">
					<option value='${site.id}' ${site.id==siteID?'selected':''}>${site.name}</option>
				</c:forEach>
			 </select>
		 栏目：  <input id="colFrm" type="text" readonly="true"/> 
		<input id="colFrmId" type="text" /> 
		<input type='button' id="colSelect" value='...' title="选择栏目"/>
		<input id="title" type="text" size="5" title="请输入标题、关键字等" placeholder="关键字查询"/>
		<input id="sys_id" type="text" size="5" title="请输入稿件ID" placeholder="稿件ID查询"/>
		
		<input type='button' id="search" value='查找'/>
		<input type='button' id="reset" value='清空'/>
		<span class="btngroup">
			<input class="btns dosure" type='button' id="doSave" value='确定'/>
			<input class="btns docancle" type='button' id="doCancel" value='取消'/>	
		</span>
	</div>
</div>
