 UE.registerUI('insertlink',function(editor,uiName){


    //创建dialog
    var insertlinkdialog = new UE.ui.Dialog({
        //指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
        iframeUrl:'dialogs/insertlink/code.html',
        //需要指定当前的编辑器实例
        editor:editor,
        //指定dialog的名字
        name:'insertlink',
        //dialog的标题
        title:"插入代码002",

        //指定dialog的外围样式
        cssRules:"width:1000px;height:572px;",

        //如果给出了buttons就代表dialog有确定和取消
        buttons:[
            {
                className:'edui-okbutton',
                label:'确定',
                onclick:function () {
                    insertlinkdialog.onok();
                    insertlinkdialog.close(true);

                }
            },
            {
                className:'edui-cancelbutton',
                label:'取消',
                onclick:function () {
                    insertlinkdialog.oncancel();
                    insertlinkdialog.close(false);

                }
            }
        ]});

    //参考addCustomizeButton.js
    var btn = new UE.ui.Button({
        name:'insertlink' ,
        title:'insertlink',
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
        cssRules :'background-position: -500px 0;',
        onclick:function () {
            //渲染dialog
            insertlinkdialog.render();
            insertlinkdialog.open();
        }
    });
        // editor.addListener('selectionchange', function () {
        //     var state = editor.queryCommandState(uiName);
        //     if (state == -1) {
        //         btn.setDisabled(true);
        //         btn.setChecked(false);
        //     } else {
        //         btn.setDisabled(false);
        //         btn.setChecked(state);
        //     }
        // });
     insertlinkdialog.onok = function () {
            if ($("#code-textarea").text().value != ""){

                UE.focus().html($("#code-textarea").text().value);
            }
        };
     insertlinkdialog.oncancel = function () {
            $("#code-textarea").text('');
        };
    return btn;



});/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/
