$(document).ready(function() {

	//右侧选项卡切换效果
	function tab(name1, className, name2, name3) {
		$(name1).click(function() {
			$(this).addClass(className).siblings().removeClass(className);
			var index = $(name1).index(this);
			$(name2).children(name3).eq(index).show().siblings().hide();
		});
	}
	tab("#sidebar-panel ul li", "selected", ".tab_box", "div")


	//获取当前浏览器的高度，设置左右功能栏高度为浏览器默认高度

	function setHeight(name1, name2) {
		var h = $(window).height();
		var th = $("#" + name1).outerHeight();
		$("#" + name2).css({
			"height": (h - th) + 'px',
			"background-color": "#3d3d3d"
		})
	}
	//setHeight("headerArea", "sliderBar");
	//setHeight("headerArea", "sidebar-panel");


	//给当前选中的元素添加背景颜色

	function setBgColor(name) {
		$("#" + name).click(function() {
			$(this).addClass('select').siblings().removeClass('select');
		})
	}
	setBgColor("textAlign li")
	setBgColor("fontSt li")
	setBgColor("tFont li")

	//收起展开效果
	function hideArea(name) {
		$(name).each(function() {
			$(this).click(function() {
				$(this).parent().siblings().slideToggle();
			})
		})
	}
	hideArea(".iconArea");

	//select下拉框效果
	function divselect(divSelector, name1) {
		

		var $div = $(divSelector);
		//显示
		$div.click(function() {
			var $this = $(this);
			var $ul = $this.children('ul');
			$ul.slideToggle();
		});
		//选择
		$div.find('a').click(function() {
			var $this = $(this);
			var $city = $this.parents('ul').siblings("cite");
			var txt = $this.text();
			$city.html(txt);
			var value = $div.attr("selectid");
			$this.children('ul').hide();
		})

	}

	divselect(".divselect", ".inputselect");


});