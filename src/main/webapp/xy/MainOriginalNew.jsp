<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<body>
<div id="divMainArea" class="mainArea">
	<%@include file="inc/ResourceOriginal.inc"%>
	<div id="wrapMain">
		<%@include file="inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="inc/Toolkit.inc"%>
				</div>
				<%@include file="inc/Statusbar.inc"%>
			</div>
		</div>
	</div>
</div>
<%@include file="inc/ReadMode.inc"%>
<style>
	.ztree *{
		font-family: "微软雅黑";
		font-size:12px;
	}
	
	#colSearch {
		border-radius:3px; 
		border:1px solid #ccc;
		width: 122px;
  		height:24px;
  		padding-left: 5px;
  		margin-left: 5px;
  		margin-top: 5px;
	}
	#colSearchById{
		border-radius:3px; 
		border:1px solid #ccc;
		width: 122px;
  		height:24px;
  		padding-left: 5px;
  		margin-left: 5px;
  		margin-top: 5px;
	}
	
	
	#rs_tree {
		min-height:250px;
	}
	.btngroup{
		margin:5px 5px;
		font-family: microsoft yahei; 
		color: #fff;
		border: none;
		background: #b1b1b1;
		border-radius: 3px;
		padding: 5px 20px;
		font-size: 12px;
	}
	#btnColOK{
	
		background: #00a0e6;
	}
</style>
</body>
<%@include file="inc/MainFooter.inc"%>
