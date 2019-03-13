<script type="text/javascript">
	var ueditor_size = {width : 828,height : 200}
	var t_type = 2;
</script>
<style type="text/css">
	body{
		margin: 0 auto !important;
	}
	.editContainer{margin-left: 73px !important;margin-top: 20px;height: 270px!important;}

	.btn-default{
		padding: 1px 6px;
		font-size:12px;
	}
	.kv-zoom-actions button{
		display: none;
	}
	.kv-zoom-actions button:last-child{
		display: block;
	}
	#kvFileinputModal{
		display: none;
	}

	#simpleEditor {
		margin-top: 15px !important;
	}
</style>
<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			<span class="left require1">*</span><span class="groupPic">组图图片：</span>
			<%--<div id="picUploadDiv">
				<%@include file="../../pic/inc/PicUpload.inc"%>
			</div>--%>

			<div id="picUploadDiv">
				<div class="container kv-main">
					<form enctype="multipart/form-data">
						<div class="form-group">
							<input id="picUploadInput" type="file" name="file" style="height: 30px;" multiple>
						</div>
					</form>
				</div>
			</div>
			<div style="clear: both;"></div>
			<span class="left substance">内容：</span><%@include file="../simpleEditor.html"%>
			<%@include file="CommonFooter.inc"%>
		</div>
		<div id="divRight">
			<%@include file="CommonRight.inc"%>
		</div>
	</div>
</form>
