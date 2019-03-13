/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["Code"] = function(){
        //当前功能面板的对象
        var $PS = $("#codeSection");

        var initAddEvent = function(){
            $("#addCode").click(function(e){
                var timeStamp = new Date().getTime();
                var data = LECurrentObject.hasClass("le-code") ? LECurrentObject.html() : "";
                LEDialog.toggleDialog(LE.options["Dialog"].codeDialog + "&timestamp=" + timeStamp, function(codeHtml){
                        try{
                            LECurrentObject.html(codeHtml);
                        } catch(err){
                            alert("加入的代码可能存在js方面的问题！");
                        }
                    },
                    data
                );
                LEHistory.trigger();
            });
        };

        return {
            //onload
            init: function(){
                initAddEvent();
            },
            run: function(options, doHide){
                LEDisplay.show($PS, doHide);
            },
            destroy: function(){
                $PS.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);