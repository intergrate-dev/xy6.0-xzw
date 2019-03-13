<!--
<%@page pageEncoding="UTF-8" %>
-->
<!doctype html>
<html>
<head>
    <meta charset="UTF-8"/>
    <!--<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">-->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>LayoutEditor</title>
    <link rel="shortcut icon" href="export/img/ninja.png">
    <script data-pace-options='{"restartOnPushState": false}' src="third/pace-0.5.6/pace.min.js"></script>
    <link href="third/pace-0.5.6/themes/pace-theme-loading-bar-customer.css" rel="stylesheet"/>


    <!--<link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/assets/css/bootstrap.min.css">-->
    <link rel="stylesheet" href="third/bootstrap-3.3.5-dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="third/bootstrap-3.3.5-dist/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/css/custom-theme/jquery-ui-1.10.3.custom.css">
    <link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/assets/css/font-awesome.min.css">
    <!--[if IE 7]>
    <link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/assets/css/font-awesome-ie7.min.css">
    <![endif]-->
    <!--[if lt IE 9]>
    <link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/css/custom-theme/jquery.ui.1.10.3.ie.css">
    <![endif]-->
    <!--<link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/assets/css/docs.css">-->
    <link rel="stylesheet" href="third/jquery-ui-bootstrap-1.0/assets/js/google-code-prettify/prettify.css">
    <!-- 颜色选择器的样式 -->
    <link href="third/bgrins-spectrum/spectrum.css" rel="stylesheet">
    <link href="third/bgrins-spectrum/spectrum_custom.css" rel="stylesheet">
    <link rel="stylesheet" href="./third/bootstrapvalidator/dist/css/bootstrapValidator.min.css"/>

    <link rel="stylesheet" href="./third/perfect-scrollbar/perfect-scrollbar.min.css"/>
    <script type="text/javascript" src="js/editor_style.js"></script>

    <!--<link rel="stylesheet" type="text/css" href="export/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="export/css/header.css"/>
    <link rel="stylesheet" type="text/css" href="export/css/sliderBar.css"/>
    <link rel="stylesheet" type="text/css" href="export/css/editor.css"/>
    <link rel="stylesheet" type="text/css" href="export/css/sidebar-panel.css"/>
    <link id="modelLink" rel="stylesheet" type="text/css" href="export/css/model.css"/>
    <link rel="stylesheet" type="text/css" href="export/css/navMenuSet.css"/>-->

    <style type="text/css" id="special_style"></style>
</head>
<body style="overflow: hidden;">
<!--头部开始-->

<!--头部结束-->


<!--左侧功能栏开始-->
<div class="pageScale pageScaleHeg Scaleline">
    <img class="mgr10 mgt6 scaleLeft right iconAreaShow" title="收起容器" src="export/images/sliderBar/pluginClosedNew.png"/>
    <img class="pull-right mgr4 mgt6 iconAreaShow hide1" title="展开容器" src="export/images/sliderBar/pluginOpenNew.png">
</div>
<div class="ScaleBtmLine"></div>
<div id="sliderBar" class="left toolbar setBarHeightSlide">
    <!--<section class="page">
        <div class="page-title pageColor pageHeg bline">
            <span class="mgl10  text-left ">页面</span>
            <img class="mgr10 right iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="pull-right  mgt5 mgr10 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
        </div>
        <ul class="page-content pageColor bline hideArea">
            <li class="pageHeg">
                <img class="mgl5 fmgr10 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <img class="mgr5" src="export/images/sliderBar/sliderBar2.png"/>
                <span>首页</span>
            </li>
            <li class="pageHeg">
                <img class="mgl25" src="export/images/sliderBar/sliderBar3.png"/>
                <span>标题</span>
            </li>
            <li class="pageHeg">
                <img class="mgl25" src="export/images/sliderBar/sliderBar3.png"/>
                <span>内容图片</span>
            </li>
        </ul>
        <div class="page-bottom pageHeg pageColor">
            <div class="right">
                <img class="mgr10" src="export/images/sliderBar/sliderBar4.png"/>
                <img class="mgr10" src="export/images/sliderBar/sliderBar5.png"/>
                <img class="mgr30" src="export/images/sliderBar/sliderBar6.png"/>
            </div>
        </div>
    </section>-->
    <!--<div class="line"></div>-->
    <section class="assembly">


        <div class="page-title pageColor pageHeg bline">
            <span class="mgl10  text-left">组件</span>
            <!--<img class="mgr10 right iconArea" src="export/images/sliderBar/sliderBar1.png"/>-->
            <img class="pull-right mgt5 mgr10 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
        </div>
        <div class="layout pageColor clearfix">
            <div class="layout-title mgt10">
                <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <span class="iconArea cursor">布局</span>
            </div>
            <ul class="layout-content">
                <li id="columnLi" class="left"><img src="export/images/sliderBar/sliderBar7.png"/><span>容器</span></li>
                <li id="columnboxLi" class="left"><img src="export/images/sliderBar/sliderBar8.png"/><span>分栏</span>
                </li>
                <li id="navigationLi" class="left"><img src="export/images/sliderBar/sliderBar9.png"/><span>导航菜单</span>
                </li>
                <li id="tabsLi" class="left"><img src="export/images/sliderBar/sliderBar10.png"/><span>标签页</span></li>
                <li id="frameLi" class="left"><img src="export/images/sliderBar/sliderBar11.png"/><span>框架</span></li>
            </ul>
        </div>
        <div class="layout pageColor clearfix">
            <div class="layout-title mgt10">
                <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="pull-left  mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <span class="iconArea cursor">公共</span>
            </div>
            <ul class="layout-content">
                <!--<li class="left"><img src="export/images/sliderBar/sliderBar12.png"/><span>标题</span></li>-->
                <li id="textLi" class="left"><img src="export/images/sliderBar/sliderBar13.png"/><span>文字</span></li>
                <li id="listGroupLi" class="left"><img src="export/images/sliderBar/sliderBar14.png"/><span>列表</span></li>
                <li id="mediaLi" class="left"><img src="export/images/sliderBar/sliderBar16.png"/><span>图文列表</span></li>
                <li id="imageLi" class="left"><img src="export/images/sliderBar/sliderBar15.png"/><span>图片</span></li>
                <li id="carouselLi" class="left"><img src="export/images/sliderBar/sliderBar17.png"/><span>图片轮播</span></li>
                <li id="galleryLi" class="left"><img src="export/images/sliderBar/sliderBar18.png"/><span>多图</span></li>
                <li id="videoLi" class="left"><img src="export/images/sliderBar/sliderBar19.png"/><span>视频</span></li>
                <li id="audioLi" class="left"><img src="export/images/sliderBar/sliderBar20.png"/><span>音频</span></li>
                <li id="flashLi" class="left"><img src="export/images/sliderBar/sliderBar21.png"/><span>Flash</span></li>
                <li id="hrLi" class="left"><img src="export/images/sliderBar/sliderBar22.png"/><span>分割线</span></li>
                <li id="CodeLi" class="left"><img src="export/images/sliderBar/sliderBar37.png"/><span>Html</span></li>

            </ul>
        </div>
        <!--<div class="layout pageColor clearfix">
            <div class="layout-title mgt10">
                <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <span>表单</span>
            </div>
            <ul class="layout-content">
                <li class="left"><img src="export/images/sliderBar/sliderBar23.png"/><span>文本框单行</span></li>
                <li class="left"><img src="export/images/sliderBar/sliderBar24.png"/><span>文本框多行</span></li>
                <li class="left"><img src="export/images/sliderBar/sliderBar25.png"/><span>下拉菜单</span></li>
                <li class="left"><img src="export/images/sliderBar/sliderBar26.png"/><span>复选框</span></li>
                <li class="left"><img src="export/images/sliderBar/sliderBar27.png"/><span>单选框</span></li>
                <li class="left"><img src="export/images/sliderBar/sliderBar28.png"/><span>按钮</span></li>
            </ul>
        </div>-->
        <div class="layout pageColor clearfix">
            <div class="layout-title mgt10">
                <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <span>第三方</span>
            </div>
            <ul class="layout-content">
                <!--<li class="left"><img src="export/images/sliderBar/sliderBar29.png"/><span>地图</span></li>-->
                <li id="shareLi" class="left"><img src="export/images/sliderBar/sliderBar30.png"/><span>分享</span></li>
                <li id="lineChartLi" class="left"><img src="export/images/sliderBar/sliderBar40.png"/><span>折线图</span></li>
                <li id="lineChartLiBar" class="left"><img src="export/images/sliderBar/sliderBar41.png"/><span>柱形图</span></li>
                <li id="lineChartLiPie" class="left"><img src="export/images/sliderBar/sliderBar42.png"/><span>饼图</span></li>
                <li id="lineChartLiWordCloud" class="left"><img src="export/images/sliderBar/sliderBar43.png"/><span>标签云</span></li>
                <li id="lineChartLiMap" class="left"><img src="export/images/sliderBar/sliderBar44.png"/><span>地图</span></li>
                <!--<li  class="left"><img src="export/images/sliderBar/sliderBar31.png"/><span>地图</span></li>-->
            </ul>
        </div>
    </section>
</div>
<!--左侧功能栏结束-->

<!--中间功能导航开始-->
<div id="mainDiv" class=" mgl210 mgr240 posRelative setBarHeightContain" style=" width: 1024px;height:700px;overflow: auto;position: relative;padding: 0 10px 0 10px;overflow-x: hidden!important;">
    <div id="container" data-w="100%" class="demo container" style="overflow-x: hidden; width: 100%; margin: 0 auto 0 auto; padding-bottom: 150px;">

    </div>
    <div id="autoSaveDiv" class="autoSave posFixed" style="display: none;">
        <div class="autoSave-info pull-right mgr20 ">
            <img class="mgr5 fmgt2" src="export/images/column/autoSave.png" alt=""/>
            <span>自动保存成功</span>
        </div>
    </div>
    <div id="savingDiv" class="autoSave posFixed" style="display: none;">
        <div class="autoSave-info pull-right mgr20 ">
            <img class="mgr5 fmgt2" src="export/images/column/saveLoading.png" alt=""/>
            <span style="color: #b7bbbe;">正在保存中</span>
        </div>
    </div>

</div>

<!--中间功能导航结束-->
<!--剪切板-->
    <div id="clipBoard" data-show="false" class="clipboard-box hide1">
        <div class="clipboard-title">
            <span>剪切板</span>
            <img id="clipboard-hide" src="export/images/sliderBar/sliderBar38.png">
        </div>
        <div id="clipboard-cup">
            <div class="plugin ui-draggable" data-type="" style="display: block;">
                <div class="preview">

                </div>
                <div class="view"></div>
            </div>
            <div class="plugin ui-draggable" data-type="" style="display: block;">
                <div class="preview">

                </div>
                <div class="view"></div>
            </div>
            <div class="plugin ui-draggable" data-type="" style="display: block;">
                <div class="preview">

                </div>
                <div class="view"></div>
            </div>

        </div>
    </div>

<!--撤销恢复预览保存退出-->
<div class="functional-menu">
    <ul>
        <li id="undoBtn" class="left Undo cursor" title="撤销">
            <div></div>
        </li>
        <li id="redoBtn" class="left Redo cursor" title="恢复">
            <div></div>
        </li>
        <li id="previewLi" class="left cursor" title="预览"></li>
        <li id="saveButton" class="left cursor" title="保存"></li>
        <li class="left Unre-leftline"></li>
        <li class="left Unre-rightline"></li>
        <li id="existBtn" class="left cursor" title="退出"></li>
    </ul>

</div>

