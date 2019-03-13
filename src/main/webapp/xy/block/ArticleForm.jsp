<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>区块内容表单</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/calendar/calendar.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<link rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 
	<script type="text/javascript" src="../../xy/block/script/article.js"></script>
	<style>
		#btnFormCancel{
		    margin-left:100px !important;
		    width: 65px !important;
		    height: 29px !important;
		    text-shadow: none;
		    position: absolute;
			left: 107px;
			margin-top: -8px; 
		}
		
		.btngroup{
			font-family: "microsoft yahei";
			width: 65px !important;
			height: 30px !important;
			padding: 0 !important;
			margin-top: -8px;
			
		}
		textarea{
			width: 386px !important;
			height:120px;
		}
		#btnFormSave{
			float: left;
			margin-top: -9px;
		}
	</style>
</head>
<body>
	<script>
		artcile_form.blockID = "<c:out value="${blockID}"/>";
        artcile_form.canEditStyle = <c:out value="${canEditStyle}"/>;
        artcile_form.init();
	</script>
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form id="form" method="post" action="../../e5workspace/manoeuvre/FormSubmit.do" >
		<input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${DocLibID}"/>"/>
		<input type="hidden" id="DocID" name="DocID" value="<c:out value="${DocIDs}"/>"/>
		<input type="hidden" id="FVID" name="FVID" value="<c:out value="${FVID}"/>"/>
		<input type="hidden" id="UUID" name="UUID" value="<c:out value="${UUID}"/>"/>
		
		<input type="hidden" id="SaveSubmit" name="SaveSubmit" value=""/>
		<div class="mainBodyWrap">
			<table class="tablecontent">
<tbody customwidth="483" customheight="400"> 
  <tr>
    <td class="ui-droppable" colspan="2">
        <span id="SPAN_ba_topic" class="custform-span"> 
        	<div style="float: left;">
        		<label id="LABEL_ba_topic" class="custform-label custform-label-require">标题</label>  
		         <div class="custform-from-wrap" id="DIV_ba_topic"> 
		              <input type="text" id="ba_topic" name="ba_topic" value="<c:out value="${topic}"/>" class="custform-input validate[maxSize[255],required,,funcCall[checkDuplicate]]" style="width:400px;" />
		          		<span class="custform-postfix">
		            		<span class="custform-aftertxt ui-draggable"/>
		          		</span> 
		         </div>
        	</div>
          
          <input style="margin: 2px 0 0 15px;" class="button btn btngroup" type="button" id="btnTitleAdv" value="编辑样式" /> 
        </span>
    </td>
  </tr>
    <tr>
    <td class="ui-droppable" colspan="2">
        <span id="SPAN_ba_subTitle" class="custform-span"> 
	        <div style="float: left;">
	        	 <label id="LABEL_ba_subTitle" class="custform-label ">副题</label>  
		          <div class="custform-from-wrap" id="DIV_ba_subTitle"> 
		               <input type="text" id="ba_subTitle" name="ba_subTitle" value="<c:out value="${subTitle}"/>" class="custform-input validate[maxSize[255]]" style="width:400px;" />
		          		<span class="custform-postfix">
		            		<span class="custform-aftertxt ui-draggable"/>
		          		</span> 
		          </div> 
	        </div>
          
          <input style="margin: 2px 0 0 15px;" class="button btn btngroup" type="button" id="btnEditSubTitle" value="编辑样式" /> 
        </span>
    </td>
  </tr>
 
  <tr>
    <td class="ui-droppable" colspan="2">
      <span id="SPAN_ba_url" class="custform-span"> 
        <label id="LABEL_ba_url" class="custform-label custform-label-require">链接</label>  
        <div class="custform-from-wrap" id="DIV_ba_url"> 
          <input type="text" id="ba_url" name="ba_url" value="<c:out value="${url}"/>" class="custform-input validate[maxSize[255],required]" style="width:400px;"/>  
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr>
   <tr>
    <td colspan="2" class="ui-droppable">
      <span class="custform-aftertxt ui-draggable"/>
      <span id="SPAN_ba_pubTime" class="custform-span">
        <label id="LABEL_ba_pubTime" class="custform-label ">发布时间</label>
        <div class="custform-from-wrap" id="DIV_ba_pubTime">
          <input type="text" id="ba_pubTime" readonly="readonly" name="ba_pubTime" value="<c:out value="${pubTime}"/>"
			class="custform-input validate[custom[dateTimeFormat1]]" style="width:160px;" />
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr> 
  <tr>
    <td class="ui-droppable" colspan="2">
      <span class="custform-aftertxt ui-draggable"/>
      <span id="SPAN_ba_pic" class="custform-span"> 
        <label id="LABEL_ba_pic" class="custform-label ">标题图片</label>  
        <div class="custform-from-wrap" id="DIV_ba_pic"> 
			<input type="file" id="ba_pic" name="ba_pic" oldValue="<c:out value="${pic}"/>"
			accept="image/gif,image/jpeg,image/jpg,image/png"/>
			<img id="img4Title" src="<c:out value="${pic}"/>" style="margin-top:5px;display:block;max-width:150px;max-height:150px;"/>
        </div> 
      </span>
    </td>
  </tr>
  <tr>
    <td class="ui-droppable" colspan="2">
        <span id="SPAN_ba_abstract" class="custform-span">
         <div style="float: left;">
         	 <label id="LABEL_ba_abstract" class="custform-label ">摘要</label>
	         <div class="custform-from-wrap" id="DIV_ba_abstract"> 
	         	<textarea class="smallTextarea" id="ba_abstract" name="ba_abstract" class="validate[maxSize[2000]]"><c:out value="${summary}"/></textarea>
	          	<span class="custform-postfix">
	            	<span class="custform-aftertxt ui-draggable"/>
	          	</span> 
			 </div>
         </div>
        
		 <input style="margin:0px 0 0 15px;" class="button btn btngroup" type="button" id="btnEditAbstract" value="编辑样式" />
    </span>
	</td>
  </tr> 
  <tr style="display: block;  margin-left: 5px;">
    <td class="ui-droppable" style="width: 65px;">
      <span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable">
      	<input class="button btn btngroup" id="btnFormSave"  value="保存"/>
      	<input  class="button btn btngroup" id="btnFormCancel" type="button" value="取消"/>
      </span>
       <span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
          
      </span>
      <!--<span class="custform-aftertxt ui-draggable"/> -->
    </td>
    <!--<td class="ui-droppable" style="width:4px; float: left;">
     
      <span class="custform-aftertxt ui-draggable"/> 
    </td>-->
  </tr>  
 </tbody>
			</table>
		</div>
<input type="hidden" id="SYS_TOPIC" name="SYS_TOPIC" value=""/>  
<input type="hidden" id="ba_blockID" name="ba_blockID" value=""/>  
<input type="hidden" id="wx_menuID" name="wx_menuID" value=""/>  
<input type="hidden" id="wx_subTitle" name="wx_subTitle" value=""/>  
<input type="hidden" id="wx_url" name="wx_url" value=""/>  
<input type="hidden" id="wx_pubTime" name="wx_pubTime" value=""/>  
<input type="hidden" id="wx_pic" name="wx_pic" value=""/>  
<input type="hidden" id="wx_abstract" name="wx_abstract" value=""/>  
	</form>
</body>
</html>