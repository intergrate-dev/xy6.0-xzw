<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>自动登录</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <script type="text/javascript" src="./e5script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="./e5script/e5.min.js"></script>
    <script type="text/javascript" src="./e5script/e5.utils.js"></script>
    <script type="text/javascript" src="./e5workspace/script/security.js"></script>
<script type="text/javascript">
       
    </script>
<script type="text/javascript">
 var headerinfo = {
            nouser : "<i18n:message key="workspace.login.nouser"/>",
            norole : "<i18n:message key="workspace.login.norole"/>",
            expired:"<i18n:message key="workspace.login.expired"/>",
            otheragain : "<i18n:message key="workspace.login.otheragain"/>",
            haserror : "<i18n:message key="workspace.login.haserror"/>",
            frozen : "<i18n:message key="workspace.login.hasfrozen"/>",
            sessionlose : "<i18n:message key="workspace.login.nossioncaptcha"/>",
            errorcaptcha : "<i18n:message key="workspace.login.errorcaptcha"/>",
            errorpubkey : "<i18n:message key="workspace.login.pwdencrypterror"/>",
            passwordwrong : "<i18n:message key="workspace.login.nouser"/>"

        }
var token='<%=StringUtils.defaultString(request.getParameter("token"))%>';
if(token==null||token==""){
    mainURL = "./e5workspace/Login.jsp";
    window.location.replace(mainURL);
}
doAutoLogin(token);

function doAutoLogin(token){

	  $.ajax({url:"./tokenHelp/getUserInfo.do",
		data : {
			token : token
		},
		dataType:'json',
	    success:function(data) {
	    	if(data.code=='1'){

	    		accessSuccess(data.result,data.userCode,data.token);
	    	}else{
	    		alert("token过期！");
	    		mainURL = "./e5workspace/Login.jsp";
	    		window.location.replace(mainURL);
	    	}
	    }
      });
}
function accessSuccess(result,userCode,token){

        if (result.indexOf("siteID:") == 0) {
            var userCode = e5.utils.encodeSpecialCode(userCode);
            e5.utils.setCookie("l$Code", userCode);
            var mainURL = "./xy/Entry.do?s=" + result.substring(7);

            if (e5.utils.isIE()) {
                window.location.href = mainURL;
                var workspace = window.open(mainURL,"_blank");
                window.opener = null;
                window.open('','_self'); 
                window.close();
            } else {
                window.location.replace(mainURL);
            }
        }else{
         alert(headerinfo[result]);
                mainURL = "./e5workspace/Login.jsp";
                window.location.replace(mainURL);
        }
    //});
}
function parseXML(xmlDoc) {
	var result = {
		success : "",
		error : ""
	};
	if (!xmlDoc || !xmlDoc.documentElement)
		return result;
	var children = xmlDoc.documentElement.childNodes;
	result[children[0].tagName] = getText(children[0]);
	return result;
}
function getText(oNode){
	return (oNode.text) ? oNode.text : oNode.textContent;
}
</script>

</head>
<body>
</body>
</html>
