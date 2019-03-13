/**
 * Created by guzm on 2015/7/31.
 * 这个插件主要是仿照weibo评论
 * 主要的功能：
 * 1. 输入框(textarea)跟随用户的输入自动改变大小（自动增加高度）；
 * 2. 当光标离开输入框(textarea)时，输入框变回原高度；
 * 3. 当光标再次聚焦时，显示完整的输入框高度
 */
$.fn.extend({
    'slideTextarea': function(){
        $(this).each(function(){
            //获得对象
            var _ta = $(this)[0];
            _ta.setAttribute("_ta-height", _ta.style.height);
            //绑定监听事件，当折行时自动增加textarea的高度
            _ta.onfocus = _ta.onpropertychange = _ta.oninput = function(){
                this.style.height = (this.scrollHeight + 1) + 'px';
            };
            _ta.onblur = function(){
                this.style.height = this.getAttribute("_ta-height");
            };
        });
    }
});

//下面是纯js方法写的，作为备份保留
/*function slideTextArea(_id){
 //获得对象
 var _ta = document.getElementById(_id);
 _ta.setAttribute("_ta-height", _ta.style.height);
 //绑定监听事件，当折行时自动增加textarea的高度
 _ta.onfocus = _ta.onpropertychange = _ta.oninput = function(){
 this.style.height = (this.scrollHeight + 1) + 'px';
 }
 _ta.onblur = function(){
 this.style.height = this.getAttribute("_ta-height");
 }
 }*/