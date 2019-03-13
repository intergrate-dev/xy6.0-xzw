//论坛详情页
var xy_share = {
	url : "ForumView.do", //模拟外网的论坛api
	//页面初始化
	init: function(){
		var id = xy_share.getParam("id");
		if (!id) return;
		
		var start = xy_share.getParam("start");
		if (!start) start = 0;
		var count = xy_share.getParam("count");
		if (!count) count = 20;	
		
		//访问后台api
		xy_share.access(id, start, count);
	},
	
	showMoreNChildren : function ($children, n) {
        //显示某jquery元素下的前n个隐藏的子元素
        var $hiddenChildren = $children.filter(":hidden");
        var cnt = $hiddenChildren.length;
        for (var i = 0; i < n && i < cnt ; i++) {
            $hiddenChildren.eq(i).show();
        }
        return cnt - n;//返回还剩余的隐藏子元素的数量
   },
   
   showMore : function (selector) {
        if (selector == undefined) { selector = "#usr-say" }
        //对页中现有的class=showMorehandle的元素，在之后添加显示更多条，并绑定点击行为
        $(selector).each(function () {
            var pagesize = $(this).attr("pagesize") || 10;
            var $children = $(this).children();
            if ($children.length > pagesize) {
                for (var i = pagesize; i < $children.length; i++) {
                    $children.eq(i).hide();
                }
                $(".showmore").append($("<div class='showMorehandle' >显示更多</div>")).insertAfter($(this)).click(function () {
                if (xy_share.showMoreNChildren($children, pagesize) <= 0) {
                        //如果目标元素已经没有隐藏的子元素了，就隐藏“点击更多的按钮条”
                        $(".showMorehandle").hide();
                    };
                });         
            }
        });
   },
     
	//访问后台api，得到json列表。读更多跟帖时也用这个函数。
	access : function(id, start, count) {
		var params = "?id=" +id + "&start=" + start + "&count=" + count;
		$.ajax({url: xy_share.url + params, async:true, dataType:"json",
			success: function(datas) {
				//************功能：获取主贴信息，并且显示   **************
				//获取主贴信息，并且显示
				var list =datas.list
				for(var li in list){					
					if(li==0){
						$(".username").text(list[0].user);
						$(".develop").text(list[0].tag);
						$(".time").text(list[li].publishtime);
						$(".newstitle").text(list[0].title);
						$(".newscontent").text(list[0].content);
						
						//获取头像地址，头像地址如果存在则显示，如果头像地址不存在则显示默认图片
						var strUrl=list[li].userIcon;
						var paraString = strUrl.substring(strUrl.indexOf("?") + 1, strUrl.length).split("&");
						var paraObj={};
						for(i=0;j=paraString[i];i++){
							var para=paraString[i];
							if(para=="uid=0"){
								$("#headerimage").attr("src","img/user.png");
							}
							else{
								$("#headerimage").attr("src",list[li].userIcon);
							}
							paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
						}				
						
						//主贴附件图片与视频
						var attachments=list[li].attachments;
						var imgObj=null;
						for(var at in attachments){
							var liID;
							var liObj = $("<li>").attr("name",liID);
							if(attachments[at].url!="" && attachments[at].url!=null){
								if(attachments[at].type=="1"){
									var url=attachments[at].url;
									if(url.toLowerCase().indexOf("http://")<0){
										url="../image.do?path="+url;
									}
									imgObj = $("<img>").attr("src",url + ".0");
									//添加到画面上的UL标签里
								     liObj.append(imgObj).appendTo("#mainimgdata");
								}
								if(attachments[at].url!="" && attachments[at].url!=null){
							        if(attachments[at].type=="2"){
							        	 picObj = $("<embed style='width:416px;height:300px;margin-top: 14px;margin-left: 13px;'>").attr("src",attachments[at].url);
									     picObj.appendTo("#mainpic");
							        }
						        }	
							}
						}
										
					}
					//显示跟帖信息
					else{
						$(".usr-say").append("<div   pagesize=\"4\" class=\"floor\" id=\"floor" + li + "\"></div>");
						$("#floor"+li).append("<img  class=\"usr-pic\" id=\"usr-pic"+li+"\" ></img>");						
						//获取头像地址，头像地址如果存在则显示，如果头像地址不存在则显示默认图片
						var strUrl=list[li].userIcon;
						var paraString = strUrl.substring(strUrl.indexOf("?") + 1, strUrl.length).split("&");
						var paraObj={};
						for(i=0;j=paraString[i];i++){
							var para=paraString[i];
							if(para=="uid=0"){
								$("#usr-pic"+li).attr("src","img/morentouxiang.png");
							}
							else{
								$("#usr-pic"+li).attr("src",list[li].userIcon);
							}
							paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
						}				
						
						$("#floor"+li).append("<span   class=\"right usrwrap\"  id=\"rightusrwrap"+li+"\"></span>");
							$("#rightusrwrap"+li).append("<span   class=\"usr-info\"  id=\"usrinfo"+li+"\"></span>");		
								$("#usrinfo"+li).append("<span  id=\"spanname"+li+"\">"+list[li].user +"</span>");
								$("#usrinfo"+li).append("<span   class=\"floor-time\"  id=\"floor-time"+li+"\"></span>");
								$("#floor-time"+li).append("<span  id=\"spanfloor"+li+"\">"+li+"F"+"</span>");
								$("#floor-time"+li).append("<span  id=\"spantime"+li+"\">"+list[li].publishtime +"</span>");
							
							$("#rightusrwrap"+li).append("<span   class=\"right\"  id=\"right"+li+"\"></span>");
								$("#right"+li).append("<img  class=\"img\" id=\"img"+li+"\"  alt=\"#\" src=\"img/praise.png\"></img>");
								$("#right"+li).append("<span   class=\"num\"  id=\"num"+li+"\">"+list[li].countPraise +"</span>");
						
							$("#rightusrwrap"+li).append("<div  style=\"clear:both\"></div>");
							$("#rightusrwrap"+li).append("<span   class=\"usr-comment\"  id=\"usrcomment"+li+"\">"+list[li].content+"</span>");
							$("#rightusrwrap"+li).append("<span   class=\"usr-attachmentsinfo\"  id=\"attachmentsinfo"+li+"\"></span>");
							
							//跟帖附件信息：图片，视频
							var attachments=list[li].attachments;
							for(var at in attachments){
								var liID;
								var liObj = $("<li>").attr("name",liID);
								if(attachments[at].url!="" && attachments[at].url!=null){
									if(attachments[at].type=="1"){
										var url=attachments[at].url;
										if(url.toLowerCase().indexOf("http://")<0){
											url="../image.do?path="+url;
										}
										imgObj = $("<img>").attr("src",url + ".0");
										//添加到画面上的UL标签里
									     liObj.append(imgObj).appendTo("#attachmentsinfo"+li);
									}
									if(attachments[at].url!="" && attachments[at].url!=null){
								        if(attachments[at].type=="2"){
								        	 picObj = $("<embed style='width:416px;height:300px;margin-top: 14px;margin-left: 13px;'>").attr("src",attachments[at].url);
										     picObj.appendTo("#mainpic");
								        }
							        }	
								}
							}														
						$("#floor"+li).append("<div  style=\"clear:both\"></div>");
					}					
				}				
				//更多显示
				 xy_share.showMore(".usr-say");									
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(xy_share.url + params + "\r\n" + errorThrown + ':' + textStatus);  // 错误处理
			}
		});
	},
	//把后台数据显示到页面上
	show : function(id, start, count,li) {
//		if (!datas) return;		
		//TODO：显示帖子列表	
		var params = "?id=" +id + "&start=" + start + "&count=" + count;
		$.ajax({url: xy_share.url + params, async:true, dataType:"json",
			success: function(datas) {
				var list=datas.list;
				for(var lis in list){
					//排除其本身，显示余下的跟帖信息
					if(lis>0){
						//找到相应的楼层，并在相应楼层下创建div
						$("#floor"+li).append("<div  id=\"floor"+li+"usranswerfloor" + lis + "\"></div>");
						
						$("#floor"+li+"usranswerfloor"+lis).append("<span   class=\"right usrwrap\"  id=\"floor"+li+"rightusranswer"+lis+"\"></span>");
						$("#floor"+li+"rightusranswer"+lis).append("<span   class=\"usr-info\"  id=\"floor"+li+"rightusranswerinfo"+lis+"\"></span>");
						$("#floor"+li+"rightusranswerinfo"+lis).append("<span  id=\"floor"+li+"spananserusername"+lis+"\">"+list[lis].user +"</span>");
						$("#floor"+li+"rightusranswerinfo"+lis).append("<span  id=\"floor"+li+"spananseruserfloor"+lis+"\">"+lis+"F"+"</span>");
						$("#floor"+li+"rightusranswerinfo"+lis).append("<span  id=\"floor"+li+"spananserusertime"+lis+"\">"+list[li].publishtime +"</span>");
						
						$("#floor"+li+"rightusranswer"+lis).append("<span   class=\"right\"  id=\"floor"+li+"rightanswer"+lis+"\"></span>");
						$("#floor"+li+"rightanswer"+lis).append("<span   class=\"num\"  id=\"floor"+li+"spananserpraisenum"+lis+"\">"+list[lis].countPraise +"</span>");
						
						$("#floor"+li+"rightusranswer"+lis).append("<div  style=\"clear:both\"></div>");
						$("#floor"+li+"rightusranswer"+lis).append("<span   class=\"usr-comment\"  id=\"floor"+li+"anserusrcomment"+lis+"\">"+list[lis].content+"</span>");					
					}
					
				}				
			}
			
		})
		
				
	},
	//************功能：获取主贴信息，并且显示   *******************************
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