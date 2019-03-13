package com.founder.xy.api.jabbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.context.E5Exception;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.param.ProcParam;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.system.Tenant;

@Controller
@RequestMapping("/xy/article")
public class JabbarOpenEditor {

	@Autowired
	private JabbarApiManager jabbarApiManager;
	private static final Logger log = LoggerFactory.getLogger(DwApiController.class);
	
	//大体积的稿件通过FTP方式处理
	//OperateType=0  打开编辑器
	@RequestMapping(value = "JabbarOpenEditor.do",params="OperateType=0")
	public ModelAndView ftpWriteArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.error("-----快闪开，JabbarOpenEditor.java：46要打开编辑器了---");
		
		int docLibID = LibHelper.getLibID(DocTypes.ARTICLE.typeID(), Tenant.DEFAULTCODE);
		int FVID= DomHelper.getFVIDByDocLibID(docLibID);
		request.setAttribute("FVID", FVID);
		String articleUUID = UUID.randomUUID().toString();
		request.setAttribute("UUID", articleUUID);		
		String type = request.getParameter("Type");
		
		boolean isNew = true; // 是否新写稿
		
		Map<String, Object> model = new HashMap<>();
		//构造稿件
		List<com.founder.e5.doc.Document> attachmentList = new ArrayList<>();
		com.founder.e5.doc.Document articleDoc = jabbarApiManager.assembleNewArticle(request);
		log.error("-----快闪开，JabbarOpenEditor.java：60要保存图片了---");
		jabbarApiManager.saveAttachment(request, attachmentList, articleDoc);
		if("1".equals(type)){
			jabbarApiManager.saveAttachmentPic(attachmentList, articleDoc);
		}
		Article article = new Article(articleDoc);
		article.setStatus(0);		
		List<Channel> chs = jabbarApiManager.getChannels(isNew, article.getChannel());
		
		ProcParam param = ProcHelper.prepareProcParam(articleDoc,"写稿");
		request.getSession().setAttribute(articleUUID,param);
		if(article.getChannel()==0){
			model.put("groupID",articleDoc.getInt("a_catID"));
		}
		model.put("channels", chs);
		model.put("channelCount", chs.size());
		model.put("isNew", isNew);
		model.put("siteID", WebUtil.get(request, "SiteID"));
		model.put("UUID", articleUUID);
		model.put("article", article);
		model.put("ch", article.getChannel());
		model.put("sessionID", request.getSession().getId()); // 解决Firefox下flash上传控件session丢失
		model.put("jabbarArticle", "true");

		jabbarApiManager.putConfigParam(model);
		
		return new ModelAndView("/xy/article/Article", model);
		
	}
	
	// OperateType=1 直接入库
	@RequestMapping(value = "JabbarOpenEditor.do",params="OperateType=1")
	public void ftpPublish(HttpServletRequest request, HttpServletResponse response)
			throws E5Exception, DocumentException, IOException {
			String result = jabbarApiManager.publish(request);
			if (!"发布成功".equals(result))
				InfoHelper.outputText(result, response);
	}

	
}