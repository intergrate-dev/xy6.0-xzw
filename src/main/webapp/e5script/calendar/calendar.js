/* Author: Mihai Bazon, September 2002
 * http://students.infoiasi.ro/~mishoo
 *
 * Version: 0.8
 *
 * Feel free to use this script under the terms of the GNU General Public
 * License, as long as you do not remove or alter this notice.
 */

var _MN = new Array(
"一月"		,//"January"
"二月"		,//"February"
"三月"		,//"March"
"四月"		,//"April"
"五月"		,//"May"
"六月"		,//"June"
"七月"		,//"July"
"八月"		,//"August"
"九月"		,//"September"
"十月"		,//"October"
"十一月"	,//"November"
"十二月"	 //"December"
);
var _DN = new Array(
"日"	,	//"Sunday"
"一"	,	//"Monday"
"二"	,	//"Tuesday"
"三"	,	//"Wednesday"
"四"	,	//"Thursday"
"五"	,	//"Friday"
"六"	,	//"Saturday"
"日"		//"Sunday"
);
var _MD = new Array(31,28,31,30,31,30,31,31,30,31,30,31);

var agt = navigator.userAgent.toLowerCase();
var is_ie = ((agt.indexOf("msie") != -1) && (agt.indexOf("opera") == -1));

var _C = null;

function isRelated(el, evt) {
  var related = evt.relatedTarget;
  if (!related) {
    var type = evt.type;
    if (type == "mouseover") related = evt.fromElement;
    else if (type == "mouseout") related = evt.toElement;
  }
  while (related) {
    if (related == el) return true;
    related = related.parentNode;
  }
  return false;
}

function getMonthDays(year, month) {
  if (((0 == (year%4)) && ( (0 != (year%100)) || (0 == (year%400)))) && month == 1) return 29;
  else return _MD[month];
}

function removeClass(el, className) {
  if (!(el && el.className)) return;
  var classes = el.className.split(" ");
  var newClasses = new Array;
  for (i = 0; i < classes.length; ++i)
    if (classes[i] != className) newClasses[newClasses.length] = classes[i];
  el.className = newClasses.join(" ");
}

function addClass(el, className) {
  el.className += " " + className;
}

function getElement(ev) {
  if (is_ie) return window.event.srcElement;
  else return ev.currentTarget;
}

function getTargetElement(ev) {
  if (is_ie) return window.event.srcElement;
  else return ev.target;
}

function stopEvent(ev) {
  if (is_ie) {
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  } else {
    ev.preventDefault();
    ev.stopPropagation();
  }
}

function addEvent(el, evname, func) {
  if (is_ie) el.attachEvent("on" + evname, func);
  else el.addEventListener(evname, func, true);
}

function removeEvent(el, evname, func) {
  if (is_ie) el.detachEvent("on" + evname, func);
  else el.removeEventListener(evname, func, true);
}

function findMonth(el) {
  if (typeof el.month != 'undefined') return el;
  else if (typeof el.parentNode.month != 'undefined') return el.parentNode;
  return null;
}

function findYear(el) {
  if (typeof el.year != 'undefined') return el;
  else if (typeof el.parentNode.year != 'undefined') return el.parentNode;
  return null;
}

function tableMouseUp(ev) {
  if (!_C) return;
  var cal = _C;
  if (cal.timeout) clearTimeout(cal.timeout);
  var el = cal.activeDateEl;
  if (!el) return;
  var target = getTargetElement(ev);
  removeClass(el, "active");
  if (target == el || target.parentNode == el) cellClick(el);
  var mon = findMonth(target);
  if (mon) {
    var date = new Date(cal.date);
    if (mon.month != date.getMonth()) {
      date.setMonth(mon.month);
      cal.setDate(date);
    }
  } else {
    var year = findYear(target);
    if (year) {
      var date = new Date(cal.date);
      if (year.year != date.getFullYear()) {
        date.setFullYear(year.year);
        cal.setDate(date);
      }
    }
  }
  removeEvent(document, "mouseup", tableMouseUp);
  removeEvent(document, "mouseover", tableMouseOver);
  removeEvent(document, "mousemove", tableMouseOver);
  el = null;
  _C.hideCombos();
  _C = null;
  stopEvent(ev);
}

