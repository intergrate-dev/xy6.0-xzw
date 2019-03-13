<script type="text/javascript" src="../../xy/pic/script/wxPic.js"></script>
<script type="text/javascript" src="../../xy/pic/script/picUpload.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 

<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
<link type="text/css" rel="stylesheet" href="../../xy/pic/css/pic.css"/>
<style>
* {
    box-sizing: border-box;
}
.uploadpic{
	width: 200px;
	height:178px;
	border: 1px solid #ddd;
	display: block;
}
.uploadpic span{
	font-size: 40px;
    margin-top: 60px;
    color: #323232;
    display: block;
}
.pics{
	font-size: 14px !important;
	color: #666 !important;
	margin-top: 15px !important;
}
.picfather{
	width: 200px;
    height: 150px;
    background-color: #efefef;
}
.editCancelbtn{
	background-color: #b1b1b1;
    border: none;
    color: #fff;
    width: 70px !important;
    border-radius: 3px;
    font-family: "microsoft yahei";
}
.confirmbtn{
	background-color: #00a0e6;
    border: none;
    color: #fff;
    width: 70px !important;
    border-radius: 3px;
    font-family: "microsoft yahei";
}
</style>
<div id="modal-overlay">
		<div class="modal-data" style="width:899px; margin: 0 auto;">
		<img style="cursor: pointer; margin-top: 20px; margin-right: -1px;" class="cancelbtn" id="cancel" src="../../Icons/close1.png" alt="" />
		<ul class="channels" style="font-family: 'microsoft yahei'; margin-top: 15px;">
			<li name="channel" class="select channelTab" id="channelUpload">本地上传</li>
			<li name="channel" class="channelTab" id="channelOnline">图片库</li>
		</ul>
		<div id="flash">
			<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=11,0,0,0" width="900" height="450" id="update" align="middle">
				<param name="allowFullScreen" value="false" />
			    <param name="allowScriptAccess" value="always" />
				<param name="movie" value="update.swf" />
			    <param name="quality" value="high" />
			    <param name="bgcolor" value="#ffffff" />
			    <embed src="../../xy/pic/update.swf" quality="high" bgcolor="#ffffff" width="900" height="440" name="update" align="middle" allowScriptAccess="always" allowFullScreen="false" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
			</object>
		</div>
		<div id="online" class="panel" style="display:none;">
			<iframe src="../../xy/GroupSelect.do?type=0&siteID=${param.siteID}&all=1" frameborder=0 style="width:99%;min-height:500px;"></iframe>
	   </div>
    </div>
</div>
<input type="hidden" id="sessionID" name="sessionID" value='${sessionID}' />