$(function(){
	//event
	$("#login-btn").click(doAction);
	$(document).keydown(function(event){
		if (event.keyCode == 13 && event.target.getAttribute("id") !== "login-btn"){
			doAction();
		}
	});
	//focus
	$("#UserCode").val( e5.utils.getCookie("l$code"));
	if ($("#UserCode").val()) {
		$("#UserPassword").focus();
	} else {
		$("#UserCode").focus();
	}
	//read config
	if (!headerinfo["captchaEnabled"]) {
		$.ajax({url:"../e5workspace/security/securitypolicy.do", dataType:"json",async:false, 
			success:function(data) {
			if (data){
				$.each(data,function(i,n){
					if (n.captchaEnabled) {
						headerinfo.captchaEnabled = true;
						$(".security_class").css("display","block");
						$("#randImg").attr("src","../e5workspace/security/captcha.do");
					}
					if (n.transferEncrypt) {
						headerinfo.transferEncrypt = true;
					}
				});
			}
		}});
	}
});
 
function doAction() {
	if (!headerinfo["captchaEnabled"]) {
		aftercaptcha();
	} else {
		var c = document.getElementById("inputcode").value;
		var checkCodeURL = "../xy/security/verifyCaptcha.do";
		var params = {checkCode : c};
		$.post(checkCodeURL, params, function(data){
			if (data){
				if(data == 0){
					alert(headerinfo.errorcaptcha);
				}else if (data == -1){
					alert(headerinfo.sessionlose);
				}else if(data == 1){
					aftercaptcha();
				}
			}
		});
	}
}
function aftercaptcha() {
	var theURL = "../xy/auth.do";
	var params = {
		UserCode : document.getElementById("UserCode").value,
		UserPassword : document.getElementById("UserPassword").value
	}
	if (headerinfo.transferEncrypt){
		encriptpwd(theURL, params);
	} else{
		access(theURL, params);
	}
}
function encriptpwd(theURL, params) {
	var data = {};
	var keyUrl="../e5workspace/security/pubkey.do";
	$.post(keyUrl, data, function(xmlDoc) {
		 if (xmlDoc){
			var children = xmlDoc.documentElement.childNodes;
			var modulus = getText(children[0]);
			var exponent = getText(children[1]);
			
			var key = RSAUtils.getKeyPair(exponent, '', modulus);
			var psw = document.getElementById("UserPassword").value;
			if(psw && psw.length > 0 && key)
				encryptPsw = RSAUtils.encryptedString(key, encodeURIComponent(psw));
			else
				encryptPsw = psw;
	
			params["UserPassword"] = encryptPsw;
			params["encrypttools"] = "rsa";
			
			access(theURL, params);
		} else {
			alert(headerinfo[errorpubkey]);
		}
	}, "xml");
}
//��̨url���ʷ���
function access(theURL, params){
	$.post(theURL, params, function(result) {
		debugger;
		if (result.indexOf("siteID:") != 0) {
			if (headerinfo[result])
				alert(headerinfo[result]);
			else
				alert(headerinfo.otheragain + ":" + result);
		} else {
			var usiteID = result.substring(7);
			$("#usiteID").val(usiteID);
			var thePhoneURL = "../xy/getAuthPhone.do";
			var params2 = {
					UserCode : document.getElementById("UserCode").value
			}
			$.post(thePhoneURL, params, function(data) {
				if(data != "noneNumber"){
					show(data);
				}else{
					alert("用户手机号码为空！")
				}
			});
		}
	});
	function show(data)  //显示隐藏层和弹出层
	{
		var phoneBegin = data.substring(0,4);
		var phoneEnd = data.substring(8,11);
	   var hideobj=document.getElementById("hidebg");
	   hidebg.style.display="block";  //显示隐藏层
	   hidebg.style.height=window.screen.availHeight +"px";  //设置隐藏层的高度为当前页面高度
	   document.getElementById("hidebox").style.display="block";  //显示弹出层
	   var html = "验证注册手机号："+phoneBegin+"***"+phoneEnd+"";
	   $("#checkPhone").html(html);
	   $("#phone").val(data);
	}
	
}
function randImgFresh(){
	var myDate = new Date();
	document.getElementById("randImg").src = "../e5workspace/security/captcha.do?mydate=" + myDate.toString();
}

