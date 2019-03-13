var autoCompleter = {
	select : function(event, row, formatted){ 
		var src = event.target;
		if (autoCompleter.hasIdField(src)) {
			var idField = autoCompleter.getIdField(src);
			idField.val(row.key);
			idField.attr("nameValue", row.value);
		}
	},
	//若不是从选项选的而是手输的，则blur时把之前的ID清空
	blur : function(event){
		var src = event.target;
		if (autoCompleter.hasIdField(src)) {
			var idField = autoCompleter.getIdField(src);
			
			var nameValue = idField.attr("nameValue");
			if (nameValue == null) {
				//若还没有namevalue属性，则可能是新增或者修改表单时。
				//此时若ID已经有值，则应该是修改表单，自动初始化namevalue
				if (idField.val()) {
					idField.attr("nameValue", src.value);
					return;
				}
				
				nameValue = "";
			}
			
			if (src.value != nameValue) {
				idField.val("");
				idField.attr("nameValue", "");
			}
		}
	},
	hasIdField : function(src) {
		return (src.getAttribute("pair") == "true");
	},
	getIdField : function(src) {
		return $("#" + src.id + "_ID");
	},
	//auto-complete控件需要的参数
	options : {
		minChars : 1,
		delay : 400,
		autoFill : true,
		selectFirst : true,
		cacheLength : 1,
		matchSubset : false,
		//dataType:'json',
		//需要把data转换成json数据格式
		parse: function(data) {
			if(!data||~data.indexOf("No Records")){
				return [];
			}
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }
	},
	autoCompleteInit : function() {
		//找到所有的auto-complete输入框
		var sels = $("#form input[auto-complete='true']");
		for (var i = 0; i < sels.length; i++) {
			var sel = $(sels[i]);
			var dataUrl = sel.attr("url");
			if (!dataUrl) return;
			
			sel.autocomplete(dataUrl, autoCompleter.options);
			//若是ID/NAME两个输入框，则设置对ID域的赋值
			if (autoCompleter.hasIdField(sel[0])) {
				sel.result(autoCompleter.select);
				sel.blur(autoCompleter.blur);
			}
		}
	}
}
