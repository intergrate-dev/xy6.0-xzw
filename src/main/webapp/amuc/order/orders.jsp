<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String path = WebUtil.getRoot(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>添加订单-直接购买套餐</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <link href="css/style.css" type="text/css" rel="stylesheet"/>
    <script src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="./js/common.js"></script>
    <script type="text/javascript" src="./js/jquery.similar.msgbox.js"></script>
    <style type="text/css">
    
    </style>
</head>
<body>
<iframe id="iframe" name="iframe" style="display:none;"></iframe>
  <div class="base">
    <!--<div>
      <p class="head">购买数字报</p>
    </div>-->
    <div class="sel-package clear">
      <div style="background-color: #f6f6f6;" class="title-bar">
        <p>选择套餐</p>
      </div>
      <div class="package-group">
        <!-- <div class="item">
          <div class="item-top">
            <p class="package-name">套餐名称:<span>sdsf</span></p><p><span class="package-price">20元</span></p>
          </div>
          <p class="package-info">内含方正日报、人民日报、光明日报 共3份</p>
          <p class="package-date">有效期：<span> 连续 30 天</span></p>
        </div> -->
      </div>
    </div>
    <div class="confirm-msg">
      <div class="title-bar" style="background-color: #f6f6f6; height: 35px; line-height: 35px;">
        <p style="width: 100px; border-bottom: none; text-align: left; margin-left: 18px;">确认信息</p>
      </div>
      <p class="msg-tips">提交订单后，将分别生成对应的订单信息、并分别发送短信通知，请认真核对真实姓名及手机号码！</p>
      <div class="msg-group">
        <div class="msg-item">
         	<label for="name1" class="custform-label-require">真实姓名</label><input id="name1" type="text" class="name" value="" /><label for="tel1">手机号码</label><input id="tel1" type="text" class="mobile"  onblur='checkuser(1)'/><button class="del">删除</button>
          <input type="text" id="user1" class="user" name="user" value="" style="display:none"/>
        </div>
        <!-- <div class="msg-item">
          <i class="msg-index">2</i><label for="name2">真实姓名</label><input id="name2" type="text" /><label for="tel2">手机号码</label><input id="tel2" type="text" /><button class="del">删除</button>
        </div>
        <div class="msg-item">
          <i class="msg-index">3</i><label for="name3">真实姓名</label><input id="name3" type="text" /><label for="tel3">手机号码</label><input id="tel3" type="text" /><button class="del">删除</button>
        </div> -->
      </div>
      <button class="add-msg">新增一条</button>
    </div>
    <div class="pay">
      <div>
        <h2>支付方式</h2>
        <div class="pay-way clearfix">
          <span>
          	<input name="payway" type="radio" value="免费" checked="checked" />
          	<span class="pull-left">免费</span>
          </span>
          <span>
          	<input name="payway" type="radio" value="银行转账(全款)" />
          	<span class="pull-left">银行转账(全款)</span>
          </span>
          <span>
          	<input name="payway" type="radio" value="现金(全款)" />
          	<span class="pull-left">现金(全款)</span>
          </span>
        </div>
       
      
      	<input type="text" id="meal" name="meal" value="" style="display:none"/>
      	<input type="text" id="users" name="users" value="" style="display:none"/>
      	<input type="text" id="operator" name="operator" value="${sessionScope.sysUser.userName}" style="display:none"/>
        <input type="hidden" id="DocIDs" name="DocIDs" value="<%=request.getParameter("DocIDs")%>" /> 
        <input type="hidden" id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>" />
        <input type="hidden" id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>" />
      </div>
      
    </div>
    <p class="pay-head">共<font class="pack-num" style="color:#f00">0</font>个套餐，(<span class="calculation">0</span>)<span id="usersLength"></span>,合计：<font class="total" style="color:#f00">0</font>元</p>
    <button class="submit pull-left">提交订单</button>
    <span style="color: #999; float: left; margin-top: 36px; margin-left: 10px;" class="pay-tips">订单一经提交，将不能取消订单和修改订单，请再次确认收货人信息和支付金额</span>
  	<input type="text" id="totalMoney" name="totalMoney" value="" style="display:none"/>
  </div>
</body>
<script type="text/javascript">
  $(document).ready(function(){
	  setMeal();
  });
  // $("body").off()
  $("body").on("click",".item",function(){
    var calculation = '',total = 0.00,packNum = 0,mealid = '',mealname = '';
    $(this).toggleClass("active");
    $(".item.active").each(function(index,ele){
      var val = parseFloat($(ele).find(".item-top").find(".package-price").text().substring(1,$(ele).find(".item-top").find(".package-price").text().length));
      calculation += val + "+";
      total = accAdd(total,val);
      packNum = parseInt(index) + 1;
      //
      mealid += ","+$(ele).find(".mealid").val();
      mealname += ","+$(ele).find(".item-top").find(".package-name").find(".package-name1").text();
    })
    calculation != '' ? calculation = calculation.substring(0,calculation.length-1):calculation = 0;
    $(".pack-num").text(packNum);
    $(".calculation").text(calculation);
    var usersLength = $('input[class="user"]').length;
    $("#usersLength").text("*"+usersLength);
    $(".total").text(total*usersLength);
    $("#totalMoney").val(total);
    var meal = '{"setMealName":"'+mealname.substr(1,mealname.length)+'","setMealID":"'+mealid.substr(1,mealid.length)+'"}';
    $("#meal").val(meal);
  }).on("click",".del",function(){
    $(this).parent().remove();
    var usersLength = $('input[class="user"]').length;
    $("#usersLength").text("*"+usersLength);
    var paytotal = $("#totalMoney").val();
    $(".total").text(paytotal*usersLength);
  }).on("click",".add-msg",function(){
    var container = $(this).prev(), 
        index = parseInt(container.find(".msg-item:last-child").find(".msg-index").text()),
        str='';
        isNaN(index)? index = 1 : index = index +1;
    str += "<div class='msg-item'>"
//      +  "<i class='msg-index'>"+index+"</i>"
        +  "<label for='name"+index+"'>真实姓名</label><input id='name"+index+"' type='text' class='name'/>"
        +  "<label for='tel"+index+"'>手机号码</label><input id='tel"+index+"' type='text' class='mobile' onblur='checkuser("+index+")'/>"
        +  "<button class='del'>删除</button>"
        +  "<input type='text' id='user"+index+"' class='user' name='user' value='' style='display:none'/>"
        +  "</div>";
    container.append(str);
    var usersLength = $('input[class="user"]').length;
    var paytotal = $("#totalMoney").val();
    $("#usersLength").text("*"+usersLength);
    $(".total").text(paytotal*usersLength);
  }).on("click",".submit",function(){
	var meal = $("#meal").val();
	var users ="";
	var flag = true;
	for(var i=0;i<$('input[class="user"]').length;i++){
		var name = $('input[class="name"]').eq(i).val();
		//alert(name)
		var mobile = $('input[class="mobile"]').eq(i).val();
		var user = ',{"realName":"'+name+'","mobile":"'+mobile+'"}';
		//alert($('input[class="name"]').eq(i).val())
		if(name == "" || typeof(name) == "undefined" ){
			//alert(i);
			$.MsgBox.Alert("温馨提示","请输入用户真实姓名！");
			flag = false;
            return flag;
		}else if(mobile =="" || typeof(mobile) == "undefined"){			
			//alert("请输入用户手机号！");
			$.MsgBox.Alert("温馨提示","请输入用户手机号！");
			flag = false;
            return flag;
		}
		users = users + user;
	}
	if(!flag){
		//alert(flag)
		return; 	
	}
	var users1 = "["+users.substr(1,users.length)+"]";
	//alert(users1.length);
	
	var operator = $("#operator").val();
	var pay = $('.pay-way input[name="payway"]:checked').val();
	var b = $(".msg-items");
    var a = $(".item.active");
    if(a.length <= 0){
      //alert("请至少选择一个套餐！");
      $.MsgBox.Alert("温馨提示","请至少选择一个套餐！");
      return;
    }
    //alert($(".total").text())
    create(meal,users1,pay,operator,$("#totalMoney").val(),$("#siteID").val());
    on_Close();
  });
  
  function setMeal(){
	  var siteID = $("#siteID").val();
	  var url1 ='../setmeal/FindSetMeal.do';
		$.ajax({
			url:url1,
			type:'get',
			data:{siteID:siteID},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				//alert(JSON.stringify(data));
				var code = data.code;
				var data = data.data;
				if(code == '1'){
					$(".package-group").empty();
					
					for(var i=0;i<data.length;i++){
					
						$(".package-group").append("<div class='item'><div class='item-top'><p class='package-name'>套餐名称：<span class='package-name1'>"
							+data[i].setMealNmae+"</span></p><p><span class='package-price'>￥"+data[i].setMealMoney+"</span></p></div><p class='package-info'>内含"
							+data[i].setMealContent+" 共"+data[i].paperNumber+"份</p><p class='package-date'>有效期：<span>连续"+data[i].EffectTime+data[i].expiryDate+data[i].ExpireTime+"天</span></p><input type='text' class='mealid' value='"+data[i].docid+"' style='display:none'/></div>");
					} 
					
				}else if(code == '0'){  //参数错误
					$.MsgBox.Alert("温馨提示",msg);
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	//alert("error status : " + XMLHttpRequest.status);
		    }
		});
	}
  
  function checkuser(index){
	  var realname1 = $("#name"+index).val();
	  var mobile1 = $("#tel"+index).val();
	  var siteID = $("#siteID").val();
	  var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/; 
	  if(!myreg.test(mobile1)) 
	  { 
	      //alert('请输入有效的手机号码！'); 
	      $.MsgBox.Alert("温馨提示",'请输入有效的手机号码！');
	      return false; 
	  } 
	  for(var j=0;j<$('input[class="user"]').length-1;j++){
		if(j==(index-1)){
			j++ ;
		}  
		if(mobile1 == $('input[class="mobile"]').eq(j).val()){
			$("#tel"+index).val("");
			$.MsgBox.Alert("温馨提示",'手机号码重复！');
		}
		}
	  var url2 ='../order/checkuser.do';
		$.ajax({
			url:url2,
			type:'get',
			data:{mobile:mobile1,siteID:siteID},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				//alert(JSON.stringify(data));
				var code = data.code;
				var msg = data.msg;
				var mail = data.mail;
				var userName = data.userName;
				if(code == '1003'){
					
				}else if(code == '1000' || code == '1001' || code == '1002'){  //参数错误、该设备已经添加过邀请码使用记录、邀请码不存在
					$.MsgBox.Alert("温馨提示",msg);
					$("#tel"+index).val("");
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	//alert("error status : " + XMLHttpRequest.status);
		    }
		});
	}
  
  function create(meal,users,pay,operator,total,siteID){
	  var url3 = '../order/create.do';
		$.ajax({
			url:url3,
			type:'get',
			data:{meal:meal,users:users,pay:pay,operator:operator,total:total,siteID:siteID},
			dataType:"json",
			cache:false,
			async:false,
			success:function(data){
				//alert(JSON.stringify(data));
				var code = data.code;
				var msg = data.msg;
				if(code == '1003'){
				}else if(code == '1000' || code == '1001' || code == '1002'){  //参数错误、该设备已经添加过邀请码使用记录、邀请码不存在
					$.MsgBox.Alert("温馨提示",msg);
				}
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	//alert("error status : " + XMLHttpRequest.status);
		    }
		});
	}
  
  function clos(){
		document.getElementById("iframe").contentWindow.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
		}
  
  function on_Close() {
		window.onbeforeunload = "javascript:void(0);";
		window.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
		parent.location.reload();
	}
  
  function accAdd(arg1,arg2){ 
	  var r1,r2,m; 
	  try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0} 
	  try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0} 
	  m=Math.pow(10,Math.max(r1,r2)); 
	  var result = (arg1*m+arg2*m)/m; 
	  return result.toFixed(2);
	  } 
</script>
</html>
