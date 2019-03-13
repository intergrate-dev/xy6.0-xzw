/**
 * Created by isaac_gu on 2016/1/14.文件名带Old及XXX的为不用的js
 */
var Link_Map=new Map();
(function(window, $, LE){
    LE.stylesheets["Link"] = function(){
        var $PS = $("#linkSection");
        var $fontActive = $("#fontActive");

        /*将html中id为setAStyle的style标签的内容解析为map对象*/
        var initGetLinkStyle=function(){
        	var str=$("#setAStyle").html();
        	var arr=str.split("/*id*/");
        	var l=arr.length;
        	for(var i=1;i<l;i++){
        		var thisK=arr[i].split(" ")[0].replace("#","");
        		var thisV={
        			linkDec:arr[i].split("/*d*/")[1],
        			linkColor:arr[i].split("/*r*/")[1],
        			hoverDec:arr[i].split("/*d*/")[3],
        			hoverColor:arr[i].split("/*r*/")[3]
        		};
        		Link_Map.put(thisK,thisV);
        	}
        };
        /*动态创建style标签中的内容，用于动态设置文字模块下a标签的样式。
         1、构造全局变量Link_Map，用于存储键值对，目标对象的ID作为键，值以对象的形式存储，包括需要设置的各个属性值；
         2、每次点击“添加超链接”按钮，新的键值对直接插入，已有键值对的话，更新值的内容（键是唯一标识，值的内容可变）*/
        function changAStyle(){
        	var $target = LECurrentObject;
        	var thisId=$target.attr("id");
        	var thisData={
        		linkDec:$("#linkUnder").attr("data-value"),
        		linkColor:$("#linkColor").val(),
        		hoverDec:$("#hoverUnder").attr("data-value"),
        		hoverColor:$("#hoverColor").val()
        	};
        	Link_Map.put(thisId,thisData);
        	var s="";
        	Link_Map.each(function(key,value,index){
                var oneValue = '/*id*/#' + key + ' a{text-decoration:/*d*/' + value.linkDec + '/*d*/;color:/*r*/' + value.linkColor + '/*r*/;}#' + key + ' a:hover{text-decoration:/*d*/' + value.hoverDec + '/*d*/;color:/*r*/' + value.hoverColor + '/*r*/;}\n';
                s+=oneValue;
	            $("#setAStyle").html(s);
            });
        }
        /*为选中的文字添加a标签*/
        var initLinkEvent = function(){
            $fontActive.bind("click", function(){
                $("#linkTextDialog").modal("show");
               /* var $target = LECurrentObject;
	    		var contenteditable = $target.attr("contenteditable") && $target.attr("contenteditable") == "true" ? true : false;
                if(contenteditable){ 
		            var bookmark;
		            bookmark = rangy.getSelection().getBookmark($target[0]);
		          	$target.focus();
		            rangy.getSelection().moveToBookmark(bookmark);
		            //a链接的href值
		            document.execCommand("CreateLink", false, "http://www.baidu.com");	
                    //_$this.removeClass("fnormal");
                }
                else{
                	//_$this.addClass("fnormal");
                }
               changAStyle();*/
            });

            $("#TextDivClose").click(function(){
                $("#linkTextDialog").modal("hide");
            });

            $("#TextUrlBtn").click(function(){
                var $target = LECurrentObject;
                var contenteditable = $target.attr("contenteditable") && $target.attr("contenteditable") == "true" ? true : false;
                if(contenteditable){
                    var bookmark;
                    bookmark = rangy.getSelection().getBookmark($target[0]);
                    $target.focus();
                    rangy.getSelection().moveToBookmark(bookmark);
                    //a链接的href值
                    document.execCommand("CreateLink", false, $("#TextUrlText").val());
                    //_$this.removeClass("fnormal");
                }
                else{
                    //_$this.addClass("fnormal");
                }
                changAStyle();
                $("#linkTextDialog").modal("hide");
            });
            $("#TextUrlText").keypress(function(e){
                if(e.keyCode == 13){
                    var $target = LECurrentObject;
                    var contenteditable = $target.attr("contenteditable") && $target.attr("contenteditable") == "true" ? true : false;
                    if(contenteditable){
                        var bookmark;
                        bookmark = rangy.getSelection().getBookmark($target[0]);
                        $target.focus();
                        rangy.getSelection().moveToBookmark(bookmark);
                        //a链接的href值
                        document.execCommand("CreateLink", false, $("#TextUrlText").val());
                        //_$this.removeClass("fnormal");
                    }
                    else{
                        //_$this.addClass("fnormal");
                    }
                    changAStyle();
                    $("#linkTextDialog").modal("hide");
                }
            });
        };
        
        /*修改输入框的值改变超链接颜色和颜色选择框默认样式*/
        var initLinkColorChange=function(){
        	var linkColor=$("#linkColor");
        	linkColor.change(function(){
        		var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
        		if(reg.test($(this).val())){
	        		$("#linkColorPick").spectrum("set", $(this).val());
	        		changAStyle();
        		}else{
	        		$(this).val("#0000ff");
	        		$("#linkColorPick").spectrum("set","#0000ff");
	        		changAStyle();
        		}
        	})
        };
        /*修改输入框的值改变超链接悬停颜色和颜色选择框默认样式*/
        var initHoverColorChange=function(){
        	var hoverColor=$("#hoverColor");
        	hoverColor.change(function(){
        		var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
        		if(reg.test($(this).val())){
	        		$("#hoverColorPick").spectrum("set", $(this).val());
	        		changAStyle();
        		}else{
	        		$(this).val("#ff0000");
	        		$("#hoverColorPick").spectrum("set","#ff0000");
	        		changAStyle();
        		}
        	})
        };
        
        /*a标签的默认下划线有无*/
   		var linkUnder = function(){
        	$("#linkUnder").bind("click",function(){
        		if($("#linkUnder").hasClass("select")){
        			$(this).removeClass("select");
        			$(this).attr("data-value","none");
        		}else{
        			$(this).addClass("select");
        			$(this).attr("data-value","underline");
        		}
				changAStyle();
        	});
        		
   		};
   		
       	/*a标签悬浮时的默认下划线有无*/	
   		var hoverUnder = function(){
        	$("#hoverUnder").bind("click",function(){
        		if($("#hoverUnder").hasClass("select")){
        			$(this).removeClass("select");
        			$(this).attr("data-value","none");
        		}else{
        			$(this).addClass("select");
        			$(this).attr("data-value","underline");
        		}
				changAStyle();
        	});
   		};
   		
   		/*字体超链接的初始化颜色 蓝色*/
        var linkColorDefaultStatus=function(){
        	$("#linkColorPick").spectrum("set", "#00f");
        };
        /*a标签的默认颜色，点击可以改变，原理是通过点击按钮动态改变html中style标签的内容*/
        var initLinkColorEvent = function(){
            $("#linkColorPick").spectrum(
	                LEColorPicker.getOptions(function(tinycolor){
                        var _c = tinycolor ? tinycolor.toRgbaString() : "";
                        var _v = tinycolor?tinycolor.toHexString():"#000000";
	                    //获取颜色值填到input
                    	$("#linkColor").val(_v);
    					changAStyle();
	                })
	            );
        };
        
        /*字体超链接的初始化颜色 红色*/
        var hoverColorDefaultStatus=function(){
        	$("#hoverColorPick").spectrum("set", "#f00");
        };
        /*a标签悬浮时的默认颜色，点击可以改变，原理是通过点击按钮动态改变html中style标签的内容*/
       var initHoverColorEvent = function(){
            $("#hoverColorPick").spectrum(
	                LEColorPicker.getOptions(function(tinycolor){
                        var _c = tinycolor ? tinycolor.toRgbaString() : "";
                        var _v = tinycolor?tinycolor.toHexString():"#000000";
	                    //获取颜色值填到input
                    	$("#hoverColor").val(_v);
    					changAStyle();
	                })
	            );
        };
        /*RGB颜色转换为16进制*/
        function colorHexFn(){
			var reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
			String.prototype.colorHex = function(){
				var that = this;
				if(/^(rgb|RGB)/.test(that)){
					var aColor = that.replace(/(?:\(|\)|rgb|RGB)*/g,"").split(",");
					var strHex = "#";
					for(var i=0; i<aColor.length; i++){
						var hex = Number(aColor[i]).toString(16);
						if(hex === "0"){
							hex += hex;
						}
						strHex += hex;
					}
					if(strHex.length !== 7){
						strHex = that;
					}
					return strHex;
				}else if(reg.test(that)){
					var aNum = that.replace(/#/,"").split("");
					if(aNum.length === 6){
						return that;
					}else if(aNum.length === 3){
						var numHex = "#";
						for(var i=0; i<aNum.length; i+=1){
							numHex += (aNum[i]+aNum[i]);
						}
						return numHex;
					}
				}else{
					return that;
				}
			};
        }
        /*初始化颜色输入框的值
         1、判断刚开始目标是否有a标签颜色 没有设置为蓝色 有的话为当前颜色
         2、将获得的颜色从rgb格式转换为十六进制*/
        var resetLinkColorStatus=function(){
        	colorHexFn();
        	var $target = LECurrentObject;
        	if($('div[id='+$target.attr("id")+']>a').css("color")){
        		$("#linkColor").val($('div[id='+$target.attr("id")+']>a').css("color").colorHex());
        		$("#linkColorPick").spectrum("set",$('div[id='+$target.attr("id")+']>a').css("color"));
        	}else{
        		$("#linkColor").val("#0000ff");
        		$("#linkColorPick").spectrum("set", "#00f");
        	}
        };
        var resetLinkDecStatus=function(){
        	var $target = LECurrentObject;
        	if($('div[id='+$target.attr("id")+']>a').css("text-decoration")){
        		if($('div[id='+$target.attr("id")+']>a').css("text-decoration")=="underline"){
        			$("#linkUnder").addClass("select");
        		}else{
        			$("#linkUnder").removeClass("select");
        		}
        	}else{
        		$("#linkUnder").addClass("select");
        	}
        };
        var resetHoverColorStatus=function(){
        	var $target = LECurrentObject;
        	if(Link_Map.size()>0){
        		if(Link_Map.get($target.attr("id"))){
        			var oValue=Link_Map.get($target.attr("id"));
		        	var ahovCol=oValue.hoverColor;
		        	$("#hoverColor").val(ahovCol);
        			$("#hoverColorPick").spectrum("set",ahovCol);
        		}else{
        			$("#hoverColor").val("#ff0000");
        			$("#hoverColorPick").spectrum("set", "#f00");
        		}
        	}else{
        		$("#hoverColor").val("#ff0000");
        		$("#hoverColorPick").spectrum("set", "#f00");
        	}
        };
        var resetHoverDecStatus=function(){
        	var $target = LECurrentObject;
        	if(Link_Map.size()>0){
        		if(Link_Map.get($target.attr("id"))){
        			var oValue=Link_Map.get($target.attr("id"));
		        	var ahovDec=oValue.hoverDec;
		        	if(ahovDec=="none"){
	        			$("#hoverUnder").removeClass("select");
	        		}else{
	        			$("#hoverUnder").addClass("select");
	        		}
        		}
        	}else{
        		$("#hoverUnder").addClass("select");
        	}
        };
        
        
        return {
            init: function(){
            	rangy.init();
            	initLinkEvent();
            	initLinkColorEvent();
            	initHoverColorEvent();
            	linkUnder();
            	hoverUnder();
            	initLinkColorChange();
            	initHoverColorChange();
            	linkColorDefaultStatus();
            	hoverColorDefaultStatus();
            	initGetLinkStyle();
                //console.info("PageSetting init")
            },
            run: function(options, doHide){
            	resetLinkColorStatus();
            	resetLinkDecStatus();
            	resetHoverColorStatus();
            	resetHoverDecStatus();
                //console.info("PageSetting run")
				LEDisplay.show($PS, doHide);
            },
            destroy: function(){
                //console.info("PageSetting destroy")
                $PS.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);