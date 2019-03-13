<head>
    <meta charset="UTF-8">
    <title>Title</title>
	<style>
		body{
			background-color:#fff;
		}
	</style>
</head>
<script src="../third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>
<body style="height:auto;">
<%@include file="SpecialArticle.jsp" %>
</body>
<script type="text/javascript">
    function getData(){
    	return parent.window.LEDialog.getData();
    };
 
</script>
</html>