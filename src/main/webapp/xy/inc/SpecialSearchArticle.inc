<style>
	input{
		font-size: 12px;
	}
	#title{	
		font-size: 12px; 
		color:#666; 
		width:150px;
		margin-top: 10px;
		height: 21px;
  		padding: 0;
  		border-radius:0px;
  		padding-left: 10px;	
	}
	#typeselect,#statusselect{
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
	.spanbar{
		display:inline-block;
		float:left;
		font-family:'Arial Negreta', 'Arial';
		font-weight:700;
		font-size:16px;
	}
	.rightBtn{
		display:inline-block;
		float:right;
		margin-top:12px;
		margin-right:10px;
	}
	.showDiv{
		z-index:100;
		width: 1000px;
		height: 800px;
		background-color:#ffffff;
		opacity: 1;
     	-moz-opacity: 1;
        filter: alpha(opacity=100);
	}
	.divbar{
		margin:0px;
		padding:0px;
		z-index:100;
		width:1000px;
		height:20px;
		text-align:center;
	}
	.pagespan{
		display:inline-block;
		height:20px;
		width:20px;
		
	}
</style>
<%@ page pageEncoding="UTF-8"%>
<div id="searchBtnArea">
	<div class="div">
		 稿件类型：
		<select id="typeselect" >
			<option value=''>全部</option>
			<option value='0'>文字</option>
			<option value='1'>组图</option>
			<option value='2'>视频</option>
			<option value='3'>专题</option>
			<option value='4'>链接</option>
			<option value='5'>多标题</option>
			<option value='12'>H5</option>
		</select>
		稿件状态：
		<select id="statusselect" >
			<!-- <option value=''>全部</option>
			<option value="0">未发布</option> -->
			<option value="1">已发布</option>
			<!-- <option value="2">定时发布</option>
			<option value="3">发布中</option>
			<option value="4">审批中</option>
			<option value="5">已驳回</option>
			<option value="6">等待制图</option> -->
		</select>
		<input id="title" type="text" size="5" title="请输入标题、关键字等" placeholder="关键字查询"/>
		
		<input type='button' id="search" value='查找'/>
		<input type='button' id="reset" value='清空'/>
	</div>
</div>
<script>
e5.mod("workspace.search",function() {
	var api;
	var searchClick = function(colId, title, typeselect,statusselect) {
		
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
			
		var rule = "";
		if (colId)
			rule += "_AND_CLASS_1_EQ_" + colId;
		if(typeselect){
			rule += "_AND_a_type_EQ_" + typeselect;
		}
		if(statusselect){
			rule += "_AND_a_status_EQ_" + statusselect;
		}
		
		rule += "_AND_a_siteID_EQ_" + main_param.siteID;
		//最后去掉_AND_
		searchparam.ruleFormula = rule.substring(5);
		//标题
		if (title != ""){
			searchparam.query = "SYS_TOPIC=" + title;
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
"../e5workspace/script/Param.js"
]});

//共查找稿件使用
var search_art = {	
	init : function() {			
		$('#search').click(search_art.Search);
		$('#reset').click(search_art.Reset);
		$('#typeselect').change(search_art.Search);
		$('#statusselect').change(search_art.Search);
	},
	Search : function() {
		var title = $("#title").val();
		var typeselect = $("#typeselect").val();
		var statusselect = $("#statusselect").val();
		e5.mods["workspace.search"].searchClick(colID,title,typeselect,statusselect);
	},
	Reset : function(){
		$("#title").val("");
		$("#typeselect").val("");
		$("#statusselect").val("");
	}
};
$(function() {
	search_art.init();
});
	main_param["catTypeID"] = "<c:out value="${catTypeID}"/>";
	search_art.type = "<c:out value="${type}"/>";
	
	//从后台获取参数
	var docLibID = main_param.docLibID;
	var siteID = "<c:out value="${siteID}"/>";
	var colID = "<c:out value="${colID}"/>";
	var colName = "<c:out value="${colName}"/>";
	var type = "<c:out value="${type}"/>";
	var ch = "<c:out value="${ch}"/>";
	
	//为关键词查询输入框增加回车提交功能
	$("#title").keydown(function(event){
		if(event.keyCode == 13){
			search_art.Search();
		}
	});
	
	//选后处理：去掉unselectedIDs中的，再加上selectedIDs中的。再显示。
	function addSelected(selectedIDs, selectedHtmls, unselectedIDs) {
	    var arr=[],dataArr={};
	    var ids=$("#checkedSpan",parent.document).attr("ids");
	    var htmls=$("#checkedSpan",parent.document).attr("htmls");
        if(ids){
            arr=ids.split(",");
        }

        //html转为json用于重新组合
        if(htmls){
            dataArr=JSON.parse(htmls);
        }

        //id合并去重
        var arr1=arr.concat(selectedIDs);
        var arr2=removeDuplicatedItem(arr1);
        //过滤
        for(var i=0;i<arr2.length;i++){
            for(var j=0;j<unselectedIDs.length;j++){
                if(arr2[i]==unselectedIDs[j]){
                    arr2.splice(i,1);
                }
            }
        }

        //获取新选择的html，整理为json
        var newJson={};
        for(var m=0;m<selectedIDs.length;m++){
            newJson[selectedIDs[m]]=selectedHtmls[selectedIDs[m]];
            //新的htmlJson与缓存的htmlJson合并
            dataArr[selectedIDs[m]]=selectedHtmls[selectedIDs[m]];
        }

        //去掉unselectedIDs对应的html
        for(var n=0;n<unselectedIDs.length;n++){
            delete dataArr[unselectedIDs[n]];
        }

	    $("#checkedSpan",parent.document).attr("ids",arr2);
	    $("#checkedSpan",parent.document).attr("htmls",JSON.stringify(dataArr));
		//修改已选稿件数
		$("#checkedSpan",parent.document).text("已选("+arr2.length+")");
	}

	//去重
	function removeDuplicatedItem(ar) {
        var tmp = {},
            ret = [];
        for (var i = 0, j = ar.length; i < j; i++) {
            if (!tmp[ar[i]]) {
                tmp[ar[i]] = 1;
                ret.push(ar[i]);
            }
        }
        return ret;
    }

	//当用户提交选择的栏目,实现栏目选择树的接口
	function columnClose(filterChecked, allFilterChecked) {
		var colIds = allFilterChecked[0];
		var colNames = allFilterChecked[1];
		search_art.columnClose(colIds,colNames);
	}
	
	function columnCancel() {
		search_art.columnCancel();
	}
</script>
