<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />

<%

String strDocTypeID = request.getParameter("docTypeID");
String strType = request.getParameter("type");
String strGroup = request.getParameter("group");

%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>

<meta name="author" content="<i18n:message key='e5dom.Author' />" />
<meta name="description" content="" />
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<link rel="stylesheet" rev="stylesheet" href="css/filterlist.css" type="text/css" media="all" />

<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
<script language="javascript"><!--

function iniFilterList() {
	var docTypeID = parseInt(<%=strDocTypeID%>);
	$.ajax({
		url : "FilterController.do?invoke=getFilters&docTypeID=" + docTypeID,
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
					op.value = datas[i].filterID;
					op.text = datas[i].filterName;
					document.getElementById("FilterList").options.add(op);
				}
			}
		}
	});
}

function addFilters() {
	var optionz = document.getElementById("FilterList").options;
	var selectedOpz = new Array();
	for(var i=0;i<optionz.length;i++)
	{
		var op = optionz[i];
		if(op.selected)
		{
			selectedOpz.push(op);
		}
	}
	window.parent.page.handlers.AssignFiltersOK(selectedOpz,"<%=strType%>","<%=strGroup%>");
	window.parent.e5.dialog.close("AssignFilters");
}

function MoveUp()
{
	var selectfilter = document.getElementById("FilterList");

    var tempValue="",tempText="";
    if(selectfilter.selectedIndex>0)
    {
        for(var i=1;i<selectfilter.length;i++)
        {
            if(selectfilter.options.item(i).selected==true)
            {
                 tempValue=selectfilter.options.item(i).value;
                 tempText=selectfilter.options.item(i).text;

                 selectfilter.options.item(i).value=selectfilter.options.item(i-1).value;
                 selectfilter.options.item(i).text=selectfilter.options.item(i-1).text;

                 selectfilter.options.item(i-1).value=tempValue;
                 selectfilter.options.item(i-1).text=tempText;

                 selectfilter.options.item(i-1).selected=true;
                 selectfilter.options.item(i).selected=false;
            }
        }
    }

}

function MoveDown()
{
    var tempValue="",tempText="";
	
	var selectfilter = document.getElementById("FilterList");
    var lastSelectedIndex=selectfilter.length;

    for(var i=selectfilter.length-1;i>=0;i--)
    {
        if(selectfilter.options.item(i).selected==true)
        {
            lastSelectedIndex=i;
            break;
        }
    }

    if(lastSelectedIndex<selectfilter.length-1)
    {
        for(var i=selectfilter.length-2;i>=0;i--)
        {
            if(selectfilter.options.item(i).selected==true)
            {
                 tempValue=selectfilter.options.item(i).value;
                 tempText=selectfilter.options.item(i).text;

                 selectfilter.options.item(i).value=selectfilter.options.item(i+1).value;
                 selectfilter.options.item(i).text=selectfilter.options.item(i+1).text;

                 selectfilter.options.item(i+1).value=tempValue;
                 selectfilter.options.item(i+1).text=tempText;

                 selectfilter.options.item(i+1).selected=true;
                 selectfilter.options.item(i).selected=false;
            }
        }
    }
}

function MoveToTop()
{
	var selectfilter = document.getElementById("FilterList");
    while(selectfilter.selectedIndex>0)
    {
        MoveUp();
    }
}

function MoveToBottom()
{
	var selectfilter = document.getElementById("FilterList");
	var lastSelectedIndex=selectfilter.length;
    do
    {
	    MoveDown();
    	for(var i=selectfilter.length-1;i>=0;i--)
    	{
        	if(selectfilter.options.item(i).selected==true)
        	{
            	lastSelectedIndex=i;
            	break;
        	}
    	}

    }while(lastSelectedIndex<selectfilter.length-1);
}

--></script>

</head>

<body onload="iniFilterList();">
<br>
<table cellpadding="10" cellspacing="0" class="table">
<caption><i18n:message key="e5dom.Folder.ChooseFilters"/></caption>
  <tr>
    <td width="50%">
		<select style="width:260px;" id="FilterList" name="selectfilter" multiple="multiple" size="10"></select>
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
		<input class="button" type="button"  onclick="addFilters();" value="<i18n:message key="e5dom.Folder.AddFilters"/>">
		<input type="reset" class="button"  onclick="window.parent.e5.dialog.close('AssignFilters');" name="Submit2" value="<i18n:message key="e5dom.Folder.Cancel"/>">
    </td>
  </tr>
</table>
</body>
</html>
