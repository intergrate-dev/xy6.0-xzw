<%@include file="../../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
<meta name="renderer" content="webkit">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>微信图文编辑</title>

<link rel="stylesheet" type="text/css"
	href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css"
	href="../script/jquery-ui-1.11.4.custom/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="css/wx.css" />
	<link type="text/css" rel="stylesheet" href="css/jeDate-test.css">
	<link type="text/css" rel="stylesheet" href="css/skin/jedate.css">
	
	<script>
		var wxGroup_data = {
		    accountID : "${accountID}",
		    docLibID : "${docLibID}",
		    groupID : "${groupID}",
		    isNew : "${isNew}",
		    UUID : "${UUID}",
		    groupStatus : "${groupStatus}"
	    };
	
	    var article = {
		    siteID: "${siteID}"
	    };
		var chosenImg, notUseSameSetting = true;
	    function resetImageInfo(_src){
	    	chosenImg.src = _src;
	    }
	</script>

<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
<script type="text/javascript"
	src="../script/jquery-ui-1.11.4.custom/jquery-ui.min.js"></script>
<script type="text/javascript"
	src="../script/jquery-ui-1.11.4.custom/jquery-ui-i18n.js"></script>
	<script src="script/jquery.pagination.js"></script>
<script type="text/javascript"
	src="../script/bootstrap-3.3.4/js/bootstrap.js"></script>
<script type="text/javascript" src="../ueditor/ueditor.config.wx.js"></script>
<%--<script type="text/javascript" src="script/ueditor/ueditor.config.js"></script>--%>
<%--<script type="text/javascript" src="../ueditor/ueditor.all.min.js"></script>--%>
<script type="text/javascript" src="script/editor_api.js"></script>
<%--<script type="text/javascript" charset="utf-8" src="../ueditor/ueditor.custom.js"></script>--%>
<script type="text/javascript" src="script/wxGroup.js"></script>
	<script type="text/javascript" src="script/jquery.jedate.js"></script>
	<script type="text/javascript" src="script/jedate-test.js"></script>
<%--hhr--%>
<style type="text/css">
/*#moveitem{*/
/*position: absolute;*/
/*}*/
html {
	font: 500 14px 'roboto';
	color: #333;
	background-color: #F5F5F5;
}

a {
	text-decoration: none;
}

ul,ol,li {
	list-style: none;
	padding: 0;
	margin: 0;
}

#wxGra_datapicker {
	right: -260px;
	display: block;
	position: absolute;
	background: #fff;
	border: 1px solid #eee;
	top: 40px;
	z-index: 10;
}

p {
	margin: 0;
}

body {
	background: #F5F5F5;
}

#dt {
	margin: 30px auto;
	height: 28px;
	width: 200px;
	padding: 0 6px;
	border: 1px solid #ccc;
	outline: none;
}

.editorCalendar {
	padding: 0 10px;
}

.search_cal {
	position: absolute;
	right: 6px;
	top: 1px;
}

.wxGrapTitle {
	color: #404040;
	border-bottom: 1px solid #9E9E9E;
	font-size: 20px;
	font-weight: bold;
}

.wxGra_pubdate {
	float: right;
	color: #606060;
	position: relative;
}

.wxGra_edit>div:after {
	display: block;
	content: "";
	clear: both;
}

.wxGra_editTitle {
	height: 30px;
	line-height: 30px;
	color: #606060;
}

.edit_border {
	padding: 20px 40px;
	width: 100%;
	height: 245px;
	box-sizing: border-box;
}

#edit_box {
	border: 1px solid #eee;
	width: 100%;
	height: 100%;
}

#edit_box ul li {
	height: 160px;
	padding: 20px;
	padding-bottom: 0;
}

#edit_box ul li .edit_list {
	height: 100%;
	border: 1px dashed #CDCDCD;
	background: #F3F3F3;
	color: #999999;
	text-align: center;
	line-height: 160px;
}

.detit_btn {
	display: inline-block;
	padding: 9px 0px;
	/*border-radius: 5px;*/
	margin-right: 14px;
	width: 76px;
	text-align: center;
}

.edit_active {
	background: #FF6600;
	color: #fff;
}

.edit_default {
	border: 1px solid #909090;
	color: #323232;
}

.detitBtn_box {
	margin: auto;
	margin-top: 20px;
	width: 360px;
}
/*选稿区*/
.wxGra_artSelect {
	/*height: 700px;*/
	width: 100%;
	/*background: #F3F3F3;*/
	padding: 20px;
	float: right;
}

.artSelect_head {
	position: relative;
	height: 80px;
}
.ser_group{
	float:left;
}
.artSelect_head input {
	height: 30px;
	/*border: 1px solid #767676;*/
	padding: 0 9px;
	margin-right: 5px;
	font-size: 12px;
	border-radius: inherit;
	border: 1px solid #ECECEC;
	background: #F5F5F5;
}
:-moz-placeholder { /* Mozilla Firefox 4 to 18 */
    color: #999; 
}

::-moz-placeholder { /* Mozilla Firefox 19+ */
    color: #999;
}

input:-ms-input-placeholder{
    color: #999;
}

input::-webkit-input-placeholder{
    color: #999;
}
.wxGra_startTime {
	display: inline-block;
	position: relative;
}

#select_artID,#select_author {
	width: 100px;
}

.wxGra_startTime input {
	width: 90px;
}

.btnimg {
	position: absolute;
    right: 10px;
}

#backbtn {
	color: #3B3B3B;
	border: 1px solid #CFCFCF;
	border-radius: 5px;
	padding: 10px 25px;
	background: #F3F3F3;
}

