var slSortfield,
	//slSortfieldWay,
    txtfsort,
	columnList,
	columnListWrap,
	fieldArea,
	fieldArrow,
	positionIndex = 0,
	up,
	down,
	fields,
	fieldsTabs,
	txtfieldWidth,
	txtfieldName,
    AllSortFieldsList,
    SortFieldsList,
	colsort;

$(function() {

    //定义元素
    txtfsort = $("#txt-field-sort");
    slSortfield = $("#sl-sortfield");
	//slSortfieldWay = $("#sl-sortfield-way");
	columnList     = $("#column-list");
	columnListWrap = $("#column-list-wrap");
	fieldAttrArea  = $("#field-attr-area").css({
		left : $("#draphelp").offset().left,
		top  : columnList.offset().top + 60
	});
	fieldArrow = $("#field-attr-arrow");
	fields     = $("#field-attr-fields");
	fieldsTabs = $("#field-attr-fields-tabs");
	AllSortFieldsList = $("#AllSortFieldsList");
	SortFieldsList = $("#SortFieldsList");
	colsort = $("#sl-column-sortfield");

	$("#btnFieldSort").click(function () {
	   
	    initSortFields();
	});
	//选择图标
	e5.dialog('', {
		title: i18n.selecticon,
		id: "iconview",
		ishide:true
	}).DOM.content.append($("#icon-list"));

	//设置按钮事件
	$("#btn-complete").click(function() {
		saveList();
	});
	$("#btn-cancle").click(function() {
		parent.parentUpdate($.query.get("listID"));
	});
	$("#btn-pre").click(function(){
		var params = {
				"docTypeID"  : $.query.get("docTypeID"),
				"docTypeName": $.query.get("docTypeName"),
				"listID"     : $.query.get("listID"),
				"listName"   : $.query.get("listName"),
				"slshowID"   : $.query.get("slshowID"),
				"slshowName" : $.query.get("slshowName"),
				"hasXLS"     : $.query.get("hasXLS"),
				"xlsPath"    : $.query.get("xlsPath")
			},
		url = "ListPageCustomList.jsp?" + $.param(params);
		document.location.href = url;
	});

	up = $("#up").click(moveColumnList);
	down = $("#down").click(moveColumnList);

	txtfieldWidth = $("#txt-field-width").blur(function(){
		var p1 = /^[0-9]*[1-9][0-9]*$/;
		if(!e5.utils.regExpTest(p1,this.value)){
			alert(i18n.widthnumber);
			$(this).focus();
			return;
		}
		var w = parseInt(this.value);
		if(isNaN(w)){
			return;
		}
		columnList.find(".field-selected").width(w);
		setColunmWith();
		btnShow();
	});

	txtfieldName = $("#txt-field-name").blur(function(){
		var t = this.value;
		var p1 = /^[^&<>'"!]+$/i;
		if(!e5.utils.regExpTest(p1,this.value)){
			alert(i18n.charnumberchiness);
			$(this).focus();
			return;
		}
		columnList.find(".field-selected .th").html(t);
	});

	$("#txt-showcontent").blur(function(){
		var t = this.value;
		var p1 = /^[^&<>'"!]+$/i;
		if(!e5.utils.regExpTest(p1,this.value)){
			alert(i18n.charnumberchiness);
			$(this).focus();
			return;
		}
	});

	//设置文档可以改变大小
	$(document.body).bind("resize",resizeHandle);

	// 展开--收起高级选项程序
	foldAdvancedOptionHandle();

	
	//获取文档类型字段
	$.ajax({
		url: "../e5listpage/cust_typeReader.do?docType=" + $.query.get("docTypeID")+"&withext=1",
		async: false,
		success: function(data) {
			if (data != null) {
				var docs = $.xml2json(data),
					lifiles = "",
					datas,
					search,
					fieldList = $("#field-list");
				if (docs != null) {
					datas = [];
					search = $("#txt-field-search");

					if (!$.isArray(docs.field)) {
						datas.push(docs.field);
					} else {
						datas = docs.field;
					}

					search.data("fields",datas);

					$.each(datas, function (i,f) {
						lifiles = lifiles + "<li class='field-li' id='" + f.id + "' title='" + f.code + "' name='" + f.code + "'>" + f.name + "</li>";
					});

					//自动完成开始
					search.autocomplete(datas, {
						minChars: 0,
						width: 1,
						matchContains: true,
						autoFill: false,
						formatItem: function(row, i, max) {
							//隐藏下拉框
							$(".ac_results").remove();
							if (!search.val()) {
								fieldList.empty();
								$.each(datas, function(i, n) {
									fieldList.append("<li class='field-li' id='" + n.id + "' title='" + n.code + "' name='" + n.code + "'>" + n.name + "</li>");
								});
								//设置字段可以拖动
								setFieldsDragable();
								return false;
							}
							if (max > 0) {
								//清除所有的字段
								if (i == 1) {
									fieldList.empty();
								}
								fieldList.append("<li class='field-li' id='" + row.id + "' title='" + row.code + "' name='" + row.code + "'>" + row.name + "</li>");
								//设置字段可以拖动
								setFieldsDragable();
							}
							return false;
						},
						formatMatch: function(row, i, max) {
							return row.name + " " + row.code;
						},
						formatResult: function(row) {
							//return row.code;
						}

					});
					//自动完成结束
				}

				fieldList.append(lifiles);
				//禁止选中
				fieldList.disableSelection();

				//开始加载已有数据
				$.ajax({
					url: "../e5listpage/ListSubmit.do?method=getlistpageinfo&id=" + $.query.get("listID")+"&showtypeid="+$.query.get("slshowID"),
					dataType: "json",
					async: false,
					success: function(data) {
						if (data != null) {
							if(data.fields!=null){
								var tempData;
								$.each(data.fields,function(nindex,n2){
									if(n2.code == n2.name){
										data.fields[nindex].name = getFieldNameByCode(n2.code);
									}
								});
							}
							setUpdateData(data);
						}
					}
				});
				//开始加载已有数据结束

			}
		}
	});

	// end 获取字段类型

	$("#draphelp").droppable({
		accept: "#field-list li",
		activeClass: "visual-draphelp-hover",
		hoverClass: "visual-draphelp-active",
		over: function(event, ui) {

		},
		drop: function(event, ui) {
			drapablefield(event, ui);
		}

	});


	//end droppable
	var sortableCur;

	columnList.sortable({
		placeholder: "ui-state-highlight",
		axis:'x',
		start: function(event,ui){
			ui.placeholder.css({
				width :ui.helper.width(),
				height:ui.helper.height()
			});
			if(getLock(columnList)){
				sortableCur = columnList.find(".field-selected").click();
			}
		},
		stop: function(){
			if(sortableCur){
				sortableCur.click();
				sortableCur = undefined;
			}
		}
	});

	columnList.disableSelection();
	//设置字段可以拖动
	setFieldsDragable();

	//$("#fielddraphelp").droppable({
    $("#fielddraphelp").droppable({
		accept: "#field-list li",
		activeClass: "field-attr-draphelp-hover",
		hoverClass: "field-attr-draphelp-active",
		drop: function(event, ui) {
			//0.先查找当前被选中的列
			var column = columnList.find(".field-selected"),
				elmName = ui.draggable.attr("name"),
				elmHtml = ui.draggable.html(),
				columnData,
				i;
			if (column[0]){
				//不能拖动相同的列
				if (columnList.find("#column-" + elmName)[0]){
					return false;
				}
				//1.先判断是否已近存在该列
				columnData = column.data("field-data");
				if (columnData != null && columnData.length > 0) {
					for (i = 0; i < columnData.length; i++) {
						if (columnData[i].code == elmName) {
							return false;
						}
					}
				}
				//2.把数据存储到列数据里
				column.data("field-data").push({
					code: elmName,
					name: elmHtml
				});
				//3.增加字段显示
				appendfield(elmName, elmHtml);
				//4.添加排序字段
				setSortfields(elmName, elmHtml);
				//5.添加列排序字段
				setColumnSortfields(elmName, elmHtml);
				//6.添加字段显示
				var oldText = $("#txt-showcontent").val();
				//$("#txt-showcontent").val(oldText + "%" + elmName + "%");
				$("#txt-showcontent").val(oldText + "[" + elmHtml + "]");
				saveAttrs(column);
			}

		}
	});
	
	resizeHandle();
	//字段拖动结束
});
function setSortFieldsListSrotable() {
    /// <summary>设置可以拖动排序</summary>


    SortFieldsList.sortable({
        placeholder: "ui-state-highlight",
        axis: 'y',
        start: function (event, ui) {
            ui.placeholder.css({
                width: ui.helper.width(),
                height: ui.helper.height()
            });

        },
        stop: function () {

        }
    });

}
 
function initSortFields() {
    /// <summary>初始化表格的字段排序</summary>
    //找到所有的排序字段
    AllSortFieldsList.empty();
    SortFieldsList.empty();
    synDisplaySortField();
    //初始化排序字段
    var sData = slSortfield.data("sortdata");
    if (sData != null && sData.length>0) {

        $.each(sData, function (i, n) {

            //var fsd = { "name": n.name, "code": n.code, "sort": 1 };
             
            var swaytext = (n.sort.toString() == "1") ? i18n.sfdesc : i18n.sfasc;
            var swayclass = (n.sort.toString() == "1") ? "sort_li_desc" : "sort_li_asc";

            var stempname = n.name;
            if(n.name == n.code){
            	stempname = getFieldNameByCode(n.code);
            }
            var lsi = $("<li><div title=\"" + i18n.sfsortway + "\" class=\"sort_li_right "+swayclass+"\">" + swaytext + "</div><div class=\"sort_li_left\">" + stempname + "</div></li>");
            lsi.data("fsd", n);
            lsi.attr("title", i18n.sfstitle);
            lsi.attr("id", "s_" + n.code);
            SortFieldsList.append(lsi);
            lsi.find(".sort_li_right").click(function () {

                sortWayChange($(this));
            });
        });
    }
    setSortFieldsListSrotable();
    slSortfield.children("option").each(function (i, o) {
        //排除已经在排序列表中的字段
        if ($("#s_" + o.value).length == 0) {

            var li = $("<li>" + o.text + "</li>");
            var fd = { "name": o.text, "code": o.value };
            li.data("fd", fd);
            li.attr("title", i18n.sdbsort);
            li.attr("id", "a_" + o.value);
            AllSortFieldsList.append(li);
        }

    });


    //设置双击事件
    AllSortFieldsList.find("li").live("dblclick",function () {
        //获取
        var li = $(this);
        var ldata = li.data("fd");
        if (ldata != null) {
            //默认为降序:1,升序:0
            var fsd = { "name": ldata.name, "code": ldata.code, "sort": 1 };
            var lsi = $("<li><div title=\""+i18n.sfsortway+"\" class=\"sort_li_right sort_li_desc\">"+i18n.sfdesc+"</div><div class=\"sort_li_left\">" + ldata.name + "</div></li>");
            lsi.data("fsd", fsd);
            lsi.attr("title", i18n.sfstitle);
            lsi.attr("id", "s_" + ldata.code);
            SortFieldsList.append(lsi);
            lsi.find(".sort_li_right").click(function () {

                sortWayChange($(this));
            });
            //删除自己
            li.remove();
        }
    });

    var left = $("#btnFieldSort").position().left-16;
    var top = $("#btnFieldSort").position().top+30;
    $(".sort_container").css({ "left": left, "top": top });
    $(".sort_container").show();
    $("#btnSortOK").click(function () {
        //保存数据
        var sortData = new Array();
        
        SortFieldsList.find("li").each(function (i, n) {

            if ($(this).data("fsd") != null) {
                sortData.push($(this).data("fsd"));
            }
        });
        //保存数据
        slSortfield.data("sortdata", sortData);
        synDisplaySortField();
        $(".sort_container").hide();
    });
    $("#btnSortCancel").click(function () {

        $(".sort_container").hide();
    });

    SortFieldsList.find(".sort_li_left").live("dblclick", function () {
        //添加到右边
        var li = $(this).parent();
        var fsd = li.data("fsd");
        if (fsd != null) {
            if ($("#a_" + fsd.code).length == 0) {
                var fd = { "name": fsd.name, "code": fsd.code };
                var nli = $("<li>" + fsd.name + "</li>");
                nli.data("fd", fd);
                nli.attr("title", i18n.sdbsort);
                nli.attr("id", "a_" + fsd.code);
                AllSortFieldsList.append(nli);

                //移除
                li.remove();
            }
        }
    });
}

function sortWayChange(jobj) {
    /// <summary>实现点击排序功能</summary>
    /// <param name="jobj" type="jquery object">event</param>
    jobj.attr("title", i18n.sfsortway);
    var sort = "1";
    if (jobj.hasClass("sort_li_asc")) {
        jobj.removeClass("sort_li_asc");
        jobj.addClass("sort_li_desc");
        jobj.html(i18n.sfdesc);
        sort = "1";
    }
    else {
        jobj.removeClass("sort_li_desc");
        jobj.addClass("sort_li_asc");
        jobj.html(i18n.sfasc);
        sort = "0";
    }
    //修改数据的值
    var pli = jobj.parent();
    var odata = pli.data("fsd");
    var pfsd = { "name": odata.name, "code": odata.code, "sort": sort };
    pli.removeData("fsd");
    pli.data("fsd", pfsd);
}

function drapablefield(event, ui) {
	/// <summary>实现拖动功能</summary>
	/// <param name="event" type="event">event</param>
	/// <param name="ui" type="OBJECT">拖动元素</param>
	var elm = ui ? ui.draggable : $(this),
		elmHtml = elm.html(),
		elmName = elm.attr("name"),
		elmId = "#column-" + elmName;
	//不能拖动相同的列
	if (columnList.has(elmId).length > 0) {
		return false;
	}
	//保存上一列数据
	if (columnList.children("li").length > 0) {
		//保存属性
		var precol = columnList.children(".field-selected");
		if (precol[0]) {
			saveAttrs(precol);
		}
	}
	var fieldli = $("<li class='ui-state-default ui-widget-header' title='" + elmHtml + "' name='" + elmName + "'  id='" + elmId.substr(1) + "'>" + "<div class=\"th\">" + elmHtml + "</div>" + "<div class=\"td\"><div class=\"text\"></div></div>" + "</li>");
	columnListWrap.addClass("ui-state-highlight");
	columnList.append(fieldli);
	//设置容器宽度
	setColunmWith();
	//存储数据
	var fielddata = [];
	fielddata.push({
		code: elmName,
		name: elmHtml
	});

	fieldli.data("field-data", fielddata);
	//增加选中样式
	//移除被选中样式
	columnList.find("li").each(function() {
		$(this).removeClass("field-selected");
	});
	fieldli.addClass("field-selected");
	//添加排序字段
	setSortfields(elmName, elmHtml);
	//添加列排序字段
	setColumnSortfields(elmName, elmHtml);

	//设置删除功能
	fieldli.append("<span id=\"field-delete-icon" + fieldli.attr("name") + "\" class=\"field-delete-icon\">[X]</span>");
	var delIcon = $("#field-delete-icon" + fieldli.attr("name")).attr("title",i18n.closetip);

	delIcon.click(function() {
		
		if (confirm(i18n.confirmok)) {
			//先保存当前值
			saveAttrs(fieldli);
			
			delCol(fieldli.attr("name"));
			if (columnList.find("li").length == 0) {
				columnListWrap.removeClass("ui-state-highlight");
			}

			setColunmWith();
			btnShow();
		}
		
	});

	//设置点击事件
	fieldli.click(function(event) {
		if(event.target.className.indexOf("ui-resizable-e") !== -1){
			return false;
		}
		var elm = $(this),
			oldcol = columnList.children(".field-selected");
		
			//保存属性

		if (oldcol[0]) {
			//移除被选中样式
			oldcol.removeClass("field-selected");
			saveAttrs(oldcol);
		}

		//如果被选中，再点击要保存上次的值
		if(this !== oldcol[0]){
			elm.addClass("field-selected");
			lock(columnList);
		}else{
			unlock(columnList);
		}
		//移除列排序字段
		resetColumnSortfields(elmName);
		//设置属性
		setAttrs(elm);
	
		//计算并移动字段属性上方的箭头箭头
		arrowPosition();
		//移动字段可视化操作区域
		moveColumnList.call(this);
	});

	//增加字段显示
	addfield(elmName, elmHtml);

	//设置属性
	setAttrs(fieldli);

	//调整大小
	setColsResize();
	//移动可是化编辑区域
	moveColumnList.call(fieldli[0]);
	//移动属性区域箭头
	arrowPosition();
	//锁定可是化编辑区域禁止拖动排序
	lock(columnList);
	//显示按钮
	btnShow();
}

function setFieldsDragable() {
	/// <summary>设置字段可以拖动</summary>
	$("#field-list li").draggable({
		helper: "clone",
		cursor: "move"
	}).dblclick(drapablefield);
}

function setColunmWith() {
	/// <summary>该方法主要是来设置列容器的宽度，在拖放和调整列宽度时调用</summary>
	var liborder = 1, //列的左右border值
		widthSum = 0; //所有列宽度的总合
	
	columnList.find("li").each(function(i) {
		widthSum = widthSum + $(this).outerWidth(true);
	});
	
	columnList.width(widthSum+10);
	
}

function saveList() {
	/// <summary>保存列表数据</summary>
	//验证每页记录数
	var numbers = $("#txt-field-pagenum").val();
	var validateNum = true;

	if(numbers!=null&&numbers!=""){
		
		var num = [];
		num = numbers.split(',');
		if(num.length>0){
			for(i=0;i<num.length;i++){
				var regx = /^[0-9]*[1-9][0-9]*$/;
				if(!regx.test(num[i])){
					validateNum = false;
				}
			}
		}
		else{
			validateNum = false;
		}
	}
	else{
		validateNum = false;
	}
	
	if(!validateNum){
		alert(i18n.numerror);
		return;
	}
	//1.保存最后一个被选中列的数据
	var oldcol = columnList.children(".field-selected");
	if (oldcol.length > 0) {
		saveAttrs(oldcol);
	}

	//2.组装xml数据
	var sb = [];

	sb.push("<?xml version='1.0' encoding='UTF-8'?>");
	sb.push("<listpage>");
    var pathxsl = "";
    if($.query.get("hasXLS")=="1"){
    	pathxsl = $.query.get("xlsPath");
    }
    
	//listpage属性开始
	sb.push("<doctypename>" + $.query.get("docTypeName") + "</doctypename>");
	sb.push("<doctypeid>" + $.query.get("docTypeID") + "</doctypeid>");
	sb.push("<name>" + $.query.get("listName") + "</name>");
	sb.push("<id>" + $.query.get("listID") + "</id>");
	sb.push("<rendertype>" + $.query.get("slshowID") + "</rendertype>");
	sb.push("<hasxsl>" + $.query.get("hasXLS") + "</hasxsl>");
	sb.push("<pathxsl><![CDATA[" + pathxsl + "]]></pathxsl>");
	//listpage属性结束

	sb.push("<root>");

	//fields开始
	sb.push("<fields>");
	//遍历所有用到的字段
	slSortfield.children("option").each(function(i, o) {
		sb.push("<field>");
		sb.push("<name>" + o.value + "</name>");
		sb.push("<title>" + o.text + "</title>");
		sb.push("<status>1</status>");
		sb.push("</field>");

	});
	sb.push("</fields>");
	//fields结束
	//排序开始
	sb.push("<sort>");
	
	
	var sData = null;
	var fstr = new Array(); //<fields></fields>字符串
	var tstr = new Array(); //排序字符串
    //有排序
	sData = slSortfield.data("sortdata");
	if (sData != null&&sData.length > 0) {
	    $.each(sData, function (i, n) {

	        fstr.push(n.code);
	        tstr.push(n.sort);

	        if (i < sData.length-1) {
	            tstr.push(",");
	            fstr.push(",");
	        }

	    });
	}
	sb.push("<fields>" + fstr.join('') + "</fields>");
	sb.push("<types>" + tstr.join('') + "</types>");
	sb.push("</sort>");
	//排序结束
	//xsl 开始
	sb.push("<xsl>");
	//找到每个列的数据
	columnList.find("li").each(function(j, l) {
		var fieldDataSer = $(this).data("field-col-data");
		if (fieldDataSer != null) {
			$.each(fieldDataSer, function(o, c) {
				sb.push("<field>");
				if(c.type=="undefined"){
					c.type = "1";
				}
				sb.push("<type>" + c.type + "</type>");
				sb.push("<title>" + c.title + "</title>");
				sb.push("<name>" + c.name + "</name>");
				if (c.type == 1) {

					sb.push("<tooltip>" + c.tooltip + "</tooltip>");
				} else if (c.type == 2) {

					sb.push("<enums>");
					if (c.decide != null && c.decide.length > 0) {
						$.each(c.decide, function(m, d) {

							sb.push("<enum>");
						    var op = "0";
						    if(d.op){
						    	op = d.op;
						    }
							sb.push("<key>" + d.key + "</key>");
							sb.push("<value>" + d.value + "</value>");
							sb.push("<icon><![CDATA[" + abstorelativeIcon(d.icon) + "]]></icon>");
							
							sb.push("<op>" + op + "</op>");

							sb.push("</enum>");
						});

					}
					sb.push("</enums>");
				} else if (c.type == 3) {

                    var newContent = convertName2Code(fieldDataSer,c.content);
					sb.push("<content><![CDATA[" + newContent + "]]></content>");
				}
				sb.push("</field>");
			});

		}
	});

	sb.push("</xsl>");
	//xsl 结束
	sb.push("</root>");

	// custom开始
	sb.push("<custom>");

	var auto_check = ($("#cb-checkbox").attr("checked") == "checked") ? "1" : "0";
	var auto_seq = ($("#cb-lineindex").attr("checked") == "checked") ? "1" : "0";
	sb.push("<para>");
	sb.push("<auto_seq>" + auto_seq + "</auto_seq>");
	sb.push("<auto_check>" + auto_check + "</auto_check>");
	sb.push("<icon>" + abstorelativeIcon($.query.get("icon")) + "</icon>");
	sb.push("<change_line>1</change_line>");
	sb.push("<rows>"+$("#txt-field-pagenum").val()+"</rows>")
	sb.push("</para>");


	sb.push("<slice>");
	//循环设置每列的属性
	//找到每个列的数据
	if(columnList.find("li")){
		columnList.find("li").each(function(y, p) {
			var qc = $(this).data("field-attr-data");
			var cc = $(this).data("field-col-data");
			if (qc != null) {

				sb.push("<col>");
				sb.push("<name><![CDATA[" + qc.name + "]]></name>");
				sb.push("<head><![CDATA[" + qc.head + "]]></head>");
				sb.push("<width>" + qc.width + "</width>");
				sb.push("<wrap>" + qc.wrap + "</wrap>");
				sb.push("<class><![CDATA[" + qc.classname + "]]></class>");
				if(qc.sort=="-1"||qc.sort==null||qc.sort==""){
					sb.push("<sort></sort>");
				}
				else{
					sb.push("<sort>" + qc.sort + "</sort>");
				}
				var newTemp = convertName2Code(cc,qc.template);
				sb.push("<template><![CDATA[" + newTemp + "]]></template>");
				sb.push("</col>");
			}

		});
	}
	
	


	sb.push("</slice>");

	sb.push("</custom>");
	// custom结束
	sb.push("</listpage>");
	 
	//3.post xml格式的数据
	var dataxml = {
		listxml: sb.join('')
	}
	//alert(sb.join(''));
	//return;
	$.ajax({
		type: "POST",
		url: "../e5listpage/ListSubmit.do",
		data: dataxml,
		beforeSend: function() {
			//设置按钮不可用，防止重复提交
			$("#btn-complete").attr("disabled", true);
		},

		success: function(data, textStatus) {
			$("#btn-complete").attr("disabled", false);
			if (data == "OK") {
				alert(i18n.success);
				parent.parentUpdate($.query.get("listID"));

			} else {
				alert(i18n.faield);
			}
		},
		error: function() {
			//请求出错处理
			$("#btn-complete").attr("disabled", false);
			alert(i18n.faield);
		}

	}); 
}
function getFieldNameByCode(code){
	var falldatas = $("#txt-field-search").data("fields");
	var fieldname;
	if(falldatas!=null && falldatas.length>0){
		$.each(falldatas,function(index,p){
			if(p.code == code){
				fieldname = p.name;
			}
		});
	}
	return fieldname;
}
function setUpdateData(data) {

	if (data.fields != null) {
		$.each(data.fields, function(i, n) {
			$("<option/>").appendTo(slSortfield).val(n.code).html(n.name);
		});
	    //组装排序字段数据
		var gData = new Array();

		if (data.sortField != null && data.sortField.toString().length > 0&&
            data.sortWay != null && data.sortWay.toString().length>0) {

		    var fstr = new Array();
		    var tstr = new Array();
		    fstr = data.sortField.toString().split(',');
		    tstr = data.sortWay.toString().split(',');

		    $.each(fstr, function (i, n) {

		        //组装排序字段数据
		        var gname = "";
		        $.each(data.fields, function (d, p) {
		            if (p.code.toLowerCase() == fstr[i].toString().toLowerCase()) {
		                gname = p.name;
		            }
		        });

		        var gsd = { "name": gname, "code": fstr[i], "sort": tstr[i] };
		        gData.push(gsd);
		       
		    });
		}
        //保存排序字段数据
		slSortfield.data("sortdata", gData);
		synDisplaySortField();

		if (data.showChebox == "1") {
			$("#cb-checkbox").attr("checked", true);
		}
		if (data.showIndex == "1") {
			$("#cb-lineindex").attr("checked", true);
		}
		$("#txt-field-pagenum").val(data.rows);
		//如果为E5V3版本升级过来的columns=null
		if (data.columns.length ==0) {
			$.each(data.fields, function(p, k) {
				var fieldli,
					fielddata,
					fieldColDatas,
					delIcon,
					elmHtml = k.name,
					elmName = k.code,
					elmId = "column-" + k.code;
				fieldli = $("<li class='ui-state-default ui-widget-header' title='" + elmHtml + "' name='" + elmName + "'  id='" + elmId + "'>" + "<div class=\"th\">" + elmHtml + "</div>" + "<div class=\"td\"><div class=\"text\"></div></div>" + "</li>");
				columnListWrap.addClass("ui-state-highlight");
				columnList.append(fieldli);
				setColunmWith();
				fielddata = [];
				fielddata.push({
					code: k.code,
					name: k.name
				});
				
				fieldli.data("field-data", fielddata);
				columnList.find("li").each(function() {
					$(this).removeClass("field-selected");
				});
				fieldli.addClass("field-selected");

				fieldli.append("<span id=\"field-delete-icon" + fieldli.attr("name") + "\" class=\"field-delete-icon\">[X]</span>");
				
				delIcon = $("#field-delete-icon" + fieldli.attr("name"));
				
				delIcon.click(function() {
					if (confirm(i18n.confirmok)) {
						delCol(fieldli.attr("name"));
						if (columnList.find("li").length == 0) {
							columnListWrap.removeClass("ui-state-highlight");
						}
						setColunmWith();
						btnShow();
					}
				});


				fieldli.click(function(event) {
					if(event.target.className.indexOf("ui-resizable-e") !== -1){
						return false;
					}
					var elm = $(this),
						oldcol = columnList.children(".field-selected");
					   
						if (oldcol[0]) {
							oldcol.removeClass("field-selected");
							saveAttrs(oldcol);
						}
						if(this !== oldcol[0]){
							elm.addClass("field-selected");
							lock(columnList);
						}else{
							unlock(columnList);
						}
						resetColumnSortfields(elmName);
						setAttrs(elm);
						arrowPosition();
						moveColumnList.call(this);
				});
				
				 
				if (k.fieldList == null) {
					fieldColDatas = [];
					var colfielddatas = {
							name   : k.code,
							title  : k.name,
							type   : "1",
							tooltip: "0",
							decide : null,
							content: ""
						};
						fieldColDatas.push(colfielddatas);
					fieldli.data("field-col-data", fieldColDatas);
				}
				
				
				var newtemp = convertCode2Name(fieldColDatas,"%"+k.code+"%");
				var colData = {
					width    : "100",
					name     : k.code,
					head     : elmHtml,
					wrap     : "0",
					classname: "",
					sort     : k.code,
					template : newtemp
				};
				fieldli.data("field-attr-data", colData);
			
				
				setAttrs(fieldli);
				setColsResize();
				
				lock(columnList);
				btnShow();
			});
		}
		
		
		//
		if (data.columns.length>0) {
			$.each(data.columns, function(p, k) {
				var fieldli,
					fielddata,
					fieldColDatas,
					delIcon,
					elmHtml = k.head,
					elmName = k.name,
					elmId = "column-" + k.name;
				fieldli = $("<li class='ui-state-default ui-widget-header' title='" + elmHtml + "' name='" + elmName + "'  id='" + elmId + "'>" + "<div class=\"th\">" + elmHtml + "</div>" + "<div class=\"td\"><div class=\"text\"></div></div>" + "</li>");
				columnListWrap.addClass("ui-state-highlight");
				columnList.append(fieldli);
				setColunmWith();
				fielddata = [];
				if (k.fieldList != null) {
					$.each(k.fieldList, function(a, b) {
						
						if(b){
							fielddata.push({
								code: b.name,
								name: b.title
							});
						}
					});
				}
				fieldli.data("field-data", fielddata);
				columnList.find("li").each(function() {
					$(this).removeClass("field-selected");
				});
				fieldli.addClass("field-selected");

				fieldli.append("<span id=\"field-delete-icon" + fieldli.attr("name") + "\" class=\"field-delete-icon\">[X]</span>");
				
				delIcon = $("#field-delete-icon" + fieldli.attr("name"));
				
				delIcon.click(function() {
					if (confirm(i18n.confirmok)) {
						delCol(fieldli.attr("name"));
						if (columnList.find("li").length == 0) {
							columnListWrap.removeClass("ui-state-highlight");
						}
						setColunmWith();
						btnShow();
					}
				});


				fieldli.click(function(event) {
					if(event.target.className.indexOf("ui-resizable-e") !== -1){
						return false;
					}
					var elm = $(this),
						oldcol = columnList.children(".field-selected");
					   
						if (oldcol[0]) {
							oldcol.removeClass("field-selected");
							saveAttrs(oldcol);
						}
						if(this !== oldcol[0]){
							elm.addClass("field-selected");
							lock(columnList);
						}else{
							unlock(columnList);
						}
						resetColumnSortfields(elmName);
						setAttrs(elm);
						arrowPosition();
						moveColumnList.call(this);
				});
				
				 
				if (k.fieldList != null) {
					fieldColDatas = [];
					$.each(k.fieldList, function(o, d) {
						if(d){
							var uname    = "";
							var utitle   = "";
							var utype    = "";
							var utooltip = "";
							var udecide  = null;
							var ucontent = "";
							uname = d.name;
							utitle = d.title;
							utype = d.type;
							if (d.type == 1) {
								utooltip = d.tooltip;
							} else if (d.type == 2) {
								udecide = d.enumslist;
							} else if (d.type == 3) {
								ucontent = d.content;
							 
							}
							var colfielddatas = {
								name   : uname,
								title  : utitle,
								type   : utype,
								tooltip: utooltip,
								decide : udecide,
								content: ucontent
							};
							fieldColDatas.push(colfielddatas);
						}
					});
					fieldli.data("field-col-data", fieldColDatas);
				}
				//将type=3的类型中的编码转换成名称
				var newFieldColDatas = [];
				
				$.each(fieldColDatas,function(t,y){
					
					if(y.type==3){
						y.content = convertCode2Name(fieldColDatas,y.content);
					}
					newFieldColDatas.push(y);
					
					
				});
				fieldli.data("field-col-data", newFieldColDatas);
				
				var newtemp = convertCode2Name(fieldColDatas,k.template);
				var colData = {
					width    : k.width,
					name     : elmName,
					head     : elmHtml,
					wrap     : k.warp,
					classname: k.classname,
					sort     : k.sort,
					template : newtemp
				};
				fieldli.data("field-attr-data", colData);
			
				
				setAttrs(fieldli);
				setColsResize();
				
				lock(columnList);
				btnShow();
			});
		}
		if (data.columns.length>0) {
			
		}
		columnList.find(":first").click();
	}
}

function delCol(code) {
	/// <summary>删除一列</summary>
	var col = $("#column-" + code);
	//删除所有包含列
	if (col.data("field-col-data") != null) {
		$.each(col.data("field-col-data"), function(i, n) {
		    slSortfield.find("option[value='" + n.name + "']").remove();
		    synSortField(n.name);
		});
	}
	if(col.hasClass("field-selected")){
		fieldAttrArea.hide();
		unlock(columnList);
	}
	col.remove();
}

function synSortField(code) {
    /// <summary>同步排序字段</summary>
    var sData = slSortfield.data("sortdata");
    
    if (sData != null && sData.length > 0) {

        var nData = new Array();
        $.each(sData, function (i, n) {

            if (n.code.toString() != code.toString()) {
                nData.push(n);
            }
        });
        slSortfield.removeData("sortdata");
        slSortfield.data("sortdata",nData);
    }
    synDisplaySortField();
}
function synDisplaySortField() {
    /// <summary>同步显示排序字段</summary>
    var sData = slSortfield.data("sortdata");
    var sfstr = new Array();
    if (sData != null && sData.length > 0) {

        $.each(sData, function (i, n) {
            var sw = (n.sort.toString() == "1") ? i18n.sfdesc : i18n.sfasc;
            sfstr.push(n.name + ":" + sw);
            if (i < sData.length - 1) {
                sfstr.push(",");
            }
        });
    }

    txtfsort.val(sfstr.join(''));
    txtfsort.attr("title",sfstr.join(''));
}

function setColsResize() {
	/// <summary>设置拖动</summary>
	var resizeCur;
	columnList.find("li").resizable({
		handles: "e",
		start: function(){
			if(getLock(columnList)){
				resizeCur = columnList.find(".field-selected").click();
			}
		},
		resize: function(event, ui) {
		
			var sizeToolTip = $(".size-tooltip-container");
			sizeToolTip.show();
			sizeToolTip.css({
				top: ui.element.offset().top - sizeToolTip.height() -2,
				left: ui.element.offset().left,
				width: ui.element.outerWidth() - 2
			});


			$(".size-tooltip-text").html(ui.size.width-2 + "px");
			txtfieldWidth.val(ui.size.width);
			setColunmWith();
			arrowPosition();
		},
		stop: function(event, ui) {
			//设置容器宽度
			//设置列的宽度
			if ($(this).data("field-attr-data") != null) {

				$(this).data("field-attr-data").width = ui.size.width-2;
				setColunmWith();
				btnShow();
			} else {
				saveAttrs($(this));
			}

			$(".size-tooltip-container").hide();
			if(resizeCur){
				resizeCur.click();
				resizeCur = undefined;
			}
		}
	});
}

function saveAttrs(obj) {
	/// <summary>保存属性内存</summary>
	/// <param name="obj" type="Object">当前列jquery 对象</param>
	var iswrap = ($("#cb-field-nowrap").attr("checked") == "checked") ? "1" : "0";
	//1.保存字段的内容
	var colData = {
		width    : txtfieldWidth.val(),
		name     : $("#txt-field-code").html(),
		head     : $("#txt-field-name").val(),
		wrap     : iswrap,
		classname: $("#txt-field-class").val(),
		sort     : colsort.val(),
		template : $("#txt-showcontent").val()
	};
	//设置列属性数据
	obj.data("field-attr-data", colData);

	var fieldColData = [];


	$.each(obj.data("field-data"), function(i, n) {

		//1.找到字段类型
		var fieldTyped = $("#sl-showtype-" + n.code).val();
		var showtooltip = "0";
		var enums = [];

		var content = "";

		if (fieldTyped == 1) {
			//直接显示
			showtooltip = ($("#tooltip-" + n.code).attr("checked") == "checked") ? "1" : "0";
			enums = null;
		} else if (fieldTyped == 2) {
			//判断显示
			$("#ul-decide-" + n.code).find("li").each(function(i, d) {
				var imgurl = $(this).find("img").attr("src");
				if(imgurl.toString().indexOf("decied_default1")>0){
					imgurl = "";
				}
				var decideType = "0";
				if($(this).attr("name")){
					decideType = $(this).attr("name");
				}
				
				var enumrow = {
					key: $(this).find("input[name='decide-key']").val(),
					value: $(this).find("input[name='decide-value']").val(),
					icon: imgurl,
					op:decideType
				};
				enums.push(enumrow);
			});
		} else if (fieldTyped == 3) {
			//自定义显示
			content = $("#txt-customfield-" + n.code).val();
			enums = null;
		}

		var colfielddata = {
			name   : n.code,
			title  : n.name,
			type   : fieldTyped,
			tooltip: showtooltip,
			decide : enums,
			content: content
		};

		fieldColData.push(colfielddata);
	});

	//设置列属性数据
	obj.data("field-col-data", fieldColData);

}

function setAttrs(obj) {
	/// <summary>设置属性</summary>
	/// <param name="obj" type="Object">列</param>
	//1.设置列属性
	var colData = obj.data("field-attr-data");
	if (colData != null) {
		txtfieldWidth.val(colData.width);
		//设置宽度
		obj.width(colData.width + "px");
		$("#txt-field-code").html(colData.name);
		$("#txt-field-name").val(colData.head);
		var sortDefault = "-1";
		resetColumnSortfields(obj.attr("name"));
		if(colData.sort!=null&&colData.sort!=""){
			sortDefault = colData.sort;
		}

		colsort.val(sortDefault);
		
		$("#txt-showcontent").val(colData.template);
		if (colData.wrap == "0") {
			$("#cb-field-nowrap").attr("checked",false);
		}
		if (colData.wrap == "1") {
			$("#cb-field-nowrap").attr("checked",true);
		}
		$("#txt-field-class").val(colData.classname);
		

	} else {
		//设置默认值
		txtfieldWidth.val("100");
		obj.width("100px");
		$("#txt-field-code").html(obj.attr("name"));
		$("#txt-field-name").val(obj.attr("title"));
		$("#cb-field-nowrap").attr("checked",false);
		$("#txt-field-class").val("");
		$("#txt-showcontent").val("[" + obj.attr("title") + "]");
		resetColumnSortfields(obj.attr("name"));
		
		//colsort.val(obj.attr("name"));
	}
	setColunmWith();
	//2.设置字段
	var fcdata = obj.data("field-col-data");

	if (fcdata != null) {
		//先清空内容
		/*$("#field-attr-fields")*/fields.empty();
		fieldsTabs.empty();
		$.each(fcdata, function(i, n) {
			if (n.type == 1) {
				//1.增加字段
				appendfieldType(n.name, n.title, n.type,i);
				//2.设置字段
				 
				$("#sl-showtype-" + n.name).attr("value", n.type);
				if (n.tooltip == "0") {
					$("#tooltip-" + n.name).removeAttr("checked");
				}
				if (n.tooltip == "1") {
					$("#tooltip-" + n.name).attr("checked", "checked");
				}
			} else if (n.type == 2) {
				//1.增加字段
				appendfieldType(n.name, n.title, n.type,i);
				//2.设置字段
				 
				$("#sl-showtype-" + n.name).attr("value", n.type);
				//增加判断
				if (n.decide != null) {
					setDecides(n.name, n.decide);
				}

			} else if (n.type == 3) {
				//1.增加字段
				appendfieldType(n.name, n.title, n.type,i);
				//2.设置字段
				 
				$("#sl-showtype-" + n.name).attr("value", n.type);
				$("#txt-customfield-" + n.name).val(n.content);
			}
		});

	}
}

function addfield(code, name) {
	/// <summary>增加一个字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	
	//先清空内容
	fields.empty();
	fieldsTabs.empty();
	
	$("<div/>").appendTo(fields)
		.addClass("fields-attr-container clearfix")
		.attr('id','field-container-' + code)
		.append(addfieldType1(code, name));

	addfieldTab(code, name, true).addClass("cur fst");
}

function appendfield(code, name) {
	/// <summary>附加一个字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>

	$("<div/>").appendTo(fields)
		.addClass("fields-attr-container clearfix")
		.attr('id','field-container-' + code)
		.append(addfieldType1(code, name)).hide();
	
	//tab页添加
	addfieldTab(code, name);
}

function appendfieldType(code, name, type,i) {
	/// <summary>附加一个字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <param name="type" type="String">类型</param>

	var s = $("<div/>").appendTo(fields)
		.addClass("fields-attr-container clearfix")
		.attr('id','field-container-' + code);

	if (type == 1) {
		s.append(addfieldType1(code, name));
	} else if (type == 2) {
		s.append(addfieldType2(code, name));
	} else if (type == 3) {
		s.append(addfieldType3(code, name));
	}
	if(i!==0){
		s.hide();
		addfieldTab(code, name);
		return;
	}
	
	//tab页添加
	addfieldTab(code, name, true).addClass("cur fst");
}

function addfieldType1(code, name) {
	/// <summary>增加直接显示类型的字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <returns type="String" />
	var content,
		options = i18n.types,
		sloption = options ? options.split(','):[];

	content = "<div class=\"list-row fl\">" + i18n.showtype + ":";
	content = content + "<select name='slshowtype' onchange=\"showTypeChanged('" + code + "','" + name + "','1');\" id=\"sl-showtype-" + code + "\">";
	for (var i = 0; i < sloption.length; i++) {
		if (i == 0) {
			content = content + "<option selected='selected' value='" + (i + 1) + "'>" + sloption[i] + "</option>";
		} else {
			content = content + "<option value='" + (i + 1) + "'>" + sloption[i] + "</option>";
		}
	}
	content = content + "</select>";
	content = content + "</div>";
	content = content + "<div class=\"list-row fl\"><input class=\"fl\" id=\"tooltip-" + code + "\" type=\"checkbox\" />" + "<label for=\"tooltip-" + code + "\" class=\"fl\">" +i18n.tooltip + "</label>" + "</div>";

	return content;
}

function addfieldType2(code, name) {
	/// <summary>增加判断显示类型的字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <returns type="String" />
	var content,
		options = i18n.types;
		sloption = options ? options.split(','):[];

	content = "<div class=\"list-row\">" + i18n.showtype + ":";
	content = content + "<select name='slshowtype' onchange=\"showTypeChanged('" + code + "','" + name + "','2');\" id=\"sl-showtype-" + code + "\">";
	for (var i = 0; i < sloption.length; i++) {
		if (i == 1) {
			content = content + "<option selected='selected' value='" + (i + 1) + "'>" + sloption[i] + "</option>";
		} else {
			content = content + "<option value='" + (i + 1) + "'>" + sloption[i] + "</option>";
		}
	}
	content = content + "</select>";
	content = content + "</div>";
	content = content + "<div class=\"list-row\">" + i18n.adddecied + ":";
	content = content + "<button class='button' onclick=\"decideAdd('" + code + "','0');\" id='decide-" + code + "' >" + i18n.add + "</button>";
	content = content + "<button class='button' onclick=\"decideAdd('" + code + "','1');\" id='decide1-" + code + "' >" + i18n.add1 + "</button>";
	content = content + "<button class='button' onclick=\"decideAdd('" + code + "','2');\" id='decide2-" + code + "' >" + i18n.add2 + "</button>";
	content = content + "</div>";
	content = content + "<ul id='ul-decide-" + code + "' class='decied-list'>";
	content = content + "</ul>";
	return content;
}

function addfieldType3(code, name) {
	/// <summary>增加自定义显示类型的字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <returns type="String" />
	var content,
		options = i18n.types;
		sloption = options ? options.split(','):[];
	// content = "<div class=\"list-row\">" + name + ":<input id=\"txt-field-" + code + "\" type=\"text\" value='%" + code + "%' /></div>";
	content = "<div class=\"list-row\">" + i18n.showtype + ":";
	content = content + "<select name='slshowtype' onchange=\"showTypeChanged('" + code + "','" + name + "','3');\"  id=\"sl-showtype-" + code + "\">";
	for (var i = 0; i < sloption.length; i++) {
		if (i == 2) {
			content = content + "<option selected='selected' value='" + (i + 1) + "'>" + sloption[i] + "</option>";
		} else {
			content = content + "<option value='" + (i + 1) + "'>" + sloption[i] + "</option>";
		}
	}
	content = content + "</select>";
	content = content + "</div>";
	content = content + "<div class=\"list-row\">" + i18n.customfield + ":</div><textarea class='customfield' id=\"txt-customfield-" + code + "\"></textarea>";
	return content;
}

function showTypeChanged(code, name, oldtype) {
	/// <summary>当选择的字段类型改变时</summary>
	/// <param name="code" type="String">字段编码</param>
	// alert(code+name+oldtype);
	var sl = $("#sl-showtype-" + code);
	if (sl != null) {
		if (sl.val() == oldtype) {
			return false;
		}

		if (sl.val() == 1) {
			$("#field-container-" + code).html(addfieldType1(code, name));

		}
		if (sl.val() == 2) {
			$("#field-container-" + code).html(addfieldType2(code, name));

		}
		if (sl.val() == 3) {
			$("#field-container-" + code).html(addfieldType3(code, name));
		}

	}
	
}

function addfieldTab(code,name,fst){

	var li = $("<li/>").appendTo(fieldsTabs).click(function(){
			fieldsTabs.find("li").removeClass("cur");
			$(this).addClass("cur");
			fields.find(".fields-attr-container").hide();
			$('#field-container-' + code).show();
		}),
		delIcon = fst ? undefined : $("<a/>").appendTo(li).attr("href","#").html("[X]").click(function(event){
			var isCur,oldText="";
			if (confirm(i18n.confirmok)) {
				isCur = li.hasClass("cur");
				li.empty();
				li.remove();
				$('#field-container-' + code).empty();
				$('#field-container-' + code).remove();
				if(isCur){
					fieldsTabs.children(":first").click();
				}
				
				removeColumnSortField(code);
				removeSortField(code);
				
				updatefieldisplay(code,name);
			}
			event.preventDefault();
			event.stopPropagation();
		}),
		text = $("<span/>").appendTo(li).html(name);
	
	return li;
}
function updatefieldisplay(code,name){
	/// <summary>更新字段显示</summary>
	
	if(columnList.find(".field-selected").length==1){
		var li = columnList.find(".field-selected");
		var fields = li.data("field-data");
		if(fields!=null){
			var newfields = new Array();
			$.each(fields,function(i,n){
				if(n.code != code){
					newfields.push(n);
				}
				
			});
		}
		li.removeData("field-data");
		li.data("field-data",newfields);
		saveAttrs(li);

		//更新显示内容
		var attrData = li.data("field-attr-data");
		if(attrData!=null){
			
			if(attrData.template!=null){
				var replaceName = "["+name+"]";
				$("#txt-showcontent").val(attrData.template.replace(replaceName,""));
			}
		}
	}
}
function setSortfields(code, name) {
	/// <summary>设置排序字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	//1.过滤重复
	var hasRepeat = false;
	slSortfield.children("option").each(function(i, item) {
		if (item.value == code) {
			hasRepeat = true;
		}
	});
	if(!hasRepeat){
		//2.附加选项
		$("<option/>").appendTo(slSortfield).val(code).html(name);
	}
}

function removeSortField(code){
	/// <summary>设置排序字段</summary>
	/// <param name="code" type="String">字段编码</param>
	
	var rmoption = slSortfield.find("option[value='"+code+"']");
	if(rmoption.length>0){
		
	    rmoption.remove();
	    synSortField(code);
	}
}

function setColumnSortfields(code, name) {
	/// <summary>设置排序字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	// var colsort = $("#sl-column-sortfield");
	//1.过滤重复
	var hasRepeat = false;
	$("option", colsort).each(function(i, item) {
		if (item.value == code) {
			hasRepeat = true;
		}
	});
	if(!hasRepeat){
		//2.附加选项
		$("<option/>").appendTo(colsort).val(code).html(name);
		
	}
}

function removeColumnSortField(code){
	/// <summary>设置排序字段</summary>
	/// <param name="code" type="String">字段编码</param>
	
	var rmli = colsort.find("option[value='"+code+"']");
	
	if(rmli.length>0){
		
		if(colsort.val() == code){
			if(colsort.find("option:first").length>0){
				colsort.find("option:first").attr("selected",true);
			}
		}
		rmli.remove();
	}
	
}

function resetColumnSortfields(fathercode) {
	/// <summary>清楚排序选项</summary>
	// var colsort = $("#sl-column-sortfield");
	colsort.empty();
	$("<option/>").appendTo(colsort).val('-1').html(i18n.nocolumnsort);
	//把该列已有的字段附加上 
	var fieldli = $("#column-" + fathercode);

	if (fieldli.data("field-data")) {
		$.each(fieldli.data("field-data"), function(i, n) {
			$("<option/>").appendTo(colsort).val(n.code).html(n.name);
		});
	}
	if (fieldli.data("field-attr-data")) {
		colsort.children().each(function(i,n){
			if(n.value === fieldli.data("field-attr-data").sort){
				colsort.val(n);
			}
		});
	} else {
		colsort.val(fathercode);
		//colsort.val("-1");
	}
}

function decideAdd(code,type) {
	/// <summary>增加判断选项</summary>
	/// <param name="code" type="String">字段编码</param>
	var ul = $("#ul-decide-" + code);
	if (ul != null) {
		var index = ul.find("li").length + 1;
		//var licontent = "<li id='ul-decide-li-" + index.toString() + "'><input id=\"li-decide-" + code + index.toString() + "\" type=\"text\" value='%" + code + "%' />=<input name='decide-key' id=\"li-decide-value" + code + index.toString() + "\" type=\"text\" />" + i18n.show + ":<input name='decide-value' id=\"li-decide-txt" + code + index.toString() + "\" type=\"text\" /><img name='decide-icon'  id=\"li-decide-icon" + code + index.toString() + "\" alt=\"\" src=\"\" /><button class='button' onclick=\"selectIcon('" + code + "','" + index + "');\">" + i18n.selecticon + "</button><button class='button' onclick=\"decideDelete('" + code + "','" + index + "');\">" + i18n._delete + "</button></li>";
		var decideType = getDecideType(type);
		var sb = new Array();
		sb.push("<li id='ul-decide-li-" + index.toString() + "' name='"+type+"'>");
		sb.push("<div>")
		sb.push(i18n.valueof+decideType+"<input class='decide-input' name='decide-key' id=\"li-decide-value" + code + index.toString() + "\" type=\"text\" onblur=\"onlyCharNumberChiness(this);\" value=\"1\" />"+i18n.onvalue+"，");
		sb.push(i18n.showimg+"：<img name='decide-icon' class='decide-img'  id=\"li-decide-icon" + code + index.toString() + "\" alt=\"\" src=\"../images/decied_default1.gif\" />");
		sb.push("&nbsp;&nbsp;<button class='button' onclick=\"selectIcon('" + code + "','" + index + "');\">" + i18n.selecticon + "</button>,");
		sb.push(i18n.showtext+"：<input class='decide-input' name='decide-value' id=\"li-decide-txt" + code + index.toString() + "\" type=\"text\" onblur=\"onlyCharNumberChiness(this);\" />");
		sb.push("<button class='button' onclick=\"decideDelete('" + code + "','" + index + "');\">" + i18n._delete + "</button>");
		sb.push("</div>")
		sb.push("<div class='decide-tooltip'>"+i18n.imgtexttooltip+"</div>")
		sb.push("</li>");
		ul.append(sb.join(''));
		
		$("#li-decide-value"+ code + index.toString()).focus().select();
		
	}
}
function getDecideType(v){
	/// <summary>获取判断的类型</summary>
	/// <param name="v" type="String">0,1,2</param>
	var strType = "=";
	if(v!=null&&v.toString().length>0){
		switch(v.toString()){
		case "0":
			strType = "=";
			break;
		case "1":
			strType = ">";
			break;
		case "2":
			strType = "<";
			break;
		}
	}
	return strType;
}
function onlyCharNumberChiness(obj){
	var p1 = /^[^&<>'"!]+$/i;
	if(!e5.utils.regExpTest(p1,obj.value)){
		alert(i18n.charnumberchiness);
		$(obj).focus();
		return;
	}
}
function setDecides(code, enums) {
	/// <summary>选择图标</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="enums" type="Array">字段编码</param>
	var ul = $("#ul-decide-" + code);
	if (enums != null && ul != null) {
		$.each(enums, function(i, k) {

			var index = i + 1;
			
			var imgurl = "../images/decied_default1.gif";
			if(k.icon!=null&& k.icon!=""){
				imgurl = k.icon;
			}
			var type = "0";
			if(k.op){
				type = k.op;
			}
			
			var sb = new Array();
			sb.push("<li id='ul-decide-li-" + index.toString() + "' name='"+type+"'>");
			sb.push("<div>")
			sb.push(i18n.valueof+getDecideType(type)+"<input class='decide-input' name='decide-key' id=\"li-decide-value" + code + index.toString() + "\" type=\"text\" value='" + k.key + "' onblur=\"onlyCharNumberChiness(this);\" />"+i18n.onvalue+"，");
			sb.push(i18n.showimg+"：<img name='decide-icon' class='decide-img'  id=\"li-decide-icon" + code + index.toString() + "\" alt=\"\" src=\""+imgurl+"\" />");
			sb.push("&nbsp;&nbsp;<button class='button' onclick=\"selectIcon('" + code + "','" + index + "');\">" + i18n.selecticon + "</button>,");
			sb.push(i18n.showtext+"：<input class='decide-input' name='decide-value' id=\"li-decide-txt" + code + index.toString() + "\" type=\"text\" value='" + k.value + "' onblur=\"onlyCharNumberChiness(this);\" />");
			sb.push("<button class='button' onclick=\"decideDelete('" + code + "','" + index + "');\">" + i18n._delete + "</button>");
			sb.push("</div>")
			sb.push("<div class='decide-tooltip'>"+i18n.imgtexttooltip+"</div>")
			sb.push("</li>");
			ul.append(sb.join(''));
		});

	}

}

function selectIcon(code, index) {
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
					$("#icon-list ul").append("<li><table><tr><td class='l'></td><td class='c'><img onclick=\"setIcon(this);\" name=\"" + code + index.toString() + "\"  id=\"" + code + "-" + index.toString() + "-" + n.ID + "\" alt=\"" + n.description + "\" title=\"" + n.description + "\" src=\"../" + n.url + "\" /></td><td class='r'></td></tr></table></li>");
				});
			}
		}
	});
	//2.显示图标
	e5.dialog.show("iconview");
}

