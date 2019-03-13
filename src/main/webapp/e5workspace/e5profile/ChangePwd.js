$(function(){
	$("#submitbtn").click(doAction);
    $("#qrCodeBtn").click(getQrCode);
	$("#oldPwd").focus();
	$("#form_id").validationEngine();
	$("#newPwd,#newPwd2").addClass("validate[minSize[8]],validate[custom[strongpass]]");
});

$(document).keydown(function(event){
	if(event.keyCode == 13){
		doAction();
	}
});

function doAction() {
	if (checkFrm()){
		chpwd($("#oldPwd").attr("value"),$("#newPwd").attr("value"));
	}
}

function chpwd(oldpwd,newpwd)
{
	var params = {
		newPwd : newpwd,
		oldPwd : oldpwd
	}
	var url = "../../e5workspace/changepwd.do";

	if (transferEncrypt){
		encriptpwd(url, params);
	}else{
		changePwd(url, params);
	}
}

function checkFrm() {
	var newPwd = document.getElementById("newPwd").value;
	var newPwd2 = document.getElementById("newPwd2").value;

	if (newPwd != newPwd2) {
		alert(pwdInfo.different);
		document.getElementById("newPwd2").focus();
		return false;
	}
	if (!newPwd || !newPwd2) {
		alert(pwdInfo.newpwd);
		return false;
	}
	if (!$("#form_id").validationEngine("validate")) {
		return false;
	}
	return true;
}
var captchaEnabled;
var transferEncrypt;
	$.ajax({url:"../security/securitypolicy.do", dataType:"json",async:false, 
		success:function(data) {
		if (data){
			$.each(data,function(i,n){
				if (n.captchaEnabled) {
					captchaEnabled = true;
					$(".security_class").css("display","block");
					$("#randImg").attr("src","../e5workspace/security/captcha.do");
				}
				if (n.transferEncrypt) {
					transferEncrypt = true;
				}
			});
		}
	}});




var encryptPsw;
var encryptPswOld;
function encriptpwd(url,params) {
	var data = {};
	var keyUrl="../security/pubkey.do";
	$.post(keyUrl, data, function(xmlDoc) {
		 if (xmlDoc){
			var children = xmlDoc.documentElement.childNodes;
			var modulus = getText(children[0]);
			var exponent = getText(children[1]);
			
			var key = RSAUtils.getKeyPair(exponent, '', modulus);
			var psw = document.getElementById('newPwd').value;
			var oldPwd = document.getElementById('oldPwd').value;
			if(psw && psw.length > 0 && key){
				encryptPsw = RSAUtils.encryptedString(key, encodeURIComponent(psw));
				encryptPswOld = RSAUtils.encryptedString(key, encodeURIComponent(oldPwd));
			}else
				encryptPsw = psw;
	
			params["newPwd"] = encryptPsw;
			params["oldPwd"] = encryptPswOld;
			params["encrypttools"] = "rsa";
			changePwd(url,params);
		} 
	}, "xml");
}
function changePwd(url,params){
	$.post(url, params, function(data){
		if (data){
			console.log(data);
			alert(pwdInfo[data]);
		}
	});
}



function getText(oNode){
	return (oNode.text) ? oNode.text : oNode.textContent;
}

function getQrCode() {
    $("#qrCodeImgDiv").show();
    $("#qrCodeImg").attr("src", "../../xy/showCode.do");
}