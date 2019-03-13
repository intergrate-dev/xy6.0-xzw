<%@include file="../e5cat/CatTree.jsp"%>
<script type="text/javascript">
	//在文档类型上的菜单、在分组上的菜单
	var catMenu0, catMenu1;
	function prepareMenu() {
		//在“字段分组”上的菜单，可以做已删分类管理
		catTypeMenu = new WebFXMenu;
		catTypeMenu.width = 160;
		catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.catRestore"/>", "catRestore(this);", "<i18n:message key="catTree.menuCatType.catRestore"/>"));
		document.write(catTypeMenu);

		//在文档类型上的菜单：可添加分组、删除文档类型、排序分组、刷新
		if (catMenu0 == null) {
			catMenu0 = new WebFXMenu;
			catMenu0.width = 160;
			catMenu0.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.addCat"/>", "addSubCat(this);", "<i18n:message key="catTree.menuCat.addCat.title"/>"));
			catMenu0.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.deleteCat"/>", "deleteCat(this);", "<i18n:message key="catTree.menuCat.deleteCat.title"/>"));
			catMenu0.add(new WebFXMenuSeparator());
			catMenu0.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.sort"/>", "catSort(this);", "<i18n:message key="catTree.menuCat.sort"/>"));
			catMenu0.add(new WebFXMenuSeparator());
			catMenu0.add(new WebFXMenuItem("<i18n:message key="catTree.menu.reload.title"/>", "reloadNode(this);", "<i18n:message key="catTree.menu.reload.title"/>"));
			document.write(catMenu0);
		}
		//在分组上的菜单：可修改分组、删除分组、排序字段、刷新
		if (catMenu1 == null) {
			catMenu1 = new WebFXMenu;
			catMenu1.width = 160;
			catMenu1.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.updateCat"/>", "modifyCat(this);", "<i18n:message key="catTree.menuCat.updateCat.title"/>"));
			catMenu1.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.deleteCat"/>", "deleteCat(this);", "<i18n:message key="catTree.menuCat.deleteCat.title"/>"));
			catMenu1.add(new WebFXMenuSeparator());
			catMenu1.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.sort"/>", "catSort(this);", "<i18n:message key="catTree.menuCat.sort"/>"));
			catMenu1.add(new WebFXMenuSeparator());
			catMenu1.add(new WebFXMenuItem("<i18n:message key="catTree.menu.reload.title"/>", "reloadNode(this);", "<i18n:message key="catTree.menu.reload.title"/>"));
			document.write(catMenu1);
		}
	}
	prepareMenu();
	
	//右键菜单
	function popmenu(flag, src) {
		if (flag == 2) {
			catTypeMenu.catType  = src.getAttribute("catType");
 			catTypeMenu.treeID   = src.getAttribute("id");
			catTypeMenu.catID    = 0;
			
			webFXMenuHandler.showMenu(catTypeMenu, src);
		} else if (flag == 3) {
			var level = src.getAttribute("level");
			if (level == 0) {
				catMenu0.catType  = src.getAttribute("catType");
				catMenu0.treeID   = src.getAttribute("id");
				catMenu0.catID    = src.getAttribute("catID");
				webFXMenuHandler.showMenu(catMenu0, src);
			} else if (level == 1) {
				catMenu1.catType  = src.getAttribute("catType");
				catMenu1.treeID   = src.getAttribute("id");
				catMenu1.catID    = src.getAttribute("catID");
				webFXMenuHandler.showMenu(catMenu1, src);
			} 
		}
	}
	//拖拽控制
	function canDrop(src, dest) {
		var srcLevel = src.getAttribute("level");
		var destLevel = dest.getAttribute("level");
		
		if (srcLevel != 2 || destLevel != 1)
			return false;
		else
			return true;
	}
	//删除分类：判断是否分组下无字段
	function canDeleteCat(p) {
		var node = webFXTreeHandler.getNode(p.treeID);
		var level = node.getAttribute("level");
		//删除分组前，判断是否分组下无字段
		if (level == 1) {
			if (node.childNodes.length > 0) {
				alert("<i18n:message key="fieldgroup.delete.hint"/>");
				return false;
			}
		}
		return true;
	}

	//分类类型上点击，右边显示说明文字
	function catTypeClick(src) {
		parent.mainBody.location.href = "FieldGroupStart.jsp";
	}
</script>
