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
		/*.tab-content{width:380px;float:left;}*/
		.frm{border:0;width:85%;height:450px; float: right;}
		.nav-stacked li a{
			font-family: "microsoft yahei";
			color: #666;
			font-size: 13px;
		}
	</style>
</head> 
<body>
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
		<li><a href="#home" data-type="50">
			<span>分类操作</span>
		</a></li>
		<li><a href="#profile" data-type="51">
			<span>分类权限</span>
		</a></li>
    </ul>
    <div class="tab-content"> 
      <div class="tab-pane active" id="frmDiv">
      	<iframe id="frmColumn" src="" class="frm"></iframe>
      </div> 
    </div> 
<script>
	var roleID = "<c:out value="${roleID}"/>";
	var DocLibID = "<c:out value="${DocLibID}"/>";
	//var DocIDs = "<c:out value="${DocIDs}"/>";
	//var UUID = "<c:out value="${UUID}"/>";
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
//          $("#frmColumn").height($(window.parent).height() - 170).width(500);
 			$("#frmColumn").height($(window.parent).height() - 170);  //资源权限设置不能设置宽度
        };
	});
	
    //切换站点的时候，切换frame中的内容
	function changeType(src) {
		var theURL = "";
		var columCheckUrl = "";
		var type = src.attr("data-type");
		type = type==0? 50 : type;
    	//当type=50时，设置分类操作
		//当type=51时，设置分类权限
    	if (parseInt(type) == 50){
    		columCheckUrl = "../column/OriginalCheck.jsp?type=all"
    			+ "&siteID=" + $("#siteID").val();
    	} else if (parseInt(type) == 51){
    		columCheckUrl = "../column/OriginalCheck.jsp?type=role"
    			+ "&siteID=" + $("#siteID").val()
    			+ "&roleID=" + roleID;
    	} else {
    		columCheckUrl = "../column/OriginalCheck.jsp?type=all"
    			+ "&siteID=" + $("#siteID").val();
    	}
    	
    	if(roleID!="") {
            theURL = "../../xy/system/getColPermission.do?roleID=" + roleID
                + "&siteID=" + $("#siteID").val()
            	+ "&type=" + type;
		}
    	
    	
		$.ajax({url:theURL, async:false, success:function(data){
		    data = JSON.parse( data );
			oldIDs = data.ids;
			if(data.ids.length<2000){
				columCheckUrl += "&ids="+data.ids + "&parentIDs=" + data.parent;
			}
			else{
				sessionStorage.setItem("ids",data.ids);
				sessionStorage.setItem("parentIDs",data.parent);
			}
			$("#frmColumn").attr("src", columCheckUrl);
		}});
	}



    //点击保存
	function columnClose(filterChecked) {
		var ids = filterChecked[0];

        if(roleID!="") {
			$.ajax({
				url:"../../xy/system/saveColPermission.do",
				type:"POST",
				async:false,
				data: {
					"roleID": roleID,
					"siteID": $("#siteID").val(),
					"type": columnType,
					"ids": ids,
					"notExpanded": filterChecked[3]
				},
				success:function(data){
				if (data) {
					alert("保存完毕");
				}
			}});
        }
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