/**
 * 初始化一个监听方法，当table数据更新完成时，使新生成的table可以移动
 */
e5.mod("article.drag", function(){
        var api;
        var oldOrder = "";
        var listening = function(){


            /**/
            //使用jquery-ui-sortable达到表格tr移动的效果
            //注意： 当使用sortable的时候，需将结尾的requires加到mod方法里面
            $("#listing").sortable({tolerance: "pointer"});
            $("#listing").css("display", "block");
            $("#doclistframe").css("overflow", "hidden");
            //$("#listing table tbody").disableSelection();
            //获取table 的顺序
            oldOrder = getOrderOfTableFn();
        };
        var init = function(sandbox){
            api = sandbox;
            api.listen("workspace.doclist:setDataFinish", listening);
        };

        /**
         * 点击 更新排序 按钮的时候，调用该方法
         */
        var updateOrderOfTable = function(url, colID, DocLibID, callback){
            //判断新顺序和原来的顺序是否一样
            var newOrder = getOrderOfTableFn();
            /**/
            if(newOrder == oldOrder){
                return;
            }
            var str = "{" + getUpdateOrderlist(oldOrder) + "}";

            var json = eval('(' + str + ')');
            //把更新后的数据上传到数据库当中
            $.ajax({
                async: false,
                url: "../" + url + ".do", //xy/article/updateArticleOrder.do",
                type: 'POST',
                data: {
                    "jsonStr": str,
                    "colID": colID,
                    "DocLibID": DocLibID
                },
                dataType: 'json',
                success: function(data, status){
                    // 如果成功了之后，在form里面添加一个显示icon的table
                    //console.info(data);
                    //var tool = e5.mods["workspace.toolkit"];
                    //tool.self.closeOpDialog("OK", 2);
                    if(callback){
                        callback();
                    }
                },
                error: function(xhr, textStatus, errorThrown){
                }
            });

        };

        /**
         * 获得当前table的顺序， 例如 2,3,1,4,5,
         * @returns
         */
        var getOrderOfTableFn = function(){
            var order = new Array();
            $("#listing").children("table").each(function(){
                order.push($(this).attr("id"));
            });
            return order.join(",");

        };

        /**
         * 获得json格式的数据，例如 2:3,4:23,5:32
         * @returns
         */
        var getUpdateOrderlist = function(oldList){
            //console.info(oldList);
            var oldl = oldList.split(",");
            var updateL = new Array();
            for(var _x = 0, size = oldl.length; _x < size; _x++){
                //取出新的order
                newOrder = $("#listing").children("table").eq(_x).attr("id");
                //如果跟以前的顺序不一致
                if(oldl[_x] != newOrder){
                    //updateL.push(newl[i]);
                    updateL.push(oldl[_x] + ":" + newOrder);
                }
            }
            return updateL.join(",");
        };

        return {
            init: init,
            updateOrderOfTable: updateOrderOfTable

        };
    }
    , {requires: ["../e5script/jquery/jquery.min.js", '../e5script/jquery/jquery-ui/jquery-ui.min.js']}
);
function updateOrderOfTable(url, colID, callback){
    e5.mods["article.drag"].updateOrderOfTable(url, colID, callback);
}





