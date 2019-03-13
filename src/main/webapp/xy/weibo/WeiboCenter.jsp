<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-COMPATIBLE" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1">
	<title>微博发布系统</title>
	<link rel="stylesheet" type="text/css" href="./script/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="./weibo/css/jquery-sinaEmotion-2.1.0.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="./weibo/sudoku/control/css/sudokuUpload.css"/>
	<link rel="stylesheet" type="text/css" href="./weibo/css/weiboCenter.css"/>        
	<script type="text/javascript" src="./script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="./weibo/script/jquery-sinaEmotion-2.1.0.js"></script>
	<script type="text/javascript" src="./weibo/sudoku/core/sudokuFile.js"></script>
	<script type="text/javascript" src="./weibo/sudoku/control/js/sudokuUpload.js"></script>
	<script type="text/javascript" src="./weibo/sudoku/core/sudoku.js"></script>
	<script type="text/javascript" src="./weibo/script/weiboCenter.js"></script>   
</head>
<style>
	.doclist{
		width: 1000px;
	}
	.doclist tbody tr > td{border-right: 1px solid #ddd;}
	#main_search{width: 1000px;}
	#searchBtnArea .searchListGroup{width: 75px;}
	.select{padding:0 0px; border-radius: 3px;}
	.custform-controls label{padding: 0 10px;}
</style>
<body>
	<div id="wb-content">
		<div>
			<div id="wb-home" class='wwa-tab tab-active'> 
				<!-- 编辑器开始 -->
				<div id="wb-editor">
					<div class="content">
						<form id="addWeiboForm" action="" method="post" role="form">
							<input id="code" type="hidden" value="WCOL"/>
							<input id="docId" type="hidden" value=""/>
							<input id="docLibID" type="hidden" value="38"/>
							<input id="weiboType" type="hidden" value="1"/>
							<input id="groupID" type="hidden" value=""/>
							<input id="groupName" type="hidden" value=""/>
							<input id="groupHead" type="hidden" value=""/>
							<input id="isPublish" type="hidden" value="1"/>
							<input id="FVID" type="hidden" value="${weibo.FVID}"/>
							<input id="begin" type="hidden" value=""/>
							<input id="count" type="hidden" value=""/>
							<input id="timingFlag" type="hidden" value="0"/>
	
							<div class="form-group">
								<textarea class="form-control" rows="4" id="weiboText"></textarea>
							</div>
							<div class="form-group domain">
								<div class="left">
									<span class="">
										<a class="face" href="javascript:void(0);" id="homeFace">
											<img src="./weibo/images/6.png" alt=""/>
											<span>表情</span>
										</a>
									</span>
									<span class="">
										<a class="uploadPic" href="javascript:void(0);" id="imgBtn">
											<img src="./weibo/images/7.png" alt=""/>
											<span>图片</span>
										</a>
									</span>
									<span class="">
										<a class="" href="javascript:void(0);" id="topicBtn">
											<img src="./weibo/images/9.png" alt=""/>
											<span>话题</span>
										</a>
									</span>
									<span class="">
										<a class="" id="timingsend" href="javascript:void(0);">
											<img src="./weibo/images/11.png" alt=""/>
											<span>定时发</span>
										</a>
									</span>
									<span class="display-off" id="sendtime">&nbsp;&nbsp;发送时间:
										<input type="text" name="timing" id="timing" class="Wdate" readonly="readonly"
										value="<%=com.founder.e5.commons.DateUtils.format("yyyy-MM-dd HH:mm")%>"/>
									</span>
								</div>
								<div class="pull-right">
									 <span>
										<span id="wordType">还可以输入</span>
										<span id="wordCheck" style="color: #ff0000; font-weight: bold;">140</span>个字符
									</span>
									<button class="btn btn-default btn-lg" id="pubButton" type="submit" onclick="weiboSubmit(1);return false;">发布</button>
								</div>		                       		 
							</div>
						</form>
					</div>
				</div>
				<!-- 编辑器结束 -->
			</div>
		</div>
		<div class="weibo-feed"> 
			<div id="weibo-comments" class="weibo-comments" style="display:none;">
				<div id="weibo-comments-list" class="weibo-comments-list">
					<div id="weibo-comments-box" class="left weibo-comments-box">
						<div class="clearfix">
							<span class="left span">评论</span>
							<span class="right"><img class="closeBtn" src="./weibo/images/5.png" alt="关闭"/></span>
						</div>
						<form id="addCommentForm" action="" method="post" role="form1">
							<div>
								<input type="hidden" id="cBelongtoDocId" name="cBelongtoDocId" value="" />
								<input class="Long" type="text" id="commentText" />
							</div>
							<div class="editor">
								<div class="left">
									<span class="">
										<a class="face" href="javascript:void(0)" id="commentFace">
										<img src="./weibo/images/6.png" alt=""/>
										<span>表情</span>
										</a>
									</span>
								</div>
								<div class="right">
									<button type="button" class="btn btn-default commentBtn">评论</button>
								</div>
							</div>
						</form>
						<div class="left weibo-comment-1" >
						</div>
						<div class="weibo-page display-off">
							<span class="weibo-prepage" >
								<button class="" id="weibo-comment-prepage" onclick="preCommentsPage()">上一页</button>
							</span>
							<span class="weibo-nextpage">
								<button class="" id="weibo-comment-nextpage" onclick="nextCommentsPage()">下一页</button>
							</span>
						</div>
					</div>
				</div>
			</div>
			<div id="weibo-reposts" class="weibo-reposts" style="display:none;">
				<div id="weibo-reposts-list" class="weibo-reposts-list" >
					<div id="weibo-reposts-box" class="left weibo-reposts-box">
						<div class="clearfix">
							<span class="left span">转发</span>
							<span class="right"><img class="closeBtn" src="./weibo/images/5.png" alt="关闭"/></span>
						</div>
						<div class="btn-group weibo-reposts-tab" role="group" aria-label="" id="weibo-reposts-tab">
						</div>
						<div class="weibo-reposts-1">
							
						</div>
						<div class="weibo-page display-off">
							<span class="weibo-prepage" >
								<button class="" id="weibo-reposts-prepage" onclick="preRepostsPage()">上一页</button>
							</span>
							<span class="weibo-nextpage">
								<button class="" id="weibo-reposts-nextpage" onclick="nextRepostsPage()">下一页</button>
							</span>
						</div>
					</div>
				</div>					            		
			</div>
		</div>
		<div class="unpublic">	
			<div class="text">
				<form id="modifyWeiboForm" action="" method="post" role="modifyWeiboForm">
					<input id="modifyDocID" type="hidden" value="" />
					<div>
						<span class="left span">编辑微博</span>
						<span class="right">
							<i class="sheng"><span id="modifyFlag">还可以输入</span><span class="red" id="modifySurplusWord">140</span>字符</i>
							<img class="img" src="./weibo/images/5.png" alt="" />
						</span>
					</div>
					<div class="" >
						<textarea class="textarea" name="" rows="" cols="" id="modifyWeiboText" style="resize: none;"></textarea>
					</div>					
					<!--img class="san" src="./weibo/images/12.png" alt="" /-->
					<div class="lan">
						<div class="form-group" style="margin-bottom:-8px;">
							<div class="left">
								<span class="">
									<a class="face" href="javascript:void(0);" id="modifyFace">
										<img src="./weibo/images/6.png" alt=""/>
										<span>表情</span>
									</a>
								</span>
								<span class="">
									<a class="uploadPic" href="javascript:void(0);" id="nopubImgBtn">
										<img src="./weibo/images/7.png" alt=""/>
										<span>图片</span>
									</a>
								</span>
								<span class="">
									<a class="" href="javascript:void(0);" id="nopubTopicBtn">
										<img src="./weibo/images/9.png" alt=""/>
										<span>话题</span>
									</a>
								</span>
								<span class="">
									<a class="" href="javascript:void(0);" id="timeSendBtn">
										<img src="./weibo/images/11.png" alt=""/>
										<span>定时发</span>
									</a>
								</span>
								<span class="display-off" id="nopubsendtime">
									<input type="text" name="modifyTiming" id="modifyTiming" class="Wdate" readonly="readonly"
									value="<%=com.founder.e5.commons.DateUtils.format("yyyy-MM-dd HH:mm")%>"/>
								</span>
							</div>
							
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="rode">
								<div class="right made">
									<ul class="hold left">
										<li><input class="save" type="button" value="保存" onclick="weiboSubmit(3)" id="saveBtn"/></li>									
										<!-- <li><span class="load" id="loadManuscript">加载文章原稿</span>	</li>	 -->										
									</ul>
									<span class="left">
										<input class="public" type="button" value="发布" id="modifyPubButton" onclick="weiboSubmit(2)"/>
									</span>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
		<div id="layer_send_pic" class="layer_send_pic">					
			<div id="layer_send_btn" class="layer_send_btn weibo-opp-outer weibo-opp-inner">
				<span class="wb-horn-f"></span>
				<span class="wb-horn-d"></span>
				<ul>
					<li><a href="javascript:void(0);"><img src="./weibo/images/add-t.png" alt="" />单图/多图</a></li>
				</ul>
			</div>
		</div>    
	</div>
	<div id="sudokuUpload" class="sudokuUpload"></div>
</body>
</html>