<!--右侧功能栏开始-->
<div id="sidebar-panel" class="setBarHeight sidebar">
    <!--<ul class="tab_menu">
        <li class="left selected">样式</li>
        <li class="left">属性</li>
    </ul>-->
    <div class="tab_box">
        <div>

            <!--控制栏数开始-->
            <section id="columnboxSection" class="bline padb10">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png">
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">分栏设置</span>
                </div>
                <div class="lebody">
                    <ul id="cbOptionUl" class="clearfix">
                        <li class="pull-left btn-hint mgl15 mgr5" data-hint="6:6">
                            <img src="export/images/column/column.png"/>
                        </li>
                        <li class="pull-left btn-hint mgl5 mgr5" data-hint="3:9">
                            <img src="export/images/column/column1.png"/>
                        </li>
                        <li class="pull-left btn-hint mgl5 mgr5" data-hint="9:3">
                            <img src="export/images/column/column2.png"/>
                        </li>
                        <li class="pull-left btn-hint mgl5 mgr5" data-hint="4:4:4">
                            <img src="export/images/column/column3.png"/>
                        </li>
                        <li class="pull-left btn-hint mgl15 mgr5 mgt5" data-hint="3:6:3">
                            <img src="export/images/column/column4.png"/></li>
                        <li class="pull-left btn-hint mgl5 mgr5 mgt5" data-hint="6:3:3">
                            <img src="export/images/column/column5.png"/></li>
                        <li class="pull-left btn-hint mgl5 mgr5 mgt5" data-hint="3:3:3:3">
                            <img src="export/images/column/column6.png"/></li>
                        <li class="pull-left btn-hint mgl5 mgr5 mgt5" data-hint="2:4:4:2">
                            <img src="export/images/column/column7.png"/></li>
                        <li class="pull-left btn-hint mgl15 mgr5 mgt5" data-hint="3:3:2:2:2">
                            <img src="export/images/column/column8.png"/></li>
                        <li class="pull-left btn-hint mgl5 mgr5 mgt5" data-hint="2:2:2:2:2:2">
                            <img src="export/images/column/column9.png"/></li>

                    </ul>
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr5 mgt5">栏数</li>
                        <li class="pull-left">
                            <input id="cbColumnSize" class="inputStyle35 pad5 selectHeight" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl10 mgr5 mgt5">栏间距</li>
                        <li class="pull-left">
                            <input id="cbColumnMargin" class="inputStyle35 selectHeight pad5" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl10  mgt5">等分</li>
                        <li id="cbAvgBtn" class="pull-left column-equal" title="等分">
                            <img src="export/images/sliderPanel/textAlign11.png"/></li>
                    </ul>

                </div>

            </section>
            <!--控制栏数结束-->

            <!--标签管理开始-->
            <section id="tabManageSection" class="bline padb5">
                <div class="sidebar-panel-title padd10 clearfix">
                    <img class="pull-left fmgr5 mgl5 iconArea fmgt5" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="pull-left  mgl5 fmgr5 iconArea hide1 fmgt5" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">标签管理</span>
                    <!--<span class="pull-right blue mgr15 cursor">清除</span>-->
                </div>
                <div class="lebody">
                    <ul id="tmTabs">
                        <li class="setHeight btn-hint padl20 select">
                            <span class="span160">Home1</span>
	        				<span class="pull-right mgr15">
	        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
	        					<span class="glyphicon glyphicon-trash cursor"></span>
	        				</span>
                        </li>
                        <li class="padl20 setHeight btn-hint">
                            <span class="span160">Profile2</span>
							<span class="pull-right mgr15">
	        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
	        					<span class="glyphicon glyphicon-trash cursor"></span>
	        				</span>
                        </li>
                        <li class="padl20 setHeight btn-hint">
                            <span class="span160">Message3</span>
	        				<span class="pull-right mgr15">
	        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
	        					<span class="glyphicon glyphicon-trash cursor"></span>
	        				</span>
                        </li>
                    </ul>
                    <button id="addTabBtn" style="width: 205px;" class="mgl20 factive mgt15 mgb10">添加标签</button>
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl20 cursor">显示样式</span>
                        <!--<span class="pull-right blue mgr15 cursor" id="modelMore">更多</span>-->
                    </div>
                    <div id="modelPreview" style="overflow: hidden;padding-left:20px">
                        <!-- <ul data-ref="tabmanage_style1" class="clearfix mgt10 displayStyle">
                             <li class="pull-left menu-btn clo2d mgl20 cursor bgf btn-act act">首页</li>
                             <li class="pull-left menu-btn clo2d cursor bgf btn-act">新闻</li>
                             <li class="pull-left menu-btn clo2d cursor bgf btn-act">体育</li>
                         </ul>

                         <ul data-ref="tabmanage_style2" class="clearfix mgt15 displayStyle">
                             <li class="pull-left menu-btn mgl20 cursor btn-cur activ">首页</li>
                             <li class="pull-left menu-btn cursor btn-cur">新闻</li>
                             <li class="pull-left menu-btn cursor btn-cur">体育</li>
                         </ul>-->

                        <ul data-ref="tabmanage_style3" class="clearfix mgt10 displayStyle">
                            <li class="pull-left menu-btn clo555 cursor bgf bordtom">标签1</li>
                            <li class="pull-left menu-btn clo69d6 cursor bgf">标签2</li>
                            <li class="pull-left menu-btn clo69d6 cursor bgf">标签3</li>
                        </ul>

                    </div>
                    <button id="editTabBtn" class="mgl20 editList mgt10 mgb10">编辑标签样式</button>
                </div>

            </section>
            <!--标签管理结束-->

            <!--标签开始-->
    <!--<section id="tabSettingSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="pull-left  mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">标签</span>
                </div>
                <div class="lebody">
                    <ul class="clearfix">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left">
                            <input data-ref="width" class="inputStyle inputStyle55 pad5 unitOption selectHeight numInput tabWH" data-unit="px" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input data-ref="height" class="inputStyle inputStyle55 selectHeight pad5 unitOption numInput tabWH" data-unit="px" type="text" value="960px"/>
                        </li>
                    </ul>-->
                    <!--<div class="sidebar-panel-color padd10 mgt10">
                        <span class="mgl10 mgr10">菜单宽度</span>
                        <input  class="inputStyle inputStyle80 pad5" type="text" value="960像素"/>
                    </div>
                    <div class="sidebar-panel-color padd10">
                        <span class="mgl10 mgr10">菜单边距</span>
                        <input class="inputStyle inputStyle80 pad5" type="text" value="960像素"/>
                    </div>-->
    <!--</div>

            </section>-->

            <!--标签结束-->
            <!--交互样式开始-->
            <section id="activeSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">交互样式</span>
                    <!--<span class="pull-right blue mgr15 cursor mgt3">更多</span>-->
                </div>
                <div class="lebody">
                    <ul class="clearfix text-center" id="tabUl">
                        <li id="tabNormal" class="pull-left interactiveStyles  activt mgl15">普通</li>
                        <li id="tabHover" class="pull-left interactiveStyles ">悬停</li>
                        <li id="tabClick" class="pull-left interactiveStyles ">点击</li>
                    </ul>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
                        <span class="left mgl15 mgr10">文字颜色</span>
                        <!--<input class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                        <span class="left setColor mgl5"></span>-->
                        <input data-0="#0069D6" data-1="#0069D6" data-2="#555" id="tabLitxColor" class="left inputStyle inputStyle80 pad5" type="text" value="#0069D6"/>
                        <span><input id="tabLitxColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>

                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
                        <span class="left mgl15 mgr10">背景颜色</span>
                        <!--<input class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                        <span class="left setColor mgl5"></span>-->

                        <input data-0="#ffffff" data-1="#f5f5f5" data-2="#ffffff" id="tabLibgColor" class="left inputStyle inputStyle80 pad5" type="text" value="#ffffff"/>
                        <span><input id="tabLibgColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景图</span>
                        <label id="tabBgFileLabel" class="posRelative">
                            <img id="tabBgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                            <img id="tabBgDeleteImg" style="display:none" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                        </label>
                        <!-- <img src="export/images/sliderPanel/sliderPanel1.png"/>-->
                    </div>
                </div>

            </section>
            <!--交互样式结束-->


            <!--设置宽度开始-->
            <section id="widthSection" class="sidebar-panel-color padd10 bline">
                <span class="mgl15 mgr10">页面宽度</span>
                <input id="containerWidth" data-unit="px" data-unitex="e" data-dv="1024px" data-ref="width" class="inputStyle inputStyle80 pad5 unitOption" type="text" value="1024px"/>
            </section>
            <!--设置宽度结束-->

            <!--代码开始-->
            <section id="codeSection" class="bline padb5 form-group">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">代码</span>
                </div>
                <!--id="addPic"-->
                <!--data-toggle="modal" data-target="#myModal"-->
                <div class="lebody">
                    <button id="addCode" class="mgl20 factive mgt10 mgb10 width205">编辑代码</button>
                </div>
            </section>
            <!--代码结束-->

            <!--图片设置开始-->
            <section id="picSection" class="bline padb5 form-group">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">图片设置</span>
                </div>
                <!--id="addPic"-->
                <!--data-toggle="modal" data-target="#myModal"-->
                <div class="lebody">
                    <button id="addPic" class="mgl20 factive mgt10 mgb10 width205">添加图片</button>
                    <button id="resetPic" class="mgl20 factive mgt10 mgb10 width205">重新选择</button>
                    <button id="editPic" class="mgl20 factive mgt10 mgb10 width205">编辑图片</button>
                    <button id="toLocalPic" class="mgl20 factive mgt10 mgb10 width205">设为本地图片</button>
                    <ul class="piclinkbox">
                        <li class="mgl20 mgt10">设置链接</li>
                        <button id="linkPic" class="mgl20 factive mgt10 mgb10 width205">添加超链接</button>
                        <!--<li>
                        <input id="picLinkInput" name="picLinkInput" class="mgl20 factive mgt10 mgb10 width205 height25 pad5" type="text" placeholder="http://www.founder.com.cn"/>
                        </li>-->
                    </ul>
                </div>
                <div>
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <!--<span class="unit1 posAbsolute"></span>-->
                            <input id="pWidth" class="inputStyle inputStyle55 pad5 selectHeight unitOption picWH" data-unitex="e" data-direct="width" data-ref="width" type="text" value="100%"/>
                        </li>
                        <li class="pull-left mgl15 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input id="pHeight" class="inputStyle inputStyle55 selectHeight unitOption pad5 picWH" data-unitex="e" data-direct="height" data-ref="height" type="text" value="300px"/>
                        </li>
                        <li class="pull-left mgl20 cursor pic-ratio" title="自由比例">
                            <!--<img class="pull-right pic-ratio ratioOn" title="自由比例" src="export/images/sliderBar/picRatio.png"/>-->
                        </li>
                    </ul>
                </div>

            </section>
            <!--图片设置结束-->
            <!--视频设置开始-->
            <section id="videoSection" class="bline padb5 form-group">
                <div class="lebody">
                    <div class="sidebar-panel-title">
                        <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                        <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                        <span class="iconArea cursor">视频设置</span>
                    </div>
                    <button id="addVideo" class="mgl20 factive mgt10 mgb10 width205">添加视频</button>
                    <button id="resetVideo" data-url="" class="mgl20 factive mgt10 mgb10 width205">修改视频</button>
                    <button id="addVideoLib" class="mgl20 factive mgt10 mgb10 width205">视频库</button>

                    <!--<ul class="clearfix mgt20">
                     <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                     <li class="pull-left posRelative">
                         <input id="vWidth" class="inputStyle inputStyle78 pad5 selectHeight unitOption" data-direct="width" data-ref="width" type="text" value="960"/>
                     </li>
                     <li class="pull-left mgl25 mgr10 mgt5">高</li>
                     <li class="pull-left">
                         <input id="vHeight" class="inputStyle inputStyle78 selectHeight unitOption pad5" data-direct="height" data-ref="height" type="text" value="960"/>
                     </li>
                 </ul>-->
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <!--<span class="unit1 posAbsolute"></span>-->
                            <input id="vWidth" class="inputStyle inputStyle55 pad5 selectHeight unitOption" data-direct="width" data-ref="width" type="text" value="100%"/>

                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input id="vHeight" class="inputStyle inputStyle55 selectHeight unitOption pad5" data-direct="height" data-ref="height" type="text" value="300px"/>
                        </li>
                    </ul>
                </div>

            </section>
            <!--视频设置结束-->
            <!--音频设置开始-->
            <section id="audioSection" class="bline padb5 form-group">
                <div class="sidebar-panel-title">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">音频设置</span>
                </div>
                <div class="lebody">
                    <button id="addAudio" class="mgl20 factive mgt10 mgb10 width205">添加音频</button>
                    <button id="resetAudio" data-url="" class="mgl20 factive mgt10 mgb10 width205">修改音频</button>
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <input id="aWidth" class="inputStyle inputStyle55 pad5 selectHeight unitOption" data-direct="width" data-ref="width" type="text" value="100%"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input id="aHeight" class="inputStyle inputStyle55 selectHeight unitOption pad5" data-direct="height" data-ref="height" type="text" value="300px"/>
                        </li>
                    </ul>
                </div>

            </section>
            <!--音频设置结束-->
            <!--Flash设置开始-->
            <section id="flashSection" class="bline padb5 form-group">
                <div class="sidebar-panel-title">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">Flash设置</span>
                </div>
                <div class="lebody">
                    <button id="addFlash" class="mgl20 factive mgt10 mgb10 width205">添加Flash</button>
                    <button id="resetFlash" data-url="" class="mgl20 factive mgt10 mgb10 width205">修改Flash</button>
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <input id="fWidth" class="inputStyle inputStyle55 pad5 selectHeight unitOption" data-direct="width" data-ref="width" type="text" value="100%"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input id="fHeight" class="inputStyle inputStyle55 selectHeight unitOption pad5" data-direct="height" data-ref="height" type="text" value="300px"/>
                        </li>
                    </ul>
                </div>

            </section>
            <!--Flash设置结束-->

            <!--导航菜单管理添加-->
            <section id="navManageSection" class="bline padb5">
                <div class="sidebar-panel-title padd10 clearfix">
                    <img class="fmgr5 mgl5 iconArea fmgt5" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class=" mgl5 fmgr5 iconArea hide1 fmgt5" src="export/images/sliderBar/sliderBar33.png">
                    <span class=" iconArea cursor">导航菜单管理</span>
                    <span id="nmReselect" class="pull-right blue mgr15 cursor">重新设置</span>
                    <span id="nmAppend" class="pull-right blue mgr15 cursor">追加</span>
                </div>
                <div class="lebody">

                    <ul id="nmTabs">
                        <li class="setHeight btn-hint padl20 select">
                            <img class="mgl5 flag" src="export/images/sliderBar/sliderBar34.png" alt=""/>
                            <span>首页</span>
	        				<span class="pull-right mgr15">
	        					<span class="glyphicon glyphicon-plus mgr10"></span>
	        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
	        					<span class="glyphicon glyphicon-trash"></span>
	        				</span>
                        </li>
                        <li class="padl20 setHeight btn-hint">
                            <img class="mgl5 flag" src="export/images/sliderBar/sliderBar34.png" alt=""/>
                            <span>新闻</span>
	                        <span class="pull-right mgr15">
	        					<span class="glyphicon glyphicon-plus mgr10"></span>
	        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
	        					<span class="glyphicon glyphicon-trash"></span>
	        				</span>
                            <ul>
                                <li class="padl20 setHeight">
                                    <img class="mgr15 flag" style="margin-left: -15px;" src="export/images/sliderBar/sliderBar34.png" alt=""/>
                                    <span>时政要闻</span>
			                        <span class="pull-right mgr15">
			        					<span class="glyphicon glyphicon-plus mgr10"></span>
			        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
			        					<span class="glyphicon glyphicon-trash"></span>
			        				</span>
                                </li>
                                <li class="padl20 setHeight">
                                    <img class="mgr15 flag" style="margin-left: -15px;" src="export/images/sliderBar/sliderBar34.png" alt=""/>
                                    <span>热点新闻</span>
                                </li>
                            </ul>
                        </li>
                        <li class="padl20 setHeight btn-hint">
                            <img class="mgl5 flag" src="export/images/sliderBar/sliderBar34.png" alt=""/>
                            <span>体育</span>
	                    	<span class="pull-right mgr15">
	        					<span class="glyphicon glyphicon-plus mgr10"></span>
	        					<span class="glyphicon glyphicon-pencil mgr10 cursor"></span>
	        					<span class="glyphicon glyphicon-trash"></span>
	        				</span>
                        </li>
                    </ul>
                    <button id="addnmTabBtn" style="width: 205px;" class="mgl20 factive mgt15 mgb10">添加主导航</button>
                    <div id="mainModelMoreBox" style="display:none;">
                        <div class="sidebar-panel-title padd10 clearfix">
                            <span class="pull-left mgl20 cursor">主导航显示样式</span>
                            <!--<span class="pull-right blue mgr15 cursor" id="mainModelMore">编辑</span>-->
                        </div>
                        <div id="mainModelPreview" style="overflow: hidden;">
                            <ul data-ref="tabmanage_style3" class="clearfix">
                                <li class="pull-left menu-btn clo555 cursor bgf">首页</li>
                                <li class="pull-left menu-btn clo69d6 cursor bgf">新闻</li>
                                <li class="pull-left menu-btn clo69d6 cursor bgf">体育</li>
                            </ul>
                        </div>
                        <button id="ediMainNavBtn" class="mgl20 editList mgt10 mgb10">编辑主导航样式</button>
                    </div>
                    <!--<ul class="clearfix">
                        <li class="pull-left menu-btn clo2d mgl20 cursor bgf btn-act act">首页</li>
                        <li class="pull-left menu-btn clo2d cursor bgf btn-act">新闻</li>
                        <li class="pull-left menu-btn clo2d cursor bgf btn-act">体育</li>
                    </ul>-->
                    <div id="subModelMoreBox" style="display:none;">
                        <div class="sidebar-panel-title padd10 clearfix mgt10">
                            <span class="pull-left mgl20 cursor">子导航显示样式</span>
                            <!--<span class="pull-right blue mgr15 cursor" id="subModelMore">编辑</span>-->
                        </div>
                        <div id="subModelPreview" style="overflow: hidden;padding-left:20px">
                            <ul data-ref="tabmanage_style3" class="clearfix">
                                <li class="submenu-btn clo555 cursor bgf">首页</li>
                                <li class="submenu-btn clo69d6 mgl20 cursor bgf">新闻</li>
                                <li class="submenu-btn clo69d6 mgl20 cursor bgf">体育</li>
                            </ul>
                        </div>
                        <button id="ediSubNavBtn" class="mgl20 editList mgt10 mgb10">编辑子导航样式</button>
                        <!--<ul class="clearfix ">
                            <li class="pull-left menu-btn mgl20 cursor btn-cur activ">首页</li>
                            <li class="pull-left menu-btn cursor btn-cur">新闻</li>
                            <li class="pull-left menu-btn cursor btn-cur">体育</li>
                        </ul>-->
                    </div>
                </div>

            </section>
            <!--导航菜单管理添加-->

            <!--列表设置开始-->
            <section id="listSection" class="bline padb5">
                <div class="sidebar-panel-title padd10 clearfix">
                    <img class="pull-left fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="pull-left  mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="pull-left iconArea cursor">文章管理</span>
                </div>
                <div class="lebody">
                    <button id="updateListBtn" style="width: 205px;" class="mgl20 factive mgt10 mgb10">动态更新</button>
                    <button id="changeListBtn" style="width: 205px;display:none;" class="mgl20 factive">修改更新</button>
                    <button id="addListBtn" style="width: 205px;" class="mgl20 factive mgt10 mgb10">系统选稿</button>
                    <button id="addCustomList" style="width: 205px;" class="mgl20 factive mgt10 mgb10">手动添加</button>
                    <div id="listEdit" class="bline padb5" style="display: none">
                        <div id="listNewSet">
                            <input id="listSet" class="button-style padd3 mgl20 pa5 itemBtn" type="button" value="编辑列表"/>
                            <input id="listAdd" class="button-style padd3 mgl10 pa5 itemBtn" type="button" value="系统选稿"/>
                           <!-- <input id="listReset" class="button-style padd3 inputStyle40 mgl10 itemBtn" type="button" value="重置"/>-->
                            <input id="listClear" class="button-style padd3 mgl10 pa5 itemBtn" type="button" value="清空"/>
                        </div>
                        <div class="sidebar-panel-title padd10 clearfix">
                            <span class="pull-left mgl20 bdLeft cursor">样式管理</span>
                            <!--<span class="pull-right blue mgr15 cursor" id="listModelMore">更多</span>-->
                        </div>
                        <div id="listModelPreview" style="overflow: hidden;padding-left:20px">
                            <ul data-ref="listmanage_style3" class="clearfix">
                                <li class="cursor clo555 bgf model-list-item">文章标题<span class="badge pull-right">2016-06-18 10:49:01</span>
                                </li>
                                <li class="cursor clo555 bgf model-list-item">文章标题<span class="badge pull-right">2016-06-18 10:49:01</span>
                                </li>
                                <li class="cursor clo555 bgf model-list-item">文章标题<span class="badge pull-right">2016-06-18 10:49:01</span>
                                </li>
                            </ul>
                        </div>
                        <button id="editListBtn" class="mgl20 editList mgt10 mgb10">编辑单列样式</button>
                        <div class="sidebar-panel-title padd10 clearfix">
                            <span class="pull-left mgl20 cursor">右侧链接设置</span>
                        </div>
                        <div class="sidebar-panel-title clearfix mgl20">
                            <input id="moreListShow" class="pull-left" type="checkbox" checked>
                            <span class="pull-left mgl5 mgt1">显示</span>
                            <input id="moreListName" class="padd3 pull-right mgr15 pad5 width130" placeholder="请输入提示文字" type="text" value="更多">
                        </div>
                        <div class="sidebar-panel-title clearfix mgl20 padd10">
                            <span class="pull-left mgt1">链接地址</span>
                            <!--<span class="pull-right mgr15 mgl5 mgt1">同标题链接</span>
                            <input class="pull-right" type="checkbox">-->
                            <input id="moreListLink" class="padd3 mgr15 pad5 mgt10 width205" type="text" placeholder="请输入链接地址" value="#">
                        </div>
                        <div class="sidebar-panel-title clearfix mgl20">
                            <input id="moreListTime" class="pull-left" type="checkbox" checked>
                            <span class="pull-left mgl5 mgt1">发布时间</span>
                        </div>
                        <div class="sidebar-panel-title clearfix mgl20 padd10">
                            <span class="pull-left mgt1">时间格式</span>
                            <select id="timeFormat" class="padd3 pull-right mgr15 pad5 width130">
                                <option value="@{Y}-@{M}-@{D} @{H}:@{MI}:@{S}" selected>年-月-日 时:分:秒</option>
                                <option value="@{Y}/@{M}/@{D} @{H}:@{MI}:@{S}">年/月/日 时:分:秒</option>
                                <option value="@{D}/@{M}/@{Y} @{H}:@{MI}:@{S}">日/月/年 时:分:秒</option>
                                <option value="@{D}/@{M}/@{Y} @{H}:@{MI}">日/月/年 时:分</option>
                                <option value="@{Y}-@{M}-@{D} @{H}:@{MI}">年-月-日 时:分</option>
                                <option value="@{Y}-@{M}-@{D}">年-月-日</option>
                            </select>
                        </div>

                        <div class="sidebar-panel-title clearfix mgl20">
                            <input id="moreListAbstract" class="pull-left" type="checkbox" checked>
                            <span class="pull-left mgl5 mgt1 mgr25">摘要</span>
                            <input id="moreListSource" class="pull-left" type="checkbox" checked>
                            <span class="pull-left mgl5 mgt1 mgr25">来源</span>
                            <input id="moreListTag" class="pull-left" type="checkbox" checked style="display:none">
                            <span class="pull-left mgl5 mgt1" style="display:none">标签</span>
                        </div>

                        <div class="sidebar-panel-title padd10 clearfix">
                            <span class="pull-left mgl20 cursor">链接设置</span>
                        </div>
                        <div class="sidebar-panel-title clearfix mgl20">
                            <input id="listTarget" class="pull-left" type="checkbox" checked>
                            <span class="pull-left mgl5 mgt1">在新窗口打开标题链接</span>
                        </div>
                    </div>
                    <!--<div id="wholeSet" class="clearfix padd10 padr30 cursor">
                        <span class="pull-left cursor mgl15">组件全局设置</span>
                        <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
                    </div>-->


                    <!--<div class="sidebar-panel-title padd10">
                        <span class="border-left mgl10 pad5">列表设置</span>
                    </div>
                    <div class="sidebar-panel-title clearfix mgt10 mgl20">
                        <span class="pull-left">区域名称</span>
                        <input class="padd3 pull-right mgr15 pad5 width130" type="text" value="首页">
                    </div>
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl20 cursor">区块链接</span>
                        <span class="pull-right blue mgr15 cursor">提取</span>
                    </div>
                    <div class="sidebar-panel-title padd10 mgl20">
                        <input class="pad5 padd3 width205" type="text" value="https://itsoft.hold.founder.com">
                    </div>
                    <div class="sidebar-panel-title clearfix mgt10 mgl20">
                        <span class="pull-left">其他名称</span>
                        <input class="padd3 pull-right mgr15 pad5 width130" type="text" value="首页">
                    </div>
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl20 cursor">区块链接</span>
                        <span class="pull-right blue mgr15 cursor">提取</span>
                    </div>
                    <div class="sidebar-panel-title padd10 mgl20">
                        <input class="pad5 padd3 width205" type="text" value="https://itsoft.hold.founder.com">
                    </div>
                    <div class="sidebar-panel-title clearfix mgt10 mgl20">
                        <input class="pull-left" type="checkbox">
                        <span class="pull-left mgl5 mgt1">显示标题</span>
                        <input class="padd3 pull-right mgr15 pad5 width130" type="text" value="首页">
                    </div>
                    <div class="sidebar-panel-title clearfix mgt20 mgl20">
                        <input class="pull-left" type="checkbox">
                        <span class="pull-left mgl5 mgt1">显示标题</span>
                        <input class="padd3 pull-right mgr15 pad5 width130" type="text" value="首页">
                    </div>
    -->
                </div>

            </section>
            <!--galleryDiv-列表设置结束-->

            <!--轮播图管理开始-->
            <section id="galleryDiv" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png">
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">轮播图管理</span>
                </div>
                <div class="lebody">
                    <button id="updatePic" class="mgl20 factive mgb10 width205 mgt10">动态更新</button>
                    <button id="changePic" style="display:none;" class="mgl20 factive width205">修改更新</button>
                    <button id="gAddLocalPic" class="mgl20 factive mgb10 width205 mgt10">图片上传</button>
                    <button id="gAddItems" class="mgl20 factive mgb10 width205 mgt10">添加稿件</button>
                    <div id="carouselEdit" class="mgt20" style="display: none;">
                        <div class="carousel-editor">
                            <input id="carousSet" class="button-style padd3 inputStyle40 mgl20 itemBtn" type="button" value="编辑"/>
                            <input id="carousAdd" class="button-style padd3 inputStyle40 mgl10 itemBtn" type="button" value="追加"/>
                            <input id="carousReset" class="button-style padd3 inputStyle40 mgl10 itemBtn" type="button" value="重置"/>
                            <input id="gClear" class="button-style padd3 inputStyle40 mgl10 itemBtn" type="button" value="清空"/>
                        </div>
                        <ul id="gItemList" class="clearfix mgl25 mgt25 carousel-editor">
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                        </ul>
                        <div class="sidebar-panel-title padd10 clearfix">
                            <span class="pull-left mgl20 bdLeft cursor">显示样式</span>
                            <!--<span class="pull-right blue mgr15 cursor" id="carModelMore">更多</span>-->
                        </div>
                        <div id="carModelPreview" style="overflow: hidden;padding-left:20px">
                            <!--<ul data-ref="carmanage_style3" class="clearfix mgt10">
                                <li class="pull-left menu-btn clo555 mgl20 cursor bgf btn-act bordtom">首页</li>
                                <li class="pull-left menu-btn clo69d6 cursor bgf btn-act">新闻</li>
                                <li class="pull-left menu-btn clo69d6 cursor bgf btn-act">体育</li>
                            </ul>-->
                            <div class="posRelative mgl20 width205">
                                <ol class="carouselOl-model bottom10">
                                    <li class="width10 height10 bdRadius10 bgf borderf"></li>
                                    <li class="width10 height10 bdRadius10 bgfnone borderf"></li>
                                </ol>
                                <div class="carouselBox">
                                    <div>
                                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                                        <div class="carouselTitle">
                                            <h5>标题</h5>
                                            <p>内容简介</p>
                                        </div>
                                    </div>
                                </div>
                                <a class="left0 carouselBtn">
                                    <span class="glyphicon carouselBtn-left"></span>
                                </a>
                                <a class="right0 carouselBtn">
                                    <span class="glyphicon carouselBtn-right"></span>
                                </a>
                            </div>
                        </div>

                    </div>
                </div>
            </section>
            <!--轮播图管理结束-->

            <!--多图设置开始-->
            <section id="gallerySettingSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png">
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">多图管理</span>
                </div>
                <div class="lebody">
                    <button id="gUpdatePic" class="mgl20 factive mgb10 width205 mgt10">动态更新</button>
                    <button id="gChangePic" style="display:none;" class="mgl20 factive width205">修改更新</button>
                    <button id="addLocalPic" class="mgl20 factive mgb10 width205 mgt10">图片上传</button>
                    <button id="addMorePic" class="mgl20 factive mgb10 width205 mgt10">添加稿件</button>
                    <div id="galleryEdit" class="mgt20" style="display: none">
                        <div class="gallery-editor">
                            <input id="gAdd" class="button-style padd3 inputStyle50 mgl25 itemBtn" type="button" value="追加"/>
                            <input id="gReset" class="button-style padd3 inputStyle50 mgl10 itemBtn" type="button" value="重置"/>
                            <input id="gAllClear" class="button-style padd3 inputStyle50 mgl10 itemBtn" type="button" value="清空"/>
                        </div>
                        <ul id="gAllItemList" class="clearfix mgl25 mgt25 gallery-editor">
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                            <li class="posRelative pull-left mgr15 mgb10 border65">
                                <img src="export/images/sliderPanel/sliderPanel18.png" alt=""/>
                                <img class="posAbsolute cancelBtn" src="export/images/sliderPanel/sliderPanel19.png" alt=""/>
                            </li>
                        </ul>

                        <div class="sidebar-panel-title padd10">
                            <span class="mgl20">显示列数</span>
                        </div>
                        <ul id="columnChange" class="clearfix repeat mgl20">
                            <li class="pull-left btn-hint text-center select">1</li>
                            <li class="pull-left btn-hint text-center mgl5">2</li>
                            <li class="pull-left btn-hint text-center mgl5">3</li>
                            <li class="pull-left btn-hint text-center mgl5">4</li>
                            <li class="pull-left btn-hint text-center mgl5">5</li>
                            <li class="pull-left btn-hint text-center mgl5">6</li>
                        </ul>
                        <!-- <ul id="cropPicture" class="clearfix mgt15">
                             <span class="pull-left mgl20 mgr10 mgt5">裁切</span>
                             <li class="pull-left btn-hint handle-style mgl5 select">无</li>
                             <li class="pull-left btn-hint handle-style mgl5">正方形</li>
                             <li class="pull-left btn-hint handle-style mgl5">长方形</li>
                         </ul>-->
                        <ul id="paddPic" class="clearfix mgt15">
                            <span class="pull-left mgl20 mgr10 mgt5">间距</span>
                            <li class="pull-left btn-hint handle-style mgl5 select">无</li>
                            <li class="pull-left btn-hint handle-style mgl5">小</li>
                            <li class="pull-left btn-hint handle-style mgl5">中</li>
                            <li class="pull-left btn-hint handle-style mgl5">大</li>
                        </ul>
                        <ul id="borderPic" class="clearfix mgt15">
                            <span class="pull-left mgl20 mgr10 mgt5">相框</span>
                            <li data-border="0px" data-pad="0px" class="pull-left btn-hint handle-style mgl5 select">无</li>
                            <li data-border="1px" data-pad="2px" class="pull-left btn-hint handle-style mgl5">薄</li>
                            <li data-border="1px" data-pad="4px" class="pull-left btn-hint handle-style mgl5">中</li>
                            <li data-border="1px" data-pad="6px" class="pull-left btn-hint handle-style mgl5">厚</li>
                        </ul>
                        <div class="sidebar-panel-title clearfix mgt10 mgl20">
                            <input id="titleShow" class="pull-left" type="checkbox" checked>
                            <span class="pull-left mgl5 mgt1">显示标题</span>
                        </div>
                        <ul id="titlePic" class="clearfix mgt15">
                            <span class="pull-left mgl20 mgr10 mgt5">标题位置</span>
                            <li class="pull-left btn-hint handle-style mgl5 select">底部</li>
                            <li class="pull-left btn-hint handle-style mgl5">覆盖</li>
                        </ul>
                        <!--<div class="sidebar-panel-title clearfix mgt10 mgl20">
                            <input class="pull-left" type="checkbox">
                            <span class="pull-left mgl5 mgt1">点击图片放大</span>
                        </div>-->
                    </div>
                </div>
            </section>
            <!--稿件设置结束-->

            <!-- 基本样式 返回 -->
            <section id="backSection" class="sidebar-panel-color tline" style="padding-top: 5px;">
                <div class="text-center cursor" style="width: 100%; margin:0px auto 0px auto; background-color: #303030;border: 1px solid #242424;line-height: 24px;">
                    <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
                    <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
                    <span class="mgl10 text-left" id="">基本样式</span>
                </div>
            </section>
            <!-- 基本样式 返回 -->

            <!--框架设置开始-->
            <section id="frameSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">框架设置</span>
                </div>
                <div class="lebody">
                    <button id="frameAddBtn" class="mgl10 factive text-center">添加框架页面</button>
                    <ul class="clearfix mgt20 mgb10">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left">
                            <input data-ref="width" class="inputStyle inputStyle55 pad5 selectHeight frameWH unitOption" data-unit="%" data-unitex="e" type="text" value="100%"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input data-ref="height" class="inputStyle inputStyle55 selectHeight pad5 frameWH unitOption" data-unit="px" data-unitex="ep" type="text" value="140px"/>
                        </li>
                    </ul>
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl10 cursor">滚动条</span>
                    </div>
                    <div class="textAlign sidebar-panel-color clearfix">
                        <button data-ref="auto" class="button btn-hint mgl10 select frameScroll">按需加载</button>
                        <button data-ref="hidden" class="button btn-hint fmgl5 frameScroll">始终隐藏</button>
                    </div>
                </div>

            </section>
            <!--框架设置结束-->

            <!--对齐样式开始-->
            <section id="textAlignSection" class="sidebar-panel-color padb5 bline">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">组件对齐</span>
                </div>
                <div class="lebody">
                    <div class="textAlign sidebar-panel-color clearfix">
                        <ul id="aAlignUl">
                            <li data-align="left" class="left mgl5 btn-hint ">
                                <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                            <li data-align="center" class="left mgl5 btn-hint ">
                                <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                            <li data-align="right" class="left mgl5 mgr25 btn-hint ">
                                <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
                        </ul>
                        <!--<ul>
                            <li class="left mgl5 btn-hint"><img src="export/images/sliderPanel/textAlign4.png" alt=""/></li>
                            <li class="left mgl5 btn-hint"><img src="export/images/sliderPanel/textAlign5.png" alt=""/></li>
                            <li class="left mgl5 btn-hint"><img src="export/images/sliderPanel/textAlign6.png" alt=""/></li>
                        </ul>-->
                    </div>
                </div>

            </section>
            <!--对齐样式结束-->

            <!--位置大小開始 行间-->
            <section id="positionSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">容器位置大小</span>
                </div>
                <div class="lebody">
                    <table id="posAndBs" class="content shadow-top" align="center">
                        <tbody>
                        <tr class="row">
                            <td class="cell" colspan="2">
                                <div class="diy-control offset">
                                    <div class="box">
                                        <span class="handle top pHandler" data-ref="y" data-direct="margin-top"><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                        <span class="handle right pHandler" data-ref="x" data-direct="margin-right"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                        <span class="handle bottom pHandler" data-ref="y" data-direct="margin-bottom"><b>外边距</b><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                        <span class="handle left pHandler" data-ref="x" data-direct="margin-left"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                        <span class="label top disable-select positionData" contenteditable="true" data-direct="margin-top">0</span>
                                        <span class="label right disable-select positionData" contenteditable="true" data-direct="margin-right">0</span>
                                        <span class="label bottom disable-select positionData" contenteditable="true" data-direct="margin-bottom">0</span>
                                        <span class="label left disable-select positionData" contenteditable="true" data-direct="margin-left">0</span>
                                        <div class="padding">
                                            <span class="handle top pHandler" data-ref="y" data-direct="padding-top"><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                            <span class="handle right pHandler" data-ref="x" data-direct="padding-right"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                            <span class="handle bottom pHandler" data-ref="y" data-direct="padding-bottom"><b>内边距</b><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                            <span class="handle left pHandler" data-ref="x" data-direct="padding-left"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                            <span class="label top disable-select positionData" contenteditable="true" data-direct="padding-top">10</span>
                                            <span class="label right disable-select positionData" contenteditable="true" data-direct="padding-right">10</span>
                                            <span class="label bottom disable-select positionData" contenteditable="true" data-direct="padding-bottom">10</span>
                                            <span class="label left disable-select positionData" contenteditable="true" data-direct="padding-left">10</span>
                                        </div>
                                    </div>

                                </div>
                            </td>
                        </tr>

                        </tbody>
                    </table>

                    <ul id="positionBtnUl" class="clearfix mgt15">
                        <li style="padding-bottom: 2px !important;" class="pull-left mgl35 button-style padd3 padr10">
                            <img class="mgl5 fmgt5 conMiddle" src="export/images/sliderPanel/sliderPanel17.png"/>
                            <span class="cursor conMiddle">容器居中</span>
                        </li>
                        <li class="pull-left mgl15 padr10 button-style">
                            <img class="conClear" src="export/images/sliderPanel/sliderPanel16.png"/>
                            <span class="cursor conClear mgt1">清除</span>
                        </li>

                    </ul>
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <!--<span class="unit1 posAbsolute"></span>-->
                            <input id="position_width" class="inputStyle inputStyle55 pad5 selectHeight unitOption positionwh" data-direct="width" data-ref="width" type="text" value="960"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input id="position_height" class="inputStyle inputStyle55 selectHeight unitOption pad5 positionwh" data-direct="height" data-ref="height" type="text" value="960"/>
                        </li>
                    </ul>
                </div>


            </section>
            <!--位置大小結束 行间-->

            <!--位置大小開始 CSS文件-->
            <section id="css-positionSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">容器位置大小</span>
                </div>
                <div class="lebody">
                    <table id="css-posAndBs" class="content shadow-top" align="center">
                        <tbody>
                        <tr class="row">
                            <td class="cell" colspan="2">
                                <div class="diy-control offset">
                                    <div class="box">
                                        <span class="handle top pHandler" data-ref="y" data-direct="margin-top"><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                        <span class="handle right pHandler" data-ref="x" data-direct="margin-right"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                        <span class="handle bottom pHandler" data-ref="y" data-direct="margin-bottom"><b>外边距</b><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                        <span class="handle left pHandler" data-ref="x" data-direct="margin-left"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                        <span class="label top disable-select positionData" contenteditable="true" data-direct="margin-top">0</span>
                                        <span class="label right disable-select positionData" contenteditable="true" data-direct="margin-right">0</span>
                                        <span class="label bottom disable-select positionData" contenteditable="true" data-direct="margin-bottom">0</span>
                                        <span class="label left disable-select positionData" contenteditable="true" data-direct="margin-left">0</span>
                                        <div class="padding">
                                            <span class="handle top pHandler" data-ref="y" data-direct="padding-top"><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                            <span class="handle right pHandler" data-ref="x" data-direct="padding-right"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                            <span class="handle bottom pHandler" data-ref="y" data-direct="padding-bottom"><b>内边距</b><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                            <span class="handle left pHandler" data-ref="x" data-direct="padding-left"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                            <span class="label top disable-select positionData" contenteditable="true" data-direct="padding-top">10</span>
                                            <span class="label right disable-select positionData" contenteditable="true" data-direct="padding-right">10</span>
                                            <span class="label bottom disable-select positionData" contenteditable="true" data-direct="padding-bottom">10</span>
                                            <span class="label left disable-select positionData" contenteditable="true" data-direct="padding-left">10</span>
                                        </div>
                                    </div>

                                </div>
                            </td>
                        </tr>

                        </tbody>
                    </table>

                    <ul id="css-positionBtnUl" class="clearfix mgt15">
                        <li style="padding-bottom: 2px !important;" class="pull-left mgl35 button-style padd3 padr10">
                            <img class="mgl5 fmgt5 conMiddle" src="export/images/sliderPanel/sliderPanel17.png"/>
                            <span class="cursor conMiddle">容器居中</span>
                        </li>
                        <li class="pull-left mgl15 padr10 button-style">
                            <img class="conClear" src="export/images/sliderPanel/sliderPanel16.png"/>
                            <span class="cursor conClear mgt1">清除</span>
                        </li>

                    </ul>
                    <ul class="clearfix mgt20">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <!--<span class="unit1 posAbsolute"></span>-->
                            <input id="css-position_width" class="inputStyle inputStyle55 pad5 selectHeight unitOption positionwh" data-direct="width" data-ref="width" type="text" value="960"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input id="css-position_height" class="inputStyle inputStyle55 selectHeight unitOption pad5 positionwh" data-direct="height" data-ref="height" type="text" value="960"/>
                        </li>
                    </ul>
                </div>

            </section>
            <!--位置大小結束 CSS文件-->

            <!--背景样式开始 行间-->
            <section id="bgSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">背景</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景色</span>
                        <input id="inputBGColorPick" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                        <input id="bgColorPick" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景图</span>
                        <label id="bgFileLabel" class="posRelative">
                            <img id="bgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                            <img id="bgDeleteImg" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                        </label>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10 layoutBg-word">定位</span>
                        <div class="clearfix layoutBg-pos left">
                            <ul class="clearfix layoutBg-pos-nine">
                                <li class="left btn-hint bgPosition" data-x="0%" data-y="0%">
                                    <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="50%" data-y="0%">
                                    <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="100%" data-y="0%">
                                    <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="0%" data-y="50%">
                                    <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="50%" data-y="50%"></li>
                                <li class="left btn-hint bgPosition" data-x="100%" data-y="50%">
                                    <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                                <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%">
                                    <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                                <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%">
                                    <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                                <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%">
                                    <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
                            </ul>

                        </div>
                        <ul>
                            <li class="mgb10">
                                <span class="mgl5 mgr5">横向</span>
                                <input id="bgX" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                            </li>
                            <li>
                                <span class="mgl5 mgr5">纵向</span>
                                <input id="bgY" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                            </li>
                        </ul>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">重复</span>
                        <ul class="repeat">
                            <li class="left mgr5 bgRepeat" data-ref="repeat">
                                <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                            <li class="left mgr5 bgRepeat" data-ref="repeat-x">
                                <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                            <li class="left mgr5 bgRepeat" data-ref="repeat-y">
                                <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                            <li class="left mgr18 bgRepeat" data-ref="no-repeat">
                                <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                        </ul>
                    </div>
                </div>

            </section>
            <!--背景样式结束 行间-->

            <!--背景样式开始 CSS文件-->
            <section id="css-bgSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">背景</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景色</span>
                        <input id="css-inputBGColorPick" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                        <input id="css-bgColorPick" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景图</span>
                        <label id="css-bgFileLabel" class="posRelative">
                            <img id="css-bgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                            <img id="css-bgDeleteImg" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                        </label>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10 layoutBg-word">定位</span>
                        <div class="clearfix layoutBg-pos left">
                            <ul class="clearfix layoutBg-pos-nine">
                                <li class="left btn-hint bgPosition" data-x="0%" data-y="0%">
                                    <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="50%" data-y="0%">
                                    <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="100%" data-y="0%">
                                    <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="0%" data-y="50%">
                                    <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                                <li class="left btn-hint bgPosition" data-x="50%" data-y="50%"></li>
                                <li class="left btn-hint bgPosition" data-x="100%" data-y="50%">
                                    <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                                <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%">
                                    <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                                <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%">
                                    <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                                <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%">
                                    <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
                            </ul>

                        </div>
                        <ul>
                            <li class="mgb10">
                                <span class="mgl5 mgr5">横向</span>
                                <input id="css-bgX" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                            </li>
                            <li>
                                <span class="mgl5 mgr5">纵向</span>
                                <input id="css-bgY" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                            </li>
                        </ul>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">重复</span>
                        <ul class="repeat">
                            <li class="left mgr5 bgRepeat" data-ref="repeat">
                                <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                            <li class="left mgr5 bgRepeat" data-ref="repeat-x">
                                <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                            <li class="left mgr5 bgRepeat" data-ref="repeat-y">
                                <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                            <li class="left mgr18 bgRepeat" data-ref="no-repeat">
                                <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                        </ul>
                    </div>
                </div>

            </section>
            <!--背景样式结束 CSS文件-->
            <!--文字样式开始  行间-->
            <section id="textSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline clearfix">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">文字</span>
                </div>
                <div class="lebody">
                    <div class="selectPos mgb10 mgr15 mgl15 fontFa">
                        <form action="" method="post">
                            <div class="selectHeight fontFa divselect" id="fontFm">
                                <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">微软雅黑</cite>
                                <ul class="fontFa" id="fontFa">
                                    <li><a href="javascript:;" selectid="1" value="微软雅黑">微软雅黑</a></li>
                                    <li><a href="javascript:;" selectid="2" value="宋体">宋体</a></li>
                                    <li><a href="javascript:;" selectid="3" value="楷体">楷体</a></li>
                                    <li><a href="javascript:;" selectid="4" value="隶书">隶书</a></li>
                                    <li><a href="javascript:;" selectid="5" value="华文新魏">华文新魏</a></li>
                                    <li><a href="javascript:;" selectid="6" value="新宋体">新宋体</a></li>
                                    <li><a href="javascript:;" selectid="7" value="幼圆">幼圆</a></li>
                                    <li><a href="javascript:;" selectid="8" value="仿宋">仿宋</a></li>
                                    <li><a href="javascript:;" selectid="9" value="黑体">黑体</a></li>
                                    <li><a href="javascript:;" selectid="10" value="Arial">Arial</a></li>
                                    <li><a href="javascript:;" selectid="11" value="courier">courier</a></li>
                                    <li><a href="javascript:;" selectid="12" value="forte">forte</a></li>
                                    <li><a href="javascript:;" selectid="13" value="elephant">elephant</a></li>
                                    <li><a href="javascript:;" selectid="14" value="fantasy">fantasy</a></li>
                                </ul>
                            </div>
                            <input class="" name="" type="hidden" value=""/>
                        </form>
                    </div>
                    <div class="mgb10">
                        <img class="mgr5 mgl10" src="export/images/header/header2.png" alt="">
                        <div class="selectPos inlineBlock">
                            <form action="" method="post">
                                <div class="selectHeight fontSz divselect" id="fontSiz">
                                    <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">12px</cite>
                                    <!--<ul class="fontFa hide1 fontSz ">
                                        <li><a href="javascript:;" selectid="1" value="56">初号</a></li>
                                        <li><a href="javascript:;" selectid="2" value="48">小初</a></li>
                                        <li><a href="javascript:;" selectid="3" value="34.7">一号</a></li>
                                        <li><a href="javascript:;" selectid="4" value="32">小一</a></li>
                                        <li><a href="javascript:;" selectid="5" value="29.3">二号</a></li>
                                        <li><a href="javascript:;" selectid="6" value="24">小二</a></li>
                                        <li><a href="javascript:;" selectid="7" value="21.3">三号</a></li>
                                        <li><a href="javascript:;" selectid="8" value="20">小三</a></li>
                                        <li><a href="javascript:;" selectid="9" value="18.7">四号</a></li>
                                        <li><a href="javascript:;" selectid="10" value="16">小四</a></li>
                                        <li><a href="javascript:;" selectid="11" value="14">五号</a></li>
                                        <li><a href="javascript:;" selectid="12" value="12">小五</a></li>
                                    </ul>-->
                                    <!--<ul class="fontFa hide1 fontSz ">
                                        <li><a href="javascript:;" selectid="1" value="56">56px</a></li>
                                        <li><a href="javascript:;" selectid="2" value="48">48px</a></li>
                                        <li><a href="javascript:;" selectid="3" value="34.7">34px</a></li>
                                        <li><a href="javascript:;" selectid="4" value="32">32px</a></li>
                                        <li><a href="javascript:;" selectid="5" value="29.3">29px</a></li>
                                        <li><a href="javascript:;" selectid="6" value="24">24px</a></li>
                                        <li><a href="javascript:;" selectid="7" value="21.3">21px</a></li>
                                        <li><a href="javascript:;" selectid="8" value="20">20px</a></li>
                                        <li><a href="javascript:;" selectid="9" value="18.7">18px</a></li>
                                        <li><a href="javascript:;" selectid="10" value="16">16px</a></li>
                                        <li><a href="javascript:;" selectid="11" value="14">14px</a></li>
                                        <li><a href="javascript:;" selectid="12" value="12">12px</a></li>
                                    </ul>-->
                                    <ul class="fontFa hide1 fontSz ">
                                        <li><a href="javascript:;" selectid="1" value="12">12px</a></li>
                                        <li><a href="javascript:;" selectid="13" value="13">13px</a></li>
                                        <li><a href="javascript:;" selectid="2" value="14">14px</a></li>
                                        <li><a href="javascript:;" selectid="14" value="15">15px</a></li>
                                        <li><a href="javascript:;" selectid="3" value="16">16px</a></li>
                                        <li><a href="javascript:;" selectid="4" value="18">18px</a></li>
                                        <li><a href="javascript:;" selectid="5" value="20">20px</a></li>
                                        <li><a href="javascript:;" selectid="6" value="21">21px</a></li>
                                        <li><a href="javascript:;" selectid="7" value="24">24px</a></li>
                                        <li><a href="javascript:;" selectid="8" value="29">29px</a></li>
                                        <li><a href="javascript:;" selectid="9" value="32">32px</a></li>
                                        <li><a href="javascript:;" selectid="10" value="34">34px</a></li>
                                        <li><a href="javascript:;" selectid="11" value="48">48px</a></li>
                                        <li><a href="javascript:;" selectid="12" value="56">56px</a></li>
                                    </ul>
                                </div>
                                <input class="" name="" type="hidden" value=""/>
                            </form>
                        </div>
                        <span id="textColorEditor"><input id="headColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <!--<div class="mgb10">
                        <!--<div class="mgl15 mgr15 selectPos  fontSt inlineBlock">
                            <form action="" method="post">
                                <div class="electHeight fontSt divselect">
                                    <cite class="selectHeight pad5">Regular</cite>
                                    <ul class="fontFa hide1 fontSt">
                                        <li><a href="javascript:;" selectid="1">Regular</a></li>
                                        <li><a href="javascript:;" selectid="2">Normal</a></li>
                                        <li><a href="javascript:;" selectid="3">Regular</a></li>
                                        <li><a href="javascript:;" selectid="4">Normal</a></li>
                                    </ul>
                                </div>
                                <input class="" name="" type="hidden" value=""/>
                            </form>
                        </div>-->
                    <!--<span class="mgr5">宽度</span>
                    <span><input id="textColorPick" type="text" style="width: 20px; height: 16px;margin-top: 17px;"/></span>
                    <span class="mgr5">颜色</span>
                    <span><input id="headColorPick" type="text" style="width: 20px; height: 16px;margin-top: 17px;"/></span>

                </div>-->

                    <!--<div class="mgb10">
                        <img class="mgl15" src="export/images/header/header3.png" alt="">
                        <div class="selectPos  inlineBlock">
                            <form action="" method="post">
                                <div class="electHeight fontWz divselect">
                                    <cite class="selectHeight pad5">浑厚</cite>
                                    <ul class="fontFa hide1 fontWz">
                                        <li><a href="javascript:;" selectid="1">浑厚</a></li>
                                        <li><a href="javascript:;" selectid="2">浑厚</a></li>
                                        <li><a href="javascript:;" selectid="3">浑厚</a></li>
                                        <li><a href="javascript:;" selectid="4">浑厚</a></li>
                                    </ul>
                                </div>
                                <input class="" name="" type="hidden" value=""/>
                            </form>
                        </div>
                    </div>-->
                    <ul id="fontSt" class="mgb10 pull-left">
                        <li id="fontBold" data-kg="true" data-fontstyle="font-weight" data-value="700" data-reset="200" class="left  mgl10">
                            <img src="export/images/header/header7.png" alt=""></li>
                        <li id="fontItalic" data-kg="true" data-fontstyle="font-style" data-value="italic" data-reset="normal" class="left ">
                            <img src="export/images/header/header8.png" alt=""></li>
                        <li id="fontUnder" data-kg="true" data-fontstyle="text-decoration" data-value="underline" data-reset="none" data-line="under" class="left throughFont">
                            <img src="export/images/header/header9.png" alt=""></li>
                        <li id="fontThrough" data-kg="true" data-fontstyle="text-decoration" data-value="line-through" data-reset="none" data-line="through" class="left throughFont">
                            <img src="export/images/header/header10.png" alt=""></li>
                    </ul>
                    <ul id="alignUl" class="mgb10 pull-left">
                        <li data-align="left" class="left mgl5 clearbg">
                            <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                        <li data-align="center" class="left mgl5 clearbg">
                            <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                        <li data-align="right" class="left mgl5 clearbg">
                            <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
                    </ul>
                    <ul class="pull-left">
                        <li class="pull-left mgl10 mgr10 mgt5">行距</li>
                        <li class="pull-left">
                            <input data-ref="line-height" id="txtLineHeight" class="inputStyle inputStyle55 pad5 unitOption selectHeight numInput tabWH" data-unit="px" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl5 mgr5">
                            <img class="" src="export/images/header/indent.png" alt="">
                        </li>
                        <li class="pull-left">
                            <input data-ref="text-indent" id="txtIndent" class="inputStyle inputStyle55 pad5 selectHeight numInput tabWH" data-unit="px" type="text" value="2"/>
                        </li>
                    </ul>
                </div>
            </section>
            <!--文字样式结束 行间-->

            <!--文字样式开始  css文件-->
            <section id="css-textSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline clearfix">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">文字</span>
                </div>
                <div class="lebody">
                    <div class="selectPos mgb10 mgr15 mgl15 fontFa">
                        <form action="" method="post">
                            <div class="selectHeight fontFa divselect" id="css-fontFm">
                                <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">微软雅黑</cite>
                                <ul class="fontFa" id="css-fontFa">
                                    <li><a href="javascript:;" selectid="1" value="微软雅黑">微软雅黑</a></li>
                                    <li><a href="javascript:;" selectid="2" value="宋体">宋体</a></li>
                                    <li><a href="javascript:;" selectid="3" value="楷体">楷体</a></li>
                                    <li><a href="javascript:;" selectid="4" value="隶书">隶书</a></li>
                                    <li><a href="javascript:;" selectid="5" value="华文新魏">华文新魏</a></li>
                                    <li><a href="javascript:;" selectid="6" value="新宋体">新宋体</a></li>
                                    <li><a href="javascript:;" selectid="7" value="幼圆">幼圆</a></li>
                                    <li><a href="javascript:;" selectid="8" value="仿宋">仿宋</a></li>
                                    <li><a href="javascript:;" selectid="9" value="黑体">黑体</a></li>
                                    <li><a href="javascript:;" selectid="10" value="Arial">Arial</a></li>
                                    <li><a href="javascript:;" selectid="11" value="courier">courier</a></li>
                                    <li><a href="javascript:;" selectid="12" value="forte">forte</a></li>
                                    <li><a href="javascript:;" selectid="13" value="elephant">elephant</a></li>
                                    <li><a href="javascript:;" selectid="14" value="fantasy">fantasy</a></li>
                                </ul>
                            </div>
                            <input class="" name="" type="hidden" value=""/>
                        </form>
                    </div>
                    <div class="mgb10">
                        <img class="mgr5 mgl10" src="export/images/header/header2.png" alt="">
                        <div class="selectPos inlineBlock">
                            <form action="" method="post">
                                <div class="selectHeight fontSz divselect" id="css-fontSiz">
                                    <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">12px</cite>
                                    <ul class="fontFa hide1 fontSz ">
                                        <li><a href="javascript:;" selectid="1" value="12">12px</a></li>
                                        <li><a href="javascript:;" selectid="13" value="13">13px</a></li>
                                        <li><a href="javascript:;" selectid="2" value="14">14px</a></li>
                                        <li><a href="javascript:;" selectid="14" value="15">15px</a></li>
                                        <li><a href="javascript:;" selectid="3" value="16">16px</a></li>
                                        <li><a href="javascript:;" selectid="4" value="18">18px</a></li>
                                        <li><a href="javascript:;" selectid="5" value="20">20px</a></li>
                                        <li><a href="javascript:;" selectid="6" value="21">21px</a></li>
                                        <li><a href="javascript:;" selectid="7" value="24">24px</a></li>
                                        <li><a href="javascript:;" selectid="8" value="29">29px</a></li>
                                        <li><a href="javascript:;" selectid="9" value="32">32px</a></li>
                                        <li><a href="javascript:;" selectid="10" value="34">34px</a></li>
                                        <li><a href="javascript:;" selectid="11" value="48">48px</a></li>
                                        <li><a href="javascript:;" selectid="12" value="56">56px</a></li>
                                    </ul>
                                </div>
                                <input class="" name="" type="hidden" value=""/>
                            </form>
                        </div>
                        <span id="css-textColorEditor"><input id="css-headColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <ul id="css-fontSt" class="mgb10 pull-left">
                        <li id="css-fontBold" data-kg="true" data-fontstyle="font-weight" data-value="700" data-reset="200" class="left  mgl10">
                            <img src="export/images/header/header7.png" alt=""></li>
                        <li id="css-fontItalic" data-kg="true" data-fontstyle="font-style" data-value="italic" data-reset="normal" class="left ">
                            <img src="export/images/header/header8.png" alt=""></li>
                        <li id="css-fontUnder" data-kg="true" data-fontstyle="text-decoration" data-value="underline" data-reset="none" data-line="under" class="left throughFont">
                            <img src="export/images/header/header9.png" alt=""></li>
                        <li id="css-fontThrough" data-kg="true" data-fontstyle="text-decoration" data-value="line-through" data-reset="none" data-line="through" class="left throughFont">
                            <img src="export/images/header/header10.png" alt=""></li>
                    </ul>
                    <ul id="css-alignUl" class="mgb10 pull-left">
                        <li data-align="left" class="left mgl5 clearbg">
                            <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                        <li data-align="center" class="left mgl5 clearbg">
                            <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                        <li data-align="right" class="left mgl5 clearbg">
                            <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
                    </ul>
                    <ul class="pull-left">
                        <li class="pull-left mgl10 mgr10 mgt5">行距</li>
                        <li class="pull-left">
                            <input data-ref="line-height" id="css-txtLineHeight" class="inputStyle inputStyle55 pad5 unitOption selectHeight numInput tabWH" data-unit="px" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl5 mgr5">
                            <img class="" src="export/images/header/indent.png" alt="">
                        </li>
                        <li class="pull-left">
                            <input data-ref="text-indent" id="css-txtIndent" class="inputStyle inputStyle55 pad5 selectHeight numInput tabWH" data-unit="px" type="text" value="2"/>
                        </li>
                    </ul>
                </div>
            </section>
            <!--文字样式结束 css文件-->


            <!--边框样式开始  行间-->
            <section id="bolderSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline ">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">边框</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">颜色</span>
                        <input id="bBolderColor" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                        <input id="bolderColorPick" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
                    </div>
                    <div class="mgb10">
                        <span class="mgl15 mgr6">厚度</span>
                        <input id="bBolderWidth" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>
                    <div id="bPositionDiv" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">位置</span>
                        <ul id="bdPUl" class="repeat">
                            <li data-ref="border-top" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                            </li>
                            <li data-ref="border-bottom" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                            </li>
                            <li data-ref="border-left" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                            </li>
                            <li data-ref="border-right" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                            </li>
                            <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                            </li>
                            <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                            </li>
                            <li data-ref="border" class="left mgr5 btn-hint mgt5">
                                <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                            </li>
                        </ul>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">样式</span>
                        <ul id="bolderStyleUl" class="repeat">
                            <li data-ref="solid" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                            </li>
                            <li data-ref="dashed" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                            </li>
                            <li data-ref="dotted" class="left mgr6 btn-hint">
                                <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                            </li>
                            <li data-ref="none" class="left mgr18 btn-hint">
                                <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                            </li>
                        </ul>
                    </div>
                    <div id="bRadiusDiv" class="mgb10">
                        <span class="mgl15 mgr6">圆角半径</span>
                        <input id="bBolderRadius" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>
                    <!--<div id="bWidthDiv" class="mgb10">
                        <span class="mgl15 mgr6">宽度</span>
                        <input id="bWidth" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>-->
                    <!--<div id="bWidthDiv" class="mgb10">
                        <span class="mgl15 mgr6">宽度</span>
                        <input id="bWidth" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>-->
                    <!--<div class="mgb10">
                        <span class="mgl15 mgr6">不透明度</span>
                    <div class="selectPos mgr15 fontWz inlineBlock">
                        <form action="" method="post">
                            <div class="electHeight fontWz divselect">
                                <cite class="selectHeight pad5">100%</cite>
                                <ul class="fontWz">666
                                    <li><a href="javascript:;" selectid="1">80%</a></li>
                                    <li><a href="javascript:;" selectid="2">60%</a></li>
                                    <li><a href="javascript:;" selectid="3">40%</a></li>
                                    <li><a href="javascript:;" selectid="4">20%</a></li>
                                </ul>
                            </div>
                            <input class="" name="" type="hidden" value=""/>
                        </form>
                    </div>
                    </div>-->
                </div>
            </section>
            <!--边框样式结束  行间-->

            <!--边框样式开始  CSS文件-->
            <section id="css-bolderSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline ">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">边框</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">颜色</span>
                        <input id="css-bBolderColor" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                        <input id="css-bolderColorPick" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
                    </div>
                    <div class="mgb10">
                        <span class="mgl15 mgr6">厚度</span>
                        <input id="css-bBolderWidth" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>
                    <div id="css-bPositionDiv" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">位置</span>
                        <ul id="css-bdPUl" class="repeat">
                            <li data-ref="border-top" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                            </li>
                            <li data-ref="border-bottom" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                            </li>
                            <li data-ref="border-left" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                            </li>
                            <li data-ref="border-right" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                            </li>
                            <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                            </li>
                            <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                            </li>
                            <li data-ref="border" class="left mgr5 btn-hint mgt5">
                                <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                            </li>
                        </ul>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">样式</span>
                        <ul id="css-bolderStyleUl" class="repeat">
                            <li data-ref="solid" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                            </li>
                            <li data-ref="dashed" class="left mgr5 btn-hint">
                                <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                            </li>
                            <li data-ref="dotted" class="left mgr6 btn-hint">
                                <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                            </li>
                            <li data-ref="none" class="left mgr18 btn-hint">
                                <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                            </li>
                        </ul>
                    </div>
                    <div id="css-bRadiusDiv" class="mgb10">
                        <span class="mgl15 mgr6">圆角半径</span>
                        <input id="css-bBolderRadius" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>
                    <!--<div id="bWidthDiv" class="mgb10">
                        <span class="mgl15 mgr6">宽度</span>
                        <input id="bWidth" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>-->
                    <!--<div id="bWidthDiv" class="mgb10">
                        <span class="mgl15 mgr6">宽度</span>
                        <input id="bWidth" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
                    </div>-->
                    <!--<div class="mgb10">
                        <span class="mgl15 mgr6">不透明度</span>
                    <div class="selectPos mgr15 fontWz inlineBlock">
                        <form action="" method="post">
                            <div class="electHeight fontWz divselect">
                                <cite class="selectHeight pad5">100%</cite>
                                <ul class="fontWz">666
                                    <li><a href="javascript:;" selectid="1">80%</a></li>
                                    <li><a href="javascript:;" selectid="2">60%</a></li>
                                    <li><a href="javascript:;" selectid="3">40%</a></li>
                                    <li><a href="javascript:;" selectid="4">20%</a></li>
                                </ul>
                            </div>
                            <input class="" name="" type="hidden" value=""/>
                        </form>
                    </div>
                    </div>-->
                </div>
            </section>
            <!--边框样式结束  CSS文件-->

            <!--主导航显示样式开始-->
            <section id="mainMenuSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">主导航样式设置</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl10 cursor border-left pad5">基本样式</span>
                        <span id="mmBasicMain" data-ref="Position,BolderSetting,BackGround,TextSetting" class="pull-right blue mgr15 cursor">更多</span>
                    </div>
                    <ul class="clearfix" style="display:none">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <input class="mainBoxWH inputStyle inputStyle55 pad5 selectHeight unitOption" data-direct="width" data-ref="width" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input class="mainBoxWH inputStyle inputStyle55 selectHeight unitOption pad5" data-direct="height" data-ref="height" type="text" value="960px"/>
                        </li>
                    </ul>
                    <div class="sidebar-panel-color padd10 mgt10" style="display:none">
                        <span class="mgl10 mgr10">菜单宽度</span>
                        <input data-ref="width" class="mmTabWH inputStyle inputStyle80 pad5 selectHeight numInput" data-unit="px" type="text" value="960像素"/>
                    </div>
                   <!-- <div class="sidebar-panel-color padd10">
                        <span class="mgl10 mgr10">菜单内距</span>
                        <input data-ref="padding-left" id="mmTabPadding" class="inputStyle inputStyle80 pad5 unitOption selectHeight numInput" data-unit="px" type="text" value="960像素"/>
                    </div>
                    <div class="sidebar-panel-color padd10">
                        <span class="mgl10 mgr10">菜单高度</span>
                        <input data-ref="height" class="mmTabWH inputStyle inputStyle80 pad5 selectHeight unitOption numInput" data-unit="px" type="text" value="960像素"/>
                    </div>-->
                    <div class="sidebar-panel-color padd10" style="display:none">
                        <span class="mgl10 mgr10">菜单边距</span>
                        <input id="mmTabMargin" data-ref="margin-right" class="inputStyle inputStyle80 pad5 selectHeight numInput" data-unit="px" type="text" value="960像素"/>
                    </div>
                    <!--<div class="sidebar-panel-title padd10 mgl10">
                        <span class="border-left pad5">菜单文字对齐</span>
                    </div>
                    <ul id="mainAlignUl" class="textAlign sidebar-panel-color clearfix">
                        <li data-align="left" class="left mgl15 btn-hint">
                            <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                        <li data-align="center" class="left mgl5 btn-hint">
                            <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                        <li data-align="right" class="left mgl5 mgr15 btn-hint">
                            <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
                    </ul>-->
                    <div class="sidebar-panel-title padd10 clearfix mgt20">
                        <span class="pull-left mgl10 cursor border-left pad5">交互样式</span>
                        <!--<span class="pull-right blue mgr15 cursor">更多</span>-->
                    </div>
                    <!--<ul class="clearfix text-center" id="mainUl">
                        <li id="mainMenuNormal" class="pull-left interactiveStyles btn-curr activt mgl15">普通</li>
                        <li id="mainMenuHover" class="pull-left interactiveStyles btn-curr">悬停</li>
                        <li id="mainMenuClick" class="pull-left interactiveStyles btn-curr">点击</li>
                    </ul>

                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
                        <span class="left mgl15 mgr10">文字颜色</span>
                        <input data-0="#0069D6" data-1="#0069D6" data-2="#555" id="mainLitxColor" class="left inputStyle inputStyle80 pad5" type="text" value="#0069D6"/>
                        <span><input id="mainLitxColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
                        <span class="left mgl15 mgr10">背景颜色</span>
                        <input data-0="#ffffff" data-1="#f5f5f5" data-2="#ffffff" id="mainLibgColor" class="left inputStyle inputStyle80 pad5" type="text" value="#ffffff"/>
                        <span><input id="mainLibgColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景图</span>
                        <label id="mainBgFileLabel" class="posRelative">
                            <img id="mainBgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                            <img id="mainBgDeleteImg" style="display:none" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                        </label>
                    </div>-->
                </div>

            </section>
            <!--主导航显示样式结束-->


            <!--子导航显示样式开始-->
            <section id="subMenuSection" class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">子导航样式设置</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl10 cursor border-left pad5">基本样式</span>
                        <span id="subMenuBasicMain" class="pull-right blue mgr15 cursor">更多</span>
                    </div>
                    <!--<ul class="clearfix">
                        <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                        <li class="pull-left posRelative">
                            <input  class="subBoxWH inputStyle inputStyle55 pad5 selectHeight unitOption" data-direct="width" data-ref="width" type="text" value="960px"/>
                        </li>
                        <li class="pull-left mgl25 mgr10 mgt5">高</li>
                        <li class="pull-left">
                            <input  class="subBoxWH inputStyle inputStyle55 selectHeight unitOption pad5" data-direct="height" data-ref="height" type="text" value="960px"/>
                        </li>
                    </ul>-->
                    <div class="sidebar-panel-color padd10 mgt10" style="display:none;">
                        <span class="mgl10 mgr10">菜单宽度</span>
                        <input data-ref="width" class="subTabWH inputStyle inputStyle80 pad5 selectHeight numInput" data-unit="px" type="text" value="960像素"/>
                    </div>
                    <!--<div class="sidebar-panel-color padd10">
                        <span class="mgl10 mgr10">菜单内距</span>
                        <input data-ref="padding-left" id="subTabPadding" class="inputStyle inputStyle80 pad5 selectHeight unitOption numInput" data-unit="px" type="text" value="960像素"/>
                    </div>
                    <div class="sidebar-panel-color padd10">
                        <span class="mgl10 mgr10">菜单高度</span>
                        <input data-ref="height" class="subTabWH inputStyle inputStyle80 pad5 selectHeight unitOption numInput" data-unit="px" type="text" value="960像素"/>
                    </div>-->
                    <!-- <div class="sidebar-panel-color padd10">
                         <span class="mgl10 mgr10">菜单边距</span>
                         <input id="subTabMargin" data-ref="margin-bottom" class="inputStyle inputStyle80 pad5 selectHeight numInput" data-unit="px" type="text" value="960像素"/>
                     </div>-->
                    <!--<div class="sidebar-panel-title padd10 mgl10">
                        <span class="border-left pad5">菜单文字对齐</span>
                    </div>
                    <ul id="subAlignUl" class="textAlign sidebar-panel-color clearfix">
                        <li data-align="left" class="left mgl15 btn-hint">
                            <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                        <li data-align="center" class="left mgl5 btn-hint">
                            <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                        <li data-align="right" class="left mgl5 mgr15 btn-hint">
                            <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
                    </ul>-->
                    <div class="sidebar-panel-title padd10 clearfix mgt20">
                        <span class="pull-left mgl10 cursor border-left pad5">交互样式</span>
                        <!--<span class="pull-right blue mgr15 cursor">更多</span>-->
                    </div>
                    <!--<ul class="clearfix text-center" id="subUl">
                        <li id="subMenuNormal" class="pull-left interactiveStyles btn-curr activt mgl15">普通</li>
                        <li id="subMenuHover" class="pull-left interactiveStyles btn-curr">悬停</li>
                        <li id="subMenuClick" class="pull-left interactiveStyles btn-curr">点击</li>
                    </ul>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
                        <span class="left mgl15 mgr10">文字颜色</span>
                        <input data-0="#0069D6" data-1="#0069D6" data-2="#555" id="subLitxColor" class="left inputStyle inputStyle80 pad5" type="text" value="#0069D6"/>
                        <span><input id="subLitxColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
                        <span class="left mgl15 mgr10">背景颜色</span>
                        <input data-0="#ffffff" data-1="#f5f5f5" data-2="#ffffff" id="subLibgColor" class="left inputStyle inputStyle80 pad5" type="text" value="#ffffff"/>
                        <span><input id="subLibgColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">背景图</span>
                        <label id="subBgFileLabel" class="posRelative">
                            <img id="subBgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                            <img id="subBgDeleteImg" style="display:none" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                        </label>
                    </div>-->
                </div>

            </section>
            <!--字导航显示样式结束-->


            <!--超链接样式设置开始-->
            <section id="linkSection" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">链接</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <button id="fontActive" class="mgl15 factive">添加链接</button>
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">颜色</span>
                        <input id="linkColor" class="left inputStyle inputStyle80 pad5" type="text" value="#0000ff"/>
                        <!--<span class="left setColor mgl5 mgr5"></span>-->
                        <span><input id="linkColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>

                        <img id="linkUnder" data-value="underline" class="select" src="export/images/header/header9.png" alt="">
                    </div>
                    <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                        <span class="left mgl15 mgr10">悬停</span>
                        <input id="hoverColor" class="left inputStyle inputStyle80 pad5" type="text" value="#ff0000"/>
                        <!--<span class="left setColor mgl5 mgr5"></span>-->
                        <span><input id="hoverColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                        <img id="hoverUnder" data-value="underline" class="select" src="export/images/header/header9.png" alt="">
                    </div>
                </div>

            </section>
            <!--超链接样式设置结束-->


            <!--分享样式开始-->
            <!--   <section id="shareSection" class="sidebar-panel-color padb5 bline">
                    <div class="sidebar-panel-title padd10">
                        <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                        <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                        <span class="iconArea cursor">分享设置</span>
                    </div>
                    <div class="lebody">
                        <div class="share-style sidebar-panel-color clearfix">
                         <span class="left mgl15 mgr10">风格</span>
                            <ul id="shareStyle">
                            <li data-style="0" class="mgl5 btn-hint width130 height30 bg57">
                            <img src="" alt=""/></li>
                            <li data-style="1" class=" mgl5 btn-hint width130 height30 bg57 ">
                            <img src="" alt=""/></li>
                            </ul>
                        </div>
                        <div class="share-size sidebar-panel-color clearfix">
                        <span class="left mgl15 mgt5 mgr10">大小</span>
                            <ul id="shareSize">
                            <li data-size="16" class="cursor left mgl5 padall5 bg57 btn-hint ">16*16</li>
                            <li data-size="24" class="cursor left mgl5 padall5 bg57 btn-hint ">24*24</li>
                            <li data-size="32" class="cursor left mgl5 padall5 bg57 btn-hint ">32*32</li>
                            </ul>
                        </div>
                    </div>
                </section>-->
            <!--分享样式结束-->
            <!--分享样式开始-->
            <section id="shareSection" class="sidebar-panel-color padb5 bline">
                <!--<div class="sidebar-panel-title padd10">
                        <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                        <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                        <span class="iconArea cursor">分享设置</span>
                    </div>-->
                <div class="lebody">
                    <div class="share-style sidebar-panel-color clearfix">
                        <span class="mgl15 mgr10">按钮风格</span>
                        <ul id="shareStyle" class="mgl15">
                            <li data-style="0" class="width200 padall5 mgt10 bgf displayStyle height40 share1">
                                <img src="" alt=""/></li>
                            <li data-style="1" class="width200 padall5 mgt10 mgb10 bgf displayStyle height40 share2">
                                <img src="" alt=""/></li>
                        </ul>
                    </div>
                    <div class="share-size sidebar-panel-color clearfix">
                        <span class="mgl15 mgt10 mgr10">按钮大小</span>
                        <ul id="shareSize" class="mgl15 mgt10">
                            <li data-size="16" class="cursor left padl15t5 bg57 btn-hint ">16*16</li>
                            <li data-size="24" class="cursor left mgl5 padl15t5 bg57 btn-hint ">24*24</li>
                            <li data-size="32" class="cursor left mgl5 padl15t5 bg57 btn-hint ">32*32</li>
                        </ul>
                    </div>
                </div>
            </section>
            <!--分享样式结束-->
            <!--折线图开始-->
            <section id="LineChartSection" class="bline">
               <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">数据管理</span>
               </div>
               <div class="lebody clearfix">
                    <button id="editDataBtn" class="inputStyle90 mgl20 factive mgt5 mgb10 pull-left">编辑数据</button>
                    <button id="exportDataBtn" class="inputStyle90 mgr20 factive mgt5 mgb10 pull-right">导入数据</button>
               </div>

                <div class="sidebar-panel-title padd10 clearfix styleManager">
                    <span class="pull-left mgl20 cursor">样式管理</span>
                </div>
                <div id="lineChartModelPreview" style="overflow:hidden;padding-left:20px;">
                    <ul class="mgt5 mgb10 mgr20">
                        <li class="cursor bordtom">
                            <img src="export/images/sliderBar/sliderBar501.png" class="inputStyle198 cursor">
                        </li>
                    </ul>
                </div>

                <div class="sidebar-panel-title padd10 clearfix echartColor">
                    <span class="pull-left mgl20 cursor">图表配色</span>
                </div>
                <div id="chartColorPreview" style="overflow:hidden;">
                    <ul data-ref="chartColor_style1" class="clearfix width200 mgl20 padall5 bgf">
                        <li class="pull-left cursor width190 clearfix">
                            <div class="pull-left width20 col-c23531"></div>
                            <div class="pull-left width20 col-2f4554"></div>
                            <div class="pull-left width20 col-61a0a8"></div>
                            <div class="pull-left width20 col-d48265"></div>
                            <div class="pull-left width20 col-91c7ae"></div>
                        </li>
                    </ul>
                </div>
                <!--<button id="editColorBtn" class="mgl20 editList mgt10 mgb10">编辑此配色</button>-->

                <div class="sidebar-panel-title padd10 clearfix">
                    <span class="pull-left mgl20 cursor">字段显示设置</span>
                </div>
            <!--    <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgl20 mgt10">
                    <input id="columnTitle" class="pull-left mgt7" type="checkbox">
                    <span class="pull-left mgl5 mgr20" style="margin-right:31px;">标题</span>
                    <input id="chartTitleColor" class="pull-left inputStyle inputStyle80 pad5" type="text" value="#000000"/>
                    <input id="columnTitlePick" type="text" class="inputStyle20">
                </div>
                <div class="sidebar-panel-title mgb10 mgl20 mgt5">
                    <input id="chartTitleName" class="padd3 mgr15 pad5 mgt5 inputStyle185" type="text" placeholder="请输入标题名称" value="标题名称">
                </div>
                <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgl20 mgt20">
                    <input id="columnTitle-II" class="pull-left mgt7" type="checkbox">
                    <span class="pull-left mgl5 mgr20">副标题</span>
                    <input id="inputColumnTitle-II" class="pull-left inputStyle inputStyle80 pad5" type="text" value="#000000"/>
                    <input id="columnTitlePick-II" type="text" class="inputStyle20">
                </div>
                <div class="sidebar-panel-title mgb10 mgl20 mgt5">
                    <input id="chartTitleName-II" class="padd3 mgr15 pad5 mgt5 inputStyle185" type="text" placeholder="请输入副标题名称" value="副标题名称">
                </div>    -->
                <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgl20 mgt10 picLegend">
                    <input id="picChart" class="pull-left mgt7" type="checkbox">
                    <span class="pull-left mgl5 mgr10">图例文字</span>
                    <input id="inputWord" class="pull-left inputStyle inputStyle80 pad5" type="text" value="#000000"/>
                    <input id="inputWordPick" type="text" class="inputStyle20">
                </div>
                <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgb10 mgl20 mgt10">
                    <input id="alertChart" class="pull-left mgt7" type="checkbox">
                    <span class="pull-left mgl5 mgr20">提示框</span>
                </div>
                <div class="sidebar-panel-layout  vertical clearfix mgb10 tip">
                    <span class="left mgl15 mgr20" style="margin-left:37px;">指示线</span>
                    <input id="inputTipChart" class="left inputStyle inputStyle80 pad5" type="text" value="#000000"/>
                    <input id="tipChartPick" type="text" class="inputStyle20">
                </div>
                <div class="sidebar-panel-layout  vertical clearfix mgb10">
                    <span class="left mgl15 mgr10" style="margin-left:27px;">提示框背景</span>
                    <input id="inputTipBg" class="left inputStyle inputStyle80 pad5" type="text" value="#000000"/>
                    <input id="tipBgPick" type="text" class="inputStyle20">
                </div>
                <div class="sidebar-panel-layout  vertical clearfix mgb10">
                    <span class="left mgl15 mgr10" style="margin-left:27px;">提示框文字</span>
                    <input id="inputTipText" class="left inputStyle inputStyle80 pad5" type="text" value="#000000"/>
                    <input id="tipTextPick" type="text" class="inputStyle20">
                </div>
                <div id="chartTableSet" class="width240 bdtop2e clearfix padd10 padr30 cursor sidebar-panel-btn" style="display:block;">
                    <span class="pull-left cursor mgl15">图表设置</span>
                    <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
                </div>

            </section>

            <!--折线图结束-->
            <!--组件全局设置开始-->
            <section id="wholeSetSection" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
                <span class="pull-left cursor mgl15">组件全局设置</span>
                <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
            </section>
            <!--组件全局设置结束-->


        </div>
        <!--样式结束-->
        <!--属性开始-->
        <div class="hide-text">
            <!--导航菜单管理开始-->
            <section class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">导航菜单管理</span>

                </div>
                <div class="lebody">
                    <img class="mgl15" src="export/images/sliderPanel/menu1.png" alt=""/>
                    <div class="sidebar-panel-title padd10 clearfix">
                        <span class="pull-left mgl15 cursor border-left pad5">主导航显示样式</span>
                        <span class="pull-right blue mgr15 cursor">编辑</span>
                    </div>
                    <ul class="clearfix">
                        <li class="pull-left menu-btn clo2d mgl15 cursor bgf">首页</li>
                        <li class="pull-left menu-btn clo2d cursor bgf">新闻</li>
                        <li class="pull-left menu-btn clo2d cursor bgf">体育</li>
                    </ul>
                    <div class="sidebar-panel-title padd10 clearfix mgt20">
                        <span class="pull-left mgl15 cursor border-left pad5">子导航显示样式</span>
                        <span class="pull-right blue mgr15 cursor">编辑</span>
                    </div>
                    <ul class="clearfix mgb10">
                        <li class="pull-left menu-btn mgl15 cursor">首页</li>
                        <li class="pull-left menu-btn cursor">新闻</li>
                        <li class="pull-left menu-btn cursor">体育</li>
                    </ul>
                </div>

            </section>
            <!--导航菜单管理结束-->


            <!--标题属性开始-->
            <section class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <span class="border-left mgl10 pad5">标题设置</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-title padd10">
                        <span class="mgl20">标题名称</span>
                    </div>
                    <div class="sidebar-panel-title padd10 mgl20">
                        <input class="pad5 padd3 width205" type="text" value="首页"/>
                    </div>
                    <div class="sidebar-panel-title clearfix mgt10">
                        <span class="pull-left mgl20 cursor">链接地址</span>
                        <span class="pull-right blue mgr15 cursor">提取</span>
                    </div>
                    <div class="sidebar-panel-title padd10 mgl20">
                        <input class="pad5 padd3 width205" type="text" value="https://itsoft.hold.founder.com"/>
                    </div>
                    <div class="sidebar-panel-title padd10">
                        <span class="border-left mgl10 pad5">右侧链接设置</span>
                    </div>
                    <div class="sidebar-panel-title clearfix mgt10 mgl20">
                        <input class="pull-left" type="checkbox"/>
                        <span class="pull-left mgl5 mgt1">显示</span>
                        <input class="padd3 pull-right mgr15 pad5 width130" type="text" value="更多"/>
                    </div>
                    <div class="sidebar-panel-title clearfix mgt20 mgl20">
                        <span class="pull-left">链接地址</span>
	                	<span class="pull-right">
	                		<input class="pull-left" type="checkbox"/>
	            			<span class=" pull-left padd3 mgr15 pad5 fmgt2">同标题链接</span>
	                	</span>

                    </div>
                    <div class="sidebar-panel-title padd10 mgl20">
                        <input class="pad5 padd3 width205" type="text" value="https://itsoft.hold.founder.com"/>
                    </div>
                </div>


            </section>
            <!--标题属性结束-->
            <!--可选字段显示开始-->
            <section class="bline padb10">
                <div class="sidebar-panel-title padd10">
                    <span class="mgl20">可选字段显示</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-title clearfix mgl20">
                        <input class="pull-left" type="checkbox">
                        <span class="pull-left mgl5 mgt1">发布时间</span>
                    </div>
                    <div class="sidebar-panel-title padd10 mgt10">
                        <span class="mgl20">时间格式</span>
                    </div>
                    <div class="sidebar-panel-title mgl20">
                        <input class="pad5 padd3 width205" type="text" value="2015-04-30">
                    </div>
                </div>

            </section>
            <section class="bline padb10">
                <div class="sidebar-panel-title padd10">
                    <span class="mgl20">可选字段显示</span>
                </div>
                <div class="lebody">
                    <div class="sidebar-panel-title clearfix mgt10 mgl20">
                        <input class="pull-left" type="checkbox">
                        <span class="pull-left mgl5 mgt1">在新窗口打开标题链接</span>
                    </div>
                    <div class="sidebar-panel-title clearfix mgt10 mgl20">
                        <input class="pull-left" type="checkbox">
                        <span class="pull-left mgl5 mgt1">在新窗口打开分类标题链接</span>
                    </div>
                </div>

            </section>
            <!--可选字段显示结束-->
            <!--显示样式开始-->
            <section class="bline padb5">
                <div class="sidebar-panel-title padd10">
                    <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                    <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                    <span class="iconArea cursor">显示样式</span>
                    <span class="pull-right blue mgr15 cursor mgt3">编辑</span>
                </div>
                <div class="lebody">
                    <ul class="clearfix text-center">
                        <li class="pull-left interactiveStyles btn-curr mgl15 activt">普通</li>
                        <li class="pull-left interactiveStyles btn-curr ">悬停</li>
                        <li class="pull-left interactiveStyles btn-curr">点击</li>
                    </ul>
                </div>

            </section>
            <!--显示样式开始-->
            <!--添加列表开始-->
            <section class="bline padb5">
                <button class="mgl20 factive mgb10 width205 mgt20">添加列表</button>
                <div class="sidebar-panel-title padd10 clearfix">
                    <span class="pull-left mgl20 cursor">显示样式</span>
                    <span class="pull-right blue mgr15 cursor">编辑</span>
                </div>
            </section>

            <!--添加列表结束-->
        </div>

    </div>

