package com.founder.amuc.score;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.workspace.form.NewFormSaver;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.dom.DocTypeField;

/**
 * 积分表单（添加/扣减）保存的单独处理：
 * 1）积分规则ID是包含了多个隐藏域的值，保存前需处理
 * 2）若是扣减，则改成负分
 * 3）把积分加到会员表中
 * @author Gong Lijie
 * 2014-6-11
 */
public class ScoreFormSaver extends NewFormSaver{
	/**
	 * 表单保存
	 * @param request
	 * @throws Exception
	 */
	public long handle(HttpServletRequest request) throws Exception {
		Document doc = prepareDoc(request);
		handle(doc, request);
		
		//把积分加到会员表中 TODO:需要做成事务
		String tenantCode = InfoHelper.getTenantCode(request);
		addScore(tenantCode, doc.getLong("msMember_ID"), doc.getInt("msScore"));
		return doc.getDocID();
	}
	
	//保存前，把扣减的分数改成负数
	protected void save(Document doc) throws E5Exception {
		int msType = doc.getInt("msType");
		//扣减，把积分改成负数
		if (msType == 2) {
			int score = doc.getInt("msScore");
			//int experience = doc.getInt("msExperience");
			
			if (score > 0) score = -1 * score;
			//if (experience > 0) experience = -1 * experience;
			
			doc.set("msScore", score);
			//doc.set("msExperience", experience);
		}
		super.save(doc);
	}
	//把积分加到会员表中
	private void addScore(String tenantCode, long memberID, int score) {
		try {
			String table = InfoHelper.getLibTable(Constant.DOCTYPE_MEMBER, tenantCode);
			String sql = "update " + table + " set mScore=mScore+?  where SYS_DOCUMENTID=?";
			InfoHelper.executeUpdate(sql, new Object[]{score, memberID});
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	//积分规则ID：隐含了多个字段的值，处理
	protected void setVarcharValue(String columnCode, DocTypeField docTypeField, Document doc, HttpServletRequest request){
		if (columnCode.equals("msRule")) {
			String value = get(request, columnCode);
			doc.set(columnCode, value);
			
			//增加的部分
			columnCode = "msRule_ID";
			value = get(request, columnCode);
			if (!StringUtils.isBlank(value)) {
				String[] values = value.split(",");
				doc.set(columnCode, values[0]);
			}
		} else 
			super.setVarcharValue(columnCode, docTypeField, doc, request);
	}
}
