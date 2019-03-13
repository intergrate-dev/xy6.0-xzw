<%@page pageEncoding="UTF-8"%>
<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<link rel="stylesheet" href="zTree/css/bootstrap.min.css">
<style>
	#wrapMain{
		padding-left:0;
	}
	#myModal{
	width: 522px;
	height: 258px;
	position: absolute;
	top: 80px;
	left: 188px;
	}
	#myModal .modal-header{
	width: 520px;
	height: 60px;
	border-bottom: 1px solid #e5e5e5;
	}
	#myModal .modal-body {
	width: 520px;
	height: 71px;
	border-bottom:1px solid #e5e5e5;
	}
	#myModal .modal-body1{
	width: 520px;
	height: 71px;
	position: relative;
	padding: 15px;
	}
	#myModal input{
	width: 150px;
	height: 30px;
	}
	#myModal span:nth-of-type(2n){
	margin-left:26px;
	}
	#myModal span{
	position:relative;
	top:-5px;
	}
	#myModal .modal-footer{
	padding: 10px;
	}
</style>
<%@include file="inc/MainHeader.inc"%>
<body>
	<div id="wrapMain">
		<%@include file="inc/ChannelTab.inc"%>
		<%@include file="inc/ResourceChannel.inc"%>
		<span id="ResourceGroup"><%@include file="inc/ResourceGroup.inc"%></span>
		
		<%@include file="inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="inc/Toolkit.inc"%>
				</div>
				<%@include file="inc/Statusbar.inc"%>
			</div>
		</div>

		<%--全局弹出框--%>
		<div class="modal-content modal" id="myModal" >
		<div class="modal-header">
		<button type="button" id="close" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4 class="modal-title" id="myModalLabel">修改**父级 **子集对应的信息</h4>
		</div>
		<div class="modal-body">
		<span>原有名称：</span><input type="text" placeholder="原有信息" value="12311231" disabled="true">
		<span>原有Code：</span><input type="text" placeholder="原有信息" value="12311231" disabled="true">
		</div>
		<div class="modal-body1">
		<span>现有名称：</span><input type="text" placeholder="请填写名称信息" value="">
		<span>现有Code：</span><input type="text" placeholder="请填写Code信息" value="">
		</div>
		<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		<button type="button" class="btn btn-primary">提交更改</button>
		</div>
		</div>
	</div>
</body>
<%@include file="inc/MainFooter.inc"%>

<%--引入z-tree插件--%>
<script type="text/javascript" src="zTree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="zTree/js/jquery.ztree.excheck.js"></script>
<script type="text/javascript" src="zTree/js/jquery.ztree.exedit.js"></script>
<script type="text/javascript" src="zTree/js/zTreeDiagram.js"></script>

