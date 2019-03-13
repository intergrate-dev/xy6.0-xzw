<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<HTML>
<HEAD>
<TITLE><i18n:message key="manoeuvre.transfer.title"/></TITLE>
<link rel="stylesheet" type="text/css" href="../../e5style/Style.css">
<link rel="stylesheet" type="text/css" href="../../e5style/work.css">
<script type="text/javascript" src="../../e5script/Function.js"></script>
</HEAD>
<BODY onbeforeunload="return unLoadWindow();" onconextmenu="return false;" style="overflow:auto;">
<IFrame Name="iframe" ID="iframe" SRC="" Style="display:none" ></IFrame>
<div id="nameDiv"><i18n:message key="manoeuvre.transfer.direction"/></div>
<Form Method="Post" Action="Manoeuvre.do" Name="form" target="iframe">
		<Input Name="invoke" value="doTransfer" type="Hidden">
		<Input Name="TargetFvID" Value="<c:out value="${offerkey.FVID}"/>" Type="Hidden">
		<Input Name="TargetLibID" Value="<c:out value="${offerkey.docLibID}"/>" Type="Hidden">
		<Input Name="FLOW_HANDLERID" Value="" Type="Hidden">
		<Input Name="FLOW_HANDLERNAME" Value="" Type="Hidden">
		<Input Name="DocLibID"	Value="<c:out value="${offerkey.docLibID}"/>" Type="Hidden">
		<Input Name="DocIDs"	Value="<c:out value="${offerkey.docIDs}"/>" Type="Hidden">
		<Input Name="UUID"	Value="<c:out value="${offerkey.UUID}"/>" Type="Hidden">
		<Input Name="DocTypeID" value="<c:out value="${offerkey.docTypeID}"/>" Type="Hidden">

<table width="100%"  border="0" cellspacing="1" cellpadding="0">
  <tr>
	  <td width="80"><i18n:message key="manoeuvre.transfer.advisory"/></td>
	  <td><input name="Opinion" type="text" class="field" size="50"></td>
  </tr>
  <tr>
	  <td height="50" align="center" colspan="2">
		  <input type="button" class="button" onClick="validate();" value="<i18n:message key="manoeuvre.transfer.submit"/>"/>
		  <input type="button" class="button" onClick="closeWindow();" value="<i18n:message key="manoeuvre.transfer.cancel"/>"/>
	  </td>
  </tr>
</table>

</Form>
<Script type="text/javascript">
	function validate(){
		if (((String)(form.TargetFvID.value)=="") ||
			((String)(form.TargetLibID.value)=="")){
			alert("<i18n:message key="manoeuvre.transfer.form.alert"/>");
			return false;
		}
		if(getLength(form.Opinion.value)>254)
		{
			alert("<i18n:message key="manoeuvre.transfer.advisory.alert"/>");
			return false;
		}

		form.submit();
	}

	var bClose=false;
	function closeWindow(){
		if(bClose)
			return;
		form.invoke.value="doCancel";
		form.submit();
		bClose=true;
	}
	function unLoadWindow()
	{
		if(!bClose)
		{
			iframe.location.href = "Manoeuvre.do?invoke=doCancel&UUID="+form.UUID.value;
		}
	}

	function getDealer(){
		if(iframe.selid=="")
		{
			form.selDealer.checked=false;
			alert("<i18n:message key="manoeuvre.transfer.list.user.alert"/>");
			return;
		}
		if (form.selDealer.checked)
		{
			window.frames("dealerFrame").Dealer.disabled= false;
		}
		else
		{
			window.frames("dealerFrame").Dealer.disabled= true;
		}
	}
</Script>
