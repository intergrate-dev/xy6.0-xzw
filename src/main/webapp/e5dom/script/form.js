
/// <reference path="../../../../../projects/e5ui/e5ui/e5ui/scripts/jquery-1.7.1-vsdoc.js" />
/// <reference path="../../../../../projects/e5ui/e5ui/e5ui/scripts/jquery-ui-1.8.18.js" />
//字段元素标签
var eletag = "span";
var label_suffix = "";
var sf_aftertext = "<span class=\"custform-aftertxt\"></span>";
var tb_ul ;
;$(function () {
	//判断注册为操作的按钮是否显示
	tb_ul = $("#tablehide-ul");

	var formID = $.query.get("formID");
	if(formID==0){
		$("#btnPreview").hide();
		$("#btnRegOperation").hide();
	}else{
		$("#btnPreview").show();
		$("#btnRegOperation").show();
	}
	var formCode = $.query.get("formCode");
	if(formCode != null && formCode != "") {
		$("#formcodeTd").html(formCode + "<input type=\"hidden\" id=\"formcode\" name=\"formcode\" value=\"\" />");
		$("#formcode").val(formCode);
	} else {
		$("#formcodeTd").html("<input type=\"text\" id=\"formcode\" name=\"formcode\" value=\"\" class=\"validate[required]\"/>");
	}
	//注册为操作时初始化填写表单编码dialog
	var regOperationDialog;
	regOperationDialog = e5.dialog("", {
		title : i18n.regoption_title,
		id : "regOperationDialog",
		width : 450,
		height : 200,
		resizable : true,
		showClose : false
	});
	regOperationDialog.DOM.content.append($("#regOperationDiv"));
	
	//初始化表格
	initTable($("#tbcontent"));
	
	//setTDDropable();
	//按钮操作
	$("#btnRegOperation").click(function () {
		regOperationDialog.show();
		$("#saveRegOperation").attr("disabled",false);
		$("#cancelRegOperation").attr("disabled",false);
		$("#dealCount1").attr("checked", true);
		$("#dealCount2").attr("checked", false);
	});
	$("#btnSave").click(function () {
		saveXml();
	});
	$("#btnCancel").click(function () {
		parent.document.location.reload();
	});
	$("#btnExportJsp").click(function () {
		exportJsp();
	});
	$("#btnPreview").click(function(){
		previewForm();
	});
	$("#btnpre").click(function(){
		
		var params = {"docTypeID":$.query.get("docTypeID"),
				"docTypeName":$.query.get("docTypeName"),
				"formID":$.query.get("formID"),
				"formName":$.query.get("formName"),
				"formCode":$.query.get("formCode"),
				"cssPath":$.query.get("cssPath"),
				"jsPath":$.query.get("jsPath")
		},
		url = "FormCustom.jsp?" + $.param(params);
		document.location.href = url;
	});
	//按钮操作

	getDoctTypefields();

	//设置字段可以拖动
	setfieldDragable();

    $("#listCust li").draggable({
        helper: "clone",
        cursor: "move"
    });
    
    $("#txtFormName").html($.query.get("formName"));
    $("#txtFormCode").html($.query.get("formCode"));
    //设置字段可以拖动
    if($.query.get("formID")!=""){
        //更新
        if ($.query.get("formCode") != "") {
            //$("#btnRegOperation").attr("disabled", true);
        }
        
    }

	$("#tableContainer").width($("#tbcontent").width());
	$("#tableContainer").height($("#tbcontent").height());
	tableContainerResize();

	$("#regOperationForm").validationEngine({
		autoPositionUpdate:true,
		onValidationComplete:function(from,r){
			if(r){
				window.onbeforeunload=null;
				$("#saveRegOperation").attr("disabled",false);
				$("#cancelRegOperation").attr("disabled",false);
				regOperation();
			}
		}
	});
	$(document.body).bind("resize",resizeHandle);
	resizeHandle();

	//$("#tbcontent").colResizable();
	setColResize(true);
	setTablehideDropable();
});

function setColResize(isinit) {
    //设置第一行的列宽度
    var tb = $("#tbcontent");
    var firstrow = tb.find("tr:first");
   
    var _tb_width = tb.width();
   
    if (!isinit) {
        //如果不是初始化就平分列宽
        var colWidth = (tb.width() / firstrow.find("td").length);
        $.each(firstrow.find("td"), function (i, n) {
            $(this).width(colWidth);
            $(this).css("width", colWidth);
        });
    }
    tb.colResizable({ disable: true });
    tb.colResizable();
    tableContainerResize();

}

function synchronizationTableResize(eventType){
	/// <summary>同步表格与container高度和宽度</summary>
	
	 var tb = $("#tbcontent");
	 var cb = $("#tableContainer");
	 
     if(parseInt(tb.height(),10)!=parseInt(cb.height(),10)){
    	 cb.height(tb.height());
    	 
     }
     if(parseInt(tb.width(),10)!=parseInt(cb.width(),10)){
   	  	cb.width(tb.width());
     }
     setTDDropable();
    
     //如果为列的变化才进行修改拖动列宽操作
     if (eventType != null && eventType.toString().toLowerCase() == "col") {
        
         setColResize(false);
     }
    
     

}
function tableContainerResize(){
	/// <summary>设置表格大小</summary>
	
	var oldTableHeight = 0;
	$("#tableContainer").resizable({
        handles: "se",
        resize: function (event, ui) {

        },
        stop: function (event, ui) {
            //设置容器宽度
          var tb = $("#tbcontent");
          if(parseInt(tb.height(),10)>parseInt($(this).height(),10)){
        	  $(this).height(tb.height());
          }
          if(parseInt(tb.width(),10)>parseInt($(this).width(),10)){
        	  $(this).width(tb.width());
          }
        },
        zIndex:99
    });
	
  
}
function previewForm(){
	/// <summary>预览表单</summary>
	if($.query.get("formID")!=""){
		
		var winWidth = $(".header").width() - 20;
		 e5.dialog({
		        type: 'iframe',
		        value: 'form/Preview.do?action=preView&formID=' + $.query.get("formID")
		        },
		        { title: i18n.preview, id: "formpreview", width: winWidth, height: 500,showClose: true }
		        ).show();
	}
}

function setfieldDragable() {
	/// <summary>Description</summary>
	$("#listfield li").draggable({
		helper: "clone",
		cursor: "move"
	});
}

