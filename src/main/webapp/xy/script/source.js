/**
 * 已废弃，改在xy/site/script/source.js
 * 来源管理 1.来源表单打开时，应按照src_icon(来源图标的url)显示image。该img元素已经定义在“图标“标签之后。
 * 2.点击选择按钮时，应显示出公共资源中类型为“来源图标“的所有图标。类似于系统管理端的如下选择图标界面。
 * 3.选择图标后，按图标的发布目录和文件名组织出url，赋值给src_icon字段，并在img元素里显示新图标。
 * 
 * @author guzm
 */
$(function() {
	var params = {
			res_type : 3,
			iconTableContain : ".mainBodyWrap",
			showIconImg : "#sourceIcon",
			originalIconPath : $("#src_icon").val(),
			chooseIconBtn : "#btnIcon",
			formIcon : "#src_icon",
			//useSameIcons : false,
			flag : 1
		};
	resource_icon.init(params);
	$("#src_url").addClass("validate[custom[urlVal]]");
});

