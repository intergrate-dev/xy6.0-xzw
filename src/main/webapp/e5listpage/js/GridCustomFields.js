//选中个数
var selectCount = 0,
	columnContainer,
     AllSortFieldsList,
    SortFieldsList,
    slSortfield,
    txtfsort,
	fieldsTabs;
// init
$(function () {
    // 定义变量
    txtfsort = $("#txt-field-sort");
    AllSortFieldsList = $("#AllSortFieldsList");
    SortFieldsList = $("#SortFieldsList");
	columnContainer = $("#grid-custom");
	fieldsTabs = $("#field-attr-fields-tabs");
	slSortfield = $("#sl-sortfield");
	//初始化表格
	initTable(columnContainer);
	//设置按钮事件
	$("#btn-complete").click(function(){
		saveList();
	});
	$("#btn-cancle").click(function(){
		parent.parentUpdate($.query.get("listID"));
	});
	$("#btn-pre").click(function(){
		var params = {"docTypeID":$.query.get("docTypeID"),
				"docTypeName":$.query.get("docTypeName"),
				"listID"     :$.query.get("listID"),
				"listName"   :$.query.get("listName"),
				"slshowID"   :$.query.get("slshowID"),
				"slshowName" :$.query.get("slshowName"),
				"hasXLS"     :$.query.get("hasXLS"),
				"icon"		 :$.query.get("icon"),
				"xlsPath"    :$.query.get("xlsPath")
		};
		var url  = "ListPageCustomList.jsp?"+$.param(params);
		document.location.href = url;
	});
	$(document.body).bind("resize",resizeHandle);
	
	$("#txt-field-width").blur(function(){
		var p1 = /^[0-9]*[1-9][0-9]*$/;
		if(!e5.utils.regExpTest(p1,this.value)){
			alert(i18n.widthnumber);
			$(this).focus();
			return;
		}
	});
	
	//初始化模板
	if (table_templates != null) {
		if (table_templates.templates != null) {
			$.each(table_templates.templates,function(i,n){
				var li = $("<li id='tpl-" + n.id + "' onclick=\"changeTemplate("+n.id+");\"  name='" + n.id + "'>&nbsp;</li>");
				if (n.tempclass != "") {
					li.addClass(n.tempclass);
				}
				$("#template-list").find("ul").append(li);
			});
		}
	}

	$("#txt-template").click(function () {
		//2.显示图标
		e5.dialog({ type: 'id', value: 'template-list' }, { title: i18n.template, width:500, id: "templateview" }).show();
	});
	
	//隐藏图片输入框
	$("#img_container").hide();
	
	
	$("#btnFieldSort").click(function () {

	    initSortFields();
	});
	//获取文档类型字段
	$.ajax({
		url: "../e5listpage/cust_typeReader.do?docType=" + $.query.get("docTypeID"),
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
							if(!search.val()){
								fieldList.empty();
								$.each(datas,function(i,n){
									fieldList.append("<li class='field-li' id='" + n.id + "' title='" + n.code + "' name='" + n.code + "'>" + n.name + "</li>");
								});
								//设置字段可以拖动
								setFieldsDragable();
								return false;
							}
							if(max>0){
								//清除所有的字段
								if(i==1){
									fieldList.empty();
								}
								fieldList.append("<li class='field-li' id='" + row.id + "' title='" + row.code + "' name='" + row.code + "'>" + row.name + "</li>");
								//设置字段可以拖动
								setFieldsDragable();
							}
							return false;
						},
						formatMatch: function(row, i, max){
							return row.name +" "+ row.code;
						},
						formatResult: function(row) {
							//return row.code;
						}
					});
				  //自动完成结束
				}
				fieldList.append(lifiles).
				//禁止选中
				disableSelection();
				//开始加载已有数据
				loadUpdateData();
			  //开始加载已有数据结束
			}
		}
	});
	// end 获取字段类型
	//end droppable
	//设置字段可以拖动
	setFieldsDragable();
	setTDDropable();
	setDropHelpDropable();
	resizeHandle();
});

function loadUpdateData(){
	
	$.ajax({
		url:"../e5listpage/ListSubmit.do?method=getlistpageinfo&id="+$.query.get("listID")+"&showtypeid="+$.query.get("slshowID"),
		dataType:"json",
		async:false, 
		success:function(data) {
			if(data!=null){
				setUpdateData(data);
				//初始化表格
				initTable(columnContainer);
			}
		}
	});
}

