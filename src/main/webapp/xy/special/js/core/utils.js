/**
 * 存放一些公共方法
 * Created by isaac_gu on 2016/1/4.
 */
(function(window, $, XXX){
    if(!$){
        throw new Error("没有引入jquery");
    }
    Function.prototype.getName = function(){
        return this.name || this.toString().match(/function\s*([^(]*)\(/)[1]
    };

    /**
     * 接口
     * @param name
     * @param methods
     * @constructor
     */
    XXX.Interface = function(name, methods){
        //判断接口的参数个数
        if(arguments.length != 2){
            throw new Error("this instance interface constructor arguments must be 2!");
        }
        this.name = name;
        this.methods = [];      //定义一个内置的空数组对象 等待接收methods里的元素（方法名称）
        for(var i = 0, len = methods.length; i < len; i++){

            if(typeof methods[i] !== 'string'){
                throw new Error("the interface method name ");
            }
            this.methods.push(methods[i]);
        }

    };
    /**
     * 查看实现接口的类是否实现了所有的方法
     * @param object
     * @constructor
     */
    XXX.Interface.EnsureImplements = function(object){
        //如果检测的方法的参数小于2个 参数传递失败！
        if(arguments.length < 2){
            throw new Error('this instance interface constructor arguments must be 2!');
        }

        //获得接口实例对象
        for(var i = 1, len = arguments.length; i < len; i++){
            var instanceInterface = arguments[i];
            //判断参数是否是接口的类型
            if(instanceInterface.constructor !== Tools.Interface){
                throw new Error("the arguments constructor not be interface class!");
            }
            //循环接口实例对象里面的每一个方法
            for(var j = 0; j < instanceInterface.methods.length; j++){
                //string 用一个临时变量接收每一个方法的名字
                var methodName = instanceInterface.methods[j];
                //object[key] 就是方法
                //不纯在，或者存在但不是函数
                if(!object[methodName] || typeof object[methodName] !== 'function'){
                    throw new Error('the method [' + methodName + '] is not found');
                }
            }
        }

    };
    /**
     * 继承
     * @param sub   子类
     * @param sup   父类
     */
    XXX.extend = function(sub, sup){
        //1. 实现只继承父类的原型对象 - 用一个空函数进行中转
        var F = new Function();
        //2. 实现了空函数和父类的原型兑现高转换
        F.prototype = sup.prototype;
        //3. 原型继承
        sub.prototype = new F();
        //4. 还原子类的构造器
        sub.prototype.constructor = sub;
        //保存一下父类的原型对象； 一方面方便解耦；另一方面可以轻松获得父类的原型对象
        sub.superclass = sup.prototype;

        //#3. 加保险 - 判断父类的原型构造器
        if(sup.prototype.constructor == Object.prototype.constructor){
            sup.prototype.constructor = sup; //手动还原父类的构造器
        }

    };

    /**
     * 常用工具
     * @type {{supportStorage: Function}}
     */
    window.utils = XXX.utils = {
        /**
         * 用给定的迭代器遍历数组或类数组对象
         * @method each
         * @param { Array } array 需要遍历的数组或者类数组
         * @param { Function } iterator 迭代器， 该方法接受两个参数， 第一个参数是当前所处理的value， 第二个参数是当前遍历对象的key
         * @example
         * ```javascript
         * var divs = document.getElmentByTagNames( "div" );
         *
         * //output: 0: DIV, 1: DIV ...
         * UE.utils.each( divs, funciton ( value, key ) {
     *
     *     console.log( key + ":" + value.tagName );
     *
     * } );
         * ```
         */
        each: function(obj, iterator, context){
            if(obj == null) return;
            if(obj.length === +obj.length){
                for(var i = 0, l = obj.length; i < l; i++){
                    if(iterator.call(context, obj[i], i, obj) === false)
                        return false;
                }
            } else{
                for(var key in obj){
                    if(obj.hasOwnProperty(key)){
                        if(iterator.call(context, obj[key], key, obj) === false)
                            return false;
                    }
                }
            }
        },
        /**
         * 将字符串数组转换成哈希对象， 其生成的hash对象的key为数组中的元素， value为1
         * @method listToMap
         * @warning 该方法在生成的hash对象中，会为每一个key同时生成一个另一个全大写的key。
         * @param { Array } arr 字符串数组
         * @return { Object } 转化之后的hash对象
         * @example
         * ```javascript
         *
         * //output: Object {UEdtior: 1, UEDTIOR: 1, Hello: 1, HELLO: 1}
         * console.log( UE.utils.listToMap( [ 'UEdtior', 'Hello' ] ) );
         *
         * ```
         */
        listToMap: function(list){
            if(!list)return {};
            list = utils.isArray(list) ? list : list.split(',');
            for(var i = 0, ci, obj = {}; ci = list[i++];){
                obj[ci.toUpperCase()] = obj[ci] = 1;
            }
            return obj;
        },
        /**
         * 创建延迟指定时间后执行的函数fn, 如果在延迟时间内再次执行该方法， 将会根据指定的exclusion的值，
         * 决定是否取消前一次函数的执行， 如果exclusion的值为true， 则取消执行，反之，将继续执行前一个方法。
         * @method defer
         * @param { Function } fn 需要延迟执行的函数对象
         * @param { int } delay 延迟的时间， 单位是毫秒
         * @param { Boolean } exclusion 如果在延迟时间内再次执行该函数，该值将决定是否取消执行前一次函数的执行，
         *                     值为true表示取消执行， 反之则将在执行前一次函数之后才执行本次函数调用。
         * @warning 该方法的时间控制是不精确的，仅仅只能保证函数的执行是在给定的时间之后，
         *           而不能保证刚好到达延迟时间时执行。
         * @return { Function } 目标函数fn的代理函数， 只有执行该函数才能起到延时效果
         * @example
         * ```javascript
         *
         * function test(){
     *     console.log(1);
     * }
         *
         * var testDefer = UE.utils.defer( test, 1000, true );
         *
         * //output: (两次调用仅有一次输出) 1
         * testDefer();
         * testDefer();
         * ```
         */
        defer: function(fn, delay, exclusion){
            var timerID;
            return function(){
                if(exclusion){
                    clearTimeout(timerID);
                }
                timerID = setTimeout(fn, delay);
            };
        },

        /**
         * 获取元素item在数组array中首次出现的位置, 如果未找到item， 则返回-1
         * @method indexOf
         * @remind 该方法的匹配过程使用的是恒等“===”
         * @param { Array } array 需要查找的数组对象
         * @param { * } item 需要在目标数组中查找的值
         * @return { int } 返回item在目标数组array中首次出现的位置， 如果在数组中未找到item， 则返回-1
         * @example
         * ```javascript
         * var item = 1,
         *     arr = [ 3, 4, 6, 8, 1, 1, 2 ];
         *
         * //output: 4
         * console.log( UE.utils.indexOf( arr, item ) );
         * ```
         */

        /**
         * 获取元素item数组array中首次出现的位置, 如果未找到item， 则返回-1。通过start的值可以指定搜索的起始位置。
         * @method indexOf
         * @remind 该方法的匹配过程使用的是恒等“===”
         * @param { Array } array 需要查找的数组对象
         * @param { * } item 需要在目标数组中查找的值
         * @param { int } start 搜索的起始位置
         * @return { int } 返回item在目标数组array中的start位置之后首次出现的位置， 如果在数组中未找到item， 则返回-1
         * @example
         * ```javascript
         * var item = 1,
         *     arr = [ 3, 4, 6, 8, 1, 2, 8, 3, 2, 1, 1, 4 ];
         *
         * //output: 9
         * console.log( UE.utils.indexOf( arr, item, 5 ) );
         * ```
         */
        indexOf: function(array, item, start){
            var index = -1;
            start = this.isNumber(start) ? start : 0;
            this.each(array, function(v, i){
                if(i >= start && v === item){
                    index = i;
                    return false;
                }
            });
            return index;
        },

        /**
         * 移除数组array中所有的元素item
         * @method removeItem
         * @param { Array } array 要移除元素的目标数组
         * @param { * } item 将要被移除的元素
         * @remind 该方法的匹配过程使用的是恒等“===”
         * @example
         * ```javascript
         * var arr = [ 4, 5, 7, 1, 3, 4, 6 ];
         *
         * UE.utils.removeItem( arr, 4 );
         * //output: [ 5, 7, 1, 3, 6 ]
         * console.log( arr );
         *
         * ```
         */
        removeItem: function(array, item){
            for(var i = 0, l = array.length; i < l; i++){
                if(array[i] === item){
                    array.splice(i, 1);
                    i--;
                }
            }
        },

        /**
         * 删除字符串str的首尾空格
         * @method trim
         * @param { String } str 需要删除首尾空格的字符串
         * @return { String } 删除了首尾的空格后的字符串
         * @example
         * ```javascript
         *
         * var str = " UEdtior ";
         *
         * //output: 9
         * console.log( str.length );
         *
         * //output: 7
         * console.log( UE.utils.trim( " UEdtior " ).length );
         *
         * //output: 9
         * console.log( str.length );
         *
         *  ```
         */
        trim: function(str){
            return str.replace(/(^[ \t\n\r]+)|([ \t\n\r]+$)/g, '');
        },
        /**
         * 将str中的html符号转义,将转义“'，&，<，"，>”五个字符
         * @method unhtml
         * @param { String } str 需要转义的字符串
         * @return { String } 转义后的字符串
         * @example
         * ```javascript
         * var html = '<body>&</body>';
         *
         * //output: &lt;body&gt;&amp;&lt;/body&gt;
         * console.log( UE.utils.unhtml( html ) );
         *
         * ```
         */
        unhtml: function(str, reg){
            return str ? str.replace(reg || /[&<">'](?:(amp|lt|quot|gt|#39|nbsp|#\d+);)?/g, function(a, b){
                if(b){
                    return a;
                } else{
                    return {
                        '<': '&lt;',
                        '&': '&amp;',
                        '"': '&quot;',
                        '>': '&gt;',
                        "'": '&#39;'
                    }[a]
                }

            }) : '';
        },

        /**
         * 将str中的转义字符还原成html字符
         * @see UE.utils.unhtml(String);
         * @method html
         * @param { String } str 需要逆转义的字符串
         * @return { String } 逆转义后的字符串
         * @example
         * ```javascript
         *
         * var str = '&lt;body&gt;&amp;&lt;/body&gt;';
         *
         * //output: <body>&</body>
         * console.log( UE.utils.html( str ) );
         *
         * ```
         */
        html: function(str){
            return str ? str.replace(/&((g|l|quo)t|amp|#39|nbsp);/g, function(m){
                return {
                    '&lt;': '<',
                    '&amp;': '&',
                    '&quot;': '"',
                    '&gt;': '>',
                    '&#39;': "'",
                    '&nbsp;': ' '
                }[m]
            }) : '';
        },
        /**
         * 判断obj对象是否为空
         * @method isEmptyObject
         * @param { * } obj 需要判断的对象
         * @remind 如果判断的对象是NULL， 将直接返回true， 如果是数组且为空， 返回true， 如果是字符串， 且字符串为空，
         *          返回true， 如果是普通对象， 且该对象没有任何实例属性， 返回true
         * @return { Boolean } 对象是否为空
         * @example
         * ```javascript
         *
         * //output: true
         * console.log( UE.utils.isEmptyObject( {} ) );
         *
         * //output: true
         * console.log( UE.utils.isEmptyObject( [] ) );
         *
         * //output: true
         * console.log( UE.utils.isEmptyObject( "" ) );
         *
         * //output: false
         * console.log( UE.utils.isEmptyObject( { key: 1 } ) );
         *
         * //output: false
         * console.log( UE.utils.isEmptyObject( [1] ) );
         *
         * //output: false
         * console.log( UE.utils.isEmptyObject( "1" ) );
         *
         * ```
         */
        isEmptyObject: function(obj){
            if(obj == null) return true;
            if(this.isArray(obj) || this.isString(obj)) return obj.length === 0;
            for(var key in obj) if(obj.hasOwnProperty(key)) return false;
            return true;
        },

        /**
         * 判断是否有 localStorage
         * @returns {boolean}
         */
        supportStorage: function(){
            return typeof window.localStorage == 'object';
        },
        ensureImplements: function(object, array){
            //如果检测的方法的参数小于2个 参数传递失败！
            if(arguments.length < 2){
                throw new Error('this instance interface constructor arguments must be 2!');
            }
            //获得接口实例对象
            for(var i = 0; fn = array[i++];){
                if(!(fn && object[fn] && object.hasOwnProperty(fn))){
                    throw new Error(object._$$key$$ + '插件没有实现[' + fn + ']方法!');
                }
            }
        },
        /**
         * 将css样式转换为驼峰的形式
         * @method cssStyleToDomStyle
         * @param { String } cssName 需要转换的css样式名
         * @return { String } 转换成驼峰形式后的css样式名
         * @example
         * ```javascript
         *
         * var str = 'border-top';
         *
         * //output: borderTop
         * console.log( UE.utils.cssStyleToDomStyle( str ) );
         *
         * ```
         */
        cssStyleToDomStyle: function(){
            var test = document.createElement('div').style,
                cache = {
                    'float': test.cssFloat != undefined ? 'cssFloat' : test.styleFloat != undefined ? 'styleFloat' : 'float'
                };

            return function(cssName){
                return cache[cssName] || (cache[cssName] = cssName.toLowerCase().replace(/-./g, function(match){
                        return match.charAt(1).toUpperCase();
                    }));
            };
        }(),
        /**
         * 把rgb格式的颜色值转换成16进制格式
         * @method fixColor
         * @param { String } rgb格式的颜色值
         * @param { String }
         * @example
         * rgb(255,255,255)  => "#ffffff"
         */
        fixColor: function(name, value){
            if(/color/i.test(name) && /rgba?/.test(value)){
                var array = value.split(",");
                if(array.length > 3)
                    return "";
                value = "#";
                for(var i = 0, color; color = array[i++];){
                    color = parseInt(color.replace(/[^\d]/gi, ''), 10).toString(16);
                    value += color.length == 1 ? "0" + color : color;
                }
                value = value.toUpperCase();
            }
            return value;
        },
        /**
         * 只针对border,padding,margin做了处理，因为性能问题
         * @public
         * @function
         * @param {String}    val style字符串
         */
        optCss: function(val){
            var padding, margin, border;
            val = val.replace(/(padding|margin|border)\-([^:]+):([^;]+);?/gi, function(str, key, name, val){
                if(val.split(' ').length == 1){
                    switch(key){
                        case 'padding':
                            !padding && (padding = {});
                            padding[name] = val;
                            return '';
                        case 'margin':
                            !margin && (margin = {});
                            margin[name] = val;
                            return '';
                        case 'border':
                            return val == 'initial' ? '' : str;
                    }
                }
                return str;
            });

            function opt(obj, name){
                if(!obj){
                    return '';
                }
                var t = obj.top, b = obj.bottom, l = obj.left, r = obj.right, val = '';
                if(!t || !l || !b || !r){
                    for(var p in obj){
                        val += ';' + name + '-' + p + ':' + obj[p] + ';';
                    }
                } else{
                    val += ';' + name + ':' +
                        (t == b && b == l && l == r ? t :
                            t == b && l == r ? (t + ' ' + l) :
                                l == r ? (t + ' ' + l + ' ' + b) : (t + ' ' + r + ' ' + b + ' ' + l)) + ';'
                }
                return val;
            }

            val += opt(padding, 'padding') + opt(margin, 'margin');
            return val.replace(/^[ \n\r\t;]*|[ \n\r\t]*$/, '').replace(/;([ \n\r\t]+)|\1;/g, ';')
                .replace(/(&((l|g)t|quot|#39))?;{2,}/g, function(a, b){
                    return b ? b + ";;" : ';'
                });
        },
        combineObject: function(orignalObject, targetObject){
            return $.extend({}, orignalObject, targetObject);
        },

        getQueryString: function(name){
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if(r != null)return unescape(r[2]);
            return null;
        },
        contains: function(item, array){
            return RegExp("\\b" + item + "\\b").test(array);
        },
        getId: function(){
            return new Date().getTime() + Math.round(Math.random() * 10000);
        },
        sleep: function(numberMillis){
            var now = new Date();
            var exitTime = now.getTime() + numberMillis;
            while(true){
                now = new Date();
                if(now.getTime() > exitTime)
                    return;
            }
        },
        isColor:function(colorVal){
            var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
            if (reg.test(colorVal)) {
                return true;
            }else{
                return false;
            }
        }
    };
    XXX.utils.each(['String', 'Function', 'Array', 'Number', 'RegExp', 'Object', 'Date'], function(v){
        XXX.utils['is' + v] = function(obj){
            return Object.prototype.toString.apply(obj) == '[object ' + v + ']';
        }
    });

    /**
     *
     * // 限制使用了onlyNum类样式的控件只能输入数字
     $(".onlyNum").onlyNum();
     //限制使用了onlyAlpha类样式的控件只能输入字母
     $(".onlyAlpha").onlyAlpha();
     // 限制使用了onlyNumAlpha类样式的控件只能输入数字和字母
     $(".onlyNumAlpha").onlyNumAlpha();
     */
    /**
     * 只能输入数字
     * $(".onlyNum").onlyNum();
     * @returns {jQuery}
     */
    $.fn.onlyNum = function(){
        $(this).keypress(function(event){
            var eventObj = event || e;
            var keyCode = eventObj.keyCode || eventObj.which;
            if((keyCode >= 48 && keyCode <= 57))
                return true;
            else
                return false;
        }).focus(function(){
            //禁用输入法
            this.style.imeMode = 'disabled';
        }).bind("paste", function(){
            //获取剪切板的内容
            var clipboard = window.clipboardData.getData("Text");
            if(/^\d+$/.test(clipboard))
                return true;
            else
                return false;
        });
        return this;
    };

    /**
     * 限制只能输入字母
     * @returns {jQuery}
     */
    $.fn.onlyAlpha = function(){
        $(this).keypress(function(event){
            var eventObj = event || e;
            var keyCode = eventObj.keyCode || eventObj.which;
            if((keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122))
                return true;
            else
                return false;
        }).focus(function(){
            this.style.imeMode = 'disabled';
        }).bind("paste", function(){
            var clipboard = window.clipboardData.getData("Text");
            if(/^[a-zA-Z]+$/.test(clipboard))
                return true;
            else
                return false;
        });
        return this;
    };

    /**
     *  限制只能输入数字和字母
     * @returns {jQuery}
     */
    $.fn.onlyNumAlpha = function(){
        $(this).keypress(function(event){
            var eventObj = event || e;
            var keyCode = eventObj.keyCode || eventObj.which;
            if((keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122))
                return true;
            else
                return false;
        }).focus(function(){
            this.style.imeMode = 'disabled';
        }).bind("paste", function(){
            var clipboard = window.clipboardData.getData("Text");
            if(/^(\d|[a-zA-Z])+$/.test(clipboard))
                return true;
            else
                return false;
        });

        return this;
    };

    /**
     *  限制只能输入数字和字母
     * @returns {jQuery}
     */
    /*$.fn.numAndAuto = function(){
     $(this).keypress(function(event){
     var eventObj = event || e;
     var keyCode = eventObj.keyCode || eventObj.which;
     console.info(keyCode)
     if((keyCode >= 48 && keyCode <= 57)
     || keyCode == 65 || keyCode == 85 || keyCode == 84 || keyCode ==79
     || keyCode == 97 || keyCode == 117 || keyCode == 116 || keyCode ==111
     )
     return true;
     else
     return false;
     }).focus(function(){
     this.style.imeMode = 'disabled';
     }).bind("paste", function(){
     var clipboard = window.clipboardData.getData("Text");
     if(/^(\d|[a-zA-Z])+$/.test(clipboard))
     return true;
     else
     return false;
     });

     return this;
     };*/
    /* /!**
     *
     * 数组中 val的位置
     * @param val
     * @returns {number}
     *!/
     Array.prototype._indexOf = function(val){
     for(var i = 0; i < this.length; i++){
     if(this[i] == val) return i;
     }
     return -1;
     };
     /!**
     * 去掉数组中一个对象
     * @param val
     *!/
     Array.prototype._remove = function(val){
     var index = this._indexOf(val);
     if(index > -1){
     this.splice(index, 1);
     }
     };
     */

})(window, jQuery, XXX = window.XXX = {}, undefined);

/**
 * Simple Map
 *
 *
 * var m = new Map();
 * m.put('key','value');
 * ...
 * var s = "";
 * m.each(function(key,value,index){
 *      s += index+":"+ key+"="+value+"/n";
 * });
 * alert(s);
 *
 * @author dewitt
 * @date 2008-05-24
 */
function Map(){
    /** 存放键的数组(遍历用到) */
    this.keys = new Array();
    /** 存放数据 */
    this.data = new Object();

    /**
     * 放入一个键值对
     * @param {String} key
     * @param {Object} value
     */
    this.put = function(key, value){
        if(this.data[key] == null){
            this.keys.push(key);
        }
        this.data[key] = value;
    };

    /**
     * 获取某键对应的值
     * @param {String} key
     * @return {Object} value
     */
    this.get = function(key){
        return this.data[key];
    };

    /**
     * 删除一个键值对
     * @param {String} key
     */
    this.remove = function(key){
        for(var i = 0; i < this.keys.length; i++){
            if(key == this.keys[i])
                this.keys.splice(i, 1);
        }
        this.data[key] = null;
    };

    /**
     * 遍历Map,执行处理函数
     *
     * @param {Function} 回调函数 function(key,value,index){..}
     */
    this.each = function(fn){
        if(typeof fn != 'function'){
            return;
        }
        var len = this.keys.length;
        for(var i = 0; i < len; i++){
            var k = this.keys[i];
            fn(k, this.data[k], i);
        }
    };

    /**
     * 获取键值数组(类似Java的entrySet())
     * @return 键值对象{key,value}的数组
     */
    this.entrys = function(){
        var len = this.keys.length;
        var entrys = new Array(len);
        for(var i = 0; i < len; i++){
            entrys[i] = {
                key: this.keys[i],
                value: this.data[i]
            };
        }
        return entrys;
    };

    /**
     * 判断Map是否为空
     */
    this.isEmpty = function(){
        return this.keys.length == 0;
    };

    /**
     * 获取键值对数量
     */
    this.size = function(){
        return this.keys.length;
    };

    /**
     * 重写toString
     */
    this.toString = function(){
        var s = "{";
        for(var i = 0; i < this.keys.length; i++, s += ','){
            var k = this.keys[i];
            s += k + "=" + this.data[k];
        }
        s += "}";
        return s;
    };
}


/*
 function testMap(){
 var m = new Map();
 m.put('key1', 'Comtop');
 m.put('key2', '南方电网');
 m.put('key3', '景新花园');
 alert("init:" + m);

 m.put('key1', '康拓普');
 alert("set key1:" + m);

 m.remove("key2");
 alert("remove key2: " + m);

 var s = "";
 m.each(function(key, value, index){
 s += index + ":" + key + "=" + value + "/n";
 });
 alert(s);
 }*/