.startTime_div {
	position: relative;
}

.startTime {
	position: absolute;
	right: 10px;
	top: -5px
}

.endTime {
	position: absolute;
	right: 6px;
	top: 3px;
}

table {
	width: 100%;
	border: 1px solid #DCDCDC;
	border-bottom: none;
}

table tr td,table tr th {
	border:1px solid #DCDCDC;
	border-bottom-width: thin;
	border-right-style:none;
	border-left-style:none;
	border-top-style: none;
	text-align: center;
	background: #fff;
	padding: 10px;
	font-size: 13px;
	color: #505050;
	font-weight: 700;
}

.key_img,.key_illegal {
	padding: 3px;
	border: 1px solid red;
	color: red;
}

.key_sensitive {
	padding: 3px;
	border: 1px solid #FF980D;
	color: #FF980D;
}

.key_select {
	display: none;
}
/*正文*/
.tab_box {
	width: 300px;
	border: 1px solid #eee;
}

.tab_box_list {
	position: relative;
	border-bottom: 1px solid #E5E5E5;
}

.deleteitem {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: #989898;
    display: block;
    position: absolute;
    right: -20px;
    top: 0;
}
.deleteitem a.close{
    width: 20px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    color: #fff;
    opacity: 1;
    border-radius: 50%;
    font-size:14px;
    }

.tab_box_list>div:after {
	display: block;
	content: "";
	clear: both;
}

.tab_box_list>div {
	height: 100px;
	box-sizing: border-box;
	padding: 10px;
	position: relative;
}

.tabList_left {
	margin-right: 10px;
	float: left;
	width: 180px;
	box-sizing: border-box;
	height: 100%;
	border: 1px dashed #eee;
}

.tabList_img {
	float: left;
	width: 85px;
	height: 81px;
}

.tabList_img img {
	width: 100%;
	height: 100%;
}
/*选稿区*/
.list_tbodycon {
	text-align: left;
}



.editoption {
	position: absolute;
	left: 160px;
	top: 40px;
}

.editoptionInput {
	display: none;
	margin-right: 10px;
	float: left;
	width: 180px;
	box-sizing: border-box;
	height: 100%;
}

* {
	padding: 0;
	margin: 0;
}

.wxGrapTitle {
	width: 85%;
	min-width: 1250px;
	color: #fff;
	/*border-bottom: 1px solid #9E9E9E;*/
	padding: 13px 15px;
	font-size: 16px;
	font-weight: bold;
	margin: 0 auto;
}

.wxGrapTitlecon {
	padding: 10px 20px;
	width: 1250px;
	margin: 0 auto;
}

.editorCalendar {
	border: 1px solid #E4E4E4;
}

.wxGra_con:after,.wxGra_edit:after {
	display: block;
	clear: both;
	content: "";
}

.wxGra_edit {
	float: left;
	width: 380px;
	padding: 20px;
}

.wxGra_editF {
	width: 385px;
	left: 61px;
	position: absolute;
}

.wxGra_editTitle {
	float: left;
}

.editorCalendar {
	width: 160px;
	height: 30px;
}

#app {
	width: 1250px;
	margin: 0 auto;
}

p.list-group-item div img {
	width: 100%;
	height: 100%;
}
.list-group-item{
	position:relative;
}

.message-list-sub input {
	width: 190px;
}
.container{
	width: 85%;
	min-width: 1250px;
}
<%--日历    --%>
 .wxGra_startTime input{
    width:100%
    }
    #material-reback{
    float:left;
	margin-bottom:10px
    }
	<%--分页--%>
	.selectartpager>span.active{
	<%--border: 1px solid #00A680;--%>
	color: #fff;
	background:#00A680;
	padding: 5px 10px;
	border-radius: 5px;
	margin-right: 5px;
	}
	.selectartpager>a{
	padding: 5px 10px;
	border: 1px solid #ddd;
	color: #949494;
	border-radius: 5px;
	margin-right: 5px;
	text-decoration: none;
	}
	.jump-ipt{
	width: 50px;
	height: 30px;
	border-radius: 5px;
	}
	.selectartpager{
	padding:20px 0;
	float:right;
	}
	
	/*为了方便拖动排序，将图文组中每个li以及li内标签的内联样式提出来---start----*/
	.uploadImgDiv {
		width:312px;
		height:180px;
		overflow: hidden;
	}
	.uploadImgDiv img {
		width:100%;
		height:100%;
		margin-top:0px;
	}
	.message-list-thumbnail img {
		max-height:90px;
		max-width:90px
	}
	.drag_icon {
		position: absolute;
		width: 20px;
		height: 20px;
		left: -20px;
	    top: 0;
	    border: 1px solid #ccc;
	    background-position: 1px -79px;
	}
	
	#message-list-template {
		display: none;
	}
	/*暂时不用编辑标题和编辑摘要按钮，先将其隐藏*/
	.editTitle, .editAbstract {
		display: none;
	}
	textarea {
		resize: none;
	}
</style>
<script>

//  $(document).ready(function(){
//  var h=$(".message-cover-title").outerHeight();
//  $('.message-cover-title').focus(function(){
//  $(this).css("height","50px");
//  })
//  $('.message-cover-title').blur(function(){
//  $(this).css("height", h);
//  })
//  });

    </script>
