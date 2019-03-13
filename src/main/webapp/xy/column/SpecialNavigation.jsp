	<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>栏目树checkbox选择</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body style="margin-bottom:0">
	<%@include file="Tree.inc"%>
	<script>
		//渠道
		col_tree.ch = "<c:out value="${param.ch}"/>";

		//url : xy/column/ColumnCheck.jsp?type=&ids=
		
		//设置栏目树需要的参数
		col_tree.check.ids = "<c:out value="${param.ids}"/>";
		col_tree.siteID = parent.siteID;
		if (!col_tree.siteID) col_tree.siteID = 1;
		col_tree.check.enable = true;

		/* //是否使用缓存
		var usecache = "<c:out value="${param.cache}"/>";
        col_tree.rootUrl = "../../xy/special/Tree.do";
        col_tree.rootPath = "../../xy/special/";

        col_tree.rootUrl += "?special=1&ch="+col_tree.ch;


		//默认是复选，需单选时可加参数style=radio
		if ("<c:out value="${param.style}"/>" == "radio") {
			col_tree.check.chkStyle = "radio";
		} 
		col_tree.check.chkboxType = {"Y":"","N":""};*/
		
		//调整为展示可操作栏目树
		var usecache = "<c:out value="${param.cache}"/>";
			col_tree.rootUrl = "../../xy/colcache/Tree.do";
			col_tree.rootPath = "../../xy/colcache/";
		
			//取有操作权限的树
			col_tree.rootUrl += "?op=1&ch=" + col_tree.ch;
			//默认是复选，需单选时可加参数style=radio
			if ("<c:out value="${param.style}"/>" == "radio") {
				col_tree.check.chkStyle = "radio";
			} 
			col_tree.check.chkboxType = {"Y":"","N":""};
		

		function getChecks() {
			try {
				parent.columnClose(col_tree.getFilterChecks(), col_tree.getChecks());
			} catch (e) {
				//var hint = "父窗口应实现columnClose(filterChecked, checked)方法供栏目树关闭时调用。"
				//	+ "\n   每个参数的格式是:  [ids, names, cascadeIDs]"
				//alert(hint);
			}
		}
		
		function doCancel() {
			try {
				parent.columnCancel();
			} catch (e) {
				//var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
				//alert(hint);
			}
		}
		
		//提供一个接口供父级窗口调用
		function  getAllFilterChecked(){
			return col_tree.getChecks();
		};
		function updateColumnNum(i){
			var colNum = $("#colNum",parent.document);
			var num = colNum.html();
			var num = parseInt(num)+i;
			colNum.html(num);
		};
		var colClick0 = function(event, treeId, treeNode, clickFlag) {
			dealClick(treeNode);
		};
		function dealClick(treeNode){
			var colID = treeNode.id
			if(treeNode.checked){//选中
				var ul = $("#column-tags",parent.document);
				var tId = treeNode.tId;
				var colName = treeNode.name;
				var li = $('<li id="'+colID+'_col" data-id="'+colID+'" class="pull-left"></li>');
				li.html(colName);
				var img = $('<img id="'+colID+'_del" data-tid="'+tId+'" data-id="'+colID+'" class="posAbsolute closeBtn" src="" alt=""/>');
				li.append(img);
				ul.append(li);
				img.attr("src","../export/images/navMenu/navMenu2.png");
				img.click(delColClick);
				updateColumnNum(1);
			}else{
				$('#'+colID+'_col',parent.document).remove();
				updateColumnNum(-1);
			}
		}
		col_tree.colClick0 = colClick0;
		function _check(event,treeId,treeNode){
			dealClick(treeNode);
		};
		function _beforeCheck(treeId,treeNode){
			return false;
		}
		col_tree._beforeCheck = _beforeCheck;
		col_tree._check = _check;
		function delColByTId(tId){
			var node = col_tree.tree.getNodeByTId(tId);
			col_tree._nodeClick(node);
		}
		function delColClick(event){
			var target = event.target;
			var tId = $(target).attr("data-tid");
			delColByTId(tId);
		}
		function removeAll(){
			var nodes = col_tree.tree.getCheckedNodes();
			for(var i = 0; i < nodes.length; i++){
				col_tree._nodeClick(nodes[i]);
			}
		}
    //选中节点
    function _checkNode(n, parentTree){
        //记住选中的节点
        special_navigation.selecteds = n;
        //展开选中节点的父级节点
        _expandNode(parentTree);
//			setTimeout(function(){
//				$("#rs_tree li").each(function(){
//					var _this = $(this);
//					for(var i=0; i<n.length; i++){
//						var _title=$.trim(_this.children("a").attr("title"));
//						if(_title.indexOf("["+n[i]+"]")!=-1){
//							_this.children("a").click();
//						}
//					// $("#rs_tree_"+n[i]+"_a").click();
//					}
//				})
//			},100)
    }

		// 在选中节点之前,展开其父节点
		function _expandNode(nodes){
			var zTree = col_tree.tree;//获取树对象
			//["{'level':'level1'---'columnId':'6'}", "{'level':'level0'---'columnId':'1'}"]parentTree的栏目ID.
			//数据解析
      for(var i=0; i<nodes.length; i++){
				var _node = nodes[i];
				nodes[i] = _node = JSON.parse(_node.replace(/\-\-\-/gi, "\,").replace(/\'/gi, "\""));
				var level = _node.level;
				_node.level = level.replace('level', '');
			}
			nodes.sort(compare('level'));
      //展开是异步的，所以要等节点展开后再展开下一级节点 这里用onAsyncSuccess
      for(var i=0; i<nodes.length; i++){
          //展开没有展开的节点
          var id = $("a.level"+nodes[i].level+"[title$='[" + nodes[i].columnId + "]']").closest('li').attr('id');
          var _treeNode = zTree.getNodeByTId("rs_tree_" + id);

          if(_treeNode && !_treeNode.open){
              special_navigation.choose_columns_id = i;
              special_navigation.choose_columns_nodes = nodes;
              special_navigation._expanded(nodes, i);
              break;
          }
      }
		}
		// 对象数组根据某个属性排序
		function compare(property){
			return function(a,b){
				var value1 = a[property];
				var value2 = b[property];
				return value1 - value2;
			}
		}
		//按钮
		$("#divColBtn").hide();

    var special_navigation = {
        //---选中记住功能。若选中的是深层的栏目，则按路径层层展开---
        choose_columns_id : null,
        choose_columns_nodes : null,
        //勾选选中的节点
        _clickNode: function(){
            var n = special_navigation.selecteds;
            if (!n){
                return;
            }
            $("#rs_tree li").each(function(){
                var _this = $(this);
                for(var i=0; i<n.length; i++){
                    var _title=$.trim(_this.children("a").attr("title"));
                    if(_title.indexOf("["+n[i]+"]")!=-1){
                        _this.children("a").click();
                    }
                    // $("#rs_tree_"+n[i]+"_a").click();
                }
            });
            special_navigation.selecteds = null;
        },
        _expanded: function(nodes, i) {
            if (i < nodes.length) {
                var zTree = col_tree.tree;//获取树对象
                var level = node[i].level;
                $("#rs_tree li.level" + level).each(function () {
                    var _this = $(this);
                    var _title = $.trim(_this.children("a").attr("title"));
                    if (_title.indexOf("[" + nodes[ i ].columnId + "]") != -1) {
                        var node = zTree.getNodeByTId(_this.attr("id"));
                        zTree.expandNode(node, true, false, true);
                        special_navigation.choose_columns_id++;
                        return false;
                    }
                });
            }else {
                special_navigation._clickNode()
                return;
            }
        },
    };
	</script>
	<script type="text/javascript">
		$(function(){
			//$(parent.window).height() + "px"
			//document.getElementById("rs_tree").style.height = 500;
			$("#rs_tree", window.parent.document).css("height","400px");
		});
	</script>
</body>
</html>