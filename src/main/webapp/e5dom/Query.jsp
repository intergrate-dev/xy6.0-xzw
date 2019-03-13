<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
<head>
	<title></title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript">
		var selectedTr, selectedTrFormcode, bodyFrame, browser;
		var cloneFormDialog, regOperationDialog;
		var needResize,sidebarList;
		$(document).ready(function(){
			init();
			//初始化复制表单验证
			$("#cloneForm").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						var theURL = "QueryFormController.do?action=cloneForm&formID=" + selectedTr + 
							  		"&formname=" + encodeURI($("#cloneName").val()) + 
							  		"&formcode=" + $("#cloneCode").val();
						$.ajax({
							  type: "POST",
							  url: theURL,
							  dataType : "json",
							  beforeSend:function(){
								 	//设置按钮不可用，防止重复提交
								  	$("#btn-save").attr("disabled","true");
									$("#btn-cancle").attr("disabled","true");
							  },
							  success: function(data, textStatus){
								  var dataStr = new String(data);
									if(data != null && data != ""&&!(dataStr.indexOf("error:")>=0)){
										$("#cloneForm").validationEngine("hideAll");
										e5.dialog.hide('cloneFormDialog');
										initFormList();
										bodyFrame.attr("src","");
										selectedTr = null;
									} else{
										alert("<i18n:message key="e5dom.custom.form.clone.failed"/>" + "," + data);
									}
									$("#btn-save").removeAttr("disabled");
									$("#btn-cancle").removeAttr("disabled");
									$("#formname").val("");
									$("#formcode").val("");
							  },
							  error: function(data){
								  //请求出错处理
								  alert("<i18n:message key="e5dom.custom.form.clone.failed"/>" + "," + data);
								  $("#btn-save").removeAttr("disabled");
								  $("#btn-cancle").removeAttr("disabled");
								  $("#formname").val("");
								  $("#formcode").val("");
							  }
						});
					}
				}
			});
			//注册为操作初始化
			$("#regOperationForm").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						$("#saveRegOperation").attr("disabled",true);
						$("#cancelRegOperation").attr("disabled",true);
						regOperation();
					}
				}
			});
			initFormList();
		});
		function init(){
			sidebarList = $("#sidebar");
			$("body").bind("resize",resizeHandle);
			needResize = 1;
			bodyFrame = $("#bodyFrame");
			browser = $("#browser");
			$(".sidebar-list-hide").click(sidebarListHide);
			$(".sidebar-list-show").click(sidebarListShow);
			$(".browserIsHidden").click(sidebarListShow);
			function sidebarListHide(){
				sidebarList.hide();
				$(".bodyFrame").css("left","40px");
				if(browser.is(":hidden")){
					$(".browserIsHidden").show();
				}else{
					$(".sidebar-list-show").show();
				}
			}
			function sidebarListShow(){
				sidebarList.show();
				$(".bodyFrame").css("left","");
				if(browser.is(":hidden")){
					$(".browserIsHidden").hide();
				}else{
					$(".sidebar-list-show").hide();
				}
			}
			//初始化复制dialog
			cloneFormDialog = e5.dialog("", {
				title : "<i18n:message key="e5dom.custom.form.clone.title"/>",
				id : "cloneFormDialog",
				width : 450,
				height : 100,
				resizable : true,
				showClose : true,
				ishide : true 
			});
			try {
				cloneFormDialog.DOM.content.append($("#cloneFormDiv"));
			} catch (e) {}
			//注册为操作时初始化填写表单编码dialog
			regOperationDialog = e5.dialog("", {
				title : "<i18n:message key="e5dom.custom.btn.regoption.title"/>",
				id : "regOperationDialog",
				width : 500,
				height : 210,
				resizable : true,
				showClose : false
			});
			try {
				regOperationDialog.DOM.content.append($("#regOperationDiv"));
			} catch (e) {}

			//按钮操作
			$("#btnRegOperation").click(function () {
				regOperationDialog.show();
				$("#saveRegOperation").show();
				$("#cancelRegOperation").show();
				$("#dealCount1").attr("checked", true);
				$("#dealCount2").attr("checked", false);
			});
		}
		function resizeHandle(){
			var bodyH = document.documentElement.scrollHeight;
			var sidebarListH = bodyH-sidebarList.offset().top;
			if(sidebarList[0].scrollHeight > sidebarListH){
				sidebarList.css("height",sidebarListH);
				$("#sidebar-scroll").css("height",sidebarListH-70);
			}else{
				sidebarList.css("height","");
			}
			if(!needResize){return}
			var bodyFrameH = bodyH-browser.outerHeight(true);
			bodyFrame.height(bodyFrameH);
		}
		function selected(src){
			if(selectedTr){
				document.getElementById(selectedTr).style.backgroundColor='';
			}
			src.style.backgroundColor="#e8e8e8";
			selectedTr = src.getAttribute("id");
			selectedTrFormcode = src.getAttribute("name");
			doBrowser();
			// $(window).bind("resize",resizeHandle);
			needResize = 1;
			resizeHandle();
		}
		function doBrowser(){
			if(selectedTr){
				bodyFrame.attr({
					"title":"<i18n:message key="e5dom.form.custom.preview"/>",
					"src":"QueryFormPreview.do?action=preView&formID=" + selectedTr
				});
				if(selectedTrFormcode != null && selectedTrFormcode != "") {
					$("#formcodeTd").html(selectedTrFormcode + "<input type=\"hidden\" id=\"formcode\" name=\"formcode\" value=\"\" />");
					$("#formcode").val(selectedTrFormcode);
				} else {
					$("#formcodeTd").html("<input type=\"text\" id=\"formcode\" name=\"formcode\" value=\"\" class=\"validate[required]\"/>");
				}
				browser.removeAttr("style");
			}
		}
		function doAdd(){
			browser.attr("style","display:none");
			bodyFrame.attr("src","../e5dom/QueryFormCustom.jsp");
			// $(window).unbind("resize",resizeHandle);
			needResize = 0;
			bodyFrame.height("100%");
		}
		function doModNew(){
			browser.attr("style","display:none");
			if(selectedTr){
				//得到现有表单数据
				$.ajax({
					url:"QueryFormController.do?action=getForm&formID="+ selectedTr,
					dataType:"json",
					async:false,
					success:function(data) {
						if(data!=""){
							var formData = {
									"docTypeID":data.docTypeID,
									"formID":data.id,
									"formName":data.name,
									"formCode":data.code,
									"cssPath":data.pathCSS,
									"jsPath":data.pathJS
							};
							bodyFrame.attr("src","../e5dom/QueryFormCustom.jsp?"+$.param(formData));
							// $(window).unbind("resize",resizeHandle);
							needResize = 0;
							bodyFrame.height("100%");
						}
					}
				});
			}
		}
		function doDel(){
			if(selectedTr){
				var message = "";
				if(selectedTrFormcode != null && selectedTrFormcode != ""){
					message = "<i18n:message key="e5dom.form.confirm.delete2"/>";
					message = message.replace("<表单编码>", selectedTrFormcode);
				} else {
					message = "<i18n:message key="e5dom.form.confirm.delete1"/>";
				}
				var ok = confirm(message);
				if(ok){
					$.get("QueryFormController.do", { action: "delForm", formID: selectedTr } ,function(){
						//document.location.reload();
						browser.attr("style","display:none");
						bodyFrame.attr("src","");
						initFormList();
						selectedTr = null;
					});
				}
			}else{
				alert('<i18n:message key="e5dom.form.alert.delete"/>');
			}
		}
		function initFormList(){
			var docTypeID;
			$.ajax({
				url:"QueryFormController.do?action=listFormJson", 
				type:"GET",
				dataType:"json",
				async:false, 
				success:function(data) {
					if(data!=""){
						var tr = $("#formlist");
						tr.nextAll("tr").remove();

						var trContent = new Array();
						var d1 = parseInt(0);
						$.each(data,function(i,n){
							if(n.forms != null && n.forms != ""){
								for(var j=0;j<n.forms.length;j++){
									var form = n.forms[j];
									trContent.push("<tr id='"+form.id+"' name='" + form.code + "' onclick='selected(this);' >");
									if (j == 0){
										if (n.forms.length > 1) {
											trContent.push("<td id='ti0" + i + 
												"' onclick=te_show(event," + d1+"," +n.forms.length + 
												") rowspan='"+n.forms.length+"' class='fold'>" +
												"<img id='img" + d1+ "' src='../images/minus.gif'/>" +
												n.docTypeName+"</td>");
										} else {
											trContent.push("<td id='ti0" + i + 
												"' class='fold'>" +
												n.docTypeName+"</td>");
										}
									}
									trContent.push("<td>"+form.id+"</td>");
									trContent.push("<td>"+form.name+"</td>");
									trContent.push("<td>"+form.code+"</td>");
									trContent.push("</tr>");
									
			
									docTypeID = form.docTypeID;
									
								}
								d1 = d1 + parseInt(n.forms.length);
							}
						});
						tr.after(trContent.join(''));
						$("body").trigger("resize");
					}
				}
			});
		}
		function doClone(){
			if(selectedTr){
				$("#formname").val("");
				$("#formcode").val("");
				cloneFormDialog.show();
			}else{
				alert('<i18n:message key="e5dom.form.alert.clone"/>');
			}
		}

		function regOperation(){
			var bodyFrameContent = bodyFrame[0].contentWindow;
			var height = bodyFrameContent.document.getElementById("tbcontent").style.height;
			var width = bodyFrameContent.document.getElementById("tbcontent").style.width;
			if(height != null && height.length > 0 && height.indexOf("px") >= 0){
				height = height.substr(0, height.indexOf("px"));	
			}else{
				height = 0;	
			}
			if(width != null && width.length > 0 && width.indexOf("px") >= 0){
				width = width.substr(0,width.indexOf("px"));	
			}else{
				width = 0;	
			}
			//alert(height + "," + width);
			var dealCount = 0;
			var dealCounts = document.getElementsByName("dealCount");
			for(var i=0;i<dealCounts.length;i++){
				if(dealCounts[i].checked)
					dealCount = dealCounts[i].value;
			}
			$.ajax({
				  type: "POST",
				  url: "QueryFormController.do?action=registOperate&formID=" + selectedTr + 
				  	"&formcode=" + encodeURI($("#formcode").val()) + 
				  	"&operationname=" + encodeURI($("#operationname").val()) +
				  	"&dealCount=" + dealCount + 
				  	"&height=" + height + 
				  	"&width=" + width ,
				  dataType : "json",
				  success: function(data, textStatus){
					  var dataStr = new String(data);
					  if(data != null && data != ""&&!(dataStr.indexOf("error:")>=0)){
						  e5.dialog.hide('regOperationDialog');
						  $("#regOperationForm").validationEngine("hideAll");
						  initFormList();
					  } else {
						  alert("<i18n:message key="e5dom.custom.btn.regoption.failed"/>"  +"," + data);
					  }
					  $("#saveRegOperation").attr("disabled",false);
					  $("#cancelRegOperation").attr("disabled",false);
				  },
				  error: function(data){
					  //请求出错处理
					  alert("<i18n:message key="e5dom.custom.btn.regoption.failed"/>" +"," + data);
					  $("#saveRegOperation").attr("disabled",false);
					  $("#cancelRegOperation").attr("disabled",false);
				  }
			});
		}
		function doCancel(){
			e5.dialog.hide('regOperationDialog');
			$("#regOperationForm").validationEngine("hideAll");
		}
		function exportJsp(){
			bodyFrame.attr(
					"src",
					"QueryFormController.do?action=exportToJsp&formID=" + selectedTr);
		}

		function te_show(e, trnum, length)
		{
			e = e || event;
			e.cancelBubble = true;
			
			if(length>1){
				var img=document.getElementById("img"+trnum);
				var imgSrc=img.src;
				var imgName = imgSrc.substr((imgSrc.lastIndexOf("/")+1),imgSrc.length);
				$.each($("#tab1 tr"), function(i){     
				     if(i > (trnum+1)&&i<=(trnum+length)){        
				           if(this.style.display == 'none'){
				        	   this.style.display="";
					       	}else{
					       		this.style.display="none";
					       	}
				      }   
				});
				if(imgName == 'plus.gif'){
					img.src="../images/minus.gif";
					img.parentNode.setAttribute("rowspan",length);
				}else{
					img.src="../images/plus.gif";
					img.parentNode.setAttribute("rowspan",1);
				}
			}	
		}
		
	</script>
	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	<link type="text/css" rel="stylesheet" href="css/form.css"/>
	<style>
		.table td.fold{
			background:#F5F5F5;
			border-right:1px solid #e8e8e8;
			vertical-align:top;
		}
		#sidebar-scroll{
			height:520px;
		}
	</style>
