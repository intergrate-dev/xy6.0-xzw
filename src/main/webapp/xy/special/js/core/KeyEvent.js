/**
 * Created by isaac_gu on 2016/4/7.
 */
window.LEUnitHover = null;
window.LEUnitTarget = null;
(function($, LE){
    LE.cores["KeyEvent"] = function(){
        var $unitDiv = $("#unit");
        var $unitHintDiv = $("#unit_hint");
        var $markHint = $unitHintDiv.find("span");
        var $mark = $unitDiv.find("span");
        var $options = $unitDiv.find("ul");
        var numInputKeyDown = function(event, $this, $target){
            //如果是上下的话，执行以下的操作
            if(event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13){
                var _value = $this.val();
                var num = parseInt(_value);
                if(isNaN(num)) return;

                checkUnit(_value, $this);

                var _unit = $this.attr("data-unit") || "px";
                //点击上下按钮，来改变当前的值
                event.keyCode == 38 && ++num;
                event.keyCode == 40 && --num;

                //设定目标的css样式
                //设定目标的css样式
                $target.css($this.attr("data-ref"), num + _unit);
                $this.val(num + _unit);
                return num;

            }
            return null;
        };

        var numInputBlur = function(event, $this, $target){
            var _value = $this.val();
            $.trim(_value) == "" && $this.val($this.data("dv"));
            if($.trim(_value) == "")  return;
            checkUnit(_value, $this);
            var num = parseInt(_value);
            var _unit = $this.attr("data-unit") || "px";
            $target.css($this.attr("data-ref"), num + _unit);
            $this.val(num + _unit);
        };

        var numInputFocus = function(event, $this, $target){
            $this.attr("data-dv", $target.css($this.attr("data-ref")));
        };

        function checkUnit(_value, $this){
            //如果包含px，就以px为主
            if(_value.indexOf("px") != -1){
                $this.attr("data-unit", "px");
            } else if(_value.indexOf("%") != -1){
                $this.attr("data-unit", "%");
            }
        }

        var initUnitOptionEvent = function(){
            /**
             *  悬浮的时候，显示hint
             */
            $("#sidebar-panel").on({
                mouseover: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var $this = $(this);
                    var _span = $this.outerWidth() - $markHint.outerWidth();
                    $unitHintDiv.css({
                        top: $this.offset().top,
                        left: $this.offset().left + _span
                    });
                    $markHint.show();
                    LEUnitHover = $this;
                },
                mouseout: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    $markHint.hide();
                }
            }, ".unitOption");

            $unitHintDiv.hover(function(e){
                e.preventDefault();
                e.stopPropagation();
                $markHint.show();
            }, function(e){
                e.preventDefault();
                e.stopPropagation();
                $markHint.hide();
            });

            $markHint.click(function(e){
                e.preventDefault();
                e.stopPropagation();
                LEUnitTarget = LEUnitHover;
                var $this = $(this);
                $unitDiv.css({
                    top: $this.offset().top - 2,
                    left: $this.offset().left
                });
                $mark.show();
                $options.show();
                $options.find("li").show();
                if(LEUnitTarget.data("unitex")){
                    var _arr = LEUnitTarget.data("unitex").split("");
                    if(_arr){
                        for(var _a in _arr){
                            $options.find("li[data-ref='" + _arr[_a] + "']").hide();
                        }
                    }
                }

                $unitDiv.show();

            });
            $options.find("li").click(function(e){
                e.preventDefault();
                e.stopPropagation();
                LEUnitTarget.attr("data-unit", $(this).text());
                !isNaN(parseInt(LEUnitTarget.val())) && LEUnitTarget.val(parseInt(LEUnitTarget.val()) + $(this).text());
                LEUnitTarget.trigger("blur");
                $markHint.hide();
                $unitDiv.hide();
            });

            $(document).click(function(){
                $markHint.hide();
                $unitDiv.hide();
            });

        };

        return {
            init: function(){
                initUnitOptionEvent();
                //NumInput();
            },
            numInputKeyDown: function(event, $this, $target){
                return numInputKeyDown(event, $this, $target);
            },
            numInputBlur: function(event, $this, $target){
                return numInputBlur(event, $this, $target);
            },
            numInputFocus: function(event, $this, $target){
                return numInputFocus(event, $this, $target);
            }
        }
    };
})(jQuery, LE, undefined);

window.LEKey = LE.cores["KeyEvent"]();