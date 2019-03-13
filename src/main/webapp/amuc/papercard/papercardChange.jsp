<%@page import="com.founder.e5.doc.Document"%>
<%@page import="com.founder.e5.doc.DocumentManagerFactory"%>
<%@page import="com.founder.e5.doc.DocumentManager"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String path = WebUtil.getRoot(request);
	DocumentManager docManager = DocumentManagerFactory.getInstance();
	int DocLibID = Integer.parseInt(request.getParameter("DocLibID"));
	long DocID = Long.parseLong(request.getParameter("DocIDs"));
	Document doc = docManager.get(DocLibID, DocID);
%>
<!DOCTYPE html>
<html>
<head>
	<title>报卡表单</title>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/lhgcalendar/lhgcalendar.bootstrap.css">
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<script type="text/javascript" src="../../e5script/calendar/usecalendar.js"></script>
	<script type="text/javascript" src="../../e5script/lhgcalendar/lhgcalendar.js"></script>
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 
	<script type="text/javascript">
		$(function(){
			//自定义提醒时间填写，前边复选框自动变化
			$("#fjts").change(function(){
				if($("#fjts").val()!=""){
					$("#weizhi").attr("checked",true);
				}else{
					$("#weizhi").attr("checked",false);
				}
			})
			//点击保存按钮
			$("#btnFormSave").click(function(){
				//校验阅读方式和提醒时间未选，弹框
				var vremind=$("input[name='pcRemind']:checked").val();
				var vreadway=$("input[name='pcReadway']:checked").val();
				if(vreadway==undefined || vreadway==null || vreadway==''){
					alert("阅读渠道未选");
					return;
				}
				if(vremind==undefined || vremind==null || vremind==''){
					alert("提醒时间未选");
					return;
				}
				
				//如果选自定义提醒时间则必须输入
				if($("#weizhi").attr("checked")=="checked" && $("#fjts").val()==""){
					alert("请输入自定义时间！");
					return;
				}
				if($("#weizhi").attr("checked")=="checked" && Number($("#fjts").val())<0){
					alert("请填写正确自定义提醒时间！");
					return;
				}
				//如果输入自定义提醒时间则必是选中状态
				var fjtsval=$("#fjts").val();
				if(fjtsval!="" && fjtsval!=null && fjtsval!=undefined){
					$("#weizhi").attr("checked",true);
				}else{
					$("#weizhi").attr("checked",false);
				}
				$("#form").submit();
		
			})
		})
		
		function setText(obj)
{
    if(obj.checked==true)
    {
        document.getElementById("fjts").disabled=false;
    }
    else
    {
        document.getElementById("fjts").disabled=true;
        document.getElementById("fjts").value="";
    }
}
		$(function(){
			var pcReadway="<%=doc.getInt("pcReadway")%>";
			var pcStatus="<%=doc.getInt("pcStatus")%>"; 
			var pcRemind="<%=doc.getString("pcRemind")%>".split(",");
			var pcActiveStatus="<%=doc.getString("pcActiveStatus")%>";
			var pcRemarks="<%=doc.getString("pcRemarks")%>";
			
			$("#pcRemarks").val(pcRemarks);
			//激活的报卡不能修改状态
			if(pcActiveStatus=='激活'){
				$("input[name='pcStatus']").attr("disabled",true);
			}
		if(pcReadway==0){
			$("input:radio[name='pcReadway']:eq(0)").attr('checked',true); 
		}else if(pcReadway==1){
			$("input:radio[name='pcReadway']:eq(1)").attr('checked',true);
		}
		else if(pcReadway==2){
			$("input:radio[name='pcReadway']:eq(2)").attr('checked',true);
		}
			$("input[value='"+pcStatus+"']").eq($("input[value='"+pcStatus+"']").length-1).attr("checked","true"); 
		
			for ( var int = 0; int < pcRemind.length; int++) {
				if(pcRemind[int]==1){
					one.checked=true;
				}
			else if($("input[value='"+pcRemind[int]+"']").eq($("input[value='"+pcRemind[int]+"']").length-1).val()==undefined){
					$("#weizhi").val(pcRemind[int]);
					$("#weizhi").attr("checked","true");
					$("#fjts").val(pcRemind[int]);
					$("#fjts").removeClass("defalttest"); 
				}else{
					if($("input[value='"+pcRemind[int]+"']").eq($("input[value='"+pcRemind[int]+"']").length-1).attr("type")=="checkbox"){
						$("input[value='"+pcRemind[int]+"']").eq($("input[value='"+pcRemind[int]+"']").length-1).attr("checked","true");
					}else{
						$("#weizhi").val(pcRemind[int]);
						$("#weizhi").attr("checked","true");
						$("#fjts").val(pcRemind[int]);
						$("#fjts").removeClass("defalttest"); 
					}
				}
			}
			 if(weizhi.checked==true)
			    {
			        document.getElementById("fjts").disabled=false;
			    }
			    else
			    {
			        document.getElementById("fjts").disabled=true;
			        document.getElementById("fjts").value="";
			    }
		
		})

	function showlog(){
		var docid = $("#DocID").val();
		window.location.href="pcLog.jsp?DocID="+docid; 
	}
	</script>
	<style type="text/css">
	.formError{margin-top: 88px;}
	</style>
