function myGetXmlHttpPrefix() {
	if (myGetXmlHttpPrefix.prefix)
		return myGetXmlHttpPrefix.prefix;

	var prefixes = ["MSXML2", "Microsoft", "MSXML", "MSXML3"];
	var o;
	for (var i = 0; i < prefixes.length; i++) {
		try {
			// try to create the objects
			o = new ActiveXObject(prefixes[i] + ".XmlHttp");
			return myGetXmlHttpPrefix.prefix = prefixes[i];
		}
		catch (ex) {};
	}

	throw new Error("Could not find an installed XML parser");
}
// XmlHttp factory

	// XmlHttp factory
function MyXmlHttp() {}
MyXmlHttp.create = function () {
	try {
		if (window.XMLHttpRequest) {
			var req = new XMLHttpRequest();

			// some versions of Moz do not support the readyState property
			// and the onreadystate event so we patch it!
			if (req.readyState == null) {
				req.readyState = 1;
				req.addEventListener("load", function () {
					req.readyState = 4;
					if (typeof req.onreadystatechange == "function")
						req.onreadystatechange();
				}, false);
			}

			return req;
		}
		if (window.ActiveXObject) {
			return new ActiveXObject(myGetXmlHttpPrefix() + ".XmlHttp");
		}
	}
	catch (ex) {}
	// fell through
	throw new Error("Your browser does not support XmlHttp objects");
};


	function invokeGetXmlHttp(src,treeid)
	{
		var xmlhttp = MyXmlHttp.create();
		xmlhttp.open("GET", src, false);
		xmlhttp.send(null);
		xmlhttp.onreadystatechange = function () {
		if (xmlhttp.readyState == 4) {
//			alert(xmlHttp.responseText);
		}
	};
	removeOrgTreeNode(treeid);
	return 1;

	}

	function invokeGetXmlHttpUpdate(src,localurl)
	{
		var xmlhttp = MyXmlHttp.create();
		xmlhttp.open("GET", src, false);
		xmlhttp.send(null);
		xmlhttp.onreadystatechange = function () {
		if (xmlhttp.readyState == 4) {
//			alert(xmlHttp.responseText);
			}
		};
		document.location.href=localurl;
		return 1;

	}

	function invokeGetXmlHttpDo(src)
	{
		var xmlhttp = MyXmlHttp.create();
		xmlhttp.open("GET", src, false);
		xmlhttp.send(null);
		xmlhttp.onreadystatechange = function () {
		if (xmlhttp.readyState == 4) {
//			alert(xmlHttp.responseText);
			}
		};

		return 1;

	}

	function invokeGetXmlHttpDoForResponse(src)
	{
		var xmlhttp = MyXmlHttp.create();
		xmlhttp.open("GET", src, false);
		xmlhttp.send(null);
		xmlhttp.onreadystatechange = function () {
		if (xmlhttp.readyState == 4) {
//			alert(xmlHttp.responseText);
			}
		};
		return xmlhttp.responseText;
	}


	function removeOrgTreeNode(treeid)
	{
	   var node = webFXTreeHandler.getNode(treeid);
	   node.remove();
	}

	function invokeIEGetXmlHttp(src,treeid)
	{
		var xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
		xmlhttp.Open("GET", src, false);
		xmlhttp.Send();
		if(xmlhttp.status != 200)
		{
			return -1;
		}
		else
		{
			sInf = xmlhttp.responseText;
			if(sInf=="1")
			{
				removeOrgTreeNode(p.treeid);
			}
			else
			{
				return 0;
			}
		}

	}
   function refreshPage(){
	   var x= getOuterOpener();
		x.location.reload();
	}

	function closeWindow(varReturnValue){
		var x= getOuterWindow();
       if (x!= null){
	   x.returnValue= varReturnValue;
		    x.callAP= true;
		    x.close();
       }
       else {window.callAP= true; window.close();}
	}

	function getOuterWindow(){
		var x= window;
		while (x.parent != null){
			if (x== x.parent)
				break;
			x= x.parent;
		}
		return x;
	}
	function getOuterOpener(){
		var x= getOuterWindow();
		while (x.opener != null){
			x= x.opener;
		}
		return x;
	}
