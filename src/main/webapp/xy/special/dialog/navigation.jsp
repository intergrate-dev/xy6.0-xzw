<%@include file="../../../e5include/IncludeTag.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="../third/bootstrap-3.3.5-dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="../export/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="../export/css/header.css"/>
    <link rel="stylesheet" type="text/css" href="../export/css/sliderBar.css"/>
    <link rel="stylesheet" type="text/css" href="../export/css/editor.css"/>
    <link rel="stylesheet" type="text/css" href="../export/css/sidebar-panel.css"/>
    <link rel="stylesheet" type="text/css" href="../export/css/model.css"/>
    <link rel="stylesheet" type="text/css" href="../export/css/navMenuSet.css"/>
    <script type="text/javascript" src="../../../e5script/jquery/jquery.min.js"></script>
    <style type="text/css">
        .frm{
            height:100%;
            width: 100%;
        }
        .columnList{
            overflow: hidden;
        }
    </style>
</head>
<body style="overflow: hidden;">
<!--悬浮框开始-->
<div class="navMenuSet posAbsolute" style="display:block;">
    <div class="navMenuSet-tit">
        <div class="pull-left mgl20 font16">
            <b>导航菜单设置</b>
        </div>
        <div class="pull-right mgr20">
            <img id="doClose" class="btn-hide" src="../export/images/navMenu/navMenu1.png"/>
        </div>
    </div>
    <div class="columnList bgf pull-left clearfix">
        <iframe name="frmColumn" id="frmColumn" src="" class="frm"></iframe>
        <script type="text/javascript">
            //从后台获取参数
            var siteID = parent.special.siteID;
            var ch = "<c:out value="${param.ch}"/>";
            var type = "<c:out value="${param.type}"/>";
            var ids = "<c:out value="${param.ids}"/>";

            var newIds = "";

            //初始化-直接在弹出窗口中设置iframe的src
            $(function() {
                var url = "../../../xy/column/SpecialNavigation.jsp?siteID=" + siteID
                                + "&ids=" + ids
                                + "&ch=" + ch
                        ;
                $("#frmColumn").attr("src", url);
            });
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
    </div>
    <div class="pull-right column-right clearfix">
        <ul id="column-tags" class="setColumn font14 clearfix posRelative">
            <div class="posAbsolute total font16"><b class="pa5">共选中<span id="colNum" class="num">0</span>个</b></div>
        </ul>
    </div>
    <!--自定义导航开始-->
    <div class="pull-right column-right clearfix" style="position:absolute;right:0;bottom:85px;border: 2px solid #eee;padding:10px;">
    <input id="navName" style="border: solid 1px #C3B8B8;padding:4px" class="pic-style" type="text" placeholder="菜单名称" value="">
    <input id="navAdd" class="pull-right factive pic-style-btn" style="width: 124px; height:82px;" type="button" value="添加">
    <input id="navLink" style="border: solid 1px #C3B8B8;padding:4px" class="pic-style mgt10" type="text" placeholder="http://" value="">
   <!-- <input id="navAdd"  class="" type="button" name="" value="添加">-->
    </div>
    <!--自定义导航结束-->
    <div class="text-center btn-content">
        <input id="doSubmit" class="btn fontWeight mgt20 mgr15" type="button" name="" value="确定"/>
        <input id="doClean" class="btn fontWeight mgt20 mgr15" type="button" name="" value="清空"/>
        <input id="doCancel" class="btn fontWeight mgt20" type="button" name="" value="取消"/>
    </div>
    <script type="text/javascript">
        $(function(){
            //清空所有已选栏目
            function removeAllChecks(){
                $("#frmColumn")[0].contentWindow.removeAll();
                $("#column-tags").find("li").remove();
                $("#colNum").html("0");
            }
            //取已选栏目id,用","分隔
            function getCheckedColId(){
                var lis = $("#column-tags li");
                var ids="";
                $.each(lis,function(){
                    var id = $(this).attr("data-id");
                    if(id.indexOf("-11")=="-1"){
                        ids += id+",";
                        }
                });
                ids = ids.substring(0,ids.length-1);
                return ids;
            }
            //获取已选栏目json数据
            function getColumnsJson(){
                var ids = getCheckedColId();
                var LiLeng =$("#column-tags").find("li").length;
                if(ids.length<1 && LiLeng <1) return;
                if(ids.length<1 && LiLeng >0) {
                    var dataNew=[];
                    $("#column-tags").find("li").each(function(){
                    var _this=$(this);
                    if(_this.attr("data-id").indexOf("-11")!="-1"){
                    var dataId=_this.attr("data-id");
                    var title=_this.attr("data-title");
                    var link=_this.attr("data-link");
                    var arr='{"id":"'+dataId+'","title":"'+title+'","link":"'+link+'"}';
                    var arr=JSON.parse(arr);
                    dataNew.push(arr);
                    }
                    });
                    parent.window.LEDialog.dialogConfirm(dataNew);
                };

                var url = "../../../xy/special/getColumnsJson.do?docIDs="+ids;
                $.ajax({
                    type:'get',
                    url:url,
                    success:function(data){
                        var dataNew=data;
                         var  sortArr=[];
                        $("#column-tags").find("li").each(function(){
                        var _this=$(this);
                            if(_this.attr("data-id").indexOf("-11")!="-1"){
                                var dataId=_this.attr("data-id");
                                var title=_this.attr("data-title");
                                var link=_this.attr("data-link");
                                var arr='{"id":"'+dataId+'","title":"'+title+'","link":"'+link+'"}';
                                var arr=JSON.parse(arr);
                                dataNew.push(arr);
                            }
                            //将添加后的的liId推入数组
                            sortArr.push(_this.attr("data-id"));

                        });

                        //根据li的顺序对dataNew排序
                        var arrLeng=sortArr.length;
                        var dataLen=dataNew.length;
                        var sortJson=[];
                        for(var i=0;i<arrLeng;i++){
                            for(var j=0;j<dataLen;j++){
                                if(dataNew[j].id==sortArr[i]){
                                    sortJson.push(dataNew[j])
                                }
                            }
                        }

                        parent.window.LEDialog.dialogConfirm(sortJson);
                        //console.info(JSON.stringify(dataNew));
                        //console.info(sortArr);
                        //console.info(JSON.stringify(sortJson))
                    }
                });
            }
            function closeDialog(){
                parent.window.LEDialog.closeDialog();
            }

            $('#doClean').click(removeAllChecks);
            $('#doSubmit').click(getColumnsJson);
            $('#doCancel').click(closeDialog);
            $('#doClose').click(closeDialog);

            $('#navAdd').click(function(){
                var _navName=$("#navName").val();
                var _navLink=$("#navLink").val();
                //查重
                var isRepeat=false;
                $("#column-tags").find("li").each(function(){
                    var _text=$(this).text();
                    if(_text==_navName){
                        isRepeat=true;
                        return;
                    }
                });

                //查空
                if(_navName==""){
                    alert("名称不允许为空，请输入内容！");
                }else{
                    if(isRepeat){
                        alert("不允许重名，请重新输入！");
                    }else{

                        var timeStamp = new Date().getTime();
                        var dataId="-11"+timeStamp;
                        var str='<li id="-1_col" data-id="'+dataId+'" data-title="'+_navName+'" data-link="'+_navLink+'" class="pull-left">'+_navName+'<img id="-1_del" data-tid="rs_tree_-1" data-id="-1" class="posAbsolute closeBtn" src="../export/images/navMenu/navMenu2.png" alt=""></li>';
                        $("#column-tags").append(str);
                        var num=$("#colNum").html();
                        var num = parseInt(num)+1;
                        $("#colNum").html(num);
                    }
                }
                $("#navName").val("");
                $("#navLink").val("");

            });
            $("#column-tags").on({"click":function(){
                if($(this).attr("data-id")=="-1"){
                    $(this).parent().remove();
                    var num=$("#colNum").html();
                    var num = parseInt(num)-1;
                    $("#colNum").html(num);
                }
            }},".closeBtn")
        });
    </script>
</div>
<!--悬浮框结束-->
</body>
</html>
