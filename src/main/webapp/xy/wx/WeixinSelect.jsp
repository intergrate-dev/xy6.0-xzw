<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>微信公众号授权列表</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
</head>
<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
<!-- <script language="javascript" type="text/javascript" src="../../xy/wx/script/weixinSelect.js"></script> -->
<body>
	<script>
		$(function(){
			var type = $("#checkType").val();
			if(type == 1){//微信公众号选择签发
				$("#btnBlockOK").hide();
				pubSelect($("#checkIds").val());
			}else{//用户授权微信公众号权限
				$("#btnBlockPub").hide();
				// grantPower();
                pubSelect1($("#selectIDs").val());
			}
			
			//点击确定按钮
			$('#btnBlockPub').click(function(){
				var list = $('input[type=checkbox]:checked');
				var array = new Array();
	            var  nameArry= new Array();
				for ( var i = 0; i < list.length; i++) {
					array[i] = $(list[i]).val();
	                nameArry[i] = $("#checkNames_"+$(list[i]).val()).val();
				}
				$('#oldWeixinID', window.parent.document).val(array.toString());
				$('#weixinID', window.parent.document).val(array.toString());
				$('#weixinName', window.parent.document).val(nameArry.toString());
				doCancel();
			});
			//点击保存按钮
			$('#btnBlockOK').click(function(){
				getChecks();			
			});
			//点击取消按钮
			$('#btnBlockCancel').click(function(){
				doCancel();
			});

			//#wxaccountSearch搜索微信公众号 blur事件
			$("#wxaccountSearch").blur(function(){
                var wxaccountSearchVal=$("#wxaccountSearch").val();
                var SearchValArray=wxaccountSearchVal.split("，");
                var nameArray=$('form input[type=checkbox]');
                for(var i=0;i<nameArray.length;i++){
                console.log($(nameArray[i]))
                nameArray[i].checked =false;
                }
                var classNameArray=$('form input[name=checkNames]');
                for(var j=0;j<SearchValArray.length;j++){
                for(var i=0;i<classNameArray.length;i++){
                if($(classNameArray[i]).val()==SearchValArray[j]){
                var curentId=$($(classNameArray[i])[0]).attr("id");
                var curentIdNum=curentId.slice(curentId.indexOf("_")+1);
                nameArray[i].checked =true;
                continue;
                }
                }
                }
			})
			$("#wxaccountSearch").on('keypress',function(event){
                if(event.keyCode==13){
                    var wxaccountSearchVal=$("#wxaccountSearch").val();
                    var SearchValArray=wxaccountSearchVal.split("，");
                    var nameArray=$('form input[type=checkbox]');
                    for(var i=0;i<nameArray.length;i++){
                        console.log($(nameArray[i]))
                        nameArray[i].checked =false;
                    }
                   var classNameArray=$('form input[name=checkNames]');
                    for(var j=0;j<SearchValArray.length;j++){
                        for(var i=0;i<classNameArray.length;i++){
                            if($(classNameArray[i]).val()==SearchValArray[j]){
                            var curentId=$($(classNameArray[i])[0]).attr("id");
                            var curentIdNum=curentId.slice(curentId.indexOf("_")+1);
                            nameArray[i].checked =true;
                            continue;
                            }
                        }
                    }
            }
			});
    });
		
		function grantPower(){
			var theURL = "../../xy/user/Rel.do?userID=" + $("#userID").val()
	            + "&siteID=" + $("#siteID").val()
	            + "&type=10";
			$.ajax({url:theURL, async:false, success:function(data){
				var idArr = data.split(',');
				pubSelect(idArr)
			}});
		}
		function pubSelect1(idArr) {
		    var idAr = idArr.split(",");
            for ( var i = 0; i < idAr.length; i++) {
                $("#acc_"+idAr[i]).attr("checked", true);
            }
        }
		function pubSelect(idArr){
			if(idArr.length>=1){
				var list = $('input[type=checkbox]');
				var array = new Array();
				for ( var i = 0; i < list.length; i++) {
					array[i] = $(list[i]).val();
					if($.inArray(array[i], idArr)>-1){
						$("#acc_"+array[i]).attr("checked", true);
					}
				}
			}
		}
		
		function getChecks() {
			var list = $('input[type=checkbox]:checked');
			var array = new Array();
			for ( var i = 0; i < list.length; i++) {
				array[i] = $(list[i]).val();
			}
			var theURL = "../../xy/user/RelSave.do?userID=" + $("#DocIDs").val()
	            + "&siteID=" + $("#siteID").val()
	            + "&ids=" + array.toString()
	            + "&type=10";
			$.ajax({url:theURL, async:false, success:function(){
				alert("保存完成！");
			}});
		}
		
		function doCancel() {
			try {
				parent.columnCancel();
			} catch (e) {
				var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
				alert(hint);
			}
		}
	</script>
	
	<input id="wxaccountSearch" type="text" value="" title="请输入微信公众号进行查询" size="16"/>
	<form>
		<input type="hidden" id="siteID" value="${siteID}"/>
		<input type="hidden" id="userID" value="${userID}"/>
		<input type="hidden" id="DocIDs" value="${DocIDs}"/>
		<input type="hidden" id="selectIDs" value="${selectIDs}"/>
		<input type="hidden" id="checkType" value="${checkType}"/>
		<input type="hidden" id="checkIds" value="${checkIds}"/>
		<c:forEach var="account" items="${accounts}">
			<input type="checkbox" id="acc_${account.id}" value="<c:out value="${account.id}"/>"><c:out value="${account.name}"/>
			<input type="hidden" id="checkNames_${account.id}" name="checkNames" value="${account.name}"/>
			<br/>
		</c:forEach>
	</form>
	<div id="getAccountsBtn">
		<input type='button' id="btnBlockOK" value='保存'/>
		<input type='button' id="btnBlockPub" value='确定'/>
		<input type='button' id="btnBlockCancel" value='取消'/>
	</div>


	<style>
	body{
			padding: 10px;
			height: 220px;
			overflow-y: auto;
	}
	form{
			font-size: 14px;
			color: #333;
	}
	#wxaccountSearch{
			height: 20px;
			margin-bottom: 10px
	}
	#getAccountsBtn{
			text-align: center;
			position: fixed;
			bottom: 10px;
			left: 30%;
	}
	#getAccountsBtn input{
			border-radius: 3px;
			color: #fff;
			background: #1bb8fa;
			height: 30px;
			border: none;
			font-size: 12px;
			cursor: pointer;
			text-shadow: none;
			padding: 0 20px;
			font-family: "microsoft yahei";
			margin-right: 10px;
			cursor: pointer;
	}
	#getAccountsBtn #btnBlockCancel{
			background: #b1b1b1;
	}
	</style>
</body>
</html>