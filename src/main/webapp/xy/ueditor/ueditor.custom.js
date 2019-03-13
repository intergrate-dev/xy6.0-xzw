/*!
 * UEditor
 * version: ueditor
 * build:2014.9.15
 */
/**
 * @description 提取标题
 * @author 001 LJS
 */

/**
* 全角半角转换
*/
/**
 * time:2015/06/11
 * description: 半角转换成全角
 * @author 001 LJS
 */
UE.commands['halftofull_char'] = {
	execCommand: function (cmd) {
		var dom = UE.dom,
			domUtils = UE.dom.domUtils,
			utils = UE.utils,
			browser = UE.browser;
		var cont = this.document.body;
		var nodes = domUtils.getElementsByTagName(cont, '*');
		if (cmd) {
			var root = UE.htmlparser(cont.innerHTML);
			root.traversal(function (node) {
				if (node.type == 'text') {
					node.data = ToDBC(node.data)
				}
			});
			cont.innerHTML = root.toHtml()

		}
		function ToDBC(txtstring) {
			txtstring = utils.html(txtstring);
			var tmp = "";
			var mark = "";
			/*用于判断,如果是html尖括里的标记,则不进行全角的转换*/
			for (var i = 0; i < txtstring.length; i++) {
				if (txtstring.charCodeAt(i) == 32) {
					tmp = tmp + String.fromCharCode(12288);
				}
				else if (txtstring.charCodeAt(i) < 127 && !(txtstring.charCodeAt(i) >= 48 && txtstring.charCodeAt(i) <= 57)) {
					tmp = tmp + String.fromCharCode(txtstring.charCodeAt(i) + 65248);
				}
				else {
					tmp += txtstring.charAt(i);
				}
			}
			return tmp;
		}
	},
	queryCommandState: function () {
		return this.highlight ? -1 : 0;
	}
};

/**
 * time:2015/06/11
 * description: 全角转换成半角
 * @author 001 LJS
 */
UE.commands['fulltohalf_char'] = {
	execCommand: function (cmd) {
		var dom = UE.dom,
			domUtils = UE.dom.domUtils,
			utils = UE.utils,
			browser = UE.browser;
		var cont = this.document.body;
		var nodes = domUtils.getElementsByTagName(cont, '*');
		if (cmd) {
			var root = UE.htmlparser(cont.innerHTML);
			root.traversal(function (node) {
				if (node.type == 'text') {
					node.data = DBC2SB(node.data)
				}
			});
			cont.innerHTML = root.toHtml()

		}
		function DBC2SB(str) {
			var result = '';
			for (var i = 0; i < str.length; i++) {
				var code = str.charCodeAt(i); //获取当前字符的unicode编码
				if (code >= 65281 && code <= 65373 && !(code >= 65296 && code <= 65305) )//在这个unicode编码范围中的是所有的英文字母已经各种字符
				{
					result += String.fromCharCode(str.charCodeAt(i) - 65248); //把全角字符的unicode编码转换为对应半角字符的unicode码
				} else if (code == 12288)//空格
				{
					result += String.fromCharCode(str.charCodeAt(i) - 12288 + 32);
				} else {
					result += str.charAt(i);
				}
			}
			return result;
		}
	},
	queryCommandState: function () {
		return this.highlight ? -1 : 0;
	}
};

/**
 * time:2015/06/11
 * description: 半角转换成全角
 * @author 001 LJS
 */
UE.commands['halftofull_num'] = {
	execCommand: function (cmd) {

		var dom = UE.dom,
			domUtils = UE.dom.domUtils,
			utils = UE.utils,
			browser = UE.browser;
		var cont = this.document.body;
		var nodes = domUtils.getElementsByTagName(cont, '*');
		if (cmd) {
			var root = UE.htmlparser(cont.innerHTML);
			root.traversal(function (node) {
				if (node.type == 'text') {
					node.data = ToDBC(node.data)
				}
			});
			cont.innerHTML = root.toHtml()

		}
		function ToDBC(txtstring) {
			txtstring = utils.html(txtstring);
			var tmp = "";
			var mark = "";
			/*用于判断,如果是html尖括里的标记,则不进行全角的转换*/
			for (var i = 0; i < txtstring.length; i++) {
				if (txtstring.charCodeAt(i) == 32) {
					tmp = tmp + String.fromCharCode(12288);
				}
				else if (txtstring.charCodeAt(i) >= 48 && txtstring.charCodeAt(i) <= 57 ) {
					tmp = tmp + String.fromCharCode(txtstring.charCodeAt(i) + 65248);
				}
				else {
					tmp += txtstring.charAt(i);
				}
			}
			return tmp;
		}
	},
	queryCommandState: function () {
		return this.highlight ? -1 : 0;
	}
};

/**
 * time:2015/06/11
 * description: 全角转换成半角
 * @author 001 LJS
 */
UE.commands['fulltohalf_num'] = {
	execCommand: function (cmd) {

		var dom = UE.dom,
			domUtils = UE.dom.domUtils,
			utils = UE.utils,
			browser = UE.browser;
		var cont = this.document.body;
		var nodes = domUtils.getElementsByTagName(cont, '*');
		if (cmd) {
			var root = UE.htmlparser(cont.innerHTML);
			root.traversal(function (node) {
				if (node.type == 'text') {
					node.data = DBC2SB(node.data)
				}
			});
			cont.innerHTML = root.toHtml()

		}
		function DBC2SB(str) {
			var result = '';
			for (var i = 0; i < str.length; i++) {
				var code = str.charCodeAt(i); //获取当前字符的unicode编码
				if (code >= 65296 && code <= 65305)//在这个unicode编码范围中的是所有的英文字母已经各种字符
				{
					result += String.fromCharCode(str.charCodeAt(i) - 65248); //把全角字符的unicode编码转换为对应半角字符的unicode码
				} else if (code == 12288)//空格
				{
					result += String.fromCharCode(str.charCodeAt(i) - 12288 + 32);
				} else {
					result += str.charAt(i);
				}
			}
			return result;
		}
	},
	queryCommandState: function () {
		return this.highlight ? -1 : 0;
	}
};



///import core
///commands 删除
///commandsName  Delete
///commandsTitle  删除
/**
* 删除
* @function
* @name baidu.editor.execCommand
* @param  {String}    cmdName    delete删除
* @author LJS
*/

UE.commands['delete'] = {
  execCommand : function (){
	  var dom = UE.dom,
	      dtd = UE.dom.dtd,
          domUtils = UE.dom.domUtils,
          utils = UE.utils,
          browser = UE.browser;
      var range = this.selection.getRange(),
          mStart = 0,
          mEnd = 0,
          me = this;
      if(this.selectAll ){
          //trace:1633
          me.body.innerHTML = '<p>'+(browser.ie ? '&nbsp;' : '<br/>')+'</p>';

          range.setStart(me.body.firstChild,0).setCursor(false,true);

          me.selectAll = false;
          return;
      }
      if(me.currentSelectedArr && me.currentSelectedArr.length > 0){
          for(var i=0,ci;ci=me.currentSelectedArr[i++];){
              if(ci.style.display != 'none'){
                  ci.innerHTML = browser.ie ? domUtils.fillChar : '<br/>';
              }

          }
          range.setStart(me.currentSelectedArr[0],0).setCursor();
          return;
      }
      if(range.collapsed){
          return;
      }
      range.txtToElmBoundary();
      //&& !domUtils.isBlockElm(range.startContainer)
      while(!range.startOffset &&  !domUtils.isBody(range.startContainer) &&  !dtd.$tableContent[range.startContainer.tagName] ){
          mStart = 1;
          range.setStartBefore(range.startContainer);
      }
      //&& !domUtils.isBlockElm(range.endContainer)
      //不对文本节点进行操作
      //trace:2428
      while(range.endContainer.nodeType != 3 && !domUtils.isBody(range.endContainer)&&  !dtd.$tableContent[range.endContainer.tagName]  ){
          var child,endContainer = range.endContainer,endOffset = range.endOffset;
//              if(endContainer.nodeType == 3 &&  endOffset == endContainer.nodeValue.length){
//                  range.setEndAfter(endContainer);
//                  continue;
//              }

          child = endContainer.childNodes[endOffset];
          if(!child || domUtils.isBr(child) && endContainer.lastChild === child){
              range.setEndAfter(endContainer);
              continue;
          }
          break;

      }
      if(mStart){
          var start = me.document.createElement('span');
          start.innerHTML = 'start';
          start.id = '_baidu_cut_start';
          range.insertNode(start).setStartBefore(start);
      }
      if(mEnd){
          var end = me.document.createElement('span');
          end.innerHTML = 'end';
          end.id = '_baidu_cut_end';
          range.cloneRange().collapse(false).insertNode(end);
          range.setEndAfter(end);

      }



      range.deleteContents();


      if(domUtils.isBody(range.startContainer) && domUtils.isEmptyBlock(me.body)){
          me.body.innerHTML = '<p>'+(browser.ie?'':'<br/>')+'</p>';
          range.setStart(me.body.firstChild,0).collapse(true);
      }else if ( !browser.ie && domUtils.isEmptyBlock(range.startContainer)){
          range.startContainer.innerHTML = '<br/>';
      }

      range.select(true);
  },
  queryCommandState : function(){

      if(this.currentSelectedArr && this.currentSelectedArr.length > 0){
          return 0;
      }
      return this.highlight || this.selection.getRange().collapsed ? -1 : 0;
  }
};

/**
 * @description 保存
 * @author fzf
 */
UE.commands['save'] = {
	execCommand : function(cmd) {
		var editor = UE.getEditor('editor');

        //var contentTxt = editor.getContent();
		// if(contentTxt.indexOf('img src="http')!=-1){
         //    $('div[title="批量下载图片"]').trigger('click');
		// }
        savaDraft(editor);
	}
};
/**
 * @description字符串
 * @author hhr
 */
