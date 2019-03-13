<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="com.founder.amuc.commons.InfoHelper"%>
<%@page import="com.founder.e5.commons.StringUtils"%>
<%@page import="com.founder.e5.commons.Pair"%>
<%@page import="com.founder.e5.web.DomInfo"%>
<%@page import="com.founder.e5.workspace.app.form.*"%>
<%
    int docLibID = new Integer(request.getParameter("DocLibID"));
    String docIDs = request.getParameter("DocIDs");
    String uuid = request.getParameter("UUID");
    
    List<Pair> formJsp = InfoHelper.getFormJsp(docLibID, docIDs, "FormCustomerMerge", uuid);
	String srcDocId = formJsp.get(1).getKey();
    request.setAttribute("formHead", formJsp.get(0).getStringValue());
%>
<html>
<head>
    <meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
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
    </style>
</head> 
<body>
	<div class="wrap">
		<h1>会员合并</h1>
        <table class="merge">
            <tr>
            	<%for(int i=1;i<formJsp.size();i++){ %>
            	<td style="padding-left:10px;padding-bottom:10px;"><label for="mainMember<%=i %>"><input type="radio" onclick="getMainMember(this);" name="mainMember" id="mainMember<%=i %>" value="<%=formJsp.get(i).getKey()%>" <%= i==1?"Checked":""%> />作为主会员</label></td>
            	<% }%>
            </tr>
            <tr>
                <%for(int i=1;i<formJsp.size();i++){%>
                <th class="title">原属性值[<%=formJsp.get(i).getKey()%>]</th>
                <% }%>
            </tr>
            <tr>
                <%for(int i=1;i<formJsp.size();i++){%>
                <td class="cont" id="cust_<%=formJsp.get(i).getKey()%>">
                    <input type="hidden" id="custCount" name="custCount" value="<%=formJsp.size()%>" />
                    <iframe name="iframe<%=i %>" id="iframe<%=i %>" style="display:none"></iframe>
                    <form target="iframe<%=i %>" id="from_<%=formJsp.get(i).getKey()%>" name="from_<%=formJsp.get(i).getKey()%>" method="post" action="Merge.do?a=merge&memberId=<%=formJsp.get(i).getKey()%>">
                        <input type="hidden" id="dupDocLibID" name="dupDocLibID" value="<%=docLibID%>" />
                        <input type="hidden" id="DocIDs" name="DocIDs" value="<%=docIDs%>" />
                        <input type="hidden" id="dupUUID" name="dupUUID" value="<%=uuid%>" />
                        <input type="hidden" id="mainMemId" name="mainMemId" value="<%=srcDocId%>" />
                        <%=InfoHelper.getFormContent(formJsp.get(i).getStringValue())%>
                        <div class="text-center">
                            <input class="btn" id="btnSave" type="submit" value="确定"/>
                            <input class="btn" id="btnCancel" type="button" value="取消" onclick="doCancel()" style="margin-left:20px;"/>
                        </div>
                    </form>
                </td>
                <% }%>
            </tr>
        </table>
	</div>
	<script>
	function getMainMember(obj) {
		var mainId=obj.value;
		setMainMember(mainId);
	}
	</script>
</body>
</html>