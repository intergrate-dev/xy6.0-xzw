
<!DOCTYPE html>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<%@include file="../../inc/copy.inc"%>
<head>
    <meta charset="UTF-8">
	<script language="javascript" type="text/javascript" src="special/dialog/script/group.js"></script>
</head>
<body>
<form id="formid" name="copyform" action="${pageContext.request.contextPath }/xy/special/Copy.do" >
    <div class="rename" style="margin-top:10px;">
		<input type="button" id="btn-confirm" value="确定" />
		<input type="button" id="btn-cancle" value="取消" onClick="doCancel()"/>
	</div>
    <div id="rs_tree" class="ztree">
		<ul id="groupUl">
			<c:forEach var="group" items="${groups}">
				<li class="group" groupID="<c:out value="${group.catID}"/>">
					<div class="active" name="grouplist"   id="groupNameDiv_<c:out value="${group.catID}"/>" groupID="<c:out value="${group.catID}"/>">
						<c:out value="${group.catName}" />
					</div>
					<input class="input" maxlength="10" id='groupIdInput_<c:out value="${group.catID}"/>'
						groupID="<c:out value="${group.catID}"/>" groupName="<c:out value="${group.catName}" />"
						value="" />
				</li>
			</c:forEach>
	    </ul>
	    
		<input type="hidden" id="chosenGroupIDInput" name="groupID" value="" />
		
	</div>
	<input type="hidden" name="DocIDs" value="${docID}">
	<input type="hidden" name="DocLibID" value="${docLibID}">
	<input id="copy-uuid" type="hidden" name="UUID" value="${UUID}">
	<input type="hidden" name="siteID" value="${siteID}">

</form>
</body>
<script>
	main_param["catTypeID"] = "<c:out value="${catTypeID}"/>";
	main_param["groupField"] = "<c:out value="${groupField}"/>";
	main_param["siteField"] = "<c:out value="${siteField}"/>";
</script>
<script type="text/javascript">

	$(function () {
		$("#btn-confirm").click(function(){
            debugger;
			$("#formid").submit();
		});
	})
	/* function doCancel(){
		window.onbeforeunload = null;
		
		$("#btn-confirm").disabled = true;
		$("#btn-cancle").disabled = true;
		
		beforeExit();
	};
	
	function beforeExit() {
		var uuid=$("#UUID").val();
		var dataUrl = "../../../e5workspace/after.do?UUID=" + uuid;
	
		window.location.href = dataUrl;
	}*/

	  function doCancel() {
			window.onbeforeunload = null;
			
			$("#btn-cancle").disabled = true;
			beforeExit();
		}
      
		//关闭窗口。调after.do解锁
		 function beforeExit() {
			var dataUrl = "../e5workspace/after.do?UUID=" + $("#copy-uuid").val();
			window.location.href = dataUrl;
		}
	
 </script>
</html>
