var updatecode = {
	city: ['南京','镇江','泰州','宿迁','无锡','徐州','常州','苏州','南通','连云港','淮安','盐城','扬州','省直','集团'],
	number:['A','L','M','N','B','C','D','E','F','G','H','J','K','S','Z'],
	init : function(){
		updatecode.icLevel1Event();
		
		//关闭点击事件
		$("#btnCancel").click(updatecode.close);
	},
	
	icLevel1Event :function(){
		$("#icLevel1").change(function () {
			 var level1 = $("#icLevel1").val();
			 var index = updatecode.in_array(updatecode.city,level1);
			 $("#icLevel1Index").val(updatecode.number[index]); 
		 });
	},
	in_array : function(array , e){
		for(i=0;i<array.length;i++)  
		{  
			if(array[i] == e)  
				return i;  
		}  
		return -1;  
	},
	close : function() {
		window.close();
	},
}

$(function(){
	updatecode.init();
});