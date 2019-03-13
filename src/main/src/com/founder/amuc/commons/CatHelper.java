package com.founder.amuc.commons;


import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.CatType;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;

/**
 * 分类相关的辅助类。
 * 包括分类类型的常量定义，获取分类类型ID的简便方法，根据分类ID得到分类名的方法等。
 * @author Gong Lijie
 * 2011-11-2
 */
public class CatHelper {
	private static Log log = Context.getLog("sfa");
	private static CatReader catReader = (CatReader) Context.getBean(CatReader.class);

	/**
	 * 根据分类类型的名称，获取分类类型的ID
	 * @param catName
	 * @return
	 */
	public static int getCatTypeID(String catTypeName) {
		CatType catType;
		try {
			catType = catReader.getType(catTypeName);
		} catch (E5Exception e) {
			catType = null;
		}
		if (catType == null)
			return 0;
		return catType.getCatType();
	}
	/** 
	* @author  leijj 
	* 功能： 根据分类名称和分类id获取分类名称
	* @param catTypeName
	* @param catID
	* @return
	* @throws E5Exception 
	*/ 
	public static String getCatName(String catTypeName, int catID) throws E5Exception {
		Category category = catReader.getCat(catTypeName, catID);
		if(category == null) return null;
		return category.getCatName();
	}
	/**
	 * 获取客户分类中的“未分类”
	 * @return
	 * @throws E5Exception
	 */
	public static Category getCustNotBaseCat() throws E5Exception{
		int baseCatID = CatTypes.CAT_BASE.typeID();
		CatReader catReader = (CatReader)Context.getBean(CatReader.class);
		Category base = catReader.getCatByName(baseCatID, "未分类");
		//doc.set("CUST_CAT", base.getCatID());
		//doc.set("CUST_CATNAME", base.getCatName());
		return base;
	}
	/**
	 * 获取行业分类中的“未分类”
	 * @return
	 * @throws E5Exception
	 */
	public static Category getTradeNotBaseCat() throws E5Exception{
		int baseCatID = CatTypes.CAT_TRADE.typeID();
		CatReader catReader = (CatReader)Context.getBean(CatReader.class);
		Category base = catReader.getCatByName(baseCatID, "未分类");
		return base;
	}
	
	/**
	 * 根据分类类型Id，以及该分类名称 获取分类ID
	 * @param catTypeID
	 * @param catName
	 * @return
	 */
	public static int getCatID(int catTypeID, String catName)
	{
		if( catTypeID == 0 || StringUtils.isBlank( catName ))
		{
			return 0;
		}
		
		try 
		{
			Category[] cats = catReader.getCats(catTypeID);
			if( cats!=null && cats.length!=0 )
			{
				for( int i=0; i<cats.length; i++ )
				{
					if( cats[i].getCatName().endsWith( catName ) )
					{
						return cats[i].getCatID();
					}
				}
			}
		} 
		catch (E5Exception e) 
		{
			log.error( e );
		}
		return 0;
	}
	
	/**
	 * 根据分类的ID串，得到名字串，以在界面上显示分类名称
	 * 
	 * @param catIDs
	 *            ID串用分号分隔多个分类，每个分类ID中保存的是级联ID 如：31_100_323;31_103_2445;
	 * @return
	 */
	public static String getCatNames(int catType, String catIDs) {
		if (log.isDebugEnabled())
			log.debug("[GetCatNames]:" + catIDs);

		String[] catIDArray = StringUtils.split(catIDs, ";");
		if (catIDArray == null) return "";

		StringBuffer sbResult = new StringBuffer();
		int count = 0;
		Category cat = null;
		for (int i = 0; i < catIDArray.length; i++) {
			int[] idArray = StringUtils.getIntArray(catIDArray[i], "_");
			if (idArray == null)
				continue;

			try {
				cat = catReader.getCat(catType, idArray[idArray.length - 1]);
				if (cat == null)
					continue;
			} catch (E5Exception e) {
				log.error("Error in [getCatNames]", e);
				continue;
			}

			count++;
			if (count > 1)
				sbResult.append(",");

			sbResult.append(cat.getCatName());
		}
		return sbResult.toString();
	}
	
	/**
	 * 按分类码得到一个分类。
	 * 用于与广告系统通信的场合：
	 * 广告系统中的行业分类的ID记录在本系统内行业分类的code里，
	 * 当导入广告系统中的品牌时，按品牌里的原行业分类ID查到本系统里的行业分类。
	 * @param code
	 */
	public static Category getByCode(int catTypeID, String code) {
		Category[] cats = null;
		try {
			cats = catReader.getCats(catTypeID);
		} catch (E5Exception e) {
		}
		if (cats == null) return null;
		
		for (Category cat : cats) {
			if (cat != null && code.equals(cat.getCatCode())) return cat;
		}
		return null;
	}
	
	/**
	 * 根据分类名称获取分类id
	 */
	public static int getCatIDByName(int typeID, String catName){
		Category cat;
		try {
			cat = catReader.getCatByName(typeID, catName);
			if(cat != null)
				return cat.getCatID();
		} catch (E5Exception e) {
			log.error(e);
			return 0;
		}
		
		return 0;
	}
	/**
	 * 根据分类名称获取分类
	 */
	public static Category[] getCatsByName(String catName){
		Category[] cat=null;
		try {
			cat = catReader.getCats(catName);
		} catch (E5Exception e) {
			log.error(e);
		}
		return cat;
	}
	
	/**
	 * 根据分类类型名称、分类ID取得一个分类
	 * @param catTypeName 分类类型名
	 * @param catID 分类ID
	 * @return Category
	 * @throws E5Exception
	 */
	public static Category getCatByID(String catType, int catID){
		Category cat=null;
		try {
			cat = catReader.getCat(catType, catID);
		} catch (E5Exception e) {
			log.error(e);
		}
		return cat;
	}
	
	public static Category getAccountCatByID(int catID){
		String catType = CatTypes.CAT_ACCOUNT.typeName();
		return getCatByID(catType, catID);
	}
	
	public static int getMenuIDByCatID(int catID){
		Category cat = getAccountCatByID(catID);
		if(cat == null) return 0;
		return cat.getLinkID();
	}
}