</head>
<body>
	<%--<nav class="navbar">--%>
	<%--<div class="container">--%>
	<%--<div class='row'>--%>
	<%--<div class="col-md-12">--%>
	<%--<div class="button-fluid">--%>
	<%--<button id="edit-save-message" type="button" class="wx_btn_a" title="保存">保存</button>--%>
	<%--<button id="edit-cancel-message" type="button" class="wx_btn_c" title="取消">取消</button>--%>
	<%--<button id="edit-preview-message" type="button" class="wx_btn_b" title="发送预览消息到手机">预览</button>--%>
	<%--<button id="edit-push-message" type="button" class="wx_btn_b" title="保存并发布">发布</button>--%>
	<%--</div>--%>
	<%--</div>--%>
	<%--</div>--%>
	<%--</div>--%>
	<%--</nav>--%>
	<div style="background-color: #FF6600;"><div class="wxGrapTitle">${isNew?"新建":"修改"}</div></div>
	<div class="container">
		<div class="row">
			<div class="col-md-4" style="background: #fff;padding-bottom: 13px;width: 32%;margin-right: 10px;">
				<div style="margin:0 auto;margin-top: 20px;height:50px;width: 360px;">
					<div class="wxGra_editTitle">公众号：${accountName}</div>
					<%--<div class="wxGra_pubdate">--%>
						<%--<span>发布时间：</span> <span> <input type="text"--%>
							<%--class="editorCalendar"> <img src="" alt=""--%>
							<%--class="search_cal" id="search_cal">--%>
						<%--</span>--%>
						<%--<!--日历-->--%>
						<%--<div id="wxGra_datapicker" style="display: none">--%>
							<%--<div id="ca"></div>--%>
							<%--<input type="text" id="dt" placeholder="trigger calendar"--%>
								<%--style="display: none;">--%>
							<%--<div id="dd"></div>--%>
						<%--</div>--%>
					<%--</div>--%>
				</div>
				<div class='panel panel-default' style="width:360px;border-color: #ECECEC;padding: 13px;margin: auto;">

					<ul id="message-list" class="list-group">
						<li draggable="true" id="message-menu-1"
							class="addTitle list-group-item message-edit text-center"
							style="padding: 0px;">
							<span class="ui-icon ui-icon-arrow-4 drag_icon dragAndDrop"></span>
							<div class='message-list-input message-list-cover'>
								<div class="deleteitem" onclick="deleteitem(this)"><a href="#" class="close">×</a></div>
								
								<!--<input type="text" class="message-cover-abstract" disabled />-->
								<input type="hidden" class="message-cover-url"> 
								<input type="hidden" class="message-cover-checkID"> 
								<input type="hidden" class="message-cover-checkLibID">
								<div class='tip'>
									<a href='#'> <span class=' glyphicon glyphicon-plus'></span>
										封面图片
									</a>
								</div>
								<div class="abstractAndTitleBox">
									<textarea class="message-cover-title" placeholder="点此输入标题" aria-describedby="basic-addon1" ></textarea>
									<!--<input type="text" class="message-cover-title" placeholder="点此输入标题" aria-describedby="basic-addon1" disabled>-->
									<!--<span class="glyphicon glyphicon-pencil editTitle" title="编辑标题"></span>-->	
									<textarea class="message-cover-abstract" placeholder="点此输入摘要"></textarea>
									<!--<span class="glyphicon glyphicon-pencil editAbstract" title="编辑摘要"></span>-->
								</div>
							</div>
							<div class="message-cover-toolbar">
								<span class='glyphicon glyphicon-pencil' title="编辑封面图片"></span>
								<span class='glyphicon glyphicon-cog' title="设置封面图片" style="margin-left: 10px;"></span>
							</div>
							<%--悬浮效果--%>
							<%--<div class="message-cover-mask">--%>
								<%--<span class="glyphicon glyphicon-pencil message-cover-edit">--%>
									<%--编辑内容</span>--%>
							<%--</div> --%>

						</li>

						<!--<li class="list-group-item message-add">
							<div class='message-list-input message-list-add'>
								<div class='tip'>
									<span class='glyphicon glyphicon-plus' style="color: #666;"></span>
								</div>
							</div>
						</li>-->
					</ul>
				</div>
				<div>
					<div class="detitBtn_box">
						<p id="edit-save-message" class="detit_btn edit_active">保存</p>
						<c:if test="${groupStatus == 0 && isCensorship }">
							<p id="edit-censorship-message" class="detit_btn edit_active">送审</p>
						</c:if>
						<p id="edit-preview-message" class="detit_btn edit_default">手机预览</p>
						<p id="edit-cancel-message" class="detit_btn edit_default" style="margin-right: 0;">取消</p>
					</div>
				</div>
			</div>
			<div class="col-md-8" style="padding: 0;background: #fff;">
				<%--hhr --%>
				<!--选稿区-->
				<div class="wxGra_artSelect">
					<div class="artSelect_head">
						<div>
							<div class="ser_group">
							<input type="text" placeholder="请输入搜索词" id="select_keyword">
							<input type="text" placeholder="请输入稿件ID" id="select_artID">
							<input type="text" placeholder="请输入作者" id="select_author">
							</div>
							<div class="wxGra_startTime">
								<%--<span>创建时间：</span> <span class="startTime_div"> <input--%>
									<%--type="text" id="select_beginTime" class="editorCalendar"> <img src="" alt=""--%>
									<%--class="startTime">--%>
							<div class="jeitem" style="float:left">
							<label class="jelabel" style=" width:80px;">创建时间:</label>
							<div class="jeinpbox" style=" width:150px;"><input type="text" class="jeinput" id="select_beginTime" placeholder=""></div>

							</div>

    <div class="jeitem" style="float:left">
    <%--<label class="jelabel">-</label>--%>
    <div class="jeinpbox" style=" width:150px;"><input type="text" class="jeinput" id="select_endTime" placeholder=""></div>
    </div>




    <%--</span> <span>-</span> <span> <input type="text"--%>
									<%--id="select_endTime" class="editorCalendar"> <img src="" alt=""--%>
									<%--class="endTime">--%>
								<%--</span>--%>

							</div>
							<span class="btnimg"><img src="../../xy/img/search.png"
								alt="" onclick="showListDetail()">
								<img src="../../xy/img/reload.png" onclick="termReload()" alt=""></span>

					<%--<div><span class="btn btn-default" id="material-reback">退回</span></div>--%>

					</div>

					</div>
					<!--table-->
					<div>
						<table id="select_table" cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<!--<th><input type="checkbox"></th>-->
									<th>标题</th>
									<th>作者</th>
									<th>最后处理人</th>
									<th>处理时间</th>
									<th>ID</th>
	                                <th>退回</th>
								</tr>
							</thead>
							<tbody id="list_tbody">

							</tbody>
						</table>
					</div>
					<%--分页	--%>
					<div class="selectartpager" style="display:none"></div>
				</div>

			</div>
		</div>

		<div class="row" style="margin: 0; display: none">
			<div id='edit-arrow' class='edit-arrow'>
				<span class='glyphicon glyphicon-triangle-left'></span> <em
					class='glyphicon glyphicon-triangle-left'></em>
			</div>
			<div id="message-edit" class='panel panel-default'>
				<div class="panel-heading">
					<span>文章内容</span> <span id="message-del"
						style='float: right; display: none;'><a
						style="color: #666; font-size: 12px;" href="#">删除本条</a></span>
				</div>
				<div class='panel-body'
					style="background: #FAFAFA; padding: 15px 5px;">
					<div class='col-md-6'>
						<div id="editor-area" class="panel panel-default "
							style="height: 653px; margin-bottom: 0;">
							<!--编辑区域-->
							<script id="editor"></script>
						</div>
					</div>
					<div class='col-md-6' style='padding: 0 0 0 0'>
						<ul class="nav nav-tabs text-center" role="tablist"
							style="border-bottom: none;">
							<li role="presentation" class="active"><a href="#sys-temp"
								aria-controls="sys-temp" role="tab" data-toggle="tab">系统模板</a></li>
							<li role="presentation"><a href="#material"
								aria-controls="messages" role="tab" data-toggle="tab">文章素材</a></li>
						</ul>
						<!-- <div class="col-md-10"> -->
						<div class="tab-content panel panel-default"
							style='margin-bottom: 0'>
							<%@include file="wx_template.html"%>

							<div role="tabpanel" class="tab-pane" id="material"
								style="height: 600px; overflow-y: auto; padding: 5px; font-size: 13px;">
								<!-- 文章素材 -->
								<div id='material-list'>
									<div id="material-pagination" class="text-center">
										<iframe
											src="../../e5workspace/DataMain.do?type=WXARTICLE&rule=wx_menuID_EQ_0_AND_wx_accountID_EQ_${accountID}
    &noOp=1&list=微信图文素材列表"
											style="width: 100%; height: 600px; border: 0px"></iframe>
									</div>
								</div>
								<div id='material-detail' class='panel panel-default'>
									<div class='panel-heading'>
										<button id='material-connect' class='btn btn-default'>选用</button>
										<button id='material-reback' class='btn btn-default'>退回</button>
										<button id='material-colse' type="button" class="close"
											data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
									</div>
									<div id='material-detail-body' class='panel-body'></div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<c:if test="${useMugeda}">
					<div class="edit-message-bottom">
						<span class="Original-link">原文链接（选填）</span> <input
							id="message-link" class="form-control" placeholder="原文链接"
							type="text" />
					</div>
					<div class="edit-message-bottom">
						<%--<button id="edit-preview-doc" type="button" class="btn btn-success ">预览稿件</button>--%>
						<button id="btnH5" type="button" class="btn btn-success ">添加H5</button>
					</div>
				</c:if>
			</div>
		</div>
