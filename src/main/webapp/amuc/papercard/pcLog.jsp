<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String path = WebUtil.getRoot(request);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>订单详情</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <link href="css/orderDetial.css" type="text/css" rel="stylesheet"/>
    <script src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="./js/jquery.similar.msgbox.js"></script>
    <script type="text/javascript" src="../order/js/common.js"></script>
     <style type="text/css">   
    .border-table {   
        border-collapse: collapse;   
        border: none; 
        width:450px;
        height:80px;
    }   
    .border-table td {   
        border: solid #000 1px;
        text-align:center;   
        width:80px;
        height:30px;
    }   
</style> 
</head>
<body>
  <div class="base">
    <div>
      <p class="head">报卡日志</p>
    </div>
    <center>
    <div class="order-details clear" style="margin-top:10px;width:408px;">
    
    	<span style="float:left;">报卡号：<span id="num">23853323</span></span>
	    <span>状态：<span id="status">252</span></span>
      	<table id="tableExcel" class="border-table">   
			<tr>  
			   <td>操作记录</td>  
			   <td>对应状态</td> 
			   <td>操作时间</td>  
			   <td>备注</td> 
			   <td>操作人</td>
			</tr>  
		</table>  
    </div> 
    </center>   
  </div>
</body>
<script type="text/javascript">
  $(document).ready(function(){
	  var Request = new Object();
	  Request = getUrlStr();
	  docid = Request["DocID"];
	  //alert(docid)
	  var url = 'pcardLog.do';
		$.ajax({
			url:url,
			type:'get',
			data:{DocIDs:docid},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				$("#num").text(data.pcNo);
				$("#status").text(data.pcActiveStatus);
				$("#tableExcel").empty();
				$("#tableExcel").append("<tr><td>操作记录</td><td>对应状态</td><td>操作时间</td><td>备注</td><td>操作人</td></tr>");
				$("#tableExcel").append("<tr>"  
			    +"<td>新建</td>" 
			    +"<td>未激活</td>"
			    +"<td>"+(data.SYS_CREATED).substring(0,19)+"</td>" 
			    +"<td></td>"
			    +"<td>"+data.pcOperator+"</td>"
			    +"</tr>");
				if(parseInt(data.logLength)>0){
					for(var i = 0;i<parseInt(data.logLength);i++){
						$("#tableExcel").append("<tr>"  
							    +"<td>修改</td>" 
							    +"<td>已修改</td>"
							    +"<td>"+(data.log)[i].LogCreate.substring(0,19)+"</td>" 
							    +"<td></td>"
							    +"<td>"+(data.log)[i].operator+"</td>"
							    +"</tr>");
					}
				}
			    
			    if(data.pcActiveStatus=="激活"){
					$("#tableExcel").append("<tr>"  
						    +"<td>激活</td>" 
						    +"<td>已激活</td>"
						    +"<td>"+(data.pcActiveTime).substring(0,19)+"</td>" 
						    +"<td></td>"
						    +"<td>"+data.pcMember+"</td>"
						    +"</tr>");
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert("error status : " + XMLHttpRequest.status);
		    }
		});
  });
  function getUrlStr(){
	   var url = location.search; //获取url中"?"符后的字串   
	   var theRequest = new Object();   
	   if (url.indexOf("?") != -1) {   
	      var str = url.substr(1);   
	      strs = str.split("&");   
	      for(var i = 0; i < strs.length; i ++) {   
	         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);   
	      }   
	   }   
	   return theRequest;   
	}
</script>
</html>