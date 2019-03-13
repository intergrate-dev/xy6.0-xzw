<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="com.founder.amuc.commons.InfoHelper"%>
<%@page import="com.founder.e5.commons.Pair"%>
<%
	String uuid = request.getParameter("UUID");	
	List<Pair> formJsp = null;
	String srcDocId = "";
	if("1".equals(request.getAttribute("result").toString())) {
		formJsp = (List<Pair>)request.getAttribute("formJsp");  
		srcDocId = formJsp.get(1).getKey();
		request.setAttribute("formHead", formJsp.get(0).getStringValue());
	} else {
		request.setAttribute("formHead", "");
	}
%>
<html>
<head>
    <title>属性更新</title>
    <meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<%=request.getAttribute("formHead")%>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.css"/>
	<link type="text/css" rel="stylesheet" href="../css/form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../css/sub-page.css"/>
    
    <script type="text/javascript" src="../../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../../e5script/e5.utils.js"></script>  
    <script type="text/javascript" src="../member/script/merge.js"></script>
    <style>
        .merge{
            margin-left:20px;
        }
        .title{
            background:#F5F5F5;
            text-align:center;
            font-weight:bold;
            border:1px solid #ddd;
            padding:3px;
        }
        .cont{
            vertical-align:top;
            border:1px solid #ddd;
        }
        .mainBodyWrap{
            margin:0;
        }
        .text-center{
            margin-bottom:10px;
        }
        .custform-label-require{
        	width:100px !important;
        }
    </style>
