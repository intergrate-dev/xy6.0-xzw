var site_list = {
		//外界修改的参数
		type:"", 	//跳转页面分类
		domaindirUrl:"DomainDirTree.jsp?", //跳转到域名目录的页面
		ruleUrl : "../../e5sys/DataMain.do?type=SITERULE",//跳转到发布规则的页面
		init : function() {
			$('#sitelist li').click(site_list.select);		
			$("#sitelist li:first").click();
			
			site_list.autoCompleter.init();
		},
		select: function(evt){
			$('#sitelist li').removeClass("select");
			$(evt.target).addClass("select");
			
			var siteid = $(evt.target).val();
			var domaindirURL = site_list.domaindirUrl + "&siteID=" + siteid;
			//用extParams传递siteID，该参数会传递到操作中
			var ruleURL = site_list.ruleUrl + "&rule=rule_siteID_EQ_" + siteid + "&extParams=siteID=" + siteid;
			
			if (site_list.type == "dir"){
				window.parent.frames["frmStrtDir"].location.href = domaindirURL;
			}else if(site_list.type == "rule"){
				window.parent.frames["frmStrtRule"].location.href = ruleURL;
			}
		}
};
//---------站点名称查找框-------------
site_list.autoCompleter = {
	url : null,
	init : function() {
		site_list.autoCompleter.url = "Find.do?";
		
		var s = $("#domainSearch");
		s.autocomplete(site_list.autoCompleter.url, site_list.autoCompleter.options);
		s.keydown(site_list.autoCompleter.search);
		s.focus();
	},
	search : function(event) {
		if (event.keyCode == 13) {
			var text = $("#domainSearch").val();
			var theURL = site_list.autoCompleter.url + "&q=" + site_list.autoCompleter.encode(text);
			
			$.ajax({url:theURL, async:false, dataType:"json", success:function(data){
				if (data) {
					var data = data[0].key;
					$("li[value="+data+"]").click();
				}
			}});
			return false;
		}
	},
	options : {
		minChars : 1,
		delay : 400,
		autoFill : true,
		selectFirst : true,
		matchContains: true,
		cacheLength : 1,
		dataType:'json',
		//把data转换成json数据格式
		parse: function(data) {
			if (!data)
				return [];
			
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }
	},
	/** 对特殊字符和中文编码 */
	encode : function(param1){
		if (!param1) return "";

		var res = "";
		for(var i = 0;i < param1.length;i ++){
			switch (param1.charCodeAt(i)){
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
			}
		}
		return res;
	}
}
$(function() {
	site_list.init();
});