function setIcon(obj) {
	/// <summary>设置图标</summary>
	/// <param name="obj" type="Object">img</param>
	var img = $("#" + "li-decide-icon" + $(obj).attr("name"));
	img.attr("src", $(obj).attr("src"));
	img.attr("alt", $("#li-decide-txt" + $(obj).attr("name")).val());
	img.attr("title", $("#li-decide-txt" + $(obj).attr("name")).val());
	//关闭窗口
	e5.dialog.hide("iconview");

}

function decideDelete(code, index) {
	/// <summary>删除判断分支</summary>
	/// <param name="code" type="String">字段编码</param>
	if (confirm(i18n.confirmok)) {
		var li = $("#ul-decide-li-" + index);
		if (li != null) {
			li.remove();
		}
	}
	
}

/* 锁定功能部分 */
function lock(jqueryElm){
	jqueryElm.data("lock",true);
	// columnList.sortable("disable");
	fieldAttrArea.show();
}
function unlock(jqueryElm){
	jqueryElm.data("lock",false);
	// columnList.sortable("enable");
	fieldAttrArea.hide();
}
function getLock(jqueryElm){
	return jqueryElm.data("lock");
}

/* 移动 */
function moveColumnList(event){
	var column,
		columns = columnList.find("li"),
		columnSize = columns.size()-1,
		columnListOffset = parseInt(columnList.css("left")),
		elm = event ? (event.target ? event.target : event) : this,
		elmTag = elm.tagName.toLowerCase(),
		elmId = elm.id,
		offset = 0,
		left,
		width;
	switch(elmTag){
		case "button" :
			if(elmId == "up"){
				positionIndex = positionIndex <= 0 ? 0 : positionIndex-1;
			}
			if(elmId == "down"){
				positionIndex = positionIndex >= columnSize ? columnSize : positionIndex+1;
			}
			if(columns[positionIndex]){
				column = $(columns[positionIndex]);
				offset = positionIndex ? -column.position().left : 4;
				if(!inDisplayArea(offset)){
					columnList.find(".field-selected").click();
				}
				columnList.animate({"left":offset},{duration:"fast",step:arrowPosition});
			}
			break;
		case "li" :
			column = $(elm);
			left = column.position().left + columnListOffset;
			width = left + column.width();
			if(inDisplayArea(columnListOffset)){
				break;
			}
			if(left < 0 || column.width()>columnListWrap.width()){
				positionIndex = column.index();
				offset = positionIndex ? -column.position().left : 4;
			}else if(width > columnListWrap.width()){
				offset = columnListOffset - (width - columnListWrap.width()) - 5;
				columns.each(function(index){
					if($(this).position().left + offset > 0){
						positionIndex = index;
						return false;
					}
				});
			}
			columnList.animate({"left":offset},{duration:"fast",step:arrowPosition});
			break;
	}
}

