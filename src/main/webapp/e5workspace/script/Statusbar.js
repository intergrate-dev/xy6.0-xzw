e5.mod("workspace.doclist",function() {
	var ready = false;
	//变量定义
	var handlerParam  = {
		selBgColor : "#5588DD", //选中一列时背景颜色
		selColor : 	 "#FFFFFF", //选择一列时文字颜色
		
		idArr : [],
		libidArr : [],
		bgColorArr : [],
		fgColorArr : [],
		
		ALL_TH : [],
		
		curCustDialog : null
	};
	//一些不太可能需要上层应用修改的方法，作为私有方法
	var priv = {
		//订阅事件，实际调用了self.listening。多一层是为了方便上层应用系统对listening重新定义
		listening : function(msgName, callerId, param) {
			self.listening(msgName, callerId, param);
		},
		
		//-------以下是列表方式相关方法------------
		resetListpage : function(param){
			var theURL = self.prepaireListpageURL(param);
			$.ajax({url:theURL, async:false, success:priv._setListPage});
		},
		//列表方式的图标/文字生成
		_setListPage : function(data) {
			//原列表方式清除
			var area = $("#listPageArea");
			area.children().remove();
			area.hide();
			
			if (data){
				var listArr = data.split(";");//每条数据用;分隔
				for (var j = 0; j < listArr.length; j++) {
					priv._addOneListPage(area, listArr[j]);
				}
				if (listArr.length > 1) {
					area.show();
				}
				priv._defaultListClick(listArr[0]);
			}
		},
		//一个列表方式的图标/文字生成。数据格式：listID@cachePages@count_per_page@cols,listname,listIcon
		_addOneListPage : function(area, list) {
			var className = "page-unselected";
			var f = list.split(","); //每条数据的value和text之间用,分隔
			if (f[2]) {
				var theText = "<image class='image-list " + className 
					+ "' value='" + f[0]
					+ "' id='listpage_" + priv._listID(f[0])
					+ "' src=\"" + self.prepaireListIconURL(f[2]) + "\" alt=\"" + f[1] + "\"/>";
			} else {
				var theText = "<a href='#' class='" + className 
					+ "' value='" + f[0]
					+ "' id='listpage_" + priv._listID(f[0])
					+ "'>" + f[1] + "</a>";
			}
			var onepage = $(theText);
			onepage.click(priv.listPageClick);
			area.append(onepage);
		},
		//初始选中列表方式：若cookie里有上次选中记录，则用cookie
		_defaultListClick : function(list0) {
			var last = priv._readCookie_lp();
			var lp = $("#listpage_" + last);
			if (lp.length == 0) {
				last = priv._listID(list0);
				lp = $("#listpage_" + last);
			}
			lp.click();
		},
		_readCookie_lp : function() {
			var cookieName = priv._cookie_lp(self.statusparam);
			return e5.utils.getCookie(cookieName);
		},
		/* cookie里记的列表方式名。
		 * 缺省是用fvID区分，文件夹太多时可能cookie不够用。
		 * 上层应用可酌情修改为docLibID或docTypeID。
		 * 注意若文件夹只有一个列表方式，则不会记录cookie
		 */
		_cookie_lp : function(statusparam) {
			return "ws_lp_" + statusparam.fvID;
		},
		
		//从列表方式返回串里提取列表方式ID
		_listID : function(listValue) {
			var arr = listValue.split("@");
			return arr[0];
		},
		//列表方式的图标/文字点击事件
		listPageClick : function(e) {
			var src = e.target;
			var myvalue = src.getAttribute("value");
			//遵循原有逻辑，对隐藏列表方式域赋值，其它各处引用的是这个隐藏列表方式域
			var lp = $("#ListPages");
			var oldValue = priv._listID(lp.val());
			lp.val(myvalue);
			
			//清除上次的选中状态
			$("#listpage_" + oldValue).removeClass("page-selected").addClass("page-unselected");
			//设置当前的选中状态
			$(src).removeClass("page-unselected").addClass("page-selected");
			
			priv.refresh4ListChange();
			
			priv._save2Cookie(self.statusparam);
			return false;
		},
		//列表方式记在Cookie中
		_save2Cookie : function(param) {
			try {
				//只有一个列表方式时，不记cookie
				if ($("[id^='listpage_']").length <= 1) return;

				var lp = $("#ListPages");
				var oldValue = priv._listID(lp.val());
				
				var cookieName = priv._cookie_lp(param);
				e5.utils.setCookie(cookieName, oldValue);
			}catch (e){}
		},
		//列表方式切换后的页面刷新
		refresh4ListChange : function() {
			//重新从第一页开始显示
			self.resetPage();
			//重置每页显示条数的select
			priv.setCountPerPage();
			
			self.refreshPage();
		},
		//重置每页显示条数的select
		setCountPerPage : function(){
			var sel = document.getElementById("CountPerPage");
			while (sel.options.length > 0)
				sel.remove(0);

			var datas = $("#ListPages").val();
			if (!datas) return;
			
			var data = datas.split("@")[2];
			if (data){
				var filterArr = data.split(".");//每条数据用;分隔
				for (var j = 0; j < filterArr.length; j++) {
					var op = document.createElement("OPTION");
					op.value = filterArr[j];
					op.text = filterArr[j];
					sel.options.add(op);
				}
			}
			if (sel.options.length == 1) {
				sel.style.display = "none";
			} else {
				sel.style.display = "block";
			}
		},
		
		//---列表自定义---
		showCustomListpage : function(){
			//相册方式，不做自定义
			if(self.isAlbum()) return;

			//var tabCookie = e5.utils.getCookie("ListCust");
			//if (tabCookie != "true") return;
			
			//在按定制做呈现之前，取出所有的列名，保存在ALL_TH里
			var pinHeader = $("#tablePinHeader");
			priv.initAllLines(pinHeader);
			
			//对每一列的列头添加click、delete、swap事件
			priv.defSortEvents(pinHeader);
			
			var table = $("#listing table");
			priv.defineSwapEvents(table, pinHeader);
			
			//按自定义的效果做展现
			priv._applyListCustom(pinHeader);
		},
		
		_contains : function(names, aid) {
			for (var i = 0; i < names.length; i++)
				if (names[i] == aid) return true;
			return false;
		},
		//get all table lines information before changing.
		initAllLines : function(table) {
			handlerParam.ALL_TH = [];
			
			var ths = table.find("tr:first th");
			if (!ths) return;
			
			for (var i = 0; i < ths.length; i++){
				//给每个th加id
				if (!$(ths[i]).attr("id")) $(ths[i]).attr("id", "th_" + i);
				//记住th的id和其内span里的显示名（列名）
				var spans = $(ths[i]).find("span");
				if (!spans || spans.length == 0) {
					handlerParam.ALL_TH.push({"id":$(ths[i]).attr("id"), "name":""});
				}
				else {
					var thName = $(spans[0]).html();
					handlerParam.ALL_TH.push({"id":$(ths[i]).attr("id"), "name": thName});
				}
			}
		},
		//add "delete" and "goto" to <TH>
		defineSwapEvents : function(table, pinHeader) {
			var firstThs = pinHeader.find("tr:first th");
			var pinHeaderId = pinHeader.attr("id");
			firstThs.each(function(i){
				var src = $(this);
				
				priv._defDelete(src, table, pinHeaderId);
				priv._defGoto(src, table, pinHeaderId);
			});
		},
		defSortEvents : function(table) {
			var firstThs = table.find("tr:first th");
			firstThs.each(function(i){
				var src = $(this);
				priv._defSort(src);
			});
		},
		_defSort : function(src) {
			src.bind("click", function() {
				var nosort = src.attr("nosort");
				if (nosort) return;
				
				var sortby = src.attr("sortby");
				if (!sortby) {
					sortby = src.id;
					if (sortby) sortby = sortby.substr(3);
				}
				if (!sortby) return;
				
				self.setOrderBy(sortby);
				self.refresh4Order();
			});
		},
		_defDelete : function(src, table, pinHeaderId) {
			src.bind("delete", function(){
				//重新确定一次当前th在tr中的第几列。因为可能之前经历过删除列。
				var pos = priv._findThPos(src);
				
				//每行删除当前列
				var trs = table[0].rows;
				if (!trs) return;
				for (var idx = 0; idx < trs.length; idx++)
					trs[idx].deleteCell(pos);
				//pinHeader也删列
				if (typeof pinHeaderId != "undefined") {
					var pinHeader = $("#" + pinHeaderId);
					var trs = pinHeader[0].rows;
					if (!trs) return;
					for (var idx = 0; idx < trs.length; idx++)
						trs[idx].deleteCell(pos);
				}
			 });
		},
		_defGoto : function(src, table, pinHeaderId) {
			src.bind("goto", function(){
				//重新确定一次当前th在tr中的第几列。因为可能之前经历过删除列。
				var pos = priv._findThPos(src);
				var tgt = arguments[1];
				if (pos == tgt) return;
				
				//把每行的当前列交换到指定位置
				table.find("tr").each(function(){   
					var tdlist =  $(this).children();
					priv._swap(tdlist[pos], tdlist[tgt]);   
				});
				//pinHeader也删列
				if (typeof pinHeaderId != "undefined") {
					var pinHeader = $("#" + pinHeaderId);
					pinHeader.find("tr").each(function(){   
						var tdlist =  $(this).children();
						priv._swap(tdlist[pos], tdlist[tgt]);   
					});
				}
			});
		},
		//定位TH为第几列
		_findThPos : function(src) {
			var pos = -1;
			var ths = src.parent().children();
			for (var idx = 0; idx < ths.length; idx++) {
				if ($(ths[idx]).attr("id") == src.attr("id")) {
					pos = idx;
					break;
				}
			}
			return pos;
		},
		//应用列表定制来展现
		_applyListCustom : function(table) {
			//从后台读出自定义的列表列
			var names = priv.getCustLines();
			if (!names) return;
			
			//以下开始实际的去掉列和交换列
			//delete lines useless.
			var ths = table.find("tr:first th");
			ths.each(function(i) {
				var aid = $(this).attr("id");
				if (priv._contains(names, aid)) return;
				
				$(this).trigger("delete");
			});
			//swap lines.
			var begin = 0;
			for (var idx = 0; idx < names.length; idx++) {
				var theTH = table.find("#" + names[idx]);
				if (!theTH) continue;
				
				theTH.trigger("goto", begin++);
			}
		},

		doListCustomize : function() {
			var url = "../e5workspace/ListpageCust.do?listID=" + self.statusparam.listPage;
			handlerParam.curCustDialog = e5.dialog({type:"iframe", value:url},{title:statusinfo.listcustom, width:600, height:520, resizable:true});
			handlerParam.curCustDialog.show();
		},
		listCustDone : function() {
			if (handlerParam.curCustDialog)
				handlerParam.curCustDialog.closeEvt();
			handlerParam.curCustDialog = null;
			
			self.refresh4ListCust();
		},
		getListHeaders : function() {
			return handlerParam.ALL_TH;
		},

		_swap : function(obj1,obj2){
			var _o1 = $(obj1);
			var _o2 = $(obj2);   
			var _temp = _o1.html();
			_o1.html(_o2.html());
			_o2.html(_temp);   

			var h1 = _o1.width();
			_o1.width(_o2.width());
			_o2.width(h1);

			var aid = _o1.attr("id");
			_o1.attr("id", _o2.attr("id"));
			_o2.attr("id", aid);
			
			var sortby1 = _o1.attr("sortby");
			var sortby2 = _o2.attr("sortby");
			_o1.attr("sortby", (sortby2 ? sortby2 : ""));
			_o2.attr("sortby", (sortby1 ? sortby1 : ""));
		},
		getCustLines : function(){
			var theURL = "../e5workspace/ListpageCust.do?reader=1&listID=" + self.statusparam.listPage;
			var names;
			$.ajax({url:theURL, async:false, success: function(data) {
				names = data;
				if (names) names = names.split(",");
			}});
			return names;
		},
		
		/**
		 * --------页面上复选框 全选/全不选--------
		*1.在各资源的list.xsl中放置一个用来全选的复选框，名为"cbAll",
		*<input type="checkbox" name="cbAll" onclick="priv.selAll()" title="全选/全不选" />
		*2.每个文档行(相册模式为TD)内放置一个复选框，统一取名"cb",
		*<xsl:element name="INPUT">
		*	 <xsl:attribute name="type">checkbox</xsl:attribute>
		*	 <xsl:attribute name="name">cb</xsl:attribute>
		*	 <xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
		*	 <xsl:attribute name="onclick">inputclick(this)</xsl:attribute>
		*</xsl:element>
		*3.在原来处理文档选中的dealSelect()中同时改变复选框的选中状态
		*/
		//复选框的点击事件
		inputclick : function(obj, evt){
			evt = evt || event;
			//阻止事件冒泡，以免触发selectDoc()事件
			evt.cancelBubble = true;
			
			//取消任一复选框的选中，则取消"全选"
			if (!obj.checked) {
				var cbAll = document.getElementById("cbAll");
				cbAll.checked = false;
			}
			var src = document.getElementById(obj.value);
			//复选框相当于Ctrl多选，因此传入参数为true
			self.dealSelect(true, src);
			
			self._selectDoc(self._ids(), self._libids());
		},

		//选中某复选框
		docheck : function(val){
			var cb = $("input[type='checkbox'][name='cb'][value='" + val + "']");
			if (cb.length > 0) {
				cb.attr("checked", true);
			}
		},
		//取消选中某复选框
		uncheck : function(val){
			var cb = $("input[type='checkbox'][name='cb'][value='" + val + "']");
			if (cb.length > 0) cb.attr("checked", false);
		},

		//点击全选时的事件响应
		selAll : function() {
			var cbAll = document.getElementById("cbAll");
			if (!cbAll.checked) {
				self.unselectAll();
				self._selectDoc(self._ids(), self._libids());
			}
			else {
				self.selectAll();
			}
		},
		//由selAll()调用
		checkAll : function() {
			var cbAll = $("#cbAll");
			if (cbAll.length == 0) return;
			
			cbAll.attr("checked", true);
			
			var checkboxs = $("input[type='checkbox'][name='cb']");
			if (checkboxs.length == 0) return;

			checkboxs.each(function(i){
				$(this).attr("checked", true);
			});	
		},
		//由unselectAll()调用
		uncheckAll : function() {
			var cbAll = $("#cbAll");
			if (cbAll.length == 0) return;
			
			cbAll.attr("checked", false);
			
			var checkboxs = $("input[type='checkbox'][name='cb']");
			if (checkboxs.length == 0) return;

			checkboxs.each(function(i){
				$(this).attr("checked", false);
			});	
		},
		//定义input type="checkbox"的事件
		_defineCheckEvent : function() {
			var checkboxAll = $("#cbAll");
			if (checkboxAll.length > 0) {
				if (!checkboxAll[0].onclick)
					checkboxAll[0].onclick = priv.selAll;
				
				var checkboxs = $("input[type='checkbox'][name='cb']");
				checkboxs.each(function(i){
					if (!this.onclick)
						this.onclick = function(evt){priv.inputclick(this, evt);};
				});
			}
		},

		//--------列表导出------
		exportList : function(){
			var listPage = self.getCurrentListID();
			if (listPage == 0){
				alert(statuserror.noListPage);
				return;
			}
			
			var countPerPage = self.getCurrentCountPerPage();

			var theURL = "../e5workspace/listExport.do?DocLibID=" + self.statusparam.docLibID
				+ "&FVID=" + self.statusparam.fvID
				+ "&FilterID=" + self.statusparam.filterID
				+ "&ListPage=" + listPage
				+ "&CurrentPage=1"
				+ "&CatTypeID=" + self.statusparam.catTypeID
				+ "&ExtType=" + self.statusparam.extType  /*--扩展类型--*/
				+ "&RuleFormula=" + e5.utils.encodeSpecialCode(self.statusparam.ruleFormula)
				+ "&Query=" + e5.utils.encodeSpecialCode(self.statusparam.query)
				;
			theURL += "&CountOfPage=" + countPerPage;
			if (self.getOrderBy()) {
				theURL += "&OrderBy=" + self.getOrderBy()
						+ "&OrderType=" + self.getOrderType();
			}
			//当前定制列
			var fields = priv.exportFields();
			if (fields) {
				theURL += "&Fields=" + fields;
			}
			
			window.open(theURL, "_blank");
		},
		//当前定制列
		exportFields : function() {
			var fields = "";
			
			var names = priv.getCustLines();
			if (names) {
				for (var i = 0; i < names.length; i++) {
					names[i] = names[i].substring(3);//去掉前面的"TH_"
					if (names[i] != "DocOrder" && names[i] != "Checkbox") {
						if (fields) fields += ",";
						fields += names[i];
					}
				}
			} else {
				$("#tablePinHeader tr th").each(function() {
					var name = this.id;
					name = name.substring(3);//去掉前面的"TH_"
					if (name != "DocOrder" && name != "CheckBox") {
						if (fields) fields += ",";
						fields += name;
					}
				});
			}
			return fields;
		},
	
		/* ---------------------------------------
		 * 新的页数显示方式, 在setCount方法中调用
		 * 设置页面分页页数列表：1 2 3 4 5 ... 10
		 * ---------------------------------------*/
		//当前页前后各有多个记录
		pageListMaxPage : 2,
		setPagesArea : function(pages) {
			//当前页：self.statusparam.currentPage
			//总页数：pages
			var pres = self.statusparam.currentPage - priv.pageListMaxPage;
			var nexts= self.statusparam.currentPage + priv.pageListMaxPage + 1;

			if (pres < 0 ) {//当前页小于5
				nexts += pres * -1; //在后续页数中加上差值
				pres = 0;
			}
			if (nexts > pages) {
				pres = pres - (nexts - pages);
				nexts = pages;

				if(pres < 0) pres = 0;
			}
			var area = $("#pagesArea");
			area.children().remove();
			//始终显示第一页
			if (pres > 0) {
				var name = (pres == 1) ? "1" : "1...";
				priv._addOnPage(area, 0, name);
			}

			//加前pre页；
			for (var i = pres; i < self.statusparam.currentPage; i++)
				priv._addOnPage(area, i);
			//加当前页
			priv._addOnPage(area, self.statusparam.currentPage);
			//加后nexts页
			for (var i = self.statusparam.currentPage + 1; i < nexts; i++)
				priv._addOnPage(area, i);
			//始终显示最后一页
			if (nexts < pages) {
				var name = (nexts == pages - 1) ? pages : "..." + pages;
				priv._addOnPage(area, pages - 1, name);
			}
		},
		_addOnPage : function(area, page, name) {
			var className = "page-unselected";
			if (page == self.statusparam.currentPage)
				className = "page-selected";

			var showName = (name) ? name : (parseInt(page) + 1);
			var onepage = $("<a href='#' class='" + className + "'>" + showName + "</a>");
			onepage.click(function(){
				self.changePage(3, page);
				return false;
			});
			area.append(onepage);
		},
		
		//---列表选择的某些私有方法---
		_joinArr : function(arr){
			var ss = "";
			for (var i = 0; i < arr.length; i++)
				ss += arr[i] + ",";
			return ss;
		},
		//计算应该到哪一页
		_calculatePage : function(flag, page) {
			var pages = parseInt(self._getText("Pages"));
			if (pages <= 1) return -1;/* 只有一页时，不存在页数变化的问题 */
			
			var currentpage = self.statusparam.currentPage;
			switch (flag) {
				case 0: /* 上一页 */
					if (currentpage != 0)
						currentpage--;
					break;
				case 1:/* 下一页 */
					if (currentpage != pages - 1)
						currentpage++;
					break;
				case 2:/* 跳转页 */
					var jumpto =  parseInt(document.getElementById("Turn2Page").value);
					if (!jumpto || jumpto > pages || jumpto <= 0){
						alert(statuserror.invalidPage);
						return -1;
					}
					currentpage = --jumpto;
					break;
				case 3: /*第几页*/
					currentpage = page;
					break;
				case 4:/*第一页*/
					currentpage = 0;
					break;
				case 5:/* 最后一页 */
					currentpage = pages - 1;
					break;
				default:
					currentpage = 0;
					break;
			}
			return currentpage;
		}
	};
	
	//滚动方式翻页的集中处理
	var scroll2page = {
		enabled : false, //只需打开此开关，并引用page-scroll.js，就可以进行滚动翻页
		//滚动翻页--按自定义列表显示
		scrollCustomListpage : function(data) {
			var myDiv = $("<div/>");
			myDiv.append(data);
			var table = myDiv.find("#listing table");
			
			//对每一列的列头添加delete、swap事件
			var firstThs = table.find("tr:first th");
			firstThs.each(function(i){
				var src = $(this);
				
				priv._defDelete(src, table);
				priv._defGoto(src, table);
			});
			
			//按自定义的效果做展现
			priv._applyListCustom(table);
			
			//截取实际行，附加到table末尾
			data = table.html();
			var pos1 = data.indexOf("</tr>");
			var pos2 = data.lastIndexOf("</tr>");
			data = data.substring(pos1 + 5, pos2 + 5);
			
			$("#listing table tbody").append(data);
			
			//定义每行事件
			self.defineListEvents();
		},
		//滚动翻页--刷新列表
		refresh4Page : function() {
			self.statusparam.listPage = self.getCurrentListID();
			self.statusparam.countPerPage = self.getCurrentCountPerPage();

			self.statusparam.startPage = self.statusparam.currentPage; /* 缓存起始页就是当前刷新的页 */
			if (self.statusparam.listPage == 0){
				alert(statuserror.noListPage);
				return;
			}
			var theURL = self.prepaireListURL(self.statusparam);
			
			//对滚动翻页的新数据，按自定义列表整理出数据，附加到最后
			$.ajax({url:theURL, async:false, success: function(data){
				if (!data) return;
				scroll2page.scrollCustomListpage(data);
			}});
		},
		//滚动翻页--操作完成后的刷新，从第一页开始刷
		refresh4Tool : function() {
			self.resetPage();
			self.refreshPage();
		},
		//滚动翻页--列表内容填充
		setListData : function(data) {
			self._setListData(data);
			
			//列表显示后，设置滚动事件
			var pages = parseInt(self._getText("Pages"));
			if (pages > 1) {
				scroll2page._scrollPage(pages - 1);
			}
		},
		//滚动翻页--加滚动翻页的事件（需引入js:jquery-paged-scroll/jquery-paged-scroll.min.js）
		_scrollPage : function(pages){
			$("#listing").paged_scroll({
				handleScroll: function (page, container, doneCallback) {
					setTimeout(function () {
						self.changePage(1);
					}, 500);

					return true;
				},
				triggerFromBottom: '1px',
				pagesToScroll: pages,
				targetElement: $("#listing table")
			});
		}
	}
	/* ------------------------------------------
	 * 把方法都包在一个对象里，方便外部调用或修改
	 * ------------------------------------------*/
	var self = {
		api : null,
		statusparam : null,
		doclistparam : null,
		scroll2page : scroll2page,
		/*默认路径是../e5workspace/。若深度不对，则修改pathPrefix为"../"等*/
		pathPrefix : "",
	
		listening : function(msgName, callerId, param) {
			for (var name in self.statusparam) self.statusparam[name] = "";
			for (var name in param) self.statusparam[name] = param[name];
			
			self.resetStatusbar();
			priv.resetListpage(self.statusparam);
		},
		resetStatusbar : function(){
			self.resetPage();
			for (var name in self.doclistparam) self.doclistparam[name] = "";
		},
		resetPage : function() {
			self._setText("PageCount", "");
			self._setText("Pages", "");
			self.setCurrentPage(0);

			self.statusparam.currentPage = 0;	//当前页
			self.statusparam.startPage = 0;		//起始页
			self.statusparam.cachePage = 1;		//缓存页（无用）
			self.statusparam.countPerPage = 10; //每页个数
		},
		//列表方式的读取url
		prepaireListpageURL : function(param){
			if (!param["listID"]) {
				var theURL = self.pathPrefix + "../e5workspace/statusbar.do?DocTypeID=" + param.docTypeID
					+ "&Flag=1&FVID=" + param.fvID;
				return theURL;
			} else {
				//直接指定列表方式
				var theURL = self.pathPrefix + "../e5workspace/statusbar.do?Flag=1&ListID=" + param.listID;
				return theURL;
			}
		},
		//列表方式的图标路径
		prepaireListIconURL : function(iconURL){
			return self.pathPrefix + iconURL;
		},
	
		isAlbum : function(){
			var album = $("#listing").attr("album");
			return (album == "true");
		},

		//设置总条数，文档列表查询出来后进行设置。同时设置总页数
		setCount : function(value){
			self._setText("PageCount", value);
			var pages = Math.ceil(parseInt(value) / self.statusparam.countPerPage);
			self._setText("Pages", pages);
			if (pages == 0)
				self.setCurrentPage(-1);
			else {
				//无个数时设当前页数为0了，则有个数后，需改成1
				var curPage = parseInt(self._getText("CurrentPage"));
				if (curPage == 0) self.setCurrentPage(0);
			}
			//新的页数展示方式 <<   <   1 2 3 4 ... 9 10 11 >  >>
			priv.setPagesArea(pages);
		},
		setCurrentPage : function(value){
			var a = 1 + parseInt(value);
			self._setText("CurrentPage", a);
		},
		//修改一个元素的Text
		_setText : function(name, value){
			var span = document.getElementById(name);
			if (typeof span.innerText == "undefined")
				span.textContent = value;
			else
				span.innerText = value;
		},
		//获取一个元素的Text
		_getText : function(name){
			var span = document.getElementById(name);
			if (typeof span.innerText == "undefined")
				return span.textContent;
			else
				return span.innerText;
		},
		//last page, next page, jump to page
		changePage : function(flag, page){
			if (!self._getText("Pages")) return;
			
			var currentpage = priv._calculatePage(flag, page);
			if (currentpage < 0) return;
			
			self.statusparam.currentPage = currentpage;
			self.setCurrentPage(currentpage);

			self.refresh4Page();
		},
		/* 在页数跳转框中敲回车键 */
		turnPage : function(evt){
			evt = evt || event;
			if (evt.keyCode == 13) self.changePage(2);
		},
		//读出当前列表ID
		getCurrentListID : function() {
			var value = $("#ListPages").val();
			//列表的value记录了4个信息：listID@缓存页数@每页条数@可选行数
			var value1 = value.split("@");
			if (value1.length == 4)	{
				return value1[0];
			}
			return 0;
		},
		//读出当前每页个数
		getCurrentCountPerPage : function() {
			return $("#CountPerPage").val();
		},
		
		//点刷新时的响应(或者操作完成后需要刷新时)
		refreshPage : function(){
			self.statusparam.listPage = self.getCurrentListID();
			self.statusparam.countPerPage = self.getCurrentCountPerPage();

			self.statusparam.startPage = self.statusparam.currentPage; /* 缓存起始页就是当前刷新的页 */
			self.callDocList(); //状态条和文档列表合在一起，因此不需要做消息订阅等，直接调用
		},
		//操作栏的操作完成后的刷新列表
		refreshListening: function() {
			self.refresh4Tool();
		},
		//操作完成后的刷新，可被覆写
		refresh4Tool : function() {
			if (scroll2page.enabled)
				scroll2page.refresh4Tool();
			else
				self.refreshPage();
		},
		//翻页时的刷新列表
		refresh4Page : function() {
			if (scroll2page.enabled)
				scroll2page.refresh4Page();
			else
				self.refreshPage();
		},
		//点击列头排序时的刷新列表
		refresh4Order : function() {
			//新的排序时从第一页开始显示
			self.resetPage();
			
			self.refreshPage();
		},
		//列表自定义完成后的刷新列表
		refresh4ListCust : function() {
			//列表自定义后，从第一页开始显示
			self.resetPage();

			self.refreshPage();
		},
		//改变每页个数后的刷新列表
		refresh4ChangeCount : function() {
			//改变每页个数后，从第一页开始显示
			self.resetPage();

			self.refreshPage();
		},
		
		//调用文档列表
		callDocList : function(){
			if (self.statusparam.listPage == 0){
				alert(statuserror.noListPage);
				return;
			}
			var theURL = self.prepaireListURL(self.statusparam);
			
			$.ajax({url:theURL, async:false, success: self.setListData});
		},
		//列表查询的url，调用doclist.do的Url
		prepaireListURL : function(statusparam) {
			var theURL = self.pathPrefix + "../e5workspace/doclist.do?DocLibID=" + statusparam.docLibID
				+ "&FVID=" + statusparam.fvID
				+ "&FilterID=" + statusparam.filterID
				+ "&ListPage=" + statusparam.listPage
				+ "&CurrentPage=" + (1 + parseInt(statusparam.currentPage))
				+ "&CatTypeID=" + statusparam.catTypeID
				+ "&ExtType=" + statusparam.extType  /*--扩展类型--*/
				+ "&RuleFormula=" + e5.utils.encodeSpecialCode(statusparam.ruleFormula)
				+ "&Query=" + e5.utils.encodeSpecialCode(statusparam.query)
				;
			theURL += "&CountOfPage=" + statusparam.countPerPage;
			if (self.getOrderBy(statusparam)) {
				theURL += "&OrderBy=" + self.getOrderBy(statusparam)
						+ "&OrderType=" + self.getOrderType(statusparam);
			}
			//还可以加用户个性化指定信息：Fields（查询字段）:逗号分隔
			return theURL;
		},
		/* 文档列表有变化时，传出事件 */
		_selectDoc : function(ids, libids){
			for (var name in self.doclistparam)
				self.doclistparam[name] = "";
			for (var name in self.statusparam)
				self.doclistparam[name] = self.statusparam[name];
			self.doclistparam.docIDs = ids;
			self.doclistparam.docLibIDs = libids;
			self.doclistparam.isQuery = (self.statusparam.query ? true : false);
			self.doclistparam.isRule = (self.statusparam.ruleFormula ? true : false);

			self.api.broadcast("doclistTopic", self.doclistparam);
		},
		/* 设置扩展参数。该参数将一直传递到操作 */
		setExtParams : function(params){
			self.statusparam.extParams = params;
		},
		setOrderBy : function(field) {
			if (field == self.statusparam["orderBy"]) {
				self.statusparam["orderType"] = self.statusparam["orderType"] ? 0 : 1;
			}
			else {
				self.statusparam["orderType"] = 0;
			}
			self.statusparam["orderBy"] = field;
		},
		//排序需保证唯一性，否则翻页可能出现重复记录。因此加SYS_DOCUMENTID
		getOrderBy : function(statusparam) {
			//return (typeof statusparam == "undefined") ? self.statusparam["orderBy"] : statusparam["orderBy"];
			var orderBy = (typeof statusparam == "undefined") ? self.statusparam["orderBy"] : statusparam["orderBy"];
			if (orderBy && orderBy != "SYS_DOCUMENTID") {
				orderBy += ",SYS_DOCUMENTID";
			}
			return orderBy;
		},
		getOrderType : function(statusparam) {
			//return (typeof statusparam == "undefined") ? self.statusparam["orderType"] : statusparam["orderType"];
			var orderType = (typeof statusparam == "undefined") ? self.statusparam["orderType"] : statusparam["orderType"];
			
			var orderBy = (typeof statusparam == "undefined") ? self.statusparam["orderBy"] : statusparam["orderBy"];
			if (orderBy && orderBy != "SYS_DOCUMENTID") {
				orderType += ",1";
			}
			return orderType;
		},
		
//----上面是状态条上的函数，下面是列表方式上的函数----
//列表结果返回后的显示：清空table，重新填写内容
setListData : function(data) {
	if (scroll2page.enabled)
		scroll2page.setListData(data);
	else
		self._setListData(data);
	
},
_setListData : function(data) {
	$("#doclistframe").empty();
	$("#doclistframe").html(data);
	
	self.doclistInit();
	self.api.broadcast("setDataFinish");
},

//固定表头
pinHeader : function() {
	if (self.isAlbum()) {
		$("#doclistframe").css("overflow","auto");
		return;
	}
	
	var wrap = $("#doclistframe").css("overflow","hidden");
	var list = $("#listing table");
	var thead = list.find("tr:first")
	
	var theadWrap = $("#tablePinHeader").append(thead);
	var tbodyWrap = $("#listing").css({
				"overflow":"auto",
				"height":wrap.height()-theadWrap.height()
			});
	var tbodyHead = list.find("tr:first").children();
	thead.find("th").each(function(i,elm){
		var w = parseInt(this.style.width)||this.getAttribute("width");
		if(!w)return;
		
		var nowtd = tbodyHead.eq(i);
		nowtd.width(w);
	});
	tbodyWrap.scroll(function (){
		theadWrap.css("margin-left",-tbodyWrap.scrollLeft());
	})
},

//文档列表窗口加载后的第一次显示处理
doclistInit : function(){
	//固定表头
	self.pinHeader();
	
	//记录选中行的内部数组变量清空
	self._resetSelectParam();
		
	//设置状态条上的总个数
	var listing = document.getElementById("listing");
	var total = listing.getAttribute("totalCount");
	self.setCount(total);

	//列表显示方面的处理：填补空格，设置列头排序标记。nCurPage没用
	var nCurPage = self.statusparam.currentPage;
	self.disp(nCurPage, true);//true=donot highlight.wait util filterOldSelects()

	//定义每行的click等事件
	self.defineListEvents();
	
	//最后表明：选择了n个文档，以发出事件
	self.filterOldSelects();
	self._selectDoc(self._ids(), self._libids());
	
	//按自定义列表显示。会每次读数据库取个人配置。需要时打开
	self.showCustomListpage();
},
showCustomListpage : function() {
	priv.showCustomListpage();
},
//定义每行的点击、双击、右键菜单事件
defineListEvents : function(table){
	var objs = (self.isAlbum()) ? $("#listing table") : $("#listing table tr");
	if (objs.length == 0) return;
	
    objs.each(function(i){
		//if (!self.isAlbum() && i == 0) return; //table header
		
		if (!this.onclick)
			this.onclick = function(evt){self.selectDoc(this, evt);};
		if (!this.ondblclick)
			this.ondblclick = function(evt){self.dClickDoc(this, evt);};
		if (!this.oncontextmenu)
			this.oncontextmenu = function(evt){self.callMenu(this,evt);};
	});
	
	//定义所有checkbox的事件
	self.defineChecksEvent();
},
//定义所有checkbox的事件
defineChecksEvent : function() {
	priv._defineCheckEvent();
},
//定义一个checkbox的事件，用于手工加一行时
defineCheckEvent : function(src, evt) {
	priv.inputclick(src, evt);
},
selectDoc : function(src, evt){
	evt = evt || event;

	var ctrl = evt.ctrlKey;
	var a = self.getClickRoot(src);
	self.dealSelect(ctrl, a);
	self._selectDoc(self._ids(), self._libids());
},
dClickDoc : function(src, evt){
	self.unselectAll();
	
	var a = self.getClickRoot(src);
	self.dealSelect(false, a);
	self._selectDoc(self._ids(), self._libids());
	
	self.refreshDocView(a.id, a.getAttribute("libid"));//打开浏览窗口
},
//----end.文档列表初始加载

//列表的显示
disp : function(curPage, afterRefresh){
	if (!afterRefresh) {
		self.refreshPage();
		return;
	}
	//self.fillBlankTD(document);
	document.body.scrollTop = 0;//翻页后从头开始显示

	self.changeSymbol();
},
//把表格中空的部分填上一个空格，以免显示难看
fillBlankTD : function(ele){
	var tds = ele.getElementsByTagName("TD");
	var i = 0;
	for(i = 0;i < tds.length;i ++) {
		if(!tds[i].innerHTML)
			tds[i].innerHTML += "&nbsp;";
	}
},
/**
 * 在重新取DocList后使用这个方法，判断原来选中的文档是否还在，若不在则修改idArr
 * 注意这样当翻页之后做了操作再回来刷新，则隐藏页面里的选中文档会被去掉。

 * 因为只判断了当前document中是否可以找到。
 */
filterOldSelects : function(){
	var docids = self.doclistparam.docIDs;
	if (!docids) return;
	var ids1 = docids.split(",");
	for (var i = 0; i < ids1.length; i++)
	{
		var index = self.getIndex(handlerParam.idArr, ids1[i]);
		var a = document.getElementById(ids1[i]);
		
		if (a == null) {//没找到之前选中的文档，则去掉
			if (index >= 0) self._spliceArray(index);
		} else {//找到了选中的文档
			if (index < 0) self._pushArray(a);
			self._clickOne(a);
			if (a.scrollIntoView) a.scrollIntoView(false);
		}
	}
},
_clickOne : function(a) {
	self.updateColor(a, handlerParam.selBgColor, handlerParam.selColor);//填入颜色
	priv.docheck(a.id);//选中当前复选框
},
_unclickOne : function(a, index) {
	self.updateColor(a, handlerParam.bgColorArr[index], handlerParam.fgColorArr[index]);
	priv.uncheck(a.id);//取消选中当前复选框
},

//在列头显示排序标记 ▲▼▽△
changeSymbol : function() {
	var orderBy = self.statusparam["orderBy"];
	if (!orderBy) return;
	
	var symbol = ["▲", "▼"];
	var order = self.statusparam["orderType"];

	var id = "TH_" + orderBy;
	var span = document.getElementById(id);
	if (!span) return;
	
	var value = span.innerHTML;
	if (order) { //down
		var pos = value.indexOf(symbol[0]);
		if (pos > 0) value = value.substring(0, pos);
		value += symbol[1];
	}
	else {
		var pos = value.indexOf(symbol[1]);
		if (pos > 0) value = value.substring(0, pos);
		value += symbol[0];
	}
	span.innerHTML = value;
},

//点击文档列表时，找到可以取属性的位置
//当列表方式时，找到TR；当图片相册方式时，找到Table
getClickRoot : function(src){
	return src;
},

//分别按单选、多选来改变选中值、改变颜色
dealSelect : function(ctrl, a){
	//---单选---
	if (!ctrl){
		var sameTD = false;
		if (self._ids() == a.id + ",") sameTD = true;
		
		self.unselectAll();
		if (sameTD) return;//若只是选中的再次选择，则取消后就返回
		
		self._pushArray(a);
		self._clickOne(a);
		return;
	}
	//---多选---
	else {
		var index = self.getIndex(handlerParam.idArr, a.id);
		if (index < 0){//之前没选中
			self._pushArray(a);
			self._clickOne(a);
			return;
		} else {
			//之前选中了，则去掉
			self._spliceArray(index);
			self._unclickOne(a, index);
		}
	}
},
//填充表格颜色
updateColor : function(a, abgColor, afgColor){
	if (!abgColor) abgColor = "";
	if (!afgColor) afgColor = "";
	
	a.bgColor = abgColor;

	var tds = a.getElementsByTagName("TD");
	if(tds == null) return;

	for (var i = 0;i < tds.length;i++){
		tds[i].style.backgroundColor = abgColor;
		tds[i].style.color = afgColor;
	}
},
//取消所有选中
unselectAll : function(){
	for (var i = 0; i < handlerParam.idArr.length; i++){
		var id = handlerParam.idArr[i];
		var a = document.getElementById(id);
		if(a != null)
			self.updateColor(a, handlerParam.bgColorArr[i], handlerParam.fgColorArr[i]);
	}
	self._resetSelectParam();
	
	priv.uncheckAll();//同时取消所有复选
},

//--Select All (Ctrl + A)
selectAll : function(){
	var allTR = null;
	if(self.isAlbum()) {
		allTR = $("#listing")[0].getElementsByTagName("table");
	}
	else
		allTR = $("#listing table")[0].rows;
	if (allTR == null || allTR.length == 0) return;
	
	//先取消全部
	self.unselectAll();

	//全选
	for (var i = 0; i < allTR.length; i++){
		var a = allTR[i];
		if (a.id == null || a.id == "") continue;

		self._pushArray(a);
		self.updateColor(a, handlerParam.selBgColor, handlerParam.selColor);
	}
	//设置checkbox
	priv.checkAll();
	
	//发出选择事件
	self._selectDoc(self._ids(), self._libids());
},
//---处理数组的操作---
getIndex : function(arr, id){
	for (var i = 0; i < arr.length; i++)
		if (arr[i] == id) return i;
	return -1;
},
_pushArray : function(a){
	handlerParam.idArr.push(a.id);
	handlerParam.libidArr.push(a.getAttribute("libid"));
	handlerParam.bgColorArr.push(a.bgColor);
	handlerParam.fgColorArr.push(a.style.color);
},
_spliceArray : function(index){
	handlerParam.idArr.splice(index, 1);
	handlerParam.libidArr.splice(index, 1);
	handlerParam.bgColorArr.splice(index, 1);
	handlerParam.fgColorArr.splice(index, 1);
},
_ids : function() {
	return priv._joinArr(handlerParam.idArr);
},
_libids : function() {
	return priv._joinArr(handlerParam.libidArr);
},

//记录选中行的内部变量清空
_resetSelectParam : function(){
	handlerParam.idArr = [];
	handlerParam.libidArr = [];
	handlerParam.bgColorArr = [];
	handlerParam.fgColorArr = [];
},
//------------------------------------

	//打开浏览窗口
	refreshDocView : function(id, libid){
		var url = self.pathPrefix + "../e5workspace/DocView.do?DocIDs=" + id + "&DocLibID=" + libid;
		var feature = "scrollbars=yes,status=no,toolbar=no,location=no,menubar=no,resizable=1,"
			+ "width=1000,height=700";
		var wnd = window.open(url,"_blank", feature);
		wnd.focus();
	},
	//---keydown的事件响应---
	localKeyDown : function(evt){
		evt = evt || event;
		if (evt.ctrlKey && (evt.keyCode == 65)) {
			self.keyAll();
			return false;
		}
	},
	//按Ctrl + A的响应函数
	keyAll : function() {
		self.selectAll();
	},

	//---右键菜单---
	callMenu : function(src, evt){
		evt = evt || event;

		//先作为左键点击一下（若已经左键选中了，则不再点击）
		var a = self.getClickRoot(src);

		var hasSelected = (self.getIndex(handlerParam.idArr, a.id) >= 0);
		if (!hasSelected)
			self.selectDoc(src, evt);
		//右键菜单显示的是操作，因此逻辑放在Toolkit.js里完成，这里发出右键菜单的消息。
		self.api.broadcast("showMenu", {x:evt.clientX, y:evt.clientY});
	},

	//---跨页选中情况的记录---
	//跨页选中功能:增量式记录选中的记录
	//数组形式:[ {libid:1, id:123}, {...}, {...} ]
	recordList : [], //[ {libid:1, id:123}, {...}, {...} ]
	remember : function() {
		var ids = self.doclistparam.docIDs;
		var libs = self.doclistparam.docLibIDs;;
		
		if (!ids || !libs) return;
		ids = ids.split(","); //最后多一个逗号
		libs = libs.split(",");
		
		for (var i = 0; i < ids.length - 1; i++) {
			var data = {"libid": libs[i], "id": ids[i]};
			if (self._containsID(self.recordList, data)) {
				continue;
			}
			else {
				self.recordList.push(data);
			}
		}
		self.api.broadcast("listRemember", self.recordList);
		alert(statusinfo.remember_ok);
	},
	clearRemember : function() {
		self.recordList = [];
		self.api.broadcast("listRemember", self.recordList);
		alert(statusinfo.remember_ok);
	},
	_containsID : function(arr, data) {
		for (var i = 0; i < arr.length; i++) {
			if (arr[i].id == data.id) return true;
		}
		return false;
	}
}

	//-----init & onload--------
	var init = function(sandbox){
			self.api = sandbox;
			self.statusparam = new StatusbarParam();
			self.doclistparam = new DocListParam();
			$("#Turn2Page").keydown(self.turnPage);
			$("#btnRefresh").click(self.refreshPage);
			$("#btnLastpage").click(function(){self.changePage(0)});
			$("#btnNextpage").click(function(){self.changePage(1)});
			//$("#btnself.changePage").click(function(){self.changePage(2)});
			
			$("#btnFirstpage").click(function(){self.changePage(4)});
			$("#btnFinalpage").click(function(){self.changePage(5)});
			
			$("#ListCustSpan").click(priv.doListCustomize);
			//$("#ListPages").change(priv.changeListPage);
			$("#CountPerPage").change(self.refresh4ChangeCount);
			$("#ListExportSpan").click(priv.exportList);
			$("#ListRemember").click(self.remember);
			$("#ListRemClear").click(self.clearRemember);
			
			var hideListRemember = true;
			if (hideListRemember) {
				$("#ListRemember").hide();
				$("#ListRemClear").hide();
				$("#ListSeparator").hide();
			}
			
			$(document).keydown(self.localKeyDown);

			self.api.listen("workspace.search:searchTopic", priv.listening);
			//响应工具栏的刷新列表的消息
			self.api.listen("workspace.toolkit:refreshTopic", self.refreshListening);
			
			//发出消息表示加载完毕ready
			self.api.broadcast("ready");
			
			ready = true;
		};
	var isReady = function() {
		return ready;
	}
	return {
		init : init,
		getListHeaders : priv.getListHeaders,
		listCustDone : priv.listCustDone,
		handlerParam : handlerParam,
		self : self,
		isReady : isReady
	}
},{requires:["../e5script/jquery/jquery.min.js", 
"../e5script/e5.utils.js", 
"../e5workspace/script/Param.js",
"../e5script/jquery/jquery.dialog.js", 
"../e5script/jquery/dialog.style.css"
]});