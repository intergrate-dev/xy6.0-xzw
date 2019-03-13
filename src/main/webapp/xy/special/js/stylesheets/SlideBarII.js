/**
 * Created by isaac_gu on 2016/1/14.
 * 第二层相关的方法
 */
(function(window, $, LE){
    LE.stylesheets["SlideBarII"] = function(){
        /**
         * 显示第二层
         */
        var showSlideBarII = function(){
            $("#editModelGoBack").animate({right: "0"}, "fast", "linear");
            $("#sidebar-panel-II").animate({right: "0"}, "fast", "linear");
        };

        /**
         * 返回按钮
         */
        var initGoBackToIBtn = function(){
            $("#model-edit-goback").bind("click", function(){
                LEStyle.destroy("SlideBarII").destroy("TitleSettingBtn").destroy("summarySettingBtn").destroy("sourceSettingBtn").destroy("TimeBtn").destroy("LinkBtn").destroy("css-BolderSettingII");
            });
        };

        return {
            init: function(){
                initGoBackToIBtn();
            },
            run: function(){
                showSlideBarII();
            },
            destroy: function(){
                $("#editModelGoBack").animate({right: "-250px"}, "fast", "linear");
                $("#sidebar-panel-II").animate({right: "-250px"}, "fast", "linear");
            }
        };
    };

    /**
     * 标题样式设置按钮相关的方法
     * @returns {{init: init, run: run, destroy: destroy}}
     * @constructor
     */
    LE.stylesheets["TitleSettingBtn"] = function(){
        var $PC = $("#titleSettingBtn");
        var initTitleSettingBtn = function(){
            $PC.bind("click", function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LE2ndLayerObject,
                    thirdObject: LE2ndLayerObject.find(".media-body").children("ul"),
                    type: "all"
                };
                    $("#model-edit-goback-III").find("span").text("标题样式设置");
                LEStyle.run("SlideBarIII", options).run("css-PositionIII", options).run("css-BolderSettingIII", options).run("css-TextSettingWholeIII", options).run("css-BackGroundIII", options);
                    //LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("TextSettingWholeIII", options).run("BackGroundIII", options);
            });
        };
        return {
            init: function(){
                initTitleSettingBtn();
            },
            run: function(options, doHide, doSlide){
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };


/**摘要样式设置
 * */
    LE.stylesheets["summarySettingBtn"] = function(){
        var $PC = $("#summarySettingBtn");
        var initTitleSettingBtn = function(){
            $PC.bind("click", function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LE2ndLayerObject,
                    thirdObject: LE2ndLayerObject.find(".listSummary").find(".listSummaryBox"),
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("摘要样式设置");
                LEStyle.run("SlideBarIII", options).run("css-PositionIII", options).run("css-BolderSettingIII", options).run("css-TextSettingWholeIII", options).run("css-BackGroundIII", options);
                //LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("TextSettingWholeIII", options).run("BackGroundIII", options);
            });
        };
        return {
            init: function(){
                initTitleSettingBtn();
            },
            run: function(options, doHide, doSlide){
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };


    /**来源样式设置
     * */
    LE.stylesheets["sourceSettingBtn"] = function(){
        var $PC = $("#sourceSettingBtn");
        var initTitleSettingBtn = function(){
            $PC.bind("click", function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LE2ndLayerObject,
                    //thirdObject: LE2ndLayerObject.find(".listSource").find(".listSourceBox"),
                    thirdObject: LE2ndLayerObject.find(".listSourceDetailedStyle"),
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("来源样式设置");
                LEStyle.run("SlideBarIII", options).run("css-PositionIII", options).run("css-BolderSettingIII", options).run("css-TextSettingWholeIII", options).run("css-BackGroundIII", options);
                //LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("TextSettingWholeIII", options).run("BackGroundIII", options);
            });
        };
        return {
            init: function(){
                initTitleSettingBtn();
            },
            run: function(options, doHide, doSlide){
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };

    LE.stylesheets["TimeBtn"] = function(){
        var $PC = $("#timeBtn");
        var initTitleSettingBtn = function(){
            $PC.bind("click", function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LE2ndLayerObject,
                    //thirdObject: LE2ndLayerObject.find(".media-right").children("span"),
                    thirdObject: LE2ndLayerObject.find(".listTimeDetailedStyle"),
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("发布时间样式设置");
                LEStyle.run("SlideBarIII", options).run("css-PositionIII", options).run("css-BolderSettingIII", options).run("css-TextSettingWholeIII", options).run("css-BackGroundIII", options);
                //LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("TextSettingWholeIII", options).run("BackGroundIII", options);
            });
        };
        return {
            init: function(){
                initTitleSettingBtn();
            },
            run: function(options, doHide, doSlide){
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };

    LE.stylesheets["LinkBtn"] = function(){
        var $PC = $("#linkBtn");
        var initTitleSettingBtn = function(){
            $PC.bind("click", function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LE2ndLayerObject,
                    thirdObject: LECurrentObject.children(".moreListShow"),
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("右侧链接样式设置");
                LEStyle.run("SlideBarIII", options).run("css-PositionIII", options).run("css-BolderSettingIII", options).run("LinkIII", options).run("css-TextSettingWholeIII", options).run("css-BackGroundIII", options);
                //LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("LinkIII", options).run("TextSettingWholeIII", options).run("BackGroundIII", options);
            });
        };
        return {
            init: function(){
                initTitleSettingBtn();
            },
            run: function(options, doHide, doSlide){
                LEDisplay.show($PC, doHide, doSlide);
            },
            destroy: function(){
                $PC.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);