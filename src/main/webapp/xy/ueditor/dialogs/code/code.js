/**
 * Created by caicai on 2016/10/24.
 */
(function () {
    parent.document.all("edui181_iframe").style.height=document.body.scrollHeight;

    parent.document.all("edui181_iframe").style.width=document.body.scrollWidth;

    var code = {},
        uploadCodeList = [],
        isModifyUploadCode = false,
        uploadFile;
    var onlineImage;

    window.onload = function () {
        //聚焦在input上
        $focus($G("codeUrl"));
        //初始化各个
        initTabs();
        initCode();
        //initUpload();
    };
    function initTabs() {
        //获得所有的标签
        var tabs = $G('tabHeads').children;
        //遍历每个标签，并添加点击事件
        for (var i = 0; i < tabs.length; i++) {
            domUtils.on(tabs[i], "click", function (e) {
                var j, bodyId, target = e.target || e.srcElement;
                for (j = 0; j < tabs.length; j++) {
                    //获得自己的id
                    bodyId = tabs[j].getAttribute('data-content-id');
                    //添加样式
                    if (tabs[j] == target) {
                        domUtils.addClass(tabs[j], 'focus');
                        domUtils.addClass($G(bodyId), 'focus');
                    } else {
                        domUtils.removeClasses(tabs[j], 'focus');
                        domUtils.removeClasses($G(bodyId), 'focus');
                    }
                }
                //点击时重新加载视频库界面，使刷新，且高度定位正确
                var id = target.getAttribute('data-content-id');
                if (id == "videoLib") {
                    onlineImage = onlineImage || new OnlineImage('imageList');
                }
            });
        }
    }

    function initCode() {
        //创建浮动按钮
        createAlignButton(["codeFloat", "upload_alignment"]);
       // addUrlChangeListener($G("codeUrl"));
        addOkListener();
    }

    /**
     * 监听确认和取消两个按钮事件，用户执行插入或者清空正在插入代码实例操作
     */
    function addOkListener() {
        dialog.onok = function () {
            // $G("#code-textarea").innerHTML = "";

            $(".view").html($("#code-textarea").text());
        };
        dialog.oncancel = function () {
            $("#code-textarea").text('');
        };
    }

    function createAlignButton(ids) {
        for (var i = 0, ci; ci = ids[i++];) {
            var floatContainer = $G(ci),
                nameMaps = {
                    "none": lang['default'],
                    "left": lang.floatLeft,
                    "right": lang.floatRight,
                    "center": lang.block
                };
            for (var j in nameMaps) {
                var div = document.createElement("div");
                div.setAttribute("name", j);
                if (j == "none") div.className = "focus";
                div.style.cssText = "background:url(images/" + j + "_focus.jpg);";
                div.setAttribute("title", nameMaps[j]);
                floatContainer.appendChild(div);
            }
            switchSelect(ci);
        }
    }

    function switchSelect(selectParentId) {
        var selects = $G(selectParentId).children;
        for (var i = 0, ci; ci = selects[i++];) {
            domUtils.on(ci, "click", function () {
                for (var j = 0, cj; cj = selects[j++];) {
                    cj.className = "";
                    cj.removeAttribute && cj.removeAttribute("class");
                }
                this.className = "focus";
            })
        }
    }

    $(function(){

        $("#edui185").click(function(){

            $("#edui181_body").height(572).width(769);
            $("#edui181_buttons").height(500).width(760);

        })

    });
    var codeDialog = new UE.ui.Dialog({

        // 指定弹出层路径
        iframeUrl: editor + 'dialogs/code/code.html',
        // 编辑器实例
        editor: editor,
        // dialog 名称
        name: uiname,
        // dialog 标题
        title: '插入代码',

        // dialog 外围 css
        cssRules: 'width:783px; height: 386px;',

        //如果给出了buttons就代表dialog有确定和取消
        buttons:[
            {
                className:'edui-okbutton',
                label:'确定',
                onclick:function () {
                    codeDialog.close(true);
                }
            },
            {
                className:'edui-cancelbutton',
                label:'取消',
                onclick:function () {
                    codeDialog.close(false);
                }
            }
        ]});
    editor.ready(function(){
        UE.utils.cssRule('insertcode', 'img.insertcode{vertical-align: middle;}', editor.document);
    });

})();