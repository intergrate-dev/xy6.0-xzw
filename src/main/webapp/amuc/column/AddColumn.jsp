<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.founder.e5.commons.Pair"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.founder.e5.commons.ResourceMgr"%>
<%@page import="com.founder.e5.db.DBSession"%>
<%@page import="com.founder.e5.context.E5Exception"%>
<%@page import="com.founder.e5.context.Context"%>
<%@page import="com.founder.e5.db.IResultSet"%>
<%
	String docLibID = request.getParameter("DocLibID");
	String docID = request.getParameter("DocIDs");
	String uuID = request.getParameter("UUID");
	
	int doclibid = 0;
	int docId = 0;
	int len = 0;
	if(docID!=null){
		docId = Integer.parseInt(docID);
	}
	if(docLibID!=null){
		doclibid = Integer.parseInt(docLibID);;
	}
	
%>
<!DOCTYPE html>
<HTML>
<HEAD>
	<TITLE> ZTREE DEMO - checkbox</TITLE>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="css/demo.css" type="text/css">
	<link rel="stylesheet" href="css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="js/jquery-1.4.4.min.js"></script>
	<script type="text/javascript" src="js/jquery.ztree.core.js"></script>
	<script type="text/javascript" src="js/jquery.ztree.excheck.js"></script>
	<!--
	<script type="text/javascript" src="js/jquery.ztree.exedit.js"></script>
	-->
	<SCRIPT type="text/javascript">
	var docid = <%=docID%>;
	$(document).ready(function(){
		columnInit();
		//$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		setCheck();
		$("#py").bind("change", setCheck);
		$("#sy").bind("change", setCheck);
		$("#pn").bind("change", setCheck);
		$("#sn").bind("change", setCheck);
		onChecked();
	});
	function columnInit(){
		  var siteID = $("#siteID").val();
		  var url1 ='../../amuc/column/columnInit.do';
			$.ajax({
				url:url1,
				type:'get',
				data:{siteID:siteID},
				dataType:"json",
				cache:false,
				async:false,
				success:function(data){
					//alert(data)
					var setting = {
							check: {
								enable: true
							},
							data: {
								simpleData: {
									enable: true
								}
							}
						};

						var zNodes = data;
						$.fn.zTree.init($("#treeDemo"), setting, zNodes);
				},
				complete: function(XMLHttpRequest, textStatus){
			        this;  // 调用本次AJAX请求时传递的options参数
			    },
			    error: function (XMLHttpRequest, textStatus, errorThrown) {
			    	//alert("error status : " + XMLHttpRequest.status);
			    }
			});
		}
		
		
		function setCheck() {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			py = $("#py").attr("checked")? "p":"",
			sy = $("#sy").attr("checked")? "s":"",
			pn = $("#pn").attr("checked")? "p":"",
			sn = $("#sn").attr("checked")? "s":"",
			/* type = { "Y":py + sy, "N":pn + sn}; 2016.12.23  李经洲 修改 子节点与父节点关联，而父节点与子节点不关联*/
			type = { "Y":"s", "N":"s"};
			zTree.setting.check.chkboxType = type;
			showCode('setting.check.chkboxType = { "Y" : "s", "N" : "s" };');
		}
		function showCode(str) {
			if (!code) code = $("#code");
			//code.empty();
			code.append("<li>"+str+"</li>");
		}
		
		function onCheck(){
            var treeObj=$.fn.zTree.getZTreeObj("treeDemo"),
            nodes=treeObj.getCheckedNodes(true),
            columnName="";
            columnId="";
            columnpId="";
            for(var i=0;i<nodes.length;i++){
            	columnName+=nodes[i].name + "~";
            	columnId+=nodes[i].id + ",";           	
            	columnpId+=nodes[i].pId + ",";        	
            	
            }
            var url = encodeURI(encodeURI("../../amuc/column/save.do?columnName=" + columnName + "&columnId=" + columnId + "&columnpId=" + columnpId + "&DocIDs=" + docid));
            
			//后台删除选中的策划案
			$.ajax({
				url: url,
				async:false,
				cache:false,
				success: function(data) {
					
			   },
			   error:function (XMLHttpRequest, textStatus, errorThrown) {
			     alert("栏目不能为空！");
			   }
			});
			on_Close();
		}
			
		function onChecked(){	
	    	var url = encodeURI(encodeURI("../../amuc/column/getChecked.do?DocIDs=" + docid));
	            
				//后台删除选中的策划案
				$.ajax({
					url: url,
					async:false,
					cache:false,
					success: function(data) {
					   var	column =  data.split(",");
					   for(var i=0;i<column.length;i++){	
							var zTree = $.fn.zTree.getZTreeObj("treeDemo");
							var node = zTree.getNodeByParam("id",column[i]);
							zTree.selectNode(node);
							node.checked = true; 
							zTree.updateNode(node);
					   }
				   },
				   error:function (XMLHttpRequest, textStatus, errorThrown) {
				     //alert(textStatus);
				   }
				});
         } 
		
		function on_Close() {
			window.onbeforeunload = "javascript:void(0);";
			window.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
			parent.location.reload();
		}
		
	</SCRIPT>
</HEAD>

<BODY>
<div class="content_wrap">
	<div class="zTreeDemoBackground left">
		<ul id="treeDemo" class="ztree"></ul>
		
		<div class="btnarea text-center" style="text-align: center;margin-left: 50px;">
		<input class="btn" type="button" id="btnSave" onclick="onCheck()" value="保存"/>
		<input class="btn" type="button" id="btnCancel" style="margin-left:20px" onclick="on_Close()" value="取消"/>
		</div>
	</div>
	<div class="right" style="display:none">
		<ul class="info">
			<li class="title"><h2>1、setting 配置信息说明</h2>
				<ul class="list">
				<li class="highlight_red">使用 checkbox，必须设置 setting.check 中的各个属性，详细请参见 API 文档中的相关内容</li>
				<li><p>父子关联关系：<br/>
						被勾选时：<input type="checkbox" id="py" class="checkbox first" checked /><span>关联父</span>
						<input type="checkbox" id="sy" class="checkbox first" checked /><span>关联子</span><br/>
						取消勾选时：<input type="checkbox" id="pn" class="checkbox first" checked /><span>关联父</span>
						<input type="checkbox" id="sn" class="checkbox first" checked /><span>关联子</span><br/>
						<ul id="code" class="log" style="height:20px;"></ul></p>
				</li>
				</ul>
			</li>
		</ul>
	</div>
</div>
<input type="hidden" id="DocIDs" name="DocIDs" value="<%=request.getParameter("DocIDs")%>" /> 
<input type="hidden" id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>" />
<input type="hidden" id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>" />
</BODY>
</HTML>