<ul id="message-list-template" class="list-group">
	<li draggable="true" id="message-menu-1"
		class="addTitle list-group-item message-edit text-center"
		style="padding: 0px;">
		<span class="ui-icon ui-icon-arrow-4 drag_icon dragAndDrop"></span>
		<div class='message-list-input message-list-cover'>
			<div class="deleteitem" onclick="deleteitem(this)"><a href="#" class="close">×</a></div>
			
			<input type="hidden" class="message-cover-url"> 
			<input type="hidden" class="message-cover-checkID"> 
			<input type="hidden" class="message-cover-checkLibID">
			<div class='tip'>
				<a href='#'> <span class=' glyphicon glyphicon-plus'></span>
					封面图片
				</a>
			</div>
			<div class="abstractAndTitleBox">
				<textarea class="message-cover-title" placeholder="点此输入标题" aria-describedby="basic-addon1" ></textarea>
				<!--<span class="glyphicon glyphicon-pencil editTitle" title="编辑标题"></span>-->	
				<textarea class="message-cover-abstract" placeholder="点此输入摘要"></textarea>
				<!--<span class="glyphicon glyphicon-pencil editAbstract" title="编辑摘要"></span>-->
			</div>
		</div>
		<div class="message-cover-toolbar">
			<span class='glyphicon glyphicon-pencil' title="编辑封面图片"></span>
			<span class='glyphicon glyphicon-cog' title="设置封面图片" style="margin-left: 10px;"></span>
		</div>
	
	</li>