</div>
<!--右侧功能栏结束-->
<!--右侧功能栏II开始-->
<div id="editModelGoBack" class="model-edit clearfix bline zindex102">
    <div id="model-edit-goback" class="text-center pull-left  model-edit-goback width100">
        <img class="pull-left" style="margin: 10px 10px 0 15px;" src="export/images/sliderPanel/listBack.png" alt=""/>
        <span class="pull-left cursor">编辑样式</span>
    </div>
    <!--<div class="text-center pull-right model-edit-save">
        <span class="cursor">保存样式</span>
    </div>-->
</div>
<div id="sidebar-panel-II" class="setBarHeightModel bline padb5 listModelDiv zindex102 sidebar">
    <Section class="width240 bline clearfix padd10">
        <ul>
            <li class="left mgl10 padd2">
                <input id="listStyle" type="checkbox"></li>
            <li class="left mgl5 padd3">
                <span class="">列表图标</span></li>
            <li class="left mgl10 bgf displayStyle">
                <img src="export/images/sliderPanel/listFounder.png" alt=""/></li>
            <li class="left mgl10 bgf displayStyle">
                <img src="export/images/sliderPanel/listCircle.png" alt=""/></li>
            <li id="listImgPreview" class="left mgl10 btn-hint bgf posRelative hide1 displayStyle">
                <img class="previewListItem" src="export/images/sliderPanel/listCircle.png" alt=""/>
                <img class="removeListItem posAbsolute cancelBtn" src="export/images/sliderPanel/listRemove.png" alt=""/>
            </li>
            <li id="listImgAdd" class="left mgl10 bg57">
                <img src="export/images/sliderPanel/listAdd.png" alt=""/></li>
        </ul>
    </Section>
    <!--位置大小開始  行间-->
    <section id="positionSection-II" class="width240 bline padb5">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">位置</span>
        </div>
        <div class="lebody">
            <table id="posAndBs-II" class="content shadow-top" align="center">
                <tbody>
                <tr class="row">
                    <td class="cell" colspan="2">
                        <div class="diy-control offset">
                            <div class="box">
                                <span class="handle top pHandler" data-ref="y" data-direct="margin-top"><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                <span class="handle right pHandler" data-ref="x" data-direct="margin-right"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                <span class="handle bottom pHandler" data-ref="y" data-direct="margin-bottom"><b>外边距</b><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                <span class="handle left pHandler" data-ref="x" data-direct="margin-left"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                <span class="label top disable-select positionData" contenteditable="true" data-direct="margin-top">0</span>
                                <span class="label right disable-select positionData" contenteditable="true" data-direct="margin-right">0</span>
                                <span class="label bottom disable-select positionData" contenteditable="true" data-direct="margin-bottom">0</span>
                                <span class="label left disable-select positionData" contenteditable="true" data-direct="margin-left">0</span>
                                <div class="padding">
                                    <span class="handle top pHandler" data-ref="y" data-direct="padding-top"><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                    <span class="handle right pHandler" data-ref="x" data-direct="padding-right"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                    <span class="handle bottom pHandler" data-ref="y" data-direct="padding-bottom"><b>内边距</b><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                    <span class="handle left pHandler" data-ref="x" data-direct="padding-left"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                    <span class="label top disable-select positionData" contenteditable="true" data-direct="padding-top">10</span>
                                    <span class="label right disable-select positionData" contenteditable="true" data-direct="padding-right">10</span>
                                    <span class="label bottom disable-select positionData" contenteditable="true" data-direct="padding-bottom">10</span>
                                    <span class="label left disable-select positionData" contenteditable="true" data-direct="padding-left">10</span>
                                </div>
                            </div>

                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <ul id="positionBtnUl-II" class="clearfix mgt15">
                <li style="padding-bottom: 2px !important;" class="pull-left mgl35 button-style padd3 padr10">
                    <img class="mgl5 fmgt5 conMiddle" src="export/images/sliderPanel/sliderPanel17.png"/>
                    <span class="cursor conMiddle">容器居中</span>
                </li>
                <li class="pull-left mgl15 padr10 button-style">
                    <img class="conClear" src="export/images/sliderPanel/sliderPanel16.png"/>
                    <span class="cursor conClear mgt1">清除</span>
                </li>
            </ul>
            <ul class="clearfix mgt20">
                <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                <li class="pull-left posRelative">
                    <!--<span class="unit1 posAbsolute"></span>-->
                    <input id="position_width-II" class="inputStyle inputStyle55 pad5 selectHeight unitOption positionwh" data-direct="width" data-ref="width" type="text" value="960"/>

                </li>
                <li class="pull-left mgl25 mgr10 mgt5">高</li>
                <li class="pull-left">
                    <input id="position_height-II" class="inputStyle inputStyle55 selectHeight unitOption pad5 positionwh" data-direct="height" data-ref="height" type="text" value="960"/>
                </li>
            </ul>
        </div>
    </section>

    <!--位置大小結束   行间-->

    <!--位置大小開始  CSS文件-->
    <section id="css-positionSection-II" class="width240 bline padb5">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">位置</span>
        </div>
        <div class="lebody">
            <table id="css-posAndBs-II" class="content shadow-top" align="center">
                <tbody>
                <tr class="row">
                    <td class="cell" colspan="2">
                        <div class="diy-control offset">
                            <div class="box">
                                <span class="handle top pHandler" data-ref="y" data-direct="margin-top"><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                <span class="handle right pHandler" data-ref="x" data-direct="margin-right"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                <span class="handle bottom pHandler" data-ref="y" data-direct="margin-bottom"><b>外边距</b><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                <span class="handle left pHandler" data-ref="x" data-direct="margin-left"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                <span class="label top disable-select positionData" contenteditable="true" data-direct="margin-top">0</span>
                                <span class="label right disable-select positionData" contenteditable="true" data-direct="margin-right">0</span>
                                <span class="label bottom disable-select positionData" contenteditable="true" data-direct="margin-bottom">0</span>
                                <span class="label left disable-select positionData" contenteditable="true" data-direct="margin-left">0</span>
                                <div class="padding">
                                    <span class="handle top pHandler" data-ref="y" data-direct="padding-top"><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                    <span class="handle right pHandler" data-ref="x" data-direct="padding-right"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                    <span class="handle bottom pHandler" data-ref="y" data-direct="padding-bottom"><b>内边距</b><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                    <span class="handle left pHandler" data-ref="x" data-direct="padding-left"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                    <span class="label top disable-select positionData" contenteditable="true" data-direct="padding-top">10</span>
                                    <span class="label right disable-select positionData" contenteditable="true" data-direct="padding-right">10</span>
                                    <span class="label bottom disable-select positionData" contenteditable="true" data-direct="padding-bottom">10</span>
                                    <span class="label left disable-select positionData" contenteditable="true" data-direct="padding-left">10</span>
                                </div>
                            </div>

                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <ul id="css-positionBtnUl-II" class="clearfix mgt15">
                <li style="padding-bottom: 2px !important;" class="pull-left mgl35 button-style padd3 padr10">
                    <img class="mgl5 fmgt5 conMiddle" src="export/images/sliderPanel/sliderPanel17.png"/>
                    <span class="cursor conMiddle">容器居中</span>
                </li>
                <li class="pull-left mgl15 padr10 button-style">
                    <img class="conClear" src="export/images/sliderPanel/sliderPanel16.png"/>
                    <span class="cursor conClear mgt1">清除</span>
                </li>
            </ul>
            <ul class="clearfix mgt20">
                <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                <li class="pull-left posRelative">
                    <!--<span class="unit1 posAbsolute"></span>-->
                    <input id="css-position_width-II" class="inputStyle inputStyle55 pad5 selectHeight unitOption positionwh" data-direct="width" data-ref="width" type="text" value="960"/>

                </li>
                <li class="pull-left mgl25 mgr10 mgt5">高</li>
                <li class="pull-left">
                    <input id="css-position_height-II" class="inputStyle inputStyle55 selectHeight unitOption pad5 positionwh" data-direct="height" data-ref="height" type="text" value="960"/>
                </li>
            </ul>
        </div>
    </section>

    <!--位置大小結束   CSS文件-->

    <!--背景样式开始  行间-->
    <section id="bgSection-II" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">背景</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景色</span>
                <input id="inputBGColorPick-II" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="bgColorPick-II" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景图</span>
                <label id="bgFileLabel-II" class="posRelative">
                    <img id="bgPreviewImg-II" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                    <img id="bgDeleteImg-II" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                </label>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10 layoutBg-word">定位</span>
            <div class="clearfix layoutBg-pos left">
            <ul class="clearfix layoutBg-pos-nine">
                <li class="left btn-hint bgPosition" data-x="0%" data-y="0%">
                    <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="50%" data-y="0%">
                    <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="100%" data-y="0%">
                    <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="0%" data-y="50%">
                    <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="50%" data-y="50%"></li>
                <li class="left btn-hint bgPosition" data-x="100%" data-y="50%">
                    <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%">
                    <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%">
                    <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%">
                    <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
            </ul>

            </div>
                <ul>
                    <li class="mgb10">
                        <span class="mgl5 mgr5">横向</span>
                        <input id="bgX-II" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                    <li>
                        <span class="mgl5 mgr5">纵向</span>
                        <input id="bgY-II" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">重复</span>
                <ul class="repeat">
                    <li class="left mgr5 bgRepeat" data-ref="repeat">
                        <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-x">
                        <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-y">
                        <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                    <li class="left mgr18 bgRepeat" data-ref="no-repeat">
                        <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                </ul>
            </div>
        </div>

    </section>
    <!--背景样式结束   行间-->

    <!--背景样式开始  CSS文件-->
    <section id="css-bgSection-II" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">背景</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景色</span>
                <input id="css-inputBGColorPick-II" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="css-bgColorPick-II" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景图</span>
                <label id="css-bgFileLabel-II" class="posRelative">
                    <img id="css-bgPreviewImg-II" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                    <img id="css-bgDeleteImg-II" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                </label>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10 layoutBg-word">定位</span>
            <div class="clearfix layoutBg-pos left">
            <ul class="clearfix layoutBg-pos-nine">
                <li class="left btn-hint bgPosition" data-x="0%" data-y="0%">
                    <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="50%" data-y="0%">
                    <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="100%" data-y="0%">
                    <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="0%" data-y="50%">
                    <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                <li class="left btn-hint bgPosition" data-x="50%" data-y="50%"></li>
                <li class="left btn-hint bgPosition" data-x="100%" data-y="50%">
                    <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%">
                    <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%">
                    <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%">
                    <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
            </ul>

            </div>
                <ul>
                    <li class="mgb10">
                        <span class="mgl5 mgr5">横向</span>
                        <input id="css-bgX-II" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                    <li>
                        <span class="mgl5 mgr5">纵向</span>
                        <input id="css-bgY-II" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">重复</span>
                <ul class="repeat">
                    <li class="left mgr5 bgRepeat" data-ref="repeat">
                        <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-x">
                        <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-y">
                        <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                    <li class="left mgr18 bgRepeat" data-ref="no-repeat">
                        <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                </ul>
            </div>
        </div>

    </section>
    <!--背景样式结束   CSS文件-->

    <!--边框样式开始  行间-->
    <section id="bolderSection-II" class="width240 sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">边框</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">颜色</span>
                <input id="bBolderColor-II" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="bolderColorPick-II" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="mgb10">
                <span class="mgl15 mgr6">厚度</span>
                <input id="bBolderWidth-II" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <div id="bPositionDiv-II" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">位置</span>
                <ul id="bdPUl-II" class="repeat">
                    <li data-ref="border-top" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                    </li>
                    <li data-ref="border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                    </li>
                    <li data-ref="border-left" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                    </li>
                    <li data-ref="border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                    </li>
                    <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                    </li>
                    <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                    </li>
                    <li data-ref="border" class="left mgr5 btn-hint mgt5">
                        <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">样式</span>
                <ul id="bolderStyleUl-II" class="repeat">
                    <li data-ref="solid" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                    </li>
                    <li data-ref="dashed" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                    </li>
                    <li data-ref="dotted" class="left mgr6 btn-hint">
                        <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                    </li>
                    <li data-ref="none" class="left mgr18 btn-hint">
                        <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div id="bRadiusDiv-II" class="mgb10">
                <span class="mgl15 mgr6">圆角半径</span>
                <input id="bBolderRadius-II" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <!--<div id="bWidthDiv-II" class="mgb10">
            <span class="mgl15 mgr6">宽度</span>
            <input id="bWidth-II" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div id="bWidthDiv-II" class="mgb10">
            <span class="mgl15 mgr6">宽度</span>
            <input id="bWidth-II" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div class="mgb10">
            <span class="mgl15 mgr6">不透明度</span>
            <div class="selectPos mgr15 fontWz inlineBlock">
            <form action="" method="post">
            <div class="electHeight fontWz divselect">
            <cite class="selectHeight pad5">100%</cite>
            <ul class="fontWz">
            <li><a href="javascript:;" selectid="1">80%</a></li>
            <li><a href="javascript:;" selectid="2">60%</a></li>
            <li><a href="javascript:;" selectid="3">40%</a></li>
            <li><a href="javascript:;" selectid="4">20%</a></li>
            </ul>
            </div>
            <input class="" name="" type="hidden" value=""/>
            </form>
            </div>
            </div>-->
        </div>
    </section>
    <!--边框样式结束   行间-->

    <!--边框样式开始  CSS文件-->
    <section id="css-bolderSection-II" class="width240 sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">边框</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">颜色</span>
                <input id="css-bBolderColor-II" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="css-bolderColorPick-II" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="mgb10">
                <span class="mgl15 mgr6">厚度</span>
                <input id="css-bBolderWidth-II" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <div id="css-bPositionDiv-II" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">位置</span>
                <ul id="css-bdPUl-II" class="repeat">
                    <li data-ref="border-top" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                    </li>
                    <li data-ref="border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                    </li>
                    <li data-ref="border-left" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                    </li>
                    <li data-ref="border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                    </li>
                    <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                    </li>
                    <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                    </li>
                    <li data-ref="border" class="left mgr5 btn-hint mgt5">
                        <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">样式</span>
                <ul id="css-bolderStyleUl-II" class="repeat">
                    <li data-ref="solid" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                    </li>
                    <li data-ref="dashed" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                    </li>
                    <li data-ref="dotted" class="left mgr6 btn-hint">
                        <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                    </li>
                    <li data-ref="none" class="left mgr18 btn-hint">
                        <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div id="css-bRadiusDiv-II" class="mgb10">
                <span class="mgl15 mgr6">圆角半径</span>
                <input id="css-bBolderRadius-II" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <!--<div id="bWidthDiv-II" class="mgb10">
            <span class="mgl15 mgr6">宽度</span>
            <input id="bWidth-II" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div id="bWidthDiv-II" class="mgb10">
            <span class="mgl15 mgr6">宽度</span>
            <input id="bWidth-II" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div class="mgb10">
            <span class="mgl15 mgr6">不透明度</span>
            <div class="selectPos mgr15 fontWz inlineBlock">
            <form action="" method="post">
            <div class="electHeight fontWz divselect">
            <cite class="selectHeight pad5">100%</cite>
            <ul class="fontWz">
            <li><a href="javascript:;" selectid="1">80%</a></li>
            <li><a href="javascript:;" selectid="2">60%</a></li>
            <li><a href="javascript:;" selectid="3">40%</a></li>
            <li><a href="javascript:;" selectid="4">20%</a></li>
            </ul>
            </div>
            <input class="" name="" type="hidden" value=""/>
            </form>
            </div>
            </div>-->
        </div>
    </section>
    <!--边框样式结束   CSS文件-->
    <section id="titleSettingBtn" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
        <span class="pull-left cursor mgl15">标题样式设置</span>
        <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
    </section>
    <section id="summarySettingBtn" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
        <span class="pull-left cursor mgl15">摘要样式设置</span>
        <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
    </section>
    <section id="sourceSettingBtn" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
        <span class="pull-left cursor mgl15">来源样式设置</span>
        <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
    </section>
    <section id="timeBtn" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
        <span class="pull-left cursor mgl15">发布时间样式设置</span>
        <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
    </section>
    <section id="linkBtn" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
        <span class="pull-left cursor mgl15">右侧链接样式设置</span>
        <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
    </section>
    <section id="keyWordBtn" class="width240 bline clearfix padd10 padr30 cursor sidebar-panel-btn">
    <span class="pull-left cursor mgl15">标签样式设置</span>
    <img class="pull-right mgt5" src="export/images/sliderPanel/listForward.png" alt=""/>
    </section>
