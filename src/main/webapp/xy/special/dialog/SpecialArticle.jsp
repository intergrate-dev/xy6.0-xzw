<!DOCTYPE html>
<%@include file="../../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
</head>
<style type="text/css">
	.wrapper{
		width:900px;
	}
	.left{
		width:196px;
		float: left;
	}
	.right{
		width:700px;
		float: left;
	}
	#frmRight{
		width:700px;
		height:640px;
	}
	.title{
		display: block;
		margin-top:12px;
		width:875px;
		height:50px;
		border: 1px solid #ddd;
		margin-left:2px;
		overflow: visible;
	}
	.spanbar{
		display:inline-block;
		float:left;
		height:30px;
		width:80px;
		font-family:'Arial Negreta', 'Arial';
		padding-top:12px;
		padding-left:12px;
		font-weight:700;
		font-size:16px;
	}
	.rightBtn{
		display:inline-block;
		float:right;
		margin-top:12px;
		margin-right:10px;
	}
	.showDiv {
		position : absolute;
		z-index: 100;
		width: 700px;
		height: 520px;
		top:60px;
		left:80px;
		background:#ffffff;
		color:#FFF;
		border:1px solid #666;
		opacity: 1;
		-moz-opacity: 1;
		filter: alpha(opacity=100);
	}
	.dosure {
		background: #00a0e6;
	}
	#frmRight{
		border:0px;
	}
	.imglist{
		height:100%;
		width:100%;
		overflow-y:auto;
	}
	.hidden{
		display:none;
	}
	td{
		color: #000000;
	}
	.td_style{
		white-space: nowrap;
	}
	.field_1{
		width: 50px;
	}
	.field_2{
		width: 255px;
	}
	.field_3{
		width: 80px;
	}
	.field_4{
		width: 30px;
	}
	#listing_ table tr th{
		color: #000;
	}
	.edit_class{
		display: inline-block;
		position: absolute;
		left: 490px;
		background-color: #ffffff;
	}
	.edit-dialog{
		position: absolute;
		z-index:200;
		width: 300px;
		left:180px;
		top:100px;
		background:#f7f6f0;
		border: 1px solid #fff3f3;
		-moz-border-radius: 5px;
		-webkit-border-radius: 5px;
		-khtml-border-radius: 5px;
		border-radius: 5px;
		line-height: 30px;
		list-style: none;
		padding: 5px 10px;
		margin-bottom: 2px;
	}
	.editbar{
		display: inline-block;
		position: relative;
		z-index: 5;
		width: 200px;
		height:16px;
		background-color: #C4CFBB;
		top:0px;
		left:0px;
	}
	.edit_bar_class,.delete_bar_class{
		display: inline-block;
		margin-left: 10px;
		float: right;
	}
	.selectSpan{
		display: inline-block;
		margin-top: 15px;
		cursor: pointer;
	}
	.isClicked{
		-webkit-transform: rotateZ(180deg);
		-moz-transform: rotateZ(180deg);
		-o-transform: rotateZ(180deg);
		-ms-transform: rotateZ(180deg);
		transform: rotateZ(180deg);
	}
</style>
<link rel="stylesheet" type="text/css" href="../../css/main.css"/>
<script type="text/javascript">
	var style = "radio";
</script>
<body style="overflow: hidden;">
<div class="wrapper">
	<div class="div title">
		<div id="u5198" class="text" style="visibility: visible;">
			<label id="checkedSpan"  class="spanbar">已选(0)</label><span class="selectSpan"><img id="checkedIcon" class="" src="../export/img/u4770.png"/></span>
			<input class="btns docancle rightBtn" type='button' id="doClose" value='关闭'/>
			<input class="btns docancle rightBtn" type='button' id="doCancel" value='清空'/>
			<input class="btns dosure rightBtn" type='button' id="doSave" value='确定'/>
		</div>
		<div id="showWapper" class="showDiv hidden">
			<div id="doclistframe_" class="doclistframe imglist"></div>
			<fieldset id="edit-dialog" class="edit-dialog hidden">
				<label class="" style="color: #000">编辑标题</label>
				<textarea id="artitle-title" data-type="" data-id="" cols="40" rows="10" class="article-title">
				</textarea><br/>
				<input type="button" id="del-html" value="去除html"/>
				<input type="button" id="commit-title" value="确定"/>
				<input type="button" id="edit-conncel" value="取消"/>
			</fieldset>

		</div>
	</div>
	<div class="left">
		<%@include file="ColumnAggregate.jsp" %>
	</div>
	<div class="right">
		<iframe id="frmRight" name="frmRight"></iframe>
	</div>
