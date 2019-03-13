
/// <reference path="../../../../../projects/e5ui/e5ui/e5ui/scripts/jquery-1.7.1-vsdoc.js" />
/// <reference path="../../../../../projects/e5ui/e5ui/e5ui/scripts/jquery-ui-1.8.18.js" />
//字段元素标签
var eletag = "span";
var tbc;
var list_common, list_more;
;$(function () {
    //判断注册为操作的按钮是否显示
    //初始化变量
    tbc = $("#tableContainer");
   
    list_common = $("#list_common");
    list_more = $("#list_more");

    list_common.css("min-height", "100px");
    list_more.css("min-height", "100px");

	var formID = $.query.get("formID");
	if(formID==0){
		$("#btnPreview").hide();
		$("#btnRegOperation").hide();
	}else{
		$("#btnPreview").show();
		//$("#btnRegOperation").show();
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
	 
	setFListDropable();
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
		url = "QueryFormCustom.jsp?" + $.param(params);
		document.location.href = url;
	});
	//按钮操作

	getDoctTypefields();


    // $("#listCust li").draggable({
    //     helper: "clone",
    //     cursor: "move"
    // });
    
    $("#txtFormName").html($.query.get("formName"));
    $("#txtFormCode").html($.query.get("formCode"));
    //设置字段可以拖动
    if($.query.get("formID")!=""){
        //更新
        if ($.query.get("formCode") != "") {
            //$("#btnRegOperation").attr("disabled", true);
        }
        
    }

    tbc.width(list_common.width());
   
	tableContainerResize();

 
	$(document.body).bind("resize",resizeHandle);
	resizeHandle();
    //
	addSearchHelp();
	setLiSortable();
	setFieldAttr();
	
});

function synchronizationTableResize(){
	/// <summary>同步表格与container高度和宽度</summary>
    if ((parseInt(list_common.height(), 10) + parseInt(list_more.height(), 10)) != parseInt(tbc.height(), 10)) {
        tbc.height(list_common.height() + list_more.height() + (parseInt($(".list_searchhelp").height(),10) * 2));
    	 
     }
    if (parseInt(list_common.width(), 10) != parseInt(tbc.width(), 10)) {
        tbc.width(list_common.width());
     }
    setFListDropable();
    setLiSortable();
    setFieldAttr();
    
}
function setFieldAttr() {

    if (tbc.length > 0) {
        $("li", tbc).find(".list_search_span").each(function (i, n) {
            $(this).dblclick(function () {
                fieldlDbClick($(this));
            });
        });
    }

}
function tableContainerResize(){
	/// <summary>设置表格大小</summary>
	
	var oldTableHeight = 0;
	tbc.resizable({
        handles: "e",
        resize: function (event, ui) {

        },
        stop: function (event, ui) {
            //设置容器宽度
            synchronizationTableResize();
        },
        zIndex:99
    });
}
function tableContainerUnResize() {

    if (tbc.length == 0) {
        return;
    }
    tbc.resizable("destroy");
}