</div>
<!--右侧功II能栏结束-->

<!--右侧功能栏III开始-->
<div id="editModelGoBack-III" class="model-edit clearfix bline zindex103">
    <div id="model-edit-goback-III" class="text-center pull-left model-edit-goback">
        <img class="pull-left" style="margin: 10px 10px 0 15px;" src="export/images/sliderPanel/listBack.png" alt=""/>
        <span class="pull-left cursor">标题样式设置</span>
    </div>
</div>
<div id="sidebar-panel-III" class="setBarHeightModel bline padb5 listModelDiv zindex103 sidebar">
    <!--导航设置开始-->
    <section id="panel-III-tab" class="sidebar-panel-layout sidebar-panel-color padb5 clearfix">
        <div id="panel-III-basic" class="mgl10 nav-set-tab pull-left select cursor mgt10 mgb10">基本样式</div>
        <div id="panel-III-active" class="nav-set-tab pull-left mgt10 cursor mgb10">交互样式</div>
    </section>
    <section id="panel-III-mainWH" class="bline pdb10">
        <div class="sidebar-panel-color padd10">
            <span class="mgl10 mgr10">菜单内距</span>
            <input data-ref="padding-left" id="mmTabPadding" class="inputStyle inputStyle80 pad5 unitOption selectHeight numInput" data-unit="px" type="text" value="960像素"/>
        </div>
        <div class="sidebar-panel-color padd10">
            <span class="mgl10 mgr10">菜单高度</span>
            <input data-ref="height" class="mmTabWH inputStyle inputStyle80 pad5 selectHeight unitOption numInput" data-unit="px" type="text" value="960像素"/>
        </div>
        <div class="sidebar-panel-title padd10 mgl10">
            <span class="border-left pad5">菜单文字对齐</span>
        </div>
        <ul id="mainAlignUl" class="textAlign sidebar-panel-color clearfix">
            <li data-align="left" class="left mgl15 btn-hint">
                <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
            <li data-align="center" class="left mgl5 btn-hint">
                <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
            <li data-align="right" class="left mgl5 mgr15 btn-hint">
                <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
        </ul>
    </section>
    <section id="panel-III-subWH" class="bline pdb10">
        <div class="sidebar-panel-color padd10">
            <span class="mgl10 mgr10">菜单内距</span>
            <input data-ref="padding-left" id="subTabPadding" class="inputStyle inputStyle80 pad5 selectHeight unitOption numInput" data-unit="px" type="text" value="960像素"/>
        </div>
        <div class="sidebar-panel-color padd10">
            <span class="mgl10 mgr10">菜单高度</span>
            <input data-ref="height" class="subTabWH inputStyle inputStyle80 pad5 selectHeight unitOption numInput" data-unit="px" type="text" value="960像素"/>
        </div>
        <div class="sidebar-panel-title padd10 mgl10">
            <span class="border-left pad5">菜单文字对齐</span>
        </div>
        <ul id="subAlignUl" class="textAlign sidebar-panel-color clearfix">
            <li data-align="left" class="left mgl15 btn-hint">
                <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
            <li data-align="center" class="left mgl5 btn-hint">
                <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
            <li data-align="right" class="left mgl5 mgr15 btn-hint">
                <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
        </ul>
    </section>
    <section id="panel-III-tabWH" class="bline pdb10">
        <ul class="clearfix">
            <li class="pull-left mgl10 mgr10 mgt5">宽</li>
            <li class="pull-left">
            <input data-ref="width" class="inputStyle inputStyle55 pad5 unitOption selectHeight numInput tabWH" data-unit="px" type="text" value="960px"/>
            </li>
            <li class="pull-left mgl25 mgr10 mgt5">高</li>
            <li class="pull-left">
            <input data-ref="height" class="inputStyle inputStyle55 selectHeight pad5 unitOption numInput tabWH" data-unit="px" type="text" value="960px"/>
            </li>
        </ul>
    </section>
    <section id="panel-III-mainActive" class="bline">
        <ul class="clearfix text-center" id="mainUl">
            <li id="mainMenuNormal" class="pull-left interactiveStyles btn-curr activt mgl15">普通</li>
            <li id="mainMenuHover" class="pull-left interactiveStyles btn-curr">悬停</li>
            <li id="mainMenuClick" class="pull-left interactiveStyles btn-curr">点击</li>
        </ul>

        <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
            <span class="left mgl15 mgr10">文字颜色</span>
            <input data-0="#0069D6" data-1="#0069D6" data-2="#555" id="mainLitxColor" class="left inputStyle inputStyle80 pad5" type="text" value="#0069D6"/>
            <span><input id="mainLitxColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
        </div>
        <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
            <span class="left mgl15 mgr10">背景颜色</span>
            <input data-0="#ffffff" data-1="#f5f5f5" data-2="#ffffff" id="mainLibgColor" class="left inputStyle inputStyle80 pad5" type="text" value="#ffffff"/>
            <span><input id="mainLibgColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
        </div>
        <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
            <span class="left mgl15 mgr10">背景图</span>
            <label id="mainBgFileLabel" class="posRelative">
            <img id="mainBgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
            <img id="mainBgDeleteImg" style="display:none" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
        </label>
        </div>

    </section>
    <section id="panel-III-subActive" class="bline">
        <ul class="clearfix text-center" id="subUl">
            <li id="subMenuNormal" class="pull-left interactiveStyles btn-curr activt mgl15">普通</li>
            <li id="subMenuHover" class="pull-left interactiveStyles btn-curr">悬停</li>
            <li id="subMenuClick" class="pull-left interactiveStyles btn-curr">点击</li>
        </ul>
        <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
            <span class="left mgl15 mgr10">文字颜色</span>
            <input data-0="#0069D6" data-1="#0069D6" data-2="#555" id="subLitxColor" class="left inputStyle inputStyle80 pad5" type="text" value="#0069D6"/>
            <span><input id="subLitxColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
        </div>
        <div class="sidebar-panel-layoutBg  vertical clearfix mgb10 mgt20">
            <span class="left mgl15 mgr10">背景颜色</span>
            <input data-0="#ffffff" data-1="#f5f5f5" data-2="#ffffff" id="subLibgColor" class="left inputStyle inputStyle80 pad5" type="text" value="#ffffff"/>
            <span><input id="subLibgColorPick" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
        </div>
        <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
            <span class="left mgl15 mgr10">背景图</span>
            <label id="subBgFileLabel" class="posRelative">
            <img id="subBgPreviewImg" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
            <img id="subBgDeleteImg" style="display:none" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
            </label>
        </div>
    </section>
    <!--导航交互按钮TAB开始-->
    <section id="panel-III-styleTab" >
        <ul class="clearfix text-center" id="styletab-Ul">
            <li id="style-normal" class="pull-left interactiveStyles btn-curr activt mgl15">普通</li>
            <li id="style-hover" class="pull-left interactiveStyles btn-curr">悬停</li>
            <li id="style-click" class="pull-left interactiveStyles btn-curr">点击</li>
        </ul>
    </section>
    <!--导航交互按钮TAB结束-->

    <!--导航交互文字样式开始-->
    <section id="textSection-style" class="sidebar-panel-layout sidebar-panel-color padb5 bline clearfix">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">文字</span>
        </div>
        <div class="lebody">
            <div class="selectPos mgb10 mgr15 mgl15 fontFa">
                <form action="" method="post">
                    <div class="selectHeight fontFa divselect" id="fontFm-style">
                        <cite data-0="微软雅黑" data-1="宋体" data-2="楷体" class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">微软雅黑</cite>
                        <ul class="fontFa" id="fontFa-style">
                            <li><a href="javascript:;" selectid="1" value="微软雅黑">微软雅黑</a></li>
                            <li><a href="javascript:;" selectid="2" value="宋体">宋体</a></li>
                            <li><a href="javascript:;" selectid="3" value="楷体">楷体</a></li>
                            <li><a href="javascript:;" selectid="4" value="隶书">隶书</a></li>
                            <li><a href="javascript:;" selectid="5" value="华文新魏">华文新魏</a></li>
                            <li><a href="javascript:;" selectid="6" value="新宋体">新宋体</a></li>
                            <li><a href="javascript:;" selectid="7" value="幼圆">幼圆</a></li>
                            <li><a href="javascript:;" selectid="8" value="仿宋">仿宋</a></li>
                            <li><a href="javascript:;" selectid="9" value="黑体">黑体</a></li>
                            <li><a href="javascript:;" selectid="10" value="Arial">Arial</a></li>
                            <li><a href="javascript:;" selectid="11" value="courier">courier</a></li>
                            <li><a href="javascript:;" selectid="12" value="forte">forte</a></li>
                            <li><a href="javascript:;" selectid="13" value="elephant">elephant</a></li>
                            <li><a href="javascript:;" selectid="14" value="fantasy">fantasy</a></li>
                        </ul>
                    </div>
                    <input class="" name="" type="hidden" value=""/>
                </form>
            </div>
            <div class="mgb10">
                <img class="mgr5 mgl10" src="export/images/header/header2.png" alt="">
                <div class="selectPos inlineBlock">
                    <form action="" method="post">
                        <div class="selectHeight fontSz divselect" id="fontSiz-style">
                            <cite data-0="12px" data-1="12px" data-2="12px" class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">12px</cite>
                            <ul class="fontFa hide1 fontSz ">
                                <li><a href="javascript:;" selectid="1" value="12">12px</a></li>
                                <li><a href="javascript:;" selectid="13" value="13">13px</a></li>
                                <li><a href="javascript:;" selectid="2" value="14">14px</a></li>
                                <li><a href="javascript:;" selectid="14" value="15">15px</a></li>
                                <li><a href="javascript:;" selectid="3" value="16">16px</a></li>
                                <li><a href="javascript:;" selectid="4" value="18">18px</a></li>
                                <li><a href="javascript:;" selectid="5" value="20">20px</a></li>
                                <li><a href="javascript:;" selectid="6" value="21">21px</a></li>
                                <li><a href="javascript:;" selectid="7" value="24">24px</a></li>
                                <li><a href="javascript:;" selectid="8" value="29">29px</a></li>
                                <li><a href="javascript:;" selectid="9" value="32">32px</a></li>
                                <li><a href="javascript:;" selectid="10" value="34">34px</a></li>
                                <li><a href="javascript:;" selectid="11" value="48">48px</a></li>
                                <li><a href="javascript:;" selectid="12" value="56">56px</a></li>
                            </ul>
                        </div>
                        <input class="" name="" type="hidden" value=""/>
                    </form>
                </div>
                <span id="textColorEditor-style"><input data-0="12px" data-1="12px" data-2="12px" id="headColorPick-style" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
            </div>
            <ul id="fontSt-style" class="mgb10 pull-left">
                <li id="fontBold-style" data-kg="true" data-fontstyle="font-weight" data-value="bold" data-reset="200" data-0="200" data-1="200" data-2="200" class="left  mgl10">
                <img src="export/images/header/header7.png" alt=""></li>
                <li id="fontItalic-style" data-kg="true" data-fontstyle="font-style" data-value="italic" data-reset="normal" data-0="normal" data-1="normal" data-2="normal" class="left ">
                <img src="export/images/header/header8.png" alt=""></li>
                <li id="fontUnder-style" data-kg="true" data-fontstyle="text-decoration" data-value="underline" data-reset="none" data-line="under" data-0="none" data-1="none" data-2="none" class="left throughFontStyle">
                <img src="export/images/header/header9.png" alt=""></li>
                <li id="fontThrough-style" data-kg="true" data-fontstyle="text-decoration" data-value="line-through" data-reset="none" data-line="through" data-0="none" data-1="none" data-2="none" class="left throughFontStyle">
                <img src="export/images/header/header10.png" alt=""></li>
            </ul>
            <!--<ul id="alignUl-style" class="mgb10 pull-left">
                <li data-align="left" class="left mgl5 clearbg">
                <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                <li data-align="center" class="left mgl5 clearbg">
                <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                <li data-align="right" class="left mgl5 clearbg">
                <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
            </ul>-->
        </div>
    </section>
    <!--导航交互文字样式结束-->
    <!--导航交互背景样式开始-->
    <section id="bgSection-style" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">背景</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景色</span>
                <input id="inputBGColorPick-style" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="bgColorPick-style" data-0="rgb(51, 51, 51)" data-1="rgba(27%, 27%, 27%, 1)" data-2="rgba(27%, 27%, 27%, 1)" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景图</span>
                <label id="bgFileLabel-style" class="posRelative">
                    <img id="bgPreviewImg-style" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png" data-0="export/images/sliderPanel/sliderPanel1.png" data-1="export/images/sliderPanel/sliderPanel1.png" data-2="export/images/sliderPanel/sliderPanel1.png"/>
                    <img id="bgDeleteImg-style" style="display:none" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                </label>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10 layoutBg-word">定位</span>
                <div class="clearfix layoutBg-pos left">
                    <ul id="bgPosition-ul" class="clearfix layoutBg-pos-nine">
                        <li class="left btn-hint bgPosition" data-x="0%" data-y="0%" >
                            <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="50%" data-y="0%" >
                            <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="100%" data-y="0%" >
                            <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="0%" data-y="50%" >
                            <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="50%" data-y="50%" ></li>
                        <li class="left btn-hint bgPosition" data-x="100%" data-y="50%" >
                            <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                        <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%" >
                            <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                        <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%" >
                            <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                        <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%" >
                            <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
                    </ul>
                </div>
                <ul id="bgPosition-xy">
                    <li class="mgb10">
                        <span class="mgl5 mgr5">横向</span>
                        <input id="bgX-style" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%" data-0="0% 0%" data-1="0% 0%" data-2="0% 0%"/>
                    </li>
                    <li>
                        <span class="mgl5 mgr5">纵向</span>
                        <input id="bgY-style" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%" />
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">重复</span>
                <ul id="bgRepeat-ul" class="repeat">
                    <li class="left mgr5 bgRepeat" data-ref="repeat" data-0="repeat" data-1="repeat" data-2="repeat">
                        <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-x" data-0="repeat" data-1="repeat" data-2="repeat">
                        <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-y" data-0="repeat" data-1="repeat" data-2="repeat">
                        <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                    <li class="left mgr18 bgRepeat" data-ref="no-repeat" data-0="repeat" data-1="repeat" data-2="repeat">
                        <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                </ul>
            </div>
        </div>
    </section>
    <!--导航交互背景样式开始-->
    <!--导航交互边框样式开始-->
    <section id="bolderSection-style" class="width240 sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">边框</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">颜色</span>
                <input id="bBolderColor-style" data-0="#333333" data-1="#333333" data-2="#333333" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="bolderColorPick-style"  data-0="rgb(51,51,51)" data-1="rgb(51,51,51)" data-2="rgb(51,51,51)" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="mgb10">
                <span class="mgl15 mgr6">厚度</span>
                <input id="bBolderWidth-style" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <div id="bPositionDiv-style" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">位置</span>
                <ul id="bdPUl-style" class="repeat">
                    <li data-ref="border-top" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                    </li>
                    <li data-ref="border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                    </li>
                    <li data-ref="border-left" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                    </li>
                    <li data-ref="border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                    </li>
                    <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                    </li>
                    <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                    </li>
                    <li data-ref="border" class="left mgr5 btn-hint mgt5">
                        <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">样式</span>
                <ul id="bolderStyleUl-style" class="repeat">
                    <li data-ref="solid" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                    </li>
                    <li data-ref="dashed" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                    </li>
                    <li data-ref="dotted" class="left mgr6 btn-hint">
                        <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                    </li>
                    <li data-ref="none" class="left mgr18 btn-hint">
                        <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div id="bRadiusDiv-style" class="mgb10">
                <span class="mgl15 mgr6">圆角半径</span>
                <input id="bBolderRadius-style" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <!--<div id="bWidthDiv-style" class="mgb10">
            <span class="mgl15 mgr6">宽度</span>
            <input id="bWidth-style" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div id="bWidthDiv-style" class="mgb10">
            <span class="mgl15 mgr6">宽度</span>
            <input id="bWidth-style" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div class="mgb10">
            <span class="mgl15 mgr6">不透明度</span>
            <div class="selectPos mgr15 fontWz inlineBlock">
            <form action="" method="post">
            <div class="electHeight fontWz divselect">
            <cite class="selectHeight pad5">100%</cite>
            <ul class="fontWz">
            <li><a href="javascript:;" selectid="1">80%</a></li>
            <li><a href="javascript:;" selectid="2">60%</a></li>
            <li><a href="javascript:;" selectid="3">40%</a></li>
            <li><a href="javascript:;" selectid="4">20%</a></li>
            </ul>
            </div>
            <input class="" name="" type="hidden" value=""/>
            </form>
            </div>
            </div>-->
        </div>
    </section>
    <!--导航交互边框样式开始-->

    <!--导航设置结束-->

    <!--文字样式开始 行間-->
    <section id="textSection-III" class="sidebar-panel-layout sidebar-panel-color padb5 bline clearfix">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">文字</span>
        </div>
        <div class="lebody">
            <div class="selectPos mgb10 mgr15 mgl15 fontFa">
                <form action="" method="post">
                    <div class="selectHeight fontFa divselect" id="fontFm-III">
                        <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">微软雅黑</cite>
                        <ul class="fontFa" id="fontFa-III">
                            <li><a href="javascript:;" selectid="1" value="微软雅黑">微软雅黑</a></li>
                            <li><a href="javascript:;" selectid="2" value="宋体">宋体</a></li>
                            <li><a href="javascript:;" selectid="3" value="楷体">楷体</a></li>
                            <li><a href="javascript:;" selectid="4" value="隶书">隶书</a></li>
                            <li><a href="javascript:;" selectid="5" value="华文新魏">华文新魏</a></li>
                            <li><a href="javascript:;" selectid="6" value="新宋体">新宋体</a></li>
                            <li><a href="javascript:;" selectid="7" value="幼圆">幼圆</a></li>
                            <li><a href="javascript:;" selectid="8" value="仿宋">仿宋</a></li>
                            <li><a href="javascript:;" selectid="9" value="黑体">黑体</a></li>
                            <li><a href="javascript:;" selectid="10" value="Arial">Arial</a></li>
                            <li><a href="javascript:;" selectid="11" value="courier">courier</a></li>
                            <li><a href="javascript:;" selectid="12" value="forte">forte</a></li>
                            <li><a href="javascript:;" selectid="13" value="elephant">elephant</a></li>
                            <li><a href="javascript:;" selectid="14" value="fantasy">fantasy</a></li>
                        </ul>
                    </div>
                    <input class="" name="" type="hidden" value=""/>
                </form>
            </div>
            <div class="mgb10">
                <img class="mgr5 mgl10" src="export/images/header/header2.png" alt="">
                <div class="selectPos inlineBlock">
                    <form action="" method="post">
                        <div class="selectHeight fontSz divselect" id="fontSiz-III">
                            <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">12px</cite>
                            <ul class="fontFa hide1 fontSz ">
                                <li><a href="javascript:;" selectid="1" value="12">12px</a></li>
                                <li><a href="javascript:;" selectid="13" value="13">13px</a></li>
                                <li><a href="javascript:;" selectid="2" value="14">14px</a></li>
                                <li><a href="javascript:;" selectid="14" value="15">15px</a></li>
                                <li><a href="javascript:;" selectid="3" value="16">16px</a></li>
                                <li><a href="javascript:;" selectid="4" value="18">18px</a></li>
                                <li><a href="javascript:;" selectid="5" value="20">20px</a></li>
                                <li><a href="javascript:;" selectid="6" value="21">21px</a></li>
                                <li><a href="javascript:;" selectid="7" value="24">24px</a></li>
                                <li><a href="javascript:;" selectid="8" value="29">29px</a></li>
                                <li><a href="javascript:;" selectid="9" value="32">32px</a></li>
                                <li><a href="javascript:;" selectid="10" value="34">34px</a></li>
                                <li><a href="javascript:;" selectid="11" value="48">48px</a></li>
                                <li><a href="javascript:;" selectid="12" value="56">56px</a></li>
                            </ul>
                        </div>
                        <input class="" name="" type="hidden" value=""/>
                    </form>
                </div>
                <span id="textColorEditor-III"><input id="headColorPick-III" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
            </div>
            <ul id="fontSt-III" class="mgb10 pull-left">
                <li id="fontBold-III" data-kg="true" data-fontstyle="font-weight" data-value="700" data-reset="200" class="left  mgl10">
                    <img src="export/images/header/header7.png" alt=""></li>
                <li id="fontItalic-III" data-kg="true" data-fontstyle="font-style" data-value="italic" data-reset="normal" class="left ">
                    <img src="export/images/header/header8.png" alt=""></li>
                <li id="fontUnder-III" data-kg="true" data-fontstyle="text-decoration" data-value="underline" data-reset="none" data-line="under" class="left throughFont">
                    <img src="export/images/header/header9.png" alt=""></li>
                <li id="fontThrough-III" data-kg="true" data-fontstyle="text-decoration" data-value="line-through" data-reset="none" data-line="through" class="left throughFont">
                    <img src="export/images/header/header10.png" alt=""></li>
            </ul>
            <ul id="alignUl-III" class="mgb10 pull-left">
                <li data-align="left" class="left mgl5 clearbg">
                    <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                <li data-align="center" class="left mgl5 clearbg">
                    <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                <li data-align="right" class="left mgl5 clearbg">
                    <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
            </ul>
            <ul class="pull-left">
                <li class="pull-left mgl10 mgr10 mgt5">行距</li>
                <li class="pull-left">
                    <input data-ref="line-height" id="txtLineHeight-III" class="inputStyle inputStyle55 pad5 unitOption selectHeight numInput tabWH" data-unit="px" type="text" value="960px"/>
                </li>
                <li class="pull-left mgl5 mgr5">
                    <img class="" src="export/images/header/indent.png" alt="">
                </li>
                <li class="pull-left">
                    <input data-ref="text-indent" id="txtIndent-III" class="inputStyle inputStyle55 pad5 selectHeight numInput tabWH" data-unit="px" type="text" value="2"/>
                </li>
            </ul>
        </div>
    </section>
    <!--文字样式结束 行間-->
    <!--文字样式开始 css-->
    <section id="css-textSection-III" class="sidebar-panel-layout sidebar-panel-color padb5 bline clearfix">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">文字</span>
        </div>
        <div class="lebody">
            <div class="selectPos mgb10 mgr15 mgl15 fontFa">
                <form action="" method="post">
                    <div class="selectHeight fontFa divselect"id="css-fontFm-III">
                        <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">微软雅黑</cite>
                        <ul class="fontFa" id="css-fontFa-III">
                            <li><a href="javascript:;" selectid="1" value="微软雅黑">微软雅黑</a></li>
                            <li><a href="javascript:;" selectid="2" value="宋体">宋体</a></li>
                            <li><a href="javascript:;" selectid="3" value="楷体">楷体</a></li>
                            <li><a href="javascript:;" selectid="4" value="隶书">隶书</a></li>
                            <li><a href="javascript:;" selectid="5" value="华文新魏">华文新魏</a></li>
                            <li><a href="javascript:;" selectid="6" value="新宋体">新宋体</a></li>
                            <li><a href="javascript:;" selectid="7" value="幼圆">幼圆</a></li>
                            <li><a href="javascript:;" selectid="8" value="仿宋">仿宋</a></li>
                            <li><a href="javascript:;" selectid="9" value="黑体">黑体</a></li>
                            <li><a href="javascript:;" selectid="10" value="Arial">Arial</a></li>
                            <li><a href="javascript:;" selectid="11" value="courier">courier</a></li>
                            <li><a href="javascript:;" selectid="12" value="forte">forte</a></li>
                            <li><a href="javascript:;" selectid="13" value="elephant">elephant</a></li>
                            <li><a href="javascript:;" selectid="14" value="fantasy">fantasy</a></li>
                        </ul>
                    </div>
                    <input class="" name="" type="hidden" value=""/>
                </form>
            </div>
            <div class="mgb10">
                <img class="mgr5 mgl10" src="export/images/header/header2.png" alt="">
                <div class="selectPos inlineBlock">
                    <form action="" method="post">
                        <div class="selectHeight fontSz divselect" id="css-fontSiz-III">
                            <cite class="selectHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">12px</cite>
                            <ul class="fontFa hide1 fontSz ">
                                <li><a href="javascript:;" selectid="1" value="12">12px</a></li>
                                <li><a href="javascript:;" selectid="13" value="13">13px</a></li>
                                <li><a href="javascript:;" selectid="2" value="14">14px</a></li>
                                <li><a href="javascript:;" selectid="14" value="15">15px</a></li>
                                <li><a href="javascript:;" selectid="3" value="16">16px</a></li>
                                <li><a href="javascript:;" selectid="4" value="18">18px</a></li>
                                <li><a href="javascript:;" selectid="5" value="20">20px</a></li>
                                <li><a href="javascript:;" selectid="6" value="21">21px</a></li>
                                <li><a href="javascript:;" selectid="7" value="24">24px</a></li>
                                <li><a href="javascript:;" selectid="8" value="29">29px</a></li>
                                <li><a href="javascript:;" selectid="9" value="32">32px</a></li>
                                <li><a href="javascript:;" selectid="10" value="34">34px</a></li>
                                <li><a href="javascript:;" selectid="11" value="48">48px</a></li>
                                <li><a href="javascript:;" selectid="12" value="56">56px</a></li>
                            </ul>
                        </div>
                        <input class="" name="" type="hidden" value=""/>
                    </form>
                </div>
                <span id="css-textColorEditor-III"><input id="css-headColorPick-III" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
            </div>
            <ul id="css-fontSt-III" class="mgb10 pull-left">
                <li id="css-fontBold-III" data-kg="true" data-fontstyle="font-weight" data-value="700" data-reset="200" class="left  mgl10">
                    <img src="export/images/header/header7.png" alt=""></li>
                <li id="css-fontItalic-III" data-kg="true" data-fontstyle="font-style" data-value="italic" data-reset="normal" class="left ">
                    <img src="export/images/header/header8.png" alt=""></li>
                <li id="css-fontUnder-III" data-kg="true" data-fontstyle="text-decoration" data-value="underline" data-reset="none" data-line="under" class="left throughFont">
                    <img src="export/images/header/header9.png" alt=""></li>
                <li id="css-fontThrough-III" data-kg="true" data-fontstyle="text-decoration" data-value="line-through" data-reset="none" data-line="through" class="left throughFont">
                    <img src="export/images/header/header10.png" alt=""></li>
            </ul>
            <ul id="css-alignUl-III" class="mgb10 pull-left">
                <li data-align="left" class="left mgl5 clearbg">
                    <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
                <li data-align="center" class="left mgl5 clearbg">
                    <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
                <li data-align="right" class="left mgl5 clearbg">
                    <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
            </ul>
            <ul class="pull-left">
                <li class="pull-left mgl10 mgr10 mgt5">行距</li>
                <li class="pull-left">
                    <input data-ref="line-height" id="css-txtLineHeight-III" class="inputStyle inputStyle55 pad5 unitOption selectHeight numInput tabWH" data-unit="px" type="text" value="960px"/>
                </li>
                <li class="pull-left mgl5 mgr5">
                    <img class="" src="export/images/header/indent.png" alt="">
                </li>
                <li class="pull-left">
                    <input data-ref="text-indent" id="css-txtIndent-III" class="inputStyle inputStyle55 pad5 selectHeight numInput tabWH" data-unit="px" type="text" value="2"/>
                </li>
            </ul>
        </div>
    </section>
    <!--文字样式结束 css-->

    <!--超链接样式设置开始-->
    <section id="linkSection-III" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">链接</span>
        </div>
        <div class="lebody">
          <!--<div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <button id="fontActive-III" class="mgl15 factive">添加链接</button>
            </div>-->
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">颜色</span>
                <input id="linkColor-III" class="left inputStyle inputStyle80 pad5" type="text" value="#0000ff"/>
                <!--<span class="left setColor mgl5 mgr5"></span>-->
                <span><input id="linkColorPick-III" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>

                <img id="linkUnder-III" data-value="underline" class="select" src="export/images/header/header9.png" alt="">
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">悬停</span>
                <input id="hoverColor-III" class="left inputStyle inputStyle80 pad5" type="text" value="#ff0000"/>
                <!--<span class="left setColor mgl5 mgr5"></span>-->
                <span><input id="hoverColorPick-III" type="text" unselectable="on" onselectstart="return false;" style="width: 20px; height: 16px;margin-top: 17px;-webkit-user-select: none;-moz-user-select: none;"/></span>
                <img id="hoverUnder-III" data-value="underline" class="select" src="export/images/header/header9.png" alt="">
            </div>
        </div>

    </section>
    <!--超链接样式设置结束-->


    <!--位置大小開始   行间-->
    <section id="positionSection-III" class="width240 bline padb5">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">位置</span>
        </div>
        <div class="lebody">
            <table id="posAndBs-III" class="content shadow-top" align="center">
                <tbody>
                <tr class="row">
                    <td class="cell" colspan="2">
                        <div class="diy-control offset">
                            <div class="box">
                                <span class="handle top pHandler" data-ref="y" data-direct="margin-top"><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                <span class="handle right pHandler" data-ref="x" data-direct="margin-right"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                <span class="handle bottom pHandler" data-ref="y" data-direct="margin-bottom"><b>外边距</b><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                <span class="handle left pHandler" data-ref="x" data-direct="margin-left"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                <span class="label top disable-select positionData" contenteditable="true" data-direct="margin-top">0</span>
                                <span class="label right disable-select positionData" contenteditable="true" data-direct="margin-right">0</span>
                                <span class="label bottom disable-select positionData" contenteditable="true" data-direct="margin-bottom">0</span>
                                <span class="label left disable-select positionData" contenteditable="true" data-direct="margin-left">0</span>
                                <div class="padding">
                                    <span class="handle top pHandler" data-ref="y" data-direct="padding-top"><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                    <span class="handle right pHandler" data-ref="x" data-direct="padding-right"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                    <span class="handle bottom pHandler" data-ref="y" data-direct="padding-bottom"><b>内边距</b><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                    <span class="handle left pHandler" data-ref="x" data-direct="padding-left"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                    <span class="label top disable-select positionData" contenteditable="true" data-direct="padding-top">10</span>
                                    <span class="label right disable-select positionData" contenteditable="true" data-direct="padding-right">10</span>
                                    <span class="label bottom disable-select positionData" contenteditable="true" data-direct="padding-bottom">10</span>
                                    <span class="label left disable-select positionData" contenteditable="true" data-direct="padding-left">10</span>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <ul id="positionBtnUl-III" class="clearfix mgt15">
                <li style="padding-bottom: 2px !important;" class="pull-left mgl35 button-style padd3 padr10">
                    <img class="mgl5 fmgt5 conMiddle" src="export/images/sliderPanel/sliderPanel17.png"/>
                    <span class="cursor conMiddle">容器居中</span>
                </li>
                <li class="pull-left mgl15 padr10 button-style">
                    <img class="conClear" src="export/images/sliderPanel/sliderPanel16.png"/>
                    <span class="cursor conClear mgt1 ">清除</span>
                </li>
            </ul>
            <ul class="clearfix mgt20">
                <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                <li class="pull-left posRelative">
                    <!--<span class="unit1 posAbsolute"></span>-->
                    <input id="position_width-III" class="inputStyle inputStyle55 pad5 selectHeight unitOption positionwh" data-direct="width" data-ref="width" type="text" value="960"/>

                </li>
                <li class="pull-left mgl25 mgr10 mgt5">高</li>
                <li class="pull-left">
                    <input id="position_height-III" class="inputStyle inputStyle55 selectHeight unitOption pad5 positionwh" data-direct="height" data-ref="height" type="text" value="960"/>
                </li>
            </ul>
        </div>
    </section>
    <!--位置大小結束   行间-->

    <!--位置大小開始   CSS文件-->
    <section id="css-positionSection-III" class="width240 bline padb5">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">位置</span>
        </div>
        <div class="lebody">
            <table id="css-posAndBs-III" class="content shadow-top" align="center">
                <tbody>
                <tr class="row">
                    <td class="cell" colspan="2">
                        <div class="diy-control offset">
                            <div class="box">
                                <span class="handle top pHandler" data-ref="y" data-direct="margin-top"><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                <span class="handle right pHandler" data-ref="x" data-direct="margin-right"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                <span class="handle bottom pHandler" data-ref="y" data-direct="margin-bottom"><b>外边距</b><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                <span class="handle left pHandler" data-ref="x" data-direct="margin-left"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                <span class="label top disable-select positionData" contenteditable="true" data-direct="margin-top">0</span>
                                <span class="label right disable-select positionData" contenteditable="true" data-direct="margin-right">0</span>
                                <span class="label bottom disable-select positionData" contenteditable="true" data-direct="margin-bottom">0</span>
                                <span class="label left disable-select positionData" contenteditable="true" data-direct="margin-left">0</span>
                                <div class="padding">
                                    <span class="handle top pHandler" data-ref="y" data-direct="padding-top"><i><img src="export/images/posAndBs/posAndBs3.png"/></i></span>
                                    <span class="handle right pHandler" data-ref="x" data-direct="padding-right"><i><img src="export/images/posAndBs/posAndBs4.png"/></i></span>
                                    <span class="handle bottom pHandler" data-ref="y" data-direct="padding-bottom"><b>内边距</b><i><img src="export/images/posAndBs/posAndBs1.png"/></i></span>
                                    <span class="handle left pHandler" data-ref="x" data-direct="padding-left"><i><img src="export/images/posAndBs/posAndBs2.png"/></i></span>
                                    <span class="label top disable-select positionData" contenteditable="true" data-direct="padding-top">10</span>
                                    <span class="label right disable-select positionData" contenteditable="true" data-direct="padding-right">10</span>
                                    <span class="label bottom disable-select positionData" contenteditable="true" data-direct="padding-bottom">10</span>
                                    <span class="label left disable-select positionData" contenteditable="true" data-direct="padding-left">10</span>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <ul id="css-positionBtnUl-III" class="clearfix mgt15">
                <li style="padding-bottom: 2px !important;" class="pull-left mgl35 button-style padd3 padr10">
                    <img class="mgl5 fmgt5 conMiddle" src="export/images/sliderPanel/sliderPanel17.png"/>
                    <span class="cursor conMiddle">容器居中</span>
                </li>
                <li class="pull-left mgl15 padr10 button-style">
                    <img class="conClear" src="export/images/sliderPanel/sliderPanel16.png"/>
                    <span class="cursor conClear mgt1 ">清除</span>
                </li>
            </ul>
            <ul class="clearfix mgt20">
                <li class="pull-left mgl10 mgr10 mgt5">宽</li>
                <li class="pull-left posRelative">
                    <!--<span class="unit1 posAbsolute"></span>-->
                    <input id="css-position_width-III" class="inputStyle inputStyle55 pad5 selectHeight unitOption positionwh" data-direct="width" data-ref="width" type="text" value="960"/>

                </li>
                <li class="pull-left mgl25 mgr10 mgt5">高</li>
                <li class="pull-left">
                    <input id="css-position_height-III" class="inputStyle inputStyle55 selectHeight unitOption pad5 positionwh" data-direct="height" data-ref="height" type="text" value="960"/>
                </li>
            </ul>
        </div>
    </section>
    <!--位置大小結束   CSS文件-->

    <!--背景样式开始   行间-->
    <section id="bgSection-III" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">背景</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景色</span>
                <input id="inputBGColorPick-III" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="bgColorPick-III" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景图</span>
                <label id="bgFileLabel-III" class="posRelative">
                    <img id="bgPreviewImg-III" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                    <img id="bgDeleteImg-III" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                </label>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10 layoutBg-word">定位</span>
                <div class="clearfix layoutBg-pos left">
                    <ul class="clearfix layoutBg-pos-nine">
                        <li class="left btn-hint bgPosition" data-x="0%" data-y="0%">
                            <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="50%" data-y="0%">
                            <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="100%" data-y="0%">
                            <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="0%" data-y="50%">
                            <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="50%" data-y="50%"></li>
                        <li class="left btn-hint bgPosition" data-x="100%" data-y="50%">
                            <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                        <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%">
                            <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                        <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%">
                            <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                        <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%">
                            <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
                    </ul>

                </div>
                <ul>
                    <li class="mgb10">
                        <span class="mgl5 mgr5">横向</span>
                        <input id="bgX-III" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                    <li>
                        <span class="mgl5 mgr5">纵向</span>
                        <input id="bgY-III" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">重复</span>
                <ul class="repeat">
                    <li class="left mgr5 bgRepeat" data-ref="repeat">
                        <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-x">
                        <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-y">
                        <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                    <li class="left mgr18 bgRepeat" data-ref="no-repeat">
                        <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                </ul>
            </div>
        </div>

    </section>
    <!--背景样式结束   行间-->

    <!--背景样式开始   CSS文件-->
    <section id="css-bgSection-III" class="sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">背景</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景色</span>
                <input id="css-inputBGColorPick-III" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="css-bgColorPick-III" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">背景图</span>
                <label id="css-bgFileLabel-III" class="posRelative">
                    <img id="css-bgPreviewImg-III" style="max-height: 70px; max-width: 120px;" onerror="this.src='export/images/sliderPanel/sliderPanel1.png'" src="export/images/sliderPanel/sliderPanel1.png"/>
                    <img id="css-bgDeleteImg-III" class="posAbsolute cancelBtn" src="export/images/navMenu/navMenu2.png" alt=""/>
                </label>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10 layoutBg-word">定位</span>
                <div class="clearfix layoutBg-pos left">
                    <ul class="clearfix layoutBg-pos-nine">
                        <li class="left btn-hint bgPosition" data-x="0%" data-y="0%">
                            <img src="export/images/sliderPanel/sliderPanel2.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="50%" data-y="0%">
                            <img src="export/images/sliderPanel/sliderPanel3.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="100%" data-y="0%">
                            <img src="export/images/sliderPanel/sliderPanel4.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="0%" data-y="50%">
                            <img src="export/images/sliderPanel/sliderPanel5.png" alt=""/></li>
                        <li class="left btn-hint bgPosition" data-x="50%" data-y="50%"></li>
                        <li class="left btn-hint bgPosition" data-x="100%" data-y="50%">
                            <img src="export/images/sliderPanel/sliderPanel6.png" alt=""/></li>
                        <li class="left btn-hint mgl25  bgPosition" data-x="0%" data-y="100%">
                            <img src="export/images/sliderPanel/sliderPanel7.png" alt=""/></li>
                        <li class="left btn-hint  bgPosition" data-x="50%" data-y="100%">
                            <img src="export/images/sliderPanel/sliderPanel8.png" alt=""/></li>
                        <li class="left btn-hint  bgPosition" data-x="100%" data-y="100%">
                            <img src="export/images/sliderPanel/sliderPanel9.png" alt=""/></li>
                    </ul>

                </div>
                <ul>
                    <li class="mgb10">
                        <span class="mgl5 mgr5">横向</span>
                        <input id="css-bgX-III" data-unit="%" data-ref="background-position-x" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                    <li>
                        <span class="mgl5 mgr5">纵向</span>
                        <input id="css-bgY-III" data-unit="%" data-ref="background-position-y" data-unitex="e" class="inputStyle inputStyle55 pad3 unitOption bgxy" type="text" value="0%"/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">重复</span>
                <ul class="repeat">
                    <li class="left mgr5 bgRepeat" data-ref="repeat">
                        <img src="export/images/sliderPanel/sliderPanel10.png" alt="平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-x">
                        <img src="export/images/sliderPanel/sliderPanel11.png" alt="横向平铺"/></li>
                    <li class="left mgr5 bgRepeat" data-ref="repeat-y">
                        <img src="export/images/sliderPanel/sliderPanel12.png" alt="纵向平铺"/></li>
                    <li class="left mgr18 bgRepeat" data-ref="no-repeat">
                        <img src="export/images/sliderPanel/sliderPanel15.png" alt=""/></li>
                </ul>
            </div>
        </div>

    </section>
    <!--背景样式结束   CSS文件-->

    <!--边框样式开始   行间-->
    <section id="bolderSection-III" class="width240 sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">边框</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">颜色</span>
                <input id="bBolderColor-III" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="bolderColorPick-III" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="mgb10">
                <span class="mgl15 mgr6">厚度</span>
                <input id="bBolderWidth-III" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <div id="bPositionDiv-III" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">位置</span>
                <ul id="bdPUl-III" class="repeat">
                    <li data-ref="border-top" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                    </li>
                    <li data-ref="border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                    </li>
                    <li data-ref="border-left" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                    </li>
                    <li data-ref="border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                    </li>
                    <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                    </li>
                    <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                    </li>
                    <li data-ref="border" class="left mgr5 btn-hint mgt5">
                        <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">样式</span>
                <ul id="bolderStyleUl-III" class="repeat">
                    <li data-ref="solid" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                    </li>
                    <li data-ref="dashed" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                    </li>
                    <li data-ref="dotted" class="left mgr6 btn-hint">
                        <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                    </li>
                    <li data-ref="none" class="left mgr18 btn-hint">
                        <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div id="bRadiusDiv-III" class="mgb10">
                <span class="mgl15 mgr6">圆角半径</span>
                <input id="bBolderRadius-III" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <!--<div id="bWidthDiv-III" class="mgb10">
                <span class="mgl15 mgr6">宽度</span>
                <input id="bWidth-III" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div id="bWidthDiv-III" class="mgb10">
                <span class="mgl15 mgr6">宽度</span>
                <input id="bWidth-III" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div class="mgb10">
                <span class="mgl15 mgr6">不透明度</span>
            <div class="selectPos mgr15 fontWz inlineBlock">
            <form action="" method="post">
                <div class="electHeight fontWz divselect">
                <cite class="selectHeight pad5">100%</cite>
                <ul class="fontWz">
                <li><a href="javascript:;" selectid="1">80%</a></li>
                <li><a href="javascript:;" selectid="2">60%</a></li>
                <li><a href="javascript:;" selectid="3">40%</a></li>
                <li><a href="javascript:;" selectid="4">20%</a></li>
                </ul>
                </div>
                <input class="" name="" type="hidden" value=""/>
                </form>
                </div>
            </div>-->
        </div>
    </section>
    <!--边框样式结束   行间-->

    <!--边框样式开始   CSS文件-->
    <section id="css-bolderSection-III" class="width240 sidebar-panel-layout sidebar-panel-color padb5 bline">
        <div class="sidebar-panel-title padd10">
            <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
            <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
            <span class="iconArea cursor">边框</span>
        </div>
        <div class="lebody">
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">颜色</span>
                <input id="css-bBolderColor-III" name="bBolderColor" class="left inputStyle inputStyle80 pad5" type="text" value="#03a8f1"/>
                <input id="css-bolderColorPick-III" type="text" style="width: 26px; height: 26px;margin-left: 18px;border-radius: 0;">
            </div>
            <div class="mgb10">
                <span class="mgl15 mgr6">厚度</span>
                <input id="css-bBolderWidth-III" name="bBolderWidth" data-ref="borderWidth" class="inputStyle inputStyle80 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <div id="css-bPositionDiv-III" class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">位置</span>
                <ul id="css-bdPUl-III" class="repeat">
                    <li data-ref="border-top" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign12.png" alt=""/>
                    </li>
                    <li data-ref="border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign13.png" alt=""/>
                    </li>
                    <li data-ref="border-left" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign14.png" alt=""/>
                    </li>
                    <li data-ref="border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign15.png" alt=""/>
                    </li>
                    <li data-ref="border-top,border-bottom" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign16.png" alt=""/>
                    </li>
                    <li data-ref="border-left,border-right" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign17.png" alt=""/>
                    </li>
                    <li data-ref="border" class="left mgr5 btn-hint mgt5">
                        <img src="export/images/sliderPanel/textAlign18.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div class="sidebar-panel-layoutBg  vertical clearfix mgb10">
                <span class="left mgl15 mgr10">样式</span>
                <ul id="css-bolderStyleUl-III" class="repeat">
                    <li data-ref="solid" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign7.png" alt=""/>
                    </li>
                    <li data-ref="dashed" class="left mgr5 btn-hint">
                        <img src="export/images/sliderPanel/textAlign8.png" alt=""/>
                    </li>
                    <li data-ref="dotted" class="left mgr6 btn-hint">
                        <img src="export/images/sliderPanel/textAlign9.png" alt=""/>
                    </li>
                    <li data-ref="none" class="left mgr18 btn-hint">
                        <img src="export/images/sliderPanel/textAlign10.png" alt=""/>
                    </li>
                </ul>
            </div>
            <div id="css-bRadiusDiv-III" class="mgb10">
                <span class="mgl15 mgr6">圆角半径</span>
                <input id="css-bBolderRadius-III" data-ref="border-radius" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>
            <!--<div id="bWidthDiv-III" class="mgb10">
                <span class="mgl15 mgr6">宽度</span>
                <input id="bWidth-III" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div id="bWidthDiv-III" class="mgb10">
                <span class="mgl15 mgr6">宽度</span>
                <input id="bWidth-III" data-ref="width" class="inputStyle inputStyle50 pad5" data-unitex="ep" type="text" value="0px"/>
            </div>-->
            <!--<div class="mgb10">
                <span class="mgl15 mgr6">不透明度</span>
            <div class="selectPos mgr15 fontWz inlineBlock">
            <form action="" method="post">
                <div class="electHeight fontWz divselect">
                <cite class="selectHeight pad5">100%</cite>
                <ul class="fontWz">
                <li><a href="javascript:;" selectid="1">80%</a></li>
                <li><a href="javascript:;" selectid="2">60%</a></li>
                <li><a href="javascript:;" selectid="3">40%</a></li>
                <li><a href="javascript:;" selectid="4">20%</a></li>
                </ul>
                </div>
                <input class="" name="" type="hidden" value=""/>
                </form>
                </div>
            </div>-->
        </div>
    </section>
    <!--边框样式结束   CSS文件-->
