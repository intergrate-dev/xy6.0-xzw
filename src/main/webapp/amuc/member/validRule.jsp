<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false" />
<%@page pageEncoding="UTF-8"%>

<html>
<head>
	<title>有效性规则定义</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css" />
	<link type="text/css" rel="StyleSheet" href="../../e5style/e5form-custom.css" />
	<link type="text/css" rel="stylesheet" href="../../amuc/script/bootstrap/css/bootstrap.css"/>
	<link type="text/css" rel="stylesheet" href="../../amuc/css/form-custom.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript">
		var org ="";
		var att  ="";
		var org_create ="false";
		var att_create ="false";
		var postform ={
			init:function(){
				postform.chushihua();
				$("#btnPost").click(postform.submit);
			},
			chushihua:function(){
				var dataOrginal = '${validrule_sour}';
				if(dataOrginal==""){
					org_create = "true";
				}else{
					var str= new Array();   
					str=dataOrginal.split(",");
					for (i=0;i<str.length ;i++ )   //根据系统参数设置复选框已选择
				    {   
				    	$("[value = "+str[i]+"]:checkbox").attr("checked", true);
				    	
				    } 
				}
				var attribute_attr = '${validrule_attr}';
				if(attribute_attr==""){
					att_create = "true";
				}
				else{
					 var attr= new Array();
					 attr=attribute_attr.split(",");
					 for (i=0;i<attr.length ;i++ )   //根据系统参数设置复选框已选择
					 {   
					    $("[value = "+attr[i]+"]:checkbox").attr("checked", true);
					 } 
				}
			},
			getData:function(){
				org="";
				att="";
				$("input[name='org']:checked").each(function(){
					var temp = ($(this).val());
					org +=temp+",";
				});
				org = org.substring(0,org.length-1);
				$("input[name='ck']:checked").each(function(){
					var temp = ($(this).val());
					att +=temp+",";
				});
				att = att.substring(0,att.length-1);
			},
			submit:function(){
				postform.getData();
				var url = "../../amuc/member/Member.do?a=rules&att="+att +"&org="+org+"&org_create="+org_create+"&att_create="+att_create;
				url = encodeURI(url);
				$.ajax({
					type:"post",
					url :url,
				    dataType:"text",
				    async:false,
				    success:function(data){
                         alert("保存成功");
                         window.location.reload();
				    }
				});
			}
		}
		
		$(function() {
		    postform.init();
		});
	</script>
	<style>
		#label{
			margin:10px;
		}
		#content_ori{
			position:relative;
			width:82%;
			left:6%;
			overflow:auto;
		}
		#content_attr{
			position:relative;
			left:6%;
			width:82%;
			overflow:auto;
		}
		li{
			float:left;
			margin:5px;
			list-style-type:none
		}
		#btnPost{
			position:relative;
			left:40%;
		}
		input:checked+span{
			background:#999;
			color:#fff;
		}
	</style> 
</head>
<body>
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form id="postForm" name="postForm" action="*.do" method="post" target="iframe">
		<div id="ori" >
		    <label id="label" class="label">数据来源：</label>
		    <div id="content_ori">
			    <c:forEach var="category" items="${category}" varStatus="status">
				    <li>
				    	<label for="<c:out value="${category.catID}"/>">
				    		<input type="checkbox" name="org" class="items" id="<c:out value="${category.catID}"/>" value="<c:out value="${category.catName}"/>"/>
				    		<span><c:out value="${category.catName}"/></span>
				    	</label>
				    </li>
				</c:forEach>  
			</div>
		</div>
		<div id="attri" class="attri">
			<label id="label" class="label">必填属性：</label>
			<div id="content_attr">
				<c:forEach items="${attribute}" var="attribute" >
					<li>
						<label for="<c:out value="${attribute.key}"/>">
							<input type="checkbox" name="ck" class="items" id="<c:out value="${attribute.key}"/>" value="<c:out value="${attribute.key}"/>"/>
							<span><c:out value="${attribute.value}"/></span>
						</label>
					</li>
				</c:forEach> 
			</div>
		</div>
		<div id="btn">
			<input type="button" id="btnPost" name="btnPost" value="保存"/>
		</div>
	</form>
</body>
</html>