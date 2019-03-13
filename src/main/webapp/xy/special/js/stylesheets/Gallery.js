/**
 * Created by isaac_gu on 2016/4/27.
 */
(function (window, $, LE) {
    LE.stylesheets["Gallery"] = function () {
        var $PS = $("#gallerySettingSection");
        var $container = $("#container");
        var $addMorePic = $("#addMorePic");
        var $galleryEdit = $("#galleryEdit");
        var $titleShow = $("#titleShow");
        var $gAllItemList = $("#gAllItemList");
        var $gUpdatePic=$("#gUpdatePic");
        var $gChangePic=$("#gChangePic");

        /*
         鼠标悬停点击时编辑标题内容 多图
         */
        function initTitleEdit() {
            $container.on({
                'mouseover': function () {
                    var unEdit=$(".le-gallery").hasClass("edit-disable");
                    if(!unEdit){
                        $(this).find(".imgToolbarOver").stop().show();
                    }/*,'mouseout':function(){
                     $(this).find(".imgToolbarOver").stop().fadeOut(100);
                     }*/
                    }

            }, '.over-title,.imgToolbarOver,.over-title>span');

            $container.on({
                'mouseout': function () {
                    $(this).find(".imgToolbarOver").stop().hide();
                }
            }, '.over-title');

            $container.on({
                'mouseover': function () {
                    var unEdit=$(".le-gallery").hasClass("edit-disable");
                    if(!unEdit){
                        $(this).find(".imgToolbar").stop().show();
                    }/*,'mouseout':function(){
                     $(this).find(".imgToolbar").stop().fadeOut(100);
                     }*/
                    }

            }, '.bottom-title,.imgToolbar,.bottom-title>a');

            $container.on({
                'mouseout': function () {
                    $(this).find(".imgToolbar").stop().hide();
                }
            }, '.bottom-title');

            $container.on({
                "click": function () {
                    $(this).parent().find("a").attr("contenteditable", "plaintext-only");
                    //$(this).parent().find("a").attr("contenteditable",true);
                    $(this).parent().find("a").css({"display": "block", "width": "100%", "height": "100%","overflow":"inherit","white-space":"inherit"});
                    $(this).parent().find("a").trigger("focus");
                }
            }, '.imgToolbar');

            $container.on({
                "click": function () {
                    $(this).parent().find("span.over-titleBox").attr("contenteditable", "plaintext-only");
                    $(this).parent().find("span.over-titleBox").css({"display":"block","overflow":"inherit","height":"100%"});
                    $(this).attr("contenteditable", false);
                    $(this).parent().find("span.over-titleBox").trigger("focus");
                }
            }, '.imgToolbarOver');

            $container.on({
                "blur": function () {
                    $(this).attr("contenteditable", false);
                    $(this).parent().parent().parent().parent().parent().find("div.marginItem").next().find("a").html($(this).html());
                    $(this).css({"display":"","overflow":"","height":""});
                    LEHistory.trigger();
                }/*, "keydown": function(event){
                 if(event.keyCode == 13){
                 $(this).trigger("blur");
                 }
                 }*/
            }, '.over-titleBox');

            $container.on({
                "blur": function () {
                    $(this).attr("contenteditable", false);
                    $(this).parent().prev().find("p.over-title").find("span").eq(0).html($(this).html());
                    $(this).css({"overflow":"","white-space":""});
                    if(LECurrentObject.hasClass("le-gallery")) {
                        LE.stylesheets["Gallery"]().changeImageHeight();
                    }
                    LEHistory.trigger();
                }/*, "keydown": function(event){
                 if(event.keyCode == 13){
                 $(this).trigger("blur");
                 }
                 }*/
            }, '.bottom-title>a');

            //复制粘贴清除格式
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
                            //alert(LECurrentObject.html());
                        }, 100)
                    }
                }}, '.bottom-title>a,.over-titleBox');
        }

        var initaddMorePicbyModel = function () {
            /**
             * 多图的添加功能 添加html内容
             */
            $addMorePic.click(function () {
                var data = [];
                LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function (json) {
                        addGallerys(json);
                    },
                    data
                );
                LECurrentObject.attr("data-type","sql");
                // LEHistory.trigger();//已经在添加图片完成后触发的$("#css-position_height").trigger("focus").trigger("blur");里执行了该方法
            });
        };

        var initResetPicbyModel = function () {
            /**
             * 多图的重置功能 重写html内容
             */
            $("#gReset").click(function () {
                LECurrentObject.attr("data-reset","true");
                if(LECurrentObject.attr("data-type")=="sql"){
                    var data = [];
                    LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function (json) {
                            addGallerys(json);
                        },
                        data
                    );
                    LECurrentObject.attr("data-type","sql");
                }else{
                    chooseLocalImage();
                    LECurrentObject.attr("data-type","local");
                }

                // LEHistory.trigger();//已经在添加图片完成后触发的$("#css-position_height").trigger("focus").trigger("blur");里执行了该方法
            });
        };
        var initUpdatePicbyModel = function () {
            /**
             * 多图的动态更新
             */
            $gUpdatePic.click(function () {
                var data = [];
                LEDialog.toggleDialog(LE.options["Dialog"].updaListDialog, function(updateSet){
                        var countStart=updateSet.lastIndexOf(":")+1;
                        var countEnd=updateSet.lastIndexOf("}");
                        var count=updateSet.substring(countStart,countEnd);
                        var jsonList=[];
                        var list={
                            PublishTime: "2017-02-08 18:09:42",
                            id:"example001",
                            imgPath: "export/images/navMenu/navMenu15.png",
                            //imgPath: "http://172.19.33.95/pic/201608/05/t2_(9X57X600X389)a432d0f1-3702-4239-ad8f-7405a534fb24.jpg",
                            link: "http://",
                            src: "多图动态更新的标题",
                            summary: "多图动态更新的标题",
                            title: "多图动态更新的标题"
                        };
                        for(var i=0;i<count;i++){
                            jsonList.push(list)
                        }
                        // console.log(countStart+"---"+countEnd+"---"+count);
                        //为设置了动态更新的多图添加标记
                        LECurrentObject.attr("data-update","true");
                        addGallerys(jsonList);
                        //添加保存时替换的标记
                        var _id=LECurrentObject.attr("id");
                        LECurrentObject.prepend("<!-----galleryUpdate"+_id+"----->");
                        LECurrentObject.append("<!-----galleryUpdate"+_id+"----->");
                        LECurrentObject.attr("data-updateset",updateSet);
                        LEHistory.trigger();
                    },
                    data
                );
            });

            $gChangePic.click(function () {
                //{'columnid':[82],'columntype':'self', 'articletype':'article|pic|video|link|special','parentTree':'{'level':'level1'---'columnId':'52'}|{'level':'level0'---'columnId':'8'}','articleattr':'all','start':0, 'count':1}
                var updateset=LECurrentObject.attr("data-updateset");
                var selecteds = updateset.split("[")[1].split("]")[0].split(",").map(function(x){return parseInt(x)});
                var selectednum = updateset.split("'count':")[1].split("}")[0];
                var columntype = updateset.split("'articletype':'")[1].split("',")[0].split('|');
                var parentTree = updateset.split("'parentTree':'")[1].split("',")[0].split('|');
                var articleattr= [];
                if(updateset.indexOf("'article_attr':'")!=-1){
                    articleattr=updateset.split("'article_attr':'")[1].split("',")[0].toLowerCase().split('|');
                }
                var data={
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
                            PublishTime: "2017-02-08 18:09:42",
                            id:"example001",
                            imgPath: "export/images/navMenu/navMenu15.png",
                            //imgPath: "http://172.19.33.95/pic/201608/05/t2_(9X57X600X389)a432d0f1-3702-4239-ad8f-7405a534fb24.jpg",
                            link: "http://",
                            src: "多图动态更新的标题",
                            summary: "多图动态更新的标题",
                            title: "多图动态更新的标题"
                        };
                        for(var i=0;i<count;i++){
                            jsonList.push(list)
                        }
                        // console.log(countStart+"---"+countEnd+"---"+count);
                        //为设置了动态更新的多图添加标记
                        LECurrentObject.attr("data-update","true");
                        addGallerys(jsonList);
                        //添加保存时替换的标记
                        var _id=LECurrentObject.attr("id");
                        LECurrentObject.prepend("<!-----galleryUpdate"+_id+"----->");
                        LECurrentObject.append("<!-----galleryUpdate"+_id+"----->");
                        LECurrentObject.attr("data-updateset",updateSet);
                       LEHistory.trigger();
                    },
                    data
                );
            });
        };
        function addGallerys(jsonArray) {
            if (jsonArray.length == 0) return;
            $galleryEdit.show();
            $addMorePic.hide();
            $("#addLocalPic").hide();
            var len = jsonArray.length;
            var strH = "";
            var previewstrH = "";
            for (var i = 0; i < len; i++) {
                var id_Gallery = 'le_Gallery_div_' + LEDrag.getNextId() + '_' + jsonArray[i].id;
                var str = ['<div id="',
                    id_Gallery,
                    '" class=" col-md-3 pull-left"><div class="marginItem"><div class="borderItem"><a href="',
                    jsonArray[i].link,
                    '" style="position: relative; display: block; overflow: hidden;"target=_blank;>',
                    '<p class="over-title gallery-title" style=""><span class="over-titleBox gallery-title-text" style="min-height: 2.8em;width: 100%">',
                    jsonArray[i].title,
                    '</span><span class="imgToolbarOver">',
                    '</span></p><img onerror="showImgAgain(this,\''+jsonArray[i].imgPath+'\',10,true);" data-src="holder.js/100%x100%" class="" alt="" src="',
                    jsonArray[i].imgPath,
                    '" data-holder-rendered="true" style="width: 100%;display: block;">',
                    '</a></div></div><p class="bottom-title gallery-title"><a class="gallery-title-text" href="',
                    jsonArray[i].link,
                    '" target=_blank;>',
                    jsonArray[i].title,
                    '</a><span class="imgToolbar">',
                    '</span></p></div>'].join("");
                strH += str;
                var previewStr = '<li data-imgpath="' + jsonArray[i].imgPath + '" data-title="' + jsonArray[i].title + '" data-link="' + jsonArray[i].link + '" data-id="' + id_Gallery + '" data-flag="' + i + '" class="posRelative pull-left mgr15 mgb10 border65"><img class="previewGallery" width="78" height="50"  onerror="showImgAgain(this,\''+jsonArray[i].imgPath+'\',10,\'preview\');"    src="' + jsonArray[i].imgPath + '" alt="" /><img class="removeGallery posAbsolute cancelBtn" data-removeId="' + id_Gallery + '" src="export/images/sliderPanel/sliderPanel19.png" alt="" /></li>';
                previewstrH += previewStr;
            }

            //重置之前记录多图样式
            /*if(LECurrentObject.attr("data-reset") == "true"){
                var _record = {
                    //显示列数
                    'columnStyle':LECurrentObject.find(".col-md-3").attr("style"),
                    //间距
                    'marginStyle':LECurrentObject.find(".marginItem").attr("style"),
                    //相框
                    'borderStyle':LECurrentObject.find(".borderItem").attr("style"),
                    //中部标题显示
                    'overTitleStyle': LECurrentObject.find(".over-title").attr("style"),
                    //底部部标题显示
                    'bottomTitleStyle': LECurrentObject.find(".bottom-title").attr("style"),
                    //位置底部，位置覆盖
                    'titleStyle': LECurrentObject.find(".bottom-title").children("a").attr("style"),
                    //容器位置大小，背景，边框
                    'otherStyle': LECurrentObject.attr("style")

                }
            }*/

            LECurrentObject.html(strH);
            $gAllItemList.html(previewstrH);


            //重置之后写回样式
            /*if(LECurrentObject.attr("data-reset") == "true"){
                //显示列数
                if(_record.columnStyle){
                    LECurrentObject.find(".col-md-3").attr("style", _record.columnStyle);
                }
                //间距
                if(_record.marginStyle){
                    LECurrentObject.find(".marginItem").attr("style", _record.marginStyle);
                }
                //相框
                if(_record.borderStyle){
                    LECurrentObject.find(".borderItem").attr("style", _record.borderStyle);
                }
                if(_record.titleStyle){
                    //位置底部
                    LECurrentObject.find(".bottom-title").children("a").attr("style",_record.titleStyle);
                    //标题位置覆盖
                    LECurrentObject.find(".over-title").children(".over-titleBox").attr("style",_record.titleStyle);
                }
                //中部标题显示
               if(_record.overTitleStyle){
                    LECurrentObject.find(".over-title").attr("style", _record.overTitleStyle);
               }
                //底部标题显示
                if(_record.bottomTitleStyle){
                    LECurrentObject.find(".bottom-title").attr("style", _record.bottomTitleStyle);
                }
                //容器位置大小，背景，边框
                if(_record.otherStyle){
                    LECurrentObject.attr("style", _record.otherStyle);
                }

            }*/

            LE.stylesheets["Gallery"]().changeImageHeight();
            $("#css-position_height").trigger("focus").trigger("blur");
            LECurrentObject.trigger("click");
        }


    /*
     点击添加稿件 添加内容
     */
        var initaddMorePic = function () {
            //var editStr='<div class="altwrap" style="display: none;"><div class="title">标题修改</div><textarea class="input"  id="alt" cols="30" rows="10"></textarea><div><input type="button" class="titleConfirm" value="确认"><input type="button" class="titleCancel" value="取消"></div></div>';
            $addMorePic.bind("click", function () {
                $galleryEdit.show();
                $addMorePic.hide();
                var len = jsonArray.length;
                var strH = "";
                var previewstrH = "";
                for (var i = 0; i < len; i++) {
                    var str = ['<div id="',
                        jsonArray[i].id,
                        '" class=" col-md-3"><div class="marginItem"><div class="borderItem"><a href="',
                        jsonArray[i].link,
                        '" style="position: relative; display: block; overflow: hidden;"target=_blank;>',
                        '<p class="over-title gallery-title" style=""><span class="over-titleBox" style="display: block;min-height: 2.8em;height: 100%;width: 100%">',
                        jsonArray[i].title,
                        '</span><span class="imgToolbarOver">',
                        '</span></p><img onerror=\"this.src=\'http://127.0.0.1:8080/xy/xy/special/export/img/noimg.png\'\" data-src="holder.js/100%x100%" class="" alt="" src="',
                        jsonArray[i].imgPath,
                        '" data-holder-rendered="true" style="width: 100%;height:212px;display: block;">',
                        '</a></div></div><p class="bottom-title gallery-title"><a href="',
                        jsonArray[i].link,
                        '" target=_blank;>',
                        jsonArray[i].title,
                        '</a><span class="imgToolbar">',
                        '</span></p></div>'].join("");
                    strH += str;
                    var previewStr = '<li data-imgpath="' + jsonArray[i].imgPath + '" data-title="' + jsonArray[i].title + '" data-link="' + jsonArray[i].link + '" data-id="' + jsonArray[i].id + '" data-flag="' + i + '" class="posRelative pull-left mgr15 mgb10 border65"><img onerror=\"this.src=\'http://127.0.0.1:8080/xy/xy/special/export/img/noimg.png\'\" class="previewGallery" width="78" height="50"  src="' + jsonArray[i].imgPath + '" alt="" /><img class="removeGallery posAbsolute cancelBtn" data-removeId="' + jsonArray[i].id + '" src="export/images/sliderPanel/sliderPanel19.png" alt="" /></li>';
                    previewstrH += previewStr;
                }
                LECurrentObject.html(strH);
                $gAllItemList.html(previewstrH);
                LE.stylesheets["Gallery"]().changeImageHeight();
                $("#position_height").trigger("focus").trigger("blur");
                LECurrentObject.trigger("click");
            })
        };

        /*
         删除稿件
         */
        var initclearAllPic = function () {
            /*缩略图hover事件 删除按钮的显示隐藏*/
            $PS.on({
                'mouseover': function () {
                    $(this).next().show();
                }, 'mouseout': function () {
                    $(this).next().hide();
                }
            }, '.previewGallery');
            $PS.on({
                'mouseover': function () {
                    $(this).show();
                }, 'mouseout': function () {
                    $(this).hide();
                }
            }, '.removeGallery');
            /*全部清空*/
            $("#gAllClear").bind("click", function () {
                LECurrentObject.html(LE.options["Gallery"].viewinnerHtml);
                //修正图片列数
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " .col-md-3");
                var _width=parseFloat(100 / 4) + "%";
                StyleManager.setStyle(selector, "width",_width);

                LE.stylesheets["Gallery"]().changeImageHeight();
                LEHistory.trigger();
                LECurrentObject.trigger("click");
            });
            /*部分删除*/
            $PS.on({
                'click': function () {
                    var removeId = $(this).attr("data-removeId");
                    $(this).parent().remove();
                    $("#" + removeId).remove();
                    // $PS.find("li").filter('.select').trigger("click");
                    if (LECurrentObject.html() == "") {
                        LECurrentObject.html(LE.options["Gallery"].viewinnerHtml);
                        LE.stylesheets["Gallery"]().changeImageHeight();
                        LECurrentObject.trigger("click");
                    }
                    LEHistory.trigger();
                }
            }, '.removeGallery');
        };


        var initAppendMorePicbyModel = function () {
            /**
             * 多图的追加功能
             */
            $("#gAdd").click(function () {
                if(LECurrentObject.attr("data-type")=="sql") {
                    var data = [];
                    LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function (json) {
                            appendGallerys(json);
                        },
                        data
                    );
                }else{
                    appendLocalImage();
                }
                //LEHistory.trigger();//已经在方法appendGallerys里执行了该方法

            });
        };

        function appendGallerys(jsonArray2) {
            if (jsonArray2.length == 0) return;

            var len = jsonArray2.length;
            var addstrH = "";
            var previewstrH = "";
            for (var i = 0; i < len; i++) {
                var id_Gallery_append = 'le_Gallery_div_' + LEDrag.getNextId() + '_' + jsonArray2[i].id;
                var str = ['<div id="',
                    id_Gallery_append,
                    '" class=" col-md-3 pull-left"><div class="marginItem"><div class="borderItem"><a href="',
                    jsonArray2[i].link,
                    '" style="position: relative; display: block; overflow: hidden;"target=_blank;>',
                    '<p class="over-title gallery-title" style=""><span class="over-titleBox gallery-title-text" style="min-height: 2.8em;width: 100%">',
                    jsonArray2[i].title,
                    '</span><span class="imgToolbarOver">',
                    '</span></p><img  onerror="showImgAgain(this,\''+jsonArray2[i].imgPath+'\',10,true);"  data-src="holder.js/100%x100%" class="" alt="" src="',
                    jsonArray2[i].imgPath,
                    '" data-holder-rendered="true" style="width: 100%;display: block;">',
                    '</a></div></div><p class="bottom-title gallery-title"><a class="gallery-title-text" href="',
                    jsonArray2[i].link,
                    '" target=_blank;>',
                    jsonArray2[i].title,
                    '</a><span class="imgToolbar">',
                    '</span></p></div>'].join("");
                addstrH += str;
                var previewStr = '<li data-imgpath="' + jsonArray2[i].imgPath + '" data-title="' + jsonArray2[i].title + '" data-link="' + jsonArray2[i].link + '" data-id="' + id_Gallery_append + '" data-flag="' + i + '" class="posRelative pull-left mgr15 mgb10 border65"><img class="previewGallery" width="78" height="50"   onerror="showImgAgain(this,\''+jsonArray2[i].imgPath+'\',10,\'preview\');"  src="' + jsonArray2[i].imgPath + '" alt="" /><img class="removeGallery posAbsolute cancelBtn" data-removeId="' + id_Gallery_append + '" src="export/images/sliderPanel/sliderPanel19.png" alt="" /></li>';
                previewstrH += previewStr;
            }

            //获取自定义设置的样式
           // var _record=LECurrentObject.find(".bottom-title").children("a").attr("style");

            LECurrentObject.append(addstrH);
            $gAllItemList.append(previewstrH);
            //自定义样式赋给追加后的内容
            /*LECurrentObject.find(".bottom-title").children("a").attr("style",_record);
            LECurrentObject.find(".over-title").children(".over-titleBox").attr("style",_record);*/

            LE.stylesheets["Gallery"]().changeImageHeight();
            //$(this).children().eq(0).trigger("click");
            // $PS.find("li").filter('.select').trigger("click");
            LEHistory.trigger();
        }

        /*
         点击追加稿件
         */
        var initAppendMorePic = function () {
            $("#gAdd").bind("click", function () {
                var len = jsonArray2.length;
                var addstrH = "";
                var previewstrH = "";
                for (var i = 0; i < len; i++) {
                    var str = ['<div id=',
                        jsonArray2[i].id,
                        ' class=" col-md-3"><div class="marginItem"><div class="borderItem"><a href=',
                        jsonArray2[i].link,
                        ' style="position: relative; display: block; overflow: hidden;"target=_blank;>',
                        '<p class="over-title gallery-title" style=""><span style="display: block;min-height: 2.8em;height: 100%;width: 100%">',
                        jsonArray2[i].title,
                        '</span><span class="imgToolbarOver">',
                        '</span></p><img data-src="holder.js/100%x100%" class="" alt="" src=',
                        jsonArray2[i].imgPath,
                        ' data-holder-rendered="true" style="; width: 100%;height:212px;display: block;">',
                        '</a></div></div><p class="bottom-title gallery-title"><a href=',
                        jsonArray2[i].link,
                        ' target=_blank;>',
                        jsonArray2[i].title,
                        '</a><span class="imgToolbar">',
                        '</span></p></div>'].join("");
                    addstrH += str;
                    var previewStr = '<li data-imgpath=' + jsonArray2[i].imgPath + ' data-title=' + jsonArray2[i].title + ' data-link=' + jsonArray2[i].link + ' data-id=' + jsonArray2[i].id + ' data-flag=' + i + ' class="posRelative pull-left mgr15 mgb10 border65"><img class="previewGallery" width="78" height="50"  src=' + jsonArray2[i].imgPath + ' alt="" /><img class="removeGallery posAbsolute cancelBtn" data-removeId=' + jsonArray2[i].id + ' src="export/images/sliderPanel/sliderPanel19.png" alt="" /></li>';
                    previewstrH += previewStr;
                }

                LECurrentObject.append(addstrH);
                $gAllItemList.append(previewstrH);
                LE.stylesheets["Gallery"]().changeImageHeight();
                //$(this).children().eq(0).trigger("click");
                $PS.find("li").filter('.select').trigger("click");
            })
        };
        var initdragPic = function () {
            var dragstartWidth = null;
            var dragstartMrgin = null;
            var dragstartPadding = null;
            var dragstartBorder = null;
            var dragstartBtitle = null;
            var dragstartOtitle = null;
            var dragstartCursor = null;
            var dragstartStyle = null;

            $gAllItemList.sortable({
                items: "li",
                placeholder: "gallery-state-highlight",
                start: function (event) {
                    var startWidth = parseInt(LECurrentObject.find("div[class*='col-md-3']").css("width"));
                    var startMrgin = parseInt(LECurrentObject.find("div[class*='marginItem']").css("padding"));
                    var startPadding = parseInt(LECurrentObject.find("div[class*='borderItem']").css("padding"));
                    var startBorder = parseInt(LECurrentObject.find("div[class*='borderItem']").css("border"));
                    var startBtitle = LECurrentObject.find("p[class*='bottom-title']").css("display");
                    var startOtitle = LECurrentObject.find("p[class*='over-title']").css("display");
                    var startCursor = LECurrentObject.find("img").css("cursor");
                    //获取自定义设置的样式
                    //var _record=LECurrentObject.find(".bottom-title").children("a").attr("style");


                    dragstartWidth = startWidth;
                    dragstartMrgin = startMrgin;
                    dragstartPadding = startPadding;
                    dragstartBorder = startBorder;
                    dragstartBtitle = startBtitle;
                    dragstartOtitle = startOtitle;
                    dragstartCursor=startCursor;
                    // dragstartStyle = _record;
                    var leng = LECurrentObject.find("div[class*='col-md-3']").length;
                    //arrTitle=[];
                    for (var i = 0; i < leng; i++) {
                        var startHtitle = LECurrentObject.find("div[class*='col-md-3']").eq(i).find("p[class*='over-title']").children().eq(0).html();
                        //arrTitle.push(dragstartHtitle1);
                        $gAllItemList.find("li").not(":empty").eq(i).attr("data-title", startHtitle);
                    }

                    //var i=Math.ceil(parseFloat(_boxColum/_colum).toFixed(1));
                    /*dragstartX=event.pageX;
                     dragstartY=event.pageY;*/
                    /* console.error(123);
                     var len=$(this).find("li").length-1;
                     for(var i=0;i<len;i++){
                     $(this).find("li")[i].attr("data-flag","i");
                     }*/
                },
                stop: function (event) {
                    var len = $(this).find("li").length;
                    var sortStr = "";
                    for (var i = 0; i < len; i++) {
                        var sortId = $(this).children().eq(i).attr("data-id");
                        var sortLink = $(this).children().eq(i).attr("data-link");
                        var sortTitle = $(this).children().eq(i).attr("data-title");
                        var sortImgpath = $(this).children().eq(i).attr("data-imgpath");
                        var str = ['<div id="',
                            sortId,
                                '" class=" col-md-3 pull-left" style="width:' + dragstartWidth + 'px;"><div class="marginItem" style="padding:' + dragstartMrgin + 'px;">',
                                '<div class="borderItem" style="border:' + dragstartBorder + 'px solid #ccc;padding:' + dragstartPadding + 'px;"><a href="',
                            sortLink,
                            '" style="position: relative; display: block; overflow: hidden;" target=_blank;>',
                                '<p class="over-title gallery-title" style="display: ' + dragstartOtitle + '";><span class="over-titleBox gallery-title-text" style="min-height: 2.8em;width: 100%">',
                            sortTitle,
                            '</span><span class="imgToolbarOver">',
                            '</span></p><img  onerror="showImgAgain(this,\''+sortImgpath+'\',10,true);"  data-src="holder.js/100%x100%" class="" alt="" src="',
                            sortImgpath,
                            '" data-holder-rendered="true" style="width: 100%;display: block;">',
                                '</a></div></div><p class="bottom-title gallery-title" style="display: ' + dragstartBtitle + '";><a class="gallery-title-text" href="',
                            sortLink,
                            '" target=_blank;>',
                            sortTitle,
                            '</a><span class="imgToolbar">',
                            '</span></p></div>'].join("");
                        sortStr += str;
                    }
                    LECurrentObject.html(sortStr);
                    //自定义样式赋给drag后的内容
                    //LECurrentObject.find(".bottom-title").children("a").attr("style",dragstartStyle);
                    LECurrentObject.find("img").css("cursor",dragstartCursor);
                    //LECurrentObject.find(".over-title").children(".over-titleBox").attr("style",dragstartStyle);
                    //var previewStr='<li class="posRelative pull-left mgr15 mgb10 border65"><img width="78" height="50" src="export/images/sliderPanel/sliderPanel18.png" alt="" /><img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt="" /></li>';
                    LE.stylesheets["Gallery"]().changeImageHeight();
                    LECurrentObject.trigger("click");
                    $PS.find("li").filter('.select').trigger("click");
                    LEHistory.trigger();

                    /*var stopX=event.pageX;
                     var stopY=event.pageY;
                     //alert((stopX-dragstartX)+"!!!!!!!!"+(stopY-dragstartY));
                     console.info(111);
                     var len = $(this).find("li").length;
                     var arr=[];
                     for (var i = 0; i < len; i++) {
                     if($(this).children().eq(i).index()!=$(this).children().eq(i).attr("data-flag")){
                     arr.push(i);
                     }
                     $(this).children().eq(i).attr("data-flag",i);
                     }
                     var starIndex=arr[0];
                     var endIndex=arr.length-1+starIndex;
                     if(stopX-dragstartX+stopY-dragstartY>0){
                     //从前往后拖拽
                     var startBox=$('<div id='+LECurrentObject.children().eq(starIndex).attr("id")+' class=" col-md-3"></div>');
                     var startstring=LECurrentObject.children().eq(starIndex).html();
                     startBox.append(startstring);
                     LECurrentObject.children().eq(endIndex).after(startBox);
                     LECurrentObject.children().eq(starIndex).remove();
                     // alert(starIndex+"------------"+endIndex);
                     }else{
                     //从后往前拖拽
                     var endBox=$('<div id='+LECurrentObject.children().eq(endIndex).attr("id")+' class=" col-md-3"></div>');
                     var endstring=LECurrentObject.children().eq(endIndex).html();
                     endBox.append(endstring);
                     LECurrentObject.children().eq(starIndex).before(endBox);
                     LECurrentObject.children().eq(endIndex+1).remove();
                     }*/
                }
            });
        };
        var initdragPicCss = function () {
            var dragstartCursor = null;

            $gAllItemList.sortable({
                items: "li",
                placeholder: "gallery-state-highlight",
                start: function (event) {
                    var startCursor = LECurrentObject.find("img").css("cursor");

                    dragstartCursor=startCursor;
                    var leng = LECurrentObject.find("div[class*='col-md-3']").length;
                    for (var i = 0; i < leng; i++) {
                        var startHtitle = LECurrentObject.find("div[class*='col-md-3']").eq(i).find("p[class*='over-title']").children().eq(0).html();
                        $gAllItemList.find("li").not(":empty").eq(i).attr("data-title", startHtitle);
                    }

                },
                stop: function (event) {
                    var len = $(this).find("li").length;
                    var sortStr = "";
                    for (var i = 0; i < len; i++) {
                        var sortId = $(this).children().eq(i).attr("data-id");
                        var sortLink = $(this).children().eq(i).attr("data-link");
                        var sortTitle = $(this).children().eq(i).attr("data-title");
                        var sortImgpath = $(this).children().eq(i).attr("data-imgpath");
                        var str = ['<div id="',
                            sortId,
                                '" class=" col-md-3 pull-left"><div class="marginItem">',
                                '<div class="borderItem"><a href="',
                            sortLink,
                            '" style="position: relative; display: block; overflow: hidden;" target=_blank;>',
                                '<p class="over-title gallery-title"><span class="over-titleBox gallery-title-text" style="min-height: 2.8em;width: 100%">',
                            sortTitle,
                            '</span><span class="imgToolbarOver">',
                            '</span></p><img data-src="holder.js/100%x100%" class="" alt="" src="',
                            sortImgpath,
                            '" data-holder-rendered="true" style="width: 100%;display: block;">',
                                '</a></div></div><p class="bottom-title gallery-title"><a class="gallery-title-text" href="',
                            sortLink,
                            '" target=_blank;>',
                            sortTitle,
                            '</a><span class="imgToolbar">',
                            '</span></p></div>'].join("");
                        sortStr += str;
                    }
                    LECurrentObject.html(sortStr);
                    LECurrentObject.find("img").css("cursor",dragstartCursor);
                    LE.stylesheets["Gallery"]().changeImageHeight();
                    LECurrentObject.trigger("click");
                    $PS.find("li").filter('.select').trigger("click");
                    LEHistory.trigger();
                }
            });
        };
        /*
         1、点击列数 改变li宽度 所占百分比实现
         2、点击间距 最外层div padding 0 4 8 12
         3、点击相框 第二层div border  0 1 1 1
         padding 0 2 4 6
         4、点击裁切
         */

        /*改变组图列数*/
        var initColumnChange = function () {
            $("#columnChange").find("li").bind("click", function () {
               // LECurrentObject.find("div[class*='col-md-3']").css("width", parseFloat(100 / $(this).text()) + "%");
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " .col-md-3");
                var _width=parseFloat(100 / $(this).text()) + "%";
                StyleManager.setStyle(selector, "width",_width);
                LE.stylesheets["Gallery"]().changeImageHeight();
                LEHistory.trigger();
            })
        };
        /*改变组图间距*/
        var initPaddPic = function () {
            $("#paddPic").find("li").bind("click", function () {
                /*LECurrentObject.find("div[class*='marginItem']").css("padding", parseInt(($(this).index() - 1) * 4));
                LECurrentObject.find("p.bottom-title").css({
                    "padding-left": parseInt(($(this).index() - 1) * 4),
                    "padding-right": parseInt(($(this).index() - 1) * 4),
                    "padding-top": "0",
                    "padding-bottom": "0"
                });*/
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " .col-md-3 .marginItem");
                var _padding=parseInt(($(this).index() - 1) * 4);
                StyleManager.setStyle(selector, "padding",_padding+"px");

                var bottomTitle = $.trim("#" + id + " .col-md-3 .bottom-title");
                StyleManager.setStyle(bottomTitle, "padding-left",_padding+"px");
                StyleManager.setStyle(bottomTitle, "padding-right",_padding+"px");
                StyleManager.setStyle(bottomTitle, "padding-top","0");
                StyleManager.setStyle(bottomTitle, "padding-bottom","0");

                LE.stylesheets["Gallery"]().changeImageHeight();
                LECurrentObject.trigger("click");
                LEHistory.trigger();
            })
        };
        /*改变组图相框 包括边框和内边距*/
        var initBorderPic = function () {
            $("#borderPic").find("li").bind("click", function () {
                /*LECurrentObject.find("div[class*='borderItem']").css({
                    "border": $(this).attr('data-border') + " solid #ccc",
                    "padding": $(this).attr("data-pad")
                });*/
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " .col-md-3 .marginItem .borderItem");
                var _border=$(this).attr('data-border') + " solid #ccc";
                var _padding=$(this).attr("data-pad");
                StyleManager.setStyle(selector, "border",_border);
                StyleManager.setStyle(selector, "padding",_padding);

                LE.stylesheets["Gallery"]().changeImageHeight();
                LECurrentObject.trigger("click");
                LEHistory.trigger();
            })
        };
        /*改变标题是否可以显示*/
        var inittitleShow = function () {
            $titleShow.bind("click", function () {
                var id = LECurrentObject.attr("id");
                var _overTitle = $.trim("#" + id + " .col-md-3 .over-title");
                var _bottomTitle = $.trim("#" + id + " .col-md-3 .bottom-title");
                if ($titleShow.is(':checked')) {
                    if ($("#titlePic").find("li").eq(0).hasClass("select")) {
                        //LECurrentObject.find("p[class*='over-title']").hide();
                       // LECurrentObject.find("p[class*='bottom-title']").show();
                        StyleManager.setStyle(_overTitle, "display", "none");
                        StyleManager.setStyle(_bottomTitle, "display", "block");
                    } else if ($("#titlePic").find("li").eq(1).hasClass("select")) {
                        //LECurrentObject.find("p[class*='over-title']").show();
                        //LECurrentObject.find("p[class*='bottom-title']").hide();
                        StyleManager.setStyle(_overTitle, "display", "block");
                        StyleManager.setStyle(_bottomTitle, "display", "none");
                    }
                } else {
                    //LECurrentObject.find("p[class*='over-title']").hide();
                    //LECurrentObject.find("p[class*='bottom-title']").hide();
                    StyleManager.setStyle(_overTitle, "display", "none");
                    StyleManager.setStyle(_bottomTitle, "display", "none");
                }
                LE.stylesheets["Gallery"]().changeImageHeight();
                LECurrentObject.trigger("click");
                LEHistory.trigger();
            })
        };
        /*改变标题位置*/
        var inittitlePlace = function () {
            $("#titlePic").find("li").bind("click", function () {
                var id = LECurrentObject.attr("id");
                var _overTitle = $.trim("#" + id + " .col-md-3 .over-title");
                var _bottomTitle = $.trim("#" + id + " .col-md-3 .bottom-title");
                if ($titleShow.is(':checked')) {
                    if ($(this).index() == 1) {
                        //LECurrentObject.find("p[class*='over-title']").hide();
                       //LECurrentObject.find("p[class*='bottom-title']").show();
                        StyleManager.setStyle(_overTitle, "display", "none");
                        StyleManager.setStyle(_bottomTitle, "display", "block");
                    } else if ($(this).index() == 2) {
                        //LECurrentObject.find("p[class*='over-title']").show();
                        //LECurrentObject.find("p[class*='bottom-title']").hide();
                        StyleManager.setStyle(_overTitle, "display", "block");
                        StyleManager.setStyle(_bottomTitle, "display", "none");
                    }
                } else {
                    //LECurrentObject.find("p[class*='over-title']").hide();
                    //LECurrentObject.find("p[class*='bottom-title']").hide();
                    StyleManager.setStyle(_overTitle, "display", "none");
                    StyleManager.setStyle(_bottomTitle, "display", "none");
                }
                LE.stylesheets["Gallery"]().changeImageHeight();
                LECurrentObject.trigger("click");
                LEHistory.trigger();
            })
        };
        /*点击进行图片修改*/
        var initGalleryUpload = function () {
            $PS.on({
                'click': function () {
                    var _$this = $(this);
                    _$this.addClass("gp_chosen");
                    var data = [];
                    var _$imgs = _$this.parent().parent().find(".previewGallery");
                    var _index = null;
                    _$imgs.each(function (index) {
                        if ($(this).hasClass("gp_chosen")) {
                            _index = index;
                        }
                        data.push($(this).attr("src"));
                    });
                    _$this.removeClass("gp_chosen");
                    var imgWidth = parseInt(LECurrentObject.find("img").css("width"));
                    var imgHeight = parseInt(LECurrentObject.find("img").css("height"));
                    var ratio = imgWidth / imgHeight;
                    var timeStamp = new Date().getTime();
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog + "ratio=" + ratio + "&index=" + _index + "&timestamp=" + timeStamp, function (imgList) {
                            if (imgList.length > 0) {
                                for (var i = 0, len = imgList.length; i < len; i++) {
                                    var thisID = _$this.parent().parent().find("li").eq(i).attr("data-id");
                                    _$this.parent().parent().find("li").eq(i).find(".previewGallery").attr("src", imgList[i].path);
                                    _$this.parent().parent().find("li").eq(i).attr("data-imgpath", imgList[i].path);
                                    $("#" + thisID).find("img").attr("src", imgList[i].path);
                                }
                                //在图片修改完毕之后触发
                                LEHistory.trigger();
                            }
                        },
                        data
                    );
                    // LEHistory.trigger();
                }
            }, '.previewGallery');
        };

            /*添加本地图片*/
        var  initAddLocalPicEvent=function(){
            $("#addLocalPic").click(function(){
                chooseLocalImage();
                LECurrentObject.attr("data-type","local");
                // LEHistory.trigger();  //已经在添加图片完成后触发的$("#css-position_height").trigger("focus").trigger("blur");里执行了该方法
               // LECurrentObject.trigger("click");
            });
        };
        /*选择本地图片*/
        function chooseLocalImage(){
            var data=LECurrentObject.hasClass("le-gallery");
            LEDialog.toggleDialog(LE.options["Dialog"].multiPicUploadDialog,
                function(jsonUploadImg){
                    addGallerys(jsonUploadImg);
                    /*如果添加的是本地图片 点击超链接不跳转*/
                    LECurrentObject.find("a").attr("href","javascript:void(0)");
                    LECurrentObject.find("a").attr("target","_self");
                    /*如果添加的是本地图片 鼠标显示箭头*/
                    LECurrentObject.find("img").css("cursor","default");
                    LECurrentObject.find("a").css("cursor","default");
                    LECurrentObject.find(".bottom-title").find("a").addClass("formatA");
                },
            data
            );
        }
        /*追加本地图片*/
        function appendLocalImage(){
            LEDialog.toggleDialog(LE.options["Dialog"].multiPicUploadDialog,
                function(jsonUploadImg){
                    appendGallerys(jsonUploadImg);
                    /*如果追加的是本地图片 点击超链接不跳转*/
                    LECurrentObject.find("a").attr("href","javascript:void(0)");
                    LECurrentObject.find("a").attr("target","_self");
                    /*如果添加的是本地图片 鼠标显示箭头*/
                    LECurrentObject.find("img").css("cursor","default");
                    LECurrentObject.find("a").css("cursor","default");
                    LECurrentObject.find(".bottom-title").find("a").addClass("formatA");
                }
            );
        }

        /*初始化添加稿件按钮状态*/
        var resetaddMorePic = function () {
            $obj = LECurrentObject;
           // if ($.trim($obj.find("a").attr("href")) != "")
                if (!$obj.find("div.col-md-3").hasClass("g-void")){
                $addMorePic.hide();
                $("#addLocalPic").hide();
                $gUpdatePic.hide();
                $galleryEdit.show();
                if (isSameObject == false) {
                    var leng = LECurrentObject.find("div[class*='col-md-3']").length;
                    var previewstrH = "";

                    for (var i = 0; i < leng; i++) {
                        var imgPath = LECurrentObject.find("div[class*='col-md-3']").eq(i).find("img").attr("src");
                        var title = LECurrentObject.find("div[class*='col-md-3']").eq(i).find("p[class*='over-title']").children().eq(0).html();
                        var link = LECurrentObject.find("div[class*='col-md-3']").eq(i).find("a").attr("href");
                        var id = LECurrentObject.find("div[class*='col-md-3']").eq(i).attr("id");
                        //console.log(id);
                        var previewStr = '<li data-imgpath="' + imgPath + '" data-title="' + title + '" data-link="' + link + '" data-id="' + id + '" data-flag="' + i + '" class="posRelative pull-left mgr15 mgb10 border65"><img class="previewGallery" width="78" height="50"  src="' + imgPath + '" alt="" /><img class="removeGallery posAbsolute cancelBtn" data-removeId="' + id + '" src="export/images/sliderPanel/sliderPanel19.png" alt="" /></li>';
                        previewstrH += previewStr;
                    }
                    $gAllItemList.html(previewstrH);
                }
            } else {
                $addMorePic.show();
                $("#addLocalPic").show();
                $gUpdatePic.show();
                $galleryEdit.hide();
            }
            //根据是否为动态更新 修改多图编辑按钮显示状态
            var isUpdate=LECurrentObject.attr("data-update");
            if(isUpdate=="true"){
                $gChangePic.show();
                $(".gallery-editor").hide();
                $("#galleryEdit").removeClass("mgt20");
            }else{
                $gChangePic.hide();
                $(".gallery-editor").show();
                $("#galleryEdit").removeClass("mgt20");
                //$("#galleryEdit").addClass("mgt20");
            }
        };
        /*初始化列数按钮状态*/
        var resetColumn = function () {
            var _boxColum = parseInt(LECurrentObject.css("width"))-parseInt(LECurrentObject.css("border-left-width"))-parseInt(LECurrentObject.css("border-right-width"));
            var _colum = parseInt(LECurrentObject.find("div[class*='col-md-3']").css("width"));
            var i = Math.ceil(parseFloat(_boxColum / _colum).toFixed(1));
            //var i=Math.ceil(_boxColum/_colum);
            $("#columnChange").find("li").eq(i - 1).addClass("select").siblings().removeClass("select");

        };
        /*初始化间距按钮状态*/
        var resetPaddPic = function () {
            var padding = parseInt(LECurrentObject.find("div[class*='marginItem']").css("padding"));
            var i = parseInt(padding / 4);
            $("#paddPic").find("li").eq(i).addClass("select").siblings().removeClass("select");
        };
        /*初始化相框按钮状态*/
        var resetBorderPic = function () {
            var _padding = parseInt(LECurrentObject.find("div[class*='borderItem']").css("padding"));
            var i = parseInt(_padding / 2);
            $("#borderPic").find("li").eq(i).addClass("select").siblings().removeClass("select");
        };
        /*初始化标题显示框按钮*/
        var resettitleShow = function () {
            var p1 = LECurrentObject.find("p[class*='over-title']").css("display");
            var p2 = LECurrentObject.find("p[class*='bottom-title']").css("display");
            if (p1 == "none" && p2 == "none") {
                $titleShow.attr("checked", false);
            } else {
                $titleShow.attr("checked", true);
            }
        };
        /*初始化标题位置按钮*/
        var resettitlePlace = function () {
            var p1 = LECurrentObject.find("p[class*='over-title']").css("display");
            var p2 = LECurrentObject.find("p[class*='bottom-title']").css("display");
            if (p1 == "block") {
                $("#titlePic").find("li").eq(1).addClass("select").siblings().removeClass("select");
            } else if (p2 == "block") {
                $("#titlePic").find("li").eq(0).addClass("select").siblings().removeClass("select");
            }
        };

        var initAControlEvent = function () {
            $("#container").on({
                click: function () {
                    $(this).hasClass("left") && LECurrentObject.carousel('pre');
                    $(this).hasClass("right") && LECurrentObject.carousel('next');
                }
            }, ".carousel-control");
        }
        return {
            init: function () {
                initColumnChange();
                initPaddPic();
                initBorderPic();
                //initaddMorePic();
                initResetPicbyModel();
                initaddMorePicbyModel();
                inittitlePlace();
                inittitleShow();
                initTitleEdit();
                initclearAllPic();
                //initAppendMorePic();
                initAppendMorePicbyModel();
                initdragPicCss();
                initGalleryUpload();
                initAddLocalPicEvent();
                initUpdatePicbyModel();

                //initAControlEvent();
            },
            run: function (options, doHide) {
                resetColumn();
                resetPaddPic();
                resetBorderPic();
                resettitleShow();
                resettitlePlace();
                resetaddMorePic();
                LEDisplay.show($PS, doHide);
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();


            },
            destroy: function () {
                $PS.hide();
            },
            changeImageHeight: function () {
                //console.trace();
                //左右边框
                var bdLeftWidth=parseInt(LECurrentObject.css("border-top-width"));
                var bdRightWidth=parseInt(LECurrentObject.css("border-bottom-width"));
                /*图片列数i*/
                var _boxColum = parseInt(parseInt(LECurrentObject.css("width"))-bdLeftWidth-bdRightWidth);
                var _colum = parseInt(LECurrentObject.find("div[class*='col-md-3']").css("width"));
                var i = Math.ceil(parseFloat(_boxColum / _colum).toFixed(1));
                /*图片行数 对除完的数进行上舍入*/
                var imgRow = Math.ceil(parseFloat(LECurrentObject.find(".col-md-3").length / i));
                //上下边框
                var bdTopWidth=parseInt(LECurrentObject.css("border-top-width"));
                var bdBtmWidth=parseInt(LECurrentObject.css("border-bottom-width"));
                /*未去除padding与border时的图片高度*/
                var imgAllHeight = parseInt(parseInt(LECurrentObject.css("height"))-bdTopWidth-bdBtmWidth) / imgRow;
                var imgmrgin = parseInt(LECurrentObject.find("div[class*='marginItem']").css("padding")) * 2;
                var imgPadding = parseInt(LECurrentObject.find("div[class*='borderItem']").css("padding")) * 2;
                var imgBorder = parseInt(LECurrentObject.find("div[class*='borderItem']").css("border")) * 2;
                /*去除padding与border时的图片高度*/
                var imgHeight = imgAllHeight - imgmrgin - imgPadding - imgBorder;
                /*标题在下方时候 p标签的高度*/
                /*取得下方title的所有高度*/
                var leng = LECurrentObject.find(".col-md-3").length;
                var arrHeight = [];
                for (var i = 0; i < leng; i++) {
                    var height = parseInt(LECurrentObject.find("p[class*='bottom-title']").eq(i).css("height"));
                    arrHeight.push(height);
                }
                //var max = Math.max(n);
                /*取得p标签title的最大高度max*/
                var max = arrHeight[0];
                for (var i = 1; i < leng; i++) {
                    if (max < arrHeight[i]) {
                        max = arrHeight[i];
                    }
                }
                var pHeight = max || 0;
                //var pHeight=parseInt(LECurrentObject.find("p[class*='bottom-title']").css("height"))||0;
                /*去除padding与border以及p标签高度时的图片高度*/
                var imgHeightHasP = imgHeight - pHeight;
                var pBottomShow = LECurrentObject.find("p[class*='bottom-title']").css("display");
                /*if (pBottomShow == "block") {
                    LECurrentObject.find("img").css("height", imgHeightHasP + "px")
                } else {
                    LECurrentObject.find("img").css("height", imgHeight + "px")
                }
                LECurrentObject.find("div.col-md-3").css("height", imgAllHeight);*/
                var id = LECurrentObject.attr("id");
                if(imgHeightHasP<0){
                    imgHeightHasP=0;
                }
                if(imgHeight<0){
                    imgHeight=0;
                }
                if (pBottomShow == "block") {
                    var selector = $.trim("#" + id + " " + "div.col-md-3 img");
                    StyleManager.setStyle(selector, "height", imgHeightHasP+"px");
                } else {
                    var selector = $.trim("#" + id + " " + "div.col-md-3 img");
                    StyleManager.setStyle(selector, "height", imgHeight+"px");
                }
                var selector = $.trim("#" + id + " " + "div.col-md-3");
                StyleManager.setStyle(selector, "height", imgAllHeight+"px");
                //console.log("列数"+i+"行数"+imgRow+"imgAllHeight"+imgAllHeight+"imgmrgin"+imgmrgin+"imgPadding"+"imgBorder"+imgBorder+"imgHeight"+imgHeight);
            }
        }
    };
})(window, jQuery, LE, undefined);

