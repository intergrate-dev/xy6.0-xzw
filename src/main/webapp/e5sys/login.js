function LoginParam()
{
	this.roleID = [];
	this.roleName = [];
}
function getText(oNode)
{
	if (oNode.text)
		return oNode.text;
	else
		return oNode.textContent;
}
function getLoginParam(children)
{
	var values = new LoginParam();
	for (var i = 0; i < children.length; i++)
	{
		if (!children[i].tagName) continue;

		if (children[i].tagName == "success")       values.success = getText(children[i]);
		else if (children[i].tagName == "usercode") values.usercode = getText(children[i]);
		else if (children[i].tagName == "username") values.username = getText(children[i]);
		else if (children[i].tagName == "userid")   values.userid = getText(children[i]);
		else if (children[i].tagName == "password") values.password = getText(children[i]);
		else if (children[i].tagName == "error")    values.error = getText(children[i]);
		else if (children[i].tagName == "roles")
		{
			var roleArr = children[i].childNodes;
			if (roleArr == null) continue;
			for (var j = 0; j < roleArr.length; j++)
			{
				if (roleArr[j].tagName == "role")
				{
					for (var k = 0; k < roleArr[j].childNodes.length; k++)
					{
						if (roleArr[j].childNodes[k].tagName == "id")
							values.roleID[values.roleID.length] = getText(roleArr[j].childNodes[k]);
						else if (roleArr[j].childNodes[k].tagName == "name")
							values.roleName[values.roleName.length] = getText(roleArr[j].childNodes[k]);
					}
				}
			}
		}
	}
	return values;
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
		//var c = "1246";
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
    //doLogin();
	if (document.getElementById("Super").value == "-1")
		doAuth();
	else
		doLogin();
}

function doAuth() {
	var theURL = "sysAuth.do";
	var params = {
		UserCode: document.getElementById("UserCode").value,
		UserPassword : document.getElementById("UserPassword").value
	}
	$.post(theURL, params, doCheckResult);
}

function doLogin() {
	var theURL = "sysLogin.do";
	
	var params = {
		Super : document.getElementById("Super").value,
		UserCode: document.getElementById("UserCode").value,
		UserPassword : document.getElementById("UserPassword").value,
		UserID : document.getElementById("UserID").value,
		UserName : document.getElementById("UserName").value//,
        //RoleID: "1"
	}
	var nIndex = document.getElementById("RoleID").selectedIndex;
	if (nIndex >= 0){
		params["RoleID"] = document.getElementById("RoleID").options(nIndex).value;
	}
	
	$.post(theURL, params, doCheckLogin);
}

function doCheckLogin(xmlDoc) {
	var children = xmlDoc.documentElement.childNodes;
	var values = getLoginParam(children);

	if (values.success) {
		if (isIE()) {
			var syswin = window.open("sysMenu.do","_blank","location=no, resizable=yes,status=yes,toolbar=no,menubar=no");
			window.opener = null;
			window.open('','_self'); 
			window.close();
		}
		else {
			window.location.replace("sysMenu.do");
		}
	} else {
		var infos = values.error;
		if (infos == "self")
			alert(WarningInfos[5]);
		else if (infos = "haserror")
			alert(WarningInfos[7]);
		else
			alert(WarningInfos[6]+"["+infos+"]");
	}
}

function doCheckResult(xmlDoc) {
	if ((xmlDoc == null) || (xmlDoc.documentElement == null)) {
		alert("no xmldoc");
		return;
	}
	var children = xmlDoc.documentElement.childNodes;
	var values = getLoginParam(children);
	if ("super" == values.success) {
		document.getElementById("Super").value = "1";
		document.getElementById("UserID").value = values.userid;
		document.getElementById("UserName").value = values.username;
		doLogin();
	} else if (values.success) {
		document.getElementById("Super").value = "0";
		document.getElementById("UserID").value = values.userid;
		document.getElementById("UserName").value = values.username;
		if (values.roleID != null) {
			var nCount = values.roleID.length;
			document.getElementById("role").style.display = "block";
			var select = document.getElementById("RoleID");
			for (var i = 0; i < nCount; i++) {
				var opt = document.createElement("option");
				select.appendChild(opt);
				opt.text = values.roleName[i];
				opt.value = values.roleID[i];
			}
			if (nCount == 1) {
				document.getElementById("RoleID").selectedIndex = 0;
				doLogin();
			}
		} else {
			alert(WarningInfos[2]);
		}
	} else {
		var infos = values.error;
		if (infos == "nouser")              alert(WarningInfos[1]);
		else if (infos == "invaliduser")    alert(WarningInfos[0]);
		else if (infos == "norole")         alert(WarningInfos[2]);
		else if (infos == "noadminrole")    alert(WarningInfos[3]);
		else if (infos == "invalidpassword") alert(WarningInfos[4]);
		else if (infos == "haserror")       alert(WarningInfos[7]);
	}
}
function randImgFresh(){
	var myDate = new Date();
	document.getElementById("randImg").src = "../e5workspace/security/captcha.do?mydate=" + myDate.toString();
}
