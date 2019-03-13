<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8" />
	<title>详情 - ${SYS_TOPIC}</title>
	<meta name="Keywords" content=""/>
	<meta name="Description" content=""/>
	<meta http-equiv="Pragma" content="no-cache" />
	<link rel="stylesheet" type="text/css" href="css/active.css"/>
</head>
<body>
	<div class="wrap">
		<!--头部开始-->
		<!--<div class="header" id="header">
			<input type="button" id="change" class="change" value="修改" />
			<input type="button" id="publish" class="publish" value="发布" />
			<input type="button" id="back" class="back" value="撤回" />
			<input type="button" id="sign" class="sign" value="报名名单" />
			<input type="button" id="comment" class="comment" value="查看评论" />
		</div>-->
		<!--头部结束-->
		<!--活动地点时间开始-->
		<div class="activeDetail clearfix" id="activeDetail">
			<img class="pull-left activeImg" id="activeImg" src="${picSmall}" alt="图片"/>
			<ul class="pull-left address"  id="address">
				<li class="title" id="title">${SYS_TOPIC}</li>
				<li class="time" id="time">
					<span>有效时间：</span>
					<span class="startTime" id="startTime">${a_startTime}</span> ~
					<span class="endTime" id="endTime">${a_endTime}</span>
				</li>
				<li class="positionDiv" id="positionDiv"><span><span style="letter-spacing: 16px;">地点</span>：</span><span class="position" id="position">${a_location}</span></li>
				<li>人数限制：<span class="allNum" id="allNum">${a_countLimited}</span>人 &nbsp;&nbsp;&nbsp;<b>目前报名：</b><span class="limitNum" id="limitNum">${a_count}</span>人</li>
				<li class="authorDiv" id="authorDiv">
					作者：<span id="author" class="author">${SYS_AUTHORS}</span>
				</li>
				<li class="handleTimeDiv" id="handleTimeDiv">最后处理时间：<span class="handleTime" id="handleTime">${SYS_LASTMODIFIED}</span></li>
			</ul>
		</div>	
		<!--活动地点时间结束-->
		<!--活动规则类型-->
		<ul id="typeAndRule">
			<li class="type" id="type">活动类型：${a_type}</li>
			<li class="rule mt10" id="rule">活动规则：${a_rule}</li>
		</ul>
		<!--活动规则类型-->
		<!--正文开始-->
		<div class="content">
			${a_content}
		</div>
		<!--正文结束-->
		<div class="content">
			${a_otherInfo}
		</div>
	</div>
	
	
</body>
</html>