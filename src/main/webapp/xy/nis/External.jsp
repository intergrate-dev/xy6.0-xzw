<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../e5include/IncludeTag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>外部系统</title>
    <meta content="IE=edge" http-equiv="X-UA-Compatible" />
    <link type="text/css" rel="stylesheet" href="../../e5style/reset.css" />
    <link type="text/css" rel="stylesheet" href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="../../xy/script/webuploader/webuploader.css">

    <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../../xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../../xy/script/webuploader/webuploader.js"></script>
    <script type="text/javascript" src="js/external.js"></script>
    <style>
        *{font-family: "microsoft yahei"}
        #font-name{
            height: 34px;
            width: 400px;
        }
        #form{
            width:740px
        }
        .font-list{
            text-align:center;
            font-size:13px;
            border-collapse:collapse;
        }
        .font-detail{
            margin-top: 15px;
            width:100%;
        }
        .description{
            float:left;
            margin:0 10px;
        }
        .fl{
            float:left
        }
        .font-list-title{
            width:157px;
            height:36px;
            border:1px solid #d5d5d5;
            text-align: center;
            background-color:#f2f2f2;
            font-weight:bold;
            color:#666;
            word-break: break-all;
        }
        .font-list-content{
            width:157px;
            height:36px;
            border:1px solid #d5d5d5;
            text-align: center;
            background-color:#fff;
            font-weight:200;
            color:#666;
            word-break: break-all;
        }
        .delete{
            padding:1px 10px;
            background:none;
            border:1px solid #ccc;
            border-radius:2px;
            outline:none;
        }
        .delete:hover{
            background:#f2f2f2;
        }
        .tablecontent{
            margin-top:20px;
        }
        .tablecontent td{
            margin-left:92px;
        }
        #btnFormSave{
            border-radius:3px;
            color: #fff;
            background:rgb(0,183,238);
            height: 30px;
            border: none;
            text-shadow: none;
            padding: 0 27px;
        }
        #btnFormCancel{
            height: 30px;
            background: #b1b1b1;
            border: none;
            color: #fff;
            border-radius: 3px;
            padding: 0 27px;
            text-shadow: none;
        }
    </style>
</head>
<body onload="doInit()">
<form id="form" method="post" action="ExternalSubmit.do" onsubmit="return beforeSubmit();">
    <input type="hidden" id="UUID" name="UUID" value="${UUID}" />
    <input type="hidden" id="siteID" name="siteID" value="${siteID}" />
    <input type="hidden" id="siteLibID" name="siteLibID" value="${siteLibID}" />
    <input type="hidden" id="external" name="external" value="${external}" />
    <div class="font-detail clearfix">
        <span class="description">外部系统名称:</span>
        <div class="fl" style="position: relative;">
            <input id="font-name" type="text" autocomplete="on"  onkeydown="if(event.keyCode==13) return false;"/>
            <input type="button" value="确定" onclick="makesure()">
        </div>
    </div>

    <div class="font-detail clearfix">
        <span class="description">外部系统列表:</span>
        <table class="font-list fl" id="externalList">
            <thead>
            <tr>
                <th class="font-list-title">ID</th>
                <th class="font-list-title">名称</th>
                <th class="font-list-title">KEY</th>
                <th class="font-list-title">操作</th>
            </tr>
            </thead>
            <tbody>
            <!--<tr>
                <td class="font-list-content">名称</td>
                <td class="font-list-content">大小</td>
                <td class="font-list-content">url</td>
                <td class="font-list-content"><input type="button" class="delete" value="删除"/></td>
            </tr>-->
            </tbody>
        </table>
    </div>
    <table class="tablecontent">
        <tr>
            <td style="float:left;">
                    <span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
                        <input id="btnFormSave" type="submit" value="保存" />
                    </span>
            </td>
            <td style="float:left;">
                    <span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable">
                        <input id="btnFormCancel" type="button" onclick="window.close()" value="取消" />
                    </span>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
