function imageManger(){
 var list = $G("imageList1");
                list.style.display = "none";
                //切换到图片管理时，ajax请求后台图片列表
                    list.style.display = "";
                    //已经初始化过时不再重复提交请求
                        ajax.request(editor.options.imageManagerUrl, {
                            timeout:100000,
                            action:"get",
                            onsuccess:function (xhr) {
                                //去除空格
                                var tmp = utils.trim(xhr.responseText),
                                    imageUrls = !tmp ? [] : tmp.split("ue_separate_ue"),
                                    length = imageUrls.length;
                                 $G("imageList1").innerHTML = !length ? "&nbsp;&nbsp;" + lang.noUploadImage : "";
                                for (var k = 0, ci; ci = imageUrls[k++];) {
                                    var img = document.createElement("img");
                                    var a1 = document.createElement("a");
									var a2 = document.createElement("a");
                                    var div = document.createElement("div");
									var li = document.createElement("li");									
								    li.className="ui-widget-content ui-corner-tr";																		
									a1.className="ui-icon ui-icon-zoomin";
									a2.className="ui-icon ui-icon-trash";
									a1.innerHTML ="查看大图";
									a2.innerHTML ="删除图像";
									a1.href ="###";
									a2.href ="###";
									a2.onclick =function(){
										if (confirm("您确定要删除这张水印图片吗？")){
											if(window.ActiveXObject){
												this.parentNode.parentNode.removeNode("li")
												}
										     this.parentNode.parentNode.remove(this);
										}
										};									
                                    li.appendChild(div);
									div.appendChild(img);
									div.appendChild(a1);
									div.appendChild(a2);
                                    div.style.display = "none";
                                	$G("imageList1").appendChild(li);
                                    img.onclick = function () {
                                        changeSelected(this);
										//alert(this.data_ue_art);
                                    };
                                    img.onload = function () {
                                        this.parentNode.style.display = "";
                                        var w = this.width, h = this.height;
                                        scale(this, 60, 60, 50);
                                        this.title = lang.toggleSelect + w + "X" + h;
                                    };
                                    img.setAttribute(k < 135 ? "src" : "lazy_src", editor.options.imageManagerPath + ci.replace(/\s+|\s+/ig, ""));
                                    img.setAttribute("data_ue_src", editor.options.imageManagerPath + ci.replace(/\s+|\s+/ig, ""));
                                    img.setAttribute("data_ue_art",ci.replace(/\s+|\s+/ig, ""));

                                }
								disSelected();
                            },
                            onerror:function () {
                                $G("imageList1").innerHTML = lang.imageLoadError;
                            }
                        });
               
} 

    /**
     * 图片缩放
     * @param img
     * @param max
     */
    function scale(img, max, oWidth, oHeight) {
        var width = 0, height = 0, percent, ow = img.width || oWidth, oh = img.height || oHeight;
        if (ow > max || oh > max) {
            if (ow >= oh) {
                if (width = ow - max) {
                    percent = (width / ow).toFixed(2);
                    img.height = oh - oh * percent;
                    img.width = max;
                }
            } else {
                if (height = oh - max) {
                    percent = (height / oh).toFixed(2);
                    img.width = ow - ow * percent;
                    img.height = max;
                }
            }
        }
    }
    
     /**
     * 改变o的选中状态
     * @param o
     */
    function changeSelected(o) {
	 //$G("wpimagebgurl").value=o.data_ue_art;// alert(o.data_ue_art+"=======");
     $G("wpimagebgurl").value=o.getAttribute('data_ue_art');
	 //editor.options.wpimagebgurl=o.data_ue_art;//console.();
     editor.options.wpimagebgurl=o.getAttribute('data_ue_art');
 		var imgs=document.getElementsByTagName("img");
		 for(var i=0;i<imgs.length;i++){	
		     //if(imgs[i].data_ue_art==o.data_ue_art){		
		     if(imgs[i].getAttribute('data_ue_art')==o.getAttribute('data_ue_art')){
				 //alert(imgs[i].data_ue_art);
		            imgs[i].setAttribute("selected", "true");
		            imgs[i].style.cssText = "filter:alpha(Opacity=50);-moz-opacity:0.5;-webkit-opacity:0.5;opacity: 0.5;border:2px solid blue;";
		     }else{
		           imgs[i].removeAttribute("selected");
		           imgs[i].style.cssText = "filter:alpha(Opacity=100);-webkit-opacity:1;-moz-opacity:1;opacity: 1;border: 2px solid #fff";
		     }
		 }
}

	/**
	*默认选中的水印图标
     */
 function disSelected() {
	 var imgs=document.getElementsByTagName("img");
	 for(var i=0;i<imgs.length;i++){
		 //if(imgs[i].data_ue_art== $G("wpimagebgurl").value){
	     if(imgs[i].getAttribute('data_ue_art')== $G("wpimagebgurl").value){
			 //alert($G("wpimagebgurl").value);
	            imgs[i].setAttribute("selected", "true");
	            imgs[i].style.cssText = "filter:alpha(Opacity=50);-moz-opacity:0.5;opacity: 0.5;border:2px solid blue;";
	     }else{
	           imgs[i].removeAttribute("selected");
	           imgs[i].style.cssText = "filter:alpha(Opacity=100);-moz-opacity:1;opacity: 1;border: 2px solid #fff";
	     }
	 }
}

   function loadSelected() {
     var sid=$G("wpimagetype").value;
     var dgname=$G("wpimagewz");
        editor.options.wpimagetype=sid;
     if(sid=="1"){
        dgname.options[0]=new Option("左上","1" );
        dgname.options[1]=new Option("左下","2" );
        dgname.options[2]=new Option("左中","3" );
        dgname.options[3]=new Option("中上","4" );
        dgname.options[4]=new Option("中下","5" );
        dgname.options[5]=new Option("中中","6" );
        dgname.options[6]=new Option("右上","7" );
        dgname.options[7]=new Option("右下","8" );
        dgname.options[8]=new Option("右中","9" );
     }else{
          
        dgname.options[0]=new Option("左上","1" );
        dgname.options[1]=new Option("左下","2" );
        dgname.options[2]=new Option("左中","3" );
        dgname.options[3]=new Option("中上","4" );
        dgname.options[4]=new Option("中下","6" );
        dgname.options[5]=new Option("中中","5" );
        dgname.options[6]=new Option("右上","7" );
        dgname.options[7]=new Option("右下","9" );
        dgname.options[8]=new Option("右中","8" );
        
     }
     
     
  }
  
  function  loadSelected2(){
        var dgname=$G("wpimagewz");
        editor.options.wpimagewz=dgname.value;
  }

imageManger();
