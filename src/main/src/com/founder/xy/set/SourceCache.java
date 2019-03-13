package com.founder.xy.set;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;

/**
 * 来源缓存
 */
public class SourceCache extends BaseCache {
	private SourceManager sourceManager;

	//按组ID缓存来源，方便读。<srcLibID, <groupId,Set<source>>>
	private Map<Integer, Map<Long, Set<Source>>> sourceByGroup = new HashMap<>();
	//<来源库ID, <来源名,来源ID>>   用于写稿中取来源ID
	private Map<Integer, Map<String, Long>> sourceByName = new HashMap<>();
	
	//<srcLibID, <ColumnId,Set<source>>>
	//private Map<Integer, Map<Long, Set<Source>>> KColumnVSourceSetMap = new HashMap<>();

	public SourceCache() {
	}

	@Override
	public void refresh(int srcLibID) {
		if (sourceManager == null) {
			sourceManager = new SourceManager();
		}
		try {
			Map<Long, Set<Source>> map1 = sourceManager.findSourceMap(srcLibID);
			
			sourceByGroup.put(srcLibID, map1);
			sourceByName.put(srcLibID, getSrcByNameMap(map1));
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 按组ID取出来源
	 * @param srcLibID 来源库ID
	 * @param groupId 组ID
	 * @return
	 */
	public Set<Source> getSourceSetByGroupId(int srcLibID, long groupId) {
		return sourceByGroup.get(srcLibID).get(groupId);
	}
	
	/**
	 * 按来源名得到来源ID
	 * @param srcLibID 来源库ID
	 * @param name 来源名
	 * @return
	 */
	public long getSourceIDByName(int srcLibID, String name) {
		Long ret = sourceByName.get(srcLibID).get(name);
		return (ret != null) ? ret.longValue() : 0;
	}
	
	/**
	 * 按栏目ID取出来源
	 * @param srcLibID 来源库ID
	 * @param colID 栏目ID
	 * @return
	public Set<Source> getSourceSetByColumnId(int srcLibID, long colID) {
		return KColumnVSourceSetMap.get(srcLibID).get(colID);
	}
	 */

	@Override
	protected int getDocTypeID() {
		return DocTypes.SOURCE.typeID();
	}

	private Map<String, Long> getSrcByNameMap(Map<Long, Set<Source>> map1) {
		Map<String, Long> map2 = new HashMap<>();
		if (!map1.isEmpty()) {
			for (long groupID : map1.keySet()) {
				Set<Source> srcs = map1.get(groupID);
				if (!srcs.isEmpty()) {
					for (Source src : srcs) {
						map2.put(src.getName(), src.getId());
					}
				}
			}
		}
		return map2;
	}
}
