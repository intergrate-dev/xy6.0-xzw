var xy_cookie = {
	// 设置cookie
	setCookie : function(name, value, exdays) {
		var d = new Date();
		if(!exdays){
			exdays = 30;
		}
		d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
		var expires = "expires=" + d.toUTCString()+"; ";
		var path = "path=/; ";
		document.cookie = name + "=" + escape(value) + "; " + expires + path;
	},
	// 获得cookie
	getCookie : function(name) {
		var arr = document.cookie.match(new RegExp("(^| )" + name
				+ "=([^;]*)(;|$)"));
		if (arr != null)
			return unescape(arr[2]);
		return null;
	}

};