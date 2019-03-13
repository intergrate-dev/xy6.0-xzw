package com.founder.xy.nis.web;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.xy.commons.LibHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.system.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.nis.ForumManager;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

@Controller
@RequestMapping("/xy/nis")
public class ForumController {
    @Autowired
    ForumManager forumManager;

    /**
     * 互动数据（如直播、话题等）审核通过
     */
    @RequestMapping(value = {"ForumPass.do"})
    public String forumPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String DocIDs = request.getParameter("DocIDs");
        long[] docIDs = StringUtils.getLongArray(DocIDs);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        
        int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);

        boolean isSubjectQA = docTypeID == DocTypes.SUBJECTQA.typeID();
        boolean noChildren = docTypeID == DocTypes.QA.typeID()
        		|| docTypeID == DocTypes.ACTIVITY.typeID(); //无子帖的类型：互动问答、互动活动
        
        String resultIDs = "";
        String failedIDs = "";
		// 创建列表
		List<Document> forums = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document forum = docManager.get(docLibID, docID);

			/**判断是否抽图完毕，生成trans信息文件*/
			int result = forumManager.transWhenPass(forum);
			if (result != 0) {
				if (failedIDs.length() > 0) failedIDs += ",";
				failedIDs += docID;
				
				continue; // 未能发布图片
			}
			
			if (!noChildren) {
				//审批通过，则增加子帖数
				addCount(docLibID, isSubjectQA, forum);
			}
			
			forum.set("a_status", 1);// 设置a_status状态为1，通过
			forums.add(forum);

			if (resultIDs.length() > 0) resultIDs += ",";
			resultIDs += docID;
		}

		String message = forumManager.save(docLibID, forums);
		
		clearRedis(docLibID, forums, true);


        //更改状态成功，生成活动页
		if(docTypeID == DocTypes.ACTIVITY.typeID()&&message==null){
            generateActivityHtml(forums);
		}


		// 调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		if (message == null) {
			url += "&DocIDs=" + resultIDs; // 操作成功

		}
		String info = "";
		if (failedIDs.length() > 0)
			info = "不允许操作（id：" + failedIDs + "）。请检查是否未完成抽图。";
		if (message != null) {
			info += message;
		}
		if (info.length() > 0)
			url += "&Info=" + URLEncoder.encode(info, "UTF-8");// 有错误，需返回前台做提示
		return "redirect:" + url;
	}

    private void generateActivityHtml(List<Document> forms) {
        for(Document form : forms){
            StringBuilder stringHtml = new StringBuilder();
            FileOutputStream fileOutputStream = null;
            try{
                //获取资源目录配置
                long siteID = form.getLong("a_siteID");
                int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
                DocumentManager docManager = DocumentManagerFactory.getInstance();
                Document siteDoc = docManager.get(siteLibID,siteID);
                String rootPath = "";
                String rootUrl = "";
                if(siteDoc!=null){
                    rootPath = siteDoc.getString("site_resPath");
                    rootUrl = siteDoc.getString("site_resUrl");
                }

                //获得自定义日期路径
                Date createDate = form.getDate("SYS_CREATED");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                String yyyyMM = sdf.format(createDate);
                SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
                String day = sdfDay.format(createDate);

                //拼接url及存储路径
                long docID = form.getDocID();
                String osName = System.getProperties().getProperty("os.name");
                String directoryPath = "";
                String filePath = "";
                String theShareUrl = "";
                if(osName.indexOf("Window")!=0){//linux
                    directoryPath = rootPath+"/activity/"+yyyyMM+"/"+day;
                    filePath = directoryPath+"/"+"activity_"+docID+".html";
                    theShareUrl = rootUrl+"/activity/"+yyyyMM+"/"+day+"/"+"activity_"+docID+".html";
                }else{//windows
                    rootPath = "c:";
                    directoryPath = rootPath+"\\activity\\"+yyyyMM+"\\"+day;
                    filePath = directoryPath+"\\"+"activity_"+docID+".html";
                }

                //生成html文件
                File Directory = new File(directoryPath);
                if (!Directory.exists()) {
                    Directory.mkdirs();
                }

                File file = new File(filePath);
                fileOutputStream = new FileOutputStream(file);
                PrintStream printStream = new PrintStream(fileOutputStream);

                String title = form.getString("SYS_TOPIC");
                String description = form.getString("A_ABSTRACT");
                int docLibID = form.getDocLibID();
                String url = getNisPicUrl(docID, docLibID);
                if(StringUtils.isBlank(url)){
                    url = "https://appif.sinchew.com.my/resource/m-header-logo.png";
                }

                stringHtml.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
                stringHtml.append("\n<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                stringHtml.append("\n<head>");
                stringHtml.append("\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
                stringHtml.append("\n<meta property=\"og:title\" content=\"").append(title).append("\"/>");
                stringHtml.append("\n<meta property=\"og:description\" content=\"").append(description).append("\">");
                stringHtml.append("\n<meta property=\"og:image\" content=\"").append(url).append("\"/>");
                stringHtml.append("\n<meta property=\"og:image:width\" content=\"1200\" />");
                stringHtml.append("\n<meta property=\"og:image:height\" content=\"628\"/>");

                stringHtml.append("\n<meta name=\"twitter:title\" content=\"").append(title).append("\"/>");
                stringHtml.append("\n<meta name=\"twitter:description\" content=\"").append(description).append("\">");
                stringHtml.append("\n<meta name=\"twitter:image\" content=\"").append(url).append("\"/>");
                stringHtml.append("\n<meta name=\"twitter:card\" content=\"summary_large_image\">");
                stringHtml.append("\n<meta name=\"twitter:image:width\" content=\"1200\" />");
                stringHtml.append("\n<meta name=\"twitter:image:height\" content=\"628\"/>");
                stringHtml.append("\n<meta name=viewport content=\"width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no\"/>");
                stringHtml.append("\n<title>").append(title).append("</title>");
                stringHtml.append("\n</head>\n</html>");

                String jsrootUrl = InfoHelper.getConfig("互动","外网资源地址");
                stringHtml.append("\n<script src=\"").append(jsrootUrl).append("common.js\"></script>");

                stringHtml.append("\n<script>");
                stringHtml.append("\nif (window.location.href.split('?')[1]) {\n" +
                        "        var shareUrl = window.location.href.split('?')[1].split('&');\n" +
                        "        var shareObj = {};\n" +
                        "        for (var i = 0; i < shareUrl.length; i++) {\n" +
                        "            var sarr = shareUrl[i].split('=');\n" +
                        "            shareObj[sarr[0]] = sarr[1];\n" +
                        "        }\n" +
                        "        switch (shareObj.type) {\n" +
                        "            case 'detailActivity':\n" +
                        "                window.location.href = '"+jsrootUrl+"pad/index.html#/' + shareObj.type + '/' + shareObj.fileId;\n" +
                        "                break;\n" +
                        "        }\n" +
                        "    }");

                stringHtml.append("\n</script>");

                //将HTML文件内容写入文件中
                printStream.println(stringHtml.toString());

                //trans：生成分发信息文件
                if(osName.indexOf("Window")!=0) {//linux
                    String root = InfoHelper.getConfig("发布服务", "发布根目录");
                    PublishHelper.writeTransPath(filePath, root);
                }
                //生成文件后修改数据库中存储
                updateActivityShareUrl(theShareUrl,docID,docLibID);

            }catch(FileNotFoundException e){
                e.printStackTrace();
                System.out.println("生成活动分享页失败！");
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("生成活动分享页失败！");
            }finally {
                if(fileOutputStream!=null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateActivityShareUrl(String theShareUrl, long docID, int docLibID) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document activity = docManager.get(docLibID,docID);
            if(activity!=null){
                activity.set("a_shareUrl",theShareUrl);
                docManager.save(activity);
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
    }

    private String getNisPicUrl(long docID, int docLibID) {
//        List<Document> documents = do
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), docLibID);
        DocumentManager documentManager = DocumentManagerFactory.getInstance();
        try {
            Document[] attDocs = documentManager.find(attLibID,
                    "att_articleID=? and att_articleLibID=? and att_url is not null and att_type in (3,4,5) order by att_type asc limit 1",
                    new Object[]{docID, docLibID});
            if(attDocs.length==0){
                return "";
            }else{
                return attDocs[0].getString("att_url");
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 不通过
     */
    @RequestMapping(value = {"ForumReject.do"})
    public String forumReject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String DocIDs = request.getParameter("DocIDs");
        long[] docIDs = StringUtils.getLongArray(DocIDs);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);

        List<Document> forums = new ArrayList<Document>();
        for (long docID : docIDs) {
            Document forum = docManager.get(docLibID, docID);
            //设置a_status状态为2，不通过
            forum.set("a_status", 2);
            forums.add(forum);
        }
        String message = forumManager.save(docLibID, forums);
        
        //调用after.do进行后处理：改变流程状态、解锁、刷新列表
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
        if (message == null) {
            url += "&DocIDs=" + DocIDs; //操作成功
        } else {
            url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示
        }
        return "redirect:" + url;
    }

    /**
     * 论坛、直播、话题删除
     */
    @RequestMapping(value = "ForumDelete.do")
	public void forumDelete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String DocIDs = request.getParameter("DocIDs");
		long[] docIDs = StringUtils.getLongArray(DocIDs);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
        int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		
		boolean isSubjectQA = docTypeID == DocTypes.SUBJECTQA.typeID();
        boolean noChildren = docTypeID == DocTypes.QA.typeID()
        		|| docTypeID == DocTypes.ACTIVITY.typeID(); //无子帖的类型：互动问答、互动活动
        
		List<Document> forums = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document forum = docManager.get(docLibID, docID);
			
			//若是已通过审核的，则删除时应减少子帖数
			if (!noChildren && forum.getInt("a_status") == 1) {
				decreaseCount(docLibID, isSubjectQA, forum);
			}
			forums.add(forum);
		}
		// 删除帖子
		String error = forumManager.delete(docLibID, forums);
		
		// 若删除的是已通过的帖子，则清理Redis里的列表缓存
		clearRedis(docLibID, forums, false);
		
		if (error == null) {
			InfoHelper.outputText("@refresh@", response); // 操作成功
		} else {
			InfoHelper.outputText("@refresh@" + error, response); // 操作成功
		}
	}
    
	/**
	 * 撤回
	 */
	@RequestMapping(value = {"ForumRevoke.do"})
	public String forumRevoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    String DocIDs = request.getParameter("DocIDs");
	    long[] docIDs = StringUtils.getLongArray(DocIDs);
	    int docLibID = WebUtil.getInt(request, "DocLibID", 0);
	
        int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		
		boolean isSubjectQA = docTypeID == DocTypes.SUBJECTQA.typeID();
        boolean noChildren = docTypeID == DocTypes.QA.typeID()
        		|| docTypeID == DocTypes.ACTIVITY.typeID(); //无子帖的类型：互动问答、互动活动
        
		List<Document> forums = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document forum = docManager.get(docLibID, docID);
			if (!noChildren) {
				//减少子帖数
				decreaseCount(docLibID, isSubjectQA, forum);
			}
			
			forum.set("a_status", 0);
			forums.add(forum);
		}
	
		String message = forumManager.save(docLibID, forums);
		
		//清理缓存
		clearRedis(docLibID, forums, true);


        //撤回活动成功，删除生成的活动页
        if(docTypeID == DocTypes.ACTIVITY.typeID()&&message==null){
            deleteActivityHtml(forums);
        }

		
		// 调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (message == null) {
			url += "&DocIDs=" + DocIDs; // 操作成功
		} else {
			url += "&Info=" + URLEncoder.encode(message, "UTF-8");// 有错误，需返回前台做提示
		}
		return "redirect:" + url;
	}

    private void deleteActivityHtml(List<Document> forms) {
            for(Document form : forms){
                    long docID = form.getDocID();
                    int docLibID = form.getDocLibID();
                    updateActivityShareUrl("",docID,docLibID);
            }
    }

    /**
     * 论坛加精、直播加精
     */
    @RequestMapping(value = "ForumGood.do")
    public String forumGood(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return forumGood(request, 1);
    }

	/**
     * 论坛、直播取消加精
     */
    @RequestMapping(value = "ForumGoodCancel.do")
    public String forumGoodCancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return forumGood(request, 0);
    }

    /**
     * 置顶操作： 帖子的a_order默认与ID相等。 字段a_order是一个19位的长整数。
     * 1. 设为置顶时，在这个长整数的最高位（十进制）设置1，即A=100000000<id>
     * 2. 并查库取出B=max(a_order)+1，
     * 3. 取A和B的较大者作为新的a_order。
     * <p/>
     * notice: 界面是MainTopic.jsp论坛的置顶操作中，只有主贴才可以置顶，判断a_parentID=0
     */
    @RequestMapping("ForumTop.do")
    public String moveTop(HttpServletResponse response,
            int DocLibID, long DocIDs, String UUID)
            throws UnsupportedEncodingException, E5Exception {

        //order常量
        BigDecimal ORDER_CONSTANT = new BigDecimal("1000000000000000000");
        //获得相应的formDoc
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document forumDoc = docManager.get(DocLibID, DocIDs);
        String _order = forumDoc.getString("a_order");
        long parentID = forumDoc.getLong("a_parentID");

        //不是主贴不能置顶
        if (parentID > 0){
            InfoHelper.outputText("不是主帖，不能置顶！", response);
            return "/e5workspace/after.do?UUID=" + UUID;
        }

        BigDecimal a_order;
        //如果order为空，把order设为 docId
        a_order = _order == null || "0".equals(_order) ? BigDecimal.valueOf(forumDoc.getDocID()) : new BigDecimal(_order);
        //1. 设为置顶时，在这个长整数的最高位（十进制）设置1，即A=100000000<id>
        if (a_order.compareTo(ORDER_CONSTANT) < 0)
            a_order = a_order.add(ORDER_CONSTANT);
        //2. 并查库取出B=max(a_order)+1
        //3. 取A和B的较大者作为新的a_order。
        a_order = forumManager.getMaxOrder(DocLibID, a_order);

        //重新保存
        forumDoc.set("a_order", a_order);
        docManager.save(forumDoc);

        //清理Redis
        List<Document> forums = new ArrayList<Document>();
        forums.add(forumDoc);
        clearRedis(DocLibID, forums, false);
        
        //刷新列表
        String url = "/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + DocIDs + "&DocLibID=" + DocLibID;
        return "redirect:" + url;
    }

    /**
     * 取消置顶操作：
     * 把a_order重新=SYS_DOCUMENTID
     */
    @RequestMapping("ForumTopCancel.do")
    public String topCancel(int DocLibID, long DocIDs,
            String UUID) throws UnsupportedEncodingException, E5Exception {
    	
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document forumDoc = docManager.get(DocLibID, DocIDs);
		if (forumDoc != null) {
			forumDoc.set("a_order", forumDoc.getDocID());
			docManager.save(forumDoc);
			
	        //清理Redis
	        List<Document> forums = new ArrayList<Document>();
	        forums.add(forumDoc);
	        clearRedis(DocLibID, forums, false);
		}
		
        //刷新列表
        String url = "/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + DocIDs + "&DocLibID=" + DocLibID;
        return "redirect:" + url;
    }

	private String forumGood(HttpServletRequest request, int goodFlag)
			throws E5Exception, UnsupportedEncodingException {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
	    String DocIDs = request.getParameter("DocIDs");
	    long[] docIDs = StringUtils.getLongArray(DocIDs);
	    int docLibID = WebUtil.getInt(request, "DocLibID", 0);
	
	    //设置加精标记
	    List<Document> forums = new ArrayList<Document>();
	    for (long docID : docIDs) {
	        Document forum = docManager.get(docLibID, docID);
	        forum.set("a_good", goodFlag);
	        forums.add(forum);
	    }
	    String error = forumManager.save(docLibID, forums);
	    
	    String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
	    if (error == null) {
	        url += "&DocIDs=" + DocIDs; //操作成功
	    } else {
	        url += "&Info=" + URLEncoder.encode(error, "UTF-8");//有错误，需返回前台做提示
	    }
	    return "redirect:" + url;
	}

	private void addCount(int docLibID, boolean isSubjectQA, Document forum)
			throws E5Exception {
		long rootID = forum.getLong("a_rootID");// 主贴ID
		if (isSubjectQA) {
			forumManager.addForumCount(docLibID, rootID);// 话题的提问数+1
		} else {
			long parentID = forum.getLong("a_parentID");// 父贴ID
			if (parentID > 0 && rootID > 0) {
				forumManager.addForumCount(docLibID, rootID);// 主贴的评论数+1
			}
			if (parentID > 0 && parentID != rootID) {
				forumManager.addForumCount(docLibID, parentID);// 父贴的评论数+1
			}
		}
	}

	private void decreaseCount(int docLibID, boolean isSubjectQA, Document forum)
			throws E5Exception {
		long rootID = forum.getLong("a_rootID");// 主贴ID
		if (isSubjectQA) {
			forumManager.decreaseForumCount(docLibID, rootID);// 话题的提问数-1
		} else {
			long parentID = forum.getLong("a_parentID");// 父贴ID
			if (parentID > 0 && rootID > 0) {
				forumManager.decreaseForumCount(docLibID, rootID);// 主贴的评论数-1
			}
			if (parentID > 0 && parentID != rootID) {
				forumManager.decreaseForumCount(docLibID, parentID);// 父贴的评论数-1
			}
		}
	}

	// 清理Redis里的列表缓存
	private void clearRedis(int docLibID, List<Document> forums, boolean needClear) {
		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
	    boolean isLive = docTypeID == DocTypes.LIVE.typeID();
	    boolean isSubject = docTypeID == DocTypes.SUBJECT.typeID()
	    		|| docTypeID == DocTypes.SUBJECTQA.typeID();
        boolean isQA = docTypeID == DocTypes.QA.typeID(); //互动问答
        boolean isActivity = docTypeID == DocTypes.ACTIVITY.typeID(); //互动问答
		
		for (Document forum : forums) {
			//明确要求清理（撤回时）时，或判断是已通过的帖子时，清理缓存
			if (needClear || forum.getInt("a_status") == 1) {
				long rootID = forum.getLong("a_rootID");
				int siteID = forum.getInt("a_siteID");
				
				if (isLive) {
					long parentID = forum.getLong("a_parentID");
					long keyID = (parentID > 0) ? rootID : 0;
					RedisManager.clearKeys(RedisKey.APP_LIVELIST_KEY + keyID);
				} else if (isSubject) {
					long catID =forum.getLong("a_group_ID");
		    		long answererID =forum.getLong("a_answererID");
					String key = RedisManager.getKeyBySite(RedisKey.APP_SUBJECT_LIST_KEY, siteID) ;
					RedisManager.clearKeyPages(key);
					RedisManager.clearKeyPages(RedisKey.MY_SUBJECT_KEY+answererID); //题主的话题列表
		       		if(catID > 0)
		       			RedisManager.clearKeyPages(RedisKey.APP_SUBJECT_CAT_KEY+catID); //相关分类的话题列表
				} else if (isQA) {
					String key = RedisManager.getKeyBySite(RedisKey.APP_QALIST_KEY, siteID);
					RedisManager.clearLongKeys(key + "-1");
					RedisManager.clearLongKeys(key + forum.getInt("a_group_ID"));
					RedisManager.clear(RedisKey.APP_QA_KEY + forum.getDocID());
				} else if (isActivity) {
					String key = RedisManager.getKeyBySite(RedisKey.APP_ACTIVITY_KEY, siteID);
					RedisManager.clearLongKeys(key);
					RedisManager.clear(RedisKey.APP_ACTIVITY_DETAIL_KEY + forum.getDocID());
				}
			}
		}
	}
}