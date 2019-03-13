<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
		<script type="text/javascript" src="../e5script/e5.min.js"></script>
		<script type="text/javascript" src="../e5script/e5.utils.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			$(function(){
				$("#btn-cancle").click(function(){
					parent.document.location.reload();
				});
				$("#btn-next").click(function(){
					
					var sldocTypeO = $("#sl-doc-type");
					var docTypeID = sldocTypeO.val();
					var docTypeName = sldocTypeO.find("option:selected").text();
					
					var params = {"docTypeID":docTypeID,
							"docTypeName":docTypeName,
							"detailID":$("#J_DetailID").val(),
							"formID":$.query.get("formID"),
							"formName":$("#txt-formname").val(),
							"formCode":$("#txt-formcode").val(),
							"cssPath":$("#txt-csspath").val(),
							"jsPath":$("#txt-jspath").val()
					};
					//验证是否重复
					
					
					$.ajax({
						url:"DetailViewController.do?action=existView&docTypeID="+$.query.get("docTypeID")+"&formCode="+$("#txt-formcode").val()+"&formID="+$.query.get("formID"),
						async:false,
						success:function(data) {
							if(data.toString()=="OK"){
								alert("该<i18n:message key="e5dom.custom.formcode"/>已近存在。")
								return false;
							}else{
								var Url = "DetailViewCustomField.jsp"+"?"+$.param(params);
								document.location.href = Url;
							}
						}
					});
					
				});
				//加载文档类型
				$.ajax({
					url:"../e5listpage/cust_typeReader.do",
					async:false,
					success:function(data) {
						if(data!=null){
							var docs = $.xml2json(data);
							var options = "";
							if(docs!=null){
								var datas = new Array();
								if(!$.isArray(docs.docType)){
									datas.push(docs.docType);
								}else{
									datas = docs.docType;
								}
								$.each( datas, function(i, d){
									if(i==0){
										options = options+ "<option selected='selected' value='"+d.id+"'>"+d.name+"</option>";
									}else{
										options = options+ "<option value='"+d.id+"'>"+d.name+"</option>";
									}
								});
							}
							$("#sl-doc-type").html(options);
							//初始化
							init();
						}
					}
				});
				//加载文档类型

				
			});
			function init(){
				if($.query.get("docTypeID")!=null&&$.query.get("docTypeID")!=""){
					 
					$("#sl-doc-type").val($.query.get("docTypeID"));
					$("#sl-doc-type").prop("disabled",true);
					
					//获取内容
					$.ajax({
						url:"DetailViewController.do?action=getview&docTypeID="+$.query.get("docTypeID")+"&formID="+$.query.get("formID"),
						async:false,
						dataType: "json",
						success:function(data) {
							if(data!=null){
	
								$("#txt-csspath").val(data.pathCSS);
								$("#txt-jspath").val(data.pathJS);
								$("#txt-formname").val(data.name);
								$("#txt-formcode").val(data.code);
							}
						}
					});

				}
			}
		</script>
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" rel="stylesheet" type="text/css" />
		<link href="css/form.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<form name="formcustom" id="formcustom" method="post" action="">
			<div class="field-container">
				<div class="container">
					<table cellpadding="0" cellspacing="0" class="table">
						<tr>
							<td width="82" style="text-align:right;"><span class="field-required">*</span><i18n:message key="e5dom.custom.doctype"/>:</td>
							<td><select class="fl" id="sl-doc-type"></select><span class="field-note fl align-margin"><i18n:message key="e5dom.custom.readonly"/></span></td>
						</tr>
						<tr>
							<td style="text-align:right;"><i18n:message key="e5dom.custom.formname"/>:</td>
							<td><input  id="txt-formname" class="validate[maxSize[255]]" type="text" value="" style="width:300px;" /></td>
						</tr>
						<tr>
							<td style="text-align:right;"><i18n:message key="e5dom.custom.formcode"/>:</td>
							<td><input  id="txt-formcode" class="validate[maxSize[255]]" type="text" value="" style="width:300px;" /></td>
						</tr>
						<tr>
							<td style="text-align:right;"><i18n:message key="e5dom.custom.csspath"/>:</td>
							<td><input  id="txt-csspath" class="validate[maxSize[255]]" type="text" value="" style="width:300px;" /></td>
						</tr>
						<tr>
							<td style="text-align:right;"><i18n:message key="e5dom.custom.jspath"/>:</td>
							<td><input id="txt-jspath" class="validate[maxSize[255]]" type="text" value="" style="width:300px;" /></td>
						</tr>
					</table>
					<input id="J_DetailID" type="hidden" />
				</div>
				<div class="btn-area">
					<input id="btn-cancle" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btncancel"/>" />
					<input id="btn-next" class="button" type="button" value="<i18n:message key="e5dom.custom.btnnext"/>" />
				</div>
			</div>
		</form>
	</body>
</html>