<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%>
    </title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta content="IE=edge" http-equiv="X-UA-Compatible"/>
    <link rel="shortcut icon" href="export/img/ninja.png">
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css"/>

    <script type="text/javascript" src="../script/jquery/jquery.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript">
        var special = {
            siteID: "<c:out value="${siteID}"/>",
            groupID: "<c:out value="${groupID}"/>",
            docLibID: "<c:out value="${docLibID}"/>",
            docID: "<c:out value="${docID}"/>",
            templateID: "<c:out value="${templateID}"/>",
            isNew: "<c:out value="${isNew}"/>",
            UUID: "<c:out value="${UUID}"/>"
        };
        /**
         * 2）    页面设计完毕后，点保存按钮。填写名称，上传文件。
         * 3）    需要在两个表中添加记录：专题表、模板表。
         */
        jQuery(function(){
            //初始化的时候把，template放到画板当中
            checkDocId();
            jQuery.ajax({
                url: 'getDesignFile.do',
                type: "GET",
                dataType: "json",
                data: special,
                success: function(result){
                    window.sourceURL = result._location;
                    if(result.status){
                        var _html = result.htmlData;
                        var _seperator = _html.indexOf("<-------------$$$$STYLEHTML$$$$------------->") != -1 ? "<-------------$$$$STYLEHTML$$$$------------->" : "$$$$STYLEHTML$$$$";

                        var _a = _html.split(_seperator);
                        if(_a.length > 0){
                            var _style = _a[0];
                            jQuery("#special_style").append(_style);

                            var _ch = _a[1];
                            jQuery("#container").html(_ch);

                            var _containerStyle = _a[2];
                            jQuery("#container").attr("style", _containerStyle);

                            /*页面刚加载时修改script标签type并写入图表初始化配置*/
                            var lineChartJs = _a[3];
                            jQuery("#lineChart_script").attr("type","text/javascript");
                            jQuery("#lineChart_script").html(lineChartJs);

                            jQuery("#projectName").val(result.s_name);

                            LEClean.resetEntityEvent();

                            LEHistory.trigger();

                        }

                    } else{
                        var _columnHtml = '<div class="plugin ui-draggable" data-type="Column" style="display: block;"><div class="preview"><img src="export/images/sliderBar/sliderBar7.png"><span>容器</span></div><div class="view obj_click"><div class="span12 plugin-hint plugin_entity column column_style ui-sortable drag_hint_click" id="le_Column_1470643305745" data-mark="le_Column_1470643305745" data-position="border" data-color="rgb(181, 181, 181)" data-width="1px" data-style="dashed" data-radius="0px" data-positionset="true" style="width: 1024px; margin: 0px auto; padding-bottom: 20px; min-height: @{height};"></div><div class="drag_handler" style="display: block;"><ul class="diy-ctrl-ul"><i class="diy-ctrl remove" data-hint="移除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></i><i class="diy-ctrl move" data-hint="拖动"><span class="glyphicon glyphicon-move"></span></i></ul></div></div></div>';
                        _columnHtml = _columnHtml.replace(/@\{height\}/g, jQuery(window).height() + "px");
                        jQuery("#container").html(_columnHtml);
                        LEDrag.columnDraggable();
                    }

                    //成功之后根据当前已有图表生成lineChart_Map
                    var str = $("#lineChart_script").html();
                    var arr = str.split("/**nextJs**/");
                    var l = arr.length-1;
                    for (var i = 0; i < l; i++) {
                        var thisK = arr[i].split("/**id**/")[1].split("\'")[1];
                        var thisVStr =arr[i].split("/**option**/")[1];
                        var thisV=JSON.parse(thisVStr);
                        lineChart_Map.put(thisK, thisV);
                    }
                }
            });

    jQuery("#saveButton").click(function(){
        //如果没有给项目命名就弹出对话框
        var _pn = jQuery("#projectName").val();
        if(_pn === "" || _pn == "新专题"){
            jQuery("#saveDialog").modal("show");
        }else{
            if(timer){
                clearInterval(timer);
                timer = null;
            }

            jQuery("#savingDiv").show();
            containerSave("保存成功！");
            }
    });
    //更新截止日期
    jQuery('.form_date').datetimepicker({
        language: 'cn',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        minView: 2,
        forceParse: 0
    });

    jQuery("#saveDivClose").click(function(){
        jQuery("#saveDialog").modal("hide");
    });

    jQuery("#saveConfirmBtn").click(function(){
        var pn = jQuery("#saveNameText").val();
        jQuery("#saveDialog").modal("hide");
        //如果有意义，且不为空
        if(pn && jQuery.trim(pn) != ""){
            var msg;
            if(msg = checkProjectName(pn)){
                alert(msg);
                return;
            }
        } else{
            return;
        }
        pn && jQuery("#projectName").val(pn);
        if(timer){
            clearInterval(timer);
            timer = null;
        }

        jQuery("#savingDiv").show();
        containerSave("保存成功！");
    });

    /*jQuery("#saveButton").click(function(){
                //如果没有给项目命名就弹出对话框
                var _pn = jQuery("#projectName").val();
                if(_pn === "" || _pn == "新专题"){
                    var pn = prompt("请输入项目名称:", "");
                    //如果有意义，且不为空
                    if(pn && jQuery.trim(pn) != ""){
                        var msg;
                        if(msg = checkProjectName(pn)){
                            alert(msg);
                            return;
                        }
                    } else{
                        return;
                    }
                }
                pn && jQuery("#projectName").val(pn);
                if(timer){
                    clearInterval(timer);
                    timer = null;
                }

                jQuery("#savingDiv").show();
                containerSave("保存成功！");
            });*/

            function checkProjectName(_name){
                var param = {};
                param.specialName = _name;
                param.docLibId = special.docLibID;
                var _msg = null;

                jQuery.ajax({
                    url: 'checkspecialname.do',
                    async: false,
                    type: "POST",
                    dataType: "json",
                    data: param,
                    success: function(result){
                        if(result.status){
                            _msg = result.msg;
                        }
                    }
                });

                return _msg;
            }

            function lineChartMapToScript(){
                //保存页面时将当前图表的map对象转换为字符串保存在script标签中
                $("#lineChart_script").attr("type","text/plain");
                var lineChartJsHtml="";
                lineChart_Map.each(function (key, value, index) {
                    value=JSON.stringify(value);
                    var lineChartStr=["\nvar myChart"+index+" = echarts.init(document.getElementById(/**id**/'"+key+"'/**id**/));",
                                        "\nvar option"+index+"=/**option**/"+value+"/**option**/;",
                                        "\nmyChart"+index+".setOption(option"+index+");"+"/**nextJs**/"
                                    ].join("");
                    lineChartJsHtml+=lineChartStr;
                });
                $("#lineChart_script").html(lineChartJsHtml);
            }
            function replaceList(param){
                //替换列表内容
                $("#container").find(".le-list-group[data-update='true']").each(function(){
                        var _this=$(this);
                        var listId=_this.attr("id");
                        var updateSet=$("#"+listId).attr("data-updateset");//查询条件
                        updateSet = updateSet.replace(/'parentTree.*?,/gi, '');
                        var target=$("#"+listId).attr("data-target") || "_blank";//链接样式
                        var timeFormat=$("#"+listId).attr("data-timeformat") || "yyyy-MM-dd HH:mm:ss";//时间格式
                        var _flag="<!-----listUpdate"+listId+"----->";
                        var start=param.htmlData.indexOf(_flag);
                        var end=param.htmlData.lastIndexOf(_flag);
                        var needReplace=param.htmlData.substring(start,end);
                        //增加时间格式
                        var updateModel='<FOUNDER-XY type=\"articlelist\" data=\"'+updateSet+'\">'+
                                        '<#list articles as article>'+
                                        '<li class="list-group-item">'+
                                        '<div class="media">' +
                                        '<div class="media-left">' +
                                        '<a target=\"'+target+'\" href="\${article.url!}">' +
                                        '<img onerror="javascript:this.src=\'./export/img/noimg.jpg\';this.onerror=null;this.parentNode.parentNode.style.display=\'none\';" class="media-object" alt="" src="\${article.picBig!}">' +
                                        '</a>' +
                                        '</div>' +
                                        '<div class="media-body">' +

                                        '<ul><li class="titList">' +
                                        '<h4 class="media-heading">' +
                                        '<em class="itemList" style="text-indent: 0;">'+'\${article_index+1}'+'</em><em class="itemListImg"></em>' +
                                        '<a class="listTitle" target=\"'+target+'\" href="\${article.url!}">' +
                                        '\${article.title!}' +
                                        '</a>' +
                                        '<span class="listToolbar"></span>' +
                                        '</h4>' +
                                        '</li></ul>' +
                                        '<p class="listSummary">' + '<span class="listSummaryBox" style="display: block;min-height: 12px;height: 100%;width: 100%">' +
                                        '\${article.summary!}' + '</span>' +
                                        '<span class="listSummarybar"></span>' + '</p>' +
                                        '<p class="listSource">' + '<span class="listSourceBox listSourceDetailedStyle" style="display: block;min-height: 12px;height: 100%;width: 100%">' +
                                        '\${article.source!}' + '</span>' +
                                        '<span class="listSourcebar"></span>' + '</p>' +
                                        '<div class="clearfix">'+
                                        '<p class="listSource-middle pull-left mgr20">' + '<span class="listSourceBox-middle listSourceDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                                        '\${article.source!}' + '</span>' +
                                        '<span class="listSourcebar-middle"></span>' + '</p>' +
                                        '<p class="listTime-middle pull-left">' + '<span class="listTimeeBox-middle listTimeDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                                        '\${article.pubTime?string("'+timeFormat+'")}' + '</span>' +
                                        '<span class="listTimebar-middle"></span>' + '</p>' +
                                        '</div>' +
                                        '</div>' +
                                        '<div class="media-right">' +
                                        '<span class="badge timeShow listTimeDetailedStyle" data-time="' + '\${article.pubTime?string("yyyy-MM-dd HH:mm:ss")}' + '">' + '\${article.pubTime?string("'+timeFormat+'")}' + '</span>' +
                                        '</div>' +
                                        '</div>' +
                                        '<div class="clearfix">'+
                                        '<p class="listSource-bottom pull-left mgr20">' + '<span class="listSourceBox-bottom listSourceDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                                        '\${article.source!}' + '</span>' +
                                        '<span class="listSourcebar-bottom"></span>' + '</p>' +
                                        '<p class="listTime-bottom  pull-left">' + '<span class="listTimeeBox-bottom listTimeDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                                        '\${article.pubTime?string("'+timeFormat+'")}' + '</span>' +
                                        '<span class="listTimebar-bottom"></span>' + '</p>' +
                                        '</div>' +
                                        '</li>'+
                                        '</#list>'+
                                        '</FOUNDER-XY>';
                        param.htmlData=param.htmlData.replace(needReplace,updateModel);
                });
            }
            function replaceCarousel(param){
                //替换轮播图内容
                $("#container").find(".le-carousel[data-update='true']").each(function(){
                        var _this=$(this);
                        var listId=_this.attr("id");
                        var _updateSet=$("#"+listId).attr("data-updateset");//查询条件
                        //过滤没有标题图的
                        _updateSet = _updateSet.replace(/'parentTree.*?,/gi, '');
                        var updateSet = _updateSet.substring(0,_updateSet.length-1)+",'titlePic':'a_picBig|a_picMiddle|a_picSmall'}";

                        var _flag_Ol="<!-----carouselOlUpdate"+listId+"----->";
                        var start_Ol=param.htmlData.indexOf(_flag_Ol);
                        var end_Ol=param.htmlData.lastIndexOf(_flag_Ol);
                        var needReplace_Ol=param.htmlData.substring(start_Ol,end_Ol);

                        var _flag_Div="<!-----carouselDivUpdate"+listId+"----->";
                        var start_Div=param.htmlData.indexOf(_flag_Div);
                        var end_Div=param.htmlData.lastIndexOf(_flag_Div);
                        var needReplace_Div=param.htmlData.substring(start_Div,end_Div);

                        var updateModel_Ol='<FOUNDER-XY type=\"articlelist\" data=\"'+updateSet+'\">'+
                        '<#list articles as article>'+
                            '<#if article_index == 0>'+
                        '<li data-target="#'+listId+'" data-slide-to="\${article_index}" class="active"><span>\${article_index+1}</span></li>'+
                            '<#else>'+
                        '<li data-target="#'+listId+'" data-slide-to="\${article_index}" class=""><span>\${article_index+1}</span></li>'+
                            '</#if>'+
                        '</#list>'+
                        '</FOUNDER-XY>';

                        var updateModel_Div='<FOUNDER-XY type=\"articlelist\" data=\"'+updateSet+'\">'+
                        '<#list articles as article>'+
                            '<#if article_index == 0>'+
                        '<div class="item active">'+
                            '<#else>'+
                        '<div class="item">'+
                            '</#if>'+
                        '<a href="\${article.url!}" target="_blank">'+
                        '<img  onerror="javascript:this.src=\'./export/img/noimg.jpg\';this.onerror=null;" alt="" width="100%" src="\${article.picBig!}" >'+
                        '<div class="carousel-caption">'+
                        '<h4>\${article.title!}</h4><p>\${article.summary!}</p></div></a></div>'+
                        '</#list>'+
                        '</FOUNDER-XY>';
                        param.htmlData=param.htmlData.replace(needReplace_Ol,updateModel_Ol);
                        param.htmlData=param.htmlData.replace(needReplace_Div,updateModel_Div);
                });
            }
            function replaceGallery(param){
                //替换多图内容
                $("#container").find(".le-gallery[data-update='true']").each(function(){
                        var _this=$(this);
                        var listId=_this.attr("id");
                        var _updateSet=$("#"+listId).attr("data-updateset");//查询条件
                        //过滤没有标题图的
                        _updateSet = _updateSet.replace(/'parentTree.*?,/gi, '');
                        var updateSet = _updateSet.substring(0,_updateSet.length-1)+",'titlePic':'a_picBig|a_picMiddle|a_picSmall'}";

                        var _flag_gallery="<!-----galleryUpdate"+listId+"----->";
                        var start_gallery=param.htmlData.indexOf(_flag_gallery);
                        var end_gallery=param.htmlData.lastIndexOf(_flag_gallery);
                        var needReplace_gallery=param.htmlData.substring(start_gallery,end_gallery);

                        var updateModel_gallery='<FOUNDER-XY type=\"articlelist\" data=\"'+updateSet+'\">'+
                        '<#list articles as article>'+
                        '<div class=" col-md-3 pull-left">'+
                        '<div class="marginItem">'+
                        '<div class="borderItem">'+
                        '<a href="\${article.url!}" style="position: relative; display: block; overflow: hidden;" target="_blank;">'+
                        '<p class="over-title gallery-title" style="">'+
                        '<span class="over-titleBox gallery-title-text" style="min-height: 12px;width: 100%">\${article.title!}</span>'+
                        '<span class="imgToolbarOver" style="display: none;"></span></p>'+
                        '<img onerror="javascript:this.src=\'./export/img/noimg.jpg\';this.onerror=null;" class="" alt="" src="\${article.picBig!}" style="width: 100%;display: block;"></a></div></div>'+
                        '<p class="bottom-title gallery-title">'+
                        '<a class="gallery-title-text" href="\${article.url!}" target="_blank;">\${article.title!}</a>'+
                        '<span class="imgToolbar" style="display: none;"></span></p></div>'+
                        '</#list>'+
                        '</FOUNDER-XY>';

                        param.htmlData=param.htmlData.replace(needReplace_gallery,updateModel_gallery);
                });
            }
            function replaceHoldImg(param){
                param.htmlData=param.htmlData.replace(/.\/export\/img\/noimg.jpg/g,sourceURL+"third/export/img/noimg.png");
            }
            /*function changeImgSrc(doChange){
                var imgUrl = $("#imgurl").val();
                var serverUrl = $("#serverurl").val();
                if(!doChange){
                    var t = imgUrl;
                    imgUrl = serverUrl;
                    serverUrl = t;
                }
                $("#container").find("img[src^='" + serverUrl + "']").each(function(){
                    var _src = $(this).attr("src");
                    _src = _src.replace(serverUrl, imgUrl);
                    $(this).attr("src", _src);
                });
            }*/
            function changeImgSrc(doChange){
                var imgUrl = $("#imgurl").val();
                var serverUrl = $("#serverurl").val();
                if(!doChange){
                    var t = imgUrl;
                    imgUrl = serverUrl;
                    serverUrl = t;
                }
                $("#container").find("img[src^='" + serverUrl + "']").each(function(){
                    //获取图片大小 画图之前
                    if(doChange){
                        var _width=$(this).css("width");
                        var _height=$(this).css("height");
                        var _maxWidth=$(this).css("max-width");

                        $(this).attr("data-ch-width",_width);
                        $(this).attr("data-ch-height",_height);
                        $(this).attr("data-ch-maxwidth",_maxWidth);
                    }

                    var _src = $(this).attr("src");
                    _src = _src.replace(serverUrl, imgUrl);
                    $(this).attr("src", _src);

                    //修改图片大小 画图之后
                    if(!doChange){
                        var _width=$(this).attr("data-ch-width");
                        var _height=$(this).attr("data-ch-height");
                        var _maxWidth=$(this).attr("data-ch-maxwidth");
                        $(this).on("load",function(){
                            if(_width){
                                $(this).css("width",_width);
                            }
                            if(_height){
                                $(this).css("height",_height);
                            }
                            if(_maxWidth){
                                $(this).css("max-width",_maxWidth);
                            }
                        })
                    }
                });
            }
            function containerSave(_t){
                lineChartMapToScript();
                //生成缩略图
                $(".drag_handler").hide();
                ///changeImgSrc(true);
                ///html2canvas($("#container")).then(function(canvas) {
                 ///   changeImgSrc(false);
                 ///   var canvasurl = canvas.toDataURL();
                    //传到后台的参数
                    var param = {};
                 ///   param.canvasurl = canvasurl;
                    //截止日期
                    var date = new Date();
                    var year = date.getFullYear() + 1;
                    var month = date.getMonth() + 1;
                    var day = date.getDate();
                    var defaultExpDate = year + "-" + month + "-" + day;
                    var expDate = jQuery("#saveNameDate").val() || defaultExpDate;
                    param.expDate = expDate;
                    param.s_name = jQuery("#projectName").val();
                    param.designHtml = LE.cores["Clean"]().getDesignHtml();
                    param.htmlData = LE.cores["Clean"]().getSpecialHtml();
                    param.mobileHtmlData = "";

                    //替换列表内容
                    replaceList(param);
                    //替换轮播图内容
                    replaceCarousel(param);
                    //替换多图内容
                    replaceGallery(param);
                    replaceHoldImg(param);
                    //替换占位图
                    param = utils.combineObject(special, param);

                    jQuery("#autoSaveDiv").find("span").html(_t || "保存成功！");

                    jQuery.ajax({
                        url: 'Save.do',
                        type: "POST",
                        dataType: "json",
                        data: param,
                        success: function(result){
                            if(!result.status){
                                alert("保存失败！");
                            }
                            special.docID = result.docID;
                            checkUrl(special.docID);
                            jQuery("#savingDiv").hide();
                            jQuery("#autoSaveDiv").show();
                            setTimeout('jQuery("#autoSaveDiv").hide()', 2500);

                            if(!timer){
                                autoSaveFn();
                            }
                            refreshList();
                        }
                    });

             ///   });

            }

            /**
             *
             * 刷新主列表
             */
            function refreshList(){
                var tool = window.opener.e5.mods["workspace.toolkit"];
                tool.self.closeOpDialog("OK", 1);
            }

            function checkUrl(_id){
                var _url = window.location.search;
                if(!_url || _url.indexOf("DocIDs") != -1)
                    return;
                window.history.pushState(null, null, _url + "&DocIDs=" + _id);
            }

            var timer = null;

            /**
             * 自动保存
             */
            function autoSaveFn(){
                timer = setInterval(function(){

                    var _pn = jQuery("#projectName").val();
                    var isShow = jQuery("#container").parent().hasClass("mgr240");
                    if(_pn === "" || !historyChanged || !isShow)
                        return;
                    containerSave("自动保存成功！");
                    historyChanged = false;
                }, 1000 * 60 * 5);
            }

            autoSaveFn();

            function checkDocId(){
                var _url = window.location.search;
                if(_url && _url.indexOf("DocIDs") != -1){
                    special.docID = utils.getQueryString("DocIDs");
                }
            }

            function resetTitle(){
                if(jQuery("#projectName").val() != ""){
                    jQuery("title").html(jQuery("#projectName").val());
                }
            }

            resetTitle();
        });


    </script>
</head>
<body>
<%--<input id="projectName" type="text" placeholder="项目名称"/>
<button id="saveButton" type="button" class="btn btn-primary">保存</button>--%>
<input id="projectName" type="hidden" placeholder="项目名称" value='<c:out value="${specialName}" />'/>
<form id="data_form" accept-charset="utf-8" action="Save.do" method="post">
    <input type="hidden" name="htmlData" id="htmlData" value=""/>
    <input type="hidden" name="designHtml" id="designHtml" value=""/>
    <input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="groupID" id="groupID" value="<c:out value="${groupID}"/>"/>
    <input type="hidden" name="docLibID" id="docLibID" value="<c:out value="${docLibID}"/>"/>
    <input type="hidden" name="docID" id="docID" value="<c:out value="${docID}"/>"/>
    <input type="hidden" name="templateID" id="templateID" value=""/>
    <input type="hidden" name="isNew" id="isNew" value="<c:out value="${templateID}"/>"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="s_name" id="s_name" value=""/>
    <input type="hidden" name="expDate" id="expDate" value=""/>
    <input type="hidden" name="canvasurl" id="canvasurl" value=""/>
    <input type="hidden" name="imgurl" id="imgurl" value="<c:out value="${imgurl}"/>"/>
    <input type="hidden" name="serverurl" id="serverurl" value="<c:out value="${serverurl}"/>"/>
</form>

<%@include file="index.jsp" %>
</body>
</html>
