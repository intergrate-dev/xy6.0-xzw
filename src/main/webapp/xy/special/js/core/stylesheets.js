window.LEStyle = LE.stylesheets = function(){
    var _styles = {};
    return {
        init: function(){
            var defaultMethod = ["init", "register", "load", "run", "destroy", "destroyAll", "getObject"];
            var _obj = utils.listToMap(defaultMethod);
            for(var s in LE.stylesheets){
                if(!_obj[s]){
                    var _bb = new LE.stylesheets[s];
                    _bb._$$key$$ = s;
                    utils.ensureImplements(_bb, ["init", "run", "destroy"]);
                    this.register(s, LE.stylesheets[s]);
                }
            }
        },
        register: function(styleName, fn){
            _styles[styleName] = {
                execFn: fn,
                status: false
            }
        },
        load: function(){
            this.init();
            //向下兼容
            utils.each(_styles, function(_style){
                if(_style){
                    _style.execFn.call(this).init();
                }
            });
        },
        run: function(styleName, options, doHide, doSlide){
            var _style = _styles[styleName];
            if(_style){
                _style.status = true;
                LECurrentObject = options.object;
                LE2ndLayerObject = options.secondObject;
                LE3rdLayerObject = options.thirdObject;
                _style.execFn.call(this).run(options, doHide, doSlide);
            }
            return this;
        },
        destroy: function(styleName){
            var _style = _styles[styleName];
            if(_style && _style.status){
                _style.status = false;
                _style.execFn.call(this).destroy();
            }
            return this;
        },
        destroyAll: function(_id){
            var isCurrentObj = _id && LECurrentObject && _id === LECurrentObject.attr("id");
            utils.each(_styles, function(_style){
                if(_style && _style.status){
                    _style.status = false;
                    _style.execFn.call(this).destroy();
                }
            });
            isSameObject = isCurrentObj;
            return this;
        },
        getObject: function(layer){
            var obj = null;
            if(!layer){
                return LECurrentObject;
            }
            switch(layer){
                case 0:
                    obj = LECurrentObject;
                    break;
                case 1:
                    obj = LE2ndLayerObject;
                    break;
                case 2:
                    obj = LE3rdLayerObject;
                    break;
                default:
                    obj = LECurrentObject;
            }
            return obj;
        }
    };
}();

/**
 * 显示的时候是滑动还是直接展示
 * @param _$section
 * @param doHide
 * @param isSlide
 * @constructor
 */
window.LEDisplay = function(){
    return {
        show: function(_$section, doHide, doSlide){

            var _p = _$section.children("div:first");
            /*if(doHide){
             //同级img
             _p.children("img:first").hide();
             _p.children("img:last").show();
             _p.siblings().hide();
             } else{*/
            _p.children("img:first").show();
            _p.children("img:last").hide();
            _p.siblings().show();
            //}

            if(doSlide){
                _$section.slideDown();
            } else{
                _$section.show();
            }
        }
    };
}();


LE.options["color_options"] = {
    color: "#fff",
    allowEmpty: true,
    showAlpha: true,
    chooseText: "选择",
    showInput:true,
    cancelText: "取消",
    showInitial: true,
    showPalette: true,
    showSelectionPalette: true,
    maxPaletteSize: 10,
    preferredFormat: "hex",
    togglePaletteMoreText: '更多',
    togglePaletteLessText: '收起',
    palette: [
        ["#000", "#444", "#666", "#999", "#ccc", "#eee", "#f3f3f3", "#fff"],
        ["#f00", "#f90", "#ff0", "#0f0", "#0ff", "#00f", "#90f", "#f0f"],
        ["#f4cccc", "#fce5cd", "#fff2cc", "#d9ead3", "#d0e0e3", "#cfe2f3", "#d9d2e9", "#ead1dc"],
        ["#ea9999", "#f9cb9c", "#ffe599", "#b6d7a8", "#a2c4c9", "#9fc5e8", "#b4a7d6", "#d5a6bd"],
        ["#e06666", "#f6b26b", "#ffd966", "#93c47d", "#76a5af", "#6fa8dc", "#8e7cc3", "#c27ba0"],
        ["#c00", "#e69138", "#f1c232", "#6aa84f", "#45818e", "#3d85c6", "#674ea7", "#a64d79"],
        ["#900", "#b45f06", "#bf9000", "#38761d", "#134f5c", "#0b5394", "#351c75", "#741b47"],
        ["#600", "#783f04", "#7f6000", "#274e13", "#0c343d", "#073763", "#20124d", "#4c1130"]
    ]
};

window.LEColorPicker = {
    getOptions: function(fn){
        return utils.combineObject(LE.options["color_options"], {choose: fn});
    }
};