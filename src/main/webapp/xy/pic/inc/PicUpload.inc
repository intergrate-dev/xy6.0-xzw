<script type="text/javascript" src="../pic/script/edit.js"></script>
<script type="text/javascript" src="../pic/script/picUpload.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 

<c:if test="${fromPage != 'pic'}">
	<script type="text/javascript" src="../ueditor/ueditor.all.js"> </script>
	<script type="text/javascript" src="../ueditor/internal.js"></script>
	<script type="text/javascript" src="../pic/script/picOnline.js"></script>
</c:if>

<c:if test="${fromPage == 'pic' || fromPage == 'live'}">
	<link rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
</c:if>
<link type="text/css" rel="stylesheet" href="../pic/css/pic.css"/>
<style>
* {
    box-sizing: inherit;
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
<c:choose>
	<c:when test="${fromPage == 'pic'}">
		#ul1 {
			width:1050px;
			position:relative;
			margin: 10px;
			margin-left: 10px;
			margin-top: 20px;
			height: 183px;
		}
	</c:when>
	<c:otherwise>
		#ul1 {
			width: 900px !important;
			position: relative;
			/*margin: 10px;*/
			margin-left:-10px;
			height: 183px;
			margin-top: 20px;
		}
		
	</c:otherwise>
</c:choose>
</style>
<div id="modal-overlay">
		<div class="modal-data" style="width:899px; margin: 0 auto;">
       	<!--<input class="cancelbtn" type="button" id="cancel" name="cancel" value="取消" />-->
		<img style="cursor: pointer; margin-top: 20px; margin-right: -1px;" class="cancelbtn" id="cancel" src="../../Icons/close1.png" alt="" />
       	<c:if test="${fromPage != 'pic'}">
	       	<ul class="channels" style="font-family: 'microsoft yahei'; margin-top: 15px;">
				<li name="channel" class="select channelTab" id="channelUpload">本地上传</li>
				<li name="channel" class="channelTab" id="channelOnline">图片库</li>
			</ul>
		</c:if>
		<div id="flash">
			<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=11,0,0,0" width="900" height="450" id="update" align="middle">
				<param name="allowFullScreen" value="false" />
			    <param name="allowScriptAccess" value="always" />
				<param name="movie" value="update.swf" />
			    <param name="quality" value="high" />
			    <param name="bgcolor" value="#ffffff" />
			    <embed src="../pic/update.swf" quality="high" bgcolor="#ffffff" width="900" height="440" name="update" align="middle" allowScriptAccess="always" allowFullScreen="false" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
			</object>
		</div>
	 	<c:if test="${fromPage != 'pic'}">
            <div id="online" class="panel" style="display:none;">
           		<iframe src="../GroupSelect.do?type=0&siteID=${siteID}" frameborder=0 style="width:99%;min-height:500px;"></iframe>
           </div>
		</c:if>
    </div>
</div>

<ul id="ul1">
	<c:forEach var="picInfo" items="${picInfoList}" varStatus="st">
		<li class="linkli_" id="linkli_${st.index + 1}">
			<div class="picDiv">
				<input ${picInfo.isIndex} title="设为索引图" class="radio_" type="radio" name="radio_" id="radio_${st.index + 1}">
				<span class="indexPic">设为索引图</span>
				<input class="synBtn_" type="button" title="同步图片说明" name="synBtn_" id="synBtn_${st.index + 1}" />
				<span class="icon-retweet"></span>
				<input class="delBtn_" value="删除" title="删除图片" type="button" name="delBtn_" id="delBtn_${st.index + 1}" />
				<span class="icon-trash"></span>
				<c:if test="${fromPage != 'pic'}">
					<input class="title" type="button" title="设为标题图片" name="titleBtn_" id="titleBtn_${st.index + 1}" />
					<span class="icon-picture"></span>
				</c:if>
				<input class="cut" type="button" title="修改" name="modifyBtn_" id="modifyBtn_${st.index + 1}" />
				<span class="icon-pencil"></span>
			</div>
			<div class="picfather">
				<img class="imgsize" alt="图片" src="../image.do?path=${picInfo.picPath}.0" pic="${picInfo.pic}">
				<input value="${picInfo.picPath}" type="hidden" />
			</div>
			<div>
				<textarea placeholder="输入图片说明..." type="text" class="text_" id="text_${st.index + 1}" name="text_">${picInfo.content}</textarea>
			</div>
		</li>
	</c:forEach>
	<li id="linkli" class="linkli">
		<img class="imgsize1" alt="上传图片" src="../pic/img/upload.jpg">
	</li>
</ul>
<input type="hidden" id="sessionID" name="sessionID" value='${sessionID}' />

<c:if test="${fromPage == 'pic'|| fromPage == 'live'}">
	<link type="text/css" rel="stylesheet" href="../ueditor/themes/iframe.css"/>
	<script type="text/javascript" src="../ueditor/ueditor.config.js"></script>
	<script type="text/javascript" src="../ueditor/ueditor.all.min.js"> </script>

	<script type="text/javascript">
	$(function(){
		UE.getEditor('simpleEditor', {
					toolbars:[]
				});
	});

		
	</script>
	<div id="editContainer"  class="editContainer" style="display:none;">
		<script id="simpleEditor" type="text/plain">
		</script>
	</div>
</c:if>