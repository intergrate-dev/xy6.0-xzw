<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
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
		var selectedTr, bodyFrame,browser,selectedTrDoctypeID,selectedTrFormcode,selectedTrFormid;
		var needResize,sidebarList;
		$(function(){
		
			initViewList();
			init();
		});
		
	
		function init(){
			sidebarList = $("#sidebar");
			$("body").bind("resize",resizeHandle);
			needResize = 1;
			browser = $("#browser");
			bodyFrame = $("#bodyFrame");
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
		}
		function resizeHandle(){
			var bodyH = document.documentElement.scrollHeight;
			var sidebarListH = bodyH-sidebarList.offset().top;
			if(sidebarList[0].scrollHeight-10 > sidebarListH){
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
			selectedTrFormcode = src.getAttribute("formcode");
			selectedTrFormid = src.getAttribute("formname");
			selectedTrDoctypeID = src.getAttribute("doctypeid");
			
			doBrowser();
			needResize = 1;
			resizeHandle();
			 
		}
		function doBrowser(){
			if(selectedTr){
				bodyFrame.attr({
					"title":"<i18n:message key="e5dom.form.custom.preview"/>",
					"src":"DetailViewController.do?action=preView&docTypeID=" + selectedTrDoctypeID+"&formID="+selectedTr
				});
				 
				browser.removeAttr("style");
			}
		}
		function doDel(){
			if(selectedTr){
				var ok = confirm("<i18n:message key="e5dom.form.confirm.delete3"/>");
				if(ok){
					$.get("DetailViewController.do", { action: "delete", docTypeID: selectedTrDoctypeID,formID:selectedTr } ,function(){
						//document.location.reload();
						initViewList();
						selectedTr = null;
					});
				}
			}else{
				alert('<i18n:message key="e5dom.form.alert.delete"/>');
			}
		}
		function doAdd(){
			browser.attr("style","display:none");
			bodyFrame.attr("src","../e5dom/DetailViewCustom.jsp");
			// $(window).unbind("resize",resizeHandle);
			needResize = 0;
			bodyFrame.height("100%");
		}
		function doModify(){
			if(selectedTr){
				browser.attr("style","display:none");
				bodyFrame.attr("src","../e5dom/DetailViewCustom.jsp?docTypeID="+selectedTrDoctypeID+"&formID="+selectedTr);
				// $(window).unbind("resize",resizeHandle);
				needResize = 0;
				bodyFrame.height("100%");
			}
		}
		
		function initViewList(){
			var docTypeID;
			$.ajax({
				url:"DetailViewController.do?action=getviewsforjson", 
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
								for(var j=0;j < n.forms.length;j++){
									var form = n.forms[j];
									trContent.push("<tr doctypeid='"+form.docTypeID+"' id='"+form.id+"' formname='" + form.name + "' formcode='"+form.code+"' onclick='selected(this);'  >");
									if (j == 0) {
										if (n.forms.length > 1){
											trContent.push("<td id='ti0" + i + 
												"' onclick='te_show(event, " + d1+"," +n.forms.length + 
												")' rowspan='"+n.forms.length+"' class='fold'>" +
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
			height:530px;
		}
	</style>
</head>
<body>
<div class="mainBodyWrap">
	<div id="sidebar" class="sidebar-list">
		<table id="tab0" cellpadding="0" cellspacing="0" class="table">
			<caption><b class="sidebar-list-hide"></b><i18n:message key="e5dom.detailView.title"/></caption>
		</table>
		<div id="sidebar-scroll" class="sidebar-list">
			<table id="tab1" cellpadding="0" cellspacing="0" class="table">
				<tr id="formlist">
					<th width="30%"><i18n:message key="e5dom.form.docTypeName"/></th>
					<th width="10%"><i18n:message key="e5dom.form.id"/></th>
					<th width="30%"><i18n:message key="e5dom.form.name"/></th>
					<th width="30%"><i18n:message key="e5dom.form.code"/></th>
				</tr>
			</table>
		</div>
		<table id="tab2" cellpadding="0" cellspacing="0" class="table">
			<tr>
				<td colspan="4" class="alignCenter">
					<input class="button" type="button" value="<i18n:message key="e5dom.form.button.add"/>" onclick="doAdd()">
					<input class="button" type="button" value="<i18n:message key="e5dom.form.button.mod"/>" onclick="doModify()">
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
					<td width="50%"><b class="sidebar-list-show"></b><i18n:message key="e5dom.docTypeView.custom.preview"/></td>
				</tr>
			</table>
		</div>
		<iframe id="bodyFrame" name="bodyFrame" src="" frameborder="0"></iframe>
	</div>
</div>
</body>
</html>