function tableMouseOver(ev) {
  if (!_C) return;
  var cal = _C;
  var el = cal.activeDateEl;
  var target = getTargetElement(ev);
  if (target == el || target.parentNode == el) addClass(el, "hilite active");
  else {
    removeClass(el, "active");
    removeClass(el, "hilite");
  }
  var mon = findMonth(target);
  if (mon) {
    var date = new Date(cal.date);
    if (mon.month != date.getMonth()) {
      if (cal.activeMonEl)
        removeClass(cal.activeMonEl, "hilite");
      addClass(mon, "hilite");
      cal.activeMonEl = mon;
    }
  } else {
    var year = findYear(target);
    if (year) {
      var date = new Date(cal.date);
      if (year.year != date.getFullYear()) {
        if (cal.activeYearEl)
          removeClass(cal.activeYearEl, "hilite");
        addClass(year, "hilite");
        cal.activeYearEl = year;
      }
    }
  }
  stopEvent(ev);
}

function tableMouseDown(ev) { if (getTargetElement(ev) == getElement(ev)) stopEvent(ev); }

function showMonthsCombo() {
  if (!_C) return;
  var cal = _C;
  var cd = cal.activeDateEl;
  var mc = cal.monthsCombo;
  if (cal.activeMonEl)
    removeClass(cal.activeMonEl, "hilite");
  var mon = cal.monthsCombo.getElementsByTagName("div")[cal.date.getMonth()];
  addClass(mon, "hilite");
  cal.activeMonEl = mon;
  mc.style.left = cd.offsetLeft;
  mc.style.top = cd.offsetTop + cd.offsetHeight;
  mc.style.display = "block";
}

function showYearsCombo(fwd) {
  if (!_C) return;
  var cal = _C;
  var cd = cal.activeDateEl;
  var yc = cal.yearsCombo;
  if (cal.activeYearEl)
    removeClass(cal.activeYearEl, "hilite");
  cal.activeYearEl = null;
  var Y = cal.date.getFullYear() + (fwd ? 1 : -1);
  var yr = yc.firstChild;
  var show = false;
  for (var i = 0; i < 12; ++i) {
    if (Y >= cal.minYear && Y <= cal.maxYear) {
      yr.firstChild.data = Y;
      yr.year = Y;
      yr.style.display = "block";
      show = true;
    } else yr.style.display = "none";
    yr = yr.nextSibling;
    Y += fwd ? 2 : -2;
  }
  if (show) {
    yc.style.left = cd.offsetLeft;
    yc.style.top = cd.offsetTop + cd.offsetHeight;
    yc.style.display = "block";
  }
}

function calDragIt(ev) {
  if (!_C.dragging) return;
  var posX;
  var posY;
  if (is_ie) {
    posY = window.event.clientY + document.body.scrollTop;
    posX = window.event.clientX + document.body.scrollLeft;
  } else {
    posX = ev.pageX;
    posY = ev.pageY;
  }
  var st = _C.element.style;
  st.left = (posX - _C.xOffs) + "px";
  st.top = (posY - _C.yOffs) + "px";
  stopEvent(ev);
}

function calDragEnd(ev) {
  _C.dragging = false;
  removeEvent(document, "mousemove", calDragIt);
  removeEvent(document, "mouseover", stopEvent);
  removeEvent(document, "mouseup", calDragEnd);
  tableMouseUp(ev);
}

function dayMouseDown(ev) {
  var el = getElement(ev);
  var cal = el.calendar;
  cal.activeDateEl = el;
  _C = cal;
  if (el.navtype == 300)
    cal.dragStart(ev);
  else {
    addClass(el, "hilite active");
    addEvent(document, "mouseover", tableMouseOver);
    addEvent(document, "mousemove", tableMouseOver);
    addEvent(document, "mouseup", tableMouseUp);
  }
  stopEvent(ev);
  if (el.navtype == -1 || el.navtype == 1) {
    cal.timeout = setTimeout("showMonthsCombo()", 250);
  } else if (el.navtype == -2 || el.navtype == 2) {
    cal.timeout = setTimeout((el.navtype > 0) ? "showYearsCombo(true)" : "showYearsCombo(false)", 250);
  } else cal.timeout = null;
}

