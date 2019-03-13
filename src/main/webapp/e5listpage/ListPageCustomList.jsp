<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="ListPageCustomFields.title"/></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	    <script type="text/javascript" src="../e5script/e5.min.js"></script>
		<script type="text/javascript" src="../e5script/e5.utils.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
    	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
    	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			$(function () {
				
				//选择图标
				e5.dialog('', {
					title: "<i18n:message key="ListPageCustomFields.showtypeicon"/>",
					id: "iconview",
					ishide:true
				}).DOM.content.append($("#icon-list"));
				
				 $("#trpath").hide();
				 $("#btn-icon").click(function(){
					
					 selectIcon();
					 
				 });
				$("#cb-custom-xls").change(function(){
					if($(this).attr("checked") == "checked"){
						$("#trpath").show();
					}
					else{
						$("#trpath").hide();
						//$("#txt-custom-xlspath").val("");
					}
				});
				$("#listcustom").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
						 //éªè¯
							var sldocTypeO = $("#sl-doc-type");
							var docTypeID = sldocTypeO.val();
							var docTypeName = sldocTypeO.find("option:selected").text();
							
							var listID = ($("#J_ListID").val()=="")?-1:$("#J_ListID").val();
							var listName = $("#txt-listname").val();
							
							var slshowTypeO = $("#sl-show-type");
							var slshowID = slshowTypeO.val();
							var slshowName = slshowTypeO.find("option:selected").text();
							
							var hasXLS = ($("#cb-custom-xls").attr("checked")=="checked")?"1":"0";
							
							var xlsPath = $("#txt-custom-xlspath").val();
							
							var icon = $("#listicon").attr("src");
							if(icon.toString().indexOf("decied_default1.gif")>0){
								icon = "";
							}
							
							var params = {"docTypeID":docTypeID,
									"docTypeName":docTypeName,
									"listID":listID,
									"listName":listName,
									"slshowID":slshowID,
									"slshowName":slshowName,
									"icon":icon,
									"hasXLS":hasXLS,
									"xlsPath":xlsPath
							};
							var typeUrl = "";
							
							//éªè¯åç§°æ¯å¦éå¤
							$.ajax({
								url: "../e5listpage/ListSubmit.do?method=existListPage&id=" +listID+"&name="+listName+"&doctypeid="+docTypeID,
								//dataType: "json",
								async: false,
								success: function(data) {
									if(data.toString().toLowerCase() =="true"){
										alert("<i18n:message key="listPageCustom.existname"/>");
										return;
									}
									else{
										if(slshowID.toString().toLowerCase() == "table"){
											typeUrl = "ListPageCustomFields.jsp";
										}
										else if(slshowID.toString().toLowerCase() == "grid"){
											typeUrl = "GridCustomFields.jsp";
										}
										else if(slshowID.toString().toLowerCase() == "tree"){
											
										}
										if(typeUrl!=""){
											var url  = typeUrl+"?"+$.param(params);
											//alert(url);
											document.location.href = url;
										}
									}
								}
							});
							
							
							
						
					}
					}
				});
				//è®¾ç½®æé®
				$("#btn-next").on("click",function(){
					$("#listcustom").submit();
				});
				$("#btn-cancle").on("click",function(){
					//è¿åå°æ¥çççé¢
					parent.parentUpdate($.query.get("listID"));
				});
				
				//è·åææææ¡£ç±»å
				$.ajax({url:"../e5listpage/cust_typeReader.do", async:false, success:function(data) {
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
								  }
								  else{
									  options = options+ "<option value='"+d.id+"'>"+d.name+"</option>";
								  }
							});
						}
						$("#sl-doc-type").html(options);
						
						//è·åå±ç¤ºæ¹å¼
						$.ajax({url:"../e5listpage/cust_customBuilderReader.do", async:false, success:function(data) {
							if(data!=null){
								var docs = $.xml2json(data);
								var options = "";
								var datas = new Array();
								if(!$.isArray(docs.docType)){
									datas.push(docs.docType);
								}else{
									datas = docs.docType;
								}
								if(datas!=null){
									$.each( datas, function(i, d){
										  if(i==0){
											  options = options+ "<option selected='selected' value='"+d.id+"'>"+d.name+"</option>";
										  }
										  else{
											  options = options+ "<option value='"+d.id+"'>"+d.name+"</option>";
										  }
									});
								}
								$("#sl-show-type").html(options);
							}
						
							//è®¾ç½®æ´æ°
							if($.query.get("listID")!=null&&$.query.get("listID")!=""){
								$("#sl-doc-type").val($.query.get("docTypeID"));
								
								$("#sl-doc-type").attr("disabled","disabled");
								
								$("#J_ListID").val($.query.get("listID"));
								$("#txt-listname").val($.query.get("listName"));
								$("#sl-show-type").val($.query.get("slshowID"));
								$("#sl-show-type").attr("disabled","disabled");
								

								if($.query.get("icon")!=null &&$.query.get("icon").toString().length>0 ){
									 
									$("#listicon").attr("src",$.query.get("icon"));
								}
								
								
								var hasXLS = ($("#cb-custom-xls").attr("checked")=="checked")?"1":"0";
								
								if($.query.get("hasXLS")=="1"){
									$("#cb-custom-xls").attr("checked","checked");
									$("#trpath").show();
									$("#txt-custom-xlspath").val($.query.get("xlsPath"));
									
								}
								else{
									$("#trpath").hide();
								}
								
								  
								}
							//è®¾ç½®æ´æ°
						}});
						//è·åå±ç¤ºæ¹å¼
					}
				 }});
			});
			function selectIcon() {
				/// <summary>选择图标</summary>
				/// <param name="code" type="String">字段编码</param>
				//1.获取图标
				 
				$.ajax({
					url: "../e5listpage/cust_iconViewReader.do",
					dataType: "json",
					async: false,
					success: function(data) {
						if (data != null) {
							//清空内容
							$("#icon-list ul").empty();
							$.each(data, function(i, n) {
								$("#icon-list ul").append("<li><table><tr><td class='l'></td><td class='c'><img onclick=\"setIcon(this);\" name=\"" + n.ID + "\"  id=\"" + n.ID + "\" alt=\"" + n.description + "\" title=\"" + n.description + "\" src=\"../" + n.url + "\" /></td><td class='r'></td></tr></table></li>");
							});
						}
					}
				});
				//2.显示图标
				e5.dialog.show("iconview");
				return false;
			}
			function setIcon(obj) {
				/// <summary>设置图标</summary>
				/// <param name="obj" type="Object">img</param>
				var img = $("#listicon");
				img.attr("src", $(obj).attr("src"));
				img.attr("alt", $("" + $(obj).attr("name")).val());
				img.attr("title", $("" + $(obj).attr("name")).val());
				 
				//关闭窗口
				e5.dialog.hide("iconview");

			}
		</script>
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/dialog.style.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/e5-list.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
	<form name="listcustom" id="listcustom" method="post" action="">
		<div class="field-container">
			<div class="container">
				<table cellpadding="0" cellspacing="0" class="table" style="table-layout:fixed;">
					<tr>
						<td width="80">
							<span class="field-required">*</span><i18n:message key="ListPageCustomFields.doctype"/>:
						</td>
						<td>
							<select class="fl" id="sl-doc-type"></select>&nbsp;
							<span class="field-help-tootip" title="<i18n:message key="ListPageCustomFields.field.note"/>">[?]</span>
						</td>
					</tr>
					<tr>
						<td width="80">
							<span class="field-required">*</span><i18n:message key="ListPageCustomFields.listname"/>:
						</td>
						<td>
							<input id="txt-listname" class="validate[required,custom[onlyCharNumberChiness],maxSize[60]]" type="text" />
							<span class="field-help-tootip" title="<i18n:message key="inputvalidate.specialchar"/><i18n:message key="ListPageCustomFields.field.note"/>">[?]</span>
						</td>
					</tr>
					<tr>
						<td width="80">
							<span class="field-required">*</span><i18n:message key="ListPageCustomFields.showtype"/>:
						</td>
						<td>
							<select class="fl" id="sl-show-type"></select>&nbsp;
							<span class="field-help-tootip" title="<i18n:message key="ListPageCustomFields.field.note"/>">[?]</span>
						</td>
					</tr>
					<tr>
						<td width="80">
						     
							<i18n:message key="ListPageCustomFields.showtypeicon"/>:
						</td>
						<td>
							 <img src="../images/decied_default1.gif" alt="" id="listicon" />
							 <input id="btn-icon" class="button" type="button" value="<i18n:message key="ListPageCustomFields.field.selecticon"/>" />

						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input class="fl" id="cb-custom-xls" type="checkbox" /><label class="fl align-margin" for="cb-custom-xls"><i18n:message key="ListPageCustomFields.isxsl"/></label>
						</td>
					</tr>
					<tr id="trpath">
						<td colspan="2">
							<span class="fl"><i18n:message key="ListPageCustomFields.xslpath"/>:</span><input id="txt-custom-xlspath" class="fl longinput validate[maxSize[255]]" type="text" />
						</td>
					</tr>
				</table>
			</div>
			<input id="J_ListID" type="hidden" />
			<div class="btn-area">
				
				<input id="btn-cancle" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.cancle"/>" />
				<input id="btn-next" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.next"/>" />
			</div>
		</div>
		<div class="icon-list-container" id="icon-list">
				<ul class="icon_sortlist"></ul>
			</div>
		</form>
		
	</body>
</html>