function SetCookie(sName, sValue){
	date = new Date();
	document.cookie = sName + "=" + sValue;
}
function GetCookie(sName){
	var aCookie = document.cookie.split("; ");
	for (var i=0; i < aCookie.length; i++)
	{
		var aCrumb = aCookie[i].split("=");
		if (sName == aCrumb[0])
			return aCrumb[1];
	}

	return null;
}


var namesss='{"namesss":"xiaoming","name2":"name2","name3":"name3","name4":"name4","name5":"name5","name6":"name6"}'
SetCookie("namesss",namesss);



UE.commands['name1'] = {
	execCommand : function(cmd) {
		//var allObj=JSON.parse(GetCookie("namesss"));
		var name1Val= localStorage.getItem("quickNotesOneK");
		var me = this;
		var range = me.selection.getRange();
		range.collapse(false);
		/*//cookie中读取值
		var cookieKey=UE.I18N['zh-cn'].contextMenu['name1']
		//获取整个cookie对象
		//var cookieObj=JSON.parse(GetCookie("namesss"));
*/
		//var inserText=allObj[cookieKey];
		me.execCommand("inserthtml", name1Val)

	}/*,
	queryCommandState:function () {
		return 0;
	}*/
};
/**
 * @description字符串
 * @author hhr
 */
UE.commands['name2'] = {
	execCommand : function(cmd) {
		var name1Val= localStorage.getItem("quickNotesTwoK");
		var me = this;
		var range = me.selection.getRange();
		range.collapse(false);
		me.execCommand("inserthtml", name1Val)

	}/*,
	 queryCommandState:function () {
	 return 0;
	 }*/
};
/**
 * @description字符串
 * @author hhr
 */
UE.commands['name3'] = {
	execCommand : function(cmd) {
		var name1Val=localStorage.getItem("quickNotesTrdK");
		var me = this;
		var range = me.selection.getRange();
		range.collapse(false);
		me.execCommand("inserthtml", name1Val)

	}/*,
	 queryCommandState:function () {
	 return 0;
	 }*/
};
/**
 * @description字符串
 * @author hhr
 */
UE.commands['name4'] = {
	execCommand : function(cmd) {
		var name1Val=localStorage.getItem("quickNotesFouK");
		var me = this;
		var range = me.selection.getRange();
		range.collapse(false);
		me.execCommand("inserthtml", name1Val)

	}/*,
	 queryCommandState:function () {
	 return 0;
	 }*/
};
/**
 * @description字符串
 * @author hhr
 */
UE.commands['name5'] = {
	execCommand : function(cmd) {
		var name1Val=localStorage.getItem("quickNotesFivK");
		var me = this;
		var range = me.selection.getRange();
		range.collapse(false);
		me.execCommand("inserthtml", name1Val)

	}/*,
	 queryCommandState:function () {
	 return 0;
	 }*/
};

//UE.commands['quickphrases'] = {
//	execCommand : function(cmd) {
//		this.getDialog("insertimage").open();
//		//this.getDialog("link").open();
//	}/*,
//	 queryCommandState:function () {
//	 return 0;
//	 }*/
//};

/**
 * @description 图片另存为
 * @author qxm
 */
UE.commands['saveas'] = {
    execCommand : function(cmd) {
        var editor = UE.getEditor('editor');

        var contentTxt = editor.getContent();

        var range = this.selection.getRange(),
            startNode;
        if (range.collapsed)  return ;

        startNode = range.getClosedNode();
        if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG') {
            var imagePathURL=startNode.src;
            var _type=imagePathURL.split('.');
            var len=_type.length;
            var type=_type[len-1];
            var triggerDownload = $("<a>").attr("href", imagePathURL).attr("download", "download"+type).appendTo("body");
            triggerDownload[0].click();
            triggerDownload.remove();
        }

    },
    queryCommandState:function () {
        var range = this.selection.getRange(),
            startNode;

        if (range.collapsed)  return -1;

        startNode = range.getClosedNode();
        if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG') {
            return 0;
        }
        return -1;
    }
};

/**
 * @description 草稿箱
 * @author yb
 * 生成草稿箱  并打开草稿执行的函数
 */
sureFun=null;
UE.plugins["drafts"] = function() {
    var me = this;
    function queryDraftList(editor1) {
        $.ajax({
            url: "../../xy/article/DraftList.do",
            dataType: "json",
            // async: false,
            success: function (data) {
            	// alert(123)
            	// console.log(data)
                $("#box tbody").empty();

                $("#box").removeClass('draftNone');

                for (var i = 0; i < data.drafts.length; i++) {
                    var aData = data.drafts[i];
                    var title = aData.title;
                    if(title.length > 24){
                        title =  title.substring(0,24) + '.....';
					}
                    $("#box tbody").append('<tr docID =' + aData.docID + '><td style="width: 113px;text-align: center;border-top: 0px;border-bottom: 1px solid #dddddd;">' + aData.lastModifided + '</td><td style="width: 350px;border-top: 0px;border-bottom: 1px solid #dddddd;padding: 0px;text-indent: 8px;line-height: 57px;">' + title + '</td></tr>')
                }

                $("#sure").removeClass('draftNone');

                $('#box tbody tr').on('click', function () {

                    $('#box tbody tr td').css({
                        'backgroundColor': ''
                    })
                    $(this).find('td').css({
                        'backgroundColor': '#dde8ee'
                    })
                    $('#sure').children().eq(0).attr('_dataId', $(this).attr('docID'));
                    return false;
                })

                $("#ueditor_0").contents().find("body").on('click',function(){
                    $("#box").addClass('draftNone');
				})

                $('#sure').children().eq(1).on('click', function () {
                    $("#box").addClass('draftNone')

                });
                $('body').on('click', function () {
                    $("#box").addClass('draftNone')
                });
                sureFun = function () {
                	//暂时存在草稿箱按钮上
                	$("#edui4_body").attr('artileIdPerson',$('#sure').children().eq(0).attr('_dataId'));
                    $("#box").removeClass('draftNone');
                    $("#ueditor_0").contents().find("body").children().remove();
                    // getEditor 查看官网API 及 kitygraph.all.js
                    getDoc($('#sure').children().eq(0).attr('_dataId'), editor1);
                }

            },
            error: function () {
                alert("请求数据出错，请刷新重试")
            }
        });
    }
	//获取稿件
    function getDoc(objId,oldEditor){
        $.ajax({
            url : "../../xy/article/DraftView.do",
            type : "POST",
            data : {
                "docID" : objId
            },
            dataType : "json",
            success : function(data){

                if(data.info){

                    oldEditor.setContent(data.draft.content , false)

                    $("#SYS_TOPIC").val(data.draft.title);
                    	//添加div变输入框的内容
                    $("#SYS_TOPICDIV").text(data.draft.title);

                    //加入打开草稿后
                    channel_frame.titleCheck();
                }else{
                    alert('操作失败');
                }
            },
            error : function(data){

            }
        })
    }
    me.commands["drafts"] =  {
        execCommand : function(){
            var editor1 = UE.getEditor('editor');
            //请求数据
            queryDraftList(editor1);
            // 点击打开执行取得稿件函数
            // 稿件 ID : docID
		}
	}
}
/**
 *自动保存的本地的方法
 */



/**
 * @description 文本打印(2栏)
 * @author fzf
 */
