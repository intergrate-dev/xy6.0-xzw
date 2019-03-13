package com.founder.xy.jpublish.magazine;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.founder.e5.doc.Document;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.paper.Paper;

/**
 * 期刊
 * @author Gong Lijie
 */
public class Magazine extends Paper implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Date date; //刊期
	private Attachment pic; //该刊期的封面图
	private List<Attachment> attachments; //附件（图片、Pdf）
	
	public Magazine(Document doc) {
		super(doc);
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Attachment getPic() {
		return pic;
	}
	public void setPic(Attachment pic) {
		this.pic = pic;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}
