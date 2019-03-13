
	function getContentHZCount() {
		var count=0;
		var str = UE.getEditor('editor').getContentTxt().match(/[\u4e00-\u9fa5]/g);
		if(str!=null){
			count = str.length;
		}
		return count;		 
	}
	function getContentTxtCount() {
		return UE.getEditor('editor').getContentTxt().length;
	}
	function getContentTxtCountTrim() {
		return trimall(UE.getEditor('editor').getContentTxt()).length;
	}
    
	function getContentCountTable(find) {
		var str = UE.getEditor('editor').getContent(); 
		var count = countTagOrStr(str,find);
		return count ;
	} 
	
	
     /**
     * 删除字符串str中的所有空格
     * @name trimall
     * @grammar UE.utils.trimall(str) => String
     */
	function trimall(str) {
	    str= str.replace(/(^[ \t\n\r]+)|([ \t\n\r]+$)/g, '');//去除字符串前后所有空格（jquery中可用$.trim（str.toString()））
        str = str.replace(/[ \t\n\r]/g,"");//去除字符串中所有空格
	   return str ;
	}
	  
	function countTagOrStr(str,find){ 
		var reg = new RegExp(find,"g");//建立了一个正则表达式，也可以写为var reg=/is/g;
		var count = str.match(reg); //match则匹配返回的字符串,这是很正规的做法
		return count ? count.length : 0;
	}