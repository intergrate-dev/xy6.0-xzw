<%@include file="../../e5include/IncludeTag.jsp" %>
<html>
<head>
    <title>版面叠管理</title>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body>

<iframe id="closeFrame" name="closeFrame" src="" style="display: none;"></iframe>
<form name="postForm" id="postForm" target="closeFrame" method="post" action="PileChange.do">
	<input type="hidden" name="UUID" id="UUID" value="${param.UUID}"/>
	<input type="hidden" name="DocLibID" id="DocLibID" value="${param.DocLibID}"/>
	<input type="hidden" name="DocIDs" id="DocID" value="${param.DocIDs}"/>
	<div style="margin:10px;">
		<div class="form-group">
			请选择叠：<select name="pile" id="pile" style="width:150px;"></select>
		</div>
		<button type="button" class="btn btn-info" onclick="paper_piles.doSubmit();">确定</button>
		<button type="button" class="btn btn-default" onclick="paper_piles.doCancel();">取消</button>
	</div>
</form>
<script>
	var paper_piles = {
		pile : '${pile}',
		piles : '${piles}',
		init: function(){
			var voArray = eval(paper_piles.piles);
			if (voArray && voArray.length > 0){
				var sel = $("#pile");
				for (var i = 0; i < voArray.length; i++){
					var one = $(voArray[i]);
					var op = $("<option/>").val(one.attr("code"))
						.html(one.attr("name"));
					sel.append(op);
					if (one.attr("code") == paper_piles.pile) {
						sel[0].selectedIndex = i;
					}
				}
			}
		},
		doCancel: function(){
			var url = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
			$('#closeFrame').attr("src", url);
		},
		doSubmit: function(){
			$("#postForm").submit();
		}
	};
	$(function(){
		paper_piles.init();
	});
</script>
</body>
</html>