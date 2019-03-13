<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="script/attachment.js"></script>

<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="script/griddrag.js"></script>
<link type="text/css" rel="stylesheet" href="../pic/css/attachment.css"/>
<link rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
<style>
	.undertop1 input{
		border: none;
	    color: #fff;
	    padding: 5px 20px;
	    border-radius: 3px;
	    font-family: "microsoft yahei";
	    font-size: 12px;
	}
	.docancle{
		background-color: #b1b1b1;
	}
	.dosave{
		background-color: #1bb8fa;
		margin-right:6px;
	}
	body{
		font-family:"微软雅黑";
	}
	#ul1{
		/* margin:15px auto; */
		width:885px;
		overflow-x:hidden;
		height:440px;
		position:absolute;
	}
	.undertop1{
		text-align:center;
		position:fixed;
		bottom:30px;
		left:50%;
		margin-left:-127px;
	}
	.uploadImg{
		position: ABSOLUTE;
	    top: -4px;
	    left: 0;
	    opacity: 0;
	    width: 23px !important;
	}
	
}  
</style>
<html>
<body>
<form id="form"  method="post" >
<input type="hidden"  name="data" value="${documentIDs}"/>
<ul id="ul1">
<c:forEach items="${list}" var="att">
	<li class="linkli_" id="${att.SYS_DOCUMENTID}">
			<div class="picDiv" id="edit${att.SYS_DOCUMENTID}">
				<input class="uploadImg" type="file" docid="${att.SYS_DOCUMENTID}" docPath="${att.att_path}" accept="image/jpg" id="Pic${att.SYS_DOCUMENTID}"  value="${att.att_path}"  name="Pic"/>
				<input type="hidden" class="order"  id="Att_Order${att.SYS_DOCUMENTID}" name="Att_Order${att.SYS_DOCUMENTID}" value="${att.att_order}">
				<span class="icon-pencil"></span>
			</div>	
			<div class="picfather">
				<img id="AttImg${att.SYS_DOCUMENTID}"  src="../image.do?path=${att.att_path}"  class="imgsize">
				<input type='hidden' id="Path${att.SYS_DOCUMENTID}" name="Path${att.SYS_DOCUMENTID}" value="${att.att_path}"/>
			</div>
			<div>
				<textarea placeholder="输入图片说明..." type="text" class="text_" id="Att_Content${att.SYS_DOCUMENTID}" name="Att_Content${att.SYS_DOCUMENTID}">${att.att_content}</textarea>
			</div>
		</li>
</c:forEach>
</ul>
	<input type="hidden" id="DocLibID" name="DocLibID" value="${docLibID}">  
	<input type="hidden" id="DocID" name="DocIDs" value="${docID}">  
	<input type="hidden" id="UUID" name="UUID" value="${uuid}">
	<div class="undertop1">
		<input class="dosave" type="button" id="btnSave" value="保存"/>
		<input class="docancle" type="button" id="btnCancel" value="关闭"/>
		<span style="color:red; font-size:12px;">调整完成后请重发版面</span>
	</div>
</body>
</form>

</html>