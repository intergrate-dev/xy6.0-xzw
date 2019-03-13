<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
<link rel="stylesheet" href="../../../script/bootstrap-3.3.4/css/bootstrap.min.css">
<link href="../../../script/jquery-ui-bootstrap/css/custom-theme/jquery-ui-1.9.2.custom.css" rel="stylesheet" media="screen">
<link rel="stylesheet" href="../../../ueditor/dialogs/imagecrop/jquery/jcrop/css/jquery.Jcrop.css" type="text/css" />
<link rel="stylesheet" href="../../../ueditor/dialogs/image/image.css" type="text/css" />
<link rel="stylesheet" href="../../../ueditor/dialogs/imagecrop/jquery/evol.colorpicker.min.css" type="text/css" />

<!-- 上传组件 -->
<link type="text/css" rel="stylesheet" href="../../../../e5script/jquery/uploadify/uploadify.orignal.css">
<style type="text/css">
	.warpper2{ position:relative; width: 700px;}
	.cropbody{height: 570px; margin: 5px;};
	.cropbody .imgcrop{width:780px;height:496px; border: 1px solid #d7d7d7; margin: 2px;}
	.cropbody .imgcropbut{text-align: right; padding-top: 5px;  padding-bottom: 5px;}
	.cropbody .imgcropbut .cropinfo{width:190px; float: left;}
	.cropbody .imgcropbut .cropinfo label{font-weight: bold; color: #990000;}
	.cropbody .imgcropbut .pumpinfo{width: 539px; float: left;}
	.cropbody .imgcropbut .pumpinfo label{font-weight: bold; color: #990000;}
	.confirmbtn {
	    background-color: #00a0e6;
	    border: none;
	    color: #fff;
	    padding: 5px 20px;
	    border-radius: 3px;
	   
	}
	.editCancelbtn {
	    background-color: #b1b1b1;
	    border: none;
	    color: #fff;
	   	padding: 5px 20px;
	    border-radius: 3px;
	    margin-right: 7px;
	}
	body input{
		font-family: "microsoft yahei";
		color: #333;
	}
	body{overflow-x: hidden;font-family: "microsoft yahei" !important;}
	.cute{
		width: 45px;
		text-align: center;
	}
    .positiontype{
        width: 36px;
    }
    .gray {
        -webkit-filter: grayscale(100%);
        -moz-filter: grayscale(100%);
        -ms-filter: grayscale(100%);
        -o-filter: grayscale(100%);
        filter: grayscale(100%);
        filter: gray;
    }
    .evo-cp-wrap{
        display: inline-block;
        width: 100px!important;
    }
    .evo-pointer{
        float: left;
        height: 28px;
        width: 28px;
        margin: 4px;
    }
    .evo-pop {
        width: 213px;
    }
    ._0{
        /*margin:10px auto  0;*/
    }
    ._90{
        /*margin:100px auto  0;*/
        -ms-transform:rotate(90deg); /* IE 9 */
        -moz-transform:rotate(90deg); /* Firefox */
        -webkit-transform:rotate(90deg); /* Safari and Chrome */
        -o-transform:rotate(90deg); /* Opera */
    }
    ._180{
        /*margin:100px auto  0;*/
        -ms-transform:rotate(180deg); /* IE 9 */
        -moz-transform:rotate(180deg); /* Firefox */
        -webkit-transform:rotate(180deg); /* Safari and Chrome */
        -o-transform:rotate(180deg); /* Opera */
    }
    ._270{
        /*margin:100px auto  0;*/
        -ms-transform:rotate(270deg); /* IE 9 */
        -moz-transform:rotate(270deg); /* Firefox */
        -webkit-transform:rotate(270deg); /* Safari and Chrome */
        -o-transform:rotate(270deg); /* Opera */
    }
    div#rotatePreviewDiv{
        height: 500px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .rotateRadio{
        margin-left: 20px!important;
    }
    #rotatePreviewDiv {
		position: relative;
	}
    #watermarkPickerBlock{
        margin-bottom: 0px;
        height: 110px;
        margin-left: 10px;
    }
    #uploadFile, #uploadFile object{
        width: 110px !important;
        height: 110px !important;
        margin: 0;
    }
    #uploadFile-button{
        height: 109px !important;
        width: 109px !important;
        margin: 0px;
    }
    .wrapimg img{
        width: 100%;
        border: 1px solid red;
        border-radius: 3px;
    }
    #wmUl li{
        margin-bottom: 8px;
    }
    #queueLi{
        display: none !important;
    }
    #watermark{
        position: relative;
        padding-left: 16px;
        margin-buttom: 0px;
    }
    #watermark .textarea{
        width: 102%;
        margin-left: -14px;
        height: 348px;
        overflow-y: auto;
    }
    #img_wmDiv{
        margin-top:10px;
    }
    /*马赛克 start*/
    .imgmosaic{
        margin: 2px;
        min-height: 500px;
        text-align: center;
        border: 1px solid #d7d7d7;
    }
    .imgmosaic img{
        max-width: 100%;
        max-height: 480px;
    }
    .imgmosaic-vote{
        overflow: hidden;
        padding-top: 10px;
        padding-bottom: 10px;
    }
    .imgmosaic-vote .imgmosaic-left{
        overflow: hidden;
        float: left;
    }
    .imgmosaic-vote .imgmosaic-right{
        overflow: hidden;
        float: right;
    }
    .imgmosaic-left span, .imgmosaic-left label, .imgmosaic-left input{
        float: left;
    }
    .imgmosaic-left input{
        margin-left: 18px;
    }
    .imgmosaic-left label{
        margin-top: 1px;
        margin-left: 4px;
    }
    .imgmosaic-vote .imgmosaic-yes{
        float: left;
        margin-right: 20px;
        padding: 5px 20px;
        color: #fff;
        border: none;
        border-radius: 3px;
        background-color: #00a0e6;
    }
    .imgmosaic-vote .imgmosaic-no{
        float: right;
        margin-right: 40px;
        background-color: #b1b1b1;
        border: none;
        color: #fff;
        padding: 5px 20px;
        border-radius: 3px;
        margin-right: 7px;
    }
    #cvs-mosaic{
        margin-top: 10px;
    }
    /*马赛克 over*/
