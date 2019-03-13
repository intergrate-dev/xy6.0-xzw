<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String rootURL = WebUtil.getRoot(request);
    String vsOptionType = request.getParameter("vsOptionType");
    String voteId = request.getParameter("voteID");
    String UUID = request.getParameter("UUID");
    String addOrEdit = request.getParameter("addOrEdit");
    String chooseNumType = request.getParameter("chooseNumType");
  	//投票类别；0（实名投票），1（用户IP）
    String vsVoteType = request.getParameter("vsVoteType");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>页面配置</title>
		<%=request.getAttribute("formHead")%>
		<!--引入CSS-->
		<link rel="stylesheet" type="text/css" href="../vote/upload/webuploader.css">
		<link rel="stylesheet" type="text/css" href="../vote/upload/style.css">
		<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css"/>
		<link type="text/css" rel="stylesheet" href="../vote//css/font-awesome.min.css"/>
		<script type="text/javascript">
			var rootURL = '<%=rootURL %>';
			var vsOptionType = '<%=vsOptionType %>';
			var voteId = '<%=voteId %>';
			var UUID = '<%=UUID %>';
			var addOrEdit = '<%=addOrEdit %>';
			var chooseNumType = '<%=chooseNumType%>';
			var vsVoteType = '<%=vsVoteType%>';
		</script>
		<!--引入JS-->
   		<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	    <script type="text/javascript" src="../vote/upload/webuploader.js"></script>
	    <script type="text/javascript" src="../vote/upload/upload.js"></script>
	    <script type="text/javascript" src="../vote/js/ZeroClipboard.min.js"></script>
	    <script src="../vote/js/qrcode.js"></script>
	    <script type="text/javascript" src="../vote/js/HandersImg.js"></script>
	    <script type="text/javascript" src="../vote/js/updateImg.js"></script>
	    <style type="text/css">
	    
	    	#LABEL_vsFootersWord{
	    		margin-left: 0px;
	    	}
	    	body {
				    color: rgb(34, 34, 34); 
				    line-height: 1.6; 
				    font-family: "Microsoft YaHei","微软雅黑",Arial,Tahoma; 
				    font-size: 14px;
			}
			.tablecontent td {
				border-bottom:none;
			}
			.vote_address {
				border-bottom:1px solid #b9cfe4;
				margin:10px;
			}
			.copyvoteurl {
				cursor:pointer;
			}
	    </style>
		<script type="text/javascript">
			
			$(function() {
				
				$("#LABEL_vsFootersWord").hide();
				$("#DIV_vsFootersWord").removeClass("custform-from-wrap");
				$("#SPAN_vsFootersWord").removeClass("custform-span");
				$(".mainBodyWrap").before("<h4 style=\"margin-left:20px;margin-top: 20px\">页脚文字</h4>");
			});
	    </script>
	</head>
	
	<body>
		<ul class="nav nav-tabs" id="optionTab">
			   <li class="votesetTab"><a id="votesetTab" href="javascript:void(0)"><i class="icon-edit"></i>投票基本设置</a></li>
			   <li class="pageconfigTab"><a id="voteoptionTab" href="VoteOption.do?action=initOptions&voteID=1"><i class="icon-tasks"></i>投票选项</a></li>
			   <!-- <li class="votepublishTab"><a id="votepublishTab" href="javascript:void(0)"><i class="icon-cogs"></i>发布设置</a></li> -->
			   <li class="active voteoptionTab"><a id="pageconfigTab" href="javascript:void(0)"><i class="icon-magic"></i>页面配置</a></li>
			   <!-- <li class="checkvotePageTab"><a id="checkvotePageTab" href="javascript:void(0)"><i class="icon-external-link"></i>查看投票页</a></li> -->
		</ul>
		<h4 style="margin-left:20px;margin-top: 20px">页眉图片</h4>
	   	<div id="wrapper">
	        <div id="container" style="width: 570px;height: 20%">
	            <!--头部，相册选择和格式选择-->
	
	            <div id="uploader" >
	                <div class="queueList" id="queueListid">
	                    <div id="dndArea" class="placeholder" >
	                        <div id="filePicker"></div>
	                        <p>（宽度960px，高度不限，建议不超过200px,大小不超过1M）</p>
	                    </div>
	                   
	                </div>
	                <div class="statusBar">
	                    <div class="progress">
	                        <span class="text">0%</span>
	                        <span class="percentage"></span>
	                    </div><div class="info"></div>
	                    <div class="btns">
	                        <div id="filePicker2"></div>
	                        <div class="uploadBtn" id="subBtn">开始上传</div>
	                        <div class="uploadBtn" id="delBtn">删除</div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	    
	    <form id="form" method="post" action="" >
	   		<%=request.getAttribute("formContent")%>
	   		<div id="qrcode" style="margin-left:13px;border:3px solid #ddd;width:100px;height:100px;">
			</div>
	   		<div class="control-group vote_address" style="margin-top:20px;">
				<div class="input-prepend input-append">
					  <span class="add-on">投票地址</span>
					  <input style="width:510px;" id="votefullurl" type="text" value="" readonly />
					  <span class="add-on copyvoteurl" id="copyvoteurl">复制</span>
				</div>
				<div style="margin: 20px 0px;">
					  <img alt="" src="../vote/image/share.png" style="margin-bottom: 3px;">
					  <a class="add-on" href="" target="_blank" id="votefullurl2">查看投票页</a>
				</div>
			</div>
	   		<div class="btnarea text-center">
				<input class="btn" type="button" id="btnSave" value="保存" style="border-radius:3px;color: #fff;background: #00a0e6;height: 30px;border: none;margin-left: 97px;font-size: 12px;cursor: pointer;text-shadow: none;padding: 0 27px;font-family: "microsoft yahei";"/>
				<input class="btn" type="button" id="btnCancel" value="取消" style="height: 30px;color: rgb(255, 255, 255);text-shadow: none;background: rgb(177, 177, 177);border-width: initial;border-style: none;border-color: initial;border-image: initial;border-radius: 3px;padding: 0px 27px;"/>
			</div>
		</form>
	</body>
</html>