</head>
<body>
<%  
int a = doc.getInt("pcReadway");  
%>  
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form id="form" method="post" action="pcardchange.do">
		<input type="hidden" id="DocLibID" name="DocLibID" value="<%=request.getParameter("DocLibID")%>"/>
		<input type="hidden" id="DocID" name="DocID" value="<%=request.getParameter("DocIDs")%>"/>
		<input type="hidden" id="FVID" name="FVID" value="<%=request.getParameter("FVID")%>"/>
		<input type="hidden" id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>"/>
		<input type="hidden" id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>"/>
		
		<input type="hidden" id="SaveSubmit" name="SaveSubmit" value=""/>
		<div class="mainBodyWrap">
			<table class="tablecontent">
		<tr style="display:none">
    <td>
      <div id="hiddenDiv"> 
        <ul id="tablehide-ul" class="ui-droppable">
          <li>
            <span id="SPAN_pcOperator" class="custform-span"> 
              <label id="LABEL_pcOperator" class="custform-label ">操作人</label>  
              <div class="custform-from-wrap" id="DIV_pcOperator"> 
                <input type="text" id="pcOperatorName" name="pcOperatorName" value="${sessionScope.sysUser.userName}" class="custform-input validate[maxSize[255]]" style="width:133px;"/>  
                <input type="text" id="pcOperatorID" name="pcOperatorID" value="${sessionScope.sysUser.userID}" class="custform-input validate[maxSize[255]]" style="width:133px;"/>  
                <span class="custform-postfix">
                  <span class="custform-aftertxt"/>
                </span> 
              </div> 
            </span>
          </li> 
        </ul> 
      </div>
    </td>
  </tr>	

<tbody customwidth="794" customheight="289">
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcTotal" class="custform-span"> 
        <label id="LABEL_pcTotal" class="custform-label custform-label-require">报卡号</label>  
        <div class="custform-from-wrap" id="DIV_pcTotal"> 
          <label id="pcNo" value=""><%=doc.getString("pcNo")%></label>
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr> 
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcTotal" class="custform-span"> 
        <label id="LABEL_pcTotal" class="custform-label custform-label-require">有效期</label>  
        <div class="custform-from-wrap" id="DIV_pcTotal"> 
          <label id="pcEffectTime" value=""><%=doc.getString("pcEffectTime")%></label>
          <label id="" value=""><%=doc.getString("pcExpiryDate")%></label>
          <label id="pcExpireTime" value=""><%=doc.getString("pcExpireTime")%></label>
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr> 
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcTotal" class="custform-span"> 
        <label id="LABEL_pcTotal" class="custform-label custform-label-require">报纸内容</label>  
        <div class="custform-from-wrap" id="DIV_pcTotal"> 
          <label id="pcPaperName" value=""><%=doc.getString("pcPaperName")%></label>
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr> 
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcTotal" class="custform-span"> 
        <label id="LABEL_pcTotal" class="custform-label custform-label-require">报卡金额</label>  
        <div class="custform-from-wrap" id="DIV_pcTotal"> 
          <label id="pcMoney" value=""><%=doc.getString("pcMoney")%></label>
          <span class="custform-postfix">
            <span class="custform-aftertxt ui-draggable"/>
          </span> 
        </div> 
      </span>
    </td>
  </tr> 
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">阅读渠道</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input id="" value="0" name="pcReadway" type="radio">全部渠道</input> 
          <input id="" value="1" name="pcReadway" type="radio">PC</input>
          <input id="" value="2" name="pcReadway" type="radio">移动</input>
        </div> 
      </span>
    </td>
  </tr>
  <tr style="display:none;"><!-- 隐藏过期提醒 -->
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">过期提醒</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input id="one" value="1" name="pcRemind" type="checkbox">1天前</input> 
          <input id="" value="7" name="pcRemind" type="checkbox">7天前</input>
          <input id="" value="14" name="pcRemind" type="checkbox">14天前</input>
          <input id="" value="30" name="pcRemind" type="checkbox">30天前</input>
          <input id="weizhi" value="" name="pcRemind" type="checkbox" onclick="setText(this)"><input id="fjts" value="" name="" disabled="true" type="text" style="width:66px;" onBlur="val=$(this).val();var n = Number(val);if (!isNaN(n)){$('#weizhi').val(val);}else{alert('请输入数字')}"></input></input>
        </div> 
      </span>
    </td>
  </tr>
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">状态</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input id="" value="1" name="pcStatus" type="radio">有效</input> 
          <input id="" value="0" name="pcStatus" type="radio">无效</input>
        </div> 
      </span>
    </td>
  </tr>  
  <tr>
    <td class="ui-droppable" style="width: 792px;">
      <span id="SPAN_pcArea" class="custform-span"> 
        <label id="LABEL_pcArea" class="custform-label custform-label-require">备注</label> 
        <div class="custform-from-wrap" id="DIV_pcArea"> 
          <input id="pcRemarks" value="" name="pcRemarks" type="text" style="margin-top: 6px;"></input> 
        </div> 
      </span>
    </td>
  </tr>
  <tr>
    <td class="ui-droppable">
      <span class="ui-draggable" id="txtFormSave" fieldtype="-1" fieldcode="insertsave">
        <input class="button btn" id="btnFormSave" value="保存" type="button"/>
      </span>
      <span class="ui-draggable" fieldtype="-1" fieldcode="insertsave">
        <input class="button btn" id="btnFormLog" onclick="showlog()" value="查看操作日志" type="button"/>
      </span>
      <span class="custform-aftertxt ui-draggable"/>
      <span class="ui-draggable" id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel">
        <input class="button btn" id="btnFormCancel" value="取消" type="button"/>
      </span>
      <span class="custform-aftertxt ui-draggable"/> 
    </td>
  </tr>  
</tbody>

			</table>
		</div>
	</form>
</body>
</html>
