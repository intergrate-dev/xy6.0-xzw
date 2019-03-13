<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>源稿栏目树checkbox选择</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style>
		
		body{
			font-family: "微软雅黑";
			font-size : 14px;
		}
		input{border: 0;}
		
		.pull-left{
			float:left;
		}
		#hint{color:gray;}
		.th{
			cursor:pointer;
			width:100px;
			white-space:nowrap;
			font-weight: 100;
		}
		label{
			margin: 5px 20px;
		}
		.list{
			width: 93.5%;
		}
		.checkbox{
			margin: 2px;
		}
		.glyphicon-stop{
			font-size : 18px;
			margin: 0 5px 0 15px;
		}
		table{
			margin-top: 10px;
		}
		.font{
			color: #00a0e6;
			float: left;
			margin-left: 15px;
		}
		.clearfix:after {
		   content:""; 
		   display: block; 
		   clear:both; 
		}
		.tipGroup{
			margin: 10px 0 0 25px;
			height: 50px;
		}
		.tipTxt{
			margin: 0 40px 5px 31px;
		}
		#flowDivs{
			margin-left: 200px;
		}
		#flows,.tipGroup{
			margin-left: 6px;
    		display: block;
    		font-size: 12px;
		}
		#flows{
			margin-left: 33px;
		}
		#unFlows{
			margin-left: 12px;
    		display: BLOCK;
		}
		.ml40{
			margin-left: 40px;
		}
		.ths{
			margin-right: 40px;
			display: block;
		}
	</style>

