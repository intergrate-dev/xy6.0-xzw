package com.founder.xy.commons;

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
 * @author Gong Lijie
 */
public enum CatTypes {
	CAT_COLUMN("栏目类型"),
	CAT_REGION("地区"),
	CAT_ORIGINAL("源稿分类"),
	
	CAT_PHOTO("图片分类"),
	CAT_VIDEO("视频分类"),
	CAT_SPECIAL("专题组"),
	CAT_SOURCE("来源组"),
	CAT_TEMPLATE("模板组"),
	CAT_BLOCK("页面区块组"),
	CAT_RESOURCE("公共资源组"),
	CAT_EXTFIELD("稿件扩展字段组"),
	CAT_VOTE("互动投票组"),
	CAT_TAG("标签组"),

	CAT_COLUMNSTYLE("栏目样式"),
	CAT_DISCUSSTYPE("话题分类"),

	CAT_QA("问答分类"),
	
	CAT_COLUMNARTICLE("栏目稿件关联"), //这个分类只是为了栏目稿件关联表的对应
	
	CAT_CORPTYPE("单位类别"),
	CAT_TRADE("行业"),
	CAT_STOCK("证券交易所"),
	CAT_APP("App渠道"),
	CAT_PUSHREGION("个推地区"),
	CAT_MEMBERINVITECODE("邀请码部门"),
	CAT_ARTICLETRADE("稿件行业")
	;

	private String typeName;
	private CatTypes(String typeName) {
		this.typeName = typeName;
	}
	public String typeName() {
		return this.typeName;
	}
	public int typeID() {
		CatType type = type();
		if (type == null)
			return 0;
		else
			return type.getCatType();
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
