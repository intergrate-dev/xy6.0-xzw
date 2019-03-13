package com.founder.xy.block.web;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.web.util.UploadHelper;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.block.BlockArticleManager;
import com.founder.xy.block.BlockManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;

/**
 * 页面区块内容功能
 * @author Deng Chaochen
 */
@Controller
@RequestMapping("/xy/block")
public class BlockArticleController {
	@Autowired
	BlockManager blockManager;
	@Autowired
	ArticleManager articleManager;
	@Autowired
	BlockArticleManager blockArticleManager;
	
	/** 新建或修改 */
	@RequestMapping(value = "BlockArt.do")
	public ModelAndView writeBlockArt(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
		//打开表单时就预取稿件ID
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		Map<String, Object> model = new HashMap<String, Object>();
        //界面上是否显示“编辑样式”按钮
        boolean canEditStyle = "是".equals(InfoHelper.getConfig("写稿", "启用编辑样式"));
        model.put("canEditStyle", canEditStyle);
		model.put("DocLibID", WebUtil.get(request, "DocLibID"));
		model.put("DocIDs", docID);
		model.put("FVID", WebUtil.get(request, "FVID"));
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("blockID", WebUtil.get(request, "groupID")); //前端用groupID传入区块ID
	
		if (docID == 0) {//是否新建区块内容稿件
			model.put("topic", "");
			model.put("url", "");
			model.put("pic", "");
			model.put("subTitle", "");
			model.put("pubTime", "");
			model.put("summary", "");
		} else {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			model.put("topic", doc.getTopic());
			
			if (doc.getDocTypeID() == DocTypes.BLOCKARTICLE.typeID()) {
				model.put("url", doc.get("ba_url"));
				model.put("pic", doc.get("ba_pic"));
				model.put("subTitle", doc.get("ba_subTitle"));
				model.put("pubTime", DateUtils.format(doc.getTimestamp("ba_pubTime"), "yyyy-MM-dd HH:mm:ss"));
				model.put("summary", doc.get("ba_abstract"));
			} else {
				//微信菜单稿件
				model.put("url", doc.get("wx_url"));
				model.put("pic", doc.get("wx_pic"));
				model.put("subTitle", doc.get("wx_subTitle"));
				model.put("pubTime", DateUtils.format(doc.getTimestamp("wx_pubTime"), "yyyy-MM-dd HH:mm:ss"));
				model.put("summary", doc.get("wx_abstract"));
			}
		}

		return new ModelAndView("/xy/block/ArticleForm", model);
	}
	
	/**保存页面区块内容后，自己处理保存后的区块内容的顺序*/
	@RequestMapping(value = "FormSave.do")
	public String formSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		FormSaver formSaver = (FormSaver)Context.getBean(FormSaver.class);
	
		Pair changed;
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");	
		try {
			changed = formSaver.handleChanged(request);
		} catch (Exception e) {
			url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
			return "redirect:" + url;
		}
		String id = request.getParameter("DocID");
		boolean isNew = (StringUtils.isBlank(id) || "0".equals(id));
		
		//读出区块稿件
		long docID = Long.parseLong(changed.getKey());
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document barticle =  docManager.get(docLibID, docID);
		
		if (barticle.getDocTypeID() == DocTypes.BLOCKARTICLE.typeID())
			changeStatus(isNew, docID, barticle);
		else {
			String pic = WebUtil.get(request, "ba_pic");
			changeWeixinStatus(isNew, docID, barticle, pic);
		}
		
