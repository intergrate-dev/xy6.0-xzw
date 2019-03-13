/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["SlideBar"] = function(){
        var initListEditStyleBtn = function(){
            $("#editListBtn").click(function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LECurrentObject.children("ul").children("li"),
                    type: "all"
                };
                //LEStyle.run("SlideBarII", options).run("PositionII", options).run("BolderSettingII", options).run("BackGroundII", options).run("TitleSettingBtn", options);
                LEStyle.run("SlideBarII", options).run("css-PositionII", options).run("css-BolderSettingII", options).run("css-BackGroundII", options).run("TitleSettingBtn", options);
                if(LECurrentObject.children(".moreListShow").is(":visible")){
                    LEStyle.run("LinkBtn", options);
                }
                if(LECurrentObject.find("span.timeShow").is(":visible")||LECurrentObject.find("p.listTime-middle").is(":visible")||LECurrentObject.find("p.listTime-bottom").is(":visible")){
                    LEStyle.run("TimeBtn", options);
                }
                if(LECurrentObject.find("span.listSummaryBox").is(":visible")){
                    LEStyle.run("summarySettingBtn", options);
                }
                if(LECurrentObject.find("span.listSourceBox").is(":visible")||LECurrentObject.find("p.listSource-middle").is(":visible")||LECurrentObject.find("p.listSource-bottom").is(":visible")){
                    LEStyle.run("sourceSettingBtn", options);
                }
            });
        };


        return {
            init: function(){
                initListEditStyleBtn();

            },
            run: function(options, doHide){
                LEStyle.destroy("SlideBarII");
                LEStyle.destroy("SlideBarIII");
                LEStyle.destroy("TextSettingWholeIII");
                LEStyle.destroy("LinkIII");
            },
            destroy: function(){

            }
        };
    };

    LE.stylesheets["WholeSetting"] = function(){
        var $PC = $("#wholeSetSection");
        var initWholeSetBtn = function(){
            $PC.click(function(){
                var options = {
                    object: LECurrentObject,
                    secondObject: LECurrentObject,
                    type: "all"
                };
                if(LECurrentObject.hasClass("le-list-group")){
                    LEStyle.run("SlideBarII", options).run("css-PositionII", options).run("css-BolderSettingII", options).run("css-BackGroundII", options);
                }else{
                    LEStyle.run("SlideBarII", options).run("PositionII", options).run("BolderSettingII", options).run("BackGroundII", options);
                }

            });
        };


        //主导航
        var initMainNavSetBtn = function(){
            $("#ediMainNavBtn").click(function(){
                $("#panel-III-active").removeClass("select");
                $("#panel-III-basic").addClass("select");
                var options = {
                    object: LECurrentObject,
                    secondObject: LECurrentObject,
                    thirdObject: LECurrentObject,
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("编辑主导航样式");
                LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("BackGroundIII", options);
                $("#panel-III-tab").show();
                $("#panel-III-mainWH").show();
            });
            //基本样式
            $("#panel-III-basic").click(function(){
                $("#panel-III-active").removeClass("select");
                $(this).addClass("select");
                var isMain=$("#model-edit-goback-III").find("span").text();
                if(isMain=="编辑主导航样式"){
                    var options = {
                        object: LECurrentObject,
                        secondObject: LECurrentObject,
                        thirdObject: LECurrentObject,
                        type: "all"
                    };
                    LEStyle.destroy("TextSettingWholeIII").destroy("ActiveTextStyle").destroy("ActiveBackgroundStyle").destroy("ActiveBolderStyle");
                    LEStyle.run("PositionIII", options).run("BolderSettingIII", options).run("BackGroundIII", options);
                    $("#panel-III-mainWH").show();
                    $("#panel-III-subWH").hide();
                    $("#panel-III-tabWH").hide();
                    $("#panel-III-mainActive").hide();
                    $("#panel-III-subActive").hide();
                }else if(isMain=="编辑子导航样式"){
                    var options = {
                        object: LECurrentObject,
                        secondObject: LECurrentObject,
                        thirdObject: LECurrentObject.find("ul.dropdown-menu"),
                        type: "all"
                    };
                    LEStyle.destroy("TextSettingWholeIII").destroy("ActiveTextStyle").destroy("ActiveBackgroundStyle").destroy("ActiveBolderStyle");
                    LEStyle.run("PositionIII", options).run("BolderSettingIII", options).run("BackGroundIII", options);
                    $("#panel-III-subWH").show();
                    $("#panel-III-mainWH").hide();
                    $("#panel-III-tabWH").hide();
                    $("#panel-III-mainActive").hide();
                    $("#panel-III-subActive").hide();
                }else if(isMain=="编辑标签样式"){
                    var options = {
                        object: LECurrentObject,
                        secondObject: LECurrentObject,
                        thirdObject: LECurrentObject.find("ul.le-tabs-ul"),
                        type: "all"
                    };
                    LEStyle.destroy("TextSettingWholeIII").destroy("ActiveTextStyle").destroy("ActiveBackgroundStyle").destroy("ActiveBolderStyle");
                    LEStyle.run("PositionIII", options).run("BolderSettingIII", options).run("BackGroundIII", options);
                    $("#panel-III-mainWH").hide();
                    $("#panel-III-subWH").hide();
                    $("#panel-III-tabWH").show();
                    $("#panel-III-mainActive").hide();
                    $("#panel-III-subActive").hide();

                }
            });
            //交互样式
            $("#panel-III-active").click(function(){
                $("#panel-III-basic").removeClass("select");
                $(this).addClass("select");
                var isMain=$("#model-edit-goback-III").find("span").text();
                if(isMain=="编辑主导航样式"){
                    //添加标记用于区分是对主导航还是子导航的设置
                    LECurrentObject.attr("data-setting","mainNav");
                    var options = {
                        object: LECurrentObject,
                        secondObject: LECurrentObject,
                        thirdObject: LECurrentObject.find("ul.nav>li"),
                        type: "all"
                    };
                    LEStyle.destroy("PositionIII").destroy("BackGroundIII").destroy("BolderSettingIII");
                    LEStyle.run("ActiveTextStyle", options).run("ActiveBolderStyle", options).run("ActiveBackgroundStyle", options);
                    $("#panel-III-subWH").hide();
                    $("#panel-III-mainWH").hide();
                    $("#panel-III-tabWH").hide();
                    //$("#panel-III-mainActive").show();
                    $("#panel-III-mainActive").hide();
                    $("#panel-III-subActive").hide();
                }else if(isMain=="编辑子导航样式"){
                    LECurrentObject.attr("data-setting","subNav");
                    var options = {
                        object: LECurrentObject,
                        secondObject: LECurrentObject,
                        thirdObject: LECurrentObject.find("ul.dropdown-menu>li"),
                        type: "all"
                    };
                    LEStyle.destroy("PositionIII").destroy("BackGroundIII").destroy("BolderSettingIII");
                    LEStyle.run("ActiveTextStyle", options).run("ActiveBolderStyle", options).run("ActiveBackgroundStyle", options);
                    $("#panel-III-subWH").hide();
                    $("#panel-III-mainWH").hide();
                    $("#panel-III-tabWH").hide();
                    $("#panel-III-mainActive").hide();
                    //$("#panel-III-subActive").show();
                    $("#panel-III-subActive").hide();
                }else if(isMain=="编辑标签样式"){
                    LECurrentObject.attr("data-setting","tab");
                    var options = {
                        object: LECurrentObject,
                        secondObject: LECurrentObject,
                        thirdObject: LECurrentObject.find("ul.le-tabs-ul>li"),
                        type: "all"
                    };
                    LEStyle.destroy("PositionIII").destroy("BackGroundIII").destroy("BolderSettingIII");
                    LEStyle.run("ActiveTextStyle", options).run("ActiveBolderStyle", options).run("ActiveBackgroundStyle", options);
                    $("#panel-III-subWH").hide();
                    $("#panel-III-mainWH").hide();
                    $("#panel-III-tabWH").hide();
                    $("#panel-III-mainActive").hide();
                    //$("#panel-III-subActive").show();
                    $("#panel-III-subActive").hide();
                }
            });

        };
        //子导航
        var initSubNavSetBtn = function(){
            $("#ediSubNavBtn").click(function(){
                $("#panel-III-active").removeClass("select");
                $("#panel-III-basic").addClass("select");
                var options = {
                    object: LECurrentObject,
                    secondObject: LECurrentObject,
                    thirdObject: LECurrentObject.find("ul.dropdown-menu"),
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("编辑子导航样式");
                LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("BackGroundIII", options);
                $("#panel-III-tab").show();
                $("#panel-III-subWH").show();
                $("#panel-III-tabWH").hide();
            });
        };

        //标签
        var initTabSetBtn = function(){
            $("#editTabBtn").click(function(){
                $("#panel-III-active").removeClass("select");
                $("#panel-III-basic").addClass("select");
                var options = {
                    object: LECurrentObject,
                    secondObject: LECurrentObject,
                    thirdObject: LECurrentObject.find("ul.le-tabs-ul"),
                    type: "all"
                };
                $("#model-edit-goback-III").find("span").text("编辑标签样式");
                LEStyle.run("SlideBarIII", options).run("PositionIII", options).run("BolderSettingIII", options).run("BackGroundIII", options);
                $("#panel-III-tab").show();
                $("#panel-III-mainWH").hide();
                $("#panel-III-subWH").hide();
                $("#panel-III-tabWH").show();
            });
        };


        return {
            init: function(){
                initWholeSetBtn();
                initMainNavSetBtn();
                initSubNavSetBtn();
                initTabSetBtn();
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