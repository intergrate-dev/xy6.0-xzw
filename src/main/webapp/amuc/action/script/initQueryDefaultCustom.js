var initQueryDefaultCustom = function(){
	
	if($("form#queryForm #maApplyTime_0").length > 0 ){
		
		$("form#queryForm #maApplyTime_0").val(e5.utils.date_now());
		$("form#queryForm #maApplyTime_1").val(e5.utils.date_now());
	}
	if($("form#queryForm #msTime_0").length > 0){
		$("form#queryForm #msTime_0").val(e5.utils.date_dayBefore(2));
		$("form#queryForm #msTime_1").val(e5.utils.date_now());
	}
};