function dayMouseDblClick(ev) { cellClick(getElement(ev)); if (is_ie) { document.selection.empty(); } }

function dayMouseOver(ev) {
  var el = getElement(ev);
  if (isRelated(el, ev) || _C) return;
  if (el.ttip) el.calendar.tooltips.firstChild.data = el.ttip;
  if (el.navtype != 300) addClass(el, "hilite");
  stopEvent(ev);
}

function dayMouseOut(ev) {
  var el = getElement(ev);
  if (isRelated(el, ev) || _C) return;
  removeClass(el, "hilite");
  el.calendar.tooltips.firstChild.data = "选择日期";	//Select date
  stopEvent(ev);
}

function datesEqual(a, b) {
  return a.getFullYear() == b.getFullYear() && a.getMonth() == b.getMonth() && a.getDate() == b.getDate();
}

function cellClick(el) {
  var cal = el.calendar;
  var closing = false;
  var newdate = false;
  if (typeof el.navtype == 'undefined') {
    removeClass(cal.currentDateEl, "selected");
    addClass(el, "selected");
    closing = cal.currentDateEl == el;
    if (!closing) cal.currentDateEl = el;
    var date = el.caldate;
    cal.date = date;
    newdate = true;
  } else {
    if (el.navtype == 200) {
      cal.callCloseHandler();
      return;
    }
    var date = (el.navtype == 0) ? new Date() : new Date(cal.date);
    var year = date.getFullYear();
    var mon = date.getMonth();
    var setMonth = function (mon) {
      var day = date.getDate();
      var max = getMonthDays(year, mon);
      if (day > max) date.setDate(max);
      date.setMonth(mon);
    }
    switch (el.navtype) {
    case -2:
      if (year > cal.minYear) date.setFullYear(year - 1);
      break;
    case -1:
      if (mon > 0) setMonth(mon - 1);
      else if (year-- > cal.minYear) {
        date.setFullYear(year);
        setMonth(11);
      }
      break;
    case 1:
      if (mon < 11) setMonth(mon + 1);
      else if (year < cal.maxYear) {
        date.setFullYear(year + 1);
        setMonth(0);
      }
      break;
    case 2:
      if (year < cal.maxYear) date.setFullYear(year + 1);
      break;
    case 100:
      cal.setMondayFirst(!cal.mondayFirst);
      return;
    }
    if (!datesEqual(date, cal.date)) {
      cal.setDate(date);
      newdate = el.navtype == 0;
    }
  }
  if (newdate) cal.callHandler();
  if (closing) cal.callCloseHandler();
}

