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
					//if (n.dynamicCodeEnabled) {
						headerinfo.dynamicCodeEnabled = true;
						$(".dynamic_class").css("display","block");
					//}
				});
			}
		}});
	}
    preventBack();
});
function preventBack(){
    // 防止后退
    if (window.history && window.history.pushState) {
        $(window).on('popstate', function () {
            window.history.pushState('forward', null, '#');
            window.history.forward(1);
        });
    }
    window.history.pushState('forward', null, '#'); //IE
    window.history.forward(1);
}
 
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
	if (!headerinfo["dynamicCodeEnabled"]) {
		afterDynamicCode();
	} else {
		var c = document.getElementById("DynamicCode").value;
		//var c = "123456";
		var u = document.getElementById("UserCode").value;
		var checkCodeURL = "../xy/security/verifyDynamicCode.do";
		var params = {DynamicCode : c, UserCode : u};
		$.post(checkCodeURL, params, function(data){
			if (data){
				if(data == 0){
					alert(headerinfo.errordynamiccode);
				}else if (data == -1){
					alert(headerinfo.nodynamiccode);
				}else if(data == 1){
					afterDynamicCode();
				}
			}
		});
	}
}
function afterDynamicCode() {
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
		if (result.indexOf("siteID:") != 0) {
			if (headerinfo[result])
				alert(headerinfo[result]);
			else
				alert(headerinfo.otheragain + ":" + result);
		} else {
			var userCode = e5.utils.encodeSpecialCode($("#UserCode").val());
			e5.utils.setCookie("l$code", userCode);
			
			var mainURL = "../xy/Entry.do?s=" + result.substring(7);
			if (headerinfo["app"]) {
				mainURL += "&app=1";
			}
			if (e5.utils.isIE()) {
				window.location.href = mainURL;
				//var workspace = window.open(mainURL,"_blank");
				//window.opener = null;
				//window.open('','_self'); 
				//window.close();
			} else {
				window.location.replace(mainURL);
			}
		}
	});
}
function randImgFresh(){
	var myDate = new Date();
	document.getElementById("randImg").src = "../e5workspace/security/captcha.do?mydate=" + myDate.toString();
}

function getText(oNode){
	return (oNode.text) ? oNode.text : oNode.textContent;
}
