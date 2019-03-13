﻿﻿<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-validation-engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../script/jquery-validation-engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 

	
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	<link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<link type="text/css" rel="stylesheet" href="../script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
	<link type="text/css" rel="stylesheet" href="css/live.css"/>

	<link href="../script/jquery-textext/src/css/textext.core.css" rel="stylesheet">
	<link href="../script/jquery-textext/src/css/textext.plugin.autocomplete.css" rel="stylesheet">
	<link href="../script/jquery-textext/src/css/textext.plugin.clear.css" rel="stylesheet">
	<link href="../script/jquery-textext/src/css/textext.plugin.focus.css" rel="stylesheet">
	<link href="../script/jquery-textext/src/css/textext.plugin.prompt.css" rel="stylesheet">
	<link href="../script/jquery-textext/src/css/textext.plugin.tags.css" rel="stylesheet">

	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.core.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.ajax.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.autocomplete.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.clear.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.filter.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.focus.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.prompt.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.suggestions.js"></script>
	<script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.tags.js"></script>

	
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.1.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script src="../script/picupload/upload_api.js" type="text/javascript"></script>
	<script type="text/javascript" src="./js/live_new.js?t=4"></script>
	<script>
		var article = {
			videoPlugin : "<c:out value="${videoPlugin}"/>", //模拟视频稿中使用的参数：视频控件播放地址
			isNew : ${isNew},
			siteID : ${siteID},
			author : "${author}",
			topic : "${topic}",
			noPic : "true"
		}
	</script>
	<style>
		.out-border{
			padding: 9px 14px;
			/*margin-bottom: -15px;*/
			/*background-color: #f7f7f9;*/
			border: 1px solid #e1e1e8;
			border-radius: 4px;
			margin-left: 111px;
			margin-top:-20px;
		}
		.file-drop-zone{
			height: 236px;
		}
		.tablecontent .custform-label-require{
			padding-right: 6px;
		}
		#LABEL_a_status,#LABEL_a_type{
			width: 83px;
		}
		input[name = 'a_status'],input[name = 'a_type']{
			float: left;
			margin-top: 2px !important;
		}
		#DIV_a_status label{
			width:37px;
			margin-top: 4px;
		}
		#SPAN_a_discussClosed{
			margin-left: 18px;
    		margin-top: -10px;
    		margin-bottom: -10px;
		}
		#DIV_a_type label{
			margin-top: 5px;
		}
		#liveAreaDiv{
			margin-left: 30px;
    		width: 810px;
    		display: table;
		}
		.out-border div{
			margin-bottom: 10px;
		}
		.out-border div:last-child{
			margin-bottom: 0;
		}
		button{
			font-family: "microsoft yahei";
		}
		.text-core{
			margin-left: 113px;
		}
		#liver{
			float: left;
		    font-family: '微软雅黑';
		    color: #000;
		    font-weight: bold;
		    margin-left: 60px;
		}
		#userTA{
			height: 38px;
			border: 1px solid #ddd;
			line-height: 38px;
		}
		.stream{
			width:600px;
			height: 40px;
			float: left;
		}
		.streamRevise{
			width:20px;
		}
		.text-core .text-wrap .text-tags .text-tag .text-button{
			font-family: '微软雅黑';
		    height: 22px;
		    padding: 0px 17px 12px 3px;
		    margin: 6px 2px 2px 0;
		    background: #5bc0de;
		    color: #fff;
		    line-height: 22px;
		    border:none;
		}
		.text-remove{
			margin-top: 4px;
		}
		/*.btngroup{
			margin-left: 143px;
		}*/
		div.btn-file{
			border-radius: 3px;
			padding: 5px 6px;
    		font-size: 14px;
		}
		.removeBtn{
		    border: none;
		    border-radius: 50%;
		    width: 20px;
		    height: 20px;
		    font-size: 14px;
		    position: absolute;
		    right: -18px;
    		top: -16px;
    		outline: none;
		}
		.userNameTA{
			width: 500px !important;
			margin-left: -110px !important;
			border: 1px solid #ddd !important;
			border-radius: 3px !important;
			background: #fff !important;
			height: 36px !important;
		}
		.text-core .text-wrap{
			background: none;
		}
		.liveChooseBtn{
			position: ABSOLUTE;
			right: -4px;
			top: 1px;
		}
		#liveUrlDiv	.text-dropdown{
			left: -110px;
		}
		#liveUrlDiv	.text-tags{
			position: absolute;
			z-index: 2;
			margin-left: -105px;
		}
		.custform-label{
			width: 85px;
		}
		.file-preview{
			border: hidden!important;
		}
		.file-drop-zone{
			margin: 0px;
		}
		/*.krajee-default.file-preview-frame{
			margin-top: -20px;
		}*/
	.datetimepicker table tr td span {
		width: 31%;
	}

		.file-thumb-progress{
			display: none!important;
		}
	.file-preview{
		padding: 0;
	}
	</style>
