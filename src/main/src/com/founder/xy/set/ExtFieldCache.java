package com.founder.xy.set;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;

/**
 * 扩展字段缓存类。提供按组ID读出缓存数据的方法
 * @author guzm
 */
public class ExtFieldCache extends BaseCache {
	private ExtFieldManager extFieldManager;

	//<libID, <groupID, fieldSet>>
	private Map<Integer, Map<Long, Set<ExtField>>> extFieldMap = new HashMap<>();

	public ExtFieldCache() {
	}

	@Override
	public void refresh(int docLibID) throws E5Exception {
		if (extFieldManager == null) {
			extFieldManager = (ExtFieldManager)Context.getBean("extFieldManager");
		}

		try {
			Map<Long, Set<ExtField>> map = extFieldManager.findExtFieldMap(docLibID);
			
			extFieldMap.put(docLibID, map);
			
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	public Set<ExtField> getExtFieldSetByGroupId(int docLibID, long groupId) {
		return extFieldMap.get(docLibID).get(groupId);
	}

	@Override
	protected int getDocTypeID() {
		return DocTypes.EXTFIELD.typeID();
	}
}
