<ul id="ulvideo">
	<li id="linklivideo" class="linklivideo clearfix">
		<img class="imgvideo1" alt="请选择视频" src="../pic/img/uploadvideo.png">
	</li>
	<li id="videoTitle"></li>
</ul>
<script>
	var videoDialog = null;
	var video_form = {
		init : function(){
			//点击保存按钮
			$('#linklivideo').click(openVideoSelect);
		},
		//写视频稿操作：保存前读视频信息。由channel.js调用
		getVideoInfo : function(){
			var video = $('#ulvideo').find("p");
			if (video.length == 0) return null;
			
			video = $(video);
			var result = [{}];
			result[0].url = video.attr("url");
			result[0].urlApp = video.attr("urlapp");
			result[0].videoID = video.attr("videoid");
			
			return result;
		},
		//修改视频稿操作：打开时初始化显示已有视频。由channel.js调用
		setVideoInfo : function(datas) {
			var video = $('#ulvideo').find("p");
			if (video.length > 0) {
				$(video).remove();
			}
			
			if (!datas || datas.length == 0) return;
			
			var v = datas[0];
			var p = $("<p/>")
				.attr("url", v.url)
				.attr("urlapp", v.urlApp)
				.attr("videoID", v.videoID)
				;
			p.css('position', 'relative');
			$("#videoTitle").html(v.videoName);
			var pClose = $('<span title="删除">&times;</span>');
			pClose.css({
				position: 'absolute',
				left: '420px',
				top: '0',
				fontSize: '26px',
				width: '32px',
				textAlign: 'center',
				cursor: 'pointer',
				fontWeight: 'bold'
			}).on('click', function(){
				$(this).parent().remove();
				return false;
			});
			p.append(pClose);
			var url = v.url;
			if (!url) url = v.urlApp;
			
			var plugIn = v.plugin;
			if (!plugIn) plugIn = article.videoPlugin;
			
			var embed = $('<embed type="application/x-shockwave-flash" class="edui-faked-video"'
				+ ' pluginspage="http://www.macromedia.com/go/getflashplayer" width="420" height="280"'
				+ ' wmode="transparent" play="true" loop="false" menu="false" allowscriptaccess="never"'
				+ ' allowfullscreen="true"/>');
			embed.attr("src", plugIn + "?src=" + url);
			
			p.append(embed);
			
			$('.imgvideo1').before(p);
		}
	}
	$(function(){
		video_form.init();
	})
	function openVideoSelect(){
		var dataUrl = "../../xy/GroupSelect.do?type=1&siteID=${siteID}";

		videoDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			title : "视频库",
			width : "1050px",
			height : "550px",
			resizable : false,
			fixed : true
		});
		videoDialog.show();
	}
	
	function videoClose(docLibID, docID){
		if (!docID) return;
		
		$.ajax({ url: "../video/Info.do", async: false,
			data : {
				"docID" : docID,
				"docLibID" : docLibID
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);
			},
			success: function (data) {
				data = data.split(",");
				var videoID = (docLibID + "," + docID);
				
				var videoInfo = [{
					plugin : data[0],
					url : data[1],
					urlApp : data[2],
					videoName:data[3],
					videoID : videoID
				}];
				if (!videoInfo[0].url) videoInfo[0].url = videoInfo[0].urlApp;
				if (!videoInfo[0].urlApp) videoInfo[0].urlApp = videoInfo[0].url;
				
				video_form.setVideoInfo(videoInfo);
			}
		});
		videoDialog.close();
	}
	function videoCancel() {
		videoDialog.close();
	}
</script>