<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="./script/columnStat.js"></script>
	
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link type="text/css" rel="stylesheet" href="./css/stat.css"/>
	<style>
		#channels li:hover{
			border-bottom: 2px solid #ddd;
		}
		#channels{
			border-bottom: 1px solid #ddd;
		}
		a:focus,a:visited,a:active,a:hover{
			text-decoration: none;
		}
	</style>
</head>
<body>
	<div class="division">
		<!-- 栏目订阅统计 -->
		<div id="dept">
			<div id="detail">
				<div style="float: left;">
					<div class="custform-controls">
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="column_stat.outputcsv()" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId">
						<tr class="tdtr">
							<td class="title tdtr">栏目名</td>
							<td class="title tdtr">订阅数</td>
						</tr>
						<c:set var="total0" value="0" />
						<c:forEach var="bean" items="${list}" varStatus="st">
							<tr class="tdtr">
								<td class="tdtr">${bean.name}</td>
								<td class="tdtr">${bean.count}</td>
							</tr>
							<c:set var="total0" value="${bean.count + total0}" />
						</c:forEach>
						<tr class="tdtr">
							<td class="total tdtr">总计（${list.size()}个栏目）</td>
							<td class="total tdtr">${total0}</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
	<form id="form" method="post"><!-- csv输出 -->
		<input type="hidden" id="jsonData" name="jsonData" />
		<input type="hidden" id="csvName" name="csvName" />
	</form>
</body>
</html>