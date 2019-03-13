<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>跨站点推稿</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../xy/script/cookie.js"></script>
	<style>
        body{
            padding-left:4px;
            padding-right:4px;
            background-color: #ffffff;
        }
		.frm {
			border: 0;
			/*width: 100%;*/
			/*height: 430px;*/
            margin-left:40px;
			width: 350px;
			height: 300px;
		}
		.border_solid{
			border: 1px solid #ddd;
		}
        .ratio_tosite{
            font-size:13px;
        }
        .top{
            margin-top: 18px;
            font-family: "microsoft yahei";
            font-color:#000;
        }
        .row_1{
            font-size:14px;
        }
        .row_2{
            font-size:14px;
            padding-top:2px;
            padding-bottom: 10px;
        }
        .left{
            float: left;
			margin-left: 30px;
            /*border:solid 1px #ddd;*/
            /*overflow: auto;*/
            width : 350px;
			height: 300px;
            /*height : 260px;*/
			overflow: auto;
        }
        .right{
            /*width: 300px;*/
            /*height: 260px;*/
            /*margin-left:20px;*/
            /*border:solid 1px #ddd;*/
            /*overflow: auto;*/
        }
        .left table{
            width: 100%;
			border-collapse:collapse;
			/*border:1px solid #ddd;*/
			/*border-left:1px solid #ddd;*/
			/*border-top:1px solid #ddd;*/
        }
        .left table td{
			border:1px solid #ddd;
			height: 35px;
			padding-left: 17px;
			text-align:left;
        }
        .left table tr:hover{
            cursor: pointer;
            background-color:#E4E8EB;
        }
        .left table span{
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 13px;
        }
        .bottom{
            text-align: center;
            margin-top: 10px;
        }
        #ok{
            background: #00a0e6;
			margin-right: 10px;
        }
        .btngroup{
            margin: 5px 5px;
            font-family: microsoft yahei;
            color: #fff;
            border: none;
            background: #b1b1b1;
            border-radius: 4px;
            padding: 5px 20px;
            font-size: 12px;
        }
		#siteList .selected{
			/*border: 1px solid rgba(22, 155, 213, 1);*/
			background: url(image/u55.png) right center no-repeat;
		}
		#siteList .current{
			/*border: 1px solid rgba(22, 155, 213, 1);*/
			background: rgba(204, 235, 248, 1) url(image/u59.png) right center no-repeat;

		}
	</style>
</head>
<body>
<div>
	<div class="top">
            <span class="row_1">1&nbsp推稿方式：</span>
            <span class="ratio_tosite">
		        <input type = "radio"  value = "copy"  name = "tosite"  checked="checked">复制
		        <input type = "radio"  value = "Rel"  name = "tosite">关联
            </span>
	</div>
    <div style="width:800px;height:13px;margin:0px auto;padding:0px; overflow:hidden; border-bottom: 1px #ddd solid;"></div>
    <div>
	    <div>
		    <p class="row_2">2&nbsp选择目标站点及站点对应的目标栏目</p>
	    </div>
        <div class="parent">
	        <div class="left">
				<table id="siteList">
				</table>
	        </div>
	        <div class="right">
                <span class="">
		            <iframe name="frmRefColumn" id="frmRefColumn" src="" class="frm"></iframe>
                </span>
	        </div>
        </div>
    </div>
	<div class="bottom">
	    <button id="ok"  class="btngroup" type="button" >确定</button>
	    <button id= "cancel"  class="btngroup" type="button">取消</button>
	</div>
