<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<body>
	<style type="text/css">
		body{
			font-family: "microsoft yahei";
			font-size: 13px;
		}
		label{
			font-weight: 100;
			padding: 0;
			margin: 0;
		}
		li{list-style: none;}
		.account{padding-left:10px;font-size: 14px;color: #333; font-weight: bold;}
		.menu-group{margin: 5px 0;}
		.menu-main{margin:5px;}
		.menu-sub{padding-left:50px;font-size: 12px;}
		.btngroup {
		    margin: 5px 5px;
		    font-family: microsoft yahei;
		    color: #fff;
		    border: none;
		    background: #b1b1b1;
		    border-radius: 3px;
		    padding: 5px 20px;
		    font-size: 12px;
		    position:absolute;
		    bottom:10px;
		    cursor: pointer;
		    
		}
		#btnSubmit{
			background: #00a0e6;
			
			left:35%;
		}
		
		#btnCancel{
			left:50%;
		}
		.commition{
			border:1px solid #ddd;
			resize:none;
			margin-right:20px;
		}
		.ztree{
			max-height: 650px;
			overflow: auto;
			
		}
		.userName{
			border:1px solid #ddd;
			width:200px;
			float:left;
			height:520px;
			overflow:auto;
		}
		
		.imgContent{
			width:441px;
			height:360px;
			border:1px solid #ddd;
			overflow:auto;
		}
		.imgContent img{
			max-width:110px;
			max-height:110px;
		
		
		}
		.imgContent div{
			width:110px;
			height:110px;
			margin:5px;
			position:relative;
			float: left;
			
		}
		.imgContent span{
			position:absolute;
			right:0;
			top:0;
			cursor:pointer;
			background-color: #000;
			color:#fff;
			border-radius: 50%;
			padding: 4px;
		}
		.content div:first-child{
			margin:10px 0;
			margin-top:-26px;
			
		}
		.newsContent{
			float:left;
			margin-left:20px;
			
		}
	</style>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="../../xy/article/script/WBSelect.js"></script> 
	<script>
		var UUID = "<c:out value="${UUID}"/>";
		push_WB.DocIDs = "<c:out value="${docID}"/>";
		push_WB.DocLibID = "<c:out value="${docLibID}"/>";
	</script>
	<div id="rs_tree" class="ztree">
	<span>微博账号</span>
		<ul id="groupUl" style="padding-left:0;">
		<div class="userName">
			
			<c:forEach var="account" items="${accounts}">
				<li class="group">
				<!-- 账号 -->
					<div class="menu-group" level="0">
						<label class="account">
							<input type="checkbox" id="<c:out value="${account.id}"/>"/>
							<c:out value="${account.name}" />
						</label>
					</div>
					
				</li>
				
			</c:forEach>
		
		</div>
		<div class="newsContent">
			<div class="content">
				<div>编辑内容</div>
				<textarea class="commition" type="text" id="pushDescription" rows="10" cols="60"><c:out value="${description}" escapeXml="false"/></textarea>
			</div>
			<div class="imgContent">
				<c:forEach var="img" items="${imgs}">
					<div id ="${img.id}" >
						<img src="${img.imgUrl}"  id ="${img.id}" style="max-width: 100%;"/>
						<span>X</span>
					</div>
					
				</c:forEach>
			</div>
			
		</ul>
		</div>
		<input class="btngroup" type='button' id="btnSubmit" value='确定'/>
		<input class="btngroup" type='button' id="btnCancel" value='取消'/>
	</div>
</body>
</html>