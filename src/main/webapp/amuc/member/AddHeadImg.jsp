<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>上传头像</title>
<script type="text/javascript" src="script/drag.js"></script>
<script type="text/javascript" src="script/ImageCopper.js"></script>
<style type="text/css">
	body
	{
		font-size:12px;
		font-family:微软雅黑, 宋体;
	}
	.title
	{
	    border-bottom:solid 1px #ccc;	
	}
	.photocontainer
	{
		margin-top:10px;
		background:url("../image/bg_120.gif") no-repeat left top;   
		padding:7px 12px 12px 7px;                                        
	}  
	.uploadtooltip
	{
		color: #555;	
	}
</style>
<script type="text/javascript">
	//校验上传文件
	function checkPic() {
		var location = document.getElementById('headImg').value;
		if (location == "") {
			alert("请先选择图片文件");
			return false;
		}
		var point = location.lastIndexOf(".");
		var type = location.substr(point).toLowerCase() ;
		if (type == ".jpg" || type == ".gif" || type == ".png" || type == ".jpeg" || type == ".bmp") {
			img = document.createElement("img");
			img.src = location;
			if (img.fileSize > 1024000) {
				alert("图片尺寸请不要大于100KB");
				return false;
			} else {
				document.getElementById("nowDiv").style.display="none";
				return true;
			}
		} else {
			alert("只能上传jpg、jpeg、gif、png、bmp格式的图片");
			return false;
		}
		document.getElementById("nowDiv").style.display="none";
		return true;
	}
	//图片上传后的回调函数
	function callback(url, width, height) {
		document.getElementById('cut_img').width = width;
		document.getElementById('cut_img').height = height;
		document.getElementById('cut_img').src = url + "?" + Math.round(Math.random() * 10000);
		document.getElementById('cut_url').value = url;
		document.getElementById('hide').style.display = '';
		imageinit();
		gripinit();
	}
	function hide(){
		document.getElementById('hide').style.display='none';
	}
</script>
</head>
<body>
    <form name="picForm" id="picForm" action="UploadHeadImg.do" method="post" enctype="multipart/form-data" 
    	onsubmit="return checkPic();" target="hidden_frame">
		<div style="margin:10px;">
             <div>
                 <div style="margin-top:10px;color: #555;line-height:150%;">请选择头像文件(128*128)，文件<2.5MB</div>
                 <div style="margin-top:10px;">
                     <input type="file" class="" size="50" id="headImg" name="headImg" />
                     <input type="submit" value="上传" />
                     <iframe name='hidden_frame' id="hidden_frame" style='display: none'></iframe>
                 </div>
             </div>
             <div id="nowDiv" style="margin-top:20px;">
             	<div class="title"><b>当前头像</b></div>
             	<div class="photocontainer">
             	  <img id="nowPhoto" style="width: 150px; height: 150px; margin: 0px" />
             	</div>
             </div>
         </div>
     </form>
     <form name="imgForm" id="imgForm" action="SaveHeadImg.do" method="post" onsubmit="return getcutpos();">
        <div id="hide" style="display: none;margin:10px;">
            <div id="cut_div" style="border: 2px solid #888888; width: 284px; height: 266px; overflow: hidden; position: relative; top: 0px; left: 0px; margin: 4px; cursor: pointer;">
                <table
                    style="border-collapse: collapse; z-index: 10; filter: alpha(opacity = 75); position: relative; left: 0px; top: 0px; width: 284px; height: 266px; opacity: 0.75;"
                    cellspacing="0" cellpadding="0" border="0" unselectable="on">
                    <tr>
                        <td style="background: #cccccc; height: 73px;" colspan="3"></td>
                    </tr>
                    <tr>
                        <td style="background: #cccccc; width: 82px;"></td>
                        <td style="border: 1px solid #ffffff; width: 120px; height: 120px;"></td>
                        <td style="background: #cccccc; width: 82px;"></td>
                    </tr>
                    <tr>
                        <td style="background: #cccccc; height: 73px;" colspan="3"></td>
                    </tr>
                </table>
                <img id="cut_img" style="position: relative; top: -266px; left: 0px" src="file:///D:/a.jpg" />
            </div>
            <table cellspacing="0" cellpadding="0">
                <tr>
                    <td>
                        <img style="margin-top: 5px; cursor: pointer;" src="../img/head/_h.gif" alt="图片缩小" onmouseover="this.src='../img/head/_c.gif'"
                            onmouseout="this.src='../img/head/_h.gif'" onclick="imageresize(false)" />
                    </td>
                    <td>
                        <img id="img_track"  style="width: 250px; height: 18px; margin-top: 5px" src="../img/head/track.gif" />
                    </td>
                    <td>
                        <img style="margin-top: 5px; cursor: pointer;" src="../img/head/+h.gif" alt="图片放大" onmouseover="this.src='../img/head/+c.gif'"
                            onmouseout="this.src='../img/head/+h.gif'" onclick="imageresize(true)" />
                    </td>
                </tr>
            </table>
            <img id="img_grip" src="../img/head/grip.gif" 
                style="position: absolute; z-index: 100; left: 100px; top: 350px; cursor: pointer;"/>
            <div style="padding-top: 15px; padding-left: 5px;">
            	<input type="hidden" name="MemberID" id="MemberID" value="<c:out value="${memberID}"/>" />
                <input type="hidden" name="DocLibID" id="DocLibID" value="<c:out value="${docLibID}"/>" />
                <input type="hidden" name="cut_pos" id="cut_pos" value="" />
                <input type="hidden" name="cut_url" id="cut_url" value="" />
                <input type="submit" class="button" name="submit" id="submit" value=" 保存头像 " />
            </div>
        </div>
    </form>
    <script type="text/javascript">
		var headSrc="<c:out value="${headSrc}"/>";
		if(headSrc!="")
			document.getElementById('nowPhoto').src = headSrc;
		else 
			document.getElementById('nowPhoto').src = "headImg/default.bmp";
	</script>
</body>
</html>