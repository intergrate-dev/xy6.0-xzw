<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body>
	<script>
		//从后台获取参数
		var sessionID = "<c:out value="${sessionID}"/>";
		var isAudio = "<c:out value="${isAudio}"/>";
	</script>
	<script language="javascript" type="text/javascript" src="script/video-upload.js"></script>
	<div id="upload">
		<div style="height:380px;width:100%;">
			<OBJECT ID="VJUploadAx"
				CLASSID="CLSID:E37856DD-E172-4ECC-9A0B-28CF2098B25F"
				codebase="../vjupax.cab#version=3,0,1130,0"
				style="height: 100%;width:100%;">
				<param name="Server" value="${uploadPort}" />
				<param name="SessionId" value="${sessionID}" />
				<!-- 可删除功能在webservice上无法体现 -->
				<param name="EnableDeleteFile" value="false">
				<param name="EnableCreateDirectory" value="false">
				<param name="EnableMoveFile" value="false">
				<param name="EnableSelectDirectory" value="false">
				<!-- 如果启用MD5，web汇报中就没有文件名 -->
				<param name="EnableUseMD5ForName" value="false">
				<param name="EnableSimpleUI" value="false">
				<c:if test="${isAudio==true}">
					<param name="FileFilterString"
						   value="*.mp3;">
				</c:if>
				<c:if test="${isAudio==false}">
				<param name="FileFilterString"
					value="*.wmv;*.asf;*.wma;*.rm;*.rmvb;*.flv;*.mp3;*.mp4;*.mkv;*.avi;*.mpg;*.vob;*.mov;*.3gp;*.ogg;*.ogm;">
				</c:if>
				<param name="EnableShowServerDirectory" value="false">
				<param name="UploadLimit" value="1">
				<param name="EnableFilterForFiles" value="true">
				<!--[if !IE]>-->
				<object type="application/x-vjupload-plugin" id="VJUploadAx" width="100%" height="100%" Server="${uploadPort}" SessionId="${sessionID}" 
						enabledeletefile="true" enablemovefile="true" enableusemd5forname="false" EnableSelectDirectory="false" EnableCreateDirectory="false"
						EnableSimpleUI="false" enableshowserverdirectory="true" uploadlimit="1"
				<c:if test="${isAudio==true}">
				    FileFilterString ="*.mp3;"
			    </c:if>
				<c:if test="${isAudio==false}">
					FileFilterString ="*.wmv;*.asf;*.wma;*.rm;*.rmvb;*.flv;*.mp3;*.mp4;*.mkv;*.avi;*.mpg;*.vob;*.mov;*.3gp;*.ogg;*.ogm;"
				</c:if>
						EnableFilterforfiles="true">
				<!--<![endif]-->
				<a href="http://www.nagasoft.cn/download/vjocx3">
					<img src="http://www.nagasoft.cn/download/vjocx3/get_vjocx3_player.gif" alt="Get Naga player" />
				</a>
				</object>
			</OBJECT>
		</div>
		<input type="button" id="uploadSubmit" style="margin-left: 211px;"  value="确定" /> 
		<input type="button" id="uploadCancel" style="margin-left: 211px;"  value="取消" /> 
	</div>
</body>
</html>