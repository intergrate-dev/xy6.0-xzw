function Param(){
	this.docTypeID = 0;
	this.docLibID = 0;
	this.fvID = 0;
	this.extType = 0;//扩展类型，1表示垃圾箱
	this.catTypeID = 0;
}
Param.prototype.toString = function() {
	var str = "";
	for (var name in this) {
		if (name == "toString") continue;
		str += name + "=" + this[name] + "; ";
	}
	return str;
}

function ResourceParam(){
	this.ruleFormula = "";
}
ResourceParam.prototype = new Param();

function SearchParam(){
	this.ruleFormula = "";
	this.filterID = 0;	//过滤器ID。可以是多个，用逗号分隔"12,34,23"
	this.query = "";    //快速检索条件
}
SearchParam.prototype = new Param();

function StatusbarParam(){
	this.ruleFormula = "";//规则公式
	this.filterID = 0;	//过滤器ID。可以是多个，用逗号分隔"12,34,23"
	this.query = "";    //快速检索条件

	this.listPage = 0;	//列表方式ID
	this.extParams = ""; //扩展的参数，由上层应用调用setExtParams进行扩展。该参数一直传递给操作，格式"param1=value1&param2=value2"

	this.currentPage = 0;   //当前页数
	this.startPage = 0; 	//文档列表中缓存的起始页数，就是需要调用doclist时的当前页数
	this.cachePage = 1; 	//文档列表中缓存的页数，由列表方式决定
	this.countPerPage = 10; //文档列表中每页显示的条数，由列表方式决定
	this.albumColumn = 1;   //相册方式时的列数
}
StatusbarParam.prototype = new Param();

function DocListParam(){
	this.docLibIDs = "";//目前没有支持多文档库，这个属性还没有使用
	this.docIDs = "";

	this.extParams = ""; //扩展的参数，由上层应用调用setExtParams进行扩展。该参数一直传递给操作，格式"param1=value1&param2=value2"

	this.isRule = false;
	this.isQuery = false;
}
DocListParam.prototype = new Param();

function ToolkitParam(){
	this.flowNodeID = 0; //当前工具条显示操作所在的流程节点
	this.docIDs = "";

	this.extParams = ""; //扩展的参数，由上层应用调用setExtParams进行扩展。该参数一直传递给操作，格式"param1=value1&param2=value2"

	this.uuid = "";
	this.before = "";

	this.isQuery = false;
	this.menu = false;		//缺省是工具栏显示操作，当显式在文档列表上用右键显示菜单时，menu=true
	this.operations = {}; //操作的信息}
ToolkitParam.prototype = new Param();
ToolkitParam.prototype.add = function (op1) {
	this.operations[op1.id] = op1;
}

function Operation(op_id, op_proctype, op_procid, op_flownode, op_callmode, op_text,
	op_opheight, op_opwidth, op_resizable, op_opid, op_opurl, op_needprompt, imgurl, op_dealcount)
{
	this.id			= op_id; //一个唯一索引
	this.proctype 	= op_proctype;
	this.procid 	= op_procid;
	this.flownode 	= op_flownode;
	this.callmode	= op_callmode;
	this.text		= op_text;
	this.opheight 	= op_opheight;
	this.opwidth 	= op_opwidth;
	this.resizable 	= op_resizable;
	this.opid 		= op_opid;
	this.opurl		= op_opurl;
	this.needprompt = op_needprompt;
	this.imgurl     = imgurl;
	this.dealcount  = op_dealcount;
	this.canTool = true;
	this.canMenu = true;
}