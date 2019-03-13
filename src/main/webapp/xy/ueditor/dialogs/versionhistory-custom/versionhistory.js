/**
 * 版本管理
 * User: fzf
 * Date: 14-8-22
 */
 
var reversionXml; 
var maindocXml;

// editor.js
UEDITOR_CONFIG = window.UEDITOR_CONFIG || {};
var baidu = window.baidu || {};
window.baidu = baidu;
window.UE = baidu.editor =  window.UE || {};

//加载XML对象
var loadXmlDOc = function (xmlString){
	var xmlDoc; 
	if (document.implementation.createDocument) {// Mozilla and Netscape browsers
		var parser = new DOMParser();
		xmlDoc = parser.parseFromString(xmlString, "application/xml"); 
	} else if (window.ActiveXObject) {// MSIE    
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async="false";
		xmlDoc.loadXML(xmlString);
	}  
	//alert(xmlDoc);
	return xmlDoc;
}

//xml转String
var XML2String = function (xmlObj){ 
	if (window.ActiveXObject){  
		return xmlObj.xml; 
	}else { 
		return (new XMLSerializer()).serializeToString(xmlObj); 
	} 
}
//把转义字符，改成标签
function escapeHtml(str) {
	return str ? str.replace(/&((g|l|quo)t|amp|#39|nbsp);/g, function (m) {
		return {
			'&lt;':'<',
			'&amp;':'&',
			'&quot;':'"',
			'&gt;':'>',
			'&#39;':"'",
			'&nbsp;':' '
		}[m]
	}) : '';
}

/**
*对Date的扩展，将 Date 转化为指定格式的String 
*月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
*年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
*(new Date()).Format("yyyy-MM-dd hh:mm:ss") ==> 2006-07-02 08:09:04
*/	
Date.prototype.Format = function(fmt){
	var o = { 
		"M+" : this.getMonth()+1,                 //月份 
		"d+" : this.getDate(),                    //日 
		"h+" : this.getHours(),                   //小时 
		"m+" : this.getMinutes(),                 //分 
		"s+" : this.getSeconds(),                 //秒 
		"q+" : Math.floor((this.getMonth()+3)/3), //季度 
		"S"  : this.getMilliseconds()             //毫秒 
	}; 
	if(/(y+)/.test(fmt)) 
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	for(var k in o) 
		if(new RegExp("("+ k +")").test(fmt)) 
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
	return fmt; 
}

//查看
document.getElementById("viewBtn").onclick = function () {//$G
	if( $("#versionList .selected").length > 0 ){
		 var tr = $("#versionList .selected").get(0);
		 var td = tr.cells[0];
		 var version = td.innerHTML;
		var contents = window.reversionXml.getElementsByTagName('content');
		if( !isNaN(parseInt(version)) ){
			if (navigator.userAgent.indexOf("MSIE") > 0) {
				window.parent.viewVersionContent = contents[parseInt(version)].text;
			}else{
				window.parent.viewVersionContent = contents[parseInt(version)].textContent;
			}
			var url = '~/dialogs/versionhistory-custom/versionContent.html';
			//alert(url.replace('~/', UE.getEditor('editor').options.UEDITOR_HOME_URL || ''));
			var dialog = new baidu.editor.ui.Dialog(baidu.editor.utils.extend({
				iframeUrl:url.replace('~/', UE.getEditor('editor').options.UEDITOR_HOME_URL || ''),
				editor:UE.getEditor('editor'),
				className:'edui-for-versioncontent',
				title:"查看版本"+version,
				holdScroll:false,
				fullscreen: false
			}, {} 
			));
			dialog.render();
			dialog.open();
		}
	}else{
		alert("请选择版本！");
	}
};

//切换
document.getElementById("switchBtn").onclick = function () {//$G
	if( $("#versionList .selected").length > 0 ){
		 var tr = $("#versionList .selected").get(0);
		 var td = tr.cells[0];
		 var version = td.innerHTML;
		 //从XML获得版本内容
		var contents = window.reversionXml.getElementsByTagName('content');
		if( !isNaN(parseInt(version)) ){
			var content;
			if (navigator.userAgent.indexOf("MSIE") > 0) {
				content = contents[parseInt(version)].text;
			}else{
				content = contents[parseInt(version)].textContent;
			}
			//赋值内容，关闭窗口
			UE.getEditor('editor').setContent(content, false);
			dialog.close();
				
			//TODO 赋值稿签信息
		}
	}else{
		alert("请选择版本！");
	}
	
};

//获得版本列表
var getVersionList = function () {	
	//读取XML文件,生成表格内容
	var versions = reversionXml.getElementsByTagName('version');
	var t = document.getElementById("versionList");//$G("versionList")
	var tbody = document.createElement("TBODY");
	for(var i=0;i<versions.length;i++){	
		var tr= document.createElement("tr"); //新建一个tr类型的Element节点
		var td0 = document.createElement("td");
		td0.align = "center";
		td0.appendChild(document.createTextNode(versions[i].getAttribute("verId")));
		tr.appendChild(td0);
		
		var td1 = document.createElement("td");
		td1.align = "center";
		td1.appendChild(document.createTextNode(versions[i].getAttribute("userName")));
		tr.appendChild(td1);
		
		var td2 = document.createElement("td");
		td2.align = "center";
		td2.appendChild(document.createTextNode(versions[i].getAttribute("createTime")));
		tr.appendChild(td2);
		
		var td3 = document.createElement("td");
		td3.appendChild(document.createTextNode(versions[i].getAttribute("note")));
		tr.appendChild(td3);
		
		tbody.appendChild(tr);
	}
	t.appendChild(tbody);
	
	trClick();
};

//给每行加选中事件
var trClick = function () {
	$("#versionList tr").each(function(){
		$(this).click(function(){	
			if( $(this).attr("class")==undefined || $(this).attr("class").indexOf("selected")<0 ){
				$("#versionList tr").removeClass("selected");
				$(this).addClass("selected");
			}
		})
	})
	
};
var getVersionXml = function(){
	var editor = UE.getEditor('editor');
	$.ajax({
		 url:'../../getVersionXml.do',
	     type: 'POST',
	     async: true,
	     data: {
	    	 "docID" : editor.getOpt("DocID"),
			 "docLibID" : editor.getOpt("DocLibID")
	     },
	     success: function ( data ) {
	    	 reversionXml = loadXmlDOc(data);
	    	 getVersionList();
	     },
	     error: function ( xhr ) {
	          alert( 'Ajax请求失败' );
	     }
	});
}
window.onload = getVersionXml();