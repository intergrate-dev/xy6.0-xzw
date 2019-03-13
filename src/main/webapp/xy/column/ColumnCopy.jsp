<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>栏目复制</title>
    <link rel="stylesheet" type="text/css" href="../script/bootstrap/css/bootstrap.css">
    <style type="text/css">
        form{
            margin-left:20px;
            margin-top:25px;
        }
        p{
            font-size:16px;
        }
        .btn{
            font-size: 16px;
        }
        .tm,.noTem{
            display: inline-block;
            margin-top: 11px;
            float: left;
            margin-right: 5px;
        }
        .noTem{
            margin-bottom: 13px;
        }
        .input{
            display: inline-block;
            FLOAT: LEFT;
            MARGIN-RIGHT: 5PX !important;
        }
        .nameInput{
            font-size: 12px !important;
            border-radius: 2px !important;
            padding: 3px !important;
            padding-left: 5px !important;
        }
        input[type="button"]{
            font-family: "Microsoft YaHei";
            font-size: 12px;
            padding: 2px 10px;
        }
        .Duplicate{
            color:red;
            font-size: 12px;
        }
        body{
            line-height: 13px;
        }
    </style>
</head>


<script language="javascript" type="text/javascript" src="script/columnCopy.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript">
    colCopy.siteID =  "<c:out value="${siteID}"/>";
    colCopy.temDoclibID = "<c:out value="${temDoclibID}"/>";
    colCopy.webTem = <c:out value="${webTem}"/>;
    colCopy.padTem = <c:out value="${padTem}"/>;
</script>
<body>
<form action="">
    <c:if test="${webTem || padTem }">
    <div  id="copyTemDiv" >
        <input CLASS="input" style="display: inline-block" id="copyTem" type="checkbox" name="copyTem" onclick="colCopy.showTemName()" checked="checked" />
        <label FOR="copyTem" style="display: inline-block">生成新模板</label>
        <span style="color:#ef4949;font-size: 12px;">（根据新栏目的ID 生成新的栏目模板）</span>
    </div>
    </c:if>
    <c:if test="${webTem ==false && padTem == false }">
        <div  id="copyTemDiv" >
            <span style="color:#ef4949;font-size: 12px;">原栏目没有配置栏目模板或者没有模板组权限,无法生成新模板</span>
        </div>
    </c:if>
    <c:if test="${webTem !=false || padTem != false }">
    <div id="newTemNameDiv">
        <c:if test="${webTem}">
        <span class="tm">网站栏目模板名称:</span>
        <input class="nameInput" style="font-size:12px;" id="newWebTemName" groupID ="<c:out value="${webTemGroupID}"/>" value="<c:out value="${newWebTemName}"/>"
               onBlur="colCopy.Duplicate(true)" type="text" name="newTemName" >
            <div class="Duplicate"  style="display: none" id="webTemDuplicate">此名称已被使用</div>
            </input>
        <br>
        </c:if>
        <c:if test="${webTem==false}">
            <span class="noTem">原栏目没有配置网站栏目模板或者没有模板组权限</span>
            <br>
        </c:if>
        <c:if test="${padTem}">
        <span class="tm">触屏栏目模板名称:</span>
        <input class="nameInput" style="font-size:12px;" id="newPadTemName" groupID ="<c:out value="${padTemGroupID}"/>"  value="<c:out value="${newPadTemName}"/>"
               onBlur=" colCopy.Duplicate(true)" type="text" name="newTemName" >
            <div class="Duplicate"  style="display: none" id="padTemDuplicate">此名称已被使用</div>
            </input>
        <br>
        </c:if>
        <c:if test="${padTem==false}">
            <span class="noTem">原栏目没有配置触屏栏目模板或者没有模板组权限</span>
            <br>
        </c:if>
    </div>
    </c:if>
    <div>
        <SPAN  class="tm">新栏目名称:</SPAN>
        <input  class="nameInput" style="font-size:12px;" id="newColName"  type="text" value="<c:out value="${newColName}"/>" name="newColName">
        <br>
    </div>


    <div class="text-center">
        <input id="submitBtn"  type="button" class="button btn btn-primary " onclick="colCopy.batSubmit()" value="确定">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" class="button btn" onclick="colCopy.closeWin()" value="取消">
    </div>

</form>
</body>
</html>