function addSearchHelp() {
    if (list_common.length > 0) {
        list_common.before($("<div class=\"list_searchhelp\">"+i18n.help1+"</div>"));
    }
    if (list_more.length > 0) {
        list_more.before($("<div class=\"list_searchhelp\">"+i18n.help2+"</div>"));
    }
}
function removeSearchHelp() {
    $(".list_searchhelp").remove();
}
function previewForm(){
	/// <summary>预览表单</summary>
	if($.query.get("formID")!=""){
		
		var winWidth = $(".header").width() - 20;
		 e5.dialog({
		        type: 'iframe',
		        value: 'QueryFormPreview.do?action=preView&formID=' + $.query.get("formID")
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

	//设置双击字段添加到一般检索区域
	$("#listfield li").each(function(i,n){

		$(this).dblclick(function(){
			var ui = {
				draggable:$(this),
				helper:$(this),
				offset:$(this).offset(),
				position:$(this).position()
			};
			listCommonDropable(null,ui);

		});
	});
}
function getDoctTypefields() {
	/// <summary>初始加载</summary>
	if ( $("#sl-doc-type").length > 0 ) { 
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
							if(d.id==$.query.get("docTypeID")){
								options = options+ "<option selected='selected' value='"+d.id+"'>"+d.name+"</option>";
							}else{
								options = options+ "<option value='"+d.id+"'>"+d.name+"</option>";
							}
						});
					}
					$("#sl-doc-type").html(options);
					getDoctTypefieldsFun($.query.get("docTypeID"),$.query.get("formID"));
				}
			}
		});
		$("#sl-doc-type").change(function(){
			getDoctTypefieldsFun($("#sl-doc-type").val(),$.query.get("formID"));
		});
	} 
}
function getDoctTypefieldsFun(docTypeID,formId) {
	/// <summary>获取文档字段</summary>

	//获取文档类型字段
	$.ajax({
		url: "../e5listpage/cust_typeReader.do?docType=" + docTypeID,
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
					var docType=$("#sl-doc-type option:selected").text();
					$.each(datas, function (i, f) {
						// <li fieldtype="0" fieldname="名称" fieldcode="sys_name">名称</li>
						lifiles = lifiles + "<li class='drop-class' datatype='"+f.datatype+"' fieldtype='" + f.fieldtype + "' fieldcode='" + f.code + "' fieldname='" + f.name + "' docType='" +docType + "' docTypeId='" + docTypeID+ "'>" + f.name + "</li>";
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

									$("#listfield").append("<li class='drop-class' datatype='"+n.datatype+"'  fieldtype='" + n.fieldtype + "' fieldcode='" + n.code + "' fieldname='" + n.name + "' docType='" +docType + "' docTypeId='" + docTypeID+ "'>" + n.name + "</li>");
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

								$("#listfield").append("<li class='drop-class' datatype='"+row.datatype+"' fieldtype='" + row.fieldtype + "' fieldcode='" + row.code + "' fieldname='" + row.name +"' docType='" +docType + "' docTypeId='" + docTypeID+ "'>" + row.name + "</li>");
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
				setfieldDragable();
			   if($.query.get("formID") == ""||$.query.get("formID")==null){
				   return;
			   }
			  //开始加载已有数据
				$.ajax({
					url: "QueryFormController.do?action=getForm&formID=" + formId,
					dataType: "json",
					async: false,
					success: function (data) {
					    
						
						if (data != null) {

							if(data.formSlice!=null){

								var tableContent = data.formSlice.toString().replace("<custom><![CDATA[","").replace("]]></custom>","");
							
								tbc.html(tableContent);
								list_common = $("#list_common");
								list_more = $("#list_more");
								//设置初始高度
								if(list_common.length>0){
									 
								    tbc.width(list_common.attr("customwidth"));
								    
								}
								
								
								list_common.css("min-height", "100px");
								list_more.css("min-height", "100px");
						
								
								//1.设置每个td可以dropable
								synchronizationTableResize();

							}
					    
						}
						
					}
				});
				//开始加载已有数据结束
			}
		}
	});
}

function setLiSortable() {
    /// <summary>设置LI可以拖动排序</summary>
    if (list_common.length > 0) {
        list_common.sortable();
    }
    if (list_more.length > 0) {
        list_more.sortable();
    }

}
function setFListDropable() {
    /// <summary>设置</summary>
    list_common.droppable({
            accept: ".drop-class",
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            over: function (event, ui) {
               
            },
            drop: function (event, ui) {
                //
               listCommonDropable(event,ui);
            }
            
    });
    //高级拖放
    list_more.droppable({
        accept: ".drop-class",
        activeClass: "ui-state-hover",
        hoverClass: "ui-state-active",
        over: function (event, ui) {
          //listCommonDropable(event,ui);
        },
        drop: function (event, ui) {
            //
            if (ui.draggable.is("li")) {

                if (ui.draggable.parent().attr("id") == "listfield") {
                    var fileType = ui.draggable.attr("fieldtype");
                    var doctypeid = ui.draggable.attr("doctypeid");
                    //大于-4的只能拖动一次
                    if (parseInt(fileType, 10) > -4) {
                        //判断是否已经存在该字段
                        var span = tbc.find("span[fieldcode='" + ui.draggable.attr("fieldcode") + "'][doctypeid='" + doctypeid + "']");
                        if (span.length == 0) {
                            dropablefield(list_more, event, ui);
                        }
                    }
                    else {
                        dropablefield(list_more, event, ui);
                    }
                }
                if (ui.draggable.parent().attr("id") == "list_common") {
                    //从一般检索拖放到稿件检索
                    var litemp = ui.draggable.html();
                    ui.draggable.remove();
                    list_more.append("<li class='drop-class'>" + litemp + "</li>");
                    
                }
            }
           
            //同步表格与container大小
            synchronizationTableResize();
        }

    });

}

