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
		.menu-group{padding-left:30px;margin: 5px 0;}
		.menu-main{padding-left:30px;margin:5px;}
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
		}
		#btnSubmit{
			background: #00a0e6;
			margin-left: 50px;
		}
		
		.ztree{
			max-height: 400px;
			overflow: auto;
		}
	</style>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../xy/article/script/WXSelect.js"></script>
	<script>
		var tmpPadID = "<c:out value="${tmpPadID}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		push_WX.DocIDs = "<c:out value="${docIDs}"/>";
		push_WX.DocLibID = "<c:out value="${docLibID}"/>";
	</script>

	<div id="rs_tree" class="ztree">
		<ul id="groupUl">
			<c:if test="${tmpPadID <= 0}">
				<tr>您好，所在栏目没有挂载触屏模板！</tr>
			</c:if>
			<c:if test="${tmpPadID > 0}">
				<c:forEach var="account" items="${accounts}">
					<li class="group">
					<!-- 账号 -->
						<div class="account"><c:out value="${account.name}" /></div>
						<div class="menu-group" level="0">
							<label>
								<input type="checkbox" id="0" accountID="<c:out value="${account.id}"/>"/>
								<c:out value="图文" />
							</label>
						</div>
						<c:forEach var="menu" items="${account.menus}">
							<!-- 1级菜单 -->
							<div class="menu-main" level="0"><c:out value="${menu.name}"/></div>
						
							<!-- 2级菜单 -->
							<c:forEach var="subMenu" items="${menu.children}">
								<div class="menu-sub" groupID="<c:out value="${subMenu.id}"/>" level="1">
									<label>
										<input type="checkbox" id="<c:out value="${subMenu.id}"/>" accountID="<c:out value="${account.id}"/>"/>
										<c:out value="${subMenu.name}"/>
									</label>
								</div>
							</c:forEach>
						</c:forEach>
					</li>
				</c:forEach>
			</c:if>
		</ul>
	</div>
		<input class="btngroup" type='button' id="btnSubmit" value='确定'/>
		<input class="btngroup" type='button' id="btnCancel" value='取消'/>
	</div>
</body>
</html>