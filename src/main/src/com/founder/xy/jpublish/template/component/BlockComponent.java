package com.founder.xy.jpublish.template.component;

import org.json.JSONObject;

import com.founder.e5.context.CacheReader;
import com.founder.xy.block.Block;
import com.founder.xy.block.BlockCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsonHelper;
import com.founder.xy.system.site.DomainDir;

/**
 * 区块组件，替换成<!--#include virtual=""-->
 * @author Gong Lijie
 */
public class BlockComponent extends AbstractComponent implements Component {

	public BlockComponent(ColParam param, JSONObject coJson) {
		super(coJson);
		
		this.param = param;
	}

	@Override
	public String getComponentResult() throws Exception {
		int blockID = JsonHelper.getInt(dataJSON, "blockid");
		
		int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), param.getColLibID());

		BlockCache cache = (BlockCache) (CacheReader.find(BlockCache.class));
		if (cache == null) return ""; //预览时web中无BlockCache，不显示区块了吧
		
		Block block = cache.get(blockLibID, blockID);
		
		if (block == null) {
			return "";
		} else {
			String path = getBlockDir(block);
			return "<!--#include virtual=\"" + path + "\"-->";
		}
	}

	//区块的发布地址
	private String getBlockDir(Block block){
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), param.getColLibID());
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		DomainDir dir = siteCache.getDir(libID, block.getDirID());
		if (dir == null) return "";
		
		//去掉域名根目录
		String root = dir.getPath();
		if (root.lastIndexOf("/") == 0) //只有一个/，表示是根目录:/hold
			root = "";
		else //目录不是根目录，/hold/block
			root = root.substring(root.indexOf("/", 1));
		
		String path = root + "/" + block.getFileName();
		
		return path;
	}
	@Override
	protected void getComponentData() {
		
	}
}
