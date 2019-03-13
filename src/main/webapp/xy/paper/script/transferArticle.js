var transferArticle = {
	UUID : '',
	layoutID : 0,
	targetID : 0,
	init : function() {
		transferArticle._refreshRelArticle();
		var theURL = "getLayoutArticles.do?layoutID="+transferArticle.layoutID+"&keyword=";
		transferArticle._post(theURL);
	},
	reset : function(){
		transferArticle._refreshRelArticle();
		transferArticle.doSearch();
	},
	_post : function(theURL) {
		$.ajax({url:theURL, async:false, dataType:"json", success:transferArticle._show});	
	},
	_show : function(data) {
		$("#alist").empty();
		var articles = data.articles;
		var index = 1;
		for(var i=0;i<articles.length;i++){
			if(articles[i].id == transferArticle.targetID) continue;
			var html = "<tr><td>"+(index++)+"</td>";
			html += "<td>"+transferArticle._getTypeIcon(articles[i].type)+"</td>";
			html += "<td>"+articles[i].title+"</td>";
			html += "<td>"+articles[i].author+"</td>";
			html += "<td><a href='javascript:doSynthesis("+articles[i].id+")'>合成</a></td></tr>";
			$("#alist").append(html);
		}
	},
	doSearch : function(){
		var layoutID = $("#paperLayout option:selected").val();
		var keyword = $("#keyword").val();
		var theURL = "getLayoutArticles.do?layoutID="+layoutID+"&keyword="+keyword;
		transferArticle._post(theURL);
	},
	doCancel :function(){
		var url = "../../e5workspace/after.do?UUID=" + transferArticle.UUID;
		window.location.href= url;
	},
	_getTypeIcon : function(typeID) {
		var ret = "<img src='../../Icons/";
		switch(typeID) {
			case 0:
				ret += "article.png' title='文章'";
				break;
			case 1:
				ret += "pic.png' title='组图'";
				break;
			default:
				return "article.png";
		}
		return ret + "/>";
	},
	_refreshRelArticle : function() {
		var theURL = "getTransferLayoutArticles.do?targetID="+transferArticle.targetID;
		$.ajax({url:theURL, async:false, dataType:"json", success:transferArticle._showRel});
	},
	_showRel : function(data) {
		$("#rellist").empty();
		if(data.length<1) $("#transStatus").empty().append("处理状态：未合成");
		else $("#transStatus").empty().append("处理状态：已合成");
		for(var i=0;i<data.length;i++){
			var html = "<tr><td class='text-left'>转版稿件："+data[i].title+"</td>";
			html += "<td>版次："+data[i].layoutName+"</td>";
			html += "<td><a href='javascript:doRecovery("+data[i].id+")'>还原</a></td></tr>";
			$("#rellist").append(html);
		}
	},
	_process : function(thisID,flag) {
		var theURL = "TransferLayoutArticle.do?targetID="+transferArticle.targetID+"&articleID="+thisID+"&flag="+flag;
		$.ajax({url:theURL, async:false, dataType:"json", success:function(data){
				if(data.success==true){
					transferArticle.reset();
				}else{
					alert(data.errMsg);
				}
			}
		});
	}
}

var doSynthesis=function(thisID) {
	transferArticle._process(thisID,1);
}

var doRecovery=function(thisID) {
	transferArticle._process(thisID,2);
}