function listCommonDropable(event,ui){
	//一般检索区域拖放时间
	 if (ui.draggable.is("li")) {
			   
        if (ui.draggable.parent().attr("id") == "listfield") {
            var fileType = ui.draggable.attr("fieldtype");
			
			if(fileType == "38") {
				alert("附件不能作为查询条件");
				return;
			}
			
            var doctypeid = ui.draggable.attr("doctypeid");
            //大于-4的只能拖动一次
            if (parseInt(fileType, 10) > -4) {
                //判断是否已经存在该字段
                var span = tbc.find("span[fieldcode='" + ui.draggable.attr("fieldcode") + "'][doctypeid='" + doctypeid + "']");
                if (span.length == 0) {
                    dropablefield(list_common, event, ui);
                }
            }
            else {
                dropablefield(list_common, event, ui);
            }
        }
        if (ui.draggable.parent().attr("id") == "list_more") {
            //从一般检索拖放到稿件检索
            var litemp = ui.draggable.html();
            ui.draggable.remove();
            list_common.append("<li class='drop-class'>" + litemp + "</li>");

        }
	
    }
	//同步表格与container大小
    synchronizationTableResize();
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
	sb.push("<root>");

	sb.push("<fields>");
	tbc.find(eletag).each(function (i, n) {

		if($(this).attr("fieldtype")!=null&&parseInt($(this).attr("fieldtype"),10)>-1){
			sb.push("<field docTypeId=\""+$(this).attr("doctypeid") +"\" docType=\""+$(this).attr("doctype") +"\">" + $(this).attr("fieldcode") + "</field>");
		}
	});
	sb.push("</fields>");
	sb.push("</root>");
	//移除tbc的拖动大小
	tableContainerUnResize();
	removeSearchHelp();
	//保存table内容开始
	sb.push("<formslice>");
	sb.push("<custom><![CDATA[");
	
	//设置表格高度和宽度
	if (tbc.length > 0) {
	    list_common.attr("customwidth", tbc.width());
	    list_common.attr("customheight", list_common.height());
	    list_common.removeAttr("style");
	    list_more.removeAttr("style");
	}
	//处理input没有结束标签的问题
	var tempContent = "";

	//格式化input
	tempContent = appendUnClosedTag(tbc.html());
	
	//格式化自有属性
	tempContent = formatSelfAttrTag(tempContent);
	
	sb.push(tempContent);
	
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
					  url: "QueryFormController.do?action=updateForm&formID="+$.query.get("formID"),
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
									var Url = "FormCustomSearchField.jsp?"+$.param(params);
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
    
	obj.append("<li class='drop-class'>" + newfield + "</li>");

	obj.select(function () { return false; });

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
	
        var showall = false;
        var showmodel = false;
        var multiple = false;
        var isrequired = false;
		var range = false;
		var datatype = "";
       
        if(obj.attr("showall")!=null){
			showall = obj.attr("showall");
		}
        if(obj.attr("showmodel")!=null){
			showmodel = obj.attr("showmodel");
		}
        if(obj.attr("ismultiple")!=null){
			multiple = obj.attr("ismultiple");
		}
		if(obj.attr("isrange")!=null){
			range = obj.attr("isrange");
		}
		if(obj.attr("datatype")!=null){
			datatype = obj.attr("datatype").toString().toLowerCase();
		}else if(obj.attr("datetype")!=null){
			datatype = obj.attr("datetype").toString().toLowerCase();
		}
       
		var fieldData = {
				fieldtype: obj.attr("fieldtype"),
				doctypeid:obj.attr("doctypeid"),
				code: obj.attr("fieldcode"),
				name: obj.attr("fieldname"),
				datatype: datatype,
				range:range,
				showall:showall,
				showmodel:showmodel,
				multiple:multiple
		};
	    try{
	        e5.dialog({
	            type: 'iframe',
	            value: 'FormCustomSearchFieldWindow.jsp?' + $.param(fieldData)
	        },
				{ title: i18n.modifyfield, id: "field-window", width: 330, height: 200, resizable: false, minH: 100, minW: 330, showClose: false }
				).show();
	    }
	    catch(e){}
			
		
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
		    try{
		        e5.dialog({
		            type: 'iframe',
		            value: 'FormFieldTxtWindow.jsp?' + $.param(fieldData)
		        },
               { title: i18n.preview, id: "fieldtxt-window", width: 330, height: 180, resizable: false, minH: 140, minW: 330, showClose: false }
               ).show();
		    }
		    catch(e){}
		
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
	    var span = tbc.find(eletag+"[fieldcode='" + data.code + "'][doctypeid='"+data.doctypeid+"']");

		if (isdel == true) {
			//删除
			span.remove();
			return;
		}
		if (span != null) {
		    span.attr("fieldname", data.name);
		    if (span.find("label").prev("input").length == 0) {
		        span.find("label").html(data.name + ":");
		    }
		    else {
		        span.find("label").html(data.name );
		    }
			
			if(data.fieldtype.toString()=="1" || data.fieldtype.toString()=="5" || data.fieldtype.toString()=="7" ||
			data.fieldtype.toString()=="13" || data.fieldtype.toString()=="11" || data.fieldtype.toString()=="12" || 
			data.fieldtype.toString()=="14"  || data.fieldtype.toString()=="15" || data.fieldtype.toString()=="16" || data.fieldtype.toString()=="17"){
                var oldModel = span.attr("showmodel");
                if(oldModel!="select"){
                    oldModel = "input[type='"+oldModel+"']";
                }
				span.attr("showall",data.showall);
				span.attr("ismultiple",data.multiple);
				span.attr("showmodel",data.showmodel);
				var modelHtml = "<select class='list_search_selectOne'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>";
                if(data.showmodel=="radio"){
                    modelHtml="<input type='radio' />"+i18n.option1+" <input type='radio' />"+i18n.option2;
                }else if(data.showmodel=="checkbox"){
                    modelHtml="<input type='checkbox' />"+i18n.option1+" <input type='checkbox' />"+i18n.option2;
                }
                var label = span.find("label");
                span.empty();
                span.append(label);
                span.append(modelHtml);
			}
            if(data.fieldtype.toString()=="6" || data.fieldtype.toString()=="33" || data.fieldtype.toString()=="29" ||
			data.fieldtype.toString()=="34" || data.fieldtype.toString()=="30" || data.fieldtype.toString()=="35" || 
			data.fieldtype.toString()=="36"  || data.fieldtype.toString()=="37"){
                span.attr("ismultiple",data.multiple);
            }
			if(data.fieldtype.toString()=="0" && 
				(data.datatype.toString()=="integer"
				|| data.datatype.toString()=="long" 
				|| data.datatype.toString()=="float" 
				|| data.datatype.toString()=="double")){
				span.attr("isrange",data.range);
				var modelHtml = "<input type='text' class='list_search_inputOne' />";
				if(data.range=="true")
					modelHtml = "<input type='text' class='list_search_inputHalf' />至<input type='text' class='list_search_inputHalf'/>";
				var label = span.find("label");
                span.empty();
                span.append(label);
                span.append(modelHtml);
			}
		}
	}
	
}