</div>
<!--右侧功能栏III结束-->


<!--单位设置开始-->
<div id="unit" class="unit">
     <span class="handle1 mgb10">
        <img class="" src="export/images/sliderBar/sliderBar32.png"/>
     </span>
    <ul class="text-center bgb unit">
        <li data-ref="x">px</li>
        <li data-ref="e">em</li>
        <li data-ref="p">%</li>
    </ul>
</div>

<div id="unit_hint" class="unit">
     <span class="handle1 mgb10">
        <img class="" src="export/images/sliderBar/sliderBar32.png"/>
     </span>
</div>
<!--单位设置结束-->


<!--文字局部设置开始-->
<div id="partText" class="partText">
    <div class="partText-set">
        <div class="parttHeight width55 divselect pull-left mgr20 part-hover" id="partFamilyBox">
            <span class="parttHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">字体</span>
            <ul class="fontFa" id="partFamily">
                <li><a href="javascript:;" selectid="1" value="微软雅黑">微软雅黑</a></li>
                <li><a href="javascript:;" selectid="2" value="宋体">宋体</a></li>
                <li><a href="javascript:;" selectid="3" value="楷体">楷体</a></li>
                <li><a href="javascript:;" selectid="4" value="隶书">隶书</a></li>
                <li><a href="javascript:;" selectid="5" value="华文新魏">华文新魏</a></li>
                <li><a href="javascript:;" selectid="6" value="新宋体">新宋体</a></li>
                <li><a href="javascript:;" selectid="7" value="幼圆">幼圆</a></li>
                <li><a href="javascript:;" selectid="8" value="仿宋">仿宋</a></li>
                <li><a href="javascript:;" selectid="9" value="黑体">黑体</a></li>
                <li><a href="javascript:;" selectid="10" value="Arial">Arial</a></li>
                <li><a href="javascript:;" selectid="11" value="courier">courier</a></li>
                <li><a href="javascript:;" selectid="12" value="forte">forte</a></li>
                <li><a href="javascript:;" selectid="13" value="elephant">elephant</a></li>
                <li><a href="javascript:;" selectid="14" value="fantasy">fantasy</a></li>
            </ul>
        </div>
        <div id="partColorPick" class="parttHeight width55 divselect pull-left cursor mgr20 part-hover" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">
            <span class="parttHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">字色</span>
        </div>
        <div class="parttHeight width55 divselect pull-left mgr20 part-hover" id="partSize">
            <span class="parttHeight pad5" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">字号</span>
            <ul class="fontFa hide1 fontSz ">
                <li><a href="javascript:;" selectid="1" value="12">12px</a></li>
                <li><a href="javascript:;" selectid="13" value="13">13px</a></li>
                <li><a href="javascript:;" selectid="2" value="14">14px</a></li>
                <li><a href="javascript:;" selectid="14" value="15">15px</a></li>
                <li><a href="javascript:;" selectid="3" value="16">16px</a></li>
                <li><a href="javascript:;" selectid="4" value="18">18px</a></li>
                <li><a href="javascript:;" selectid="5" value="20">20px</a></li>
                <li><a href="javascript:;" selectid="6" value="21">21px</a></li>
                <li><a href="javascript:;" selectid="7" value="24">24px</a></li>
                <li><a href="javascript:;" selectid="8" value="29">29px</a></li>
                <li><a href="javascript:;" selectid="9" value="32">32px</a></li>
                <li><a href="javascript:;" selectid="10" value="34">34px</a></li>
                <li><a href="javascript:;" selectid="11" value="48">48px</a></li>
                <li><a href="javascript:;" selectid="12" value="56">56px</a></li>
            </ul>
        </div>
        <!--<em class="left-line"></em>
        <em class="right-line"></em>
        <ul id="partSz" class="mgb10 pull-left">
            <li id="partAdd" class="left  mgl10 part-hover">
            <img src="export/images/header/add.png" alt=""></li>
            <li id="partMinus" class="left mgl10 mgr10 part-hover">
            <img src="export/images/header/minus.png" alt=""></li>
        </ul>-->
        <em class="left-line"></em>
        <em class="right-line"></em>
        <ul id="partSt" class=" pull-left">
            <li id="partBold" title="加粗" data-kg="true" data-fontstyle="font-weight" data-value="700" data-reset="200" class="left  mgl20 part-hover">
                <img src="export/images/header/header7.png" alt=""></li>
            <li id="partItalic" title="斜体" data-kg="true" data-fontstyle="font-style" data-value="italic" data-reset="normal" class="left mgl20 part-hover">
                <img src="export/images/header/header8.png" alt=""></li>
            <li id="partUnder" title="下划线" data-kg="true" data-fontstyle="text-decoration" data-value="underline" data-reset="none" data-line="under" class="left throughFont mgl20 part-hover">
                <img src="export/images/header/header9.png" alt=""></li>
            <li id="partThrough" title="删除线" data-kg="true" data-fontstyle="text-decoration" data-value="line-through" data-reset="none" data-line="through" class="left throughFont mgl20 mgr20 part-hover">
                <img src="export/images/header/header10.png" alt=""></li>
        </ul>
        <em class="left-line"></em>
        <em class="right-line"></em>
        <ul id="partAlign" class="pull-left">
            <li id="partLeft" title="左对齐" data-align="JustifyLeft" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;" class="left cursor  mgl20 part-hover">
                <img src="export/images/sliderPanel/textAlign1.png" alt=""/></li>
            <li id="partCenter" title="居中对齐" data-align="JustifyCenter" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;" class="left cursor mgl20 part-hover">
                <img src="export/images/sliderPanel/textAlign2.png" alt=""/></li>
            <li id="partRight" title="右对齐" data-align="JustifyRight" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;" class="left cursor mgl20 mgr20 part-hover">
                <img src="export/images/sliderPanel/textAlign3.png" alt=""/></li>
        </ul>
        <em class="left-line"></em>
        <em class="right-line"></em>
        <div class="pull-left posRelative">
            <div id="partLink" title="添加链接" class="pull-left cursor mgl20 part-hover" unselectable="on" onselectstart="return false;" style="-webkit-user-select: none;-moz-user-select: none;">
                <img src="export/images/header/partLink.png" alt=""></div>
            <div class="partlink-box hide1">
                <input id="partLinkUrl" style="border: solid 1px #C3B8B8;" class=" mgr10" type="text" value="http://www.baidu.com">
                <label for="openNewWindowBtn"><input type="checkbox" id="openNewWindowBtn" checked/>新窗口打开</label>
                <input id="partLinkBtn" class="" style="width: 70px;" type="button" value="确定">
            </div>
        </div>
        <div id="partunlink" title="取消链接" class="pull-left cursor mgl20 mgr20 part-hover" unselectable="on" onselectstart="return false;" style=" -webkit-user-select: none;-moz-user-select: none;">
            <img src="export/images/header/partUnlink.png" alt=""></div>
        <em class="left-line"></em>
        <em class="right-line"></em>
        <div id="formatBlock" title="清除格式" class="pull-left cursor mgl20 part-hover" unselectable="on" onselectstart="return false;" style=" -webkit-user-select: none;-moz-user-select: none;">
            <img src="export/images/header/clearStyle.png" alt=""></div>

    </div>
