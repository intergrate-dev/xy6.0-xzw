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
    <title>微信图文详情</title>

	<link rel="stylesheet" type="text/css" href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="../script/jquery-ui-1.11.4.custom/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css" href="css/wx.css"/>

	<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-ui-1.11.4.custom/jquery-ui.min.js"></script>
	<script type="text/javascript" src="../script/jquery-ui-1.11.4.custom/jquery-ui-i18n.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.js"></script>
	<script type="text/javascript" src="../ueditor/ueditor.config.wx.js"></script>
	<%--<script type="text/javascript" src="script/ueditor/ueditor.config.js"></script>--%>
	<%--<script type="text/javascript" src="../ueditor/ueditor.all.min.js"></script>--%>
	<script type="text/javascript" src="script/editor_api.js"></script>
	<%--<script type="text/javascript" charset="utf-8" src="../ueditor/ueditor.custom.js"></script>--%>
	<script type="text/javascript" src="script/wxGroupView.js"></script>
	<style>
	p{
		margin:0;
	}
	.navbar{
		background: #e97a32;
		margin-bottom:30px;
	}
	#proof_tip{
		float: left;
		width: 30%;
		display: none;
	}
	#proof_tip span{
		font-size: 12px;
	    padding: 6px 5px;
	    float: left;
	}
	.wx_btn_a {
	width: 70px;
	height: 30px;
	background: #FF6600;
	color: #fff;
	border: none;
	float: left;
	margin-right: 10px;
	border-radius: 5px;
	}
	.wx_btn_c{
		/*border-radius: 5px;*/
		border: 1px solid #ececec;
		background: #fff;
		color: #333;
		font-size: 12px;
	}
	#edit-cancel-message{
		/*background:url("/xy/xy/article/image/wxclose.png") no-repeat;*/
		width: 70px;
	    height: 30px;
	    display: inline-block;
	    float: right;
	    margin-left: 20px;
	}
	.tab_box ul{
		margin-bottom: 0;
	}
	<%--内容--%>
	.tabutation_title{
	padding: 5px 10px;
	border-bottom: 1px solid #e4e4e4;
	}
	#preview_box{
	width: 700px;
	margin: 0 auto;
	}
	#preview_box:after,.tabutation_title:after{
	display: block;
	clear: both;
	content: "";
	}
	#preview_box{
	position: relative;
	}
	.arrowright{
	position: absolute;
	left: 302px;
	top: 90px;
	}
	#tabutation{
	float: left;
	width:300px;
	border: 1px solid #E5E5E5;
	/*border-bottom: none;*/
	}
	#artDetail{
	float: right;
	}
	.tabutation_square{
	float: left;
	display: inline-block;
	padding: 6px;
	border: 1px solid #eee;
	}
	.tabutation_time{
	float: right;
	font-size:12px;
	}
	.tab_banner{
	position: relative;
	}
	.tabBanner_title{
	position: absolute;
	bottom: 0;
	width: 100%;
	padding: 5px 10px;
	z-index: 20;
	color:#fff;
	}
	.tabBanner_title_single {
		margin: 10px;
	}
	.tabBanner_abstract {
		margin: 10px;
		color: #ccc;
		height: 38px;
		display: -webkit-box;
	    -webkit-box-orient: vertical;
	    -webkit-line-clamp: 2;
	    overflow: hidden;
	}
	.tabBanner_titlepop{
	position: absolute;
	bottom: 0;
	width: 300px;
	background: #000;
	opacity: 0.3;
	z-index: 10;
	height: 32px;
	}
	.tabBanner_con{
	height: 160px;
	width: 100%;
	background: url("banner.png") no-repeat;
	}
	.tabBanner_con img,.tabList_img img{
	width:100%;
	height:100%;
	}
	.tab_box_list{
	overflow: hidden;
	border-bottom:1px solid #E5E5E5;
	}
	.tab_box_list>div:after{
	display: block;
	content: "";
	clear: both;
	}
	.tab_box_list>div{
	height: 100px;
	box-sizing: border-box;
	padding: 10px;
	}
	.tabList_left{
	margin-right: 10px;
	float: left;
	width: 180px;
	box-sizing: border-box;
	}
	.tabList_img{
	float: left;
	width: 85px;
	height: 81px;
	}
	.tabList_img img{
	width: 100%;
	height: 100%;
	}
	.option_btn{
		position: absolute;
		right: -71px;
		text-align: center;
	}
	.option_btn p{
	width: 60px;
	font-size: 12px;
	}
	.option_btn1{
	bottom: 340px;
	}
	.option_btn1 p{
	    background: #fff;
	    width: 71px;
	    height: 50px;
	    line-height: 50px;
	    text-align: center;
	    border: 1px solid #B2B2B2;
	    border-top-right-radius: 25px;
	    border-bottom-right-radius: 25px;
	    font-size: 14px;
	}
	.option_btn2{
	bottom: 50px;
	}
	.option_btn2 p{
		background: #F2F2F2;
	    width: 70px;
	    height: 50px;
	    margin-bottom: 10px;
	    border-top-right-radius: 25px;
	    border-bottom-right-radius: 25px;
	    font-size: 14px;
	    color: #000;
	}
	.option_btn2 p span{
	display: inline-block;
	}
	.option_btn2 p span.span1{
	margin-top: 6px;
	}
	#option_close,#annotationInfo{
	width: 50px;
	height: 40px;
	text-align: center;
	background: #ffffff;
	border-radius: 5px;
	}
	#option_close{
	line-height: 40px;
	}
	#annotationInfo span.span1{
	margin-top: 3px;
	}
	#logTable{
	position: absolute;
	width: 510px;
	height: 200px;
	bottom: 69px;
	right: -570px;
	}
	#examine{
	border-radius: 5px;
	background: #FF6000;
	width: 50px;
	padding: 5px 0;
	line-height: 14px;
	text-align: center;
	border: 1px solid #B2B2B2;
	margin-bottom: 10px;
	color: #fff;
	}
	#preview_box{
	width: 700px;
	margin: 0 auto;
	}
	#wx_content{
	/*iphone6背景图的大小要和这个width保持一致*/
	height: 564px;
	width: 362px;
	background: url("bg.png");
	position: relative;
	box-sizing: border-box;
	border: 1px solid #797979;
	}
	#wxBox{
	height: 100%;
	position:relative;
	}
	#wxBox_title{
	font-size: 15px;
	font-weight: 700;
	background:#000000 ;
	color: #ffffff;
	font-size: 14px;
	text-align: center;
	height:40px ;
	line-height:40px ;
	color:#fff;
	positon:absolute;
	top:0;
	left:0;
	}
	#wxBox_content{
		overflow-y: auto;
		height: 522px;
		padding: 5px 10px;
		overflow-x: hidden;
	}
	#logTable,#drawHistoryTable{
		position: absolute;
		width: 510px;
		/*height: 200px;*/
		bottom: -145px;
		right: -365px;
	}
    #histotrytable{
        width:430px;
        border:none;
    }
    #drawHistoryTable{
        height:260px;
    }
    #histotrytable>tbody>tr>td, #histotrytable>tbody>tr>th, #histotrytable>tfoot>tr>td, #histotrytable>tfoot>tr>th, #histotrytable>thead>tr>td, #histotrytable>thead>tr>th{
        border:none;
    }
    #histotrytable>thead>tr>td{
        color:#ccc;
        font-size:14px;
    }
    <%--历史版本分页--%>
    .pagebtn_border{
    border:1px solid #ddd;
    border-radius: 5px;
    padding: 5px 10px;
    margin-right: 10px;
    }
    #pagejumpval input{
    padding: 0 5px;
    display: inline-block;
    border-radius: 5px;
    width: 50px;
    height: 30px;
    outline: none;
    }
    #pagecotainner{
    float: right;
    margin: 20px 10px 10px
    }
    <%--流程记录--%>
    .table>thead>tr>td{
        font-weight:700;
        font-size:14px;
    }
    .table>tbody>tr>td, .table>tbody>tr>th, .table>tfoot>tr>td, .table>tfoot>tr>th, .table>thead>tr>td, .table>thead>tr>th{
        text-align: center;
        border-top: 1px solid #ddd;
    }
    #pageright{
        padding: 5px 3px;
    }
    #pageright img{
        position: relative;
        left: 5px;
        top: -1px;
    }
    #doThrough .span1{
    position: relative;
    line-height: 0px;
    top: -10px;
    }
     #doThrough .doThroughspan{
	    position: relative;
	    line-height: 0;
	    top: -40px;
     }
     .tip_word{
 	    text-align: center;
	    background: #fff;
	    margin-right: 10px;
	    color: #e97a32;
	    letter-spacing: 1px;
	    border: 1px solid #ececec;
    	box-sizing: border-box;
     }
     .wx_check{
     	color: #fff;
     	/*font-size: 13px;*/
     }
	</style>
    <script>
		var globalData={};
		var groupArticleInfo = {}; //图文组稿件信息
    	$(document).ready(function(){
    		var h=$(".message-cover-title").outerHeight();
    		$('.message-cover-title').focus(function(){
    			$(this).css("height","50px");
    		})
    		$('.message-cover-title').blur(function(){
    			$(this).css("height", h);
    		})
    		$("#doThrough").hide(); $("#doReject").hide();//隐藏审核通过 和 驳回 按钮
    		$("#alldoThrough").hide(); $("#alldoReject").hide();//隐藏全部审核通过 和 全部驳回 按钮
    		
    		// 默认全屏
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

		var wxGroup_data = {
			accountID : "${accountID}",
			docLibID : "${docLibID}",
			groupID : "${groupID}",
			isNew : "${isNew}",
			UUID : "${UUID}",
			groupStatus : "${groupStatus}",
			isCensorshipThrough : "${isCensorshipThrough}",
			isReback : "${isReback}"
		};
		var article = {
			siteID: "${siteID}"
		};
		var chosenImg, notUseSameSetting = true;
		function resetImageInfo(_src){
			chosenImg.src = _src;
		}

		var param = {
			groupLibID : wxGroup_data.docLibID,
			groupID : wxGroup_data.groupID
		};
	</script>
</head>
<body>
	<%--编辑区内容以便复制--%>
	<textarea cols="20" rows="10" id="wxGroupTextarea" style="opacity:0;position:absolute">用户定义的代码区域</textarea>
	<nav class="navbar">
		<div class="container">
			<div class='row'>
			<div class="col-md-12">
                <div class="button-fluid">
					<%--敏感词--%>
					<div id="proof_tip">
						<span class="wx_check" style="font-size: 13px;">校对提示：</span>
						<span class="wx_tip tip_word">校对敏感词</span>
						<span class="wx_sensitiveWord tip_word">敏感词</span>
						<span class="wx_illegalWord tip_word">非法词</span>
						<span class="wx_spellError tip_word">拼写错误</span>
					</div>
					<!--<span id="edit-cancel-message"></span>-->
					<div style="float:right">
						<button id="alldoThrough" type="button" class="wx_btn_a" title="保存">全部通过</button>
						<button id="alldoReject" type="button" class="wx_btn_a" title="保存">全部驳回</button>
						<button id="edit-preview-message" type="button" class="wx_btn_c" title="发送预览消息到手机">手机预览</button>						<button id="edit-cancel-message" type="button" class="wx_btn_c" title="取消">关闭</button>

					</div>
                	
					<%--<button id="edit-preview-message"  type="button" class="wx_btn_b" title="发送预览消息到手机">手机预览</button>--%>
                </div>
            </div>
		</div>
		</div>
	</nav>
	<%--内容--%>
	<div id="container">
	<div id="preview_box">
	<div id="tabutation">

	</div>
	<div class="arrowright"><img src="../../xy/article/image/arrowright.png" alt=""></div>
	<div id="artDetail">
	<div id="wx_container">
	<div id="wx_content">
	<div id="wxBox">
	<div id="wxBox_title">
	<c:out value="${orignal.catName}"/>
	</div>
	<div id="wxBox_content">
	<c:out value="${orignal.topic}" escapeXml="false"/>
	<c:out value="${orignal.content}" escapeXml="false"/>
	</div>
	</div>
	<div class="option_btn option_btn1">
	<p id="option_btn" style="margin-bottom: 10px">修改</p>
	<p id="doThrough" style="margin-bottom:10px"><span class="span1">审核</span></br><span class="doThroughspan">通过</span></p>
	<p id="doReject">驳回</p>

	</div>
	<div class="option_btn option_btn2">
	<p id="copyContent"><span class="span1">全文</span></br><span>复制</span></p>
	<p id="drawHistoryInfo"><span class="span1">历史</span></br><span>版本</span></p>
	<p id="flowInfo"><span class="span1">流程</span></br><span>记录</span></p>
	<%--<button id="edit-preview-history"  type="button" class="wx_btn_b" title="历史版本">历史版本</button>--%>
    <%--<button id="edit-preview-flow"  type="button" class="wx_btn_b" title="流程记录">流程记录</button>--%>
	<!--<p id="option_close">关闭</p>-->
	</div>
	<!--流程记录-->
	<div id="logTable" style="display:none"></div>
	<div id="drawHistoryTable" style="display:none"></div>
	</div>
	</div>
	</div>
	</div>
	</div>

	<%--<table id="historyTable"></table>--%>
	<%--<div class="checkArt">--%>
		<%--<div id="logTable"></div>--%>
	<%--</div>--%>

    <div class="container" >
        <div class="row">
			<div class="col-md-4" style="display:none">
				<div class='panel panel-default'>
					<div class="panel-heading"></div>
				 	<ul id="message-list" class="list-group">
				 		<li id="message-menu-1" class="list-group-item message-edit text-center" style="padding: 0px;">
                            <div class='message-list-input message-list-cover' style="background: #ececec;border: none;">
                                <%--<input type="text" class="message-cover-title" maxlength="64"  placeholder="点此输入标题" aria-describedby="basic-addon1">--%>
                                <input type="hidden" class="message-cover-checkID">
                                <input type="hidden" class="message-cover-checkLibID">
                                <div class='tip'>
                                    <a href='#'>
                                        <span class=' glyphicon glyphicon-plus'></span>
                                        封面图片
                                    </a>
                                </div>
                            </div>
                            <div class="message-cover-mask">
                                 <span class="glyphicon glyphicon-pencil message-cover-edit"> 详情</span>
							</div>
						</li>

				 		<li class="list-group-item message-add">
							<div class='message-list-input message-list-add'>
								<div class='tip'>
									<span class='glyphicon glyphicon-plus' style="color: #666;"></span>
								</div>
							</div>
						</li>
				 	</ul>
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
	<!--  
	<div id="main">
		<div class="advices">留言\意见: <span id="censorshipReasonCount"></span></div>
		<textarea id="censorshipReason" class="text" placeholder="留言\意见"></textarea>
		<iframe name="frmCensorship" id="frmCensorship" src="" class="frm"></iframe>
		<div style="text-align: center;">
			<input type='button' id="doSubmit1" value='确定'/>
			<input type='button' id="doCancel1" value='取消'/>
		</div>
	</div>-->
	<script>
	<%--时间转换--%>
	function test(){
		alert(1);
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
	<%--初始化列表--%>
	function innitTabList(data){
	var date=formatDate(new Date(data[0].date),"MM-dd hh:mm:ss");
    var listHtml="";
		if(data.length == 1) {
			listHtml+='<p class="tabutation_title">'+
				'			<span class="tabutation_square"></span>'+
				'			<span class="tabutation_time">'+date+'</span>'+
				'			</p>'+
				'			<div class="tab_banner" data-checkID="'+data[0].checkID+'" data-checkLibID="'+data[0].checkLibID+
				'			" data-groupArticleID="'+data[0].groupArticleID+'" data-groupArticleLibID="'+data[0].groupArticleLibID+
				'			" data-status="'+data[0].status+
				'			" onclick="showCurrentList(this)">'+
				'				<p class="tabBanner_con">'+
				'					<img src="../image.do?path='+data[0].pic+'">'+
				'				</p>'+
				'				<p class="tabBanner_title_single">'+data[0].title+'</p>'+
				'				<p class="tabBanner_abstract">'+data[0].abstract+'</p>'+
				'				<%--index--%>'+
				'				<div class="tabListIndex" style="display:none">0</div>'+
				'			</div>';
		}else {
			listHtml+='<p class="tabutation_title">'+
				'			<span class="tabutation_square"></span>'+
				'			<span class="tabutation_time">'+date+'</span>'+
				'			</p>'+
				'			<div class="tab_banner" data-checkID="'+data[0].checkID+'" data-checkLibID="'+data[0].checkLibID+
				'			" data-groupArticleID="'+data[0].groupArticleID+'" data-groupArticleLibID="'+data[0].groupArticleLibID+
				'			" data-status="'+data[0].status+
				'			" onclick="showCurrentList(this)">'+
				'				<p class="tabBanner_con">'+
				'					<img src="../image.do?path='+data[0].pic+'">'+
				'				</p>'+
				'				<p class="tabBanner_title">'+data[0].title+'</p>'+
				'				<p class="tabBanner_titlepop"></p>'+
				'				<%--index--%>'+
				'				<div class="tabListIndex" style="display:none">0</div>'+
				'			</div>';
		}
	if(data.length>=2){
	     listHtml+='<div class="tab_box">'+
					'<ul>';
		for(var i=1;i<data.length;i++){
			if(data[i].pic.indexOf("image.do?path=") != -1){
				data[i].pic = data[i].pic.split("image.do?path=")[1];
			}
			listHtml+='<li class="tab_box_list" data-checkID="'+data[i].checkID+'" data-checkLibID="'+data[i].checkLibID+
						'" data-groupArticleID="'+data[i].groupArticleID+'" data-groupArticleLibID="'+data[i].groupArticleLibID+
						'" data-status="'+data[i].status+
						'" onclick="showCurrentList(this)">'+
						'					<div>'+
						'					<div class="tabList_left">'+
						data[i].title+
						'					</div>'+
						'					<div class="tabList_img">'+
						'					<img src="../image.do?path='+data[i].pic+'">'+
						'					</div>'+
						'					</div>'+
						'					<%--index--%>'+
						'					<div class="tabListIndex" style="display:none">'+i+'</div>'+
						'</li>';


		}
	    listHtml+='</ul>'+
	               '</div>';
	   }


	    $("#tabutation").html(listHtml);
		$(".tab_banner").click();
	}

	function initButton(data){
		if(wxGroup_data.groupStatus==1){
			var throughCount = 0;
			var rejectCount = 0;
			for(var i=0;i<data.length;i++){
				if(data[i].status==4){//图文组中存在驳回的稿件
					rejectCount ++;
				}
				if(data[i].status==2){//图文组中存在审核通过的稿件
					throughCount ++;
				}
			}
			//图文组中不存在被驳回的稿件
			if(rejectCount < data.length && throughCount < data.length){
				if(wxGroup_data.isCensorshipThrough){
					$("#alldoReject").show();
				}
				if(wxGroup_data.isReback){
					$("#alldoThrough").show();
				}
			}
		}
	}
	
	<%--历史版本和流程记录点击事件--%>
	$("#drawHistoryInfo").click(function(){
		var historyPage='../article/history.html?checkID='+groupArticleInfo.checkID+'&checkLibID='+groupArticleInfo.checkLibID+"&wx_groupid="+wxGroup_data.groupID+"&type=1";
		window.open(historyPage,'_blank')
	});

	<%--流程记录--%>
	$("#flowInfo").click(function(){
		if($("#logTable").css("display")=='block'){
			$("#logTable").css({display:'none'})
		}else{
			$("#logTable").css({display:'block'})
		}

	});
	$("#logTable").click(function(){
		$("#logTable").css({display:'none'})
	});
	
	//初始化
	$.get('GroupArticles.do', param ,function(data){
		console.log("333");
		globalData=data;
		console.log(data);
		initButton(data);
		innitTabList(data);
	});
	//点击显示当前详情
	function showCurrentList(item){
		$(".tab_banner").removeClass("currentOn");
		if($(".tab_box_list").length > 0) {
			$(".tab_box_list").removeClass("currentOn");
		}
		$(item).addClass("currentOn");
		groupArticleInfo.checkID = $(item).attr("data-checkid");
		groupArticleInfo.checkLibID = $(item).attr("data-checklibid");
		groupArticleInfo.groupArticleID = $(item).attr("data-grouparticleid");
		groupArticleInfo.groupArticleLibID = $(item).attr("data-grouparticlelibid");
		groupArticleInfo.item = item;
		var arrowheight=$(item).offset().top-51;
		$.get('GroupArticles.do', param ,function(data){
			globalData=data;
			if(wxGroup_data.groupStatus == 1 && globalData[currentIndex].status == 1){
				if(wxGroup_data.isCensorshipThrough){
					$("#doThrough").show(); 
				}
				if(wxGroup_data.isReback){
					$("#doReject").show();
				}
			}else {
				$("#doThrough").hide();
				$("#doReject").hide();
			}
		});
	    $(".arrowright").css({top:arrowheight+"px"});
		var currentIndex=$(item).find(".tabListIndex").text();
		$("#wxBox_title").html(globalData[currentIndex].title);
		$("#wxBox_content").html(globalData[currentIndex].content);
	//	alert(globalData[currentIndex].status)
		
		$("#logTable").css({"display": "none"})
	}
	window.onload = function(){
		$("embed").attr('width','100%')
	}
	</script>
</body>
</html>