</head>
<body>
<div class="mainBodyWrap">
	<div id="sidebar" class="sidebar-list">
		<table id="tab0" cellpadding="0" cellspacing="0" class="table">
			<caption><b class="sidebar-list-hide"></b><i18n:message key="e5dom.search.tabletitle"/></caption>
		</table>
		<div id="sidebar-scroll" class="sidebar-list">
			<table id="tab1" cellpadding="0" cellspacing="0" class="table">
				<tr id="formlist">
					<th width="30%"><i18n:message key="e5dom.form.docTypeName"/></th>
					<th width="10%"><i18n:message key="e5dom.form.id"/></th>
					<th width="30%"><i18n:message key="e5dom.search.title"/></th>
					<th width="30%"><i18n:message key="e5dom.form.code"/></th>
				</tr>
			</table>
		</div>
		<table id="tab2" cellpadding="0" cellspacing="0" class="table">
			<tr>
				<td colspan="4" class="alignCenter">
					<input class="button" type="button" value="<i18n:message key="e5dom.form.button.add"/>" onclick="doAdd()">
					<input class="button" type="button" value="<i18n:message key="e5dom.form.button.mod"/>" onclick="doModNew()">
					<input class="button" type="button" value="<i18n:message key="e5dom.form.button.del"/>" onclick="doDel()">
				</td>
			</tr>
		</table>
	</div>
	<b class="browserIsHidden"></b>
	<div class="bodyFrame">
		<div id="browser" class="caption" style="display:none">
		<table width="100%">
			<tr>
				<td width="50%"><b class="sidebar-list-show"></b><i18n:message key="e5dom.search.tabletitle"/></td>
				<td width="50%" align="right">
				<input class="button" type="button" value="<i18n:message key="e5dom.form.button.clone"/>" onclick="doClone()">
				<input id="btnRegOperation" style="display:none;" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btnregistoper"/>" />
				<input id="btnExportJsp" style="display:none;" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btnexportjsp"/>" onclick="exportJsp();"/>
				</td>
			</tr>
		</table>
		</div>
		<iframe id="bodyFrame" name="bodyFrame" src="" frameborder="0"></iframe>
	</div>
	<div id="cloneFormDiv">
		<form name="cloneForm" id="cloneForm" method="post" action="">
		<table class="table">
			<tr>
				<th style="text-align:right;" width="w90"><span class="field-required">*</span><i18n:message key="e5dom.form.custom.formname"/>:</td>
				<td width="300px"><input id="cloneName" name="cloneName" class="validate[required,custom[onlyCharNumberChiness],maxSize[60]]" type="text" /></td>
			</tr>
			<tr>
				<th style="text-align:right;" width="w90"><span class="field-required">*</span><i18n:message key="e5dom.form.custom.formcode"/>:</td>
				<td width="300px"><input id="cloneCode" name="cloneCode" class="validate[required,custom[onlyCharNumberChiness],maxSize[60]]" type="text" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input id="btn-save" class="button" type="submit" value="<i18n:message key="e5dom.form.custom.btnsave"/>" />
					<input id="btn-cancle" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btncancel"/>" onclick="e5.dialog.hide('cloneFormDialog');"/>
				</td>
			</tr>
		</table>
		</form>
	</div>
	<div style="display:none">
			<div id="regOperationDiv">
				<form id="regOperationForm" name="regOperationForm" action="">
					<table cellpadding="0" cellspacing="0" class="table">
						<tr>
							<td class="w90"><span style="color:red">*</span><i18n:message key="e5dom.custom.form.formcode"/>：</td>
							<td id="formcodeTd"></td>
						</tr>
						<!--
						<tr>
							<td></td>
							<td><span style="color:gray;font-style:italic"><i18n:message key="e5dom.custom.form.formcode.title"/></span></td>
						</tr>
						-->
						<tr>
							<td class="w90"><span style="color:red">*</span><i18n:message key="e5dom.custom.form.operationname"/>：</td>
							<td><input type="text" id="operationname" name="operationname" value="" class="validate[required]"/></td>
						</tr>
						<tr>
							<td colspan="2"><span style="color:red">*</span><i18n:message key="e5dom.custom.form.operation"/>：</td>
						</tr>
						<tr>
							<td colspan="2">
							<label>
							<input type="radio" id="dealCount1" name="dealCount" value="0" checked/><i18n:message key="e5dom.custom.form.operation.new"/>
							<span style="color:gray;font-style:italic"><i18n:message key="e5dom.custom.form.operation.new.info"/></span>
							</label>
							</td>
						</tr>
						<tr>
							<td colspan="2">
							<label>
    						<input type="radio" id="dealCount2" name="dealCount" value="1" /><i18n:message key="e5dom.custom.form.operation.modify"/>
							<span style="color:gray;font-style:italic"><i18n:message key="e5dom.custom.form.operation.modify.info"/></span>
							</label>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input class="button" type="submit" id="saveRegOperation" name="saveRegOperation" value="<i18n:message key="e5dom.custom.btn.confirm"/>"/>
								<input class="button" type="button" id="cancelRegOperation" name="cancelRegOperation" value="<i18n:message key="e5dom.custom.btn.cancel"/>" onclick="doCancel();"/>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
</div>
</body>
</html>