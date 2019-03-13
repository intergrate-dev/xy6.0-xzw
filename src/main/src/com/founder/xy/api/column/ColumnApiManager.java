package com.founder.xy.api.column;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.E5Exception;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.BaseDataCache;

@Service
public class ColumnApiManager {

	@Autowired
	private ColumnReader colReader;

	public String getColumns(int siteID) throws E5Exception {
		int colLibID = LibHelper.getColumnLibID();

		if (siteID <= 0)
			siteID = 1; // 若没有siteID，则当做默认站点

		// 先判断站点ID是否存在，避免用无效站点ID对缓存攻击
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(),
				colLibID);
		if (!siteExist(siteLibID, siteID))
			return "";

		List<Column> subList = colReader.getRoot(siteID, colLibID, 1);
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><result><success><head><hasnext>false</hasnext></head><body><columns>");

		sb.append(getColumnsAllJson(siteID, colLibID, subList));

		sb.append("</columns></body></success></result>");
		return sb.toString();
	}

	private String getColumnsAllJson(int siteID, int colLibID,
			List<Column> subList) throws E5Exception {
		StringBuffer sb = new StringBuffer();
		for (Column column : subList) {
			sb.append(colCommonFields(column));
			long _colID = column.getId();
			List<Column> _subList = (_colID == 0) ? colReader.getRoot(siteID,
					colLibID, 1) : colReader.getSub(colLibID, _colID);
			if (_subList != null && _subList.size() > 0) {
				sb.append(getColumnsAllJson(siteID, colLibID, _subList));
			}
			sb.append("</column>");

		}
		return sb.toString();
	}

	/**
	 * 设置栏目共通字段
	 */
	private String colCommonFields(Column bean) {

		StringBuffer sb = new StringBuffer();
		sb.append("<column id=\"");
		sb.append(bean.getId());
		sb.append("\" level=\"");
		sb.append(bean.getLevel());
		sb.append("\" name=\"");
		sb.append(StringUtils.getNotNull(bean.getName()));
		sb.append("\" type=\"column\">");

		return sb.toString();
	}

	// 检查网站ID是否存在。若能从缓存中读到网站发布路径，则认为存在。
	private boolean siteExist(int siteLibID, int siteID) {
		BaseDataCache cache = (BaseDataCache) CacheManager
				.find(BaseDataCache.class);
		try {
			String root = cache.getSiteWebRootByID(siteLibID, siteID);
			return (root != null);
		} catch (Exception e) {
			return false;
		}
	}
}
