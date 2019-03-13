/**
 * Created by isaac_gu on 2016/1/5.
 */
$(function(){
    new LayoutEditor().init();
    //调整格式
    $("body").css("min-height", $(window).height() );

    /**
     * 初始化拖动 - 布局格
     * 只能能拖动到demo中及 column中
     */
    /*$("#toolDiv").find(".lyrow").draggable({
        connectToSortable: "#demoDiv",		//拖动到demo 的div上
        helper: "clone",	//拖动时显示的效果
        handle: ".preview",	//置顶拖动的目标
        start: function(event,ui) {
        },
        drag: function(event, ui) {
            ui.helper.width(400);
        },
        stop: function(event, ui) {
            var _$this = $(this);
            var _viewHtml =
            console.info(_$this.html());

            $(".demo .column").sortable({
                connectWith: ".column",
                opacity: .35,
                start: function(event,ui) {
                },
                stop: function(event,ui) {
                }
            });
        }
    });
    /!**
     * 跟其他column
     *!/
    $("#demoDiv, #demoDiv .column").sortable({
        connectWith: ".column",
        opacity: .35,
        handle: ".move",
        start: function(event,ui) {
        },
        stop: function(event,ui) {
        }
    });

    $("#toolDiv").find(".plugin").draggable({
        connectToSortable: ".column",
        helper: "clone",
        handle: ".preview,.move",
        start: function(event,ui) {
        },
        drag: function(event, ui) {
            ui.helper.width(400)
        },
        stop: function() {
            //handleJsIds();
        }
    });
    /!**
     * 画板下的 删除
     *!/
    $(".demo").on("click", ".remove",  function(event) {
        event.preventDefault();
        $(this).parent().parent().remove();
        if (!$(".demo .lyrow").length > 0) {
            //clearDemo()
        }
    })*/
});
