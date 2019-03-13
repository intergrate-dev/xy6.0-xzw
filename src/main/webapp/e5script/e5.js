if (!(typeof e5 !== 'undefined'&&e5.mod)) {
	var e5 = e5||undefined;
	var _e5 = (function(win,doc) {
		var e5 = {
			mods: {name:"mods"},
			listeners: {},
			// map: {},
			namespace: namespace,
			mod: mod,
			run: run,
			runAll: runAll,
			ran: {}
		},
			UNDEFINED,
			NOOP = function(){},
			ARRAY = 'array',
			BOOLEAN = 'boolean',
			DATE = 'date',
			ERROR = 'error',
			FUNCTION = 'function',
			NUMBER = 'number',
			NULL = 'null',
			OBJECT = 'object',
			REGEX = 'regexp',
			STRING = 'string',
			TOSTRING = Object.prototype.toString,
			UNDEFINED = 'undefined',
			TYPES = {
				'undefined': UNDEFINED,
				'number': NUMBER,
				'boolean': BOOLEAN,
				'string': STRING,
				'[object Function]': FUNCTION,
				'[object RegExp]': REGEX,
				'[object Array]': ARRAY,
				'[object Date]': DATE,
				'[object Error]': ERROR
			},
			TRIMREGEX = /^\s+|\s+$/g,
			EMPTYSTRING = '',
			SUBREGEX = /\{\s*([^\|\}]+?)\s*(?:\|([^\}]*))?\s*\}/g,
			Native = Array.prototype,
			LENGTH = 'length',
			_uidx = 0,
			L = {},
			A,
			listeners = e5.listeners,
			// map = e5.map,
			mods = e5.mods,
			obj = O,
			ran = e5.ran;


		// =========================== core模块 ===========================

		/**
		 * 创造命名空间.
		 *
		 * @method _creatNamespace
		 * @param name {String} 要创造命名空间的名字或路径，必须是字符串，用“.”分割多层路径.
		 * @param root {Object} 创造命名空间的基础路径.可以在全局变量环境下创建命名空间，默认为e5.
		 * @param partition {Boolean} 决定name属性是否用"."分割
		 * @private
		 * @return 无 如果name不合法返回
		 * @return {Object} 创建成功返回创建好的命名空间
		 */

		function _createNamespace(name, root, partition) {
			var i, part, parts;
			if (!(name && L.isString(name))) {
				// console.warn("e5: _createNamespace")
				return false;
			}

			partition = (L.isUndefined(partition)) ? true : partition;
			parts = partition ? name.split(".") : [name];
			root = root || e5;
			for (i = (parts[0] === "e5" && root === e5) ? 1 : 0; typeof(part = parts[i++]) !== "undefined";) {
				root[part] = root[part] || {};
				root = root[part];
			}
			return root;
		}

		/**
		 * 创造命名空间或模块的封装.
		 *
		 * @method namespace
		 * @param name {String} 要创造命名空间的名字或路径，必须是字符串，用“.”分割多层路径.
		 * @param fn {Object/Function} 创造命名空间的初始化内容，可选.
		 * @param options {Object} 配置属性数型，可选，有效参数root(创建命名空间的基础路径)，requires(依赖模块)，use(创建后自动启动的模块)，partition(是否用"."分割路径).
		 * @public
		 * @return 无 如果name不合法返回
		 * @return {Object} 创建成功返回创建好的命名空间
		 */

		function namespace(name, fn, options) {

			if(L.isObject(fn,true)){
				if(/root|requires|use|partition|autoRun/.test(obj.keys(fn).toString)){
					options = fn;
					fn = NOOP;
				}
			}

			var l = arguments.length,
				name = name && L.isString(name) ? name : "",
				fn = fn || NOOP,
				options = options || {},
				root = options["root"],
				req = options["requires"],
				use = options["use"],
				partition = options["partition"],
				autoRun = (L.isUndefined(options["autoRun"])) ? true : options["autoRun"],
				space;

			if(!(space = _createNamespace(name, root, partition))){
				// console.warn("e5: namespace")
				return false;
			}
			if(l==1){
				return space;
			}

			space.__name__ = name;
			space.__fn__ = fn;
			space.__options__ = options;

			if (req && req.length) {
				req = req.concat(execution);
				if(fn.run){
					req = req.concat(fn.run);
				}
				R.apply(this,req);
			}else{
				execution();
			}

			if (use && use.length) {
				//TODO 检查需要加载的模块
			}

			// map[name] = space;

			return space;

			function execution(){
				var i,expand;
				if(L.isFunction(fn)){
					if(autoRun){
						expand = fn.call(space,e5);
					}else{
						expand = {}
					}
				}else{
					expand = fn
				}
				for (i in expand) {
					space[i] = expand[i];
				}
			}
		}

		/**
		 * 创造dom模块的封装.
		 *
		 * @method mod
		 * @param name {String} 要创建dom模块的名字.
		 * @param fn {Function} dom模块的启动函数，必须返回含有init.
		 * @param options {Object} 配置属性数型，可选，有效参数root(创建命名空间的基础路径)，requires(依赖模块)，use(创建后自动启动的模块)，partition(是否用"."分割路径)，autoRun(是否在创建后自动执行).
		 * @public
		 * @return 无 如果name不合法返回
		 * @return {Object} 创建成功返回创建好的命名空间
		 */

		function mod(name, fn, options) {
			var domMod,
				options = options || {},
				autoRun = (L.isUndefined(options["autoRun"])) ? true : options["autoRun"];

			if (!L.isFunction(fn)) {
				// console.warn("e5: createDomModule(\"" + name + "\", \"" + fn + "\", \"" + details + "\") 函数参数fn只接受function类型且不能为空.");
				return;
			}

			options["root"] = mods;
			options["partition"] = false;

			fn.run = function (){
				if(autoRun){
					run(name);
				}
			};

			domMod = namespace(name, fn, options);

			if(!options["requires"]){
				fn.run();
			}

			return domMod;
		}

		/**
		 * 启动dom模块
		 *
		 * @method run
		 * @param modId{String} 要启动dom模块的名字，可以一次只能输入一个模块名，直接输入"*"启动所有dom模块。
		 * @public
		 * @return 无
		 */

		function run(name) {
			var domMod;
			if(name==="*"){
				return runAll();
			}
			if(!obj.hasKey(mods,name)){
				// console.warn("core: runDomModule("+modId+") 模块不存在.")
				return;
			}
			domMod = mods[name];
			if (L.isUndefined(domMod.init)) {
				// console.warn("core: runDomModule(): 模块init函数未定义.");
				return;
			}
			//var sandbox = new Sandbox(modId);
			domMod.init(new Sandbox(name));
			// if (L.isUndefined(domMod.onload)) {
				// console.warn("core: runDomModule(): 模块onload函数未定义.");
			// 	return;
			// }
			if (doc.getElementById(name)) {
				domMod.onload();
			} else {
				$(doc).ready(domMod.onload);
			}
		}

		/**
		 * 启动所有模块
		 *
		 * @method runAllModule
		 * @public
		 * @return 无
		 */

		function runAll() {
			var i;
			// console.log("core: runAllModule() 执行.");
			for( i in mods){
				run(i);
			}
		}

		/**
		 * 匹配消息和注册消息的模块
		 *
		 * @method match
		 * @param msg {String} 要匹配的消息
		 * @param callerId {String} 广播事件的模块的id
		 * @param callerData {Object} 广播事件的模块传递的数据
		 * @private
		 * @return 无
		 */

		function _match(msg, callerId, callerData) {
			// console.log("e5: match(\"" + msg + "\", \"" + callerId + "\", \"" + callerData + "\") 执行.");
			var /*modules = [],*/
				modIds,
				key, msgName,p,top,
				_listeners;
			if (msg.indexOf(":") !== -1) {
				if (callerId !== msg.split(":")[0]) {
					// console.warn("e5: match(\"" + msg + "\") 消息指定id (" + msg.split(":")[0] + ") 与模块 id (" + callerId + ") 匹配. 终止执行.");
					return;
				}
			}
			modIds = {};
			msgName = msg.split(":")[1];
			// 查找注册了该事件的模块
			p = window;
			top = window.top;
			do{
				try {
					if(!(p.e5&&p.e5.listeners)) break;
				} catch (e) {
					//若页面被整个iframe包在别的系统中，则会产生跨域冲突
					break;
				}
				_listeners = p.e5.listeners;
				traversal(_listeners);				
				if(p == top){
					break;
				}
			}while(p = p.parent)
			function traversal(listeners){
				for (var i in listeners) {
					if ((!(msg in listeners[i]))||modIds[i]) {
						continue;
					}
					key = msg;
					modIds[i] = true;
					// 防止处理函数错误
					try {
						listeners[i][key](msgName, callerId, callerData);
						if (!L.isUndefined(mods[i].onmessage)) {
							mods[i].onmessage(msgName, callerId, callerData);
						}
						// modules.push(i);
					} catch (e) {
						// console.error("e5: match() " + e.message);
					}
				}
			}
			// console.log("e5: match(\"" + msgName + "\", \"" + callerId + "\", \"" + callerData + "\") 执行完毕 " + modules.length + " 模块(s)响应: \"#" + modules.join(", #") + "\"");
		}
		/**
		 * 让模块监听信息
		 *
		 * @method addListener
		 * @param msg {String} 监听的消息名.
		 * @param moduleId {String} 监听的模块的id.
		 * @param handler {Function} 事件触发时执行的函数.
		 * @private
		 * @return {String} 回调函数的id，用于监听的remove和update.
		 */

		function _addListener(msg, moduleId, handler) {
			// console.log("e5: addListener(\"" + moduleId + "\", \"" + msg + "\") 执行.");
			var i, j, listener, /*listenerId,*/ targetId;
			handler = handler || function(){};
			// listenerId = _getUid();
			if (L.isUndefined(listeners[moduleId])) {
				listeners[moduleId] = {};
			}
			listeners[moduleId][msg] = handler;
			// map[listenerId] = listeners[moduleId][msg];
			// return listenerId;
		}


		/**
		 * 生成随机id.
		 *
		 * @method getUid
		 * @param pre {String} 生成id的前缀，只能包含数字字母和下划线.
		 * @public
		 * @return {String} 生成的id
		 */

		function _getUid(pre) {
			var id = new Date().getTime() + "_" + (++_uidx);
			pre = ((pre) ? (pre + "_" + id) : id).replace(/\W/g, '_');
			return pre;
		}

		// =========================== 沙箱模块 ===========================
		/**
		 * 沙箱构造函数.
		 *
		 * @method Sandbox
		 * @param id {String} 要装入沙箱的模块的id.
		 * @public
		 * @return 无
		 */

		function Sandbox(id) {
			this.id = id;
		};
		Sandbox.prototype = {
			/**
			 * 向核心类广播消息.
			 * 
			 * @method broadcast
			 * @param msg {String} 要广播的消息，用“:”分割，“:”前是广播消息的模块id，之后是消息名.
			 * @param data {Object} 模块要传递的数据.
			 * @public
			 * @return 无
			 */

			broadcast: function(msg, data, path) {
				// console.log("sandbox: #" + this.id + "broadcast(\"" + msg + "\") 执行.");
				var moduleId;
				if (msg.indexOf(":") !== -1) {
					moduleId = msg.split(":")[0];
					if (moduleId !== this.id) {
						// console.warn("sandbox: broadcast(\"" + msg + "\") 消息id与当前模块id不一致.");
						return false;
					}
				} else {
					msg = this.id + ":" + msg;
				}
				_match(msg, this.id, data);
			},
			/**
			 * 模块事件监听.
			 *
			 * @method listen
			 * @param msg {String} 要监听的消息.
			 * @param callback {Function} 事件发生时要执行的函数.
			 * @public
			 * @return 无
			 */
			listen: function(msg, callback, path) {
				// console.log("sandbox: #" + this.id + "listen(\"" + msg + "\") 执行.");
				/*return*/ _addListener(msg, this.id, callback);
			},
			/**
			 * 修复原型链.
			 */
			constructor: Sandbox
		};

	// =========================== obj模块 ===========================
		/**
		 * obj模块提供的是object对象的扩展方法，为老式浏览器提供EM5(ECMAscript5)方法
		 * @class obj
		 */

		/**
		 * O(o)即e5.obj(o) 会生成一个继承自o的新对象
		 * @TODO 在可以使用EM5方法时应使用Object.create()
		 * @param o 要继承的对象
		 * @static
		 * @return {Object} 新创建的对象
		 */

		function O(o) {
			var E = function() {};
			E.prototype = o;
			return new E();
		}

		/**
		 * 测试k是否是o的属性
		 * @param o {Object} 查找的对象
		 * @param k {String} 要测试的key值
		 * @static
		 * @return {Boolean} 如果k是o的属性返回true
		 */

		function owns(o, k) {
			return o && o.hasOwnProperty && o.hasOwnProperty(k);
			// return Object.prototype.hasOwnProperty.call(o, k);
		}

		/**
		 * 从一个对象中取出keys, values或者size属性
		 *
		 * @method _extract
		 * @param o {Object} 被提取的对象
		 * @param what {Number} 要提取的属性 (0: keys, 1: values, 2: size)
		 * @return {Number|Array} 提取的值
		 * @static
		 * @private
		 */

		function _extract(o, what) {
			var count = (what === 2),
				out = (count) ? 0 : [],
				i;
			for (i in o) {
				if (owns(o, i)) {
					if (count) {
						out++;
					} else {
						out.push((what) ? o[i] : i);
					}
				}
			}
			return out;
		}

		/**
		 * 提取一个对象的所有key值
		 * @TODO 如果Object.keys()存在应该使用Object.keys()
		 * @method keys
		 * @static
		 * @param o {Object} 要提取的对象
		 * @return {Array} 对象的key值
		 */

		O.keys = function(o) {
			return _extract(o);
		};

		/**
		 * 提取一个对象的所有value值
		 * @TODO 如果Object.values()存在应该使用Object.values()
		 * @method values
		 * @static
		 * @param o {Object} 要提取的对象
		 * @return {Array} 对象的value值
		 */

		O.values = function(o) {
			return _extract(o, 1);
		};

		/**
		 * 提取一个对象的size
		 * @TODO 如果Object.size()存在应该使用Object.size()
		 * @method size
		 * @static
		 * @param o {Object} 要提取的对象
		 * @return {int} 对象的size
		 */

		O.size = function(o) {
			return _extract(o, 2);
		};

		/**
		 * 检查对象中是否存在某一key值
		 * @method hasKey
		 * @static
		 * @param o {Object} 要检查的对象
		 * @param k {String} 要查询的key值
		 * @return {Boolean} 如果存在返回true
		 */

		O.hasKey = owns;

		/**
		 * 检查对象中是否存在某一value值
		 * @method hasValue
		 * @static
		 * @param o {Object} 要检查的对象
		 * @param v {String} 要查询的value值
		 * @return {boolean} 如果存在返回true
		 */

		O.hasValue = function(o, v) {
			return (A.indexOf(O.values(o), v) > -1);
		};

		/**
		 * 检查对象是某一属性是否存在，如果属性存在或者通过集成存在则返回true
		 *
		 * @method owns
		 * @static
		 * @param o {Object} 要检查的对象
		 * @param p {String} 要查找的属性键值
		 * @return {Boolean} 如果属性存在返回true
		 */

		O.owns = owns;

		/**
		 * 遍历对象的所有属性，作为参数执行一个函数. 
		 * 
		 * @method each
		 * @static
		 * @param o {Object} 要遍历的对象
		 * @param f {Function} 对每个属性执行的函数。接受三个参数，value ,key和要遍历的完整对象
		 * @param c {Object} 函数的上下文(即执行函数的对象，函数中this指向的对象)
		 * @param proto {Boolean} 是否遍历原型链上的属性
		 * @return {Object} e5
		 */

		O.each = function(o, f, c, proto) {
			var s = c || e5,
				i;
			for (i in o) {
				if (proto || owns(o, i)) {
					f.call(s, o[i], i, o);
				}
			}
			return e5;
		};

		/**
		 * 遍历对象的所有属性，作为参数执行一个函数. 如果函数返回true则停止遍历
		 * 
		 * @method some
		 * @static
		 * @param o {Object} 要遍历的对象
		 * @param f {Function} 对每个属性执行的函数。接受三个参数，value ,key和要遍历的完整对象
		 * @param c {Object} 函数的上下文(即执行函数的对象，函数中this指向的对象)
		 * @param proto {Boolean} 是否遍历原型链上的属性
		 * @return {Boolean} 如果遍历过程中f返回true则返回true，没有则返回false
		 */

		O.some = function(o, f, c, proto) {
			var s = c || e5,
				i;
			for (i in o) {
				if (proto || owns(o, i)) {
					if (f.call(s, o[i], i, o)) {
						return true;
					}
				}
			}
			return false;
		};

		/**
		 * 查找对象的指定路径上的value值.
		 *
		 * @method getValue
		 * @param o {Object} 要查找的对象
		 * @param path {Array} 路径数组, 指定查找value的路径，如果路径为空则返回当前对象.
		 * @return {Any} 查找到的value值.
		 */

		O.getValue = function(o, path) {
			if (!L.isObject(o)) {
				return UNDEFINED;
			}
			var i, p = A(path),
				l = p.length;
			for (i = 0; !L.isUndefined(o) && i < l; i++) {
				o = o[p[i]];
			}
			return o;
		};

		/**
		 * 设置对象指定路径上的属性值
		 *
		 * @method setValue
		 * @param o {Object} 要设置的对象。
		 * @param path {Array} 路径数组, 指定设置value的路径，如果路径为空则返回当前对象.
		 * @param val {Any} 要设置的值.
		 * @return {Object} 修改后的对象
		 */

		O.setValue = function(o, path, val) {
			var i, p = A(path),
				leafIdx = p.length - 1,
				ref = o;
			if (leafIdx >= 0) {
				for (i = 0; !L.isUndefined(ref) && i < leafIdx; i++) {
					ref = ref[p[i]];
				}
				if (L.isUndefined(ref)) {
					return UNDEFINED;
				} else {
					ref[p[i]] = val;
				}
			}
			return o;
		};

		/**
		 * 检查对象是否有属性值
		 * @method isEmpty
		 * @param o {Object} 要检查的对象
		 * @return {Boolean} 如果对象中没有任何属性返回true
		 */

		O.isEmpty = function(o) {
			for (var i in o) {
				if (owns(o, i)) {
					return false;
				}
			}
			return true;
		};

	// =========================== lang模块 ===========================


		/**
		 * 提供javascript的扩展功能
		 * @class lang
		 * @static
		 */

		/**
		 * 检查对象是否是数组.
		 * 类数组元素会返回false，比如function的参数列表(arguments),html节点列表.
		 * 更详细的检查可以使用e5.arr.test()方法
		 * 
		 * @method isArray
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是数组则返回true
		 */

		L.isArray = function(o) {
			return L.type(o) === ARRAY;
		};

		/**
		 * 检测是否是布尔值
		 *
		 * @method isBoolean
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是布尔值则返回true
		 */

		L.isBoolean = function(o) {
			return typeof o === BOOLEAN;
		};

		/**
		 * 检测是否是函数
		 * 备注: IE认为以下函数是对象:
		 *
		 * var obj = document.createElement("object");
		 * e5.lang.isFunction(obj.getAttribute) // return false in IE
		 *
		 * var input = document.createElement("input");
		 * e5.lang.isFunction(input.focus) // return false in IE
		 *
		 * 如果需要检测以上函数，必须进行额外的测试
		 *
		 * @method isFunction
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是函数则返回true
		 */

		L.isFunction = function(o) {
			return L.type(o) === FUNCTION;
		};

		/**
		 * 检测是否是日期类型
		 * @method isDate
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是日期类型则返回true
		 */

		L.isDate = function(o) {
			// return o instanceof Date;
			return L.type(o) === DATE && o.toString() !== 'Invalid Date' && !isNaN(o);
		};

		/**
		 * 检测是否是null类型
		 * @method isNull
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是null类型则返回true
		 */

		L.isNull = function(o) {
			return o === null;
		};

		/**
		 * 检测是否是Number类型
		 * @method isNull
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是Number类型则返回true
		 */

		L.isNumber = function(o) {
			return typeof o === NUMBER && isFinite(o);
		};

		/**
		 * 检查是否是Object类型
		 *
		 * @method isObject
		 * @static
		 * @param o {Any} 要检测的对象
		 * @param failfn {boolean} 是否忽略Function类型，如果设置为false或者不填，是Function类型也会返回true。
		 * @return {Boolean} 如果o是Object类型则返回true
		 */

		L.isObject = function(o, failfn) {
			var t = typeof o;
			return (o && (t === OBJECT || (!failfn && (t === FUNCTION || L.isFunction(o))))) || false;
		};

		/**
		 * 检查是否是String类型
		 *
		 * @method isString
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是String类型则返回true
		 */

		L.isString = function(o) {
			return typeof o === STRING;
		};

		/**
		 * 检查是否是undefined
		 *
		 * @method isUndefined
		 * @static
		 * @param o {Any} 要检测的对象
		 * @return {Boolean} 如果o是undefined则返回true
		 */

		L.isUndefined = function(o) {
			return typeof o === UNDEFINED;
		};

		/**
		 * 去掉字符串前后的空格.
		 * 
		 * @method trim
		 * @static
		 * @param s {String} 要修改的字符串
		 * @return {String} 修改后的字符串
		 */

		L.trim = function(s) {
			try {
				return s.replace(TRIMREGEX, EMPTYSTRING);
			} catch (e) {
				return s;
			}
		};

		/**
		 * 检查值是否有效.
		 * 
		 * @method isValue
		 * @static
		 * @param o {Any} 要检查的值
		 * @return {Boolean} 如果是null/undefined/NaN返回false, 其他都返回true，包括0,false和''
		 */
		L.isValue = function(o) {
			var t = L.type(o);
			switch (t) {
			case NUMBER:
				return isFinite(o);
			case NULL:
			case UNDEFINED:
				return false;
			default:
				return !!(t);
			}
		};

		/**
		 * 检测对象的数据类型.
		 * 
		 * 备注:使用javascript原生的typeof测试HTML元素集合在Safari中会返回function, 
		 * 但是用e5.lang.type()则返回object, 这是一个错误，不过貌似object更准确一些……(-_-;)
		 *
		 * @method type
		 * @param o {Any} 要检查的对象
		 * @return {String} 检测到的类型
		 */

		L.type = function(o) {
			return TYPES[typeof o] || TYPES[TOSTRING.call(o)] || (o ? OBJECT : NULL);
		};

		/**
		 * 轻量级文本替换程序，使用{}作为替换的标示符，{}内是要替换的文字在对象中的key值，可以加入注释，格式为{key|注释}
		 *
		 * @method format
		 * @param s {String} 要替换的文字
		 * @param o {Object} 存储替换文字的对象
		 * @return {String} 替换完成的文字
		 */

		L.format = function(s, o) {
			return ((s.replace) ? s.replace(SUBREGEX, function(match, key) {
				return (!L.isUndefined(o[key])) ? o[key] : match;
			}) : s);
		};

	// =========================== arr模块 ===========================

		/**
		 * arr模块提供的是array对象的扩展方法，为老式浏览器提供EM5(ECMAscript5)方法.
		 * 为查找、编辑数组提供更方便的帮助.
		 * @class arr
		 */

		/** 
		 * A(o) 将 o 转化为数组后返回，
		 * - 如果o为数组则根据startIdx返回截取后的数组，startIdx省略则返回原数组.
		 * - 类似数组集合将被转换为数组(类似数组集合是指typeof返回的不是array,但用e5.Array.test测试返回2的对象)
		 * - 其他类型将被装入一个数组后返回
		 *
		 * @method ()
		 * @static
		 * @param o {Any} 要转换的对象
		 * @param startIdx {Int} 截取数组的开始位置
		 * @param arraylike {Boolean} 如果为true，程序会牵制进入类似数组元素分支.可以用它来避免大量调用A.test.
		 * @return {Array} 转化后的数组
		 */

		A = function(o, startIdx, arraylike) {
			var t = (arraylike) ? 2 : A.test(o),
				l, a, start = startIdx || 0;
			if (t) {
				// 对HTML元素集合使用slice方法IE会报错
				try {
					return Native.slice.call(o, start);
				} catch (e) {
					a = [];
					l = o.length;
					for (; start < l; start++) {
						a.push(o[start]);
					}
					return a;
				}
			} else {
				return [o];
			}
		};

		/** 
		 * 测试对象的类型(数组，类数组元素，还是其他元素). 用于处理函数的参数列表(arguments)和html元素集合
		 *
		 * @method test
		 * @static
		 * @return {Int} 返回一个数字作为结果
		 * 0: 不是数组或类数组集合
		 * 1: 数组.
		 * 2: 类数组集合.
		 */

		A.test = function(o) {
			var r = 0;
			if (L.isObject(o)) {
				if (L.isArray(o)) {
					r = 1;
				} else {
					try {
						// indexed, but no tagName (element) or alert (window), or functions without apply/call (Safari HTMLElementCollection bug)
						if ((LENGTH in o) && !o.tagName && !o.alert && !o.apply) {
							r = 2;
						}

					} catch (e) {}
				}
			}
			return r;
		};

		/**
		 * 遍历数组，将数组的每一个值都作为参数执行函数.
		 *
		 * @method each
		 * @param a {Array} 要遍历的数组
		 * @param f {Function} 遍历数组执行的函数.执行时将传入value,value对应的索引，整个数组三个参数
		 * @param o {Object} 函数上下文(执行函数的对象)
		 * @static
		 * @return {Object} e5
		 */
		A.each = (Native.forEach) ?
		function(a, f, o) {
			Native.forEach.call(a || [], f, o || e5);
			return e5;
		} : function(a, f, o) {
			var l = (a && a.length) || 0,
				i;
			for (i = 0; i < l; i = i + 1) {
				f.call(o || e5, a[i], i, a);
			}
			return e5;
		};

		/**
		 * 遍历数组，将数组的每一个值都作为参数执行函数，如果函数返回true则停止遍历
		 * 
		 * @method some
		 * @param a {Array} 要遍历的数组
		 * @param f {Function} 遍历数组执行的函数，执行时将传入value,value对应的索引，整个数组三个参数
		 * @param o {Object} 函数上下文(执行函数的对象)
		 * @static
		 * @return {Boolean} 如果遍历过程中函数返回true则返回true,如果没有则返回false
		 */

		A.some = (Native.some) ?
		function(a, f, o) {
			return Native.some.call(a, f, o);
		} : function(a, f, o) {
			var l = a.length,
				i;
			for (i = 0; i < l; i = i + 1) {
				if (f.call(o, a[i], i, a)) {
					return true;
				}
			}
			return false;
		};

		/**
		 * 将两个数组映射为一个对象。第一个数组的内容作为key,第二个数组的内容作为value,
		 * 如果第二个数组长度不够，则将value设为true.
		 * 
		 * @method hash
		 * @static
		 * @param k {Array} 作为key的数组
		 * @param v {Array} 作为value的数组
		 * @return {object} 映射好的对象
		 */

		A.hash = function(k, v) {
			var o = {},
				l = k.length,
				vl = v && v.length,
				i;
			for (i = 0; i < l; i = i + 1) {
				if (k[i]) {
					o[k[i]] = (vl && vl > i) ? v[i] : true;
				}
			}

			return o;
		};

		/**
		 * 在数组中查找指定值，找到后返回索引
		 * 
		 * @method indexOf
		 * @static
		 * @param a {Array} 要查找的数组
		 * @param val {Any} 要查找的值
		 * @return {Int} 要查找值的索引，没找到返回-1
		 */

		A.indexOf = (Native.indexOf) ?
		function(a, val) {
			return Native.indexOf.call(a, val);
		} : function(a, val) {
			for (var i = 0; i < a.length; i = i + 1) {
				if (a[i] === val) {
					return i;
				}
			}
			return -1;
		};

	// =========================== require模块 ===========================

		var pathLoaded = {},// 已加载文件
			pathLoading = {},// 待加载文件
			// 判断事发后是数组
			// 全局配置
			global = {
				core_lib: ["../e5script/jquery/jquery.min.js"],
				/* mods结构：
					'modName' : {
						path: 'http://.../xxx.js',
						requires: ['common']
					}
				*/
				mods: {}
			},
			scripts = doc.getElementsByTagName("script"),
			scriptLast = scripts[scripts.length - 1],
			head = document.head || document.getElementsByTagName('head')[0] || document.documentElement,

			// 资源加载函数
			loadResource = function(path, charset, onload, param) {
				//参数检查
				if (!path) {
					return
				}
				
				// var firstScript = scripts[0];
				var isCss = /\.css(?:\?|$)/i.test(path); //读取文件类型
				var node = document.createElement(isCss ? 'link' : 'script');
				
				//判断文件是否已经加载，如果已经加载直接运行onload
				if (pathLoaded[path]) {
					pathLoading[path] = false;
					if (onload) {
						onload(path, param)
					}
					return;
				}
				
				//这里是对文件较大正在加载情况的处理
				if (pathLoading[path]) {
					setTimeout(function() {
						loadResource(path, charset, onload, param)
					}, 1);
					return;
				}
				pathLoading[path] = true;

				//判断文件标签是否已经创建好，如果未创建好避免错误
				if (!node) {
					return;
				}

				//设置编码格式
				if (charset) {
					node.charset = charset;
				}

				// 监听完成事件
				nodeOnload(node,function(){
					onload(path, param);
				});

				//创建标签
				if (isCss) {
					node.type = 'text/css';
					node.rel = 'stylesheet';
					node.href = path;
					head.appendChild(node);
					pathLoaded[path] = true;
				} else {
					node.type = 'text/javascript';
					node.src = path;
					node.async = 'async';
					head.insertBefore(node, head.firstChild);
				}

				// console.log(node);

			},

			nodeOnload = function(node, callback){
				if (node.nodeName === 'SCRIPT') {
					scriptOnload(node, cb);
				} else {
					styleOnload(node, cb);
				}
				var timer = setTimeout(function() {
						// console.log('Time is out:', node.src);
						cb();
					}, 20000);
				function cb(){
					if(!cb.isCalled){
						cb.isCalled = true;
						pathLoaded[node.getAttribute("src")] = true;
						clearTimeout(timer);
						callback();
					}
				}
			},

			scriptOnload = function(node, callback){
				node.onload = node.onreadystatechange = node.onerror = function(){
					if(/loaded|complete|undefined/.test(node.readyState)){
						node.onload = node.onreadystatechange = node.onerror = null;
						callback();

					// 删除节点释放内存的方法，暂时先注释掉，需要时再放出来
					
						if(node.parentNode){
							try {
								if (node.clearAttributes) {
									node.clearAttributes();
								} else {
									for (var p in node) delete node[p];
								}
							} catch (x) {
								// console.log("e5: scriptOnload : " + e.message)
							}

							head.removeChild(node);
						}
						node = undefined;
					
					}
				}
			},

			styleOnload = function(node, callback){
				if(node.attachEvent){
					node.attachEvent("onload",callback);
				}else{
					setTimeout(function(){
						poll(node, callback);
					},0);
				}
			},

			poll = function(node, callback) {
				var isLoaded;

				if (callback.isCalled) {
					return;
				}

				if (~navigator.userAgent.indexOf('AppleWebKit')) {
					if (node['sheet']) {
						isLoaded = true;
					}
				}else{
					if (node['sheet']) {
						try {
							if (node['sheet'].cssRules) {
								isLoaded = true;
							}
						} catch (ex) {
							// NS_ERROR_DOM_SECURITY_ERR
							if (ex.code === 1000) {
								isLoaded = true;
							}
						}
					}
				}

				setTimeout(function() {
					if (isLoaded) {
						// Place callback in here due to giving time for style rendering.
						callback();
					} else {
						poll(node, callback);
					}
				}, 1);
			},

			//把各模块与库的混合数据变成队列,将同一模块的文件编为一组
			makeQueue = function(arr) {
				//检查参数
				if (!arr || !L.isArray(arr)) {
					return
				}
				var i = 0,
					item, ret = [],
					mods = global.mods,
					queue = [],
					added = {},
					// 添加依赖模块函数
					addRequire = function(name) {
						var j = 0,
							requireMod, requires;
						// 重复的模块不添加
						if (added[name]) {
							return queue;
						}
						added[name] = true;
						// 如果有依赖模块就添加依赖模块
						if (mods[name].requires) {
							requires = mods[name].requires;
							for (; typeof(requireMod = requires[j++]) !== "undefined";) {
								// 如果依赖模块还有依赖模块就继续迭代
								if (mods[requireMod]) {
									addRequire(requireMod);
									queue.push(requireMod);
								} else {
									queue.push(requireMod);
								}
							}
							return queue;
						}
						return queue;
					};
				for (; typeof(item = arr[i++]) !== "undefined";) {
					if (mods[item] && mods[item].requires && mods[item].requires[0]) {
						// 每次重置队列和添加变量
						queue = [];
						added = {};
						// 连接模块数组
						ret = ret.concat(addRequire(item));
					}
					ret.push(item);
				}
				return ret;
			},

			//队列执行器
			Execute = function(queue) {
				// 判断参数
				if (!queue || !L.isArray(queue)) {
					return;
				}
				this.queue = queue;
				this.current = null;
				this.end = false;
			};
		Execute.prototype = {
			start: function() {
				var self = this;
				self.current = self.next();
				if (!self.current) {
					self.end = true;
					return;
				}
				self.run();
			},
			run: function() {
				var self = this,
					mod, current = self.current;
				// 如果参数是函数则执行函数后开始下一次迭代
				if (typeof current === "function") {
					current();
					self.start();
					return;
				} else {
					if (typeof current === "string") {
						// 如果参数是模块名字，则查找模块配置
						if (global.mods[current]) {
							mod = global.mods[current];
							loadResource(mod.path, mod.charset, function(path,self) {
								self.start();
							}, self)
						} else {
							// 如果参数是文件地址且以js或css结尾则直接加载文件
							if (/\.js|\.css/i.test(current)) {
								loadResource(current, "", function(path, self) {
									self.start();
								}, self)
							} else {
								self.start();
							}
						}
					}
				}
			},
			next: function() {
				return this.queue.shift();
			}
		};
		function R() {
			// 将参数转化为数组
			// 相当于Array.prototype.slice.call(arguments, 0)
			//[].slice == Array.prototype.slice
			//slice的start由undefined自动转换为0
			// var args = [].slice.call(arguments),
			var args = Array.prototype.concat.apply([],arguments),
				// 将核心框架插入到队列前端
				// 检查加入队列的模块是否有依赖模块，如果有同样加入到队列中
				// 建立执行实例
				execute = new Execute(makeQueue(args));
			// 开始执行
			execute.start();
		};

		R.add = function(name, obj) {
			// 判断参数是否正确
			if (!name || !obj || !obj.path) {
				return;
			}
			// 将参数加入队列
			global.mods[name] = obj;
		};
		R.delay = function() {
			// var args = [].slice.call(arguments),
			var args = arguments,
				delay = args.shift();
			win.setTimeout(function() {
				R.apply(this, args);
			}, delay);
		};
		// 加载底层框架
		// R(global.core_lib);

		e5.require = R;
		e5.obj = O;
		e5.lang = L;
		e5.arr = A;

		return e5;
	})(window,document);
	if(e5){
		var oldE5 = e5;
		e5 = _e5;
		for(var i in oldE5){
			e5[i] = oldE5[i];
		}
		oldE5 = null;
	}else{
		e5 = _e5;
	}
	_e5 = null;
}