UE.plugins['textpreview'] = function() {
	var me = this;
	me.commands["textpreview"] = {
		execCommand : function(cmd) {
			var editor = UE.getEditor('editor');
			// 10px-30 12px-25 14px-21
			var setting = {
				a4width : 650,
				a4height : 890,
				splitnum : 2,// 分栏数目
				splitword : 25,// 分栏字数 21个字/14px 25个字/12px 29个字/10px
				splitpadding : 20,// 栏间距
				showLine : false,
				linenumpadding : 10,// 行号间距
				linenumwidth : 0,// 30
				fontsize : 12
			}, content = "^" + editor.getContentTxt().replace(/[\n]/g, '^'), // TODO
			splitWidth = 0, // 分栏的宽度
			wordsWidth = 0, // 实际字体占的宽度
			pageLines = 0, // 每页行数
			totalLines = 0, // 总行数
			totalsplits = 0, // 总分栏数
			pagenum = 1;// 总页数

			// alert(content);
			splitWidth = Math.floor((setting.a4width - setting.splitpadding
					* (setting.splitnum - 1))
					/ setting.splitnum) - 2;
			wordsWidth = (setting.fontsize) * setting.splitword;
			if (wordsWidth > splitWidth - setting.linenumpadding
					- setting.linenumwidth) {
				alert("可能会超出打印宽度！！");
				return;
			}

			// 确定文本打印窗口
			// if(textPreviewWindow.closed){
			var textPreviewWindow = window.open("", "文本打印");// ,"text","status,height=200,width=300"
			// }
			textPreviewWindow.focus();

			var wordHeight = setting.fontsize + 6;// 行高
			pageLines = Math.floor(setting.a4height / wordHeight);

			var html = [
					'<style type="text/css">',
					'.main{width:' + setting.a4width + 'px;height:'
							+ setting.a4height
							+ 'px;margin-bottom:22px;border:1px solid #000;}',
					'.split{width:' + splitWidth
							+ 'px;height:100%;float:left;font-size:'
							+ setting.fontsize + 'px;line-height:'
							+ (setting.fontsize + 6) + 'px;margin-right:10px;}',
					'.linenumTxt{text-align:right;font-size:13px;display:inline-block;width:'
							+ setting.linenumwidth + 'px;margin-right:'
							+ setting.linenumpadding + 'px;}',
					'.pageNext{page-break-after: always;}', '</style>' ];

			var size = 0;// 当前打印结果字数
			// html.push("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			var lineWord = 0;// 每行字体，控制换行
			var lineEmpty = 0;// 每行满4个空格时，lineWord-1
			var lines = 0;// 总行数，控制分栏
			var splits = 0;// 总分栏数，控制分页
			var lineTxt = [];
			html.push('<div class="main">');
			html.push('<div class="split">');
			for ( var i = 0; i < content.length; i++) {
				var c = content.charAt(i);
				if (c == " ") {
					lineEmpty++;
					lineTxt.push("&nbsp;");
					if (lineEmpty % 4 == 0) {
						lineWord++;
						if (lineWord % setting.splitword == 0) {// br
							lines++;
							lineTxt.push("<br/>");
							if (setting.showLine) {
								html.push('<span class="linenumTxt">' + lines
										+ '</span>');
							}
							html.push(lineTxt.join(''));
							lineWord = 0;
							lineEmpty = 0;
							lineTxt = [];
							if (lines % pageLines == 0) {// split
								html.push('</div>');
								splits++;
								if (splits % setting.splitnum == 0) {
									html.push('</div>');
									html.push('<div class="pageNext"></div>');
									html.push('<div class="main">');
								}
								html.push('<div class="split">');
							}
						}
					}
					// alert(lineTxt.join(''));
				} else if (c == "^") {
					lines++;
					lineTxt.push("<br/>");// &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					// alert('<span
					// class="linenumTxt">'+lines+'</span>'+lineTxt.join(''));
					if (setting.showLine) {
						html.push('<span class="linenumTxt">' + lines
								+ '</span>');
					}
					html.push(lineTxt.join(''));
					lineWord = 2;
					lineEmpty = 8;
					lineTxt = [ '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' ];
					if (lines % pageLines == 0) {// split
						html.push('</div>');
						splits++;
						if (splits % setting.splitnum == 0) {
							html.push('</div>');
							html.push('<div class="pageNext"></div>');
							html.push('<div class="main">');
						}
						html.push('<div class="split">');
					}
				} else {
					lineWord++;
					lineTxt.push(c);
					if (lineWord % setting.splitword == 0) {// br
						lines++;
						lineTxt.push("<br/>");
						// alert('<span
						// class="linenumTxt">'+lines+'</span>'+lineTxt.join(''));

						if (setting.showLine) {
							html.push('<span class="linenumTxt">' + lines
									+ '</span>');
						}
						html.push(lineTxt.join(''));
						lineWord = 0;
						lineEmpty = 0;
						lineTxt = [];
						if (lines % pageLines == 0) {// split
							html.push('</div>');
							splits++;
							if (splits % setting.splitnum == 0) {
								html.push('</div>');
								html.push('<div class="pageNext"></div>');
								html.push('<div class="main">');
							}
							html.push('<div class="split">');
						}
					}
					;
				}

				// end for
			}
			if (lineTxt.length != 0) {
				lines++;
				if (setting.showLine) {
					html.push('<span class="linenumTxt">' + lines + '</span>');
				}
				html.push(lineTxt.join(''));
			}
			html.push('</div>');
			html.push('</div>');

			html
					.push('<OBJECT classid="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2" height="0" id="wb" name="wb"></OBJECT>');
			html.push('<script type="text/javascript">');
			html.push('wb.execwb(7,1);');
			html.push('</script>');

			// html.push('</body></html>');

			// alert(html.join(''));

			textPreviewWindow.document.write(html.join(''));
			textPreviewWindow.document.close();
		},
		queryCommandState : function(command) {
			var domUtils = baidu.editor.dom.domUtils;
			var images = domUtils.getElementsByTagName(me.body, "img"), tables = domUtils
					.getElementsByTagName(me.body, "table");
			if (images.length > 0 || tables.length > 0) {
				return -1;
			}
			return 0;
		}
	};
};

/**
 * @description 新建版本 每次操作DOM对象，保存时，保存XML流
 * @author fzf
 */
UE.commands['newversion'] = {
	execCommand : function(cmd) {
		var editor = UE.getEditor('editor');
		var contentTxt = editor.getContent();
		// 保存后台，生成XML文件
		var saveData = [
				{
					name : 'UserID',
					value : editor.getOpt("UserID")
				},
				{
					name : 'UserName',
					value : editor.getOpt("UserName")
				},
				{
					name : 'DocID',
					value : editor.getOpt("DocID")
				},
				{
					name : 'DocLibID',
					value : editor.getOpt("DocLibID")
				},
				{
					name : 'Note',
					value : 'note'
				},
				{
					name : 'Content',
					value : '<body xmlns="http://www.w3.org/1999/xhtml">'
							+ contentTxt + '</body>'
				} ];
		$.ajax({
			url : './addVersion.do',
			type : 'post',
			dataType : "json",
			data : saveData,
			traditional : true,
			success : function(data) {
				alert(UE.getEditor('editor').getLang('versionManage.newVersionSucc'));
			}
		});
	}
};

/**
 * @description 修改痕迹
 * @author fzf
 */
UE.commands['modifytrace'] = {
	execCommand : function(cmd) {
		var editor = UE.getEditor('editor');
		$.ajax({
			url : './getTrace.do',
			type : 'post',
			data : {
				"docID":editor.getOpt("DocID"),
				"docLibID":editor.getOpt("DocLibID")
			},
			traditional : true,
			success : function(data) {
				openTextDialog(
						'~/dialogs/versionhistory-custom/versionContent.html',
						data, '修改痕迹');
			}
		});
	},
	queryCommandState : function(command) {
		// TODO 如果至少 一个 版本内容的话，才可用
		/**
		 * var xmlDoc = loadXmlDOc(reversionXmlString); var versions =
		 * xmlDoc.getElementsByTagName('version'); if( versions.length <1 ){
		 * return -1; } return 0;
		 */
		return 0;
	}
};

/**
 * @description 文本比较
 * @author fzf
 */
UE.commands['textcompare'] = {
		execCommand:function (cmd) {
			//确定窗口
			var iframeUrlText = '~/dialogs/versionhistory-custom/textcompare.html';
			var dialog = new baidu.editor.ui.Dialog(baidu.editor.utils.extend({
				iframeUrl:iframeUrlText.replace('~/', UE.getEditor('editor').options.UEDITOR_HOME_URL || ''),
				editor:UE.getEditor('editor'),
				className:'edui-for-textcompare',
				title:"文本比较",
				holdScroll:false,
				fullscreen: false
			}, {
				buttons:[
					{
						className:'edui-okbutton',
						label:UE.getEditor('editor').getLang("ok"),
						editor:UE.getEditor('editor'),
						onclick:function () {
							//获得版本号
							var iframe0 = dialog.getDom('iframe');
							var v1 = iframe0.contentWindow.document.getElementById("v1"),
								v2 = iframe0.contentWindow.document.getElementById("v2");
							if( v1.selectedIndex == v2.selectedIndex ){
								alert(UE.getEditor('editor').getLang('versionManage.differVersion'));
								return;

							}
							//alert("v1="+v1.value+"，v2="+v2.value);
							dialog.close(true);

							//TODO 传给后台,确定比较结果！！！！
							openTextDialog('~/dialogs/versionhistory-custom/versionContent.html','<p>哈哈，这是比较结果</p>','文本比较');
							/**
							$.ajax({
								url: 			'',
								type: 			'post',
								data: 			'v1=' +v1+"&v2="+v2
								traditional: 	true,
								success: 		function(data){
									//if(data.success){
										openTextDialog('~/dialogs/versionhistory/versionContent.html',data.text,'文本比较');
									//}
								}
							});
							*/
						}
					},
					{
						className:'edui-cancelbutton',
						label:UE.getEditor('editor').getLang("cancel"),
						editor:UE.getEditor('editor'),
						onclick:function () {
							dialog.close(false);
						}
					}
				]
			}
			));
			dialog.render();
			dialog.open();

		},
		queryCommandState:function (command) {
			// TODO 如果至少 一个 版本内容的话，才可用
			/**
			var xmlDoc = loadXmlDOc(reversionXmlString);
			var versions = xmlDoc.getElementsByTagName('version');
			if( versions.length <1 ){
				return -1;
			}*/
			return 0;
		}
	};

/**
 * @description 修订
 * @author fzf
 */
