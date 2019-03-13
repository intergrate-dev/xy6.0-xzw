package com.founder.amuc.commons;

import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.CatType;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;

/**
 * 常量定义：分类类型名称。<br/>
 * 每个常量有三个方法：<br/>
 * .typeName();<br/>
 * .typeID();<br/>
 * .type();<br/>
 * 
 * 可以用如下方式得到常量：<br/>
 * Enum.valueOf(CatTypes.class, "CAT_" + code);<br/>
 * 
 * 更多说明，请参考DocTypes
 * @author Gong Lijie
 * 2011-11-9
 */
public enum CatTypes {
	CAT_BASE("分类"),
	CAT_REGION("地区"),
	CAT_TRADE("行业分类"),
	CAT_CREDITTYPE("信用分类"),
	CAT_MENU("一级菜单"),
	CAT_STAT("统计维度"),
	CAT_ACCOUNT("微信多账号"),
	;

	private String typeName;
	private CatTypes(String typeName) {
		this.typeName = typeName;
	}
	public String typeName() {
		return this.typeName;
	}
	public int typeID() {
		return type().getCatType();
	}
	public CatType type() {
		CatType catType = null;
		CatReader catReader = (CatReader) Context.getBean(CatReader.class);
		try {
			catType = catReader.getType(typeName());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return catType;
	}
}