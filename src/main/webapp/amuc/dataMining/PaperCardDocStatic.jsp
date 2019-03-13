<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<%@page import="java.net.InetAddress"%>
<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="IE=edge" http-equiv="X-UA-Compatible" />
<title><%=com.founder.e5.context.Context.getSystemName()%>-<c:out
		value="${subTab.name}" /> - [<%=InetAddress.getLocalHost().getHostAddress()%><%=request.getContextPath()%>]</title>
<link type="text/css" rel="stylesheet"
	href="./script/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="./../amuc/css/main.css" />
<link type="text/css" rel="stylesheet" href="./css/sub-page.css" />
<link type="text/css" rel="stylesheet"
	href="./../e5script/lhgcalendar/lhgcalendar.bootstrap.css">
<script type="text/javascript" src="./../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript"
	src="./../e5script/jquery/jquery.table2excel.js"></script>
<script type="text/javascript" src="./../e5script/e5.min.js"></script>
<script type="text/javascript"
	src="./../amuc/script/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="./../e5script/calendar/usecalendar.js"></script>
<script type="text/javascript"
	src="./../e5script/lhgcalendar/lhgcalendar.js"></script>




<style>
input {
	border: 1px solid #ddd;
	width: 100px;
	border-radius: 0;
	background-color: #fff !important;
	font-size: 12px;
	padding-left: 5px;
}
</style>
</head>
<body>
	<input type="hidden" id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>" />
	<%-- <%@include file="../inc/Header.inc"%> --%>
	<input id="orderdate" type="hidden">
	<div class="wrap" style="margin:-1px; border: none;">
		<!--tab-->
		<ul class="channels">
		<li class="channelTab select" id="channelTab0" onclick="_tabClick(0)">总览</li>		
		<li class="channelTab" id="channelTab1" onclick="_tabClick(1)">报卡明细</li>
	</ul>
		
	
	</div>
	<div id="contentFrame" class="wrap" style="width:96%;height:100%;overflow:auto;margin-left:15px">
	<br/>
	<center>
		销售时间 ：<input id="beginTime" readonly="true" onclick="showCalendar('beginTime', 'yyyy-mm-dd');">
		--
			  	<input id="endTime" readonly="true" onclick="showCalendar('endTime', 'yyyy-mm-dd');">
		<!--<button onclick="search()">查询</button>
		<button onclick="dao()">导出</button>-->
		<img style="margin-left: 5px;" onclick="search()" src="img/11.png" alt="" />
		<img style="margin-left: 5px;" onclick="dao()" src="img/13.png" alt="" />
		<br/><br/>
		<table border="1px" id="mytable" class="table table-striped"></table>
	</center>
</div>
</body>
</html>

