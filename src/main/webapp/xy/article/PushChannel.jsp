<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>选择推动渠道主栏目</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/styles.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.1.min.js"></script>
	<script type="text/javascript" src="../script/cookie.js"></script>
	<script type="text/javascript">
		var pushchannel = {
			UUID : "<c:out value="${UUID}"/>",
			DocIDs : "<c:out value="${DocIDs}"/>",
			DocLibID : "<c:out value="${DocLibID}"/>",
			siteID : "<c:out value="${siteID}"/>",
			channelCount : "<c:out value="${channelCount}"/>"
		};
	</script>
	<style>
		body{
			font-family: "microsoft yahei";
		}
		body input{
			font-family: "microsoft yahei";
		}
		.frm {
			border: 0;
			width: 250px;
			height: 250px;
		}
		.li
		{
			margin:20px 0; 
			
			list-style:none;
			font-size:13px;
		}
		.label
		{
			font-weight: 400;
		}
		.input
		{
			border-radius:4px; 
			border: 1px solid #ddd;
			
			font-size: 12px;
  			width: 141px;
  			padding-left: 10px;
  			height: 19px;
		}
		#btnSubmit
		{
		  border-radius: 3px;
		  color: #fff;
		  background:#1bb8fa;
		  width: 64px;
		  height: 30px;
		  border: none;

		  font-size: 12px;
		  cursor: pointer;
		}
		#btnCancel{
		  border-radius: 3px;
		  color: #fff;
		  background:#b1b1b1;
		  width: 64px;
		  height: 30px;
		  border: none;
		  margin-left:15px;
		  font-size: 12px;
		  cursor: pointer;
		}
		#form div{
			text-align: center;
			margin-top: 19%;
  			margin-bottom: 6%;
		}
		.btngroup{
			margin-left: 43%;
		}
	</style>
</head>
<body>
	<script language="javascript" type="text/javascript" src="script/pushchannel.js"></script>
	<form id="form">
		<div>
			<c:if test="${channelCount > 0}">
				<c:forEach var="ch" items="${channels}">
					<li class="li">
						<label class="label" for="channel<c:out value="${ch.id}"/>">
							<input type="checkbox" id="channel<c:out value="${ch.id}"/>" name="channel<c:out value="${ch.id}"/>"/>
							<span><c:out value="${ch.name}"/></span>
						</label>
						主栏目：<input type="hidden" id="<c:out value="${ch.id}"/>_columnID" name="<c:out value="${ch.id}"/>_columnID">
						<input class="input" style="" type="text" id="<c:out value="${ch.id}"/>_column" name="<c:out value="${ch.id}"/>_column" 
								readonly="true" placeholder="请选择栏目">  
						<input type="button" for="<c:out value="${ch.id}"/>_column" ch="<c:out value="${ch.id}"/>"
								value="..." class="btn btnColumn" title="选择栏目">
					</li>
				</c:forEach>
			</c:if>
			<c:if test="${channelCount == 0}">
				<tr>稿件
				<c:if test="${pushedDocIDsCount > 0}">
					<c:out value="${pushedDocIDs}"/>
				</c:if>
				在所有渠道已存在，不可再推送！</tr>
			</c:if>
		</div>
	</form>
	<div class="btngroup">
		<input type='button' id="btnSubmit" value='确定'/>
		<input type='button' id="btnCancel" value='取消'/>
	</div>

</body>
</html>