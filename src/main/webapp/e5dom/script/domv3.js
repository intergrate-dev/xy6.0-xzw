var e5dom = {
	currentDocType : null,

	docTypes: null,

	currentDocLibID : 0,
	
	docLibs : null,

	loadDocTypes: function(){
		$.ajax({
			url:"../e5dom/DocTypeController.do?invoke=getDocTypes",
			dataType:"json",
			async:false,
			success:function(data) {
		        if(data!="-1"){
	        		var datas = new Array();
	        		if(!$.isArray(data)){
	        			datas.push(data);
	        		}else{
	        			datas = data;
	        		}
		        	e5dom.docTypes = datas;
		        }
		    }
		});
	},
	
	getDocType: function(docTypeID) {
		if(e5dom.docTypes != null && e5dom.docTypes.length >0){
			for(var i=0;i<e5dom.docTypes.length;i++)
			{
				if(e5dom.docTypes[i].docTypeID == docTypeID)
					return e5dom.docTypes[i];
			}
		}
	},

	loadDocLibs : function () {
		$.ajax({
			url:"DocTypeController.do?invoke=getDocLibs",
			dataType:"json",
			async:false,
			success:function(data) {
		        if(data!="-1"){
	        		var datas = new Array();
	        		if(!$.isArray(data)){
	        			datas.push(data);
	        		}else{
	        			datas = data;
	        		}
	        		e5dom.docLibs = datas;
		        }
	      }
		});
	},
	
	getDocLib : function(docLibID){
		if(e5dom.docLibs!=null && e5dom.docLibs.length >0){
			for(var i=0;i<e5dom.docLibs.length;i++)
			{
				if(e5dom.docLibs[i].docLibID == docLibID)
					return e5dom.docLibs[i];
			}
		}

	}
}

e5dom.util = {
	
	filterBeforeShow : function (ret) {
		if(typeof(ret) == 'undefined')
			return "";
	},

	srcEventElement: function (evt) {
		if(evt == null)
			evt = window.event; // For IE
		var el = evt.srcElement? evt.srcElement : evt.target;
		return el;
	}
}