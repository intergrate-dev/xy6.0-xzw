e5.mod("workspace.search",function() {
	var api;
	var searchClick = function(colId, title, typeselect,sys_id) {

		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
			
		var rule = "";
				
		// if (sys_id) rule += "_AND_sys_documentID_EQ_" + sys_id;
		//选择栏目
		if (colId){
			var array = new Array();
			array = colId.split(",");
			if(array.length==1){
				rule += "_AND_a_columnID_EQ_" + colId;
			}else{
				rule += "_AND_a_columnID in (";
				for(var i=0;i<array.length;i++){
					rule += array[i];
					if(i!=array.length-1){
						rule += ",";
					}else{
						rule += ")";
					}
				}
			}	
		} 
		//页面区块内容选稿
		if(search_art.type == 1 && typeselect){
			rule += "_AND_a_type_EQ_" + typeselect;
		}
		
		//合成多标题
		if(search_art.type == 0 && typeselect){
			rule += "_AND_a_type_EQ_" + typeselect;
		}
		
		//挂件选择：组图稿/视频稿
		if (search_art.type == 2){
			rule += "_AND_a_type_EQ_1";
		} else if (search_art.type == 3){
			rule += "_AND_a_type_EQ_2";
		}
		//基本查询条件,已发布、站点
		rule += "_AND_a_status_EQ_1_AND_a_siteID_EQ_" + main_param.siteID;
        if (search_art.type == 4){
            if(title!=""){
                rule += "_AND_-SYS_DOCUMENTID_EQ_"+search_art.docIDRel;
            }else{
                rule += "_AND_SYS_DOCUMENTID!="+search_art.docIDRel;
            }
        }

        //最后去掉_AND_
		searchparam.ruleFormula = rule.substring(5);
		
		//标题
		if (title != ""){
			//searchparam.query = "(SYS_TOPIC like '%"+title+"%')";
			searchparam.query = "@QUERYCODE@=qArticle"+"&" + "SYS_TOPIC" + "=" + e5.utils.encodeSpecialCode(title);
		}
		//稿件id
		if (sys_id != ""){
		    var query = searchparam.query;
            query = query.indexOf("@QUERYCODE@=qArticle")!=-1 ? query+"&" + "SYS_DOCUMENTID" + "=" + sys_id : "@QUERYCODE@=qArticle"+"&" + "SYS_DOCUMENTID" + "=" + sys_id;
            searchparam.query = query;
        }
		api.broadcast("searchTopic", searchparam);
	};
	var checkLoad = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var ready = !!statusReady&&statusReady();
		if (!ready) {
			setTimeout(checkLoad, 100);
			return;
		}
		
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
			
		api.broadcast("searchTopic", searchparam);
	}
	
	var init = function(sandbox) {
		api = sandbox;
	}
	var onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload: onload,
		searchClick : searchClick
	}
},{requires:[
"../e5workspace/script/Param.js",
"../e5script/e5.utils.js"
]});
