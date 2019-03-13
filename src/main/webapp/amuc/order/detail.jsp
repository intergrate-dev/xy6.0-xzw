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
    <script src="./js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="./js/common.js"></script>
    <script type="text/javascript" src="./js/jquery.similar.msgbox.js"></script>
</head>
<body>
  <div class="base">
    <div>
      <p class="head">订单详情</p>
    </div>
    <div class="order-details clear">
      <div class="order-info">
        <p>订单号：<span id="ordernum"></span></p>
        <p>下单时间：<span id="create"></span></p>
        <p>状态：<span id="orderstatus"></span></p>
        <p>来源：<span id="ordersource">2121</span></p>
      </div>
      <div class="user-info">
        <h4>会员信息</h4>
        <p>用户名：<span id="username"></span></p>
        <p>手机号码/邮箱：<span id="mobile"></span></p>
      </div>
      <div class="contacts-info">
        <h4>联系人信息</h4>
        <p>姓名：<span id="realname"></span></p>
        <p>手机号码：<span id="mobile1"></span></p>
      </div>
    </div>
    <div class="package-msg">
      <!-- <div style="border-bottom:#333 solid 1px;padding:10px 50px 10px 0;">
        <h2 class="pack-name">套餐名称1fd</h2>
        <p class="pack-right">
          <span class="pack-price">￥50.00</span>
          <span class="pack-num">x 1</span>
        </p>
        <p class="pack-info">内含方正日报、人民日报、光明日报 共3份,有效期30天</p>
      </div> -->
      <div id="setmeal"></div>
      <div class="pack-money">
        <p>订单总额:<font style="color:#ff9900;" >￥<span id="total1"></span></font></p>
        <p style="font-size:25px;font-weight:bold;">实付金额:<font style="color:#ff9900;">￥<span id="total2"></span></font></p>
      </div>
    </div>
    <div class="pay-msg">
      <div class="payment2" id="cancelorder" style="margin-right:5px;"><p style="text-align:center;margin-top:30px;color:#ff9900;font-size:20px;font-weight:bold;">待付款</p></div>
      <div class="payment1" id="cancel" style="margin-right:5px;"><p style="text-align:center;margin-top:30px;color:#999999;font-size:20px;">订单已取消</p></div>
      <div class="payment" id="payment">
      	<div></div>
        <h4>支付信息</h4>
        <p class="pay-ways">支付方式 <span id="payWay">免费</span></p>
        <p class="pay-time">支付时间 <span id="payTime"></span></p>
        <p>支付交易号 <span id="payNumber"></span></p>
      </div>
      <div class="invoice">
        <h4>状态：<span style="color:#ff9900;" id="invoice"></span></h4><h4>发票信息</h4>
        <p class="invoice-ways">发票类型: <span>普通发票</span></p>
        <p class="invoice-title">发票抬头 :
        	<span id="invoice1">个人</span>
        	<span id="unitNum" style="margin-left: 25px;"></span>
			<span id="unitAddr" style="margin-left: 25px;"></span>
        </p>
      </div>
      <div style="text-align:center;padding:20px 0;">
      	<button class="off-order" onclick="cancel()">取消订单</button>
        <button class="send-msg">短信重发</button>
        <button class="issue-invoice">开具发票</button>
      </div>
    </div>
    <input type="hidden" id="DocLibID" name="DocLibID" value="<%=request.getParameter("DocLibID")%>"/>
	<input type="hidden" id="DocID" name="DocID" value="<%=request.getParameter("DocIDs")%>"/>
	<input type="hidden" id="FVID" name="FVID" value="<%=request.getParameter("FVID")%>"/>
	<input type="hidden" id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>"/>
	<input type="hidden" id="setMealID" name="setMealID" value=""/>
  </div>