UE.plugins['ajustment'] = function() {
	var me = this, // 修订按钮
	selectTxt = '', flag = 0;
	// 加事件的不是me,是整个编辑区域的keyup
	/**
	 * me.addListener('reset',function(){ flag = 0; });
	 */

	// 获得选中内容
	function getSelectTxt(type, evt) {
		var range = UE.getEditor('editor').selection.getRange();
		range.select();
		selectTxt = UE.getEditor('editor').selection.getText();
	}

	// 添加修订痕迹
	function addAjustment(type, evt) {
		var key = evt.keyCode;
		// alert("弹起时："+key);
		// alert("弹起时："+selectTxt);
		// TODO如果有选中的文字或内容，则取出内容，添加<strike>

		// 1、删除光标后的内容
		// 2、如果有选中的内容，则标为删除
		if (key == 8) {// backspace
			if (selectTxt.length != 0) {
				evt.returnValue = false;
				// 如果是表格，就不执行这一行。
				UE.getEditor('editor').execCommand(
						'insertHtml',
						'<strike style=\'color:#ff0000;\' class=\'deleted\'>'
								+ selectTxt + '</strike>');
			} else {
				evt.returnValue = false;

				var rng, text;
				if (document.createRange) {
					// alert("chrome");
					// UE.getEditor('editor').execCommand('selectall');

					var ht = '<b style="color:red;">1</b>';
					var rng = $("#edui1_iframeholder").find("iframe").get(0).contentWindow
							.getSelection().getRangeAt(0);
					var frg = rng.createContextualFragment(ht);
					rng.insertNode(frg);
					// rng.setStartBefore(frg);

					// var editorIframe =
					// $("#edui1_iframeholder").find("iframe").get(0).contentWindow;
					// var rng = editorIframe.getSelection().getRangeAt(0);

					// var tmpText = editorIframe.document.createTextNode('');
					// rng.insertNode( tmpText );
					// rng.setStartBefore(tmpText);

					// rng.setStart(node,-2);
					// rng.select();

					// alert($("#edui1_iframeholder").html());
					// alert($("#edui1_iframeholder").find("iframe").length);

					// alert($("#edui1_iframeholder").find("iframe").get(0).contentWindow.document);

					// var rangeStart =
					// $("#edui1_iframeholder").find("iframe").get(0).contentWindow.document.body.selectionStart;

					// alert("chrome=="+rangeStart);
					// var rangeEnd = UE.getEditor('editor').selectionEnd;

					// UE.getEditor('editor').setSelectionRange(rangeStart - 1,
					// rangeStart);//后一个字

				} else {
					rng = document.selection.createRange();
					rng.moveStart("character", -2);
					rng.select();// 显式选择文本区域（选中前一个字符）
					text = rng.text;
				}
				// alert(text);

				// var caretPos = rng.duplicate();
				document.selection.clear();
				UE.getEditor('editor').execCommand(
						'insertHtml',
						'<strike style=\'color:#ff0000;\' class=\'deleted\'>'
								+ text + '</strike>');
				// 光标向前移动一个，不然不能连续删除
				rng.moveEnd("character", -1);
				rng.select();
			}
			// TODO 判断表格，背景色为浅红色
			// TODO 删除图片

		}
		// 1、删除光标后的内容
		if (key == 46) {// delete???????笔记本的keycode?????????
			// alert("delete");
			if (selectTxt.length != 0) {
				evt.returnValue = false;
				UE.getEditor('editor').execCommand(
						'insertHtml',
						'<strike style=\'color:#ff0000;\' class=\'deleted\'>'
								+ selectTxt + '</strike>');
			} else {
				evt.returnValue = false;
				var rng = document.selection.createRange();
				rng.moveEnd("character", 1);
				rng.select();// 显式选择文本区域（选中后一个字符）
				var text = rng.text;// 获得选中的文本（后一个字符）
				// alert(text);

				var caretPos = rng.duplicate();
				document.selection.clear();
				UE.getEditor('editor').execCommand(
						'insertHtml',
						'<strike style=\'color:#ff0000;\' class=\'deleted\'>'
								+ text + '</strike>');
			}
		}

		// 字母，数字，小键盘数字，符号
		// 1、输入的所有内容，都标为新增
		// 2、如果有选中的内容，则标为删除
		// 3、各种 UE.getEditor('editor').execCommand('insertHtml',
		// value)新增（图片，表格，时间，分页符）
		// 4、粘贴
		// 5、回车键
		// 6、如果父元素已经删掉了，输入的内容全是删除的
		if ((key >= 65 && key <= 90) || (key >= 48 && key <= 57)
				|| (key >= 96 && key <= 111) || (key >= 186 && key <= 192)
				|| (key >= 219 && key <= 222)) {
			// alert("数字或字母或符号");
			// alert();
			// 先删掉选中的内容，再添加输入的内容，不能阻止默认事件
		}
	}
	me.commands['ajustment'] = {
		execCommand : function(cmdName) {
			// alert("flag="+flag);
			if (flag) {
				flag = 0;
				me.removeListener('keydown', addAjustment);
				me.removeListener('beforekeydown', getSelectTxt);
				// alert("应用修订结果！");

				// TODO 保存修订结果
				// 添加的文字
				var addeds = $(
						".added",
						document.getElementById('ueditor_0').contentWindow.document.body);
				addeds.css({
					"color" : "#000",
					"text-decoration" : "none"
				});
				addeds.find("span").css({
					"color" : "#000",
					"text-decoration" : "none"
				});
				addeds.find("p").css({
					"color" : "#000",
					"text-decoration" : "none"
				});

				// 添加的表格
				var addedTDs = $(
						"td.added",
						document.getElementById('ueditor_0').contentWindow.document.body);
				addeds.css("background-color", "#fff");

				addeds.removeClass("added");
				addedTDs.removeClass("added");
				// TODO added img
				var addedImgs = $(
						".addedImg",
						document.getElementById('ueditor_0').contentWindow.document.body);
				addedImgs.removeClass("addedImg");
				// *********************************************************************************

				// 删除文字
				$(
						".deleted",
						document.getElementById('ueditor_0').contentWindow.document.body)
						.remove();

				// 删除table(跨行，跨列问题)
				$(
						"table",
						document.getElementById('ueditor_0').contentWindow.document.body)
						.each(
								function() {
									var rowTds = $(this).find(
											"td[class*='deletedRow']");
									// alert(rowTds.length);
									var rowIndexArr = [];
									for ( var i = 0; i < rowTds.length; i++) {
										var tdClass = rowTds.eq(i)
												.attr("class");
										rowIndexArr.push(tdClass.substring(10));
										if (rowTds.get(i).rowSpan > 1) {
											rowTds.get(i).rowSpan--;
										} else {
											rowTds.get(i).parentNode
													.removeChild(rowTds.get(i));
										}
									}
									rowIndexArr = rowIndexArr.distinct();
									// alert("rowIndexArr-----"+rowIndexArr);
									for ( var i = 0; i < rowIndexArr.length; i++) {
										var index = rowIndexArr[i];
										if (i != 0) {
											index--;
										}
										$(this).get(0).deleteRow(index);
									}
								});

				// 删除列
				var colTds = $(
						"td[class*='deletedCol']",
						document.getElementById('ueditor_0').contentWindow.document.body);
				// alert(tds.length);
				for ( var i = 0; i < colTds.length; i++) {
					// alert(tds.get(i).rowSpan);
					if (colTds.get(i).colSpan > 1) {
						colTds.get(i).colSpan--;
					} else {
						colTds.get(i).parentNode.removeChild(colTds.get(i));
					}
				}

				// TODO delete img
				// *********************************************************************************

				return;
			}
			me.addListener('keydown', addAjustment);
			// if( evt != undefined && evt.keyCode == 37) {return;}
			me.addListener('beforekeydown', getSelectTxt);// TODO
															// 左箭头事件不好用了！！！！！evt.keyCode
															// == 37
			// alert("给整个编辑区域加keyup监听");
			flag = 1;
		},
		queryCommandState : function() {
			return flag;
		},
		notNeedUndo : 1
	};
};

/**
 * @description 提取标题
 * @author fzf LJS
 */
UE.plugins['extracttitle'] = function() {
	var me = this;

	me.commands['extracttitle'] = {
		execCommand : function(cmdName) {
			var content = UE.getEditor('editor').selection.getText();
			content = UE.utils.trim(content);
			document.getElementById('SYS_TOPIC').value = content;
            document.getElementById('SYS_TOPICDIV').innerHTML  = content;
			//$("#SYS_TOPIC").val(content);
			//添加了提取后的统计标题字数
            channel_frame.titleCheck()
		},
		queryCommandState : function() {
			var text = UE.getEditor('editor').selection.getText();
			if (text.length <= 0) {
				return -1;
			}
			return 0;
		}
	};
	me.addshortcutkey("extracttitle", "112");
};
/**
 * @description 提取链接标题
 * @author qxm
 */
UE.plugins['extractlinktitle'] = function() {
    var me = this;

    me.commands['extractlinktitle'] = {
        execCommand : function(cmdName) {
            var content = UE.getEditor('editor').selection.getText();
            content = UE.utils.trim(content);
            document.getElementById('a_linkTitle').value = content;
        },
        queryCommandState : function() {
            var text = UE.getEditor('editor').selection.getText();
            if (text.length <= 0) {
                return -1;
            }
            return 0;
        }
    };
    me.addshortcutkey("extractlinktitle", "115");
};
/**
 * @description 提取引题
 * @author fzf LJS
 */
UE.plugins['extractguidetitle'] = function() {
	var me = this;

	me.commands['extractguidetitle'] = {
		execCommand : function(cmdName) {
			alert("提取引题");
		},
		queryCommandState : function() {
			var text = UE.getEditor('editor').selection.getText();
			if (text.length <= 0) {
				return -1;
			}
			return 0;
		}
	};
	me.addshortcutkey("extractguidetitle", "119");
};

/**
 * @description 提取副题
 * @author fzf LJS
 */
UE.plugins['extractsubtitle'] = function() {
	var me = this;

	me.commands['extractsubtitle'] = {
		execCommand : function(cmdName) {
			var content = UE.getEditor('editor').selection.getText();
			content = UE.utils.trim(content);
			document.getElementById("a_subTitle").value = content;
		},
		queryCommandState : function() {
			var text = UE.getEditor('editor').selection.getText();
			if (text.length <= 0) {
				return -1;
			}
			return 0;
		}
	};
	me.addshortcutkey("extractsubtitle", "113");
};

/**
 * @description 提取作者
 * @author fzf LJS
 */
UE.plugins['extractauthor'] = function() {
	var me = this;
	me.commands['extractauthor'] = {
		execCommand : function(cmdName) {
			var content = UE.getEditor('editor').selection.getText();
			content = UE.utils.trim(content);
			document.getElementById('SYS_AUTHORS').value = content;
		},
		queryCommandState : function() {
			var text = UE.getEditor('editor').selection.getText();
			if (text.length <= 0) {
				return -1;
			}
			return 0;
		}
	};
	me.addshortcutkey("extractauthor", "114");
};

/**
 * @description 提取关键字
 * @author fzf LJS
 */
UE.plugins['extractkey'] = function() {
	var me = this;
	me.commands['extractkey'] = {
		execCommand : function(cmdName) {
			//获得选择的内容
			var content = UE.getEditor('editor').selection.getText();
			content = UE.utils.trim(content);
			//获得关键词
			var _keyword = document.getElementById("a_keyword").value;
			_keyword = UE.utils.trim(_keyword);
			//赋值
			document.getElementById("a_keyword").value += _keyword == ""? content : "," + content;
		},
		queryCommandState : function() {
			var text = UE.getEditor('editor').selection.getText();
			if (text.length <= 0) {
				return -1;
			}
			return 0;
		}
	};
	me.addshortcutkey("extractkey","ctrl+75" );
};


/**
 * @description 纯文本粘贴
 * @author fzf LJS
 */
UE.plugins['extractkeya'] = function() {
    var me = this;
    me.commands['extractkeya'] = {

        execCommand : function(cmdName) {


            // alert(1111)

           /* //获得选择的内容
            var content = UE.getEditor('editor').selection.getText();
            content = UE.utils.trim(content);
            //获得关键词
            var _keyword = document.getElementById("a_keyword").value;
            _keyword = UE.utils.trim(_keyword);
            //赋值
            document.getElementById("a_keyword").value += _keyword == ""? content : "," + content;*/
        }
    };
    // me.addshortcutkey("extractkeya","ctrl+81" );
};

