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
	<script type="text/javascript" src="../script/echarts/echarts.js"></script>
	
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link rel="stylesheet" href="../script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="./css/stat.css"/>
</head>
<style>		
	.channels
	{
		padding: 0;
		margin-top: 10px;
		margin-bottom: 10px;
		border-bottom: 1px solid #ddd;
	}
	a:focus,a:visited,a:active,a:hover{
		text-decoration: none;
	}
	
	
	.time1 #thisWeek,.time1 #thisWeek_App{
		width: 100px;
		display: inline-block;
		text-align: center;
	}
	#lineWeb_ div,#lineApp_ div{
		left: -40px;
	}
	.wrapper,.wrapper1{
		border: 1px solid #ddd;
		width: 470px;
		
	}
	.wrapper{
		 margin-bottom: 10px;
		 margin-top:5px;
	}
	.channels li:hover {
	    border-bottom: 2px solid #ddd;
	  }
</style>
<body>
<div>
	<div class="left">
		<div class="wrapper">
			<ul id="ul" class="channels">
			<c:forEach var="ch" items="${channels}">
				<c:if test="${ch != null}">
				<li class="channelTab" id="pie<c:out value="${ch.code}"/>"><c:out value="${ch.name}"/></li>
				</c:if>
			</c:forEach>
			</ul>
			<c:forEach var="ch" items="${channels}">
				<c:if test="${ch != null}">
				<div style="width: 400px;" id="pie<c:out value="${ch.code}"/>_"></div>
				</c:if>
			</c:forEach>
		</div>
		<div class="wrapper1">
			<ul id="ul1" class="channels">
			<c:forEach var="ch" items="${channels}">
				<c:if test="${ch != null}">
				<li class="channelTab" id="line<c:out value="${ch.code}"/>"><c:out value="${ch.name}"/></li>
				</c:if>
			</c:forEach>
			</ul>
			<c:forEach var="ch" items="${channels}">
				<c:if test="${ch != null}">
				<div id="line<c:out value="${ch.code}"/>_"></div>
				</c:if>
			</c:forEach>
		</div>
	</div>
	<div class="div"></div>
	<ul id="ul2" class="channels">
		<c:forEach var="ch" items="${channels}">
			<c:if test="${ch != null}">
			<li class="channelTab" id="detail<c:out value="${ch.code}"/>"><c:out value="${ch.name}"/></li>
			</c:if>
		</c:forEach>
	</ul>
	<div style="width:750px; margin-top:5px;" class="left" id="detailWeb_">
		<div>
			<div class="left">
				<span class="span">栏目:</span>
				<input id="colName" type="text" placeholder="请选择栏目" readonly name="colName" />
				<input id="colID" type="hidden" name="colID" />
				<input style="" id="colSelect" class="btn btnColumn" type="button" title="选择栏目" value="选择" />
			</div>
			<div class="left">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;类型:
				<select id="artSelect">
					<option value="-1" selected></option>
					<c:forEach var="at" items="${articleTypes}">
					<option value="<c:out value="${at.key}"/>"><c:out value="${at.value}"/></option>
					</c:forEach>
				</select>
			</div>
			<div class="time1">
				时间:&nbsp;&nbsp;&nbsp;
				<a id="today" class="time"  href="javascript:load_stat.search('today', '')">今天</a>
				&nbsp;&nbsp;&nbsp;
				<a id="yesterday" class="time"  href="javascript:load_stat.search('yesterday', '')">昨天</a>
				&nbsp;&nbsp;&nbsp;
				<a id="thisWeek" class="time"  href="javascript:load_stat.search('thisWeek', '')">最近一周内</a>
				&nbsp;&nbsp;&nbsp;
			</div>
			<div>
				<div class="custform-controls">
					<input id="pubTime_from" readonly type="text" value="" name="pubTime_from">
					-
					<input id="pubTime_to"  readonly type="text" value="" name="pubTime_to">
					<input class="see" type="button" value="确定" onclick="load_stat.search('searchBtn', '')">
					<input class="see" style='width:75px' type="button" value="导出数据" onclick="load_stat.outputcsv('')">
				</div>
			</div>
		</div>
		
		<div class="detail">
			<table class="loadTable" id="loadId">
				<tr class="tdtr">
					<td class="title tdtr">时间</td>
					<td class="title tdtr">稿件量</td>
					<td class="title tdtr">点击量</td>
					<td class="title tdtr">评论量</td>
				</tr>
				<c:set var="totalCount" value="0" />
				<c:set var="totalDiscuss" value="0" />
				<c:set var="totalClick" value="0" />
				<c:forEach var="detailMap" items="${detailMapList}" varStatus="st">
					<tr class="tdtr">
						<td class="tdtr">${detailMap.date}</td>
						<td class="tdtr">${detailMap.count}</td>
						<td class="tdtr">${detailMap.click}</td>
						<td class="tdtr">${detailMap.discuss}</td>
					</tr>
					<c:set var="totalCount" value="${totalCount + detailMap.count}" />
					<c:set var="totalDiscuss" value="${totalDiscuss + detailMap.discuss}" />
					<c:set var="totalClick" value="${totalClick + detailMap.click}" />
				</c:forEach>
				<tr class="tdtr">
					<td class="total tdtr">总计（${detailMapList.size()}天）</td>
					<td class="total tdtr">${totalCount}</td>
					<td class="total tdtr">${totalClick}</td>
					<td class="total tdtr">${totalDiscuss}</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div id="detailApp_" style="width:750px; margin-top:10px;">
		<div>
			<div class="left">
				<span class="span">栏目:</span>
				<input id="colName_App" type="text" placeholder="请选择栏目" readonly name="colName_App" />
				<input id="colID_App" type="hidden" name="colID_App" />
				<input id="colSelect_App" class="btn btnColumn" type="button" title="选择栏目" value="选择" />
			</div>
			<div class="left">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;类型:
				<select id="artSelect_App">
					<option value="-1" selected></option>
					<c:forEach var="at" items="${articleTypes}">
					<option value="<c:out value="${at.key}"/>"><c:out value="${at.value}"/></option>
					</c:forEach>
				</select>
			</div>
			<div class="time1">
				时间:&nbsp;&nbsp;&nbsp;
				<a id="today_App" class="time"  href="javascript:load_stat.search('today', '_App')">今天</a>
				&nbsp;&nbsp;&nbsp;
				<a id="yesterday_App" class="time"  href="javascript:load_stat.search('yesterday', '_App')">昨天</a>
				&nbsp;&nbsp;&nbsp;
				<a id="thisWeek_App" class="time"  href="javascript:load_stat.search('thisWeek', '_App')">最近一周内</a>
				&nbsp;&nbsp;&nbsp;
			</div>
			<div class="left">
				<div class="custform-controls">
					<input id="pubTime_from_App" readonly type="text" value="" name="pubTime_from_App">
					-
					<input id="pubTime_to_App" readonly type="text" value="" name="pubTime_to_App">
					<input class="see" type="button" value="确定" onclick="load_stat.search('searchBtn_App', '_App')">
					<input class="see" style='width:75px' type="button" value="导出数据" onclick="load_stat.outputcsv('_App')">
				</div>
			</div>
		</div>
		<div class="detail">
			<table class="loadTable" id="loadId_App">
				<tr class="tdtr">
					<td class="title tdtr">时间</td>
					<td class="title tdtr">稿件量</td>
					<td class="title tdtr">点击量</td>
					<td class="title tdtr">评论量</td>
				</tr>
				<c:set var="totalCount" value="0" />
				<c:set var="totalDiscuss" value="0" />
				<c:set var="totalClick" value="0" />
				<c:forEach var="detailMap" items="${detailMapList_App}" varStatus="st">
					<tr class="tdtr">
						<td class="tdtr">${detailMap.date}</td>
						<td class="tdtr">${detailMap.count}</td>
						<td class="tdtr">${detailMap.click}</td>
						<td class="tdtr">${detailMap.discuss}</td>
					</tr>
					<c:set var="totalCount" value="${totalCount + detailMap.count}" />
					<c:set var="totalDiscuss" value="${totalDiscuss + detailMap.discuss}" />
					<c:set var="totalClick" value="${totalClick + detailMap.click}" />
				</c:forEach>
				<tr class="tdtr">
					<td class="total tdtr">总计（${detailMapList_App.size()}天）</td>
					<td class="total tdtr">${totalCount}</td>
					<td class="total tdtr">${totalClick}</td>
					<td class="total tdtr">${totalDiscuss}</td>
				</tr>
			</table>
		</div>
	</div>