function getText(oNode){
	return (oNode.text) ? oNode.text : oNode.textContent;
}
function hide()  //去除隐藏层和弹出层
{
   document.getElementById("hidebg").style.display="none";
   document.getElementById("hidebox").style.display="none";
}
function sendCode(obj){  
	debugger;
    var phone = document.getElementById("phone");  
    var value = phone.value.trim();  
    var senURL = "../xy/sendCode.do";
    if(value && value.length == 11){ 
    	var params = {type : "phone", value : value}
    	if (headerinfo.transferEncrypt){
    		encriptPhone(senURL,params);
    	}else{
    		accessCheck(senURL, params);
    	}
    }else{  
        alert("手机号码格式错误，请与管理员联系更改！");  
    }  
}
function encriptPhone(theURL, params) {
	var data = {};
	var keyUrl="../e5workspace/security/pubkey.do";
	$.post(keyUrl, data, function(xmlDoc) {
		 if (xmlDoc){
			var children = xmlDoc.documentElement.childNodes;
			var modulus = getText(children[0]);
			var exponent = getText(children[1]);
			
			var key = RSAUtils.getKeyPair(exponent, '', modulus);
			var phone = document.getElementById("phone").value;
			if(phone && phone.length > 0 && key)
				encryptPsw = RSAUtils.encryptedString(key, encodeURIComponent(phone));
			else
				encryptPsw = phone;
	
			params["value"] = encryptPsw;
			params["encrypttools"] = "rsa";
			
			accessCheck(theURL, params);
		} else {
			alert(headerinfo[errorpubkey]);
		}
	}, "xml");
}
function accessCheck(theURL, params){
	if(params["inputCode"]){

		$.post(theURL, params, function(data) {
			var results = JSON.parse(data);
			if(results.result == "success"){
			   //验证码通过后跳转
				var userCode = e5.utils.encodeSpecialCode($("#UserCode").val());
				e5.utils.setCookie("l$code", userCode);
				
				var mainURL = "../xy/Entry.do?s=" + $("#usiteID").val();
				if (headerinfo["app"]) {
					mainURL += "&app=1";
				}
				if (e5.utils.isIE()) {
					window.location.href = mainURL;
				} else {
					window.location.replace(mainURL);
				}
			}else{
				$("#errorDiv").css("display","block");
		    	$("#checkCode").css("margin-top","4px");
		    	$("#errorDiv").html("验证码错误！");
			}
		});
	
	}else{
        $.ajax({  
            cache : false,  
            url : theURL,  
            data : params,
            success:function(data) {
				// 1分钟内禁止点击  
		        for (var i = 1; i <= 60; i++) {  
		            // 1秒后显示  
		            window.setTimeout("updateTime(" + (60 - i) + ")", i * 1000);  
    			}
    		}
        });  
	}
}
function updateTime(i){  
    // setTimeout传多个参数到function有点麻烦，只能重新获取对象  
    var obj = document.getElementById("sendCode");  
    if(i > 0){  
        obj.innerHTML  = "" + i + "秒后再次获取";  
        obj.disabled = true;  
    }else{  
        obj.innerHTML = "获取验证码";  
        obj.disabled = false;  
    }  
}
function doValidation(){  
	debugger;
	var value = document.getElementById("phone").value
    if(document.getElementById("checkNum").value != ""){  
    	var thePhoneURL = "../xy/checkCode.do";
    	var params = {
    			type : "phone",
    			value : value,
    			inputCode : document.getElementById("checkNum").value
    	}
    	if (headerinfo.transferEncrypt){
    		value = encriptPhone(thePhoneURL, params);
    	}else{
    		accessCheck(thePhoneURL, params);
    	}
    }else{
    	$("#errorDiv").css("display","block");
    	$("#checkCode").css("margin-top","4px");
    	$("#errorDiv").html("请输入验证码！");
    }  
} 
function hideError(){
	$("#errorDiv").css("display","none");
	$("#checkCode").css("margin-top","70px");
	$("#errorDiv").html("");
}

