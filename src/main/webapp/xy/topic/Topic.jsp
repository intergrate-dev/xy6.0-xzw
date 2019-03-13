<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<link type="text/css" rel="stylesheet" href="../script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/styles.css"/>
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/autoComplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	<link type="text/css" rel="stylesheet" href="../article/css/article.css"/>
    <link type="text/css" rel="stylesheet" href="css/colpick.css" />
    <link type="text/css" rel="stylesheet" href="css/topic.css"/>

	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/autoComplete.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.2.min.js"></script>
    <script type="text/javascript" src="js/colpick.js"></script>
	<script type="text/javascript" src="../script/cookie.js"></script>
	<script type="text/javascript" src="../article/script/json2.js"></script>
    <script type="text/javascript" src="js/Topic.js" ></script>

	<script type="text/javascript">
		top.window.moveTo(0,0);  
		var docElm = document.documentElement;
		if (docElm.requestFullscreen) {
			//W3C
			top.window.resizeTo(screen.availWidth,screen.availHeight);  
		} else if (docElm.mozRequestFullScreen) {//FireFox
			top.window.resizeTo(screen.availWidth,screen.availHeight);  
		} else if (docElm.webkitRequestFullScreen) {//Chrome等
			 top.window.resizeTo(screen.availWidth,screen.availHeight); 
		} else if (document.all) {//IE
		    top.window.resizeTo(screen.availWidth,screen.availHeight);  
		}
		var topic = {
			UUID : "<c:out value="${UUID}"/>",
			docID : "<c:out value="${topic.docID}"/>",
			docLibID : "<c:out value="${topic.docLibID}"/>",
			siteID : "<c:out value="${topic.siteID}"/>",
			isNew : "<c:out value="${isNew}"/>",
            topic:"<c:out value="${topic.topic}"/>",
            color:"<c:out value="${topic.color}"/>",
            icon:"<c:out value="${topic.icon}"/>",
            status:"<c:out value="${topic.status}"/>"
    };
	</script>
</head>
<body>
<div class="main-wrap">
  <form class="main-container" id="form" method="post" action="topicCommit.do">
    <input type="hidden" id="isNew" name="isNew" value="<c:out value="${isNew}"/>">
    <input type="hidden" id="a_siteID" name="a_siteID" value="<c:out value="${topic.siteID}"/>">
    <input type="hidden" id="a_icon" name="a_icon" value="<c:out value="${topic.icon}"/>">
    <input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${topic.docLibID}"/>">
    <input type="hidden" id="DocID" name="DocID" value="<c:out value="${topic.docID}"/>">
    <input type="hidden" id="UUID" name="UUID" value="<c:out value="${UUID}"/>">
    <input type="hidden" id="a_groupID" name="a_groupID" value="<c:out value="${topic.groupID}"/>">
    <input type="hidden" id="a_status" name="a_status" value="1">
    <%--<div class="navigation">--%>
      <%--<div class="nav-left">--%>
        <%--<span class="top-title">新建话题</span>--%>
      <%--</div>--%>
    <%--</div>--%>
    <div class="container-div">
      <ul class="container-ul">
        <li id="topicId">
            <div class="container-details">
            <span class="inform-text content-left">话题ID&nbsp;&nbsp;&nbsp;</span>
                <input class="topic-id" value="<c:out value="${topic.docID}"/>"/>
            </div>
        </li>
        <li id="liTitle">
          <div class="container-details">
            <span class="inform-text content-left"><span class="reqOption">*</span>话题名称</span>
            <div class="content-div">
              <div id="topicContent" class="rich-editor check-sensitive topic">
                   <input type="text" id="topic" name="SYS_TOPIC" value="<c:out value="${topic.topic}"/>"class="validate[maxSize[1024],required]">
              </div>
            </div>
          </div>
        </li>

        <li>
            <div class="container-details">
                <span class="inform-text content-left"><span class="reqOption">*</span>话题颜色</span>
                <div class="content-div">
                    <div class="showColor" style="background: ${topic.color}"></div>
                    <input type="text"   id="a_color" name="a_color" value="<c:out value="${topic.color}"/>" readonly>
                    <input type="button" id="pickerColor" name="choose-color" class="choose-color" value="选择">
                </div>
            </div>
        </li>
        <li id="tab1">
          <div class="container-details">
            <span class="inform-text content-left">话题图片</span>
            <div class="content-div picTopic" id="topicPicBigDiv" itype="big" title="焦点图片">
              <c:choose>
                <c:when test="${topic.icon != null and topic.icon != ''}">
                  <img id="aIcon" name="a_icon" src="<c:out value="${topic.icon}"/>"/>
                  <span class="icon-remove"></span>
                </c:when>
                <c:otherwise>
                  <p class="plus">+</p>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </li>
        <li>
            <div class="container-details">
                <span class="inform-text content-left">话题状态</span>
                <div class="topic-radios">
                    <input type="radio" name="topicStatus" class="topicStatus"  <c:if test='${topic.status== 0 }'>checked</c:if> value="0"  checked><span class="topicEnable">启用</span>
                    <input type="radio" name="topicStatus" class="topicStatus"  <c:if test='${topic.status== 1 }'>checked</c:if> value="1"><span class="topicDisable">禁用</span>
                </div>
            </div>
        </li>
      </ul>
      <div class="topic-buttons">
        <input class="btns" type="button" id="tobottomSave" value="保存" />
        <input class="btns" style="margin-left:100px;" type="button" id="tobottomCancel" value="取消" />
      </div>
    </div>
  </form>
</div>
</body>
    <script type="text/javascript">
      $("#pickerColor").colpick();
    </script>
</html>