</head> 
<body>
	<div id="wrap" class="wrap">
		<!--  
		<h1>属性更新</h1>
		-->
		<%if("0".equals(request.getAttribute("result").toString())) {%>
		<iframe name="iframe1" id="iframe1" style="display:none"></iframe>
		<div style="height: 230px; padding-left: 20px;">
			<div style="margin:0 auto;height: 80%;width:400px;text-align:center;line-height:200px;">
				<img src="../../amuc/img/tip.gif" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<span style='font-size:small;filter:alpha(Opacity=80);-moz-opacity:0.5;opacity: 0.5;'>没有属性需要更新！</span>
			</div>
			<div style="margin:0 auto;text-align:center;">
				<input type="hidden" id="hdUUID" name="hdUUID" value="<%=uuid%>" />
				<input class="btn" style="display:none;" id="btnSave" type="submit" value="确定"/>
				<input class="btn" id="btnCancel" type="button" value="关闭" onclick="doCancel()" style="margin-left:20px;"/>
			</div>
		</div>
		<%} else { %>
        <table class="merge">
           <tr>
            	<th class="title">当前会员ID[<%=formJsp.get(1).getKey()%>]</th>
                <%if(formJsp.get(2).getKey().indexOf("ori")>-1) {%>
                	<th class="title">采集会员ID[<%=formJsp.get(2).getKey().replace("ori","")%>]</th>
	                <%for(int i=3;i<formJsp.size();i++){%>
	                <th class="title">已合并会员ID[<%=formJsp.get(i).getKey()%>]</th>
	                <% }%>
                <%} else { %>
	                <%for(int i=2;i<formJsp.size();i++){%>
		                <%if(formJsp.get(i).getKey().indexOf("发行系统")>-1) {%>
		                	<th class="title">发行系统会员[<%=formJsp.get(i).getKey().replace("发行系统-","")%>]</th>
		                <% }%>
	                	<%if(formJsp.get(i).getKey().indexOf("SSO系统")>-1) {%>
		                	<th class="title">SSO系统会员[<%=formJsp.get(i).getKey().replace("SSO系统-","")%>]</th>
		                <% }%>
		                <%if(formJsp.get(i).getKey().indexOf("呼叫中心系统")>-1) {%>
		                	<th class="title">呼叫中心系统会员[<%=formJsp.get(i).getKey().replace("呼叫中心系统-","")%>]</th>
		                <% }%>
		                <%if(formJsp.get(i).getKey().indexOf("活动微网站")>-1) {%>
		                	<th class="title">活动微网站会员[<%=formJsp.get(i).getKey().replace("活动微网站-","")%>]</th>
		                <% }%>
		                <%if(formJsp.get(i).getKey().indexOf("线下Excel")>-1) {%>
		                	<th class="title">线下Excel会员[<%=formJsp.get(i).getKey().replace("线下Excel-","")%>]</th>
		                <% }%>
	                <% }%>
                <%} %>
            </tr>
            <tr>
                <td class="cont" id="cust_<%=formJsp.get(1).getKey()%>">
                    <input type="hidden" id="custCount" name="custCount" value="<%=formJsp.size()%>" />
                    <iframe name="iframe1" id="iframe1" style="display:none"></iframe>
                    <form target="iframe1" id="from_<%=formJsp.get(1).getKey()%>" name="from_<%=formJsp.get(1).getKey()%>" method="post" action="Merge.do?a=mergeUpdate">
                        <input type="hidden" id="mainMemId" name="mainMemId" value="<%=srcDocId%>" />
                        <%=InfoHelper.getFormContent(formJsp.get(1).getStringValue())%>
                        <div class="text-center">
                            <input class="btn" id="btnSave" type="submit" value="确定"/>
                            <input class="btn" id="btnCancel" type="button" value="取消" onclick="doCancel()" style="margin-left:20px;"/>
                            <input class="btn" id="checkAllAttr" type="button" value="查看全部属性" style="margin-left:20px;"/>
                            <input class="btn" id="checkDepAttr" type="button" value="查看不同值属性" style="margin-left:20px;display:none;"/>
                        </div>
                    </form>
                </td>
                <%for(int i=2;i<formJsp.size();i++){%>
                <td class="cont" id="cust_<%=formJsp.get(i).getKey()%>">
                    <form id="from_<%=formJsp.get(i).getKey()%>" name="from_<%=formJsp.get(i).getKey()%>">
                        <input type="hidden" id="mainMemId" name="mainMemId" value="<%=srcDocId%>" />
                        <%=InfoHelper.getFormContent(formJsp.get(i).getStringValue())%>
                    </form>
                </td>
                <% }%>
            </tr>
        </table>
        <%} %>
	</div>
	<script type="text/javascript">
		$(function(){
			<%if("0".equals(request.getAttribute("result").toString())) {%>
				document.getElementById("wrap").style.width = 100+"%";
			<%} else {%>
				var size = <%=formJsp.size()%>;
				document.getElementById("wrap").style.width = size*35+"%";
				//默认显示值不相同的属性
				hideAttr();
				$("#checkAllAttr").click(function(){
					showAttr();
				});
				$("#checkDepAttr").click(function(){
					hideAttr();
				});
				function hideAttr(){
					//隐藏相同属性
					k=0;  //全局变量
					var title = [];  //存入标题
					var opArr = [];   //存入各个表单的各项选项值
					<%for(int j=1;j<formJsp.size();j++){%>
						var name = "<%=formJsp.get(j).getKey()%>";
						title[k]=name;
						k++;
					<%}%>
					var titleLen = title.length;  //获取title数组的长度
					var trLen = $("#from_"+title[0]+" tr").length;  //获取表单中共有多少tr
					//这里用trLen-1，是因为最后一个tr没有span标签，否则会报错
					for(var i=0;i<trLen-1;i++){
						for(var j = 0;j<titleLen;j++){
							var trId = $("#from_"+title[j]+" tr").eq(i).children("td").eq(0).children("span").attr("id");
							if(trId.indexOf("SPAN_") > -1){
								var opId = trId.replace("SPAN_","");
							}
							var opVal = $("#from_"+title[j]+" #"+opId).val();
							opArr[j]=opVal;  //将不同选项的值存入数组中
						}
						//隐藏值相同的选项
						var b = 0 ;
						for(var n=0;n<titleLen;n++){
							if(opArr[0] == opArr[n+1] ){
								b++;
							}
						}
						if(b == titleLen-1){
							for(var n=0;n<titleLen;n++){
								$("#from_"+title[0]+" #SPAN_"+opId).parent().parent().hide();
								$("#from_"+title[n+1]+" #SPAN_"+opId).parent().parent().hide();
							}
							b = 0;
							opArr = [];//置空
						}
					}
					$("#checkAllAttr").show();
					$("#checkDepAttr").hide();
				}
				function showAttr(){
					//显示相同属性
					k=0;  //全局变量
					var title = [];  //存入标题
					<%for(int j=1;j<formJsp.size();j++){%>
						var name = "<%=formJsp.get(j).getKey()%>";
						title[k]=name;
						k++;
					<%}%>
					var titleLen = title.length;  //获取title数组的长度
					for(var j = 0;j<titleLen;j++){
						$("#from_"+title[j]+" tr").show();
						//最后一个tr是无用的，因此不显示
						$("#from_"+title[j]).find("tr:last").hide();
					}
					$("#checkDepAttr").show();
					$("#checkAllAttr").hide();
				}
			<%}%>
		});
    </script>
</body>
</html>