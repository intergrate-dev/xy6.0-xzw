e5.mod("workspace.skin",function() {
	var api,
		skin,
		curSkin,
		oldSkin,
		defaultSkin = "blue",
		cookieName = "Workspace_Skin";
	function _setWorkspaceSkin(skin){
		var date = new Date();
		date.setMonth(1 + parseInt(date.getMonth()));

		document.cookie = cookieName + "=-" + encodeURIComponent(skin)
			+ ";expires=" + date.toGMTString()
			+ ";path=/";
	}
	function _getWorkspaceSkin(name){
		var cookies = document.cookie.split(';'),
			cookie,
			cookieValue,
			i,
			name = name + "=",
			l = name.length;
		for (i = 0; i < cookies.length; i++) {
			cookie = e5.lang.trim(cookies[i]);
			if (cookie.substring(0, l) == name) {
				cookieValue = decodeURIComponent(cookie.substring(l));
				break;
			}
		}
		return cookieValue;
	}
	function _delWorkspaceSkin(){
		document.cookie = cookieName + "=;path=/"
	}
	function _selectStyle(event){
		var self = $(this);
		if(self.hasClass("cur")){
			return;
		}
		skin = self.addClass("cur").siblings().removeClass("cur").end().attr("skin-data");
		event.preventDefault();
	}
	function handlerSaveBtn(){
		if(skin != curSkin){
			if(skin == defaultSkin){
				_delWorkspaceSkin();
			}else{
				_setWorkspaceSkin(skin);
			}
			api.broadcast("changeStyle",  skin == defaultSkin ? "" :  ("-" + skin));
			curSkin = skin;
		}
	}
	function init(sandbox){
		api = sandbox;
	}
	function onload() {
		oldSkin = (oldSkin = _getWorkspaceSkin(cookieName)) ? oldSkin.substring(1) : defaultSkin;
		$("#skin a").click(_selectStyle).filter("[skin-data='" + oldSkin +"']").addClass("cur");
		$("#save").click(handlerSaveBtn);
	};
	return {
		init: init,
		onload: onload
	}
},{requires:["SkinSwitch.css"]});