function getDoctTypefields() {
	/// <summary>获取文档字段</summary>

	//获取文档类型字段
	$.ajax({
		url: "../e5listpage/cust_typeReader.do?docType=" + $.query.get("docTypeID"),
		async: false,
		success: function (data) {
			if (data != null) {
				var docs = $.xml2json(data);

				var lifiles = "";
			   
				if (docs != null) {
					var datas = [];
					if (!$.isArray(docs.field)) {
						datas.push(docs.field);
					} else {
						datas = docs.field;
					}
					$("#txt-field-search").data("fields", datas);
					$.each(datas, function (i, f) {
						// <li fieldtype="0" fieldname="名称" fieldcode="sys_name">名称</li>
						lifiles = lifiles + "<li class='drop-class' datatype='"+f.datatype+"' fieldtype='" + f.fieldtype + "' fieldcode='" + f.code + "' fieldname='" + f.name + "'>" + f.name + "</li>";
					});
					//自动完成开始
					$("#txt-field-search").autocomplete(datas, {
						minChars: 0,
						width: 1,
						matchContains: true,
						autoFill: false,
						formatItem: function (row, i, max) {
							//隐藏下拉框
							var dd = $(".ac_results");

							dd.remove();
							if ($("#txt-field-search").val() == "") {
								$("#listfield").html("");
								$.each(datas, function (i, n) {

									$("#listfield").append("<li class='drop-class' datatype='"+n.datatype+"'  fieldtype='" + n.fieldtype + "' fieldcode='" + n.code + "' fieldname='" + n.name + "'>" + n.name + "</li>");
								});
								//设置字段可以拖动
								setfieldDragable();
								return false;
							}
							if (max > 0) {
								//清除所有的字段
								if (i == 1) {
									$("#listfield").html("");
								}

								$("#listfield").append("<li class='drop-class' datatype='"+row.datatype+"' fieldtype='" + row.fieldtype + "' fieldcode='" + row.code + "' fieldname='" + row.name + "'>" + row.name + "</li>");
								//设置字段可以拖动
								setfieldDragable();
							}

							return false;

						},
						formatMatch: function (row, i, max) {

							return row.name + " " + row.code;
						},
						formatResult: function (row) {
							//return row.code;
						}

					});
					//自动完成结束
				}

				$("#listfield").html(lifiles);
				//禁止选中
				$("#listfield").disableSelection();
				//设置指针为移动
				$("#listfield").css("cursor", "move");

			   if($.query.get("formID") == ""||$.query.get("formID")==null){
				   return;
			   }
			  //开始加载已有数据
				$.ajax({
					url: "FormController.do?action=getForm&formID=" + $.query.get("formID"),
					dataType: "json",
					async: false,
					success: function (data) {
						tableContainerResize();
						if (data != null) {

							var tb = $("#tbcontent");
							
							if(data.formSlice!=null){

								var tableContent = data.formSlice.toString().replace("<custom><![CDATA[","").replace("]]></custom>","").replace("</custom>","");
								
								var indexhidediv = tableContent.indexOf("<div id='hiddenDiv'>");
								if(indexhidediv>0){
									tb.html(tableContent.substring(0,indexhidediv));
									$("#tablehide-ul").remove();
									$("#hiddenDiv").remove();

									//$("#tablehide").html(tableContent.substring(indexhidediv,tableContent.length));
									$("#tablehide").append(tableContent.substring(indexhidediv,tableContent.length));
									tb_ul = $("#tablehide-ul");
									
								}
								else{
									tb.html(tableContent);
								}
								setTablehideDropable();
								sethideDivDblClick();
								//设置初始高度
								if(tb.find("tbody").length>0){
									 
									$("#tableContainer").width(tb.find("tbody").attr("customwidth"));
									$("#tableContainer").height(tb.find("tbody").attr("customheight"));
								}
								//设置表格事件
								 //初始化表格
								initTable($("#tbcontent"));
								
								//1.设置每个td可以dropable
								//setTDDropable();
								
								//2.设置span可以dragable
								tb.find("td").each(function(i,n){
									
									var span = $(this).find(".custom-field");
									if(span.length>0){
										span.draggable({ helper: "clone",cursor: "move"});
										//4.双击事件
										span.dblclick(function () {
											fieldlDbClick($(this));
										});
										//3.设置span中的input、select、textarea可以resizeable
										//清除样式
										//clearResizeUI(span);
										if(span.attr("fieldtype")!=null&&span.attr("fieldtype")!="-4"){
											setSelectInputResize(span);
										}
										
										
									}
								});
								//3.设置宽度和高度
							}
							
						}
						
					}
				});
				//开始加载已有数据结束
			}
		}
	});
}


function initTable(tb) {
	/// <summary>设置单元格可以拖放</summary>
	/// <param name="tb" type="jq">table jquery对象</param>
	if (tb == null) {
		return;
	}
	tb.tablehandle({

		tablehandle_addrow_up: i18n.tablehandle_addrow_up,
		tablehandle_addrow_down: i18n.tablehandle_addrow_down,
		tablehandle_addcol_left: i18n.tablehandle_addcol_left,
		tablehandle_addcol_right: i18n.tablehandle_addcol_right,
		tablehandle_delrow: i18n.tablehandle_delrow,
		tablehandle_delcol: i18n.tablehandle_delcol,
		tablehandle_mergcell: i18n.tablehandle_mergcell,
		tablehandle_splitrow: i18n.tablehandle_splitrow,
		tablehandle_splitcol: i18n.tablehandle_splitcol,
		tablehandle_delcell:i18n.tablehandle_delcell,
		tablehandle_mergdown:i18n.tablehandle_mergdown,
		tablehandle_mergright:i18n.tablehandle_mergright,
		tablehandle_formatimg: "",
		tablehandle_removeimgformat: "",
		tablehandle_recover: "",
		tablehandle_removefield:i18n.tablehandle_removefield,

		tablehandle_merg_tiptext: i18n.tablehandle_merg_tiptext,
		tablehandle_celldelall_tiptext: i18n.tablehandle_celldelall_tiptext,
		tablehandle_selectcellone_tiptext: i18n.tablehandle_selectcellone_tiptext,
		tablehandle_dontdelonlycol: i18n.tablehandle_dontdelonlycol,
		tablehandle_dontdelonlyrow: i18n.tablehandle_dontdelonlyrow,
	  
		canselect:false,
		tableMenuContainer:$("#tablecontent"),
		//event
		cellUpdateEvent: setTDDropable,
		tableChangedEvent:synchronizationTableResize,

		id: ""
	});
}

