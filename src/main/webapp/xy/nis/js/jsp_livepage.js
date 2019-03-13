//直播详情页，用于分享
var xy_share = {		
		url:"LiveView.do",//模拟外网的直播api
	//页面初始化
	init: function(){
		var id = xy_share.getParam("id");
		if (!id) return;
		var start = xy_share.getParam("start");
		if (!start) start = 0;
		
		var count = xy_share.getParam("count");
		if (!count) count = 20;
		
		var index = xy_share.getParam("index");
		if (!index) index = 0;
		
		var lo = xy_share.getParam("lo");
		if (!lo) lo = 0;
		
		var la = xy_share.getParam("la");
		if (!la) la = 0;
		//访问后台api
		xy_share.access(id, start, count,index,lo,la);
	},
	
	//点击放大图片
	enlargePics:function(at){
	
		//锁住全屏
		var width=$(document.body).width();
		var height=$(document.body).height();
		$('#screen').css('width',width+'px').css('height',height+'px').css("overflow","hidden");
		
		$("body,html").css("overflow","hidden");    //禁用滚动条
		$("#screen").css("display","block");
	
		//显示大图
		var url=$("#liimg"+at).attr("attrurl");
		$('#maximg').attr("src",url+".2");
		$('#modal-overlay').css({
			"position":"absolute",
			"left":"50%",
			"top":"50%",
			"margin-top":"-30%",
			"margin-left":"-50%"
			
			
		
		});	
		$('#modal-overlay img').css({
			"position":"absolute",
			"left":"50%",
			"top":"50%",
			"margin-top":"-30%",
			"margin-left":"-50%"
			
		});	
		
		$('#modal-overlay img').show(600)
		$("#modal-overlay").show();
	},
	
	//访问后台api，得到json列表。读更多跟帖时也用这个函数。
	access : function(id, start, count,index,lo,la) {
		//id=0,查询所有的主播信息
		var params = "?id=" + id + "&start=" + start + "&count=" + count+"&index=" + index+ "&lo=" + lo+ "&la=" + la;
		$.ajax({url: xy_share.url + params,crossDomain: true,async:true, dataType:"json",cache: true,
			success: function(datas) {
				//获得主播ID=xy_share.getParam("id")主播信息，作为直播详情界面的主播信息
				var list=datas.list;
				for (var li in list){
					var fileId=null;
					if(li==0){
						//显示主贴主播信息
						fileId=list[li].fileId;
						$("#title").text(list[li].title);
						var publishtime=list[li].publishtime;
						var dateTimeStamp=xy_share.getDateTimeStamp(publishtime);
						$("#publishtime").html(xy_share.getDateDiff(dateTimeStamp));
						$("#user").html(list[li].user);
						$("#userID").val(list[li].userID);
						var strUrl=list[li].userIcon;
						var paraString = strUrl.substring(strUrl.indexOf("?") + 1, strUrl.length).split("&");
						var paraObj={};
						for(i=0;j=paraString[i];i++){
							var para=paraString[i];
							if(para=="uid=0"){
								$(".header-img").attr("src","img/user.png");
							}
							else{
								$(".header-img").attr("src",list[li].userIcon);
							}
							paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
						}						
						$("#countPraise").html(list[li].countPraise);						
						$("#class1content").html(list[li].content);
						
						if (list[li].attachments && list[li].attachments.length > 0) {
							var attachmentsurl=list[li].attachments[0].url;
							if(attachmentsurl.toLowerCase().indexOf("http://")<0){
								attachmentsurl="../image.do?path="+attachmentsurl;
							}			
							$(".img001").attr("src",attachmentsurl);
						}
						
						var attachments=list[li].attachments;
						var imgObj=null;
						var picObj=null;
						for(var at in attachments){
							var liID;
							var liObj = $("<li>").attr("name",liID);
							if(attachments[at].url!="" && attachments[at].url!=null){
								if(attachments[at].type=="1"){
									var url=attachments[at].url;
									if (url.toLowerCase().indexOf("http://") < 0) {
										url = "../image.do?path=" + url;
									}
									imgObj = $("<img id='liimg"+at+"' attrurl='"+url+"' onclick='xy_share.enlargePics("+at+");'>").attr("src", url + ".0");
									//添加到画面上的UL标签里
								      liObj.append(imgObj).appendTo("#mainimgdata");
								}
							}
							if(attachments[at].url!="" && attachments[at].url!=null){
						        if(attachments[at].type=="2"){
						        	 picObj = $("<embed style='width:416px;height:300px;margin-top: 14px;margin-left: 13px;'>").attr("src",attachments[at].url);
								     picObj.appendTo("#mainpic");
						        }
					        }					        
						}
					}
					else{
						//显示跟贴信息
						$("#context").append("<div class=\"context\" pagesize=\"2\" id=\"context" + li + "\"></div>");					
						//拼接hoster(头像)的html
						$("#context"+li).append("<div class=\"hoster\"  id=\"hoster" + li + "\"></div>");
						$("#hoster"+li).append("<div class=\"userpic\"  id=\"userpic" + li + "\"></div>");
						$("#userpic"+li).append("<div class=\"headerpic\"  id=\"headerpic" + li + "\"></div>");
						var strUrl=list[li].userIcon;
						var paraString = strUrl.substring(strUrl.indexOf("?") + 1, strUrl.length).split("&");
						var paraObj={};
						for(i=0;j=paraString[i];i++){
							var para=paraString[i];
							if(para=="uid=0"){
								$("#headerpic" + li).append("<img  class=\"userIcon"+li+"\"  src='img/user.png'></img>");
							}
							else{
								$("#headerpic" + li).append("<img  class=\"userIcon"+li+"\"  src=\""+list[li].userIcon+"\"></img>");
							}
							paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
						}								
						$("#userpic" + li).append("<i class=\"traigle\" id=\"traigle"+li+"\"></i>");
						if(fileId==list[li].fileId){
							$("#hoster" + li).append("<h2>播主</h2>");
						}						
						//拼接content（跟帖）的html
						$("#context"+li).append("<div class=\"usrname\"  id=\"usrname" + li + "\"></div>");
						$("#usrname"+li).append("<div class=\"title\"  id=\"title" + li + "\"></div>");					
						$("#title" + li).append("<span  class=\"left\" id=\"left"+li+"\"></span>");
						$("#left" + li).append("<span class=\"user"+li+"\">"+list[li].user+"</span>");
						$("#left" + li).append("<span class=\"time\" id=\"time"+li+"\">"+list[li].publishtime+"</span>");					
						$("#title" + li).append("<span class=\"right\"  id=\"right"+li+"\"></span>");
						$("#right" + li).append("<img class=\"img"+li+"\"  src=\"img/praise.png\"></img>");
						$("#right" + li).append("<span class=\"num"+li+"\">"+list[li].countPraise+"</span>");
											
						$("#usrname"+li).append("<div id=\"content" + li + "\"></div>");
						$("#content" + li).append("<span>"+list[li].content +"</span>");
						
						$("#usrname"+li).append("<div id=\"imgandvideo" + li + "\"></div>");
						$("#imgandvideo"+li).append("<ul  id=\"ulimgdata"+li+"\"></ul>");
						$("#imgandvideo"+li).append("<div  id=\"ulvideodata"+li+"\"></div>");
						var attachments=list[li].attachments;
						for(var at in attachments){
							if(attachments[at].type=="1"){
								if(attachments[at].url!="" && attachments[at].url!=null){		
									var url=attachments[at].url;
									if(url.toLowerCase().indexOf("http://")<0){
										url = "../image.do?path=" + url;	
									}
									$("#ulimgdata"+li).append("<li><img  id='liimg"+at+"' attrurl='"+url+"' onclick='xy_share.enlargePics("+at+");'  alt=\"\" src=\""+url+".0\"></li>");
																	
									for (var i = 0; i < $("#ulimgdata"+li).find("li").length; i++) {
										if($("#ulimgdata"+li).find("li").length !== 1){
											$("#ulimgdata"+li).find("li").css({
												"width": "",
												"height": "",
												"margin":"0 auto"
											})
										}else{
											var ht = $("embed").outerHeight();
											var wt = $("embed").outerWidth();
											$("#ulimgdata"+li).find("li").css({
												"width": wt,
												"height": ht,
												"margin":"0 auto"
											})
											
										}
									}
									
								}
								
							}
							if(attachments[at].type=="2"){
								if(attachments[at].url!="" && attachments[at].url!=null){
									$("#ulvideodata"+li).append("<li><embed style='width:416px;height:302px;' alt=\"\" src=\""+attachments[at].url+"\"></li>");
								}	
							}					
						}					
					}										
				}
				//更多显示
				 $.showMore("#context");
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(xy_share.url + params + "\r\n" + errorThrown + ':' + textStatus);  // 错误处理
			}
		});
	},
	//把后台数据显示到页面上
	show : function(datas) {
	
	},
	
	//js函数代码：字符串转换为时间戳
	getDateTimeStamp:function(dateStr){
		 return Date.parse(dateStr.replace(/-/gi,"/"));
	},
	
	//计算时间差	
	getDateDiff:function(dateTimeStamp){
		var minute = 1000 * 60;
		var hour = minute * 60;
		var day = hour * 24;
		var halfamonth = day * 15;
		var month = day * 30;
		var now = new Date().getTime();
		var diffValue = now - dateTimeStamp;
		if(diffValue < 0){
		 //若日期不符则弹出窗口告之
		 }
		var monthC =diffValue/month;
		var weekC =diffValue/(7*day);
		var dayC =diffValue/day;
		var hourC =diffValue/hour;
		var minC =diffValue/minute;
		if(monthC>=1){
		 result=parseInt(monthC) + "个月前";
		 }
		 else if(weekC>=1){
		 result=parseInt(weekC) + "周前";
		 }
		 else if(dayC>=1){
		 result=parseInt(dayC) +"天前";
		 }
		 else if(hourC>=1){
		 result= parseInt(hourC) +"个小时前";
		 }
		 else if(minC>=1){
		 result= parseInt(minC) +"分钟前";
		 }else
		 result="刚刚发表";		
		 return result;
	},
	
	//从当前页面的url中得到参数
	getParam : function(name) {
		var params = window.location.href;
		
		var start = params.indexOf(name + "=");
		if (start < 0) return "";
		
		start += name.length + 1;
		var end = params.indexOf("&", start);
		
		if (end > 0) {
			return params.substring(start, end);
		} else {
			return params.substring(start);
		}
	}
}


