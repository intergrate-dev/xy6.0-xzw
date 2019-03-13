var video_view = {
	init : function() {
		var td = $("#SPAN_v_url").parent();
		var url = $("#v_url").html();
		var suffix = $("#v_format").html();

		td.html("");
		//suffix = new RegExp(suffix).test("mp3")?".mp3":".mp4" ;
		if(new RegExp(suffix).test("mp3")){
			suffix = ".mp3" ;
		}else if(new RegExp(suffix).test("mp4")){
			suffix = ".mp4" ;
		}else{
			suffix = "."+suffix;
		}
			
		
		//读播放器控件的地址
		var player;
		var preview;
		var transRoot;
		$.ajax({type: "POST", url: "../xy/video/Player.do", async:false, 
			success: function (data) {
				data = data.split(",");
				player = data[0];
				preview = data[1];
				transRoot = data[2];
			}
		});

		
		// url = url.replace(transRoot, preview) + suffix;
		
		url = url.replace(/&nbsp;/g, " ");
        // var rand = url.substring(url.substring(0, url.lastIndexOf("/")).lastIndexOf("/"), url.lastIndexOf("/"));
        // var filename = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
        // url = url.replace(filename, rand);

		
		/*
		var video = '<embed type="application/x-shockwave-flash" class="edui-faked-video"'
			+ '	pluginspage="http://www.macromedia.com/go/getflashplayer"'
			+ ' width="420" height="280" wmode="transparent" play="true" loop="false"'
			+ ' menu="false" allowscriptaccess="never" allowfullscreen="true"';
		if (url) {
			video += ' src="' + player + '?autoplay=0&src=' + url + '"/>';
			
			td.html(video);
		}
		*/
		if (url) {
			var video = '<video src="' + url + '" controls="controls" width1="420" height1="280">'
				+ '您的浏览器不支持video预览'
				+ '</video>';
			td.html(video);
		}
	}
};

$(function() {
	video_view.init();
});