package com.founder.xy.set;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.founder.e5.context.CacheReader;

/**
 * 从缓存中读扩展字段
 * @author Gong Lijie
 */
@Component
public class ExtFieldReader {
	/**
	 * 按组取扩展字段
	 * @param groupID 组ID
	 * @return
	 */
	public Set<ExtField> getFields(int docLibID, long groupID) {
		ExtFieldCache cache = (ExtFieldCache) (CacheReader.find(ExtFieldCache.class));
		
		Set<ExtField> extFieldSet = cache.getExtFieldSetByGroupId(docLibID, groupID);
		return extFieldSet;
	}

}
