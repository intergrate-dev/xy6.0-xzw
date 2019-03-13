<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<body>
<%@include file="inc/ResourceSimple.inc"%>
<div id="wrapMain">
	<%@include file="inc/Search.inc"%>
	<div id="main">
		<div id="panContent" class="panContent">
			<div class="tabHr toolkitArea">
				<div id="main_toolbar">
	<span class="columnGarbageTitle">
		<c:if test="${type == 0}">栏目更新时间一览</c:if>
		<c:if test="${type == 1}">删除栏目一览</c:if>
		<c:if test="${type == 2}">僵尸栏目一览</c:if>
	</span>
					<div id="toolTable" class="overflow">
						<ul id="toolTR">
							<c:if test="${type == 0}">
							<li class="toolButton tIconBText" opid="1" title="标为僵尸栏目，则栏目树中不再显示">
								<button class="btn btn-small" type="button">标为僵尸栏目</button>
							</li>
							</c:if>
							<c:if test="${type != 0}">
							<li class="toolButton tIconBText" opid="0" title="恢复">
								<button class="btn btn-small" type="button">恢复</button>
							</li>
							</c:if>
							<li class="toolButton tIconBText" opid="2" title="日志">
								<button class="btn btn-small" type="button">日志</button>
							</li>
						</ul>
					</div>
				</div>
				<script language="javascript" type="text/javascript" src="script/ToolColGarbage.js"></script>				</div>
			<%@include file="inc/Statusbar.inc"%>
		</div>
	</div>
</div>
</body>
<%@include file="inc/MainFooter.inc"%>

<style>
	.columnGarbageTitle {
		margin-left:10px;
		margin-top:5px;
		float:left;
	}
</style>