</div>

<script type="text/javascript" src="../../../e5script/e5.min.js"></script>
<script type="text/javascript" src="../../script/columnColor.js"></script>
<script type="text/javascript">
	var idsArr=[];
	var dataArr={};
	var ch;
	var docLibID=1;
	var artTitle={};
	$(function(){
		//向前台发送已选稿件的json数据
		function doSave(){
            var idsArr=$("#checkedSpan").attr("ids");
			if(!idsArr || idsArr.length<1) return;
			var url = "../../../xy/special/getArticlesJson.do?docLibID="+docLibID
					+"&docIDs="+idsArr+"&ch="+ch+"&style="+type;
			$.ajax({
				type:'get',
				url:url,
				success:function(data){
					var jsonArray=JSON.parse(data);
					//设置成新修改的标题
					$.each(jsonArray,function(){
						var id = this.id;
						if(artTitle[id]&&artTitle[id]!=null){
							this.title = artTitle[id];
						}
					});
					if(type==0){
						parent.window.LEDialog.dialogConfirm(jsonArray);
					}else if(type==1){
						parent.window.LEDialog.dialogConfirm(jsonArray);
					}
				}
			});
		};
		//设置已选稿件数
		function setArtCounts(){
            var idsArr=$("#checkedSpan").attr("ids");
            if(idsArr){
                idsArr=$("#checkedSpan").attr("ids").split(",");
            }else{
                idsArr=[];
            }
			$("#checkedSpan").text("已选("+idsArr.length+")");
		}
		//清空所有已选内容
		function doClean(){
			idsArr = [];
			dataArr = {};
            $("#checkedSpan").attr("ids","");
            $("#checkedSpan").attr("htmls","");
			setArtCounts();
			cleanList();

			cleanShowDiv();
		};
		//重新加载iframe中的列表
		function cleanList(){
			var columnID = window.frames["frmColumn"].getFirstNodeId();
			var url = "../../../xy/SpecialArticle.do?ch=" + ch
							+ "&colID="+columnID
							+ "&siteID=" + siteID
							+ "&type=" + type
					;
			window.frames["frmRight"].location.href = url;
		}
		//加载图册已选列表
		function docListWithImg(){
            var idsArr=$("#checkedSpan").attr("ids");
                if(idsArr){
                    idsArr=$("#checkedSpan").attr("ids").split(",");
                }else{
                    idsArr=[];
                }
            //var dataArr=$("#checkedSpan").attr("htmls").split(",");
			var dataArr=JSON.parse($("#checkedSpan").attr("htmls"));
			var spanlist = $('<div id="listing_" class="" album="true" oncontextmenu="return false"></div>');
			for(var i=0;i<idsArr.length;i++){
				var key = idsArr[i];
				var htm = dataArr[key];
				var table = $('<table class="album_table" data-type="1" data-id="'+key+'" id="' + key + '_" libid="' + docLibID +'"></table>').html(htm);
				var url = table.find("img")[0].src;
				var path = url.split("?")[1];
				imgPath = "../../../xy/image.do?"+path;
				table.find("img")[0].src=imgPath;
				var img1 = table.find("img")[1].src;
				img1path = "../../../Icons/"+img1.substring(img1.lastIndexOf("/"),img1.length);
				table.find("img")[1].src=img1path;
				var img2 = table.find("img")[2].src;
				img2path = "../../../Icons/"+img2.substring(img2.lastIndexOf("/"),img2.length);
				table.find("img")[2].src=img2path;

				var tspan = table.find("span[title]");
				tspan.attr("id",key+"_title");
				tspan.attr("data-id",key);
				tspan.attr("data-type","1");
				//列表显示编辑后的title
				if(artTitle[key] && artTitle[key]!=null){
					tspan.attr("title",artTitle[key]);
					tspan.html(artTitle[key]);
				}
				//增加编辑条
				var editbar = $('<span id="editbar" class="editbar"></span>');
				var close_bar=$('<img id="'+key+'_del" class="delete_bar_class" data-type="1" data-id="'+key+'" src="../../../Icons/del.gif"/>');
				var edit_bar=$('<img id="'+key+'_edit" class="edit_bar_class" data-type="1" data-id="'+key+'" src="../../../Icons/edit.gif"/>');
				editbar.append(close_bar);
				editbar.append(edit_bar);

				table.append(editbar);
				spanlist.append(table);
			}
			$("#doclistframe_").html(spanlist);
			//绑定删除事件
			bindListDeleteClick();
			//绑定编辑标题事件
			bindListEditClick();
		}
		//加载已选列表
		function docList(){
            var idsArr=$("#checkedSpan").attr("ids");
            if(idsArr){
                idsArr=$("#checkedSpan").attr("ids").split(",");
            }else{
                idsArr=[];
            }

            //var dataArr=$("#checkedSpan").attr("htmls").split(",");
			var dataArr=JSON.parse($("#checkedSpan").attr("htmls"));
			var spanlist = $('<div id="listing_" class="" album="true" oncontextmenu="return false"></div>');
			var tableTh = $('<table id="tablePinHeader_" cellpadding="0" cellspacing="0" class="doclist"></table>');
			var tableHead = $(getTableTH());
			var TH={};
			var ths = tableHead.find("th");
			$.each(ths,function(index,item){
				var th = $(item);
				var id = th.attr("id");
				if(id == 'TH_a_status'){
					TH.th_a_status=index;
				}else if(id == 'TH_a_type'){
					TH.th_a_type=index;
				}else if(id == 'TH_a_linkTitle'){
					TH.th_a_linktitle=index;
				}else if(id == 'TH_SYS_LASTMODIFIED'){
					TH.th_sys_lastmodified=index;
				}else if(id == 'TH_SYS_TOPIC'){
					TH.th_sys_topic=index;
				}
			});
			var opt = $('<th id="op_delete" style="width: 30px">操作</th>');
		    tableHead.append(opt);
			tableTh.html(tableHead);
			var table = $('<table cellpadding="0" cellspacing="0" class="doclist"><tbody></tbody></table>');
			table.append(tableHead);
			for(var i=0;i<idsArr.length;i++){
				var key = idsArr[i];
				var tr = $('<tr></tr>').html(dataArr[key]);
				var imgs = tr.find("img");
				$.each(imgs,function(index,item){
					var img = $(item);
					var imgpath = img.attr("src");
					var newPath = "../../../Icons/"+imgpath.substring(imgpath.lastIndexOf("/"),imgpath.length);
					img.attr("src",newPath);
				});
				var op = $('<td><span  id="'+key+'_del" data-type="2" data-id="'+key+'" class="delete_class">删除</span></td>');
				var tds = tr.find("td");
				//清除原有的行内样式，设置新的class
				$.each(tds,function(index,item){
					var td = $(item);
				/*	td.removeAttr("style");
					td.addClass("td_style");
					td.addClass("field_"+(index+1));*/
					if(index==TH.th_a_linktitle||index==TH.th_sys_topic){
						var title = td.html();
						var tspan = $('<span id="'+key+'_title"><span>').html(title);
						td.html(tspan);
						//如果标题编辑过则显示编辑后的标题
						if( artTitle[key] && artTitle[key]!=null){
							tspan.html(artTitle[key]);
						}

						/*//给标题加编辑图标
						var edit = $('<img id="'+ key + '_edit" data-type="2" data-id="'+key+'" class="edit_class" src="../../../Icons/edit.gif"/>');
						td.append(edit);*/
					}
				});
				tr.append(op);
				tr.unbind('click');
				table.append(tr);

			}
			spanlist.append(table);
			$("#doclistframe_").html(spanlist);
			//绑定删除事件
			bindDeleteClick();
			//绑定编辑标题事件
			bindEditClick();
		}
		//给已选图册列表绑定delete事件
		function bindListDeleteClick(){
			var dels = $('#listing_ table span img[class="delete_bar_class"]');
			$.each(dels,function(){
				$(this).click(listDeleteOne);
			});

		}
		//已选图册列表绑定edit事件
		function bindListEditClick(){
			var edits = $('#listing_ table span img[class="edit_bar_class"]');
			$.each(edits,function(){
				$(this).click(function(){
					var id = $(this).attr('data-id');
					var type = $(this).attr('data-type');
					var title = getArticleTitle(id,type);
					openEditForm(id,type,title);
				});
			});
		}
		//已选列表删除响应事件
		function listDeleteOne(){
            var idsArr=$("#checkedSpan").attr("ids").split(",");
            //var dataArr=$("#checkedSpan").attr("htmls").split(",");
			var dataArr=JSON.parse($("#checkedSpan").attr("htmls"));

			var src = event.target;
			var id = $(src).attr("data-id");
			var type = $(src).attr("data-type");
			var index = getIndex(idsArr, id);

			idsArr.splice(index, 1);
			dataArr[id] = null;
			artTitle[id] = null;
            $("#checkedSpan").attr("ids",idsArr);
            $("#checkedSpan").attr("htmls",JSON.stringify(dataArr));
			//修改已选稿件数
			setArtCounts();
			//重新加载列表
			cleanAndLoad(type);
		}
		//清空并重新加载列表
		function cleanAndLoad(type){
			//清空列表
			$("#doclistframe_").html("");
			//重新加载列表
			if(type==1){
				docListWithImg();
			}else if(type==2){
				docList();
			}
		}
		//已选列表方式绑定删除点击事件
		function bindDeleteClick() {
			var dels = $('#listing_ table tr td span[class="delete_class"]');
			$.each(dels, function () {
				$(this).click(listDeleteOne);
			});

		}
		//已选列表绑定编辑稿件标题点击事件
		function bindEditClick(){
			var edits = $('#listing_ table tr td img[class="edit_class"]');
			$.each(edits,function(){
				$(this).click(function(){
					var id = $(this).attr('data-id');
					var type = $(this).attr('data-type');
					var title = getArticleTitle(id,type);
					openEditForm(id,type,title);
				});
			});
		}
		//获取元素在数组中的下标
		function getIndex(arr,id){
			for (var i = 0; i < arr.length; i++)
				if (arr[i] == id) return i;
			return -1;
		}
		function cleanShowDiv(){
			$("#doclistframe_").html("");
			$("#showWapper").addClass("hidden");
			$("#checkedIcon").removeClass("isClicked");
		}
		//已选标签点击事件
		function seleckedLabelClick(e){
			var source = e.target;
			var src = $(source);
			if(src.hasClass("isClicked")){
				cleanShowDiv();
			}else{
				if(type==0){
					docListWithImg();
				}else if(type==1){
					docList();
				}
				$("#showWapper").removeClass("hidden");
				src.addClass("isClicked");
			}
		}


		//获取列表标题
		function getArticleTitle(id,type){
			var title = $("#"+id+"_title").html();
			return title;
		}
		//设置新的标题
		function setArticleTitle(id,type,title){
			if(type==2){
				$('#'+id+'_title').html(title);
				//重新加载列表
				//cleanAndLoad(type);
			}else if(type==1){
				var title_elm = $('#'+id+'_title');
				title_elm.attr("title",title);
				title_elm.html(title);
			}else if(type==0){
				return;
			}
		}

		/*编辑事件开始*/
		//去除html
		function delHtmlTag(str){
			return str.replace(/<[^>]+>/g,"");//去掉所有的html标记
		}
		//打开编辑框
		function openEditForm(id,type,title){
			var dialog_title = $('#artitle-title');
			if(id&&id!=null&&id!=""){
				dialog_title.attr("data-id",id);
			}else{
				dialog_title.attr("data-id",0);
			}
			if(type&&type!=null&&type!=""){
				dialog_title.attr("data-type",type);
			}else{
				dialog_title.attr("data-type",0);
			}
			if(title&&title!=null&&title!=""){
				dialog_title.val(title);
			}
			$('#edit-dialog').removeClass("hidden");
		}
		//去除标题中html事件
		function clearHtml(){
			var old = $('#artitle-title').val();
			old = old.replace(/&lt;/g,"<");
			old = old.replace(/&gt;/g,">");
			$('#artitle-title').val(delHtmlTag(old));
		}
		//编辑确定事件
		function commitBtnClick(){
			var elm = $('#artitle-title');
			var title = elm.val();
			var id = elm.attr("data-id");
			var type = elm.attr("data-type");
			//将新标题存入数组
			artTitle[id]=title;
			//修改列表中的标题
			setArticleTitle(id,type,title);
			//关闭编辑框
			closeEditForm();

		}
		//取消编辑事件
		function conncelEdit() {
			closeEditForm();
		}
		//关闭编辑框
		function closeEditForm(){
			var dialog_title = $('#artitle-title');
			dialog_title.attr("data-id",0);
			dialog_title.attr("data-type",0);
			dialog_title.val("");
			$('#edit-dialog').addClass("hidden");
		}
		/*编辑事件结束*/

		function closeDialog(){
			parent.window.LEDialog.closeDialog();
		}
		//ajax根据前台传的ID取完整的数据，
		function getFrontPageData(){
			var docIDs = getData();
			if(docIDs==null||docIDs=="")return;
			var url = "../../../xy/special/getArticlesJson.do?docLibID="+docLibID
					+"&docIDs="+docIDs+"&ch="+ch+"&style=2";
			$.ajax({
				type:'get',
				url:url,
				success:function(data){
					putFrontPageData(data);
				}
			});
		}
		//将前台数据放入已选列表中
		function putFrontPageData(data){
			var jsonArray = JSON.parse(data);
			if(type==0){
				$.each(jsonArray,function(index,value){
					idsArr.push(value.id);
					putImgListOneData(value);
				});
			}else if(type==1){
				$.each(jsonArray,function(index,value){
					idsArr.push(value.id);
					putListOneData(value);
				});
			}
			
			//设置已选稿件数
			setArtCounts();
		}

		function getIconByPubStaus(status){
			var result = "";
//			if(status == 1){
				result += '<img src="../Icons/pubed.png" title="已发布"/>';
//			}
			return result;
		}
		function getIconByType(type){
			var result = '';
			if(type==0){
				result += '<img src="../Icons/article.png" title="文章"/>'
			}else if(type==1){
				result += '<img src="../Icons/pic.png" title="组图"/>'
			}else if(type==2){
				result += '<img src="../Icons/video2.png" title="视频"/>'
			}else if(type==3){
				result += '<img src="../Icons/special.png" title="专题"/>'
			}else if(type==4){
				result += '<img src="../Icons/link.png" title="链接稿"/>'
			}else if(type==5){
				result += '<img src="../Icons/multi.png" title="多标题稿"/>'
			}else if(type==6){
				result += '<img src="../Icons/live.png" title="直播稿"/>'
			}else if(type==7){
				result += '<img src="../Icons/activity.png" title="活动稿"/>'
			}else if(type==8){
				result += '<img src="../Icons/ad.png" title="广告"/>'
			}
			return result;
		}
		function putImgListOneData(data){
			var table = $('<table><tbody></tbody></table>');
			var result = '<tbody>';

			result += '<tr>';
			result += '<td colspan="2" id="a_picBig" style="white-space:nowrap;width:177px;" class=" album_image_td" align="center">';
			if(data.bigPic==null||data.bigPic==""){
				result += '<img src="../xy/image.do?path="/>'
			}else{
				result += '<img src="../xy/image.do?path='+data.bigPic+'"/>'
			}
			result += '</td>';
			result += '</tr>';

			result += '<tr>';
			result += '<td id="a_status" style="white-space:nowrap;width:88px;">';
			result += '<span id="VALUE_a_status">';
			result += getIconByPubStaus(data.pubStatus);
			result += '</span>';
			result += data.editor;
			result += '</td>';
			result += '<td id="a_pubTime" style="white-space:nowrap;width:87px;">';
			result += data.pubTime;
			result += '</td>';
			result += '</tr>';

			result += '<tr>';
			result += '<td id="a_linkTitle" colspan="2" style="white-space:nowrap;width:177px;">';
			result += '<span id="VALUE_a_type">';
			result += getIconByType(data.type);
			result += '</span>';
			result += '<span title="'+data.linkTitle+'">'+data.linkTitle+'</span>';
			result += '</td>';
			result += '</tr>';
			result += '</tbody>';
 			dataArr[data.id]=result;
		}

		function putListOneData(data){
			var result = '<td style="white-space:nowrap;">'
			result += '<span id="VALUE_a_status">';
			result += getIconByPubStaus(data.pubStatus);
			result += '</span><span></span>';
			result += '</td>';

			result += '<td>';
			result += data.linkTitle;
			result += '</td>';

			result += '<td>';
			result += data.modifyTime;
			result += '</td>';
			dataArr[data.id]=result;
		}

		function getTableTH(){
			var document = window.frames["frmRight"].document;
			var tablePinHeader = $("#tablePinHeader tbody",document);
			return tablePinHeader.html();
		}

		$("#doSave").click(doSave);
		$("#doCancel").click(doClean);
		//点击已选的功能
		$("#checkedIcon").click(seleckedLabelClick);
		$("#doClose").click(closeDialog);

		/*编辑事件*/
		$('#del-html').click(clearHtml);
		$('#commit-title').click(commitBtnClick);
		$('#edit-conncel').click(conncelEdit);

		//getFrontPageData();
	});
</script>
</body>
</html>