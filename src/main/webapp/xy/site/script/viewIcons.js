//显示头像
var view_icons = {
	init : function() {
		view_icons.showIcon("ba_pic");
		view_icons.showIcon("l_icon");
		view_icons.showIcon("u_icon");
		view_icons.showIcon("src_icon");
		view_icons.showIcon("os_picUrlBig");
		view_icons.showIcon("os_picUrlMiddle");
		view_icons.showIcon("os_picSmall");
		view_icons.showIcon("pa_iconBig");
		view_icons.showIcon("pa_iconSmall");
		view_icons.showIcon("pl_picPath");
		view_icons.showIconURL("pl_url");
		view_icons.showIconURL("pl_urlPad");
	},
	showIcon : function(id) {
		var icon = $("#" + id);
		if (icon.length == 0) return;
		
		$("#LABEL_" + id).hide();
		
		var a = icon.find("a");
		if (a.length > 0) {
			a = $(a);
			var img = a.find("img");
			img = $(img);
			
			var url = a.attr("href");
			url = url.substring(url.indexOf("path=") + 5);
			if (!url) {
				img.hide();
				return;
			}
			
			if (id != "pl_picPath") {
				img.css("max-width", 150);
				img.css("max-height", 150);
				if (url.indexOf("http://") != 0)
					url = "../xy/image.do?path=" + url;
			} else {
				img.css("max-width", 170);
				img.css("max-height", 250);
				url = "../xy/image.do?path=" + url + ".1.jpg";
			}
			img.attr("src", url);
			
			img.parent().attr("href", url);
		}
	},
	showIconURL : function(id) {
		var icon = $("#" + id);
		if (icon.length == 0) return;
		
		/*$("#LABEL_" + id).hide();*/
		
		var html = icon.html();
		var newHtml="";
		if (html.length > 0) {
			newHtml = "<a href='"+html+"' style='cursor:pointer;text-decoration:underline'>"+html+"</a>";
			icon.html(newHtml);
		}
	}
}
$(function() {
	view_icons.init();
});