function setTablehideDropable(){
	/// <summary>设置隐藏区域可以拖放</summary>
	if(tb_ul.length==0){
		return;
	}
	tb_ul.droppable({
		accept: ".drop-class",
		activeClass: "ui-state-hover",
		hoverClass: "ui-state-active",
		over: function (event, ui) {

		},
		drop: function (event, ui) {
			//是否已经有字段
			if(ui.draggable.closest("ul[id='listfield']").length==1){
				if(fieldExist(ui)){
					return;
				}
			}
			else{
				return;
			}
			

			//
			dropablefieldtohide($(this), event, ui);
			
		}

	});
	
}

function fieldExist(ui){
	/// <summary>判断字段是否存在</summary>
	var rlt = false;
	var tb = $("#tbcontent");
	//判断是否从左侧列表拖动的字段
	var lul = ui.draggable.closest("ul[id='listfield']");
	if(lul.length == 0){
		return rlt;
	}
	//从表格里面找
	if (tb.find(eletag).length > 0) {
		//如果是当前字段就返回
		if(tb.find(eletag+"[fieldcode='"+ui.draggable.attr("fieldcode")+"']").length>0){
			rlt = true;
		}
	}
	//从隐藏区域找
	if(!rlt){
		if(tb_ul.find(eletag+"[fieldcode='"+ui.draggable.attr("fieldcode")+"']").length>0){
			rlt = true;
		}
	}

	return rlt;
}
function setTDDropable() {
	/// <summary>设置</summary>
	var tb = $("#tbcontent");
	tb.find("td").droppable({
		accept: ".drop-class",
		activeClass: "ui-state-hover",
		hoverClass: "ui-state-active",
		over: function (event, ui) {

		},
		drop: function (event, ui) {
			//是否已经有字段
			 
			if(ui.draggable.closest("ul[id='listfield'],ul[id='listCust'],td").length>0){
				if(fieldExist(ui)){
					return;
				}
			}
			else{
				return;
			}
			if (ui.draggable.is("li")) {
			   
				var fileType = ui.draggable.attr("fieldtype");
				//大于-4的只能拖动一次
				if( parseInt(fileType,10)>-4){
					 //判断是否已经存在该字段
					var span = $("#tbcontent").find("span[fieldcode='" + ui.draggable.attr("fieldcode") + "']");
					if (span.length==0) {
						dropablefield($(this), event, ui);
					}
				}
				else{
					dropablefield($(this), event, ui);
				}
				
			}
			if (ui.draggable.is("span")) {
				var td = ui.draggable.closest("td");
				$(this).prepend(ui.draggable);
				td.find(eletag).remove();
			}
			//同步表格与container大小
			synchronizationTableResize();
			
		}

	});
	

}

function saveXml() {
	/// <summary>实现保存功能</summary>

	//2.组装xml数据
	var sb = [];

	sb.push("<?xml version='1.0' encoding='UTF-8'?>");
	sb.push("<formcustom>");

	sb.push("<doctypeid>" + $.query.get("docTypeID") + "</doctypeid>");
	sb.push("<formid>" + $.query.get("formID") + "</formid>");
	sb.push("<formname>" + $.query.get("formName") + "</formname>");
	sb.push("<formcode>" + $.query.get("formCode") + "</formcode>");
	sb.push("<csspath><![CDATA[" + $.query.get("cssPath") + "]]></csspath>");
	sb.push("<jspath><![CDATA[" + $.query.get("jsPath") + "]]></jspath>");

	//获取字段开始
	var tb = $("#tbcontent");
	sb.push("<root>");

	sb.push("<fields>");
	tb.find(eletag).each(function (i, n) {

		if($(this).attr("fieldtype")!=null&&parseInt($(this).attr("fieldtype"),10)>-1){
			sb.push("<field>" + $(this).attr("fieldcode") + "</field>");
		}
		

	});
	tb_ul.find(eletag).each(function (i, n) {

		if($(this).attr("fieldtype")!=null&&parseInt($(this).attr("fieldtype"),10)>-1){
			sb.push("<field>" + $(this).attr("fieldcode") + "</field>");
		}
	});
	sb.push("</fields>");
	sb.push("</root>");
	//获取字段结束

	//保存table内容开始
	sb.push("<formslice>");
	sb.push("<custom><![CDATA[");
	
    //清理resize脚本
	if(tb.find(eletag).length>0){
		
		tb.find(eletag).each(function(i,n){
			
			destroyResizeable($(this));
		});
	}
	
	//设置表格高度和宽度
	var tbody = tb.find("tbody");
	if(tbody.length>0){
		tbody.attr("customwidth",$("#tableContainer").width());
		tbody.attr("customheight",$("#tableContainer").height());
	}
	else{
		tempContent.push("<tbody customwidth='"+$("#tableContainer").width()+"' customheight='"+$("#tableContainer").height()+"'>")
	}
	//处理input没有结束标签的问题
	var tempContent = "";

	//格式化input
	tempContent = appendUnClosedTag(tb.html());
	
	//格式化自有属性
	tempContent = formatSelfAttrTag(tempContent);
	
	sb.push(tempContent);
	
	if(tbody.length==0){
		sb.push("</tbody>")
	}
	
	var temphidecontent = "";
	temphidecontent = appendUnClosedTag($("#hiddenDiv").html());
	//格式化自有属性
	temphidecontent = formatSelfAttrTag(temphidecontent);
	sb.push("<div id='hiddenDiv'>"+temphidecontent+"</div>");
	sb.push("]]></custom>");

	sb.push("</formslice>");
	//保存table内容结束
	sb.push("</formcustom>");

	//alert(sb.join(''));
	//return;
	//保持数据
	var dataxml = {
			formxml:sb.join('')
	}
	//验证xml格式是否正确
	
	var xmldata = {
		xmlcontent:tempContent
	};
	$.ajax({
		url: "../e5listpage/ListSubmit.do?method=isXml",
		type: "POST",
		async: false,
		data:xmldata,
		success: function(data) {

			if(data.toString().toLowerCase() =="true"){
				
				//保存数据
				$.ajax({
					  type: "POST",
					  url: "FormController.do?action=updateForm&formID="+$.query.get("formID"),
					  data:dataxml,
					  dataType : "json",
					  beforeSend:function(){
						  //设置按钮不可用，防止重复提交
						  $("#btnSave").attr("disabled",true);
					  },

					  success: function(data, textStatus){
						  $("#btnSave").attr("disabled",false);
							if(data!= null && data!= ""){
								var datas = new Array();
								if (!$.isArray(data)) {
									datas.push(data);
								} else {
									datas = data;
								}
								if(datas!=null&&datas.length>0){
									var form = datas[0];
									alert(i18n.operation_success);
									var params = {"docTypeID":form.docTypeID,
											"docTypeName":form.docTypeName,
											"formID":form.id,
											"formName":form.name,
											"formCode":form.code,
											"cssPath":form.pathCSS,
											"jsPath":form.pathJS
									};
									var Url = "FormCustomField.jsp?"+$.param(params);
									document.location.href = Url;
									parent.initFormList();
								}
							}
							else{
								alert(i18n.operation_fail);
								parent.initFormList();
							}
					  },
					  error: function(){
							//请求出错处理
						  $("#btnSave").attr("disabled",false);
						  alert(i18n.operation_fail);
						  parent.initFormList();
					  }

					});
				//保存数据
			}
			else{
				alert(i18n.xmlerror);
			}
			
		}
	});
	
	
}

