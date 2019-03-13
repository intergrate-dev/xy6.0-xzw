<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title>addIcon</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			th{width:100px;}
			input[type='file']{width:500px;}
		</style>
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script type="text/javascript">
			var count = 1;
			function subform(){
				if (getLength(form1.format.value)>8) {
					alert('<i18n:message key="operation.icon.format"/><i18n:message key="operation.common.prelength"/>8<i18n:message key="operation.common.afterchars"/>');
					return false;
				}
				if(getLength(form1.size.value)>20) {
					alert('<i18n:message key="operation.icon.size"/><i18n:message key="operation.common.prelength"/>20<i18n:message key="operation.common.afterchars"/>');
					return false;
				}
				if(getLength(form1.description.value)>40*3) {
					alert('<i18n:message key="operation.icon.description"/><i18n:message key="operation.common.prelength"/>40<i18n:message key="operation.common.afterlength"/>');
					return false;
				}
				
				var errMsg = '';
				if (form1.upList.options.length == 0)
					errMsg += '<i18n:message key="operation.icon.fileName"/>,';
						
				if (!form1.format.value) {
					errMsg += '<i18n:message key="operation.icon.format"/>,';
				}
				if (!form1.size.value) {
					errMsg += '<i18n:message key="operation.icon.size"/>,';
				}
				
				if (!errMsg)
					form1.submit();
				else{
					alert(errMsg+'<i18n:message key="operation.common.script"/>');
					return false;
				}
			}
			//删除一个列表中的项目
			function ondelete(){
				var optionDel = form1.upList.value;
				
				var curObj = null;
				for (var i = 1; i < count; i++) {
					var aFile = document.getElementById("file" + i);
					if (aFile && aFile.value == optionDel) {
						var node1 = document.getElementById("append1");
						node1.removeChild(aFile);
						break;
					}
				}
				form1.upList.remove(form1.upList.selectedIndex);

				form1.delbtn.disabled = true;
				return false;
			}
			//列表发生变化时，触发“删除”按钮
			function candel(src){
				if (form1.upList.value) {
					form1.delbtn.disabled = false;
				}
			}
			//加入列表
			function append2(){
				try{
					var currObj = document.getElementById("file" + count);
					for (var i = 0; i < form1.upList.options.length; i++) {
						if (currObj.value==form1.upList.options[i].value) {
							alert('<i18n:message key="operation.listIcon.already"/>');
							return false;
						}
					}
					//不允许上传非图标类型的文件
					var file = currObj.value;
					var pos = file.lastIndexOf(".");
					var suffix = "";
					if (pos >= 0) {
						suffix = file.substring(pos + 1).toLowerCase();
					}
					if (suffix != "jpg" && suffix != "gif" && suffix != "png") {
						alert("INVALID FILE!");
						return false;
					}
					
					form1.upList.options[form1.upList.options.length] = new Option(currObj.value,currObj.value);
					currObj.style.display = "none";

					//创建一个新的input type='file'对象					
					count++;
					var fileNew = document.createElement("input");
					fileNew.type = "file";
					fileNew.name = "file" + count;
					fileNew.id = "file" + count;
					fileNew.setAttribute("accept", "image/gif,image/jpeg,image/png");
					
					var span1 = document.getElementById("append1");
					span1.appendChild(fileNew);
				} catch(e){
					alert(e.message);
				}
			}
			//清空
			function clearlist(){
				if (count == 1) return;

				var node1 = document.getElementById("append1");
				while (count > 1) {
					var aFile = document.getElementById("file" + count);
					if (aFile)
						node1.removeChild(aFile);
					count--;
				}
				
				form1["file1"].style.display = "block";
				form1.upList.options.length = 0;
			}
			//返回
			function goBack(){
				window.location.href="listIcon.do";
			}
		</script>
	</head>
	<body>
		<form name="form1" method="post" action="IconSubmit.do" enctype="multipart/form-data">
			<div class="mainBodyWrap">
				<table cellpadding="5" cellspacing="0" border="0" class="table">
					<caption><i18n:message key="operation.addIcon.contentTitle"/></caption>
					<tr>
						<th align="center" width="20%"><i18n:message key="operation.icon.format"/></th>
						<td colspan="2"><input type="text" name="format" value="GIF" /><i18n:message key="operation.common.prelength"/>8<i18n:message key="operation.common.afterchars"/></td>
					</tr>
					<tr>
						<th align="center"><i18n:message key="operation.icon.size"/></th>
						<td colspan="2"><input type="text" name="size" value="20*20" /><i18n:message key="operation.common.prelength"/>20<i18n:message key="operation.common.afterchars"/></td>
					</tr>
					<tr>
						<th align="center"><i18n:message key="operation.icon.description"/></th>
						<td colspan="2"><input type="text" name="description" value="" /><i18n:message key="operation.common.prelength"/>40<i18n:message key="operation.common.afterlength"/></td>
					</tr>
					<tr>
						<th align="center"><i18n:message key="operation.icon.selfile"/></th>
						<td width="500">
							<span ID="append1">
								<input type="file" name="file1" id="file1" accept="image/gif,image/jpeg,image/png"/>
							</span>
						</td>
						<td>
							<button class="button" id="addbtn" onclick="append2();return false;"><i18n:message key="operation.addIcon.another"/></button>
							<button class="button" id="delbtn" onclick="ondelete();return false;" disabled><i18n:message key="operation.icon.delete"/></button>
						</td>
					</tr>
					<tr>
						<th align="center"><i18n:message key="operation.listIcon.selected"/></th>
						<td colspan=2>
							<SELECT Name="upList" size=10 style="width:500px" MULTIPLE  onchange="candel(this)">
							</SELECT>
						</td>
					</tr>
					<tr>
					<th style="display:none" align="center"><i18n:message key="operation.icon.url"/></th>
						<td style="display:none"><input type="text" name="url" value="" /></td>
					</tr>
					<tr>
						<td colspan=3 align="center">
							<button type="button" class="button" onclick="subform();"><i18n:message key="operation.icon.submit" /></button>
							<button type="button" class="button" onclick="clearlist();"><i18n:message key="operation.icon.reset" /></button>
							<button type="button" class="button" onclick="goBack();"><i18n:message key="operation.icon.return" /></button>
						</td>
					</tr>
				</table>
			</div>
		</form>
	</body>
</html>
