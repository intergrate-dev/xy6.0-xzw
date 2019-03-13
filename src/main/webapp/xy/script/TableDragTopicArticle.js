/**
 * 初始化一个监听方法，当table数据更新完成时，使新生成的table可以移动
 */
e5.mod("article.drag", function() {
	var api;
	var oldOrder ="";
	var listening = function() {
		
		//把首行td的样式赋给其他每一行，否则当使用jquery-ui-sortable组件，并且把任意tr移到第一组时，样式会改变
		$("#listing table tr").each(function(e){
			var tdSize = $(this).find("td").size();
			for(var i = 0 ; i < tdSize; i++){
				$(this).find("td").eq(i).attr("style", $("#listing table tr").eq(0).find("td").eq(i).attr("style") );
			}
		});
		
		
		//设置table可移动
		//使用 dragSort组件，使表格tr达到移动的效果
		//dragSort("#listing table");
		
		/**/
		//使用jquery-ui-sortable达到表格tr移动的效果
		//注意： 当使用sortable的时候，需将结尾的requires加到mod方法里面
		//$("#listing table tbody").sortable({axis: 'y',containment: 'parent', items:'tr'  });

		//多行拖拽排序
        var group_str='';
        var needRemove=false;
        $("#listing table tbody").sortable({
            axis: 'y',
            containment: 'parent',
			tolerance: 'pointer',
            //拖拽显示内容
            helper:function(event,ui){
                return $("<div style='background: #dae9f3;line-height: 2.4;'>正在排序中...</div>");
            },
            start:function( event, ui ){
                group_str='';
                //选中的个数
                var selecteds=jQuery("#listing table tbody>tr.selected:not(.ui-sortable-placeholder):not(.ui-sortable-helper)");
                var selecte_num=selecteds.length;
                if(selecte_num>1 && ui.item.hasClass('selected')){
                    selecteds.each(function(){
                         var _id=$(this).attr("id");
                         var _libid=$(this).attr("libid");
                         group_str +='<tr id="'+_id+'" libid="'+_libid+'" class="selected">'+$(this).html()+'</tr>';
                        //group_str +=$(this).prop('outerHTML');
                        $(this).addClass("delete_flag");
                        $(this).css("display", "none");
                    });
                    needRemove=true;
                }else{
                    needRemove=false;
				}
            },
            stop:function( event, ui ){
            	if(needRemove){
                     ui.item.before(group_str);
                     $("#listing table tbody>tr.delete_flag").remove();
				}
            },

        });

		//$("#listing table tbody").disableSelection();
		//获取table 的顺序
		oldOrder = getOrderOfTableFn();
		
		
		
	};
	var init = function(sandbox) {
		api = sandbox;
		api.listen("workspace.doclist:setDataFinish", listening);
	};
	
	/**
	 * 点击 更新排序 按钮的时候，调用该方法
	 */
	var updateOrderOfTable = function(){
		//判断新顺序和原来的顺序是否一样
		var newOrder = getOrderOfTableFn();

		if(newOrder==oldOrder){
			alert("顺序未改变！");
			return;
		}
		var str = "{"+ getUpdateOrderlist(oldOrder)+"}";
		
		var json = eval('(' + str + ')');
		var result = "1";

        var topicID = $("#topicID").val();
        var channel = $("#channel").val();

		//把更新后的数据上传到数据库当中
		$.ajax({
			async: false,
			url : "../xy/articleorder/updateTopicArticleOrder.do", //xy/article/updateArticleOrder.do",
			type : 'POST',
			data : {"jsonStr":str,topicID:topicID,channel:channel
					},
			dataType : 'json',
			success : function(data, status) {
				if(data.status == 1 || data.status == "1"){
                    $("#btnRefresh").click();
                    alert("更新成功！");
                }else{
                    alert("更新失败！");
                    console.log(data);
                }
			},
			error : function(xhr, textStatus, errorThrown) {
				result = "更新出现异常！请稍后再试！";
				if(console){
					console.error(xhr);
					console.error(textStatus);
					console.error(errorThrown);
				}
			}
		});
		return result;
	};
	
	/**
	 * 获得当前table的顺序， 例如 2,3,1,4,5,
	 * @returns
	 */
	var getOrderOfTableFn = function(){
		var order = new Array();
		for(var _x =0, _size= $("#listing table").find(".list-order").size() ; _x < _size; _x ++){
			order.push($("#listing table").find("tr").eq( _x).attr("id"));
		}
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
		for(var _x =0 , size = oldl.length; _x < size ; _x++){
			//取出新的order
			newOrder = $("#listing table").find("tr").eq( _x).attr("id");
			//如果跟以前的顺序不一致
			if(oldl[ _x] != newOrder){
				//updateL.push(newl[i]);
				updateL.push( oldl[ _x] + ":" + newOrder );
			}
		}
		return updateL.join(",");
	};
	
	return {
		init : init,
		updateOrderOfTable : updateOrderOfTable
		
	};
}
,{requires:["../e5script/jquery/jquery.min.js",'../e5script/jquery/jquery-ui/jquery-ui.min.js']}
);
function updateOrderOfTable(){
	return e5.mods["article.drag"].updateOrderOfTable();
}





