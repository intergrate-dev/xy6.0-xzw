//图片处理js，与edit.js同时出现
// 正在上传第几个文件
var currentNum = 0;
// 已上传图片信息汇总
var picInfoList = [];
//Flash 初始化函数
function challs_flash_update(){
	var a={};
	//定义变量为Object 类型
	a.title = "上传图片"; //设置组件头部名称
	a.FormName = "Filedata";
	//设置Form表单的文本域的Name属性
	a.url = "../../xy/pic/Upload.do;jsessionid="+ $('#sessionID').val() + "?fromPage=" + $('#fromPage').val();
	//设置服务器接收代码文件
	a.parameter = "";
	//设置提交参数，以GET形式提交,例："key=value&key=value&..."
	a.typefile = ["Images (*.gif,*.png,*.jpg,*jpeg)","*.gif;*.png;*.jpg;*.jpeg;",
				"GIF (*.gif)","*.gif;",
				"PNG (*.png)","*.png;",
				"JPEG (*.jpg,*.jpeg)","*.jpg;*.jpeg;"];
	//设置可以上传文件 数组类型
	//"Images (*.gif,*.png,*.jpg)"为用户选择要上载的文件时可以看到的描述字符串,
	//"*.gif;*.png;*.jpg"为文件扩展名列表，其中列出用户选择要上载的文件时可以看到的 Windows 文件格式，以分号相隔
	//2个为一组，可以设置多组文件类型
	a.newTypeFile = ["Images (*.gif,*.png,*.jpg,*jpeg)","*.gif;*.png;*.jpg;*.jpeg;","JPE;JPEG;JPG;GIF;PNG",
				"GIF (*.gif)","*.gif;","GIF",
				"PNG (*.png)","*.png;","PNG",
				"JPEG (*.jpg,*.jpeg)","*.jpg;*.jpeg;","JPE;JPEG;JPG"];
	//设置可以上传文件，多了一个苹果电脑文件类型过滤 数组类型, 设置了此项，typefile将无效
	//"Images (*.gif,*.png,*.jpg)"为用户选择要上载的文件时可以看到的描述字符串,
	//"*.gif;*.png;*.jpg"为文件扩展名列表，其中列出用户选择要上载的文件时可以看到的 Windows 文件格式，以分号相隔
	//"JPE;JPEG;JPG;GIF;PNG" 分号分隔的 Macintosh 文件类型列表，如下面的字符串所示："JPEG;jp2_;GI
	a.UpSize = 0;
	//可限制传输文件总容量，0或负数为不限制，单位MB
	a.fileNum = 0;
	//可限制待传文件的数量，0或负数为不限制
	a.size = 100;
	//上传单个文件限制大小，单位MB，可以填写小数类型
	a.FormID = ['siteID', 'DocLibID', 'FVID', 'UUID', 'currentNum', 'totalNum', 'filePath',
	            'picInfoList', 'topic', 'p_groupID', 'fromPage', 'p_catID', 'overall'];
	//设置每次上传时将注册了ID的表单数据以POST形式发送到服务器
	//需要设置的FORM表单中checkbox,text,textarea,radio,select项目的ID值,radio组只需要一个设置ID即可
	//参数为数组类型，注意使用此参数必须有 challs_flash_FormData() 函数支持
	a.autoClose = 1;
	//上传完成条目，将自动删除已完成的条目，值为延迟时间，以秒为单位，当值为 -1 时不会自动关闭，注意：当参数CompleteClose为false时无效
	a.CompleteClose = true;
	//设置为true时，上传完成的条目，将也可以取消删除条目，这样参数 UpSize 将失效, 默认为false
	a.repeatFile = true;
	//设置为true时，可以过滤用户已经选择的重复文件，否则可以让用户多次选择上传同一个文件，默认为false
	a.returnServer = true;
	//设置为true时，组件必须等到服务器有反馈值了才会进行下一个步骤，否则不会等待服务器返回值，直接进行下一步骤，默认为false
	a.MD5File = 1;
	//设置MD5文件签名模式，参数如下 ,注意：FLASH无法计算超过100M的文件,在无特殊需要时，请设置为0
	//0为关闭MD5计算签名
	//1为直接计算MD5签名后上传
	//2为计算签名，将签名提交服务器验证，在根据服务器反馈来执行上传或不上传
	//3为先提交文件基本信息，根据服务器反馈，执行MD5签名计算或直接上传，如果是要进行MD5计算，计算后，提交计算结果，在根据服务器反馈，来执行是否上传或不上传
	a.loadFileOrder=true;
	//选择的文件加载文件列表顺序，TRUE = 正序加载，FALSE = 倒序加载
	a.mixFileNum=0;
	//至少选择的文件数量，设置这个将限制文件列表最少正常数量（包括等待上传和已经上传）为设置的数量，才能点击上传，0为不限制
	a.ListShowType = 2;
	//文件列表显示类型：
	//1 = 传统列表显示，
	//2 = 缩略图列表显示（适用于图片专用上传）
	//3 = 单列模式
	//4 = MP3播放模式（适用于MP3专用上传）
	//5 = 极简模式
	a.TitleSwitch = true;
	//是否显示组件头部
	a.ForceFileNum = 0;
	//强制条目数量，已上传和待上传条目相加等于为设置的值（不包括上传失败的条目），否则不让上传, 0为不限制，设置限制后mixFileNum,autoClose和fileNum属性将无效！
	a.autoUpload = false;
	//设置为true时，用户选择文件后，直接开始上传，无需点击上传，默认为false;
	a.adjustOrder = true;
	//设置为true时，用户可以拖动列表，重新排列位置
	a.deleteAllShow = true
	//设置是否显示，全部清除按钮
	a.countData = true;
	//是否向服务器端提交组件文件列表统计信息，POST方式提交数据
	//access2008_box_info_max 列表总数量
	//access2008_box_info_upload 剩余数量 （包括当前上传条目）
	//access2008_box_info_over 已经上传完成数量 （不包括当前上传条目)
	a.isShowUploadButton = true;
	//是否显示上传按钮，默认为true
	a.isRotation = false;
	//是否可旋转图片
	//此项只有在缩略图模式下才有用
	//开启此项会POST一个图片角度到服务器端，由服务器端旋转图片
	//access2008_image_rotation 角度  0 到 -360 
	a.access2008_image_rotation = true;
	a.isUploadRate = true;
	//是否显示上传速率
	a.requireMpegBitrates = 0;
	//MP3播放模式下有效
	//MP3频率要求，0为不限制
	//频率与值,例子
	//128kbps = 128
	//320kbps = 320
	//低于要求将报错，不予上传
	a.mpegBitratesError = "mpegBitrates Error";
	//MP3播放模式下有效
	//a.requireMpegBitrates 不为0 时
	//报错显示文本设置
	a.isErrorStop = true;
	//遇见错误时，是否停止上传，如果为false时，忽略错误进入下一个上传
	a.isReadCookie = false;
	//是否读取cookie值，POST到服务器端
	//需要增加了challs_flash_cookies() JS函数支持
	//POST到服务器端，值名格式为 access2008_cookie_<cookie值名称>
	//比如 cookie名称为 aaa ,post 服务器 名称为 access2008_cookie_aaa
	a.isGIFAnimation = true;
	//缩略图模式下，支持GIF动画显示
	//a.ListShowType = 2 时有效
	//setHeiAndWid();
	return a ;
	//返回Object
}
//每次上传完成调用的函数，并传入一个Object类型变量，包括刚上传文件的大小，名称，上传所用时间,文件类型
function challs_flash_onComplete(a){
	var name=a.fileName; //获取上传文件名
	var size=a.fileSize; //获取上传文件大小，单位字节
	var time=a.updateTime; //获取上传所用时间 单位毫秒
	var type=a.fileType; //获取文件类型，在 Windows 上，此属性是文件扩展名。 在 Macintosh 上，此属性是由四个字符组成的文件类型
	var creationDate = a.fileCreationDate //获取文件创建时间
	var modificationDate = a.fileModificationDate //获取文件最后修改时间
	//document.getElementById('show').innerHTML+=name+' --- '+size+'字节 ----文件类型：'+type+'--- 用时 '+(time/1000)+'秒<br><br>'
}

