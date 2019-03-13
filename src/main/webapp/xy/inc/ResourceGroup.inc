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
<script language="javascript" type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>

<div id="main_resourcetree" class="sidebar" >
	<div class="div">
		<c:if test="${!isGroupSelect}"> <!-- 若非选择图片库、选择视频库、选择专题等窗口-->
			<span id="btnAdd" title="增加" class="icon-plus"></span>
			<span id="btnUpdate" title="修改"  class="icon-pencil"></span>
			<span  id="btnDelete" title="删除" class="icon-trash"></span>
		</c:if>
		<c:if test="${isExtField}">
			<span id="btnGrant" title="挂接栏目" class="icon-tag"></span>
		</c:if>
	</div>
	<hr class="hr" />
	<div id="rs_tree" class="ztree">
		<ul id="groupUl">
			<li style="padding-left:5px;"><input id="Search" type="text" value="" class="ac_input" title="请输入栏目名进行查询"/></li>
			<c:forEach var="group" items="${groups}">
				<li class="group" groupID="<c:out value="${group.catID}"/>">
					<div class="active" name="grouplist"   id="groupNameDiv_<c:out value="${group.catID}"/>" groupID="<c:out value="${group.catID}"/>">
						<c:out value="${group.catName}" />
					</div>
					<input class="input" maxlength="10" id='groupIdInput_<c:out value="${group.catID}"/>'
						groupID="<c:out value="${group.catID}"/>" groupName="<c:out value="${group.catName}" />"
						value="" />
				</li>
			</c:forEach>
		</ul>
		<c:if test="${!isGroupSelect}">
			<input class="input1" maxlength="10" name="newGroupInput" id="newGroupInput" type="text"
				placeholder="新组名称" onclick="" />
			<input type="hidden" id="chosenGroupIDInput" value="" />
		</c:if>
	</div>
</div>
<script>
	main_param["catTypeID"] = "<c:out value="${catTypeID}"/>";
	main_param["groupField"] = "<c:out value="${groupField}"/>";
	main_param["siteField"] = "<c:out value="${siteField}"/>";
</script>
<script language="javascript" type="text/javascript" src="script/ResourceGroup.js"></script>
<script language="javascript" type="text/javascript" src="script/autocomplete.js"></script>
