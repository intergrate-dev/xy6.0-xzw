var icon_list;
var tabs_list;
var tabcontent_list;
var tabcontent;
var tab_add;
; $(function () {


    icon_list = $("#icon_sortlist");
    tabs_list = $("#tab_list");
    tabcontent_list = $("#tabconten_list");
    tab_add = $("#tab_add");
    tabcontent = $("#tabcontent_container");
    //得到流程操作
    getProcIcons();



    //设置添加分组事件
    tab_add.click(function () {
        tabAdd();
    });
    // 自动建立好组0
    //tabAdd();
    //设置可以拖放
    setIconContentDropable();
    //设置可以拖动排序
    setTabContentListSrotable();

    removeIconContent();

    removeTab();

    $("#btnSave").click(function () {
        saveXml();
    });
    $("#btnReset").click(function () {

        document.location.reload();
    });
});

function saveXml() {
    /// <summary>保存数据</summary>
    /*
    <root>
    <doctypeid></doctypeid>
    <flowid></flowid>
    <flownodeid></flownodeid>
    <groups>
	    <group>
	        <procid></procid>
	    </group>
    </groups>
    </root>
    */

    //保存数据
    saveData();

    var sb = new Array();

    sb.push("<?xml version='1.0' encoding='UTF-8'?>");
    sb.push("<root>");
    sb.push("<doctypeid>"+$.query.get("docTypeID")+"</doctypeid>");
    sb.push("<flowid>"+$.query.get("flowID")+"</flowid>");
    sb.push("<flownodeid>"+ $.query.get("flowNodeID")+"</flownodeid>");
    sb.push("<groups>");

    tabs_list.find("li").each(function () {

        if (!$(this).hasClass("add")) {

            if ($(this).data("icons") != null) {
                sb.push(" <group>");

                $.each($(this).data("icons"), function (i, n) {
                    sb.push(" <procid>"+n.procID+"</procid>");
                });

                sb.push("</group>");
            }
        }

    });

    sb.push("</groups>");

    sb.push("</root>");

    var dataxml = {
        groupxml:sb.join('')
    };

    $.ajax({
        type: "POST",
        url: "../e5flow/cust_flowIconGroup.do?method=resetIconGroup",
        data: dataxml,
        beforeSend: function () {
            //设置按钮不可用，防止重复提交
            $("#btnSave").attr("disabled", true);
        },

        success: function (data, textStatus) {
            $("#btnSave").attr("disabled", false);
            if (data == "OK") {
                alert(i18n.opt_success);
               

            } else {
                alert(i18n.opt_failed);
            }
        },
        error: function () {
            //请求出错处理
            $("#btnSave").attr("disabled", false);
            alert(i18n.opt_failed);
        }

    });


}

function removeTab() {
    /// <summary>删除分组</summary>
    tabs_list.find("img[name='group_del']").each(function () {

        $(this).unbind("click");
        $(this).click(function () {

            if (confirm(i18n.opt_confirm)) {

                $(this).parent().parent().remove();
              
                //设置第一个被选中
                if(tabs_list.find("li[class!='add']").length>0){
                	
                	 tabs_list.find("li:first").trigger("click");
                }
                else{
                	tabcontent_list.empty();
                	
                }
               

            }
        });
        

    });
   
}

function removeIconContent() {
    /// <summary>双击移除组内操作 </summary>
    tabcontent_list.find("li").live("dblclick", function () {
        if (confirm(i18n.opt_procconfirm)) {

            $(this).remove();
        }

    });
}

function setIconListDragable() {
    /// <summary>设置图标可以拖动 </summary>

    icon_list.find("li").draggable({
        helper: "clone",
        cursor: "move"
    }).dblclick(function(){
    	
    		dragableIcon(null,null,$(this));
    	
    	
    });
    //禁止选中
    icon_list.disableSelection();
}

