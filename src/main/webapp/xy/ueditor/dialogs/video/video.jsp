<%@ page language="java" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>

    <link rel="stylesheet" href="../../../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link href="../../../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link rel="stylesheet" type="text/css" href="../../third-party/webuploader/webuploader.css">
    <link rel="stylesheet" type="text/css" href="video.css" />
    
</head>
<body>
<div class="wrapper">
    <div id="videoTab">
        <!-- 标题 -->
        <div id="tabHeads" class="tabhead">
            <span tabSrc="video" class="focus" data-content-id="video"><var id="lang_tab_insertV"></var></span>
            <%--<span tabSrc="upload" data-content-id="upload"><var id="lang_tab_uploadV"></var></span>--%>
            <span tabSrc="videoLib" data-content-id="videoLib"><var id="lang_tab_videoLib"></var></span>
        </div><!-- END 标题 -->

        <div id="tabBodys" class="tabbody">
            <!-- 插入视频 -->
            <div id="video" class="panel focus">
                <table><tr><td><label for="videoUrl" class="url"><var id="lang_video_url"></var></label></td><td><input id="videoUrl" type="text"></td></tr></table>
                <div id="preview"></div>
                <div id="videoInfo">
                    <fieldset style="margin: 5px !important;">
                        <legend>添加播放器</legend>
                        <table style="margin: 0 auto;width: 80%;">
                            <tr>
                                <td style="width: 40%;"><label for="useP" style="cursor: pointer;">是</label><input id="useP" name="usePlayer" type="radio"  style="cursor: pointer;" value="1"/></td>

                                <td style="width: 40%;"><label for="nuseP" style="cursor: pointer;">否</label><input id="nuseP" name="usePlayer" type="radio"  style="cursor: pointer;" value="0" checked="checked"/></td>
                            </tr>
                        </table>
                    </fieldset>
                    <fieldset style="margin: 5px !important;">
                        <legend><var id="lang_video_size"></var></legend>
                        <table style="margin: 0 auto;">
                            <tr><td><label for="videoWidth"><var id="lang_videoW"></var></label></td><td><input class="txt" id="videoWidth" type="text"/></td></tr>
                            <tr><td><label for="videoHeight"><var id="lang_videoH"></var></label></td><td><input class="txt" id="videoHeight" type="text"/></td></tr>
                        </table>
                    </fieldset>
                    <fieldset style="margin: 5px !important;">
                        <legend><var id="lang_alignment"></var></legend>
                        <div id="videoFloat"></div>
                    </fieldset>
                </div>
            </div><!-- END 插入视频 -->

            <!-- 上传视频 -->
            <div id="upload" class="panel">
                <div id="upload_left">
                    <div id="queueList" class="queueList">
                        <div class="statusBar element-invisible">
                            <div class="progress">
                                <span class="text">0%</span>
                                <span class="percentage"></span>
                            </div><div class="info"></div>
                            <div class="btns">
                                <div id="filePickerBtn"></div>
                                <div class="uploadBtn"><var id="lang_start_upload"></var></div>
                            </div>
                        </div>
                        <div id="dndArea" class="placeholder">
                            <div class="filePickerContainer">
                                <div id="filePickerReady"></div>
                            </div>
                        </div>
                        <ul class="filelist element-invisible">
                            <li id="filePickerBlock" class="filePickerBlock"></li>
                        </ul>
                    </div>
                </div>
                <!-- 视频信息 -->
                <div id="uploadVideoInfo">
                    <fieldset>
                        <legend><var id="lang_upload_size"></var></legend>
                        <table style="margin: 0 auto;">
                            <tr><td><label><var id="lang_upload_width"></var></label></td><td><input class="txt" id="upload_width" type="text"/></td></tr>
                            <tr><td><label><var id="lang_upload_height"></var></label></td><td><input class="txt" id="upload_height" type="text"/></td></tr>
                        </table>
                    </fieldset>
                    <fieldset>
                        <legend><var id="lang_upload_alignment"></var></legend>
                        <div id="upload_alignment"></div>
                    </fieldset>
                </div> <!-- END 视频信息 -->

            </div><!-- END 上传视频 -->

            <!-- 视频库 -->
            <div id="videoLib" class="panel">
                <iframe id="videoFrame" src="" frameborder=0 style="width:100%;min-height:480px;"></iframe>
            </div><!-- END 视频库 -->
        </div>
    </div>
</div>

<!-- jquery -->
<script type="text/javascript" src="../../third-party/jquery-1.10.2.min.js"></script>

<!-- webuploader -->
<script type="text/javascript" src="../../third-party/webuploader/webuploader.min.js"></script>
<script type="text/javascript" src="../internal.js"></script>
<script type="text/javascript" src="../../../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
<script type="text/javascript" src="../../../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>

<!-- video -->
<script type="text/javascript" src="video.js"></script>
</body>
</html>