</style>
</head>
<body>
<div class="wrapper2">
    <div class="cropbody">
        <!-- 按钮div -->
        <div id="btnDiv">
            <input class="btn" type="button" name="tagBtn" id="modifyDivBtn" style="display: none;" value="修改" onclick="remoteImage.showModify(this);"/>
            <input class="btn" type="button" name="tagBtn" id="cropDivBtn" value="裁剪" onclick="imageCrop.showCropArea(this);"/>
            <input class="btn" type="button" name="tagBtn" value="抽大小图" onclick="imageCrop.showPumpArea(this);"/>
            <input class="btn" type="button" name="tagBtn" value="水印" onclick="waterMark.showWaterMark(this);"/>
            <input class="btn" type="button" name="tagBtn" value="转黑白图" onclick="toGray.showToGray(this);"/>
            <input class="btn" type="button" name="tagBtn" value="旋转" onclick="rotate.showRotate(this);"/>
            <input class="btn mosaic" type="button" name="tagBtn" value="马赛克"/>
        </div>
        <!-- END 按钮div -->
        <!-- 图片修改 -->
        <div id="remote" class="panel mosaicMsk">
            <div>
                <div class="row" style="display: none;">
                    <label for="url"><var id="lang_input_url"></var></label>
                    <span><input class="text" id="url" type="text"/></span>
                </div>
            </div>

            <div class="imgcrop" style="height: 420px;">
                <div id="preview"></div>
            </div>
            <div>
                <div class="row">
                    <div style="float: left; margin-top: 5px;">
                        <span>大&nbsp;小：</span>
                        <span style="font-size: 12px; color:#646464;">宽度<input class="text" type="text" id="width"/>px </span>
                        <span style="font-size: 12px; color:#646464; margin-left: 15px;">高度<input class="text" type="text" id="height"/>px </span>
                        <input id="align" name="align" type="hidden"/>
                        <span><input id="lock" type="checkbox" disabled="disabled"><span id="lockicon"></span></span>
                    </div>
                </div>
                <div class="row">
                    <div style="float: left;margin-top: 5px;">
                        <span>边&nbsp;框：</span>
                        <span><input class="text cute" type="text" id="border"/>px </span>
                        <label style="margin-left: 15px;">边&nbsp;距：</label>
                        <span><input class="text cute" type="text" id="vhSpace"/>px </span>
                    </div>
                </div>

                <div class="row">
                    <div style="float: left;margin-top: 5px;">
                        <span>描&nbsp;述：</span>
                        <span><input class="text" style="width: 350px;" type="text" id="title"/></span>
                        <span style="margin-left: 15px;">应用于其他图片：</span>
                        <span><input style="display: inline-block;" class="checkbox" type="checkbox" id="ssTitle"/></span>
                    </div>
                    <div style="float: right;margin-top: 5px;margin-right: 40px;">
                        <input class="confirmbtn" type="button" value="确定" onclick="remoteImage.btnOk()"/>&nbsp;&nbsp;&nbsp;
                        <input class="editCancelbtn" type="button" value="取消" onclick="imageCrop.cropImgCancle();"/>
                    </div>
                </div>
            </div>
        </div>
        <!-- END 图片修改 -->
        <!-- 切图 -->
        <div id="imgCrop" class="imgcrop mosaicMsk" style="display: none;">
            <input type="hidden" id="imageCropPath"/>
            <input type="hidden" id="imgTitle"/>
            <input type="hidden" id="imageW"/>
            <input type="hidden" id="imageH"/>
            <input type="hidden" id="selectorX"/>
            <input type="hidden" id="selectorY"/>
            <input type="hidden" id="selectorW" value="0"/>
            <input type="hidden" id="selectorH" value="0"/>
            <div id="imgCropBoxDiv" style="width: 98%; height: 98%;margin:5px; "></div>
        </div>
        <!-- END 切图 -->
        <!-- 抽图 -->
        <div id="imgPump" class="imgcrop mosaicMsk" style="display: none;">
            <div id="imgPumpBoxDiv" style="width: 98%; height: 98%;margin:5px; "></div>
        </div>
        <div class="imgcropbut mosaicMsk" style="display: none;">
            <div id="imgCropParam" class="cropinfo">
                <label>宽：</label><input class="cute" id="selectorWShow" type="text"/>&nbsp;&nbsp;&nbsp;&nbsp;
                <label>高：</label><input class="cute" id="selectorHShow" type="text"/>&nbsp;&nbsp;
                <a title="刷新裁剪区域" href="javascript:" onclick="imageCrop.refreshSelect();"><img style="vertical-align: middle;" border="0" width="20" height="20" alt="刷新裁剪区域" src="../../../ueditor/themes/default/images/refresh_icon.png"/></a>
            </div>
            <div id="imgPumpParam" class="pumpinfo" style="display: none;">
                原图大小：宽&nbsp;<input class="cute" id="oImagePumpW" type="text" disabled="disabled" readonly/>&nbsp;px&nbsp;&nbsp;高&nbsp;<input class="cute" id="oImagePumpH" type="text" disabled="disabled" readonly/>&nbsp;px&nbsp;&nbsp;&nbsp;
                抽图大小：<span>宽</span>&nbsp;<input class="cute" id="imagePumpW" type="text" onkeyup="imageCrop.changeImgPumpW(this);"/>&nbsp;px&nbsp;&nbsp;
                <span>高</span>&nbsp;<input class="cute" id="imagePumpH" type="text" onkeyup="imageCrop.changeImgPumpH(this);"/>&nbsp;px&nbsp;&nbsp;&nbsp;
                <select id="imgPumpZoomSize" onchange="imageCrop.changePumpImgSize(this);">
                    <option value="0.1">10%</option>
                    <option value="0.2" selected="selected">20%</option>
                    <option value="0.3">30%</option>
                    <option value="0.4">40%</option>
                    <option value="0.5" selected="selected">50%</option>
                    <option value="0.6">60%</option>
                    <option value="0.7">70%</option>
                    <option value="0.8">80%</option>
                    <option value="0.9">90%</option>
                    <option value="1">100%</option>
                </select>
            </div>
            <input class="confirmbtn" type="button" value="确定" onclick="imageCrop.cropImgOk();"/>&nbsp;&nbsp;&nbsp;
            <input class="editCancelbtn" type="button" value="取消" onclick="imageCrop.cropImgCancle();"/>
        </div>

        <!-- 水印 -->
        <div id="watermark" class="panel mosaicMsk" style="display: none;">
            <div style="margin:15px;">
            	<div class="watermark clearfix" style="display: none;">
                    <span class="left">图片列表：</span>
                    <div class="left textarea">
                        <ul id="orignalImageUl" class="filelist">
                        </ul>
                    </div>
                </div>
                <div>
                    <span>默认透明度：</span>
                    <div class="opacitybtn">
                        <div id="h-slider" class="ui-slider-range ui-widget-header"></div>
                        <input id="opacity" name="opacity" type="text" readonly="readonly" />
                    </div>
                </div>
                <div class="sliencepos">
                    <span class="left">默认定位：</span>
                    <div class="localpos">
                        <input class="positiontype" type="button" id="position_1" data-position="1" value="左上" />
                        <input class="positiontype" type="button" id="position_2" data-position="2" value="上" />
                        <input class="positiontype" type="button" id="position_3" data-position="3" value="右上" />
                        <input class="positiontype" type="button" id="position_4" data-position="4" value="左中" />
                        <input class="positiontype" type="button" id="position_5" data-position="5" value="中" />
                        <input class="positiontype" type="button" id="position_6" data-position="6" value="右中" />
                        <input class="positiontype" type="button" id="position_7" data-position="7" value="左下" />
                        <input class="positiontype" type="button" id="position_8" data-position="8" value="下" />
                        <input class="positiontype" type="button" id="position_9" data-position="9" value="右下" />
                        <input class="positiontype" type="hidden" id="position" name="position" value="" />
                    </div>
                </div>
                <div id="ssDiv" class="sliencepos">
                    <span class="left">应用于其他图片：</span>
                    <div class="localpos" style="  margin-top: -10px; border: 0px;">
                        <input id="sameSettings_0" name="sameSettings" type="radio" value="0" style="cursor: pointer;" /><label for="sameSettings_0" style="cursor: pointer;" >否</label>
                        <input id="sameSettings_1" name="sameSettings" type="radio" value="1" style="cursor: pointer;" /><label for="sameSettings_1" style="cursor: pointer;" >是</label>
                    </div>
                </div>

                <div class="sliencepos">
                    <span class="left">水印格式：</span>
                    <div class="localpos" style="  margin-top: -10px; border: 0px; width: 200px;">
                        <input id="img_waterMark" name="watermark_type" checked type="radio" value="0" style="cursor: pointer;" /><label for="img_waterMark" style="cursor: pointer;" >图片水印</label>
                        <input id="word_waterMark" name="watermark_type" type="radio" value="1" style="cursor: pointer;" /><label for="word_waterMark" style="cursor: pointer;" >文字水印</label>
                    </div>
                </div>

                <div id="word_wmDiv" class="sliencepos" style="display: none;">
                    <span class="left">文字水印：</span>
                    <div class="localpos" style="  margin-top: -10px; border: 0px; width: 660px;">
                        <div>
                            <label>水印设置：</label>
                            <select id="ww_font"></select>
                            <select id="ww_fontsize"></select>
                            <input id="ww_bold" type="checkbox" name="ww_fontstyle" value="1" /><label for="ww_bold" style="cursor: pointer;">加粗</label>
                            <input id="ww_italic" type="checkbox" name="ww_fontstyle" value="2" /><label for="ww_italic" style="cursor: pointer;">斜体</label>
                        </div>
                        <div >
                            <label>水印文字：</label><input type="text" id="ww_word" style="border: solid 1px black;" />
                            <input id="word_color" type="text" class="cute" style="visibility: hidden;"/>
                        </div>
                    </div>
                </div>

                <div id="img_wmDiv" class="watermark clearfix" style="display: none;">
                    <!-- <span class="left">水印列表：</span> -->
                    <div class="left textarea">
                        <ul id="wmUl" class="filelist">
                            <!-- 上传组件 -->
                            <li id="queueLi" style="float: left;margin-left: 10px;display:none;">
                                <!-- 显示图片 -->
                                <div class="form-inline">
                                    <div id="uploadFileQueue" class="thumbnail" style="width: 110px;height: 110px;">
                                    </div>
                                </div>
                                <!-- END 显示图片 -->
                            </li>
                            <li id="watermarkPickerBlock" style="float: left;">
                                <!-- 上传按钮 -->
                                <div style="display: inline-block;">
                                    <div id="uploadFile"></div>
                                </div>
                                <!-- END 上传按钮 -->
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="sliencepos" style="color: red;">
                    <span class="left">水印存放地址：</span>
                    <div class="" style="  margin-top: -10px; border: 0px;">
                        <div id="watermarklocation"></div>
                        <div style="margin-left: 85px;">如需添加新的水印，请找管理员！</div>
                    </div>
                </div>

            </div>
            <div style="text-align: right; margin-right: 50px;  margin-bottom: 15px;">
                <input class="confirmbtn" type="button" value="确定" onclick="waterMark.watermarkConfirm();"/>&nbsp;&nbsp;&nbsp;
                <input class="editCancelbtn" type="button" value="取消" onclick="imageCrop.cropImgCancle();"/>
            </div>
        </div>
        <!-- END 水印 -->

        <div id="grayDiv" class="mosaicMsk">
            <div class="imgcrop" id="grayContainer">
                <div id="grayPreviewDiv"></div>
            </div>
            <div style="text-align: right; margin-right: 50px;  margin-bottom: 15px;">
                <input class="confirmbtn" type="button" value="确定" onclick="toGray.toGrayConfirm();"/>&nbsp;&nbsp;&nbsp;
                <input class="editCancelbtn" type="button" value="取消" onclick="imageCrop.cropImgCancle();"/>
            </div>
        </div>

        <div id="rotateDiv" class="mosaicMsk">
            <div class="imgcrop" id="rotateContainer" style="text-align: center;">
                <div id="rotatePreviewDiv"></div>
            </div>
            <div>
                <div style="float: left;margin-top: 5px;">
                    <span>旋&nbsp;转：</span>
                    <span><input name="rotateRate" class="rotateRadio" id="r0" type="radio" value="0" /><label for="r0" style="cursor: pointer;">0度</label></span>
                    <span><input name="rotateRate" class="rotateRadio" id="r90"  type="radio" value="90" /><label for="r90" style="cursor: pointer;">90度</label></span>
                    <span><input name="rotateRate" class="rotateRadio" id="r180"  type="radio" value="180" /><label for="r180" style="cursor: pointer;">180度</label></span>
                    <span><input name="rotateRate" class="rotateRadio" id="r270"  type="radio" value="270" /><label for="r270" style="cursor: pointer;">270度</label></span>
                </div>
                <div style="float: right;margin-top: 5px;margin-right: 40px;">
                    <input class="confirmbtn" type="button" value="确定" onclick="rotate.rotateConfirm()"/>&nbsp;&nbsp;&nbsp;
                    <input class="editCancelbtn" type="button" value="取消" onclick="imageCrop.cropImgCancle();"/>
                </div>
            </div>
        </div>
        <!-- 马赛克 start-->
        <div id="mosaic">
            <div class="imgmosaic">
                <canvas id="cvs-mosaic">对不起，您的浏览器不支持Canvas，请更换浏览器</canvas>
            </div>
            <div class="imgmosaic-vote">
                <div class="imgmosaic-left">
                    <span>马赛克：</span>
                    <input id="mosaic-size1" type="radio" name="mosaic" value="10"><label for="mosaic-size1">10 &times; 10</label>
                    <input id="mosaic-size2" type="radio" name="mosaic" value="20" checked><label for="mosaic-size2">20 &times; 20</label>
                    <input id="mosaic-size3" type="radio" name="mosaic" value="30"><label for="mosaic-size3">30 &times; 30</label>
                </div>
                <div class="imgmosaic-right">
                    <input class="imgmosaic-yes" type="button" value="确定"/>
                    <input class="imgmosaic-no" type="button" value="取消"/>
                </div>
            </div>
        </div>
        <!-- 马赛克 over-->
    </div>