/**
 * @description 提取摘要
 * @author guzm
 *
 */
UE.plugins['extractabstract'] = function() {
	var me = this;
	me.commands['extractabstract'] = {
		execCommand : function(cmdName) {
			var content = UE.getEditor('editor').selection.getText();
			content = UE.utils.trim(content);
			document.getElementById('a_abstract').value = content;
		},
		queryCommandState : function() {
			var text = UE.getEditor('editor').selection.getText();
			if (text.length <= 0) {
				return -1;
			}
			return 0;
		}
	};
	me.addshortcutkey("extractabstract", "ctrl+89");
};
/**
 * @description 提取来源
 * @author yb
 *
 */
UE.plugins['extractsource'] = function() {
    var me = this;
    me.commands['extractsource'] = {
        execCommand : function(cmdName) {
            var content = UE.getEditor('editor').selection.getText();
            content = UE.utils.trim(content);
            document.getElementById('findSourceInput').value = content;
        },
        queryCommandState : function() {
            var text = UE.getEditor('editor').selection.getText();
            if (text.length <= 0) {
                return -1;
            }
            return 0;
        }
    };
    me.addshortcutkey("extractsource", "118");
};
/**
 * @description 自定义分页 F10
 * @author yb
 * 写入了pagebreak.js
 */


UE.plugins['titleImg'] = function() {
	var me = this;
	me.commands['titleImg'] = {
		execCommand : function(cmdName) {
			alert(cmdName);
		},
		queryCommandState : function() {
			return 0;
		}
	};
	me.addshortcutkey("titleImg", "118");
};

// 数组去重
Array.prototype.distinct = function() {
	var clone, newArr = [], n = 0;
	if (this.length < 2)
		return;
	clone = this;
	for ( var i = 0, len = this.length; i < len; i++) {
		for ( var j = i + 1, len2 = clone.length; j < len2; j++) {
			if (this[i] !== clone[j]) {
				n++;
			}
		}
		if (n == (len - i - 1)) {
			newArr.push(this[i])
		}
		n = 0;
	}
	return newArr;
}

/* 确定窗口 */
function openTextDialog(url, content, windowTitle) {
	viewVersionContent = content;
	var dialog = new baidu.editor.ui.Dialog(baidu.editor.utils.extend({
		iframeUrl : url.replace('~/',
				UE.getEditor('editor').options.UEDITOR_HOME_URL || ''),
		editor : UE.getEditor('editor'),
		className : 'edui-for-versioncontent',
		title : windowTitle,
		holdScroll : false,
		fullscreen : false
	}, {}));
	dialog.render();
	dialog.open();
}
// 获得窗口路径 参数
function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}



UE.commands['kityformula'] = {
	execCommand:function (cmd) {
		// 创建dialog
		var kfDialog = new UE.ui.Dialog({

			// 指定弹出层路径
			iframeUrl: this.options.UEDITOR_HOME_URL + 'kityformula/kityFormulaDialog.html',
			// 编辑器实例
			editor: this,
			// dialog 名称
			name: "kityformula",
			// dialog 标题
			title: '插入公式 - KityFormula',

			// dialog 外围 css
			cssRules: 'width:783px; height: 386px;',

			//如果给出了buttons就代表dialog有确定和取消
			buttons:[
				{
					className:'edui-okbutton',
					label:'确定',
					onclick:function () {
						kfDialog.close(true);
					}
				},
				{
					className:'edui-cancelbutton',
					label:'取消',
					onclick:function () {
						kfDialog.close(false);
					}
				}
			]});

		kfDialog.render();
		kfDialog.open();
	}
};

UE.commands['code'] = {
	execCommand:function (cmd) {
		// 创建dialog
		var codeDialog = new UE.ui.Dialog({

			// 指定弹出层路径
			iframeUrl: this.options.UEDITOR_HOME_URL + 'dialogs/code/code.html',
			// 编辑器实例
			editor: this,
			// dialog 名称
			name: "insertcode",
			// dialog 标题
			title: '插入代码',

			// dialog 外围 css
			cssRules: 'width:783px; height: 572px;',

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

		codeDialog.render();
		codeDialog.open();
	}
};
//字符串
UE.commands['quickinsertion'] = {
	execCommand:function (cmd) {
		// 创建dialog
		var quickinsertionDialog = new UE.ui.Dialog({

			// 指定弹出层路径
			iframeUrl: this.options.UEDITOR_HOME_URL + 'dialogs/quickinsertion/code.html',
			// 编辑器实例
			editor: this,
			// dialog 名称
			name: "quickinsertion",
			// dialog 标题
			title: '自定义插入字符串',

			// dialog 外围 css
			cssRules: 'width:500px; height: 216px;'

			//如果给出了buttons就代表dialog有确定和取消
			 ,buttons:[
			 {
			 className:'edui-okbutton',
			 label:'确定',
			 onclick:function () {
				 //console.log( UE.I18N['zh-cn'].contextMenu['name1'])
				 //UE.I18N['zh-cn'].contextMenu['name1']='aaaaa';
				 //console.log(UE.I18N['zh-cn'].contextMenu['name1'])
				 quickinsertionDialog.close(true);
			 }
			 },
			 {
			 className:'edui-cancelbutton',
			 label:'取消',
			 onclick:function () {
				 quickinsertionDialog.close(false);
			 }
			 }
			 ]
		});

		quickinsertionDialog.render();
		quickinsertionDialog.open();
	}
};
//图片
UE.commands['insertlink'] = {
	execCommand:function (cmd) {
		// 创建dialog
		var insertlinkDialog = new UE.ui.Dialog({

			// 指定弹出层路径
			iframeUrl: this.options.UEDITOR_HOME_URL + 'dialogs/insertlink/insertlink.html',
			// 编辑器实例
			editor: this,
			// dialog 名称
			name: "insertlink",
			// dialog 标题
			title: '图片页跳转',

			// dialog 外围 css
			cssRules: 'width:500px; height: 216px;'

			//如果给出了buttons就代表dialog有确定和取消
			/* ,buttons:[
			 {
			 className:'edui-okbutton',
			 label:'确定',
			 onclick:function () {
			 insertlinkDialog.close(true);
			 }
			 },
			 {
			 className:'edui-cancelbutton',
			 label:'取消',
			 onclick:function () {
			 insertlinkDialog.close(false);
			 }
			 }
			 ]*/
		});

		insertlinkDialog.render();
		insertlinkDialog.open();
	}
};
//添加批注插件
UE.plugins["comment_xy"] = function() {
	var me = this;
	me.commands["comment_xy"] =  {
		execCommand : function() {
            //将光标移动至选中区域的最后
            var range = me.selection.getRange();
            //range.collapse(false);
            if (range.collapsed) {
				alert("请选择内容后再添加批注！")
            } else {
                var commentContent = "";

				 //获取选中的文字
				 /*if(range.cloneContents()){
				 var abc=range.cloneContents();
				 var _div=document.createElement("div");
				 _div.appendChild(abc);
				 commentContent=_div.innerHTML;
				 }*/
                $("ul.tabs #tabComment").trigger('click');
                //修改已加批注的颜色
                $("#ueditor_0").contents().find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
                $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
                //在光标处插入批注标签
                var now = new Date();
                var timestamp = now.getTime();
                var timeStr = now.getFullYear() + '-' + (now.getMonth() + 1) + '-' + now.getDate() + ' ' + now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds();
                //为选中的图片添加背景颜色

				if(range.getClosedNode()){
					var oImg=$(range.getClosedNode());
                    //oImg.wrap("<span style='display:inline-block;background-color:rgb(249, 181, 82);border:5px solid rgb(249, 181, 82);' class='selected-comment selected-comment-img' title='批注' id='"+timestamp+"' comment='"+commentContent+"' data-date='"+timeStr+"' data-user='"+article.userName+"' herfto='#comment" + timestamp+"'></span>");
                    oImg.css({'background-color':'rgb(249, 181, 82)','border':'5px solid rgb(249, 181, 82)'});
                    oImg.addClass('selected-comment selected-comment-img '+timestamp);
                    oImg.attr({'title':'批注','flag-id':timestamp,'comment':commentContent,'data-date':timeStr,'data-user':article.userName,'herfto':'#comment' + timestamp});
				}else{
                    //为选中的文字添加背景颜色
                    range.applyInlineStyle("span", {
                        "style": "display:inline-block;background-color:rgb(249, 181, 82);text-indent: 0;",
                        "class": "selected-comment "+timestamp,
                        "title": "批注",
                        "flag-id": timestamp,
                        "comment": commentContent,
                        "data-date": timeStr,
                        "data-user": article.userName,
                        "herfto": "#comment" + timestamp
                    });
				}
                //me.execCommand("inserthtml", "<a title=\'批注\' id=\'" + timestamp + "\' comment=\'"+commentContent+"\'  data-date=\'" + timeStr + "\' data-user=\'" + article.userName + "\' class=\'comment-a no-remove-comment\' href=\'#comment-" + timestamp + "\' style='display: inline-block;'>&nbsp;<span class='comment glyphicon glyphicon-comment no-remove-comment'></span>&nbsp;</a>");//<span class='add123'>&nbsp;</span>
                // range.deleteContents();
                //右侧插入批注面板
				if(typeof(timestamp) !== 'undefined'){
					$("#tab3 .list-group").append('<a id="comment-' + timestamp + '" href="#' + timestamp + '"  class="list-group-item">'
						+ '<dl style="margin-bottom: 0;">'
						+ '<dt>'
						+ '<span style="float: left;padding:5px">' + article.userName + '</span>'
						+ '<span style="float: right;padding:5px">' + timeStr + '</span>'
						+ '</dt>'
						+ '<dd>'
						+ '<textarea data-id="#' + timestamp + '" style="width: 95%;min-height:60px; overflow:hidden;resize:none ">' + commentContent + '</textarea>'
						+ '</dd>'
						+ '<dd>'
						+ '<button class="btn-xs btn-default" data-id="#' + timestamp + '" style="border: 1px solid #ddd;">移除</button>'
						+ '</dd>'
						+ '</dl>'
						+ '</a>');
                }

                //定位到新增批注位置 并修改颜色
                var $commentList = $("#tab3 .list-group");
                $commentList.find("a").css("background-color", "");
                $commentList.find("a").css("color", "");
                $("#comment-" + timestamp).css("background-color", "rgb(249, 181, 82)");
                $("#comment-" + timestamp).css("color", "#333");
                if($("#comment-" + timestamp).position()){
                    $("#tab3").scrollTop($("#comment-" + timestamp).position().top);
				}
            }
        },
        queryCommandState : function() {
			// 源稿库不显示批注
            /*var getQueryString=function(name){
                var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                var r = window.location.search.substr(1).match(reg);
                if(r != null)return unescape(r[2]);
                return null;
            };
			var _ch=getQueryString('ch');
            if(_ch==null){return -1}*/
            return 0;
        }
	}
    me.addListener('keyup',function(cmd, evt) {
        if(evt.keyCode == 8 || evt.keyCode == 46){
            initcomment();
            /*var range = me.selection.getRange();
            var node = range.startContainer;
            //当删除的是文章中的批注标签时，应该删除批注面板中对应的信息
            if(node.nodeName != "P" && range.startOffset == 1 && node.nodeValue.length == 1) {
                if(node.previousElementSibling && node.previousElementSibling.className == "comment-a no-remove-comment") {
                    if(node.nextSibling != null && node.previousSibling.nodeType != 1){
                        node.previousElementSibling.remove();
                        evt.preventDefault ? evt.preventDefault() : (evt.returnValue = false);
                        //删除批注后重新初始化批注数据
                        initcomment();
                    }else{
                        $("a").bind('DOMNodeRemoved',function(e) {
                            //alert(2);
                        });
                    }
                }
                if(node.previousElementSibling && node.previousElementSibling.className == "comment glyphicon glyphicon-comment no-remove-comment"){
                    //alert(0);
                    node.previousElementSibling.parentNode.remove();
                    evt.preventDefault ? evt.preventDefault() : (evt.returnValue = false);
                    initcomment();
                }
            }*/
		}
    });

    me.addListener('keydown',function(cmd, evt) {
        if(evt.keyCode == 13){
        	setTimeout(function(){
                initcomment();
			},300);
        }
    });

	//绑定批注面板删除事件(删除批注面板同时删除稿件中的批注标签)
	$("#tab3").delegate(".list-group .btn-xs", "click", function() {
		var _id = $(this).attr("data-id");
		var id='.selected-comment[flag-id="'+_id.substring(1)+'"]';
		//$(me.body).find(id).remove();
        //移除批注编辑
		if($(me.body).find(id).hasClass("selected-comment-img")){
            $(me.body).find(id).css({'background-color':'','border':''});
            $(me.body).find(id).removeClass('selected-comment selected-comment-img');
            $(me.body).find(id).attr({'title':'','id':'','comment':'','data-date':'','data-user':'','herfto':''});
		}else{
            $(me.body).find(id).after($(me.body).find(id).html()).remove();
		}

		$(this).parent().parent().parent().remove();
	});

	//绑定批注面板内容变化事件（内容变化时将批注内容添加到批注标签中）
	$("#tab3").delegate(".list-group textarea", "change", function() {
        var _id = $(this).attr("data-id");
        var id='.selected-comment[flag-id="'+_id.substring(1)+'"]';
		$(me.body).find(id).attr("comment", $(this).val());
	});

	//点击批注Tab触发   加载右侧批注面板并显示文章中的批注标签
	$("ul.tabs li").click(function (){
		if($(this).attr("id") == "tabComment"){
			//当批注面板没有批注时执行初始化
			if(!$("#tab3 .list-group").has('a').length){
				initcomment();
			}
         	$("#ueditor_0").contents().find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
            $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
            $("#tab3 .list-group a").each(function(){
				if($.trim($(this).css("background-color"))=="rgb(249, 181, 82)"){
					//var id=$(this).attr("href");
                    var _id = $(this).attr("href");
                    var id='.selected-comment[flag-id="'+_id.substring(1)+'"]';
					if($(me.body).find(id).hasClass("selected-comment-img")){
                        $(me.body).find(id).css("background-color","rgb(249, 181, 82)").css("border", "5px solid rgb(249, 181, 82)");
					}else{
                        $(me.body).find(id).css("background-color","rgb(249, 181, 82)");
					}

				}
			})
		} else {
         	$("#ueditor_0").contents().find(".selected-comment").css("background-color", "");
            $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "").css("border", "");
		}
	});
};

//显示/隐藏文本中的批注插件
UE.plugins['showcomment'] = function() {
	var me = this;
	var isShow = 1;
	me.commands['showcomment'] = {
		execCommand : function(){
			if(isShow){
                $("#ueditor_0").contents().find(".selected-comment").css("background-color", "");
                $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "").css("border", "");
				isShow = 0;
			}else{
                $("#ueditor_0").contents().find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
                $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
				isShow = 1;
			}
		},
		queryCommandState : function(){
			return isShow;
		},
		notNeedUndo : 1
	}
}

