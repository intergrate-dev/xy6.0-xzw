var column_stat = {
		
	init : function() {
		
	},
	outputcsv: function(){
		var td = $('#loadId td');
		var jsonParam = '';
		var datas = [];
		var length = td.length;
		for(var a = 0; a < length; a++){
			jsonParam = {
				"1" : $(td[a++]).text(), // 用户名
				"2" : $(td[a]).text() // 评论量
			};
			datas.push(jsonParam);
		}
		$('#jsonData').val(JSON.stringify(datas));
		$('#csvName').val('工作量统计.csv');
		$("#form").attr("action", "../../xy/stat/outputcsv.do");
		$("#form").submit();
	}
}

$(function(){
	column_stat.init();
});