</ul>
	</div>
	<%@include file="../../xy/pic/inc/WXPic.inc"%>
	<!-- 预览消息弹出层 start -->
	<div class="modal fade" id="previewMessageModal" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header"
					style='padding: 10px; height: 45px; border-bottom: none; background: #DEF1FE;'>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close" style="margin-top: 0px;">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel"
						style="color: #333; float: left; line-height: 25px; font-weight: bold; font-size: 20px;">预览消息</h4>
				</div>
				<div class="modal-body" style='height: 350px; padding: 10px'>
					<div class="center-block" style="margin-top: 20%; width: 60%">
						<p id='preview-info'
							style="height: 30px; line-height: 30px; margin: 0;">关注公众号后，才能接收图文消息预览</p>
						<!--<input style="height:35px; line-height:35px;box-sizing:content-box;display: inline-block;"
    id="message-preview-wxname" class="form-control" placeholder="请输入微信号" type="text" />-->
						<input class="form-control" id="message-preview-wxname"
							type="text" placeholder="请输入微信号" />
						<div id='preview-error' style='color: red'></div>
					</div>
				</div>
				<div class="modal-footer" style="padding: 10px">
					<button id="message-preview-send" type="button"
						class="btn btn-primary">确定</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				</div>

			</div>
		</div>
	</div>
	<!-- 预览消息弹出层 end -->
	<!-- 提示弹出层 start -->
	<div id="confirm-dialog" class="modal fade" tabindex="-1" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header"
					style='padding: 10px; height: 45px; border-bottom: none; background: #DEF1FE;'>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close" style="margin-top: 0px;">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel"
						style="color: #333; float: left; line-height: 25px; font-size: 22px;">确认操作！</h4>
				</div>
				<div class="modal-body" style='height: 350px; padding: 10px'>
					<div class="center-block text-center"
						style="margin-top: 20%; width: 60%">
						<h3 id='confirm-info'>确定群发此消息？</h3>
						<div id='confirm-error' style='color: red'></div>
					</div>
				</div>
				<div class="modal-footer" style="padding: 10px">
					<button id="operation-confirm-btn" type="button"
						class="btn btn-primary">确定</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 提示弹出层 end -->
	<!-- 预览稿件 start -->
	<div id="previewDoc-dialog" class="modal fade" tabindex="-1"
		role="dialog">
		<div class="modal-dialog modal-sm" style="margin-top: 0">
			<div class="modal-content"
				style="width: 380px; height: 850px; background-image: url(../../../images/iphone5-frame.png); background-color: rgba(0, 0, 0, 0); border: none; box-shadow: none;">
				<div class="modal-body">
					<div
						style="font-weight: bold; color: black; position: absolute; left: 384px;">
						<button type="button"
							style="width: 30px; font-size: 24px; opacity: 0.4; border: 1px solid #000;"
							data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="center-block"
						style="width: 320px; height: 570px; margin: 100px 10px 10px 16px; background-color: #fff">
						<div id="previewDoc-content" class="preview-content"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 预览稿件 end -->
	</div>

	</div>

	<script>
	<%--hhr--%>
	<%--&lt;%&ndash;hhr加载右侧素材&ndash;%&gt;--%>
	<%--initMaterialArt();--%>
	<%--function initMaterialArt(){--%>

	<%--}--%>
	<%--hhr手写拖动--%>

    <%--//删除选取的稿件--%>
    <%--function deleteitem(item) {--%>
    <%--$(item).parent().remove()--%>
    <%--//获取删除的对象并把右侧已选状态移除--%>
    <%--var deleteid=$(item).parent().find(".message-list-sub .message-list-input").attr("data-checkID");--%>
    <%--$("#list_tbody tr[data-checkID="+deleteid+"]").find(".key_select").css({display:"none"});--%>
    <%--}--%>
    <%--//        给input添加onblurf--%>
    <%--messageInput();--%>
    <%--function messageInput(){--%>
    <%--$(".message-list-input").each(function(index,val){--%>
    <%--$(val).blur(function(){--%>
    <%--//不能为空格和空--%>
    <%--var currentTitle=$(this).val();--%>
    <%--if($(this).attr("title")&&$(this).attr("title").indexOf(currentTitle)!=-1){--%>
    <%--currentTitle=$(this).attr("title");--%>
    <%--}--%>
    <%--if(currentTitle.length>5){--%>
    <%--var gramCurTitle=currentTitle.slice(0,5)+"......";--%>
    <%--$(this).attr({"title":currentTitle});--%>
    <%--$(this).val(gramCurTitle)--%>
    <%--}else{--%>
    <%--$(this).attr({"title":currentTitle});--%>
    <%--$(this).val(currentTitle)--%>
    <%--}--%>

    <%--});--%>
    <%--$(val).focus(function(){--%>
    <%--var currentTitle=$(this).attr("title");--%>
    <%--if(!currentTitle){--%>
    <%--return--%>
    <%--}--%>
    <%--$(this).attr({"title":currentTitle});--%>
    <%--$(this).val(currentTitle)--%>
    <%--//                if(currentTitle.length>5){--%>
    <%--//                    var gramCurTitle=currentTitle.slice(0,5)+"......";--%>
    <%--//                    $(this).attr({"title":currentTitle});--%>
    <%--//                    $(this).val(gramCurTitle)--%>
    <%--//                }else{--%>
    <%--//                    $(this).val(currentTitle)--%>
    <%--//                    $(this).attr({"title":currentTitle});--%>
    <%--//                }--%>

    <%--})--%>
    <%--});--%>
    <%--$(".message-list-input input").each(function(index,val){--%>
    <%--$(val).blur(function(){--%>
    <%--var currentTitle=$(this).val();--%>
    <%--if($(this).attr("title")&&$(this).attr("title").indexOf(currentTitle)!=-1){--%>
    <%--currentTitle=$(this).attr("title");--%>
    <%--}--%>

    <%--if(currentTitle.length>5){--%>
    <%--var gramCurTitle=currentTitle.slice(0,5)+"......";--%>
    <%--$(this).attr({"title":currentTitle});--%>
    <%--$(this).val(gramCurTitle)--%>
    <%--}else{--%>
    <%--$(this).attr({"title":currentTitle});--%>
    <%--$(this).val(currentTitle)--%>
    <%--}--%>

    <%--});--%>
    <%--$(val).focus(function(){--%>
    <%--var currentTitle=$(this).attr("title");--%>
    <%--if(!currentTitle){--%>
    <%--return--%>
    <%--}--%>
    <%--$(this).attr({"title":currentTitle});--%>
    <%--$(this).val(currentTitle)--%>
    <%--//                 if(currentTitle.length>5){--%>
    <%--//                     var gramCurTitle=currentTitle.slice(0,5)+"......";--%>
    <%--//                     $(this).attr({"title":currentTitle});--%>
    <%--//                     $(this).val(gramCurTitle)--%>
    <%--//                 }else{--%>
    <%--//                     $(this).val(currentTitle)--%>
    <%--//                     $(this).attr({"title":currentTitle});--%>
    <%--//                 }--%>

    <%--})--%>

    <%--});--%>
    <%--}--%>



    <%--// 拖拽开始--%>
    <%--editDragF();--%>
    <%--function editDragF(){--%>
    <%--var listTr=document.querySelectorAll("#message-list li");--%>
    <%--var ofx=0,ofy=0;--%>
    <%--var dragObj=null;--%>
    <%--//拖动元素的标题和图片--%>
    <%--var dragObjInfact={};--%>
    <%--var prcho1sObj=null;--%>
    <%--//目标元素的标题和图片--%>
    <%--var prcho1sObjInfact={};--%>
    <%--var dragendflag;--%>

    <%--for(var i=0;i<listTr.length;i++){--%>
    <%--var iObj=listTr[i];--%>
    <%--iObj.ondragstart=function (e) {--%>

    <%--ofx=e.offsetX;--%>
    <%--ofy=e.offsetY;--%>
    <%--dragObj=this;--%>
    <%--// $("#moveitem").css({display:"block"});--%>
    <%--dragendflag=0;--%>
    <%--};--%>
    <%--iObj.ondrag=function (e) {--%>
    <%--var x=e.pageX;--%>
    <%--var y=e.pageY;--%>
    <%--y-=ofy;--%>
    <%--x-=ofx;--%>

    <%--};--%>
    <%--iObj.ondragend=function (e) {--%>

    <%--//                目标对象和拖拽对象不是左侧列表时不做操作--%>
    <%--if($(dragObj).hasClass("message-add")){--%>
    <%--return--%>
    <%--}--%>
    <%--if($(prcho1sObj).hasClass("message-add")){--%>
    <%--return--%>
    <%--}--%>
    <%--if(!$(dragObj).hasClass("list-group-item")){--%>
    <%--return--%>
    <%--}--%>
    <%--if(!$(prcho1sObj).hasClass("list-group-item")){--%>
    <%--return--%>
    <%--}--%>

    <%--var currentitle=$(this).find(".message-list-input").attr("title");--%>
    <%--if(currentitle){--%>
    <%--dragObjInfact.img=$(this).find(".message-list-thumbnail img").attr("src");--%>
    <%--dragObjInfact.title= $(this).find(".message-list-input").attr("title");--%>
    <%--dragObjInfact.dataId= $(this).find(".message-list-input").attr("data-checkID");--%>
    <%--dragObjInfact.materialID= $(this).find(".message-list-input").attr("data-materialID");--%>
    <%--dragObjInfact.materialLibID= $(this).find(".message-list-input").attr("data-materialLibID");--%>
    <%--dragObjInfact.checkLibID= $(this).find(".message-list-input").attr("data-checkLibID");--%>
    <%--}else{--%>
    <%--dragObjInfact.img=$(this).find(".message-list-input img").attr("src");--%>
    <%--dragObjInfact.title= $(this).find(".message-list-input").find("input").attr("title");--%>
    <%--dragObjInfact.dataId= $(this).find(".message-list-input").find("input").attr("data-checkID");--%>
    <%--dragObjInfact.materialID= $(this).find(".message-list-input").find("input").attr("data-materialID");--%>
    <%--dragObjInfact.materialLibID= $(this).find(".message-list-input").find("input").attr("data-materialLibID");--%>
    <%--dragObjInfact.checkLibID= $(this).find(".message-list-input").find("input").attr("data-checkLibID");--%>
    <%--}--%>
    <%--//        目标对象是否为空如果为空则不替换文字--%>
    <%--var currentitleRight=$(prcho1sObj).find(".message-list-input").attr("title");--%>
    <%--if(currentitleRight){--%>
    <%--currentitleRight=$(prcho1sObj).find(".message-list-input").val();--%>
    <%--}else{--%>
    <%--currentitleRight=$(prcho1sObj).find(".message-list-input input").val();--%>
    <%--}--%>

    <%--if(!dragObjInfact.title){--%>
    <%--return--%>
    <%--}--%>

    <%--if(!currentitleRight){--%>
    <%--return--%>
    <%--}--%>


    <%--//拿到对象后交换标题和图片--%>
    <%--//交换（给拖动元素复目标元素的值）--%>
    <%--var gramPrcho1sObjInfact="";--%>
    <%--if(prcho1sObjInfact.title&&prcho1sObjInfact.title.length>5){--%>
    <%--gramPrcho1sObjInfact=prcho1sObjInfact.title.slice(0,5)+"......";--%>
    <%--}else{--%>
    <%--gramPrcho1sObjInfact=prcho1sObjInfact.title;--%>
    <%--}--%>
    <%--var gramDragObjInfact="";--%>
    <%--if(dragObjInfact.title&&dragObjInfact.title.length>5){--%>
    <%--gramDragObjInfact=dragObjInfact.title.slice(0,5)+"......";--%>
    <%--}else{--%>
    <%--gramDragObjInfact=dragObjInfact.title;--%>
    <%--}--%>

    <%--if(currentitle){--%>
    <%--$(this).find(".message-list-thumbnail img").attr("src",prcho1sObjInfact.img);--%>
    <%--$(this).find(".message-list-input").val(gramPrcho1sObjInfact);--%>
    <%--$(this).find(".message-list-input").attr({"title":prcho1sObjInfact.title,"data-checkID":prcho1sObjInfact.dataId,"data-materialID":prcho1sObjInfact.materialID,"data-materialLibID":prcho1sObjInfact.materialLibID,"data-checkLibID":prcho1sObjInfact.checkLibID});--%>
    <%--}else{--%>
    <%--$(this).find(".message-list-input img").attr("src",prcho1sObjInfact.img);--%>
    <%--$(this).find(".message-list-input input").val(gramPrcho1sObjInfact);--%>
    <%--$(this).find(".message-list-input input").attr({"title":prcho1sObjInfact.title,"data-checkID":prcho1sObjInfact.dataId,"data-materialID":prcho1sObjInfact.materialID,"data-materialLibID":prcho1sObjInfact.materialLibID,"data-checkLibID":prcho1sObjInfact.checkLibID});--%>
    <%--}--%>
    <%--//继续交换（给目标元素替换拖动元素的值）--%>
    <%--var prccurrentitle=$(prcho1sObj).find(".message-list-input").attr("title");--%>
    <%--var prcholObjId=$(prcho1sObj).attr("id");--%>
    <%--if(prccurrentitle){--%>
    <%--$("#"+prcholObjId).find(".message-list-thumbnail img").attr("src",dragObjInfact.img);--%>
    <%--$("#"+prcholObjId).find(".message-list-input").val(gramDragObjInfact);--%>
    <%--$("#"+prcholObjId).find(".message-list-input").attr({"title":dragObjInfact.title,"data-checkID":dragObjInfact.dataId,"data-materialID":dragObjInfact.materialID,"data-materialLibID":dragObjInfact.materialLibID,"data-checkLibID":dragObjInfact.checkLibID});--%>
    <%--}else{--%>
    <%--$("#"+prcholObjId).find(".message-list-input img").attr("src",dragObjInfact.img);--%>
    <%--$("#"+prcholObjId).find(".message-list-input input").val(gramDragObjInfact);--%>
    <%--$("#"+prcholObjId).find(".message-list-input input").attr({"title":dragObjInfact.title,"data-checkID":dragObjInfact.dataId,"data-materialID":dragObjInfact.materialID,"data-materialLibID":dragObjInfact.materialLibID,"data-checkLibID":dragObjInfact.checkLibID});--%>
    <%--}--%>

    <%--//            拖拽结束调用blur事件--%>
    <%--$(dragObj).blur();--%>
    <%--$(prcho1sObj).blur();--%>
    <%--};--%>
    <%--}--%>

    <%--// 右侧拖拽开始--%>
    <%--var listTrRight=document.querySelectorAll("#list_tbody tr");--%>
    <%--var ofxRight=0,ofyRight=0;--%>
    <%--var dragObjRight=null;--%>
    <%--var prcho1sObjRight=null;--%>
    <%--var dragendflagRight;--%>
    <%--for(var i=0;i<listTrRight.length;i++){--%>
    <%--var iObj=listTrRight[i];--%>
    <%--iObj.ondragstart=function (e) {--%>
    <%--ofxRight=e.offsetX;--%>
    <%--ofyRight=e.offsetY;--%>
    <%--dragObjRight=this;--%>
    <%--// $("#moveitem").css({display:"block"});--%>
    <%--dragendflagRight=0;--%>
    <%--};--%>
    <%--iObj.ondrag=function (e) {--%>
    <%--var x=e.pageX;--%>
    <%--var y=e.pageY;--%>
    <%--y-=ofyRight;--%>
    <%--x-=ofxRight;--%>

    <%--$(dragObjRight).find(".list_item").text();--%>

    <%--};--%>
    <%--iObj.ondragend=function (e) {--%>
    <%--console.log("dragend")--%>
    <%--console.log(prcho1sObjRight)--%>
    <%--// if(dragendflagRight==1){--%>
    <%--$(dragObjRight).find(".list_item").text();--%>
    <%--var currentText=$(dragObjRight).find(".list_item").text();--%>
    <%--var curTextGram="";--%>
	<%--&lt;%&ndash;crrentId为checkID&ndash;%&gt;--%>
    <%--var crrentId=$(dragObjRight).attr('data-checkID');--%>
    <%--var materialID=$(dragObjRight).attr('data-materialID');--%>
    <%--var materialLibID=$(dragObjRight).attr('data-materialLibID');--%>
    <%--var checkLibID=$(dragObjRight).attr('data-checkLibID');--%>
    <%--//--%>
    <%--// （需要判断字数，字数过多为省略号）--%>
    <%--if(currentText.length>5){--%>
    <%--curTextGram=currentText.slice(0,5)+"......"--%>
    <%--}--%>
    <%--// 判断是添加还是替换--%>
    <%--if($(prcho1sObjRight).hasClass('message-add')){--%>
		<%--$(".message-add .glyphicon-plus").click();--%>

		<%--alert("添加end")--%>
		<%--&lt;%&ndash;再次绑定拖拽事件&ndash;%&gt;--%>
		<%--editDragF();--%>
		<%--messageInput();--%>
    <%--}else{--%>
			<%--if($(prcho1sObjRight).hasClass("list-group-item")) {--%>
				<%--var currentitle = $(prcho1sObjRight).hasClass("text-center");--%>

				<%--if (!currentitle) {--%>
					<%--$(prcho1sObjRight).find(".message-list-input").val(curTextGram);--%>
					<%--var needRemoveId=$(prcho1sObjRight).find(".message-list-input").attr("data-checkID");--%>

					<%--//                            从右侧找到当前目标对象的data-checkID移除右侧已选状态--%>
					<%--$("#list_tbody tr[data-checkID="+needRemoveId+"]").find(".key_select").css("display","none")--%>
					<%--$(prcho1sObjRight).find(".message-list-input").attr({--%>
					<%--"title": currentText,--%>
					<%--"data-checkID": crrentId,--%>
					<%--"data-materialID": materialID,--%>
					<%--"data-materialLibID": materialLibID,--%>
					<%--"data-checkLibID": checkLibID--%>
					<%--});--%>
					<%--$("#list_tbody tr[data-checkID="+crrentId+"]").find(".key_select").css("display","inline-block")--%>
				<%--} else {--%>
					<%--var needRemoveId=$(prcho1sObjRight).find(".message-list-input input").attr("data-checkID");--%>
					<%--//                            从右侧找到当前目标对象的data-checkID移除右侧已选状态--%>
					<%--$("#list_tbody tr[data-checkID="+needRemoveId+"]").find(".key_select").css("display","none")--%>
					<%--$(prcho1sObjRight).find(".message-list-input input").val(curTextGram);--%>
					<%--$(prcho1sObjRight).find(".message-list-input input").attr({--%>
					<%--"title": currentText,--%>
					<%--"data-checkID": crrentId,--%>
					<%--"data-materialID": materialID,--%>
					<%--"data-materialLibID": materialLibID,--%>
					<%--"data-checkLibID": checkLibID--%>
					<%--});--%>
					<%--$("#list_tbody tr[data-checkID="+crrentId+"]").find(".key_select").css("display","inline-block")--%>
				<%--}--%>

			<%--}--%>
	<%--editDragF();--%>
	<%--messageInput();--%>
    <%--}--%>
    <%--//  }--%>
    <%--}--%>
    <%--}--%>





    <%--var prcho1s = document.querySelectorAll("#message-list li");--%>
    <%--for (var i = 0; i < prcho1s.length; i++) {--%>
    <%--var prcho1 = prcho1s[i]--%>
    <%--prcho1.ondragenter = function (e) {--%>

    <%--dragendflag = 1;--%>
    <%--dragendflagRight = 1;--%>
    <%--console.log("进入 flag");--%>
    <%--console.log(dragendflag);--%>
    <%--};--%>
    <%--prcho1.ondragover = function (e) {--%>
    <%--dragendflag = 1;--%>
    <%--dragendflagRight = 1;--%>
    <%--prcho1sObj=this;--%>
    <%--prcho1sObjRight = this;--%>
    <%--var currentitle=$(this).find(".message-list-input").attr("title");--%>
    <%--if(currentitle){--%>
		<%--prcho1sObjInfact.img=$(this).find(".message-list-thumbnail img").attr("src");--%>
		<%--prcho1sObjInfact.title= $(this).find(".message-list-input").attr("title");--%>
		<%--prcho1sObjInfact.dataId= $(this).find(".message-list-input").attr("data-checkID");--%>
		<%--prcho1sObjInfact.materialID= $(this).find(".message-list-input").attr("data-materialID");--%>
		<%--prcho1sObjInfact.materialLibID= $(this).find(".message-list-input").attr("data-materialLibID");--%>
		<%--prcho1sObjInfact.checkLibID= $(this).find(".message-list-input").attr("data-checkLibID");--%>
    <%--}else{--%>
		<%--prcho1sObjInfact.img=$(this).find(".message-list-input img").attr("src");--%>
		<%--prcho1sObjInfact.title= $(this).find(".message-list-input input").attr("title");--%>
		<%--prcho1sObjInfact.dataId= $(this).find(".message-list-input input").attr("data-checkID");--%>
		<%--prcho1sObjInfact.materialID= $(this).find(".message-list-input input").attr("data-materialID");--%>
		<%--prcho1sObjInfact.materialLibID= $(this).find(".message-list-input input").attr("data-materialLibID");--%>
		<%--prcho1sObjInfact.checkLibID= $(this).find(".message-list-input input").attr("data-checkLibID");--%>
    <%--}--%>



    <%--// console.log(2);--%>
    <%--};--%>
    <%--prcho1.ondragleave = function (e) {--%>

    <%--dragendflag = 0;--%>
    <%--dragendflagRight = 0;--%>

    <%--console.log("离开flag")--%>
    <%--console.log(dragendflag)--%>
    <%--// dragendflag=true;--%>
    <%--return--%>
    <%--};--%>
    <%--}--%>




    <%--}--%>
	// 分页
	/*$('.selectartpager').pagination({
	pageCount: 5,
	jump: true,
	coping: true,
	homePage: '首页',
	endPage: '末页',
	prevContent: '上页',
	nextContent: '下页',
	callback: function (api) {
		showListDetail(api.getCurrent())
	}
	});*/
    </script>
</body>
</html>