</body>
<script type="text/javascript">
  $(document).ready(function(){
	  setMsg();
	  
  });
  
  function setMsg(){
	  var docid = $("#DocID").val();
	  var url = 'getMsg.do';
		$.ajax({
			url:url,
			type:'get',
			data:{docid:docid},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				//alert(JSON.stringify(data));
				var code = data.code;
				var data = data.data;
				var msg = data.msg;
				if(code == '1000'){
					$("#ordernum").text(data[0].orderNum);
					$("#create").text(data[0].createTime.substr(0,data[0].createTime.length-2));
					if(data[0].orderStatus==0){
						$("#orderstatus").text("未支付");
					}
					if(data[0].orderStatus==1){
						$("#orderstatus").text("已支付");
					}
					if(data[0].orderStatus==2){
						$("#orderstatus").text("已取消");
					}
					if(data[0].orderStatus==3){
						$("#orderstatus").text("已提交");
					}
					if(data[0].orderStatus==4){
						$("#orderstatus").text("已完成");
					}
				    if(data[0].orderSource==0){
						$("#ordersource").text("系统内创建");
					}
					if(data[0].orderSource==1){
						$("#ordersource").text("PC");
					}
					if(data[0].orderSource==2){
						$("#ordersource").text("移动");
					}
					$("#payWay").text(data[0].payType);
					$("#payTime").text(data[0].payTime);
					$("#payNumber").text(data[0].payNumber);
					$("#username").text(data[0].userName);
					$("#realname").text(data[0].realName);
					$("#mobile").text(data[0].mobile);
					$("#mobile1").text(data[0].mobile);
					$("#total1").text(data[0].total);
					if(data[0].orderStatus==0||data[0].orderStatus==3||data[0].payWay=="免费"){
						$("#total2").text("0.00");
					}else{
						$("#total2").text(data[0].total);
					}
					if(data[0].invoice==0){
						$("#invoice").text("未开");
						$("#invoice1").html("未开");
					}
					if(data[0].invoice==1){
						$("#invoice").text("已开");
						$("#invoice1").html(data[0].taxpayer);
					}
					if(data[0].taxpayer != "个人" && data[0].invoice != "0"){
						if(data[0].unitNum == undefined ){
							$("#unitNum").html('单位税号:');
						}else{
							$("#unitNum").html('单位税号: &nbsp;'+data[0].unitNum);
						}
						if(data[0].unitAddr == undefined ){
							$("#unitAddr").html('单位地址:');
						}else{
							$("#unitAddr").html('单位地址: &nbsp;'+data[0].unitAddr);
						}

					}
					//$("#setMealID").val(data[0].setMealID);
					setMeal(data[0].setMealID);
					if(data[0].orderStatus == 0||data[0].orderStatus == 3){
						//alert("已提交")
						$(".send-msg").hide();
						$(".issue-invoice").hide();
						$("#cancel").hide();
						$("#payment").hide();				
					}else if(data[0].orderStatus == 1||data[0].orderStatus == 4){
						$(".off-order").hide();
						$("#cancel").hide();
						$("#cancelorder").hide();
					}else if(data[0].orderStatus == 2){
						$(".off-order").hide();
						$(".send-msg").hide();
						$(".issue-invoice").hide();
						$("#cancelorder").hide();
						$("#payment").hide();
					}
					
				}else if(code == '1001'){  //参数错误
					$.MsgBox.Alert("温馨提示",msg);
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert("error status : " + XMLHttpRequest.status);
		    }
		});
	}
  
  function setMeal(ids){
	  var url1 = '../setmeal/FindSetMealByIds.do';
		$.ajax({
			url:url1,
			type:'get',
			data:{ids:ids},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				//alert(JSON.stringify(data));
				var code = data.code;
				var data = data.data;
				var msg = data.msg;
				if(code == '0'){
					$("#setmeal").empty();
					
					for(var i=0;i<data.length;i++){				
						$("#setmeal").append("<div style='border-bottom:#333 solid 1px;padding:10px 50px 10px 0;'><h2 class='pack-name'>"+data[i].setMealNmae+"</h2><p class='pack-right'>"
				        +  "<span class='pack-price'>￥"+data[i].setMealMoney+"</span>"
				        +  "<span class='pack-num'>x 1</span>"
				        +"</p>"
				        +"<p class='pack-info'>内含"+data[i].setMealContent+" 共"+data[i].paperNumber+"份,有效期:"+data[i].EffectTime+data[i].expiryDate+data[i].ExpireTime+"</p>"
				        +"</div>");
					} 				
				}else if(code == '1' || code == '2'){  //参数错误
					$.MsgBox.Alert("温馨提示",msg);
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert("error status : " + XMLHttpRequest.status);
		    }
		});
	}
  function cancel(){
	  var docid = $("#DocID").val();
	  var url = 'cancel.do';
		$.ajax({
			url:url,
			type:'get',
			data:{docid:docid},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				var code = data.code;
				var msg = data.msg;
				if(code == '1000'){
					$.MsgBox.Alert("温馨提示",msg);
					window.location.reload(); 
				}else if(code == '1001'){  //参数错误
					$.MsgBox.Alert("温馨提示",msg);
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert("error status : " + XMLHttpRequest.status);
		    }
		});
	}
</script>
</html>