// 每次上传一个文件获取ajax的返回字符串
function challs_flash_onCompleteData(a){
	var result = eval('(' + a + ')');
	var jsonParam = {
		"isIndex":result.isIndex,// 是否索引图
		"picPath":result.picPath,// 图片路径
		"pic":result.pic,
		"content":result.content// 图片说明
	};
	picInfoList.push(jsonParam);
}
//上传文件列表全部上传完毕事件,参数 a 数值类型，返回上传失败的数量
var flag = false; // 控制直播页面进来flash显示
function challs_flash_onCompleteAll(a){
	
	// 解决不同浏览器多次进入方法
	chroIEUploadflag = false;
	var fromPage = $('#fromPage').val();
	if (fromPage != 'live' || flag){
		setOverlay();
	}else{
		if(a=="0") setOverlay();
		flag = true;
	}
	
	var len0 = 1;
	var len = $('#ul1').children('li').length;
	// 先把原来的图画上去
	if(len > 1){
		var picInfo = picUpload_getOldPics();
		
		len0 += picInfo.length;
		
		$('#ul1').html(''); // 放在上面的话IE会有bug
		for(var a = 0; a < picInfo.length; a++){
			pic_group.drawImg(fromPage, a + 1, picInfo[a], true);
		}
	}else{
		$('#ul1').html('');
	}
	len0 += picInfoList.length;
	// 新上传的图画上去
	for(var a = 0; a < 	picInfoList.length; a++){
		pic_group.drawImg(fromPage, a + len, picInfoList[a]);
	}
	pic_group.addUploadBtn();
	
	setDragInit();
	setPicUploadDivHeight(len0, fromPage);
	
	//pic_group.addPicInfo(picInfoList);试图只附加新图，不重画旧图，但不成功，新图会压在旧图上。
	
	picInfoList = [];
//	document.documentElement.scrollTop = 190;
}

