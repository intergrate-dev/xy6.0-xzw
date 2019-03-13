/**
 * 撤销与恢复
 * 修改促发器 LEHistory.trigger();
 * Created by isaac_gu on 2016/1/20.
 */
window._lehistory = {};
window._lehistory.pointer = 0;
window._lehistory.MAX_LENGTH = 20;
window._lehistory.EABLE_MAX = true;
window._lehistory.historyList = [];
window._lehistory.containerStyleList = [];
window._lehistory.styleTabList = [];
window._lehistory.chartsOptions = [];
window._charts = "";

window._lehtml = "";
window._leconstyle = "";
window._lestyleTag = "";
window.historyChanged = false;      //判断是否需要保存当前的页面 - 自动保存
(function(window, $, LE, localStorage){
    LE.cores["AutoSave"] = function(){
        var $undoBtn = $("#undoBtn");
        var $undoDiv = $undoBtn.find("div");
        var $redoBtn = $("#redoBtn");
        var $redoDiv = $redoBtn.find("div");
        var $container = $("#container");

        //撤销按钮的事件
        /**
         * 1. 点击恢复到上一个记录
         * 2. 判断当前个数，然后给button添加恢复样式
         */
        var initBtnEvent = function(){
            //撤销按钮
            $undoBtn.click(function(){
                if($undoDiv.hasClass("recover")){
                    undoLayout();
                    resetBtn();
                    LEClean.resetEntityEvent();
                  //撤销成功之后初始化图表组件 遍历map
                    resetChart();
                }

            });
            //恢复按钮
            $redoBtn.click(function(){
                //如果可以恢复的话就进行恢复操作
                if($redoDiv.hasClass("cancel")){
                    redoLayout();
                    resetBtn();
                    LEClean.resetEntityEvent();
                    //恢复成功之后初始化图表组件 遍历map
                    resetChart();
                }

            });
        };

        function resetChart(){
            $("#container").find(".le-lineChart").each(function(){
                var _id=$(this).attr("id");
                if(_id){
                    var myChart=echarts.init(document.getElementById(_id));
                    var option=lineChart_Map.get(_id);
                    myChart.setOption(option);
                }
            });
        }

        function resetBtn(){
            //  指针当前的位置
            var _p = _lehistory.pointer;
            // 历史记录的长度
            var _h = _lehistory.historyList.length;

            if(_h == 0 || _p === 1){
                $undoDiv.removeClass("recover");
            }
            if(_h == 0 || _p <= _h){
                $redoDiv.removeClass("cancel");
            }

            //撤销按钮可用： 1. _h>0 && _p>1
            if(_h > 0 && _p > 1){
                $undoDiv.addClass("recover");
            }
            // 恢复按钮可用, _p < _h
            if(_p < _h){
                $redoDiv.addClass("cancel");
            }

        }

        /**
         * 撤销
         * @returns {boolean}
         */
        function undoLayout(){
            var history = _lehistory;
            if(history){
                var _clickedId = LECurrentObject.attr("id");
                //如果，当前的指针在
                if(history.pointer < 2) return false;
                _lehtml = history.historyList[history.pointer - 2];
                //取出 container的样式
                _leconstyle = history.containerStyleList[history.pointer - 2];
                //取出 style标签的样式
                $("#container").attr("style", _leconstyle);
                //保存style标签里的样式
                _lestyleTag = history.styleTabList[history.pointer - 2];

                //取出 图表配置
                _charts=history.chartsOptions[history.pointer - 2];
                var _chartsObj=JSON.parse(_charts);
                for( var i in _chartsObj["options"]){
                    //用取出的图表配置刷新lineChart_Map
                    var _chartsObj_i=_chartsObj["options"][i];
                    lineChart_Map.put(i,_chartsObj_i)
                }

                $("#special_style").html(_lestyleTag);
                $container.html(_lehtml);

                history.pointer--;
                localStorage.setItem("lehistory", JSON.stringify(history));
                //$(".obj_click").click();
                if($("#" + _clickedId).size() > 0){
                    $("#" + _clickedId).click();
                } else{
                    $("#container").find(".drag_hint_click").click();
                }

                historyChanged = true;
                return true;
            }
            return false;
        }

        /**
         * 恢复
         * @returns {boolean}
         */
        function redoLayout(){
            var history = _lehistory;
            if(history){
                if(history.historyList[history.pointer]){
                    var _clickedId = LECurrentObject.attr("id");
                    //取出html
                    window._lehtml = history.historyList[history.pointer];
                    //取出 container的样式
                    _leconstyle = history.containerStyleList[history.pointer];
                    //取出 style标签的样式
                    $("#container").attr("style", _leconstyle);
                    //保存style标签里的样式
                    _lestyleTag = history.styleTabList[history.pointer];
                    $("#special_style").html(_lestyleTag);

                    //取出 图表配置
                    _charts=history.chartsOptions[history.pointer];
                    var _chartsObj=JSON.parse(_charts);
                    for( var i in _chartsObj["options"]){
                        //用取出的图表配置刷新lineChart_Map
                        var _chartsObj_i=_chartsObj["options"][i];
                        lineChart_Map.put(i,_chartsObj_i)
                    }

                    history.pointer++;
                    $container.html(window._lehtml);
                    localStorage.setItem("lehistory", JSON.stringify(history));
                    if(_clickedId){
                        $("#" + _clickedId).click();
                    } else{
                        $(".drag_hint_click").click();
                    }

                    historyChanged = true;
                    return true;
                }
            }
            return false;
        }

        //保存当前历史
        /**
         * 保存也分为：drag的与修改元素的
         */
        function saveHistory(){
            /*console.trace();*/
            //var _clickedObjId = removeClasses();
            var _h = $container.html();
            var _c = $("#container").attr("style");
            var _s = $("#special_style").html();
            //将画布中所有图表配置生成字符串保存
            var _chartsobj={"options":{}};
            var obj={};
            lineChart_Map.each(function (key, value, index) {
                obj[key]=value;
            });
            _chartsobj.options=jQuery.extend(true,{},obj);
            var _chart=JSON.stringify(_chartsobj);

            var history = _lehistory;
            if(_h != _lehtml || _c != _leconstyle || _s != _lestyleTag ||_chart !=_charts){
                _lehtml = _h;
                _leconstyle = _c;
                _lestyleTag = _s;
                _charts=_chart;
                //如果，历史为空，初始化一个
                //当插入一个新的纪录的时候，去掉后面的记录
                if(history.historyList.length > history.pointer){
                    for(i = history.pointer; i < history.historyList.length; i++){
                        history.historyList.pop();
                    }
                }
                //保存html
                history.historyList[history.pointer] = _lehtml;
                //保存container的样式
                history.containerStyleList[history.pointer] = _leconstyle;
                //保存style标签里的样式
                history.styleTabList[history.pointer] = _lestyleTag;
                //保存所有图表配置
                history.chartsOptions[history.pointer] = _charts;

                history.pointer++;
                localStorage.setItem("lehistory", JSON.stringify(history));

                //只能存规定长度
                if(history.EABLE_MAX && history.historyList.length > history.MAX_LENGTH){
                    history.historyList.shift();
                    history.pointer--;
                }
                _lehistory = history;

                //重新设置恢复与撤销按钮的样式
                resetBtn();


                /*if(_clickedObjId){
                 addClass(_clickedObjId);
                 LECurrentObject = $("#" + _clickedObjId);
                 }*/
                historyChanged = true;
            }
        }

        /**
         * 保存前，去掉所有的class
         */
        function removeClasses(){
            if(!LECurrentObject){
                return;
            }

            var _$target = $("#container").find("div").filter(".obj_click");
            var _$entity = _$target.children(".plugin_entity");
            var _id = _$entity.attr("id");
            if(_id){
                _$target.removeClass("obj_click");
                _$target.find(".drag_hint_click").removeClass("drag_hint_click");
                _$target.find(".drag_handler").hide();
                return _id;
            }
            return null;
        }

        function addcClass(_id){
            var _$target = $("#" + _id);
            var _$p = _$target.parent();
            if(_id){
                _$p.addClass("obj_click");
                _$target.addClass("drag_hint_click");
                _$p.find(".drag_handler").show();
            }
        }

        return {
            init: function(){
                initBtnEvent();
                saveHistory();

            },
            //修改内容的时候，触发
            trigger: function(){
                saveHistory();
            }
        };
    };
    window.LEHistory = LE.cores["AutoSave"]();
})(window, jQuery, LE, LocalStorage, undefined);