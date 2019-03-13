/**
 * Created by isaac_gu on 2016/1/14.
 */
(function(window, $, LE){
    LE.cores["Reset"] = function(){

        /**
         * 1. 计算每一个组件的大小
         * 2. 如果组件的总高度比 container - 150 大，container + 150
         * 3. 如果组件的总高度 + 150 比 container 小， container 的高度 =总高度 + 150
         */
        function changeContainerHeight(){
            var $container = $("#container");
            //1. 计算每一个组件的大小 - 组件的总高度
            var _entityHeight = getEntityHeight();
            //窗口的高度
            var _windowHeight = Number($(window).height()) - 2;

            $container.css("min-height", _windowHeight);
            $("#mainDiv").perfectScrollbar("update");
        }

        function getEntityHeight(){
            var $entities = $("#container").children(".plugin");
            //1. 计算每一个组件的大小 - 组件的总高度
            var _height = 0;
            $entities.each(function(){
                _height += $(this).children(".view").children(".plugin_entity").outerHeight(true);
            });
            return _height;
        }

        /**
         * 使得 columnBox的每一个column 都一样高
         */
        function changeColumnBoxHeight(){
            var $cbs = $("#container").find(".le-columnbox");
            $cbs.each(function(){
                var _maxHeight = 0;
                var _hasEntity = false;
                var _boxs = $(this).children(".w-col");
                _boxs.each(function(){
                    $(this).children(".column").not("[data-positionset=true]").css("min-height", "");
                    var _h = $(this).children(".column").innerHeight();
                    if(_h > _maxHeight){
                        _maxHeight = _h;
                    }
                    var __html = $(this).children(".column").html();
                    __html = $.trim(__html);
                    if($(this).children(".column").size() > 0 && __html != ""){
                        _hasEntity = true;
                    }
                });
                _maxHeight = _hasEntity ? _maxHeight : 50;
                _boxs.children(".column").not("[data-positionset=true]").css("min-height", _maxHeight);

            });

        }

        /**
         * 重新设置组件的宽高 - 不能超过容器的大小
         * 主要是拖拽时设置
         */
        function resetEntityWH(){
            var _$target = LECurrentObject;
            var _$parent = _$target.parent().parent().parent();

            //图片组件需要重新设置
            if(_$target.hasClass("le-image")){
                resizeImg(_$target.find(".le-image_img"));
            }

            //如果最外层是 container就不做处理了
            if(_$parent.attr("id") == "container"){
                return;
            }

            //找到最近的容器 - 如果没有容器大，也不做处理
            var _$co = _$target.closest(".column");
            if(_$co.width() > _$target.width()){
                return;
            }

            //图片组件
            if(_$target.hasClass("le-image") && _$co.width() > _$target.find(".le-image_img").width()){
                return;
            }

            // 去掉width，让组件自己撑开
            if(_$target.attr("id") != "container" && !_$target.hasClass("le-image")){
                _$target.css("width", "");
                _$target.css("max-width", "");
            }
        }

        /**
         * 图片不能超过容器的大小
         * 1. 计算预计的宽高
         * 2. 如果图片的宽小于容器的宽， - 不能返回 - 还是需要设置最大宽高
         * 3. 如果图片的宽大于容器的宽，
         * a. 去掉resizable组件，
         * b.设置图片的宽为容器的宽，并且通过计算得到高
         * c.加上组件 - 设置最大宽,如果保存比例设置最大高，否则不设
         * @param _$this
         */
        function resizeImg(_$this){
            //最外层的容器宽高 - entity
            //var _$column = LECurrentObject.parent().parent().parent().attr("id") == "container" ? LECurrentObject.parent().parent().parent() : _$this.closest(".column");

            var _colWidth = LECurrentObject.width();
            var _entityWidth = _$this.width();

            //a. 去掉resizable组件
            _$this.resizable("destroy");

            _$this.css("max-width", "");
            _$this.css("max-heigth", "");
            //2. 如果图片的宽大于于容器的宽，return
            if(_entityWidth > _colWidth){
                _entityWidth = _colWidth;
                _$this.css("width", _entityWidth);
            }

            //b.设置图片的宽为容器的宽，并且通过计算得到高

            var keepRatio = _$this.attr("data-unlocked");
            keepRatio = keepRatio + "" != "true";

            var option = {
                aspectRatio: keepRatio,
                ghost: false,
                maxWidth: _colWidth,
                start: function(){
                    LEHandler.handler_enableMouseisOver = false;
                    $(this).parent().parent().css("height", "");
                    LEDrag.hideHint();
                },
                stop: function(){
                    LEHandler.handler_enableMouseisOver = true;

                    var _$this = $(this);
                    LEHistory.trigger();
                    _$this.click();
                    LEDrag.hideHint();
                }
            };
            if(keepRatio){
                var _entityHeight = _entityWidth / parseFloat(_$this.attr("data-radio"));
                _$this.css("height", _entityHeight);
            }

            //c.加上组件 - 设置最大宽,如果保存比例设置最大高，否则不设
            _$this.resizable(option);

        }

        function resizeImg_old(_$this){
            var _w = _$this.parent().parent().css("width");
            _$this.parent().parent().css("height", "");
            _$this.css("width", "");
            _$this.css("height", "");
            _$this.css("max-width", _w);
            _$this.css("width", _$this.css("width"));
            _$this.css("height", _$this.css("height"));
            _$this.parent().css("width", _$this.css("width"));
            _$this.parent().css("height", _$this.css("height"));
            _$this.parent().css("max-width", _w);
            _$this.attr("data-radio", parseInt(_$this.css("width")) / parseInt(_$this.css("height")));
            changeColumnBoxHeight();
        }

        function po_Last_Div(obj){
            if(window.getSelection){//ie11 10 9 ff safari
                obj.focus(); //解决ff不获取焦点无法定位问题
                var range = window.getSelection();//创建range
                range.selectAllChildren(obj);//range 选择obj下所有子内容
                range.collapseToEnd();//光标移至最后
            }
            else if(document.selection){//ie10 9 8 7 6 5
                var range = document.selection.createRange();//创建选择对象
                //var range = document.body.createTextRange();
                range.moveToElementText(obj);//range定位到obj
                range.collapse(false);//光标移至最后
                range.select();
            }
        }

        return {
            init: function(){

            },
            changeContainerHeight: function(){
                changeContainerHeight();
            },
            changeColumnBoxHeight: changeColumnBoxHeight,
            resetEntityWH: resetEntityWH,
            resizeImage: resizeImg,
            MoveCunsorToEnd: function(obj){
                po_Last_Div(obj[0]);
            }
        };
    };
})(window, jQuery, LE, undefined);
window.LEReset = LE.cores["Reset"]();