/**
 * Created by qianxm on 2016/12/23.
 */
(function (window, $, LE) {
    LE.stylesheets["ListSettingCss"] = function () {
        //当前功能面板的对象
        var $PS = $("#listSection");
        var $listModelDiv = $("#listModelDiv");
        var $container = $("#container");
        var $listModelPreview = $("#listModelPreview");
        var $listModelGoBack = $("#listModelGoBack");
        var $addListBtn = $("#addListBtn");
        var $moreListShow = $("#moreListShow");
        var $listTarget = $("#listTarget");
        var $updateListBtn=$("#updateListBtn");
        var $changeListBtn=$("#changeListBtn");

/**
* 列表新增中部、底部字段后
* 选稿、点击模板样式时添加顶、中、底部显示标记 data-level，编辑前后带回标记
* 通过标记区分 时间等复选框点击时的作用对象
 *编辑前记录详细设置的行间样式，编辑后写回
 * 详细设置*/

/**
 * 摘要显示隐藏
 * 1、init设置
 * 2、reset初始化
 * 3、编辑前记录 这里需要走一遍弹窗
 * 4、编辑后设置
 * 5、追加前记录
 * 6、追加后设置
 * 7、添加新样式时清除
 *
 * 来源、时间、标签区分顶部、中部、底部分别记录与设置*/
        var initSetListbyModel = function () {
            /**
             * 列表内容编辑
             */
            $("#listSet").click(function () {
                //列表现有数据生成json，可由dialog接收
                var data = {};
                var list;
                var len = LECurrentObject.find("ul.list-group").children().length;
                var _Li = LECurrentObject.find("ul.list-group").children();
                for (var i = 0; i < len; i++) {
                    list = {
                        //判断是列表还是图文列表
                        'listName':LECurrentObject.attr("data-name"),
                        //单列的类型 系统选择还是手动添加的
                        'listType':LECurrentObject.find(".list-group-item").eq(i).attr("data-type"),
                        'liId': _Li.eq(i).attr("id"),
                        'link': _Li.eq(i).find("div.media-left").children("a").attr("href"),
                        'imgPath': _Li.eq(i).find("div.media-left").find("img").attr("src"),
                        'title': _Li.eq(i).find("div.media-body").find("a.listTitle").html(),
                        'summary': _Li.eq(i).find("div.media-body").find("span.listSummaryBox").html(),
                        'src': _Li.eq(i).find("div.media-body").find("span.listSourceBox").html(),
                        'PublishTime': _Li.eq(i).find("div.media-right").find("span.timeShow").attr("data-time"),
                        //关键词
                        'keyWord': "",
                        //选择的模板样式
                        'modelClass': LECurrentObject.find("ul.list-group").attr("data-prestyle"),
                        //"更多"的显示状态
                        'moreState' : LECurrentObject.find("div.moreListShow").css("display"),
                        //"更多"的内容
                        'moreText' : LECurrentObject.find("div.moreListShow").find("a").find("span").text(),
                        //"更多"的链接地址
                        'moreHref' :LECurrentObject.find("div.moreListShow").find("a").attr("href"),
                        //"发布时间"显示状态
                        'timeState':LECurrentObject.find("span.timeShow").css("display"),
                        //时间格式
                        'timeFormat':$("#timeFormat").val(),
                        //超链接跳转方式
                        'hrefTarget':LECurrentObject.find("a").attr("target"),
                        //更多的id
                        'moreId':LECurrentObject.find("div.moreListShow").attr("id"),
                        //单列样式
                        'theSecondStyle':LECurrentObject.find(".list-group-item").eq(0).attr("style"),
                       //标题背景、位置、边框样式
                        'titleStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").children("ul").attr("style"),
                        //标题字体样式
                        'titleFont':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").children("ul").children("li").find(".listTitle").attr("style"),
                        //摘要样式
                        'summaryStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").find(".listSummary").find(".listSummaryBox").attr("style"),
                        //来源样式
                        'sourceStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").find(".listSource").find(".listSourceBox").attr("style"),
                        //时间样式
                        'timeStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-right").children("span").attr("style"),
                        //"更多"链接样式
                        'moreStyle':LECurrentObject.children(".moreListShow").attr("style"),
                        //字段位于顶部、中部、底部的标记
                        'dataLevel':LECurrentObject.attr("data-level"),
                        //中部"发布时间"显示状态
                        'middleTimeState':LECurrentObject.find("p.listTime-middle").css("display"),
                        //底部"发布时间"显示状态
                        'bottomTimeState':LECurrentObject.find("p.listTime-bottom").css("display"),
                        //摘要显示状态
                        'summaryState':LECurrentObject.find("p.listSummary").css("display"),
                        //顶部来源显示状态
                        'sourceState':LECurrentObject.find("p.listSource").css("display"),
                        //中部来源显示状态
                        'middleSourceState':LECurrentObject.find("p.listSource-middle").css("display"),
                        //底部来源显示状态
                        'bottomSourceState':LECurrentObject.find("p.listSource-bottom").css("display")

                    };
                    data[i] = list;
                }
                var timeStamp = new Date().getTime();
                LEDialog.toggleDialog(LE.options["Dialog"].listAddandSettingDialog + "timestamp=" + timeStamp, function (jsonListed) {
                        //接收后台传递的数据并展示到前台页面
                        addListEvent(jsonListed);
                        if (!jsonListed[0]) {
                            var listName=LECurrentObject.attr("data-name");
                            if(listName=="listMedia"){
                                LECurrentObject.html(LE.options["listMedia"].viewinnerHtml);
                            }else if(listName=="listGroup"){
                                LECurrentObject.html(LE.options["ListGroup"].viewinnerHtml);
                            }
                        } else if (jsonListed[0]){
                            //将id编辑之前的id重新写回来 listType写回来
                            var leng = jsonListed.length;
                            for (var i = 0; i < leng; i++) {
                                LECurrentObject.find("ul.list-group").children().eq(i).attr("id", jsonListed[i].OldId);
                                LECurrentObject.find(".list-group-item").eq(i).attr("data-type",jsonListed[i].listType);
                            }
                            LECurrentObject.find("div.moreListShow").attr("id",jsonListed[0].moreId);
                            //如果数据为空 前端显示默认页面

                             if(jsonListed[0].modelClass){
                                //加回模板样式
                                var listName=LECurrentObject.attr("data-name");
                                if(listName=="listMedia"){
                                    LECurrentObject.find("ul.list-group").removeClass("listmanage_style18");
                                }else if(listName=="listGroup"){
                                    LECurrentObject.find("ul.list-group").removeClass("listmanage_style1");
                                }
                                LECurrentObject.find("ul.list-group").addClass(jsonListed[0].modelClass);
                                LECurrentObject.find("ul.list-group").attr("data-prestyle", jsonListed[0].modelClass);
                            }

                            /*if(jsonListed[0].theSecondStyle){
                                //对每个列写入样式
                                LECurrentObject.find(".list-group-item").attr("style",jsonListed[0].theSecondStyle);
                            }

                            if(jsonListed[0].titleStyle){
                                //单列标题样式
                                LECurrentObject.find(".list-group-item").find(".media-body").children("ul").attr("style",jsonListed[0].titleStyle);
                            }

                            if(jsonListed[0].titleFont){
                                //单列标题字体样式
                                LECurrentObject.find(".list-group-item").find(".media-body").children("ul").children("li").find(".listTitle").attr("style",jsonListed[0].titleFont);
                            }

                            if(jsonListed[0].summaryStyle){
                                //单列摘要样式
                                LECurrentObject.find(".list-group-item").find(".media-body").find(".listSummary").find(".listSummaryBox").attr("style",jsonListed[0].summaryStyle);
                            }

                            if(jsonListed[0].sourceStyle){
                                //单列来源样式
                                //LECurrentObject.find(".list-group-item").find(".media-body").find(".listSource").find(".listSourceBox").attr("style",jsonListed[0].sourceStyle);
                                LECurrentObject.find(".list-group-item").find(".listSourceDetailedStyle").attr("style",jsonListed[0].sourceStyle);
                            }*/

                            if(jsonListed[0].timeStyle){
                                //单列时间样式
                                //LECurrentObject.find(".list-group-item").find(".media-right").children("span").attr("style",jsonListed[0].timeStyle);
                                LECurrentObject.find(".list-group-item").find(".listTimeDetailedStyle").attr("style",jsonListed[0].timeStyle);
                                //显示隐藏样式改为块状，由其父元素判定是否隐藏
                                LECurrentObject.find(".list-group-item").find(".listTimeDetailedStyle").css("display","block");
                            }
                            if(jsonListed[0].moreStyle){
                                //"更多"链接样式
                                LECurrentObject.children(".moreListShow").attr("style",jsonListed[0].moreStyle);
                            }

                            //"更多"的显示状态
                            // LECurrentObject.find("div.moreListShow").css("display",jsonListed[0].moreState);
                            //"更多"的内容
                            LECurrentObject.find("div.moreListShow").find("a").find("span").text(jsonListed[0].moreText);
                            //"更多"的链接地址
                            LECurrentObject.find("div.moreListShow").find("a").attr("href",jsonListed[0].moreHref);
                            //"发布时间"显示状态
                            //LECurrentObject.find("span.timeShow").css("display",jsonListed[0].timeState);
                            //时间格式
                            var _format = jsonListed[0].timeFormat;
                            LECurrentObject.find("li.list-group-item").find("span.timeShow").each(function () {
                                var timeStr = $(this).attr("data-time");
                                var arr1 = timeStr.split(" ");
                                var arr2 = arr1[0].split("-");
                                var arr3 = arr1[1].split(":");
                                var _year = arr2[0];
                                var _month = arr2[1];
                                var _day = arr2[2];
                                var _hours = arr3[0];
                                var _minutes = arr3[1];
                                var _seconds = arr3[2];
                                timeStr = _format.replace(/@\{Y\}/g, _year).replace(/@\{M\}/g, _month).replace(/@\{D\}/g, _day).replace(/@\{H\}/g, _hours).replace(/@\{MI\}/g, _minutes).replace(/@\{S\}/g, _seconds);
                                $(this).text(timeStr);
                                $(this).closest(".list-group-item").find(".listTimeeBox-bottom").text(timeStr);
                                $(this).closest(".list-group-item").find(".listTimeeBox-middle").text(timeStr);
                            });

                            //超链接跳转方式
                            LECurrentObject.find("a").attr("target",jsonListed[0].hrefTarget);

                            //字段位于顶部、中部、底部的标记
                            LECurrentObject.attr("data-level",jsonListed[0].dataLevel);

                            //中部"发布时间"显示状态
                            //LECurrentObject.find("p.listTime-middle").css("display",jsonListed[0].middleTimeState);
                                //底部"发布时间"显示状态
                            //LECurrentObject.find("p.listTime-bottom").css("display",jsonListed[0].bottomTimeState);
                            //摘要显示状态
                            //LECurrentObject.find("p.listSummary").css("display",jsonListed[0].summaryState);
                            //顶部来源显示状态
                           // LECurrentObject.find("p.listSource").css("display",jsonListed[0].sourceState);
                            ////中部来源显示状态
                            //LECurrentObject.find("p.listSource-middle").css("display",jsonListed[0].middleSourceState);
                            //底部来源显示状态
                            //LECurrentObject.find("p.listSource-bottom").css("display",jsonListed[0].bottomSourceState);
                            //处理修改前图片路径不对的项
                            LECurrentObject.find(".media-left").each(function (){
                                var _this=$(this);
                                if(_this.find('img').attr('src')=='./export/img/noimg.jpg'){
                                    _this.hide();
                                }
                            })
                        }

                        LECurrentObject.trigger("click");
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };

        /*更多样式面板出现消失*/
        var initListModelShow = function () {
            /*$("#listModelMore")*/

            $listModelPreview.bind("click", function () {
                //判断显示图文还是文字列表样式
                var listName=LECurrentObject.attr("data-name");
                if(listName=="listMedia"){
                    $('.listGroupModel').hide();
                    $('.listMediaModel').show();
                }else if(listName=="listGroup"){
                    $('.listMediaModel').hide();
                    $('.listGroupModel').show();
                }
                //滚动条置顶
                $("#listModelDiv").scrollTop(0);
                $("#listModelDiv").perfectScrollbar("update");

                $listModelGoBack.animate({right: "0"}, "fast", "linear");
                $listModelDiv.animate({right: "0"}, "fast", "linear");
            });
            $listModelGoBack.bind("click", function () {
                $listModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $listModelDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };
        /**
         * 添加自定义列表*/
        var initaddCustomList = function () {
            $("#addCustomList").click(function () {
                var data=LECurrentObject.attr("data-name");
                var timeStamp = new Date().getTime();
                LEDialog.toggleDialog(LE.options["Dialog"].listAddandSettingDialog + "timestamp=" + timeStamp, function (jsonListed) {
                        addListEvent(jsonListed);

                        if (jsonListed[0]) {
                            //将listType写回来
                            var leng = jsonListed.length;
                            for (var i = 0; i < leng; i++) {
                                LECurrentObject.find(".list-group-item").eq(i).attr("data-type",jsonListed[i].listType);
                            }
                        }
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };
        var initlistStyleSet=function(){
            /*缩略图hover事件 删除按钮的显示隐藏*/
            $("#sidebar-panel-II").on({
                'mouseover': function () {
                    $(this).next().show();
                }, 'mouseout': function () {
                    $(this).next().hide();
                }
            }, '.previewListItem');
            $("#sidebar-panel-II").on({
                'mouseover': function () {
                    $(this).show();
                }, 'mouseout': function () {
                    $(this).hide();
                }
            }, '.removeListItem');

            $("#sidebar-panel-II").on({
                'click': function () {
                    $(this).parent().hide();
                    $("#listImgAdd").show();
                }
            }, '.removeListItem');
        };
        var initlistImgUpload = function () {
            $("#listImgAdd").click(function (e) {
                LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog, function (jsonUploadImg) {
                    var url = jsonUploadImg.imgPath;
                    $("#listImgPreview").find(".previewListItem").attr("src",url);
                    $("#listImgAdd").hide();
                    $("#listImgPreview").show();
                });

            });
        };

        /*
         点击标签栏模板按钮的同时，更新样式预览处的html。
         */
        function initListModelPreview() {
            $listModelDiv.find("ul").bind("click", function () {
                $listModelPreview.html($(this).html());
            })
        }

        /*
         点击标签样式按钮
         为目标对象添加class 同时去掉<style>标签中的样式 每次添加记住class 下次点击先移除 再添加
         */
        var initChangeListStyleBlock = function () {
            $listModelDiv.find(".displayStyle").click(function (e) {

                //添加新样式之前清除写在行间的样式
                /*LECurrentObject.find("span.timeShow").css("display","");
                LECurrentObject.find("p.listTime-middle").css("display","");
                LECurrentObject.find("p.listTime-bottom").css("display","");
                LECurrentObject.find("p.listSummary").css("display","");
                LECurrentObject.find("p.listSource").css("display","");
                LECurrentObject.find("p.listSource-middle").css("display","");
                LECurrentObject.find("p.listSource-bottom").css("display","");*/
                var id = LECurrentObject.attr("id");
                 //清除style标签中的内容
                StyleManager.removeStyle($.trim("#" + id + " .timeShow"));
                StyleManager.removeStyle($.trim("#" + id + " .listTime-middle"));
                StyleManager.removeStyle($.trim("#" + id + " .listTime-bottom"));
                StyleManager.removeStyle($.trim("#" + id + " .listSummary"));
                StyleManager.removeStyle($.trim("#" + id + " .listSource"));
                StyleManager.removeStyle($.trim("#" + id + " .listSource-middle"));
                StyleManager.removeStyle($.trim("#" + id + " .listSource-bottom"));

                var _$this = $(this);
                //为当前对象添加标记，区分字段位于上部、中部、底部 并分别设置
                var _level=_$this.attr("data-level");
                LECurrentObject.attr("data-level",_level);


                //为目标对象添加class 每次添加记住class 下次点击先移除 再添加
                var $target = LECurrentObject.find("ul.list-group");
                var _style = _$this.data("ref");

                var listName=LECurrentObject.attr("data-name");
                if(listName=="listMedia"){
                    $target.removeClass("listmanage_style18");
                }else if(listName=="listGroup"){
                    $target.removeClass("listmanage_style1");
                }

                //$target.removeClass("listmanage_style1");
                $target.removeClass($target.attr("data-prestyle"));
                $target.attr("data-prestyle", _style);
                $target.addClass(_style);
                // LECurrentObject.trigger("click");
                resetLinkShow();
                resetLinkMessage();
                resetPublishTime();
                resetTargetBtn();
                resetListModelPreview();
                resettimeFormat();
                resetListAbstract();
                resetListSource();
                LEHistory.trigger();
            });
        };

        //区分列表和图文列表调用不同的选稿框 列表模式和相册模式
        function selectType(){
            var listName=LECurrentObject.attr("data-name");
            var Dialog=LE.options["Dialog"].listGroupDialog;
            if(listName=="listMedia"){
                Dialog=LE.options["Dialog"].thumbListDialog;
            }else if(listName=="listGroup"){
                Dialog=LE.options["Dialog"].listGroupDialog;
            }
            return Dialog
        }
        var initAddListbyModel = function () {
            /**
             * 列表的添加功能 添加html内容
             */
            $addListBtn.click(function () {
                var _url=selectType();
                var data = [];
                LEDialog.toggleDialog(_url, function (json) {
                        addListEvent(json);
                        LECurrentObject.find(".list-group-item").attr("data-type","sql");
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };

        var initUpdateList = function () {
            /**
             * 列表的动态更新
             */
            $updateListBtn.click(function () {
                //列表选稿页面显示多标题 轮播图与多图不显示，以showMulti为标识
                var data = {"showMulti":true};
                LEDialog.toggleDialog(LE.options["Dialog"].updaListDialog, function(updateSet){
                        var countStart=updateSet.lastIndexOf(":")+1;
                        var countEnd=updateSet.lastIndexOf("}");
                        var count=updateSet.substring(countStart,countEnd);
                        var jsonList=[];
                        var list={
                            PublishTime: "2017-01-05 18:09:42",
                            id:"example01",
                            imgPath: "export/images/navMenu/navMenu15.png",
                            //imgPath: "http://172.19.33.95/pic/201608/05/t2_(9X57X600X389)a432d0f1-3702-4239-ad8f-7405a534fb24.jpg",
                            link: "http://",
                            src: "列表动态更新的来源",
                            summary: "列表动态更新的摘要",
                            title: "列表动态更新的标题"
                        };
                        for(var i=0;i<count;i++){
                            jsonList.push(list)
                        }
                       // console.log(countStart+"---"+countEnd+"---"+count);
                        //为设置了动态更新的列表添加标记
                        LECurrentObject.attr("data-update","true");
                        addListEvent(jsonList);
                        //添加保存时替换的标记
                        var _id=LECurrentObject.attr("id");
                        LECurrentObject.find(".list-group").prepend("<!-----listUpdate"+_id+"----->");
                        LECurrentObject.find(".list-group").append("<!-----listUpdate"+_id+"----->");
                        LECurrentObject.attr("data-updateset",updateSet);
                        LEHistory.trigger();
                    },
                    data
                );
            });

            $changeListBtn.click(function () {
                var updateset=LECurrentObject.attr("data-updateset");
                //{'columnid':[80,81,82,100],'columntype':'self', 'articletype':'article|pic|video|link|multi|special','parentTree':'{'level':'level1'---'columnId':'52'}|{'level':'level0'---'columnId':'8'}|{'level':'level1'---'columnId':'70'}','articleattr':'all','start':0, 'count':5}
                var selecteds = updateset.split("[")[1].split("]")[0].split(",").map(function(x){return parseInt(x)});
                var selectednum = updateset.split("'count':")[1].split("}")[0];
                var columntype = updateset.split("'articletype':'")[1].split("',")[0].split('|');
                var parentTree = updateset.split("'parentTree':'")[1].split("',")[0].split('|');
                //选择的模板样式
                var modelClass=LECurrentObject.find("ul.list-group").attr("data-prestyle");

                var articleattr= [];
                if(updateset.indexOf("'article_attr':'")!=-1){
                    articleattr=updateset.split("'article_attr':'")[1].split("',")[0].toLowerCase().split('|');
                }
                var data={
                    "showMulti":true,
                    "selecteds":selecteds,
                    "selectednum":selectednum,
                    "columntype":columntype,
                    "article_attr":articleattr,
                    "parentTree":parentTree
                };
                LEDialog.toggleDialog(LE.options["Dialog"].updaListDialog, function(updateSet){
                        var countStart=updateSet.lastIndexOf(":")+1;
                        var countEnd=updateSet.lastIndexOf("}");
                        var count=updateSet.substring(countStart,countEnd);
                        var jsonList=[];
                        var list={
                            PublishTime: "2017-01-05 18:09:42",
                            id:"example01",
                            imgPath: "export/images/navMenu/navMenu15.png",
                            //imgPath: "http://172.19.33.95/pic/201608/05/t2_(9X57X600X389)a432d0f1-3702-4239-ad8f-7405a534fb24.jpg",
                            link: "http://",
                            src: "列表动态更新的来源",
                            summary: "列表动态更新的摘要",
                            title: "列表动态更新的标题"
                        };
                        for(var i=0;i<count;i++){
                            jsonList.push(list)
                        }
                        // console.log(countStart+"---"+countEnd+"---"+count);
                        //为设置了动态更新的列表添加标记
                        LECurrentObject.attr("data-update","true");
                        addListEvent(jsonList);
                        //添加保存时替换的标记
                        var _id=LECurrentObject.attr("id");
                        LECurrentObject.find(".list-group").prepend("<!-----listUpdate"+_id+"----->");
                        LECurrentObject.find(".list-group").append("<!-----listUpdate"+_id+"----->");
                        LECurrentObject.attr("data-updateset",updateSet);

                        if(modelClass){
                            //加回模板样式
                            var listName=LECurrentObject.attr("data-name");
                            if(listName=="listMedia"){
                                LECurrentObject.find("ul.list-group").removeClass("listmanage_style18");
                            }else if(listName=="listGroup"){
                                LECurrentObject.find("ul.list-group").removeClass("listmanage_style1");
                            }
                            LECurrentObject.find("ul.list-group").addClass(modelClass);
                            LECurrentObject.find("ul.list-group").attr("data-prestyle",modelClass);
                        }
                        LECurrentObject.trigger("click");
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };

        var initResetListbyModel = function () {
            /**
             * 列表的重置功能 添加html内容
             */
            $("#listReset").click(function () {
                var _url=selectType();
                var data = [];
                LEDialog.toggleDialog(_url, function (json) {
                        addListEvent(json);
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };

        function addListEvent(jsonList) {
            if (jsonList.length == 0) return;
            $addListBtn.hide();
            $("#listEdit").show();
            var len = jsonList.length;
            var strH = "";
            for (var i = 0; i < len; i++) {
                var id_List = 'le_List_Li' + LEDrag.getNextId() + '_' + jsonList[i].id;
                var str = '<li id="' + id_List + '" class="list-group-item">' +
                    '<div class="media">' +
                    '<div class="media-left">' +
                    '<a target="_blank" href="' + jsonList[i].link + '">' +
                    '<img class="media-object" onerror="showListMediaDefaultImage(this)" data-src="holder.js/64x64" alt="" src="' + jsonList[i].imgPath + '" data-holder-rendered="true">' +
                    '</a>' +
                    '</div>' +
                    '<div class="media-body">' +

                    '<ul><li class="titList">' +
                    '<h4 class="media-heading">' +
                    '<em class="itemList" style="text-indent: 0;">' + parseInt(i + 1) + '</em><em class="itemListImg"></em>' +
                    '<a class="listTitle" target="_blank" href="' + jsonList[i].link + '">' +
                    jsonList[i].title +
                    '</a>' +
                    '<span class="listToolbar"></span>' +
                    '</h4>' +
                    '</li></ul>' +
                    '<p class="listSummary">' + '<span class="listSummaryBox" style="display: block;min-height: 12px;height: 100%;width: 100%">' +
                    jsonList[i].summary + '</span>' +
                    '<span class="listSummarybar"></span>' + '</p>' +
                    '<p class="listSource">' + '<span class="listSourceBox listSourceDetailedStyle" style="display: block;min-height: 12px;height: 100%;width: 100%">' +
                    jsonList[i].src + '</span>' +
                    '<span class="listSourcebar"></span>' + '</p>' +
                    //中部字段开始
                    '<div class="clearfix">'+
                    '<p class="listSource-middle pull-left mgr20">' + '<span class="listSourceBox-middle listSourceDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '</span>' +
                    '<span class="listSourcebar-middle"></span>' + '</p>' +
                    '<p class="listTime-middle pull-left">' + '<span class="listTimeeBox-middle listTimeDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].PublishTime + '</span>' +
                    '<span class="listTimebar-middle"></span>' + '</p>' +
                   /* '<p class="listKeyword-middle  pull-right">' + '<span class="listKeywordBox-middle" style="display: inline-block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '我是中部关键词</span>' +
                    '<span class="listKeywordbar-middle"></span>' + '</p>' +*/
                    '</div>' +
                    //中部字段结束
                    '</div>' +
                    '<div class="media-right">' +
                    '<span class="badge timeShow listTimeDetailedStyle" data-time="' + jsonList[i].PublishTime + '">' + jsonList[i].PublishTime + '</span>' +
                    '</div>' +
                    '</div>' +
                    //底部字段开始
                    '<div class="clearfix">'+
                    '<p class="listSource-bottom pull-left mgr20">' + '<span class="listSourceBox-bottom listSourceDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '</span>' +
                    '<span class="listSourcebar-bottom"></span>' + '</p>' +
                    '<p class="listTime-bottom  pull-left">' + '<span class="listTimeeBox-bottom listTimeDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].PublishTime + '</span>' +
                    '<span class="listTimebar-bottom"></span>' + '</p>' +
                   /* '<p class="listKeyword-bottom  pull-right">' + '<span class="listKeywordBox-bottom" style="display: inline-block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '我是底部关键词</span>' +
                    '<span class="listKeywordbar-bottom"></span>' + '</p>' +*/
                    '</div>' +
                    //底部字段结束
                    '</li>';
                strH += str;
            }
            var moreStr = '<div id="' + id_List + 'moreList" class="moreListShow" style="overflow: hidden;"><a  target="_blank" href="http://"><span>更多...</span></a></div>';
            //var moreStr ='<li class="moreListShow" style="overflow: hidden" ><a style="float:right" href="https://itsoft.hold.founder.com"><span>更多...</span></a></li>';

            //strH += moreStr;
            strHAll = '<ul class="list-group">' + strH + '</ul>' + moreStr;

            LECurrentObject.html(strHAll);

            var listName=LECurrentObject.attr("data-name");
            if(listName=="listMedia"){
                LECurrentObject.find("ul.list-group").addClass("listmanage_style18");
                LECurrentObject.attr("data-level","middle");
            }else if(listName=="listGroup"){
                LECurrentObject.find("ul.list-group").addClass("listmanage_style1");
                LECurrentObject.attr("data-level","top");
            }

            //默认来源、时间在顶部显示
            //LECurrentObject.attr("data-level","top");
            //LECurrentObject.find("ul.list-group").addClass("listmanage_style1");
            LECurrentObject.trigger("click");
            window.isSameObject=false;
        }

        var initaddListEvent = function () {
            $addListBtn.bind("click", function () {
                $addListBtn.hide();
                $("#listEdit").show();
                var len = jsonList.length;
                var strH = "";
                for (var i = 0; i < len; i++) {
                    var str = '<li id="' + jsonList[i].id + '" class="list-group-item"><a target="_blank" href="' + jsonList[i].link + '">' + jsonList[i].title + '</a><span class="badge timeShow" data-time="' + jsonList[i].PublishTime + '">' + jsonList[i].PublishTime + '</span></li>';
                    strH += str;
                }
                var moreStr = '<li class="moreListShow" style="overflow: hidden" ><a style="float:right" href="http://"><span>更多...</span></a></li>';
                strH += moreStr;
                strHAll = '<ul class="list-group">' + strH + '</ul>';

                LECurrentObject.html(strHAll);
                LECurrentObject.trigger("click");
            })
        };
        var initAppendListbyModel = function () {
            /**
             * 列表的追加功能对html执行append动作
             */
            $("#listAdd").click(function () {
                var _url=selectType();
                var data = [];
                LEDialog.toggleDialog(_url, function (json) {
                        appendListEvent(json);
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };

        function appendListEvent(jsonList) {
            if (jsonList.length == 0) return;
            var len = jsonList.length;
            var oldLeng = LECurrentObject.find("ul.list-group").children().length;
            var strH = "";
            for (var i = 0; i < len; i++) {

                var id_List_Append = 'le_List_Li' + LEDrag.getNextId() + '_' + jsonList[i].id;
                var str = '<li id="' + id_List_Append + '" class="list-group-item" data-type="sql">' +
                    '<div class="media">' +
                    '<div class="media-left">' +
                    '<a target="_blank" href="' + jsonList[i].link + '">' +
                    '<img class="media-object" onerror="showListMediaDefaultImage(this)" data-src="holder.js/64x64" alt="" src="' + jsonList[i].imgPath + '" data-holder-rendered="true">' +
                    '</a>' +
                    '</div>' +
                    '<div class="media-body">' +

                    '<ul><li class="titList">' +
                    '<h4 class="media-heading">' +
                    '<em class="itemList" style="text-indent: 0;">' + parseInt(oldLeng + i + 1) + '</em><em class="itemListImg"></em>' +
                    '<a class="listTitle" target="_blank" href="' + jsonList[i].link + '">' +
                    jsonList[i].title +
                    '</a>' +
                    '<span class="listToolbar"></span>' +
                    '</h4>' +
                    '</li></ul>' +
                    '<p class="listSummary">' + '<span class="listSummaryBox" style="display: block;min-height: 12px;height: 100%;width: 100%">' +
                    jsonList[i].summary + '</span>' +
                    '<span class="listSummarybar"></span>' + '</p>' +
                    '<p class="listSource">' + '<span class="listSourceBox listSourceDetailedStyle" style="display: block;min-height: 12px;height: 100%;width: 100%">' +
                    jsonList[i].src + '</span>' +
                    '<span class="listSourcebar"></span>' + '</p>' +
                    //中部字段开始
                    '<div class="clearfix">'+
                    '<p class="listSource-middle  pull-left mgr20">' + '<span class="listSourceBox-middle listSourceDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '</span>' +
                    '<span class="listSourcebar-middle"></span>' + '</p>' +
                    '<p class="listTime-middle  pull-left">' + '<span class="listTimeeBox-middle listTimeDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].PublishTime + '</span>' +
                    '<span class="listTimebar-middle"></span>' + '</p>' +
                    /*'<p class="listKeyword-middle  pull-right">' + '<span class="listKeywordBox-middle" style="display: inline-block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '我是中部关键词</span>' +
                    '<span class="listKeywordbar-middle"></span>' + '</p>' +*/
                    '</div>' +
                    //中部字段结束
                    '</div>' +
                    '<div class="media-right">' +
                    '<span class="badge timeShow listTimeDetailedStyle" data-time="' + jsonList[i].PublishTime + '">' + jsonList[i].PublishTime + '</span>' +
                    '</div>' +
                    '</div>' +
                    //底部字段开始
                    '<div class="clearfix">'+
                    '<p class="listSource-bottom  pull-left mgr20">' + '<span class="listSourceBox-bottom listSourceDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '</span>' +
                    '<span class="listSourcebar-bottom"></span>' + '</p>' +
                    '<p class="listTime-bottom  pull-left">' + '<span class="listTimeeBox-bottom listTimeDetailedStyle" style="display: block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].PublishTime + '</span>' +
                    '<span class="listTimebar-bottom"></span>' + '</p>' +
                    /*'<p class="listKeyword-bottom  pull-right">' + '<span class="listKeywordBox-bottom" style="display: inline-block;min-height: 12px;min-width:40px;height: 100%;width:auto">' +
                    jsonList[i].src + '我是底部关键词</span>' +
                    '<span class="listKeywordbar-bottom"></span>' + '</p>' +*/
                    '</div>' +
                    //底部字段结束
                    '</li>';
                strH += str;
            }

            //记录追加前的样式信息,追加之后保持样式
            var _record= {
                //"发布时间"显示状态
                'timeState':LECurrentObject.find("span.timeShow").css("display"),
                //时间格式
                'timeFormat':$("#timeFormat").val(),
                //超链接跳转方式
                'hrefTarget':LECurrentObject.find("a").attr("target"),
                //单列样式
                'theSecondStyle':LECurrentObject.find(".list-group-item").eq(0).attr("style"),
                //标题背景、位置、边框样式
                'titleStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").children("ul").attr("style"),
                //标题字体样式
                'titleFont':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").children("ul").children("li").find(".listTitle").attr("style"),
                //摘要样式
                'summaryStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").find(".listSummary").find(".listSummaryBox").attr("style"),
                //来源样式
                'sourceStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-body").find(".listSource").find(".listSourceBox").attr("style"),
                //时间样式
                'timeStyle':LECurrentObject.find(".list-group-item").eq(0).find(".media-right").children("span").attr("style"),
                //中部"发布时间"显示状态
                'middleTimeState':LECurrentObject.find("p.listTime-middle").css("display"),
                //底部"发布时间"显示状态
                'bottomTimeState':LECurrentObject.find("p.listTime-bottom").css("display"),
                //摘要显示状态
                'summaryState':LECurrentObject.find("p.listSummary").css("display"),
                //顶部来源显示状态
                'sourceState':LECurrentObject.find("p.listSource").css("display"),
                //中部来源显示状态
                'middleSourceState':LECurrentObject.find("p.listSource-middle").css("display"),
                //底部来源显示状态
                'bottomSourceState':LECurrentObject.find("p.listSource-bottom").css("display")
            };
            LECurrentObject.find("ul.list-group").append(strH);
            keepStyle(_record);



           /* //追加的时间显示状态与追加之前一致
            if ($("#moreListTime").is(':checked')) {
                LECurrentObject.find("span.timeShow").show();
            } else {
                LECurrentObject.find("span.timeShow").hide();
            }
            //追加的时间格式与追加之前一致
            var _format = $("#timeFormat").val();
            LECurrentObject.find("li.list-group-item").find("span.timeShow").each(function () {
                var timeStr = $(this).attr("data-time");
                var arr1 = timeStr.split(" ");
                var arr2 = arr1[0].split("-");
                var arr3 = arr1[1].split(":");
                var _year = arr2[0];
                var _month = arr2[1];
                var _day = arr2[2];
                var _hours = arr3[0];
                var _minutes = arr3[1];
                var _seconds = arr3[2];
                timeStr = _format.replace(/@\{Y\}/g, _year).replace(/@\{M\}/g, _month).replace(/@\{D\}/g, _day).replace(/@\{H\}/g, _hours).replace(/@\{MI\}/g, _minutes).replace(/@\{S\}/g, _seconds);
                $(this).text(timeStr);
            });*/
            LECurrentObject.trigger("click");
        }

        function keepStyle(_record){

            /*if(_record.theSecondStyle){
                //对每个列写入样式
                LECurrentObject.find(".list-group-item").attr("style",_record.theSecondStyle);
            }

            if(_record.titleStyle){
                //单列标题样式
                LECurrentObject.find(".list-group-item").find(".media-body").children("ul").attr("style",_record.titleStyle);
            }

            if(_record.titleFont){
                //单列标题字体样式
                LECurrentObject.find(".list-group-item").find(".media-body").children("ul").children("li").find(".listTitle").attr("style",_record.titleFont);
            }

            if(_record.summaryStyle){
                //单列摘要样式
                LECurrentObject.find(".list-group-item").find(".media-body").find(".listSummary").find(".listSummaryBox").attr("style",_record.summaryStyle);
            }

            if(_record.sourceStyle){
                //单列来源样式
                //LECurrentObject.find(".list-group-item").find(".media-body").find(".listSource").find(".listSourceBox").attr("style",_record.sourceStyle);
                LECurrentObject.find(".list-group-item").find(".listSourceDetailedStyle").attr("style",_record.sourceStyle);
            }*/

            if(_record.timeStyle){
                //单列时间样式
                //LECurrentObject.find(".list-group-item").find(".media-right").children("span").attr("style",_record.timeStyle);
                LECurrentObject.find(".list-group-item").find(".listTimeDetailedStyle").attr("style",_record.timeStyle);
                //显示隐藏样式改为块状，由其父元素判定
                LECurrentObject.find(".list-group-item").find(".listTimeDetailedStyle").css("display","block");
            }
            //"发布时间"显示状态
            //LECurrentObject.find("span.timeShow").css("display",_record.timeState);
            //时间格式
            var _format = _record.timeFormat;
            LECurrentObject.find("li.list-group-item").find("span.timeShow").each(function () {
                var timeStr = $(this).attr("data-time");
                var arr1 = timeStr.split(" ");
                var arr2 = arr1[0].split("-");
                var arr3 = arr1[1].split(":");
                var _year = arr2[0];
                var _month = arr2[1];
                var _day = arr2[2];
                var _hours = arr3[0];
                var _minutes = arr3[1];
                var _seconds = arr3[2];
                timeStr = _format.replace(/@\{Y\}/g, _year).replace(/@\{M\}/g, _month).replace(/@\{D\}/g, _day).replace(/@\{H\}/g, _hours).replace(/@\{MI\}/g, _minutes).replace(/@\{S\}/g, _seconds);
                $(this).text(timeStr);
                $(this).closest(".list-group-item").find(".listTimeeBox-bottom").text(timeStr);
                $(this).closest(".list-group-item").find(".listTimeeBox-middle").text(timeStr);
            });
            //超链接跳转方式
            LECurrentObject.find("a").attr("target",_record.hrefTarget);

            /*//中部"发布时间"显示状态
            LECurrentObject.find("p.listTime-middle").css("display",_record.middleTimeState);
            //底部"发布时间"显示状态
            LECurrentObject.find("p.listTime-bottom").css("display",_record.bottomTimeState);
            //摘要显示状态
            LECurrentObject.find("p.listSummary").css("display",_record.summaryState);
            //顶部来源显示状态
            LECurrentObject.find("p.listSource").css("display",_record.sourceState);
            //中部来源显示状态
            LECurrentObject.find("p.listSource-middle").css("display",_record.middleSourceState);
            //底部来源显示状态
            LECurrentObject.find("p.listSource-bottom").css("display",_record.bottomSourceState);*/
        }

        /*
         清除列表
         */
        var initclearListc = function () {
            $("#listClear").bind("click", function () {
                     var listName=LECurrentObject.attr("data-name");
                    if (listName == "listMedia") {
                        LECurrentObject.html(LE.options["listMedia"].viewinnerHtml);
                    } else if (listName == "listGroup") {
                        LECurrentObject.html(LE.options["ListGroup"].viewinnerHtml);
                    }

                //LECurrentObject.html(LE.options["ListGroup"].viewinnerHtml);
                LECurrentObject.trigger("click");
                LEHistory.trigger();
            });

        };

        /*改变更多。。。是否可以显示*/
        var initMoreLinkShow = function () {
            $moreListShow.bind("click", function () {
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " .moreListShow");
                if ($moreListShow.is(':checked')) {
                    //LECurrentObject.find("div.moreListShow").show();
                    StyleManager.setStyle(selector, "display", "block");
                } else {
                    //LECurrentObject.find("div.moreListShow").hide();
                    StyleManager.setStyle(selector, "display", "none");
                }
                LEHistory.trigger();
            });

            $("#moreListName").bind("change", function () {
                LECurrentObject.find("div.moreListShow").find("a").find("span").text($(this).val());
            });

            $("#moreListLink").bind("change", function () {
                LECurrentObject.find("div.moreListShow").find("a").attr("href", $(this).val());
            })

        };

        /*
         发布时间显示隐藏及格式设置
         */
        var initpublishTimeEvent = function () {
            $("#moreListTime").bind("click", function () {
                var _level=LECurrentObject.attr("data-level");
                var id = LECurrentObject.attr("id");
                if ($("#moreListTime").is(':checked')) {
                        if(_level=="top"){
                            //LECurrentObject.find("span.timeShow").show();
                            //LECurrentObject.find("span.timeShow").css("display","inline-block");
                            var selector = $.trim("#" + id + " .timeShow");
                            StyleManager.setStyle(selector, "display", "inline-block");
                        }else if(_level=="middle"){
                            //LECurrentObject.find("p.listTime-middle").show();
                            var selector = $.trim("#" + id + " .listTime-middle");
                            StyleManager.setStyle(selector, "display", "block");
                        }else if(_level=="bottom"){
                            //LECurrentObject.find("p.listTime-bottom").show();
                            var selector = $.trim("#" + id + " .listTime-bottom");
                            StyleManager.setStyle(selector, "display", "block");
                        }
                } else {
                   /* LECurrentObject.find("span.timeShow").hide();
                    LECurrentObject.find("p.listTime-middle").hide();
                    LECurrentObject.find("p.listTime-bottom").hide();*/
                    StyleManager.setStyle($.trim("#" + id + " .timeShow"), "display", "none");
                    StyleManager.setStyle($.trim("#" + id + " .listTime-middle"), "display", "none");
                    StyleManager.setStyle($.trim("#" + id + " .listTime-bottom"), "display", "none");

                }
            });

            $listTarget.bind("click", function () {
                if ($listTarget.is(':checked')) {
                    LECurrentObject.find("a").attr("target", "_blank");
                    LECurrentObject.attr("data-target","_blank");
                } else {
                    LECurrentObject.find("a").attr("target", "_self");
                    LECurrentObject.attr("data-target","_self");
                }
            });
        };
        /*
         设置时间格式
         */
        var inittimeFormatEvent = function () {
            $("#timeFormat").change(function (e) {
                var _format = $(this).val();
                LECurrentObject.find("li.list-group-item").find("span.timeShow").each(function () {
                    var timeStr = $(this).attr("data-time");
                    var arr1 = timeStr.split(" ");
                    var arr2 = arr1[0].split("-");
                    var arr3 = arr1[1].split(":");
                    var _year = arr2[0];
                    var _month = arr2[1];
                    var _day = arr2[2];
                    var _hours = arr3[0];
                    var _minutes = arr3[1];
                    var _seconds = arr3[2];
                    /*var _year = timeStr.slice(0, 4);
                     var _month = timeStr.slice(5, 7);
                     var _day = timeStr.slice(8, 10);
                     var _hours = timeStr.slice(11, 13);
                     var _minutes = timeStr.slice(14, 16);
                     var _seconds = timeStr.slice(17, 19);*/
                    timeStr = _format.replace(/@\{Y\}/g, _year).replace(/@\{M\}/g, _month).replace(/@\{D\}/g, _day).replace(/@\{H\}/g, _hours).replace(/@\{MI\}/g, _minutes).replace(/@\{S\}/g, _seconds);
                    $(this).text(timeStr);
                    $(this).closest(".list-group-item").find(".listTimeeBox-bottom").text(timeStr);
                    $(this).closest(".list-group-item").find(".listTimeeBox-middle").text(timeStr);
                });
                //动态更新内容的时间格式
                var timeformat=_format.replace(/@\{Y\}/g,"yyyy").replace(/@\{M\}/g,"MM").replace(/@\{D\}/g,"dd").replace(/@\{H\}/g,"HH").replace(/@\{MI\}/g,"mm").replace(/@\{S\}/g,"ss");
                LECurrentObject.attr("data-timeformat",timeformat);

                LEHistory.trigger();

            });
        };

        /*
         摘要显示隐藏
         */
        var initListAbstractEvent = function () {
            $("#moreListAbstract").bind("click", function () {
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " .listSummary");
                if ($("#moreListAbstract").is(':checked')) {
                   // LECurrentObject.find("p.listSummary").show();
                    StyleManager.setStyle(selector, "display", "block");
                } else {
                    //LECurrentObject.find("p.listSummary").hide();
                    StyleManager.setStyle(selector, "display", "none");
                }
                LEHistory.trigger();
            });
        };

        /*
         来源显示隐藏
         */
        var initListSourceEvent = function () {
            $("#moreListSource").bind("click", function () {
                var _level=LECurrentObject.attr("data-level");
                var id = LECurrentObject.attr("id");
                if ($("#moreListSource").is(':checked')) {
                    if(_level=="top"){
                       // LECurrentObject.find("p.listSource").show();
                        var selector = $.trim("#" + id + " .listSource");
                        StyleManager.setStyle(selector, "display", "block");
                    }else if(_level=="middle"){
                        //LECurrentObject.find("p.listSource-middle").show();
                        var selector = $.trim("#" + id + " .listSource-middle");
                        StyleManager.setStyle(selector, "display", "block");
                    }else if(_level=="bottom"){
                        //LECurrentObject.find("p.listSource-bottom").show();
                        var selector = $.trim("#" + id + " .listSource-bottom");
                        StyleManager.setStyle(selector, "display", "block");
                    }
                } else {
                    /*LECurrentObject.find("p.listSource").hide();
                    LECurrentObject.find("p.listSource-middle").hide();
                    LECurrentObject.find("p.listSource-bottom").hide();*/
                    StyleManager.setStyle($.trim("#" + id + " .listSource"), "display", "none");
                    StyleManager.setStyle($.trim("#" + id + " .listSource-middle"), "display", "none");
                    StyleManager.setStyle($.trim("#" + id + " .listSource-bottom"), "display", "none");
                }

                LEHistory.trigger();
            });
        };

        /*
         鼠标悬停点击时编辑列表内容
         */
        function initListTitleEdit() {
            //列表标题
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listToolbar").stop().show();
                }
            }}, '.media-heading,.listToolbar,.listTitle');

            $container.on({'mouseout': function () {
                $(this).find(".listToolbar").stop().hide();
            }}, '.media-heading');

            $container.on({"click": function () {
                $(this).parent().find("a").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("a").attr("contenteditable",true);
                var em1 = $(this).parent().find("em.itemList").css("display") != "none";
                var em2 = $(this).parent().find("em.itemListImg").css("display") != "none";
                var em1Width = parseInt($(this).parent().find("em.itemList").css("width"));
                var em2Width = parseInt($(this).parent().find("em.itemListImg").css("width"));
                var h4Width = parseInt($(this).parent().css("width"));


                //清除之前的超出隐藏，强制不换行和省略显示
                $(this).parent().find("a").css({"overflow":"auto", "text-overflow":"visible","white-space":"normal","word-wrap":""});
                if (em1) {
                    $(this).parent().find("a").css({"display": "inline-block", "width": parseInt(h4Width - em1Width - 20) + "px"});
                } else if (em2) {
                    $(this).parent().find("a").css({"display": "inline-block", "width": parseInt(h4Width - em2Width - 20) + "px"});
                } else {
                    $(this).parent().find("a").css({"display": "inline", "width": parseInt(h4Width) + "px"});
                }
                //if (em1) {
                //    $(this).parent().find("a").css({"display": "inline-block", "width": parseInt(h4Width - em1Width - 20) + "px"});
                //} else if (em2) {
                //    $(this).parent().find("a").css({"display": "inline-block", "width": parseInt(h4Width - em2Width - 20) + "px"});
                //} else {
                //    $(this).parent().find("a").css({"display": "block", "width": "100%"});
                //}
                //$(this).parent().find("a").css({"display":"block"});
                $(this).parent().find("a").trigger("focus");
            }}, '.listToolbar');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                //$(this).css({"display": "inline"});

                $(this).parent().find("a").css({"display": "inline-block", "overflow":"hidden", "text-overflow":"ellipsis","white-space":"nowrap","word-wrap":"break-word","width":"864px"});

                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.media-heading>a');

            //列表摘要
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listSummarybar").stop().show();
                }
            }}, '.listSummary,.listSummarybar,.listSummaryBox');

            $container.on({'mouseout': function () {
                $(this).find(".listSummarybar").stop().hide();
            }}, '.listSummary');

            $container.on({"click": function () {
                $(this).parent().find("span.listSummaryBox").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("span.listSummaryBox").attr("contenteditable",true);
                $(this).attr("contenteditable", false);
                $(this).parent().find("span.listSummaryBox").trigger("focus");
            }}, '.listSummarybar');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.listSummaryBox');

            //列表来源
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listSourcebar").stop().show();
                }

            }}, '.listSource,.listSourcebar,.listSourceBox');

            $container.on({'mouseout': function () {
                $(this).find(".listSourcebar").stop().hide();
            }}, '.listSource');

            $container.on({"click": function () {
                $(this).parent().find("span.listSourceBox").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("span.listSourceBox").attr("contenteditable",true);
                $(this).attr("contenteditable", false);
                $(this).parent().find("span.listSourceBox").trigger("focus");
            }}, '.listSourcebar');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                $(this).closest(".list-group-item").find(".listSourceBox-bottom").text($(this).text());
                $(this).closest(".list-group-item").find(".listSourceBox-middle").text($(this).text());
                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.listSourceBox');

            //列表底部来源
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listSourcebar-bottom").stop().show();
                }

            }}, '.listSource-bottom,.listSourcebar-bottom,.listSourceBox-bottom');

            $container.on({'mouseout': function () {
                $(this).find(".listSourcebar-bottom").stop().hide();
            }}, '.listSource-bottom');

            $container.on({"click": function () {
                $(this).parent().find("span.listSourceBox-bottom").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("span.listSourceBox").attr("contenteditable",true);
                $(this).attr("contenteditable", false);
                $(this).parent().find("span.listSourceBox-bottom").trigger("focus");
            }}, '.listSourcebar-bottom');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                $(this).closest(".list-group-item").find(".listSourceBox").text($(this).text());
                $(this).closest(".list-group-item").find(".listSourceBox-middle").text($(this).text());
                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.listSourceBox-bottom');

            //列表底部标签
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listKeywordbar-bottom").stop().show();
                }

            }}, '.listKeyword-bottom,.listKeywordbar-bottom,.listKeywordBox-bottom');

            $container.on({'mouseout': function () {
                $(this).find(".listKeywordbar-bottom").stop().hide();
            }}, '.listKeyword-bottom');

            $container.on({"click": function () {
                $(this).parent().find("span.listKeywordBox-bottom").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("span.listKeywordBox").attr("contenteditable",true);
                $(this).attr("contenteditable", false);
                $(this).parent().find("span.listKeywordBox-bottom").trigger("focus");
            }}, '.listKeywordbar-bottom');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                $(this).closest(".list-group-item").find(".listKeywordBox-middle").text($(this).text());
                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.listKeywordBox-bottom');

            //列表中部来源
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listSourcebar-middle").stop().show();
                }

            }}, '.listSource-middle,.listSourcebar-middle,.listSourceBox-middle');

            $container.on({'mouseout': function () {
                $(this).find(".listSourcebar-middle").stop().hide();
            }}, '.listSource-middle');

            $container.on({"click": function () {
                $(this).parent().find("span.listSourceBox-middle").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("span.listSourceBox").attr("contenteditable",true);
                $(this).attr("contenteditable", false);
                $(this).parent().find("span.listSourceBox-middle").trigger("focus");
            }}, '.listSourcebar-middle');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                $(this).closest(".list-group-item").find(".listSourceBox").text($(this).text());
                $(this).closest(".list-group-item").find(".listSourceBox-bottom").text($(this).text());
                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.listSourceBox-middle');

            //列表中部标签
            $container.on({'mouseover': function () {
                var unEdit=$(".canedit").hasClass("edit-disable");
                if(!unEdit){
                    $(this).find(".listKeywordbar-middle").stop().show();
                }

            }}, '.listKeyword-middle,.listKeywordbar-middle,.listKeywordBox-middle');

            $container.on({'mouseout': function () {
                $(this).find(".listKeywordbar-middle").stop().hide();
            }}, '.listKeyword-middle');

            $container.on({"click": function () {
                $(this).parent().find("span.listKeywordBox-middle").attr("contenteditable", "plaintext-only");
                //$(this).parent().find("span.listKeywordBox").attr("contenteditable",true);
                $(this).attr("contenteditable", false);
                $(this).parent().find("span.listKeywordBox-middle").trigger("focus");
            }}, '.listKeywordbar-middle');

            $container.on({"blur": function () {
                $(this).attr("contenteditable", false);
                $(this).closest(".list-group-item").find(".listKeywordBox-bottom").text($(this).text());
                LEHistory.trigger();
            }/*,"keydown":function(event){
             if(event.keyCode == 13) {
             $(this).trigger("blur");
             }
             }*/}, '.listKeywordBox-middle');

            //复制粘贴清除样式
            $container.on({
                paste:function(e){
                    var _$this = $(this);
                    if(_$this.attr("contenteditable")=="plaintext-only"){
                        setTimeout(function(){
                            //e.preventDefault();
                            e.stopPropagation();
                            _$this.find("*").each(function(){
                                $(this).after($(this).text());
                                $(this).remove();
                            });
                            var reg=/&nbsp;/ig;
                            _$this.html(_$this.html().replace(reg,""));
                        }, 100)
                    }
                }}, '.listTitle,.listSummaryBox,.listSourceBox');
        }

        var resetaddList = function () {
            var $obj = LECurrentObject;
            if (!$obj.find(".list-group").hasClass("list-void")) {
                $addListBtn.hide();
                $("#addCustomList").hide();
                $updateListBtn.hide();
                $("#listEdit").show();
                $("#wholeSet").show();
            } else {
                $addListBtn.show();
                $("#addCustomList").show();
                $updateListBtn.show();
                $("#listEdit").hide();
                $("#wholeSet").hide();
            }
            var isUpdate=LECurrentObject.attr("data-update");
            if(isUpdate=="true"){
                $("#listNewSet").hide();
                $changeListBtn.show();
            }else{
                $("#listNewSet").show();
                $changeListBtn.hide();
            }
        };
        /*右侧链接复选框按钮初始化状态*/
        var resetLinkShow = function () {
            var displayStr = LECurrentObject.find("div.moreListShow").css("display");
            if (displayStr != "none") {
                $moreListShow.attr("checked", true);
            } else {
                $moreListShow.attr("checked", false);
            }
        };
        /*右侧链接设置处输入框初始化状态*/
        var resetLinkMessage = function () {
            var _text = LECurrentObject.find("div.moreListShow").find("a").find("span").text();
            var _link = LECurrentObject.find("div.moreListShow").find("a").attr("href");
            $("#moreListName").val(_text);
            $("#moreListLink").val(_link);
        };
        /*发布时间显示初始化状态*/
        var resetPublishTime = function () {
            var displayStr = LECurrentObject.find("span.timeShow").css("display");
            var _level=LECurrentObject.attr("data-level");
            if(_level=="top"){
                displayStr = LECurrentObject.find("span.timeShow").css("display");
            }else if(_level=="middle"){
                displayStr =LECurrentObject.find("p.listTime-middle").css("display")
            }else if(_level=="bottom"){
                displayStr =LECurrentObject.find("p.listTime-bottom").css("display");
            }

            if (displayStr != "none") {
                $("#moreListTime").attr("checked", true);
            } else {
                $("#moreListTime").attr("checked", false);
            }
        };

        /*摘要复选框按钮初始化状态*/
        var resetListAbstract = function () {
            var displayStr = LECurrentObject.find("p.listSummary").css("display");
            if (displayStr != "none") {
                $("#moreListAbstract").attr("checked", true);
            } else {
                $("#moreListAbstract").attr("checked", false);
            }
        };
        /*来源复选框按钮初始化状态*/
        var resetListSource = function () {
            var displayStr = LECurrentObject.find("p.listSource").css("display");
            var _level=LECurrentObject.attr("data-level");
            if(_level=="top"){
                displayStr = LECurrentObject.find("p.listSource").css("display");
            }else if(_level=="middle"){
                displayStr =LECurrentObject.find("p.listSource-middle").css("display")
            }else if(_level=="bottom"){
                displayStr =LECurrentObject.find("p.listSource-bottom").css("display");
            }

            if (displayStr != "none") {
                $("#moreListSource").attr("checked", true);
            } else {
                $("#moreListSource").attr("checked", false);
            }
        };

        /*超链接Trarget属性设置按钮初始化状态*/
        var resetTargetBtn = function () {
            var targetStr = LECurrentObject.find("a").attr("target");
            if (targetStr == "_blank") {
                $listTarget.attr("checked", true);
            } else {
                $listTarget.attr("checked", false);
            }
        };
        /*
         标签栏样式预览 实现方法  如果设置了样式 让预览处的html为按钮的html 否则为第1个标签样式（默认样式）
         */
        function resetListModelPreview() {
            var $target = LECurrentObject;
            var l = $listModelDiv.find("ul").length;
            var tabClassName = $target.find("ul").attr("data-prestyle");
            if (!tabClassName) {
                var listName=LECurrentObject.attr("data-name");
                if(listName=="listMedia"){
                    $listModelPreview.html($listModelDiv.children("ul").eq(11).html());
                }else if(listName=="listGroup"){
                    $listModelPreview.html($listModelDiv.children("ul").eq(0).html());
                }
            } else {
                for (var i = 0; i < l; i++) {
                    var className = $listModelDiv.find("ul.displayStyle").eq(i).attr("data-ref");
                    if (tabClassName == className) {
                        $listModelPreview.html($listModelDiv.children("ul").eq(i).html());
                    }
                }

            }
        }

        /*
         初始化时间格式
         */
        var resettimeFormat = function () {
            var _text = LECurrentObject.find("li.list-group-item").eq(0).find("span.timeShow").text();
            var timeStr = LECurrentObject.find("li.list-group-item").eq(0).find("span.timeShow").attr("data-time");
            if (_text && timeStr) {
                var arr1 = timeStr.split(" ");
                var arr2 = arr1[0].split("-");
                var arr3 = arr1[1].split(":");
                var _year = arr2[0];
                var _month = arr2[1];
                var _day = arr2[2];
                var _hours = arr3[0];
                var _minutes = arr3[1];
                var _seconds = arr3[2];
                var optionVal = _text.replace(_year, "@{Y}").replace(_month, "@{M}").replace(_day, "@{D}").replace(_hours, "@{H}").replace(_minutes, "@{MI}").replace(_seconds, "@{S}");
                var leng = $("#timeFormat").find("option").length;
                for (var i = 0; i < leng; i++) {
                    if ($("#timeFormat").find("option").eq(i).val() == optionVal) {
                        $("#timeFormat").find("option").eq(i).attr("selected", true);
                    }
                }
            } else {
                $("#timeFormat").find("option").eq(0).attr("selected", true);
            }
        };


        return {
            init: function () {
                //initaddListEvent();
                initAddListbyModel();
                initResetListbyModel();
                initAppendListbyModel();
                initclearListc();
                initMoreLinkShow();
                initpublishTimeEvent();
                initListModelShow();
                initListModelPreview();
                inittimeFormatEvent();
                initChangeListStyleBlock();
                initListTitleEdit();
                initSetListbyModel();
                initaddCustomList();
                initlistStyleSet();
                initlistImgUpload();
                initListAbstractEvent();
                initListSourceEvent();
                initUpdateList();
            },
            run: function (options, doHide) {
                resetaddList();
                resetLinkShow();
                resetLinkMessage();
                resetPublishTime();
                resetTargetBtn();
                resetListModelPreview();
                resettimeFormat();
                resetListAbstract();
                resetListSource();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                $PS.show();
                LEDisplay.show($PS, doHide);
            },
            destroy: function () {
                $PS.hide();
                $listModelDiv.animate({right: "-250px"}, "fast", "linear");
                $listModelGoBack.animate({right: "-250px"}, "fast", "linear");
            }
        };
    };
})(window, jQuery, LE, undefined);
var jsonList = [
    {   id: 'le_ListGroup_li_01',//id
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/4c562123-b3ea-4e84-a6e2-2a162d69977b.jpg',
        title: '我是列表的第一条标题', //h2
        summary: '没错，我是列表的第一部分内容',//p
        link: 'http://www.baidu.com',//a
        PublishTime: '2016-01-18 10:49:01',
        src: '文章来源1'
    },
    {   id: 'le_ListGroup_li_02',//id
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/4c562123-b3ea-4e84-a6e2-2a162d69977b.jpg',
        title: '我是列表的第二条标题', //h2
        summary: '没错，我是列表的第二部分内容',//p
        link: 'http://www.baidu.com',//a
        PublishTime: '2016-02-18 10:49:01',
        src: '文章来源2'
    },
    {   id: 'le_ListGroup_li_03',//id
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/4c562123-b3ea-4e84-a6e2-2a162d69977b.jpg',
        title: '我是列表的第三条标题', //h2
        summary: '没错，我是列表的第三部分内容',//p
        link: 'http://www.baidu.com',//a
        PublishTime: '2016-03-18 10:49:01',
        src: '文章来源3'
    },
    {   id: 'le_ListGroup_li_04',//id
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/4c562123-b3ea-4e84-a6e2-2a162d69977b.jpg',
        title: '我是列表的第四条标题', //h2
        summary: '没错，我是列表的第四部分内容',//p
        link: 'http://www.baidu.com',//a
        PublishTime: '2016-04-18 10:49:01',
        src: '文章来源4'
    },
    {   id: 'le_ListGroup_li_05',//id
        imgPath: 'http://a.page.9466.com/169705/66d6ef719597d15946d91e354df6d3f7.jpg',//src
        title: '我是列表的第五条标题', //h2
        summary: '没错，我是列表的第五部分内容',//p
        link: 'http://www.baidu.com',//a
        PublishTime: '2016-05-18 10:49:01',
        src: '文章来源5'
    },
    {   id: 'le_ListGroup_li_06',//id
        imgPath: 'http://a.page.9466.com/169705/cfd589543527127ced39e2b8d9ecb9cf.png',
        title: '我是列表的第六条标题', //h2
        summary: '没错，我是列表的第六部分内容',//p
        link: 'http://www.baidu.com',//a
        PublishTime: '2016-06-18 10:49:01',
        src: '文章来源6'
    }
];
function showListMediaDefaultImage(imgObj){
    imgObj.src='./export/img/noimg.jpg';
    imgObj.onerror=null; //控制不要一直跳动
    $(imgObj).closest('.media-left').hide();
}