//写稿与修改稿件中的相关稿件
var article_rel = {
	reljsonData : '',
	possiblejsonData : '',
	artDialog : null,
	init : function() {
		article_rel.initRelData();
		
		article_rel.loadRelList();	
		
		article_rel.removeArt();

		//拖拽相关稿件
        article_rel.initDrag('#ul5');
		
		$("#linklirels").click(article_rel._relSelect);
		$("#linkliRelbtn").click(article_rel._relSelect);
		$("#linkliRelclear").click(article_rel._relClear);
		
		$("#relarts").click(article_rel.initPossibleData);
		//$("body").on("click",".close", article_rel.removeRel);
		
		if($("#possible ul").size() > 0 ){
			for(var i = 0, size = $("#possible ul").size(); i < size; i++){
				$("#" + $("#possible ul").eq(i).attr("id")).mouseover(function(){
					$(this).find(".dosure").show();
					$(this).css("background","#e8f5fb");
				});
				
				$("#" + $("#possible ul").eq(i).attr("id")).mouseout(function(){
					$(this).find(".dosure").hide();
					$(this).css("background","#fff");
				});
			}
		}
	},
	removeArt:function(){
		if($("#ul5 ul").size() > 0 ){
			for(var i = 0, size = $("#ul5 ul").size(); i < size; i  ++){
				$("#" + $("#ul5 ul").eq(i).attr("id")).mouseover(function(){
					$(this).find(".close").show();
					$(this).parent().find("ul").css("background","#fff");
					$(this).css("background","#e8f5fb");
				});
				
				$("#" + $("#ul5 ul").eq(i).attr("id")).mouseout(function(){
					$(this).find(".close").hide();
					$(this).css("background","#fff");
				});
				
				$("#" + $("#ul5 ul").eq(i).attr("id")).find(".close").click(article_rel.removeRel);
			}
		}
		$(".colCasName").find(".close").click(article_rel.removeRel);
	},
	removeRel : function(){
		var trID = $(this).closest('ul').attr('id');
			var jsObject = article_rel.reljsonData;
			for(var p in jsObject)  
			{ 
				if(jsObject[p].id == trID){
					delete article_rel.reljsonData[p]; 
					break;
				}
			}
			$(this).closest('ul').remove(); 
			
			var relObject = article_rel.reljsonData;
			var bHasData = false;
			//删除一个后判断是否还有相关稿件
			for(var rel in relObject)  
			{ 
				if( relObject[rel].id == undefined){
					continue;
				} else {
					bHasData = true;
					break;
				}
			}
				
			if(bHasData == false){
				$('#divRelBtns').hide();
				$('#divRelInitBtn').show();
			}
	        return false; 
	},
	//清空
	_relClear : function() {
		var jsObject = article_rel.reljsonData;
		for(var p in jsObject)  
		{ 
			if( jsObject[p].id == undefined) continue;
			delete article_rel.reljsonData[p]; 
		}
		$('#ul5').html('');
		//清空后更换按钮
		$('#divRelBtns').hide();
		$('#divRelInitBtn').show();
	},
	//选择
	_relSelect : function() {
		var dataUrl = "../../xy/MainArticle.do?siteID=" + article.siteID + "&type=" + 4 + "&ch=" + article.ch+"&docID="+article.docID;

		article_rel.artDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			title : "相关稿件",
			width : "800px",
			height : "450px",
			resizable : false,
			fixed : true
		});
		article_rel.artDialog.show();
	},
    initDrag:function(dragbox){
        $(dragbox).sortable({
			items:"ul",
			cancel:".dragmMove"
		});
        $(dragbox).find('.dragmMove').unbind("dragstart").unbind("dragend");
        $(dragbox).find('.dragmMove').on("dragstart",function(ev){
             var dt = ev.originalEvent.dataTransfer;
             var _title=$(this).parent().find('.article-title').text();
             var _url=$(this).parent().attr('data-url');
			 var _urlpad=$(this).parent().attr('data-urlpad');
             dt.setData('Text',_title+'_____!_____'+_url+'_____!_____'+_urlpad);
             dt.effectAllowed = 'move';
             window.isCrossIFrameDragging = true;
             //window.draggingItem = this;
        }).on("dragend",function(ev){
            window.isCrossIFrameDragging = false;
        });
},
	loadRelList : function() {
		var htmlStr = ""; 
		var jsObject = article_rel.reljsonData;
		for(var p in jsObject)  
		{
			if( jsObject[p].id == undefined) continue;
			htmlStr += "<ul data-url='"+jsObject[p].url+"' data-urlpad='"+jsObject[p].urlPad+"' style='padding:0 10px;cursor:pointer;position: relative;' id="+jsObject[p].id+">"+
			"<li style=' display:none;'>"+jsObject[p].id +"</li>"+
			"<span style='color: #ff9900;font-size: 25px; margin:-15px 0 0 -12px; float:left;'>.</span><li><a herf='"+jsObject[p].url+"' class='article-title'>"+jsObject[p].title+"</a></li>"+
			"<li class='pubTime'>"+jsObject[p].pubTime +"</li>"+
			"<li class='colCasName'>"+jsObject[p].colCasName +"<a class='close' herf='#' class='removeRel''>×</a></li>"+

            "<li draggable='true' class='dragmMove icon-move' title='拖动到左侧编辑器添加文章链接' style='position: absolute;right: 1px;top: 4px;'><a herf='#'></a></li>"+

			"</ul>"; 
			$('#ul5').append(htmlStr);
			$('#ul5').append("<div style='border-bottom:1px dashed #ddd;'></div>");
			htmlStr = '';
		} 
		
		var relObject = article_rel.reljsonData;
		var bHasData = false;
		//删除一个后判断是否还有相关稿件
		for(var rel in relObject)  
		{ 
			if( relObject[rel].id == undefined){
				continue;
			} else {
				bHasData = true;
				break;
			}
		}
			
		if(bHasData == false){
			$('#divRelBtns').hide();
			$('#divRelInitBtn').show();
		} else {
			$('#divRelBtns').show();
			$('#divRelInitBtn').hide();
		}
	},
	
	initRelData : function() {
		$.ajax({
			url : "../../xy/article/Rels.do",
			async : false,
			data : {
				"DocIDs" : article.docID,
				"DocLibID" : article.docLibID
			},
			dataType : 'json',
			success : function(datas) {
				article_rel.reljsonData = datas;
			}
		});
	},
	
	loadPossibleData :function() {
		var htmlStr = ""; 
		var jsObject = article_rel.possiblejsonData;
		$('#possible').empty();
		for(var p in jsObject)  
		{  
			if( jsObject[p].fileId == undefined || jsObject[p].fileId == article.docID) continue;
			htmlStr += "<ul draggable='true' data-url='"+jsObject[p].url+"' data-urlpad='"+jsObject[p].urlPad+"' style='padding:0 10px;cursor:pointer;' id="+'0'+jsObject[p].fileId+" ondblclick='relReadMode(this)'>"+
			"<li style=' display:none;'>"+jsObject[p].fileId +"</li>"+
			"<span style='color: #ff9900;font-size: 25px; margin:-15px 0 0 -12px; float:left;'>.</span><li><a herf='"+jsObject[p].url+"' class='article-title'>"+jsObject[p].title+"</a></li>"+
			"<li class='pubTime'>"+jsObject[p].publishtime +"</li>"+
			"<li class='colCasName'>"+jsObject[p].colName +"<a class='close' herf='#' class='removeRel''>×</a></li>"+
			"<li><a class='btns dosure' type='button' onclick=article_rel.doSave("+jsObject[p].fileId+")>√</a></li>"+
			"</ul>"; 
			htmlStr += '<div id="relReadDiv" style="display:none; position: absolute; right: 35%; top: 196px; bottom: 5px; width:500px;z-index:999;">'
				+'<img id="relReadCloseBtn" style="cursor:pointer;  position: ABSOLUTE;width: 15px;top: 10px;right: 10px;" src="../img/close.png" onclick="relReadClose()">'
				+'<iframe id="relReadFrame" src="" frameborder="1"></iframe></div>';
			
			$('#possible').append(htmlStr);
			$('#possible').append("<div style='border-bottom:1px dashed #ddd;'></div>");
			htmlStr = '';
		} 
		if($("#possible ul").size() > 0 ){
			for(var i = 0, size = $("#possible ul").size(); i < size; i++){
				$("#" + $("#possible ul").eq(i).attr("id")).mouseover(function(){
					$(this).find(".dosure").show();
					$(this).css("background","#e8f5fb");
				});
				
				$("#" + $("#possible ul").eq(i).attr("id")).mouseout(function(){
					$(this).find(".dosure").hide();
					$(this).css("background","#fff");
				});
			}
		}
	},
	
	initPossibleData :function() {
		$.ajax({
			url : "../../xy/article/RelsPossible.do",
			async : false,
			timeout : 5000,
			data : {
				"siteID" : $('#a_siteID').val(),
				"keyword" : $('#a_keyword').val(),
				"channel" : $('#a_channel').val()-1
			},
			dataType : 'json',
			success : function(datas) {
				article_rel.possiblejsonData = datas;
				article_rel.loadPossibleData();
			}
		});
	},
	
	doSave : function(docID){
		article_rel.insertRel(article.docLibID,docID);
	},
	
	/**
	 * 组织相关稿件数据，供外层调用
	 * 返回格式：[{id:<id>,lib:<id>}, ...]
	 * 没展开时，返回null
	 */
	relData : function() {
		if ($("#relDiv").parent().attr("expanded") == "true"){
			var result = [];
			var jsObject = article_rel.reljsonData;
			for(var p in jsObject){
				if(jsObject[p].id == undefined) continue;
				var rel = {};
				rel.id = jsObject[p].id;
				rel.lib = jsObject[p].lib;	
				result.push(rel);
			}

			var ulId = [],
				reljsonArr = [],
				ulList = $('#ul5').find('ul');
			for(var i = 0; i< ulList.length; i++ ){
				//这两个数组是一样长的
				ulId.push($('#ul5').find('ul').eq(i).attr('id'));
				// article_rel.reljsonData[i].id
				for(var f = 0; f< result.length; f++ ){
					//这两个数组是一样长的
					if(ulId[i] == result[f].id){
                        reljsonArr.push(result[f])
					}
				}
			}

            result = reljsonArr;
			//对result 进行排序
			return result;
		} else {
			return null;
		}
	},
	//新选中一个相关稿件
	insertRel : function(docLibID,docIDs) {
		//判断docIDs是否有存在的
		var docs = article_rel.filter(docIDs);
		if(docs == ""){
			return;
		}
		$.ajax({
			url : "../../xy/article/getRelArticle.do",
			async : false,
			data : {
				"DocIDs" : docs,
				"DocLibID" : docLibID
			},
			dataType : 'json',
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert("读相关稿件失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success : function(datas) {
				var htmlStr = "";
				for(var p in datas)  
				{  
					if(datas[p].id == undefined) continue;
					
					var add = {};
					add.id = datas[p].id;
					add.lib = datas[p].lib;
					add.title = datas[p].title;
					article_rel.reljsonData.push(add);
					
					htmlStr += "<ul  data-url='"+datas[p].url+"' data-urlpad='"+datas[p].urlPad+"' style='padding:0 10px;cursor:pointer;position: relative;' id="+datas[p].id+">"+
					"<li style=' display:none;'>"+datas[p].id +"</li>"+ 
					"<span style='color: #ff9900;font-size: 25px; margin:-15px 0 0 -7px; float:left;'>.</span><li style='margin-top: 8px; overflow:hidden'><a herf='"+datas[p].url+"' class='article-title'>"+datas[p].title+"</a></li>"+
					"<li class='pubTime'>"+datas[p].pubTime +"</li>"+
					"<li class='colCasName'>"+datas[p].colCasName +"<a class='close' herf='#' class='removeRel'>×</a></li>"+

                    "<li draggable='true' class='dragmMove icon-move' title='拖动到左侧编辑器添加文章链接' style='position: absolute;right: 1px;top: 4px;'><a herf='#'></a></li>"+

                    "</ul>";
					$('#ul5').append(htmlStr);
					$('#ul5').append("<div style='border-bottom:1px dashed #ddd;'></div>");
					htmlStr = '';
					article_rel.removeArt();
                    //拖拽相关稿件
                    article_rel.initDrag('#ul5');
				} 		

				$('#divRelBtns').show();
				$('#divRelInitBtn').hide();		
			}
		});
		
	},
	filter : function(docIDs) {
		if (!docIDs)
			return docIDs;

		docIDs = "," + docIDs + ","; // 前后加上逗号, 格式变成",1,2,3,"的样子
		var jsObject = article_rel.reljsonData;

		for(var p in jsObject)  
		{ 
			if(jsObject[p].id == undefined) continue;
			docIDs = docIDs.replace("," + jsObject[p].id + ",", ","); // 替换掉",1,"
		}
		// 去掉前后的逗号
		if (docIDs.charAt(0) == ',') {
			docIDs = docIDs.substring(1);
		}
		if (docIDs.charAt(docIDs.length - 1) == ',') {
			docIDs = docIDs.substring(0, docIDs.length - 1);
		}

		return docIDs;
	},
	articleCancel : function(){
		article_rel.artDialog.close();
	},
	articleClose : function (docLibID,docIDs){
		article_rel.insertRel(docLibID,docIDs);
		article_rel.artDialog.close();
		//给list添加样式，并添加鼠标悬浮事件- 显示删除图标
		//$(".colCasName").css("cursor","pointer");
		if(docIDs){
			var _ids = docIDs.split(",");
			for(var i = 0, size = _ids.length; i < size; i  ++){
				$("#" + _ids[i]).mouseover(function(){
					$(this).find(".close").show();
					$(this).parent().find("ul").css("background","#fff");
					$(this).css("background","#e8f5fb");
				});
				
				$("#" + _ids[i]).mouseout(function(){
					$(this).find(".close").hide();
					$(this).css("background","#fff");
				});
				
				$("#" + _ids[i]).find(".close").click(article_rel.removeRel);
				
			}
			
		}
		
	}
}

function relReadMode(obj){
	var id = $(obj).attr("id").substring(1);

	url = "./ReadMode.do?DocLibID=" + article.docLibID + "&DocIDs=" + id;
	$("#relReadFrame").css("width","485px").css("height","500px").css("background","#fff").css("cursor","pointer")
	.attr("src", url);
	$("#relReadDiv").show();
}

function relReadClose(){
	$("#relReadDiv").hide();
}

