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
	#typeselect{
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
<%@ page pageEncoding="UTF-8"%>
<div id="searchBtnArea">
	<div class="div">
		 栏目：  <input id="colFrm" type="text" readonly="true"/> 
		<input id="colFrmId" type="text" /> 
		<input type='button' id="colSelect" value='...' title="选择栏目"/>
		<c:if test="${type == 0 or type == 1}">
			<select id="typeselect" >
				<option value=''>--类型--</option>
				<option value='0'>文字</option>
				<option value='1'>组图</option>
				<option value='2'>视频</option>
				<option value='3'>专题</option>
				<option value='4'>链接</option>
				<option value='5'>多标题</option>
			</select>
		</c:if>
		<input id="title" type="text" size="5" title="请输入标题、关键字等" placeholder="关键字查询"/>
		
		<input type='button' id="search" value='查找'/>
		<input type='button' id="reset" value='清空'/>
		<span class="btngroup">
			<input class="btns dosure" type='button' id="doSave" value='确定'/>
			<input class="btns docancle" type='button' id="doCancel" value='取消'/>	
		</span>
	</div>
</div>
<script type="text/javascript" src="script/WXChannel.js"></script>
<script type="text/javascript" src="script/SearchCondition.js"></script>
<script>
	//从后台获取参数
	var siteID = "<c:out value="${siteID}"/>";
	var colID = "<c:out value="${colID}"/>";
	var colName = "<c:out value="${colName}"/>";
	var ch = "<c:out value="${ch}"/>";
	search_art.type = 1;
	//为关键词查询输入框增加回车提交功能
	$("#title").keydown(function(event){
		if(event.keyCode == 13){
			search_art.Search();
		}
	});
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
﻿<style type="text/css">
	#wrapMain{margin-left:0px;}
</style>
<script type="text/javascript">
	var main_channelLib = [
		{docLibID:"<c:out value="${channelLib0.docLibID}"/>", fvID:"<c:out value="${channelLib0.folderID}"/>"},
		{docLibID:"<c:out value="${channelLib1.docLibID}"/>", fvID:"<c:out value="${channelLib1.folderID}"/>"}
	]
</script>
