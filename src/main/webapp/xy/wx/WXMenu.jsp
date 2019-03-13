<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
<meta content="IE=edge" http-equiv="X-UA-Compatible"/>
<title>自定义菜单</title>
</head>
<body>
	<div id='wx-content1' class='wwa-tab'>
		<div class='col-md-3'>
			<div class='panel panel-default'>
				<div class='panel-heading'>
					<div id="manager-opt">
						菜单管理 <span style="float: right;"> <a id="add-one"
							href='javascript:void(0)' class="no-soft"><span
								class='glyphicon glyphicon-plus' data-toggle="tooltip"
								title="添加"></span></a> &nbsp; <a id="soft-opr"
							href='javascript:void(0)' class="no-soft"><span
								class='glyphicon glyphicon-menu-hamburger'
								data-toggle="tooltip" title="排序"></span></a> <a
							id="soft-finish" href='javascript:void(0)'
							class="soft btn btn-success btn-xs">完成</a> <a
							id="soft-cancel" href='javascript:void(0)'
							class="soft btn btn-default btn-xs">取消</a>
						</span>
					</div>
				</div>
				<div class='panel-body' style='min-height: 400px'>
					<div id="menu-list"></div>
				</div>
			</div>
		</div>
		<div class='col-md-9'>
			<div id="bd" class='panel panel-default'>
				<div class='panel-heading' style="visibility: hidden;">
					<span class="tit">&nbsp;</span> <span id="menu-rename"
						class="opt"><a href='javascript:void(0)'>重命名</a></span> <span
						id="menu-remove" class="opt"><a
						href='javascript:void(0)'>删除</a></span>
				</div>
				<div class='panel-body' style='height: 360px'>
					<p class="tips" style="color: #ccc">你可以点击左侧菜单或添加一个新菜单，然后设置菜单内容</p>
				</div>
				<div class='panel-footer'>编辑中：点击下方“保存并发布”，才能更新到手机上</div>
			</div>
			<div align="center">
				<button id="savePublish" class='btn btn-success'>保存并发布</button>
			</div>

		</div>
	</div>
</body>
</html>
<script type="text/javascript">
	var menu_data = {
		docLibID : "${docLibID}",
		accountID : "${accountID}",
		UUID : "${UUID}"
	}
</script>
<link rel="stylesheet" type="text/css" href="../script/bootstrap-3.3.4/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="css/menu.css" />
<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../script/jquery-ui-1.11.4.custom/jquery-ui.min.js"></script>
<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.js"></script>
<script type="text/javascript" src="script/wxMenu.js"></script>