var jsonArray = [
    {
        id: 'le_Gallery_li_01',//id
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/4c562123-b3ea-4e84-a6e2-2a162d69977b.jpg',
        title: '主题：罗胖子最牛逼', //h2
        summary: '简介：罗胖子最牛逼',//p
        link: 'http://www.baidu.com'//a
    },
    {
        id: 'le_Gallery_li_02',
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/4c562123-b3ea-4e84-a6e2-2a162d69977b.jpg',
        title: '主题：小胖最牛逼',
        summary: '简介：小胖最牛逼',
        link: 'http://www.baidu.com'
    },
    {
        id: 'le_Gallery_li_03',
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/46e966b5-9bee-4034-9c0a-811e6d718513.jpg',
        title: '主题：小李最牛逼',
        summary: '简介：小李最牛逼',
        link: 'http://www.baidu.com'
    },
    {
        id: 'le_Gallery_li_04',
        imgPath: 'http://localhost:8080/xy/xy/image.do?path=%E5%9B%BE%E7%89%87%E5%AD%98%E5%82%A8;xy/201605/09/46e966b5-9bee-4034-9c0a-811e6d718513.jpg',
        title: '主题：小刘最牛逼',
        summary: '简介：小刘最牛逼',
        link: 'http://www.baidu.com'
    },
    {
        id: 'le_Gallery_li_05',//id
        imgPath: 'http://a.page.9466.com/169705/66d6ef719597d15946d91e354df6d3f7.jpg',//src
        title: '主题：罗胖子最牛逼', //h2
        summary: '简介：罗胖子最牛逼',//p
        link: 'http://www.baidu.com'//a
    },
    {
        id: 'le_Gallery_li_06',
        imgPath: 'http://a.page.9466.com/169705/cfd589543527127ced39e2b8d9ecb9cf.png',
        title: '主题：小胖最牛逼',
        summary: '简介：小胖最牛逼',
        link: 'http://www.baidu.com'
    }
];
var jsonArray2 = [
    {
        id: 'le_Gallery_li_07',//id
        imgPath: 'http://a.page.9466.com/169705/66d6ef719597d15946d91e354df6d3f7.jpg',//src
        title: '主题：我是追加的图片1', //h2
        summary: '简介：罗胖子最牛逼',//p
        link: 'http://www.baidu.com'//a
    },
    {
        id: 'le_Gallery_li_08',
        imgPath: 'http://a.page.9466.com/169705/cfd589543527127ced39e2b8d9ecb9cf.png',
        title: '主题：我是追加的图片2',
        summary: '简介：小胖最牛逼',
        link: 'http://www.baidu.com'
    },
    {
        id: 'le_Gallery_li_09',
        imgPath: 'http://a.page.9466.com/169705/2c2216a6508905a34fe5e71cb48b2605.jpg',
        title: '主题：我是追加的图片3',
        summary: '简介：小李最牛逼',
        link: 'http://www.baidu.com'
    },
    {
        id: 'le_Gallery_li_10',
        imgPath: 'http://a.page.9466.com/169705/97a9c3c2ee776fa13bf1966bbbbe5f1e.jpg',
        title: '主题：我是追加的图片4',
        summary: '简介：小刘最牛逼',
        link: 'http://www.baidu.com'
    }
];

