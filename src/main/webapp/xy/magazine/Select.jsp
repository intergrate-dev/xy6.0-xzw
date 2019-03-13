<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>选择签发主栏目以及关联栏目</title>
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
		var paperInfo= {
			UUID : "<c:out value="${UUID}"/>",
			DocIDs : "<c:out value="${DocIDs}"/>",
			DocLibID : "<c:out value="${DocLibID}"/>",
			siteID : "<c:out value="${siteID}"/>",
			channelCount : "<c:out value="${channelCount}"/>"
		};
	</script>
	<style>
		body
		{
			font-family: "微软雅黑";
		}
		
		.frm {
			border: 0;
			width: 250px;
			height: 250px;}
		.li
		{
			margin:20px 0;
			font-family:'微软雅黑';
			list-style:none;
			
			margin-left: 10px;
			color: #646464;
		}
		.li .label
		{
			font-weight: 400;
		}
		.btnColumnRel
		{
			margin-left: -5px;
		}
		.btnColumn
		{
			margin-left: -5px; 
			margin-right: 30px;
		}
		#btnSubmit
		{

			font-family:"microsoft yahei";
			background-color: #00a0e6;
		    border: none;
		    color: #fff;
		    width: 70px !important;
		    border-radius: 3px;
		    height: 25px;
		    cursor: pointer;
		    margin-left: 26px;
		}
				
		#btnCancel
		{
			font-family:"microsoft yahei";
			background-color: #b1b1b1;
		    border: none;
		    color: #fff;
		    width: 70px !important;
		    border-radius: 3px;
		    height: 25px;
		    cursor: pointer;   
		}
		
		.visa{
			color: #666;
			margin: 10px;
			display: block;
		    font-weight: bold;
		}
		.line{
			border-bottom: 1px solid #ddd;
		    width: 98%;
		    margin: 0 auto;
		}
		.link{
			margin-right: 50px;
			font-weight: bold;
		}
		.public{
			margin-left: 107px;
		    color: #666;
		    display: block;
		    margin-bottom: 20px;
		    font-weight: bold;
		}
		.link1{
			font-weight: bold;  
		}
		.btngroup{
			margin-left: 109px;
		}
		#channel1{
			margin-right: 6px;
		}
		.label{
			width: 75px;
			display: block;
			float: left;
		}
	</style>
</head>
<body>
	<script language="javascript" type="text/javascript" src="script/select.js"></script>
	<form id="form">
		<div>
			<span class="visa">发布库可签发渠道:</span>
			<c:if test="${channelCount > 0}">
				<c:forEach var="ch" items="${channels}">
					<li class="li">
						<label class="label" for="channel<c:out value="${ch.id}"/>">
							<input style="float: left;" type="checkbox" id="channel<c:out value="${ch.id}"/>" name="channel<c:out value="${ch.id}"/>"/>
							<c:out value="${ch.name}"/>
						</label>
						<span>主栏目:</span><input type="hidden" id="<c:out value="${ch.id}"/>_columnID" name="<c:out value="${ch.id}"/>_columnID">
						<input type="text" id="<c:out value="${ch.id}"/>_column" name="<c:out value="${ch.id}"/>_column" 
								readonly="true" placeholder="请选择栏目">  
						<input  type="button" for="<c:out value="${ch.id}"/>_column" ch="<c:out value="${ch.id}"/>"
								value="..." class="btn btnColumn" title="选择栏目">	
						<span>关联栏目:</span><input type="hidden" id="<c:out value="${ch.id}"/>_columnRelID" name="<c:out value="${ch.id}"/>_columnRelID">
						<input type="text" id="<c:out value="${ch.id}"/>_columnRel" name="<c:out value="${ch.id}"/>_columnRel" 
								readonly="true" placeholder="请选择栏目">  
						<input  type="button" for="<c:out value="${ch.id}"/>_columnRel" ch="<c:out value="${ch.id}"/>"
								value="..." class="btn btnColumnRel" title="选择栏目">
						<input  type="button" for="<c:out value="${ch.id}"/>_preview" ch="<c:out value="${ch.id}"/>"
								value="预览" class="btn btnPreview" title="预览">	
					</li>
				</c:forEach>
			</c:if>
		</div>
	</form>
	<div class="btngroup">
		<input  type='button' id="btnSubmit" value='确定'/>
		<input type='button' id="btnCancel" value='取消'/>
	</div>

</body>
</html>