</div>
<!--文字局部设置结束-->


<!--上传图片悬浮框设置开始-->
<div id="picUploadDialog" class="picUpload posAbsolute" style="display:none;">
    <ul class="tab_menu font14">
        <li class="pull-left mgl20 width110 cursor text-center mgt10 selected">上传图片</li>
        <li class="pull-left width110 cursor text-center mgt10">远程图片</li>
        <img id="pudClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png"/>
    </ul>
    <div class="tab_box tab-box-style">
        <div class="text-center local-pic">
            <li class="fmgt25"><img src="export/images/navMenu/navMenu7.png"/></li>
            <li class="mgt40"><input class="factive height45 width205" type="button" value="从本地选择图片上传"/></li>
            <li class="mgt10 font14">将图片拖动到此区域快速上传</li>
        </div>
        <div class="hide1 pic-url">
            <div class="font14">
                请输入图片的链接地址，格式仅限 .JPG, .JPEG, .PNG, .GIF
            </div>
            <div class=" mgt10">
                <input id="picUrlText" class="pic-style mgr10" type="text" value="http://ww2.sinaimg.cn/cmw218/e71fa9bejw1eznev0h7q5j216o1s0b29.jpg"/>
                <input id="picUrlBtn" class="factive pic-style-btn" type="button" value="确定"/>
            </div>

        </div>
    </div>