function Calendar(mondayFirst, dateStr, yourHandler, yourCloseHandler) {
  var parent = document.getElementsByTagName("body")[0];
  this.init = function (mondayFirst, date) {
    var today = new Date();
    var year = date.getFullYear();
    if (year < this.minYear) year = this.minYear;
    else if (year > this.maxYear) year = this.maxYear;
    date.setFullYear(year);
    this.mondayFirst = mondayFirst;
    this.date = new Date(date);
    var month = date.getMonth();
    var mday = date.getDate();
    var no_days = getMonthDays(year, month);
    date.setDate(1);
    var wday = date.getDay();
    var MON = mondayFirst ? 1 : 0;
    var SUN = mondayFirst ? 6 : 0;
    var SAT = mondayFirst ? 5 : 6;
    if (mondayFirst) wday = (wday > 0) ? (wday - 1) : 6;
    var iday = 1;
    var row = this.table.getElementsByTagName("tbody")[0].firstChild;
    for (var i = 0; i < 6; ++i, row = row.nextSibling) {
      var cell = row.firstChild;
      if (iday > no_days) { row.className = "emptyrow"; continue; }
      else row.className = "daysrow";
      for (var j = 0; j < 7; ++j, cell = cell.nextSibling) {
        if ((!i && j < wday) || iday > no_days)
          cell.className = "emptycell";
        else {
          cell.firstChild.data = iday;
          cell.className = "day";
          date.setDate(iday);
          cell.caldate = new Date(date);
          //cell.ttip = _DN[wday + MON].substr(0, 3) + ", " + _MN[month].substr(0, 3) + " " + iday + ", " + year;
          cell.ttip = year + "年 " +  _MN[month].substr(0, 3) + " " + iday + "日 星期" + _DN[wday + MON].substr(0, 3);
          if (iday == mday) { addClass(cell, "selected"); this.currentDateEl = cell; }
					if (datesEqual(date, today)) {
						addClass(cell, "today");
						//cell.ttip += " (today)";
						cell.ttip += " (今天)";
					}
          if (wday == SAT || wday == SUN) addClass(cell, "weekend");
          ++wday; ++iday;
          if (wday == 7) wday = 0;
        }
      }
    }
    //this.title.firstChild.data = _MN[month] + ", " + year;
    this.title.firstChild.data =  year + "年 " +_MN[month];
  }
  this.setDate = function (date) {
    if (!datesEqual(date, this.date)) this.init(this.mondayFirst, date);
  }
  this.setMondayFirst = function (mondayFirst) {
    this.init(mondayFirst, this.date);
    this.displayWeekdays();
  }
  this.displayWeekdays = function () {
    var MON = this.mondayFirst ? 0 : 1;
    var SUN = this.mondayFirst ? 6 : 0;
    var SAT = this.mondayFirst ? 5 : 6;
    var cell = this.daynames.firstChild;
    for (var i = 0; i < 7; ++i, cell = cell.nextSibling) {
      cell.className = "day name";
      if (!i)
        if (this.mondayFirst) cell.ttip = "显示星期日为周的开始";	//Display Sunday first
        else cell.ttip = "显示星期一为周的开始";		//Display Monday first
      if (i == SUN || i == SAT) addClass(cell, "weekend");
      cell.firstChild.data = _DN[i + 1 - MON].substr(0,2);
    }
  }
  this.setRange = function (a, z) {
    this.minYear = a;
    this.maxYear = z;
  }
  this.callHandler = function () {
    if (this.yourHandler) this.yourHandler(this, this.formatDate());
  }
  this.callCloseHandler = function () {
    if (this.yourCloseHandler) this.yourCloseHandler(this);
  }
  this.destroy = function () {
    var el = this.element.parentNode;
    el.removeChild(this.element);
  }
  this.show = function () { this.element.style.visibility = "visible"; }
  this.hide = function () { this.element.style.visibility = "hidden"; }
  this.showAt = function (x, y) {
    var s = this.element.style;
    s.left = x + "px";
    s.top = y + "px";
    this.show();
  }
  this.showAtElement = function (el) {
    function Pos (x, y) {
      this.x = x;
      this.y = y;
    }
    function getPos(el) {
      var r = new Pos(el.offsetLeft, el.offsetTop);
      if (el.offsetParent) {
        var tmp = getPos(el.offsetParent);
        r.x += tmp.x;
        r.y += tmp.y;
      }
      return r;
    }
    var p = getPos(el);
    this.showAt(p.x, p.y + el.offsetHeight);
  }
  this.hideCombos = function () {
    this.monthsCombo.style.display = "none";
    this.yearsCombo.style.display = "none";
  }
  this.dragStart = function (ev) {
    if (this.dragging) return;
    this.dragging = true;
    var posX;
    var posY;
    if (is_ie) {
      posY = window.event.clientY + document.body.scrollTop;
      posX = window.event.clientX + document.body.scrollLeft;
    } else {
      posY = ev.clientY + window.scrollY;
      posX = ev.clientX + window.scrollX;
    }
    var st = this.element.style;
    this.xOffs = posX - parseInt(st.left);
    this.yOffs = posY - parseInt(st.top);
    addEvent(document, "mousemove", calDragIt);
    addEvent(document, "mouseover", stopEvent);
    addEvent(document, "mouseup", calDragEnd);
  }
  this.setDateFormat = function (str) { this.dateFormat = str; }
  this.formatDate = function () {
    var str = new String(this.dateFormat);
    var m = this.date.getMonth();
    var d = this.date.getDate();
    var y = this.date.getFullYear();
    var w = this.date.getDay();
    var s = new Array;
    s['d'] = d;
    s['dd'] = (d < 10) ? ('0' + d) : d;
    s['m'] = 1+m;
    s['mm'] = (m < 9) ? ('0' + (1+m)) : (1+m);
    s['y'] = y;
    s['yy'] = new String(y).substr(2, 2);
    s['D'] = _DN[w].substr(0, 3);
    s['DD'] = _DN[w];
    s['M'] = _MN[m].substr(0, 3);
    s['MM'] = _MN[m];
    var re = /(.*)([^a-zA-Z0-9]|^)(d|dd|m|mm|y|yy|MM|M|DD|D)([^a-zA-Z0-9]|$)(.*)/;
    while (re.exec(str) != null) str = RegExp.$1 + RegExp.$2 + s[RegExp.$3] + RegExp.$4 + RegExp.$5;
    return str;
  }
  this.parseDate = function (str, fmt) {
    var y = 0;
    var m = -1;
    var d = 0;
    var a = str.split(/[^a-zA-Z0-9]+/);
    if (!fmt) fmt = this.dateFormat;
    var b = fmt.split(/[^a-zA-Z0-9]+/);
    for (var i = 0; i < a.length; ++i) {
      if (b[i] == 'D' || b[i] == 'DD') continue;
      if (b[i] == 'd' || b[i] == 'dd') d = a[i];
      if (b[i] == 'm' || b[i] == 'mm') m = a[i]-1;
      if (b[i] == 'y') y = a[i];
      if (b[i] == 'yy') y = parseInt(a[i]) + 1900;
      if (b[i] == 'M' || b[i] == 'MM')
        for (var j = 0; j < 12; ++j)
          if (_MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { m = j; break; }
    }
    if (y != 0 && m != -1 && d != 0) {
      this.setDate(new Date(y, m, d));
      return;
    }
    y = 0; m = -1; d = 0;
    for (var i = 0; i < a.length; ++i) {
      if (a[i].search(/[a-zA-Z]+/) != -1) {
        var t = -1;
        for (var j = 0; j < 12; ++j)
          if (_MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { t = j; break; }
        if (t != -1) {
          if (m != -1) d = m+1;
          m = t;
        }
      } else if (parseInt(a[i]) <= 12 && m == -1) m = a[i]-1;
      else if (parseInt(a[i]) > 31 && y == 0) y = a[i];
      else if (d == 0) d = a[i];
    }
    if (y == 0) {
      var today = new Date();
      y = today.getFullYear();
    }
    if (m != -1 && d != 0) this.setDate(new Date(y, m, d));
  }

  if (yourHandler) this.yourHandler = yourHandler;
  if (yourCloseHandler) this.yourCloseHandler = yourCloseHandler;

  this.setRange(1970, 2050);
  this.dragging = false;
  this.setDateFormat("y-mm-dd");

  var date = null;
  if (dateStr) date = new Date(dateStr);
  else date = new Date();

  var table = document.createElement("table");
  this.table = table;
  table.cellSpacing = 0;
  table.cellPadding = 0;
  table.calendar = this;
  addEvent(table, "mousedown", tableMouseDown);

  var div = document.createElement("div");
  this.element = div;
  div.className = "calendar";
  div.style.position = "absolute";
  div.style.visibility = "hidden";
  div.appendChild(table);

  var add_evs = function (el) {
    addEvent(el, "mouseover", dayMouseOver);
    addEvent(el, "mousedown", dayMouseDown);
    addEvent(el, "mouseout", dayMouseOut);
    if (is_ie) {
      addEvent(el, "dblclick", dayMouseDblClick);
      el.setAttribute("unselectable", true);
    }
  }

  // table header
  var thead = document.createElement("thead");
  table.appendChild(thead);
  var cell = null;
  var row = null;

  var cal = this;
  var hh = function (text, cs, navtype) {
    cell = document.createElement("td");
    row.appendChild(cell);
    if (cs != 1) cell.colSpan = cs;
    cell.className = "button_cal";
    add_evs(cell);
    cell.calendar = cal;
    cell.navtype = navtype;
    if (text.substr(0, 1) != '&') cell.appendChild(document.createTextNode(text));
    else cell.innerHTML = text;
    return cell;
  }

  row = document.createElement("tr");
  thead.appendChild(row);
  row.className = "headrow";

  hh("-", 1, 100).ttip = "切换星期日";	//Toggle first day of week
  this.title = hh("", 5, 300);
  this.title.className = "title";
  this.title.ttip = "按住鼠标移动";	//Drag to move
  this.title.style.cursor = "move";
  hh("X", 1, 200).ttip = "关闭";	//Close

  row = document.createElement("tr");
  thead.appendChild(row);
  row.className = "headrow";

  hh("&#x00ab;", 1, -2).ttip = "上一年 (按住从菜单选取)";	//Prev. year (hold for menu)
  hh("&#x2039;", 1, -1).ttip = "上一月 (按住从菜单选取)";	//Prev. month (hold for menu)
  //hh("Today", 3, 0).ttip		 = "Go Today";
  hh("今天", 3, 0).ttip			 = "今天";
  hh("&#x203a;", 1, 1).ttip  = "下一月 (按住从菜单选取)";	//Next month (hold for menu)
  hh("&#x00bb;", 1, 2).ttip  = "下一年 (按住从菜单选取)";	//Next year (hold for menu)

  // day names
  row = document.createElement("tr");
  thead.appendChild(row);
  row.className = "daynames";
  this.daynames = row;
  for (var i = 0; i < 7; ++i) {
    cell = document.createElement("td");
    row.appendChild(cell);
    cell.appendChild(document.createTextNode(""));
    if (!i) {
      cell.navtype = 100;
      cell.calendar = this;
      add_evs(cell);
    }
  }
  this.mondayFirst = mondayFirst;
  this.displayWeekdays();

  var tbody = document.createElement("tbody");
  table.appendChild(tbody);

  for (var i = 0; i < 6; ++i) {
    row = document.createElement("tr");
    tbody.appendChild(row);
    for (var j = 0; j < 7; ++j) {
      cell = document.createElement("td");
      row.appendChild(cell);
      cell.appendChild(document.createTextNode(""));
      cell.calendar = this;
      add_evs(cell);
    }
  }

  var tfoot = document.createElement("tfoot");
  table.appendChild(tfoot);

  row = document.createElement("tr");
  tfoot.appendChild(row);
  row.className = "footrow";

  cell = hh("选择日期", 7, 300);	//Select date
  cell.className = "ttip";
  cell.ttip = "按住鼠标移动";	//Drag to move
  cell.style.cursor = "move";
  this.tooltips = cell;

  var div = document.createElement("div");
  this.monthsCombo = div;
  div.className = "combo";
  for (var i = 0; i < _MN.length; ++i) {
    var mn = document.createElement("div");
    mn.className = "label";
    mn.month = i;
    mn.appendChild(document.createTextNode(_MN[i].substr(0, 3)));
    div.appendChild(mn);
  }
  this.element.appendChild(div);

  div = document.createElement("div");
  this.yearsCombo = div;
  div.className = "combo";
  for (var i = 0; i < 12; ++i) {
    var yr = document.createElement("div");
    yr.className = "label";
    yr.appendChild(document.createTextNode(""));
    div.appendChild(yr);
  }
  this.element.appendChild(div);

  this.init(mondayFirst, date);
  parent.appendChild(this.element);
}
