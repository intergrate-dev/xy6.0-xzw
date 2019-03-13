<%@ page pageEncoding="UTF-8"%>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>

<script type="text/javascript" src="../../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>
<style type="text/css">
	body input{
		font-family: "microsoft yahei";
		font-size: 12px;
	}
	#doSave{
		  border-radius: 3px;
		  color: #fff;
		  background:#00a0e6;
		  width: 65px;
		  height: 25px;
		  border: none;
		  margin-left:px;
		  font-size: 12px;
		  cursor: pointer;
	}
	#doCancel{
		  border-radius: 3px;
		  color: #fff;
		  background:#b1b1b1;
		  width: 65px;
		  height: 25px;
		  border: none;
		  margin-left:px;
		  font-size: 12px;
		  cursor: pointer;
	}
	#templateSearch{
		padding-left: 10px;
		border-radius: 3px;
		border: none;
		border: 1px solid #ddd;
	}
</style>
<div style="border-bottom:1px solid #ddd; padding-bottom:10px;font-size:12px;">
	<input id="templateSearch" placeholder="查找" type="text" value="" title="请输入分组名称进行查询" size="16"/>
		<input type='button' id="doSave" value='保存'/>
		<input type='button' id="doCancel" value='取消'/>
</div>

<div id="rs_tree" class="ztree" style="min-height:300px;">
</div>

<script language="javascript" type="text/javascript" src="../../xy/template/script/channelTree.js"></script>
