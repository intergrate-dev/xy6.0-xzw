package com.founder.xy.block;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

/**
 * 区块内容管理器
 * 
 * @author Deng Chaochen
 */
@Component
public class BlockArticleManager {
	
	/**
	 * 新建一个指定ID 的页面区块内容
	 */
	public Document getNewDoc(long id) throws E5Exception {
		int docLibID = LibHelper.getBlockArticleLibID();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document barticle = docManager.get(docLibID, id);

		if (barticle == null) {
			barticle = docManager.newDocument(docLibID, id);
		}
		
		return barticle;
	}
	
	/**
	 * 设置页面区块内容稿件的所属区块、标题、链接、标题图片、稿件的流程状态
	 * 用于稿件新建
	 */
	public void setBlockArt(Document barticle,Document article,Document block) throws E5Exception {
		int ch = block.getInt("b_channel");//取区块的所属渠道
		
		//这篇内容所属区块
		barticle.set("ba_blockID", block.getDocID());
		//标题
		barticle.setTopic(article.getTopic());
		//副题
		barticle.set("ba_subTitle", article.getString("a_subTitle"));
		//摘要
		barticle.set("ba_abstract", article.getString("a_abstract"));
		//稿件的发布url，优先用相符渠道的Url，若无再换另一渠道的Url
		String url = (ch == 0) ? article.getString("a_url") : article.getString("a_urlPad");
		if (StringUtils.isBlank(url))
			url = (ch == 0) ? article.getString("a_urlPad") : article.getString("a_url");
		
		barticle.set("ba_url", url);
		
		//区块内容设置order，顺序号ba_order与DocID相同
		barticle.set("ba_order", barticle.getDocID());
		
		//标题图片
		int picTitleType = block.getInt("b_picType") + 2; //附件表里附件类型的定义正好与区块的图片类型定义差2
		String picUrl = getPicUrl(article.getDocLibID(),article.getDocID(),ch,picTitleType);
		barticle.set("ba_pic", picUrl);
		ProcHelper.initDoc(barticle);

	}
	
	//在稿件内容附件的库中根据稿件找到attType类型的发布路径
	private String getPicUrl(int docLibID, long docID, int ch, int attType) throws E5Exception {
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), docLibID);
        String sql = "att_articleID=? and att_articleLibID=? and att_type=? order by att_order";

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] attas = docManager.find(attLibID, sql, new Object[]{docID, docLibID, attType});
        String picUrl = "";
        if (attas.length > 0) {
        	Document atta = attas[0];
        	if (ch == 0){
        		picUrl = atta.getString("att_url");
        		if (StringUtils.isBlank(picUrl)) picUrl = atta.getString("att_urlPad");
        	} else {
        		picUrl = atta.getString("att_urlPad");
        		if (StringUtils.isBlank(picUrl)) picUrl = atta.getString("att_url");
        	}
        }
		return picUrl;
	}
}