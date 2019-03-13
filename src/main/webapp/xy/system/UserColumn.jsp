<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head>
	<title><i18n:message key="org.user.form.list.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../script/bootstrap-3.3.4/css/bootstrap.min.css"/>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	
	<style>
		#divSite{margin:10px;}
		.nav{width:170px;float:left;}
		.tab-content{width:380px;float:left;}
		.frm{border:0;width:100%;height:450px;}
		.nav-stacked li a{
			font-family: "microsoft yahei";
			color: #666;
			font-size: 13px;
		}
	</style>
</head> 
<body>
<c:if test="${not empty roleID}">
	<div class="alert alert-info">
		若需要按用户设置栏目权限，请在用户管理功能中操作。
	</div>
</c:if>
<c:if test="${empty param.siteID}">
<div id="divSite"> 
请选择站点：
<select onchange="changeSite()" id="siteID">
	<c:forEach var="site" items="${sites}">
		<option value="<c:out value="${site.id}"/>"><c:out value="${site.name}"/></option>
	</c:forEach>
</select>
</div>
</c:if>

<c:if test="${not empty param.siteID}">  
	<input type="hidden" id="siteID" name="siteID" value="<c:out value="${param.siteID}"/>" />
</c:if>  

	<ul class="nav nav-pills nav-stacked" id="columnTab" style="margin-bottom: 0px!important;"> 
	<c:forEach var="ch" items="${channels}">
		<c:if test="${ch != null}">
			<li><a href="#home"
				data-type="<c:if test="${ch.id == 0}">0</c:if><c:if test="${ch.id == 1}">4</c:if>" 
				ch="<c:out value="${ch.id}"/>">
				<span>可操作栏目(<c:out value="${ch.name}"/>)</span>
			</a></li>
			<li><a href="#profile"
				data-type="<c:if test="${ch.id == 0}">1</c:if><c:if test="${ch.id == 1}">5</c:if>" 
				ch="<c:out value="${ch.id}"/>">
				<span>可管理栏目(<c:out value="${ch.name}"/>)</span>
			</a></li>
			<c:if test="${ch.id == 0 && userID!=null}">
				<li><a href="#messages" data-type="3">页面区块</a></li>
			</c:if>
			<c:if test="${ch.id == 1 && userID!=null}">
				<li><a href="#messages" data-type="10">可管理微信公众号</a></li>
			</c:if>
		</c:if>
	</c:forEach>
		<c:if test="${groupPower == true}">
		<li><a href="#messages" data-type="8" >分组权限</a></li>
		</c:if>
      <!--li><a href="#settings" data-type="2">视频分类</a></li--> 
    </ul>
    <div class="tab-content"> 
      <div class="tab-pane active" id="frmDiv">
      	<iframe id="frmColumn" src="" class="frm"></iframe>
      </div> 
    </div> 
<script>
	var userID = "<c:out value="${userID}"/>";
	var roleID = "<c:out value="${roleID}"/>";
	var DocLibID = "<c:out value="${DocLibID}"/>";
	var DocIDs = "<c:out value="${DocIDs}"/>";
	var UUID = "<c:out value="${UUID}"/>";
	//var type = "<c:out value="${type}"/>";
	
	var columnType = 0;
	var oldIDs = "";
	//初始化
	$(function(){
        $('#columnTab a').click(function (e) { 
			e.preventDefault();//阻止a链接的跳转行为 
			
			var src = $(this);
			src.tab('show');//显示当前选中的链接及关联的content 
			
			columnType = src.attr("data-type");
			changeType(src);
        });
		$('#columnTab a').first().click();

        if(roleID!="") {
            $("#frmColumn").height($(window.parent).height() - 170).width(500);
        };
	});
	
    //切换站点的时候，切换frame中的内容
	function changeType(src) {
		var theURL = "";
		var columCheckUrl = "";
		var type = src.attr("data-type");
		
    	//当type=3的时候，调用区块.
		//type=8时，复用区块的前台代码，保存分组权限
		//当type=10时，调用微信公众号
    	if (parseInt(type) == 3 || parseInt(type) == 8 ){
    		columCheckUrl = "../system/BlockSelect.jsp?dataType="+type;
    	} else if (parseInt(type) == 10){
    		columCheckUrl = "../../xy/wx/getAccounts.do?DocIDs="+DocIDs;
    	} else {
    		columCheckUrl = "../column/ColumnCheck.jsp?ch=" + src.attr("ch");
    	}
    	if(userID!="") {
            theURL = "../../xy/user/RelCol.do?userID=" + userID
                + "&siteID=" + $("#siteID").val()
                + "&type=" + type;
        }
    	else if(roleID!="") {
            theURL = "../../xy/system/getColPermission.do?roleID=" + roleID
                + "&siteID=" + $("#siteID").val()
                + "&type=" + type;
		}
    	
    	
    	
    	
		$.ajax({url:theURL, async:false, success:function(data){
		    data = JSON.parse( data );
			selectIDs = data.ids;
			columCheckUrl += "&type=all&siteID=" + $("#siteID").val() +"&selectIDs="+selectIDs;
			if(data.length<2000){
				columCheckUrl += "&ids="+data.ids + "&parentIDs=" + data.parent;
			}
			else{
				sessionStorage.setItem("ids",data.ids)
				sessionStorage.setItem("parentIDs",data.parent)
			}
			$("#frmColumn").attr("src", columCheckUrl);
		}});
	}



    //点击保存
	function columnClose(filterChecked) {
		//[ids, names, cascadeIDs]
		var ids = filterChecked[0];
		// 未展开的节点判空
		var notExpanded = filterChecked[3] ? filterChecked[3] : "";
        if(userID!="") {
			/*
            var theURL = "../../xy/user/RelColumnSave.do?userID=" + userID
                + "&siteID=" +  $("#siteID").val()
                + "&type=" + columnType
                + "&ids=" + ids
                + "&notExpanded=" + filterChecked[3]
            ;
			$.ajax({url:theURL, async:false, success:function(data){
				if (data) {
					alert("保存完毕");
					//columnCancel();
				}
			}});
			*/
			// 将请求方式改为post
			$.ajax({
				url: "../../xy/user/RelColumnSave.do",
				type: "POST",
				async: false,
				data: {
					"userID": userID,
					"siteID": $("#siteID").val(),
					"type": columnType,
					"ids": ids,
					"notExpanded": notExpanded
				},
				success:function(data){
				if (data) {
					alert("保存完毕");
					//columnCancel();
				}
			}});
        }
        else if(roleID!="") {
			$.ajax({
				url:"../../xy/system/saveColPermission.do",
				type:"POST",
				async:false,
				data: {
					"roleID": roleID,
					"siteID": $("#siteID").val(),
					"type": columnType,
					"ids": ids,
					"notExpanded": notExpanded
				},
				success:function(data){
				if (data) {
					alert("保存完毕");
					//columnCancel();
				}
			}});
        }


		/*
		$.ajax({url:theURL, type:_type, async:false, success:function(data){
			if (data) {
				alert("保存完毕");
				//columnCancel();
			}
		}});
	*/
	}
	
	function columnCancel() {
		var url = "../../e5workspace/after.do?UUID=" + UUID;
		$("#frmColumn").attr("src", url);
	}
	
	function changeSite(){
		$('#columnTab a:first').click();
	}
</script>
</body> 
</html>