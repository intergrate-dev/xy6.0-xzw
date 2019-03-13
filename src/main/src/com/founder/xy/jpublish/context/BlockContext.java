package com.founder.xy.jpublish.context;

import java.util.ArrayList;
import java.util.List;

import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.block.Block;
import com.founder.xy.block.BlockCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.site.DomainDir;
import com.founder.xy.template.ComponentObj;
import com.founder.xy.template.parser.BlockParser;

/**
 * 区块发布的上下文环境
 * @author Gong Lijie
 */
public class BlockContext {
	private DocIDMsg message;
	private Block block;
	private String siteRoot;
	private List<BareArticle> articles = new ArrayList<>();
	
	public void init(DocIDMsg message){
		this.message = message;
		
		block = get(message.getDocLibID(), message.getDocID());
		
		//如果redis缓存丢失，重新刷新缓存
		if (RedisManager.hget(RedisKey.BLOCK_CO_KEY, block.getId()) == null){
			BlockParser blockParser = (BlockParser)Context.getBean("blockParser");
			List<ComponentObj> coList = blockParser.getComponentObjs(message.getDocID(), 1);
			blockParser.refreshCache(coList, block.getId(), 1);
		}
		
		readArticles(block);
		
		setPageDir();
	}
	public DocIDMsg getMessage(){
		return message;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public List<BareArticle> getArticles() {
		return articles;
	}
	
	/**
	 * 分发信息文件位置
	 */
	public String getSiteRoot() {
		return siteRoot;
	}
	/**
	 * 根据ID得到区块对象
	 */
	private Block get(int colLibID, long colID) {
		BlockCache cache = (BlockCache) (CacheReader.find(BlockCache.class));
		return cache.get(colLibID, colID);
	}
	//读区块下的稿件列表
	private void readArticles(Block block) {
		try {
			int baLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCKARTICLE.typeID(), message.getDocLibID());
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] docs = docManager.find(baLibID,
					"ba_blockID=? and SYS_CURRENTNODE=0 order by ba_order desc",
					new Object[]{message.getDocID()});
			
			int showCount = block.getCount();
			int length = (showCount <= 0) ? docs.length : ( showCount > docs.length ? docs.length : showCount);
			
			for (int i = 0; i < length; i++) {
				articles.add(new BareArticle(docs[i], true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//设置区块的发布地址
	private void setPageDir(){
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), message.getDocLibID());
		int dirLibID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), message.getDocLibID());
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		siteRoot = siteCache.getSiteWebRootByID(siteLibID, block.getSiteID());
		
		DomainDir dir = siteCache.getDir(dirLibID, block.getDirID());
		String path = siteRoot + dir.getPath();
		
		block.setDir(path); //发布的目录
		
		path += "/" + block.getFileName();
		
		block.setPath(path);//区块的发布路径
	}
}
