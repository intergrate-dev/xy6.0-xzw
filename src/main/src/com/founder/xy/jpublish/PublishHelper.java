package com.founder.xy.jpublish;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.founder.e5.context.E5Exception;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;

import com.founder.xy.jpublish.paper.PaperRevoke;
import org.apache.commons.lang.ArrayUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.magazine.MagazineGenerator;
import com.founder.xy.jpublish.page.ArticleGenerator;
import com.founder.xy.jpublish.page.BlockGenerator;
import com.founder.xy.jpublish.page.ColumnGenerator;
import com.founder.xy.jpublish.page.RevokeGenerator;
import com.founder.xy.jpublish.paper.PaperGenerator;
import com.founder.xy.jpublish.wx.WXGenerator;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 发布服务辅助类
 * @author Gong Lijie
 */
public class PublishHelper {
	private static String transPath; //trans服务的信息文件目录
	
	/**
	 * 稿件发布，用于正常发布和定时发布
	 * @param data
	 */
	public static void articlePublish(ArticleMsg data) {
		articlePublish(data, true);
	}
	/**
	 * 稿件发布，用于正常发布和定时发布
	 * @param data
	 */
	public static void articlePublish(ArticleMsg data, boolean publishColumn) {
		//文章页发布
		ArticleGenerator generator = new ArticleGenerator();
		int published = generator.generator(data);
		
		if (published == PubArticle.SUCCESS) {
			//清掉redis中的稿件相关key（for App）
			clearAppKeys(data);
			//清掉redis中的话题相关key（for App）
			clearAppTopicsKeys(data);

			//刷新栏目更新时间缓存
			long[] cols = StringUtils.getLongArray(data.getColAll(), ";");
			refreshColCacheTime(cols);

			//修改话题稿件关联表的发布状态
			changeTopicRelStatus(data,1);
			//更新话题发布时间
			changeTopicPubTime(data);

            clearHotTopickey(data);

            //清理以当前稿件为相关稿件的稿件缓存key
            clearRelArticleKey(data);

            //热门稿件缓存不再清理
//            clearHotArticleKey(data);
			
			//栏目页发布
			if (publishColumn) {
				//发稿成功后，修改栏目的“最新发稿时间”字段，用于统计栏目是否有更新。
				//按栏目发布时不太准确：只按最后一条稿件的columnAll做更新。认为够用
				changeColumnPubTime(data);
				
				columnPublish(data);
				
				//此时重发上下篇
				publishNears(data);
				
				//可能会影响到专题，检查并发布相关专题页
				specialPublish(data);
				
				//稿件发布时同时更新区块
				blockPublish(data);
			}
		}
	}

    //清理以当前稿件为相关稿件的稿件缓存key
    private static void clearRelArticleKey(ArticleMsg data) {
        String sql = "SELECT GROUP_CONCAT(a_articleID separator ',') as articleIDs FROM `xy_articlerel` where a_relID = ? and a_relLibID = ? and SYS_DELETEFLAG=0";
        DBSession conn = null;
        IResultSet rs = null;
        String articleIDs = "";
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{data.getId(),data.getDocLibID()});
            while (rs.next()) {
                articleIDs = rs.getString("articleIDs");
            }
            if(!StringUtils.isBlank(articleIDs)){
                String[] articleID_array = articleIDs.split(",");
                for(int i=0;i<articleID_array.length;i++){
                    String articleID = articleID_array[i];
                    String key = RedisKey.APP_ARTICLE_KEY + articleID;
                    System.out.println("清理相关稿件主稿件key:"+key);
                    RedisManager.clear(key);
                }
            }
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(conn);
            ResourceMgr.closeQuietly(rs);
        }
	}

