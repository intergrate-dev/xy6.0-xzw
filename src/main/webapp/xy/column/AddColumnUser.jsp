<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@ page pageEncoding="UTF-8"%>

<html>
<head>
	<title>站点</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style type="text/css">
		.lastchild{
			border-bottom: 0 !important;
		}
		.listBg
		{
			font-family:'微软雅黑';
			color: #666;
			background: white;
			border: 1px solid #ddd;	
			width: 65%;
			margin-top: 10px;
			font-size: 14px;
		}
		.listBg tr:nth-child(1){
		    background: #f9f9f9;
		    color: #666;
		    font-weight: bold;
		    height: 35px;
		}
		.listBg tr td{
			border-bottom: 1px solid #d4ebf8;
    		height: 25px;
		}
		.btngroup {
		    margin: 3px 5px;
		    font-family: "microsoft yahei";
		    color: #fff;
		    border: none;
		    background: #b1b1b1;
		    border-radius: 3px;
		    padding: 5px 20px;
		    font-size: 12px;
		    cursor: pointer;
		    margin-top: 10px;
		}
		.user{
			height: 40px;
    		line-height: 40px;
    		border-bottom: 2px solid #00a0e6;
    		width: 135px;
    		text-align: center;
    		font-family: "microsoft yahei";
    		color: #666;
    		font-size: 14px;
		}
	</style>
</head>

<script>
	$(function(){
		
		// 点击全选择全解除
		$('#chkall').click(function(){

			if($('#chkall').attr('checked')){ // 全选择
				$('[id^="chk_"]').attr('checked', true);
				$('#confirmBtn').removeAttr('disabled');
				
			}else{ // 全解除
				$('[id^="chk_"]').attr("checked", false);
				$('#confirmBtn').attr('disabled', 'disabled');
			}
		});
		
		// 点击明细条中的checkbox
		$('[id^="chk_"]').click(function(){
		
			$('[id^="chkall"]').attr('checked', false);
			var elem = $('input:checkbox[id^="chk_"]:checked');
			var length = elem.length;
			
			if(length > 0){ // 至少有一个打钩的
				$('#confirmBtn').removeAttr('disabled');
			}else{
				$('#confirmBtn').attr('disabled', 'disabled');
			}
		});
		
		// 点击确定按钮	
		$('#confirmBtn').click(function(){

			var elem = $('input:checkbox[id^="chk_"]:checked');
			var length = elem.length;
			var sysIdArr = '';
			
			for(var a = 0; a < length; a++){
				sysIdArr += ',' + elem[a].id.substr(4);
			}
			var colID = $('#hidden_colID').val();
			var siteID = $('#hidden_siteID').val();
			var roleType = $('#hidden_roleType').val();

			var url = "../../xy/column/AddColumnUser.do?sysId=" + sysIdArr
					+ "&colID=" + colID + "&siteID=" + siteID + "&roleType=" + roleType;
			window.parent.frames["frmColRight"].location.href = url;
		});
		
		// 点击取消按钮	
		$('#cancelBtn').click(function(){
			history.back();
		});
	});
</script>


<body>
	<c:if test="${roleType==0 || roleType==4}">
		<div class="user">设置操作权限</div>
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
					<td width="10%" align="left"><input type="checkbox" id="chkall"></td>
					<td width="20%" align="center">用户名</td>
					<td width="20%" align="center">姓名</td>
					<td width="15%" align="center">笔名</td>
					<td width="20%" align="center">机构</td>
				</tr>
			</c:if>
			<tr>
				<input type="hidden" id="hidden_<c:out value="${bean.sysId}"/>"><!--每一条明细的唯一ID-->
				<td width="10%" align="left"><input type="checkbox" id="chk_<c:out value="${bean.sysId}"/>"></td>
				<td width="20%" align="center"><c:out value="${bean.userCode}"/></td>
				<td width="20%" align="center"><c:out value="${bean.userName}"/></td>
				<td width="15%" align="center"><c:out value="${bean.penName}"/></td>
				<td width="20%" align="center"><c:out value="${bean.org}"/></td>
			</tr>
		</c:forEach>
		<c:if test="${userInfoList.size() != 0}">
		<tr>
			<td class="lastchild">共${userInfoList.size()}条</td>
		</tr>
		</c:if>
		<input type="hidden" id="hidden_colID" value="${colID}"><!--colID-->
		<input type="hidden" id="hidden_siteID" value="${siteID}"><!--siteID-->
		<input type="hidden" id="hidden_roleType" value="${roleType}"><!--roleType-->
	</table>
	<input style="background: #00a0e6;" type="button" id="confirmBtn" value="确定" class="button btn btngroup" disabled>
	<input type="button" id="cancelBtn" value="取消" class="button btn btngroup">
</body>
</html>