/**
 * 1.表单打开时，应按照src_icon(来源图标的url)显示image。该img元素已经定义在“图标“标签之后。
 * 2.点击选择按钮时，应显示出公共资源中类型为“来源图标“的所有图标。类似于系统管理端的如下选择图标界面。
 * 3.选择图标后，按图标的发布目录和文件名组织出url，赋值给src_icon字段，并在img元素里显示新图标。
 * 
 * @author guzm
 */
var resource_icon = {
	/**
	 *初始化
	 * @param iconTableContainTag 容器选择器 例如：".mainBodyWrap" - 用class进行选择
	 * @param showIconImgTagId 显示icon的img标签 例如： "#sourceIcon"
	 * @param originalIconPath  icon的路径，例如 "附件上传;user_img2015010101.jsp"
	 * @param chooseIconBtnTagID 选择按钮的id 例如： "#btnIcon"
	 * @param formIconID        表单中存放icon路径的标签id 例如： "#src_icon"
	 * @param flag				如果一个页面中多个地方需要用到这个方法，需要标记
	 */
	init : function(params){
		res_type = params.res_type;
		iconTableContain = params.iconTableContain;
		showIconImg = params.showIconImg;
		originalIconPath = params.originalIconPath;
		chooseIconBtn = params.chooseIconBtn;
		formIcon = params.formIcon;
		flag = params.flag;
		resource_icon.go(res_type, iconTableContain, showIconImg, originalIconPath, chooseIconBtn, formIcon, flag);
	},
		
	go  : function(res_type,iconTableContain,showIconImg, originalIconPath, chooseIconBtn,formIcon,flag) {
		//console.info(showIconImgTagId+"|"+originalIconPath+"|"+chooseIconBtnTagID+"|"+formIconID+"|"+flag);
		// 1.表单打开之后，初始化已上传的图标
		if ($.trim($(formIcon).val()) != "") {
			//$(showIconImg).attr("src", "../../xy/image.do?path=" + originalIconPath);
			$(showIconImg).attr("src", originalIconPath);
		}
		
		// 2.点击选择按钮时，应显示出公共资源中类型为“来源图标“的所有图标。类似于系统管理端的如下选择图标界面。
		// 2.点击选择按钮时，在下面显示所有图标
		// 2.1 先把所需展示的icon初始化到界面上
		resource_icon.initIcons(res_type,iconTableContain, showIconImg,formIcon,flag);
		
		// 2.2 给选择按钮添加监听
		$(chooseIconBtn).click(function() {
			$("[id^=iconTable]").hide();
			$("#iconTable"+flag).show();
		});
	},
	/**
	 * 2.2 先把所需展示的icon初始化到界面上
	 */
	initIcons : function(res_type,iconTableContain, showIconImg,formIcon,flag) {
		$.ajax({
			async: false,
			url : "../../xy/source/initIconListAjax.do",
			type : 'POST',
			data : { "res_type":res_type },
			dataType : 'json',
			success : function(data, status) {
				// 如果成功了之后，在form里面添加一个显示icon的table
				if (status == "success") {
					resource_icon.assembleHtml(data,iconTableContain, showIconImg,formIcon,flag, res_type);
					resource_icon.datas = data;
				}
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});

	},
	assembleHtml : function(data,iconTableContain, showIconImg,formIcon,flag, res_type){
		var html = new Array();
		html.push('<table id="iconTable'+flag+'" cellspacing="0" cellpadding="0" class="onlyBorder" style="margin-top:10px;display:none;"><tr>');
		if (data.length > 0) {
			for ( var x in data) {
				filename = data[x].substring(data[x].lastIndexOf("/") + 1);
				filename = filename.substring(filename.indexOf("_")+1);
				html.push('<td style="width:80px;height:80px;" align="center" >');
				html.push('<img name="icons" src="'
								//+'../../xy/image.do?path='
								+ data[x] + '" onclick="resource_icon.selIcon(\''+showIconImg+'\',\''+formIcon+'\',\''+flag+'\',\''
								+ data[x] + '\');" style="cursor:pointer;width:50px;height:50px;background-color:gray;" title="'+filename+'">');
				html.push('<br>'
//						+ data[x].substring(data[x]
//								.lastIndexOf("/") + 1)
						+ '</td>');
				if ((parseInt(x) >= 7)
						&& ((parseInt(x) + 1) % 8 == 0)) {
					html.push('</tr><tr>');
				}
			}
		} else {
			var _resource = "相应的";
			if(res_type && parseInt(res_type) == 1){
				_resource= "栏目";
			}else if(res_type && parseInt(res_type) == 3){
				_resource= "来源";
			}
			html.push("<td>对不起，公共资源中还没有"+ _resource+"图标！</td>");
		}

		html.push('</tr></table>');
		// 把table贴到form里面
		$(iconTableContain).append(html.join(""));
	},
	// 图标选择后，改变显示的图标
	selIcon : function(showIconImg,formIcon,flag, url) {
		//显示原图
		//$(showIconImg).attr("src", "../../xy/image.do?path=" + url);
		$(showIconImg).attr("src",  url);
		//表单对象赋值
		$(formIcon).val(url);
		//icontable隐藏
		$("#iconTable"+flag).hide();
	},
	datas : new Array()
}
$(function() {
//	var iconTableContainTagId=".mainBodyWrap";
//	var showIconImgTagId="#sourceIcon"
//	var originalIconPath=$("#src_icon").val();
//	var chooseIconBtnTagID ="#btnIcon";
//	var formIconID="#src_icon";
//	var flag=1;
//	icon.init(iconTableContainTagId,showIconImgTagId, originalIconPath, chooseIconBtnTagID,formIconID,flag);
});
