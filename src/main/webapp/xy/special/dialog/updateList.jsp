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
        .select-num{
            font-size:14px;
            font-weight:200;
        }
        .select-num input{
            border: solid 1px #C3B8B8;
            padding:4px;
            outline:none;
            width:100px;
            margin:0 10px;
            text-align:center;
        }
        .select-type{
            margin-top:10px;
            font-size:14px;
            font-weight:200;
        }
        .select-type input{
            margin:10px 5px 0 10px;
        }
        .select-type label{
            font-weight:200;
        }
    </style>
</head>
<body style="overflow: hidden;">
<!--悬浮框开始-->
<div class="navMenuSet posAbsolute" style="display:block;">
    <div class="navMenuSet-tit">
        <div class="pull-left mgl20 font16">
            <b>动态更新设置</b>
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
    <!--动态更新设置开始-->
    <div class="pull-right column-right clearfix" style="position:absolute;right:0;bottom:85px;border: 2px solid #eee;padding:10px;">
        <div class="select-num">从选中的栏目中读取<input id="updateNum" type="number" placeholder="请输入条数" value="">条</div>
        <div class="select-type" id="typeBox">稿件类型：<input type="checkbox" name="type" id="type-all" value="all"/><label for="type-all">全部</label><input type="checkbox" name="type" id="type-article" value="article"/><label for="type-article">文章</label><input type="checkbox" name="type" id="type-pic" value="pic"/><label for="type-pic">组图</label><input type="checkbox" name="type" id="type-video" value="video"/><label for="type-video">视频</label><input type="checkbox" name="type" id="type-link" value="link"/><label for="type-link">链接</label><input type="checkbox" name="type" id="type-multi" value="multi"/><label id="type-multi-label" for="type-multi">多标题</label><input type="checkbox" name="type" id="type-special" value="special"/><label for="type-special">专题</label></div>
        <div class="select-type" id="attrBox">稿件属性：
            <input type="checkbox" name="attr" id="attr-all" value="all"/>
            <label for="attr-all">全部</label>
            <input type="checkbox" name="attr" id="attr-common" value="common"/>
            <label for="attr-common">普通</label>
            <input type="checkbox" name="attr" id="attr-headline" value="headline"/>
            <label for="attr-headline">头条</label>
            <input type="checkbox" name="attr" id="attr-pic" value="pic"/>
            <label for="attr-pic">图片</label>
            <input type="checkbox" name="attr" id="attr-important" value="important"/>
            <label for="attr-important">重要</label>
            <input type="checkbox" name="attr" id="attr-other" value="other"/>
            <label for="attr-other">其它</label>
        </div>
    </div>
    <!--动态更新设置结束-->
    <div class="text-center btn-content">
        <input id="doSubmit" class="btn fontWeight mgt20 mgr15" type="button" name="" value="确定"/>
        <input id="doClean" class="btn fontWeight mgt20 mgr15" type="button" name="" value="清空"/>
        <input id="doCancel" class="btn fontWeight mgt20" type="button" name="" value="取消"/>
    </div>
    <script type="text/javascript">
        $(function(){
            //获取前台数据
            var PicJson = parent.window.LEDialog.getData();
            var showMulti=PicJson.showMulti;
            var selecteds=PicJson.selecteds;
            var parentTree = PicJson.parentTree;
            var selectednum=PicJson.selectednum;
            var columntype=PicJson.columntype;
            var articleattr=PicJson.article_attr;

            if(showMulti){
                $("#type-multi").show();
                $("#type-multi-label").show();
            }else{
                $("#type-multi").hide();
                $("#type-multi-label").hide();
            }
            if(selecteds){
                window.onload=function(){
                    setTimeout(function(){$("#frmColumn")[0].contentWindow._checkNode(selecteds,parentTree)},1000);
                }
            }

            if(selectednum){
                $("#updateNum").val(selectednum);
            }

            if(columntype){
                $("input[name='type'][type='checkbox']").each(function(){
                    var _this=$(this);
                    $.each(columntype,function(index,value){
                        if(_this.val()==value){
                            _this.attr("checked","checked")
                        }
                    });
                })
                var arr=[];
                $("input:visible[name='type'][type='checkbox'][checked!='checked']").each(function(){
                    arr.push($(this).attr("value"));
                })
                if(arr.length==1 && arr[0]=="all"){
                    $("#type-all").attr("checked","checked");
                }
            }

            if(articleattr){
                $("input[name='attr'][type='checkbox']").each(function(){
                    var _this=$(this);
                    $.each(articleattr,function(index,value){
                        if(_this.val()==value){
                            _this.attr("checked","checked");
                        }
                    });
                })
                if($("#attr-all").attr("checked")){
                    $("#attr-all").siblings('input:visible').attr('checked',true);
                }
            }

            //清空所有已选栏目
            function removeAllChecks(){
                $("#frmColumn")[0].contentWindow.removeAll();
                $("#updateNum").val("");
            }
            //取已选栏目id,用","分隔
            function getCheckedColId(){
                var lis = $("#column-tags li");
                var ids="";
                $.each(lis,function(){
                    var id = $(this).attr("data-id");
                        ids += id+",";
                });
                ids = ids.substring(0,ids.length-1);
                return ids;
            }
            //获取已选栏目json数据
            function getColumnsJson(){
                var ids = getCheckedColId();
                var parentTree = "";
                $("#frmColumn").contents().find("#rs_tree").find("span.checkbox_true_full").each(function(){
                    var _this = $(this);
                    _this.closest("li").parentsUntil("#rs_tree", "li").each(function(){
                        var _a = $(this).children("a");
                        var til = _a.attr("title");
                        var li_level = _a.attr("class");
                        //node:["{'level':'level1'---'columnId':'6'}", "{'level':'level0'---'columnId':'1'}"]
                        til = til.substring(til.indexOf("[")+1, til.indexOf("]"));
                        var addNode = "{'level':'"+li_level + "'---'columnId':'" + til+"'}"
                        if (parentTree.indexOf(addNode) == -1) {
                            parentTree += addNode + "|";
                        }
                    })
                });
                parentTree = parentTree.substr(0, parentTree.length-1);
                var count=$("#updateNum").val() || 1;
                if(count<=0) return;
                //稿件类型
                var articletype='';
                $("input[name='type'][type='checkbox']").each(function(){
                    var _this=$(this);
                    if(_this.attr('checked') && _this.attr('value')!='all'){
                        articletype+=_this.val()+'|';
                    }
                });
                if(!articletype){
                    alert("请选择稿件类型！");
                    return;
                }
                articletype=articletype.substring(0,articletype.length-1);
                //稿件属性
                var articleattr='';
                if($("input[name='attr'][type='checkbox'][value='all']").attr("checked")){
                    articleattr='all';
                }else{
                    $("input[name='attr'][type='checkbox']").each(function(){
                        var _this=$(this);
                        if(_this.attr('checked') && _this.attr('value')!='all'){
                            articleattr+=_this.val().toUpperCase()+'|';
                        }
                    });
                    if(!articleattr){
                        alert("请选择稿件属性！");
                        return;
                    }
                    articleattr=articleattr.substring(0,articleattr.length-1);
                }
                if(ids.length<1) return;
                //拼接查询条件  栏目id 类型 条数
                var data="{'columnid':["+ids+"],'columntype':'self', 'articletype':'"+articletype+"','parentTree':'"+parentTree+"','article_attr':'"+articleattr+"','start':0, 'count':"+count+"}";
                parent.window.LEDialog.dialogConfirm(data);
            }
            function closeDialog(){
                parent.window.LEDialog.closeDialog();
            }

            $('#doClean').click(removeAllChecks);
            $('#doSubmit').click(getColumnsJson);
            $('#doCancel').click(closeDialog);
            $('#doClose').click(closeDialog);
            //多选操作
            $('#typeBox input,#attrBox input').on('change',function(){
                var _this=$(this);
                if(_this.attr('value')=='all'){
                    if(_this.attr('checked')){
                         _this.siblings('input:visible').attr('checked',true);
                    }else{
                         _this.siblings('input:visible').attr('checked',false);
                    }
                }else{
                    _this.parent().find('input:first').attr('checked',false);
                }
            })
        });
    </script>
</div>
<!--悬浮框结束-->
</body>
</html>