function appendUnClosedTag(srchtml){
	/// <summary>格式化框没有闭合的情况</summary>
	/// <param name="srchtml" type="html">srchtml</param>
	var re1 = new RegExp(/<input[^>]*>/gim); //
	var re2 = new RegExp(/<img[^>]*>/gim); //
	var re3 = new RegExp(/<br[^>]*>/gim); //
	var re4 = new RegExp(/<hr[^>]*>/gim); //
	
    var tempContent=srchtml;
	
    tempContent = htmlReplaceAll(tempContent,re1);
    tempContent = htmlReplaceAll(tempContent,re2);
    tempContent = htmlReplaceAll(tempContent,re3);
    tempContent = htmlReplaceAll(tempContent,re4);
	
	
	return tempContent;
}

function htmlReplaceAll(srchtml,patten){
	/// <summary>格式化各种没有封闭标签的情况</summary>
	/// <param name="srchtml" type="html">srchtml</param>
	
	 var tempContent=srchtml;
	 
	tempContent = tempContent.replace(patten, function (element, index) {

		var newInput = element;
		var closeIndex = element.toString().indexOf("/>");
		var closeIndex2 = element.toString().indexOf(">");
		if (closeIndex < 0 && closeIndex2 > 0) {

			newInput = element.substring(0, closeIndex2) + "  />"+element.substring(closeIndex2+1,element.length);

		}
		return newInput;
	});
	return tempContent;
}
function convertTagcode2Html(str) {
    var relt = new RegExp("&lt;", "gim");
    var regt = new RegExp("&gt;", "gim");
    var tempContent = str;
    tempContent = tempContent.replace(relt, function (element, index) {
        
        return "<";
    });
    tempContent = tempContent.replace(regt, function (element, index) {

        return ">";
    });
    return tempContent;
}
function formatSelfAttrTag(srchtml){
	/// <summary>格式化input框没有闭合的情况</summary>
	/// <param name="srchtml" type="html">srchtml</param>
	 var tempContent="";
	 
	 tempContent = srchtml;
	 
 	var re2 = new RegExp(/<OPTION selected>/igm);
	//re2.ignoreCase = true;
	
	tempContent = tempContent.replace(re2, function (element, index) {
		var strRet = element.replace("selected","selected='selected'");
		return strRet;
	});
	var re3 = new RegExp(/\sreadOnly\s/g);
	//re3.ignoreCase = true;
	tempContent = tempContent.replace(re3, function (element, index) {
		var strRet = element.replace(element," readonly='readonly' ");
		return strRet;
	});
	var re4 = new RegExp("&nbsp;", "gim");
	
	//后台处理空格
	tempContent = tempContent.replace(re4, function (element, index) {
		return "&#160;";
	});
	
	var re61 = new RegExp("<\s*/\s*hr\s*>", "gim");
	 
	tempContent = tempContent.replace(re61, function (element, index) {

		return "";

	}); 
	
	var re7 = new RegExp(/\sCHECKED/gm);
	 
	tempContent = tempContent.replace(re7, function (element, index) {
		var endChar = tempContent.substr(index+element.toString().length,1);
		if(endChar != "="){
			var strRet = element.replace(element," checked='checked' ");
			return strRet;
		}
		else{
			return element;
		}
		
	});
	
	var re8 = new RegExp(/\smultiple/gim);

	tempContent = tempContent.replace(re8, function (element, index) {
		var endChar = tempContent.substr(index+element.toString().length,1);
		if(endChar != "="){
			var strRet = element.replace(element," multiple='multiple' ");
			return strRet;
		}
		else{
			return element;
		}
		
	});
	 
	 return tempContent;
}


function dropablefield(obj, event, ui) {
	/// <summary>实现拖放功能</summary>
	/// <param name="obj" type="jq obj">obj</param>
	/// <param name="event" type="event">event</param>
	/// <param name="ui" type="object">拖动元素</param>

   
	if (obj == null) {
		return;
	}
	var newfield = creatfieldContent(obj, event, ui);
	obj.prepend(newfield);
	
	obj.select(function () { return false; });
	obj.find(eletag).dblclick(function () {
		fieldlDbClick($(this));
	});

	setSelectInputResize(obj);
	
	//设置自己可以拖动
	obj.find(eletag).draggable({ helper: "clone",cursor: "move"});
   }
  function dropablefieldtohide(obj, event, ui) {
	/// <summary>实现拖放功能</summary>
	/// <param name="obj" type="jq obj">obj</param>
	/// <param name="event" type="event">event</param>
	/// <param name="ui" type="object">拖动元素</param>

   
	if (obj == null) {
		return;
	}
	var newfield = creatfieldContent(obj, event, ui);
	obj.prepend("<li>"+newfield+"</li>");
	
	sethideDivDblClick();

	//setSelectInputResize(obj);
	
	//设置自己可以拖动
	//obj.find(eletag).draggable({ helper: "clone",cursor: "move"});
   }
 function sethideDivDblClick(){
 	tb_ul.select(function () { return false; });
 	tb_ul.find(eletag).each(function(i,n){
 		$(this).dblclick(function(){
 			$(this).closest("li").remove();
 		});

 	});
 }