//读出已有的图片列表
function picUpload_getOldPics() {
	var li0 = $('#ul1').children('li');
	var len = li0.length;
	
	var picInfo = [];
	var div0 = '';

	for(var b = 0; b < len - 1; b++){ // 每个li的index
		for(var a = 0; a < len - 1; a++){ // 每个li
			if(b == li0[a].index){
				div0 = $(li0[a]).children("div");
				break;
			}
		}
		var isIndex = '2';
		if($(div0[0]).children("input").attr("checked")) isIndex = '0'; 
		var pic = $(div0[1]).children("img").attr("pic");
		var jsonParam = {
			"isIndexed":isIndex,// 是否索引图
			"path":$(div0[1]).children("input").val(),// 图片路径
			"pic":pic,
			"content":$(div0[2]).children("textarea").val()// 图片说明
		};
		if(pic == ''){
			jsonParam = {
				"isIndexed":isIndex,// 是否索引图
				"path":$(div0[1]).children("input").val(),// 图片路径
				"content":$(div0[2]).children("textarea").val()// 图片说明
			};
		}
		picInfo.push(jsonParam);
	}
	return picInfo;
}

//开始一个新的文件上传时事件,并传入一个Object类型变量，包括刚上传文件的大小，名称，类型
function challs_flash_onStart(a){
	var name=a.fileName; //获取上传文件名
	var size=a.fileSize; //获取上传文件大小，单位字节
	var type=a.fileType; //获取文件类型，在 Windows 上，此属性是文件扩展名。 在 Macintosh 上，此属性是由四个字符组成的文件类型
	var creationDate = a.fileCreationDate //获取文件创建时间
	var modificationDate = a.fileModificationDate //获取文件最后修改时间
	currentNum++;
	$('#currentNum').val(currentNum);
	//document.getElementById('show').innerHTML+=name+'开始上传！<br />';
	return true; //返回 false 时，组件将会停止上传
}
//当组件文件数量或状态改变时得到数量统计，参数 a 对象类型
function challs_flash_onStatistics(a){
	var uploadFile = a.uploadFile; //等待上传数量
	var overFile = a.overFile; //已经上传数量
	var errFile = a.errFile; //上传错误数量
}
//当提示时，会将提示信息传入函数，参数 a 字符串类型
function challs_flash_alert(a){
	//document.getElementById('show').innerHTML+='<font color="#ff0000">组件提示：</font>'+a+'<br />';
}
//用户选择文件完毕触发事件，参数 a 数值类型，返回等待上传文件数量
function challs_flash_onSelectFile(a){
	// 需要上传的总文件数
	$('#totalNum').val(a);
	//document.getElementById('show').innerHTML+='<font color="#ff0000">文件选择完成：</font>等待上传文件'+a+'个！<br />';
}
//清空按钮点击时，出发事件
function challs_flash_deleteAllFiles(){
	//返回 true 清空，false 不清空
	return confirm("你确定要清空列表吗?");
}

