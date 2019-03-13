<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false" />
<html>
<head>
<base href="<%=basePath%>">
<title><i18n:message key="org.user.form.list.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="e5script/jquery/jquery.min.js"></script>
<style>
    /*body,iframe{margin:0; padding:0}*/
    /*.frm {*/
	    /*border: 0;*/
	    /*width: 500px;*/
	    /*min-height: 450px;*/
    /*}*/
	body{
		padding-left:4px;
		padding-right:4px;
		background-color: #ffffff;
	}
	.main{
		margin-left: 50px;
	}
	.frm {
		border: 0;
		/*width: 100%;*/
		/*height: 430px;*/
		margin-left:40px;
		width: 350px;
		/*height: 300px;*/
	}
	.border_solid{
		border: 1px solid #ddd;
	}
	.row{
		font-family: "microsoft yahei";
		font-size: 14px;
		font-weight: bold;
		padding-top: 14px;
		padding-bottom: 8px;
	}
	.left{
		float: left;
		/*margin-left: 30px;*/
		/*border:solid 1px #ddd;*/
		/*overflow: auto;*/
		width : 350px;
		height: 414px;
		/*height : 260px;*/
		overflow: auto;
	}
	.right{
		/*width: 300px;*/
		/*height: 260px;*/
		/*margin-left:20px;*/
		/*border:solid 1px #ddd;*/
		/*overflow: auto;*/
	}
	.left table{
		width: 100%;
		border-collapse:collapse;
		/*border:1px solid #ddd;*/
		/*border-left:1px solid #ddd;*/
		/*border-top:1px solid #ddd;*/
	}
	.left table td{
		border:1px solid #ddd;
		height: 35px;
		padding-left: 17px;
		text-align:left;
	}
	.left table tr:hover{
		cursor: pointer;
		background-color:#E4E8EB;
	}
	.left table span{
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		font-size: 13px;
	}
	.bottom{
		text-align: center;
		margin-top: 10px;
	}
	#ok{
		background: #00a0e6;
		margin-right: 10px;
	}
	.btngroup{
		margin: 5px 5px;
		font-family: microsoft yahei;
		color: #fff;
		border: none;
		background: #b1b1b1;
		border-radius: 4px;
		padding: 5px 20px;
		font-size: 12px;
	}
	#siteList .selected{
		/*border: 1px solid rgba(22, 155, 213, 1);*/
		background: url(../../xy/column/image/u55.png) right center no-repeat;
	}
	#siteList .current{
		/*border: 1px solid rgba(22, 155, 213, 1);*/
		background: rgba(204, 235, 248, 1) url(../../xy/column/image/u59.png) right center no-repeat;
	}
</style>
</head>
<body onload="handlerRisize()" onresize="handlerRisize()">
<div class="main">
	<div>
		<div>
			<p class="row">请选择站点及站点对应的栏目</p>
		</div>
		<div class="parent">
			<div class="left">
				<table id="siteList">
				</table>
			</div>
			<div class="right">
                <span class="">
		            <iframe name="frmColumn" id="frmColumn" src="" class="frm"></iframe>
                </span>
			</div>
		</div>
	</div>
	<div class="bottom">
	    <button id="ok"  class="btngroup" type="button" >确定</button>
		<%--<button id= "cancel"  class="btngroup" type="button">取消</button>--%>
	</div>
