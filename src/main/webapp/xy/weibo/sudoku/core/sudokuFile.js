var SUDOKUFILE = {
		fileInput : null,             // 选择文件按钮dom对象
		url : "",  					  // 上传action路径
		uploadFile : [],  			  // 需要上传的文件数组
		curUploadFile : [],			  // 当前上传文件
		fileIndex : 0,				  // 存放当前file的序号
		fileNum : 0,                  // 代表文件总个数，因为涉及到继续添加，所以下一次添加需要在它的基础上添加索引
		/* 提供给外部的接口 */
		filterFile : function(files){ // 提供给外部的过滤文件格式等的接口，外部需要把过滤后的文件返回
			return files;
		},
		onSelect : function(selectFile, fileIndex){      // 提供给外部获取选中的文件，供外部实现预览等功能  selectFile:当前选中的文件  allFiles:还没上传的全部文件
			
		},
		onDelete : function(fileIndex){            // 提供给外部获取删除的单个文件，供外部实现删除效果  file:当前删除的文件  files:删除之后的文件
			
		},
		onProgress : function(file, loaded, total){  // 提供给外部获取单个文件的上传进度，供外部实现上传进度效果
			
		},
		onSuccess : function(file, responseInfo){    // 提供给外部获取单个文件上传成功，供外部实现成功效果

		},
		onFailure : function(file, responseInfo){    // 提供给外部获取单个文件上传失败，供外部实现失败效果
		
		},
		onComplete : function(responseInfo){         // 提供给外部获取全部文件上传完成，供外部实现完成效果
			
		},
		onLoadImage : function(fileIndex, imageUrl, weiboImage){	// 提供给外部获取图片html方法的调用
			
		},
		
		/* 内部实现功能方法 */
		// 获取文件
		funGetFiles : function(e){  
			var self = this;
			// 从事件中获取选中的所有文件
			var files = e.target.files || e.dataTransfer.files;
			this.curUploadFile = this.filterFile(files);
			
			// 调用对文件处理的方法
			this.funDealtFiles();
			
			return true;
		},
		// 处理过滤后的文件，给每个文件设置下标
		funDealtFiles : function(){
			var self = this;
			
			// 目前是遍历所有的文件，给每个文件增加唯一索引值
			$.each(this.curUploadFile, function(k, v){
				self.fileIndex++;
				v.index = self.fileIndex;
			});
			
			// 执行选择回调
			this.onSelect(this.curUploadFile, self.fileIndex);
			return this;
		},
		
		/** 点击未发布稿件，加载attachment附件图片 */
		// 加载多个图片（图片加载自attachment表，转换为json对象）
		funLoadImages : function(weiboImages){
			var self = this;
			// 遍历attachment，再调用单个图片的加载方法
			$.each(weiboImages, function(k, imageUrl){
				self.funLoadImage(k, imageUrl);
			});
		},
		// 加载单个图片
		funLoadImage : function(k, imageUrl){
			var self = this;
			self.fileIndex = k + 1;
			
			var html = this.onLoadImage(self.fileIndex, imageUrl);
			this.funAddUploadShowAndHide();
		},
		// 处理需要删除的文件  isCb代表是否回调onDelete方法  
		// 因为上传完成并不希望在页面上删除div，但是单独点击删除的时候需要删除div   所以用isCb做判断
		funDeleteFile : function(delFileIndex, isCb){
			var self = this;  // 在each中this指向没个v  所以先将this保留
			
			if(isCb){  // 执行回调
				// 回调删除方法，供外部进行删除效果的实现
				self.onDelete(delFileIndex);
			}
			return true;
		},
		// 上传多个文件
		funUploadFiles : function(){
			var self = this;  // 在each中this指向没个v  所以先将this保留
			// 遍历所有文件  ，在调用单个文件上传的方法
			$.each(this.curUploadFile, function(k, v){
				self.funUploadFile(v);
			});
		},
		// 上传单个文件
		funUploadFile : function(file){
			var self = this;  // 在each中this指向每个v  所以先将this保留
			
			var formdata = new FormData();
			formdata.append("Filedata", file);
			
			var xhr = new XMLHttpRequest();
			// 绑定上传事件
			// 进度
		    xhr.upload.addEventListener("progress",	 function(e){
		    	// 回调到外部
		    	self.onProgress(file, e.loaded, e.total);
		    }, false); 
		    // 完成
		    xhr.addEventListener("load", function(e){
	    		// 从文件中删除上传成功的文件  false是不执行onDelete回调方法
//		    	self.funDeleteFile(file.index, false);
		    	// 回调到外部
		    	self.onSuccess(file, xhr.responseText);
		    	if(self.curUploadFile.length==0){
		    		// 回调全部完成方法
		    		self.onComplete("全部完成");
		    	}
		    }, false);  
		    // 错误
		    xhr.addEventListener("error", function(e){
		    	// 回调到外部
		    	self.onFailure(file, xhr.responseText);
		    }, false);  
			
			xhr.open("POST",self.url, true);
			xhr.setRequestHeader("X_FILENAME", encodeURI(file.name));
			xhr.send(formdata);
		},
		// 返回需要上传的文件
		funReturnNeedFiles : function(){
			var self = this;
			self.fileNum = $("div[class='upload_append_list']:visible").length;
			return self.fileNum;
		},
		
		funAddUploadShowAndHide : function(){
			if($("div[class='upload_append_list']:visible").length <= 8) {
				$(".add_upload").show();
			} else {
				$(".add_upload").hide();
			}
		},
		
		// 判断是否有未上传完毕的图片，有则隐藏发布按钮，无则不隐藏
		funPubButtonShowAndHide : function(){
			$("div[id^='uploadList_']:visible").each(function(){
				var picid = $(this).children("p[id^='uploadGuid_']").attr('guid');
				if (!picid) {
					$("#pubButton").attr({"disabled": "disabled"});
					$("#modifyPubButton").attr({"disabled": "disabled"});
					return false;
				} else {
					$("#pubButton").removeAttr("disabled");
					$("#modifyPubButton").removeAttr("disabled");
				}
				
			});
		},
		// 判断是否有未上传完毕的图片，有则隐藏发布按钮，无则不隐藏
		funUploadInfShowAndHide : function(){
			if($("div[id^='uploadList_']:visible").length == 0) {
				$("#uploadInf").attr("");
				$("#uploadInf").hide();
			}
			$("div[id^='uploadList_']:visible").each(function(){
				var picid = $(this).children("p[id^='uploadGuid_']").attr('guid');
				if(!picid || picid.length == 0) {
					$("#uploadInf").show();
				} else {
					$("#uploadInf").hide();
				}
				
			});
		},
		// 初始化
		init : function(){  // 初始化方法，在此给选择、上传按钮绑定事件
			var self = this;  // 克隆一个自身
			
			// 如果选择按钮存在
			if(self.fileInput){
				// 绑定change事件
				this.fileInput.addEventListener("change", function(e) {
					self.funGetFiles(e); 
					self.funPubButtonShowAndHide();
					self.funAddUploadShowAndHide();
				}, false);	
			}
			
		}
};