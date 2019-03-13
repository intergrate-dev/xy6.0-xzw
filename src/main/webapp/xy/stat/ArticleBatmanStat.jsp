<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<script type="text/javascript" src="./script/articleBatmanStat.js"></script>
	
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<link type="text/css" rel="stylesheet" href="./css/stat.css"/>
	<style>
		.channels li.select{
			 background: #e4a744;
			 color: #fff;
			 border-bottom: none;
			 border-radius: 3px;
		}
		a:focus,a:visited,a:active,a:hover{
			text-decoration: none;
		}
	</style>
</head>
<body>
	<!-- 发稿量统计 -->
	<div id="articleBatman">
			
			<div class="left" style="display:none">
				<ul id="ul1" class="channels">
					<li style="width: 38px;" class="ditch">渠道:</li>
					<c:forEach var="ch" items="${channels}">
						<c:if test="${ch != null}">
						<li class="channelTab" id="channel<c:out value="${ch.code}"/>"><c:out value="${ch.name}"/></li>
						</c:if>
					</c:forEach>
				</ul>
			</div>
			<div id="detail_Web">
				<div class="detail1">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth" class="time"  href="javascript:articleBatman_stat.search('lastMonth', '')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth" class="time"  href="javascript:articleBatman_stat.search('thisMonth', '')">本月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisYear" class="time"  href="javascript:articleBatman_stat.search('thisYear', '')">本年</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisWeek" class="time"  href="javascript:articleBatman_stat.search('thisWeek', '')">本周</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div style="float: left;">
					<div class="custform-controls">
						<input id="pubTime_from" readonly type="text" value="" name="pubTime_from">
						-
						<input id="pubTime_to" readonly type="text" value="" name="pubTime_to">
						<input class="see" type="button" value="确定" onclick="articleBatman_stat.search('searchBtn', '')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="articleBatman_stat.outputcsv('')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId">
						<tr class="tdtr">
							<td class="title tdtr">发稿人</td>
							<td class="title tdtr">发稿量</td>
							<td class="title tdtr">发布量</td>
						</tr>
						<c:forEach var="dept" items="${statList}" varStatus="st">
							<tr class="tdtr">
								<td class="tdtr">${dept.name}</td>
								<td class="tdtr">${dept.count}</td>
								<td class="tdtr">${dept.countRelease}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
			<div id="detail_App">
				<div class="detail_App">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth_App" class="time" href="javascript:articleBatman_stat.search('lastMonth', '_App')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth_App" class="time" href="javascript:articleBatman_stat.search('thisMonth', '_App')">本月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisYear" class="time"  href="javascript:articleBatman_stat.search('thisYear', '_App')">本年</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisWeek" class="time"  href="javascript:articleBatman_stat.search('thisWeek', '_App')">本周</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div>
					<div class="custform-controls">
						<input id="pubTime_from_App" readonly type="text" value="" name="pubTime_from_App">
						-
						<input id="pubTime_to_App" readonly type="text" value="" name="pubTime_to_App">
						<input class="see" type="button" value="确定" onclick="articleBatman_stat.search('searchBtn_App','_App')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="articleBatman_stat.outputcsv('_App')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId_App">
						<tr class="tdtr">
							<td class="title tdtr">发稿人</td>
							<td class="title tdtr">发稿量</td>
							<td class="title tdtr">发布量</td>
						</tr>
						<c:forEach var="dept" items="${statList}" varStatus="st">
							<tr class="tdtr">
								<td class="tdtr">${dept.name}</td>
								<td class="tdtr">${dept.count}</td>
								<td class="tdtr">${dept.countRelease}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
	</div>
<input type="hidden" id="siteID" value="${siteID}" /><!-- 站点ID -->
<form id="form" method="post"><!-- csv输出 -->
	<input type="hidden" id="jsonData" name="jsonData" />
	<input type="hidden" id="csvName" name="csvName" />
</form>
</body>
</html>