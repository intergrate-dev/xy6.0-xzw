package com.founder.xy.commons;

import com.founder.e5.context.Cache;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLibManager;

/**
 * 系统缓存基础类。
 * 系统内的缓存需要处理多租户，每个租户各自刷新缓存
 */
public abstract class BaseCache implements Cache {
	public BaseCache() {

	}
	protected abstract int getDocTypeID();
	
	/**
	 * 刷新某一个租户下的缓存，有确定的文档库ID。
	 * 该方法供系统内调用
	 */
	public abstract void refresh(int docLibID) throws E5Exception;
	
	/** 缓存刷新 */
	@Override
	public void refresh() throws E5Exception {
		DocLibManager docLibManager = (DocLibManager)Context.getBean(DocLibManager.class);
		int[] docLibIDs = docLibManager.getIDsByTypeID(getDocTypeID());

		for (int docLibID : docLibIDs) {
			refresh(docLibID);
		}
	}

	/** 缓存重置 */
	@Override
	public void reset() {
	}
}