function setIconContentDropable() {
    /// <summary>设置图标可以拖放 </summary>
    tabcontent.droppable({
        accept: "#icon_sortlist li",
        activeClass: "visual-draphelp-hover",
        hoverClass: "visual-draphelp-active",
        over: function (event, ui) {

        },
        drop: function (event, ui) {
            dragableIcon(event, ui,null);
        }

    });
}

function setTabContentListSrotable() {
    /// <summary>设置可以拖动排序</summary>


    tabcontent_list.sortable({
        placeholder: "ui-state-highlight",
        axis: 'x',
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

function dragableIcon(event, ui,jqobj) {
    /// <summary>设置拖放事件</summary>
	//判断是否有tab
	if(tabs_list.find("li[class!='add']").length==0){
		
		return;
	}
    var elm = ui ? ui.draggable : jqobj;
    var img = elm.find("img");
    //1.判断当前tab是否已经包含此icon
    var icon = tabcontent_list.find("img[name='" + img.attr("name") + "']");
    if (icon.length > 0) {
        alert(i18n.opt_hassameproc);
        return;
    }
    //2.插入icon
    tabcontent_list.append("<li class='clearfix'><table><tr><td class='l'></td><td class='c'><img name='" + img.attr("name") + "' id='groupicon_" + img.attr("name") + "' src='" + img.attr("src") + "' alt='" + img.attr("alt") + "' /></td><td class='c'>" + img.attr("alt") + "</td><td class='r'></td></tr></table></li>");

}

function getProcIcons() {
    /// <summary>加载节点操作 </summary>
    $.ajax({
        url: "../e5flow/cust_flowIconGroup.do?method=getProcOrderData&doctypeid=" + $.query.get("docTypeID") + "&flownodeid=" + $.query.get("flowNodeID"),
        async: false,
        dataType: "json",
        success: function (data) {

            if (data == null || data == "") {

                return;
            }
            icon_list.empty();
            $.each(data, function (i, n) {
                icon_list.append("<li class='clearfix'><table><tr><td class='l'></td><td class='c'><img name='" + n.procID + "' id='icon_" + n.procID + "' src='../" + n.iconURL + "' alt='" + n.procName + "' /></td><td class='c'>" + n.procName + "</td><td class='r'></td></tr></table></li>");
            });
            //设置可以拖动
            setIconListDragable();
            //加载默认分组
            getGroups();
        }
    });

}

function getGroups() {
    /// <summary>加载组别 </summary>
    $.ajax({
        url: "../e5flow/cust_flowIconGroup.do?method=getProcGroup&doctypeid=" + $.query.get("docTypeID") + "&flownodeid=" + $.query.get("flowNodeID"),
        async: false,
        dataType: "json",
        success: function (data) {

            if (data == null || data == "") {

                return;
            }
            /*
            [{
                "docTypeID": 0,
                "flowNodeID": 0,
                "groupNo": 0,
                "list": [
                    { "docTypeID": 1, "flowID": 0, "flowNodeID": 1, "order": 0, "procID": 14 },
                    { "docTypeID": 1, "flowID": 0, "flowNodeID": 1, "order": 1, "procID": 1 },
                    { "docTypeID": 1, "flowID": 0, "flowNodeID": 1, "order": 2, "procID": 2 },
                    { "docTypeID": 1, "flowID": 0, "flowNodeID": 1, "order": 3, "procID": 312 }
                ],
                "procCount": 4
            },
            */
            $.each(data, function (i, n) {

                //增加tab
                //var tab = $("<li id='tab_" + n.groupNo + "'><a><img name='group_del' src='../images/tab_del.gif' alt='" + i18n.opt_del + "' title='" + i18n.opt_del + "' /></a>&nbsp;&nbsp;&nbsp;&nbsp;" +i18n.opt_tab_name+ n.groupNo.toString() + "</li>");
            	var tabText = parseInt(n.groupNo,10)+1;
                var tab=$("<li id='tab_" + n.groupNo + "' class='tab clearfix'><a><img name='group_del' src='../images/tab_del.gif' alt='" + i18n.opt_del + "' title='" + i18n.opt_del + "' /></a>" + i18n.opt_tab_name + tabText.toString() + "</li>");
                tab_add.before(tab);

                if (n.list != null) {

                    $("#tabcontent_list").html();

                    var icondata = new Array();
                    $.each(n.list, function (p, k) {

                        var icon = icon_list.find("img[name='" + k.procID + "']");

                        //保存数据
                        var dataitem = {
                            docTypeID: k.docTypeID,
                            flowID: k.flowID,
                            order: k.order,
                            procID: k.procID,
                            iconURL: icon.attr("src"),
                            procName: icon.attr("alt")
                        };

                        icondata.push(dataitem);
                    });
                    $("#tab_" + n.groupNo).data("icons", icondata);



                }

            });
            setTabClick();
            removeTab();

            tabs_list.find("li:first").trigger("click");

        }
    });
}

function setTabClick() {
    /// <summary>tab页选中事件 </summary>
    tabs_list.find("li").each(function () {

        if (!$(this).hasClass("add")) {
            //防止多次执行click事件
            $(this).unbind("click");
            $(this).click(function () {
                //tab切换事件
                tabClick($(this));
            });
        }
    });
}
function saveData() {
    /// <summary>保存当前选中tab的数据 </summary>
    var tab =null;tabs_list.find("li[class='current']");
    tabs_list.find("li").each(function(i,n){
    	if($(this).hasClass("current")){
    		tab = $(this);
    	}
    });
    if (tab!=null && tab.length > 0) {


        var data = new Array();

        tabcontent_list.find("img").each(function (i, n) {

            var dataitem = {
                docTypeID: $.query.get("docTypeID"),
                flowID: $.query.get("flowID"),
                order: i,
                procID: $(this).attr("name"),
                iconURL: $(this).attr("src"),
                procName: $(this).attr("alt")
            };

            data.push(dataitem);
        });

        if (data != null && data.length > 0) {
            tab.removeData("icons");
            tab.data("icons", data);
        }

    }

}

function setData(jqobj) {
    /// <summary>设置当前</summary>
    tabcontent_list.empty();
    if (jqobj.data("icons") != null) {

        $.each(jqobj.data("icons"), function (i, n) {
            tabcontent_list.append("<li class='clearfix'><table><tr><td class='l'></td><td class='c'><img name='" + n.procID + "' id='groupicon_" + n.procID + "' src='" + n.iconURL + "' alt='" + n.procName + "' /></td><td class='c'>" + n.procName + "</td><td class='r'></td></tr></table></li>");
        });
    }

}

function clearSelClass() {
    /// <summary>清除所有选中状态数据 </summary>
    tabs_list.find("li").each(function (i, n) {
        if ($(this).hasClass("current")) {
            $(this).removeClass("current");
        }
    });
}

function tabClick(jqobj) {
    /// <summary>点击事件 </summary>

    //保存前一个选中的数据
    saveData();
    //清除选中样式
    clearSelClass();
    jqobj.addClass("current");
    //加载现有数据
    setData(jqobj);

}

function tabAdd() {

    //保存当前选中tab数据
    saveData();
    //附加新组
    var groupNumb = tabs_list.find("li").length - 1;
    var groupText = groupNumb+1;
    tab_add.before("<li id='tab_" + groupNumb + "' class='tab clearfix'><a><img name='group_del' src='../images/tab_del.gif' alt='" + i18n.opt_del + "' title='" + i18n.opt_del + "' /></a>" + i18n.opt_tab_name + groupText + "</li>");
    clearSelClass();
    $("#tab_" + groupNumb).addClass("current");
    //清空tab数据
    tabcontent_list.empty();
    setTabClick();
    removeTab();
}