		url += "&DocIDs=" + docID;
		return "redirect:" + url;
	}

	/** 打开选取稿件对话框
	 * */
	@RequestMapping(value = "select.do")
	public ModelAndView select(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("siteID", WebUtil.getInt(request, "siteID", 0));
		model.put("docLibID", WebUtil.get(request, "docLibID"));
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("blockID", WebUtil.get(request, "groupID"));
	
		return new ModelAndView("/xy/block/SelectArt", model);
	}

	/** 选稿的保存
	 * artIDs 发布库稿件IDs
	 * blockID 推送的区块ID
	 * */
	@RequestMapping(value = "PushSelect.do")
	public void selectSave(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int  artLibID, @RequestParam String artIDs,
			@RequestParam int blockID) throws Exception {
		long[] aIDs = StringUtils.getLongArray(artIDs);
		
		int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), artLibID);
		int baLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCKARTICLE.typeID(), artLibID);

		//区块
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document block = docManager.get(blockLibID, blockID);
		
		//获取区块内容的ID
		int docTypeID = DocTypes.BLOCKARTICLE.typeID();
		
		//创建区块内容稿件列表
		List<Document> barticles = new ArrayList<Document>();
		StringBuilder newDocIDs = new StringBuilder();
		
		for (int i = 0; i < aIDs.length; i++) {
			long docID = InfoHelper.getNextDocID(docTypeID);
			Document barticle = docManager.newDocument(baLibID, docID);
			
			Document article = docManager.get(artLibID, aIDs[i]);
			
			blockArticleManager.setBlockArt(barticle, article, block);
			
			barticle.set("ba_pubTime", article.get("a_pubTime"));
			barticles.add(barticle);
			
			newDocIDs.append(docID + ",");
		}
		
		if (barticles != null && !barticles.isEmpty()) {
			String message = save(barticles);
			if(message == null){
				//保存成功，若是不处于审核状态需要触发区块更新
/*				if(block.getInt("b_audit") != 1){
					PublishTrigger.block(block.getDocLibID(), block.getDocID());
				}*/
				/*********** 以日志是写到区块内容列表的日志 ********/
				SysUser sysUser = ProcHelper.getUser(request);
				for (int i = 0; i < aIDs.length; i++) {
					LogHelper.writeLog(artLibID, aIDs[i], sysUser, "推送", 
							"推送到【" + block.getString("b_name") + "】");
					writeBALog(sysUser, barticles);
				}
				InfoHelper.outputText("success"+newDocIDs.toString().substring(0, newDocIDs.length() - 1), response);
			}
		}
		InfoHelper.outputText("Failed", response);
	}
	
	/**
	 * 推送区块操作的提交：创建区块内容
	 * articleID 发布库稿件ID
	 * blockids 推送的区块ID
	 * */
	@RequestMapping(value = "PushArticle.do")
	public void pushArticle(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String articleIDs,@RequestParam String blockids ) throws Exception {

		int articleLibID = WebUtil.getInt(request, "docLibID", 0);
		long[] docIDs = StringUtils.getLongArray(articleIDs);
		long[] blockIDs = StringUtils.getLongArray(blockids);
		
		StringBuilder result = new StringBuilder();
		
		SysUser sysUser = ProcHelper.getUser(request);
		for(long articleID : docIDs){
			pushOneArticle(articleLibID, articleID, blockIDs, sysUser, result);
		}

		//判断是否有推送失败的，将信息反馈回去
		if(result.length() == 0){
			result.append("success");
		} else {
			result.append("推送失败");
		}

		InfoHelper.outputText(result.toString(), response);
	}
	/**
	 * 当稿件推送成功之后，写区块内容日志
	 * @param sysUser
	 * @param barticles
	 * @throws Exception
	 */
	public void writeBALog(SysUser sysUser, List<Document> barticles) throws E5Exception {
		for (Document barticle: barticles) {
			LogHelper.writeLog(barticle.getDocLibID(), barticle.getDocID(), sysUser, "推送(区块)", "");		
		}
	}
	
	/**
	 * 区块内容，文件上传
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "Data.do")
	public void uploadPic(HttpServletRequest request, HttpServletResponse response){
		String savePath = null;
		InputStream in = null;
		StorageDevice device = null;
		try {
			FileItem file = UploadHelper.getFileItem(request);
			String reg = "(?i).+?\\.(jpg|jpeg|png|gif|bmp)";

			String fullName = file.getName();
			if(!fullName.matches(reg)){
				InfoHelper.outputText("文件格式错误！", response);
				return;
			}
			// 附件存储设备的名称
			device = InfoHelper.getPicDevice();
			
			// 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
			savePath = InfoHelper.getPicSavePath(request) + fullName.substring(fullName.lastIndexOf("."));
			
			//开始存储到存储设备上
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			in = file.getInputStream();
			sdManager.write(device, savePath, in);
		} catch (Exception e) {
			//0表示失败
			String result = "0;" + e.getLocalizedMessage();
			InfoHelper.outputText(result, response);
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		
		//1表示成功，返回“存储设备;路径”
		String result = "1;" + device.getDeviceName() + ";" + savePath;
		result = result.replaceAll("\\\\", "/");
		InfoHelper.outputText(result, response);
	}
	
	
	/**删除操作：删除页面区块内容、以及删除微信菜单稿件*/
	@RequestMapping(value = {"BlockDelete.do"})
	public void blockDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long blockDocID = 0;
		boolean isBlock = isBlock(docLibID);
		
		boolean bTrigger = false;
		for (long docID : docIDs) {	
			Document doc = docManager.get(docLibID, docID);
			if( 0 == doc.getCurrentNode() ){
				blockDocID = isBlock ? doc.getLong("ba_blockID") : doc.getLong("wx_menuID");
				bTrigger = true;
				break;
			}
		}
		
		String error = delBlockArticles(docLibID,docIDs);
		
		if(error == null && bTrigger){//删除成功，若删除内容中有已发布状态，则需要触发更新区块
			trigger(docLibID, blockDocID, isBlock);
		}
		
		InfoHelper.outputText("@refresh@", response);
	}

	/**审批通过操作：对页面区块内容、微信菜单稿件的审批通过*/
	@RequestMapping(value = {"BlockPass.do"})
	public void blockPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long blockDocID = 0;
		boolean isBlock = isBlock(docLibID);
		
		//创建区块内容稿件列表
		List<Document> barticles = new ArrayList<Document>();
		for (long docID : docIDs) {	
			Document doc = docManager.get(docLibID, docID);
			doc.setCurrentFlow(0);
			doc.setCurrentNode(0);
			doc.setCurrentStatus("已发布");
			blockDocID = isBlock ? doc.getLong("ba_blockID") : doc.getLong("wx_menuID");
			doc.setLocked(false);
			barticles.add(doc);
		}

		//同时提交多个稿件，使用事务
		String message = save(barticles);
		
		if(message == null){
			trigger(docLibID, blockDocID, isBlock);
			
			//为区块添写日志 
			SysUser sysUser = ProcHelper.getUser(request);
			for (Document barticle : barticles) {
				LogHelper.writeLog(docLibID, barticle.getDocID(), sysUser, "审核通过", null);		
			}
		}
		InfoHelper.outputText("@refresh@", response);
	}

	@RequestMapping(value = {"Transfer.do"})
	public String Transfer(HttpServletRequest request, HttpServletResponse response) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String strDocIDs = WebUtil.get(request, "DocIDs");
		long[] docIDs = StringUtils.getLongArray(strDocIDs);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), docLibID);
		long blockDocID = 0;
		boolean isBlock = isBlock(docLibID);

		//创建区块内容稿件列表
		List<Document> barticles = new ArrayList<Document>();
		Boolean isAudit = false;
		if(docIDs.length>0) {
			Document firstDoc = docManager.get(docLibID, docIDs[0]);
			Document block = docManager.get(blockLibID, firstDoc.getLong("ba_blockID"));
			isAudit = block.getInt("b_audit") == 1;
		}
		if(!isAudit) {
			for (long docID : docIDs) {
				Document doc = docManager.get(docLibID, docID);

					doc.setCurrentFlow(0);
					doc.setCurrentNode(0);
					doc.setCurrentStatus("已发布");
					blockDocID = isBlock ? doc.getLong("ba_blockID") : doc.getLong("wx_menuID");
					doc.setLocked(false);
					barticles.add(doc);
				}


			//同时提交多个稿件，使用事务
			String message = save(barticles);

			if(message == null){

				trigger(docLibID, blockDocID, isBlock);

				//为区块添写日志
				SysUser sysUser = ProcHelper.getUser(request);
				for (Document barticle : barticles) {
					LogHelper.writeLog(docLibID, barticle.getDocID(), sysUser, "发布", null);
				}
				InfoHelper.outputText("@refresh@", response);
				return "@refresh@";
			}
		}
		else {
			String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID") + "&DocIDs=" + strDocIDs + "&Info=" + URLEncoder.encode("操作完成", "UTF-8");
			return "redirect:" + url;
		}
		return "@refresh@";
	}



	/**
	 * 稿件撤回
	 * revoke
	 */
	@RequestMapping(value = {"Revoke.do"})
	public void revoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long blockDocID = 0;
		boolean isBlock = isBlock(docLibID);
		

		//创建区块内容稿件列表
		List<Document> barticles = new ArrayList<Document>();
		for (long docID : docIDs) {

			Document doc = docManager.get(docLibID, docID);
			//获得当前稿件所处流程的ID，然后找到这个流程的第一个节点，作为稿件的当前节点，同时设置稿件的当前状态
			Flow[] flows = flowReader.getFlows(doc.getDocTypeID());
			int flowID = 0, flowNodeID = 0;
			String status = null;
			if (flows != null) {
				flowID = flows[0].getID();
				FlowNode[] nodes = flowReader.getFlowNodes(flowID);
				if (nodes != null) {
					flowNodeID = nodes[0].getID();
					status = nodes[0].getWaitingStatus();
				}
			}

			doc.setCurrentFlow(flowID);
			doc.setCurrentNode(flowNodeID);
			doc.setCurrentStatus(status);
			//解锁操作
			doc.setLocked(false);
			blockDocID = isBlock ? doc.getLong("ba_blockID") : doc.getLong("wx_menuID");
			barticles.add(doc);
		}
		//同时提交多个稿件，使用事务
		String message = save(barticles);

		if(message == null){
			trigger(docLibID, blockDocID, isBlock);

			//为区块添写日志
			SysUser sysUser = ProcHelper.getUser(request);
			for (Document barticle : barticles) {
				LogHelper.writeLog(docLibID, barticle.getDocID(), sysUser, "撤稿", null);
			}
		}
		InfoHelper.outputText("@refresh@", response);
	}


	/**
	 * 点开链接查看
	 */
	@RequestMapping(value = {"ViewArticle.do"})
	public String viewArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		
		String url = isBlock(docLibID) ? doc.getString("ba_url") : doc.getString("wx_url");
		
		return "redirect:" + url;
	}

	private void trigger(int docLibID, long blockDocID, boolean isBlock) {
		if (isBlock) {
			int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), docLibID);
			PublishTrigger.block(blockLibID, blockDocID);
		} else {
			int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXMENU.typeID(), docLibID);
			PublishTrigger.wx(blockLibID, blockDocID);
		}
	}

	private void pushOneArticle(int articleLibID, long articleID,
			long[] blockIDs, SysUser sysUser, StringBuilder result)
			throws E5Exception {
		StringBuilder blockName = new StringBuilder();//区块的名字,为后面写日志做准备
		List<Document> barticles = new ArrayList<Document>();//创建区块内容稿件列表
		
		//获取推送的发布库稿件
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document article = docManager.get(articleLibID, articleID);
		
		int baTypeID = DocTypes.BLOCKARTICLE.typeID();
		
		Document[] blocks = new Document[blockIDs.length];
		if (article != null){
			//将这片稿件推送到每个选中的区块中
			
			for (int i = 0; i < blockIDs.length; i++) {
				blocks[i] = blockManager.get(blockIDs[i]);
				
				blockName.append("【" + blocks[i].getString("b_name") + "】");
				
				long baID = InfoHelper.getNextDocID(baTypeID);
				Document barticle = blockArticleManager.getNewDoc(baID);
				
				blockArticleManager.setBlockArt(barticle,article, blocks[i]);
				
				barticle.set("ba_pubTime", article.get("a_pubTime"));
				
				barticles.add(barticle);
			}
		}
	
		//操作成功后写日志
		StringBuilder logDetail = new StringBuilder();
		if (barticles != null && !barticles.isEmpty()) {
			//同时提交多个稿件，使用事务
			String message = save(barticles);
			if (message == null){
				//保存成功，若是不处于审核状态需要触发区块更新
/*				for (int i = 0; i < blockIDs.length; i++) {
					if (blocks[i].getInt("b_audit") != 1){
						PublishTrigger.block(blocks[i].getDocLibID(), blocks[i].getDocID());
					}
				}*/
				writeBALog(sysUser, barticles);//写到区块内容的日志
				
				logDetail.append("推送到：").append(blockName.toString());//写到稿件的日志
		        LogHelper.writeLog(article.getDocLibID(), article.getDocID(), sysUser, "推送(区块)", 
		        		logDetail.toString());
			}else{
				//失败
				result.append("《" + article.getTopic() + "》");
			}
		}		
	}

	/** 多个区块稿件的统一提交， 出错时返回错误信息 */
	private String save(List<Document> articles) {
		if (articles == null || articles.size() == 0) return null;
		
		int docLibID = articles.get(0).getDocLibID();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//同时保存多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();
		
			for (Document article : articles) {
				docManager.save(article, conn);
			}
			
			conn.commitTransaction();
			
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "error";
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**删除 */
	private String delBlockArticles(int docLibID, long[] docIDs) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();	
			for(long docID : docIDs){
				docManager.delete(docLibID, docID, conn);
			}	
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	//若是新区块稿件，需设置顺序、流程状态。必要时触发区块发布
	private void changeStatus(boolean isNew, long docID, Document barticle) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		int blockID = barticle.getInt("ba_blockID");
		if( blockID > 0){
			//读区块是否需审批
			int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.BLOCK.typeID(), barticle.getDocLibID());
			Document block = docManager.get(blockLibID, blockID);
			boolean needAudit = block.getInt("b_audit") == 1;
			
			//新稿件，设置顺序、流程状态
			if (isNew) {
				barticle.set("ba_order", docID);
				
				//根据区块是否审核判断稿件的流程状态
				if (needAudit){
					ProcHelper.initDoc(barticle);
				}else{
					barticle.setCurrentFlow(0);
					barticle.setCurrentNode(0);
					barticle.setCurrentStatus("已发布");
				}
				docManager.save(barticle);
			}
			
			//保存成功，若是不处于审核状态需要触发区块更新
			if (!needAudit){
				PublishTrigger.block(block.getDocLibID(), block.getDocID());
			}
		}
	}
	
	//微信稿件，需设置顺序、流程状态。必要时触发发布
	private void changeWeixinStatus(boolean isNew, long docID, Document barticle, String pic) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		int blockID = barticle.getInt("wx_menuID");
		if( blockID > 0){
			//读微信菜单是否需审批
			int blockLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXMENU.typeID(), barticle.getDocLibID());
			Document block = docManager.get(blockLibID, blockID);
			boolean needAudit = block.getInt("wxm_audit") == 1;
			
			//新稿件，设置顺序、流程状态
			if (isNew) {
				barticle.set("wx_order", docID);
				
				//根据区块是否审核判断稿件的流程状态
				if (needAudit){
					ProcHelper.initDoc(barticle);
				}else{
					barticle.setCurrentFlow(0);
					barticle.setCurrentNode(0);
					barticle.setCurrentStatus("已发布");
				}
			}
			barticle.set("wx_pic", pic);
			docManager.save(barticle);
			
			//保存成功，若是不处于审核状态需要触发菜单更新
			if (!needAudit){
				PublishTrigger.wx(block.getDocLibID(), block.getDocID());
			}
		}
	}

	private boolean isBlock(int docLibID) {
		return DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.BLOCKARTICLE.typeID();
	}
}