</head>
<body style="margin-bottom:0">
	<div class = "allDiv clearfix">
	<div  style="float:left">
		<%@include file="TreeOriginal.inc"%>
	</div>	
	<script>
		//渠道
		var parentID="<c:out value="${param.parentID}"/>";
		var colID="<c:out value="${param.ids}"/>";
        var DocLibID = "<c:out value="${param.DocLibID}"/>";
        var roleID = "<c:out value="${param.roleID}"/>";
        
		//设置栏目树需要的参数
		ori_tree.check.ids = "<c:out value="${param.ids}"/>";
		if (ori_tree.check.ids==""){
			ori_tree.check.ids=sessionStorage.getItem("ids");
			ori_tree.check.parent=sessionStorage.getItem("parentIDs");
		}
		ori_tree.siteID = "<c:out value="${param.siteID}"/>";
		if (!ori_tree.siteID) ori_tree.siteID = 1;

		ori_tree.rootUrl = "../../xy/column/OrgTree.do";
		
		
		var type = "<c:out value="${param.type}"/>";
		if (type == "all") {
			setTimeout(function(){
				$("#flowDivs").hide();
			},10);
			$("#btnColCancel").hide();//隐藏取消按钮
			ori_tree.check.enable = true;
			ori_tree.rootUrl += "?parentID=0";
		} else if (type == "role") {
			$("#btnColCancel").hide();//隐藏取消按钮
			//取有权限的树 不带勾选
			ori_tree.rootUrl += "?role=1&roleID="+roleID;
			ori_tree.colClick0 = colClick1;
		} else if (type == "radio") {
			setTimeout(function(){
				$("#flowDivs").hide();
			},10);
			//取有操作权限的树，单选
			if (parentID){
				ori_tree.rootUrl += "?parentID=" + parentID;
			}else{
				ori_tree.rootUrl += "?admin=1";
			}
			ori_tree.check.chkStyle = "radio";
			ori_tree.colClick0 = colClick2;
		} else {
			ori_tree.rootUrl += "?admin=1";
		}
		//默认是复选，需单选时可加参数style=radio
		if ("<c:out value="${param.style}"/>" == "radio") {
			ori_tree.check.chkStyle = "radio";
		}
        if(opType == "copy"){
            ori_tree.check.chkStyle = "radio";
        }
		function getChecks() {
			try {
			    if(opType == "copy"){
			        if ($("#chkColPub").is(":checked")  && $("#labColPub").is(":visible")){
			            parent.puborApr = "1";
					} else if ($("#chkColtoApr").is(":checked")  && $("#labColtoApr").is(":visible") ){
                        parent.puborApr = "2";
					} else {
                        parent.puborApr = "0";
					}
				}
			    if(type == "role"){
			    	saveFlows(ori_tree);
			    }else if(type == "radio"){
			    	selectUse();
			    }else{
			    	parent.columnClose(ori_tree.getFilterChecks(), ori_tree.getChecks());
			    }
			} catch (e) {
				var hint = "父窗口应实现columnClose(filterChecked, checked)方法供栏目树关闭时调用。";
				alert(hint);
			}
		}
		
		function doCancel() {
			try {
				parent.columnCancel();
			} catch (e) {
				var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
				alert(hint);
			}
		}
		
		//提供一个接口供父级窗口调用
		function  getAllFilterChecked(){
			return ori_tree.getChecks();
		}

		var opType = "<c:out value="${param.opType}"/>";
		
		//按钮
		$("#divColBtn").show();
		$("#btnColCancel").click(doCancel);
		$("#btnColOK").click(getChecks);

		function colClick2(event, treeId, treeNode, clickFlag){
			$("#nodeID").val(treeNode.id);
		}
		
		function colClick1(event, treeId, treeNode, clickFlag){
			$("#nodeID").val(treeNode.id);
			$("#unFlows").html("");
			$("#flows").html("");
            var theURL = "../../xy/system/getOrgFlow.do?colID=" + treeNode.id 
            		+ "&siteID=" + ori_tree.siteID 
            		+ "&roleID=" + roleID;
            $.ajax({url:theURL, async:false, success:function(data){
            	scopeCheck(data.procScopeID);
				//处理无审批流程
				var unArrs = data.flowList[0].unflowArr;
				var unFlows = "";
				for(var i=0;i<unArrs.length;i++){
					unFlows+="<span><label for=unFlow_"+unArrs[i].procID+"><input type='checkbox' id=unFlow_"+unArrs[i].procID+" value="+unArrs[i].procID+">"
						+unArrs[i].procName+'</label></span>';
				}
				$("#unFlows").append(unFlows);
				//处理审批流程
				var arrs = data.flowList[0].flows;
				for(var j=0;j<arrs.length;j++){
					var arr = arrs[j];
//					var flowStr = '<tr class="oTr">' + '<td>'+arr.flowName+'</td>';
					var flowStr = '<table class="" border="0" cellpadding="4" cellspacing="0" class="onlyBorder" doctypeid="2">'
						+'<thead><tr><td colspan="4">'+ arr.flowName +'</td></tr></thead><tbody>';
//					$("#flows").append(arr.flowName+":<br/>");
					var procs = arr.procList;
//					for(var m=0;m<procs.length;m++){
					for(var m in procs){
						flowStr += "<tr><th class='ths'>"+procs[m].flowNodeName + "</th>";
						var nodes = procs[m].procList;
//						for(var n=0;n<nodes.length;n++){
						for(var n in nodes){
							flowStr+="<td><span><label for='flow_"+nodes[n].procID+"'><input type='checkbox' id='flow_"+nodes[n].procID+"' value="+nodes[n].procID+">"
								+nodes[n].procName + '</span></label></td>';
						}
						flowStr += "</tr>";
					}
					flowStr += "</tbody></table>";
					$("#flows").append(flowStr); //添加元素追加到flows里
					
				}
				$("#flowDivs table tbody th").on('click',function(){
					var obj = $(this);
					selectAll(obj);
				});
				$(".generalBtn").on('click',function(){
					var oInputs = $(this).parents('tr').siblings().find('td').find("input[type='checkbox']");
					if(oInputs.prop("checked") == true){
						oInputs.prop('checked', false);
					}else{
						oInputs.prop('checked', true);
					}
				})
				
				flowCheck(data.unProcsIds,0);
				flowCheck(data.procsIds,1);
			}});
        }
		
		function scopeCheck(dataStr){
			if(dataStr==1){
				$("#Enable").attr("checked", true);
			}else{
				$("#DisEnable").attr("checked", true);
			}
		}
		//点击checkbox实现全选全不选功能
		function selectAll(obj) {
			var _input = $(obj).siblings('td').find("input[type='checkbox']");
			if(_input.prop("checked") == true){
				_input.prop('checked', false);
			}else{
				_input.prop('checked', true);
			}
		}
		
		function flowCheck(dataStr, type){
			var idArr = dataStr.split(',');
			if(idArr.length>=1){
				var list;
				if(type==0){
					list = $('#unFlows input[type=checkbox]');
				}else{
					list = $('#flows input[type=checkbox]');
				}
				var array = new Array();
				for ( var i = 0; i < list.length; i++) {
					array[i] = $(list[i]).val();
					if($.inArray(array[i], idArr)>-1){
						if(type==0){
							$("#unFlow_"+array[i]).attr("checked", true);
						}else{
							$("#flow_"+array[i]).attr("checked", true);
						}
					}
				}
			}
		}
		
		function saveFlows(){
			var unflowList = $('#unFlows input[type=checkbox]:checked');
			var flowList = $('#flows input[type=checkbox]:checked'); 
			var scope=$('input:radio[name="scope"]:checked').val();
			var unArray = new Array();
			for ( var i = 0; i < unflowList.length; i++) {
				unArray[i] = $(unflowList[i]).val();
			}
			var array = new Array();
			for ( var i = 0; i < flowList.length; i++) {
				array[i] = $(flowList[i]).val();
			}
			var theURL = "../../xy/system/saveOrgFlow.do?roleID=" + roleID
				+ "&colID=" + $("#nodeID").val()
	            + "&siteID=" + ori_tree.siteID
	            + "&scope=" + scope
	            + "&unIds=" + unArray.toString()
	            + "&ids=" + array.toString();
			$.ajax({url:theURL, async:false, success:function(){
				alert("保存完成！");
			}});
		}
		
		function selectUse(){
			var DocIDs = "<c:out value="${param.DocIDs}"/>";
			var theURL = "../../xy/article/SelectUse.do?catID=" + $("#nodeID").val()
	            + "&DocIDs=" + DocIDs
	            + "&DocLibID=" + DocLibID;
			$.ajax({url:theURL, async:false, success:function(){
				alert("选用完成！");
				doCancel();
			}});
		}
	</script>
	<script type="text/javascript">
		$(function(){
			$("#rs_tree", window.parent.document).css("height",500);
		});
	</script>
	<Form Name="permissionForm" Target="iframe" Action="">
		<div id="flowDivs">
			<div>请设置该分类的操作权限</div>
			<table border="0" cellspacing="4" cellpadding="">
				<tr>
					<th class="pull-left">
						<span class="glyphicon-stop">◆</span>
						<font color="#333" size="2" class="generalBtn">普通操作</font>
					</th>
					
				</tr>
				<tr>
					<td id="unFlows"></td>
				</tr>
			</table>
			<div class="tipGroup clearfix">
				<div>
					<span class="glyphicon-stop">◆</span>
					<font color="#333" size="2">稿件范围</font>
				</div>
				<div style="margin-top: 10px;">
					<div class="tipTxt pull-left">
						<input type="radio" name="scope" id="Enable" value="1">
						<span>该分类除他人草稿状态的所有稿件  </span>
					</div>  
					<div class="tipTwo">
						<input type="radio" name="scope" id="DisEnable" value="0" checked="checked">
						<span>该分类下自己创建的所有稿件</span>
					</div>
				</div>
			</div>
  			
			
			<!--审批流程：<div id="flows"></div>-->
			<table border="0" cellspacing="4" cellpadding="">
				<tr>
					<th class="pull-left">
						<span class="glyphicon-stop">◆</span>
						<font color="#333" size="2">审批流程</font>
					</th>
				</tr>
				<td id="flows">
					
				</td>
			</table>
		</div>
	</Form>
	</div>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<input type="hidden" id="nodeID" value="">
	
	
</body>
</html>