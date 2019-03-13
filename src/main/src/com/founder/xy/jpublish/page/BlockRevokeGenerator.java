package com.founder.xy.jpublish.page;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.context.BlockContext;
import com.founder.xy.jpublish.data.PubArticle;

/**
 * 区块删除操作
 * @author kangxw
 *
 */
public class BlockRevokeGenerator {

	private BlockContext context;
	
	public int generator(DocIDMsg data){
		System.out.println("【" + Thread.currentThread().getName() + "】 区块删除：" 
				+ data.getDocLibID() + "," + data.getDocID());
		
		context = new BlockContext();
		context.init(data);
		
		String pathName = context.getBlock().getPath();
		try {
			//写入空白到区块文件
			FileUtils.writeStringToFile(new File(pathName), "", "UTF-8");
			//trans：生成分发信息文件
			PublishHelper.writeTransPath(pathName, context.getSiteRoot());
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			docManager.delete(data.getDocLibID(), data.getDocID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		DocumentManager docManager = DocumentManagerFactory.getInstance();
//		try {
//			Document blockDoc = docManager.get(data.getDocLibID(), data.getDocID());
//			long bDirID = blockDoc.getLong("b_dir_ID");
//			int dirLibID = LibHelper.getDomainDirLibID();
//			Document dirDoc = docManager.get(dirLibID, bDirID);
//			String filePath = dirDoc.getString("dir_path");
//			String webroot = InfoHelper.getConfig("写稿服务", "发布根目录");
//			FileUtils.writeStringToFile(new File(filePath), " ", false);
//			docManager.delete(blockDoc);
//		} catch (E5Exception e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return PubArticle.SUCCESS;
	}
}