function setSelectInputResize(obj){
	
	var inputs = obj.find("input[type='text']");
	var oWidth, oHeight;
	var hasHeight = false;
	var fieldtype = 0;
	if(obj.find(eletag).attr("fieldheight")!=null){
		hasHeight = true;
	}
	if(obj.find(eletag).attr("fieldtype")!=null){
		
		fieldtype = obj.find(eletag).attr("fieldtype");
	}
	if(obj.attr("fieldheight")!=null){
		hasHeight = true;
		fieldtype = obj.attr("fieldtype");
	}
	if(obj.attr("fieldtype")!=null){
		
		fieldtype = obj.attr("fieldtype");
	}
	if (inputs.length>0) {
		$.each(inputs, function (i, n) {
			//设置每个input可以拖动宽度
			var handles = "e";
			if(fieldtype!="27"){
				
				oWidth = $(this).width();
				oHeight = $(this).height();

				$(this).resizable("destroy");

				$(this).width(oWidth);
				$(this).height(oHeight);

				setElementResize($(this), handles);
			}
			else{
				//如果是地址
				if(!$(this).hasClass("addrplace")){

					setAddressElementResize($(this), handles);
				}
			}
			
		});
	}
	var textaera = obj.find("textarea");
	if (textaera.length>0) {
		$.each(textaera, function (i, n) {
			var handles = "all";
			oWidth = $(this).width();
			oHeight = $(this).height();

			$(this).resizable("destroy");

			$(this).width(oWidth);
			$(this).height(oHeight);

			setElementResize($(this), handles);
		});
	}

	var selects = obj.find("select");
	if (selects.length>0) {
		$.each(selects, function (i, n) {
			var handles = "e";
			oWidth = $(this).width();
			if(hasHeight){
				oHeight = $(this).height();
				handles = "all";
			}

			$(this).resizable("destroy");

			$(this).width(oWidth);
			
			if(hasHeight){
				$(this).height(oHeight);
			}

			setElementResize($(this), handles);
		});
	}
	//
	//设置在ie7下面margin问题
	obj.find(".ui-wrapper").css({
		"margin-right": "5px",
		"margin-left": "5px",
		"margin-top": "0"
	});
	synchronizationTableResize();
}

function clearResizeUI(obj){
	/// <summary>清除resize样式</summary>
	/// <param name="obj" type="obj">Description</param>
	if(obj==null ){
		return;
	}
	var cElement = null;
	if(obj.find("textarea").length>0){
		cElement = obj.find("textarea");
	}
	if(obj.find("input[type='text']").length>0){
		cElement = obj.find("input[type='text']");
	}
	if(obj.find("select").length>0){
		cElement = obj.find("select");
	}
	if(obj.find(".ui-wrapper").length>0&& cElement!=null){
		
		$.each(cElement,function(i,n){
			
			$(this).unwrap();
			$(this).removeAttr("style");
			//设置原来的高度
			var oheight ;
			if(obj.attr("fieldheight")!=null){
				oheight = obj.attr("fieldheight");
			}
			
			var owidth = obj.attr("fieldwidth");
			$(this).width(owidth);
			if(obj.attr("fieldheight")!=null){
				$(this).height(oheight);
			}
			$(this).removeClass("ui-resizable");
			if(obj.find(".ui-resizable-handle").length>0){
				obj.find(".ui-resizable-handle").remove();
			}
		});
		
	}
}

function destroyResizeable(obj){
	/// <summary>清除resize样式</summary>
	/// <param name="obj" type="jq obj">obj</param>
	if(obj==null ){
		return;
	}
	var oWidth, oHeight;
	var hasHeight = false;

	if(obj.attr("fieldheight")!=null){
		hasHeight = true;
	}
	var cElement = null;
	if(obj.find("textarea").length>0){
		cElement = obj.find("textarea");
	}
	if(obj.find("input[type='text']").length>0){
		cElement = obj.find("input[type='text']");
	}
	if(obj.find("select").length>0){
		cElement = obj.find("select");
	}
	if(obj.find(".ui-wrapper").length>0&& cElement!=null){
		
		$.each(cElement,function(i,n){
			oWidth = $(this).width();
			if(hasHeight){
				oHeight = $(this).height();
			}
			$(this).resizable("destroy");
			$(this).removeAttr("style");
			
			$(this).width(oWidth);
			if(hasHeight){
			 $(this).height(oHeight);
			}
		});
		
	}
}
function setElementResize(obj,handles) {
	/// <summary>设置元素可以拖动大小</summary>
	/// <param name="obj" type="jq obj">obj</param>
	/// <param name="handles" type="string">拖动样式 'n, e, s, w, ne, se, sw, nw, all'.</param>

	if (obj == null) {
		return;
	}
	obj.resizable({
		handles: handles,
		resize: function (event, ui) {

			var sizeToolTip = $(".size-tooltip-container");
			sizeToolTip.show();
			sizeToolTip.css({
				top: ui.element.offset().top - sizeToolTip.height() - 2 + (ui.element.outerHeight()),
				left: ui.element.offset().left+5,
				width: ui.element.outerWidth() - 9
			});
			$(".size-tooltip-text").html($(this).width()  + "px");
		},
		stop: function (event, ui) {
			//设置容器宽度
			var span = ui.element.parent();
			if(ui.size.height!=ui.originalSize.height){
				if(span!=null){
					span.attr("fieldheight",ui.size.height);
					obj.attr("height",ui.size.height);
				}
			}
			if(ui.size.width!=ui.originalSize.width){
				if(span!=null){
					span.attr("fieldwidth",ui.size.width);
					obj.attr("width",ui.size.height);
				}
			}
			
			$(".size-tooltip-container").hide();
			//同步表格与container大小
			synchronizationTableResize();
		},
		 zIndex:99
	});
}
function setAddressElementResize(obj,handles) {
	/// <summary>设置元素可以拖动大小</summary>
	/// <param name="obj" type="jq obj">obj</param>
	/// <param name="handles" type="string">拖动样式 'n, e, s, w, ne, se, sw, nw, all'.</param>

	if (obj == null) {
		return;
	}
	obj.resizable({
		handles: handles,
		alsoResize: ".addrplace",
		resize: function (event, ui) {

			var sizeToolTip = $(".size-tooltip-container");
			sizeToolTip.show();
			sizeToolTip.css({
				top: ui.element.offset().top - sizeToolTip.height() - 2 + (ui.element.outerHeight()),
				left: ui.element.offset().left+5,
				width: ui.element.outerWidth() - 9
			});
			$(".size-tooltip-text").html($(this).width()  + "px");
		},
		stop: function (event, ui) {
			//设置容器宽度
			var span = ui.element.parent();
			if(ui.size.height!=ui.originalSize.height){
				if(span!=null){
					span.attr("fieldheight",ui.size.height);
					obj.attr("height",ui.size.height);
				}
			}
			if(ui.size.width!=ui.originalSize.width){
				if(span!=null){
					span.attr("fieldwidth",ui.size.width);
					obj.attr("width",ui.size.height);
				}
			}
			
			$(".size-tooltip-container").hide();
			//同步表格与container大小
			synchronizationTableResize();
		},
		 zIndex:99
	});
}

