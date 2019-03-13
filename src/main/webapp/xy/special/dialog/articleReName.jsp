<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
	<style>
		body{
			background-color:#fff;
		}
		.rename-btn{
			margin:20px auto;
			text-align:center;
		}
		.rename-txt{
			margin:0 auto;
			text-align:left;
		}
		.rename-txt input{
			width:300px;
			height:30px;
			font-family:"微软雅黑";
			font-size:10px;
			padding:4px;
		}
		.rename-cancle{
			margin-left:50px;
		}
		.rename-btn input{
			width:100px;
			height:30px;
			font-family:"微软雅黑";
			font-size:14px;
			
		}

	</style>
</head>


<script src="../special/third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="../special/third/bootstrap-3.3.5-dist/css/bootstrap.min.css">
<link rel="stylesheet" href="../special/third/jquery-ui-bootstrap-1.0/assets/css/font-awesome.min.css">
<link rel="stylesheet" href="../special/third/bootstrap-3.3.5-dist/css/bootstrap-datetimepicker.min.css">
<script src="../special/third/bootstrap-3.3.5-dist/js/bootstrap-datetimepicker.js" type="text/javascript"></script>
<body style="height:auto;">
<form id="renameForm" name="renameForm" method="post" action="${pageContext.request.contextPath }/xy/special/renameSave.do" >
	<div style="margin-top:35px;">
		<div class="rename-txt">
			<label class="control-label" style="margin-right: 42px;margin-left: 20px;font-size: 16px;">专题名：</label>
			<input id="specialname" type="text" name="specialname" value="${specialName}" >
			
			<input id="DocIDs" type="hidden" name="DocIDs" value="${docID}">
			<input id="doclibid" type="hidden" name="DocLibID" value="${docLibID}">
			<input id="rename-uuid" type="hidden" name="UUID" value="${UUID}">
			<input type="hidden" name="siteID" value="${siteID}">
			
		</div>
		<div style="margin-left: 92px; margin-top: 20px;">
			<span style="display: none; color: #ff0000" id="warningSpan">对不起，该专题名已存在！</span>
		</div>
		<div class="controls input-append date form_date mgt10" data-date="" data-date-format="yyyy-mm-dd" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd" >
			<label class="control-label"style="margin-right:42px;margin-left:20px;">更新截止日期:</label>
			<span class="add-on" title="为避免重复更新和发布过期的专题稿，请设置一个截止日期!"><i class="icon-th"></i></span>
			<input size="16" type="text" name="expdate" value="${expdate}" id="saveNameDate" readonly style="width:250px;">
			<span class="add-on"><i class="icon-remove"></i></span>
		</div>
		<input type="hidden" id="dtp_input2" value="" />
		<div class="rename-btn">
		<input type="button" id="btn-confirm" value="确定" />
		<input type="button" id="btn-cancle" value="取消" onClick="doCancel()"/>
		</div>
	</div>
</form>

</body>
<script type="text/javascript">
$(function(){
	$('.form_date').datetimepicker({
		language: 'cn',
		weekStart: 1,
		todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 2,
		forceParse: 0
	});
	$("#btn-confirm").click(function(){
		save();
	});
});
	
  
	function save(){
		var a = $("#specialname").val();
		var b= $("#doclibid").val();
		var c=$("#saveNameDate").val();
		var d=$("#DocIDs").val();
		$.ajax({
			url : "checkName.do",
			data : {
				"specialName" : a,
				"docLibId" : b,
				"date":c,
				"docId":d
			},
			type: "POST",
            dataType: "json",
			success : function(date) {
				if ("1" == date.status || date.status === 1) {
					$("#warningSpan").show();
				} else {
					$("#renameForm").submit();
				}

			}
		});

	}
	function doCancel() {
		window.onbeforeunload = null;

		$("#btn-confirm").disabled = true;
		$("#btn-cancle").disabled = true;

		beforeExit();
	};

	function beforeExit() {
		var uuid = $("#rename-uuid").val();
		var dataUrl = "../../e5workspace/after.do?UUID=" + uuid;

		window.location.href = dataUrl;
	}

	$(function() {
		$("#nameid").blur(function() {

		});
	});
</script>

</html>