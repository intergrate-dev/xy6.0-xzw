<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>部署、部署归档数据选择数据源</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>

	<script type="text/javascript">
		var selectDS = {
			UUID : "<c:out value="${UUID}"/>",
			DocIDs : "<c:out value="${DocIDs}"/>",
			DocLibID : "<c:out value="${DocLibID}"/>",
			type : "<c:out value="${type}"/>",
			siteID : "<c:out value="${siteID}"/>"
		};
	</script>
	<style>
		body, .label, .legend{
			font-family: "微软雅黑";
			background-color:#fff;
			font-size:14px;
		}
    	.label {
    		font-weight: normal;
    		color:#666 ;
    	}
    	.radio {
    		float:left;
    	}
		#btnSubmit,#btnCancel {
			font-family:'微软雅黑';
			margin-left:30px; 
			color: #666;
			margin-top: 10px;
		}
		.fieldset {
    		border: none;
    		font-family:'微软雅黑'; 
    		font-size: 12px;
    		color:#666; 
    		margin-left:15px;
    	}
    	.legend {
    		border: none;
    		padding-top: 12px;
    		color: #646464;
    	}	
    	.frm {
			display:none;
		}
	</style>
</head>
<body>
	<script type="text/javascript" src="script/selectDS.js"></script>
	<iframe name="frmSelectDS" id="frmSelectDS" src="" class="frm"></iframe>
	<fieldset class="fieldset">
		<c:if test="${datasourceCount > 0}">
			<legend class="legend">请您选择数据源：</legend>
			<c:forEach var="ds" items="${datasources}">
				<div style='margin:5px'>
					<input class="radio" type="radio" value="<c:out value="${ds.dsID}"/>" name="sourceDS" />
					<label class="label"><c:out value="${ds.name}"/></label>
				</div>
			</c:forEach>
		</c:if>
		<c:if test="${datasourceCount == 0}">
			<tr>读取数据源出现错误！</tr>
		</c:if>
	</fieldset>

	<input type='button' id="btnSubmit" value='确定'/>
	<input type='button' id="btnCancel" value='取消'/>
</body>
</html>