$(function() {
	xy_share.init();
});

//显示更多
(function () {
    var showMoreNChildren = function ($children, n) {
        //显示某jquery元素下的前n个隐藏的子元素
        var $hiddenChildren = $children.filter(":hidden");
        var cnt = $hiddenChildren.length;
        for (var i = 0; i < n && i < cnt ; i++) {
            $hiddenChildren.eq(i).show();
        }
        return cnt - n;//返回还剩余的隐藏子元素的数量
    }
 
    jQuery.showMore = function (selector) {
        if (selector == undefined) { selector = "#context" }
        //对页中现有的class=showMorehandle的元素，在之后添加显示更多条，并绑定点击行为
        $(selector).each(function () {
            var pagesize = $(this).attr("pagesize") || 10;
            var $children = $(this).children();
            if ($children.length > pagesize) {
                for (var i = pagesize; i < $children.length; i++) {
                    $children.eq(i).hide();
                }
           $(".more").append($("<div class='showMorehandle' >显示更多</div>")).insertAfter($(this)).click(function () {
                    if (showMoreNChildren($children, pagesize) <= 0) {
                        //如果目标元素已经没有隐藏的子元素了，就隐藏“点击更多的按钮条”
                        $(".showMorehandle").hide();
                    };
                });
            }
        });
    }
})();

//点击界面任何一个地方，关闭大图
function cancelMaxPics(){
	$("html").css({overflow:"scroll"});
	$("#screen").css("display","none");
	$("#modal-overlay").css("display","none");
}