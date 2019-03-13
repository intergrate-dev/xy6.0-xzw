/**
 * Created by isaac_gu on 2017/3/6.
 *
 * upload.data 是返回所有上传图片的路径 - 数组
 * upload.objList 是上传图片封装的对象
 */
+function(window, $){
    var Upload = function(selectorId, _option){
        this.$selector = $(selectorId);
        this.option = this.combineOption(this.defaultOption, _option);
        this.previewObj = {initialPreview: [], initialPreviewConfig: []};
        this.init();
    };

    String.prototype.replaceAll = function(s1, s2){
        return this.replace(new RegExp(s1, "gm"), s2);
    };

    //TODO 添加编辑说明的属性
    Upload.prototype = {
        defaultOption: {
            language: 'zh',
            showRemove: false,
            showUpload: false,       //是否显示上传进度条
            showCaption: false,     //显示容器 - 就是路径
            showUploadedThumbs: false,  //上传之后文件的预览，有bug，默认设为false
            autoReplace: true,     //boolean whether to automatically replace the files in the preview
            showAjaxErrorDetails: false,        //不显示出错信息
            browseClass: "btn btn-primary btn-lg",
            overwriteInitial: false,     //
            initialPreviewAsData: true,
            showSort: true,
            showContent: false,
            showApplyAll: false,
            showEditor: false,
            showTitleImage: false,
            //fileType: "image",      //默认只能图片
            //allowedFileTypes: ['image'],
            maxFileCount: 10,
            minFileCount: 1,
            maxFileSize: 0,     //最大文件大小
            autoCommit: true,
            allowedPreviewTypes: ['image'],
            allowedFileTypes: [],
            allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png','zip'],
            uploadExtraData: {targetPath: "c:\\image\\"},
            deleteUrl: "xy/upload/deletePreviewThumb.do",
            uploadUrl: '../../../xy/upload/uploadFileF.do',  //路径
            rootUrl: '../../',
            previewSettings: {
                //image: {width: "300px", height: "160px"}
            },
            beforeUpload: null,
            fileUploaded: null,
            fileDeleted: null,
            fileRemove: null,
            completed: null,
            sorted: null,
            fileSelected: null,
            fileBatchSelected: null,
            modifyPicture: null
        },
        combineOption: function(orignalObject, targetObject){
        	return $.extend({}, orignalObject, targetObject);
        },
        init: function(){
            this.createUpload();
            this.beforeUpload();
            this.fileUploaded();
            this.completed();
            this.sorted();
            this.fileSelected();
            this.fileBatchSelected();
            this.fileDeleted();
            this.fileRemove();
            this.showContent();
            if(!this.option.showContent){
                this.$selector.closest(".file-input").addClass("tag-hide");
            }
            this.resetWidth();
        },
        createUpload: function(){
            this.$selector.fileinput(this.option);
            var obj = this;
        },
        setOption: function(_option){
            this.$selector.fileinput('refresh', _option).fileinput("enable");
        	var currentImgs=this.$selector.fileinput('getPreview').config;
        	localStorage.setItem('uploadPicAll',JSON.stringify(currentImgs));
        },
        resetPic: function(_option){
            _option = this.combineOption(this.option, _option);
            this.$selector.fileinput('destroy').fileinput(_option);
        	var currentImgs=this.$selector.fileinput('getPreview').config;
        	localStorage.setItem('uploadPicAll',JSON.stringify(currentImgs));
        },
        beforeUpload: function(){
            var obj = this;
            var fn = obj.option.beforeUpload;
            this.$selector.on('filebatchpreupload', function(event, data, previewId, index){
                if(fn && typeof fn == "function")
                    fn(event, data, previewId, index);
            });
        },
        fileUploaded: function(){ 
            var obj = this;
            //上传完，预览的对象
            var fn = obj.option.fileUploaded;
            this.$selector.on('fileuploaded', function(event, o, previewId, index){
                var data = o.response;
                if(data.type && data.type == "zip/rar"){
                    if(data.list && data.list instanceof Array){
                        for(var i = 0, li = null; li = data.list[i++];){
                            obj.previewObj.initialPreview.push(obj.option.rootUrl + "xy/image.do?path=" + li);
                            obj.previewObj.initialPreviewConfig.push({
                                caption: "",
                                //size: data.fileSize,
                                url: obj.option.rootUrl + obj.option.deleteUrl,
                                width: '120px',
                                key: (new Date().getTime() + "" + Math.round(Math.random() * 100)),
                                imagePath: li
                            });
                        }
                    }
                } else{
                    obj.previewObj.initialPreview.push($("#" + previewId).find("img").filter(".kv-preview-data").attr("src"));
                    obj.previewObj.initialPreviewConfig.push({
                        caption: "",
                        width: '120px',
                        //size: data.fileSize,
                        url: obj.option.rootUrl + obj.option.deleteUrl, key: (new Date().getTime() + "" + Math.round(Math.random() * 100)),
                        imagePath: data.path
                    });
                    obj.data = obj.data || [];

                    obj.data[index] = data;
                }
                if(fn && typeof fn == "function")
                    fn(event, data, previewId, index);
            });
        },
        fileDeleted: function(){ 
            var fn = this.option.fileDeleted;
            var obj = this;
            this.$selector.on('filedeleted', function(event, key, jqXHR, data) {
            	if(obj.getDataList().length <= 0){
                    obj.option.initialPreviewConfig = [];
                    obj.option.initialPreview = [];
                    obj.$selector.fileinput('destroy').fileinput(obj.option);
                }

                if(fn && typeof fn == "function"){
                    fn(event, key);
                }
            	var currentImgs=obj.$selector.fileinput('getPreview').config;
            	localStorage.setItem('uploadPicAll',JSON.stringify(currentImgs));
            });
        },
        fileRemove: function(){ 
            var obj = this;
            var fn = obj.option.fileRemove;
            this.$selector.on('filesuccessremove', function(event, key){
                key += "";
                var _index = key.substr(key.lastIndexOf("-") + 1);
                obj.data = obj.data.filter(function(value, index){
                    return index != +_index;
                });
                if(fn && typeof fn == "function")
                    fn(event, key);
            });
        },
        completed: function(){
            var obj = this;
            var fn = obj.option.completed;
            this.$selector.on('filebatchuploadcomplete', function(event, data, previewId, index){
                if(obj.option.showSort){
                    if(obj.option.maxFileCount != 1){
                        var list = obj.getFileList().config;
                        var _list = obj.previewObj.initialPreviewConfig;
                        list = ClearNullArr(list);
                        obj.previewObj.initialPreviewConfig = list.concat(_list);

                        list = obj.getFileList().content;
                        _list = obj.previewObj.initialPreview;
                        list = ClearNullArr(list);
                        obj.previewObj.initialPreview = list.concat(_list);
                    }
                    obj.resetPic(obj.previewObj);
                    obj.previewObj.initialPreviewConfig = [];
                    obj.previewObj.initialPreview = [];
                }

                if(obj.option.showContent){
                    obj.$selector.closest(".file-input").find(".file-footer-caption").addClass("contentStyle");//.css("width", obj.$selector.closest(".file-input").find(".kv-file-content").css("width"));
                    obj.$selector.closest(".file-input").find(".file-footer-caption").each(function(){
                        $(this).css("width", $(this).parent().css("width"));
                        $(this).parent().css("height", "45px");
                        $(this).attr("placeholder", "请输入描述内容...");
                        var _content = $(this).html();
                        _content = _content.replaceAll("<br>", "");
                        $(this).html(_content);
                    });
                } else{
                    obj.$selector.closest(".file-input").addClass("tag-hide");
                }

                if(obj.option.showApplyAll){
                    obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                        if($(this).find(".kv-file-fresh").size() == 0){
                            $(this).prepend('<button type="button" class="kv-file-fresh btn btn-xs btn-default" title="同步图片说明"><i class="glyphicon glyphicon-transfer"></i></button>');
                        }
                    });
                }

                if(obj.option.showEditor){
                    obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                        if($(this).find(".kv-file-edit").size() == 0){
                            $(this).prepend('<button type="button" class="kv-file-edit btn btn-xs btn-default" title="修改"><i class="glyphicon glyphicon-pencil"></i></button>');
                        }
                    });
                }

                if(obj.option.showTitleImage){
                    obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                        $(this).prepend('<input type=radio title="设置标题图" class="kv-radio" name="indexed" style="margin-right: 2px; cursor:pointer;"/>');
                    });
                }
                if(fn && typeof fn == "function")
                    fn(event, data, previewId, index);
            });
        },
        sorted: function(){
            var obj = this;
            var fn = obj.option.sorted;
            this.$selector.on('filesorted', function(event, params){
            	if(fn && typeof fn == "function")
            		fn(params.stack, params);
            	var uploadPicAll=params.stack;
                localStorage.setItem('uploadPicAll',JSON.stringify(uploadPicAll))
            });
        },
        fileSelected: function(){
            var obj = this;
            var fn = obj.option.fileSelected;
            this.$selector.on('fileselect', function(event, params){
            	if(fn && typeof fn == "function")
                    fn(params.stack);
            });
        },
        fileBatchSelected: function(){
            var obj = this;
            var fn = obj.option.fileBatchSelected;
            this.$selector.on('filebatchselected', function(event, files){
            	if(obj.option.maxFileCount == 1){
                    obj.$selector.closest(".file-input").find(".file-preview-initial").children().hide();
                }
                if(obj.option.autoCommit){
                    obj.upload();
                }
                if(fn && typeof fn == "function")
                    fn(files);
            });
        },
        upload: function(){
            this.$selector.fileinput('upload');
        },
        reset: function(){
            this.$selector.fileinput('reset');
        },
        getFileList: function(){
            //如果有裁剪了图片
            return this.$selector.fileinput('getPreview');
        },
        getDataList: function(){

            return ClearNullArr(this.getFileList().config);
        },
        showContent: function(){
            var obj = this;
            if(obj.option.showContent){
                $(document).on('click', '.contentStyle', function(){
                    var _$this = $(this);
                    var _width = _$this.width();
                    var _content = _$this.html();
                    _content = _content.replaceAll("<br>", "");
                    var _container = _$this.closest('.file-preview-frame');
                    if(_container.find(".contentDiv").size() == 0){
                        _container.prepend('<textarea class="contentDiv" style="width: ' +
                            _width +
                            'px;height: 228px;border: 1px solid rgba(128, 128, 128, 0.39);position: absolute;z-index: 100;background: #fff;border-radius: 6px;margin-top: -3px;box-shadow: 1px 1px 30px 0 #a2958a;display: block;"  contenteditable="true" >' +
                            _content +
                            '</textarea>');
                    }
                    _container.find(".contentDiv").show();
                    _container.find(".contentDiv").focus();
                });

                $(document).on('blur', '.contentDiv', function(){
                    var $this = $(this);
                    var _html = $this.val();
                    _html = _html.replaceAll("<br>", "");
                    $this.hide();
                    $this.parent().find(".file-footer-caption").html(_html);
                    $this.parent().find(".file-footer-caption").attr("title", _html);

                    var _key = $this.closest('.file-preview-frame').find(".kv-file-remove").attr("data-key");
                    _key = _key + "";

                    var _ll = obj.getFileList().config;

                    for(var i = 0, li = null; li = _ll[i++];){
                        if(li.key + "" == _key){
                            li.caption = _html;
                        }
                    }
                    localStorage.setItem('uploadPicAll',JSON.stringify(_ll))
                });

                $(document).click(function(e){
                    var _$this = $(e.target);
                    if(!_$this.hasClass("contentDiv") && !_$this.hasClass("contentStyle")){
                        $(".contentDiv").hide();
                    }
                });

            }

            if(obj.option.showApplyAll){
                $(document).on("click", '.kv-file-fresh', function(){
                    var _h = $(this).closest(".file-thumbnail-footer").find(".contentStyle").html();
                    var $container = $(this).closest(".file-input");
                    $container.find(".contentStyle").html(_h);
                    $container.find(".contentDiv").val(_h);
                    $container.find(".contentDiv").text(_h);

                    var _ll = obj.getFileList().config;
                    for(var i = 0, li = null; li = _ll[i++];){
                        li.caption = _h;
                    }
                    localStorage.setItem('uploadPicAll',JSON.stringify(_ll))
                });
            }

            if(obj.option.showEditor){ 
                obj.$selector.closest(".file-input").parent().on("click", '.kv-file-edit', function(){
                    var key = $(this).siblings(".kv-file-remove").attr("data-key");
                    var path = null;
                    var list = obj.getDataList();
                    for(var i = 0, li = null; li = list[i++];){
                        if(key == li.key){
                            path = li.imagePath;
                        }
                    }
                    obj.option.modifyPicture(key, path, obj, list);
                });
            }
        },
        resetWidth: function(){
            var obj = this;
            setTimeout(function(){
                if(obj.option.showContent){
                    obj.$selector.closest(".file-input").find(".file-footer-caption").addClass("contentStyle");//.css("width", obj.$selector.closest(".file-input").find(".kv-file-content").css("width"));
                    obj.$selector.closest(".file-input").find(".file-footer-caption").each(function(){
                        $(this).css("width", $(this).parent().css("width"));
                        $(this).parent().css("height", "45px");
                        $(this).attr("placeholder", "请输入描述内容...");
                        var _content = $(this).html();
                        _content = _content.replaceAll("<br>", "");
                        $(this).html(_content);
                    });
                } else{
                    obj.$selector.closest(".file-input").addClass("tag-hide");
                }

                if(obj.option.showApplyAll){
                    obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                        if($(this).find(".kv-file-fresh").size() == 0){
                            $(this).prepend('<button type="button" class="kv-file-fresh btn btn-xs btn-default" title="同步图片说明"><i class="glyphicon glyphicon-transfer"></i></button>');
                        }
                    });
                }

                if(obj.option.showEditor){
                    obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                        if($(this).find(".kv-file-edit").size() == 0){
                            $(this).prepend('<button type="button" class="kv-file-edit btn btn-xs btn-default" title="修改"><i class="glyphicon glyphicon-pencil"></i></button>');
                        }
                    });
                }
                if(obj.option.showTitleImage){
                    obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                        $(this).prepend('<input type=radio class="kv-radio" title="设置标题图" name="indexed" style="margin-right: 2px; cursor:pointer;"/>');
                    });
                }
            }, 300);

        },
        doModifyPicture: function(key, path, showPath, imageList){ 
            var obj = this;
            if(imageList && imageList instanceof Array){
                obj.getFileList().config = imageList;

                var clist = [];
                for(var i = 0, li = null; li = imageList[i++];){
                    clist.push(li.imagePath);
                    obj.$selector.closest(".file-input").find(".kv-file-remove").filter("[data-key='" +
                        li.key + "']").closest(".file-preview-frame").find(".kv-file-content").children("img").attr("src", li.imagePath);
                }
                obj.getFileList().content = clist;

            }else{
                var list = obj.getFileList().config;
                var clist = obj.getFileList().content;
                for(var i = 0, li = null; li = list[i++];){
                    if(key == li.key){
                        li.imagePath=path;
                        clist[i-1] = showPath;
                        break;
                    }
                }
                obj.$selector.closest(".file-input").find(".kv-file-remove").filter("[data-key='" +
                    key + "']").closest(".file-preview-frame").find(".kv-file-content").children("img").attr("src", showPath)
            }
        	var currentImgs=obj.$selector.fileinput('getPreview').config;
        	localStorage.setItem('uploadPicAll',JSON.stringify(currentImgs));
        }
    };

    function ClearNullArr(arr){
        for(var i = 0, len = arr.length; i < len; i++){
            if(!arr[i] || arr[i] == '' || arr[i] === undefined){
                arr.splice(i, 1);
                len--;
                i--;
            }
        }
        return arr;
    }

    window.Upload = Upload;
}(window, jQuery);
