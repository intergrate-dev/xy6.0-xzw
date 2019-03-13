/*!
 * lhgcore Calendar Plugin v3.0.0
 * Date : 2012-03-13 10:35:11
 * Copyright (c) 2009 - 2012 By Li Hui Gang
 */

;(function( $, window, undefined ){

var document = window.document, _box,
	addzero = /\b(\w)\b/g,
	_ie = !!window.ActiveXObject,
	_ie6 = _ie && !window.XMLHttpRequest,
	_$window = $(window),
	expando = 'JCA' + (new Date).getTime(),
	
iframeTpl = _ie6 ? '<iframe id="lhgcal_frm" hideFocus="true" ' + 
	'frameborder="0" src="about:blank" style="position:absolute;' +
	'z-index:-1;width:100%;top:0px;left:0px;filter:' +
	'progid:DXImageTransform.Microsoft.Alpha(opacity=0)"><\/iframe>' : '',

calendarTpl =
'<table class="lcui_border" border="0" cellspacing="0" cellpadding="0">' +
	'<thead class="lcui_head">' +
		'<tr>' +
			'<th><a class="cui_pm" href="javascript:void(0);"></a></th>' +
			'<th colspan="5"><input class="cui_im" maxlength="4" value=""/>月<input class="cui_iy" maxlength="4" value=""/>年</th>' +
			'<th><a class="cui_nm" href="javascript:void(0);"></a></th>' +
		'</tr>' +
		'<tr>' +
			'<th class="dow">\u65E5</th>' +
			'<th class="dow">\u4E00</th>' +
			'<th class="dow">\u4E8C</th>' +
			'<th class="dow">\u4E09</th>' +
			'<th class="dow">\u56DB</th>' +
			'<th class="dow">\u4E94</th>' +
			'<th class="dow">\u516D</th>' +
		'</tr>' +
	'</thead>' +
	'<tbody class="lcui_body">' +
	'</tbody>' +
	'<tfoot class="cui_foot">' +
		'<tr>' +
			'<td colspan="2" align="center" class="lcui_today"><a class="cui_tbtn" href="javascript:void(0);">\u4ECA\u5929</a></td>' +
			'<td colspan="3" align="center" class="lcui_time"><input class="cui_hour" maxlength="2"/>:<input class="cui_minute" maxlength="2"/>:<input class="cui_second" maxlength="2"/></td>' +
			'<td colspan="2" align="center" class="lcui_empty"><a class="cui_dbtn" href="javascript:void(0);">\u786e\u8ba4</a></td>' +
		'</tr>' +
	'</tfoot>' +
'</table>' +
'<div class="cui_ymlist" style="display:none;">' +
	'<table width="100%" cellspacing="1" cellpadding="0" border="0">' +
		'<thead class="cui_ybar"><tr>' +
			'<td><a class="cui_pybar" href="javascript:void(0);">«</a></td>' +
			'<td><a class="cui_cybar" href="javascript:void(0);">\xd7</a></td>' +
			'<td><a class="cui_nybar" href="javascript:void(0);">»</a></td>' +
		'</tr></thead>' +
		'<tbody class="cui_lbox">' +
		
		'</tbody>' +
	'</table>' +
'</div>' + iframeTpl;


function isDigit(ev)
{
	var iCode = ( ev.keyCode || ev.charCode );

	return (
			( iCode >= 48 && iCode <= 57 )		// Numbers
			|| (iCode >= 37 && iCode <= 40)		// Arrows
			|| iCode == 8						// Backspace
			|| iCode == 46						// Delete
	);
};

function dateFormat( format )
{
	var that = this,
	
	o = {
		'M+': that.getMonth() + 1,
		'd+': that.getDate(),
		'h+': that.getHours()%12 == 0 ? 12 : that.getHours()%12,
		'H+': that.getHours(),
		'm+': that.getMinutes(),
		's+': that.getSeconds(),
		'q+': Math.floor((that.getMonth() + 3) / 3),
		'w': '0123456'.indexOf(that.getDay()),
		'S': that.getMilliseconds()
	};
	
	if( /(y+)/.test(format) )
		format = format.replace(RegExp.$1, (that.getFullYear() + '').substr(4 - RegExp.$1.length));
	
	for( var k in o )
	{
		if( new RegExp('(' + k + ')').test(format) )
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
	}
	
	return format;
};

function getDate( string, format )
{
	var regexp, tmpnow = new Date(),
	
	/** year : /yyyy/ */
	y4 = '([0-9]{4})',
	/** year : /yy/ */
	y2 = '([0-9]{2})',
	/** index year */
	yi = -1,
	
	/** month : /MM/ */
	M2 = '(0[1-9]|1[0-2])',
	/** month : /M/ */
	M1 = '([1-9]|1[0-2])',
	/** index month */
	Mi = -1,
	
	/** day : /dd/ */
	d2 = '(0[1-9]|[1-2][0-9]|30|31)',
	/** day : /d/ */
	d1 = '([1-9]|[1-2][0-9]|30|31)',
	/** index day */
	di = -1,
	
	/** hour : /HH/ */
	H2 = '([0-1][0-9]|20|21|22|23)',
	/** hour : /H/ */
	H1 = '([0-9]|1[0-9]|20|21|22|23)',
	/** index hour */
	Hi = -1,
	
	/** minute : /mm/ */
	m2 = '([0-5][0-9])',
	/** minute : /m/ */
	m1 = '([0-9]|[1-5][0-9])',
	/** index minute */
	mi = -1,
	
	/** second : /ss/ */
	s2 = '([0-5][0-9])',
	/** second : /s/ */
	s1 = '([0-9]|[1-5][0-9])',
	/** index month */
	si = -1;
	
	if( validDate(string,format) )
	{
		var val = regexp.exec( string ), reDate,
			index = getIndex( format ),
			year = index[0] >= 0 ? val[ index[0] + 1 ] : tmpnow.getFullYear(),
			month = index[1] >= 0 ? ( val[index[1]+1] - 1 ) : tmpnow.getMonth(),
			day = index[2] >= 0 ? val[ index[2] + 1 ] : tmpnow.getDate(),
			hour = index[3] >= 0 ? val[ index[3] + 1 ] : tmpnow.getHours(),
			minute = index[4] >= 0 ? val[ index[4] + 1 ] : tmpnow.getMinutes(),
			second = index[5] >= 0 ? val[ index[5] + 1 ] : tmpnow.getSeconds(),

		reDate = new Date( year, month, day, hour, minute, second );
		
		if( reDate.getDate() == day )
			return reDate;
		else
			return tmpnow;
	}
	else
		return tmpnow;
	
	function validDate( string, format )
	{
		
		sting = $.trim( string );
		if( string === '' ) return;
		
		format = format.replace(/yyyy/,y4).replace(/yy/,y2).replace(/MM/,M2)
				 .replace(/M/,M1).replace(/dd/,d2).replace(/d/,d1).replace(/HH/,H2)
				 .replace(/H/,H1).replace(/mm/,m2).replace(/m/,m1).replace(/ss/,s2)
				 .replace(/s/,s1);
		
		format = new RegExp( '^' + format + '$' );
		regexp = format;
		
		return format.test( string );
	};
	
	function getIndex( format )
	{
		var ia = [], i = 0, ia2;
		
		yi = format.indexOf('yyyy');
		if( yi < 0 ) yi = format.indexOf('yy');
		if( yi >= 0 )
		{
			ia[i] = yi;
			i++;
		}
		
		Mi = format.indexOf('MM');
		if( Mi < 0 ) Mi = format.indexOf('M');
		if( Mi >= 0 )
		{
			ia[i] = Mi;
			i++;
		}
		
		di = format.indexOf('dd');
		if( di < 0 ) di = format.indexOf('d');
		if( di >= 0 )
		{
			ia[i] = di;
			i++;
		}
		
		Hi = format.indexOf('HH');
		if( Hi < 0 ) Hi = format.indexOf('H');
		if( Hi >= 0 )
		{
			ia[i] = Hi;
			i++;
		}
		
		mi = format.indexOf('mm');
		if( mi < 0 ) mi = format.indexOf('m');
		if( mi >= 0 )
		{
			ia[i] = mi;
			i++;
		}
		
		si = format.indexOf('ss');
		if( si < 0 ) si = format.indexOf('s');
		if( si >= 0 )
		{
			ia[i] = si;
			i++;
		}
		
		ia2 = [ yi, Mi, di, Hi, mi, si ];
		
		for( i = 0; i < ia.length - 1; i++ )
		{
			for( j = 0; j < ia.length - 1 - i; j++ )
			{
				if( ia[j] > ia[j+1] )
				{
					var temp = ia[j];
					ia[j] = ia[j+1];
					ia[j+1] = temp;
				}
			}
		}
		
		for( i = 0; i < ia.length; i++ )
		{
			for( j = 0; j < ia2.length; j++ )
			{
				if( ia[i] == ia2[j] )
					ia2[j] = i;
			}
		}
		
		return ia2;
	};
};

function convertDate( date, format, day )
{
	var tmpnow = new Date();
	
	if( /%/.test(date) )
	{
		day = day || 0;
		date = date.replace( /%y/, tmpnow.getFullYear() ).replace( /%M/, tmpnow.getMonth() + 1 ).replace( /%d/, tmpnow.getDate() + day )
			.replace( /%H/, tmpnow.getHours() ).replace( /%m/, tmpnow.getMinutes() ).replace( /%s/, tmpnow.getSeconds() ).replace( addzero, '0$1' );
	}
	else if( /^#[\w-]+$/.test(date) )
	{
		date = $.trim( $(date)[0].value );

		if( date.length > 0 && format )
			date = dateFormat.call( getDate(date,format), 'yyyy-MM-dd' );
	}
	
	return date;
};

/*!--------------------------------------------------------------*/

var lhgcalendar = function( config , elm)
	{
		config = config || {};
		
		var setting = lhgcalendar.setting;
		
		for( var i in setting )
		{
			if( config[i] === undefined ) config[i] = setting[i];
		}
		
		return _box ? _box._init(config , elm) : new lhgcalendar.fn._init(config , elm);
	};

lhgcalendar.fn = lhgcalendar.prototype =
{
	constructor: lhgcalendar,
	
	_init: function( config ,elm)
	{
		var that = this, DOM,
			// evt = that._getEvent(),
			inpVal, date;
		
		that.config = config;
		
		that.DOM = DOM = that.DOM || that._getDOM();
		
		that.evObj = elm ? elm : (config.id ? $(config.id)[0] : $("<input type='hidden' id=inpt_"+expando+"/>").appendTo(document.body)[0]);
		that.inpE = config.id ? $(config.id)[0] : that.evObj;
		if( !config.btnBar )
			DOM.foot[0].style.display = 'none';
		else
			DOM.foot[0].style.display = '';

        lhgcalendar.setting.minDateTmp = null;
        lhgcalendar.setting.maxDateTmp = null;
		if( config.minDate ){
            lhgcalendar.setting.minDateTmp = config.minDate;
            lhgcalendar.setting.maxDateTmp = null;
            //设置当前开始时间的前一天（之前选择开始时间之后，开始时间的当天是不能选择的，所以往前推一天）
            var preDate = new Date((new Date(config.minDate)).getTime() - 24*60*60*1000);
            config.minDate = dateFormat.call(preDate, 'yyyy-MM-dd HH:mm:ss');
            // console.log(config.minDate)
            config.minDate = convertDate( config.minDate, config.targetFormat, config.noToday ? 1 : 0 );
			// console.log(config.minDate)
		}

		if( config.maxDate ){
            lhgcalendar.setting.maxDateTmp = config.maxDate;
            lhgcalendar.setting.minDateTmp = null;
			config.maxDate = convertDate( config.maxDate, config.targetFormat, config.noToday ? -1 : 0 );
        }

		inpVal = $.trim( that.inpE.value );
		
		if( inpVal.length > 0 )
			date = getDate( inpVal, config.format );
		else
			date = new Date();
		//不获取当前时间 qianxm
		/*DOM.hour[0].value = (date.getHours() + '').replace(addzero,'0$1');
		DOM.minute[0].value = (date.getMinutes() + '').replace(addzero,'0$1');
		DOM.second[0].value = (date.getSeconds() + '').replace(addzero,'0$1');*/
        DOM.hour[0].value = '00';
        DOM.minute[0].value = '00';
        DOM.second[0].value = '00';

		DOM.pm.addClass("icon-arrow-left");
		DOM.nm.addClass("icon-arrow-right");
		// 已经用年月的快速导航面板了，感觉输入功能作用不大，暂时先去掉
		$('input',DOM.head[0]).attr("readonly",true);
		// 原来时间输入框是在没有配置时间字段时不允许输入，现在是同时把他隐藏起来了
		// $('input',DOM.foot[0]).attr({ disabled:config.format.indexOf('H')>=0?false:true });
		if(config.format.indexOf('H')>=0){
			$('input',DOM.foot[0]).attr("disabled",false);
			DOM.time.show();
			DOM.today.attr("colspan",2);
			DOM.empty.attr("colspan",2);
		}else{
			$('input',DOM.foot[0]).attr("disabled",true);
			DOM.time.hide();
			DOM.today.attr("colspan",4);
			DOM.empty.attr("colspan",3);
		}

		that._draw(date).show()._offset(/*that.evObj*/);
		
		_ie6 && $('#lhgcal_frm').css({height:DOM.wrap[0].offsetHeight+'px'});
		
		if( !_box )
		{
			// DOM.wrap[0].style.width = DOM.wrap[0].offsetWidth + 'px';
			that._addEvent(); _box = that;
		}
		
		return that;
	},
	
	_draw: function( date, day )
	{
		var that = this,
			DOM = that.DOM,
			firstDay,
			befMonth,
			curMonth,
			arrDate = [],
			inpYear,
			inpMonth,
			opt = that.config,
			frag, row, cell, n = 0, curDateStr;
		
		that.year = inpYear = date.getFullYear();
		that.month = inpMonth = date.getMonth() + 1;
		that.day = day || date.getDate();
		
		DOM.iy[0].value = inpYear;
		DOM.im[0].value = inpMonth;
		
		firstDay = new Date( inpYear, inpMonth - 1, 1 ).getDay();
		befMonth = new Date( inpYear, inpMonth - 1, 0 ).getDate();
		curMonth = new Date( inpYear, inpMonth, 0 ).getDate();
		
		for( var i = 0; i < firstDay; i++ )
		{
			arrDate.push( befMonth );
			befMonth--;
		}
		
		arrDate.reverse();
		for( var i = 1; i <= curMonth; i++ )
			arrDate.push(i);
		
		for( var i = 1; i <= 42 - curMonth - firstDay; i++ )
			arrDate.push(i);
		
		frag = document.createDocumentFragment();
		
		for( var i = 0; i < 6; i++ )
		{
			row = document.createElement('tr');
			for( var j = 0; j < 7; j++ )
			{
				cell = document.createElement('td');
				curDateStr = (inpYear + '-' + inpMonth + '-' + arrDate[n]).replace(addzero,'0$1');
				m = "c";
				if(n < firstDay){
					m = "p";
				}else if(n >= curMonth + firstDay){
					m = "n";
				}
				
				if(/* n < firstDay || n >= curMonth + firstDay ||*/
					opt.minDate && opt.minDate > curDateStr ||
					opt.maxDate && opt.maxDate < curDateStr ||
					opt.disWeek && opt.disWeek.indexOf(j) >= 0 )
				{
					that._setCell( cell, arrDate[n] );
				}
				else if( opt.disDate )
				{
					for( var dis = 0, l = opt.disDate.length; dis < l; dis++ )
					{
						if( /%/.test(opt.disDate[dis]) )
							opt.disDate[dis] = convertDate( opt.disDate[dis] );
							
						var regex = new RegExp(opt.disDate[dis]),
							tmpre = opt.enDate ? !regex.test(curDateStr) : regex.test(curDateStr);
						
						if( tmpre ) break;
					}
						
					if( tmpre )
						that._setCell( cell, arrDate[n] );
					else{
						that._setCell( cell, arrDate[n], m, true );						
					}
				}
				else{
					that._setCell( cell, arrDate[n], m, true );					
				}
				
				row.appendChild( cell ); n++;
			}
			frag.appendChild( row );
		}
		
		while( DOM.body[0].firstChild )
			DOM.body[0].removeChild( DOM.body[0].firstChild );
			
		DOM.body[0].appendChild(frag);
		
		return that;
	},
	
	_setCell: function( cell, num, month, enabled )
	{
		if( enabled )
		{
			cell.innerHTML = num;
			cell[expando+'D'] = num;
			if(month =="c"){
				cell.className = "day";
				if( num === this.day ){
					$(cell).addClass('cui_today');
				}
			}else if(month == "p"){
				cell.className = "pDay";
			}else if(month == "n"){
				cell.className = "nDay";
			}
		}
		else
			cell.innerHTML = num + '';
	},
	
	_drawList: function( val, arr )
	{
		var DOM = this.DOM, row, cell,
			frag = document.createDocumentFragment();
			
		for( var i = 0; i < 4; i++ )
		{
			row = document.createElement('tr');
			for( var j = 0; j < 3; j++ )
			{
				cell = document.createElement('td');
				cell.innerHTML = '<a href="javascript:void(0);">' + (arr?arr[val]:val) + '</a>';
				row.appendChild(cell);
				
				if( arr )
					cell.firstChild[expando+'M'] = val;
				else
					cell.firstChild[expando+'Y'] = val;
					
				val++;
			}
			frag.appendChild(row);
		}
		
		while( DOM.lbox[0].firstChild )
			DOM.lbox[0].removeChild( DOM.lbox[0].firstChild );
		
		DOM.lbox[0].appendChild(frag);
		
		return this;
	},
	
	_showList: function()
	{
		this.DOM.ymlist[0].style.display = 'block';
	},
	
	_hideList: function()
	{
		this.DOM.ymlist[0].style.display = 'none';
	},
	
	_offset: function()
	{
		var that = this, DOM = that.DOM, ltop,
			inpP = $(that.evObj).offset(),
			inpY = inpP.top + that.evObj.offsetHeight,
			ww = _$window.width(),
			wh = _$window.height(),
			dl = _$window.scrollLeft(),
			dt = _$window.scrollTop(),
			cw = DOM.wrap[0].offsetWidth,
			ch = DOM.wrap[0].offsetHeight;
		if( inpY + ch > wh + dt )
			inpY = inpP.top - ch - 2;
			
		if( inpP.left + cw > ww + dl )
			inpP.left -= cw;
		
		DOM.wrap.css({ left:inpP.left + 'px', top:inpY + 'px' });
		
		ltop = DOM.im.offset().top + DOM.im[0].offsetHeight;
		DOM.ymlist[0].style.top = ltop - inpY + 'px';
		
		return that;
	},
	
	_getDOM: function()
	{
		var wrap = document.createElement('div');
		
		wrap.className = "cui_well";
		wrap.style.cssText = 'position:absolute;display:none;z-index:' + this.config.zIndex + ';';
		wrap.innerHTML = calendarTpl;
		
		var name, i = 0,
			DOM = { wrap: $(wrap) },
			els = wrap.getElementsByTagName('*'),
			len = els.length;
		
		for( ; i < len; i ++ )
		{
			name = els[i].className.split('cui_')[1];
			if(name) DOM[name] = $(els[i]);
		};
		
		document.body.appendChild(wrap);
		
		return DOM;
	},
	
	_getEvent: function()
	{
		if( _ie ) return window.event;
		
		var func = this._getEvent.caller;
	
		while( func != null )
		{
			var arg = func.arguments[0];
			if( arg && (arg + '').indexOf('Event') >= 0 ) return arg;
			func = func.caller;
		}
		
		return null;
	},
	
	_setDate: function( day , show)
	{
		day = parseInt( day, 10 );
		
		var that = this, opt = that.config, DOM = that.DOM,
			tmpDate = new Date( that.year, that.month-1, day );
		
		if( opt.format.indexOf('H') >= 0 )
		{
			var hourVal = parseInt( DOM.hour[0].value, 10 ),
				minuteVal = parseInt( DOM.minute[0].value, 10 ),
				secondVal = parseInt( DOM.second[0].value, 10 );
			
			tmpDate = new Date(that.year,that.month-1,day,hourVal,minuteVal,secondVal);
			//判断所选时间和开始时间或者结束时间的关系
			if(lhgcalendar.setting.minDateTmp){//如果有开始时间
				//开始时间大于结束时间
				if(new Date(lhgcalendar.setting.minDateTmp) > tmpDate){
					alert("结束时间小于开始时间，请重新输入");
					return ;
				}
			}else if(lhgcalendar.setting.maxDateTmp){//如果有结束时间
                //开始时间大于结束时间
                if(tmpDate > new Date(lhgcalendar.setting.maxDateTmp)){
                    alert("结束时间小于开始时间，请重新输入");
                    return ;
                }
			}
		}
		
		that.day = day;
		
		opt.onSetDate && opt.onSetDate.call( that );
		that.inpE.value = dateFormat.call( tmpDate, opt.format );
		
		if( opt.real )
		{
			var realFormat = opt.format.indexOf('H') >= 0 ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd';
			$(opt.real)[0].value = dateFormat.call(tmpDate,realFormat);
		}
		
		opt.afterSetDate && opt.afterSetDate.call( that );

		if(!show&&opt.autoHide)
			that.hide();
	},
	
	_addEvent: function()
	{
		var that = this,
			DOM = that.DOM;
		DOM.wrap.bind('click',function(evt){
			var target = evt.target;
			if( target[expando+'D'] ){
				if(~target.className.indexOf("pDay")){
					that._draw( new Date(that.year, that.month - 2), target[expando+'D'] );
					//that._setDate( target[expando+'D'] , true);
				}else if(~target.className.indexOf("nDay")){
					that._draw( new Date(that.year, that.month), target[expando+'D'] );
                    //that._setDate( target[expando+'D'] , true);
				}else{
					//新增点击当月的绘制事件 qianxm
                    that._draw( new Date(that.year, that.month-1), target[expando+'D'] );
					//that._setDate( target[expando+'D']);
				}
			}
			else if( target === DOM.pm[0] )
				that._draw( new Date(that.year, that.month - 2), that.day );
			else if( target === DOM.nm[0] )
				that._draw( new Date(that.year, that.month), that.day );
			/* 去掉了年的翻页按钮，所以得先把这两句注销掉，不然报错
			else if( target === DOM.py[0] )
				that._draw( new Date(that.year - 1, that.month - 1), that.day );
			else if( target === DOM.ny[0] )
				that._draw( new Date(that.year + 1, that.month - 1), that.day );
			*/
			else if( target === DOM.tbtn[0] )//点今天
			{
				var today = new Date();
				that.year = today.getFullYear();
				that.month = today.getMonth() + 1;
				that.day = today.getDate();
				that._setDate( that.day );
			}
			//注释代码为清空按钮，改为了确认 qianxm
			else if( target === DOM.dbtn[0] )//点确定
			{
				/*var config = that.config;
				
				if( config.onSetDate )
				{
					that.year = '';
					that.month = '';
					that.day = '';
					config.onSetDate.call( that );
				}
				
				that.inpE.value = '';
				if(config.autoHide)that.hide();
				
				if( config.real )
					$(config.real)[0].value = '';*/
                that._setDate( that.day );
			}
			else if( target === DOM.im[0] )
			{
				var marr = ['01','02','03','04','05','06','07','08','09','10','11','12'],
					x = DOM.im.offset().left - DOM.wrap.offset().left;
				// 取消了输入框的输入功能，所以这个也先注释掉
				// DOM.im[0].select();
				DOM.ybar[0].style.display = 'none';
				DOM.ymlist[0].style.left = x + 'px';
				that._drawList(0, marr)._showList();
				return false;
			}
			else if( target === DOM.iy[0] )
			{
				var x = DOM.iy.offset().left - DOM.wrap.offset().left;
				
				// 取消了输入框的输入功能，所以这个也先注释掉
				// DOM.iy[0].select();
				DOM.ybar[0].style.display = '';
				DOM.ymlist[0].style.left = x + 'px';
				that._drawList(that.year - 4)._showList();
				return false;
			}
			/* 每次点击日历牌的空白或不能选择的地方，会按照input框中的日期更新日历牌，
			感觉效率有些低，决定去掉这个更新的功能和input框的输入功能
			else
			{
				var today = new Date(),
					m = DOM.im[0].value || today.getMonth() + 1,
					y = DOM.iy[0].value || today.getFullYear();
				that._draw( new Date(y,m-1), that.day );
			}
			*/
			
			that._hideList();
			
			return false;
		});
		
		DOM.ymlist.bind('click',function(evt){
			var target = evt.target;
			if( target[expando+'M'] >= 0 )
			{
				DOM.im[0].value = target[expando+'M'] + 1;
				that._draw( new Date(that.year, target[expando+'M']), that.day )._hideList();
			}
			else if( target[expando+'Y'] )
			{
				DOM.iy[0].value = target[expando+'Y'];
				that._draw( new Date(target[expando+'Y'], that.month-1), that.day )._hideList();
			}
			else if( target === DOM.pybar[0] )
			{
				var p = $('a',DOM.lbox[0])[0][expando+'Y'];
				that._drawList( p - 12 );
			}
			else if( target === DOM.nybar[0] )
			{
				var p = $('a',DOM.lbox[0])[0][expando+'Y'];
				that._drawList( p + 12 );
			}
			else if( target === DOM.cybar[0] )
				that._hideList();
			return false;
		});
		/* 感觉日期的输入框方法有些鸡肋，决定去掉它
		DOM.im.bind('keypress',isDigit);
		DOM.iy.bind('keypress',isDigit);
		*/
		DOM.hour.bind('keypress',isDigit);
		DOM.minute.bind('keypress',isDigit);
		DOM.second.bind('keypress',isDigit);
		
	},
	
	show: function()
	{
		var that = this;
		that.DOM.wrap[0].style.display = 'block';
		if(that.config.autoHide){
			$(document).on('click.lhgcalendar',function(evt){
				if( evt.target !== that.evObj )
					that.hide()._hideList();
			});
			_$window.on('resize.lhgcalendar',function(evt){
				that._offset();
			});
		}
		return this;
	},
	
	hide: function()
	{
		this.DOM.wrap[0].style.display = 'none';
		$(document).off('click.lhgcalendar');
		_$window.off('resize.lhgcalendar');
		return this;
	},
	
	getDate: function( type )
	{
		var that = this, DOM = that.DOM,
			h = parseInt( DOM.hour[0].value, 10 ),
			m = parseInt( DOM.minute[0].value, 10 ),
			s = parseInt( DOM.second[0].value, 10 );
		
		if( that.year === '' && that.month === '' && that.day === '' )
			return '';
			
		switch( type )
		{
			case 'y': return that.year;
			case 'M': return that.month;
			case 'd': return that.day;
			case 'H': return h;
			case 'm': return m;
			case 's': return s;
			case 'date': return ( that.year + '-' + that.month + '-' + that.day );
			case 'dateTime': return ( that.year + '-' + that.month + '-' + that.day + ' ' + h + ':' + m + ':' + s );
		};
	}
};

lhgcalendar.fn._init.prototype = lhgcalendar.fn;

lhgcalendar.formatDate = function( date, format )
{
	return dateFormat.call( date, format );
};

lhgcalendar.setting =
{
	id: null,
	format: 'yyyy-MM-dd HH:mm:ss',
	minDate: null,
	maxDate: null,
	minDateTmp: null,
	maxDateTmp: null,
	btnBar: true,
	targetFormat: null,
	disWeek: null,
	onSetDate: null,
	afterSetDate: null,
	real: null,
	disDate: null,
	enDate: false,
	zIndex: 1978,
	noToday: false,
	linkageObj: null,
	autoHide:true
};

$.fn.calendar = function( config, event )
{
	event = event || 'click';
	
	this.bind(event, function(){
		lhgcalendar( config , this);
		return false;
	});
	
	return this;
};

window.lhgcalendar = $.calendar = lhgcalendar;

})( this.jQuery||this.lhgcore, this );

// 替换之前的usecalendar.js中的showCalendar函数，
// 之前调用日历组件的函数接口都是showCalendar函数，不太好替换，只好重写了
////调用的时候在参数上加了'y-mm-dd'，不知道是来干嘛的，所以增加了format的形参，保证其他两个参数的函数也能运行
function showCalendar(field,format,curStartTime,curEndTime){
	//$.calendar({id:"#"+field}).show(); ,format:'yyyy-MM-dd HH:mm:ss'
	if(curStartTime){
        lhgcalendar({id:"#"+field, minDate: curStartTime}).show();
	}else if(curEndTime){
        lhgcalendar({id:"#"+field, maxDate: curEndTime}).show();
	}else{
        lhgcalendar({id:"#"+field}).show();
	}

}