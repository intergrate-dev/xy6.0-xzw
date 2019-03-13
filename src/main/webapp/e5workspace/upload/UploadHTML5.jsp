<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>HTML5文件上传示例</title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<link rel="stylesheet" type="text/css" href="../../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5style/sys-main-body-style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/html5-file-upload/assets/css/styles.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<!--[if lt IE 9]>
      <script type="text/javascript" src="../../e5script/jquery/html5-file-upload/assets/js/html5.js"></script>
    <![endif]-->
    <script type="text/javascript" src="../../e5script/jquery/html5-file-upload/assets/js/jquery.filedrop.js"></script>
	<script type="text/JavaScript">
	var device,dropbox,message;

	$(function(){
		
		device = $("#sl-device");
		
		initDevice();
		
		dropbox = $('#dropbox'),
		message = $('.message', dropbox);

	});
	function upload(){
		dropbox.filedrop({
			// The name of the $_FILES entry:
			paramname:'pic',
			
			maxfiles: 5,
	    	maxfilesize: 2,
	    	url: "../../e5workspace/app/Upload.do?deviceName="+device.val(),
			
	    	uploadFinished:function(i,file,response){
				
				$.data(file).addClass('done');
				// response is the JSON object that post_file.php returns
			},
			
	    	error: function(err, file) {
				switch(err) {
					case 'BrowserNotSupported':
						showMessage('Your browser does not support HTML5 file uploads!');
						break;
					case 'TooManyFiles':
						alert('Too many files! Please select 5 at most! (configurable)');
						break;
					case 'FileTooLarge':
						alert(file.name+' is too large! Please upload files up to 2mb (configurable).');
						break;
					default:
						break;
				}
			},
			
			// Called before each upload is started
			beforeEach: function(file){
				if(!file.type.match(/^image\//)){
					alert('Only images are allowed!');
					
					// Returning false will cause the
					// file to be rejected
					return false;
				}
			},
			
			uploadStarted:function(i, file, len){
				createImage(file);
			},
			
			progressUpdated: function(i, file, progress) {
				 
				$.data(file).find('.progress').width(progress);
			}
	    	 
		});
	}
	
	function createImage(file){
		var template = 
			'<div class="preview">'+
				'<span class="imageHolder">'+
					'<img />'+
					'<span class="uploaded"></span>'+
				'</span>'+
				'<div class="progressHolder">'+
					'<div class="progress"></div>'+
				'</div>'+
			'</div>'; 
		var preview = $(template), 
			image = $('img', preview);
			
		var reader = new FileReader();
		
		image.width = 100;
		image.height = 100;
		
		reader.onload = function(e){
			
			// e.target.result holds the DataURL which
			// can be used as a source of the image:
			
			image.attr('src',e.target.result);
		};
		
		// Reading the file as a DataURL. When finished,
		// this will trigger the onload function above:
		reader.readAsDataURL(file);
		
		message.hide();
		preview.appendTo(dropbox);
		
		// Associating a preview container
		// with the file, using jQuery's $.data():
		
		$.data(file,preview);
	}

	function showMessage(msg){
		message.html(msg);
	}
	function initDevice(){
		
		 $.ajax({ url: "../../e5workspace/app/StorageDevice.do?invoke=getStorageListForJson",
				async: true,
				dataType:"json",
				success: function (data) {
					if(data!=null&& data!=""){
						
						var options =[];
						$.each(data,function(i,n){
							options.push("<option value='"+n.deviceName+"'>");
							options.push(n.deviceName);
							options.push("</option>");
						});
						device.append(options.join(''));
						
						upload();
						
					}
				}
		 });
	}
	
	</script>
</head>

<body>
<form method="post" action="">
<table class="table">
<caption>HTML5文件上传</caption>
</table>
<div>请选择存储设备：<select id="sl-device" name="deviceName"></select></div>

<div id="dropbox">
<span class="message">请拖动图片到这里 <br /></span>
</div>

</form>
</body>
</html>