</div>
</body>
	<script>
		//获取参数
		var docIDs = "<c:out value="${param.DocIDs}"/>";
		var UUID = "<c:out value="${param.UUID}"/>";
		var DocLibID = "<c:out value="${param.DocLibID}"/>";
		var oldColID = "<c:out value="${param.colID}"/>";
		var ch = "<c:out value="${param.ch}"/>";
        var currSiteID = "";
        var selectedCols = {};
		var puborApr = xy_cookie.getCookie("puborApr");

		//初始化-直接在弹出窗口中设置frame的链接地址
		$(function() {
		    //初始化所有站点
			$.ajax({url:"./SiteArticlePush.do",async:false,dataType:"json",type:"post",success:function(data){
				var html = "";
				for(var i=0;i<data.length;i++){
				    var site = data[i];
				    html +='<tr><td class="site" sid="'+site.id+'"><span>'+site.name+'</span></td></tr>'
				}
				$("#siteList").html(html);
			}});
			$(".site").click(function(){
				$("#frmRefColumn").addClass("border_solid");
				var oldSid = $(".current").attr("sid");
                //将之前站点选择的栏目添加到selectedCols
                if(oldSid != undefined){
                    var columnchecks = $("#frmRefColumn")[0].contentWindow.getAllFilterChecked();
                    if(columnchecks != undefined){
                        columnAdd(oldSid, columnchecks);
                    }
                }

				if(selectedCols["site"+oldSid] != undefined){
                    $(".current").addClass("selected");
				}
				$(".site").removeClass("current");
				$(this).addClass("current");
				currSiteID = $(this).attr("sid");
				selectColumn(currSiteID);
			});
			//确定按钮
            $("#ok").click(SiteClose);
            //取消按钮
            $("#cancel").click(columnCancel);
		});

		//根据站点名查询对应的栏目
		function selectColumn(siteID) {
			var url = "../../xy/column/ColumnFavorite.jsp?cache=1&type=op&siteID=" + siteID 
			+ "&opType=ToSite&ch=" + ch + "&ids=" + selectedCols["site"+currSiteID]+ "&puborApr=" +puborApr;
			$("#frmRefColumn").attr("src", url);
			window.onbeforeunload = operationFailure;
		}

        //将用户选择的栏目加入到selectedCols中
        function columnAdd(siteId, allFilterChecked){
            var colIDs = allFilterChecked[0];
            if(colIDs == '' || colIDs == null){
                selectedCols["site"+siteId]=undefined;
            }else{
                selectedCols["site"+siteId]=colIDs;
            }
            if(selectedCols["site"+siteId] != undefined){
                $(".current").addClass("selected");
            }else{
                $(".current").removeClass("selected");
            }
        }

		//当用户提交选择的栏目,实现columnClose方法,实现选中的栏目在切换站点后不消失
		function columnClose(filterChecked, allFilterChecked) {
			var colIDs = allFilterChecked[0];
			if(colIDs == '' || colIDs == null){
				selectedCols["site"+currSiteID]=undefined;		
			}else{
				selectedCols["site"+currSiteID]=colIDs;		
			}
			if(selectedCols["site"+currSiteID] != undefined){
				$(".current").addClass("selected");
			}else{
				$(".current").removeClass("selected");
			}
          	
		}
		
        //用户选择好栏目之后，实现SiteClose方法
        function SiteClose() {
            if(currSiteID != undefined){
                var columnchecks = $("#frmRefColumn")[0].contentWindow.getAllFilterChecked();
                if(columnchecks != undefined){
                    columnAdd(currSiteID, columnchecks);
                }
            }

			var choice = $("input[name=tosite]:checked").val();
			//var column = selectedCols["site"+currSiteID];		
			var column = "";
			$(".selected").each(function(ind,ele){
				if(ind>0) column +=",";
				var csid = $(ele).attr("sid");
				column+=selectedCols["site"+csid];
			});
            if (column == '' || column == null){
            	alert("未选择任何栏目！");
            }
            else{
            
                var arr = column.split(",");
                for(var i=0; i<arr.length; i++){
		    		if (arr[i] == oldColID){
		            	alert("您的选择中包含稿件自身栏目[id="+oldColID+"]，请重新选择");
		                return;
		            }
	            }
            
            $.ajax({				
				url : "../../xy/article/CopyRel.do",
				type : 'POST',
				data : {
					"DocIDs" : docIDs,
					"DocLibID" : DocLibID,
					"colIDs" : column,
					"puborApr" : puborApr,
					"choice" : choice,
					"oldColID" : oldColID
				},
				dataType : 'text', 
				success:function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//推送成功
							operationSuccess(msg.substr(7));
						} 
						else if(msg.substr(0, 7) == "samecol"){
							alert("您选择的是自身栏目，请重新选择");
						}
						else {
							alert("操作失败");
							operationFailure();
						}
					} else {
						operationFailure();
					}
			    }
			    ,error: function(a,b,c){
			        console.log(a);
			        console.log(b);
			        console.log(c);
			    }
			});}
            
            } 				  	

		//操作成功了调用
		function operationSuccess(opnions){
			var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
			+ "&DocLibID=" + DocLibID +"&Opinion="
			+ encodeU(opnions);
			$("#frmRefColumn").attr("src", url);
		}

		//操作失败了调用
		function operationFailure() {
			window.onbeforeunload = null;	
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmRefColumn").attr("src", url);
		}
	
	    //点击取消按钮时
		function columnCancel() {
			operationFailure();
		}
		
		/** 对特殊字符和中文编码 */
		function encodeU(param1) {
			if (!param1)
				return "";
			var res = "";
			for ( var i = 0; i < param1.length; i++) {
				switch (param1.charCodeAt(i)) {
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
					break;
				}
			}
			return res;
		}
			
</script>
</html>