/**
 * 收藏夹相关的JS
 * Created by wenkx on 2016/9/30.
 */
//----------------------------------------------------------------------------------
    //收藏夹
var favoriteDiv={
        type :"",
        siteID : "",
        id : '',
        //初始化给ul节点绑定事件
        init : function(){
            //加载栏目树
            favoriteDiv.loadColl();
            $("#menuView-col").click(col_menu.view);
            $("#menuDetails-col").click(col_menu.details);
            $("#menuPubCol-col").click(col_menu.publishCol);
            $("#menuPub-col").click(col_menu.publish);
            $("#menuDelFromColl-col").click(favoriteDiv.delFrom_collect);

            var p = col_menu.permision;
            if (p.p3 != "true") {
                $("#menuPub-col").hide();
            }
          //  $(".MyFavorite_li").mousedown(favoriteDiv.collClick(event));
            $(document).on({"mousedown":function(){
                var obj=$(this);
                favoriteDiv.collClick(event,obj);
            }},'.MyFavorite_li');

            //点击桌面其他位置，让div隐藏
            document.onclick=function(){
                $("#rMenu-col").hide();
            }

        },
        //加载我的收藏
        loadColl : function(){
            favoriteDiv.siteID = col_tree.siteID;
            favoriteDiv.type = col_tree.ch+6;

            //加载我的收藏里面的东西
            $.ajax({
                type:"post",
                url:"../xy/colcache/myFavorite.do?ch=" + col_tree.ch + "&siteID=" + favoriteDiv.siteID,
                success:function(data){
                    $("#MyFavorite_ul").text("");
                    var content = '';
                    var myCollection = eval(data);
                    if(myCollection!=null){
                        for(var i=0;i<myCollection.length;i++){
                            content+='<li  class="MyFavorite_li" '+'casID="'+myCollection[i]["casID"]+'" colID = "' + myCollection[i]["id"]+'">['+myCollection[i]["id"]+']'+myCollection[i]["name"]+'</li>'
                        }
                        $("#MyFavorite_ul").append(content);
                    }

                }
            });
        },

        //从收藏夹中删除
        delFrom_collect : function(){
            $("#rMenu-col").hide();
            var id = favoriteDiv.id
            var type = favoriteDiv.type
            var siteID = favoriteDiv.siteID
            $.ajax({
                url:"../xy/user/RelDel.do?siteID="+siteID+"&type="+type+"&id="+id,
                success:function(data){
                    var data =data;
                    if(data=="ok"){
                        alert("删除成功！");
                    }else{
                        alert("删除失败，请联系管理员！");
                    }
                    favoriteDiv.loadColl()
                }
            });
        },


        showMyColl : function (){
            if($("#MyFavorite").is(":hidden")){
                $("#MyFavorite").show();
                $("#rs_tree").hide();
            }else{
                $("#MyFavorite").hide();
            }
        },

        showZtree : function (){
            if($("#rs_tree").is(":hidden")){
                $("#rs_tree").show();
                $("#MyFavorite").hide();
            }else{
                $("#rs_tree").hide();
            }
        },


        //我收藏的栏目鼠标点击事件
        collClick : function (event,obj){
            // 权限判断
            var _this=obj;
            //改变栏目节点颜色
            _this.css({"background":"#FFE6B0"}) .siblings().css({"background":"#FFFFFF"});
            var casID = _this.attr("casID");
            favoriteDiv.id  = _this.attr("colID");
            if(0!=favoriteDiv.find(casID)) {
                //左键选中 右键显示
                if (1 == event.which) {  //鼠标左键 发送请求

                    var statusReady = e5.mods["workspace.doclist"] && e5.mods["workspace.doclist"].self;
                    var searchReady = e5.mods["workspace.search"] && e5.mods["workspace.search"].init;
                    var curNode={'id':favoriteDiv.id};

                    var treeClick = e5.mods["workspace.resourcetree"].treeClick;
                    treeClick(curNode);
                } else if (3 == event.which) {//鼠标右键  js弹出div菜单。
                    var x = event.clientX;
                    var y = event.clientY;
                    var menuHeight = 100;
                    if (y + menuHeight > $(window).height()) {
                        y = $(window).height() - menuHeight;
                    }

                    $("#rMenu-col").show().css({"left": x, "top": y});


                }
            }
        },
        //利用ztree搜索来判断栏目是否存在和栏目权限
        _ids_to_find : null,
        find : function(casID) {
            //casID:要定位的栏目的级联ID
            favoriteDiv._ids_to_find = casID.split("~");
            favoriteDiv._finding();
        },
        _finding : function() {
            var ids = favoriteDiv._ids_to_find;
            if (!ids || ids.length == 0) return;

            var id = ids[ids.length - 1];
            var col = col_tree.tree.getNodeByParam("id", id, null);
            if (col) {
                //若当前的树中可以找到了，则终止后续动作，定位，结束。
                favoriteDiv._ids_to_find = null;

                return 1;
            }

            //否则，按路径层层展开。这种展开是异步的
            var parent = null;
            while (ids.length > 0) {
                var col = col_tree.tree.getNodeByParam("id", ids[0], parent);
                if (!col) break;
                parent = col;
                ids.splice(0,1); //去掉一层父路径，[1,2,3]===>[2,3]
            }
            if (parent == null) {
                favoriteDiv._ids_to_find = null;

                if(confirm("栏目已删除或没有权限，是否从收藏夹移除?")){
                    favoriteDiv.delFrom_collect();
                }
                return 0;
            }
            favoriteDiv._ids_to_find = ids; //异步展开后，可以按_ids_to_find继续
            //col_tree.tree.expandNode(parent, true);
        }
}