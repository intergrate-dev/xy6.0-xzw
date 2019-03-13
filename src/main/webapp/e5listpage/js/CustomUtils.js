function convertName2Code(data,template){
	
	 var patten = new RegExp(/\[[^\]]*\]/gim); //
	 
	 var tempContent=template;
	 
	tempContent = tempContent.replace(patten, function (element, index) {
		
		var newElm = element;
		newElm = newElm.substring(1,newElm.length-1);
		var existElm = getCodeFromName(data,newElm);
		if(existElm.toString().length>0){
			newElm = existElm;
		}
		else{
			return element;
		}
		return "%"+newElm+"%";
		
	});
	
	return tempContent;
}

function convertCode2Name(data,template){
	
	  
	 var patten = new RegExp(/%[^%]*%/gim); //
	 var tempContent=template;
	 
	tempContent = tempContent.replace(patten, function (element, index) {
		
		var newElm = element;
		newElm = newElm.substring(1,newElm.length-1);
		newElm = getNameFromCode(data,newElm);
		
		return "["+newElm+"]";
		
	});
	
	return tempContent;
}


function getNameFromCode(data,code){
	
	var title = "";
	if(data!=null){
		
		$.each(data,function(i,n){
			
			if(n.name == code){
				title = n.title;
			}
		});
	}
	
	return title;
}
function getCodeFromName(data,name){
	
	var code = "";
	if(data!=null){
		
		$.each(data,function(i,n){
			
			if(n.title == name){
				code = n.name;
			}
		});
	}
	
	return code;
}

function abstorelativeIcon(url){
	
	var relativeurl = url;
	if(url.toString().length>0&&url.toString().indexOf("ttp:")>0){
		var indexchar = "/Icons/";
		var index = url.toString().indexOf(indexchar);
		
		relativeurl = ".."+relativeurl.substr(index,(url.toString().length-index));
	}
	
	return relativeurl;
}