//初始化批注面板
function initcomment(){
	var ue = UE.getEditor("editor");
	var $commentList = $("#tab3 .list-group").empty();

	// 收集所有的herfto属性 用于判断是否新增批注
	var herfto_Arr=[];
    $(ue.body).find(".selected-comment").each(function(){
        herfto_Arr.push($(this).attr('herfto'))
	});

	$(ue.body).find(".selected-comment").each(function(index){
		// 如果加批注的内容为空 删除
		var _reg=/\<[\s\S]*?\>/g;
		var _contents=$(this).html();
		var _text=_reg.test(_contents) ? _contents.replace(_reg,""):_contents;
		if($.trim(_text)){
			// 若有内容 添加批注
            var timestamp = $(this).attr("flag-id");
            var user = $(this).attr("data-user");
            var date = $(this).attr("data-date");
            var comment = $(this).attr("comment");
            var herfto=$(this).attr("herfto");

            // 确保相同的批注只出现一次
            if(herfto_Arr.indexOf(herfto)!=index) return;

            if(user == article.userName){
                $commentList.append('<a id="comment-' + timestamp + '" href="#' + timestamp + '" class="list-group-item">'
                    + '<dl style="margin-bottom: 0;">'
                    + '<dt>'
                    + '<span style="float: left;padding:5px">' + user + '</span>'
                    + '<span style="float: right;padding:5px">' + date + '</span>'
                    + '</dt>'
                    + '<dd>'
                    + '<textarea data-id="#' + timestamp + '" style="width: 95%;min-height:60px; overflow:hidden;resize:none ">' + comment + '</textarea>'
                    + '</dd>'
                    + '<dd>'
                    + '<button class="btn-xs btn-default" data-id="#' + timestamp + '" style="border: 1px solid #ddd;">移除</button>'
                    + '</dd>'
                    + '</dl>'
                    + '</a>');
            } else {
                //他人添加的批注  只可以查看 不可以修改
                $commentList.append('<a id="comment-' + timestamp + '" href="#' + timestamp + '" class="list-group-item-2">'
                    + '<dl style="margin-bottom: 0;">'
                    + '<dt>'
                    + '<span style="float: left;padding:5px">' + user + '</span>'
                    + '<span style="float: right;padding:5px">' + date + '</span>'
                    + '</dt>'
                    + '<dd>'
                    + '<textarea readonly data-id="#' + timestamp + '" style="width: 95%;min-height:60px; overflow:hidden;resize:none ">' + comment + '</textarea>'
                    + '</dd>'
                    + '<dd>'
                    // + '<button disabled="disabled" class="btn-xs btn-default" data-id="#' + timestamp + '" style="border: 1px solid #ddd;">移除</button>'
                    + '</dd>'
                    + '</dl>'
                    + '</a>');
            }
		}else{
            $(this).after($(this).html());
            $(this).remove();
		}
	});

	//绑定批注面板和标签的联动事件(点击批注标签同时选中批注面板)
	$(ue.body).delegate(".selected-comment", "click", function(){
		if($("#tabComment").hasClass("select")){
            $("ul.tabs #tabComment").trigger('click');
            $("#ueditor_0").contents().find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
            $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
            if($(this).hasClass("selected-comment-img")){
                $(this).css("background-color", "rgb(249, 181, 82)").css("border", "5px solid rgb(249, 181, 82)");
			}else{
                $(this).css("background-color", "rgb(249, 181, 82)");
			}

            var id = $(this).attr("flag-id");
            var $commentList = $("#tab3 .list-group");
            $commentList.find("a").css("background-color", "");
            $commentList.find("a").css("color", "");
            $("#comment-" + id).css("background-color", "rgb(249, 181, 82)");
            $("#comment-" + id).css("color", "#333");

            $("#tab3").scrollTop($("#comment-" + id).position().top);

		}

    });

	//绑定批注面板和标签的联动事件(点击批注面板同时选中批注标签)
	$("#tab3").delegate(".list-group a", "click", function(){
		var $commentList = $("#tab3 .list-group");
		$commentList.find("a").css("background-color", "");
		var id = $(this).attr("href").substr(1);
		$(this).css("background-color", "rgb(249, 181, 82)");
		$(this).css("color", "#333");
		var $ueBody = $(ue.body);
		$ueBody.find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
        $ueBody.find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
        if($ueBody.find("." + id).hasClass("selected-comment-img")){
            $ueBody.find("." + id).css("background-color", "rgb(249, 181, 82)").css("border", "5px solid rgb(249, 181, 82)");
		}else{
            $ueBody.find("." + id).css("background-color", "rgb(249, 181, 82)");
		}
		//$("html body")
		//console.log('document'+$(document).scrollTop())
		// console.log("html,body"+$("html,body").scrollTop())
        $("html,body").stop().animate({scrollTop: 90}, 100,function(){
        	if($ueBody.find("." + id) && $ueBody.find("." + id).offset() && $ueBody.find("." + id).offset().top){
                $("#ueditor_0").contents().find("html,body").stop().animate({scrollTop: $ueBody.find("." + id).offset().top},0);
			}
        });
	});

	//批注编辑鼠标移动事件
    var canSelect=false;
    function prevent(){
    	if(canSelect){
            return true;
		}else{
            return false;
		}
    }
    $("#tab3").bind("mousemove","a",prevent);
    $("#tab3").delegate("textarea", "focus", function(){
        canSelect=true;
	})
    $("#tab3").delegate("textarea", "blur", function(){
        canSelect=false;
    })
}


