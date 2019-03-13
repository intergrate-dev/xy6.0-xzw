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
	<script type="text/javascript" src="./script/articleStat.js"></script>
	
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
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
		
		
		.time1 #thisWeek,.time1 #thisWeek_App{
			width: 100px;
			display: inline-block;
			text-align: center;
		}
	</style>
</head>
<body>
	<div class="division">
		<div>
			<ul class="channels" id="channels">
				<li style="width:140px;" class="select channelTab" id="deptStat">部门统计</li>
				<li style="width:140px;" class="channelTab" id="srcStat">来源统计</li>
				<li style="width:140px;" class="channelTab" id="colStat">栏目统计</li>
			</ul>
		</div>
		<!-- 部门统计 -->
		<div id="dept">
			<div style="margin-top:10px;">
				<ul id="ul1" class="channels">
					<li style="width:38px; margin-left: -2px;" class="ditch">渠道:</li>
				<c:forEach var="ch" items="${channels}">
					<c:if test="${ch != null}">
					<li name="channel" class="channelTab" id="channel<c:out value="${ch.code}"/>"><c:out value="${ch.name}"/></li>
					</c:if>
				</c:forEach>
				</ul>
			</div>
			<div id="detail">
				<div class="detail1">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth" class="time"  href="javascript:article_stat.search('lastMonth', '', '')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth" class="time"  href="javascript:article_stat.search('thisMonth', '', '')">本月</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div style="float: left;">
					<div class="custform-controls">
						<input id="pubTime_from" readonly type="text" value="" name="pubTime_from">
						-
						<input id="pubTime_to" readonly type="text" value="" name="pubTime_to">
						<input class="see" type="button" value="确定" onclick="article_stat.search('searchBtn', '', '')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="article_stat.outputcsv('', '')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId">
						<tr class="tdtr">
							<td class="title tdtr">部门</td>
							<td class="title tdtr">稿件量</td>
							<td class="title tdtr">点击量</td>
							<td class="title tdtr">评论量</td>
						</tr>
						<c:set var="total0" value="0" />
						<c:set var="total1" value="0" />
						<c:set var="total2" value="0" />
						<c:forEach var="dept" items="${deptStatList}" varStatus="st">
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
							<td class="total tdtr">总计（${deptStatList.size()}个部门）</td>
							<td class="total tdtr">${total0}</td>
							<td class="total tdtr">${total1}</td>
							<td class="total tdtr">${total2}</td>
						</tr>
					</table>
				</div>
			</div>
			<div id="detail_App">
				<div class="detail_App">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth_App" class="time" href="javascript:article_stat.search('lastMonth', '', '_App')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth_App" class="time" href="javascript:article_stat.search('thisMonth', '', '_App')">本月</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div>
					<div class="custform-controls">
						<input id="pubTime_from_App" readonly type="text" value="" name="pubTime_from_App">
						-
						<input id="pubTime_to_App" readonly type="text" value="" name="pubTime_to_App">
						<input class="see" type="button" value="确定" onclick="article_stat.search('searchBtn', '', '_App')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="article_stat.outputcsv('', '_App')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId_App">
						<tr class="tdtr">
							<td class="title tdtr">部门</td>
							<td class="title tdtr">稿件量</td>
							<td class="title tdtr">点击量</td>
							<td class="title tdtr">评论量</td>
						</tr>
						<c:set var="total0" value="0" />
						<c:set var="total1" value="0" />
						<c:set var="total2" value="0" />
						<c:forEach var="dept" items="${deptStatList}" varStatus="st">
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
							<td class="total tdtr">总计（${deptStatList.size()}个部门）</td>
							<td class="total tdtr">${total0}</td>
							<td class="total tdtr">${total1}</td>
							<td class="total tdtr">${total2}</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		
		<!-- 来源统计 -->
		<div id="src">
			
			<div style="margin: 10px 0;">
				<ul id="ul2" class="channels">
					<li style="width:38px; margin-left: -2px;" class="ditch">渠道:</li>
				<c:forEach var="ch" items="${channels}">
					<c:if test="${ch != null}">
					<li name="channel" class="channelTab" id="channel<c:out value="${ch.code}"/>_src"><c:out value="${ch.name}"/></li>
					</c:if>
				</c:forEach>
				</ul>
			</div>
			<div id="detail_src">
				<div style="margin-bottom: 2px;">
					来源:
					<input id="srcName" type="text" name="srcName" />
					<input id="srcID" type="hidden" name="srcID" />
				</div>
				<div class="times">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth_src" class="time"  href="javascript:article_stat.search('lastMonth', '_src', '')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth_src" class="time"  href="javascript:article_stat.search('thisMonth', '_src', '')">本月</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div>
					<div class="custform-controls">
						<input id="pubTime_from_src" readonly type="text" value="" name="pubTime_from_src">
						-
						<input id="pubTime_to_src" readonly type="text" value="" name="pubTime_to_src">
						<input class="see" type="button" value="确定" onclick="article_stat.search('searchBtn', '_src' ,'')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="article_stat.outputcsv('_src' ,'')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId_src">
						<tr class="tdtr">
							<td class="title tdtr">来源</td>
							<td class="title tdtr">稿件量</td>
							<td class="title tdtr">点击量</td>
							<td class="title tdtr">评论量</td>
						</tr>
						<c:set var="total0" value="0" />
						<c:set var="total1" value="0" />
						<c:set var="total2" value="0" />
						<c:forEach var="dept" items="${srcStatList}" varStatus="st">
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
							<td class="total tdtr">总计（${srcStatList.size()}个来源）</td>
							<td class="total tdtr">${total0}</td>
							<td class="total tdtr">${total1}</td>
							<td class="total tdtr">${total2}</td>
						</tr>
					</table>
				</div>
			</div>
			<div id="detail_App_src">
				<div class="left" style="margin-bottom: 2px;">
					来源:
					<input id="srcName_App" type="text" name="srcName_App" />
					<input id="srcID_App" type="hidden" name="srcID_App" />
				</div>
				<div class="times">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth_src_App" class="time" href="javascript:article_stat.search('lastMonth', '_src', '_App')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth_src_App" class="time" href="javascript:article_stat.search('thisMonth', '_src', '_App')">本月</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div>
					<div class="custform-controls">
						<input id="pubTime_from_src_App" readonly type="text" value="" name="pubTime_from_src_App">
						-
						<input id="pubTime_to_src_App" readonly type="text" value="" name="pubTime_to_src_App">
						<input class="see" type="button" value="确定" onclick="article_stat.search('searchBtn', '_src', '_App')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="article_stat.outputcsv('_src', '_App')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId_src_App">
						<tr class="tdtr">
							<td class="title tdtr">来源</td>
							<td class="title tdtr">稿件量</td>
							<td class="title tdtr">点击量</td>
							<td class="title tdtr">评论量</td>
						</tr>
						<c:set var="total0" value="0" />
						<c:set var="total1" value="0" />
						<c:set var="total2" value="0" />
						<c:forEach var="dept" items="${srcStatList}" varStatus="st">
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
							<td class="total tdtr">总计（${srcStatList.size()}个来源）</td>
							<td class="total tdtr">${total0}</td>
							<td class="total tdtr">${total1}</td>
							<td class="total tdtr">${total2}</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		
		<!-- 栏目统计 -->
		<div id="column">
			
			<div style="margin: 10px 0;">
				<ul id="ul3" class="channels">
					<li style="width:38px;margin-left: -3px;">渠道:</li>
				<c:forEach var="ch" items="${channels}">
					<c:if test="${ch != null}">
					<li name="channel" class="channelTab" id="channel<c:out value="${ch.code}"/>_col"><c:out value="${ch.name}"/></li>
					</c:if>
				</c:forEach>
				</ul>
			</div>
			<div id="detail_col">
				<div class="left" style="margin-bottom: 2px;">
					栏目:
					<input id="colName" type="text" placeholder="请选择栏目" readonly name="colName" />
					<input id="colID" type="hidden" name="colID" />
					<input id="colSelect" class="btn btnColumn" type="button" title="选择栏目" value="选择" />
				</div>
				<div class="times">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth_col" class="time"  href="javascript:article_stat.search('lastMonth', '_col', '')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth_col" class="time"  href="javascript:article_stat.search('thisMonth', '_col', '')">本月</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div>
					<div class="custform-controls">
						<input id="pubTime_from_col" readonly type="text" value="" name="pubTime_from_col">
						-
						<input id="pubTime_to_col" readonly type="text" value="" name="pubTime_to_col">
						<input class="see" type="button" value="确定" onclick="article_stat.search('searchBtn', '_col' ,'')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="article_stat.outputcsv('_col' ,'')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId_col"></table>
				</div>
			</div>
			<div id="detail_App_col">
				<div class="left" style="margin-bottom: 2px;">
					栏目:
					<input id="colName_App" type="text" placeholder="请选择栏目" readonly name="colName_App" />
					<input id="colID_App" type="hidden" name="colID_App" />
					<input id="colSelect_App" class="btn btnColumn" type="button" title="选择栏目" value="选择" />
				</div>
				<div class="times">
					时间:&nbsp;&nbsp;&nbsp;
					<a id="lastMonth_col_App" class="time" href="javascript:article_stat.search('lastMonth', '_col', '_App')">上月</a>
					&nbsp;&nbsp;&nbsp;
					<a id="thisMonth_col_App" class="time" href="javascript:article_stat.search('thisMonth', '_col', '_App')">本月</a>
					&nbsp;&nbsp;&nbsp;
				</div>
				<div>
					<div class="custform-controls">
						<input id="pubTime_from_col_App" readonly type="text" value="" name="pubTime_from_col_App">
						-
						<input id="pubTime_to_col_App"  readonly type="text" value="" name="pubTime_to_col_App">
						<input class="see" type="button" value="确定" onclick="article_stat.search('searchBtn', '_col', '_App')" />
						<input class="see" style='width:80px' type="button" value="导出数据" onclick="article_stat.outputcsv('_col', '_App')" />
					</div>
				</div>
				<div class="detail">
					<table class="loadTable" id="loadId_col_App"></table>
				</div>
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