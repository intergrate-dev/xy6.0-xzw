var col_module = {
    closed: true,
    columnFor : "btnColumn", // 选择栏目的对应input名
    init : function() {
        $("#form").attr("action", "../../xy/module/ModuleSubmit.do");
		
        var colID = e5_form.getParam("colID");
        $("#cm_columnID").val(colID);
        $("#cm_targetColumn").attr("readonly", true);

        if ($("#DocID").val() != 0){
			//修改时，类型只读
			$("#cm_type").prop("disabled", true);
        }
        //若类型不是普通栏目、订阅号，则隐藏下面的“对应栏目”一行。
        var options=$("#cm_type option:selected");
        var col_type=options.val();
        if(col_type!=0&&col_type!=1){
            $("#SPAN_cm_targetColumn").css("display","none");
        }
        //为选择栏目附点击事件
        $("#btnColumn").click(col_module.selectColumn);
        $("#cm_type").change(col_module.hideSelectColumn);
        $("#cm_count").removeClass("validate[required,custom[integer]]") ;
        $("#cm_count").addClass("validate[required,custom[integer],max[10]]") ;
    },
    // 点击选择主栏目
    selectColumn : function(evt) {
        //取出租户下的发布库
        var docID=$("#DocID").val();
        var docLibID=$("#DocLibID").val();
        var name ;
        var id ;
        var ch ;
        $.ajax({
            url: "../../xy/article/RecommendChannel.do?DocID="+docID+"&DocLibID="+docLibID, async: false,
            dataType:"json",
            success: function (data) {
                name=data.id+"_column";
                id=name+"ID";
                ch=data.id;
            }
        });
        col_module.columnFor = "btnColumn";
        // 顶点位置
        var pos = e5_form_event._getDialogPos($("#btnColumn"));
        pos.left = (parseInt(pos.left) - 100) + "px";

        var ids=$("#cm_targetColumnID").val();
        var dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=radio&ids=" +ids
            + "&ch=" + ch + "&siteID=" + e5_form.getParam("siteID");
        e5_form_event.curDialog = e5.dialog({
            type : "iframe",
            value : dataUrl
        }, {
            showTitle : false,
            width : pos.width,
            height : 250,
            pos : pos,
            resizable : false
        });
        e5_form_event.curDialog.show();
    },
    hideSelectColumn : function() {
        var col_type=$("#cm_type option:selected").val();
        if(col_type!=0&&col_type!=1){
            $("#SPAN_cm_targetColumn").css("display","none");
        }else{
            $("#SPAN_cm_targetColumn").css("display","block");
        }
    }
};
$(function(){
    col_module.init();
});

//复制以使用表单定制中的方法
e5_form_event = {
    //---各种事件、回调事件---
    curDialog : null,
    _getDialogPos : function(el) {
        function Pos (x, y) {
            this.x = x;
            this.y = y;
        }
        function getPos(el) {
            var r = new Pos(el.offsetLeft, el.offsetTop);
            if (el.offsetParent) {
                var tmp = getPos(el.offsetParent);
                r.x += tmp.x;
                r.y += tmp.y;
            }
            return r;
        }
        var p = getPos(el);

        //决定弹出窗口的高度和宽度
        var dWidth = 400;
        var dHeight = 300;

        var sWidth = document.body.clientWidth; //窗口的宽和高
        var sHeight = document.body.clientHeight;

        if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
        if (dHeight + 30 > sHeight) dHeight = sHeight - 30;

        //顶点位置
        var pos = {left : p.x +"px",
            top : (p.y + el.offsetHeight - 1)+"px",
            width : dWidth,
            height : dHeight
        };
        if (pos.left + dWidth > sWidth)
            pos.left = sWidth - dWidth;
        if (pos.top + dHeight > sHeight)
            pos.top = sHeight - dHeight;

        return pos;
    },
    //取消按钮。调after.do解锁
    doCancel : function(e) {
        if (!confirm("您确定要关闭吗？"))
            return false;

        window.onbeforeunload = null;

        $("#btnSave").disabled = true;
        $("#btnCancel").disabled = true;

        e5_form_event.doExit();
    },
    //关闭窗口。调after.do解锁
    beforeExit : function(e) {
        if (!confirm("您确定要关闭吗？"))
            return false;

        e5_form_event.doExit();
    },
    doExit : function(e) {
        var dataUrl = "../../e5workspace/after.do?UUID=" + article.UUID;
        if (jabbarArticle=="true")
        {window.opener='';
            window.close();
        }
        //若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
        else if (e && e5_form_event.isChrome())
            window.open(dataUrl, "_blank", "width=10,height=10");
        else
            window.location.href = dataUrl;
    },
    isChrome : function() {
        var nav = e5_form_event.navigator();
        return nav.browser == "chrome";
    },
    navigator : function(){
        var ua = navigator.userAgent.toLowerCase();
        // trident IE11
        var re =/(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
        var m = ua.match(re);

        var Sys = {};
        Sys.browser = m[1].replace(/version/, "'safari");
        Sys.ver = m[2];
        return Sys;
    }
};

//保存提交前，把类型改为有效。用于修改操作。
e5_form.event.otherValidate = function() {
	$("#cm_type").prop("disabled", false);
	return true;
}

    function columnCancel() {
        if (e5_form_event.curDialog)
            e5_form_event.curDialog.closeEvt();

        e5_form_event.curDialog = null;
    }
        //栏目选择窗口关闭，回调函数
    function columnClose(filterChecked, checks) {
            // [ids, names, cascadeIDs]
            var name = col_module.columnFor;
            $("#cm_targetColumnID").val(checks[0]);
            $("#cm_targetColumn").val(checks[1]);
            columnCancel();
        }