</div>
<script src="../../../ueditor/dialogs/imagecrop/jquery/jquery-1.8.3.js"></script>
<script src="../../../ueditor/dialogs/imagecrop/jquery/jcrop/js/jquery.Jcrop.js"></script>
<script type="text/javascript" src="../../../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../../../script/jquery-ui-bootstrap/js/jquery-ui-1.9.2.custom.min.js" charset="UTF-8"></script>
<script type="text/javascript" src="../../../ueditor/dialogs/imagecrop/jquery/evol.colorpicker.min.js"></script>
<!-- 上传组件 -->
<script type="text/javascript" src="../../../../e5script/jquery/uploadify/jquery.uploadify-3.2.min.js"></script>
<script type="text/javascript">
    var CONTEXTPATH="../../../../";
</script>
<script type="text/javascript" src="../../../ueditor/dialogs/tangram.js"></script>
<script type="text/javascript" src="../../../script/cookie.js"></script>
<script type="text/javascript" src="imageeditor.js?t=343"></script>

<script type="text/javascript">
	var remoteImage, waterMark, toGray, rotate, mosaic;
	//初始化
    function $G( id ) {
        return document.getElementById( id )
    }

	$(function(){
        oldPath=window.parent.oldPath;
        oldPath=oldPath.substr(oldPath.lastIndexOf("图片存储;"));
        _key=window.parent.oldKey;
        _upload=window.parent.oldUpload;
		/**
		 * 如果是其他地方要调用这个页面，需要定义var chosenImg;(dom对象)
		 * 以及resetImageInfo(_src)方法来接收修改后的图片地址
		 */
		if(oldPath){
			isOtherFn=true;
		}
        toGray = new ToGray();

        rotate = new Rotate();
		if(isOtherFn){
            var __notUseSameSetting = window.parent.notUseSameSetting;
            if(__notUseSameSetting){
                $("#sameSettings_0").click();
                $("#ssDiv").hide();
            }
			//可能展示的是.0小图，得到原图路径
            var src = oldPath;
			if (src.substring(src.length - 2, src.length) == ".0") {
				src = src.substring(0, src.length - 2);
			} else if (src.substring(src.length - 6, src.length) == ".0.jpg") {
				src = src.substring(0, src.length - 6);
			}
			
            parentImgList = window.parent.imgLiList;

			imageCrop.init();

			$("#cropDivBtn").click();
            toGray.init(src);
            rotate.init(src);
            mosaic = new Mosaic(src);
		}
		waterMark = new WaterMark();
	});

</script>
</body>
</html>