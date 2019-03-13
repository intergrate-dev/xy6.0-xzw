package com.founder.xy.workspace.form;

import javax.servlet.http.HttpServletRequest;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.block.BlockManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 对表单保存进行修改，增加业务逻辑。
 * 继承FormSaver
 * @author Gong Lijie
 */
public class FormSaver extends com.founder.e5.workspace.app.form.FormSaver{
	@Override
	public long handle(Document doc, HttpServletRequest request) throws Exception {
		boolean isNew = doc.isNew();
		
		long docID = super.handle(doc, request);
		
		//修改部分
		changeAfterSave(isNew, doc, request);
		
		return docID;
	}
	@Override
	public Pair handleChanged(Document doc, HttpServletRequest request) throws Exception {
		boolean isNew = doc.isNew();
		
		Pair value = super.handleChanged(doc, request);
		
		//修改部分
		changeAfterSave(isNew, doc, request);

		return value;
	}
	@Override
	protected void save(Document doc) throws E5Exception {
		changeBeforeSave(doc);
		super.save(doc);
	}
	
	private void changeBeforeSave(Document doc) throws E5Exception {
		int docTypeID = doc.getDocTypeID();
		
		//若是稿件扩展字段，则填写字段code
		if (docTypeID == DocTypes.EXTFIELD.typeID()) {
			String code = doc.getString("ext_code");
			if (StringUtils.isBlank(code)) {
				doc.set("ext_code", "a_extField" + doc.getDocID());
			}
			if (doc.isNew()) doc.set("ext_order", doc.getDocID());
		} else if (doc.isNew() && docTypeID == DocTypes.TOPIC.typeID()) {
			//选题的顺序号=ID
			doc.set("t_order", doc.getDocID());
		} else if (doc.isNew() && docTypeID == DocTypes.LEADER.typeID()) {
			//顺序号
			if (doc.getInt("l_order") <= 0)
				doc.set("l_order", doc.getDocID());
		} else if (doc.isNew() && docTypeID == DocTypes.BATMAN.typeID()) {
			//通讯员密码非空
			doc.set("bm_password", "");
		}
	}
	
	private void changeAfterSave(boolean isNew, Document doc, HttpServletRequest request) throws Exception {
		int docTypeID = doc.getDocTypeID();

		if (docTypeID == DocTypes.BLOCK.typeID()) {
			//页面区块：解析模板代码、自动添加权限、发布消息
			BlockManager blockManager = (BlockManager)Context.getBean("blockManager");
			blockManager.afterSave(isNew, doc, ProcHelper.getUserID(request));
		} else if (docTypeID == DocTypes.EXTFIELD.typeID()) {
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_EXTFIELD);
		} else if (docTypeID == DocTypes.SOURCE.typeID()) {
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_SOURCE);
		} else if (docTypeID == DocTypes.SITE.typeID()) {
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_SITE);
		} else if (docTypeID == DocTypes.SITERULE.typeID()) {
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_SITERULE);
		} else if (docTypeID == DocTypes.AD.typeID()) {
			//广告，清掉redis中的旧缓存
			clearAdvKey(doc);
		}
	}
	private void clearAdvKey(Document doc) throws E5Exception {
		int type = doc.getInt("ad_type");
		int colID = doc.getInt("ad_columnID");
		if(type == 0){
			String tenantCode = LibHelper.getTenantCodeByLib(doc.getDocLibID());
			RedisManager.clear(RedisKey.ADV_STARTUP_KEY + tenantCode);	
		}
		else{
			//栏目广告修改广告类型时，需要清理该栏目的所有广告类型缓存
			RedisManager.clear(RedisKey.ADV_COLUMN_KEY + colID);
			RedisManager.clear(RedisKey.ADV_COLUMN_LIST_KEY + colID);
			RedisManager.clear(RedisKey.ADV_PAGE_KEY + colID);
			RedisManager.clear(RedisKey.ADV_PAGE_ALBUM_KEY + colID);
		}
		RedisManager.clear(RedisKey.ADV_INFO_KEY + doc.getDocID());
		
	}
}
