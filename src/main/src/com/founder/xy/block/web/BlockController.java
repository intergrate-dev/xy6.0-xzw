package com.founder.xy.block.web;

import com.founder.e5.cat.Category;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.block.BlockArticleManager;
import com.founder.xy.block.BlockManager;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.JsoupHelper;
import com.founder.xy.jpublish.page.BlockGenerator;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;


/**
 * 页面区块功能
 *
 * @author Deng Chaochen
 */
@Controller
@RequestMapping("/xy/block")
public class BlockController {
    @Autowired
    BlockManager blockManager;
    @Autowired
    ArticleManager articleManager;
    @Autowired
    BlockArticleManager blockArticleManager;

    //获取某个站点的所有区块
    @RequestMapping(value = "Block.do")
    public void tree(HttpServletRequest request, HttpServletResponse response,
            @RequestParam int siteID) throws Exception {

        //获取siteID站点下的所有区块列表
    	int blockLibID = LibHelper.getLibID(DocTypes.BLOCK.typeID(), InfoHelper.getTenantCode(request));
        Document[] docblocks = blockManager.getBlocks(blockLibID, siteID);
        //获取区块分组类型
        int catType = CatTypes.CAT_BLOCK.typeID();

        //获取区块分组
        Category[] groups = InfoHelper.getCatGroups(request, catType, siteID);
        JSONArray blocks = getJSONArr(siteID, groups, docblocks);

        InfoHelper.outputJson(blocks.toString(), response);
    }

    //区块预览
    @RequestMapping(value = "Preview.do")
    public ModelAndView Preview(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam int DocLibID, @RequestParam int DocIDs) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        DocIDMsg data = new DocIDMsg(DocLibID, DocIDs, null);
        BlockGenerator generator = new BlockGenerator();
        String page = generator.preview(data);
        if (page != null) model.put("page", JsoupHelper.replaceImgSuffix(page));
        return new ModelAndView("xy/article/BlockPreview", model);
    }

    //获取某个站点的所有区块-权限
    @RequestMapping(value = "Block.do", params = "Op")
    public void treeOp(HttpServletRequest request, HttpServletResponse response,
            @RequestParam int siteID) throws Exception {
        //获取siteID站点下的所有区块列表
        Document[] docblocks = blockManager.getBlock(siteID, ProcHelper.getUserID(request), 3);
        //获取区块分组类型
        int catType = CatTypes.CAT_BLOCK.typeID();

        //获取区块分组
        Category[] groups = InfoHelper.getCatGroups(request, catType, siteID);
        JSONArray blocks = getJSONArr(siteID, groups, docblocks);

        InfoHelper.outputJson(blocks.toString(), response);
    }

    /**
     * 将获取到的区块列表转化为JSON
     *
     * @param groups    区块分组（作为区块树的第一级）
     * @param siteID    站点ID
     * @param docblocks 符合条件的区块列表
     * @throws E5Exception
     */
    private JSONArray getJSONArr(int siteID, Category[] groups, Document[] docblocks) throws E5Exception {

        JSONArray blocks = new JSONArray();
        String catIDAppend = "_group";
        Map<String, Map<String, String>> blockMap = new HashMap<>();
        Map<String, String> json;
        //将分组（树的根目录）加入到json数据
        if(groups!=null) {
            for (Category group : groups) {
                json = new HashMap<>();
                //判断该分组下是否有页面区块，若没有页面区块，就不需要将其加入到显示的json数据中
                boolean bHas = blockManager.hasChild(siteID, group.getCatID());
                //若该目录下有页面区块则加入到json数据
                if (bHas) {
                    json.put("id", String.valueOf(group.getCatID()) + catIDAppend);
                    json.put("name", InfoHelper.filter4Json(group.getCatName()));
                    json.put("pid", "0");
                    //blocks.add(json);
                    blockMap.put(String.valueOf(group.getCatID()) + catIDAppend, json);
                }
            }
        }
        //将符合条件的页面区块加入到json数据
        for (Document block : docblocks) {
            json = new HashMap<>();

            json.put("id", String.valueOf(block.getDocID()));
            StringBuilder result = new StringBuilder();
            result.append(InfoHelper.filter4Json(block.getString("b_name")));
            if (block.getInt("b_channel") == 0) {
                result.append(" ( ").append(InfoHelper.filter4Json("网站 "));
            } else if (block.getInt("b_channel") == 1) {
                result.append(" ( ").append(InfoHelper.filter4Json("触屏 "));
            }
            if (block.getInt("b_audit") == 1) {
                result.append("审");
            }
            result.append(" ) ");
            json.put("name", result.toString());
            json.put("pid", InfoHelper.filter4Json(block.getString("b_groupID") + catIDAppend));

            blocks.add(json);
            if (blockMap.containsKey(json.get("pid"))) {
                if(!blocks.contains(blockMap.get(json.get("pid"))))
                    blocks.add(blockMap.get(json.get("pid")));
            }
        }

        return blocks;
    }

    //区块查找功能
    @RequestMapping(value = "Find.do")
    public void find(HttpServletRequest request, HttpServletResponse response,
            @RequestParam int siteID, @RequestParam String q) throws Exception {

        Document[] blocks = blockManager.find(siteID, q);
        String result = json(blocks);
        InfoHelper.outputJson(result, response);
    }

    //查找结果的json，格式为[{key,value},{key,value},...]
    private String json(Document[] blocks) throws E5Exception {
        StringBuilder result = new StringBuilder();
        result.append("[");

        for (Document block : blocks) {
            if (result.length() > 1) result.append(",");

            result.append("{\"value\":\"").append(InfoHelper.filter4Json(block.getString("b_name")))
                    .append("\",\"key\":\"").append(String.valueOf(block.getDocID()))
                    .append("\"}");
        }
        result.append("]");

        return result.toString();
    }
    
  //区块删除功能
    @RequestMapping(value = "Delete.do")
    public String delete(HttpServletRequest request, HttpServletResponse response){
    	int blockLibID = WebUtil.getInt(request, "DocLibID", 0);
    	String strBlockID = WebUtil.get(request, "DocIDs");
    	long[] blockIDs = StringUtils.getLongArray(strBlockID);
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
    	String condition = "co_templateID=? and co_templateType=1";
    	int coLibID = LibHelper.getComponentObjLibID();
    	for(long blockID : blockIDs){
    		try {
    			Document blockDoc = docManager.get(blockLibID, blockID);
    			//暂时置空模板代码
//    			blockDoc.set("b_template", " ");
//    			blockDoc.set("b_templateParsed", " ");
    			blockDoc.setDeleteFlag(1);
    			docManager.save(blockDoc);
//    			docManager.delete(blockDoc);
    			
    			//删除对应的组件
    			Document[] coDocs = docManager.find(coLibID, condition, new Object[]{blockID});
    			if (coDocs!=null) {
    				for(Document coDoc : coDocs) {
//    					coDoc.setDeleteFlag(1);
//    					docManager.save(coDoc);
    					docManager.delete(coDoc);
    				}
    			}
    			PublishTrigger.blockRevoke(blockLibID, blockID);
    		} catch (E5Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return "redirect:/e5workspace/after.do"
    			+ "?UUID=" + WebUtil.get(request, "UUID") 
    			+ "&DocIDs=" + strBlockID;
    }
}