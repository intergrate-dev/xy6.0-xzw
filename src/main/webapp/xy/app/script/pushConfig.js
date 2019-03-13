var os_pushConfig = {
		init : function(){
			$("#form").validationEngine({
				autoPositionUpdate:true,
				promptPosition:"bottomLeft",
				scroll:true
			});
			$("#btnSave").click(function(){
				//设置验证
				$("#caForm").validationEngine({
					autoPositionUpdate:true,
					promptPosition:"bottomLeft",
					scroll:true
				});
	            $("#caForm").submit();
			}) ;
			$("#btnCancel").click(function(){
				os_pushConfig.doCancel() ;
			}) ;
		},
		doCancel : function(){
			window.onbeforeunload = null;
			
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			os_pushConfig.beforeExit();
		},
		beforeExit : function(e) {
			var dataUrl = "../../e5workspace/after.do?UUID=" + $("#UUID").val();

			//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
			if (e && os_pushConfig.isChrome())
				window.open(dataUrl, "_blank", "width=10,height=10");
			else
				window.location.href = dataUrl;
		},
		isChrome : function() {
			var nav = os_pushConfig.navigator();
			return nav.browser == "chrome";
		},
		navigator : function(){
			var ua = navigator.userAgent.toLowerCase();
			// trident IE11
			var re =/(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
			var m = ua.match(re);
			
			var Sys = {};
			Sys.browser = m[1].replace(/version/, "'safari");
			Sys.ver = m[2];
			return Sys;
		},
}
$(function(){
	os_pushConfig.init() ;
});

