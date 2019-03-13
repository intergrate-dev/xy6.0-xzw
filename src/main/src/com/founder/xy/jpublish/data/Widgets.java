package com.founder.xy.jpublish.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

/**
 * 稿件挂件类，包含图片挂件、视频挂件、文件挂件、投票挂件等信息
 * @author Gong Lijie
 */
public class Widgets {
	private WidgetPic pic = new WidgetPic("", "", new ArrayList<Widget>());
	private Widget video = new Widget(0, "", "", "");
	private List<Attachment> attachments = new ArrayList<>();
	private Vote vote;
	
	public Widgets() {
	}
	
	public Widgets(Document[] ws) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int attLibID = 0;
		try {
			for (Document w : ws) {
				int type = w.getInt("w_type");
				int picLibID = w.getInt("w_objLibID");
				long picID = w.getLong("w_objID");
				
				if (attLibID == 0)
					attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), w.getDocLibID());
				
				switch (type) {
				case 1: {//组图挂件
					Document pic = docManager.get(picLibID, picID);
					Document[] members = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?", 
							new Object[]{picID, picLibID});
					if(members != null && members.length > 0){
						this.pic = new WidgetPic(pic.getTopic(), pic.getString("a_content"),
								getPicMemebers(members));
					}else{
						this.pic = new WidgetPic("", "", new ArrayList<Widget>());
					}
					break;
				}
				case 2: {//视频挂件
					Document v = docManager.get(picLibID, picID);
					Document[] members = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?", 
							new Object[]{picID, picLibID});
					if (members != null && members.length > 0) {
						Widget video = getPicMemebers(members).get(0);
						this.video = new Widget(v.getDocID(), v.getTopic(), 
								v.getString("a_content"), video.getUrl());
					}else{
						this.video = new Widget(0, "", "", "");
					}
					break;
				}
				case 3: {//投票挂件
					Document v = docManager.get(picLibID, picID);
					attLibID = LibHelper.getLibIDByOtherLib(DocTypes.VOTEOPTION.typeID(), w.getDocLibID());
					
					//投票选项
					Document[] members = docManager.find(attLibID, "vote_voteID=?", new Object[]{picID});
					if (members != null && members.length > 0) {
						this.vote = new Vote(picID, v.getString("vote_topic"), 
								v.getInt("vote_type"), v.getInt("vote_selectLimited"), v.getString("vote_endDate"),
								getVoteOptions(members));
					}
					break;
				}
				case 0: {//文件
					Attachment member = new Attachment(w, true);
					attachments.add(member);
					break;
				}
				default:
					break;
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	public WidgetPic getPic() {
		return pic;
	}

	public Widget getVideo() {
		return video;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public Vote getVote() {
		return vote;
	}

	//从附件表记录组织出组图的成员列表
	private List<Widget> getPicMemebers(Document[] members) {
		if (members == null || members.length == 0)
			return null;
		
		List<Widget> result = new ArrayList<>();
		for (Document m : members) {
			//优先用触屏url，是否可行？（视频时优先用mp4的，不会造成app出错）
			String url = m.getString("att_urlPad");
			if (StringUtils.isBlank(url)) url = m.getString("att_url");
			
			Widget member = new Widget(m.getString("att_content"), url);
			result.add(member);
		}
		return result;
	}

	//组装投票选项
	private List<VoteOption> getVoteOptions(Document[] members) {
		if (members == null || members.length == 0)
			return null;
		
		List<VoteOption> result = new ArrayList<>();
		for (Document m : members) {
			VoteOption member = new VoteOption(m.getDocID(), m.getString("vote_option"), m.getString("vote_picUrl"));
			result.add(member);
		}
		return result;
	}
}