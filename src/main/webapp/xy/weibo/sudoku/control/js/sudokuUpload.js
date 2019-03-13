(function($,undefined){
	$.fn.sudokuUpload = function(options,param){
		var otherArgs = Array.prototype.slice.call(arguments, 1);
		if (typeof options == 'string') {
			var fn = this[0][options];
			if($.isFunction(fn)){
				return fn.apply(this, otherArgs);
			}else{
				throw ("sudokuUpload - No such method: " + options);
			}
		}
		return this.each(function(){
			var para = {};    // 保留参数
			var self = this;  // 保存组件对象
			
			var defaults = {
					width            : "700px",  					// 宽度
					height           : "400px",  					// 宽度
					itemWidth        : "140px",                     // 文件项的宽度
					itemHeight       : "120px",                     // 文件项的高度
					url              : "../xy/pic/Upload.do",  	// 上传文件的路径
					multiple         : true,  						// 是否可以多个文件上传
					del              : true,  						// 是否可以删除文件
					finishDel        : true,  						// 是否在上传文件完成后删除预览
					/* 提供给外部的接口方法 */
					onSelect         : function(selectFiles, files){},// 选择文件的回调方法  selectFile:当前选中的文件  allFiles:还没上传的全部文件
					onDelete		 : function(file, files){},     // 删除一个文件的回调方法 file:当前删除的文件  files:删除之后的文件
					onSuccess		 : function(file){},            // 文件上传成功的回调方法
					onFailure		 : function(file){},            // 文件上传失败的回调方法
					onComplete		 : function(responseInfo){}    // 上传完成的回调方法
			};
			
			para = $.extend(defaults,options);
			
			this.init = function(){
				this.createHtml();  // 创建组件html
				this.createCorePlug();  // 调用核心js
			};
			
			/**
			 * 功能：创建上传所使用的html
			 * 参数: 无
			 * 返回: 无
			 */
			this.createHtml = function(){
				var multiple = "";  // 设置多选的参数
				para.multiple ? multiple = "multiple" : multiple = "";
				var html= '';
				
				var imgWidth = parseInt(para.itemWidth.replace("px", ""));
				var imgHeight = parseInt(para.itemHeight.replace("px", ""));

				// 创建不带有拖动的html
				html += '<form id="uploadForm" action="'+para.url+'" method="post" enctype="multipart/form-data">';
				html += '	<div class="upload_box">';
				html += '		<div class="upload_main single_main">';
				html += '			<span class="wb-horn-1"></span>';
				html += '			<span class="wb-horn-2"></span>';
				html += '           <div class="upload_title"><span class="Local_upload">本地上传</span><button type="button" class="close" id="close" >×</button></div>'
	            html += '			<div id="upload_preview" class="upload_preview">';
				html += '				<div class="status_bar">';
				html += '					<div id="status_info" class="info">共0张，还能上传9张</div>';
				html += '					<div class="btns">';
				html += '						<input id="fileImage" type="file" size="30" name="fileselect[]" '+multiple+'>';
				html += '					</div>';
				html += '				</div>';
			    html += '				<div class="add_upload">';
			    html += '					<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" title="点击添加文件" id="rapidAddImg" class="add_imgBox" href="javascript:void(0)">';
			    html += '						<div class="uploadImg" style="width:'+imgWidth+'px;height:'+imgHeight+'px;">';
			    html += '							<img class="upload_image" src="./weibo/sudoku/control/images/add_img.png" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';
			    html += '						</div>';
			    html += '					</a>';
			    html += '				</div>';
				html += '			</div>';
				html += '		</div>';
				html += '		<div class="upload_submit">';
				html += '			<button type="button" id="fileSubmit" class="upload_submit_btn">确认上传文件</button>';
				html += '		</div>';
				html += '		<div id="uploadInf" class="upload_inf"></div>';
				html += '	</div>';
				html += '</form>';
				
	            $(self).append(html).css({"width":para.width,"height":para.height});
	            
	            // 初始化html之后绑定按钮的点击事件
	            this.addEvent();
			};
			
			/**
			 * 功能：显示统计信息和绑定继续上传和上传按钮的点击事件
			 * 参数: 无
			 * 返回: 无
			 */
			this.funSetStatusInfo = function(fileNum){
				var size = 9;
				var minus_num = 9 - fileNum;
				// 设置内容
				if(fileNum >= 9){
					$("#status_info").html("最多选择9张图片上传");
				}else{
					$("#status_info").html("共"+fileNum+"张，还能上传"+minus_num+"张");
				}
			};
			
			/**
			 * 功能：过滤上传的文件格式等
			 * 参数: files 本次选择的文件
			 * 返回: 通过的文件
			 */
			this.funFilterEligibleFile = function(files){
				var arrFiles = [];  // 替换的文件数组
				for (var i = 0, file; file = files[i]; i++) {
					if (file.size >= 51200000) {
						alert('您这个"'+ file.name +'"文件过大');
					} else {
						// 在这里需要判断当前所有文件中
						arrFiles.push(file);	
					}
				}
				return arrFiles;
			};
			
			/**
			 * 功能： 处理参数和格式上的预览html
			 * 参数: files 本次选择的文件
			 * 返回: 预览的html
			 */
			this.funDisposePreviewHtml = function(e, file){
				var html = "";
				var imgWidth = parseInt(para.itemWidth.replace("px", ""));
				var imgHeight = parseInt(para.itemHeight.replace("px", ""));
				
				// 处理配置参数删除按钮
				var delHtml = "";
				if(para.del){  // 显示删除按钮
					delHtml = '<span class="file_del" data-index="'+file.index+'" title="删除"></span>';
				}
				
				// 处理不同类型文件代表的图标
				var fileImgSrc = "sudoku/control/images/fileType/";
				if(file.type.indexOf("rar") > 0){
					fileImgSrc = fileImgSrc + "rar.png";
				}else if(file.type.indexOf("zip") > 0){
					fileImgSrc = fileImgSrc + "zip.png";
				}else if(file.type.indexOf("text") > 0){
					fileImgSrc = fileImgSrc + "txt.png";
				}else{
					fileImgSrc = fileImgSrc + "file.png";
				}
				// 图片上传的是图片还是其他类型文件
				if (file.type.indexOf("image") == 0) {
					html += '<div id="uploadList_'+ file.index +'" class="upload_append_list">';
					html += '	<div class="file_bar">';
					html += '		<div style="padding:0px;">';
					html += 			delHtml;   // 删除按钮的html
					html += '		</div>';
					html += '	</div>';
					html += '	<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" href="#" class="imgBox">';
					html += '		<div class="uploadImg" style="width:'+imgWidth+'px;height:'+imgHeight+'px;">';
					html += '			<img id="uploadImage_'+file.index+'" class="upload_image" src="' + e.target.result + '" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';                                                                 
					html += '		</div>';
					html += '	</a>';
					html += '	<p id="uploadProgress_'+file.index+'" class="file_progress"></p>';
					html += '	<p id="uploadFailure_'+file.index+'" class="file_failure">上传失败，请重试</p>';
					html += '	<p id="uploadSuccess_'+file.index+'" class="file_success"></p>';
					html += '	<p id="uploadGuid_'+file.index+'" guid=""></p>';
					html += '</div>';
                	
				}else{
					html += '<div id="uploadList_'+ file.index +'" class="upload_append_list">';
					html += '	<div class="file_bar">';
					html += '		<div style="padding:0px;">';
					html += 			delHtml;   // 删除按钮的html
					html += '		</div>';
					html += '	</div>';
					html += '	<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" href="#" class="imgBox">';
					html += '		<div class="uploadImg" style="width:'+imgWidth+'px">';				
					html += '			<img id="uploadImage_'+file.index+'" class="upload_image" src="' + fileImgSrc + '" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';                                                                 
					html += '		</div>';
					html += '	</a>';
					html += '	<p id="uploadProgress_'+file.index+'" class="file_progress"></p>';
					html += '	<p id="uploadFailure_'+file.index+'" class="file_failure">上传失败，请重试</p>';
					html += '	<p id="uploadSuccess_'+file.index+'" class="file_success"></p>';
					html += '	<p id="uploadGuid_'+file.index+'" guid=""></p>';
					html += '</div>';
				}
				
				return html;
			};
			/**
			 * 功能：处理参数和格式上的预览html
			 * 参数：fileIndex为遍历attachment的文件
			 * 返回：预览的html
			 */
			this.funLoadImagePreviewHtml = function(fileIndex, imageUrl){
				var url = "image.do?path=" + imageUrl + ".0";
				
				var html = "";
				var imgWidth = parseInt(para.itemWidth.replace("px", ""));
				var imgHeight = parseInt(para.itemHeight.replace("px", ""));
				// 处理配置参数删除按钮
				var delHtml = "";
				if(para.del){	//	显示删除按钮
					delHtml = '<span class="file_del" data-index="' + fileIndex + '" title="删除"></span>';
				}
				// 处理不同类型文件代表的图标,假设只有图片稿
				html += '<div id="uploadList_'+ fileIndex +'" class="upload_append_list" index="'+fileIndex+'">';
				html += '	<div class="file_bar">';
				html += '		<div style="padding:0px;">';
				html += 			delHtml;   // 删除按钮的html
				html += '		</div>';
				html += '	</div>';
				html += '	<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" href="#" class="imgBox">';
				html += '		<div class="uploadImg" style="width:'+imgWidth+'px;height:'+imgHeight+'px;">';
				html += '			<img id="uploadImage_'+fileIndex+'" class="upload_image" src="' + url + '" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';                                                                 
				html += '		</div>';
				html += '	</a>';
				html += '	<p id="uploadProgress_'+fileIndex+'" class="file_progress"></p>';
				html += '	<p id="uploadFailure_'+fileIndex+'" class="file_failure">上传失败，请重试</p>';
				html += '	<p id="uploadSuccess_'+fileIndex+'" class="file_success"></p>';
				html += '	<p id="uploadGuid_'+fileIndex+'" guid="'+ imageUrl +'"></p>';
				html += '</div>';
				
				return html;
			};
			
			/**
			 * 功能：调用核心插件
			 * 参数: 无
			 * 返回: 无
			 */
			this.createCorePlug = function(){
				var params = {
					fileInput: $("#fileImage").get(0),
					url: $("#uploadForm").attr("action"),
					
					filterFile: function(files) {
						// 过滤文件数量
						var arrFiles = [];// 放置过滤后需要上传的files
						// 过滤上传的当前文件，如果超过9张，截取前面的文件
//						var pre_num = this.uploadFile.length;
						var pre_upload_num = SUDOKUFILE.funReturnNeedFiles();
						for( var i = 0, file; file = files[i]; i++){
							if((pre_upload_num + i) < 9) arrFiles.push(file); 
						}
						// 过滤合格的文件
						return self.funFilterEligibleFile(arrFiles);
					},
					onSelect: function(selectFiles, fileIndex) {
						
//						para.onSelect(selectFiles, allFiles);  // 回调方法
						self.funSetStatusInfo(SUDOKUFILE.funReturnNeedFiles());  // 显示统计信息
						var html = '', i = 0;
						// 组织预览html
						var funDealtPreviewHtml = function() {
							file = selectFiles[i];
							if (file) {
								var reader = new FileReader();
								reader.onload = function(e) {
									// 处理下配置参数和格式的html
									html += self.funDisposePreviewHtml(e, file);
									
									i++;
									// 再接着调用此方法递归组成可以预览的html
									funDealtPreviewHtml();
								}
								reader.readAsDataURL(file);
							} else {
								// 走到这里说明文件html已经组织完毕，要把html添加到预览区
								funAppendPreviewHtml(html);
							}
						};
						
						// 添加预览html
						var funAppendPreviewHtml = function(html){
							// 添加到添加按钮前
							$(".add_upload").before(html);
							// 显示后进行上传图片
							SUDOKUFILE.funPubButtonShowAndHide();
							SUDOKUFILE.funUploadFiles();
							// 绑定删除按钮
							funBindDelEvent();
							funBindHoverEvent();
						};
						
						// 绑定删除按钮事件
						var funBindDelEvent = function(){
							if($(".file_del").length>0){
								// 删除方法
								$(".file_del").click(function() {
									SUDOKUFILE.funDeleteFile(parseInt($(this).attr("data-index")), true);
									return false;		
								});
							}
							
						};
						
						// 绑定显示操作栏事件
						var funBindHoverEvent = function(){
							$(".upload_append_list").hover(
								function (e) {
									$(this).find(".file_bar").addClass("file_hover");
								},function (e) {
									$(this).find(".file_bar").removeClass("file_hover");
								}
							);
						};
						
						funDealtPreviewHtml();		
					},
					onDelete: function(fileIndex) {
						
						// 移除效果
						$("#uploadList_" + fileIndex).fadeOut("normal", function(){
							// 隐藏或显示添加按钮
							SUDOKUFILE.funAddUploadShowAndHide();
							// 重新设置统计栏信息
							self.funSetStatusInfo(SUDOKUFILE.funReturnNeedFiles());
							// 根据显示的图片是否存在picid来管理上传失败信息的显示
							SUDOKUFILE.funUploadInfShowAndHide();
						});
					},
					onProgress: function(file, loaded, total) {
						var eleProgress = $("#uploadProgress_" + file.index), percent = (loaded / total * 100).toFixed(2) + '%';
						if(eleProgress.is(":hidden")){
							eleProgress.show();
						}
						eleProgress.css("width",percent);
					},
					onSuccess: function(file, response) {
						var resultInfo = JSON.parse(response);
						
						if ( !resultInfo.picPath) {
							$("#uploadInf").append("<p>上传失败，" + resultInfo.error + "请删除重新上传</p>");
						} else {
							$("#uploadProgress_" + file.index).hide();
							$("#uploadSuccess_" + file.index).show();
							
							// 赋给页面变量
							$("#uploadGuid_" + file.index).attr("guid", resultInfo.picPath);
						}
						
						// 根据配置参数确定隐不隐藏上传成功的文件
						if(para.finishDel){
							// 移除效果
							$("#uploadList_" + file.index).fadeOut("normal", function(){
								// 重新设置统计栏信息
								self.funSetStatusInfo(SUDOKUFILE.funReturnNeedFiles());
							});
						}
						self.funSetStatusInfo(SUDOKUFILE.funReturnNeedFiles());
						SUDOKUFILE.funPubButtonShowAndHide();
						SUDOKUFILE.funAddUploadShowAndHide();
					},
					onFailure: function(file) {
						$("#uploadProgress_" + file.index).hide();
						$("#uploadSuccess_" + file.index).show();
						$("#uploadInf").append("<p>文件" + file.name + "上传失败！</p>");	
						//$("#uploadImage_" + file.index).css("opacity", 0.2);
					},
					onComplete: function(response){
					},
					onLoadImage: function(fileIndex, imageUrl){
						// 显示统计信息
						self.funSetStatusInfo(SUDOKUFILE.funReturnNeedFiles());
						var html = '';
						// 组织预览html
						var funDealLoadPreviewHtml = function() {
							html += self.funLoadImagePreviewHtml(fileIndex, imageUrl);
							funAppendLoadPreviewHtml(html);
							$("#uploadProgress_" + fileIndex).show();
							$("#uploadSuccess_" + fileIndex).show();
							// 显示统计信息
							self.funSetStatusInfo(SUDOKUFILE.funReturnNeedFiles());
						};
						// 添加预览html
						var funAppendLoadPreviewHtml = function(html){
							
							// 添加到添加按钮前
							$(".add_upload").before(html);
							// 绑定删除按钮
							funBindLoadDelEvent();
							funBindLoddHoverEvent();
						};
						// 绑定删除按钮事件
						var funBindLoadDelEvent = function(){
							if($(".file_del").length>0){
								// 删除方法
								$(".file_del").click(function() {
									SUDOKUFILE.funDeleteFile(parseInt($(this).attr("data-index")), true);
									return false;	
								});
							}
						};
						
						// 绑定显示操作栏事件
						var funBindLoddHoverEvent = function(){
							$(".upload_append_list").hover(
								function (e) {
									$(this).find(".file_bar").addClass("file_hover");
								},function (e) {
									$(this).find(".file_bar").removeClass("file_hover");
								}
							);
						};
						
						funDealLoadPreviewHtml();	
					}

				};
				
				SUDOKUFILE = $.extend(SUDOKUFILE, params);
				SUDOKUFILE.init();
			};
			
			/**
			 * 功能：绑定事件
			 * 参数: 无
			 * 返回: 无
			 */
			this.addEvent = function(){

				// 如果快捷添加文件按钮存在
				if($("#rapidAddImg").length > 0){
					// 绑定添加点击事件
					$("#rapidAddImg").bind("click", function(e){
						$("#fileImage").click();
		            });
				};
				
			};
			
			// 初始化上传控制层插件
			this.init();
		});
	};
})(jQuery);