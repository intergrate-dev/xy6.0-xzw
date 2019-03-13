var xgenerate = {
	city: ['南京','镇江','泰州','宿迁','无锡','徐州','常州','苏州','南通','连云港','淮安','盐城','扬州','省直','集团','省团'],
	number:['A','L','M','N','B','C','D','E','F','G','H','J','K','S','Z','T'],
	level:4,
	init : function() {
		xgenerate.validateEvent();  //表单验证
		// xgenerate.icLevel1Event();

		xgenerate.icTypeEvent(xgenerate.returnNum());//这里是点击事件


		xgenerate.tip('tips_1','help_content_1');  //邀请码层级提示
		
		//点击事件
		$("#btnCancel").click(xgenerate.close);
        // $("#btnSave").click(xgenerate.save);
	},

	returnNum: function(){
        var strNum1 = sessionStorage;
        var arrNum = [];
        var keyWord = [];

        var arrNumAdd = [];
		if(strNum1.length == 0){
            level = 4;
            $("#icType").selectedIndex = 4;
            $("#icType").find("option:selected").text('四层级');
            $("#icType").attr('value','4');
		}else{
            for(var key in strNum1){
                if(key.substring(0,4) == "info"){
                    keyWord.push(key);
                    // str.charAt(str.length – 1)

					//第几层级
                    arrNumAdd.push(key.split('o')[1]);

					if(arrNum.length == 0){
                        arrNum.push(key.split('o')[1]);
					}else{
						if(arrNum[0] < key.split('o')[1]){
							arrNum[0] = key.split('o')[1];
						}
					}


                    // console.log(arrNum);
                    //判定arrNum.length

                    //从0开始，长度为4的子字符串是否为test
                    //然后这里再取出键
                    //在这里直接比对


					//不能在只判定length

                }else{
                    // console.log('这是001')
                }
            }

            for(var i = 0; i < arrNumAdd.length; i++){
            	if(arrNumAdd[i] == 1){
                    alert(sessionStorage.getItem('info1'))
                    $('#icLevel1').val(JSON.parse(sessionStorage.getItem('info1')).val);
                    $('#icLevel1Index').val(JSON.parse(sessionStorage.getItem('info1')).code);
				}
                if(arrNumAdd[i] == 2){
                    $('#icLevel2').val(JSON.parse(sessionStorage.getItem('info2')).val);
                    $('#icLevel2Index').val(JSON.parse(sessionStorage.getItem('info2')).code);
                }
                if(arrNumAdd[i] == 3){
                    $('#icLevel3').val(JSON.parse(sessionStorage.getItem('info3')).val);
                    $('#icLevel3Index').val(JSON.parse(sessionStorage.getItem('info3')).code);
                }
			}

			level = arrNum[0]

            if(arrNum[0] == 3){
                level = 4;
                // $("#icType").val('3');
                $("#icType").selectedIndex = 4;
                $("#icType").find("option:selected").text('四层级');
                $("#icType").attr('value','4');

            }else if(arrNum[0] == 2){
                level = 3;
                // $("#icType").val('2')
                $("#icType").selectedIndex = 3;
                $("#icType").find("option:selected").text('三层级');

                $("#icType").attr('value','3');
                // $("#icType option").eq(1).attr('selected');

            }else if(arrNum[0] == 1){
                level = 2;
                // $("#icType").val('1')
                $("#icType").selectedIndex = 2;
                $("#icType").find("option:selected").text('两层级');
                $("#icType").attr('value','2');

            }else{
            	// alert(1233323)

            }

		}
		return level;
	},
	

	validateEvent: function(){
		$("#form").validationEngine('attach', {
			promptPosition : 'bottomRight',// 验证提示信息的位置
			scroll : false,// 屏幕自动滚动到第一个验证不通过的位置
			autoPositionUpdate : true,// 是否自动调整提示层的位置
			onValidationComplete : function(from, r) {
				if (r) {
					window.onbeforeunload = null;
					$("#btnSave").attr("disabled", true);
                    $("#icType").removeAttr("disabled");
                    if (flag){
                        from[0].submit();
                    }
				}
			},
		});
	},
	icLevel1Event :function(){
		$("#icLevel1").change(function () {
			 var level1 = $("#icLevel1").val();
			 
			 var index = xgenerate.in_array(xgenerate.city,level1);
			 $("#icLevel1Index").val(xgenerate.number[index]); 
		 });
	},
	close : function() {
		window.close();
	},
    save : function () {
        console.log('保存');
    },
	icTypeEvent : function(obj){

        if(obj == 2){
            $("#Level2").hide();
            $("#Level3").hide();
        }else if(obj == 3){
            //展示与隐藏字段
            $("#Level2").show();
            $("#Level3").hide();
        }else if(obj == 4){
            //展示与隐藏字段
            $("#Level2").show();
            $("#Level3").show();
        }
	},
	in_array : function(array , e){
		for(i=0;i<array.length;i++)  
		{  
			if(array[i] == e)  
				return i;  
		}  
		return -1;  
	},
	tip: function(id,content){
		$("#"+id).mouseover(
			function (e) {  //鼠标移上事件
				$("#"+content).css("display","block"); 
			}
		).mouseout(
			function () {  //鼠标移出事件
				$("#"+content).css("display","none"); 
			}
		);
	}
}
$(function() {
    window.alert = function(str){
        return;
    }
	xgenerate.init();
});