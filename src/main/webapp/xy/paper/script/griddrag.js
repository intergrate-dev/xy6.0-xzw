$(function() {
	$( "#ul1" ).sortable({ 
		update: function(event, ui) { 
    	setOrderOfTableFn();
    } });
	$('#ul1').sortable('option', 'revert', true);
	$('#ul1').sortable('option', 'scroll', false);
	$('#ul1').sortable('option', 'scrollSensitivity', 40);
	$('#ul1').sortable('option', 'tolerance', 'pointer');
  });

var getOrderOfTableFn = function(){
    var order = new Array();
    $("#ul1").children("li").each(function(){
        order.push($(this).find(".order").attr("value"));
    });
    return order;
};
var setOrderOfTableFn = function(){
		var i = 0;
		$("#ul1").children("li").each(function(){
		      $(this).find(".order").attr("value",i);
		      i += 1;
		  }); 	
};

