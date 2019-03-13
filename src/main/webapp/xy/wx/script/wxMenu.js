(function(){
	// 工具对象
	var tool = (function(){
		// 模态窗口
		var $modal = $(
				"<div class='modal fade' tabindex='-1' role='dialog'>" +
					"<div class='modal-dialog' role='document'>" +
						"<div class='modal-content'>" +
							"<div class='modal-header' style='color: #000; background: #eee'>" +
								"<a type='button' class='close' data-dismiss='modal' aria-label='Close'>" +
									"<span aria-hidden='true'>&times;</span>" +
								"</a>" +
								"<h4 class='modal-title' style='text-align:left;'></h4>" +
							"</div>" +
							"<div class='modal-body' style='background: #fff'></div>" +
							"<div class='modal-footer' style='background: #eee; text-align:center;'>" +
								"<a class='btn btn-success btn-ok' style='width:120px;'>确定</a>" +
								"<a class='btn btn-default' style='width:120px;color:#fff; margin-top:0px;border:1px solid #b1b1b1;background:#b1b1b1;' data-dismiss='modal'>取消</a>" +
							"</div>" +
						"</div>" +
					"</div>" +
				"</div>"
		)
		// 添加菜单
		var $modal_add = $modal.clone();
		var _$add_body = $("<div style='margin: 45px 66px'></div>").appendTo($modal_add.find(".modal-body"));
		_$add_body.append("<p class='msg'></p>");
		_$add_body.append("<span style='padding: 3px 5px; display: block; border: 1px solid #ccc'><input type='text' style='border: 0; width: 100%'/></span>");
		_$add_body.append("<p class='fail' style='padding-top: 4px; color: red; display: none'></p>");

		// 重命名窗口
		var $modal_menu_rename = $modal.clone();
		var _$rename_body = $("<div style='margin: 45px 66px'></div>").appendTo($modal_menu_rename.find(".modal-body"));
		_$rename_body.append("<span style='padding: 3px 5px; display: block; border: 1px solid #ccc'><input type='text' style='border: 0; width: 100%'/></span>");
		_$rename_body.append("<p class='fail' style='padding-top: 4px; color: red; display: none'></p>");
		_$rename_body.append("<p class='msg' style='padding-top: 4px;'></p>");

		_$rename_body.find("input").focus(function(){
			_$rename_body.find("p.fail").css("display","none");
		});

		// 删除窗口
		var $modal_menu_remove = $modal.clone();
		$modal_menu_remove.find(".modal-title").text("温馨提示");
		var _$remove_body = $("<div style='margin: 45px 0'></div>").appendTo($modal_menu_remove.find(".modal-body"));
		$("<div style='margin: 0 auto;width: 350px;'></div>").appendTo(_$remove_body)
				.append("<span style='margin-right: 15px; float: left'><i class='icon_msg warn'></i></span>")
				.append("<div style=''><p>删除确认</p><p style='color: #ccc'>删除后该菜单下设置的消息将不会被保存</p></div>");

		// 二级菜单确认
		var $modal_menu_two = $modal.clone();
		$modal_menu_two.find(".modal-title").text("温馨提示");
		var _$two_body = $("<div style='margin: 45px 0'></div>").appendTo($modal_menu_two.find(".modal-body"));
		$("<div style='margin: 0 auto;width: 500px;'></div>").appendTo(_$two_body)
				.append("<span style='margin-right: 15px; float: left'><i class='icon_msg warn'></i></span>")
				.append("<div style=''><p>二级菜单确认</p><p style='color: #ccc'>使用二级菜单后，当前编辑的消息将会被清除。确定使用二级菜单？</p></div>");

		var pfun = function($model, fn){
			$model = $model.clone();
			(fn || $.noop).call($model, $model);
			return $model;
		}

		return {
			getAdd: function(fn){
				return pfun($modal_add,fn);
			},
			getMenuRenameModal: function (fn) {
				return pfun($modal_menu_rename,fn);
			},
			getMenuRemoveModal: function (fn) {
				return pfun($modal_menu_remove, fn);
			},
			getMenuTwoModel:function(fn) {
				return pfun($modal_menu_two, fn);
			}
		}
	})();

	var $menulist = $("#menu-list").empty()

	var $menu_opr1 = $("<span class='menu_opr'>" +
			"<a href='javascript:void(0)' class='opr_meta no-soft add_gray'><span class='glyphicon glyphicon-plus' data-toggle='tooltip' title='添加'></span></a>" +
			"<a href='javascript:void(0)' class='opr_meta soft soft_gray'><span class='glyphicon glyphicon-menu-hamburger' data-toggle='tooltip' title='排序'></span></a>" +
			"</span>");

	var $menu_opr2 = $("<span class='menu_opr'>" +
			"<a href='javascript:void(0)' class='opr_meta soft soft_gray'><span class='glyphicon glyphicon-menu-hamburger' data-toggle='tooltip' title='排序'></span></a>" +
			"</span>");

	var getData = function(){
		var result = null;
		$.ajax({ url : "Menus.do", data : menu_data, async: false, success : function(data){
				data = data || { menu :{ button: [] } };
				if ($.isArray(data)){
					data = { menu :{ button: data } };
				}
				if (data.button){
					data = { menu : data };
				}
				result = data;
			}
		});
		return result;
	};

	var append_menu = function($menulist){
		return $("<dl class='menu'></dl>").appendTo($menulist);
	}
	var append_one = function($dl, btn){
		var $dt = $("<dt class='menu-item'></dt>").appendTo($dl).attr($.extend({}, btn, {sub_button: []}));
		$dt.append("<a href='javascript:void(0)' class='menu-link'>" + btn.name + "</a>");
		$dt.append($menu_opr1.clone());
		return $dt;
	};
	var append_two = function($dl, sub){
		var $dd = $("<dd class='menu-item'></dd>").appendTo($dl).attr($.extend({}, sub, {sub_button: []}));
		$dd.append("<i class='icon_dot'>●</i>");
		$dd.append("<a href='javascript:void(0)' class='menu-link'>" + sub.name + "</a>");
		$dd.append($menu_opr2.clone());
//				$dd.find("[data-toggle='tooltip']").tooltip({});
		return $dd;
	}

	var fillList = function(data){
		if(!data) return;
		
		var menu = data.menu || {"menu":{"button":[]}};
		var button = menu.button || [];
		$menulist.empty();
		
		for(var i in button){
			var btn = button[i];
			if (btn.name){
				var $dl = append_menu($menulist);
				var $dt = append_one($dl, btn);
				var sub_button = btn.sub_button || [];
				for(var j in sub_button){
					var sub = sub_button[j];
					if(sub.name){
						var $dd = append_two($dl, sub);
					}
				}
			}
		}
	};

	var bindEvent = function(){
		var $selected, $bd = $("#bd");
		var $bd_body = $bd.find(".panel-body");
		// 排序前将数据经行缓存
		var cache = null;

		// 重置为默认样式
		var def = function(){
			$("#menu-list").removeClass("soft-enabled");
			$("#manager-opt").removeClass("soft-enabled");
			$menulist.find(".selected").removeClass("selected");

			$bd.find(".panel-heading").css("visibility","hidden");
			$bd_body.empty().append("<p class='tips' style='color: #ccc'>你可以点击左侧菜单或添加一个新菜单，然后设置菜单内容</p>");
		};

		// 添加一级菜单的操作
		var $modal_add_one = tool.getAdd(function($modal){
			var validate = function(val){
				if(/^\s*$/.test(val)){
					return "输入框内容不能为空";
				}else if(val.replace(/[^\u0000-\u00ff]/g,"**").length > 8){
					return "菜单名称应不多于4个汉字或8个字母";
				}else{
					return false;
				}
			};
			$modal.appendTo($("body"));
			$modal.find(".modal-title").text("添加一级菜单");
			$modal.find("input").keyup(function(){
				var msg, $me = $(this);
				if(msg = validate($me.val())){
					$modal.find("p.fail").text(msg).show();
				}else{
					$modal.find("p.fail").hide();
				}
			});
			$modal.find(".btn-ok").click(function(){
				var val = $modal.find("input").val();
				if(validate(val)){
					return false;
				}
				var $dl = append_menu($menulist);
				$modal.modal("hide");
				append_one($dl, {"name" : $modal.find("input").val() }).click();
			});
		});
		$("#add-one").click(function(){
			var $dt = $menulist.find("dt.menu-item");
			if($dt.length < 3){
				$modal_add_one.find("input").val("");
				$modal_add_one.find("p.msg").text("还能添加" + (3 - $dt.length) + "个一级菜单，请输入名称（4个汉字或8个字母以内）");
				$modal_add_one.modal('show');
			}else{
				alert("最多只能添加三个一级菜单，当前已达设置上限");
			}
		});

		// 添加二级菜单的操作
		var $add_to, $modal_add_two = tool.getAdd(function($modal){
			var validate = function(val){
				if(/^\s*$/.test(val)){
					return "输入框内容不能为空";
				}else if(val.replace(/[^\u0000-\u00ff]/g,"**").length > 16){
					return "菜单名称应不多于8个汉字或16个字母";
				}else{
					return false;
				}
			};
			$modal.appendTo($("body"));
			$modal.find(".modal-title").text("添加二级菜单");
			$modal.find("input").keyup(function(){
				var msg, $me = $(this);
				if(msg = validate($me.val())){
					$modal.find("p.fail").text(msg).show();
				}else{
					$modal.find("p.fail").hide();
				}
			});
			$modal.find(".btn-ok").click(function(){
				var val = $modal.find("input").val();
				if(validate(val)){
					return false;
				}
				$modal.modal("hide");
				append_two($add_to, {"name" : $modal.find("input").val() }).click();
			});
		});
		// 二级菜单确认
		var $modal_menu_two = tool.getMenuTwoModel(function($modal){
			$modal.appendTo($("body"));
			$modal.find(".btn-ok").click(function(){
				$modal.modal("hide");
				$selected.removeAttr("type").removeAttr("key").removeAttr("url").click();
				var $dd = $add_to.find("dd");
				$modal_add_two.find("p.fail").hide();
				$modal_add_two.find("p.msg").text("还能添加" + (5 - $dd.length) + "个二级菜单，请输入名称（8个汉字或16个字母以内）");
				$modal_add_two.modal("show");
			});
		});
		$menulist.delegate(".add_gray", "click",function(){
			var $dt = $(this).parent().parent();
			var $dd = ($add_to = $dt.parent()).find("dd");
			if($dt.attr("type")){
				$modal_menu_two.modal("show");
				return;
			}
			if($dd.length < 5){
				$modal_add_two.find("input").val("")
				$modal_add_two.find("p.fail").hide();
				$modal_add_two.find("p.msg").text("还能添加" + (5 - $dd.length) + "个二级菜单，请输入名称（8个汉字或16个字母以内）");
				$modal_add_two.modal("show");
			}else{
				alert("同一个一级菜单下最多只能添加五个二级菜单，当前已达设置上限");
			}
		});

		// 菜单管理排序操作
		$("#soft-opr").click(function(){
			$("#menu-list").addClass("soft-enabled");
			$("#manager-opt").addClass("soft-enabled");
			$menulist.find(".selected").removeClass("selected");
			// 将列表缓存起来
			cache = $menulist.html();
			// 启用排序
			$menulist.sortable({ axis: "y" });
			$menulist.find(".menu").sortable({ axis: "y", items: "> dd"});

			$bd.find(".panel-heading").css("visibility","hidden");
			$bd_body.empty().append("<p class='tips' style='color: #ccc'>请通过拖拽左边的菜单进行排序</p>");
		});
		// 排序完成
		$("#soft-finish").click(function(){
			def();
			$menulist.sortable("destroy");
			$menulist.find(".menu").sortable("destroy");
		});
		// 排序取消
		$("#soft-cancel").click(function(){
			def();
			$menulist.sortable("destroy");
			$menulist.find(".menu").sortable("destroy");
			// 回退到排序之前的状态
			$menulist.html(cache);
		});

		// 重命名
		var $modal_menu_rename = tool.getMenuRenameModal(function($modal){
			var validate = function(val){
				var max = ($selected.is("dt") ? 4 : 8);
				if(/^\s*$/.test(val)){
					return "输入框内容不能为空";
				}else if(val.replace(/[^\u0000-\u00ff]/g,"**").length > max * 2){
					return "菜单名称名字不多于" + max + "个汉字或" + (max*2) + "个字母";
				}else{
					return false;
				}
			};
			$modal.appendTo($("body"));
			$modal.find("input").keyup(function(){
				var msg, $me = $(this);
				if(msg = validate($me.val())){
					$modal.find("p.fail").text(msg).show();
				}else{
					$modal.find("p.fail").hide();
				}
			});
			$modal.find("a.btn-ok").click(function(){
				var val = $modal.find("input").val();
				if(validate(val)){
					return false;
				}
				$modal.modal('hide');
				$selected.attr("name", val).find("a.menu-link").text(val).click();
			});
		});
		$("#menu-rename").click(function(){
			var title = "";
			var max = ($selected.is("dt") ? 4 : 8);
			var msg = "不多于"+ max +"个汉字或" + (max*2) + "个字母";
			if($selected.is("dt")){
				title = "修改一级菜单名称";
			}else if($selected.is("dd")){
				title = "修改二级菜单名称";
			}
			$modal_menu_rename.find(".modal-title").text(title);
			$modal_menu_rename.find("input").val($selected.attr("name"));
			$modal_menu_rename.find(".modal-body p.msg").text(msg);
			$modal_menu_rename.modal("show");
		});

		// 删除
		var $modal_menu_remove = tool.getMenuRemoveModal(function($modal){
			$modal.appendTo($("body"));
			$modal.find("a.btn-ok").click(function(){
				$selected.is("dt") ? $selected.parent().remove() : $selected.remove();
				$modal.modal('hide');
				def();
			});
		});
		$("#menu-remove").click(function(){
			$modal_menu_remove.modal("show");
		});

		// 保存并发布
		$("#savePublish").click(function(){
			var menu = {button:[]}, $menus = $menulist.find("dl.menu");

			var push = function($item){
				var type, btn = {};
				btn.name = $item.attr("name");
				btn.id = $item.attr("id");
				btn.sub_button = [];
				if(type = $item.attr("type")){
					btn.type = type;
					if(type == 'click'){
						btn.key = $item.attr("key");
					}else if(type == 'view0'){
						btn.url = $item.attr("url");
					}else if(type == 'view'){
						btn.url = $item.attr("url");
					}
				}
				return btn;
			};
			for(var i = 0; i < $menus.length; i++){
				var $menu = $($menus[i]);
				var $dt = $menu.find("dt.menu-item");
				var $dds = $menu.find("dd.menu-item");
				if(!$dt.attr("type") && $dds.length == 0){
					alert("一级菜单: " + $dt.attr("name") + " 没有设置内容也没有子菜单, 无法保存");
					return false;
				}
				menu.button[i] = push($dt);
				for(var j = 0; j < $dds.length; j++){
					var $dd = $($dds[j]);
					if(!$dd.attr("type")){
						alert("二级菜单: " + $dd.attr("name") + " 没有设置内容, 无法保存");
						return false;
					}
					menu.button[i].sub_button[j] = push($dd);
				}
			}
			
			menu_data.menu = JSON.stringify(menu);
			$.ajax({ url : "WXMenuSave.do", type : "post", data : menu_data, success : function(data){
					if (data){
						if(data.status == "success"){
							def();
							fillList(getData());
							alert("保存成功");
							window.close();
						}else{
							alert("保存失败:" + data.message);
						}
					}
				}
			});
		});

		// 修改 click 类型
		var update_click = function(){
			var $div = $("<div class='content'></div>").appendTo($bd_body.empty());
			$("<p class='tips' style='padding-bottom: 10px;text-align: left;'></p>").appendTo($div).append("<span>订阅者点击该子菜单会触发单击事件，并将 KEY 值返回</span>");
			$("<div style='padding-bottom: 20px;'></div>").appendTo($div).append("<span class='lab'>KEY 值</span>").append("<div class='ibox'><input type='text'/></div>");
			$("<a id='okClick' href='javascript:void(0)' class='btn btn-success' style='width: 120px;margin-left: 175px;'>确定</a>").appendTo($div);
			$("<a id='cancel' href='javascript:void(0)' class='btn btn-default' style='width: 120px;margin-left: 0px;'>返回</a>").appendTo($div);

			$div.find('input').val($selected.attr("key"));
		}

		// 修改 view 类型
		var update_view = function(){
			var $div = $("<div class='content'></div>").appendTo($bd_body.empty());
			$("<p class='tips' style='padding-bottom: 10px;text-align: left;'></p>").appendTo($div).append("<span>订阅者点击该子菜单会跳到以下链接</span>");
			$("<div style='padding-bottom: 20px;'></div>").appendTo($div).append("<span class='lab'>页面地址</span>").append("<div class='ibox'><input type='text'/></div>");
			$("<a id='okView' href='javascript:void(0)' class='btn btn-success' style='width: 120px;margin-left: 175px;'>确定</a>").appendTo($div);
			$("<a id='cancel' href='javascript:void(0)' class='btn btn-default' style='width: 120px;margin-left: 0px;'>返回</a>").appendTo($div);

			$div.find('input').val($selected.attr("url"));
		}

		// 各个菜单的设置功能
		// 设置二级菜单
		$bd_body.delegate("#add_menu_two", "click", function(){
			var $dd = ($add_to = $selected.parent()).find("dd");
			$modal_add_two.find("p.fail").hide();
			if($dd.length < 5){
				$modal_add_two.find("p.msg").text("还能添加" + (5 - $dd.length) + "个一级菜单，请输入名称（8个汉字或16个字母以内）");
				$modal_add_two.modal("show");
			}else{
				alert("同一个一级菜单下最多只能添加五个二级菜单，当前已达设置上限");
			}
		});

		// 单击推事件
		$bd_body.delegate("#click_push", "click", update_click);
		// 显示菜单稿件
		$bd_body.delegate("#view0_url", "click", function(){
			$selected.attr({"type":"view0", "url": ""}).click();
		});
		// 跳转到网页
		$bd_body.delegate("#view_url", "click", update_view);

		// 修改按钮的事件
		$bd_body.delegate("#updateClick", "click", update_click);
		$bd_body.delegate("#updateView", "click", update_view);

		// 修改 click
		$bd_body.delegate("#okClick", "click", function(){
			var key = $bd_body.find("input").val();
			$selected.attr({"type":"click", "key": key}).click();
		});
		// 修改 view
		$bd_body.delegate("#okView", "click", function(){
			var url = $bd_body.find("input").val();
			if(url.replace(/(^\s*)|(\s*$)/g, "") == ''){
				alert("请填写页面地址！");
				return;
			}
			$selected.attr({"type":"view", "url": url}).click();
		});

		// 取消修改
		$bd_body.delegate("#cancel", "click", function(){
			$selected.click();
		});

		// 重设-确定
		$bd_body.delegate(".resetContent .btn-success", "click", function(){
			$selected.removeAttr("type").removeAttr("key").removeAttr("url").click();
		});
		// 重设-取消
		$bd_body.delegate(".resetContent .btn-default", "click", function(){
			$selected.click();
		});

		// 为各个菜单项绑定单击事件
		$menulist.delegate(".menu-item", "click", function(){
			var type, $me = $selected = $(this);

			$menulist.find(".selected").removeClass("selected");
			$me.addClass("selected");

			$bd.find(".panel-heading").css("visibility","visible");

			var name = $me.find("a").text();
			if($me.is("dt")){
				$bd.find(".tit").text("一级菜单：" + name);
			}else if($me.is("dd")){
				$bd.find(".tit").text("二级菜单：" + name);
			}

			// 如果没有设置内容
			if(!(type = $me.attr("type"))){
				$bd_body.empty();
				if($me.is("dt") && $me.siblings("dd").length >= 5){
					$bd_body.append("<p class='tips' style='color: #ccc'>你已添加满5个二级菜单</p>");
				}else if($me.is("dt") && $me.siblings("dd").length > 0){
					$bd_body.append("<p class='tips'>已为“" + name + "”添加了二级菜单，无法设置其他内容。<br>你还可以添加" + (5-$me.siblings("dd").length) + "个二级菜单</p>");
				}else{
					$bd_body.append("<p class='tips'>请设置“" + name + "”菜单的内容</p>");
				}
				if($me.is("dt") && $me.siblings("dd").length < 5){
					$bd_body.append("<a id='add_menu_two' href='javascript:void(0)'><i class='icon_menu_action add'></i><span>添加二级菜单</span></a>");
				}
				if($me.is("dd") || $me.siblings("dd").length == 0){
					$bd_body.append("<a id='click_push' href='javascript:void(0)'><i class='icon_menu_action send'></i><span>点击推事件</span></a>");
					if($me.is("dd"))
						$bd_body.append("<a id='view0_url' href='javascript:void(0)'><i class='icon_menu_action url'></i><span>显示菜单稿件</span></a>");
					$bd_body.append("<a id='view_url' href='javascript:void(0)'><i class='icon_menu_action url'></i><span>跳转到网页</span></a>");
				}
			}else{
				$bd_body.empty();
				var resetContent = "<div class=\"resetContent\"><p>重设会导致当前菜单内容被清空</p><p>确定重设？</p><div><button class=\"btn btn-success\">确定</button><button class=\"btn btn-default\">取消</button></div></div>";
				var $reset = $("<a class='reset' href='javascript:void(0)'>重设菜单内容</a>");
				var $div = $("<div class='content'></div>").appendTo($bd_body);
				if(type == 'click'){
					$("<p class='tips' style='padding-bottom: 10px;text-align: left;'></p>").appendTo($div).append("<span>订阅者点击该子菜单会触发单击事件，并将 KEY 值返回，</span>").append($reset);
					$("<div style='padding-bottom: 20px;'></div>").appendTo($div).append("<span class='lab'>KEY 值</span>")
							.append("<div class='ibox' style='background-color: #eee;'><input type='text' style='background-color: #eee;'/></div>");
					$("<a id='updateClick' href='javascript:void(0)' class='btn btn-default' style='width: 120px;margin-left: 310px;'>修改</a>").appendTo($div);
					$div.find('input').attr("disabled",true).val($me.attr("key"));
				}else if(type == 'view0'){
					var url = $me.attr("url");
					$("<p class='tips' style='padding-bottom: 10px;text-align: left;'></p>").appendTo($div).append("<span>订阅者点击该子菜单会跳到以下链接，</span>").append($reset);
					$("<div style='padding-bottom: 20px;'></div>").appendTo($div).append("<span class='lab'>页面地址</span>")
							.append("<div class='ibox' style='background-color: #eee;'><input type='text' style='background-color: #eee;'/></div>")
							.append(url ? "<a class='btn btn-success btn-sm' target='_blank' href=\""+url+"\">预览</a>" : "<span></span>");
//							$("<a id='updateView' href='javascript:void(0)' class='btn btn-default' style='width: 120px;margin-left: 0px;'>修改</a>").appendTo($div);
					$div.find('input').attr("disabled",true).val(url || "(自动设置默认地址，保存后可见)");
				}else if(type == 'view'){
					var url = $me.attr("url");
					$("<p class='tips' style='padding-bottom: 10px;text-align: left;'></p>").appendTo($div).append("<span>订阅者点击该子菜单会跳到以下链接，</span>").append($reset);
					$("<div style='padding-bottom: 20px;'></div>").appendTo($div).append("<span class='lab'>页面地址</span>")
							.append("<div class='ibox' style='background-color: #eee;'><input type='text' style='background-color: #eee;'/></div>")
							.append(url ? "<a class='btn btn-success btn-sm' target='_blank' href=\""+url+"\">预览</a>" : "<span></span>");
					$("<a id='updateView' href='javascript:void(0)' class='btn btn-default' style='width: 120px;margin-left: 310px;'>修改</a>").appendTo($div);
					$div.find('input').attr("disabled",true).val(url);
				}else{
					$("<p class='tips' style='padding-bottom: 20px;text-align: left;'></p>").appendTo($div).append("<span>使用了暂不不支持的类型,</span>").append($reset);
				}
				$bd_body.find(".reset").attr("data-content", resetContent).popover({html:true, trigger:"focus", placement:"bottom"});
			}
		});
	};

	var init = function(){
		fillList(getData());
		bindEvent();
	};

	init();
})();