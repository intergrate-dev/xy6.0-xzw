<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<html lang="zh-CN">
<head>
<title>栏目详情页</title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<link type="text/css" rel="stylesheet"
	href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
<style type="text/css">


.custview-label {
    width: 100px;

    color: #3A87AD;
    text-align: right;
    float: left;
    line-height: 24px;
    font-size: 12px;
}

body {
	font-family: "微软雅黑";
    margin: 10px;
}

th {
	font-family: "微软雅黑";
    font-size: 15px;
    text-align: center;
}


</style>
<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
<script type="text/javascript"
	src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../script/qrcode.js"></script>
<script type="text/javascript">
	
</script>
</head>
<body>


	<div>
		<table >
			<tr>
				<th >&nbsp;&nbsp;&nbsp;栏目详情</th>
				<th ></th>
			</tr>
			<tr>
				<td class="custview-label">栏目id:&nbsp;</td>
				<td><c:out value="${colID}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">栏目名称:&nbsp;</td>
				<td><c:out value="${colName}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">栏目级联id:&nbsp;</td>
				<td><c:out value="${colCascadeID}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">栏目级联名称:&nbsp;</td>
				<td><c:out value="${colCascadeName}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">&nbsp;</td>
			</tr>
		
			<tr>
				<th >网页版发布详情</th>
				<th ></th>
			</tr>
			<tr>
				<td class="custview-label">栏目页URL:&nbsp;</td>
				<td><a href="<c:out value="${webColumnUrl}" />" target="_blank"><c:out value="${webColumnUrl}" /><a>&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">模板id:&nbsp;</td>
				<td><c:if test="${webTemplateID != ''}">
						<c:out value="${webTemplateID}" />
					</c:if> 
					<c:if test="${webTemplateID =='' }">
						<c:out value="网页发布模板未配置" />
					</c:if>
				&nbsp;</td>

			</tr>
			<tr>
				<td class="custview-label">模板名称:&nbsp;</td>
				<td><c:out value="${webTemplateName}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">发布规则id:&nbsp;</td>
				<td><c:if test="${webRuleID != ''}">
						<c:out value="${webRuleID}" />
					</c:if> 
					<c:if test="${webRuleID =='' }">
						<c:out value="网页发布规则未配置" />
					</c:if>&nbsp;</td>
				
			</tr>
			<tr>
				<td class="custview-label">发布规则名称:&nbsp;</td>
				<td><c:out value="${webRuleName}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">栏目目录:&nbsp;</td>
				<td><c:out value="${webRuleColumnUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">稿件目录:&nbsp;</td>
				<td><c:out value="${webRuleArticleUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">稿件图片目录:&nbsp;</td>
				<td><c:out value="${webRuleArticlePicUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">稿件附件目录:&nbsp;</td>
				<td><c:out value="${webRuleAttcUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">&nbsp;</td>
			</tr>
			<tr>
				<th >触屏版发布详情</th>
				<th ></th>
			</tr>
			<tr>
				<td class="custview-label">栏目页URL:&nbsp;</td>
				<td><c:out value="${padColumnUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">模板id:&nbsp;</td>
				<td><c:if test="${padTemplateID != ''}">
						<c:out value="${padTemplateID}" />
					</c:if> 
					<c:if test="${padTemplateID =='' }">
						<c:out value="触屏发布模板未配置" />
					</c:if>&nbsp;</td>
				
			</tr>
			<tr>
				<td class="custview-label">模板名称:&nbsp;</td>
				<td><c:out value="${padTemplateName}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">发布规则id:&nbsp;</td>
				<td><c:if test="${padRuleID != ''}">
						<c:out value="${padRuleID}" />
					</c:if> 
					<c:if test="${padRuleID =='' }">
						<c:out value="触屏发布规则未配置" />
					</c:if>&nbsp;</td>
			</tr>
			
			<tr>
				<td class="custview-label">发布规则名称:&nbsp;</td>
				<td><c:out value="${padRuleName}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">栏目目录:&nbsp;</td>
				<td><c:out value="${padRuleColumnUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">稿件目录:&nbsp;</td>
				<td><c:out value="${padRuleArticleUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">稿件图片目录:&nbsp;</td>
				<td><c:out value="${padRuleArticlePicUrl}" />&nbsp;</td>
			</tr>
			<tr>
				<td class="custview-label">稿件附件目录:&nbsp;</td>
				<td><c:out value="${padRuleAttcUrl}" />&nbsp;</td>
			</tr>

		</table>


	</div>

</body>
</html>
