var tag_form = {
	addTags : [],
	deleteTags : [],
	oldTags : [],
	
	init : function() {
		//oldTags
		var lis = $("#tags li");
		for (var i = 0; i < lis.length; i++) {
			tag_form.oldTags.push(lis[i].title);
		}
		
		$("#tag").keydown(tag_form.keydown);
		$(".btnSearch").click(tag_form.clickdown);
		$("#tag").autocomplete("Member.do?a=findTag", tag_form.options);
		$("#btnPost").click(tag_form.tagSubmit);
		$(".delete-icon").click(tag_form.delTag);
		
		//TAB页切换
		$("#tabs li a").on({
			"click":function(evt) {
				evt.preventDefault();
				var elm = $(evt.target);
				var type = elm.data("src");
				if (!!type) {
					$("#contentFrame").attr("src", type);
					elm.parent().addClass("active").siblings().removeClass("active");
				}
			}
		}).first().click();
		$(window).trigger("resize");
	},
	//标签保存
	tagSubmit : function() {
		var postForm = document.getElementById("postForm");
		postForm["Tags"].value = tag_form.join(tag_form.addTags);
		postForm["DeleteTags"].value = tag_form.join(tag_form.deleteTags);
		
		if (!postForm["Tags"].value && !postForm["DeleteTags"].value) {
			var tool = window.parent.e5.mods["workspace.toolkit"];
			tool.self.closeOpDialog("", 2);
		} else {
			postForm.submit();
		}
	},
	//添加按钮点击事件
	clickdown :function(){
		var value = $("#tag").val();
		if (!value) return;
		
		var tags = value.split(" ");
		for (var i = 0; i < tags.length; i++) {
			tag_form.addOneTag(tags[i]);
		}
		$("#tag").val("");
		
		if($(".mid1").height()<$("#tags").height()){    //显示更多按钮
			$(".more1").show();
			} 
	},
	//标签输入框
	keydown : function(evt) {
		evt = evt || event;
		if (evt.keyCode != 13) return;
		
		var value = $("#tag").val();
		if (!value) return;
		
		var tags = value.split(" ");
		for (var i = 0; i < tags.length; i++) {
			tag_form.addOneTag(tags[i]);
		}
		$("#tag").val("");
		
		if($(".mid1").height()<$("#tags").height()){    //显示更多按钮
			$(".more1").show();
			} 
	},
	//在标签区域加一个标签
	addOneTag : function(tag) {
		tag = tag.replace(/\"|\'/g, "");
		if (!tag) return;
		
		//检查是否已有同名标签
		var lis = $("#tags li");
		for (var i = 0; i < lis.length; i++) {
			if (lis[i].title == tag) {
				return;
			}
		}
		//添加标签
		var span = $("<span class='delete-icon'>[X]</span>");
		span.click(tag_form.delTag);

		var li = $("<li/>");
		li.attr("title", tag);
		li.attr("class", "tag");
		li.html(tag);
		li.append(span);
		
		$("#tags").append(li);
		
		tag_form.toAddList(tag);
	},
	//删除标签区域的一个标签
	delTag : function(evt) {
		if (!confirm("确定要删除吗？")) return;
			evt = evt || event;
			if(navigator.userAgent.indexOf("MSIE")>0) {   //判断是否是IE浏览器
				var li = evt.srcElement.parentNode;
			}else{    //其他浏览器
				var li = evt.target.parentNode;
			}
	//	var li = evt.srcElement.parentNode;
			var tag = li.title;
		
			var ul = document.getElementById("tags");
			if(navigator.userAgent.indexOf("MSIE")>0) {   //判断是否是IE浏览器
				ul.removeChild(evt.srcElement.parentNode);
			}else{    //其他浏览器
				ul.removeChild(evt.target.parentNode);
			}
	//	ul.removeChild(evt.srcElement.parentNode);
		
			tag_form.toDeleteList(tag);
			
			if($(".mid1").height()>=$("#tags").height())    //隐藏更多按钮
				$(".more1").hide();
	},
	/**
	 * 添加标签时，加到add数组：
	 * 不是旧标签，才加；
	 * 新加标签中没有，才加；
	 * 若删除列表中有，则去掉
	 */
	toAddList : function(tag) {
		if (!tag_form.contains(tag_form.oldTags, tag) && !tag_form.contains(tag_form.addTags, tag))
			tag_form.addTags.push(tag);
		
		tag_form.splice(tag_form.deleteTags, tag);
	},
	/**
	 * 删除标签时，加到delete数组：
	 * 是旧标签，才加
	 * 已删标签中没有，才加；
	 * 若新加数组中有，则去掉
	 */
	toDeleteList : function(tag) {
		if (tag_form.contains(tag_form.oldTags, tag) && !tag_form.contains(tag_form.deleteTags, tag))
			tag_form.deleteTags.push(tag);
		
		tag_form.splice(tag_form.addTags, tag);
	},

	contains : function(tags, tag) {
		for (var i = 0; i < tags.length; i++) {
			if (tags[i] == tag)
				return true;
		}
		return false;
	},
	splice : function(tags, tag) {
		for (var i = 0; i < tags.length; i++) {
			if (tags[i] == tag) {
				tags.splice(i, 1);
				break;
			}
		}
	},
	join : function(tags) {
		var result = "";
		for (var i = 0; i < tags.length; i++) {
			if (result) result += " ";
			result += tags[i];
		}
		return result;
	},
	//auto-complete控件需要的参数
	options : {
		minChars : 1,
		delay : 400,
		autoFill : true,
		selectFirst : true,
		cacheLength : 1,
		matchSubset : false,
		//需要把data转换成json数据格式
		parse: function(data) {
			if(!data||~data.indexOf("No Records")){
				return [];
			}
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }
	}
}

$(window).on({
	resize:function(){
		$("#contentFrame").css("height",document.documentElement.clientHeight - 160 +"px");
	},
	load:tag_form.init
})


window.onload=function(){    //页面加载完成之前进行判断
	  if($(".mid1").height()<$("#tags").height())
	         $(".more1").css("display","block");
	  if($(".mid2").height()<$("#tags2").height())
	         $(".more2").css("display","block");
	 }

$(function(){
	$(".more1").click(function(){
		if($(".more1").text()=="更多"){
			$(".mid1").css({"overflow":"auto","border":"none"});	
			$(".more1").html('收起<img src="../img/up.png">');
		}else{
			$(".mid1").css({"overflow":"hidden","border":"none"});	
			$(".more1").html('更多<img src="../img/down.png">');
		}
			$(".mid1").animate({scrollTop:0});   //滚动条回顶部	
		});
		
	$(".more2").click(function(){
		if($(".more2").text()=="更多"){
			$(".mid2").css({"overflow":"auto","border":"none"});	
			$(".more2").html('收起<img src="../img/up.png">');
		}else{
			$(".mid2").css({"overflow":"hidden","border":"none"});	
			$(".more2").html('更多<img src="../img/down.png">');
		}
			$(".mid2").animate({scrollTop:0});
	});
});