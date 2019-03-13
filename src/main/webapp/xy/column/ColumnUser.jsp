<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@ page pageEncoding="UTF-8"%>

<html>
<head>
	<title>站点</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style type="text/css">
		body
		{
			font-family: "微软雅黑";
			color: #666;
			font-size: 14px;
		}
	
	    table tr td a:last-child{
			color: #088ce0;
			text-decoration:none;
		}
		table tr:nth-child(1){
			background: #f9f9f9;
		    color: #666;
		    font-weight: bold;
		    height: 35px;  
		}
		table{
			border: 1px solid #ddd;
			width: 65%;
			margin-top:10px;
		}
		table tr td{
			border-bottom: 1px solid #d4ebf8;
			height: 25px;
		}
		table tr:last-child td{
			border-bottom: none;
			
		}
		/*table tr td:last-child{
			    width: 50px;
   				 display: block;
			}*/
		.btngroup {
		    margin: 3px 5px;
		    font-family: microsoft yahei;
		    color: #fff;
		    border: none;
		    background: #b1b1b1;
		    border-radius: 3px;
		    padding: 5px 20px;
		    font-size: 12px;
		}
		#addBtn {
		    background: #00a0e6;
		}
		.user{
			height: 40px;
    		line-height: 40px;
    		border-bottom: 2px solid #00a0e6;
    		width: 135px;
    		text-align: center;
		}
	</style>
</head>

<script>
	$(function(){
		
		// 点击全选择全解除
		$('#chkall').click(function(){

			if($('#chkall').attr('checked')){ // 全选择
				$('[id^="chk_"]').attr('checked', true);
				$('#delBtn').removeAttr('disabled');
				
			}else{ // 全解除
				$('[id^="chk_"]').attr("checked", false);
				$('#delBtn').attr('disabled', 'disabled');
			}
		});
		
		// 点击明细条中的checkbox
		$('[id^="chk_"]').click(function(){
		
			$('[id^="chkall"]').attr('checked', false);
			var elem = $('input:checkbox[id^="chk_"]:checked');
			var length = elem.length;
			
			if(length > 0){ // 至少有一个打钩的
				$('#delBtn').removeAttr('disabled');
			}else{
				$('#delBtn').attr('disabled', 'disabled');
			}
		});
		
		// 点击追加按钮	
		$('#addBtn').click(function(){
			var colID = $('#hidden_colID').val();
			var siteID = $('#hidden_siteID').val();
			var roleType = $('#hidden_roleType').val();
			
			var url = "../../xy/column/InitAddColumnUser.do?colID="
					+ colID + "&siteID=" + siteID + "&roleType=" + roleType;
			window.parent.frames["frmColRight"].location.href = url;
		});
		
		// 点击删除按钮	
		$('#delBtn').click(function(){
			var elem = $('input:checkbox[id^="chk_"]:checked');
			var length = elem.length;
			var sysIdArr = '';
			
			for(var a = 0; a < length; a++){
				sysIdArr += ',' + elem[a].id.substr(4);
			}
			var colID = $('#hidden_colID').val();
			var siteID = $('#hidden_siteID').val();
			var roleType = $('#hidden_roleType').val();

			delColumnUser(sysIdArr, colID, siteID, roleType);
		});
	});
	
	function delColumnUser(sysId, colID, siteID, roleType){
		var url = "../../xy/column/DelColumnUser.do?sysId=" + sysId + "&colID=" + colID
					+ "&siteID=" + siteID + "&roleType=" + roleType;
		window.parent.frames["frmColRight"].location.href = url;
	}
</script>
<script type="text/javascript">
	$(document).ready(function(){
		$("table tr").mouseover(function(){
			$(this).css({
				"background":"#f9f9f9",
				"cursor":"pointer"	
			})
		})
		$("table tr").mouseout(function(){
			$(this).css("background","")
		})
	})
</script>

<body>
	<c:if test="${roleType==0 || roleType==4}">
		<div class="user">
			设置操作权限
		</div>
	</c:if>
	<c:if test="${roleType==1 || roleType==5}">
		<div class="user">
			设置管理权限
		</div>
	</c:if>
	<table cellspacing="0" cellpadding="4" width="100%"  border="0" class="listBg">
		<c:if test="${userInfoList.size() == 0}">没有符合条件用户</c:if>
		
		<c:forEach var="bean" items="${userInfoList}" varStatus="st">
			<c:if test="${st.index == 0}">
				<tr>
					<td width="5%" align="left"><input type="checkbox" id="chkall"></td>
					<td width="6%" align="center">姓名</td>
					<td width="15%" align="center">账号</td>
					
					<td width="10%" align="center">笔名</td>
					<td width="10%" align="center">机构</td>
					<td width="15%" align="center">操作</td>
				</tr>
			</c:if>
			<tr>
				<c:if test="${sessionScope.sysUser.userID != bean.sysId}">
				<input type="hidden" id="hidden_<c:out value="${bean.sysId}"/>"><!--每一条明细的唯一ID-->
				</c:if>
				<td width="5%" align="left">
				<c:if test="${sessionScope.sysUser.userID != bean.sysId}">
					<input type="checkbox" id="chk_<c:out value="${bean.sysId}"/>">
				</c:if>
				</td>
				<td width="6%" align="center"><c:out value="${bean.userName}"/></td>
				<td width="15%" align="center"><c:out value="${bean.userCode}"/></td>
				<td width="10%" align="center"><c:out value="${bean.penName}"/></td>
				<td width="10%" align="center"><c:out value="${bean.org}"/></td>
				<td width="15%" align="center">
					<c:if test="${sessionScope.sysUser.userID != bean.sysId}">
					<a href="javascript:delColumnUser(',${bean.sysId}', '${colID}', '${siteID}', '${roleType}')">删除</a>
					</c:if>
				</td>
			</tr>
		
		</c:forEach>
		
		<input type="hidden" id="hidden_colID" value="${colID}"><!--colID-->
		<input type="hidden" id="hidden_siteID" value="${siteID}"><!--siteID-->
		<input type="hidden" id="hidden_roleType" value="${roleType}"><!--roleType-->
		<c:if test="${userInfoList.size() > 0}">
			<tr>
				<td>共${userInfoList.size()}条</td>
			</tr>
		</c:if>
	</table>
	<input type="button" id="addBtn" value="添加" class="button btn btngroup">
	<input type="button" id="delBtn" value="删除" class="button btn btngroup" disabled>
</body>
</html>