</div>
<!--上传图片悬浮框设置结束-->
<!----------------------------------------------------悬浮框---------------------------------------------->


<!-- 模态框（Modal） -->
<div class="modal fade" id="LEDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <iframe class="posAbsolute" id="dialogFrame" src="" width="100%" height="610" frameborder="0px"></iframe>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- 模态框（Modal） -->
<div class="modal fade" id="LEDialog-II" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <iframe class="posAbsolute" id="dialogFrame-II" src="" width="100%" height="610" frameborder="0px"></iframe>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- 模态框（Modal） -->
<div class="modal fade" id="linkDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">视频地址</li>
                    <img id="videoDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="videoUrlText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" value="">
                    <input id="videoUrlBtn" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="linkTextDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">超链接地址</li>
                    <img id="TextDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="TextUrlText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" value="http://www.baidu.com">
                    <label for="openNewWindowBtn2"><input type="checkbox" id="openNewWindowBtn2" checked/>新窗口打开</label>
                    <input id="TextUrlBtn" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="linkAudioDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">音频地址</li>
                    <img id="audioDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="audioUrlText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" value="http://player.video.qiyi.com/3307ad865d9efe89ac15a8a9afcb99d1.swf">
                    <input id="audioUrlBtn" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="linkFlashDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">Flash地址</li>
                    <img id="flashDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="flashUrlText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" value="http://player.hz.letv.com/hzplayer.swf/typeFrom=zw_baidushort/v_list=52/vid=25482653&camera=0&ark=100">
                    <input id="flashUrlBtn" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="frameDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">Frame地址</li>
                    <img id="frameDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="frameUrlText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" value="http://www.baidu.com">
                    <input id="frameUrlConfirm" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="picLinkDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">超链接地址</li>
                    <img id="picLinkClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="picLinkUrlText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" value="http://www.baidu.com">
                    <label for="openNewWindowBtn1"><input type="checkbox" id="openNewWindowBtn1" checked/>新窗口打开</label>
                    <input id="picLinkUrlConfirm" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<div class="modal fade" id="navDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl" style="height:234px">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">导航菜单编辑</li>
                    <img id="navDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt40">
                    <input id="navNameText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" placeholder="菜单名称" value="http://www.baidu.com">
                    <input id="navLinkText" style="border: solid 1px #C3B8B8;" class="pic-style mgt10 mgr10" type="text" placeholder="http://" value="http://www.baidu.com">
                    <input id="navConfirmBtn" class="mgt15 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="saveDialog" tabindex="-1" role="dialog"
    aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 450px;">
        <div class="modal-content">
            <div class="videoUrl">
                <ul class="font14">
                    <li class="pull-left mgt5 width110 cursor text-center selected">请输入项目名称</li>
                    <img id="saveDivClose" class="btn-hide pull-right mgr20 mgt15" src="export/images/navMenu/navMenu1.png">
                </ul>
                <div class="text-center mgt10">
                    <input id="saveNameText" style="border: solid 1px #C3B8B8;" class="pic-style mgr10" type="text" placeholder="项目名称" value="">
                   <!-- <div class="control-group">-->
                        <div class="controls input-append date form_date mgt10" data-date="" data-date-format="yyyy-mm-dd" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd" >
                            <label class="control-label"style="margin-right:42px;">更新截止日期:</label>
                            <span class="add-on" title="为避免重复更新和发布过期的专题稿，请设置一个截止日期!"><i class="icon-th"></i></span>
                            <input size="16" type="text" value="" id="saveNameDate" readonly style="width:250px;">
                            <span class="add-on"><i class="icon-remove"></i></span>
                        </div>
                        <input type="hidden" id="dtp_input2" value="" />
                   <!-- </div>-->
                    <input id="saveConfirmBtn" class="mgt5 pull-right factive pic-style-btn" style="width: 70px; margin-right: 35px;" type="button" value="确定">
                </div>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!----------------------------------------------------标签栏模板----------------------------------------->

<!--标签模板开始-->
<div id="modelGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right: -250px;z-index:101">
    <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>
</div>
<div id="modelDiv" class="setBarHeightModel bline padb5" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: hidden;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
    <!--<div id="modelGoBack" class="text-center" style="width: 84%;cursor: pointer; margin:10px auto 10px auto; background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>-->

    <ul data-ref="tabmanage_style1" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor btn-act act">标签1</li>
        <li class="pull-left menu-btn clo2d cursor bgf btn-act">标签2</li>
        <li class="pull-left menu-btn clo2d cursor bgf btn-act">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style2" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn bg3d3d3d cursor btn-cur activ">标签1</li>
        <li class="pull-left menu-btn bg3d3d3d cursor btn-cur">标签2</li>
        <li class="pull-left menu-btn bg3d3d3d cursor btn-cur">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style3" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn clo555 cursor bgf btn-act bordtom">标签1</li>
        <li class="pull-left menu-btn clo69d6 cursor bgf btn-act">标签2</li>
        <li class="pull-left menu-btn clo69d6 cursor bgf btn-act">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style4" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn bgf bordertop3 cloff8400 cursor btn-act">标签1</li>
        <li class="pull-left menu-btn clo2d cursor bgd btn-act">标签2</li>
        <li class="pull-left menu-btn clo2d cursor bgd btn-act">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style5" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn bg337ab7 cursor btn-act">标签1</li>
        <li class="pull-left menu-btn clo2d cursor col337ab7 bgf btn-act">标签2</li>
        <li class="pull-left menu-btn clo2d cursor col337ab7 bgf btn-act">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style6" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor bgBlack btn-act bor_bottom6">标签1</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">标签2</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style7" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor bg353b41 btn-act">标签1</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">标签2</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">标签3</li>
    </ul>
    <ul data-ref="tabmanage_style8" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn clo555 cursor bordertop3 bgf btn-act colff6600">标签1</li>
        <li class="pull-left menu-btn clo555 cursor bgffefe5 btn-act">标签2</li>
        <li class="pull-left menu-btn clo555 cursor bgffefe5 btn-act">标签3</li>
    </ul>

    <ul data-ref="tabmanage_style10" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor bgff6600 btn-act">标签1</li>
        <li class="pull-left menu-btn clo2d cursor bgff944c colorF btn-act">标签2</li>
        <li class="pull-left menu-btn clo2d cursor bgff944c colorF btn-act">标签3</li>
    </ul>
</div>
<!--标签模板结束-->
<!--主导航模板开始-->
<div id="mainModelGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right: -250px;z-index:101">
    <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>
</div>
<div id="mainModelDiv" class="setBarHeightModel bline padb5" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: hidden;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
    <!--<div id="mainModelGoBack" class="text-center" style="width: 84%;cursor: pointer; margin:10px auto 10px auto; background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>-->

    <ul data-ref="mainmanage_style1" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn clo2d cursor btn-act act">首页</li>
        <li class="pull-left menu-btn clo2d cursor bgf btn-act">新闻</li>
        <li class="pull-left menu-btn clo2d cursor bgf btn-act">体育</li>
    </ul>
    <ul data-ref="mainmanage_style2" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn bg3d3d3d cursor btn-cur col2db">首页</li>
        <li class="pull-left menu-btn bg3d3d3d cursor btn-cur">新闻</li>
        <li class="pull-left menu-btn bg3d3d3d cursor btn-cur">体育</li>
    </ul>
    <ul data-ref="mainmanage_style3" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn clo555 cursor bgf">首页</li>
        <li class="pull-left menu-btn clo69d6 cursor bgf">新闻</li>
        <li class="pull-left menu-btn clo69d6 cursor bgf">体育</li>
    </ul>
    <ul data-ref="mainmanage_style4" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor bgf bordertop3 cloff8400 btn-act">首页</li>
        <li class="pull-left menu-btn clo2d cursor bgd btn-act">新闻</li>
        <li class="pull-left menu-btn clo2d cursor bgd btn-act">体育</li>
    </ul>
    <ul data-ref="mainmanage_style5" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn bg337ab7 cursor btn-act">首页</li>
        <li class="pull-left menu-btn clo2d cursor col337ab7 bgf btn-act">新闻</li>
        <li class="pull-left menu-btn clo2d cursor col337ab7 bgf btn-act">体育</li>
    </ul>
    <ul data-ref="mainmanage_style6" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor bgBlack btn-act bor_bottom6">首页</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">新闻</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">体育</li>
    </ul>
    <ul data-ref="mainmanage_style7" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="pull-left menu-btn cursor bg353b41 btn-act">首页</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">新闻</li>
        <li class="pull-left menu-btn clo2d cursor bgBlack colorF btn-act">体育</li>
    </ul>
    <ul data-ref="mainmanage_style8" class="le-nav-ul navmodel nav nav-pills mainmanage_style8 mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li role="presentation" class="active">
            <a href="#">
                <div class="navStyle" style="min-width:10px;white-space: nowrap;vertical-align: middle;display: table-cell;text-align: center;">
                    首页
                </div>
            </a>
        </li>
        <li role="presentation">
            <a href="#">
                <div class="navStyle" style="min-width:10px;white-space: nowrap;vertical-align: middle;display: table-cell;text-align: center;">
                    新闻
                </div>
            </a>
        </li>
        <li role="presentation">
            <a href="#">
                <div class="navStyle" style="min-width:10px;white-space: nowrap;vertical-align: middle;display: table-cell;text-align: center;">
                    体育
                </div>
            </a>
        </li>
    </ul>
</div>
<!--主导航模板结束-->
<!--子导航模板开始-->
<div id="subModelGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right: -250px;z-index:101">
    <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>
</div>
<div id="subModelDiv" class="setBarHeightModel bline padb5" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: hidden;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
    <!--<div id="subModelGoBack" class="text-center" style="width: 84%;cursor: pointer; margin:10px auto 10px auto; background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>-->

    <ul data-ref="submanage_style1" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="submenu-btn cursor btn-act act">首页</li>
        <li class="submenu-btn clo2d  cursor bgf btn-act">新闻</li>
        <li class="submenu-btn clo2d  cursor bgf btn-act">体育</li>
    </ul>
    <ul data-ref="submanage_style2" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="submenu-btn  bg3d3d3d cursor btn-cur col2db">首页</li>
        <li class="submenu-btn  bg3d3d3d cursor btn-cur">新闻</li>
        <li class="submenu-btn  bg3d3d3d cursor btn-cur">体育</li>
    </ul>
    <ul data-ref="submanage_style3" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="submenu-btn clo555 cursor bgf">首页</li>
        <li class="submenu-btn clo69d6 cursor bgf">新闻</li>
        <li class="submenu-btn clo69d6 cursor bgf">体育</li>
    </ul>
    <ul data-ref="submanage_style4" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle">
        <li class="submenu-btn cloff8400 cursor bgf bordertop3 btn-act">首页</li>
        <li class="submenu-btn clo2d cursor bgd btn-act">新闻</li>
        <li class="submenu-btn clo2d cursor bgd btn-act">体育</li>
    </ul>
</div>
<!--子导航模板结束-->
<!--列表模板开始-->
<div id="listModelGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right: -250px;z-index:101">
    <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>
