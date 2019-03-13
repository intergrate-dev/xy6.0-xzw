e5.mod("workspace.doclist",function() {
	//变量定义
	var handlerParam  = {
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
			//var last = priv._listID(list0);
			//var lp = $("#listpage_" + last);
			//lp.click(); 这一句在刚进入页面默认点击时不起作用，改成下面一句
			priv._clickOneList(lp);
		},
		_readCookie_lp : function() {
			var cookieName = priv._cookie_lp(self.statusparam);
			return e5.utils.getCookie(cookieName);
		},
		/* cookie里记的列表方式名。
		 * 注意若文件夹只有一个列表方式，则不会记录cookie
		 */
		_cookie_lp : function(statusparam) {
			return "ws_lp_" + statusparam.docLibID;
		},
		
		//从列表方式返回串里提取列表方式ID
		_listID : function(listValue) {
			var arr = listValue.split("@");
			return arr[0];
		},
		//列表方式的图标/文字点击事件
		listPageClick : function(e) {
			var src = $(e.target);
			return priv._clickOneList(src);
		},
		_clickOneList : function(src) {
			var myvalue = src.attr("value");
			//遵循原有逻辑，对隐藏列表方式域赋值，其它各处引用的是这个隐藏列表方式域
			var lp = $("#ListPages");
			var oldValue = priv._listID(lp.val());
			lp.val(myvalue);
			
			//清除上次的选中状态
			$("#listpage_" + oldValue).removeClass("page-selected").addClass("page-unselected");
			//设置当前的选中状态
			src.removeClass("page-unselected").addClass("page-selected");
			
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
			//重置每页显示条数的select
			priv.setCountPerPage();
			
			//重新从第一页开始显示
			self.resetPage();
			
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
				
				//看看是不是缓存着上次的显示条数
				var lastSelCount = e5.utils.getCookie("ws_lp_count_" + priv._listID(datas));
				
				for (var j = 0; j < filterArr.length; j++) {
					var op = document.createElement("OPTION");
					op.value = filterArr[j];
					op.text = filterArr[j];
					sel.options.add(op);
					
					//选中上次选的
					if (lastSelCount && op.value == lastSelCount)
						op.selected = "selected";
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
			var srcID = src.attr("id");
			
			var pos = -1;
			var ths = src.parent().children();
			for (var idx = 0; idx < ths.length; idx++) {
				if ($(ths[idx]).attr("id") == srcID) {
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
			
			//重新定义checkbox
			priv._defineCheckEvent();
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
		 */
		//复选框的点击事件
		inputclick : function(obj, evt){
			evt = evt || event;
			//阻止事件冒泡，以免触发selectDoc()事件
			evt.cancelBubble = true;
			
			//取消任一复选框的选中，则取消"全选"
			if (!obj.checked) {
				$("#cbAll").prop("checked", false);
			}
			// //行选中
			// var src = document.getElementById(obj.value);
			// $(src).toggleClass("selected");

            <!--给复选框点击事件中回显到已选择话题功能的实现,1.点击就回显到已选择2.超过5个无法选择start -->
            var id = obj.value;
            var text = $(obj).parent().parent().children("td").eq(2).html();
            var src = $("#"+obj.value);
            //已选择的话题个数小于5
            if($("#topics li").length < 5){
            	//如果已经选中过，取消选中并在已选择中删除该话题
				if($(src).hasClass("selected")){
					$("#topics [data='"+id+"']").remove();
                    $(src).toggleClass("selected");
				}else{//如果未被选中，加属性class为selected，并且添加话题到已选择
                    $("#topics").append("<li data='"+id+"' name='"+text+"'>"+text+"<span>x</span></li>");
                    $(src).toggleClass("selected");
				}
			}else{
            	//如果已经选中过，则取消选中
                if($(src).hasClass("selected")){
                    $("#topics [data='"+id+"']").remove();
                    $(src).toggleClass("selected");
                }else{//未选中过：弹出最多选择5个的提示，去掉checkbox的样式
                    $("#"+obj.value + " input").prop("checked", false);
                	alert("最多可以选择5个哦,请先删除再添加！");
				}
			}
            <!--给复选框点击事件中回显到已选择话题功能的实现end -->
			self.broadcast();
		},

		//点击全选时的事件响应
		selAll : function() {
			var cbAll = document.getElementById("cbAll");
			if (!cbAll.checked) {
				self.unselectAll();
				self.broadcast();
			} else {
				self.selectAll();
			}
		},
		//定义input type="checkbox"的事件//每次后台配置的表刷新时这个函数都会执行
		_defineCheckEvent : function() {
			//不需要的按钮隐藏
            $("#cbAll").hide();
			$("#ListCustSpan").hide();
			$("#btnRefresh").hide();

			var checkboxAll = $("#cbAll");
			if (checkboxAll.length > 0) {
				if (!checkboxAll[0].onclick)
					checkboxAll[0].onclick = priv.selAll;
				
				$("input[type='checkbox'][name='cb']").each(function(i){
					if (!this.onclick)
						this.onclick = function(evt){priv.inputclick(this, evt);};
				});
                <!--设置已选择话题的check标识 start -->
                // console.log($("#listing table input").length);
				//已选择的话题id
                var idDataArray = [];
                $("#topics li").each(function(){
                    idDataArray.push($(this).attr("data"));
                });
                if(idDataArray){
                    $("#listing table tr").each(function(index, element) {
                        var id = $(element).attr("id");
                        if($.inArray(id, idDataArray) >= 0){
                            $("#" + id + " input").prop("checked", true);
                            $(element).attr("class", "selected");
						}

                    });
				}
                <!--设置已选择话题的check标识 end -->

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

			//var theURL = "../e5workspace/listExport.do?DocLibID=" + self.statusparam.docLibID
			var theURL = "../e5workspace/listExcelExport.do?DocLibID=" + self.statusparam.docLibID
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
			
			theURL += "&Count=3000";

			window.open(theURL, "_blank");
		},
		//当前定制列
		exportFields : function() {
			var fields = "";
			
			var names = priv.getCustLines();
			if (names) {
				for (var i = 0; i < names.length; i++) {
					names[i] = names[i].substring(3);//去掉前面的"TH_"
					if (names[i] != "DocOrder" && names[i] != "CheckBox") {
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
	
	/* ------------------------------------------
	 * 把方法都包在一个对象里，方便外部调用或修改
	 * ------------------------------------------*/
	var self = {
		api : null,
		statusparam : null,
		doclistparam : null,
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
			if (!param["listID"] || param["listID"] == "0") {
				var theURL = self.pathPrefix + "../e5workspace/statusbar.do?DocTypeID=" + param.docTypeID
					+ "&Flag=1&FVID=" + param.fvID;
				return theURL;
			} else {
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
			self.api.broadcast("setPrivateNums",value);
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
				var listID = value1[0];
				
				//若列表可选择多个条数，则缓存，以便下次仍用这个条数
				var sel = document.getElementById("CountPerPage");
				if (sel && sel.options && sel.options.length > 1) {
					var cookieName = "ws_lp_count_" + listID;
					e5.utils.setCookie(cookieName, sel.value);
				}
				
				return listID;
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
			self.refreshPage();
		},
		//翻页时的刷新列表
		refresh4Page : function() {
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
			var ruleFormula = statusparam.ruleFormula;
			ruleFormula = ruleFormula.replace("a_columnID_EQ_0_AND_","");
			var beforeURL = self.pathPrefix + "../e5workspace/doclist.do";
			if(statusparam.curTab=="cori"&&ruleFormula.indexOf("a_catID")>=0){//如果是原稿库
				beforeURL = self.pathPrefix + "../xy/doclistOrg.do";
			}
			
			var theURL = beforeURL + "?DocLibID=" + statusparam.docLibID
				+ "&FVID=" + statusparam.fvID
				+ "&FilterID=" + statusparam.filterID
				+ "&ListPage=" + statusparam.listPage
				+ "&CurrentPage=" + (1 + parseInt(statusparam.currentPage))
				+ "&CatTypeID=" + statusparam.catTypeID
				+ "&ExtType=" + statusparam.extType  /*--扩展类型--*/
				+ "&RuleFormula=" + e5.utils.encodeSpecialCode(ruleFormula).replace("u_siteID_EQ_0","")
				+ "&Query=" + e5.utils.encodeSpecialCode(statusparam.query)
				+ "&siteID=" + statusparam.siteID
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
		broadcast : function(){
			//选中的ID
			var docLibIDs = "";
			var docIDs = "";
			var curName = $('.select span', window.parent.document).html();
			if(curName == 'Web发布库'){
				var wH = $(window).height();
				var warning = "";
				$(".doclist tr td:nth-child(5)").mouseover(function(e){
					var topH = $(this).offset().top;
					if(topH + 115-wH > 0){
						$(".showDiv").css({
							'left': 350 + 'px',
							'top': (wH-250) + 'px'
						})
					}else{
						$(".showDiv").css({
							'left': 350 + 'px',
							'top': (topH-65) + 'px'
						})
					}
					var articleID = $(this).parent().attr("id");
					warning = setTimeout(function () {
				$.post("MainArticleInfo.do",{articleID:articleID},function(data){
					var str = "";
					var columnRel=data.columnRel.split(",");
					for (i = 0,len = columnRel.length;i < len;i++){
						x = columnRel[i].replace(/中国网栏目~/,"");
						str += x +'<br/>';
					}

					$(".showDiv").html("<p class='listP'>标题："+'<span title="'+data.articleTitle+'">'+data.articleTitle+'<span>'
						+"</p><p class='listP'>栏目："+'<span title="'+data.column+'">'+data.column+'<span>'
						+"</p><p class='listP'>关联栏目："+'<span title="'+str+'">'+str+'<span>'
						+"</p>");
				});
					$(".showDiv").css("padding","4px");
					$(".showDiv").show();
					}, 1000);
				});
				$(".doclist tr td:nth-child(5)").mouseout(function(){
					clearTimeout(warning);
					$(".showDiv").hide();
				});
			}


			$(".doclistframe .selected").each(function(i){
				var tr = $(this);
				if (tr.attr("libid")) {
					if (docIDs) {
						docLibIDs += ",";
						docIDs += ",";
					}
					docLibIDs += tr.attr("libid");
					docIDs += tr.attr("id");
				}
			});
			
			//准备发出的消息
			for (var name in self.doclistparam)
				self.doclistparam[name] = "";
			for (var name in self.statusparam)
				self.doclistparam[name] = self.statusparam[name];
				
			self.doclistparam.docIDs = docIDs;
			self.doclistparam.docLibIDs = docLibIDs;
			//self.doclistparam.isQuery = (self.statusparam.query ? true : false);
			//改成只在高级检索的时候才标识为检索，使不出现新建操作
			self.doclistparam.isQuery = (self.statusparam.extType == 10);
			self.doclistparam.isRule = (self.statusparam.ruleFormula ? true : false);

			self.api.broadcast("doclistTopic", self.doclistparam);
		},
		_ids : function() {
			//选中的ID
			var docIDs = "";
			$(".doclistframe .selected").each(function(i){
				var tr = $(this);
				if (tr.attr("libid")) {
					if (docIDs) {
						docIDs += ",";
					}
					docIDs += tr.attr("id");
				}
			});
			return docIDs;
		},
		/* 设置扩展参数。该参数将一直传递到操作 */
		setExtParams : function(params){
			self.statusparam.extParams = params;
		},
		setOrderBy : function(field) {
			if (field == self.statusparam["orderBy"]) {
				self.statusparam["orderType"] = self.statusparam["orderType"] ? 0 : 1;
			} else {
				self.statusparam["orderType"] = 0;
				self.statusparam["orderBy"] = field;
			}
		},
		getOrderBy : function(statusparam) {
			var orderBy = (typeof statusparam == "undefined") ? self.statusparam["orderBy"] : statusparam["orderBy"];
			if (orderBy && orderBy != "SYS_DOCUMENTID") {
				orderBy += ",SYS_DOCUMENTID";
			}
			return orderBy;
		},
		getOrderType : function(statusparam) {
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
	
	self.broadcast();
	
	//按自定义列表显示。会每次读数据库取个人配置。需要时打开
	self.showCustomListpage();
},
showCustomListpage : function() {
	priv.showCustomListpage();
},
showCustomListpageSort : function(){
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
},
//定义每行的点击、双击、右键菜单事件
defineListEvents : function(){
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
//选中一行事件
selectDoc : function(src, evt){
	evt = evt || event;
	self.changeTRStatus(evt.ctrlKey, src, evt.shiftKey);
	
	// self.broadcast();
},
dClickDoc : function(src, evt){
	//取消所有选中的状态
	// self.unselectAll();
	// if(evt.view.name && evt.view.name.substring(0,13) == 'OpenartDialog'){
    //     $("#doSave").click();
	// }else{
    //     //（来源管理中，双击还是查看详情）
	// 	self.changeTRStatus(false, src);
    //     self.broadcast();
    //     //打开浏览窗口
    //     self.refreshDocView(src.id, src.getAttribute("libid"));
	// }
},
//----end.文档列表初始加载

//列表的显示
disp : function(curPage, afterRefresh){
	if (!afterRefresh) {
		self.refreshPage();
		return;
	}
	document.body.scrollTop = 0;//翻页后从头开始显示

	self.changeSymbol();
},
/**
 * 刷新DocList后重新选中
 */
filterOldSelects : function(){
	var docids = self.doclistparam.docIDs;
	if (!docids) return;
	
	var ids1 = docids.split(",");
	for (var i = 0; i < ids1.length; i++) {
		var a = document.getElementById(ids1[i]);
		if (a) {
			var tr = $(a);
			tr.addClass("selected");
			tr.find("input[type='checkbox'][name='cb']").prop("checked", true);
			
			if (a.scrollIntoView) a.scrollIntoView(false);
		}
	}
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

//分别按单选、多选来改变选中值、改变颜色。旧版用名：dealSelect
changeTRStatus : function(ctrl, a, shift){
	var tr = $(a);
	
	//处理shift多选
	// if (shift) {
	// 	self.shiftChangeTR(tr);
	// 	return;
	// }
	
	self.shiftItems.length = 0;
	self.shiftItems.push(tr.index());

    var id = tr.attr("id");
    var text = $(tr).children("td").eq(2).html();
	
	if (!ctrl){
		//---单选---
		self.unselectAll();
		tr.addClass("selected");
		tr.find("input[type='checkbox'][name='cb']").prop("checked", true);

        $("#topics").html("<li data='"+id+"' name='"+text+"'>"+text+"<span>x</span></li>");
	} else {
        var src = tr;
        <!--整行的点击事件1.点击就回显到已选择2.超过5个无法选择start -->
        //已选择的话题个数小于5
        if($("#topics li").length < 5){
            //如果已经选中过，取消选中并在已选择中删除该话题
            if($(src).hasClass("selected")){
                $("#topics [data='"+id+"']").remove();
                tr.toggleClass("selected");
                var checkbox = tr.find("input[type='checkbox'][name='cb']");
                checkbox.prop("checked", !checkbox.prop("checked"));
            }else{//如果未被选中，加属性class为selected，并且添加话题到已选择
                $("#topics").append("<li data='"+id+"' name='"+text+"'>"+text+"<span>x</span></li>");
                tr.toggleClass("selected");
                var checkbox = tr.find("input[type='checkbox'][name='cb']");
                checkbox.prop("checked", !checkbox.prop("checked"));
            }
        }else{
            //如果已经选中过，则取消选中
            if($(src).hasClass("selected")){
                $("#topics [data='"+id+"']").remove();
                tr.toggleClass("selected");
                var checkbox = tr.find("input[type='checkbox'][name='cb']");
                checkbox.prop("checked", !checkbox.prop("checked"));
            }else{//未选中过：弹出最多选择5个的提示
                alert("最多可以选择5个哦,请先删除再添加！");
            }
        }
        <!--给整行添加点击事件中回显到已选择话题功能的实现end -->
	}
},

//按shift选中
shiftItems : [],
shiftChangeTR : function(tr) {
	tr.addClass("selected");

	var items = self.shiftItems;
	if (items.length == 0){
		items.push(tr.index());
		return;
	} else if (items.length == 1){
		items.push(tr.index());
	} else if (items.length == 2){
		items[1] = tr.index();
	}
	//取消其它选中
	tr.siblings(".selected").each(function(i) {
		var src = $(this);
		src.removeClass("selected");
		src.find("input[type='checkbox'][name='cb']").prop("checked", false);
	});
	
	//选中[start, end]之间的每行
	var iMin = Math.min(items[0],items[1]);
	var iMax = Math.max(items[0],items[1]);
	for (var i = iMin; i <= iMax; i++){
		var src = null;
		if (self.isAlbum()) {
			src = $(".doclistframe table[libid]:eq(" + i + ")");
		} else {
			src = $(".doclistframe tr[libid]:eq(" + i + ")");
		}
		src.addClass("selected");
		src.find("input[type='checkbox'][name='cb']").prop("checked", true);
	}
},
//取消所有选中
unselectAll : function(){
	$("#cbAll").prop("checked", false);
	
	if (self.isAlbum()) {
		$(".doclistframe table[libid]").removeClass("selected");
	} else {
		$(".doclistframe tr[libid]").removeClass("selected");
	}
	$(".doclistframe input[type='checkbox'][name='cb']").prop("checked", false);
},

//--Select All (Ctrl + A)
selectAll : function(){
	$("#cbAll").prop("checked", true);

	if (self.isAlbum()) {
		$(".doclistframe table[libid]").addClass("selected");
	} else {
		$(".doclistframe tr[libid]").addClass("selected");
	}
	$(".doclistframe input[type='checkbox'][name='cb']").prop("checked", true);

	//发出选择事件
	self.broadcast();
},
//------------------------------------

	//打开浏览窗口
	refreshDocView : function(id, libid){
		var url = self.pathPrefix + "../e5workspace/DocView.do?DocIDs=" + id + "&DocLibID=" + libid;
		var feature = "scrollbars=yes,status=no,toolbar=no,location=no,menubar=no,resizable=1,"
			+ "width=1000,height=700,left=300,top=100";
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
		// self.selectAll();
	},
	//---右键菜单---
	callMenu : function(src, evt){
		evt = evt || event;
		
		//先作为左键点击一下（若已经左键选中了，则不再点击）
		if (!$(src).hasClass("selected"))
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
			$("#btnself.changePage").click(function(){self.changePage(2)});
			$("#btnFirstpage").click(function(){self.changePage(4)});
			$("#btnFinalpage").click(function(){self.changePage(5)});
			
			$("#ListCustSpan").click(priv.doListCustomize);
			$("#ListPages").change(priv.changeListPage);
			$("#CountPerPage").change(self.refreshPage);
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
			// self.api.broadcast("ready");
		};
	return {
		init : init,
		getListHeaders : priv.getListHeaders,
		listCustDone : priv.listCustDone,
		self : self
	}
},{requires:["../e5script/jquery/jquery.min.js", 
"../e5script/jquery/jquery.dialog.js", 
"../e5script/jquery/dialog.style.css"
]});