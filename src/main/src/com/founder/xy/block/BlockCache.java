package com.founder.xy.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;

/**
 * 页面区块缓存，供发布服务使用
 * @author Gong Lijie
 */
public class BlockCache extends BaseCache {
	//<blockLibID, <blockID, Block>>
	private Map<Integer, Map<Long, Block>> blocks = new HashMap<>();

	@Override
	protected int getDocTypeID() {
		return DocTypes.BLOCK.typeID();
	}

	@Override
	public void refresh(int docLibID) throws E5Exception {
		BlockManager blockManager = (BlockManager)Context.getBean("blockManager");
		List<Block> blocks = blockManager.getBlocks(docLibID);
		
		Map<Long, Block> oneCache = new HashMap<>();
		for (Block block : blocks) {
			oneCache.put(block.getId(), block);
		}
		this.blocks.put(docLibID, oneCache);
	}

	public Block get(int docLibID, long blockID) {
		Map<Long, Block> oneLib = blocks.get(docLibID);
		if (oneLib == null) return null;
		
		Block block = oneLib.get(blockID);
		if (block == null) return null;
		
		return block.clone();
	}
}
