package com.founder.xy.system;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.AfterReturningAdvice;

import com.founder.e5.cat.CatCache;
import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.CacheManager;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.redis.RedisManager;

/**
 * 分类AOP，在话题分类有修改时，刷新redis
 * @author Gong Lijie
 */
public class CatManagerAdvice implements AfterReturningAdvice {

	@Override
	public void afterReturning(Object returnObj, Method method, Object[] args,
			Object target) throws Throwable {
		//System.out.println("method " + target.getClass().getName() + "." + method.getName() + "() has been executed.");
		Category cat = null;
		int catTypeID = 0;
		int catID = 0;
		
		String methodName = method.getName();
		try {
			//增改分类时，能得到分类对象；删除和恢复时，能得到catTypeID和catID
			if (methodName.equals("updateCat") || methodName.equals("createCat")) {
				cat = (Category)args[0];
				catTypeID = cat.getCatType();
			} else if (methodName.equals("deleteCat")) {
				if (args[0] instanceof Integer) {
					catTypeID = (Integer)args[0];
					catID = (Integer)args[1];
				}
			} else if (methodName.equals("restoreCat")) {
				catTypeID = (Integer)args[0];
				catID = (Integer)args[1];
			}
			if (catTypeID <= 0) return;
			
			//若是话题分类，则要刷新redis
			if (catTypeID == CatTypes.CAT_DISCUSSTYPE.typeID())
				refreshRedis("DISCUSSTYPE", cat, catTypeID, catID);
			else if (catTypeID == CatTypes.CAT_REGION.typeID())
				refreshRedis("REGION", cat, catTypeID, catID);
			else if (catTypeID == CatTypes.CAT_QA.typeID())
				refreshRedis("QA", cat, catTypeID, catID);
			else if (catTypeID == CatTypes.CAT_ARTICLETRADE.typeID()) {
				refreshRedis("ARTICLETRADE", cat, catTypeID, catID);
				CacheManager.refresh(CatCache.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshRedis(String catTypeCode, Category cat, int catTypeID, int catID) {
		//得到siteID，因为部分分类按站点区分
		int siteID = getSiteID(cat, catTypeID, catID);
		RedisManager.clear(RedisManager.getCatKeyBySite(siteID, catTypeCode));
	}
	
	private int getSiteID(Category cat, int catTypeID, int catID) {
		CatManager catManager = (CatManager)Context.getBean(CatManager.class);
		int siteID = 0;
		String[] temp = new String[2] ;
		try {
			if(cat !=null){
				temp[0] = cat.getCatCode(); 
				temp[1] = cat.getCascadeID();
			}else{
				temp = getCat(catTypeID,catID,temp,catManager) ;
			}
			String[] pathIDs = temp[1].split("~") ;
			if (pathIDs.length > 1)
				temp = getCat(catTypeID,Integer.parseInt(pathIDs[0]),temp,catManager) ;
			if(!StringUtils.isBlank(temp[0]))
				siteID = Integer.parseInt(temp[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return siteID;
	}
	
	private String[] getCat(int catTypeID, int catID , String[] temp, CatManager catManager) throws E5Exception{
		String sql = "select ENTRY_CODE,ENTRY_CASCADE_ID from CATEGORY_OTHER where ENTRY_ID=?" ;
		String catTable = catManager.getType(catTypeID).getTableName();
		if ((catTable != null) && (!"".equals(catTable)))
			sql = sql.replaceAll(Category.DEFAULT_TABLENAME, catTable);
		
		DBSession db = Context.getDBSession();
		IResultSet rs = null;
		try {
            rs = db.executeQuery(sql, new Object[]{catID});
            while (rs.next()){
            	temp[0] = rs.getString("ENTRY_CODE"); 
            	temp[1] = rs.getString("ENTRY_CASCADE_ID"); 
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
        }
		return temp;
	}
}