</div>
<div id="listModelDiv" class="setBarHeightModel bline padb5 listModelDiv" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: auto;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
    <!--<div id="listModelGoBack" class="text-center" style="width: 84%;cursor: pointer; margin:10px auto 10px auto; background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>-->
    <ul data-ref="listmanage_style1" class="clearfix mgt10 width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor model-list2-item">
            <span class="pad6 displayinline font16">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor model-list2-item">
            <span class="pad6 displayinline font16">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor model-list2-item">
            <span class="pad6 displayinline font16">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style11" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor model-list2-item">
            <span class="pad6 displayinline font12">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor model-list2-item">
            <span class="pad6 displayinline font12">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor model-list2-item">
            <span class="pad6 displayinline font12">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
    </ul>
    <!--<ul data-ref="listmanage_style2" class="clearfix mgt10 displayStyle" data-level="top">
        <li class="mgl20 cursor clo717171 bgf model-list-item"><span>文章标题</span><span class="badge pull-right">10-29 10:00</span>
        </li>
        <li class="mgl20 cursor clo717171 bgf model-list-item"><span>文章标题</span><span class="badge pull-right">10-29 10:00</span>
        </li>
        <li class="mgl20 cursor clo717171 bgf model-list-item"><span>文章标题</span><span class="badge pull-right">10-29 10:00</span>
        </li>
    </ul>-->
    <ul data-ref="listmanage_style3" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style13" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span><span class="colc8c8c8 pull-right pad6 displayinline font12">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style4" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor model-list4-item">
            <em class="itemList">1</em><span class="clo717171 font16">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemList">2</em><span class="clo717171 font16">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemList">3</em><span class="clo717171 font16">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style14" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor model-list4-item">
            <em class="itemList">1</em><span class="clo717171">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemList">2</em><span class="clo717171">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemList">3</em><span class="clo717171">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style5" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor model-list4-item">
            <em class="itemListImg"></em><span class="clo717171 font16">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemListImg"></em><span class="clo717171 font16">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemListImg"></em><span class="clo717171 font16">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style15" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor model-list4-item">
            <em class="itemListImg"></em><span class="clo717171">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemListImg"></em><span class="clo717171">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
        <li class="cursor model-list4-item">
            <em class="itemListImg"></em><span class="clo717171">文章标题</span><span class="colc8c8c8 pull-right">10-29 10:00</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style6" class="clearfix mgt10 width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font16">文章标题</li>
                <li class="model-list6-item colc8c8c8">文章摘要</li>
                <li class="model-list6-item colc8c8c8">文章来源</li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42">
                <li class="model-list6-item clo717171 font16">文章标题</li>
                <li class="model-list6-item colc8c8c8">文章摘要</li>
                <li class="model-list6-item colc8c8c8">文章来源</li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style7" class="clearfix mgt10  width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH26 bdBtma6">
                <li class="model-list8-item clo717171 fmgl10 font14">文章标题</li>
                <li class="model-list8-item colc8c8c8 fmgl10">文章摘要</li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH26">
                <li class="model-list8-item clo717171 fmgl10 font14">文章标题</li>
                <li class="model-list8-item colc8c8c8 fmgl10">文章摘要</li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style8" class="clearfix mgt10  width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH26 bdBtma6">
                <li class="model-list8-item clo717171 fmgl10 font14">文章标题</li>
                <li class="model-list8-item clofff fmgl10">文章摘要</li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH26">
                <li class="model-list8-item clo717171 fmgl10">文章标题</li>
                <li class="model-list8-item clofff fmgl10">文章摘要</li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style18" class="clearfix mgt10  width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor ">
            <ul class="padl70 bgf inlineBlock listBgImgH25 bdBtma6">
                <li class="model-list9-item clo717171 fmgl20">文章标题</li>
                <li class="model-list9-item clofff fmgl20">文章摘要</li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH25">
                <li class="model-list9-item clo717171 fmgl20">文章标题</li>
                <li class="model-list9-item clofff fmgl20">文章摘要</li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style9" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline padlnone font16">文章标题</span><span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章摘要内容</span>
        </li>
        <li class="cursor padl6 model-list2-item liststyle">
            <span class="pad6 displayinline padlnone font16">文章标题</span><span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章摘要内容</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style19" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span><span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章摘要内容</span>
        </li>
        <li class="cursor padl6 model-list2-item liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span><span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章摘要内容</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style10" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span>
            <span class="colc8c8c8 pad6 padl12 padtnone padbnone displayblock font12">文章摘要内容</span>
            <span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章来源</span>
        </li>
        <li class="cursor padl6 model-list2-item liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span>
            <span class="colc8c8c8 pad6 padl12 padtnone padbnone displayblock font12">文章摘要内容</span>
            <span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章来源</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style20" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="top">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span>
            <span class="colc8c8c8 pad6 padl12 padtnone padbnone displayblock font12">文章摘要内容</span>
            <span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章来源</span>
        </li>
        <li class="cursor padl6 model-list2-item liststyle">
            <span class="pad6 displayinline font12 padlnone">文章标题</span>
            <span class="colc8c8c8 pad6 padl12 padtnone padbnone displayblock font12">文章摘要内容</span>
            <span class="colc8c8c8 pad6 padl12 padtnone displayblock font12">文章来源</span>
        </li>
    </ul>

    <ul data-ref="listmanage_style21" class="clearfix mgt10  width205 mgl20 displayStyle listGroupModel" data-level="bottom">
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span>
            <span class="colc8c8c8 pad6 padl12 padtnone padbnone displayblock font12">文章摘要内容</span>
            <span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-29 10:00</span>
            <span class="colc8c8c8 pull-right">标签</span>
        </li>
        <li class="cursor padl6 model-list2-item bdBtma6 liststyle">
            <span class="pad6 displayinline font16 padlnone">文章标题</span>
            <span class="colc8c8c8 pad6 padl12 padtnone padbnone displayblock font12">文章摘要内容</span>
            <span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-29 10:00</span>
            <span class="colc8c8c8 pull-right">标签</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style22" class="clearfix mgt10  width205 mgl20 displayStyle listMediaModel" data-level="bottom">
        <li class="cursor bgf inlineBlock listBgImgH26 bdBtma6 liststyle">
            <ul class="padl70">
                <li class="model-list8-item clo717171 fmgl10 font14">文章标题</li>
                <li class="model-list8-item colc8c8c8 fmgl10">文章摘要</li>
            </ul>
            <span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-29 10:00</span>
            <span class="colc8c8c8 pull-right">标签</span>
        </li>
        <li class="cursor bgf inlineBlock listBgImgH26 bdBtma6 liststyle">
            <ul class="padl70">
                <li class="model-list8-item clo717171 fmgl10 font14">文章标题</li>
                <li class="model-list8-item colc8c8c8 fmgl10">文章摘要</li>
            </ul>
            <span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-29 10:00</span>
            <span class="colc8c8c8 pull-right">标签</span>
        </li>
    </ul>
    <ul data-ref="listmanage_style23" class="clearfix mgt10 width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font16">文章标题</li>
                <li class="model-list6-item colc8c8c8">文章摘要</li>
                <li class="model-list6-item colc8c8c8"><span class="colc8c8c8 padtnone font12">来源</span><span class="colc8c8c8">10-29 10:00</span>
                <span class="colc8c8c8 pull-right">标签</span></li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font16">文章标题</li>
                <li class="model-list6-item colc8c8c8">文章摘要</li>
                <li class="model-list6-item colc8c8c8"><span class="colc8c8c8 padtnone font12">来源</span><span class="colc8c8c8">10-29 10:00</span>
                <span class="colc8c8c8 pull-right">标签</span></li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style24" class="clearfix mgt10  width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor ">
            <ul class="padl70 bgf inlineBlock listBgImgH25 bdBtma6">
                <li class="model-list9-item clo717171 fmgl20">文章标题</li>
                <li class="model-list9-item clofff fmgl20"><span class="colc8c8c8 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span></li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH25">
                <li class="model-list9-item clo717171 fmgl20">文章标题</li>
                <li class="model-list9-item clofff fmgl20"><span class="colc8c8c8 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span></li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style25" class="clearfix mgt10 width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
                <li class="model-list6-item colc8c8c8"><span class="colc8c8c8 padtnone font12">10-20 10:00</span>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
                <li class="model-list6-item colc8c8c8"><span class="colc8c8c8 padtnone font12">10-20 10:00</span>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style26" class="clearfix mgt10 width205 mgl20 displayStyle listMediaModel" data-level="middle">
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
                <li class="model-list6-item colc8c8c8"><span class="colc8c8c8 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span></li>
            </ul>
        </li>
        <li class="cursor">
            <ul class="padl70 bgf inlineBlock listBgImgH42 bdBtma6">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
                <li class="model-list6-item colc8c8c8"><span class="colc8c8c8 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span></li>
            </ul>
        </li>
    </ul>
    <ul data-ref="listmanage_style27" class="clearfix mgt10 width205 mgl20 displayStyle listMediaModel" data-level="bottom">
        <li class="cursor bgf inlineBlock listBgImgH42 bdBtma6 liststyle">
            <ul class="padl70">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
            </ul>
            <p style="margin-top:5px;">
                <span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span>
            </p>
        </li>
        <li class="cursor bgf inlineBlock listBgImgH42 bdBtma6 liststyle">
            <ul class="padl70">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
            </ul>
            <p style="margin-top:5px;">
                <span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span>
            </p>
        </li>
    </ul>
    <ul data-ref="listmanage_style28" class="clearfix mgt10 width205 mgl20 displayStyle listMediaModel" data-level="bottom">
        <li class="cursor bgf inlineBlock listBgImgH42 bdBtma6 liststyle">
            <ul class="padl70">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
            </ul>
            <p style="margin-top:5px;margin-bottom:5px;"><span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span>
            <span class="colc8c8c8 pad6">标签：标签1</span></p>
        </li>
        <li class="cursor bgf inlineBlock listBgImgH42 bdBtma6 liststyle">
            <ul class="padl70">
                <li class="model-list6-item clo717171 font14">文章标题</li>
                <li class="model-list6-item colc8c8c8 font12">文章摘要</li>
            </ul>
            <p style="margin-top:5px;margin-bottom:5px;"><span class="colc8c8c8 pad6 padl12 padtnone font12">来源</span><span class="colc8c8c8 pad6">10-20 10:00</span>
            <span class="colc8c8c8 pad6">标签：标签1</span></p>
        </li>
    </ul>
</div>
<!--列表模板结束-->
<!--轮播图模板开始-->
<div id="carModelGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right: -250px;z-index:101">
    <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>
</div>
<div id="carModelDiv" class="setBarHeightModel bline padb5" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: hidden;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
    <!--div id="carModelGoBack" class="text-center" style="width: 84%;cursor: pointer; margin:10px auto 10px auto; background-color: #303030;border: 1px solid #242424;line-height: 24px;">
        <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
        <span class="mgl10 text-left  cursor">返回</span>
    </div>-->
    <ul data-ref="carmanage_style1" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOl-model bottom10">
                    <li class="width10 height10 bdRadius10 bgf borderf"></li>
                    <li class="width10 height10 bdRadius10 bgfnone borderf"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitle">
                            <h5>标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style2" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOl-model bottom10">
                    <li class="width30 height10 bdRadius0 bgf borderf"></li>
                    <li class="width30 height10 bdRadius0 bgfnone borderf"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitle">
                            <h5>标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style3" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative  width205">
                <ol class="carouselOlBR">
                    <li class="bdRadius10 width10 height10 inlineBlock bgf"></li>
                    <li class="bdRadius10 width10 height10 inlineBlock bgfnone"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleBtm">
                            <h5>标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style4" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOlBR">
                    <li class="bdRadius0 pd04 bg0087cb">1</li>
                    <li class="bdRadius0 pd04 bgfnone">2</li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleBtm">
                            <h5 class="pull-left pad5">标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style5" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOl-model bottom40">
                    <li class="width6 height6 bdRadius0 bgy bordery"></li>
                    <li class="width6 height6 bdRadius0 bgf borderf"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleBtm">
                            <h5>标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top70 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top70 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style6" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOlBtm">
                    <li class="width30 height10 bdRadius0 bgy bordery"></li>
                    <li class="width30 height10 bdRadius0 bgg borderg"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleCenter">
                            <h5>标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style7" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOlCenter">
                    <li class="bdRadius10 width10 height10 inlineBlock bgf"></li>
                    <li class="bdRadius10 width10 height10 inlineBlock bgfnone"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleOut">
                            <h5>标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style8" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOlCenter">
                    <li class="bdRadius10 width10 height10 inlineBlock bgf"></li>
                    <li class="bdRadius10 width10 height10 inlineBlock bgfnone"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleOut">
                            <h5 class="hide">标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
    <ul data-ref="carmanage_style9" class="clearfix mgt10 width205 mgl20 displayStyle">
        <li>
            <div class="posRelative width205">
                <ol class="carouselOlBR">
                    <li class="bdRadius10 width10 height10 inlineBlock bgf"></li>
                    <li class="bdRadius10 width10 height10 inlineBlock bgfnone"></li>
                </ol>
                <div class="carouselBox">
                    <div>
                        <img alt="" width="100%" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" style="height: 80px;">
                        <div class="carouselTitleOut">
                            <h5 class="hide">标题</h5>
                            <p></p>
                        </div>
                    </div>
                </div>
                <a class="left0 carouselBtn">
                    <span class="glyphicon carouselBtn-left top50 zindex12"></span>
                </a>
                <a class="right0 carouselBtn">
                    <span class="glyphicon carouselBtn-right top50 zindex12"></span>
                </a>
            </div>
        </li>
    </ul>
</div>
<!--轮播图模板结束-->
<!--折线图模板,柱形图模板,饼图模板开始-->
    <div id="chartPicGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right:-250px;z-index:101">
        <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
            <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
            <span class="mgl10 text-left  cursor">返回</span>
        </div>
    </div>
    <div id="chartPicDiv" class="setBarHeightModel bline padb5" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: hidden;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
        <ul data-ref="chartPic_style1" class="clearfix mgt10 mgl20 mgr20 displayStyle lineList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar501.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style2" class="clearfix mgt10 mgl20 mgr20 displayStyle lineList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar502.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style3" class="clearfix mgt10 mgl20 mgr20 displayStyle barList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar503.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style4" class="clearfix mgt10 mgl20 mgr20 displayStyle barList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar504.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style5" class="clearfix mgt10 mgl20 mgr20 displayStyle barList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar505.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style6" class="clearfix mgt10 mgl20 mgr20 displayStyle barList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar506.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style7" class="clearfix mgt10 mgl20 mgr20 displayStyle pieList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar507.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style8" class="clearfix mgt10 mgl20 mgr20 displayStyle pieList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar508.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style9" class="clearfix mgt10 mgl20 mgr20 displayStyle wordCloudList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar509.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style10" class="clearfix mgt10 mgl20 mgr20 displayStyle wordCloudList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar510.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style11" class="clearfix mgt10 mgl20 mgr20 displayStyle wordCloudList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar511.png" class="inputStyle198">
            </li>
        </ul>
        <ul data-ref="chartPic_style12" class="clearfix mgt10 mgl20 mgr20 displayStyle wordCloudList">
            <li class="pull-left cursor bordtom">
                <img src="export/images/sliderBar/sliderBar512.png" class="inputStyle198">
            </li>
        </ul>
    </div>
<!--折线图模板,柱形图模板结束-->
<!-- 选择配色模板开始 -->
    <div id="chartColorGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right:-250px;z-index:101">
        <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
            <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
            <span class="mgl10 text-left  cursor">返回</span>
        </div>
    </div>
    <div id="chartColorDiv" class="setBarHeightModel bline padb5" style="background: #3d3d3d;width: 240px;height: 655px;min-height:10px;color: #fff;overflow: hidden;position:fixed;top: 96px;right: -250px; z-index: 100;opacity:1;display: block;">
        <ul data-ref="chartColor_style1" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle"  data-textcolor ="#333333" data-subtextcolor="#aaaaaa" data-eithercolor="#333333">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-c23531"></div>
                <div class="pull-left width20 col-2f4554"></div>
                <div class="pull-left width20 col-61a0a8"></div>
                <div class="pull-left width20 col-d48265"></div>
                <div class="pull-left width20 col-91c7ae"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style2" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#516b91" data-subtextcolor="#93b7e3" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-D87C7C"></div>
                <div class="pull-left width20 col-919E8B"></div>
                <div class="pull-left width20 col-D7AB82"></div>
                <div class="pull-left width20 col-6E7074"></div>
                <div class="pull-left width20 col-61A0A8"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style3" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle " data-textcolor ="#eeeeee" data-subtextcolor="#aaaaaa" data-eithercolor="#eeeeee">
            <li class="pull-left bg3d3d3d cursor width190 clearfix">
                <div class="pull-left width20 col-DD6B66"></div>
                <div class="pull-left width20 col-759AA0"></div>
                <div class="pull-left width20 col-E69D87"></div>
                <div class="pull-left width20 col-8DC1A9"></div>
                <div class="pull-left width20 col-EA7E53"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style4" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#893448" data-subtextcolor="#d95850" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-516B91"></div>
                <div class="pull-left width20 col-59C4E6"></div>
                <div class="pull-left width20 col-EDAFDA"></div>
                <div class="pull-left width20 col-93B7E3"></div>
                <div class="pull-left width20 col-A5E7F0"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style5" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#666666" data-subtextcolor="#999999" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-893448"></div>
                <div class="pull-left width20 col-D95850"></div>
                <div class="pull-left width20 col-EB8146"></div>
                <div class="pull-left width20 col-FFB248"></div>
                <div class="pull-left width20 col-F2D643"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style6" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#27727b" data-subtextcolor="#aaaaaa" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-4EA397"></div>
                <div class="pull-left width20 col-22C3AA"></div>
                <div class="pull-left width20 col-7BD9A5"></div>
                <div class="pull-left width20 col-D0648A"></div>
                <div class="pull-left width20 col-F58DB2"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style7" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#008acd" data-subtextcolor="#aaaaaa" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-3FB1E3"></div>
                <div class="pull-left width20 col-6BE6C1"></div>
                <div class="pull-left width20 col-626C91"></div>
                <div class="pull-left width20 col-A0A7E6"></div>
                <div class="pull-left width20 col-C4EBAD"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style8" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle " data-textcolor ="#ffaf51" data-subtextcolor="#eeeeee" data-eithercolor="#999999">
            <li class="pull-left cursor bg3d3d3d width190 clearfix">
                <div class="pull-left width20 col-FC97AF"></div>
                <div class="pull-left width20 col-87F7CF"></div>
                <div class="pull-left width20 col-F7F494"></div>
                <div class="pull-left width20 col-72CCFF"></div>
                <div class="pull-left width20 col-F7C5A0"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style9" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#516b91" data-subtextcolor="#aaaaaa" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-C1232B"></div>
                <div class="pull-left width20 col-27727B"></div>
                <div class="pull-left width20 col-FCCE10"></div>
                <div class="pull-left width20 col-E87C25"></div>
                <div class="pull-left width20 col-B5C334"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style10" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#333333" data-subtextcolor="#aaaaaa" data-eithercolor="#333333">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-2EC7C9"></div>
                <div class="pull-left width20 col-B6A2DE"></div>
                <div class="pull-left width20 col-5AB1EF"></div>
                <div class="pull-left width20 col-FFB980"></div>
                <div class="pull-left width20 col-D87A80"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style11" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#893448" data-subtextcolor="#d95850" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-E01F54"></div>
                <div class="pull-left width20 col-001852"></div>
                <div class="pull-left width20 col-F5E8C8"></div>
                <div class="pull-left width20 col-B8D2C7"></div>
                <div class="pull-left width20 col-C6B38E"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style12" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle" data-textcolor ="#516b91" data-subtextcolor="#93b7e3" data-eithercolor="#999999">
            <li class="pull-left cursor width190 clearfix">
                <div class="pull-left width20 col-C12E34"></div>
                <div class="pull-left width20 col-E6B600"></div>
                <div class="pull-left width20 col-0098D9"></div>
                <div class="pull-left width20 col-2B821D"></div>
                <div class="pull-left width20 col-005EAA"></div>
            </li>
        </ul>
        <ul data-ref="chartColor_style13" class="clearfix mgt10 width200 mgl20 padall5 bgf displayStyle " data-textcolor ="#eeeeee" data-subtextcolor="#aaaaaa" data-eithercolor="#eeeeee">
            <li class="pull-left cursor bg3d3d3d width190 clearfix">
                <div class="pull-left width20 col-8A7CA8"></div>
                <div class="pull-left width20 col-E098C7"></div>
                <div class="pull-left width20 col-8FD3E8"></div>
                <div class="pull-left width20 col-71669E"></div>
                <div class="pull-left width20 col-CC70AF"></div>
            </li>
        </ul>

    </div>
<!--选择配色模板结束-->
<!--  图表设置开始 -->
    <div id="lineChartGoBack" style="width:240px;padding:10px 20px;background:#3D3D3D;color:#fff;cursor: pointer;position:fixed;top:50px;right:-250px;z-index:101">
        <div class="text-center" style="width: 200px;background-color: #303030;border: 1px solid #242424;line-height: 24px;">
            <img class="pull-left fmgr10" src="export/images/sliderPanel/sliderPanel20.png" alt=""/>
            <span class="mgl10 text-left  cursor">返回</span>
        </div>
    </div>
    <div id="lineChartDiv" class="padb5 setBarHeightModel" style="background: #3d3d3d;width: 240px;height: 855px;min-height:10px;color: #fff;overflow:hidden;position:fixed;top: 96px;right:-250px; z-index: 100;opacity:1;display: block;">
        <section id="LineChartSetPannel" class="bline padb5">
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgl20 mgt10 mgb10">
                <input id="bgGrid" class="pull-left" type="checkbox">
                <span class="pull-left mgl5 mgr20 mgt1">背景网格</span>
                <input id="inputBgGrid" class="pull-left inputStyle70" type="text" />
                <input id="bgGridPick" class="width20" type="text">
            </div>
        </section>
        <section id="LineChartSetStyle" class="bline padb5">
            <div class="sidebar-panel-title padd10">
                <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <span class="iconArea cursor">图形设置</span>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgl20 mgt5">
                <input id="smoothCurve" class="pull-left" type="checkbox">
                <span class="pull-left mgl5 mgr20 mgt1">平滑曲线</span>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt20">
                <span class="left mgl15 mgr15 mgl25">线条厚度</span>
                <input id="inputLineWidth" class="pull-left inputStyle70" type="text" value="2px"/>
            </div>
          <!--   <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="left mgl15 mgr15 mgl25">图形描边</span>
                <input id="inputShapeStroke" class="pull-left inputStyle70" type="text" value="1px"/>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="left mgl15 mgr15 mgl25">图形大小</span>
                <input id="inputShapeSize" class="pull-left inputStyle70" type="text" value="5px"/>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="left mgl15 mgr15 mgl25">数值显示位置</span>
                <select class="inputStyle70" style="color:#333;">
                    <option value="居下">居下</option>
                    <option value="居上">居上</option>
                </select>
            </div>   -->
            <div class="sidebar-panel-title padd10 clearfix">
                <span class="pull-left mgl20 mgt10 cursor">图形形状</span>
            </div>
            <div class="sidebar-panel-layout vertical clearfix mgb10">
                <ul id="symbolStyle" class="pull-left mgl20 cursor clearfix" style="width:210px;">
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape1" class="pull-left symbol" type="radio" name="symbol" value="circle">
                        <span class="pull-left mgl5 mgt1">圆形</span>
                    </li>
                    <li class="inputStyle75">
                        <input id="chartShape2" class="pull-left symbol" type="radio" name="symbol" value="emptyCircle">
                        <span class="pull-left mgl5 mgt1">空心圆形</span>
                    </li>
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape3" class="pull-left symbol" type="radio" name="symbol" value="rect">
                        <span class="pull-left mgl5 mgt1">方形</span>
                    </li>
                    <li class="inputStyle75">
                        <input id="chartShape4" class="pull-left symbol" type="radio" name="symbol" value="emptyRect">
                        <span class="pull-left mgl5 mgt1">空心方形</span>
                    </li>
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape5" class="pull-left symbol" type="radio" name="symbol" value="roundRect">
                        <span class="pull-left mgl5 mgt1">圆角矩形</span>
                    </li>
                    <li class="inputStyle95">
                        <input id="chartShape6" class="pull-left symbol" type="radio" name="symbol"  value="emptyRoundRect">
                        <span class="pull-left mgl5 mgt1">空心圆角矩形</span>
                    </li>
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape7" class="pull-left symbol" type="radio" name="symbol" value="triangle">
                        <span class="pull-left mgl5 mgt1">三角形</span>
                    </li>
                    <li class="inputStyle80">
                        <input id="chartShape8" class="pull-left symbol" type="radio" name="symbol" value="emptyTriangle">
                        <span class="pull-left mgl5 mgt1">空心三角形</span>
                    </li>
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape9" class="pull-left symbol" type="radio" name="symbol" value="diamond">
                        <span class="pull-left mgl5 mgt1">菱形</span>
                    </li>
                    <li class="inputStyle75">
                        <input id="chartShape10" class="pull-left symbol" type="radio" name="symbol" value="emptyDiamond">
                        <span class="pull-left mgl5 mgt1">空心菱形</span>
                    </li>
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape11" class="pull-left symbol" type="radio" name="symbol" value="pin">
                        <span class="pull-left mgl5 mgt1">水滴</span>
                    </li>
                    <li class="inputStyle75">
                        <input id="chartShape12" class="pull-left symbol" type="radio" name="symbol" value="emptyPin">
                        <span class="pull-left mgl5 mgt1">空心水滴</span>
                    </li>
                    <li class="mgr20 inputStyle75">
                        <input id="chartShape13" class="pull-left symbol" type="radio" name="symbol" value="arrow">
                        <span class="pull-left mgl5 mgt1">箭头</span>
                    </li>
                    <li class="inputStyle75">
                        <input id="chartShape14" class="pull-left symbol" type="radio" name="symbol" value="emptyArrow">
                        <span class="pull-left mgl5 mgt1">空心箭头</span>
                    </li>
                </ul>
            </div>
        </section>
        <section id="LineChartSetXY" class="bline padb5">
            <div class="sidebar-panel-title padd10">
                <img class="fmgr5 mgl5 iconArea" src="export/images/sliderBar/sliderBar1.png"/>
                <img class="mgl5 fmgr5 iconArea hide1" src="export/images/sliderBar/sliderBar33.png">
                <span class="iconArea cursor">坐标轴设置</span>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt10">
                <span class="left mgl15 mgr15 mgl25">X轴名称</span>
                <input id="inputLineX" class="pull-left inputStyle90" type="text" value=""/>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="left mgl15 mgr15 mgl25">Y轴名称</span>
                <input id="inputLineY" class="pull-left inputStyle90" type="text" value=""/>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="left mgl15 mgr15 mgl25">轴名称位置</span>
                <select class="inputStyle70 namePosition" style="color:#333;">
                    <option value="end">终止</option>
                    <option value="start">开始</option>
                </select>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt20">
                <span class="pull-left mgl5 mgr10 mgl25 mgt1">轴线颜色</span>
                <input id="inputColorXY" class="pull-left inputStyle70" type="text" />
                <input id="colorXYPick" class="width20" type="text">
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="left mgl15 mgr10 mgl25">轴线厚度</span>
                <input id="inputWidthXY" class="pull-left inputStyle70" type="text" value="1"/>
            </div>
            <div class="sidebar-panel-title padd10 clearfix">
                <span class="pull-left mgl20 mgt10 cursor">坐标轴字体</span>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt5">
                <span class="pull-left mgl5 mgr10 mgl25 mgt1">文字倾斜</span>
                <input id="chartFontX" class="pull-left" type="checkbox">
                <span class="pull-left mgl5 mgr20 mgt1">横坐标</span>
                <input id="chartFontY" class="pull-left" type="checkbox">
                <span class="pull-left mgl5 mgr20 mgt1">纵坐标</span>
            </div>
            <div class="sidebar-panel-layout sidebar-panel-color vertical clearfix mgt15">
                <span class="pull-left mgl5 mgr10 mgl25 mgt1">文字颜色</span>
                <input id="inputFontColorXY" class="pull-left inputStyle70" type="text" />
                <input id="fontColorXYPick" class="width20" type="text">
            </div>
        </section>
    </div>
<!-- 图表设置结束 -->

<div id="cleanDiv"></div>
</body>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/vendor/html5shiv.js" type="text/javascript"></script>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/vendor/respond.min.js" type="text/javascript"></script>
<![endif]-->
<!-- Placed at the end of the document so the pages load faster -->
<!-- bootstrap UI -->
<script src="third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
<script src="third/bootstrap-3.3.5-dist/js/bootstrap.min.js" type="text/javascript"></script>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/vendor/holder.js" type="text/javascript"></script>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/google-code-prettify/prettify.js" type="text/javascript"></script>
<script src="third/jquery-ui-bootstrap-1.0/assets/js/docs.js" type="text/javascript"></script>
<script src="third/bootstrap-3.3.5-dist/js/bootstrap-datetimepicker.js" type="text/javascript"></script>


<!-- rangy 加粗-->
<script src="third/rangy/external/log4javascript.js" type="text/javascript"></script>
<script src="third/rangy/src/core/core.js" type="text/javascript"></script>
<script src="third/rangy/src/core/dom.js" type="text/javascript"></script>
<script src="third/rangy/src/core/domrange.js" type="text/javascript"></script>
<script src="third/rangy/src/core/wrappedrange.js" type="text/javascript"></script>
<script src="third/rangy/src/core/wrappedselection.js" type="text/javascript"></script>

<!-- rangy 斜体-->
<script src="third/rangy/lib/rangy-core.js" type="text/javascript"></script>
<script src="third/rangy/lib/rangy-classapplier.js" type="text/javascript"></script>

<!-- rangy 加粗-->
<script src="third/rangy/src/modules/inactive/rangy-textcommands.js" type="text/javascript"></script>


<script type="text/javascript" src="js/jquery.ui.touch-punch.min.js"></script>
<!-- html规范 js -->
<script type="text/javascript" src="js/jquery.htmlClean.js"></script>
<!-- spectrum -->
<script type="text/javascript" src="./third/bgrins-spectrum/spectrum.js"></script>
<!-- validator -->
<script type="text/javascript" src="./third/bootstrapvalidator/dist/js/bootstrapValidator.min.js"></script>
<script type="text/javascript" src="./third/bootstrapvalidator/dist/js/language/zh_CN.js"></script>

<script type="text/javascript" src="./third/perfect-scrollbar/perfect-scrollbar.jquery.min.js"></script>

<!-- echarts js -->
<script type="text/javascript" src="./third/echarts/js/echarts.js"></script>
<script type="text/javascript" src="./third/echarts/js/echarts-wordcloud.js"></script>
<script type="text/javascript" src="./third/echarts/js/china.js"></script>
<!-- html2canvas js -->
<script type="text/javascript" src="./third/html2canvas/html2canvas.js"></script>

<script type="text/javascript" src="js/editor_api.js"></script>
<!--<script src="js/jquery.nicescroll.js" type="text/javascript" charset="utf-8"></script>-->
<script>
    $(function(){
        new LayoutEditor().init();
        /*$("#saveButton").click(
         function(){
         var h = LE.cores["Clean"]().getSpecialHtml();
         console.info(h)
         }
         );*/

    });
</script>
    <!----------------存储图表配置-------------->
    <script type="text/plain" id="lineChart_script" ></script>
</html>