/**
 * 校对 create by hd
 */
UE.plugins['check'] = function() {
    var me = this;
    // 判断是否是点击了校对按钮
    var isCheck = false;
    function getList(content) {
        $.ajax({
            url: "../../xy/ueditor/proof.do",
            dataType: "json",
			type : 'post',
            data : {
                "content": content,
            },
            success: function (data) {
                if(!data) return;
                if(data.error){
                	alert(data.error);
                	return;
				}
                showCheck(data.result);
                lightProofread(data.result);
            },
            error: function () {
                alert("请求数据出错，请刷新重试");
            }
        });
    }
    function showCheck(_json) {
    	$("#tab5 #cancelCheckBtn").show();
        if(_json) {
            var ErrorWords = _json.ErrorWords;
            var SuggestWords = _json.SuggestWords;
            if (ErrorWords) {
                var html = "<table class=\"proofread-list\" width=\"100%\">\n" +
                    "<thead align=\"center\"><tr>" +
                    "<td width=\"10%\">序号</td>" +
                    "<td width=\"45%\">错误词</td>" +
                    "<td width=\"45%\">建议词</td>" +
                    "</tr></thead>\n" +
                    "<tbody>\n";
                for (var i = 0; i < ErrorWords.length; i++) {
                    html += "<tr>" +
                        "<td align=\"center\">" + (i + 1) + "</td><td class=\"proofread-error\">" + ErrorWords[i] + "</td>" +
                        "<td class=\"proofread-suggest\">" + SuggestWords[i] + "</td>" +
                        "</tr>";
                }
                html += "</tbody>\n</table>";
            } else {
                html = '校对完毕,没有错误词!';
            }
        }
        $("#tab5 .proofread").html(html);
    }
    function lightProofread(_json) {
    	try {
            var contentHtml = me.getContent();
            var ErrorWords = _json.ErrorWords;
            var SourceSentence = _json.SourceSentence;
            var SuggestWords = _json.SuggestWords;
            for (var i = 0; i < SourceSentence.length; i++) {
                var sourceSentence = SourceSentence[i] ? SourceSentence[i] : '';
                var pattern1 = new RegExp(sourceSentence, "gi");
                var pattern2 = new RegExp("(" + ErrorWords[i] + ")", "gi");
                var _sourceHtml;
                if(sourceSentence){
                    _sourceHtml = contentHtml.match(pattern1);
                }
                if (!_sourceHtml) {
                    var a = sourceSentence.split(ErrorWords[i]);
                    var pat = "";
                    for (var ii = 0; ii < a.length; ii++) {
                        if (a[ii] != "") {
                            a[ii] = a[ii].split("").join("(\\s*<[^>]+>\\s*)*");
                            if (ii == a.length - 1) {
                                pat += a[ii];
                            } else {
                                pat += a[ii] + "(\\s*<[^>]+>\\s*)*" + ErrorWords[i] + "(\\s*<[^>]+>\\s*)*";
                            }
                        } else {
                            pat += ErrorWords[i] + "(\\s*<[^>]+>\\s*)*";
                        }
                    }
                    pattern1 = new RegExp(pat, "gi");
                    _sourceHtml = contentHtml.match(pattern1);
                }
                for (var j = 0; _sourceHtml && j < _sourceHtml.length; j++) {
                    var s = _sourceHtml[j].replace(pattern2, '<span class="highlight_check" style="background-color: rgb(255, 0, 0);" data-suggest="'+SuggestWords[i]+'">$1</span>');
                    contentHtml = contentHtml.replace(pattern1, s);
                    pat = '(\\<span class=\\"highlight_check\\" style=\\"background\\-color\\: rgb\\(255\\, 0\\, 0\\)\\;\\" data\\-suggest\\=\\"[^>]+\\>){2,}'+ErrorWords[i]+'<\\/span><\\/span>';
                    pattern1 = new RegExp(pat, "gi");
                    contentHtml = contentHtml.replace(pattern1, '<span class="highlight_check" style="background-color: rgb(255, 0, 0);" data-suggest="'+SuggestWords[i]+'">'+ErrorWords[i]);
                }
            }
            me.setContent(contentHtml, false);
        }catch (e){
    		console.log(e);
		}
    }
    //初始化校对面板
    function initProof(){
        var ue = UE.getEditor("editor");
        var proofList = $(ue.body).find(".highlight_check");
        // 如果存在错误词列表
        if(proofList.length){
            $("#tab5 #cancelCheckBtn").show();
            var html = "<table class=\"proofread-list\" width=\"100%\">\n" +
                "<thead align=\"center\"><tr>" +
                "<td width=\"10%\">序号</td>" +
                "<td width=\"45%\">错误词</td>" +
                "<td width=\"45%\">建议词</td>" +
                "</tr></thead>\n" +
                "<tbody>\n";
            proofList.each(function(i){
                var sug = $(this).attr("data-suggest");
                var err = $(this).text();
                html += "<tr>" +
                    "<td align=\"center\">" + (i + 1) + "</td><td class=\"proofread-error\">" + err + "</td>" +
                    "<td class=\"proofread-suggest\">" + sug + "</td>" +
                    "</tr>";
            });
            html += "</tbody>\n</table>";
            $("#tab5 .proofread").html(html);
        }
    }

    //点击校对Tab触发   加载右侧校对面板并显示错误词列表
    $("ul.tabs li#checkTab").click(function (){
		//当直接点击校对Tab并且校对面板没有错误词列表时执行初始化
		if(!isCheck && !$("#tab5 .cancelCheckBtn").is('visible')){
			initProof();
		// } else {
		// 	$("#ueditor_0").contents().find(".highlight_check").css("background-color", "");
		}
    });
    // 取消校对按钮绑定的事件
    $("#tab5").delegate("#cancelCheckBtn", "click", function() {
        var contentHtml = me.getContent();
		contentHtml = contentHtml.replace(/\<span class=\"highlight_check\" style=\"background\-color\: rgb\(255\, 0\, 0\)\;\" data\-suggest\=\"[^>]+\>([^>]+)<\/span>/gi, '$1')
        me.setContent(contentHtml, false);
        $("#tab5 .proofread-list tbody").html("");
        $("#tab5 .proofread-list").hide();
        $("#tab5 #cancelCheckBtn").hide();
	});

    me.commands['check'] = {
        execCommand : function() {
            isCheck = true;
        	var contentHtml = me.getContent();
        	var reg = new RegExp('\<span class=\\"highlight_check\\" style=\\"background\\-color\\: rgb\\(', "gi");
            if(reg.test(contentHtml)){
                $("#tab5 #cancelCheckBtn").trigger('click');
            }
            $("ul.tabs #checkTab").trigger('click');
            var content = me.getContentTxt();
            if(!content){
				alert("内容不能为空");
                isCheck = false;
				return;
			}
            getList(content);
            isCheck = false;
        },
    }
}

var editorui = baidu.editor.ui;
editorui["insertword"] = function (editor) {
    var name = 'insertword',
        ui = new editorui.Button({
            className:'edui-for-' + name,
            title:editor.options.labelMap[name] || editor.getLang("labelMap." + name) || '',
            onclick:function () {},
            theme:editor.options.theme,
            showText:false
        });
    editorui.buttons[name] = ui;
    editor.addListener('ready', function() {
        var b = ui.getDom('body'),
            iconSpan = b.children[0];
        editor.fireEvent('insertwordready', iconSpan);
    });
    editor.addListener('selectionchange', function (type, causeByUi, uiReady) {
        var state = editor.queryCommandState(name);
        if (state == -1) {
            ui.setDisabled(true);
            ui.setChecked(false);
        } else {
            if (!uiReady) {
                ui.setDisabled(false);
                ui.setChecked(state);
            }
        }
    });
    return ui;
};
UE.plugin.register('insertword', function (){
    var me = this,
        isLoaded = false,
        containerBtn;

    function initUploadBtn(){
        var w = containerBtn.offsetWidth || 20,
            h = containerBtn.offsetHeight || 20,
            btnIframe = document.createElement('iframe'),
            btnStyle = 'display:block;width:' + w + 'px;height:' + h + 'px;overflow:hidden;border:0;margin:0;padding:0;position:absolute;top:0;left:0;filter:alpha(opacity=0);-moz-opacity:0;-khtml-opacity: 0;opacity: 0;cursor:pointer;';

        domUtils.on(btnIframe, 'load', function(){

            var timestrap = (+new Date()).toString(36),
                wrapper,
                btnIframeDoc,
                btnIframeBody;

            btnIframeDoc = (btnIframe.contentDocument || btnIframe.contentWindow.document);
            btnIframeBody = btnIframeDoc.body;
            wrapper = btnIframeDoc.createElement('div');

            wrapper.innerHTML = '<form id="edui_form_' + timestrap + '" target="edui_iframe_' + timestrap + '" method="POST" enctype="multipart/form-data" action="' + me.getOpt('serverUrl') + '" ' +
                'style="' + btnStyle + '">' +
                '<input id="edui_input_' + timestrap + '" type="file" accept="*" name="' + me.options.imageFieldName + '" ' +
                'style="' + btnStyle + '">' +
                '</form>' +
                '<iframe id="edui_iframe_' + timestrap + '" name="edui_iframe_' + timestrap + '" style="display:none;width:0;height:0;border:0;margin:0;padding:0;position:absolute;"></iframe>';

            wrapper.className = 'edui-' + me.options.theme;
            wrapper.id = me.ui.id + '_iframeupload';
            btnIframeBody.style.cssText = btnStyle;
            btnIframeBody.style.width = w + 'px';
            btnIframeBody.style.height = h + 'px';
            btnIframeBody.appendChild(wrapper);

            if (btnIframeBody.parentNode) {
                btnIframeBody.parentNode.style.width = w + 'px';
                btnIframeBody.parentNode.style.height = w + 'px';
            }

            var form = btnIframeDoc.getElementById('edui_form_' + timestrap);
            var input = btnIframeDoc.getElementById('edui_input_' + timestrap);
            var iframe = btnIframeDoc.getElementById('edui_iframe_' + timestrap);

            domUtils.on(input, 'change', function(){
                if(this.files[0]){
                    var fileSize = this.files[0].size;

                    var size = fileSize/1024/1024;
                    var wordMaxSize=me.getOpt('wordMaxSize')
                    if(size>wordMaxSize){
                        alert("文档不能大于"+wordMaxSize+"M");
                        return;
                    }
                }
                if(!input.value) return;
                var loadingId = 'loading_' + (+new Date()).toString(36);
                var params = UE.utils.serializeParam(me.queryCommandValue('serverparam')) || '';

                // var imageActionUrl = me.getActionUrl(me.getOpt('wordActionUrl'));
                var imageActionUrl = me.getOpt('wordActionUrl');
                var allowFiles = me.getOpt('wordAllowFiles');

                me.focus();
                me.execCommand('inserthtml', '<img class="loadingclass" id="' + loadingId + '" src="' + me.options.themePath + me.options.theme +'/images/spacer.gif" title="' + (me.getLang('simpleupload.loading') || '') + '" >');

                function callback(){
                    try{
                        var link, json, loader,
                            body = (iframe.contentDocument || iframe.contentWindow.document).body,
                            result = body.innerText || body.textContent || '';
                        json = (new Function("return " + result))();
                        link = me.options.imageUrlPrefix + json.url;
                        if(json.state == 'SUCCESS') {
                            loader = me.document.getElementById(loadingId);
                            loader && domUtils.remove(loader);
                            if(json.result)
                                me.setContent(me.getContent()+json.result);
                            else
                                me.setContent(me.getContent()+"&nbsp;");//插入空记录必须放个空字符串，否则会导致字体错误
                            me.focus();
                        } else {
                            showErrorLoader && showErrorLoader(json.state);
                        }
                    }catch(er){
                        showErrorLoader && showErrorLoader(me.getLang('simpleupload.loadError'));
                    }
                    form.reset();
                    domUtils.un(iframe, 'load', callback);
                }
                function showErrorLoader(title){
                    if(loadingId) {
                        var loader = me.document.getElementById(loadingId);
                        loader && domUtils.remove(loader);
                        me.fireEvent('showmessage', {
                            'id': loadingId,
                            'content': title,
                            'type': 'error',
                            'timeout': 4000
                        });
                    }
                }

				/* 判断后端配置是否没有加载成功 */
                if (!me.getOpt('wordActionUrl')) {
                    errorHandler(me.getLang('autoupload.errorLoadConfig'));
                    return;
                }
                // 判断文件格式是否错误
                var filename = input.value,
                    fileext = filename ? filename.substr(filename.lastIndexOf('.')):'';
                if (!fileext || (allowFiles && (allowFiles.join('') + '.').indexOf(fileext.toLowerCase() + '.') == -1)) {
                    showErrorLoader(me.getLang('insertword.exceedTypeError'));
                    return;
                }

                domUtils.on(iframe, 'load', callback);
                form.action = utils.formatUrl(imageActionUrl + (imageActionUrl.indexOf('?') == -1 ? '?':'&') + params);
                var textmode = me.getOpt('textmode');
                form.action =form.action+(form.action.indexOf('?') == -1 ? '?':'&') + "textmode="+textmode;
                form.submit();
            });

            var stateTimer;
            me.addListener('selectionchange', function () {
                clearTimeout(stateTimer);
                stateTimer = setTimeout(function() {
                    var state = me.queryCommandState('insertword');
                    if (state == -1) {
                        input.disabled = 'disabled';
                    } else {
                        input.disabled = false;
                    }
                }, 400);
            });
            isLoaded = true;
        });

        btnIframe.style.cssText = btnStyle;
        containerBtn.appendChild(btnIframe);
    }

    return {
        bindEvents:{
            'ready': function() {
                //设置loading的样式
                UE.utils.cssRule('loading',
                    '.loadingclass{display:inline-block;cursor:default;background: url(\''
                    + this.options.themePath
                    + this.options.theme +'/images/loading.gif\') no-repeat center center transparent;border:1px solid #cccccc;margin-right:1px;height: 22px;width: 22px;}\n' +
                    '.loaderrorclass{display:inline-block;cursor:default;background: url(\''
                    + this.options.themePath
                    + this.options.theme +'/images/loaderror.png\') no-repeat center center transparent;border:1px solid #cccccc;margin-right:1px;height: 22px;width: 22px;' +
                    '}',
                    this.document);
            },
			/* 初始化简单上传按钮 */
            'insertwordready': function(type, container) {
                containerBtn = container;
                me.afterConfigReady(initUploadBtn);
            }
        },
        outputRule: function(root){
            UE.utils.each(root.getNodesByTagName('img'),function(n){
                if (/\b(loaderrorclass)|(bloaderrorclass)\b/.test(n.getAttr('class'))) {
                    n.parentNode.removeChild(n);
                }
            });
        },
        commands: {
            'insertword': {
                queryCommandState: function () {
                    return isLoaded ? 0:-1;
                }
            }
        }
    }
});


/**************拖拽相关稿件的实现*************************/
//  \xy\article\script\article-rel.js initDrag()实现dragstart
setTimeout(function(){
    var _body= $("#ueditor_0").contents().find('body');

    _body.on("dragenter",function(ev){
        if(window.isCrossIFrameDragging) {
          //  $(ev.target).text("enter");
        }
    }).on('dragleave', function(ev) {
        if(window.isCrossIFrameDragging) {
          //  $(ev.target).text("leave");
        }
    }).on("dragover",function(ev){
        if(window.isCrossIFrameDragging) {
            ev.preventDefault();
            ev.originalEvent.dataTransfer.dropEffect = 'move';
        }
    }).on("drop",function(ev){
        var df = ev.originalEvent.dataTransfer;
        var data = df.getData("Text").split('_____!_____');
        var _relhtml='<a href="'+data[1]+'" url="'+data[1]+'"  urlpad="'+data[2]+'" class="founder_rel">'+data[0]+'</a>';
        var editor = UE.getEditor('editor');
        editor.execCommand("insertHtml",_relhtml);
    });
},3000)
/**************拖拽相关稿件的实现*************************/
/**
 * 处理微信图片
 */
UE.plugins.wximage =
    function() {
        var u = baidu;
        var a = this
            , c = u.editor.ui
            , b = u.editor.dom.domUtils;

        function strip_stack_span(html) {
            var docObj = $('<div>' + html + '</div>');
            docObj.find('li,colgroup,a').each(function() {
                    if ($.trim($(this).text()) == "" && $(this).find('img').size() == 0) {
                        $(this).remove();
                    }
                }
            );
            var has_secspan = false;
            do
            {
                has_secspan = false;
                docObj.find('span:has(span)').each(function(i) {
                        var innerobj = $(this).find('> span');
                        if (innerobj.size() > 1) {
                            $(this).find('span').each(function() {
                                    if ($.trim($(this).text()) == "") {
                                        $(this).replaceWith($(this).html());
                                    }
                                }
                            )
                            return;
                        }
                        else if (innerobj.size() == 0) {
                            return;
                        }
                        if ($.trim($(this).text()) == $.trim(innerobj.text())) {
                            has_secspan = true;
                            var style = $(this).attr('style');
                            var innserstyle = innerobj.attr('style');
                            var newStyle = '';
                            if (style && style != "") {
                                newStyle += ';' + style;
                            }
                            if (innserstyle && innserstyle != "") {
                                newStyle += ';' + innserstyle;
                            }
                            var new_html = '';
                            $(this).find('> *').each(function() {
                                    if (this.tagName == "SPAN") {
                                        new_html += $(innerobj).html();
                                    }
                                    else {
                                        new_html += $(this).prop('outerHTML');
                                    }
                                }
                            );
                            $(this).attr('style', newStyle).html(new_html);
                        }
                    }
                );
            } while (has_secspan);return docObj.html();
        }

        a.addListener("beforepaste", function(b, c, g) {
                b = c.html;
                "function" == typeof strip_stack_span && (b = strip_stack_span(b));
                b = $("<div>" + b + "</div>");
                b.find("img").each(function() {
                        var a = ""
                            , a = this.src && "" != this.src ? this.src :
                            $(this).attr("data-src");
                        $(this).removeAttr("data-src");
                        "undefined" == typeof a || "" == a ? $(this).remove() : (a = a.replace(/http:\/\/mmbiz.qpic.cn/g, "https://mmbiz.qlogo.cn"),
                            a = a.replace(/https:\/\/mmbiz.qpic.cn/g, "https://mmbiz.qlogo.cn"),
                            a = a.replace(/http:\/\/mmbiz.qlogo.cn/g, "https://mmbiz.qlogo.cn"),
                            a = a.replace(/&wxfrom=\d+/g, ""),
                            a = a.replace(/wxfrom=\d+/g, ""),
                            a = a.replace(/&wx_lazy=\d+/g, ""),
                            a = a.replace(/wx_lazy=\d+/g, ""),
                            a = a.replace(/wx_fmt=\S+/g, ""),
                            a = a.replace(/&tp=[a-z]+/g, ""),
                            a = a.replace(/tp=[a-z]+/g, ""),
                            a = a.replace(/\?&/g, "?"),
                            $(this).attr("src", a),
                            $(this).attr("_src", a))
                    }
                );
                c.html = b.html()
            }
        );

    };

// $(document).on("input","#findSourceInput",function(){
// 	alert(123);
// })