// 计算箭头位置
function arrowPosition(){
	var selected = columnList.find(".field-selected"),offset;
	if(selected[0]){
		offset = selected.width()/2 + selected.position().left + parseInt(columnList.css("left")) - fieldArrow.width()/2 + 30;
		fieldArrow.css("left",offset+"px");
	}
}

//判断字段的位置是否出显示区域
function inDisplayArea(columnListOffset){
	if(!getLock(columnList)){
		return true;
	}
	var selected = columnList.find(".field-selected"),
		offset = selected.position().left + columnListOffset
	if(offset >= 0){
		if(offset + selected.width() <= columnListWrap.width()){
			return true;
		}else if(selected.width() >= columnListWrap.width()){
			return false;
		}
	}
	return false;
}

// 按钮显示
function btnShow(){
	var judge = columnList.width()-10-columnListWrap.width();
	if(judge>0){
		up.show();
		down.show();
	}else{
		up.hide();
		down.hide();
		columnList.animate({"left":4},{duration:"fast",step:arrowPosition});
		positionIndex = 0
	}
}

function resizeHandle(){
	var fieldContentContainer = $("#field-content-container"),
		fieldAttr = $("#field-attr"),
		fieldArea = $("#field-area"),
		fieldList = $("#field-list"),
		btnArea = $("#btn-area"),
		draphelp = $("#draphelp"),
		fieldWorkspaceContainer = $("#field-workspace-container"),
		fieldContentContainerH = $("body").data("resize-special-event").h - btnArea.outerHeight(true) - fieldContentContainer.offset().top - parseInt(fieldContentContainer.css("margin-top"))-parseInt(fieldContentContainer.css("margin-bottom"))-10,
		fieldListH = fieldContentContainerH - fieldList.offset().top + fieldArea.offset().top,
		fieldAttrAreaW = draphelp.width() + parseInt(draphelp.css("padding-left")) + parseInt(draphelp.css("padding-right")),
		//fieldAttrAreaW = draphelp.width(),
		fieldAttrH = fieldContentContainerH - (parseInt(fieldAttrArea.css("top")) - fieldWorkspaceContainer.offset().top) - parseInt(fieldAttrArea.css("padding-top")) - parseInt(fieldAttrArea.css("padding-bottom")) - parseInt(fieldAttr.css("padding-top")) - parseInt(fieldAttr.css("padding-bottom"))-10;
	fieldContentContainer.height(fieldContentContainerH);
	fieldList.height(fieldListH);
	fieldAttrArea.width(fieldAttrAreaW);
	fieldAttr.height(fieldAttrH);
	fieldAttr.width(columnListWrap.width() - parseInt(fieldAttr.css("padding-left")) - parseInt(fieldAttr.css("padding-right")));
}

function foldAdvancedOptionHandle(){
	var foldAdvancedOptionBtn = $("#fold-advanced-option"),
		advancedOption = $(".advanced-option"),
		isFold = false,
		elmText = foldAdvancedOptionBtn.html().split(','),
		unfoldText = elmText[0],
		foldText = elmText[1];
	foldAdvancedOptionBtn.html(unfoldText).click(function(event){
		if(isFold){
			advancedOption.addClass("unfold");
			foldAdvancedOptionBtn.removeClass().addClass("unfold").html(unfoldText);
		}else{
			advancedOption.removeClass("unfold");
			foldAdvancedOptionBtn.removeClass().addClass("fold").html(foldText);
		}
		isFold = !isFold;
		event.preventDefault();
	});
}