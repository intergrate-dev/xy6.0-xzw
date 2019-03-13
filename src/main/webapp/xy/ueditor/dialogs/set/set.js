// JavaScript Document
//function $(id){
//	return typeof id==='string'?document.getElementById(id):id;
//}
//封装编辑器设置功能函数
//if(window.localStorage){
    //       alert('This browser supports localStorage');
  // }else{
    //       alert('This browser does NOT support localStorage');
    //    }
(function(){

	//alert("hello,这是成功的第一步");
	var localStorage=window.localStorage;
	var fontfamily_cmd="宋体",
        indent_cmd="indent",
        fontsize_cmd="12px",
	    justify_cmd="left",
		banjiao_cmd="fulltohalf",
		imagefloat_cmd="center",
		insert_direction_cmd="ltr";

	if(window.localStorage){
		localStorage.indent_cmd="2em";
		localStorage.banjiao_cmd="fulltohalf";
	    fontfamily_cmd=localStorage.fontfamily_cmd;
	    indent_cmd=localStorage.indent_cmd;
	    fontsize_cmd=localStorage.fontsize_cmd;
	    justify_cmd=localStorage.justify_cmd;
	    imagefloat_cmd=localStorage.imagefloat_cmd;
		banjiao_cmd=localStorage.banjiao_cmd;
		insert_direction_cmd=localStorage.insert_direction_cmd;
	}
	window.onload=function(){

		initTags();
		initSelect();
		initOption();
		initButton();
		}

	function initTags(){
		 // 标签的索引
        var index=0;
        var lis=$('#menu_list li'),
        divs=$('#contents .content'),
	    lis_lang=$('#content_2 li'),
	    divs_lang=$('#content_2 .type-set');

  if(lis.length!=divs.length) return;

  // 遍历所有的页签
  for(var i=0;i<lis.length;i++){
    lis[i].id=i;
    lis[i].onclick=function(){
		if(i=1){

				for(var i=0;i<lis_lang.length;i++){
					lis_lang[i].id=i;
					lis_lang[i].onclick=function(){
						 var that=this;
						 for(var j=0;j<lis_lang.length;j++){
							 $(lis_lang[j]).removeClass("select");
                             $(divs_lang[j]).css("display","none");
							 }
							  $(lis_lang[that.id]).addClass("select");
                              $(divs_lang[that.id]).css("display","block");
						}
					}

			}
      // 用that这个变量来引用当前滑过的li
      var that=this;
      // 如果存在准备执行的定时器，立刻清除，只有当前停留时间大于500ms时才开始执行

        for(var j=0;j<lis.length;j++){

          $(lis[j]).removeClass("select");
          $(divs[j]).css("display","none");
        }
        $(lis[that.id]).addClass("select");
        $(divs[that.id]).css("display","block");
      }

  }
}

    /* 初始化选项 */
    function initSelect(){
	     if(localStorage.fontfamily_cmd){

			 for(var i=0;i<$G('fontType').options.length;i++){

				 if(localStorage.fontfamily_cmd==$G('fontType').options[i].value){

					 $G('fontType').options[i].selected = 'selected';
					 $G('cnfontType').options[i].selected = 'selected';
					 break;
					 }
				 }
			 }

			 if(localStorage.fontsize_cmd){

			 for(var i=0;i<$G('fontSize').options.length;i++){

				 if(localStorage.fontsize_cmd==$G('fontSize').options[i].value){

					 $G('fontSize').options[i].selected = 'selected';
					 $G('cnfontSize').options[i].selected = 'selected';
					 break;
					 }
				 }
			 }

			 if(localStorage.justify_cmd){

			 for(var i=0;i<$G('ParagraphType').options.length;i++){

				 if(localStorage.justify_cmd==$G('ParagraphType').options[i].value){

					 $G('ParagraphType').options[i].selected = 'selected';
					 $G('cnParagraphType').options[i].selected = 'selected';
					 break;
					 }
				 }
			 }

			 if(localStorage.imagefloat_cmd){

			 for(var i=0;i<$G('enimageFloat').options.length;i++){

				 if(localStorage.imagefloat_cmd==$G('enimageFloat').options[i].value){

					 $G('enimageFloat').options[i].selected = 'selected';

					 break;
					 }
				 }
			 }
			 if(localStorage.indent_state==="checked"){
				 $G('indent_set').checked= localStorage.indent_state;

				 }

			 if(localStorage.halftofull_char_state==="checked"){
				 $G('fulltohalf_char').checked="";
				 $G('halftofull_char').checked= localStorage.halftofull_char_state;

				 }
			 if(localStorage.fulltohalf_char_state==="checked"){
				 $G('halftofull_char').checked="";
				 $G('fulltohalf_char').checked= localStorage.fulltohalf_char_state;

				 }

			if(localStorage.halftofull_num_state==="checked"){
				$G('fulltohalf_num').checked="";
				$G('halftofull_num').checked= localStorage.halftofull_num_state;
			}
			if(localStorage.fulltohalf_num_state==="checked"){
				$G('halftofull_num').checked="";
				$G('fulltohalf_num').checked= localStorage.fulltohalf_num_state;
			}

			if(localStorage.insert_directionleft_state==="checked"){

				 $G('cn_insert_left').checked= localStorage.insert_directionleft_state;
				}
		    if(localStorage.insert_directionright_state==="checked"){

				 $G('cn_insert_right').checked= localStorage.insert_directionright_state;
				}
	}




    /* 初始化dom事件 */
    function initOption(){


		domUtils.on($G('fontType'),'change', function(e){
             var target = e.target || e.srcElement;
			 for(var i=0;i<$G('cnfontType').options.length;i++){

				 if($G('fontType').options[i].value==target.value){

					 $G('cnfontType').options[i].selected = 'selected';

					 break;
					 }
				 }
			  return localStorage.fontfamily_cmd=target.value;

            });
		domUtils.on($G('cnfontType'),'change', function(e){
             var target = e.target || e.srcElement;
			  for(var i=0;i<$G('fontType').options.length;i++){

				 if($G('fontType').options[i].value==target.value){

					 $G('fontType').options[i].selected = 'selected';

					 break;
					 }
				 }
			  return localStorage.fontfamily_cmd=target.value;

            });

		domUtils.on($G('fontSize'), 'change', function(e){
             var target = e.target || e.srcElement;
			  for(var i=0;i<$G('cnfontSize').options.length;i++){

				 if($G('cnfontSize').options[i].value==target.value){

					 $G('cnfontSize').options[i].selected = 'selected';

					 break;
					 }
				 }
			  return localStorage.fontsize_cmd=target.value;

            });
		domUtils.on($G('cnfontSize'), 'change', function(e){
             var target = e.target || e.srcElement;
			  for(var i=0;i<$G('fontSize').options.length;i++){

				 if($G('fontSize').options[i].value==target.value){

					 $G('fontSize').options[i].selected = 'selected';

					 break;
					 }
				 }
			  return localStorage.fontsize_cmd=target.value;

            });


		domUtils.on($G('ParagraphType'), 'change', function(e){
             var target = e.target || e.srcElement;
			  for(var i=0;i<$G('cnParagraphType').options.length;i++){

				 if($G('cnParagraphType').options[i].value==target.value){

					 $G('cnParagraphType').options[i].selected = 'selected';

					 break;
					 }
				 }
			   return localStorage.justify_cmd=target.value;

            });
		domUtils.on($G('cnParagraphType'), 'change', function(e){
             var target = e.target || e.srcElement;
			  for(var i=0;i<$G('ParagraphType').options.length;i++){

				 if($G('ParagraphType').options[i].value==target.value){

					 $G('ParagraphType').options[i].selected = 'selected';

					 break;
					 }
				 }
			   return localStorage.justify_cmd=target.value;

            });


		domUtils.on($G('indent_set'), 'click', function(e){
             var target = e.target || e.srcElement;
			  if(target.checked){
				  localStorage.indent_state="checked";
				  return localStorage.indent_cmd="indent";
				  }
				  else{
				  localStorage.indent_state=target.value;
				 return localStorage.indent_cmd=target.value;
					  }


            });
		domUtils.on($G('enimageFloat'), 'click', function(e){
             var target = e.target || e.srcElement;

			   return localStorage.imagefloat_cmd=target.value;

            });
		domUtils.on($G('fulltohalf_char'), 'click', function(e){
             var target = e.target || e.srcElement;
			 if(target.checked){
				  localStorage.fulltohalf_char_state="checked";
				   localStorage.halftofull_char_state=" ";
				  return localStorage.banjiao_char_cmd=target.value;
				 }

            });
		domUtils.on($G('halftofull_char'), 'click', function(e){
             var target = e.target || e.srcElement;
			 if(target.checked){
				  localStorage.halftofull_char_state="checked";
				  localStorage.fulltohalf_char_state=" ";
				  return localStorage.banjiao_char_cmd=target.value;
				 }

            });

		domUtils.on($G('fulltohalf_num'), 'click', function(e){
			var target = e.target || e.srcElement;
			if(target.checked){
				localStorage.fulltohalf_num_state="checked";
				localStorage.halftofull_num_state=" ";
				return localStorage.banjiao_num_cmd=target.value;
			}

		});
		domUtils.on($G('halftofull_num'), 'click', function(e){
			var target = e.target || e.srcElement;
			if(target.checked){
				localStorage.halftofull_num_state="checked";
				localStorage.fulltohalf_num_state=" ";
				return localStorage.banjiao_num_cmd=target.value;
			}

		});

		domUtils.on($G('cn_insert_left'), 'click', function(e){
             var target = e.target || e.srcElement;
			     localStorage.insert_directionright_state=" ";
			     localStorage.insert_directionleft_state="checked";
			   return localStorage.insert_direction_cmd=target.value;

            });

		domUtils.on($G('cn_insert_right'), 'click', function(e){
             var target = e.target || e.srcElement;
			     localStorage.insert_directionleft_state=" ";
			     localStorage.insert_directionright_state="checked";
			   return localStorage.insert_direction_cmd=target.value;

            });

		}

    /* 初始化onok事件 */
	function initButton(){
		dialog.onok = function (){
		   if(window.localStorage){
			 editor.execCommand( 'selectall' );

			 if(localStorage.fontsize_cmd){
				editor.execCommand( 'fontsize', localStorage.fontsize_cmd );
			 }
			 if(localStorage.fontfamily_cmd){
				 editor.execCommand( 'fontfamily', localStorage.fontfamily_cmd);
			 }
			 if(localStorage.imagefloat_cmd){
				 //editor.execCommand( 'imagefloat', localStorage.imagefloat_cmd );
                 (function(me , align){
                     var range = me.selection.getRange();
                     if (!range.collapsed) {
						 var imgs = editor.document.getElementsByTagName("img");
						 for(var i = 0 ; img = imgs[i++];){
							 if (img && img.tagName == 'IMG') {
								 switch (align) {
									 case 'left':
									 case 'right':
									 case 'none':
										 var pN = img.parentNode, tmpNode, pre, next;
										 while (pN.tagName == 'A') {
											 pN = pN.parentNode;
										 }
										 tmpNode = pN;
										 if (tmpNode.tagName == 'P' && domUtils.getStyle(tmpNode, 'text-align') == 'center') {
											 if (!domUtils.isBody(tmpNode) && domUtils.getChildCount(tmpNode, function (node) {
													 return !domUtils.isBr(node) && !domUtils.isWhitespace(node);
												 }) == 1) {
												 pre = tmpNode.previousSibling;
												 next = tmpNode.nextSibling;
												 if (pre && next && pre.nodeType == 1 && next.nodeType == 1 && pre.tagName == next.tagName && domUtils.isBlockElm(pre)) {
													 pre.appendChild(tmpNode.firstChild);
													 while (next.firstChild) {
														 pre.appendChild(next.firstChild);
													 }
													 domUtils.remove(tmpNode);
													 domUtils.remove(next);
												 } else {
													 domUtils.setStyle(tmpNode, 'text-align', '');
												 }


											 }

											 range.selectNode(img).select();
										 }
										 domUtils.setStyle(img, 'float', align == 'none' ? '' : align);
										 if(align == 'none'){
											 domUtils.removeAttributes(img,'align');
										 }

										 break;
									 case 'center':
										 //if (me.queryCommandValue('imagefloat') != 'center') {
											 pN = img.parentNode;
											 domUtils.setStyle(img, 'float', '');
											 domUtils.removeAttributes(img,'align');
											 tmpNode = img;
											 while (pN && domUtils.getChildCount(pN, function (node) {
												 return !domUtils.isBr(node) && !domUtils.isWhitespace(node);
											 }) == 1
											 && ( pN.tagName == 'A')) {
												 tmpNode = pN;
												 pN = pN.parentNode;
											 }
											 range.setStartBefore(tmpNode).setCursor(false);
											 pN = me.document.createElement('div');
											 pN.appendChild(tmpNode);
											 domUtils.setStyle(tmpNode, 'float', '');

											 me.execCommand('insertHtml', '<p id="_img_parent_tmp" style="text-align:center">' + pN.innerHTML + '</p>');

											 tmpNode = me.document.getElementById('_img_parent_tmp');
											 tmpNode.removeAttribute('id');
											 tmpNode = tmpNode.firstChild;
											 range.selectNode(tmpNode).select();
											 //去掉后边多余的元素
											 next = tmpNode.parentNode.nextSibling;
											 if (next && domUtils.isEmptyNode(next)) {
												 domUtils.remove(next);
											 }

										 //}

										 break;
								 }

							 }
						 }

                     }
                 })(editor,localStorage.imagefloat_cmd);



			 }
			 if(localStorage.indent_cmd){
				editor.execCommand( localStorage.indent_cmd );
			 }
			 if(localStorage.justify_cmd){
				editor.execCommand( 'justify', localStorage.justify_cmd );
			 }
			   if (localStorage.banjiao_char_cmd) {
				   editor.execCommand(localStorage.banjiao_char_cmd);
			   }
			   if (localStorage.banjiao_num_cmd) {
				   editor.execCommand(localStorage.banjiao_num_cmd);
			   }


		}
			}
		}




	})();