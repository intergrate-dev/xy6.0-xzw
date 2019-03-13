<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
		var cloneListDialog;
		$(document).ready(function(){
			var advancedOption = $(".advanced-option");
			advancedOption.hide();
			foldAdvancedOptionHandle();
			init();
			//初始化复制表单验证
			$("#cloneForm").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						$.ajax({
							  type: "POST",
							  url: "Browser.do?action=cloneForm&listid=" + $("#listID").val() + 
							  		"&listname=" + encodeURI($("#listname").val()),
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
										e5.dialog.hide('cloneListDialog');
										parent.document.location.reload();
									} else{
										alert("<i18n:message key="pagelist.form.custom.clone.failed"/>" + "," + data);
									}
									$("#btn-save").removeAttr("disabled");
									$("#btn-cancle").removeAttr("disabled");
									$("#listname").val("");
							  },
							  error: function(data){
								  //请求出错处理
								  alert("<i18n:message key="pagelist.form.custom.clone.failed"/>" + "," + data);
								  $("#btn-save").removeAttr("disabled");
								  $("#btn-cancle").removeAttr("disabled");
								  $("#listname").val("");
							  }
						});
					}
				}
			});
		});
		
		function init(){
			//初始化复制dialog
			cloneListDialog = e5.dialog("", {
				title : "<i18n:message key="pagelist.form.custom.clone.title"/>",
				id : "cloneListDialog",
				width : 450,
				height : 100,
				resizable : true,
				showClose : true,
				ishide : true 
			});
			cloneListDialog.DOM.content.append($("#cloneFormDiv"));
		}

		function doClone(){
			$("#cloneFormDiv").show();
			$("#listname").val("");
			cloneListDialog.show();
		}
		function foldAdvancedOptionHandle(){
			var foldAdvancedOptionBtn = $("#fold-advanced-option"),
				advancedOption = $(".advanced-option"),
				isFold = false,
				elmText = foldAdvancedOptionBtn.html().split(','),
				unfoldText = elmText[0] + "&nbsp;&nbsp;&nbsp;&nbsp;",
				foldText = elmText[1] + "&nbsp;&nbsp;&nbsp;&nbsp;";
			foldAdvancedOptionBtn.html(unfoldText).click(function(event){
				if(isFold){
					advancedOption.hide();
					foldAdvancedOptionBtn.removeClass().addClass("unfold").html(unfoldText);
				}else{
					advancedOption.show();
					foldAdvancedOptionBtn.removeClass().addClass("fold").html(foldText);
				}
				isFold = !isFold;
				event.preventDefault();
			});
		}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/e5-list.css"/>
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
		<style>
			html,body{
				width:auto;
				height:auto;
				overflow:auto;
			}
			.divStyle{
				height:150px;
				overflow:auto;
				border:1px solid #0078B6;
				background:#fff;
				font-size: 14px;
				line-height: 24px;
			}
			.divStyle p{
				margin:10px;
			}
			.table{
				margin-top:20px;
			}
		</style>
	</head>
	<body>
		<table border="0" cellpadding="0" cellspacing="0" class="table">
			<tr class="caption">
				<td width="50%"><b class="sidebar-list-show"></b><i18n:message key="pagelist.form.title"/></td>
				<td width="50%" align="right">
				<input class="button" type="button" value="<i18n:message key="pagelist.form.button.clone"/>" onclick="doClone()"></td>
			</tr>
			<tr>
				<td colspan="2"><i18n:message key="pagelist.form.name"/><c:out value="${item.listName}"/></td>
			</tr>
			<tr>
				<td><i18n:message key="pagelist.form.doctype"/><c:out value="${docTypeName}"/></td>
				<td><i18n:message key="pagelist.form.buildertype"/><c:out value="${builderName}"/></td>
			</tr>
			<c:if test="${Old == 1}">
			<tr>
				<td  colspan="2">
				<i18n:message key="pagelist.form.pathxsl"/>
				<c:if test="${item.pathXSL != null && item.pathXSL != 'null'}">
					<c:out value="${item.pathXSL}"/>
				</c:if>
				</td>
			</tr>
			</c:if>
			<c:if test="${Old == 0}">
			<tr>
				<td colspan="2">
				<i18n:message key="pagelist.form.pathjs"/>
				<c:if test="${item.pathJS != null && item.pathJS != 'null'}">
					<c:out value="${item.pathJS}"/>
				</c:if>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<i18n:message key="pagelist.form.pathcss"/>
				<c:if test="${item.pathCSS != null && item.pathCSS != 'null'}">
					<c:out value="${item.pathCSS}"/>
				</c:if>
				</td>
			</tr>
			</c:if>
			<tr>
				<td colspan="2" align="left">
					<a href="#" id="fold-advanced-option" class="unfold"><i18n:message key="pagelist.form.fieldadvtooltip"/></a>
				</td>
			</tr>
			<tr class="advanced-option">
				<td colspan="2"><i18n:message key="pagelist.form.xmlname"/></td>
			</tr>
			<tr class="advanced-option">
				<td colspan="2" class="wrap">
				<div class="divStyle">
					<p><c:out value="${item.listXML}"/></p>
				</div>
				</td>
			</tr>
			<tr class="advanced-option">
				<td colspan="2"><i18n:message key="pagelist.form.slice"/></td>
			</tr>
			<tr class="advanced-option">
				<td colspan="2" class="wrap">
				<div class="divStyle">
					<p><c:out value="${item.templateSlice}"/></p>
				</div>
				</td>
			</tr>
		</table>

	<div id="cloneFormDiv" style="display:none">
		<form name="cloneForm" id="cloneForm" method="post" action="">
		<input type="hidden" id="listID" name="listID" value="<c:out value="${item.listID}"/>"/>
		<table class="table">
			<tr>
				<th style="text-align:right;" width="w90" nowrap><span class="field-required">*</span><i18n:message key="pagelist.form.custom.listname"/>:</td>
				<td width="300px"><input id="listname" name="listname" class="validate[required,custom[onlyCharNumberChiness],maxSize[60]]" type="text" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input id="btn-save" class="button" type="submit" value="<i18n:message key="pagelist.form.custom.btnsave"/>" />
					<input id="btn-cancle" class="button" type="button" value="<i18n:message key="pagelist.form.custom.btncancel"/>" onclick="e5.dialog.hide('cloneListDialog');"/>
				</td>
			</tr>
		</table>
		</form>
	</div>		
	</body>
</html>
<%@include file="../e5include/Error.jsp"%>