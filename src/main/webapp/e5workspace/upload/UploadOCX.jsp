<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>OCX文件上传示例</title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<link rel="stylesheet" type="text/css" href="../../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5style/sys-main-body-style.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.query.js"></script>
 
	<script type="text/JavaScript">
	var device,dropbox,message;
	var hasFinish = false;
 
	// 上传完成后的操作
	function OnFinishTransport(fileId, localFilePathName)
	{
		 
	}

	//选择改变后
	function OnSelectChanged(fileId, localFilePathName)
	{
		
	}
	//创建文件时
	function OnCreateFile(fileId, localFilePathName)
	{
		
	}
	
	//删除文件时
	function OnDeleteFile(fileId, localFilePathName)
	{
		
	}
	
	$(function(){
		
		device = $("#sl-device");
		initDevice();
		
		
	});
	 
	function initDevice(){
		
		 $.ajax({ url: "../../e5workspace/app/StorageDevice.do?invoke=getStorageListForJson",
				async: true,
				dataType:"json",
				success: function (data) {
					if(data!=null&& data!=""){
						
						var options =[];
						$.each(data,function(i,n){
							options.push("<option value='"+n.deviceName+"'>");
							options.push(n.deviceName);
							options.push("</option>");
						});
						device.append(options.join(''));

					}
					InitialCtrls();
				}
		 });
	}
	function InitialCtrls()
	{  
		var ctrl = $("#XUploadCtrl")[0];
			if(!ctrl)
		{
  		alert("no ctrl");
		  return;
		}
			try{
		
				
			var coUrl =  getHttpUrl()+"e5workspace/OcxUpload.do?";
			coUrl+="&fileType=0";
			coUrl+="&doclibid="+$.query.get("DocLibID");
			coUrl+="&docid="+$.query.get("DocIDs");
			coUrl+="&devicename="+device.val();
			
			ctrl.CoordinatorUrl = encodeURI(coUrl); //参数每次都传输

			
			ctrl.TransporterType = 1; 
			ctrl.HttpTransporterUrl ="no Transfer By Http"; //参数每次都传输

			ctrl.FtpTransporterPort = 21;
			ctrl.FtpTransporterServer = "FtpTransporterServer";
			ctrl.FtpTransporterUserName = "FtpTransporterUserName";
			ctrl.FtpTransporterUserPwd = "FtpTransporterUserPwd";
			ctrl.TransportFileCode = ""; 

			ctrl.MaxTransportFileSize = -1;
			ctrl.TransportFileTypes = "bmp,jpg,png,gif,tif";//"<c:out value="${dfmap.AttachFormat}"/>";//"bmp,jpg,png,gif,tif"; 有错误
			ctrl.OperateMode = "1";
			ctrl.Restart();
		}
		catch(e){
			alert('请选择安装XUpload.CAB控件！');
		}
	}
	
	function getHttpUrl(){
		
		var url =[];
		var loct = document.location;
		if(loct!=null){
			url.push("http://");
			url.push(loct.host);
			url.push("/");
			var virualName = loct.pathname.split("/")[1];
			url.push(virualName);
			url.push("/");
		}
		else{
			url.push(loct.href);
		}
		return url.join('');
	}
	
	</script>
</head>

<body>
<form method="post" action="">
<table class="table">
<caption>OCX文件上传</caption>
</table>
		
<script type="text/javascript" for="XUploadCtrl" event="SelectChanged(fileId, localFilePathName)">OnSelectChanged(fileId, localFilePathName);</script>
<script type="text/javascript" for="XUploadCtrl" event="CreateFile(fileId, localFilePathName)">OnCreateFile(fileId, localFilePathName);</script>
<script type="text/javascript" for="XUploadCtrl" event="DeleteFile(fileId, localFilePathName)">OnDeleteFile(fileId, localFilePathName);</script>
<script type="text/javascript" for="XUploadCtrl" event="FinishTransport(fileId, localFilePathName)">OnFinishTransport(fileId, localFilePathName);</script>

<div>请选择存储设备：<select id="sl-device" name="deviceName"></select></div>
<div>
<object classid="clsid:1C926215-64A5-4885-87C2-A4058D4F7324" id="fileObj" CODEBASE="ocx/FileOpen.cab#Version=1,0,2,0" width="0" height="0"></object>
</div>
<div>
<object id="XUploadCtrl" classid="clsid:8FF81239-A030-4DB2-9468-69D8A1BC7E2C" codebase="ocx/XUpload.CAB#version=1,0,1,7" width="656px" height="180px;"> </object>
</div>
</form>
</body>
</html>
