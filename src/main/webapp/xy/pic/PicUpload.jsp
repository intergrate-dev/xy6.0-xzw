<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body>
<div id="picUploadDiv" >
<%@include file="inc/PicUpload.inc"%>
</div>
<div id="headline" class="headline">
	<c:if test="${fromPage == 'pic'}">
		<span class="picintroduce">描述&nbsp;&nbsp;&nbsp;</span>
		<textarea class="topicintro" id="topic" name="topic" rows="3" cols="20">${topic}</textarea>
		<div style="clear: both;"></div>
		<input class="confirmbtn" type="button" value="确定" id="confirm" name="confirm" />
		<input class="editCancelbtn" type="button" value="取消" id="editCancel" name="editCancel" />
	</c:if>
	<!--<br />-->
	<input class="overallbtn" value="${overall}" type="text" id="overall" name="overall" placeholder="统一输入图片说明...">
</div>


<input type="hidden" id="currentNum" name="currentNum" />
<input type="hidden" id="totalNum" name="totalNum" />
<input type="hidden" id="filePath" name="filePath" />

<input type="hidden" id="siteID" name="siteID" value="${siteID}" />
<input type="hidden" id="DocLibID" name="DocLibID" value="${DocLibID}" />
<input type="hidden" id="FVID" name="FVID" value="${FVID}" />
<input type="hidden" id="UUID" name="UUID" value="${UUID}" />
<input type="hidden" id="picInfoList" name="picInfoList" value="${picInfoList}" />
<input type="hidden" id="p_groupID" name="p_groupID" value="${p_groupID}" />
<input type="hidden" id="p_articleID" name="p_articleID" value="${p_articleID}" />
<input type="hidden" id="fromPage" name="fromPage" value="${fromPage}" />
<input type="hidden" id="topic" name="topic" value='${topic}' />
<input type="hidden" id="overall" name="overall" value='${overall}' />
<input type="hidden" id="p_catID" name="p_catID" value='${p_catID}' />
</body>
</html>
