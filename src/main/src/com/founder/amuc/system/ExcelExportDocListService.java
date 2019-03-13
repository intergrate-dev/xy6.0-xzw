package com.founder.amuc.system;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.founder.e5.db.IResultSet;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.workspace.ViewHelper;
import com.founder.e5.workspace.service.DefaultDocListService;
import com.founder.e5.workspace.service.ExportDocListService;

/**
 * 列表导出功能单独使用的ListService：改输出xml为输出Excel
 * <p>改用特定的分隔符分割，防止正文出现英文逗号导致拆分后导致Excel格式混乱
 * @author kangxw
 * 2018-02-09
 */
public class ExcelExportDocListService extends ExportDocListService{
	/* 分隔符 */
	public static final String DELIMITER = "##DELIMITER##";
	
	public String getXML(int count, IResultSet rs) {
		List<DocTypeField> fields = getFields(param.getFields());

		StringBuffer sbResult = new StringBuffer(1000);
		sbResult.append(getHeader(fields));//第一行显示字段名
		
		int begin = 0;
		try {
			String[] fieldDisables = getFieldsDisabled();//取出无权限的字段
			while (rs.next()) {
				if (begin < param.getBegin()) {
					begin++;
					continue;
				}
				if (++begin > (param.getBegin() + param.getCount())) break;

				for (DocTypeField f : fields) {
					if (ArrayUtils.contains(fieldDisables, f.getColumnCode())) {
						sbResult.append(DELIMITER);
					} else {
						String value = ViewHelper.getValue(rs, f);//按数据类型对值做一些处理
						sbResult.append("\"").append(parseMark(value)).append("\"").append(DELIMITER);
					}
				}
				sbResult.append("\r\n");
			}
			return sbResult.toString();
		} catch (Exception e) {
			log.error("[DefaultDocListService.getXML]", e);
			return EMPTY_XML;
		}
	}
	
	protected String getHeader(List<DocTypeField> fields) {
		StringBuffer sbResult = new StringBuffer(1000);
		//第一行显示字段名
		for (DocTypeField f : fields) {
			String name = f.getColumnName();
			sbResult.append("\"").append(name).append("\"").append(DELIMITER);
		}
		sbResult.append("\r\n");
		return sbResult.toString();
	}
}
