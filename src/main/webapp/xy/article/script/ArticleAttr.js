var article_attr = {
		init : function(){
			article_attr.btnOperation() ;
			$("#btnSave").click(function(){
				$("#caForm").submit();
			})
			$("#btnCancel").click(function(){
				article_attr.doCancel() ;
			})
		},
		btnOperation : function(){
			$("#attrDiv button").each(function(){
				$(this).click(function(){
					$("#a_attr").val($(this).val()) ;
					$("#attrDiv button").each(function(){
						$(this).attr("class","btn btn-default btn-sm") ;
					});
					$(this).attr("class","btn btn-default btn-sm active") ;
				})
			})
		},
		doCancel : function(){
			window.onbeforeunload = null;
			
			$("#btnSave").disabled = true;
			$("#btnCancel").disabled = true;
			article_attr.beforeExit();
		},
		beforeExit : function(e) {
			var dataUrl = "../../e5workspace/after.do?UUID=" + $("#UUID").val();

			//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
			if (e && article_attr.isChrome())
				window.open(dataUrl, "_blank", "width=10,height=10");
			else
				window.location.href = dataUrl;
		},
		isChrome : function() {
			var nav = article_attr.navigator();
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
	article_attr.init() ;
});

