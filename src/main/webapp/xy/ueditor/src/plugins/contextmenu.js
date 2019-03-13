///import core
///commands 右键菜单
///commandsName  ContextMenu
///commandsTitle  右键菜单
/**
 * 右键菜单
 * @function
 * @name baidu.editor.plugins.contextmenu
 * @author zhanyi
 */
UE.plugins['contextmenu'] = function () {
    var me = this;
    me.setOpt('enableContextMenu',true);
    if(me.getOpt('enableContextMenu') === false){
        return;
    }
    var lang = me.getLang( "contextMenu" ),
        menu,
        items = me.options.contextMenu || [
                {label:lang['selectall'], cmdName:'selectall'},

                {
                    label:lang.cleardoc,
                    cmdName:'cleardoc',
                    exec:function () {
                        if ( confirm( lang.confirmclear ) ) {
                            this.execCommand( 'cleardoc' );
                        }
                    }
                },
                {
                    group:lang.stringoperation,
                    icon:'stringoperation',
                    subMenu:[
                        {
                            label:lang['quickinsertion'],
                            cmdName:'quickinsertion'
                        },
                        {
                            label:lang['name1'],
                            cmdName:'name1'
                        },
                        {
                            label:lang['name2'],
                            cmdName:'name2'
                        },
                        {
                            label:lang['name3'],
                            cmdName:'name3'
                        },
                        {
                            label:lang['name4'],
                            cmdName:'name4'
                        },
                        {
                            label:lang['name5'],
                            cmdName:'name5'
                        },
                    ]
                },
                /*{
                    label:lang['quickinsertion'],
                    cmdName:'quickinsertion'
                },
                {
                    label:lang['name1'],
                    cmdName:'name1'
                },
                {
                    label:lang['name2'],
                    cmdName:'name2'
                },
                {
                    label:lang['name3'],
                    cmdName:'name3'
                },
                {
                    label:lang['name4'],
                    cmdName:'name4'
                },
                {
                    label:lang['name5'],
                    cmdName:'name5'
                },*/
                {
                    label:lang['saveas'],
                    cmdName:'saveas'
                },
                '-',
                {
                    label:lang['copy'],
                    cmdName:'copy'
                },
                {
                    label:lang['paste'],
                    cmdName:'paste'
                },
                {
                    label:lang['delete'],
                    cmdName:'delete'
                },
                '-',
                {
                    group:lang.gettextset,
                    icon:'gettextset',
                    subMenu:[
                        {
                            label:lang['extracttitle'],
                            cmdName:'extracttitle'
                        },
                        {
                            label:lang['extractsubtitle'],
                            cmdName:'extractsubtitle'
                        },
                        {
                            label:lang['extractauthor'],
                            cmdName:'extractauthor'
                        },
                        {
                            label:lang['extractlinktitle'],
                            cmdName:'extractlinktitle'
                        },
                        {
                            label:lang['extractabstract'],
                            cmdName:'extractabstract'
                        },
                        {
                            label:lang['extractkey'],
                            cmdName:'extractkey'
                        }
                    ]
                },
                '-',
                {
                    group:lang.paragraph,
                    icon:'justifyjustify',
                    subMenu:[
                        {
                            label:lang.justifyleft,
                            cmdName:'justify',
                            value:'left'
                        },
                        {
                            label:lang.justifyright,
                            cmdName:'justify',
                            value:'right'
                        },
                        {
                            label:lang.justifycenter,
                            cmdName:'justify',
                            value:'center'
                        },
                        {
                            label:lang.justifyjustify,
                            cmdName:'justify',
                            value:'justify'
                        }
                    ]
                },
                '-',
                {
                    group:lang.imagefloat,
                    icon:'simpleupload',
                    subMenu:[
                        {
                            label:lang.imageleft,
                            cmdName:'imagefloat',
                            value:'left'
                        },
                        {
                            label:lang.imageright,
                            cmdName:'imagefloat',
                            value:'right'
                        },
                        {
                            label:lang.imagecenter,
                            cmdName:'imagefloat',
                            value:'center'
                        },
                        {
                            label:lang.imagenone,
                            cmdName:'imagefloat',
                            value:'none'
                        },
                        {
                            label:lang.imagemodify,
                            cmdName:'insertimage',
                            exec:function () {
                                this.getDialog("insertimage").open();
                            }
                        },
                        {
                            label:lang.imagecrop,
                            cmdName:'snapscreen',
                            value:'imagecrop'
                        },
                        {
                            label:lang.titleImg,
                            cmdName:'snapscreen',
                            value: 'titleImg'

                        }
                    ]
                },
                '-',
                {
                    group:lang.table,
                    icon:'table',
                    subMenu:[
                        {
                            label:lang.inserttable,
                            cmdName:'inserttable'
                        },
                        {
                            label:lang.deletetable,
                            cmdName:'deletetable'
                        },
                        '-',
                        {
                            label:lang.deleterow,
                            cmdName:'deleterow'
                        },
                        {
                            label:lang.deletecol,
                            cmdName:'deletecol'
                        },
                        {
                            label:lang.insertcol,
                            cmdName:'insertcol'
                        },
                        {
                            label:lang.insertcolnext,
                            cmdName:'insertcolnext'
                        },
                        {
                            label:lang.insertrow,
                            cmdName:'insertrow'
                        },
                        {
                            label:lang.insertrownext,
                            cmdName:'insertrownext'
                        },
                        '-',
                        {
                            label:lang.insertcaption,
                            cmdName:'insertcaption'
                        },
                        {
                            label:lang.deletecaption,
                            cmdName:'deletecaption'
                        },
                        {
                            label:lang.inserttitle,
                            cmdName:'inserttitle'
                        },
                        {
                            label:lang.deletetitle,
                            cmdName:'deletetitle'
                        },
                        {
                            label:lang.inserttitlecol,
                            cmdName:'inserttitlecol'
                        },
                        {
                            label:lang.deletetitlecol,
                            cmdName:'deletetitlecol'
                        },
                        '-',
                        {
                            label:lang.mergecells,
                            cmdName:'mergecells'
                        },
                        {
                            label:lang.mergeright,
                            cmdName:'mergeright'
                        },
                        {
                            label:lang.mergedown,
                            cmdName:'mergedown'
                        },
                        '-',
                        {
                            label:lang.splittorows,
                            cmdName:'splittorows'
                        },
                        {
                            label:lang.splittocols,
                            cmdName:'splittocols'
                        },
                        {
                            label:lang.splittocells,
                            cmdName:'splittocells'
                        },
                        '-',
                        {
                            label:lang.averageDiseRow,
                            cmdName:'averagedistributerow'
                        },
                        {
                            label:lang.averageDisCol,
                            cmdName:'averagedistributecol'
                        },
                        '-',
                        {
                            label:lang.edittd,
                            cmdName:'edittd',
                            exec:function () {
                                if ( UE.ui['edittd'] ) {
                                    new UE.ui['edittd']( this );
                                }
                                this.getDialog('edittd').open();
                            }
                        },
                        {
                            label:lang.edittable,
                            cmdName:'edittable',
                            exec:function () {
                                if ( UE.ui['edittable'] ) {
                                    new UE.ui['edittable']( this );
                                }
                                this.getDialog('edittable').open();
                            }
                        },
                        {
                            label:lang.setbordervisible,
                            cmdName:'setbordervisible'
                        }
                    ]
                },
                {
                    group:lang.tablesort,
                    icon:'tablesort',
                    subMenu:[
                        {
                            label:lang.enablesort,
                            cmdName:'enablesort'
                        },
                        {
                            label:lang.disablesort,
                            cmdName:'disablesort'
                        },
                        '-',
                        {
                            label:lang.reversecurrent,
                            cmdName:'sorttable',
                            value:'reversecurrent'
                        },
                        {
                            label:lang.orderbyasc,
                            cmdName:'sorttable',
                            value:'orderbyasc'
                        },
                        {
                            label:lang.reversebyasc,
                            cmdName:'sorttable',
                            value:'reversebyasc'
                        },
                        {
                            label:lang.orderbynum,
                            cmdName:'sorttable',
                            value:'orderbynum'
                        },
                        {
                            label:lang.reversebynum,
                            cmdName:'sorttable',
                            value:'reversebynum'
                        }
                    ]
                },

                {
                    group:lang.borderbk,
                    icon:'borderBack',
                    subMenu:[
                        {
                            label:lang.setcolor,
                            cmdName:"interlacetable",
                            exec:function(){
                                this.execCommand("interlacetable");
                            }
                        },
                        {
                            label:lang.unsetcolor,
                            cmdName:"uninterlacetable",
                            exec:function(){
                                this.execCommand("uninterlacetable");
                            }
                        },
                        {
                            label:lang.setbackground,
                            cmdName:"settablebackground",
                            exec:function(){
                                this.execCommand("settablebackground",{repeat:true,colorList:["#bbb","#ccc"]});
                            }
                        },
                        {
                            label:lang.unsetbackground,
                            cmdName:"cleartablebackground",
                            exec:function(){
                                this.execCommand("cleartablebackground");
                            }
                        },
                        {
                            label:lang.redandblue,
                            cmdName:"settablebackground",
                            exec:function(){
                                this.execCommand("settablebackground",{repeat:true,colorList:["red","blue"]});
                            }
                        },
                        {
                            label:lang.threecolorgradient,
                            cmdName:"settablebackground",
                            exec:function(){
                                this.execCommand("settablebackground",{repeat:true,colorList:["#aaa","#bbb","#ccc"]});
                            }
                        }
                    ]
                },
                {
                    group:lang.aligntd,
                    icon:'aligntd',
                    subMenu:[
                        {
                            cmdName:'cellalignment',
                            value:{align:'left',vAlign:'top'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'center',vAlign:'top'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'right',vAlign:'top'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'left',vAlign:'middle'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'center',vAlign:'middle'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'right',vAlign:'middle'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'left',vAlign:'bottom'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'center',vAlign:'bottom'}
                        },
                        {
                            cmdName:'cellalignment',
                            value:{align:'right',vAlign:'bottom'}
                        }
                    ]
                },
                {
                    group:lang.aligntable,
                    icon:'aligntable',
                    subMenu:[
                        {
                            cmdName:'tablealignment',
                            className: 'left',
                            label:lang.tableleft,
                            value:"left"
                        },
                        {
                            cmdName:'tablealignment',
                            className: 'center',
                            label:lang.tablecenter,
                            value:"center"
                        },
                        {
                            cmdName:'tablealignment',
                            className: 'right',
                            label:lang.tableright,
                            value:"right"
                        }
                    ]
                },
                '-',
                {
                    label:lang.link,
                    cmdName:'link',
                    exec:function () {
                        this.getDialog("link").open();
                    }
                },
                {
                    label:lang.unlink,
                    cmdName:'unlink'
                },
                '-',
                {
                    label:lang.insertparagraphbefore,
                    cmdName:'insertparagraph',
                    value:true
                },
                {
                    label:lang.insertparagraphafter,
                    cmdName:'insertparagraph'
                },
                '-',
                {
                    label:lang.pagebreak,
                    cmdName:'pagebreak'
                },
                '-',
                {
                    label:lang.comment_xy,
                    cmdName:'comment_xy'
                }
            ];
    if ( !items.length ) {
        return;
    }
    var uiUtils = UE.ui.uiUtils;

    me.addListener( 'contextmenu', function ( type, evt ) {
        //获得边界位置
        var offset = uiUtils.getViewportOffsetByEvent( evt );
        //
        me.fireEvent( 'beforeselectionchange' );
        //如果有menu，销毁
        if ( menu ) {
            menu.destroy();
        }
        //循环1 - 列表
        for ( var i = 0, ti, contextItems = []; ti = items[i]; i++ ) {
            var last;
            (function ( item ) {
                //是 -
                if ( item == '-' ) {
                    //是最后一个的话，压一个 -
                    if ( (last = contextItems[contextItems.length - 1 ] ) && last !== '-' ) {
                        contextItems.push( '-' );
                    }
                }
                //有二级目录的话
                else if ( item.hasOwnProperty( "group" ) ) {
                    //子菜单 - 二级菜单 子菜单
                    for ( var j = 0, cj, subMenu = []; cj = item.subMenu[j]; j++ ) {
                        (function ( subItem ) {
                            //如果子菜单是 -
                            if ( subItem == '-' ) {
                                //
                                if ( (last = subMenu[subMenu.length - 1 ] ) && last !== '-' ) {
                                    subMenu.push( '-' );
                                }else{
                                    subMenu.splice(subMenu.length-1);
                                }
                            } else {//二级菜单选项
                                //有一些菜单没显示，是因为me.queryCommandState( subItem.cmdName )) > -1 为false
                                if ( (me.commands[subItem.cmdName] || UE.commands[subItem.cmdName] || subItem.query) &&
                                    (subItem.query ? subItem.query() : me.queryCommandState( subItem.cmdName )) > -1 ) {
                                    subMenu.push( {
                                        'label':subItem.label || me.getLang( "contextMenu." + subItem.cmdName + (subItem.value || '') )||"",
                                        'className':'edui-for-' +subItem.cmdName + ( subItem.className ? ( ' edui-for-' + subItem.cmdName + '-' + subItem.className ) : '' ),
                                        onclick:subItem.exec ? function () {
                                            subItem.exec.call( me );
                                        } : function () {
                                            me.execCommand( subItem.cmdName, subItem.value );
                                        }
                                    } );
                                }
                            }
                        })( cj );
                    }//子菜单 - 二级菜单 子菜单 END;
                    if ( subMenu.length ) {
                        function getLabel(){
                            switch (item.icon){
                                case "table":
                                    return me.getLang( "contextMenu.table" );
                                case "justifyjustify":
                                    return me.getLang( "contextMenu.paragraph" );
                                case "simpleupload":
                                    return me.getLang( "contextMenu.imagefloat" );
                                case "aligntd":
                                    return me.getLang("contextMenu.aligntd");
                                case "aligntable":
                                    return me.getLang("contextMenu.aligntable");
                                case "tablesort":
                                    return lang.tablesort;
                                case "borderBack":
                                    return lang.borderbk;
                                case "gettextset":
                                    return me.getLang( "contextMenu.gettextset" );
                                case "stringoperation":
                                    return me.getLang( "contextMenu.stringoperation" );
                                default :
                                    return '';
                            }
                        }
                        contextItems.push( {
                            //todo 修正成自动获取方式
                            'label':getLabel(),
                            className:'edui-for-' + item.icon,
                            'subMenu':{
                                items:subMenu,
                                editor:me
                            }
                        } );
                    }

                } else {//没有子菜单的一级菜单
                    //有可能commmand没有加载右键不能出来，或者没有command也想能展示出来添加query方法

                    if ( (me.commands[item.cmdName] || UE.commands[item.cmdName] || item.query) &&
                        (item.query ? item.query.call(me) : me.queryCommandState( item.cmdName )) > -1 ) {

                        contextItems.push( {
                            'label':item.label || me.getLang( "contextMenu." + item.cmdName ),
                            className:'edui-for-' + (item.icon ? item.icon : item.cmdName + (item.value || '')),
                            onclick:item.exec ? function () {
                                item.exec.call( me );
                            } : function () {
                                me.execCommand( item.cmdName, item.value );
                            }
                        } );
                    }

                }

            })( ti );
        }//循环1 END
        if ( contextItems[contextItems.length - 1] == '-' ) {
            contextItems.pop();
        }

        //创建 menu对象
        menu = new UE.ui.Menu( {
            items:contextItems,
            className:"edui-contextmenu",
            editor:me
        } );
        menu.render();
        menu.showAt( offset );

        me.fireEvent("aftershowcontextmenu",menu);

        domUtils.preventDefault( evt );
        if ( browser.ie ) {
            var ieRange;
            try {
                ieRange = me.selection.getNative().createRange();
            } catch ( e ) {
                return;
            }
            if ( ieRange.item ) {
                var range = new dom.Range( me.document );
                range.selectNode( ieRange.item( 0 ) ).select( true, true );
            }
        }
    });

    // 添加复制的flash按钮
    me.addListener('aftershowcontextmenu', function(type, menu) {
        if (me.zeroclipboard) {
            var items = menu.items;
            for (var key in items) {
                if (items[key].className == 'edui-for-copy') {
                    me.zeroclipboard.clip(items[key].getDom());
                }
            }
        }
    });
};
/*
setTimeout(function(){
    document.getElementById('ueditor_0').contentWindow.document.onmouseup= function (evt,el) {
        var txt = funcGetSelectText();
        if(txt)
        {
          // alert(txt); 执行鼠标选择之后的处理事件
        }
    }
},2000);

function funcGetSelectText(){
    var txt = '';
    if(document.getElementById('ueditor_0').contentWindow.document.selection){
        txt = document.getElementById('ueditor_0').contentWindow.document.selection.createRange().text;//ie
    }else{
        txt = document.getElementById('ueditor_0').contentWindow.document.getSelection();
    }
    return txt.toString();
}*/
