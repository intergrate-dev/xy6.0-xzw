<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="./script/workload.js"></script>
	
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="./css/stat.css"/>
	<style>
		a:focus,a:visited,a:active,a:hover{
			text-decoration: none;
		}
		.channels li.select{
			 background: #e4a744;
			 color: #fff;
			 border-bottom: none;
			 border-radius: 3px;
		}
		.userName{
			display: inline-block;
			margin-top: 13px;
			margin-left: 8px;
		}
	</style>
</head>
<body>
	<div id="div">
		<div class="left">
			<ul id="ul1" class="channels">
				<li style="width: 38px;" class="ditch">渠道:</li>
				<c:forEach var="ch" items="${channels}">
					<c:if test="${ch != null}">
					<li class="channelTab" id="channel<c:out value="${ch.code}"/>"><c:out value="${ch.name}"/></li>
					</c:if>
				</c:forEach>
			</ul>
		</div>
		<div id="workload_Web">
			<div>
				<span class="userName">用户:</span>
				<select id="usrSelect">
					<option value="-1" selected></option>
					<c:forEach var="usr" items="${usrList}" varStatus="st">
						<option value="${usr.key}">${usr.value}</option>
					</c:forEach>
				</select>
			</div>
			<div class="times">
				时间:&nbsp;&nbsp;&nbsp;
				<a id="lastMonth" class="time"  href="javascript:workLoad_stat.search('lastMonth', '')">上月</a>
				&nbsp;&nbsp;&nbsp;
				<a id="thisMonth" class="time"  href="javascript:workLoad_stat.search('thisMonth', '')">本月</a>
				&nbsp;&nbsp;&nbsp;
			</div>
			<div>
				<div class="custform-controls">
					<input id="pubTime_from"  readonly type="text" value="" name="pubTime_from">
					-
					<input id="pubTime_to"  readonly type="text" value="" name="pubTime_to">
					<input class="see" type="button" value="确定" onclick="workLoad_stat.search('searchBtn', '')">
					<input class="see" style='width:80px' type="button" value="导出数据" onclick="workLoad_stat.outputcsv('')">
				</div>
			</div>
			<div class="detail">
				<table class="loadTable" id="loadId">
					<tr class="tdtr">
						<td class="title tdtr">用户名</td>
						<td class="title tdtr">稿件量</td>
						<td class="title tdtr">点击量</td>
						<td class="title tdtr">评论量</td>
					</tr>
					<c:set var="total0" value="0" />
					<c:set var="total1" value="0" />
					<c:set var="total2" value="0" />
					<c:forEach var="dept" items="${detailMap}" varStatus="st">
						<tr class="tdtr">
							<td class="tdtr">${dept.name}</td>
							<td class="tdtr">${dept.count}</td>
							<td class="tdtr">${dept.countClick}</td>
							<td class="tdtr">${dept.countDiscuss}</td>
						</tr>
						<c:set var="total0" value="${dept.count + total0}" />
						<c:set var="total1" value="${dept.countClick + total1}" />
						<c:set var="total2" value="${dept.countDiscuss + total2}" />
					</c:forEach>
					<tr class="tdtr">
						<td class="total tdtr">总计（${detailMap.size()}人）</td>
						<td class="total tdtr">${total0}</td>
						<td class="total tdtr">${total1}</td>
						<td class="total tdtr">${total2}</td>
					</tr>
				</table>
			</div>
		</div>
		<div id="workload_App">
			<div>
				<span class="userName">用户:</span>
				<select id="usrSelect_App">
					<option value="-1" selected></option>
					<c:forEach var="usr" items="${usrList}" varStatus="st">
						<option value="${usr.key}">${usr.value}</option>
					</c:forEach>
				</select>
			</div>
			<div class="times">
				时间:&nbsp;&nbsp;&nbsp;
				<a id="lastMonth_App" class="time"  href="javascript:workLoad_stat.search('lastMonth', '_App')">上月</a>
				&nbsp;&nbsp;&nbsp;
				<a id="thisMonth_App" class="time"  href="javascript:workLoad_stat.search('thisMonth', '_App')">本月</a>
				&nbsp;&nbsp;&nbsp;
			</div>
			<div>
				<div class="custform-controls">
					<input id="pubTime_from_App"  readonly type="text" value="" name="pubTime_from_App">
					-
					<input id="pubTime_to_App"  readonly type="text" value="" name="pubTime_to_App">
					<input class="see" type="button" value="确定" onclick="workLoad_stat.search('searchBtn_App', '_App')" />
					<input class="see" style='width:80px' type="button" value="导出数据" onclick="workLoad_stat.outputcsv('_App')" />
				</div>
			</div>
			<div class="detail">
				<table class="loadTable" id="loadId_App">
					<tr class="tdtr">
						<td class="title tdtr">用户名</td>
						<td class="title tdtr">稿件量</td>
						<td class="title tdtr">点击量</td>
						<td class="title tdtr">评论量</td>
					</tr>
					<c:set var="total0" value="0" />
					<c:set var="total1" value="0" />
					<c:set var="total2" value="0" />
					<c:forEach var="dept" items="${detailMap_App}" varStatus="st">
						<tr class="tdtr">
							<td class="tdtr">${dept.name}</td>
							<td class="tdtr">${dept.count}</td>
							<td class="tdtr">${dept.countClick}</td>
							<td class="tdtr">${dept.countDiscuss}</td>
						</tr>
						<c:set var="total0" value="${dept.count + total0}" />
						<c:set var="total1" value="${dept.countClick + total1}" />
						<c:set var="total2" value="${dept.countDiscuss + total2}" />
					</c:forEach>
					<tr class="tdtr">
						<td class="total tdtr">总计（${detailMap_App.size()}人）</td>
						<td class="total tdtr">${total0}</td>
						<td class="total tdtr">${total1}</td>
						<td class="total tdtr">${total2}</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
<input type="hidden" id="siteID" value="${siteID}"><!-- 站点ID -->
<form id="form" method="post"><!-- csv输出 -->
	<input type="hidden" id="jsonData" name="jsonData" />
	<input type="hidden" id="csvName" name="csvName" />
</form>
</body>
</html>