//上传文件发生错误事件，并传入一个Object类型变量，包括错误文件的大小，名称，类型
function challs_flash_onError(a){
	var err=a.textErr; //错误信息
	var name=a.fileName; //获取上传文件名
	var size=a.fileSize; //获取上传文件大小，单位字节
	var type=a.fileType; //获取文件类型，在 Windows 上，此属性是文件扩展名。 在 Macintosh 上，此属性是由四个字符组成的文件类型
	var creationDate = a.fileCreationDate //获取文件创建时间
	var modificationDate = a.fileModificationDate //获取文件最后修改时间
	//document.getElementById('show').innerHTML+='<font color="#ff0000">'+name+' - '+err+'</font><br />';
}
//使用FormID参数时必要函数
function challs_flash_FormData(a){
	try{
		var value = '';
		var id=document.getElementById(a);
		if(id.type == 'radio'){
			var name = document.getElementsByName(id.name);
			for(var i = 0;i<name.length;i++){
				if(name[i].checked){
					value = name[i].value;
				}
			}
		}else if(id.type == 'checkbox'){
			var name = document.getElementsByName(id.name);
			for(var i = 0;i<name.length;i++){
				if(name[i].checked){
					if(i>0) value+=",";
					value += name[i].value;
				}
			}
		}else if(id.type == 'select-multiple'){
		    for(var i=0;i<id.length;i++){
		        if(id.options[i].selected){
					if(i>0) value+=",";
			         values += id.options[i].value; 
			    }
		    }
		}else{
			value = id.value;
		}
		return value;
	 }catch(e){
		return '';
	 }
}
//获取cookie
function challs_flash_cookies(){
	var postParams = {};
	var i, cookieArray = document.cookie.split(';'), caLength = cookieArray.length, c, eqIndex, name, value;
	for (i = 0; i < caLength; i++) {
		c = cookieArray[i];
		// Left Trim spaces
		while (c.charAt(0) === " ") {
			c = c.substring(1, c.length);
		}
		eqIndex = c.indexOf("=");
		if (eqIndex > 0) {
			name = c.substring(0, eqIndex);
			value = c.substring(eqIndex + 1);
			postParams[name] = value;
		}
	}
	return postParams;
}