//    private static void clearHotArticleKey(ArticleMsg data) {
//        int docLibID = data.getDocLibID();
//        int appDocLibID = LibHelper.getArticleAppLibID();
//        String tableName = "xy_articleapp";
//        if(docLibID!=appDocLibID){
//            tableName = "xy_article";
//        }
//        DBSession conn = null;
//        IResultSet rs = null;
//        try {
//            int siteID = 0;
//            String selectSQL = "SELECT a_siteID FROM "+tableName+" a where sys_documentid=?";
//            conn = Context.getDBSession();
//            rs = conn.executeQuery(selectSQL, new Object[]{data.getId()});
//            while (rs.next()) {
//                siteID = rs.getInt("a_siteID");
//            }
//            if(siteID!=0){
//                int articleType= 2;
//                int timeType = 3;
//                int orderType =3;
//                for(int i=0;i<articleType;i++){
//                    for (int j=0;j<timeType;j++){
//                        for (int k=0;k<orderType;k++){
//                            String key = RedisManager.getKeyBySite(RedisKey.APP_NEW_HOT_ARTICLELIST_KEY, siteID)+ (i+1) +"."+ (j+1) +"."+ (k+1);
//                            if(RedisManager.exists(key)){
//                                RedisManager.clear(key);
//                            }
//                            if(docLibID!=appDocLibID){
//                                String key1 = RedisManager.getKeyBySite(RedisKey.WEB_NEW_HOT_ARTICLELIST_KEY, siteID)+ (i+1) +"."+ (j+1) +"."+ (k+1);
//                                if(RedisManager.exists(key1)){
//                                    RedisManager.clear(key1);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            ResourceMgr.rollbackQuietly(conn);
//            e.printStackTrace();
//        } finally {
//            ResourceMgr.closeQuietly(conn);
//            ResourceMgr.closeQuietly(rs);
//        }
//    }

    //发稿成功后，清除热门话题key for APP
    private static void clearHotTopickey(ArticleMsg data) {
	    int docLibID = data.getDocLibID();
	    int appDocLibID = LibHelper.getArticleAppLibID();
	    if(docLibID==appDocLibID){//app发布库
            DBSession conn = null;
            IResultSet rs = null;
            try {
                String siteID = "";
                String selectTopicIDSQL = "SELECT a_siteID FROM `xy_articleapp` a where sys_documentid=?";
                conn = Context.getDBSession();
                rs = conn.executeQuery(selectTopicIDSQL, new Object[]{data.getId()});
                while (rs.next()) {
                    siteID = rs.getString("a_siteID");
                }
                if(!StringUtils.isBlank(siteID)){
                    String key = RedisKey.APP_HOT_TOPICS_KEY + siteID;
                    if(RedisManager.exists(key)){
                        RedisManager.clear(key);
                    }
                }

            } catch (Exception e) {
                ResourceMgr.rollbackQuietly(conn);
                e.printStackTrace();
            } finally {
                ResourceMgr.closeQuietly(conn);
                ResourceMgr.closeQuietly(rs);
            }
        }
    }

    //发稿成功后，修改话题关联表的“状态”字段
	private static void changeTopicRelStatus(ArticleMsg data, int isPublist){
		DBSession conn = null;
		if(isPublist==1){
            isPublist = Article.STATUS_PUB_DONE;
        }else{
            isPublist = Article.STATUS_REVOKE;
        }
		try {
			String sql = "update xy_topicrelart set a_status=? where a_articleID=? and a_channel=?";
			conn = Context.getDBSession();

			InfoHelper.executeUpdate(sql, new Object[]{isPublist, data.getId(), data.getChannel()}, conn);
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	//发稿成功后，修改话题的“最新发稿时间”字段
	private static void changeTopicPubTime(ArticleMsg data) {
		int topicLibID = LibHelper.getLibIDByOtherLib(DocTypes.TOPICS.typeID(), data.getDocLibID());
		DBSession conn = null;
		try {
			String sql = "update " + LibHelper.getLibTable(topicLibID)
					+ " set a_lastPubTime=? where SYS_DOCUMENTID in"
					+ " (SELECT a_topicID from xy_topicrelart where a_articleID=? and a_channel=?)";

			conn = E5docHelper.getDBSession(topicLibID);
			conn.beginTransaction();

			InfoHelper.executeUpdate(sql, new Object[]{DateUtils.getTimestamp(), data.getId(), data.getChannel()}, conn);

			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	//发稿成功后，修改栏目的“最新发稿时间”字段
	private static void changeColumnPubTime(ArticleMsg data) {
		long[] cols = StringUtils.getLongArray(data.getColAll(), ";");
		
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), data.getDocLibID());
		DBSession conn = null;
		try {
			String sql = "update " + LibHelper.getLibTable(colLibID)
					+ " set col_pubTime=? where SYS_DOCUMENTID=?";
			
			conn = E5docHelper.getDBSession(colLibID);
			conn.beginTransaction();
			
			for (long colID : cols) {
				InfoHelper.executeUpdate(sql, new Object[]{DateUtils.getTimestamp(), colID}, conn);
			}
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**
	 * 撤稿
	 * @param data
	 */
	public static void revoke(ArticleMsg data) {
		//撤掉发布的文件
		RevokeGenerator generator = new RevokeGenerator();
		generator.generator(data);
		 
		//清掉redis中的稿件相关key（for App）
		clearAppKeys(data);

		//清掉redis中的话题相关key（for App）
		clearAppTopicsKeys(data);

		//修改话题稿件关联表的发布状态
		changeTopicRelStatus(data,7);

        //清理以当前稿件为相关稿件的稿件缓存key
        clearRelArticleKey(data);

		//稿件热门缓存不再清理
//        clearHotArticleKey(data);

		//刷新栏目更新时间缓存
		long[] cols = StringUtils.getLongArray(data.getColAll(), ";");
		refreshColCacheTime(cols);
		
		//栏目页需重新发布
		columnPublish(data);
		
		//此时重发上下篇
		publishNears(data);
		
		//可能是专题稿撤稿，清掉Redis里的记录
		RedisManager.hclear(RedisKey.SPECIAL_ARTICLES, data.getId());

		//可能会影响到专题，检查并发布相关专题页
		specialPublish(data);
		
		//稿件撤稿时同时更新区块
		blockPublish(data);
	}

	/**
	 * 栏目页发布
	 * @param data
	 */
	public static void columnPublish(DocIDMsg data ) {
		columnPublish(data,false);
	}


	/**
	 * 栏目页发布
	 *
	 * @param data          the data
	 * @param pubRelatedCol 是否发布相关的栏目,专题
	 */
	public static void columnPublish(DocIDMsg data ,boolean pubRelatedCol) {
		clearAppKeys(data.getDocID());
		if(pubRelatedCol){
			int[] colIDs = {(int)data.getDocID()};
			long articleID = -1;
			int colDocLibID= data.getDocLibID();
			//发布影响到的栏目
			columnPublish(colDocLibID,colIDs);
			int artDocLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLE.typeID(), data.getDocLibID());
			ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
			try {
				Column column = columnReader.get(data.getDocLibID(), data.getDocID());
				int channel =  column.getChannel();
				//发布影响到的专题
				specialPublish(colIDs,articleID,artDocLibID,channel);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		else {
			//直接发布本栏目
			ColumnGenerator columnGenerator = new ColumnGenerator();
			columnGenerator.generator(data);
		}
	}
	/**
	 * 区块发布
	 *
	 * @param data          the data
	 */
	public static void blockPublish(ArticleMsg data) {
		
		long[] colIDs = StringUtils.getLongArray(data.getColAll(), ";");
		
		//按栏目ID找到所有的自动发布的区块ID
		Set<Long> set = getBlockIDByColumnID(colIDs);
		//获取区块的LibID
		int blockLibID = LibHelper.getBlockLibID();
		//建立区块发布任务
		BlockGenerator generator = new BlockGenerator();
		for(Long blockID : set){
			generator.generator(new DocIDMsg(blockLibID, blockID, null));
		}
	}

	/**
	 * 报纸刊期发布
	 */
	public static void paperPublish(DocIDMsg data) {
		PaperGenerator generator = new PaperGenerator();
		boolean published = generator.generator(data);
		
		//清掉redis中的key
		if (published) {
			if (data.getRelIDs() != null) {
				//整个刊期发布，清理报纸最近刊期列表、刊期版面列表
				RedisManager.clear(RedisKey.APP_PAPER_DATE_KEY + data.getDocID());
				RedisManager.clear(RedisKey.APP_PAPER_LAYOUT_KEY + data.getDocID() + "." + data.getRelIDs());
			} else {
				//单版发布，清理版面稿件列表
				RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + data.getDocID());
			}
		}
	}


	/**
	 * 报纸刊期删除
	 */
	public static void paperRevoke(DocIDMsg data) {
		PaperRevoke generator = new PaperRevoke();
		boolean revoked = generator.generator(data);
		//清掉redis中的key
		if (revoked) {
			RedisManager.clear(RedisKey.APP_PAPER_DATE_KEY + data.getDocID());
		}
	}

	/**
	 * 期刊发布
	 */
	public static void magazinePublish(DocIDMsg data) {
		MagazineGenerator generator = new MagazineGenerator();
		boolean published = generator.generator(data);
		
		//清掉redis中的key，待有需求时再加
		if (published) {
			//RedisManager.clear(RedisKey.APP_PAPER_DATE_KEY + data.getDocID());
		}
	}
	
	/**
	 * 微信菜单稿件发布
	 */
	public static void wxPublish(DocIDMsg data) {
		WXGenerator generator = new WXGenerator();
		generator.generator(data);
	}
	/**
	 * 写trans服务需要的空文件
	 * @param path 文件完整路径名
	 * @param root 站点根目录
	 */
	public static void writeTransPath(String path, String root) {
		String relativePath = path.substring(root.length() + 1);
		
		writePath(relativePath, getTransPath());
	}
	/**
	 * 按文件的相对路径创建指定目录下的空文件，相对路径以~分隔
	 * @param relativePath
	 * @param root
	 */
	public static void writePath(String relativePath, String root) {
		if (StringUtils.isBlank(root)) return;
		
		relativePath = relativePath.replaceAll("\\\\", "/");
		relativePath = relativePath.replaceAll("/", "~");
		
		try {
			File dir = new File(root);
			if (!dir.exists())
				dir.mkdir();
			
			File file = new File(dir, relativePath);
			file.createNewFile();
			
			//System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 取trans信息文件的目录。在系统参数中配置
	 * @return
	 */
	public static String getTransPath() {
		if (transPath == null)
			transPath = InfoHelper.getConfig("发布服务", "分发信息文件位置");
		
		 return transPath;
	}

	/**
	 * 清掉redis中的栏目的稿件列表key（for App）
	 * @param colID
	 */
	private static void clearAppKeys(long colID) {
		RedisManager.clearLongKeys(RedisKey.APP_ARTICLELIST_AD_KEY + colID);
		//子栏目稿件列表
		RedisManager.clear(RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + ".0");
		RedisManager.clear(RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + ".1");
		RedisManager.clear(RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + ".2");
	}

	/**
	 * 清掉redis中的稿件相关key（for App）
	 */
	private static void clearAppKeys(ArticleMsg data) {
		//清除redis中稿件json
		//RedisManager.clear(RedisKey.APP_ARTICLE_KEY + data.getId());

		//清理掉稿件关联的所有栏目的稿件列表json
		long[] cols = StringUtils.getLongArray(data.getColAll(), ";");
		if (cols != null) {
			for (long colID : cols) {
				clearAppKeys(colID);
			}
		}
	}
	/**
	 * 刷新redis中的栏目更新时间
	 * @param cols
	 */
	private static void refreshColCacheTime(long[] cols) {
		if (cols == null) return;
		
		String timeValue = String.valueOf(System.currentTimeMillis());
		
		for(long colID : cols){
			RedisManager.hset(RedisKey.COLUMNS_TIME_KEY, colID, timeValue);
		}
	}
	/**
	 * 稿件发布时，同步发布栏目页
	 * @param data
	 */
	private static void columnPublish(ArticleMsg data) {
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), data.getDocLibID());
		int[] colIDs = StringUtils.getIntArray(data.getColAll(), ";");
		columnPublish(colLibID,colIDs);
	}


	private static void columnPublish(int colLibID,int[] colIDs) {
		Set<Long> set = new HashSet<>();
		
		//要发布的栏目加入集合
		if (colIDs != null) {
			for (long colID : colIDs) {
				set.add(colID);
				//columnPublish(new DocIDMsg(colLibID, colID, null));
			}
		}

		//再发布模板中内含当前栏目的其它栏目
		BaseDataCache cache = (BaseDataCache)CacheManager.find(BaseDataCache.class);
		for (int colID : colIDs) {
			long[] relatingIDs = cache.getColumnRelating(colLibID, colID);
			if (relatingIDs != null) {
				for (long l : relatingIDs) {
					set.add(l);
				}
			}
		}

		for (long l : set) {
			columnPublish(new DocIDMsg(colLibID, l, null));
		}
	}

	
	//稿件发布之后，把受影响的专题稿再发一遍
	private static void specialPublish(ArticleMsg data) {
		//稿件能影响的栏目页
		int[] colIDs = StringUtils.getIntArray(data.getColAll(), ";");
		long articleID =  data.getId();
		int docLibID= data.getDocLibID();
		int channel =  data.getChannel();
		specialPublish(colIDs,articleID,docLibID,channel);

	}
	/**
	 * 稿件发布之后，把受影响的专题稿再发一遍
专题稿件发布后，存入Redis里的一个Hash里：
	<稿件id，json>，其中json里包括有效时间、相关栏目ids（来自专题稿的模板）。
	没有相关栏目ids的不加进来。
普通稿件发布后，对上述专题稿Hash逐一检查：
	若有效期已经过期，则从Hash里删掉专题稿id（对应的专题模板也应删）。
	若某专题稿的相关栏目里有当前稿件的相关栏目，则重发专题稿。
专题稿撤稿后，应从上述Hash里去掉。
	 */
	private static void specialPublish(int[] colIDs,long articleID,int docLibID,int channel) {
		//Redis中记录的所有专题稿
		Set<String> specialIDs = RedisManager.hkeys(RedisKey.SPECIAL_ARTICLES);
		if (specialIDs == null || specialIDs.isEmpty()) return;

		if (colIDs == null || colIDs.length == 0) return;

		Set<Long> set = new HashSet<>();
		//对每个专题稿检查
		for (String specialID : specialIDs) {
			if (Long.parseLong(specialID) == articleID ) continue;

			JSONObject obj = JsonHelper.getJson(RedisManager.hget(RedisKey.SPECIAL_ARTICLES, specialID));
			//先检查专题稿的有效期。若过了有效期，则从Redis里删除
			String date = JsonHelper.getString(obj, "expire");
			if (!StringUtils.isBlank(date)) {
				if (DateUtils.parse(date).before(DateUtils.getDate())) {
					RedisManager.hclear(RedisKey.SPECIAL_ARTICLES, specialID);
					continue;
				}
			}
			//若专题稿相关的栏目ID中，有能被稿件影响的，则记录下来
			int[] spColRelated = StringUtils.getIntArray(JsonHelper.getString(obj, "colRelated"));
			for (int spCol : spColRelated) {
				if (ArrayUtils.contains(colIDs, spCol)) {
					set.add(Long.parseLong(specialID));
					break;
				}
			}
		}

		//把受影响的专题稿依次重新发布
		for (long specialID : set) {
			//若专题稿不是已发布，则不处理
			if (!ArticleManager.hasPublished(docLibID, specialID)) continue;
			
			//得到专题稿所在栏目的ID
			JSONObject obj = JsonHelper.getJson(RedisManager.hget(RedisKey.SPECIAL_ARTICLES, specialID));
			int colID = JsonHelper.getInt(obj, "colID");

			ArticleMsg msg = new ArticleMsg( docLibID, specialID, colID, null,
					Article.TYPE_SPECIAL,channel);
			articlePublish(msg, false);
		}
	}

	/**
	 * 找到稿件的上下篇，重发
	 * @param data
	 */
	private static void publishNears(ArticleMsg data) {
		ArticleManager articleManager = (ArticleManager)Context.getBean("articleManager");
		List<ArticleMsg> nears = articleManager.getNearArticles(data);
		if (nears == null) return;
		
		for (ArticleMsg data0 : nears) {
			articlePublish(data0, false); //只重发稿件本身，不触发栏目页的生成
		}
	}
	
	private static Set<Long> getBlockIDByColumnID(long[] columnIds) {
		Set<Long> set = new HashSet<>();
		if(columnIds != null) {
			int compLibID = LibHelper.getComponentObjLibID();
			DBSession db = null;
			IResultSet rs = null;
			JSONObject jobj = null;
			JSONArray jarr = null;
			try {
				String table = LibHelper.getLibTable(compLibID);
				String sql = "select co_templateID,co_data from "+table+" where co_data like '%columnid_:[%==colID==%]%' and co_templateType=1 AND SYS_DELETEFLAG=0";
				db = InfoHelper.getDBSession(compLibID);
				for(Long columnId : columnIds) {
					rs = db.executeQuery(sql.replace("==colID==", String.valueOf(columnId)));
					while (rs.next()) {
						String data = rs.getString("co_data");
						jobj = JSONObject.fromObject(data);
						jarr = jobj.getJSONArray("columnid");
						if(jarr.contains(columnId)){
							set.add(rs.getLong("co_templateID"));
						}
					}
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(db);
			}
		}
		return set;
	}

    /**
     * 清掉redis中的话题稿件相关key（for App）
     */
    private static void clearAppTopicsKeys(ArticleMsg data) {
        //获取稿件话题id
        List<JSONObject> list = getTopicIDs(data);
        int listSize = list.size();

        for(int i=0;i<listSize;i++){
            String siteID = list.get(i).getString("siteID");
            String topicID = list.get(i).getString("topicID");
			String groupID = list.get(i).getString("groupID");

            //清除redis中话题稿件列表的缓存
            if(StringUtils.isBlank(siteID)||StringUtils.isBlank(topicID)){
                continue;
            }
//            String key1 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + "1" + "." + topicID + "."+0;
//            String key2 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + "1" + "." + topicID + "."+1;
//            String key3 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + "1" + "." + topicID + "."+2;

            String key1 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + (data.getChannel()-1) + "." + topicID + "."+0;
            String key2 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + (data.getChannel()-1) + "." + topicID + "."+1;
            String key3 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + (data.getChannel()-1) + "." + topicID + "."+2;
            String key4 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + (data.getChannel()-1) + "." + topicID + "."+100;
            
            RedisManager.clearLongKeys(key1);
            RedisManager.clearLongKeys(key2);
            RedisManager.clearLongKeys(key3);
            RedisManager.clearLongKeys(key4);

            //清空话题按组列表缓存
            String topicsGroupKey = RedisKey.APP_TOPICSBYGROUP_KEY + siteID;
            RedisManager.clear(topicsGroupKey);

			RedisManager.clear(RedisKey.ARTICLE_TOPICSGROUP_KEY + (data.getChannel()-1) + "." + data.getId()+"."+groupID);
        }

        RedisManager.clear(RedisKey.ARTICLE_TOPICSGROUP_KEY + (data.getChannel()-1) + "." + data.getId()+"."+0);
        RedisManager.clear(RedisKey.ARTICLE_TOPICS_KEY + (data.getChannel()-1) + "." + data.getId());
    }

    private static List getTopicIDs(ArticleMsg data) {
//        int docLibID = data.getDocLibID();
//        int appDocLibID = LibHelper.getArticleAppLibID();
        String topicID = "";
        String siteID = "";
        String groupID= "";
        List<JSONObject> list = new ArrayList();
//        if(docLibID==appDocLibID){//app发布库
            DBSession conn = null;
            IResultSet rs = null;
            try {
                String selectTopicIDsSQL = "SELECT a.a_topicID,a.a_siteID,b.a_groupID from xy_topicrelart a left JOIN xy_topics b on b.SYS_DOCUMENTID = a.a_topicID where a_articleID=? and a_channel=?";
                conn = Context.getDBSession();
                rs = conn.executeQuery(selectTopicIDsSQL, new Object[]{data.getId(),data.getChannel()});
                while (rs.next()) {
                    JSONObject jsonObject = new JSONObject();
                    topicID = rs.getString("a_topicID");
                    siteID = rs.getString("a_siteID");
					groupID =rs.getString("a_groupID");
                    jsonObject.put("siteID",siteID);
                    jsonObject.put("topicID",topicID);
					jsonObject.put("groupID",groupID);
                    list.add(jsonObject);
                }

            } catch (Exception e) {
                ResourceMgr.rollbackQuietly(conn);
                e.printStackTrace();
            } finally {
                ResourceMgr.closeQuietly(conn);
                ResourceMgr.closeQuietly(rs);
            }
//        }
        return list;
    }
}
