package com.founder.xy.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.system.TenantManager;

/**
 * 栏目缓存类
 * 1）按ID索引栏目
 * 2）按父ID索引子栏目
 * 3）按ID索引一个栏目被多少栏目作为聚合栏目
 */
public class ColumnCache extends BaseCache {
	@Autowired
	private TenantManager tenantManager;
	
	/** 聚合栏目Map <docLibID, <aggregat_col_id, Set<Long:col_id>>> */
	private Map<Integer, Map<Long, Set<Long>>> columnAggregates = new HashMap<>();
	
	/** 所有栏目，按ID索引 <docLibID, <columnID, column>> **/
	private Map<Integer, Map<Long, Column>> columns = new HashMap<>();
	
	/** 所有栏目，按父ID索引 <docLibID, <parentColumnID, column>> **/
	private Map<Integer, Map<Long, List<Column>>> colByParent = new HashMap<>();
	
	/** 需要审批的栏目 <docLibID, list>**/
	private Map<Integer, List<Column>> auditColumns = new HashMap<>();

	@Override
	protected int getDocTypeID() {
		return DocTypes.COLUMN.typeID();
	}

	@Override
	public synchronized void refresh(int colLibID) throws E5Exception {
		ColumnManager columnManager = (ColumnManager)Context.getBean("columnManager");

		//获取所有的聚合列表的map
		Map<Long, Set<Long>> aggregates = columnManager.findcolumnAggregateMap(colLibID);

		Map<Long, Column> byIDs = columnManager.getColumnAll(colLibID);

		//每个栏目的子栏目
		//Map<Long, List<Column>> byParents = columnManager.getColumnsOfParent(colLibID);
		Map<Long, List<Column>> byParents = new HashMap<>();
		for (Column column : byIDs.values()) {
			long parentID = column.getParentID();
			List<Column> brothers = byParents.get(parentID);
			if (brothers == null) {
				brothers = new ArrayList<Column>();
				
				byParents.put(parentID, brothers);
			}
			brothers.add(column);
		}
		
		//按栏目排序做调整
		for (List<Column> brothers : byParents.values()) {
			sort(brothers);
		}

		//找出需要审批的栏目
		List<Column> audits = new ArrayList<>();
		for (Column column : byIDs.values()) {
			if (column.getFlowID() > 1)
				audits.add(column);
		}
		
		columnAggregates.put(colLibID, aggregates);
		columns.put(colLibID, byIDs);
		colByParent.put(colLibID, byParents);
		auditColumns.put(colLibID, audits);
	}
	
	/** 读一个栏目 */
	public Column get(int colLibID, long colID) {
		Column col = columns.get(colLibID).get(colID);
		//取克隆，避免修改损害缓存
		return col == null ? null : col.clone();
	}
	
	public Column[] get(int colLibID) {
		return columns.get(colLibID).values().toArray(new Column[0]);
	}

	/** 读一个栏目的子栏目 */
	public List<Column> getSub(int colLibID, long colID) {
		List<Column> cols = colByParent.get(colLibID).get(colID);
		return clones(cols);
	}
	/** 读一个站点下的审批栏目 */
	public List<Column> getAuditColumns(int colLibID, int siteID) {
		List<Column> columns = new ArrayList<Column>();
		
		if (auditColumns.get(colLibID) != null)
		for (Column column : auditColumns.get(colLibID)) {
			if (column.getSiteID() == siteID)
				columns.add(column.clone());
		}
		return columns;
	}

	/** 获得聚合某栏目的所有栏目集合 */
	public Set<Long> getAggregators(int colLibID, long colID) {
		return columnAggregates.get(colLibID).get(colID);
	}

	private List<Column> clones(List<Column> cols) {
		if (cols == null) return null;
		
		List<Column> result = new ArrayList<>();
		for (Column col : cols) {
			result.add(col.clone());
		}
		return result;
	}
	
	private void sort(List<Column> brothers) {
		if (brothers.size() <= 1) return;
		
		Collections.sort(brothers, new Comparator<Column>() {
			public int compare(Column me1, Column me2) {
				return new Integer(me1.getOrder()).compareTo(new Integer(me2.getOrder()));
			}
		});
	}
}