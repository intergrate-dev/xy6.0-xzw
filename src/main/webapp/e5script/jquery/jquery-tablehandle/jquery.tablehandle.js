
/// <reference path="jquery-1.7.1.js" />

/*!
* jQuery Plugin tablehandle v1.0.0
*
* Copyright 2012, wang.yq
* Dual licensed under the MIT or GPL Version 2 licenses.
*
* Copyright 2012, The Founder
* Released under the MIT, BSD, and GPL Licenses.
*
* Date: 2012-5-31
* 依赖库及资源
* 1.jquery-1.7.1.js
* 2.jquery-ui-1.8.18.js
* 3.jquery-ui-1.8.18.custom.css(以及相关的图片)
* 4.jquery.contextmenu.r2.js
* 5.jquery.tablehandle.css(以及相关的图片)
*/
; (function ($) {

    jQuery.extend(jQuery.fn, {

        tablehandle: function (options) {
            /// <summary>
            /// 表格操作
            /// </summary>
            /// <returns type="jQuery" />

            if (!filter(options)) {    //验证参数是否合法

                return this;
            }

            //覆盖原来参数
            var options = jQuery.extend({}, jQuery.fn.tablehandle.defaults, options);
            options.id = $(this).attr("id");
            //保存原来的样子
            options.tablerecover = $(this).html();
            options.undocontent = $(this).html();

            //初始化表格
            tableInit(options);
           

            return this.each(function () {
            });    // end of each

        }


    });    //end of extend



    jQuery.fn.tablehandle.defaults = {   //默认参数值

        //context menu
        menuid: "table-contextmenu",
        tablehandle_undo: "撤销",
        tablehandle_addrow_up: "插入上面一行",
        tablehandle_addrow_down: "插入下面一行",
        tablehandle_addcol_left: "插入左侧列",
        tablehandle_addcol_right: "插入右侧列",
        tablehandle_delrow: "删除行",
        tablehandle_delcol: "删除列",
        //tablehandle_mergcell: "合并单元格",
        tablehandle_mergdown: "向下合并单元格",
        tablehandle_mergright:"向右合并单元格",
        tablehandle_splitrow: "拆分行",
        tablehandle_splitcol: "拆分单元格",
        tablehandle_delcell: "删除单元格",
        tablehandle_formatimg: "设置为图片格式",
        tablehandle_removeimgformat: "移除图片格式",
        tablehandle_recover: "恢复原来格式",
        tablehandle_removefield: "删除字段",


        //context menu

        //lang
        tablehandle_merg_tiptext: "请选择多个单元格。",
        tablehandle_celldelall_tiptext: "不能删除所有单元格。",
        tablehandle_selectcellone_tiptext: "请选择你要删除的单元格。",
        tablehandle_dontdelonlycol: "不能删除最后一列。",
        tablehandle_dontdelonlyrow: "不能删除最后一行。",
        tablehandle_mergeerror: "当前单元格不能合并。",
        tablehandle_spliterror: "当前单元格不能拆分。",
        tablehandle_delcoleerror: "当前表格有合并行操作，不能删除。",
        tablehandle_delrowfielderror:"请先清除当前行中的字段。",
        tablehandle_delcolfielderror:"请先清除当前列中的字段。",
        tablehandle_mergcolfielderror:"请先清除单元格中的字段。",
        
        //lang

        tableclass: "tablehandleClass",
        tdselectclass:"ui-selected",
        canselect:true, //单元格是否可以选择
        //event
        cellUpdateEvent: null,
        tableRecoverEvent: null,
        tableMenuContainer:null,
        //设置图片
        setImgEvent: null,
        selectingEvent:null,
        selectedEvent: null,
        removeFieldEvent: null,
        tableChangedEvent:null,
        contextMenuShowEvent:null,
        //event

        //图片格式样式
        imgformatclass:"",
        //表格原来的内容
        tablerecover:"",
        undocontent:"",

        id: ""
    };

    function filter(options) {
        /// <summary>
        /// 私有函数,验证参数是否有效
        /// </summary>
        /// <returns type="bool" />

        return !options || (options && typeof options == "object") ? true : false;
    }

    function tableInit(options) {
        /// <summary>初始化表格</summary>
        /// <param name="options" type="String">默认选项</param>
        var tb = $("#" + options.id);
        //设置菜单
        if (options.tableclass != "") {
            tb.addClass(options.tableclass);
        }
        //
        //初始化菜单
        initContextmenu(options);
        //设置菜单
        setContextmenu(tb, options);

        //设置单元格可以选择
        //tb.selectable();
        if (options.canselect) {
            tb.find("td").addClass("ui-state-default");
            tb.selectable({
                filter: "td",
                selected: function (event, ui) {
                    if (options.selectedEvent != null) {
                        options.selectedEvent(event, ui);
                    }
                    tb.find("td[selected_cell='1']").removeAttr("selected_cell");
                    $(this).attr("selected_cell", "1");
                    //console.log("selected");
                },
                selecting: function (event, ui) {
                    if (options.selectingEvent != null) {
                        options.selectingEvent(event, ui);
                        
                    }
                    //console.log("selecting");
                }

            });
        }
        
    }

    function buildTableMap(tb) {
        /// <summary>
        /// 把当前表格，创建一个二维数组中
        /// </summary>
        /// <param name="table" type="jq">table jquery object </param>
        /// <returns type="Array" />
        var table = tb[0];
        var aRows = table.rows;

        // Row and Column counters.
        var r = -1;

        var aMap = [];

        for (var i = 0 ; i < aRows.length ; i++) {
            r++;
            !aMap[r] && (aMap[r] = []);

            var c = -1;

            for (var j = 0 ; j < aRows[i].cells.length ; j++) {
                var oCell = aRows[i].cells[j];

                c++;
                while (aMap[r][c])
                    c++;

                var iColSpan = isNaN(oCell.colSpan) ? 1 : oCell.colSpan;
                var iRowSpan = isNaN(oCell.rowSpan) ? 1 : oCell.rowSpan;

                for (var rs = 0 ; rs < iRowSpan ; rs++) {
                    if (!aMap[r + rs])
                        aMap[r + rs] = [];

                    for (var cs = 0 ; cs < iColSpan ; cs++) {
                        aMap[r + rs][c + cs] = aRows[i].cells[j];
                    }
                }

                c += iColSpan - 1;
            }
        }
        return aMap;
    }

    function insertRow(t, options, insertBefore) {
        /// <summary>
        /// 插入一行
        /// </summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
        /// <param name="insertBefore" type="bool">是否插入前面</param>
        
        var tb = $("#" + options.id);
        var cells = $(t),
				firstCell = cells[0],
				table = tb[0],
				doc = firstCell.document,
				startRow = firstCell.parentElement,
				startRowIndex = startRow.rowIndex,
				lastCell = cells[cells.length - 1],
				endRowIndex = lastCell.parentElement.rowIndex + lastCell.rowSpan - 1,
				endRow = table.rows[endRowIndex],
				rowIndex = insertBefore ? startRowIndex : endRowIndex,
				row = insertBefore ? startRow : endRow;

        var map = buildTableMap(tb),
				cloneRow = map[rowIndex],
				nextRow = insertBefore ? map[rowIndex - 1] : map[rowIndex + 1],
				width = map[0].length;

        var newRow = $('<tr></tr>');
        for (var i = 0; cloneRow[i] && i < width; i++) {
            var cell;
            // Check whether there's a spanning row here, do not break it.
            if (cloneRow[i].rowSpan > 1 && nextRow && cloneRow[i] == nextRow[i]) {

                cell = cloneRow[i];
                cell.rowSpan += 1;
            }
            else {
                cell = $("<td>&nbsp;</td>")[0] ;
                cell.colSpan = cloneRow[i].colSpan;
                cell.removeAttribute('rowSpan');
                $(cell).addClass($(cloneRow[i]).attr("class"));

                clearSpecialClass($(cell),options);

                !$.browser.msie ;
                newRow.append($(cell));
               
            }

            i += cell.colSpan - 1;
        }

        insertBefore ?
        $(row).before(newRow):
        $(row).after(newRow);

        if (options.tableChangedEvent != null) {
            options.tableChangedEvent("row");
        }
        //设置菜单
        setContextmenu($("#" + options.id), options);
    }

    function deleteRows(t, options,currow) {
        /// <summary>
        /// 删除一行
        /// </summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
        var tb = $("#" + options.id);
        
        //判断当前行中单元格是否为空
        var tempTr = $(t).parent();
        
        var hasField = false;
        tempTr.find("td").each(function(){
        	if(!cellIsEmpty(this)){
        		hasField = true;
        	}
        });
        if(hasField){
        	alert(options.tablehandle_delrowfielderror)
        	return false;
        }
        if (currow == null) {
            
            var cells = $(t),
                    firstCell = cells[0],
                    table = tb[0],
                    map = buildTableMap(tb),
                    startRow = firstCell.parentElement,
                    startRowIndex = startRow.rowIndex,
                    lastCell = cells[cells.length - 1],
                    endRowIndex = lastCell.parentElement.rowIndex + lastCell.rowSpan - 1,
                    rowsToDelete = [];
            var rows = table.rows;
            // Delete cell or reduce cell spans by checking through the table map.
            for (var i = startRowIndex; i <= endRowIndex; i++) {
                var mapRow = map[i],
                        row = table.rows[i];

                for (var j = 0; j < mapRow.length; j++) {
                    var cell = mapRow[j],
                            cellRowIndex = cell.parentElement.rowIndex;

                    if (cell.rowSpan == 1) {
                        
                        if (rows.length > 1) {
                            $(cell).remove();
                        }
                        // Row spanned cell.
                    }
                    else {
                        // Span row of the cell, reduce spanning.
                        cell.rowSpan -= 1;
                        // Root row of the cell, root cell to next row.
                        if (cellRowIndex == i) {
                            var nextMapRow = map[i + 1];
                            //nextMapRow[j - 1] ?
                            //cell.insertAfter(new CKEDITOR.dom.element(nextMapRow[j - 1]))
                            //		: new CKEDITOR.dom.element(table.$.rows[i + 1]).append(cell, 1);
                            nextMapRow[j - 1] ?
                            $(nextMapRow[j - 1]).after($(cell))
                                    : $(table.rows[i + 1]).append($(cell));

                        }
                    }

                    j += cell.colSpan - 1;
                }

                rowsToDelete.push(row);
            }

           

            // Where to put the cursor after rows been deleted?
            // 1. Into next sibling row if any;在相临下一个行的
            // 2. Into previous sibling row if any;在相临前一行的
            // 3. Into table's parent element if it's the very last row.
            var cursorPosition = rows[endRowIndex + 1] || (startRowIndex > 0 ? rows[startRowIndex - 1] : null) || table.parentElement;

            for (var i = rowsToDelete.length ; i >= 0 ; i--) {
                if (rowsToDelete[i] != null) {
                    deleteRows(null, options, rowsToDelete[i]);
                }
            }

            return cursorPosition;
        }
        else {


            table = tb[0];

            if (table.rows.length == 1) {
            	//最后一行不能删除
                alert(options.tablehandle_dontdelonlyrow);
                return null;
            }
            else {
                $(currow).remove();
            }
        }
      
        if (options.tableChangedEvent != null) {
            options.tableChangedEvent("row");
        }
        //设置菜单
        setContextmenu($("#" + options.id), options);
        return null;
    }

    function insertColumn(t, options, insertBefore) {
        /// <summary>
        /// 插入一行
        /// </summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
        /// <param name="insertBefore" type="bool">是否插入前面</param>

        var tb = $("#" + options.id);

        var cells =$(t),
			firstCell = cells[0],
			table = tb[0],
			startCol = getColumnsIndices(cells, 1),
			lastCol = getColumnsIndices(cells),
			colIndex = insertBefore ? startCol : lastCol;

        var map = buildTableMap(tb),
			cloneCol = [],
			nextCol = [],
			height = map.length;

        for (var i = 0; i < height; i++) {
            cloneCol.push(map[i][colIndex]);
            var nextCell = insertBefore ? map[i][colIndex - 1] : map[i][colIndex + 1];
            nextCell && nextCol.push(nextCell);
        }

        for (i = 0; i < height; i++) {
            var cell;
            // Check whether there's a spanning column here, do not break it.
            if (cloneCol[i].colSpan > 1
				&& nextCol.length
				&& nextCol[i] == cloneCol[i]) {
                cell = cloneCol[i];
                cell.colSpan += 1;
            }
            else {
                cell = $("<td>&nbsp;</td>")[0];//
                cell.rowSpan = cloneCol[i].rowSpan;
                cell.removeAttribute('colSpan');
                $(cell).addClass($(cloneCol[i]).attr("class"));
               
                clearSpecialClass($(cell),options);
                !$.browser.msie;
                //cell[insertBefore ? 'insertBefore' : 'insertAfter'].call(cell, cloneCol[i]);

                insertBefore ?
                $(cloneCol[i]).before($(cell)) :
                $(cloneCol[i]).after($(cell));
                //cell = cell.$;
            }

            i += cell.rowSpan - 1;
        }
        if (options.tableChangedEvent != null) {
            options.tableChangedEvent("col");
        }
        //设置菜单
        setContextmenu($("#" + options.id), options);
    }
    
    function clearSpecialClass(cell,options){
    	/// <summary>
        /// 删除列
        /// </summary>
    	
    	if(cell.hasClass(options.imgformatclass)){
        	cell.removeClass(options.imgformatclass);
        }
        if(cell.hasClass(options.tdselectclass)){
        	cell.removeClass(options.tdselectclass);
        }
    	
    }

    function deleteColumns(t, options) {
        /// <summary>
        /// 删除列
        /// </summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>

        var tb = $("#" + options.id);
        var tableMaxColCount = getTableMaxColCount(tb);
        if(tableMaxColCount == 1){
        	alert(options.tablehandle_dontdelonlycol);
        	return;
        }
        
        //判断当前列中是否有字段
        
        var hasField = false;

        var cells = $(t),
				firstCell = cells[0],
				lastCell = cells[cells.length - 1],
				table = tb[0],
				map = buildTableMap(tb),
				startColIndex,
				endColIndex,
				rowsToDelete = [];

        // Figure out selected cells' column indices.
        for (var i = 0, rows = map.length; i < rows; i++) {
            for (var j = 0, cols = map[i].length; j < cols; j++) {
                if (map[i][j] == firstCell)
                    startColIndex = j;
                if (map[i][j] == lastCell)
                    endColIndex = j;
            }
        }

        //判断是否有数据
        for (i = startColIndex; i <= endColIndex; i++) {
            for (j = 0; j < map.length; j++) {
                var mapRow = map[j],
					row = table.rows[j],
					cell = mapRow[i];

                if (cell) {
                	if(!cellIsEmpty(cell)){
                		hasField = true;
                	}
                }
            }
        }
        
        if(hasField){
        	alert(options.tablehandle_delcolfielderror);
    		return false;
        }
        // Delete cell or reduce cell spans by checking through the table map.
     
        for (i = startColIndex; i <= endColIndex; i++) {
            for (j = 0; j < map.length; j++) {
                var mapRow = map[j],
					row = table.rows[j],
					cell = mapRow[i];

                if (cell) {
                	
                    if (cell.colSpan == 1) {
                        $(cell).remove();
                    }
                        // Reduce the col spans.
                    else
                        cell.colSpan -= 1;

                    j += cell.rowSpan - 1;

                    if (!row.cells.length) {
                        rowsToDelete.push(row);
                    }
                }
            }
        }

        var firstRowCells = table.rows[0] && table.rows[0].cells;

        // Where to put the cursor after columns been deleted?
        // 1. Into next cell of the first row if any;
        // 2. Into previous cell of the first row if any;
        // 3. Into table's parent element;
        var cursorPosition = firstRowCells[startColIndex] || (startColIndex ? firstRowCells[startColIndex - 1] : table.parentElement);

        // Delete table rows only if all columns are gone (do not remove empty row).
        //if (rowsToDelete.length == rows)
        //    table.remove();
        if (options.tableChangedEvent != null) {
            options.tableChangedEvent("col");
        }
        //设置菜单
        setContextmenu($("#" + options.id), options);
        
        return cursorPosition;
    }
    
    function cellIsEmpty(cell){
    	
    	var isEmpty = true;
    	if($.trim($(cell).html())!=""&& $.trim($(cell).html()).toLowerCase()!="&nbsp;"){
    		isEmpty = false;
    	}
    	
    	return isEmpty;
    	
    }

    function mergeCells(t,options, mergeDirection, isDetect) {
        /// <summary>
        /// 删除列
        /// </summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
        /// <param name="mergeDirection" type="object">合并方向</param>
        /// <param name="isDetect" type="object">是否检测到</param>

        var tb = $("#" + options.id);
        var cells = $(t);

        // Invalid merge request if:
        // 1. In batch mode despite that less than two selected.
        // 2. In solo mode while not exactly only one selected.
        // 3. Cells distributed in different table groups (e.g. from both thead and tbody).
        var commonAncestor;
        if ((mergeDirection ? cells.length != 1 : cells.length < 2)
				|| (commonAncestor = cells.parent()[0])
				//&& commonAncestor.type == CKEDITOR.NODE_ELEMENT
				&& commonAncestor.tagName.toLowerCase()=="table") {
            return false;
        }

        var cell,
			firstCell = cells[0],
			table = tb[0],
			map = buildTableMap(tb),
			mapHeight = map.length,
			mapWidth = map[0].length,
			startRow = firstCell.parentElement.rowIndex,
			startColumn = cellInRow(map, startRow, firstCell);

        if (mergeDirection) {
            var targetCell;
            try {
                var rowspan = parseInt(firstCell.getAttribute('rowspan'), 10) || 1;
                var colspan = parseInt(firstCell.getAttribute('colspan'), 10) || 1;

                targetCell =
					map[mergeDirection == 'up' ?
							(startRow - rowspan) :
							mergeDirection == 'down' ? (startRow + rowspan) : startRow][
						mergeDirection == 'left' ?
							(startColumn - colspan) :
						mergeDirection == 'right' ? (startColumn + colspan) : startColumn];

            }
            catch (er) {
                return false;
            }

            // 1. No cell could be merged.
            // 2. Same cell actually.
            if (!targetCell || firstCell == targetCell)
                return false;

            // Sort in map order regardless of the DOM sequence.
            cells[(mergeDirection == 'up' || mergeDirection == 'left') ?
			         'unshift' : 'push'](targetCell);
        }

        // Start from here are merging way ignorance (merge up/right, batch merge).
        var doc = firstCell.document,
			lastRowIndex = startRow,
			totalRowSpan = 0,
			totalColSpan = 0,
			// Use a documentFragment as buffer when appending cell contents.
			frag = !isDetect && doc,
			dimension = 0;
        
        //判断单元格中是否有字段
        var hasField = false;
        for (var i = 0; i < cells.length; i++) {
        	
            cell = cells[i];
            if(!cellIsEmpty(cell)){
            	hasField = true;
            }
        }
        
        if(hasField){
        	alert(options.tablehandle_mergcolfielderror);
        	return false;
        }

        for (var i = 0; i < cells.length; i++) {
            cell = cells[i];

            var tr = cell.parentElement,
				cellFirstChild = $(cell).parent().find("td:first")[0],
				colSpan = cell.colSpan,
				rowSpan = cell.rowSpan,
				rowIndex = tr.rowIndex,
				colIndex = cellInRow(map, rowIndex, cell);

            // Accumulated the actual places taken by all selected cells.
            dimension += colSpan * rowSpan;
            // Accumulated the maximum virtual spans from column and row.
            totalColSpan = Math.max(totalColSpan, colIndex - startColumn + colSpan);
            totalRowSpan = Math.max(totalRowSpan, rowIndex - startRow + rowSpan);

            if (!isDetect) {
                //$(cell).html("&nbsp;");
                // Trim all cell fillers and check to remove empty cells.
                if (cell, $(cell).children().length) {
                    // Merge vertically cells as two separated paragraphs.
                    //if (rowIndex != lastRowIndex
					//	&& cellFirstChild
					//	) {
                    //    var last = frag.getLast(CKEDITOR.dom.walker.whitespaces(true));
                    //    if (last && !(last.is && last.is('br')))
                    //        frag.append('br');
                    //}

                    //cell.moveChildren(frag);
                    //moveChildren(cell, frag);
                }
                i ? $(cell).remove() : $(cell).html("&nbsp;");
            }
            lastRowIndex = rowIndex;
        }

        if (!isDetect) {
            //moveChildren(frag, firstCell, null);
            //moveChildren(frag, firstCell,null);

            //if (!$.browser.msie)
            //    firstCell.appendBogus();

            if (totalColSpan >= mapWidth)
                firstCell.removeAttribute('rowSpan');
            else
                firstCell.rowSpan = totalRowSpan;

            if (totalRowSpan >= mapHeight)
                firstCell.removeAttribute('colSpan');
            else
                firstCell.colSpan = totalColSpan;

            // Swip empty <tr> left at the end of table due to the merging.
            //var trs = new CKEDITOR.dom.nodeList(table.$.rows),
            //	count = trs.count();
            count = table.rows.length;

            for (i = count - 1; i >= 0; i--) {
                var tailTr = table.rows[i];
                if (!tailTr.cells.length) {
                    $(tailTr).remove();
                    count++;
                    continue;
                }
            }
            if (options.tableChangedEvent != null) {
                options.tableChangedEvent("cell");
            }
            //设置菜单
            setContextmenu($("#" + options.id), options);
            return firstCell;
        }
            // Be able to merge cells only if actual dimension of selected
            // cells equals to the caculated rectangle.
        else {
            if (options.tableChangedEvent != null) {
                options.tableChangedEvent("cell");
            }
            //设置菜单
            setContextmenu($("#" + options.id), options);
            return (totalRowSpan * totalColSpan) == dimension;
        }
    }

    function splitRow(t, options) {
        /// <summary>拆分行</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
        var td = $(t);
        var tr = td.parent();
        var tb = $("#" + options.id);
        if (tr == null) {
            return;
        }
        if (tr.find("td").length == 1) {
            //只有1列的情况下直接在当前行下插入行
            var tempTr = tr.clone();
            tr.after(tempTr);
            //设置菜单
            setContextmenu($("#" + options.id), options);
            return;
        }
        //标识当前单元格
        td.attr("issplitRow", "1");

        //当前行的最大rowspan数
        var tempMaxSpan = 0;
        //当前行的rowspan数
        var rowspanCount = 0;
        if (td.attr("rowspan") != null && parseInt(td.attr("rowspan"),10)>1) {

            rowspanCount = parseInt(td.attr("rowspan"), 10) - 1;
          
            //找到当前行
            tempMaxSpan = getTrMaxRowSpan(tr);
            
            var nextTr = null;
            if (tr.nextAll("tr").length >= rowspanCount) {
                //当前行下面应该有当前行最大rowspan的增加的行数，找到当前td的rowspan-1的行就是当前需要添加td的行
                nextTr = tr.nextAll("tr").eq(rowspanCount - 1);
            }
            if (nextTr != null) {
                nextTr.append("<td>&nbsp;</td>");
            }
        }
        else {
            //在当前行下面插入一行 
            tr.after("<tr><td>&nbsp;</td></tr>");

            //查看其他td是否有conspan
            rowspanCount++;
        }

        tempMaxSpan = getTrMaxRowSpan(tr);
        //找到当前行所有其他的td，设置rowspan
        //如果当前行没有rowspan就设置它周围的td的rowspan属性，如果有，就不用设置周围兄弟td的属性
        if (td.attr("rowspan") == null || parseInt(td.attr("rowspan"),10)==1) {
            tr.find("td").each(function (i, n) {

                if ($(this).attr("issplitRow") == null) {
                    tempMaxSpan = getTrMaxRowSpan(tr);

                    var curRowSpanCount = 1;
                    if ($(this).attr("rowspan") != null) {
                        //得到当前td的rowspan
                        var curTdrowSpan = parseInt($(this).attr("rowspan"), 10);
                        //如果当前选中的tdrowspan大于现在循环中的td的rowspan，就加1否则什么都不做
                        curRowSpanCount = curTdrowSpan + 1;
                    }
                    else {
                        curRowSpanCount++;
                    }

                    if (curRowSpanCount > 1) {
                        $(this).attr("rowspan", curRowSpanCount);
                    }
                    else {
                        if ($(this).attr("rowspan") != null) {
                            $(this).removeAttr("rowspan");
                        }
                    }
                }

            });
        }
        if (rowspanCount >= 2) {
            td.attr("rowspan", rowspanCount);
        }
        else {
            if (td.attr("rowspan") != null) {
                try
                {
                    td.removeAttr("rowspan");
                }
                catch(e){
                    td.attr("rowspan", 1);
                }
            }
        }
        td.removeAttr("issplitRow");
        //设置菜单
        
        if (options.tableChangedEvent != null) {
            options.tableChangedEvent("splitrow");
        }

        //设置菜单
        setContextmenu($("#" + options.id), options);
        //console.info("splitrow body="+$("#" + options.id).html());
    }


    function splitCol(t, options) {
        /// <summary>拆分列</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
        var td = $(t);
        var tb = $("#" + options.id);
        if (tb == null) {
            return;
        }
        //找到当前行
        var tr = $(t).parent();
        var newtd = $("<td>&nbsp;</td>");
        newtd.addClass(td.attr("class"));
        clearSpecialClass(newtd,options);
        //如果当前表格只有一行，就直接在后面添加td
        if (tb.find("tr").length == 1) {
        	
        	
        	
            td.after(newtd);
            //设置菜单
            setContextmenu(tb, options);
            return;
        }
        //标识当前单元格
        td.attr("issplitCol", "1");
       
        //增加一个td

        td.after(newtd);

        if (td.attr("colspan") != null && parseInt(td.attr("colspan"),10)>1) {
            var cellSpan = parseInt(td.attr("colspan"), 10);
           
            if (cellSpan > 2) {
                cellSpan = cellSpan - 1;

                td.attr("colspan", cellSpan);
            }
            else {
                if (td.attr("colspan") != null) {
                    try{

                        td.removeAttr("colspan");
                    }
                    catch (e) {
                        //解决ie7的兼容性问题
                        td.attr("colspan","1");
                    }
                }
            }

        }

        //当前单元格索引
        var cellIndex = 0;
        //当前行单元格个数
        var cellCount = 0;
        tr.find("td").each(function (i, n) {


            if ($(this).attr("colspan") != null && parseInt($(this).attr("colspan"), 10) > 1) {
                cellCount = cellCount + parseInt($(this).attr("colspan"), 10);
            }
            else {

                cellCount++;
            }
            if ($(this).attr("issplitCol") != null && $(this).attr("issplitCol") == "1") {
                cellIndex = cellCount;
            }

        });
     
        td.removeAttr("issplitCol");
        tr.attr("cur", "1");
        //遍历所有行
        tb.find("tr").each(function (p, k) {

            if ($(this).attr("cur") == null && $(this).attr("cur") != "1") {
                //列数相同
                if ($(this).find("td").length == cellCount) {
                    return;
                }
                var colIndex = 0;
                var preColIndex = 0;
                //找到当前行的colspan数
                var curColSpanCount = 0;

                $(this).find("td").each(function () {

                    if ($(this).attr("colspan") != null && parseInt($(this).attr("colspan"), 10) > 1) {
                        curColSpanCount = curColSpanCount + parseInt($(this).attr("colspan"), 10);
                    }
                    else {
                        curColSpanCount++;
                    }
                });

                if (curColSpanCount < cellCount) {
                    //遍历所有td
                    $(this).find("td").each(function (o, p) {

                        var colspan = 1;
                        if ($(this).attr("colspan") != null && parseInt($(this).attr("colspan"), 10) > 1) {
                            colIndex = colIndex + parseInt($(this).attr("colspan"), 10);
                            colspan = parseInt($(this).attr("colspan"), 10);
                        }
                        else {
                            colIndex++;
                        }


                        if (cellIndex > preColIndex && cellIndex  <= colIndex) {

                            $(this).attr("colspan", colspan + 1);

                        }
                        preColIndex++;

                    });
                }
            }
        });
        //补漏程序
        //得到当前最大列数
        recoverTableColSpan(tb);
        tr.removeAttr("cur");
        //设置菜单
        if (options.tableChangedEvent != null) {
            options.tableChangedEvent("splitcol");
        }

        //设置菜单
        setContextmenu($("#" + options.id), options);
         
    }
    
    function tableUndo(t,options){
    	 /// <summary>拆分列</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="object">默认选项</param>
    	
    	if(options.undocontent!=null&&options.undocontent!=""){
    		 var tb = $("#" + options.id);
    		 tb.html(options.undocontent);
    		options.undocontent = "";
    		 //设置菜单
            if (options.tableChangedEvent != null) {
                options.tableChangedEvent("undo");
            }

            //设置菜单
            setContextmenu($("#" + options.id), options);
    	}
    }
    

    function recoverTableColSpan(table) {
        /// <summary>修补表格中缺少的td</summary>

        var maxColCount = 0;
        if (table == null) {
            return;
        }

        maxColCount = getTableMaxColCount(table);

        if (maxColCount == 0) {
            return;
        }

       // console.info("maxcol=" + maxColCount);
        table.find("tr").each(function () {
            var realColCount = getRowColCount($(this));
            if (maxColCount > realColCount) {
                var td = $(this).find("td:last");


                var maxrealdistant = maxColCount - realColCount + 1;

                if (td.attr("colspan") != null) {
                    var newColSpan = parseInt(td.attr("colspan"), 10) + maxrealdistant;
                    td.attr("colspan", newColSpan);
                }
                else {
                    td.attr("colspan", maxrealdistant);
                }

            }
        });
    }
    
    function getRowColCount(tr) {
        /// <summary>获得表格行中的列数</summary>
        var colCount = 0;

        if (tr == null) {
            return colCount;
        }

        tr.find("td").each(function (i, n) {

            if ($(this).attr("colspan") != null) {
                colCount = colCount + parseInt($(this).attr("colspan"), 10);
            }
            else {
                colCount++;
            }
        });
        return colCount;
    }

    function getTableMaxColCount(table) {
        /// <summary>获得表格行中的列数</summary>
        var colCount = 0;
        if (table == null) {
            return colCount;
        }

        table.find("tr").each(function () {
            var trcount = getRowColCount($(this));

            if (trcount > colCount) {
                colCount = trcount;
            }

        });
        return colCount;

    }

    function getCellColIndex(cell, isStart) {
        var row = cell.parentElement,
			rowCells = row.cells;

        var colIndex = 0;
        for (var i = 0; i < rowCells.length; i++) {
            var mapCell = rowCells[i];
            colIndex += isStart ? 1 : mapCell.colSpan;
            if (mapCell == cell)
                break;
        }

        return colIndex - 1;
    }

    function getColumnsIndices(cells, isStart) {
        var retval = isStart ? Infinity : 0;
        for (var i = 0; i < cells.length; i++) {
            var colIndex = getCellColIndex(cells[i], isStart);
            if (isStart ? colIndex < retval : colIndex > retval)
                retval = colIndex;
        }
        return retval;
    }


    function cellInRow(tableMap, rowIndex, cell) {
        var oRow = tableMap[rowIndex];
        if (typeof cell == 'undefined')
            return oRow;

        for (var c = 0 ; oRow && c < oRow.length ; c++) {
            if ( oRow[c] == cell)
                return c;
            else if (c == cell)
                return oRow[c];
        }
        return cell ? -1 : null;
    }

    function cellInCol(tableMap, colIndex) {
        var oCol = [];
        for (var r = 0; r < tableMap.length; r++) {
            var row = tableMap[r];
            oCol.push(row[colIndex]);

            // Avoid adding duplicate cells.
            if (row[colIndex].rowSpan > 1)
                r += row[colIndex].rowSpan - 1;
        }
        return oCol;
    }

    function setImgFormat(t, options) {
        /// <summary>格式化图片区域</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="String">默认选项</param>
        var cell = $(t);

        if (options.imgformatclass != "") {
            //只能有一个单元格为图片格式
            $("#" + options.id).find("." + options.imgformatclass).removeClass(options.imgformatclass);

            cell.addClass(options.imgformatclass);
            if (options.setImgEvent != null) {
                options.setImgEvent(cell, options);
            }
        }
    }
    function removeImgFormat(t, options) {
        /// <summary>移除图片格式</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="String">默认选项</param>
        var cell = $(t);
        if (options.imgformatclass != "") {

            if (cell.hasClass(options.imgformatclass)) {
                cell.removeClass(options.imgformatclass);

                if (options.setImgEvent != null) {
                    options.setImgEvent(null, options);
                }
            }
        }
    }

    function recoverTable(t, options) {
        /// <summary>将表格恢复至原来格式</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="String">默认选项</param>
        var tb = $("#" + options.id);
        if (options.tablerecover != "") {
            tb.html(options.tablerecover);
            if (options.tableRecoverEvent != null) {
                options.tableRecoverEvent();
            }
        }

        //初始化表格
        tableInit(options);

    }

    function removeField(t, options) {
        /// <summary>清楚单元格字段</summary>
        /// <param name="t" type="object">当前单元格</param>
        /// <param name="options" type="String">默认选项</param>

        var cell = $(t);

      

        if (options.removeFieldEvent != null) {

            options.removeFieldEvent(cell);

        }
        cell.empty();
        cell.html("&nbsp;");
        //设置菜单
        setContextmenu($("#" + options.id), options);
    }

    function setContextmenu(table, options) {
        /// <summary>设置表格的右键菜单</summary>
        /// <param name="table" type="String">table jquery object </param>
        /// <param name="options" type="String">default options</param>

        if (table == null) {
            return;
        }
        if (options.cellUpdateEvent != null) {
            options.cellUpdateEvent();
        }

        $(".contextMenu").find("span").addClass("ke-inline-block");
        $(".contextMenu").find("span").addClass("ke-toolbar-icon-url");


        table.find("td").contextMenu(options.menuid, {
        	showContainer: options.tableMenuContainer,
        	contextMenuShow:options.contextMenuShowEvent,
            bindings: {
                'tablehandle_addrow_up': function (t) { 
                	//options.undocontent = table.html();
                	insertRow(t, options,true); 
                	},
                'tablehandle_addrow_down': function (t) { 
                	//options.undocontent = table.html();
                	insertRow(t, options,false); 
                	},
                'tablehandle_addcol_left': function (t) { 
                	//options.undocontent = table.html();
                	insertColumn(t, options,true); 
                	},
                'tablehandle_addcol_right': function (t) { 
                	//options.undocontent = table.html();
                	insertColumn(t, options, false); 
                	},
                'tablehandle_delrow': function (t) { 
                	//options.undocontent = table.html();
                	deleteRows(t, options); 
                	},
                'tablehandle_delcol': function (t) { 
                	//options.undocontent = table.html();
                	deleteColumns(t, options); 
                	},
               // 'tablehandle_mergcell': function (t) { mergCell(t, options); },
                'tablehandle_mergdown': function (t) {
                	//options.undocontent = table.html();
                    if (mergeCells(t, options, "down", true)) {
                        mergeCells(t, options, "down", false);
                        
                        
                    } 
                    else{
                    	alert(options.tablehandle_mergeerror);
                    }
                },
                'tablehandle_mergright': function (t) {
                	//options.undocontent = table.html();
                    if (mergeCells(t, options, "right", true)) {
                        mergeCells(t, options, "right", false);
                    }
                    else{
                    	alert(options.tablehandle_mergeerror);
                    }
                },
                'tablehandle_splitrow': function (t) {
                	//options.undocontent = table.html();
                	splitRow(t,options);
                },
                'tablehandle_splitcol': function (t) {
                	//options.undocontent = table.html();
                	splitCol(t,options);
                },
               // 'tablehandle_delcell': function (t) { delCells(t, options); },
                'tablehandle_iconimage': function (t) {
                	//options.undocontent = table.html();
                	setImgFormat(t, options); 
                	},
                'tablehandle_iconimageremoveformat': function (t) { 
                	//options.undocontent = table.html();
                	removeImgFormat(t, options); 
                	},
                'tablehandle_recover': function (t) { 
                	options.undocontent = table.html();
                	recoverTable(t, options); 
                	},
                'tablehandle_removefield': function (t) {
                	//options.undocontent = table.html();
                	removeField(t, options);
                	}
                	,
                'tablehandle_undo': function (t) {
                    	tableUndo(t, options);
                    }
            }

        });

    }
    

    function initContextmenu(options) {
        /// <summary>初始化菜单</summary>
        /// <param name="options" type="String">默认选项</param>

        var context = new Array();

        context.push("<div class=\"contextMenu\" id=\"" + options.menuid + "\">");
        context.push("<ul>");
        
//        if (options.tablehandle_undo != null && options.tablehandle_undo != "") {
//            context.push("<li id=\"tablehandle_undo\"><span class=\"ke-icon-undo\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_undo + "</li>");
//        }
        if (options.tablehandle_addrow_up != null && options.tablehandle_addrow_up != "") {
            context.push("<li id=\"tablehandle_addrow_up\"><span class=\"ke-icon-tablerowinsertabove\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_addrow_up + "</li>");
        }
        if (options.tablehandle_addrow_down != null && options.tablehandle_addrow_down != "") {
            context.push("<li id=\"tablehandle_addrow_down\"><span class=\"ke-icon-tablerowinsertbelow\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_addrow_down + "</li> ");
        }
        if (options.tablehandle_addcol_left != null && options.tablehandle_addcol_left != "") {
            context.push("<li id=\"tablehandle_addcol_left\"><span class=\"ke-icon-tablecolinsertleft\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_addcol_left + "</li> ");
        }
        if (options.tablehandle_addcol_right != null && options.tablehandle_addcol_right != "") {
            context.push("<li id=\"tablehandle_addcol_right\"><span class=\"ke-icon-tablecolinsertright\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_addcol_right + "</li> ");
        }
        if (options.tablehandle_delrow != null && options.tablehandle_delrow != "") {
            context.push("<li id=\"tablehandle_delrow\"><span class=\"ke-icon-tablerowdelete\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_delrow + "</li> ");
        }
        if (options.tablehandle_delcol != null && options.tablehandle_delcol != "") {
            context.push("<li id=\"tablehandle_delcol\"><span class=\"ke-icon-tablecoldelete\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_delcol + "</li> ");
        }
        //if (options.tablehandle_mergcell != null && options.tablehandle_mergcell != "" && options.canselect == true) {
        //    context.push("<li id=\"tablehandle_mergcell\"><span class=\"ke-icon-tablecolmerge\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_mergcell + "</li> ");
        //}
        if (options.tablehandle_mergdown != null && options.tablehandle_mergdown != "") {
            context.push("<li id=\"tablehandle_mergdown\"><span class=\"ke-icon-tablecolmerge\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_mergdown + "</li> ");
        }
        if (options.tablehandle_mergright != null && options.tablehandle_mergright != "") {
            context.push("<li id=\"tablehandle_mergright\"><span class=\"ke-icon-tablecolmerge\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_mergright + "</li> ");
        }
//        if (options.tablehandle_splitrow != null && options.tablehandle_splitrow != "") {
//            context.push("<li id=\"tablehandle_splitrow\"><span class=\"ke-icon-tablerowsplit\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_splitrow + "</li> ");
//        }
        if (options.tablehandle_splitcol != null && options.tablehandle_splitcol != "") {
            context.push("<li id=\"tablehandle_splitcol\"><span class=\"ke-icon-tablecolsplit\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_splitcol + "</li> ");
        }
        //if (options.tablehandle_delcell != null && options.tablehandle_delcell != "") {
        //    context.push("<li id=\"tablehandle_delcell\"><span class=\"ke-icon-tabledelete\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_delcell + "</li> ");
        //}
        if (options.tablehandle_formatimg != null && options.tablehandle_formatimg != "") {
            context.push("<li id=\"tablehandle_iconimage\"><span class=\"ke-icon-image\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_formatimg + "</li> ");
        }
        if (options.tablehandle_removeimgformat != null && options.tablehandle_removeimgformat != "") {
            context.push("<li id=\"tablehandle_iconimageremoveformat\"><span class=\"ke-icon-removeformat\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_removeimgformat + "</li> ");
        }
        if (options.tablehandle_recover != null && options.tablehandle_recover != "") {
            context.push("<li id=\"tablehandle_recover\"><span class=\"ke-icon-source\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_recover + "</li> ");
        }
        if (options.tablehandle_removefield != null && options.tablehandle_removefield != "") {
            context.push("<li id=\"tablehandle_removefield\"><span class=\"ke-icon-removefield\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + options.tablehandle_removefield + "</li> ");
        }
        context.push("</ul>");
        context.push("</div>");

        $("body").append(context.join(''));

    }

})(jQuery);