<script type="text/javascript">
		var siteID = $("#siteID").val();
		var istab=true;
		var date = new Date();
		var nowD = date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2);
		date.setMonth(date.getMonth() - 1);
		var month = date.getMonth();
		month = ((month==0)?(12):(month));
		var befD = date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2);
		var pathPrefix = "..";
		
		function _tabClick(t) {
			$(".channelTab").removeClass("select");
			if(t==0){
				$("#channelTab0").addClass("select");
				loa(1);
			}else if(t==1){
				$("#channelTab1").addClass("select");
				loa(2);
			}		
		}
		
		
		function loa(va){
			if(va==1)
			{
				istab=true;
				$("#beginTime").val(befD);
				$("#endTime").val(nowD);
				$("#li1").attr("class","active");
				$("#li2").removeClass("active");
						loaddata(1);
	
			}else{
				istab=false;
				$("#beginTime").val(befD);
				$("#endTime").val(nowD);
				$("#li2").attr("class","active");
				$("#li1").removeClass("active");
				loaddata(2);
			}
		}
		
		function loaddata(fin){
			$.ajax({
				cache: true,
				type: "POST",
				url:"<%=request.getContextPath()%>/amuc/setmeal/Static.do?a=findPaperCard&beginTime="+$("#beginTime").val()+"&endTime="+$("#endTime").val()+"&siteID="+siteID,
			    success: function(data) {
			  orderdata=JSON.parse(data);
						if(orderdata.code==0){
								$("#orderdate").val(data);
								if(fin==1){
								pandect()
								}else if(fin==2){
								detail()
								}
						    }else{
						alert("查询失败");
					}
					}

			});
		}
		
	function search(){
		if(istab){
			loaddata(1)
		}else{
			loaddata(2)
		}
	}
		
		function pandect(){
	
			
			var orderdata= JSON.parse($("#orderdate").val());
					$("#mytable").html("")
					$("#mytable").append("<tr><td  width='100px'>日期</td><td  width='100px'>报卡金额</td><td  width='100px'>报卡数</td></tr>")
						if(orderdata.data.length>0){
							var data=orderdata.data
							var TotalMoney=0;
							var Totality=0;
							for ( var i=0;i<data.length;i++) {
							TotalMoney+=parseFloat(data[i].TotalMoney);
							Totality+=Number(data[i].Totality);
								$("#mytable").append("<tr><td>"+formatDate(data[i].Time)+"</td><td>"+numberWithCommas(data[i].TotalMoney)+"</td><td>"+data[i].Totality+"</td></tr>")
							}
							$("#mytable").append("<tr><td>合计</td><td>￥"+numberWithCommas(TotalMoney)+"</td><td>"+Totality+"</td></tr>")
						 }

		}
		
		function detail(){
		
		$.ajax({
				cache: true,
				type: "POST",
				url:"<%=request.getContextPath()%>/amuc/setmeal/Static.do?a=findAllTypeCode&siteID="+siteID,
					success : function(data) {

						data = JSON.parse(data);
						if (data.code == 0) {
							data = data.data;
							$("#mytable").html("")
							var str1 = "<tr><td rowspan='2'  width='100px'>日期</td>";
							var str2 = "<tr>"
							for ( var i = 0; i < data.length; i++) {

								str1 += "<td colspan='2'  width='180px'>" + data[i].name
										+ "</td>";
								str2 += "<td width='90px'>金额</td><td width='90px'>卡数</td>";

								data[i].money = 0
								data[i].number = 0
							}
							str1 += "<td colspan='2'  width='180px'>小计</td></tr>";
							str2 += "<td width='90px'>金额</td><td width='90px'>卡数</td></tr>";
							$("#mytable").append(str1);
							$("#mytable").append(str2);

							var orderdata = JSON.parse($("#orderdate").val());

							orderdata = orderdata.data;
							var Totality = 0;
							var TotalMoney = 0;
							for ( var a = 0; a < orderdata.length; a++) {
								if(orderdata[a].Data === "[]") {
									continue;
								} else {
									orderdata[a].Data = eval(orderdata[a].Data
											.substring(0,
													orderdata[a].Data.length - 2)
											+ "]");	
								}
								var val = "<tr><td>" + formatDate(orderdata[a].Time)
										+ "</td>"
								for ( var b = 0; b < data.length; b++) {
									var isok = false;
									for ( var c = 0; c < orderdata[a].Data.length; c++) {
										if (data[b].id == orderdata[a].Data[c].id) {
											data[b].money += parseFloat(orderdata[a].Data[c].data.money);
											data[b].number += Number(orderdata[a].Data[c].data.number);

											isok = true;
											val += "<td>"
													+ numberWithCommas(orderdata[a].Data[c].data.money)
													+ "</td><td>"
													+ orderdata[a].Data[c].data.number
													+ "</td>"
										}

									}
									if (!isok) {
										val += "<td>0</td><td>0</td>"

									}
								}
								Totality += Number(orderdata[a].Totality);
								TotalMoney += parseFloat(orderdata[a].TotalMoney);
								val += "<td>" + numberWithCommas(orderdata[a].TotalMoney)
										+ "</td><td>" + orderdata[a].Totality
										+ "</td></tr>"
								$("#mytable").append(val);
							}
							var he = "<tr><td>合计</td>"
							for ( var i = 0; i < data.length; i++) {
								he += "<td>￥" + numberWithCommas(data[i].money) + "</td><td>"
										+ data[i].number + "</td>"

							}
							he += "<td>￥" + numberWithCommas(TotalMoney) + "</td><td>" + Totality
									+ "</td></tr>"
							$("#mytable").append(he);
						} else {
							alert("查询失败");
						}

					}
				});

	}

	$(function() {
		$("#contentFrame").css("height",
				document.documentElement.clientHeight - 185 + "px");
		$("#beginTime").val(befD);
		$("#endTime").val(nowD);
		loaddata(1);

	})
	function dao() {
		if (istab) {
			var nam = "总览"
		} else {
			var nam = "明细"
		}
		$("#mytable").table2excel({

			filename : "报卡" + nam,
			fileext : ".xls",
		});

	}
	
	function numberWithCommas(value) {
	    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}
	
	function formatDate(d) { 
		return d.toString().replace(/-/g,"/");
	}
</script>