var push_WB = {
		DocIDs : 0,
		DocLibID : 0,
		init : function(){
			$('#btnSubmit').click(push_WB.doSubmit);
			$('#btnCancel').click(push_WB.doCancel);
			$("div").find("span").click(push_WB.removePic);
			window.onbeforeunload = e5_form_event.beforeExit;
		},
		doSubmit : function(){
			var paramWB = push_WB.collectWB();
			if (paramWB.length == 0){
				alert("请您选择微博账号！");
				return false;
			}
			if (!paramWB) return false;
			var strWB = JSON.stringify(paramWB);
			
			var paramPic = push_WB.collectPic();
			var strPic = JSON.stringify(paramPic);
			
			var content = $('#pushDescription').val();
			var len = push_WB.getByteLen(content);
			if( len > 140){
				alert("发布内容过长！");
				return false;
			}
			
			$('#btnSubmit').attr("disabled",true);
			
			$.ajax({type: "POST", url: "../../xy/article/DealWBPush.do", async:false, 
				data : {
					"DocIDs" : push_WB.DocIDs,
					"DocLibID" : push_WB.DocLibID,
					"paramWB" : strWB,
					"paramPic" : strPic,
					"content" : $('#pushDescription').val()
				},
				success: push_WB.close,
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				}
			});
		},
		getByteLen : function (val) {
            var len = 0;
            for (var i = 0; i < val.length; i++) {
                if (val[i].match(/[^\x00-\xff]/ig) != null) //全角
                    len += 2;
                else
                    len += 1;
            }
            return len;
        },
		doCancel : function(){
			window.onbeforeunload = null;
			
			$("#btnSubmit").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		},
		close : function(msg) {
			if (msg != "ok") {
				alert("保存时异常：" + msg);
			} 
			window.onbeforeunload = null;
			$("#btnSubmit").disabled = true;
			$("#btnCancel").disabled = true;
			
			e5_form_event.beforeExit();
		},
		collectWB : function(){	
			var dataArray = [];
	        var form0 = $("input[type='checkbox']:checked");
	        for(var i = 0; i < form0.length; i++){
	           //如果这个checkbox被选中了，在json中的这个对象后面添加
				var wbid = form0[i].id;
				var data ={id:wbid};
				dataArray.push(data);
	        }      
	        return dataArray;
		},
		collectPic : function(){	
			var dataArray = [];
	        var imglist = $("div[class='imgContent'] img");
	        for(var i=0;i<imglist.length;i++){ //循环为每个img设置
				var data ={id:imglist[i].id};
				dataArray.push(data);
	        } 
	        return dataArray;
		},
		removePic : function(){
			var divid = $(this).parent('div').attr('id');
			$(this).parent('div').remove(); //remove text box
			return false;
		}
}

$(function(){
	push_WB.init();
});


//复制以使用表单定制中的方法
e5_form_event = {
	//---各种事件、回调事件---
	curDialog : null,
	_getDialogPos : function(el) {
		function Pos (x, y) {
			this.x = x;
			this.y = y;
		}
		function getPos(el) {
			var r = new Pos(el.offsetLeft, el.offsetTop);
			if (el.offsetParent) {
				var tmp = getPos(el.offsetParent);
				r.x += tmp.x;
				r.y += tmp.y;
			}
			return r;
		}
		var p = getPos(el);
		
		//决定弹出窗口的高度和宽度
		var dWidth = 400;
		var dHeight = 300;

		var sWidth = document.body.clientWidth; //窗口的宽和高
		var sHeight = document.body.clientHeight;
		
		if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
		if (dHeight + 30 > sHeight) dHeight = sHeight - 30;
		
		//顶点位置
		var pos = {left : p.x +"px", 
			top : (p.y + el.offsetHeight - 1)+"px",
			width : dWidth,
			height : dHeight
			};
		if (pos.left + dWidth > sWidth)
			pos.left = sWidth - dWidth;
		if (pos.top + dHeight > sHeight)
			pos.top = sHeight - dHeight;
		
		return pos;
	},
	//取消按钮。调after.do解锁
	doCancel : function(e) {
		window.onbeforeunload = null;
		
		$("#btnSave").disabled = true;
		$("#btnCancel").disabled = true;
		
		e5_form_event.beforeExit();
	},
	//关闭窗口。调after.do解锁
	beforeExit : function(e) {
		var dataUrl = "../../e5workspace/after.do?UUID=" + UUID;
		
		//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
		if (e && e5_form_event.isChrome())
			window.open(dataUrl, "_blank", "width=10,height=10");
		else
			window.location.href = dataUrl;
	},
	isChrome : function() {
		var nav = e5_form_event.navigator();
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
	}
};