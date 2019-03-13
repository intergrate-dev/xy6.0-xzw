<script type="text/javascript">
	var ueditor_size = {width : 828,height : 200}
	var t_type = 3;
</script>
<style>
	.showVideo{
		float: left;
		margin-left: 21px;
    	margin-right: 5px;
	}
	.substance{
		margin-top: 70px;
   		float: left;
    	margin-left: 42px;
	}
	.editContainer{margin-left: 73px !important;margin-top: 20px;}
</style>
<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			<span style="margin-left:20px;" class="groupPic">视频文件：</span>
			<div id="picUploadDiv">
				<%@include file="../../video/inc/videoUpload.inc"%>
			</div>
			<div style="clear: both;"></div>
			<span class="substance">内容：</span><%@include file="../simpleEditor.html"%>
			<%@include file="CommonFooter.inc"%>
		</div>
		<div id="divRight">
			<%@include file="CommonRight.inc"%>
		</div>
	</div>
</form>