</head>
<body>
<form id="form" name="form" method="post" action="LiveSubmit.do">
	<%=request.getAttribute("formContent")%>
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="parentID" name="parentID" value="${parentID}">
	<input type="hidden" id="fromPage" name="fromPage" value="${fromPage}">

	<div id="liveAreaDiv" style="display: none;">
		<div class="custform-span" style="width: 100%; margin-left: 98px; margin-bottom: 14px;">
			<button id="createTXLiveBtn" class="btn btn-primary" type="button">添加腾讯云直播</button>
			<button id="createOtherLiveBtn" class="btn btn-primary" type="button">添加第三方直播</button>
		</div>
		<div id="liveUrlDiv" class="tablecontent"></div>
	</div>
	<div id="livePicAreaDiv" style="overflow: hidden; display: none;">
		<div class="out-border"  style="width: 600px; border: 0px;">
			<div class=""  style="width: 400px;margin-left: 5px;border: 0px;">
				<div style="overflow: hidden;">
					<label class="custform-label" style="width: 200px; margin-bottom: -3px;font-weight: 100; font-size: 12px;">APP头图</label>
				</div>
				<div class=""  style="width: 500px;margin-left: 0px;position: relative;">
					<input id="appPicInput" type="file" name="file" multiple>
					<span style="color: #999; position: absolute; bottom: 4px; left: 100px;">(建议图片大小27－60k,比例4:1)</span>
					<input id="appPicSrcHidden" type="hidden" value=""/>
				</div>
			</div>

			<div class=""  style="width: 500px;margin-left: 5px;border: 0px;">
				<div style="overflow: hidden;">
					<label class="custform-label" style="width: 200px; margin-bottom: -3px; font-weight: 100; font-size: 12px">网站头图</label>
				</div>
				<div class=""  style="width: 1100px;margin-left: 0px; position: relative;">
					<input id="pcPicInput" type="file" name="file" multiple>
					<span style="color: #999;  position: absolute; bottom: 4px; left: 100px;">(建议图片大小30－100k,比例7:2)</span>
					<input id="pcPicSrcHidden" type="hidden" value=""/>
				</div>
			</div>
		</div>
	</div>

	<div id="hostDiv" style="display: none; overflow: inherit;">
		<div class="custform-span" style="width: 100%; margin-bottom: 14px;">
			<span id="liver">直播员</span>
			<textarea id="userTA" rows="1" style="width: 500px; margin-left: 13px;"></textarea>
		</div>
	</div>

	<div class="underTop">
		<%--<div id="widgetDiv" style="min-height:300px;">
			<ul class="nav nav-tabs" role="tablist" id="setting">
				<li role="presentation" class="active">
					<a href="#Pic" aria-controls="Pic" role="tab" data-toggle="tab">图片</a>
				</li>
				<li role="presentation">
					<a href="#Video" aria-controls="Video" role="tab" data-toggle="tab">视频</a>
				</li>
			</ul>
			<div class="tab-content addsource" style="  height: 272px;">
				<div role="tabpane" class="tab-pane active" id="Pic">
					<div id="picUploadDiv">
						<%@include file="../pic/inc/PicUpload.inc" %>
					</div>
					<div style="clear:Both;"></div>
				</div>
				<div role="tabpane" class="tab-pane " id="Video">
					<div id="videoUploadDiv">
						<%@include file="../video/inc/videoUpload.inc" %>
					</div>
					<div style="clear:Both;"></div>
				</div>
			</div>
		</div>--%>
		<div>
			<li class="btngroup">
				<input class="dosave" type="button" id="btnSave" value="保存"/>
				<input class="docancle" type="button" id="btnCancel" value="关闭"/>
			</li>
		</div>
	</div>
