package com.founder.xy.column.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 首页推荐模块各功能
 */
@Controller
@RequestMapping("/xy/module")
public class ModuleController {

	/**
     * 根据推荐模块groupID生成各自的推荐模块项页面
     *
     */
    @RequestMapping(value = "ModuleSubmit.do")
    public String ModuleSubmit(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocID", 0);
        String uuid = WebUtil.get(request, "UUID");

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc;
        if (docID <= 0) {
        	docID = InfoHelper.getNextDocID(DocTypes.COLMODULE.typeID());
            doc = docManager.newDocument(docLibID, docID);
            ProcHelper.initDoc(doc, request);
        } else {
            doc = docManager.get(docLibID, docID);
        }

        FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
        saver.handle(doc, request);

		RedisManager.clear(RedisKey.APP_MODULE_LIST + doc.getString("cm_columnID"));
		RedisManager.clear(RedisKey.APP_MODULEITEM_LIST + docID);
		
        String url = "redirect:/e5workspace/after.do?UUID=" + uuid
                + "&DocIDs=" + docID;
        return url;
    }
    /**
     * 根据推荐模块groupID生成各自的推荐模块项页面
     *
     */
    @RequestMapping(value = "ModuleItem.do")
    public String ModuleItem(HttpServletRequest request, HttpServletResponse response, 
    		Model model) throws Exception {
        int itemLibID = WebUtil.getInt(request, "DocLibID", 0);
        long moduleID = WebUtil.getLong(request, "groupID", 0);
		
		//推荐模块
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int moduleLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLMODULE.typeID(), itemLibID);
		Document module = docManager.get(moduleLibID, moduleID);
		
		String type = module.getString("cm_type");
		int targetColID = module.getInt("cm_targetColumnID");
		
		//从栏目得到siteID
		int colID = module.getInt("cm_columnID");
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), itemLibID);
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		Column col = colReader.get(colLibID, colID);
		int siteID = col.getSiteID();
		
		model.addAttribute("siteID", siteID);
		model.addAttribute("docLibID", itemLibID);
		model.addAttribute("moduleID", moduleID);
		model.addAttribute("type", type);
		model.addAttribute("targetColID", targetColID);
		
		model.addAttribute("UUID", WebUtil.get(request, "UUID"));
		
        return "/xy/column/ModuleItem";
    }
    
    /**
     * 储存新建模块项
     */
    @RequestMapping(value = "SaveModuleItem.do")
    public void ModuleItemSubmit(HttpServletRequest request, HttpServletResponse response,
    		int itemLibID, int docLibID, String docIDs, int type, int moduleID)
            throws Exception {
        SysUser sysUser = ProcHelper.getUser(request);
		long[] ids = StringUtils.getLongArray(docIDs);
        
        DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			if (type == 1|| type==5) {
				//选栏目时，没法传回docLibID，因此取一次
				docLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), itemLibID);
			}
			//逐个读出选择的稿件/栏目/问吧等，把属性写入新创建的模块项
			for (long docID : ids) {
				long itemID = InfoHelper.getNextDocID(DocTypes.COLMODULEITEM.typeID());
				Document itemdoc = docManager.newDocument(itemLibID, itemID);
	            ProcHelper.initDoc(itemdoc);
	            
	            itemdoc.set("SYS_AUTHORS", sysUser.getUserName());
	            itemdoc.set("cmi_moduleID", moduleID);
	            itemdoc.set("cmi_type",type);
	            itemdoc.set("cmi_targetID",docID);
	            itemdoc.set("cmi_order", itemID);
	            
				Document doc = docManager.get(docLibID, docID);
				if (type == 1|| type==5) {
					itemdoc.set("cmi_name", doc.get("col_name")); //栏目名
				} else {
					String topic = doc.getString("SYS_TOPIC")
							.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
							.replaceAll("</[a-zA-Z]+[1-9]?>", "") ;
					itemdoc.set("cmi_name", topic);
				}
	            docManager.save(itemdoc);
			}
			//清空模块项redis
			String key = RedisKey.APP_MODULEITEM_LIST + moduleID;
			RedisManager.clear(key);
			
			//写推荐模块的日志
			int moduleLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLMODULE.typeID(), itemLibID);
			LogHelper.writeLog(moduleLibID, moduleID, sysUser, "添加模块项", "添加个数：" + ids.length);
			
			InfoHelper.outputText("ok", response);
        }catch (Exception e){
    		InfoHelper.outputText(e.getLocalizedMessage(), response);
        }
    }
    /**
     * 推荐模块删除
     *
     */
    @RequestMapping(value = "ModuleDelete.do")
    public String ModuleDelete(HttpServletRequest request, HttpServletResponse response,String UUID)
            throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);

        deleteModule(docLibID,docID);
        String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + String.valueOf(docID);
        return "redirect:" + url;
    }
    /**
     * 删除模块项
     *
     */
    @RequestMapping(value = "ModuleItemDelete.do")
    public String ModuleItemDelete(HttpServletRequest request, HttpServletResponse response,String UUID)
            throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String strDocIDs = WebUtil.get(request, "DocIDs");
        long[] docIDs = StringUtils.getLongArray(strDocIDs);

        deleteModItem(request,docLibID,docIDs);
        String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + strDocIDs;
        return "redirect:" + url;
    }

  //删除推荐模块
  	public void deleteModule(int docLibID,long docID) throws E5Exception{
  		DocumentManager docManager = DocumentManagerFactory.getInstance();
  		Document doc = docManager.get(docLibID, docID);
  		String columnID=doc.getString("cm_columnID");

  		docManager.delete(doc);
  		RedisManager.clear(RedisKey.APP_MODULE_LIST+columnID);
  		
  		//同时删除模块项
  		int itemDocLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLMODULEITEM.typeID(), docLibID);
  		String sql="delete from "+LibHelper.getLibTable(itemDocLibID)+" where cmi_moduleID = ?";
  		
  		DBSession conn = null;
  		try{
  			conn = Context.getDBSession() ;
  			conn.executeUpdate(sql, new Object[]{docID}) ;
  		}catch(Exception e){
  			e.printStackTrace();
  		} finally {
  			ResourceMgr.closeQuietly(conn);
  		}
  	}

      //删除推荐模块项
      public void deleteModItem(HttpServletRequest request,int docLibID,long[] docIDs) throws E5Exception{
          int groupID = WebUtil.getInt(request, "groupID", 0); ;
          SysUser sysUser = ProcHelper.getUser(request);
          DocumentManager docManager = DocumentManagerFactory.getInstance();
          for(long docID : docIDs){
  	        Document doc = docManager.get(docLibID, docID);
  	        docManager.delete(doc);
          }
          LogHelper.writeLog(LibHelper.getLibID(DocTypes.COLMODULE.typeID(), Tenant.DEFAULTCODE),groupID,sysUser,"删除模块项","");
          RedisManager.clear(RedisKey.APP_MODULEITEM_LIST+groupID);  //清空模块项redis
      }
}
