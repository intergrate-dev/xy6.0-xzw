package com.founder.xy.set;

import java.util.*;

import org.springframework.stereotype.Component;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;

/**
 * 扩展字段定义管理器
 */
@Component
public class ExtFieldManager {

	/**
	 * 从数据库中查询所有的扩展字段，并且把这些字段组成一个可以用groupId进行查询的Map
	 * @return
	 * @throws E5Exception
	 */
	public Map<Long, Set<ExtField>> findExtFieldMap(int docLibID) throws E5Exception {
		Map<Long, Set<ExtField>> extFieldMap = new HashMap<>();
		Long _groupId = null;
		Set<ExtField> extFieldSet = null;
		ExtField extfield = null;
		//取出全部扩展字段
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] documents = docManager.find(docLibID, " SYS_DELETEFLAG=0 order by ext_order desc ", new Object[] {});
		
		//如果数据库中有扩展字段，组织扩展字段的map
		if (documents != null && documents.length > 0) {
			for (Document doc : documents) {
				extfield = assembleExtField(doc);
				_groupId = doc.getLong("ext_groupID");
				//如果map中有这一项，拿出来set，并把extfield加到set里面
				if (extFieldMap.containsKey(_groupId)) {
					extFieldSet = extFieldMap.get(_groupId);
					extFieldSet.add(extfield);
				}else{
					//如果没有，初始化一个set，然后把extfield放到里面，在把他们压到map中
					extFieldSet = new LinkedHashSet<>();
					extFieldSet.add(extfield);
					extFieldMap.put(_groupId, extFieldSet);
				}
			}
		}
		return extFieldMap;
	}

	/**
	 * 把doc组装成ExtField对象
	 * @param doc
	 * @return
	 */
	private ExtField assembleExtField(Document doc) {
		if (doc == null)
			return null;
		ExtField e = new ExtField();
		e.setSys_documentid(doc.getDocID());
		e.setSys_doclibid(doc.getDocLibID());
		e.setExt_code(doc.getString("ext_code"));
		e.setExt_editType(doc.getLong("ext_edittype"));
		e.setExt_groupID(doc.getLong("ext_groupID"));
		e.setExt_name(doc.getString("ext_name"));
		e.setExt_options(doc.getString("ext_options"));
		e.setExt_siteID(doc.getLong("ext_siteID"));
		e.setExt_defaultValue(doc.getString("ext_defaultValue"));
		e.setExt_order(doc.getInt("ext_order"));
		return e;
	}
}