//切换组件时，样式设置部分回顶部 公用
function sliderbarToTop() {
    if (!isSameObject) {
        /*jQuery(".ps-scrollbar-y").css("top", "0px");
        jQuery("#sidebar-panel,#sidebar-panel-II,#sidebar-panel-III").scrollTop(0);*/
        jQuery("#sidebar-panel,#sidebar-panel-II,#sidebar-panel-III").scrollTop(0);
        jQuery("#sidebar-panel,#sidebar-panel-II,#sidebar-panel-III").perfectScrollbar("update");
    }
}
function showImgAgain(imgObj,imgSrc,maxErrorNum,isFirst){
    if(isFirst==true){
        $(imgObj).after('<img src="./export/img/noimg.gif" width="100%">');
    }else if(isFirst=="preview"){
        $(imgObj).after('<img src="./export/img/noimg.gif" width="78" height="50">');
    }

    $(imgObj).hide();
    if(maxErrorNum>0){
        setTimeout(function(){
            imgObj.src=imgSrc;
            imgObj.onerror=function(){
            	showImgAgain(imgObj,imgSrc,maxErrorNum-1);
        	};
        	imgObj.onload=function(){
            	imgObj.onerror=null;
            	$(imgObj).show();
        		$(imgObj).siblings("img").remove();
        	};
        },500);
    }else{
        imgObj.onerror=null;
        imgObj.src="./export/img/noimg.jpg";
        $(imgObj).show();
        $(imgObj).siblings("img").remove();
    }
}