var e5dom = {

	currentDocType : null,

	docTypes: null,

	currentDocLibID : 0,
	
	docLibs : null,

	loadDocTypes: function(){

		

		var buffalo = new Buffalo(END_POINT,false);
		buffalo.remoteCall("doctypeService.getDocTypes",[0], function(reply) {
				e5dom.docTypes = reply.getResult();
				}
		);

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
		
		var buffalo = new Buffalo(END_POINT,false);
		buffalo.remoteCall("doclibService.getDocLibs",[0], function(reply) {
				e5dom.docLibs = reply.getResult();
				}
		);
		
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