// 定义函数
function tableRecoverEvent(){
	/// <summary>恢复表格原来样式</summary>
	//要删除所有的数据
	//修改阶段
	if($.query.get("listID")!=null&&$.query.get("listID")!="-1"){
		loadUpdateData();
	}
	else{
		
		//设计阶段
		clearData();
		//
		
		slSortfield.find("option").remove();
	}
}
function changeTemplate(id) {
	
	var content = "";
	if (table_templates.templates != null && table_templates.templates.length > 0) {
		$.each(table_templates.templates, function (i, n) {
			if (n.id == id) {
				content= n.content;
			}
		});
	}
	if (content != "") {
		columnContainer.html(content);
	}
	e5.dialog.close("templateview");
	initTable(columnContainer);
	clearData();
	 
	slSortfield.find("option").remove();
}
function clearData() {
    /// <summary>清楚数据</summary>
	var fields = $("#field-attr-fields");
	fields.html("");
	//设置默认值
	$("#txt-field-width").val("");
  
	$("#cb-field-nowrap").removeAttr("checked");
	$("#txt-field-class").val("");
	$("#txt-showcontent").val("");
	$("#field-attr-fields-tabs").html("");

   
  
}
function setTDDropable(){
	/// <summary>设置</summary>
	columnContainer.find("td").droppable({
		accept: "#field-list li",
		activeClass: "ui-state-hover",
		hoverClass: "ui-state-active",
		over: function (event, ui){
		},
		drop: function (event, ui) {
			drapablefield($(this),event,ui);
		}
	});
}
function setDropHelpDropable(){
	$("#fielddraphelp").droppable({
		accept: "#field-list li",
		activeClass: "field-attr-draphelp-hover",
		hoverClass: "field-attr-draphelp-active",
		drop: function (event, ui) {
			//0.先查找当前被选中的列			
			var cell = $("td[isdroped='true']",columnContainer),
				elmName = ui.draggable.attr("name"),
				elmHtml = ui.draggable.html(),
				columnData,
				i;
			if(cell[0]){
				//1.先判断是否已近存在该列
				columnData = cell.data("field-data");
				if(isExistfield(elmName)){
					return;
				}
				//2.把数据存储到列数据里
				cell.data("field-data").push({
					code: elmName,
					name: elmHtml
				});
				//3.增加字段显示
				appendfield(elmName,elmHtml);
				//4.添加排序字段
				setSortfields(elmName,elmHtml);
				//5.添加字段显示
				var oldText = $("#txt-showcontent").val();
				$("#txt-showcontent").val(oldText + "[" + elmHtml + "]");
				saveAttrs(cell);
			}
			else{
				return;
			}
		}
	});
}

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
    if (sData != null && sData.length > 0) {

        $.each(sData, function (i, n) {
            var swaytext = (n.sort.toString() == "1") ? i18n.sfdesc : i18n.sfasc;
            var swayclass = (n.sort.toString() == "1") ? "sort_li_desc" : "sort_li_asc";
            var lsi = $("<li><div title=\"" + i18n.sfsortway + "\" class=\"sort_li_right "+swayclass+"\">" + swaytext + "</div><div class=\"sort_li_left\">" + n.name + "</div></li>");
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
    AllSortFieldsList.find("li").live("dblclick", function () {
        //获取
        var li = $(this);
        var ldata = li.data("fd");
        if (ldata != null) {
            //默认为降序:1,升序:0
            var fsd = { "name": ldata.name, "code": ldata.code, "sort": 1 };
            var lsi = $("<li><div title=\"" + i18n.sfsortway + "\" class=\"sort_li_right sort_li_desc\">" + i18n.sfdesc + "</div><div class=\"sort_li_left\">" + ldata.name + "</div></li>");
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

    var left = $("#btnFieldSort").position().left - 16;
    var top = $("#btnFieldSort").position().top + 30;
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

function isExistfield(fieldname){
	/// <summary>判断是否已经存在该字段</summary>
	var existfield = false;
	if (columnContainer.find("td[name='" + fieldname + "']").length > 0) {
		
		existfield = true;
	}
	columnContainer.find("td").each(function(i,n){
		var td = $(this);
		if(td.attr("name")!=null && td.attr("name")!=""){
			
			if(td.data("field-col-data")!=null){
				
				$.each(td.data("field-col-data"),function(p,k){
					if(k.name == fieldname){
						existfield = true;
					}
				});
			}
		}
		
	});
	
	return existfield;
}
function drapablefield(cell,event,ui){
	 /// <summary>实现拖动功能</summary>
	 /// <param name="event" type="event">event</param>
	/// <param name="ui" type="OBJECT">拖动元素</param>
	
	//保存上一列数据
	var isdropedTD = columnContainer.find("td[isdroped='true']");
	if(isdropedTD.length>0 ){
		
		isdropedTD.each(function(i,n){
			//保存上一列数据
			saveAttrs($(this));
			//移除属性
			$(this).removeAttr("isdroped");
			cell.removeClass("ui-selected");
		});
	}
	
	 
	//禁止重复拖动字段到单元格
	//1.不能拖动重复的字段
	if(isExistfield(ui.draggable.attr("name"))){
		alert(i18n.existfield);
		return;
	}
	//判断所有
	//2.不能在已经有字段的单元格再拖动字段
	if (cell.data("field-data") !=null){
		//替换字段
		//先删除字段
		removefield(cell);
		 
	}
	//隐藏图片输入框
	if (cell.hasClass("tablehandle-imgformat")){
		$("#img_container").show();
	}
	else {
		$("#img_container").hide();
	}
	
	//存储数据
	 
	cell.html(ui.draggable.html());
	cell.attr("name",ui.draggable.attr("name"));
	cell.attr("title",ui.draggable.html());
	//设置拖动属性
	cell.attr("isdroped","true");
	
	var fielddata = new Array();
	fielddata.push({ code: ui.draggable.attr("name"), name: ui.draggable.html() });

	cell.data("field-data", fielddata);
	
	//增加选中样式
	//移除被选中样式
	columnContainer.find("td").each(function(i,n){
	   if($(this).hasClass("ui-selected")){
		   $(this).removeClass("ui-selected");
	   }
	});

	cell.addClass("ui-selected");
	//添加排序字段
	setSortfields(ui.draggable.attr("name"),ui.draggable.html());

	//增加字段显示
	addfield(ui.draggable.attr("name"), ui.draggable.html());
	
	//设置属性
	setAttrs(cell);
}
function initTable(tb){
	/// <summary>设置</summary>
	/// <param name="tb" type="jq">table jquery对象</param>
	if(tb==null){
		return;
	}
	tb.tablehandle({
		tablehandle_addrow_up:i18n.tablehandle_addrow_up,
		tablehandle_addrow_down:i18n.tablehandle_addrow_down,
		tablehandle_addcol_left:"",
		tablehandle_addcol_right:"",
		tablehandle_delrow:i18n.tablehandle_delrow,
		tablehandle_delcol:i18n.tablehandle_delcol,
		tablehandle_mergcell:i18n.tablehandle_mergcell,
		tablehandle_mergdown: i18n.tablehandle_mergdown,
		tablehandle_mergright:i18n.tablehandle_mergright,
		tablehandle_splitrow:i18n.tablehandle_splitrow,
		tablehandle_splitcol:i18n.tablehandle_splitcol,
		tablehandle_delcell:i18n.tablehandle_delcell,

		tablehandle_merg_tiptext:i18n.tablehandle_merg_tiptext,
		tablehandle_celldelall_tiptext:i18n.tablehandle_celldelall_tiptext,
		tablehandle_selectcellone_tiptext:i18n.tablehandle_selectcellone_tiptext,
		tablehandle_dontdelonlycol:i18n.tablehandle_dontdelonlycol,
		tablehandle_dontdelonlyrow:i18n.tablehandle_dontdelonlyrow,
		tablehandle_formatimg:i18n.tablehandle_formatimg,
		tablehandle_removeimgformat:i18n.tablehandle_removeimgformat,
		tablehandle_recover:i18n.tablehandle_recover,
		//event
		
		tableMenuContainer:$("#field-content-container"),
		cellUpdateEvent:setTDDropable,
		tableRecoverEvent:tableRecoverEvent,
		setImgEvent:setImageEvent,
		selectingEvent:onSelecting,
		selectedEvent: onSelected,
		removeFieldEvent:removefield,
		contextMenuShowEvent:onContextMenuShow,
		
		imgformatclass:"tablehandle-imgformat"
	});
}
function onContextMenuShow(cell){
	/// <summary>当右键选择的时候选择当前单元格</summary>
	var ui = {
			selected:cell
	}
	selectCount ++;
	onSelected(null,ui)
}
function setFieldsDragable(){
	/// <summary>设置字段可以拖动</summary>
	 $("#field-list li").draggable({ helper: "clone",
			cursor: "move"
		});
}
function onSelecting(event,ui){
	/// <summary>当开始选择时</summary>
	/// <param name="event" type="event">事件</param>
	/// <param name="ui" type="object">被选中ui</param>

	selectCount++;
}
function removefield(cell) {
	/// <summary>删除单元格数据</summary>
	saveAttrs(cell);
	//删除排序字段
	if(cell.attr("name")!=null){
		
		if(cell.data("field-col-data")!=null){
			
			$.each(cell.data("field-col-data"),function(i,n){
				sortFiledRemove(n.name);
			});
		}
		
		cell.removeAttr("name");
		
	}
	if(cell.data("field-data")!=null){
		cell.removeData("field-data");
	}
	if(cell.attr("isdroped")!=null){
		cell.removeAttr("isdroped");
	}
	if(cell.hasClass("ui-selected")){
		cell.removeClass("ui-selected");
	}
	if(cell.data("field-attr-data")!=null){
		cell.removeData("field-attr-data");
	}
	if(cell.data("field-col-data")!=null){
		cell.removeData("field-col-data");
	}
	clearData();
}
function onSelected(event,ui){
	/// <summary>当选择结束时</summary>
	/// <param name="event" type="event">事件</param>
	/// <param name="ui" type="object">被选中ui</param>	
	//如果被选中，再点击要保存上次的值
	if (columnContainer.find("td[isdroped='true']").length > 0) {
		columnContainer.find("td[isdroped='true']").each(function (i, n) {
			saveAttrs($(this));
			//移除属性
			$(this).removeAttr("isdroped");
			$(this).removeClass("ui-selected");
		});
	}
	//选中一个表示点击事件
	if (selectCount != 1) {
		selectCount = 0;
		clearData();
		return;
	}
	selectCount = 0;
	var cell = $(ui.selected);
	if (!cell.html() || cell.html() == " " || cell.html() == "&nbsp;"){
		clearData();
		return;
	}
	//判断是否显示图片url域
	if (cell.hasClass("tablehandle-imgformat")) {
		$("#img_container").show();
		var imgUlr = "";
		if(cell.attr("t-img-url")!=null&&cell.attr("t-img-url")!="-1"){
			imgUlr = cell.attr("t-img-url");
		}
		
		//显示img url属性
		$("#txt-img-url").val(imgUlr);
		
	}
	else {
		$("#img_container").hide();
		$("#txt-img-url").val("-1");
	}

	cell.attr("isdroped","true");
	if(!cell.hasClass("ui-selected")){
		cell.addClass("ui-selected");
	}
	setAttrs(cell);
}
function setImageEvent(cell,options){
	/// <summary>设置字段可以拖动</summary>
	/// <param name="cell" type="jq">jquery td对象</param>
	/// <param name="options" type="JSON">默认选项</param>
	if(cell!=null){
		//添加时
		
		$("#img_container").show();
		if($("#txt-img-url").val()=="-1"){
			$("#txt-img-url").val("");
		}
	}
	else{
		//移除时
		$("#img_container").hide();
	}
}

function saveList(){
	/// <summary>保存列表数据</summary>
	var numbers = $("#txt-field-pagenum").val();
	var validateNum = true;
	
	if(numbers!=null&&numbers!=""){
		var num = new Array();
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
	if(columnContainer.find("td[isdroped='true']").length>0){
		columnContainer.find("td[isdroped='true']").each(function(i,n){
			saveAttrs($(this));
		});
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
	sb.push("<doctypename>"+ $.query.get("docTypeName")+"</doctypename>");
	sb.push("<doctypeid>"+ $.query.get("docTypeID")+"</doctypeid>");
	sb.push("<name>"+ $.query.get("listName")+"</name>");
	sb.push("<id>"+ $.query.get("listID")+"</id>");
	sb.push("<rendertype>"+ $.query.get("slshowID")+"</rendertype>");
	sb.push("<hasxsl>"+ $.query.get("hasXLS")+"</hasxsl>");
	sb.push("<pathxsl><![CDATA["+ pathxsl+"]]></pathxsl>");
	//listpage属性结束
	
	sb.push("<root>");
	
	//fields开始
	sb.push("<fields>");
	//遍历所有用到的字段
	slSortfield.children("option").each(function (i, o) {
		sb.push("<field>");
		sb.push("<name>"+o.value+"</name>");
		sb.push("<title>"+o.text+"</title>");
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
	if (sData != null && sData.length > 0) {
	    $.each(sData, function (i, n) {

	        fstr.push(n.code);
	        tstr.push(n.sort);

	        if (i < sData.length - 1) {
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
	columnContainer.find("td").each(function(j,l){
		var fieldDataSer = $(this).data("field-col-data");
		if(fieldDataSer!=null){
			//过滤显示内容中的[名称]成*名称*
			
			if($(this).attr("t-template")!=null){
				var newTemp = convertName2Code(fieldDataSer,$(this).attr("t-template"));
				$(this).attr("t-template",newTemp);
			}
			
			$.each(fieldDataSer,function(o,c){
				sb.push("<field>");
				sb.push("<type>"+c.type+"</type>");
				sb.push("<title>"+c.title+"</title>");
				sb.push("<name>"+c.name+"</name>");
				if(c.type == 1){
					
					sb.push("<tooltip>"+c.tooltip+"</tooltip>");
				}
				else if(c.type == 2){
					
					sb.push("<enums>");
					if(c.decide!=null && c.decide.length>0){
						$.each(c.decide,function(m,d){
							
							sb.push("<enum>");
							 var op = "0";
							    if(d.op){
							    	op = d.op;
							    }
							sb.push("<key>"+d.key+"</key>");
							sb.push("<value>"+d.value+"</value>");
							sb.push("<icon><![CDATA["+abstorelativeIcon(d.icon)+"]]></icon>");
							sb.push("<op>" + op + "</op>");
							sb.push("</enum>");
						});
						
					}
					sb.push("</enums>");
				}
				else if(c.type == 3){
					var newContent = convertName2Code(fieldDataSer,c.content);
					sb.push("<content><![CDATA[" + newContent + "]]></content>");
					//sb.push("<content><![CDATA["+c.content+"]]></content>");
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
	sb.push("<![CDATA[")

	sb.push(columnContainer.html());
	sb.push("]]>");
	
	sb.push("</slice>");
	
	sb.push("</custom>");
	// custom结束
	
	sb.push("</listpage>");
	
	var tempContent = "";
	var pattan = /(<input((?!type).)*[^>]*[/]?>)/g;
	
	tempContent = sb.join('').replace(pattan, function (element, index) {

		var newInput = element;
		var closeIndex = element.toString().indexOf("/>");
		var closeIndex2 = element.toString().indexOf(">");
		if (closeIndex < 0 && closeIndex2 > 0) {

			newInput = element.substring(0, closeIndex2) + "/>";

		}
		return newInput;
	});
	//后台处理空格
	tempContent = tempContent.replace(new RegExp("&nbsp;", "gim"), function (element, index) {
		return "&#160;";
	});
	tempContent = tempContent.replace(new RegExp("<br>", "gim"), function (element, index) {
		return "<br/>";
	});
	tempContent = tempContent.replace(new RegExp("<hr>", "gim"), function (element, index) {
		return "<hr/>";
	});
	
	//tempContent = sb.join('');
	//3.post xml格式的数据
	var dataxml = {
			listxml:tempContent
	}
	//alert(sb.join(''));
	$.ajax({
		  type: "POST",
		  url: "../e5listpage/ListSubmit.do",
		  data:dataxml,
		  beforeSend:function(){
			  //设置按钮不可用，防止重复提交
			  $("#btn-complete").attr("disabled",true);
		  },
		  success: function(data, textStatus){
			  $("#btn-complete").attr("disabled",false);
				if(data == "OK"){
					alert(i18n.operation_success);
					parent.parentUpdate($.query.get("listID"));
				}
				else{
					alert(i18n.operation_fail);
				}
		  },
		  error: function(){
				//请求出错处理
			  $("#btn-complete").attr("disabled",false);
			  alert(i18n.operation_fail);
		  }
		});
}
function setUpdateData(data){
	/// <summary>该方法主要是来设置列容器的宽度，在拖放和调整列宽度时调用</summary>
	/// <param name="data" type="JSON">json对象</param>
	if(data.fields!=null){
		//1.设置主属性
		slSortfield.find("option").remove();
		$.each(data.fields,function(i,n){
		    $("<option/>").appendTo(slSortfield).val(n.code).html(n.name);
		});
	    //组装排序字段数据
		var gData = new Array();

		if (data.sortField != null && data.sortField.toString().length > 0 &&
            data.sortWay != null && data.sortWay.toString().length > 0) {

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
		//2.创建单元格及数据
		
		//把内容添加到table里面
		if(data.gridInfo==null||data.gridInfo==""){
			return;
		}
		columnContainer.html(data.gridInfo);
	
		
		if(columnContainer.find("td[isdroped='true']").length>0){
			
			columnContainer.find("td[isdroped='true']").each(function(n,b){
				$(this).removeAttr("isdroped");
			});
		}

		$("#txt-field-pagenum").val(data.rows);
		
		if(data.columns!=null){
			$.each(data.columns,function(p,k){
				var cell = null;
				cell = $("td[name='"+k.name+"']",columnContainer);
				if(cell == null|| cell.length==0){
					return;
				}
				//存储数据
				var fielddata = new Array();
				if(k.fieldList!=null){
					var fieldColDatas = new Array();
					$.each(k.fieldList,function(a,b){
						 fielddata.push({ code: b.name, name: b.title });
					});
				}
 
				cell.data("field-data", fielddata);
				
				if(columnContainer.find("td[isdroped='true']").length>0){
					
					columnContainer.find("td[isdroped='true']").each(function(n,b){
						$(this).removeAttr("isdroped");
					});
				}
				cell.attr("isdroped","true");

			
				//field-attr-data
				//field-col-data
				if(k.fieldList!=null){
					var fieldColDatas = new Array();
					$.each(k.fieldList,function(o,d){
						var uname    ="";
						var utitle   ="";
						var utype    = "";
						var utooltip = "";
						var udecide  = null;
						var ucontent = "";
						uname = d.name;
						utitle = d.title;
						utype = d.type;
						if(d.type == 1){
							utooltip = d.tooltip;
						
						}else if(d.type == 2){
							udecide = d.enumslist;
						}else if(d.type == 3){
							ucontent = d.content;
						}
						var colfielddatas= {
								name   :uname,
								title  :utitle,
								type   :utype,
								tooltip:utooltip,
								decide :udecide,
								content:ucontent
							};
						fieldColDatas.push(colfielddatas);
					});
					cell.data("field-col-data",fieldColDatas);
				}
				
				//将type=3的类型中的编码转换成名称
				var newFieldColDatas = [];
				
				$.each(fieldColDatas,function(t,y){
					
					if(y.type==3){
						y.content = convertCode2Name(fieldColDatas,y.content);
					}
					newFieldColDatas.push(y);
				});
				cell.data("field-col-data", newFieldColDatas);
				
				var newtemp = convertCode2Name(fieldColDatas,k.template);
				//field-attr-data
				var colData = {
						width    :k.width,
						name     :k.name,
						head     :k.head,
						wrap     :k.warp,
						classname:k.classname,
						sort     :k.sort,
						template :newtemp
				};
				//设置列属性数据
				cell.data("field-attr-data",colData);
				//来设置数据
				setAttrs(cell);
				//判断最后一个选中
				if(p == data.columns.length-1){
					
					//设置第一个别选中
					if(columnContainer.find("td").length>0){
						
						columnContainer.find("td").each(function(i,n){
							if($(this).hasClass("ui-selected")){
								$(this).removeClass("ui-selected");
							}
						});
					}

					cell.addClass("ui-selected");

				}
			});
			

		}
	}
	
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
        slSortfield.data("sortdata", nData);
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
    txtfsort.attr("title", sfstr.join(''));
}
function saveAttrs(obj){
	/// <summary>保存属性内存</summary>
	/// <param name="obj" type="Object">当前列jquery 对象</param>

	//没有字段的不保存内容
	if (!obj.html() || obj.html() == "&nbsp;" || obj.html() == " ") {
		return;
	}
	var iswrap = ($("#cb-field-nowrap").attr("checked")=="checked")?"1":"0";
	 //1.保存字段的内容
	var colData = {
			width    :$("#txt-field-width").val(),
			name     :"",
			head     :"",
			wrap     :iswrap,
			classname:$("#txt-field-class").val(),
			sort     :"",
			template :$("#txt-showcontent").val()
	};
	//设置列属性数据
	var classname="-1";
	if(colData.classname!=null&&colData.classname!=""){
		classname = colData.classname;
	}
	obj.data("field-attr-data",colData);
	obj.attr("t-width",colData.width);
	obj.attr("t-wrap",colData.wrap);
	obj.attr("t-template",colData.template);
	obj.attr("t-classname", classname);
	var imgUrl = "-1";
	if (obj.hasClass("tablehandle-imgformat")) {
	    
		imgUrl = $("#txt-img-url").val();
	}
	
	if(imgUrl==""||imgUrl==null){
		imgUrl = "-1";
	}
	obj.attr("t-img-url", imgUrl);
	var fieldColData = [];
	$.each(obj.data("field-data"),function(i,n){
		//1.找到字段类型
		var fieldTyped = $("#sl-showtype-"+n.code).val();
		var showtooltip ="0";
		var enums = new Array();
		var content = "";
		if(fieldTyped == 1){
			//直接显示
			showtooltip = ($("#tooltip-"+n.code).attr("checked")=="checked")?"1":"0";
			enums = null;
		}
		else if(fieldTyped == 2){
			//判断显示
			$("#ul-decide-"+n.code).find("li").each(function(i,d){
				var imgurl = $(this).find("img").attr("src");
				if(imgurl.toString().indexOf("decied_default1")>0){
					imgurl = "";
				}
				var decideType = "0";
				if($(this).attr("name")){
					decideType = $(this).attr("name");
				}
				var enumrow = {
					key:$(this).find("input[name='decide-key']").val(),
					value:$(this).find("input[name='decide-value']").val(),
					icon:imgurl,
					op:decideType
				};
				enums.push(enumrow);
			});
		}
		else if(fieldTyped == 3){
			//自定义显示
			content = $("#txt-customfield-"+n.code).val();
			enums = null;
		}
		var colfielddata= {
				name   :n.code,
				title  :n.name,
				type   :fieldTyped,
				tooltip:showtooltip,
				decide :enums,
				content:content
		};
		fieldColData.push(colfielddata);
	});
	//设置列属性数据
	obj.data("field-col-data",fieldColData);
}
function setAttrs(obj) {
	/// <summary>设置属性</summary>
	/// <param name="obj" type="Object">列</param>
	//1.设置列属性
	var colData = obj.data("field-attr-data");
	if(colData!=null){
		$("#txt-field-width").val(colData.width);
		//设置宽度
		obj.width(colData.width+"px");
		$("#txt-showcontent").val(colData.template);
		if(colData.wrap == "0"){
			$("#cb-field-nowrap").removeAttr("checked");
		}if(colData.wrap =="1"){
			$("#cb-field-nowrap").attr("checked","checked");
		}
		var classname = "";
		if(colData.classname!="-1"){
			classname = colData.classname;
		}
		$("#txt-field-class").val(classname);

		var imgUrl = "";
		if(obj.attr("t-img-url")!="-1"){
			imgUrl = obj.attr("t-img-url");
		}
		if (obj.hasClass("tablehandle-imgformat")) {
			$("#img_container").show();
			$("#txt-img-url").val(imgUrl);
		}
		else {
			$("#img_container").hide();
		}
	}
	else{
		//设置默认值
		$("#txt-field-width").val(obj.width());
		$("#cb-field-nowrap").removeAttr("checked");
		$("#txt-field-class").val("");
		$("#txt-showcontent").val("["+obj.attr("title")+"]");
	}
	//2.设置字段
	var fcdata = obj.data("field-col-data");
	if(fcdata!=null){
		//先清空内容
		$("#field-attr-fields").empty();
		fieldsTabs.empty();
		$.each(fcdata,function(i,n){
			if(n.type == 1){
				//1.增加字段
				appendfieldType(n.name,n.title,n.type,i);
				//2.设置字段
				 
				$("#sl-showtype-"+n.name).attr("value",n.type);
				if(n.tooltip == "0"){
					$("#tooltip-"+n.name).removeAttr("checked");
				}if(n.tooltip =="1"){
					$("#tooltip-"+n.name).attr("checked","checked");
				}
			}
			else if(n.type == 2){
				//1.增加字段
				appendfieldType(n.name,n.title,n.type,i);
				//2.设置字段
				 
				$("#sl-showtype-"+n.name).attr("value",n.type);
				//增加判断
				if(n.decide!=null){
					setDecides(n.name,n.decide);
				}
			}else if(n.type == 3){
				//1.增加字段
				appendfieldType(n.name,n.title,n.type,i);
				//2.设置字段
				 
				$("#sl-showtype-"+n.name).attr("value",n.type);
				$("#txt-customfield-"+n.name).val(n.content);
			}
		});
	}
}

function addfield(code,name) {
	/// <summary>增加一个字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	var fields = $("#field-attr-fields");
	//先清空内容
	fields.empty();
	fieldsTabs.empty();
	$("<div/>").appendTo(fields)
		.addClass("fields-attr-container clearfix")
		.attr('id','field-container-' + code)
		.append(addfieldType1(code, name));
	addfieldTab(code, name, true).addClass("cur fst");
}
function appendfield(code,name) {
	/// <summary>附加一个字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	var fields = $("#field-attr-fields");
 	$("<div/>").appendTo(fields)
		.addClass("fields-attr-container clearfix")
		.attr('id','field-container-' + code)
		.append(addfieldType1(code, name)).hide();

	addfieldTab(code, name);
}
function appendfieldType(code,name,type,i) {
	/// <summary>附加一个字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <param name="type" type="String">类型</param>
	var fields = $("#field-attr-fields"),
		s = $("<div/>").appendTo(fields)
		.addClass("fields-attr-container clearfix")
		.attr('id','field-container-' + code);
	if(type == 1){
		s.append(addfieldType1(code, name));
	}
	else if(type == 2){
		s.append(addfieldType2(code, name));
	}
	else if(type == 3){
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
		options = i18n.field_types;
		sloption = options ? options.split(','):[];

	content = "<div class=\"list-row fl\">"+i18n.field_showtype+":";
	content = content + "<select name='slshowtype' onchange=\"showTypeChanged('"+code+"','"+name+"','1');\" id=\"sl-showtype-" + code + "\">";
	for(var i=0;i<sloption.length;i++){
		if(i==0){
			content = content + "<option selected='selected' value='" + (i+1) + "'>" + sloption[i] + "</option>";
		}
		else{
			 content = content + "<option value='" + (i+1) + "'>" + sloption[i] + "</option>";
		}
	}
	content = content + "</select>";
	content = content + "</div>";
	content = content + "<div class=\"list-row fl\"><input class=\"fl\" id=\"tooltip-"+code+"\" type=\"checkbox\" />" + "<label for=\"tooltip-"+code+"\" class=\"fl\">"+i18n.field_tooltip+"</label>" + "</div>";      

	return content;
}
function addfieldType2(code, name) {
	/// <summary>增加判断显示类型的字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <returns type="String" />

	var content,
		options = i18n.field_types;
		sloption = options ? options.split(','):[];

	content = "<div class=\"list-row\">"+i18n.field_showtype+":";
	content = content + "<select name='slshowtype' onchange=\"showTypeChanged('"+code+"','"+name+"','2');\"   id=\"sl-showtype-" + code + "\">";
	for(var i=0;i<sloption.length;i++){
		if(i==1){
			content = content + "<option selected='selected' value='" + (i+1) + "'>" + sloption[i] + "</option>";
		}
		else{
			 content = content + "<option value='" + (i+1) + "'>" + sloption[i] + "</option>";
		}
	}
	content = content + "</select>";
	content = content + "</div>";
	content = content + "<div class=\"list-row\">"+i18n.field_adddecied+":";
	content = content + "<button class='button' onclick=\"decideAdd('"+code+"','0');\" id='decide-" + code + "' >"+i18n.field_add+"</button>";
	content = content + "<button class='button' onclick=\"decideAdd('"+code+"','1');\" id='decide-" + code + "' >"+i18n.field_add1+"</button>";
	content = content + "<button class='button' onclick=\"decideAdd('"+code+"','2');\" id='decide-" + code + "' >"+i18n.field_add2+"</button>";
	content = content + "</div>";
	content = content + "<ul id='ul-decide-"+code+"' class='decied-list'>";       
	content = content + "</ul>";

	return content;

}
function addfieldType3(code, name) {
	/// <summary>增加自定义显示类型的字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	/// <returns type="String" />

	var content,
		options = i18n.field_types;
		sloption = options ? options.split(','):[];

	content = "<div class=\"list-row\">"+i18n.field_showtype+":";
	content = content + "<select name='slshowtype' onchange=\"showTypeChanged('"+code+"','"+name+"','3');\"  id=\"sl-showtype-" + code + "\">";
	for (var i = 0; i < sloption.length; i++) {
		if(i==2){
			content = content + "<option selected='selected' value='" + (i+1) + "'>" + sloption[i] + "</option>";
		}
		else{
			 content = content + "<option value='" + (i+1) + "'>" + sloption[i] + "</option>";
		}
	}
	content = content + "</select>";
	content = content + "</div>";
	content = content + "<div class=\"list-row\">"+i18n.field_customfield+":</div><textarea class='customfield' id=\"txt-customfield-"+code+"\"></textarea>";
	return content;
}
function showTypeChanged(code,name,oldtype) {
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
		if (sl.val() ==2) {
			$("#field-container-" + code).html(addfieldType2(code, name));
			
		}
		if (sl.val() == 3) {
			$("#field-container-" + code).html(addfieldType3(code,name));
		}
	}
}
function addfieldTab(code,name,fst){
	var li = $("<li/>").appendTo(fieldsTabs).click(function(){
			fieldsTabs.find("li").removeClass("cur");
			$(this).addClass("cur");
			$("#field-attr-fields").find(".fields-attr-container").hide();
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
				updatefieldisplay(code,name);
				//更新排序字段
				sortFiledRemove(code);
				
			}
			event.preventDefault();
			event.stopPropagation();
		}),
		text = $("<span/>").appendTo(li).html(name);
	return li;
}
function updatefieldisplay(code,name){
	/// <summary>更新字段显示</summary>
	if($("td[isdroped='true']",columnContainer).length==1){
		var li = $("td[isdroped='true']",columnContainer);
		var fields = li.data("field-data");
		if(fields!=null){
			var newfields = [];
			$.each(fields,function(i,n){
				if(n.code != code){
					newfields.push(n);
				}
				
			});
		}
		li.removeData("field-data");
		li.data("field-data",newfields);
		saveAttrs(li);

		var attrData = li.data("field-attr-data");
		if(attrData!=null){
			
			if(attrData.template!=null){
				var replaceName = "["+name+"]";
				$("#txt-showcontent").val(attrData.template.replace(replaceName,""));
			}
		}
	}
}
function setSortfields(code ,name){
	/// <summary>设置排序字段</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="name" type="String">字段名称</param>
	
		hasRepeat = false;
	//1.过滤重复
		$("option", slSortfield).each(function (i, item) {
		if(item.value == code){
			hasRepeat = true;
		}
	});
	if(!hasRepeat){
		//2.附加选项
	    $("<option/>").appendTo(slSortfield).val(code).html(name);
	}
}
function onlyCharNumberChiness(obj){
	var p1 = /^[^&<>'"!]+$/i;
	if(!e5.utils.regExpTest(p1,obj.value)){
		alert(i18n.charnumberchiness);
		$(obj).focus();
		return;
	}
}
function decideAdd(code,type) {
	/// <summary>增加判断选项</summary>
	/// <param name="code" type="String">字段编码</param>
	var ul = $("#ul-decide-" + code);
	if (ul != null) {
		var index = ul.find("li").length+1;
		var decideType = getDecideType(type);
		var sb = new Array();
		sb.push("<li id='ul-decide-li-" + index.toString() + "'  name='"+type+"'>");
		sb.push("<div>")
		sb.push(i18n.valueof+decideType+"<input class='decide-input' name='decide-key' id=\"li-decide-value" + code + index.toString() + "\" type=\"text\"  onblur=\"onlyCharNumberChiness(this);\" value=\"1\" />"+i18n.onvalue+"，");
		sb.push(i18n.showimg+"：<img name='decide-icon' class='decide-img'  id=\"li-decide-icon" + code + index.toString() + "\" alt=\"\" src=\"../images/decied_default1.gif\" />");
		sb.push("&nbsp;&nbsp;<button class='button' onclick=\"selectIcon('" + code + "','" + index + "');\">" + i18n.selecticon + "</button>,");
		sb.push(i18n.showtext+"：<input class='decide-input' name='decide-value' id=\"li-decide-txt" + code + index.toString() + "\" type=\"text\"  onblur=\"onlyCharNumberChiness(this);\" />");
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
function setDecides(code,enums){
	/// <summary>选择图标</summary>
	/// <param name="code" type="String">字段编码</param>
	/// <param name="enums" type="Array">字段编码</param>
	var ul = $("#ul-decide-" + code);
	if(enums!=null && ul != null){
		$.each(enums,function(i,k){
			var index = i+1;
			
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
			sb.push(i18n.valueof+getDecideType(type)+"<input class='decide-input' name='decide-key' id=\"li-decide-value" + code + index.toString() + "\" type=\"text\" value='" + k.key + "' />"+i18n.onvalue+"，");
			sb.push(i18n.showimg+"：<img name='decide-icon' class='decide-img'  id=\"li-decide-icon" + code + index.toString() + "\" alt=\"\" src=\""+imgurl+"\" />");
			sb.push("&nbsp;&nbsp;<button class='button' onclick=\"selectIcon('" + code + "','" + index + "');\">" + i18n.selecticon + "</button>,");
			sb.push(i18n.showtext+"：<input class='decide-input' name='decide-value' id=\"li-decide-txt" + code + index.toString() + "\" type=\"text\" value='" + k.value + "' />");
			sb.push("<button class='button' onclick=\"decideDelete('" + code + "','" + index + "');\">" + i18n._delete + "</button>");
			sb.push("</div>")
			sb.push("<div class='decide-tooltip'>"+i18n.imgtexttooltip+"</div>")
			sb.push("</li>");
			ul.append(sb.join(''));
		});
	}
}
function selectIcon(code,index) {
	/// <summary>选择图标</summary>
	/// <param name="code" type="String">字段编码</param>
	//1.获取图标
	$.ajax({url:"../e5listpage/cust_iconViewReader.do",dataType:"json", async:false, success:function(data) {
		if(data!=null){
			//清空内容
			$("#icon-list ul").html("");
			$.each(data,function(i,n){
				$("#icon-list ul").append("<li style='float:left;border:1px solid green;padding:5px;margin:5px;'><img onclick=\"setIcon(this);\" name=\""+code+index.toString()+"\"  id=\""+code+"-"+index.toString()+"-"+  n.ID + "\" alt=\""+n.description+"\" title=\""+n.description+"\" src=\"../"+n.url+"\" /></li>");
			});
		}
	}});
	//2.显示图标
	e5.dialog({type:'id',value:'icon-list'},{title:i18n.field_selection,id:"iconview"}).show();
}
function setIcon(obj){
	/// <summary>设置图标</summary>
	/// <param name="obj" type="Object">img</param>
	var img = $("#"+"li-decide-icon"+$(obj).attr("name"));
	img.attr("src",$(obj).attr("src"));
	img.attr("alt",$("#li-decide-txt"+$(obj).attr("name")).val());
	img.attr("title",$("#li-decide-txt"+$(obj).attr("name")).val());
	//关闭窗口
	e5.dialog.close("iconview");
}
function decideDelete(code,index) {
	/// <summary>删除判断分支</summary>
	/// <param name="code" type="String">字段编码</param>
	if (confirm(i18n.confirmok)) {
		var li = $("#ul-decide-li-" + index);
		if (li != null) {
			li.remove();
		}
	}
	
}
function sortFiledRemove(fieldcode){
	/// <summary>删除一个排序字段</summary>
	/// <param name="code" type="String">字段编码</param>
	slSortfield.find("option").each(function(i,n){
		if($(this).val() == fieldcode){
			$(this).remove();
		}
	});
}
function resizeHandle(){
	var fieldContentContainer = $("#field-content-container"),
		fieldAttr = $("#field-attr"),
		fieldArea = $("#field-area"),
		fieldList = $("#field-list"),
		btnArea = $("#btn-area"),
		fieldWorkspaceContainer = $("#field-workspace-container"),
		fieldContentContainerH = $("body").data("resize-special-event").h - btnArea.outerHeight(true) - fieldContentContainer.offset().top - parseInt(fieldContentContainer.css("margin-top"))-parseInt(fieldContentContainer.css("margin-bottom"))-10,
		fieldListH = fieldContentContainerH - fieldList.offset().top + fieldArea.offset().top,
		fieldAttrH = fieldContentContainerH - ( fieldAttr.offset().top - fieldWorkspaceContainer.offset().top) - parseInt(fieldAttr.css("padding-top")) - parseInt(fieldAttr.css("padding-bottom"))-10;
	fieldContentContainer.height(fieldContentContainerH);
	fieldList.height(fieldListH);
	fieldAttr.height(fieldAttrH);
}