</form>

<div id="liveModel" style="display: none;">
	<div class="custform-span liveStream newLiveModel ">
		<div class="custform-from-wrap out-border detail">
			<div> <label class="custform-label " style="display: block;height: 18px;float: left;margin-right: 15px;">线路</label>
				<select class="line_select" style="display: block;height: 20px;float: left;">
					<option value="">请选择</option>
					<option value="1">线路1</option>
					<option value="2">线路2</option>
					<option value="3">线路3</option>
					<option value="4">线路4</option>
				</select>
				<label class="custform-label " style="display: block;height: 18px;float: left;margin-right: 15px;">线路名称</label>
				<input class="line_name" type="text" value="" style="float: left;display: block;height: 20px;margin-right: 20px;"><span style="display: block;font-size: 15px;">建议2-5字</span>
				<button class="removeBtn liveDeleteBtn">×</button>
			</div>
			<div class="live_user"> <label class="custform-label custform-label-require user">直播员</label>
				<div class="custform-from-wrap" >
					<textarea rows="1" style="width: 500px;" class="userNameTA"></textarea>
					<input type="hidden" class="userName" >
					<input type="hidden" class="userID" />
					<input type="hidden" class="streamId" />
					<button class="btn btn-primary liveChooseBtn" type="button">获得地址</button>
				</div>
			</div>
			<div class="live_uploadUrl"> <label class="custform-label ">上传地址</label>
				<div class="custform-from-wrap" >
					<input type="text" value="" class="custform-input uploadUrl" readonly="true" style="width:500px;">
					<!--<button class="btn btn-danger liveDeleteBtn" type="button">删除</button>-->
				</div>
			</div>

			<div> <label class="custform-label playerUrl">播放地址（app）</label>
				<div class="custform-from-wrap" >
					<input type="text" value="" class="custform-input appLiveUrl"  style="width:500px;">
				</div>
			</div>

			<div> <label class="custform-label playerUrl">播放地址（web）</label>
				<div class="custform-from-wrap" >
					<input type="text" value="" class="custform-input webLiveUrl"  style="width:500px;">
				</div>
			</div>

			<div class="live_backUrlApp" style="display: none"> <label class="custform-label ">回放地址（app）</label>
				<div class="custform-from-wrap" >
					<input type="text" value="" class="custform-input appPlaybackUrl"  style="width:500px;">
				</div>
			</div>

			<div class="live_backUrlWeb" style="display: none"> <label class="custform-label ">回放地址（web）</label>
				<div class="custform-from-wrap" >
					<input type="text" value="" class="custform-input webPlaybackUrl"  style="width:500px;">
				</div>
			</div>
			<div>
				<li class="btngroup">
					<input type="button" style=" margin-left: 400px;" class="btnStreamSave" onclick="xy_live.lineSave(event)" value="保存"/>
				</li>
			</div>
		</div>
		<div style="display: none;" class="brief custform-from-wrap out-border">
			<div class="stream" >
				<label style="height: 40px;margin-right: 40px; display:block ;float: left">线路</label><label  name="线路" class="lineID" disabled="true" style="height: 40px;width: 100px;font-size: 16px;display: block;float: left"></label>
				<label style="height: 40px;margin-right: 40px; display:block ;float: left">线路名称</label><label  name="线路名称" class="lineName" disabled="true" style="height: 40px;width: 100px;font-size: 16px;display: block;float: left"></label>
				<label style="height: 40px;margin-right: 40px; display:block ;float: left">直播员</label><label  name="直播员" class="lineUser" disabled="true" style="height: 40px;width: 100px;font-size: 16px;display: block;float: left"></label>
			</div>
			<%--<input type="image" src="./img/revise.png" class="streamRevise" onclick="xy_live.lineRevise(event)">--%>
			<img src="./img/revise.png" class="streamRevise" onclick="xy_live.lineRevise(event)">
		</div>

	</div>
</div>

</body>
</html>
