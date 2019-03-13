<style type="text/css">
	body{
		font-family: "microsoft yahei";
		font-size: 13px;
	}
	label{
		font-weight: 100;
		padding: 0;
		margin: 0;
}
</style>
<div id="main_resourcetree" class="sidebar" >
	<div class="div">
		 <span id="btnAdd" title="增加" class="icon-plus"></span>
		 <span  id="btnDelete" title="删除" class="icon-trash"></span>
	</div>
	<hr class="hr" />
	<div id="rs_tree" class="ztree">
		<input type="hidden" value="${moId}" id="moId">
		<ul id="appUl">
			<c:forEach var="app" items="${apps}">
				<li class="group" appID="<c:out value="${app.docId}"/>">
					<div class="active" name="applist"   id="appDiv_<c:out value="${app.docId}"/>" 
					appid="<c:out value="${app.docId}"/>"  apptype="<c:out value="${app.maType}"/>" >
						<c:out value="${app.maName}" />
					</div>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>
<script language="javascript" type="text/javascript" src="app/script/mobileApp.js"></script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>

