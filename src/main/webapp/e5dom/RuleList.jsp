<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />
<%
String strDocTypeID = request.getParameter("docTypeID");
String strType = request.getParameter("type");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>

<meta name="author" content="<i18n:message key='e5dom.Author' />" />
<meta name="description" content="" />
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<link rel="stylesheet" rev="stylesheet" href="css/rulelist.css" type="text/css" media="all" />
<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>

<script language="javascript">

function iniRuleList() {
	var docTypeID = parseInt(<%=strDocTypeID%>);
	$.ajax({
		url : "RuleController.do?invoke=getRules&docTypeID=" + docTypeID,
		dataType : "json",
		async : false,
		success : function(data) {
			if (data != "-1") {
				var datas = new Array();
				if (!$.isArray(data)) {
					datas.push(data);
				} else {
					datas = data;
				}
				for(var i=0;i<datas.length;i++)
				{
					var op = document.createElement("option");
					op.value = datas[i].ruleID;
					op.text = datas[i].ruleName;
					document.getElementById("RuleList").options.add(op);
				}
			}
		}
	});
}

function addRules() {
	var optionz = document.getElementById("RuleList").options;
	var selectedOpz = new Array();
	for(var i=0;i<optionz.length;i++)
	{
		var op = optionz[i];
		if(op.selected)
		{
			selectedOpz.push(op);
		}
	}
	window.parent.page.handlers.AssignRulesOK(selectedOpz,"<%=strType%>");
	window.parent.e5.dialog.close('AssignRules');
}

function MoveUp()
{
    var tempValue="",tempText="";
    if(selectrule.selectedIndex>0)
    {
        for(var i=1;i<selectrule.length;i++)
        {
            if(selectrule.options.item(i).selected==true)
            {
                 tempValue=selectrule.options.item(i).value;
                 tempText=selectrule.options.item(i).text;

                 selectrule.options.item(i).value=selectrule.options.item(i-1).value;
                 selectrule.options.item(i).text=selectrule.options.item(i-1).text;

                 selectrule.options.item(i-1).value=tempValue;
                 selectrule.options.item(i-1).text=tempText;

                 selectrule.options.item(i-1).selected=true;
                 selectrule.options.item(i).selected=false;
            }
        }
    }

}

function MoveDown()
{
    var tempValue="",tempText="";
    var lastSelectedIndex=selectrule.length;

    for(var i=selectrule.length-1;i>=0;i--)
    {
        if(selectrule.options.item(i).selected==true)
        {
            lastSelectedIndex=i;
            break;
        }
    }

    if(lastSelectedIndex<selectrule.length-1)
    {
        for(var i=selectrule.length-2;i>=0;i--)
        {
            if(selectrule.options.item(i).selected==true)
            {
                 tempValue=selectrule.options.item(i).value;
                 tempText=selectrule.options.item(i).text;

                 selectrule.options.item(i).value=selectrule.options.item(i+1).value;
                 selectrule.options.item(i).text=selectrule.options.item(i+1).text;

                 selectrule.options.item(i+1).value=tempValue;
                 selectrule.options.item(i+1).text=tempText;

                 selectrule.options.item(i+1).selected=true;
                 selectrule.options.item(i).selected=false;
            }
        }
    }
}

function MoveToTop()
{
    while(selectrule.selectedIndex>0)
    {
        MoveUp();
    }
}

function MoveToBottom()
{
	var lastSelectedIndex=selectrule.length;
    do
    {
	    MoveDown();
    	for(var i=selectrule.length-1;i>=0;i--)
    	{
        	if(selectrule.options.item(i).selected==true)
        	{
            	lastSelectedIndex=i;
            	break;
        	}
    	}

    }while(lastSelectedIndex<selectrule.length-1);
}

</script>

</head>

<body onload="iniRuleList();">
<br>
<table cellpadding="10" cellspacing="0" class="table">
<caption><i18n:message key="e5dom.Folder.ChooseRules"/></caption>
  <tr>
    <td width="50%">
        <select id="RuleList" style="width:260px" name="selectrule" multiple="multiple" size="10"></select>
    </td>
    <td>
		<input class="bluebutton" type="button" onclick="MoveToTop()" name="buttonMoveTop" value="<i18n:message key="e5dom.Folder.Head"/>">
		<br/><br/>
		<input class="bluebutton" type="button" onclick="MoveUp()" name="buttonMoveUp" value="<i18n:message key="e5dom.Folder.Up"/>">
		<br/><br/>
		<input class="bluebutton" type="button" onclick="MoveDown()" name="buttonMoveDown" value="<i18n:message key="e5dom.Folder.Down"/>">
		<br/><br/>
		<input class="bluebutton" type="button" onclick="MoveToBottom()" name="buttonMoveBottom" value="<i18n:message key="e5dom.Folder.Bottom"/>">
    </td>
  </tr>
  <tr>
    <td colspan="2" align="center">
      <div align="center">
		<input class="button" type="button" value="<i18n:message key="e5dom.Folder.AddRules"/>" onclick="addRules();">
        <input class="button" type="reset" onclick="window.parent.e5.dialog.close('AssignRules');" name="Submit2" value="<i18n:message key="e5dom.Folder.Cancel"/>">
      </div>
    </td>
  </tr>
</table>
</body>
</html>