function challs_flash_style(){ //组件颜色样式设置函数
	var a = {};
	/*  整体背景颜色样式 */
	a.backgroundColor=['#f6f6f6','#f3f8fd','#dbe5f1'];	//颜色设置，3个颜色之间过度
	a.backgroundLineColor='#5576b8';					//组件外边框线颜色
	a.backgroundFontColor='#066AD1';					//组件最下面的文字颜色
	a.backgroundInsideColor='#FFFFFF';					//组件内框背景颜色
	a.backgroundInsideLineColor=['#e5edf5','#34629e'];	//组件内框线颜色，2个颜色之间过度
	a.upBackgroundColor='#ffffff';						//上翻按钮背景颜色设置
	a.upOutColor='#000000';								//上翻按钮箭头鼠标离开时颜色设置
	a.upOverColor='#FF0000';							//上翻按钮箭头鼠标移动上去颜色设置
	a.downBackgroundColor='#ffffff';					//下翻按钮背景颜色设置
	a.downOutColor='#000000';							//下翻按钮箭头鼠标离开时颜色设置
	a.downOverColor='#FF0000';							//下翻按钮箭头鼠标移动上去时颜色设置
	/*  头部颜色样式 */
	a.Top_backgroundColor=['#e0eaf4','#bcd1ea']; 		//颜色设置，数组类型，2个颜色之间过度
	a.Top_fontColor='#245891';							//头部文字颜色
	/*  按钮颜色样式 */
	a.button_overColor=['#FBDAB5','#f3840d'];			//鼠标移上去时的背景颜色，2个颜色之间过度
	a.button_overLineColor='#e77702';					//鼠标移上去时的边框颜色
	a.button_overFontColor='#ffffff';					//鼠标移上去时的文字颜色
	a.button_outColor=['#ffffff','#dde8fe']; 			//鼠标离开时的背景颜色，2个颜色之间过度
	a.button_outLineColor='#91bdef';					//鼠标离开时的边框颜色
	a.button_outFontColor='#245891';					//鼠标离开时的文字颜色
	/* 文件列表样式 */
	a.List_scrollBarColor="#000000"						//列表滚动条颜色
	a.List_backgroundColor='#EAF0F8';					//列表背景色
	a.List_fontColor='#333333';							//列表文字颜色
	a.List_LineColor='#B3CDF1';							//列表分割线颜色
	a.List_cancelOverFontColor='#ff0000';				//列表取消文字移上去时颜色
	a.List_cancelOutFontColor='#D76500';				//列表取消文字离开时颜色
	a.List_progressBarLineColor='#B3CDF1';				//进度条边框线颜色
	a.List_progressBarBackgroundColor='#D8E6F7';		//进度条背景颜色	
	a.List_progressBarColor=['#FFCC00','#FFFF00'];		//进度条进度颜色，2个颜色之间过度
	/* 错误提示框样式 */
	a.Err_backgroundColor='#C0D3EB';					//提示框背景色
	a.Err_fontColor='#245891';							//提示框文字颜色
	a.Err_shadowColor='#000000';						//提示框阴影颜色
	return a;
}
//组件文字设置函数
function challs_flash_language(){
	var fontSize = 15;
	if(window.navigator.userAgent.toLowerCase().indexOf('chrome') > -1) fontSize = 12;
	var a = {
		// $[1]$ $[2]$ $[3]$是替换符号
		// \n 是换行符号
		//按钮文字
		ButtonTxt_1:'停    止',
		ButtonTxt_2:'选择文件',
		ButtonTxt_3:'上    传',
		ButtonTxt_4:'清 空',
		//全局文字设置
		Font:'微软雅黑',
		FontSize:fontSize,
		//提示文字
		Alert_1:'初始化错误：\n\n没有找到 JAVASCRITP 函数 \n函数名为 challs_flash_update()',
		Alert_2:'初始化错误：\n\n函数 challs_flash_update() 返回类型必须是 "Object" 类型',
		Alert_3:'初始化错误：\n\n没有设置上传路径地址',
		Alert_4:'添加上传文件失败，\n\n不可以在添加更多的上传文件!',
		Alert_5:'添加上传文件失败，\n\n等待上传文件列表只能有$[1]$个，\n请先上传部分文件!',
		Alert_6:'提示信息：\n\n请再选择$[1]$个上传文件！',
		Alert_7:'提示信息：\n\n请至少再选择$[1]$个上传文件！',
		Alert_8:'\n\n请选择上传文件！',
		Alert_9:'上传错误：\n\n$[1]$',
		//界面文字
		Txt_5:'等待上传',
		//Txt_6:'等待上传：$[1]$个  已上传：$[2]$个',
		Txt_6:'',
		Txt_7:'字节',
		Txt_8:'总量限制（$[1]$MB）,上传失败',
		Txt_9:'文件超过$[1]$MB,上传失败',
		Txt_10:'秒',
		Txt_11:'保存数据中...',
		Txt_12:'上传完毕',
		Txt_13:'文件加载错误',
		Txt_14:'扫描文件...',
		Txt_15:'验证文件...',
		Txt_16:'取消',
		Txt_17:'无图',
		Txt_18:'加载中',

		Txt_20:'关闭',
		Txt_21:'确定',
		Txt_22:'上传文件',
		//错误提示
		Err_1:'上传地址URL无效',
		Err_2:'服务器报错：$[1]$',
		Err_3:'上传失败,$[1]$',
		Err_4:'服务器提交效验错误',
		Err_5:'效验数据无效错误'
	};
	return a;
}

$(function() {
	// 图片库中的编辑操作：若图片的对应稿件ID>0，则提示“图片已经用于稿件，请勿在此修改”
	if($('#p_articleID').val() > 0){
		alert("图片已经用于稿件，请勿在此修改！");
		location.href = "../../e5workspace/after.do?UUID=" + $('#UUID').val();
	}
	// 上传取消按钮
	$('#cancel').click(function(){
		var aLilength = 0;
		var oUl= document.getElementById("ul1");
		if (oUl) {
			var aLi = oUl.getElementsByTagName("li");
			aLilength = aLi.length;
		}
		
		if($('#fromPage').val() == 'pic' && aLilength < 2){
			location.href = "../../e5workspace/after.do?UUID=" + $('#UUID').val();
		}else{
			// 解决不同浏览器多次进入方法
			chroIEUploadflag = false;
			// 去掉上传flash层
			setOverlay();
			$("#flash").html('');
			picInfoList = [];
		}
	});
	
	$('li.channelTab').click(function(evt){
		var id = $(evt.target)[0].id;
		$('#' + id).addClass("select");
		if(id.indexOf('Upload') > -1){
			$('#' + id.replace('Upload', 'Online')).removeClass("select");
			$('#online').css('display', 'none');
			$('#flash').css('display', 'block');
		}else{
			$('#' + id.replace('Online', 'Upload')).removeClass("select");
			$('#flash').css('display', 'none');
			$('#online').css('display', 'block');
		}
		$('#' + id + '_').css('display', 'block');
	});
});