</div>
<!-- 隐藏域存放数据js用 -->
<script>
	var stat_data = {
		siteID : "${siteID}",
		serialMonth : "${serialMonth}",
		
		statTypes : [],
		typeDatas : {},
		
		monthDatas : {},
		monthDatas_App : {},
		
		serialDatas : {},
		serialDatas_App : {}
	}
	<c:forEach var="at" items="${articleTypes}">
		stat_data.statTypes.push("<c:out value="${at.value}"/>");
		stat_data.typeDatas["<c:out value="${at.key}"/>"] = "<c:out value="${at.value}"/>";
	</c:forEach>
	
	<c:forEach var="at" items="${monthMap}">
		stat_data.monthDatas["<c:out value="${at.key}"/>"] = "<c:out value="${at.value}"/>";
	</c:forEach>
	<c:forEach var="at" items="${monthMap_App}">
		stat_data.monthDatas_App["<c:out value="${at.key}"/>"] = "<c:out value="${at.value}"/>";
	</c:forEach>
	
	<c:forEach var="at" items="${monthlyDatas}" varStatus="st">
		stat_data.serialDatas["${st.index}"] = "<c:out value="${at}"/>";
	</c:forEach>
	<c:forEach var="at" items="${monthlyDatas_App}" varStatus="st">
		stat_data.serialDatas_App["${st.index}"] = "<c:out value="${at}"/>";
	</c:forEach>
</script>

<form id="form" method="post"><!-- csv输出 -->
	<input type="hidden" id="jsonData" name="jsonData" />
	<input type="hidden" id="csvName" name="csvName" />
</form>
</body>
</html>
<script type="text/javascript" src="./script/stat.js?time=1"></script>
