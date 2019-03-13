package com.founder.amuc.commons.attachment;

import java.util.List;

import com.founder.e5.context.E5Exception;
import com.founder.e5.db.IBfile;

/**
 * 文档附件管理
 * @author wanghc
 * 
 */
public interface AttachManager
{
	// ---------------------------------------
	// ----定义读取ID的名称
	// ---------------------------------------
	public static final String ID_IMAGE = "RefID";

	/**
	 * 添加个附件
	 * 
	 * @param attach 附件对象
	 */
	void addAttach(Attach attach) throws E5Exception;
	
	/**
	 * 添加个附件
	 * 
	 * @param attach 附件对象
	 * @return 生成的附件ID
	 */
	Long addAtta(Attach attach) throws E5Exception;
	
	/**
	  * 更新附件属性信息(不更新流信息)
	 * 
	 * @param attach
	 * @throws E5Exception
	 */
	void updateAttachProperty(Attach attach) throws E5Exception;
	
	/**
	 * 更新附件实体
	 * 
	 * @param attach
	 * @throws E5Exception
	 */
	void updateAttach(Attach attach) throws E5Exception;
	
	/**
	 * 删除一个附件对象
	 * @param mediaID
	 * @throws E5Exception
	 */
	void deleteAttach(long mediaID) throws E5Exception;
	
	/**
	 * 删除一个附件对象
	 */
	void deleteAttach(Attach attach) throws E5Exception;
	
	/**
	 *  根据附件id取得附件对象
	 *  
	 * @param mediaID
	 * @return
	 * @throws E5Exception
	 */
	Attach getAttach(long mediaID) throws E5Exception;	
	
	/**
	 * 根据文档ID取的其对应的所有附件
	 * @param documentID
	 * @return
	 * @throws E5Exception
	 */
	List<Attach> getAttaches(long documentID,int docLibID) throws E5Exception;
	
	/**
	 * 创建新的附件信息
	 * 
	 */
	public long create();
	
	/**
	 * 附件的复制
	 * @param refID
	 * @return
	 */
	public int copy( int refID);
	
	/**
	 * 一般附件的复制，存储于附件存储设备
	 * @param refID
	 * @param userCode
	 */
	public int copy( int refID , String userCode );
	
	/**
	 * 真正将附件存储于存储设备
	 * @param refID
	 * @param attachBfile
	 * @return
	 */
	public void writeAttach(int refID, IBfile attachBfile);
}