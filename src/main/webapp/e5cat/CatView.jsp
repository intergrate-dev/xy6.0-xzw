<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><i18n:message key="cat.view.title"/></title>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	</head>
<body>
	<div class="mainBodyWrap">
	<table cellpadding="0" cellspacing="0" class="table">
		<caption><i18n:message key="cat.view.title"/></caption>  	
		<tr>
			<th class="w90"><i18n:message key="cat.view.catID"/></th>
			<td><c:out value="${catID}"/></td>
		</tr>
		<tr>
			<th><i18n:message key="cat.edit.catName"/></th>
			<td>
		<c:out value="${item.catName}"/>
		</td>
		</tr>
		<tr>
			<th><i18n:message key="cat.edit.catCode"/></th>
			<td><c:out value="${item.catCode}"/>&nbsp;</td>
		</tr>
		<tr style="display:none;">
			<th><i18n:message key="cat.view.cascadeName"/></th>
			<td><c:out value="${item.cascadeName}"/></td>
		</tr>
		<!-- 引用的分类没有这些字段 -->
		<c:if test="${item.refType<=0}">
		<c:if test="${typeForm.supportPublish}">
		<tr>
			<th><i18n:message key="cat.edit.published"/></th>
			<td><c:if test="${item.published}"> <i18n:message key="cat.view.published.yes"/></c:if><c:if test="${!item.published}"> <i18n:message key="cat.view.published.no"/></c:if></td>
		</tr>
		</c:if>
		<c:if test="${typeForm.supportSecurityLevel}">
		<tr>
			<th><i18n:message key="cat.edit.pubLevel"/></th>
			<td><c:out value="${item.pubLevel}"/></td>
		</tr>
		</c:if>
		<c:if test="${typeForm.supportRelTable}">
		<tr>
			<th><i18n:message key="cat.edit.linkTable"/></th>
			<td><c:out value="${item.linkTable}"/>&nbsp;</td>
		</tr>
		</c:if>
		<c:if test="${typeForm.supportLinkType}">
		<tr>
			<th><i18n:message key="cat.edit.linkType"/></th>
			<td>
			<c:forEach var="catType" items="${catTypes}">
				<c:if test="${catType.catType==item.linkType}">
					<c:out value="${catType.name}"/>
				</c:if>
			</c:forEach>&nbsp;
			</td>      
		</tr>
		<tr>
			<th><i18n:message key="cat.edit.linkID"/></th>
			<td><c:out value="${item.linkID}"/></td>
		</tr>
		</c:if>
	</c:if>
	
		<!-- 正常分类不显示这个字段 -->
		<c:if test="${item.refType>0}">
		<tr>
			<th><i18n:message key="cat.edit.refCatType"/></th>
			<td><c:forEach var="catType" items="${catTypes}">
				<c:if test="${catType.catType==item.refType}">
					<c:out value="${catType.name}"/>
				</c:if>
				</c:forEach>&nbsp;
			</td>
		</tr>   
		<tr>
			<th><i18n:message key="cat.edit.refID"/></th>
			<td><c:out value="${item.refID}"/></td>
		</tr>
	</c:if>
		<tr>
			<th><i18n:message key="cat.edit.userName"/></th>
			<td><c:out value="${item.userName}"/></td>
		</tr>
		<tr>
			<th><i18n:message key="cat.edit.lastModified"/></th>
			<td><c:out value="${item.lastModified}"/></td>
		</tr>
		<c:forEach var="extType" items="${catExts}">
		<tr>
				<th><c:out value="${extType.extTypeName}"/></th>
				<td><c:out value="${extType.extName}"/>&nbsp;</td>
		</tr>
	</c:forEach>
		<tr>
			<th><i18n:message key="cat.edit.memo"/></th>
			<td><textarea name="memo" cols="50" rows="6"><c:out value="${item.memo}"/></textarea></td>
		</tr>
	</table>
	</div>
</body>
</html>
