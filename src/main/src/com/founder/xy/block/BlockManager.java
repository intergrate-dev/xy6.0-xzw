package com.founder.xy.block;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.template.parser.BlockParser;

/**
 * 区块管理器
 * 
 * @author Deng Chaochen
 */
@Component
public class BlockManager {
	@Autowired
	private SiteUserManager userManager;

	/**
	 * 取指定ID 的页面区块
	 */
	public Document get(long id) throws E5Exception {
		int blockLibID = LibHelper.getBlockLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		return docManager.get(blockLibID, id);
	}

	/**
	 * 所有区块
	 */
	public List<Block> getBlocks(int blockLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] blocks = docManager.find(blockLibID, "SYS_DELETEFLAG=0", null);
		
		List<Block> result = new ArrayList<>();
		for (Document block : blocks) {
			result.add(new Block(block));
		}
		return result;
	}

	/**
	 * 取一个站点的所有区块
	 */
	public Document[] getBlocks(int blockLibID, int siteID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] blocks = docManager.find(blockLibID, "b_siteID=? and SYS_DELETEFLAG=0",
				new Object[] { siteID });

		return blocks;
	}

	/**
	 * 取所有的页面区块
	 */
	public Document[] getBlock(int siteID, int userId , int type) throws E5Exception {
		//获取跟用户权限相关的
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] reBlocks = docManager.find(LibHelper.getUserRelLibID(),
				" SYS_DOCUMENTID=? and ur_type=? and ur_siteID=?",
				new Object[] { userId, type, siteID });
		List<Object> blockIds = new ArrayList<Object>();
		blockIds.add(siteID);
		
		StringBuilder sb = new StringBuilder();
		Document[] blocks = {};
		//如果权限上面有值
		if (reBlocks != null && reBlocks.length > 0) {
			for (Document _b : reBlocks) {
				blockIds.add(_b.get("ur_id"));
				sb.append(",?");
			}
			
			int blockLibID = LibHelper.getBlockLibID();
			blocks = docManager.find(blockLibID, "b_siteID=?"
					+ (sb.length() > 0 ? " and SYS_DOCUMENTID in (" + sb.toString().substring(1) + ")"
							: "") + " and SYS_DELETEFLAG=0 and b_type=1", blockIds.toArray() );
		}

		return blocks;
	}

	/**
	 * 查询区块列表
	 */
	public Document[] find(int siteID, String name) throws E5Exception {
		int blockLibID = LibHelper.getBlockLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] blocks = docManager.find(blockLibID,
				"b_siteID=? and b_name like ? and SYS_DELETEFLAG=0 and b_type=1", new Object[] { siteID,
						name + "%" });
		return blocks;
	}

	/**
	 * 判断该分组下是否有页面区块，若没有返回false,有则返回true
	 */
	public boolean hasChild(int siteID, int catID) throws E5Exception {

		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] blocks = null;
			String sql = "b_siteID=? " + "and b_groupID=? ";
			int blockLibID = LibHelper.getBlockLibID();

			blocks = docManager.find(blockLibID, sql, new Object[] { siteID, catID });
			if ((blocks == null || blocks.length == 0)) {
				return false;
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 区块新建后，解析、自动添加权限，并发出消息
	 * @param block
	 * @param user
	 */
	public void afterSave(boolean isNew, Document block, int userID) {
		//页面区块：解析组件实例
		try {
			BlockParser blockParser = (BlockParser)Context.getBean("blockParser");
			blockParser.parse(block);
			
			PublishTrigger.blockChanged(block.getDocLibID(), block.getDocID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//对增加的区块设置权限。刷新缓存
		if (isNew) {
			int userRelLibID = LibHelper.getLibIDByOtherLib(DocTypes.USERREL.typeID(), block.getDocLibID());
			try {
				userManager.addRelated(userRelLibID, userID, block.getInt("b_siteID"),
						block.getDocID(), 3);
				userManager.cacheUserRelTrigger(userRelLibID);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
	}
}