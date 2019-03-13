<%@page import="java.math.BigDecimal"%>
<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="com.founder.e5.sys.SysConfig"%>
<%@page import="java.util.Map"%>
<i18n:bundle baseName="i18n.e5sysconfig" changeResponseLocale="false"/>
<html>
	<head>
		<title>Add Role</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link rel="stylesheet" type="text/css" href="../../e5script/jquery/dialog.style.css" />
		<style>
			.table td.fold{
				background:#F5F5F5;
				border-right:1px solid #e8e8e8;
				vertical-align:top;
			}
		</style>
	</head>
	<body>
		<iframe name="iframe" id="iframe" src="" style="display:none" height="180" width="600" frameborder="0"></iframe>
		<div class="mainBodyWrap">
		<table id="tab1" cellpadding="0" cellspacing="0" class="table">
			<tr>
				<th width="150"><i18n:message key="sysconfig.list.title.project"/></th>
				<th width="200"><i18n:message key="sysconfig.list.title.item"/></th>
				<th width="200"><i18n:message key="sysconfig.list.title.value"/></th>
				<th width="400"><i18n:message key="sysconfig.list.title.note"/></th>
			</tr>
			<%
				Map<String, SysConfig[]> sysconfiglist = (Map<String, SysConfig[]>)request.getAttribute("sysconfiglist");
				Set<String> keySet = sysconfiglist.keySet();
				String[] keys = keySet.toArray(new String[0]);
				BigDecimal d1 = new BigDecimal(0);
				for(int i=0;i<keys.length;i++){
					SysConfig[] sysConfigs = (SysConfig[])sysconfiglist.get(keys[i]);
					int length = sysConfigs.length;
					for(int j=0;j<length;j++){
						SysConfig sysConfig = sysConfigs[j];
			%>
						<%if(j==0){ %>
						<tr id="<%=sysConfig.getSysConfigID() %>" onclick="SelectID(this)">
							<td id="ti0<%=i %>" onclick="te_show(<%=d1 %>, <%=length %>)" rowspan="<%=length%>" class="fold">
								<img id="img<%=d1 %>" src="../../images/minus.gif"/><%=keys[i] %>
							</td>
							<td><%=sysConfig.getItem() %></td>
							<td><%=sysConfig.getValue() %></td>
							<td><%=sysConfig.getNote() %></td>
						</tr>
						<%}else{ %>
						<tr id="<%=sysConfig.getSysConfigID() %>" onclick="SelectID(this)">
							<td><%=sysConfig.getItem() %></td>
							<td><%=sysConfig.getValue() %></td>
							<td><%=sysConfig.getNote() %></td>
						</tr>
						<%}
						%>
			<%
					}
					d1=d1.add(new BigDecimal(length));
				}
			%>
		</table>
		</div>
<script type="text/javascript">
function configItemForm(url){
	e5.dialog({type:"iframe", value:url},{id:"configItemForm",title:"<i18n:message key="sysconfig.form.title"/>", width:600, height:310, resizable:true}).show();
}
function configItemFormClose(){
	e5.dialog.close("configItemForm");
}
 var sysConfigID="0";
 var olde=null;

 function SelectID(e)
 {
 if(olde!=null)olde.style.backgroundColor=document.bgColor
  e.style.backgroundColor="#e8e8e8"
  olde=e
  sysConfigID=e.id;
 }

function hiddenFrame()
{
	document.getElementById("iframe").style.display="none";
}
function refreshPage()
{
	document.location.href="SysConfigureMgrAction.do?invoke=sysConfigList&appID="+parent.leftFrame.appID;
}
function te_show(trnum, length)
{
	if(length>1){
		var img=document.getElementById("img"+trnum);
		var imgSrc=img.src;
		var imgName = imgSrc.substr((imgSrc.lastIndexOf("/")+1),imgSrc.length);
		$.each($("#tab1 tr"), function(i){     
		     if(i > (trnum+1)&&i<=(trnum+length)){        
		           if(this.style.display == 'none'){
		        	   this.style.display="";
			       	}else{
			       		this.style.display="none";
			       	}
		      }   
		});
		if(imgName == 'plus.gif'){
			img.src="../../images/minus.gif";
			img.parentNode.setAttribute("rowspan",length);
		}else{
			img.src="../../images/plus.gif";
			img.parentNode.setAttribute("rowspan",1);
		}
	}	
	//alert(this.value);	
	/*
	var oti=document.getElementById("ti0"+f);
	var ote=document.getElementById("te0"+f);
	var img=document.getElementById("img"+f);
	if(ote.style.display=="none"){
		ote.style.display="block";
		img.src="../../images/minus.gif";
	}else{
		ote.style.display="none";
		img.src="../../images/plus.gif";
	}
	*/
}

</script>
</body>
</html>
