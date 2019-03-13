/**
 *
 * Created by isaac_gu on 2016/5/17.
 */
var timer = null;
$(function(){
    $(".le-tabs").tabs();

    $(".dropdown-ul").hover(function(){
        clearTimeout(timer);
        $(".dropdown-ul").removeClass("open");
        $(this).addClass("open");
    }, function(){
        var _this = $(this);
        clearTimeout(timer);
        timer = setTimeout(function(){
            _this.removeClass("open");
        }, 500);
    });

});
