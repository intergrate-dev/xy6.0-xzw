package com.founder.xy.template;

import java.util.Date;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.LibHelper;

/**
 * 发布服务中使用的模板对象
 * @author Gong Lijie
 */
public class Template {
	private long id;
	private int type;
	private int channel;
	private String name;
	private String filePath;
	private String fileType;
	private long[] colRelated;
	private Date expireDate;
	private int groupID;
	private int siteID;
	
	public Template(Document doc) {
		id = doc.getDocID();
		filePath = doc.getString("t_file");
		fileType = doc.getString("t_fileType");
		type = doc.getInt("t_type");
		channel = doc.getInt("t_channel");
		name=doc.getString("t_name");
		//模板中的稿件列表组件明确指定的栏目ID
		String colRel = doc.getString("t_colRelated");
		if (!StringUtils.isBlank(colRel)) {
			colRelated = StringUtils.getLongArray(colRel);
		}
		expireDate = doc.getDate("t_expireDate");
		groupID = doc.getInt("t_groupID");
		siteID = doc.getInt("t_siteID");
	}
	
	public Document covert2Document(){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			doc = docManager.newDocument(LibHelper.getTemplateLibID(), this.id);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		doc.set("t_file", this.filePath);
		doc.set("t_fileType", this.fileType);
		doc.set("t_type", this.type);
		doc.set("t_channel", this.channel);
		doc.set("t_name", this.name);
		doc.set("t_expireDate", this.expireDate);
		doc.set("t_groupID", this.groupID);
		doc.set("t_siteID", this.siteID);
		return doc;
	}

	public long getId() {
		return id;
	}

	public String getFilePath() {
		return filePath;
	}

	public long[] getColRelated() {
		return colRelated;
	}

	public int getType() {
		return type;
	}

	public String getFileType() {
		return fileType;
	}

	public int getChannel() {
		return channel;
	}

	public String getName() {
		return name;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public int getGroupID() {
		return groupID;
	}

	public int getSiteID() {
		return siteID;
	}
	
}