function creatfieldContent(obj, event, ui) {
	/// <summary>创建页面字段控件</summary>
	/// <param name="obj" type="jq obj">obj</param>
	/// <param name="event" type="event">event</param>
	/// <param name="ui" type="object">拖动元素</param>

	var li = ui.draggable;

	var sb = new Array();

	

	var fieldType = "";
	fieldType = li.attr("fieldtype")

	var sysfieldName = li.attr("fieldname");
	var sysfieldCode = li.attr("fieldcode");
	var sysDataType = li.attr("datatype");
	var sysDocType = li.attr("doctype");
	var sysDocTypeId = li.attr("doctypeid");

	var defaultHeight = "21";
	var defaultWidth = "133";

	switch (fieldType) {
		case "-1":
			break;
        case "1":
        case "5":
        case "16":
        case "17":
            defaultWidth = "100";
            break;
		case "7":
        case "13":
			defaultHeight = "50";
			defaultWidth = "100";
			break;
		case "21":
			defaultHeight = "50";
			defaultWidth = "100";
			break;
		case "27":
			defaultWidth = "266";
			break;
//		case "28":
//			defaultHeight = "100";
//			break;
	}
	

	if (parseInt(fieldType.toString(), 10) > -1) {
	    sb.push("<" + eletag + " fieldname='" + sysfieldName + "' doctype='" + sysDocType + "' doctypeid='" + sysDocTypeId + "' datetype='" + sysDataType + "' datetype='" + sysDataType + "' isrequired='false' fieldcode='" + sysfieldCode + "' fieldtype='" + fieldType + "'  class='list_search_span drop-class' ");
	}
	
	//alert(fieldType);
	switch (fieldType) {
		case "0":   //任意填写（单行）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			if (sysDataType.toString().toLowerCase() == "timestamp" || sysDataType.toString().toLowerCase() == "date") {
				sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
				sb.push("<input type='text' class='list_search_inputHalf' />至");
				sb.push("<input type='text' class='list_search_inputHalf'/>");
			}
			else {
				sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
				sb.push("<input type='text' class='list_search_inputOne' />");
			}
			break;
		case "1":   //单选（下拉框方式）		
			sb.push(" fieldwidth='" + defaultWidth +  "' showmodel='select'  ismultiple='false' showall='true'");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<select class='list_search_selectOne'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
			break;
		case "2":   //单选（是/否选择）
			
			if (sysDataType.toString().toLowerCase() == "timestamp" || sysDataType.toString().toLowerCase() == "date") {
				
				sb.push(" fieldwidth='" + defaultWidth +  "' ");
				sb.push(" >");
				sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
				sb.push("<input type='text' style='width:"+defaultWidth+"px;' />");
				sb.push("<input class='button'  type='button' value='" + i18n.btnselect + "' class='formcustom-button-date' />");

			}
			else {
				sb.push(" showmodel='checkbox' >");
				sb.push("<input class='fl' type='checkbox' value='' />");
				sb.push("<label class='ml5'>" + sysfieldName + "</label>");
			}
			break;
	 	case "5":   //单选（下拉框方式，动态取值）
			sb.push(" fieldwidth='" + defaultWidth +  "' showmodel='select'  ismultiple='false' showall='true'");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<select class='list_search_selectOne'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
            break;
		case "7":   //多选
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" fieldheight='" + defaultHeight + "' showmodel='select'  ismultiple='true' showall='true'");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<select multiple='multiple' class='list_search_selectOne' style='height:" + defaultHeight + "px;'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
			break;
		case "9":
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='text' class='list_search_inputOne' />");
			break;
		case "10":
			sb.push(" fieldwidth='" + defaultWidth +  "' ");			
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='text' class='list_search_inputOne' />");
			break;
        case "11"://单选Radio 		
			sb.push(" showmodel='radio'  ismultiple='false' showall='true'>");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='radio' />"+i18n.option1+" <input type='radio' />"+i18n.option2);
			break;
        case "12"://单选Radio ，动态取值		
			sb.push(" showmodel='radio'  ismultiple='false' showall='true'>");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='radio' />"+i18n.option1+" <input type='radio' />"+i18n.option2);
			break;
        case "13":   //多选,动态取值
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" fieldheight='" + defaultHeight + "' showmodel='select'  ismultiple='true' showall='true'");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<select multiple='multiple' class='list_search_selectOne' style='height:" + defaultHeight + "px;'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
			break;
        case "14"://多选 复选框 checkbox
			sb.push(" showmodel='checkbox'  ismultiple='true' showall='true'>");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='checkbox' />"+i18n.option1+" <input type='checkbox' />"+i18n.option2);
			break;
        case "15"://多选 复选框 checkbox ，动态取值		
			sb.push(" showmodel='checkbox'  ismultiple='true' showall='true'>");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='checkbox' />"+i18n.option1+" <input type='checkbox' />"+i18n.option2);
			break;
        case "16":   //分类（下拉框select，只可用于单层分类）		
			sb.push(" fieldwidth='" + defaultWidth +  "' showmodel='select'  ismultiple='false' showall='true'");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<select  class='list_search_selectOne'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
			break;
        case "17":   //其它数据（下拉框，动态取值，键值对）		
			sb.push(" fieldwidth='" + defaultWidth +  "' showmodel='select'  ismultiple='false' showall='true'");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<select  class='list_search_selectOne'><option>" + i18n.option1 + "</option><option>" + i18n.option2 + "</option></select>");
			break;
		case "21":  //任意填写（多行）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" fieldheight='" + defaultHeight + "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<textarea class='list_search_inputOne' style=';height:" + defaultHeight + "px;' ></textarea>");
			break;
		case "24":  //电子邮件（专用文本，自动进行格式验证）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input  type='text' value='' class='list_search_inputOne' />");
			break;
		case "25":  //固定电话（专用文本，自动进行格式验证）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input   type='text' value='' class='list_search_inputOne' />");
			break;
		case "26":  //手机（专用文本，自动进行格式验证）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input type='text' value='' class='list_search_inputOne' />");
			break;
		case "27":  //地址拆分（分开填写方式，分为：省,市,区/县,街道,楼号）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			//sb.push("<input readonly='readonly' style='width:"+defaultWidth+"px;' type='text' value='  "+i18n.province+i18n.city+ " ' />");
			
			sb.push("<div class='custform-from-wrap'>")
			sb.push("<input readonly='readonly' class='list_search_inputOne' type='text' value='' />" + i18n.province + "  <br class='clear'/>");
			sb.push("<input class='addrplace' readonly='readonly' class='list_search_inputOne' type='text' value='' />" + i18n.city + "  <br class='clear' />");
			sb.push("<input class='addrplace' readonly='readonly' class='list_search_inputOne' type='text' value='' />" + i18n.area + " <br class='clear'/>");
			sb.push("<input class='addrplace' readonly='readonly' class='list_search_inputOne' type='text' value='' />" + i18n.street + "  <br class='clear'/>");
			sb.push("<input class='addrplace' readonly='readonly' class='list_search_inputOne' type='text' value='' />" + i18n.building + "");
			sb.push("</div>")
			break;
		case "28":  //日期拆分（分开填写方式，分为：年,月,日）
			sb.push(" fieldwidth='" + defaultWidth +  "' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input readonly='readonly' class='list_search_inputOne' type='text' value='" + i18n.demodate + "' />");
			break;
		case "29":  //部门（部门树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='false' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input   type='text' value='' class='list_search_input43' />");
			sb.push("<input class='button list_search_button41'  type='button' value='" + i18n.selectdept + "' />");
			break;
		case "30":  //用户（用户树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='false' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input  type='text' value='' class='list_search_input43' />");
			sb.push("<input class='button list_search_button41'  type='button' value='" + i18n.selectusers + "' />");
			break;
		case "6":   //分类（分类树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='false' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input  type='text' value='' class='list_search_input43' />");
			sb.push("<input  class='button list_search_button41' type='button' value='" + i18n.selectcatgory + "' />");
			break;
		case "33":  //分类（分类树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='true' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input  type='text' value='' class='list_search_input43' />");
			sb.push("<input  class='button list_search_button41' type='button' value='" + i18n.selectcatgory + "' />");
			break;
		case "34":  //部门（部门树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='true' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input multiple='multiple'  type='text' value='' class='list_search_input43' />");
			sb.push("<input  class='button list_search_button41' type='button' value='" + i18n.selectdept + "' />");
			break;
		case "35":  //用户（用户树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='true' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input multiple='multiple'  type='text' value='' class='list_search_input43' />");
			sb.push("<input class='button list_search_button41'  type='button' value='" + i18n.selectusers + "' />");
			break;
		case "36":  //角色（角色树）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='false' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input multiple='multiple'  type='text' value='' class='list_search_input43' />");
			sb.push("<input class='button list_search_button41'  type='button' value='" + i18n.selectrole + "' />");
			break;
		case "37":  //角色（角色树，可多选）
			sb.push(" fieldwidth='" + defaultWidth +  "' ismultiple='true' ");
			sb.push(" >");
			sb.push("<label class='list_search_label'>" + sysfieldName + ":</label>");
			sb.push("<input multiple='multiple'  type='text' value='' class='list_search_input43' />");
			sb.push("<input class='button list_search_button41'  type='button' value='" + i18n.selectrole + "' />");
			break;

		case "-4":  //请插入文本
			
			var nNumInsert = 0;
			if(tbc.data("inertNum")!=null&&tbc.data("inertNum").toString().length>0){
				
				nNumInsert = parseInt(tbc.data("inertNum").toString(),10);
				nNumInsert= nNumInsert+1;
			}
			
			tbc.data("inertNum",nNumInsert.toString());
			sb.push("<" + eletag + " fieldtype='" + fieldType + "' name='inserttxt' id='inserttxt_" + nNumInsert + "' class='custom-field'>" + i18n.pleaseinserttext);
		   
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
	}
	return sb.join('');
}

 
function exportJsp(){
	$("#exportFrame").attr(
			"src",
			"QueryFormController.do?action=exportToJsp&formID="+$.query.get("formID"));
}
function resizeHandle(){
	var bodyH = $("body").data("resize-special-event").h,
		fieldContainer = $(".field-container"),
		menu = $(".menu"),
		mainContent = $(".mainContent"),
		header = $(".header"),
		sidebarTitle = $(".sidebar h2"),
		searchField = $(".search-field"),
        doctypeField = $(".docType-field"),
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
			
            -doctypeField.outerHeight(true),
		workspaceContentH = mainContentH
			- workspaceTitle.outerHeight();
		mainContent.height(mainContentH);
		listfield.height(listfieldH);
		workspaceContent.height(workspaceContentH);
}