function fieldlDbClick(obj) {
	/// <summary>字段双击事件</summary>
	/// <param name="obj" type="jq obj">obj</param>
	
	var fileType = parseInt(obj.attr("fieldtype"),10);
	if(fileType >-1){
		
		var defaultHeight = "-1";
		if(obj.attr("fieldheight")!=null){
			defaultHeight = obj.attr("fieldheight");
		}
		var defaultWidth = "-1";
		if (obj.attr("fieldwidth") != null) {
		    defaultWidth = obj.attr("fieldwidth");
		}
		var afterttext = "";
		
		if(obj.next(".custform-aftertxt").length==0){
			obj.after(sf_aftertext);
		}
		var fieldData = {
				fieldtype: obj.attr("fieldtype"),
				code: obj.attr("fieldcode"),
				name: obj.attr("fieldname").toString().replace(label_suffix,""),
				required: obj.attr("isrequired"),
				checkdup: obj.attr("ischeckdup"),
				aftertxt:obj.next(".custform-aftertxt").html(),
				width: defaultWidth,
				height:defaultHeight
			};
		
			e5.dialog({
				type: 'iframe',
				value: 'FormFieldWindow.jsp?' + $.param(fieldData)
				},
				{ title: i18n.modifyfield, id: "field-window", width: 330, height: 300, resizable: false, minH: 140, minW: 330, showClose: false }
				).show();
		
	}
	else{
		
		if(fileType == -4){
			var tempContent = obj.html();
			//格式化input
			tempContent = appendUnClosedTag(tempContent);
			
			//格式化自有属性
			tempContent = formatSelfAttrTag(tempContent);
			var fieldData = {
					insertid:obj.attr("id"),
					content:tempContent
					
			};
			//设置插入文本内容
			 e5.dialog({
			        type: 'iframe',
			        value: 'FormFieldTxtWindow.jsp?' + $.param(fieldData)
			        },
			        { title: i18n.preview, id: "fieldtxt-window", width: 330, height: 180, resizable: false, minH: 140, minW: 330, showClose: false }
			        ).show();
		}
		
	}
	
	
	

}
function listfieldtxtWindowClose(data, isdel) {
	/// <summary>关闭窗口事件</summary>
	/// <param name="data" type="array">obj</param>
	/// <param name="isdel" type="bool">是否删除</param>
	e5.dialog.close("fieldtxt-window");
	
	if(data!=null){
		
		if(isdel){
			$("#"+data.insertid).remove();
		}
		else{
			$("#" + data.insertid).html(data.content);
		}
	}
	
	
}
function listfieldWindowClose(data, isdel) {
	/// <summary>关闭窗口事件</summary>
	/// <param name="data" type="array">obj</param>
	/// <param name="isdel" type="bool">是否删除</param>
	e5.dialog.close("field-window");
	if (data != null) {
		var span = $("#tbcontent").find("span[fieldcode='" + data.code + "']");

		if (isdel == true) {
			//删除
			span.remove();
			return;
		}
		
		

		if (span != null) {
		    if (data.name.toString().length > 0) {

		        span.attr("fieldname", data.name + label_suffix);
		        span.find("label").html(data.name + label_suffix);
		    }
		    else
		    {
		        span.attr("fieldname", "");
		        span.find("label").html("&nbsp;&nbsp;&nbsp;&nbsp;");
		    }
			
			span.attr("isrequired", data.required);
			span.attr("ischeckdup", data.checkdup);
			
			if(data.height!="-1"){
				span.attr("fieldheight", data.height);
			}
			if (data.width != "-1") {
			    span.attr("fieldwidth", data.width);
			}
			if(span.find("label").hasClass("custform-label-require")){
				span.find("label").removeClass("custform-label-require");
			}
			if(data.required.toLowerCase()=="true"){
				span.find("label").addClass("custform-label-require");
			}
			if(data.checkdup.toLowerCase()=="true"){
				span.find("label").addClass("custform-label-checkdup");
			}

			if (data.aftertxt != null) {
				if(span.next(".custform-aftertxt").length>0){
					span.next(".custform-aftertxt").html(convertTagcode2Html(data.aftertxt));
				}
			}
			
			var handles = "e";
			switch (data.fieldtype.toString()) {
				case "1":
					handles = "e";
					span.find("select").width(data.width);
					//默认设置为19px
					span.find("select").height("21px");
					break;
			    case "5":
			        handles = "e";
			        span.find("select").width(data.width);
			        //默认设置为19px
			        span.find("select").height("21px");
			        break;
			    case "16":
			        handles = "e";
			        span.find("select").width(data.width);
			        //默认设置为19px
			        span.find("select").height("21px");
			        break;
			    case "17":
			        handles = "e";
			        span.find("select").width(data.width);
			        //默认设置为19px
			        span.find("select").height("21px");
			        break;
				case "7":
					handles = "all";
					span.find("select").width(data.width);
					span.find("select").height(data.height);
					break;
			    case "13":
			        handles = "all";
			        span.find("select").width(data.width);
			        span.find("select").height(data.height);
			        break;
				case "21":
					handles = "all";
					span.find("textarea").width(data.width);
					span.find("textarea").height(data.height);
					break;
				default:
					handles = "e";
					if (data.width != "-1") {
						span.find("input[type='text']").width(data.width);
					}
					break;

			}
			
			
		   setSelectInputResize(span);
		   
		   
		}
	}
	
}
function guidGenerator() {
      var S4 = function() {
         return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
      };
      return (S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
   }
function creatfieldContent(obj, event, ui) {
	/// <summary>创建页面字段控件</summary>
	/// <param name="obj" type="jq obj">obj</param>
	/// <param name="event" type="event">event</param>
	/// <param name="ui" type="object">拖动元素</param>

	var li = ui.draggable;

	var sb = new Array();

	

	var fieldType = li.attr("fieldtype")

	var sysfieldName = li.attr("fieldname") + label_suffix;
	var sysfieldCode = li.attr("fieldcode");
	var sysDataType = li.attr("datatype");

	var defaultHeight = "21";
	var defaultWidth = "133";

	switch (fieldType) {
		case "-1":
			break;
		case "7":
			defaultHeight = "50";
			//defaultWidth = "100";
			break;
	    case "13":
	        defaultHeight = "50";
	        //defaultWidth = "100";
	        break;
		case "21":
			defaultHeight = "50";
			//defaultWidth = "100";
			break;
		case "27":
			defaultWidth = "266";
			break;
//		case "28":
//			defaultHeight = "100";
//			break;
	}
	

	if (parseInt(fieldType.toString(), 10) > -1) {
		sb.push("<" + eletag + " fieldname='" + sysfieldName + "' datetype='"+sysDataType+"' isrequired='false' fieldcode='" + sysfieldCode + "' fieldtype='" + fieldType + "'  class='custom-field drop-class' ");
	}
	

	switch (fieldType) {
		case "0":   //任意填写（单行）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			if (sysDataType.toString().toLowerCase() == "timestamp" || sysDataType.toString().toLowerCase() == "date") {
			    sb.push("<label class='custform-label'>" + sysfieldName +"</label>");
				sb.push("<input type='text' />");
				sb.push("<input  class='button' type='button' value='" + i18n.btnselect + "' />");
				
			}
			else {
			    sb.push("<label class='custform-label'>" + sysfieldName  + "</label>");
				sb.push("<input type='text' style='width:"+defaultWidth+"px;' />");
			}
			break;
		case "1":   //单选（下拉框方式）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<select  style='width:"+defaultWidth+"px;'><option>"+i18n.option1+"</option><option>"+i18n.option2+"</option></select>");
			break;
		case "2":   //单选（是/否选择）
			
			if (sysDataType.toString().toLowerCase() == "timestamp" || sysDataType.toString().toLowerCase() == "date") {
				
				sb.push(" fieldwidth='" + defaultWidth +  "' ");
				sb.push(" >");
				sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
				sb.push("<input type='text' style='width:"+defaultWidth+"px;' />");
				sb.push("<input class='button'  type='button' value='" + i18n.btnselect + "' class='formcustom-button-date' />");

			}
			else {
				sb.push(" >");
				sb.push("<input class='fl' type='checkbox' value='' />");
				sb.push("<label class='ml5'>" + sysfieldName + "</label>");
			}
			break;
	    case "5":   //单选（下拉框select，动态取值）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");
	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<select  style='width:" + defaultWidth + "px;'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
	        break;
		case "7":   //多选
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" fieldheight='" + defaultHeight + "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<select multiple='multiple' style='width:"+defaultWidth+"px;height:"+defaultHeight+"px;'><option>"+i18n.option1+"</option><option>"+i18n.option2+"</option></select>");
			break;
	    case "9":   //任意填写（单行，带填写提示）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");
	       
	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<input type='text' style='width:" + defaultWidth + "px;' />");
	       
	        break;
	    case "10":   //任意填写（单行，带填写提示，键值对）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");
	        
	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<input type='text' style='width:" + defaultWidth + "px;' />");
	        
	        break;
	    case "11":   //任意填写（单行，带填写提示，键值对）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");

	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<input type='radio' />选项1<input type='radio' />选项2");

	        break;
	    case "12":   //单选（单选框radio，动态取值）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");

	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<input type='radio' />选项1<input type='radio' />选项2");

	        break;
	    case "13":   //多选（下拉框select，动态取值）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" fieldheight='" + defaultHeight + "' ");
	        sb.push(" >");
	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<select multiple='multiple' style='width:" + defaultWidth + "px;height:" + defaultHeight + "px;'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
	        break;
	    case "14":   //多选（复选框checkbox）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");

	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<input type='checkbox' />选项1<input type='checkbox' />选项2");

	        break;
	    case "15":   //多选（复选框checkbox）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");

	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<input type='checkbox' />选项1<input type='checkbox' />选项2");

	        break;
	    case "16":   //分类（下拉框select，只可用于单层分类）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");
	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<select  style='width:" + defaultWidth + "px;'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
	        break;
	    case "17":   //其它数据（下拉框，动态取值，键值对）
	        sb.push(" fieldwidth='" + defaultWidth + "' ");
	        sb.push(" >");
	        sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
	        sb.push("<select  style='width:" + defaultWidth + "px;'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
	        break;
		case "21":  //任意填写（多行）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" fieldheight='" + defaultHeight + "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<textarea style='width:"+defaultWidth+"px;height:"+defaultHeight+"px;' ></textarea>");
			break;
		case "24":  //电子邮件（专用文本，自动进行格式验证）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input  type='text' value='' style='width:"+defaultWidth+"px;' />");
			break;
		case "25":  //固定电话（专用文本，自动进行格式验证）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input   type='text' value='' style='width:"+defaultWidth+"px;' />");
			break;
		case "26":  //手机（专用文本，自动进行格式验证）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input type='text' value='' style='width:"+defaultWidth+"px;' />");
			break;
		case "27":  //地址拆分（分开填写方式，分为：省,市,区/县,街道,楼号）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			//sb.push("<input readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='  "+i18n.province+i18n.city+ " ' />");
			
			sb.push("<div class='custform-from-wrap'>")
			sb.push("<input readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='' />" + i18n.province + "  <br class='clear'/>");
			sb.push("<input class='addrplace' readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='' />" + i18n.city + "  <br class='clear' />");
			sb.push("<input class='addrplace' readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='' />" + i18n.area + " <br class='clear'/>");
			sb.push("<input class='addrplace' readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='' />" + i18n.street + "  <br class='clear'/>");
			sb.push("<input class='addrplace' readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='' />" + i18n.building + "");
			sb.push("</div>")
			break;
		case "28":  //日期拆分（分开填写方式，分为：年,月,日）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='" + i18n.demodate + "' />");
			break;
		case "29":  //部门（部门树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input   type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input class='button'  type='button' value='" + i18n.selectdept + "' />");
			break;
		case "30":  //用户（用户树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input class='button'  type='button' value='" + i18n.selectusers + "' />");
			break;
		case "6":   //分类（分类树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input  class='button' type='button' value='" + i18n.selectcatgory + "' />");
			break;
		case "33":  //分类（分类树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input  class='button' type='button' value='" + i18n.selectcatgory + "' />");
			break;
		case "34":  //部门（部门树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input multiple='multiple'  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input  class='button' type='button' value='" + i18n.selectdept + "' />");
			break;
		case "35":  //用户（用户树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input multiple='multiple'  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input class='button'  type='button' value='" + i18n.selectusers + "' />");
			break;
		case "36":  //角色（角色树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input multiple='multiple'  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input class='button'  type='button' value='" + i18n.selectrole + "' />");
			break;
		case "37":  //角色（角色树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='custform-label'>" + sysfieldName + "</label>");
			sb.push("<input multiple='multiple'  type='text' value='' style='width:"+defaultWidth+"px;' />");
			sb.push("<input class='button'  type='button' value='" + i18n.selectrole + "' />");
			break;
		case "38":   //附件
			sb.push(" fieldwidth='" + defaultWidth +  "'  >");
			sb.push("<label class='custform-label'>" + sysfieldName  + "</label>");
			sb.push("<input type='file' style='width:"+defaultWidth+"px;' />");
			break;

		case "-4":  //请插入文本

			var uidinsert =  guidGenerator();
			sb.push("<" + eletag + " fieldtype='" + fieldType + "' name='inserttxt' id='inserttxt_" + uidinsert + "' class='custom-field'>" + i18n.pleaseinserttext);
		   
			break;
		case "-5":  //插入分割线
			sb.push("<hr />");
			break;
		case "-1":  //保存
			sb.push("<" + eletag + " id='txtFormSave' fieldtype='" + fieldType + "' fieldcode='" + sysfieldCode + "'>");
			sb.push("<input class='button btn' id='btnFormSave' type='submit' value='" + i18n.btnsave + "' />");
		   
			break;
		case "-2":    //保存并提交
			sb.push("<" + eletag + " id='txtFormSaveSubmit' fieldtype='" + fieldType + "' fieldcode='" + sysfieldCode + "'>");
			sb.push("<input class='button btn' id='btnFormSaveSubmit' type='submit' value='" + i18n.btnsavesubmit + "' />");
		   
			break;
		case "-3":    //取消
			sb.push("<" + eletag + " id='txtFormCancel' fieldtype='" + fieldType + "' fieldcode='" + sysfieldCode + "'>");
			sb.push("<input class='button btn' id='btnFormCancel' type='button' value='" + i18n.btncancel + "' />");
		  
			break;

	}
	if (fieldType != "-5") {
	   
		sb.push("</"+eletag+">");
		 sb.push(sf_aftertext);
	}
	return sb.join('');
}

