<!doctype html>
<html>
	<head>
		<title>Operation Management</title>
		<link type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
		<script type="text/javascript">
			function refreshNode(){
				window.frames[0].refreshNode();
			}
			function resize(src){
				var cn = document.getElementById('test');
				if(src==1)
					test.cols="0,12,*";
				else
					test.cols="180,12,*";
			}
		</script>
	</head>
	<frameset name="test" id="test" cols="180,*" frameborder="NO" border="0" framespacing="0">
		<frame src="ProcOrderTreeSrc.jsp" name="leftFrame" scrolling="AUTO"/>
		<frame src="" name="mainBody"></frame>
	</frameset>
	<noframes><body></body></noframes>
</html>
