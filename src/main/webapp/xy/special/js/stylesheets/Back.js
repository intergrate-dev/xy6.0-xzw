/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.stylesheets["Back"] = function(){
        //当前功能面板的对象
        var $PS = $("#backSection");

        var initBlockEvent = function(){
            $PS.children("div").click(function(){
                var _a = $PS.attr("data-target") + "";
                if(_a){
                    _a = _a.split(",");
                    if(_a.length > 0){
                        $("#sidebar-panel").find("section").hide();
                        var options = {
                            object: LECurrentObject
                        };
                        for(var i in _a){
                            var _h = _a[i] != "NavManage" ? true : false;
                            LEStyle.run(_a[i], options, _h, true);
                        }
                    }
                }
            });
        };
        var resetTarget = function(_a){
            $PS.attr("data-target", _a);
        };
        return {
            init: function(){
                initBlockEvent();
            },
            run: function(options, doHide){
                resetTarget(options.target);
                LEDisplay.show($PS, doHide);
            },
            destroy: function(){
                $PS.hide();
            }
        };
    };
})
(window, jQuery, LE, undefined);