function regOperation(){
	var dealCount = 0;
	var dealCounts = document.getElementsByName("dealCount");
	for(var i=0;i<dealCounts.length;i++){
		if(dealCounts[i].checked)
			dealCount = dealCounts[i].value;
	}
	var regUrl = "FormController.do?action=registOperate&formID=" + $.query.get("formID") + 
		"&formcode=" + encodeURI($("#formcode").val()) + 
		"&dealCount=" + dealCount + 
		"&operationname=" + encodeURI($("#operationname").val()) +
		"&height=" + $("#tbcontent").height()+ 
		"&width=" +$("#tbcontent").width();
	//alert(regUrl);
	$.ajax({
		  type: "POST",
		  url: regUrl,
		  dataType : "json",
		  success: function(data, textStatus){
			  var dataStr = new String(data);
				if(data != null && data != ""&&!(dataStr.indexOf("error:")>=0)){
					e5.dialog.hide('regOperationDialog');
					$("#regOperationForm").validationEngine("hideAll");
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						var form = datas[0];
					    var params = {"docTypeID":form.docTypeID,
								"docTypeName":form.docTypeName,
								"formID":form.id,
								"formName":form.name,
								"formCode":form.code,
								"cssPath":form.pathCSS,
								"jsPath":form.pathJS
						};
					    var Url = "FormCustomField.jsp?"+$.param(params);
						document.location.href = Url;
					}
					parent.initFormList();
				}else{
					alert(i18n.regoption_failed  +"," + data);
				}
				$("#saveRegOperation").attr("disabled",false);
				$("#cancelRegOperation").attr("disabled",false);
		  },
		  error: function(data){
			  //请求出错处理
			  alert(i18n.regoption_failed +"," + data);
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
	$("#exportFrame").attr(
			"src",
			"FormController.do?action=exportToJsp&formID="+$.query.get("formID"));
}
function resizeHandle(){
	var bodyH = $("body").data("resize-special-event").h,
		fieldContainer = $(".field-container"),
		menu = $(".menu"),
		mainContent = $(".mainContent"),
		header = $(".header"),
		sidebarTitle = $(".sidebar h2"),
		searchField = $(".search-field"),
		listfield = $("#listfield"),
		listCust = $("#listCust"),
		workspaceTitle = $(".workspace h2"),
		workspaceContent = $(".workspace .content"),
		mainContentH = bodyH
			- parseInt(fieldContainer.css("margin-top"))
			- parseInt(fieldContainer.css("margin-bottom"))
			- parseInt(mainContent.css("margin-top"))
			- parseInt(mainContent.css("margin-bottom"))
			- header.outerHeight(true)
			- menu.outerHeight(true)
			- 10,
		listfieldH = mainContentH
			- sidebarTitle.outerHeight(true)
			- searchField.outerHeight(true)
			- listCust.outerHeight(true),
		workspaceContentH = mainContentH
			- workspaceTitle.outerHeight();
		mainContent.height(mainContentH);
		listfield.height(listfieldH);
		workspaceContent.height(workspaceContentH);
}