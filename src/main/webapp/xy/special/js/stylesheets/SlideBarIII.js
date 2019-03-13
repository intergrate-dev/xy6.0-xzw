/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){

    LE.stylesheets["SlideBarIII"] = function(){
        /**
         * 返回按钮
         */
        var initGoBackToIBtn = function(){
            $("#model-edit-goback-III").bind("click", function(){
                LEStyle.destroy("SlideBarIII");
                LEStyle.destroy("TextSettingWholeIII");
                LEStyle.destroy("LinkIII");
                LEStyle.destroy("css-BolderSettingIII");
            });
        };

        /**
         * 显示第三层
         */
        var showSlideBarIII = function(){
            $("#editModelGoBack-III").animate({right: "0"}, "fast", "linear");
            $("#sidebar-panel-III").animate({right: "0"}, "fast", "linear");
        };
        return {
            init: function(){
                initGoBackToIBtn();
            },
            run: function(options, doHide){
                showSlideBarIII();
            },
            destroy: function(){
                $("#editModelGoBack-III").animate({right: "-250px"}, "fast", "linear");
                $("#sidebar-panel-III").animate({right: "-250px"}, "fast", "linear");
                $("#panel-III-tab").hide();
                $("#panel-III-mainWH").hide();
                $("#panel-III-subWH").hide();
                $("#panel-III-tabWH").hide();
                $("#panel-III-mainActive").hide();
                $("#panel-III-subActive").hide();
                $("#panel-III-styleTab").hide();
                $("#textSection-style").hide();
                $("#bgSection-style").hide();
                $("#bolderSection-style").hide();
            }
        };
    };
})(window, jQuery, LE, undefined);