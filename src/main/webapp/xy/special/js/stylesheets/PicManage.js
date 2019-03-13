/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["PicManage"] = function () {
        var $panel = $("#galleryDiv");
        var $addItemBtn = $("#gAddItems");
        var $gItemList = $("#gItemList");
        var $carModelDiv = $("#carModelDiv");
        var $carModelGoBack = $("#carModelGoBack");
        var $carModelPreview = $("#carModelPreview");
        var $updatePic=$("#updatePic");
        var $changePic=$("#changePic");

        var initSetItembyModel = function () {
            /**
             * 轮播图内容编辑
             */
            $("#carousSet").click(function () {
                //var data = [1, 2, 3];
                //将轮播图现有数据生成json，可由dialog接收
                var data = {};
                var list;
                var len = LECurrentObject.find("div.carousel-inner").children().length;
                for (var i = 0; i < len; i++) {
                    list = {
                        'liId': LECurrentObject.find("ol.carousel-indicators").children().eq(i).attr("id"),
                        'imgId': LECurrentObject.find("div.carousel-inner").children().eq(i).attr("id"),
                        'imgPath': LECurrentObject.find("div.carousel-inner").children().eq(i).find("img").attr("src"),
                        'title': LECurrentObject.find("div.carousel-inner").children().eq(i).find("div.carousel-caption").find("h4").html(),
                        'link': LECurrentObject.find("div.carousel-inner").children().eq(i).find("a").attr("href"),
                        'type':LECurrentObject.attr("data-type")
                    };
                    data[i] = list;
                }

                var timeStamp = new Date().getTime();
                LEDialog.toggleDialog(LE.options["Dialog"].detailSettingDialog + "timestamp=" + timeStamp, function (jsonEdited) {

                        //如果数据为空 前端显示默认页面
                        if (!jsonEdited[0]) {
                            cleanAllPic();
                            return;
                        }

                        //接收后台传递的数据并展示到前台页面
                        addMoreItems(jsonEdited);

                        if(jsonEdited[0].type=="local"){
                            /*如果添加的是本地图片 点击超链接不跳转*/
                            LECurrentObject.find("div.carousel-inner").find("a").attr("href","javascript:void(0)");
                            LECurrentObject.find("div.carousel-inner").find("a").attr("target","_self");
                            /*如果添加的是本地图片 鼠标显示箭头*/
                            LECurrentObject.find("img").css("cursor","default");
                            LECurrentObject.find("div.carousel-caption").css("cursor","default");
                        }

                        //将id编辑之前的id重新写回来
                        var leng = jsonEdited.length;
                        for (var i = 0; i < leng; i++) {
                            LECurrentObject.find("ol.carousel-indicators").children().eq(i).attr("id", jsonEdited[i].liId);
                            LECurrentObject.find("div.carousel-inner").children().eq(i).attr("id", jsonEdited[i].imgId);
                            $gItemList.children().eq(i).attr("data-liid", jsonEdited[i].liId);
                            $gItemList.children().eq(i).attr("data-imgid", jsonEdited[i].imgId);
                            $gItemList.children().eq(i).find(".removeGallery").attr("data-removeliid", jsonEdited[i].liId);
                            $gItemList.children().eq(i).find(".removeGallery").attr("data-removeid", jsonEdited[i].imgId);
                        }
                        LEHistory.trigger();
                    },
                    data
                );
                // LEHistory.trigger();
            });
        };

        //轮播图全部清空后 还原到拖拽进来时的状态
        function cleanAllPic(){
            var _target=LECurrentObject;
            var id=_target.attr("id");
            id = "#" + id;
            var str=LE.options["Carousel"].viewinnerHtml.replace(/#\{target\}/g, "#" + _target.attr("id"));
            _target.html(str);

            $(id).on("slide.bs.carousel", function () {
                var len=$(id).find(".carousel-inner").find("div.item").length;
                var activeLiN=$(id).find(".carousel-inner").find("div.next").index();
                var activeLiP=$(id).find(".carousel-inner").find("div.prev").index();
                var activeLiA=$(id).find(".carousel-inner").find("div.active:not(.next)").eq(0).index();
                var numN=parseInt(activeLiN);
                var numP=parseInt(activeLiP);
                var numA=(parseInt(activeLiA)+1)%len;
                //console.info(numN+"numN"+numP+"numa"+numA+activeLiA+len);
                if(numN !=-1){
                    $(id).find("ol").filter(".carousel-indicators").find("li").eq(numN).addClass("active").siblings().removeClass("active");

                }else if(numP !=-1){
                    $(id).find("ol").filter(".carousel-indicators").find("li").eq(numP).addClass("active").siblings().removeClass("active");

                }else if(numP ==-1 && numN ==-1){
                    $(id).find("ol").filter(".carousel-indicators").find("li").eq(numA).addClass("active").siblings().removeClass("active");
                }
                });

            LE.stylesheets["PicManage"]().changeItemHeight();
           // $(id).carousel('cycle');
            $(id).trigger("click");

        }

        var initaddMoreItembyModel = function () {
            /**
             * 轮播图添加图文 写入html
             */
            $addItemBtn.click(function () {
                var data = [];
                LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function (json) {
                        addMoreItems(json);
                        LEHistory.trigger();
                    },
                    data
                );
                LECurrentObject.attr("data-type","sql");
                // LEHistory.trigger();
            });
        };
        var initResetItembyModel = function () {
            /**
             * 轮播图重置图文 重新写入html内容
             */
            $("#carousReset").click(function () {
                LECurrentObject.attr("data-reset","true");
                if(LECurrentObject.attr("data-type")=="sql") {
                    var data = [];
                    LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function (json) {
                            addMoreItems(json);
                            LEHistory.trigger();
                        },
                        data
                    );
                    LECurrentObject.attr("data-type","sql");
                }else{
                    chooseLocalItem();
                    LECurrentObject.attr("data-type","local");
                }
                // LEHistory.trigger();
            });
        };


        function addMoreItems(jsonArray) {
            if (jsonArray.length == 0) return;

            $("#carouselEdit").show();
            $addItemBtn.hide();
            $("#gAddLocalPic").hide();
            $updatePic.hide();
            addItemsToCarousel(jsonArray);
        }


        var initAddItemBtnEvent = function () {
            $addItemBtn.click(function (e) {
                $("#carouselEdit").show();
                $(this).hide();
                addItemsToCarousel(jsonArray);
                LE.stylesheets["PicManage"]().changeItemHeight();
            });
        };
        var addItemsToCarousel = function (itemList) {
            if (!itemList || itemList.length < 1) {
                return;
            }
            /*handleLiHtml(itemList);
             handleImgHtml(itemList);*/

            handleSlideItems(itemList);

            //LECurrentObject.carousel('cycle');
        };

        function handleSlideItems(itemList) {
            //标题
            var $target = LECurrentObject;
            var $ol = $target.find("ol").filter(".carousel-indicators");
            var _li = LE.options["Carousel"].liHtml;
            _li = _li.replace(/#\{target\}/g, "#" + $target.attr("id"));
            var _olHtml = [];
            //图片
            var $div = LECurrentObject.find(".carousel-inner");
            var _div = LE.options["Carousel"].divHtml;
            var _divHtml = [];
            //预览图片
            var $ul = $("#gItemList");
            var _item = '<li data-imgId="#{id}" data-liId="#{liId}" data-imgPath="#{src}" data-title="#{title}" data-link="#{link}" data-summary="#{summary}" class="posRelative pull-left mgr15 mgb10 border65">'
                /*+ '<div style="width: 78px; height: 52px;overflow: hidden;">'*/
                + '<img class="previewGallery" width="78" height="50" onerror="#{error}" src="#{src}" alt="">'
                /*+ '</div>'*/
                + '<img class="posAbsolute cancelBtn removeGallery" data-removeId="#{id}" data-removeLiId="#{liId}" src="export/images/sliderPanel/sliderPanel19.png" alt="">'

                + '</li>';
            var _ulHtml = [];

            for (var i = 0, it = null; it = itemList[i++];) {
                var id_Carousel = 'le_Carousel_' + LEDrag.getNextId() + '_' + it.id;
                _olHtml.push(_li.replace(/#\{index\}/g, i - 1).replace(/#\{id\}/g, "li_" + id_Carousel).replace(/#\{num\}/g, i));
                _divHtml.push(_div.replace(/#\{src\}/g, it.imgPath).replace(/#\{title\}/g, it.title).replace(/#\{summary\}/g, it.summary).replace(/#\{id\}/g, "img_" + id_Carousel).replace(/#\{link\}/g, it.link).replace(/#\{error\}/g, "showImgAgain(this,\'"+it.imgPath+"\',10,true);"));
                _ulHtml.push(_item.replace(/#\{id\}/g, "img_" + id_Carousel).replace(/#\{liId\}/g, "li_" + id_Carousel).replace(/#\{src\}/g, it.imgPath).replace(/#\{title\}/g, it.title).replace(/#\{summary\}/g, it.summary).replace(/#\{link\}/g, it.link).replace(/#\{error\}/g, "showImgAgain(this,\'"+it.imgPath+"\',10,\'preview\');"));
            }
            //记录轮播图文字样式
            if(LECurrentObject.attr("data-reset") == "true"){
                //字体样式
                var _record = LECurrentObject.find(".carousel-caption").children("h4").attr("style")
            }

            $ol.html(_olHtml.join(""));
            $ol.find("li:first").addClass("active");

            $div.html(_divHtml.join(""));
            $div.find("div:first").addClass("active");

            $ul.html(_ulHtml.join(""));
            LE.stylesheets["PicManage"]().changeItemHeight();
            //写回轮播图文字样式
            if(LECurrentObject.attr("data-reset") == "true"){
                //字体样式
                LECurrentObject.find(".carousel-caption").children("h4").attr("style",_record);
            }
            LECurrentObject.trigger("click");

        }

        /* function handleSlideItems(itemList){
         var $ul = $("#gItemList");
         var _item = '<li data-imgId="#{id}" data-liId="#{liId}" data-imgPath="#{src}" data-title="#{title}" data-link="#{link}" data-summary="#{summary}" class="posRelative pull-left mgr15 mgb10 border65">'
         */
        /*+ '<div style="width: 78px; height: 52px;overflow: hidden;">'*/
        /*
         + '<img class="previewGallery" width="78" height="50" src="#{src}" alt="">'
         */
        /*+ '</div>'*/
        /*
         + '<img class="posAbsolute cancelBtn removeGallery" data-removeId="#{id}" data-removeLiId="#{liId}" src="export/images/sliderPanel/sliderPanel19.png" alt="">'

         + '</li>';
         var _html = [];
         for(var i = 0, it = null; it = itemList[i++];){
         var id_Carousel='le_Carousel_' + LEDrag.getNextId()+'_'+it.id;
         _html.push(_item.replace(/#\{id\}/g, "img_" + id_Carousel).replace(/#\{liId\}/g, "li_" + id_Carousel).replace(/#\{src\}/g, it.imgPath).replace(/#\{title\}/g, it.title).replace(/#\{summary\}/g, it.summary).replace(/#\{link\}/g, it.link));
         }
         $ul.html(_html.join(""));
         }

         function handleImgHtml(itemList){
         var $div = LECurrentObject.find(".carousel-inner");
         var _div = LE.options["Carousel"].divHtml;
         var _html = [];
         for(var i = 0, it = null; it = itemList[i++];){
         _html.push(_div.replace(/#\{src\}/g, it.imgPath).replace(/#\{title\}/g, it.title).replace(/#\{summary\}/g, it.summary).replace(/#\{id\}/g, "img_" + it.id).replace(/#\{link\}/g,it.link));
         }
         $div.html(_html.join(""));
         $div.find("div:first").addClass("active");
         }

         function handleLiHtml(itemList){
         var $target = LECurrentObject;
         var $ol = $target.find("ol").filter(".carousel-indicators");
         var _li = LE.options["Carousel"].liHtml;
         _li = _li.replace(/#\{target\}/g, "#" + $target.attr("id"));
         var _html = [];
         for(var i = 0, it = null; it = itemList[i++];){
         _html.push(_li.replace(/#\{index\}/g, i - 1).replace(/#\{id\}/g, "li_" + it.id).replace(/#\{num\}/g, i));
         }
         $ol.html(_html.join(""));
         $ol.find("li:first").addClass("active");
         }*/

        /*
         删除稿件
         */
        var initclearAllItem = function () {
            /*缩略图hover事件 删除按钮的显示隐藏*/
            $panel.on({
                'mouseover': function () {
                    $(this).next().show();
                }, 'mouseout': function () {
                    $(this).next().hide();
                }
            }, '.previewGallery');
            $panel.on({
                'mouseover': function () {
                    $(this).show();
                }, 'mouseout': function () {
                    $(this).hide();
                }
            }, '.removeGallery');
            /*全部清空*/
            $("#gClear").bind("click", function () {
                //var divHeight=LECurrentObject.css("height");
                //var imgHeight=LECurrentObject.find("div.carousel-inner .item img").css("height");
                // LECurrentObject.css("height", divHeight);
                //LECurrentObject.find("div.carousel-inner .item img").css("height", imgHeight);
                cleanAllPic();
                LEHistory.trigger();

            });
            /*部分删除*/
            $panel.on({
                'click': function () {
                    LECurrentObject.find(".carousel-inner").find("div.item").removeClass("active");
                    LECurrentObject.find(".carousel-indicators").find("li").removeClass("active");
                    var removeId = $(this).attr("data-removeId");
                    var removeLiId = $(this).attr("data-removeLiId");
                    $(this).parent().remove();
                    LECurrentObject.find("#" + removeId).remove();
                    LECurrentObject.find("#" + removeLiId).remove();
                    //$("#"+removeId).remove();
                    //$("#"+removeLiId).remove();
                    LECurrentObject.find(".carousel-inner").find("div:first").addClass("active");
                    LECurrentObject.find(".carousel-indicators").find("li:first").addClass("active");

                    var removeLen = LECurrentObject.find("div.carousel-inner").children().length;
                    for (var i = 0; i < removeLen; i++) {
                        LECurrentObject.find(".carousel-indicators").children("li").eq(i).find("span").html(i + 1);
                    }
                    if (LECurrentObject.find("div.carousel-inner").html() == "") {
                        cleanAllPic();
                    }
                    LEHistory.trigger();
                }
            }, '.removeGallery');
        };
        var initdragItem = function () {
            var dragstartStyle = null;
            $gItemList.sortable({
                items: "li",
                placeholder: "gallery-state-highlight",
                start: function (event) {
                    //获取自定义设置的样式
                    var _record=LECurrentObject.find(".carousel-inner").find(".carousel-caption").children("h4").attr("style");
                    dragstartStyle=_record;
                    /*var leng = LECurrentObject.find("div.carousel-inner").children().length;
                     for(var i=0;i<leng;i++){
                     var startHtitle=LECurrentObject.find("div[class*='col-md-3']").eq(i).find("p[class*='over-title']").children().eq(0).html();
                     $("#gAllItemList").find("li").not(":empty").eq(i).attr("data-title",startHtitle);
                     }*/
                },
                stop: function (event) {
                    var targetId = LECurrentObject.attr("id");
                    var len = $(this).find("li").length;
                    var imgStrAll = "";
                    var liStrAll = "";
                    for (var i = 0; i < len; i++) {
                        var sortImgId = $(this).children().eq(i).attr("data-imgId");
                        var sortLiId = $(this).children().eq(i).attr("data-liId");
                        var sortImgpath = $(this).children().eq(i).attr("data-imgPath");
                        var sortTitle = $(this).children().eq(i).attr("data-title");
                        var sortSummary = $(this).children().eq(i).attr("data-summary");
                        var sortLink = $(this).children().eq(i).attr("data-link");
                        var imgStr = ['<div class="item" id="', sortImgId,
                            '"><a href="', sortLink, '" target="_blank"><img alt="" onerror="showImgAgain(this,\''+sortImgpath+'\',10,true);" width="100%" src="', sortImgpath,
                            '" data-holder-rendered="false">',
                            '<div class="carousel-caption">',
                            '<h4>', sortTitle, '</h4>',
                            '<p>', sortSummary, '</p>',
                            '</div>',
                            '</a>',
                            '</div>'].join("");
                        var liStr = '<li data-target="#' + targetId + '" id="' + sortLiId + '" data-slide-to=' + i + ' class=""><span>' + parseInt(i + 1) + '</span></li>';
                        imgStrAll += imgStr;
                        liStrAll += liStr;
                    }
                    LECurrentObject.find(".carousel-inner").html(imgStrAll);
                    LECurrentObject.find(".carousel-indicators").html(liStrAll);
                    LECurrentObject.find(".carousel-inner").find("div:first").addClass("active");
                    LECurrentObject.find(".carousel-indicators").find("li:first").addClass("active");

                    //自定义样式赋给追加后的内容
                    LECurrentObject.find(".carousel-inner").find(".carousel-caption").children("h4").attr("style",dragstartStyle);

                    LE.stylesheets["PicManage"]().changeItemHeight();
                    LECurrentObject.trigger("click");
                }
            });
        };
        /*点击进行图片修改*/
        var initCarouselUpload = function () {
            $panel.on({
                'click': function () {
                    var _$this = $(this);
                    var data = [];
                    //var thisID = _$this.parent().attr("data-imgId");
                    var _$imgs = _$this.parent().parent().find(".previewGallery");
                    var _index = null;
                    _$this.addClass("gp_chosen");
                    _$imgs.each(function (index) {
                        if ($(this).hasClass("gp_chosen")) {
                            _index = index;
                        }
                        data.push($(this).attr("src"));
                    });
                    _$this.removeClass("gp_chosen");
                    var imgWidth = parseInt(LECurrentObject.css("width"));
                    var imgHeight = parseInt(LECurrentObject.find("img").css("height"));
                    var ratio = imgWidth / imgHeight;
                    var timeStamp = new Date().getTime();
                    LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog + "ratio=" + ratio + "&index=" + _index + "&timestamp=" + timeStamp, function (imgList) {
                            if (imgList.length > 0) {
                                for (var i = 0, len = imgList.length; i < len; i++) {
                                    var thisID = _$this.parent().parent().find("li").eq(i).attr("data-imgId");
                                    _$this.parent().parent().find("li").eq(i).find(".previewGallery").attr("src", imgList[i].path);
                                    _$this.parent().parent().find("li").eq(i).attr("data-imgpath", imgList[i].path);
                                    $("#" + thisID).find("img").attr("src", imgList[i].path);
                                    /*_$this.attr("src", imgList[i].path);
                                     _$this.parent().attr("data-imgpath", imgList[i].path);
                                     $("#" + thisID).find("img").attr("src", imgList[i].path);*/
                                }
                            }
                            LEHistory.trigger();
                        },
                        data
                    );
                    //LEHistory.trigger();

                }
            }, '.previewGallery');
        };

        var initAppendItembyModel = function () {
            /**
             * 轮播图追加图文 append添加html内容
             */
            $("#carousAdd").click(function () {
                if(LECurrentObject.attr("data-type")=="sql") {
                var data = [];
                LEDialog.toggleDialog(LE.options["Dialog"].thumbListDialog, function (json) {
                        appendMoreItems(json);
                    },
                    data
                );
                }else{
                    appendLocalItem();
                }
                // LEHistory.trigger();
            });
        };

        function appendMoreItems(jsonArray2) {
            if (jsonArray2.length == 0) return;
            var targetId = LECurrentObject.attr("id");
            var len = jsonArray2.length;
            var oldLeng = LECurrentObject.find("div.carousel-inner").children().length;
            var imgStrAll = "";
            var liStrAll = "";
            var previewstrAll = "";
            for (var i = 0; i < len; i++) {
                var id_Carousel_Append = 'le_Carousel_' + LEDrag.getNextId() + '_' + jsonArray2[i].id;
                var imgStr = ['<div class="item" id=img_', id_Carousel_Append,
                    '><a href="', jsonArray2[i].link, '" target="_blank"><img alt="" width="100%" onerror="showImgAgain(this,\''+jsonArray2[i].imgPath+'\',10,true);" src="', jsonArray2[i].imgPath,
                    '" data-holder-rendered="false">',
                    '<div class="carousel-caption">',
                    '<h4>', jsonArray2[i].title, '</h4>',
                    '<p>', jsonArray2[i].summary, '</p>',
                    '</div>',
                    '</a>',
                    '</div>'].join("");
                var liStr = '<li data-target="#' + targetId + '" id=li_' + id_Carousel_Append + ' data-slide-to=' + parseInt(oldLeng + i) + ' class=""><span>' + parseInt(oldLeng + i + 1) + '</span></li>';
                var previewStr = ['<li data-imgId=img_', id_Carousel_Append, ' data-liId=li_', id_Carousel_Append, ' data-imgPath="', jsonArray2[i].imgPath, '" data-link="', jsonArray2[i].link, '" data-title="', jsonArray2[i].title, '" data-sumary="', jsonArray2[i].summary,
                    '" class="posRelative pull-left mgr15 mgb10 border65">',
                    '<img class="previewGallery" width="78" height="50" onerror="showImgAgain(this,\''+jsonArray2[i].imgPath+'\',10,\'preview\');" src="',
                    jsonArray2[i].imgPath,
                    '" alt="">',
                    '<img class="posAbsolute cancelBtn removeGallery" data-removeId=img_',
                    id_Carousel_Append,
                    ' data-removeLiId=li_',
                    id_Carousel_Append,
                    ' src="export/images/sliderPanel/sliderPanel19.png" alt="">',
                    '</li>'].join("");
                imgStrAll += imgStr;
                liStrAll += liStr;
                previewstrAll += previewStr;
            }

            //获取自定义设置的样式
            var _record=LECurrentObject.find(".carousel-inner").find(".carousel-caption").children("h4").attr("style");

            LECurrentObject.find(".carousel-inner").append(imgStrAll);
            LECurrentObject.find(".carousel-indicators").append(liStrAll);
            $gItemList.append(previewstrAll);

            //自定义样式赋给追加后的内容
            LECurrentObject.find(".carousel-inner").find(".carousel-caption").children("h4").attr("style",_record);

            //LECurrentObject.find(".carousel-inner").find("div:first").addClass("active");
            // LECurrentObject.find(".carousel-indicators").find("li:first").addClass("active");
            LE.stylesheets["PicManage"]().changeItemHeight();
            LEHistory.trigger();
        }

        /*
         点击追加稿件
         */
        var initAppendMoreItem = function () {
            $("#carousAdd").bind("click", function () {
                var targetId = LECurrentObject.attr("id");
                var len = jsonArray2.length;
                var oldLeng = LECurrentObject.find("div.carousel-inner").children().length;
                var imgStrAll = "";
                var liStrAll = "";
                var previewstrAll = "";
                for (var i = 0; i < len; i++) {
                    var imgStr = ['<div class="item" id=img_', jsonArray2[i].id,
                        '><img alt="" width="100%" src="', jsonArray2[i].imgPath,
                        '" data-holder-rendered="false">',
                        '<div class="carousel-caption">',
                        '<h4>', jsonArray2[i].title, '</h4>',
                        '<p>', jsonArray2[i].summary, '</p>',
                        '</div>',
                        '</div>'].join("");
                    var liStr = '<li data-target=#' + targetId + ' id=li_' + jsonArray2[i].id + ' data-slide-to=' + parseInt(oldLeng + i) + ' class=""></li>';
                    var previewStr = ['<li data-imgId=img_', jsonArray2[i].id, ' data-liId=li_', jsonArray2[i].id, ' data-imgPath=', jsonArray2[i].imgPath, ' data-title=', jsonArray2[i].title, ' data-sumary=', jsonArray2[i].summary,
                        ' class="posRelative pull-left mgr15 mgb10 border65">',
                        '<img class="previewGallery" width="78" height="50" src=',
                        jsonArray2[i].imgPath,
                        ' alt="">',
                        '<img class="posAbsolute cancelBtn removeGallery" data-removeId=img_',
                        jsonArray2[i].id,
                        ' data-removeLiId=li_',
                        jsonArray2[i].id,
                        ' src="export/images/sliderPanel/sliderPanel19.png" alt="">',
                        '</li>'].join("");
                    imgStrAll += imgStr;
                    liStrAll += liStr;
                    previewstrAll += previewStr;
                }
                LECurrentObject.find(".carousel-inner").append(imgStrAll);
                LECurrentObject.find(".carousel-indicators").append(liStrAll);
                $gItemList.append(previewstrAll);
                //LECurrentObject.find(".carousel-inner").find("div:first").addClass("active");
                // LECurrentObject.find(".carousel-indicators").find("li:first").addClass("active");
                LE.stylesheets["PicManage"]().changeItemHeight();
            })
        };

        var initUpdatePic = function () {
            /**
             * 轮播图的动态更新
             */
            $updatePic.click(function () {
                var data = [];
                LEDialog.toggleDialog(LE.options["Dialog"].updaListDialog, function(updateSet){
                        var countStart=updateSet.lastIndexOf(":")+1;
                        var countEnd=updateSet.lastIndexOf("}");
                        var count=updateSet.substring(countStart,countEnd);
                        var jsonList=[];
                        var list={
                            PublishTime: "2017-01-20 18:09:42",
                            id:"example01",
                            imgPath: "export/images/navMenu/navMenu15.png",
                            //imgPath: "http://172.19.33.95/pic/201608/05/t2_(9X57X600X389)a432d0f1-3702-4239-ad8f-7405a534fb24.jpg",
                            link: "http://",
                            src: "轮播图动态更新的标题",
                            summary: "轮播图动态更新的标题",
                            title: "轮播图动态更新的标题"
                        };
                        for(var i=0;i<count;i++){
                            jsonList.push(list)
                        }
                        // console.log(countStart+"---"+countEnd+"---"+count);
                        //为设置了动态更新的列表添加标记
                        LECurrentObject.attr("data-update","true");
                        addMoreItems(jsonList);
                        //添加保存时替换的标记
                        var _id=LECurrentObject.attr("id");
                        LECurrentObject.find(".carousel-indicators").prepend("<!-----carouselOlUpdate"+_id+"----->");
                        LECurrentObject.find(".carousel-indicators").append("<!-----carouselOlUpdate"+_id+"----->");
                        LECurrentObject.find(".carousel-inner").prepend("<!-----carouselDivUpdate"+_id+"----->");
                        LECurrentObject.find(".carousel-inner").append("<!-----carouselDivUpdate"+_id+"----->");
                        LECurrentObject.attr("data-updateset",updateSet);
                        LEHistory.trigger();
                    },
                    data
                );
            });
            $changePic.click(function () {
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
                            PublishTime: "2017-01-20 18:09:42",
                            id:"example01",
                            imgPath: "export/images/navMenu/navMenu15.png",
                            //imgPath: "http://172.19.33.95/pic/201608/05/t2_(9X57X600X389)a432d0f1-3702-4239-ad8f-7405a534fb24.jpg",
                            link: "http://",
                            src: "轮播图动态更新的标题",
                            summary: "轮播图动态更新的标题",
                            title: "轮播图动态更新的标题"
                        };
                        for(var i=0;i<count;i++){
                            jsonList.push(list)
                        }
                        // console.log(countStart+"---"+countEnd+"---"+count);
                        //为设置了动态更新的列表添加标记
                        LECurrentObject.attr("data-update","true");
                        addMoreItems(jsonList);
                        //添加保存时替换的标记
                        var _id=LECurrentObject.attr("id");
                        LECurrentObject.find(".carousel-indicators").prepend("<!-----carouselOlUpdate"+_id+"----->");
                        LECurrentObject.find(".carousel-indicators").append("<!-----carouselOlUpdate"+_id+"----->");
                        LECurrentObject.find(".carousel-inner").prepend("<!-----carouselDivUpdate"+_id+"----->");
                        LECurrentObject.find(".carousel-inner").append("<!-----carouselDivUpdate"+_id+"----->");
                        LECurrentObject.attr("data-updateset",updateSet);
                        LEHistory.trigger();
                    },
                    data
                );
            });
        };
        /*轮播图更多样式面板出现消失*/
        var initCarModelShow = function () {
            /*$("#carModelMore")*/
            $carModelPreview.bind("click", function () {
                $carModelGoBack.animate({right: "0"}, "fast", "linear");
                $carModelDiv.animate({right: "0"}, "fast", "linear");
            });
            $carModelGoBack.bind("click", function () {
                $carModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $carModelDiv.animate({right: "-250px"}, "fast", "linear");
            });
        };

        /*
         点击标签样式按钮
         为目标对象添加class 每次添加记住class 下次点击先移除 再添加
         */
        var initChangeCarStyle = function () {
            $carModelDiv.find(".displayStyle").click(function (e) {
                /*
                 点击轮播图模板按钮的同时，更新样式预览处的html。
                 */
                $carModelPreview.html($(this).html());
                var _$this = $(this);
                var $target = LECurrentObject;
                var _style = _$this.data("ref");
                $target.removeClass($target.attr("data-prestyle"));
                $target.attr("data-prestyle", _style);
                $target.addClass(_style);
                //LECurrentObject.trigger("click");
                /*if(_$this.index()=="4"){
                 var leng= $target.find("ol").children().length;
                 for(var i=0 ;i<leng;i++){
                 $target.find("ol").children().eq(i).html(i+1)
                 }
                 }else{
                 $target.find("ol").children().html("");
                 }*/
                LEHistory.trigger();
            });
        };

        /*添加本地图片*/
        var  initAddLocalItemEvent=function(){
            $("#gAddLocalPic").click(function(){
                chooseLocalItem();
                LECurrentObject.attr("data-type","local");
                // LEHistory.trigger();
                // LECurrentObject.trigger("click");
            });
        };
        /*选择本地图片*/
        function chooseLocalItem(){
            LEDialog.toggleDialog(LE.options["Dialog"].multiPicUploadDialog,
                function(jsonUploadImg){
                    addMoreItems(jsonUploadImg);
                    /*如果添加的是本地图片 点击超链接不跳转*/
                    LECurrentObject.find("div.carousel-inner").find("a").attr("href","javascript:void(0)");
                    LECurrentObject.find("div.carousel-inner").find("a").attr("target","_self");
                    /*如果添加的是本地图片 鼠标显示箭头*/
                    LECurrentObject.find("img").css("cursor","default");
                    LECurrentObject.find("div.carousel-caption").css("cursor","default");
                    LEHistory.trigger();
                }
            );
        }

        /*追加本地图片*/
        function appendLocalItem(){
            LEDialog.toggleDialog(LE.options["Dialog"].multiPicUploadDialog,
                function(jsonUploadImg){
                    appendMoreItems(jsonUploadImg);
                    /*如果追加的是本地图片 点击超链接不跳转*/
                    LECurrentObject.find("div.carousel-inner").find("a").attr("href","javascript:void(0)");
                    LECurrentObject.find("div.carousel-inner").find("a").attr("target","_self");
                    LECurrentObject.find("img").css("cursor","default");
                    LECurrentObject.find("div.carousel-caption").css("cursor","default");
                }
            );
        }

        /*
         轮播图样式预览 实现方法  如果设置了样式 让预览处的html为按钮的html 否则为第1个标签样式（默认样式）
         */
        function resetModelCarPreview() {
            var $target = LECurrentObject;
            var l = $carModelDiv.find("ul").length;
            var tabClassName = $target.attr("data-prestyle");
            if (!tabClassName) {
                $carModelPreview.html($carModelDiv.children("ul").eq(0).html());
            } else {
                for (var i = 0; i < l; i++) {
                    var className = $carModelDiv.find("ul").eq(i).attr("data-ref");
                    if (tabClassName == className) {
                        $carModelPreview.html($carModelDiv.children("ul").eq(i).html());
                    }
                }

            }
        }

        /*初始化添加稿件按钮状态*/
        var resetaddMoreItem = function () {
            $obj = LECurrentObject;
            //if($.trim($obj.find("a").attr("href")) != ""){
            if ($obj.find("img").attr("data-holder-rendered") == "false") {
                $addItemBtn.hide();
                $("#gAddLocalPic").hide();
                $("#carouselEdit").show();
                $updatePic.hide();
                if (isSameObject == false) {
                    var leng = LECurrentObject.find("div.carousel-inner").children().length;
                    var previewstrH = "";
                    for (var i = 0; i < leng; i++) {
                        var imgPath = LECurrentObject.find("div.carousel-inner").children().eq(i).find("img").attr("src");
                        var title = LECurrentObject.find("div.carousel-inner").children().eq(i).find("h4").html();
                        var summary = LECurrentObject.find("div.carousel-inner").children().eq(i).find("p").html();
                        var imgId = LECurrentObject.find("div.carousel-inner").children().eq(i).attr("id");
                        var liId = LECurrentObject.find("ol.carousel-indicators").children().eq(i).attr("id");
                        var link = LECurrentObject.find("div.carousel-inner").children().eq(i).find("a").attr("href");
                        var previewStr = ['<li data-imgId="', imgId, '" data-liId="', liId, '" data-imgPath="', imgPath, '" data-title="', title, '"data-link="', link, '" data-sumary="', summary,
                            '" class="posRelative pull-left mgr15 mgb10 border65">',
                            '<img class="previewGallery" width="78" height="50" src="',
                            imgPath,
                            '" alt="">',
                            '<img class="posAbsolute cancelBtn removeGallery" data-removeId="',
                            imgId,
                            '" data-removeLiId="',
                            liId,
                            '" src="export/images/sliderPanel/sliderPanel19.png" alt="">',
                            '</li>'].join("");
                        previewstrH += previewStr;
                    }
                    $gItemList.html(previewstrH);
                }
            } else {
                $addItemBtn.show();
                $("#gAddLocalPic").show();
                $("#carouselEdit").hide();
                $updatePic.show();
            }
            //根据是否为动态更新 修改轮播图编辑按钮显示状态
            var isUpdate=LECurrentObject.attr("data-update");
            if(isUpdate=="true"){
                $(".carousel-editor").hide();
                $changePic.show();
                $("#carouselEdit").removeClass("mgt20");
            }else{
                $(".carousel-editor").show();
                $changePic.hide();
                $("#carouselEdit").removeClass("mgt20");
                //$("#carouselEdit").addClass("mgt20");
            }
        };
        return {
            init: function () {
                //initAddItemBtnEvent();
                initResetItembyModel();
                initaddMoreItembyModel();
                initclearAllItem();
                initdragItem();
                initCarouselUpload();
                //initAppendMoreItem();
                initAppendItembyModel();
                initCarModelShow();
                initChangeCarStyle();
                initSetItembyModel();
                initAddLocalItemEvent();
                initUpdatePic();
            },
            run: function (options, doHide) {
                resetaddMoreItem();
                resetModelCarPreview();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();

                LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                $panel.hide();
                $carModelGoBack.animate({right: "-250px"}, "fast", "linear");
                $carModelDiv.animate({right: "-250px"}, "fast", "linear");
            },
            changeItemHeight: function () {
                var bdTopWidth=parseInt(LECurrentObject.css("border-top-width"));
                var bdBtmWidth=parseInt(LECurrentObject.css("border-bottom-width"));
                var width=parseInt(LECurrentObject.css("height"));
                var imgHeight=parseInt(width-bdTopWidth-bdBtmWidth);

                if(imgHeight<0){
                    imgHeight=0
                }
                //LECurrentObject.find("div.carousel-inner .item img").css("height", imgHeight);
                var id = LECurrentObject.attr("id");
                var selector = $.trim("#" + id + " " + "div.carousel-inner .item img");
                StyleManager.setStyle(selector, "height", imgHeight+"px");
            },
            changeHeight: function () {

            }
        };
    };
})(window, jQuery, LE, undefined);

