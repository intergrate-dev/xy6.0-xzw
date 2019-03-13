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
    <title>微信源稿编辑</title>

	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link rel="stylesheet" type="text/css" href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="../script/jquery-ui-1.11.4.custom/jquery-ui.min.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css" />
    <link rel="stylesheet" type="text/css" href="../wx/css/wx.css"/>
	<style type="text/css">
		body{
			color:#323232;
			overflow-x: hidden;
		}
		.container{
			padding: 0;
		}
		.row{
			margin: 0;
		}
		.checkArt{
			padding: 20px;
			box-sizing: border-box;
			padding-top: -10px;
			margin: -10px 0;
		}
		.table-striped{
			border:1px solid #ddd;
		}
		.mr20{
			margin-right: 20px;
		}
		.checkChoice,.advices{
			margin: 10px 0;
		}
		.li label{
			color: #333;
		}
		#logTable table tbody td:nth-child(1){
			width: 80px;
		}
		#logTable table tbody td:nth-child(2){
			width: 145px;
		}
		#logTable table tbody td:nth-child(3){
			width: 170px;
		}
	.navbar{
		border-bottom: 1px solid #ddd;
		background: #e97a32;
		margin-bottom:0;
		/*position: fixed;*/
		left: 0;
		top: 0;
		width: 100%;
		z-index: 2222;
	}
	#wx_nav{
		margin-right: 10px;
		width: 95px;
		border-bottom: none;
		float: left;
		background: #fff;
		height: 652px;
		border-right: 1px solid #ddd;
		display: none;
	}
	.panel-body #wx_nav a{
	padding: 10px 10px;
	display: block;
	text-align: left;
	text-decoration: none;
	}
	#wx_nav >div.active > a, #wx_nav >div.active > a:hover, #wx_nav >div.active > a:focus{
	background: rgb(250, 250, 250);

	}
	.nav-pills{
	background: rgb(250, 250, 250);
	}
	.nav-pills>li+li{
	 margin-left:0px;
	}
	.nav-pills>li>a{
	 border-radius:0;
	}
	.option_btn2{
	bottom: 180px;
	}
	.option_btn2 p{
	border-radius: 5px;
	background: #F1F2F1;
	width: 50px;
	height: 50px;
	margin-bottom: 10px;
	border: 1px solid #ddd;

	}
	.option_btn2 p span{
	display: inline-block;
	}
	.option_btn2 p span.span1{
	margin-top: 5px;
	}
	.option_btn2 p span.span2{
	line-height:50px;
	display:inline-block;
	height:50px;
	}
	#editor_btn{
	position: absolute;
	width: 275px;
	height: 100%;
	border: 1px solid #ccc;
	border-left: none;
	left: 500px;
	top: 0px;
	}
	#editor_btn p {
		float: left;
		margin-left: 15px;
		margin-top: 15px;
	}
	#editor_btn p span{
	font-size:12px;
	width: 100%;
	text-align: center;
	display: inline-block;
	}
	#editorProof_btn{
	padding-left: 8px;
	border-left: 1px solid #ddd;
	left: 1008px;
	top: 87px;
	z-index: 1000;
	position: fixed;
	}
	#editorProof_btn div{
	background:#faa424;
	padding: 0px 8px;
	margin-bottom: 4px;
	color: #fff;
	}
	#ueditor_0{
	border-right: 1px solid #ddd;
	}
	#proof_tip{
		float: left;
		width:395px;
		margin-top: 9px;
		display: none;
	}
	#proof_tip span:first-child{
		border: none;
	    background: none;
	    width: 70px;
	    font-size: 13px;
    	color: #fff;
	}
	#proof_tip span{
		font-size: 12px;
	    height: 30px;
	    display: inline-block;
	    line-height: 30px;
	    width: 60px;
	    text-align: CENTER;
	    border: 1px solid #ececec;
	    background: #fff;
	    letter-spacing: 1px;
	}
	<%--.wx_tip{--%>
	<%--color: #323232;--%>
	<%--background: #F1F2F1;--%>
	<%--}--%>
	.wx_sensitiveWord{
	color: #FEDD55;
	background: #fff;
	border: 1px solid #FEDD55;
	}
	.wx_illegalWord{
	color: #FF2323;
	background: #F1F2F1;
	border:1px solid #FF2323;
	}
	.wx_spellError{
	color: #1818FF;
	background: #F1F2F1;
	border:1px solid #1818FF;
	}
	.wxhead_input{
		float: left;
		margin-top:9px;
	}
	.wxhead_input label{
		color: #fff;
	    font-weight: 100;
	    margin-right: 10px;
	    line-height: 30px;
	}
	.wxhead_input select{
		height: 30px;
		width: 120px;
		border: 1px solid #ddd;
		font-size: 12px;
		margin-right: 10px;
	}
	.wxhead_input .author{
	width: 80px;
	height: 30px;
	border: 1px solid #ddd;
	padding: 0 5px;
	}
	.wxhead_input .author::-webkit-input-placeholder{
	color:#AEA8A2;
	}
	.wxhead_input .selectBtn{
		padding: 3px 5px;
		background: #fff;
		outline: none;
		border: 1px solid #ececec;
		border-radius: 0;
		font-size: 12px;
		margin-right: 10px;
    	margin-top: 3px;
	}
	.mr5{
		margin-right: 5px;
	}
	.wx_btn_a{
		width: 70px;
	    height: 30px;
	    background: #faa424;
	    color: #333;
	    border: none;
	    float: left;
	    margin-right: 10px;
	    border-radius: 0;
	    background: #fff;
	    font-size: 12px;
	    border:1px solid #ddd;
	}
	.wx_btn_c{
		border-radius: 0;
		border: 1px solid #ececec;
		background: #fff;
		color:#73797B;
		font-size: 12px;
	}
	#drawHistoryTable{
	left:580px;
	position: absolute;
	bottom: 60px;
	z-index: 1000;
	}
	#originLink{
	/*left: 260px;*/
	/*position: absolute;*/
	/*bottom: 330px;*/
	/*z-index: 1000;*/
	}
	#abstract{
		/*left: 260px;
		position: absolute;
		bottom: 150px;
		z-index: 1000;*/
	}
	#abstract span{
		display:block;
		height: 30px;
		line-height: 30px;

	}
	#abstract_message{
		/*width:500px;*/
		border: 1px solid #ccc;
	    border-radius: 5px;
	}
	.edit-message-bottom{
	/*width:500px;*/
	}
	#topicPicDiv{
		width: 160px;
		position: relative;
	}
	#topicPicDiv img{
		max-width: 100%;
	}
	.icon-remove {
	    position: absolute;
	    right: 0;
	    top: 0;
	    z-index: 100;
	    display: none;
	    background: url(../img/close.png) no-repeat;
	    width: 18px;
	    height: 18px;
	}
	#edui2{
	padding-right:60px;
	}
	.nav-pills > li.active > a, .nav-pills > li.active > a:hover, .nav-pills > li.active > a:focus{
	color:#78777A;
	background: #fff url("image/tabdown.png") no-repeat;
	border-top:1px solid #ddd;
	border-left:1px solid #ddd;
	border-right:1px solid #ddd;
	border-bottom:1px solid #fff;
	background-position: right center;
	}
	.nav-pills li a{
	border-bottom:1px solid #ddd;
	/*width: 65px;*/
	padding: 10px 18px;
	}
	.edit-tab{
	padding: 0 2px 2px 2px;
	}
	<%--流程记录--%>
	.table>thead>tr>td{
	font-weight:700;
	font-size:14px;
	}
	.table>tbody>tr>td, .table>tbody>tr>th, .table>tfoot>tr>td, .table>tfoot>tr>th, .table>thead>tr>td, .table>thead>tr>th{
	text-align: center;
	}
	#flex_btn {
		position: absolute;
		width: 20px;
		height: 83px;
		line-height: 77px;
		font-size: 12px;
		background: #e4e4e4;
		text-align: center;
		left: -27px;
		top: 0;
		cursor: pointer;
	}
	.titleMapBox {
		padding: 10px;
		cursor: pointer;
	}
	.titleMap {
		width: 200px;
		height: 100px;
		margin-top: 10px;
	    text-align: center;
	    line-height: 100px;
	    font-size: 30px;
	    color: #dedede;
		border: 1px dashed #dedede;
	}
	.textLink {
		padding: 10px;
	}
	#originLink {
		margin: 10px 0 0 0;
	}
	.abstractBox {
		padding: 10px;
	}
	#abstract {
		margin: 10px 0 0 0;
	}
	.btn-default{
		color:#333;
	}
	button{
		border-radius: none;
	}
	#cover-author{
		height: 30px;
	    border: 1px solid #ededed;
	    padding-left: 5px;
	    box-sizing: border-box;
	    width: 70px;
	    outline: none;
	    margin-right: 10px;
	}
	.btnGroup .active{
		border: 1px solid #f6caad;
    	background: #fcf2e9;
	}
	ul.tabs{overflow:hidden;}
	.tabs li{float:left; width: 50px;height: 50px;margin:15px 0 10px 15px;border: 1px solid #ddd;border-radius: 5px;
	background: #F1F2F1;font-size: 12px;text-align: center;}
	.tabs li .span1{line-height:50px;}
	.tabs li .span2{line-height:25px;}
	#tab3{height:562px;overflow-y:auto;}
	
	/*编辑器中的标题样式*/
	#cover-title-box {
		text-align: center;
		margin-top: 10px;
	}
	#cover-title {
		border:1px dashed #ddd;
		width: 400px;
		color:#000;
		height: 35px;
		padding:0 15px;
		margin-bottom: 10px;
		text-align: center;
	}
	#btnTitleAdv {
		display: inline-block;
	    padding: 5px 12px;
	    margin-bottom: 0;
	    font-size: 14px;
	    line-height: 20px;
	    margin-left: 5px;
	    color: #333;
	    text-align: center;
	    text-shadow: 0 1px 1px rgba(255,255,255,.75);
	    /*vertical-align: middle;*/
	    cursor: pointer;
	    background-color: #f5f5f5;
	    background-image: -moz-linear-gradient(top,#fff,#e6e6e6);
	    background-image: -webkit-gradient(linear,0 0,0 100%,from(#fff),to(#e6e6e6));
	    background-image: -webkit-linear-gradient(top,#fff,#e6e6e6);
	    background-image: -o-linear-gradient(top,#fff,#e6e6e6);
	    background-image: linear-gradient(to bottom,#fff,#e6e6e6);
	    background-repeat: repeat-x;
	    border: 1px solid #ccc;
	    border-color: #e6e6e6 #e6e6e6 #bfbfbf;
	    border-color: rgba(0,0,0,.1) rgba(0,0,0,.1) rgba(0,0,0,.25);
	    border-bottom-color: #b3b3b3;
	    -webkit-border-radius: 4px;
	    -moz-border-radius: 4px;
	    border-radius: 4px;
	}
	.rContent {
		height: 628px;
		overflow: hidden;
		overflow-y: auto;
	}
	
	.table {
		margin-bottom: 0;
	}
	
	</style>
	<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-ui-1.11.4.custom/jquery-ui.min.js"></script>
	<script type="text/javascript" src="../script/jquery-ui-1.11.4.custom/jquery-ui-i18n.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.js"></script>
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="../../e5script/e5.js"></script>
	<script type="text/javascript" src="../ueditor/ueditor.config.wx.js"></script>
	<!--<script type="text/javascript" src="script/ueditor/ueditor.config.js"></script>-->
	<script type="text/javascript" src="../ueditor/ueditor.all.min.js"></script>
	<script type="text/javascript" src="script/editor_api.js"></script>
	<%--<script type="text/javascript" charset="utf-8" src="../ueditor/ueditor.custom.js"></script>--%>
	<script type="text/javascript" src="../ueditor/ueditor.parse.js"></script>
	<script type="text/javascript" src="script/originalWX.js"></script>

   <script>
	   var wxGroup_data = {
			catID : "${catID}",
			docLibID : "${docLibID}",
			docID : "${docID}",
			isNew : "${isNew}",
			status : "${status}",
			UUID : "${UUID}",
			siteID: "${siteID}",
			hasSensitive: "${hasSensitive}",
			hasIllegal: "${hasIllegal}",
			docidForhisAndFlow: "${docidForhisAndFlow}",
			docLibIdForhisAndFlow: "${docLibIdForhisAndFlow}"
		};
		var article = {
			siteID: "${siteID}",
			userName: "<c:out value="${userName}"/>",
			userId: "<c:out value="${userId}"/>"
		};
		var chosenImg, notUseSameSetting = true;
		
		function resetImageInfo(_src){
			chosenImg.src = _src;
		}
		
		var getParam = function(key) {
			var lot = location.search;
			if(lot.indexOf(key) != -1) {
				var reg = new RegExp(".*" + key + "\\s*=([^=&#]*)(?=&|#|).*", "g");
				return decodeURIComponent(lot.replace(reg, "$1"));
			}else {
				return 0;					
			}
		};
    	$(document).ready(function(){
    		
			var wx_groupid = getParam("wx_groupid");
    		
    		var h=$(".message-cover-title").outerHeight();
    		$('.message-cover-title').focus(function(){
    			$(this).css("height","50px");
    		})
    		$('.message-cover-title').blur(function(){
    			$(this).css("height", h);
    		});

			<%--hhr--%>
			$("#wx_nav div").each(function(index, val) {
				$(val).click(function() {
					$("#wx_nav div").each(function(indexinner, valinner) {
						if($(valinner).hasClass("active")) {
							$(valinner).removeClass("active")
						}
					})
					$(val).addClass("active")
				})
			});

			<%--历史版本和流程记录点击事件--%>
			$("#drawHistoryInfo").click(function(){
				if(wxGroup_data.docidForhisAndFlow && wxGroup_data.docLibIdForhisAndFlow) {
					var historyPage='../article/history.html?checkID='+wxGroup_data.docidForhisAndFlow+'&checkLibID='+wxGroup_data.docLibIdForhisAndFlow+"&wx_groupid="+wxGroup_data.catID+"&type=1";
				window.open(historyPage,'_blank');
				}else {
					var historyPage='../article/history.html?checkID='+wxGroup_data.docID+'&checkLibID='+wxGroup_data.docLibID+"&wx_groupid="+wxGroup_data.catID+"&type=1";
				window.open(historyPage,'_blank');
				}


				return false;
	     	});

			<%--时间处理--%>
			function formatCSTDate(strDate,format){
				return formatDate(new Date(strDate),format);
			}

			function formatDate(date,format){
				var paddNum = function(num){
				num += "";
				return num.replace(/^(\d)$/,"0$1");
			};
			//指定格式字符
			var cfg = {
				yyyy : date.getFullYear() //年 : 4位
				,yy : date.getFullYear().toString().substring(2)//年 : 2位
				,M  : date.getMonth() + 1  //月 : 如果1位的时候不补0
				,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0
				,d  : date.getDate()   //日 : 如果1位的时候不补0
				,dd : paddNum(date.getDate())//日 : 如果1位的时候补0
				,hh : paddNum(date.getHours())  //时
				,mm : paddNum(date.getMinutes()) //分
				,ss : paddNum(date.getSeconds()) //秒
			};
			format || (format = "yyyy-MM-dd hh:mm:ss");
				return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
			}
			//历史版本
			historyShow();
			function historyShow(){
				var page = 1;
				var theURL = "../../xy/article/HistoryInfo.do?DocIDs=" + ${docID}
				+ "&Page=" + page;
				$.ajax({url:theURL, async:false, success:function(data){
					drawHistoryTable(data.list);
				}});
			}

			//渲染历史版本
			function drawHistoryTable(data) {
				if(data.length > 0) {
					var logTableHtml = "<table class='table ' border='1' style='width:510px;background:#fff'><thead><tr><td>审核人</td>" +
						"<td>审核操作</td><td>审核时间</td></tr></thead><tbody>";
					for(var i = 0; i < data.length; i++) {
						logTableHtml += "<tr><td>" + data[i].operator + "</td>" +
							"<td>" + data[i].operation + "</td>" +
							"<td sytle='width:200px'>" + data[i].created + "</td>";
					}
					logTableHtml += "</tbody></table>";
					$("#drawHistoryTable").append(logTableHtml);
				}
			}
			//点击按钮显示和隐藏
			$("#flex_btn").on("click", function () {
				$("#wx_left").toggle(50, function () {
					if($("#flex_btn").html() == "&lt;&lt;"){
						$("#flex_btn").html(">>");
//						var _mgleft=($(window).width()-775)/2+'px';
						var _mgleft=(1362-775)/2+'px';
						var _btnMgLeft=$(window).width()>1362 ? ($(window).width()-1362)/2 + (1362-775)/2 +'px': _mgleft;
						$("#wx_right").css('margin-left',_mgleft)
						$("#flex_btn").css('left','-'+ _btnMgLeft)
					}else {
						$("#flex_btn").html("<<");
						$("#wx_right").css('margin-left','27px')
						$("#flex_btn").css('left','-27px')
					}
				});
			})
			//判断当前元素是显示还是隐藏wx_right
			function showOrHide(ele){
				if($(ele).css("display")=='block'){
					$(ele).css({display:'none'})
				}else{
					$(ele).css({display:'block'})
				}
			}
			
			//判断是否是新建微信稿件，如果是显示流程记录为空
			$("#flowInfo").click(function(){
				showOrHide('.rContentBox');
				if(wxGroup_data.isNew == 'true'){
					$('.rContent').html('无流程记录');
					$('.rContentBox').addClass('newRcontent');
				}else{
					$('.rContentBox').addClass('oldRcontent');
				}
				$('.rightTabBox').hide();
			})
			
			//点击预览判断其他兄弟内容元素是否显示
			$("#edit-preview-message").click(function(){
				showOrHide('.rContentBox');
			})
			
			//点击批注按钮设置其他功能隐藏
			$("#edit-commit-message").click(function(){
				showOrHide('.rightTabBox');
				if($(".list-group").html() != "") {
					$(".pzTip").hide()
				}
				$('.rContentBox').hide();
			})
			
			//点击预览按钮隐藏其他功能
			$("#edit-preview-message").click(function(){
				$('.rContentBox,.rightTabBox').hide();
				
			})
			
			$(".closeBtn").click(function(){
				$(this).parent().hide();
			})
			
			top.window.moveTo(0,0);  
			var docElm = document.documentElement;
			//W3C  
			if (docElm.requestFullscreen) {  
				top.window.resizeTo(screen.availWidth,screen.availHeight);  
			}
			//FireFox  
			else if (docElm.mozRequestFullScreen) {  
				top.window.resizeTo(screen.availWidth,screen.availHeight);  
			}
			//Chrome等  
			else if (docElm.webkitRequestFullScreen) {  
				 top.window.resizeTo(screen.availWidth,screen.availHeight); 
			}
			//IE
			else if (document.all)   
			{  
			    top.window.resizeTo(screen.availWidth,screen.availHeight);  
			}
			
    	});
	</script>
</head>
<body>
	<nav class="navbar">
		<div class='header clearfix'>
			<div id="proof_tip">
				<span class="wx_tip">校对提示：</span>
				<span class="wx_sensitiveWord">敏感词</span>
				<span class="wx_illegalWord">非法词</span>
				<span class="wx_spellError">拼写错误</span>
			</div>
			<!--头部右侧代码-->
			<div class="pull-right">
				<div class="wxhead_input">
					<label class="pull-left">
						<input type="checkbox" id="copyright" checked="false">
						声明原创
					</label>
					<select class="pull-left">
						<option>行业分类</option>
					</select>
					<input class="pull-left" class="author" id="cover-author" type="text" placeholder="作者" style="display: none;">
					<input class="selectBtn pull-left" type="button" value="选择" style="display: none;">
                	<%-- status: 0草稿 1待审核 2未签发 3已签发 4已驳回 5已提交 --%>
					<button id="edit-save-message"  type="button" class="wx_btn_a" title="保存">保存</button>
					<c:if test="${status == 0 && isCanSubmit }">
						<button id="edit-submit-message"  type="button" class="wx_btn_a" title="提交">提交</button>
					</c:if>
					<c:if test="${status != 3 && isCheckThrough }">
						<button id="edit-check-message"  type="button" class="wx_btn_a" title="校对完成">校对完成</button>
					</c:if>
					<c:if test="${status == 0 || status == 4 || status == 5 }">
						<c:if test="${isCensorship }">
							<button id="edit-censorship-message"  type="button" class="wx_btn_a" title="送审">送审</button>
						</c:if>
						<c:if test="${isPubCensorship }">
							<button id="edit-censorpub-message"  type="button" class="wx_btn_a" title="预签并送审" style="width:85px">预签并送审</button>
						</c:if>
					</c:if>
					
					<button id="edit-cancel-message" type="button" class="wx_btn_c" title="取消">关闭</button>
                </div>
			</div>
			<!--头部右侧代码-->
	            
        </div>
	</nav>
    <div class="container" >
        <div class="row">
			<%--其他信息--%>
            <div class="col-md-12" style="padding-left: 0;">
                <div class="row" style="margin: 0;">
                	<div id='edit-arrow' class='edit-arrow'>
                		<%--<span class='glyphicon glyphicon-triangle-left'></span>--%>
                		<%--<em class='glyphicon glyphicon-triangle-left'></em>--%>
                	</div>
                	<div id="message-edit" class='panel panel-default' style="border:none;box-shadow: none;">
                		 <%--<div class="panel-heading">--%>
                		 <%--<span>文章内容</span>--%>
                		 <%--<span id="message-del" style='float:right;display:none; '><a style="color: #666; font-size:12px ;" href="#">删除本条</a></span>--%>
                		 <%--</div>--%>
                		 <div class='panel-body' style=" padding:0 0;">
		                	<div id="wx_left" class='col-md-5' style="padding:0;border: 1px solid #ddd;border-right:none;background:rgb(250, 250, 250);">
									<div id="wx_nav" class="nav  nav-tabs  text-center" role="tablist" style="border-bottom: none;float:left;">
										<div role="presentation" class="active" style="float:auto">
											<a href="#sys-temp" aria-controls="sys-temp" role="tab" data-toggle="tab">系统模板</a>
										</div>
										<div role="presentation"  style="float:auto">
											<a href="#art-lib" aria-controls="art-lib" role="tab" data-toggle="tab">文章素材</a>
										</div>
										<div role="presentation"  style="float:auto">
										<a href="#pic-lib" aria-controls="art-lib" role="tab" data-toggle="tab">图片库</a>
										</div>
										<div role="presentation"  style="float:auto">
										<a href="#video-lib" aria-controls="art-lib" role="tab" data-toggle="tab">视频库</a>
										</div>
										<div role="presentation"  style="float:auto">
										<a href="#temp-lib" aria-controls="art-lib" role="tab" data-toggle="tab">模板</a>
										</div>
									</div>
									<div class="tab-content panel panel-default" style='margin-bottom: 0;float:left;width:400px;border-top:none;margin-left: 80px;'>
								<%@include file="../wx/wx_template.html"%>

								<div role="tabpanel" class="tab-pane" id="material" style="height: 600px ;overflow-y:auto; padding: 5px; font-size:13px ;">
								<div id='material-detail' class='panel panel-default'>
								<div class='panel-heading'>
								<button id='material-connect' class='btn btn-default'>选用</button>
								<button id='material-colse' type="button" class="close" data-dismiss="modal" aria-label="Close"  >
								<span aria-hidden="true">&times;</span>
								</button>
								</div>
								<div id='material-detail-body' class='panel-body'>

								</div>
								</div>
								</div>
								</div>
		                	</div>
		                	<div id="wx_right" class='col-md-5' style='margin-left:27px;padding:0 0 0 0;psotion:relative;background:#fff'>
								<div id="editor-area" class="panel panel-default " style="height: 653px;margin-bottom: 0;border-top:none;width:500px">
								<%--校对和批注		--%>
								<%--<div id="editorProof_btn">--%>
								<%--<div>校对</div>--%>
								<%--<div>批注</div>--%>
								<%--</div>--%>
								<!--编辑区域-->
								<script id="editor"></script>
								</div>

								<%--编辑区button--%>
							<div id="editor_btn">
								<div style="height: 100%; overflow: auto;">
									<div class="option_btn option_btn2 clearfix">
										<div class="clearfix btnGroup" style="border-bottom: 1px solid #000;height: 81px;">
											<p id="edit-preview-message" class="active"><span class="span2">预览</span></p>
											<p id="drawHistoryInfo"><span class="span1">历史</span></br><span>版本</span></p>
											<p id="flowInfo"><span class="span1">流程</span></br><span>记录</span></p>
											<!--批注-->
											<p id="edit-commit-message"><span class="span2">批注</span></p>
											<!--批注-->
										</div>
										<!--选项卡-->
										<div class="showElements">
											<!--第一个-->
											<div>
												<div class="titleMapBox">
													<span>标题图</span>
													<div id="topicPicDivAdd" class="titleMap" onclick="channel_frame.picTopic()">
														<span>+</span>
													</div>
													<div id="topicPicDiv" style="display: none;" onclick="channel_frame.picTopic()">
														<img id="picSmall" itype="small" src="../image.do?path=" />
														<span class="icon-remove"></span>
													</div>
												</div>
												<div class="abstractBox">
													<div id="abstractBtn"><span class="span2">摘要</span></div>
													<div id="abstract" class="edit-message-bottom">
														<!--<span class="abstract_title">添加摘要</span>-->
														<textarea id="abstract_message" name="摘要" cols="30" rows="7"></textarea>
													</div>
												</div>
												<div class="textLink">
													<div id="originLinkBtn"><span class="span1">原文链接</span></div>
													<div id="originLink" class="edit-message-bottom">
														<!--<span class="Original-link">原文链接（选填）</span>-->
														<input id="cover-url" class="form-control" placeholder="选填" type="text" />
													</div>
												</div>
												<c:if test="${useMugeda}">
													<div class="edit-message-bottom" style="margin-top:20px;">
														<%--<button id="edit-preview-doc" type="button" class="btn btn-success ">预览稿件</button>--%>
														<button id="btnH5" type="button" class="btn btn-success ">添加H5</button>
													</div>
												</c:if>
												<%-- status: 0草稿 1待审核 2未签发 3已签发 4已驳回 5已提交 --%>
												<c:if test="${status == 1 || status == 2 || status == 3 }">
													<p id="pubShow"><span class="span1">签发</span></br><span>情况</span></p>
												</c:if>
											</div>
											<!--第一个-->
											<!--<div style="display: none;">我是第二个</div>-->
											<!--<div id="logTable" style="display: none;">我是第三个</div>-->
											<%--<div style="display: none;">--%>
												<!--<div class="rightTab" id="tab3" style="display:none;">
													<div class="pzTip">暂时无批注</div>
													<div class="list-group">
										
													</div>
												</div>-->
											<%--</div>--%>
										</div>
										
										<!--选项卡-->
										<!--显示内容-->
										<div class="rContentBox">
											<img class="closeBtn" src="./image/wxclose.png"/>
											<div class="rContent">
												
											</div>
										</div>
										
										<div class="rightTabBox">
											<img class="closeBtn" src="./image/wxclose.png"/>
											<div class="rightTab" id="tab3">
												<div class="pzTip">暂时无批注</div>
												<div class="list-group">
													
												</div>
											<%--</div>--%>
										</div>
										<!--显示内容-->
									</div>
								</div>
							</div>
								<%--历史版本--%>
								<div id="drawHistoryTable" style="display:none"></div>
								<%--流程记录--%>
								
	                		</div>
	                		<div id="flex_btn"><<</div>
                	</div>

                </div>
            </div>

        </div>
		<%@include file="../../xy/pic/inc/WXPic.inc"%>
        <!-- 预览消息弹出层 start -->
        <div class="modal fade" id="previewMessageModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        	<div class="modal-dialog" role="document">
        		<div class="modal-content">
        			<div class="modal-header" style='padding:10px;height: 45px;border-bottom: none; background: #DEF1FE;'>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close" style="margin-top: 0px;"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title" id="myModalLabel" style="color: #333;float: left;line-height:25px; font-weight:bold;font-size:20px ; ">预览消息</h4>
					</div>
        			<div class="modal-body" style='height: 350px;padding:10px'>
        				<div class="center-block" style="margin-top:20%;width:60%">
        					<p id='preview-info' style="height: 30px;line-height: 30px; margin: 0;">关注公众号后，才能接收图文消息预览</p>
        					<!--<input style="height:35px; line-height:35px;box-sizing:content-box;display: inline-block;" id="message-preview-wxname" class="form-control"  placeholder="请输入微信号" type="text" />-->
        					<input class="form-control" id="message-preview-wxname" type="text" placeholder="请输入微信号" />
        					<div id='preview-error' style='color:red'></div>
        				</div>
        			</div>
        			<div class="modal-footer" style="padding:10px">
						<button id="message-preview-send" type="button" class="btn btn-primary">确定</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					</div>

        		</div>
        	</div>
        </div>
        <!-- 预览消息弹出层 end -->
        <!-- 预览稿件 start -->
        <div id="previewDoc-dialog" class="modal fade" tabindex="-1" role="dialog" >
        	<div class="modal-dialog modal-sm" style="margin-top:0">
        		<div class="modal-content" style="width:380px;height: 850px;background-image:url(../../../images/iphone5-frame.png);background-color: rgba(0,0,0,0);border: none;box-shadow:none; ">
					<div class="modal-body">
						<div style="font-weight: bold; color: black;position: absolute; left: 384px;">
						<button type="button" style="width:30px; font-size:24px; opacity: 0.4;border: 1px solid #000;" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						</div>
        				<div  class="center-block" style="width:320px;height:570px;margin: 100px 10px 10px 16px;background-color: #fff">
        					<div id="previewDoc-content" class="preview-content">

        					</div>
        				</div>
        			</div>
        		</div>
        	</div>
        </div>
        <!-- 预览稿件 end -->
    </div>

	</div>
	<script type="text/javascript">
		$(function(){
			$(".option_btn .btnGroup p").click(function(){
				$(this).addClass('active select').siblings().removeClass('active select');
			})
		})
	</script>
</body>
</html>