</div>
</body>
<script>
    //从后台获取参数
    var siteID = "<c:out value="${siteID}"/>";
    var docIDs = "<c:out value="${DocIDs}"/>";
    var docLibID = "<c:out value="${DocLibID}"/>";
    var UUID = "<c:out value="${UUID}"/>";
    var groupID = "<c:out value="${groupID}"/>";
    var ch = "<c:out value="${ch}"/>";
    var selectedCols = {};
    var currSiteID = "";
    var type = "<c:out value="${type}"/>";
    var ids = "<c:out value="${ids}"/>";
    //var ColSite = "<%=request.getParameter("ColSite")%>";
    var newIds = "";

    $(function() {
        //初始化所有的站点
		initSite();
        //初始化站点设置
        initSetting();
		//点击站点，直接在弹出窗口中设置iframe的src
        $(".site").click(function(){
            $("#frmColumn").addClass("border_solid");
            var oldSid = $(".current").attr("sid");
            //将之前站点选择的栏目添加到selectedCols
            // console.log(frmColumn.window.parentID);
            // console.log(document.getElementById("frmColumn").window.parentID);
            // console.log(document.getElementById("frmColumn").contentWindow);
            if(oldSid != undefined){
                var columnchecks = $("#frmColumn")[0].contentWindow.getAllFilterChecked();
                if(columnchecks != undefined){
                    columnAdd(oldSid, columnchecks);
                }
            }

            if(selectedCols["site"+oldSid] != undefined){
                $(".current").addClass("selected");
            }
            $(".site").removeClass("current");
            $(this).addClass("current");
            currSiteID = $(this).attr("sid");
            selectColumn(currSiteID);
        });
        //确定按钮
        $("#ok").click(SiteClose);
        //取消按钮
        $("#cancel").click(columnCancel);
    });
    //初始化所有的站点
    function initSite(){
        $.ajax({url:"./xy/column/ToSiteColumnAggregate.do",async:false,dataType:"json",type:"post",
            success:function(data){
                var html = "";
                for(var i=0;i<data.length;i++){
                    var site = data[i];
                    html +='<tr><td class="site" sid="'+site.id+'"><span>'+site.name+'</span></td></tr>'
                }
                $("#siteList").html(html);
            }});
	}
    //初始化设置
    function initSetting(){
        //获得左边已经选择的站点，并设置左边的站点选中的状态样式
        $.ajax({url:"./xy/column/selectedColumnAggregate.do?DocIDs="+docIDs,async:false,type:"post",dataType:"json",
            success:function(data){
                selectedCols=data;
                for(var key in selectedCols){
                    var siteId = key.substring(4);
                    $("[sid=" +siteId + "]").addClass("selected");
                }
            }
        });
	}

    //根据站点名查询对应的栏目
    function selectColumn(siteID) {
        var url = "xy/column/ColumnCheck.jsp?type=op&siteID=" + siteID
            + "&opType=ColumnAggregate&ids=" + selectedCols["site"+currSiteID]
            + "&ch=" + ch;
        $("#frmColumn").attr("src", url);
    }

    //将用户选择的栏目加入到selectedCols中
    function columnAdd(siteId, allFilterChecked){
        var colIDs = allFilterChecked[0];
        if(colIDs == '' || colIDs == null){
            selectedCols["site"+siteId]=undefined;
        }else{
            selectedCols["site"+siteId]=colIDs;
        }
        if(selectedCols["site"+siteId] != undefined){
            $(".current").addClass("selected");
        }else{
            $(".current").removeClass("selected");
        }
    }

    //当用户提交选择的栏目,实现columnClose方法,实现选中的栏目在切换站点后不消失
    function columnClose(filterChecked, allFilterChecked) {
        var colIDs = allFilterChecked[0];
        if(colIDs == '' || colIDs == null){
            selectedCols["site"+currSiteID]=undefined;
        }else{
            selectedCols["site"+currSiteID]=colIDs;
        }
        if(selectedCols["site"+currSiteID] != undefined){
            $(".current").addClass("selected");
        }else{
            $(".current").removeClass("selected");
        }

    }

    //用户选择好栏目之后，实现SiteClose方法
    function SiteClose() {
        if(currSiteID != undefined){
            var columnchecks = $("#frmColumn")[0].contentWindow.getAllFilterChecked();
            if(columnchecks != undefined){
                columnAdd(currSiteID, columnchecks);
            }
        }

        var column = "";
        $(".selected").each(function(ind,ele){
            if(ind>0) column +=",";
            var csid = $(ele).attr("sid");
            column+=selectedCols["site"+csid];
        });
        $.ajax({
            url : "xy/column/updateColumnAggregate.do",
            type : 'POST',
            data : {
                "newIds" : column,
                "siteID" : siteID,
                "docIDs" : docIDs,
                "docLibID" : docLibID,
                "UUID" : UUID
            },
            dataType : 'html',
            success : function(msg, status) {
                //成功时，调用operationSuccess(),并且写日志（opinion）
                if (status == "success") {
                    alert("操作完成。");
                }
            },
            error : function(xhr, textStatus, errorThrown) {
                //operationFailure();
                alert("访问服务器异常：" + errorThrown);
            }
        });

    }

    function columnCancel() {
        /* var url = "xy/column/ColumnCheck.jsp?type=op&siteID="
                + siteID + "&ids=" + selectedCols["site"+currSiteID] + "&ch=" + ch;


        $("#frmColumn").attr("src", url); */
    }

    //FIXME*******************************
    //不好使
    window.onbeforeunload = function(event) {

    };

    /** 对特殊字符和中文编码 */
    function encodeU(param1) {
        if (!param1)
            return "";
        var res = "";
        for ( var i = 0; i < param1.length; i++) {
            switch (param1.charCodeAt(i)) {
                case 0x20://space
                case 0x3f://?
                case 0x23://#
                case 0x26://&
                case 0x22://"
                case 0x27://'
                case 0x2a://*
                case 0x3d://=
                case 0x5c:// \
                case 0x2f:// /
                case 0x2e:// .
                case 0x25:// .
                    res += escape(param1.charAt(i));
                    break;
                case 0x2b:
                    res += "%2b";
                    break;
                default:
                    res += encodeURI(param1.charAt(i));
                    break;
            }
        }
        return res;
    }
    function handlerRisize(){
        var winH = $(window).height();
        //IE9：窗口高度读为0
        if (winH == 0) {
            var iframes = $(window.parent.document).find("iframe");
            for (var i = 0; i < iframes.length; i++) {
                winH = $(iframes[i]).height();
                if (winH > 0) break;
            }
        }
        $("#frmColumn").height(winH-5);
    }
</script>
</html>