package com.founder.amuc.commons.attachment;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IBfile;
import com.founder.e5.db.IResultSet;
import com.founder.e5.db.LobHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceHelper;
import com.founder.e5.sys.SysFactory;

/**
 * 文稿附件相关操作
 * 存储在附件存储设备
 */
public class AttachManagerImpl implements AttachManager
{
	private final static String TABLE_DOM_ATTACHES = "DOM_ATTACHMENT";
	private Log log = Context.getLog( "amuc" );

	private static final String SQL_INSERT = "insert into " + TABLE_DOM_ATTACHES
			+ "(GUID,DOCLIBID,DOCID,ATT_TOPIC,ATT_CONTENT,ATT_ATTPATH,ATT_FORMAT,ATT_SIZE,ATT_TYPE,USERID)"
			+ " values(?,?,?,?,?,?,?,?,?,?)";

	private static final String SQL_UPDATE = "update " + TABLE_DOM_ATTACHES
			+ " set ATT_ATTPATH=?,ATT_SIZE=?" + " where GUID=?";

	private static final String SQL_DELETE = "delete from " + TABLE_DOM_ATTACHES + " where GUID=? ";
	

	/*
	 * （非 Javadoc）
	 * 
	 * @see com.founder.e5.edit.dom.AttachManager#addAttach(com.founder.e5.edit.dom.Attach)
	 */
	public void addAttach( Attach attach ) throws E5Exception
	{
		DBSession conn = null;
		try
		{
			conn = Context.getDBSession();
			assistantAddAttach( attach );
			// set filename
			//String fileName = AttachUtil.getAttachPath() + "/" + AttachUtil.getAttachName( attach );
			String fileName = attach.getAttPath();
			
			// set video thumbnail
//			IBfile bfile = LobHelper.createBfile( dir, fileName, attach.getIn(), BfileType.EXTFILE );

			if ( attach.getAttTopic() == null )
				attach.setAttTopic( "" );

			conn.executeUpdate( SQL_INSERT, new Object[] { new Long( attach.getGuid() ),
					new Integer( attach.getDocLibID() ), new Long( attach.getDocID() ),
					attach.getAttTopic(), attach.getAttContent(), fileName, attach.getAttFormat(),
					new Long( attach.getAttSize() ),attach.getAttType(),attach.getUser() }, new int[] { Types.NUMERIC, Types.NUMERIC,
					Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, Types.OTHER, Types.VARCHAR,
					Types.BIGINT,Types.NUMERIC,Types.NUMERIC } );
		}
		catch ( Exception e )
		{
			throw new E5Exception( "添加附件失败", e );
		}
		finally
		{
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	public Long addAtta(Attach attach) throws E5Exception {
		DBSession conn = null;
		try
		{
			conn = Context.getDBSession();
			assistantAddAttach( attach );
			// set filename
			//String fileName = AttachUtil.getAttachPath() + "/" + AttachUtil.getAttachName( attach );
			String fileName = attach.getAttPath();
			
			// set video thumbnail
//			IBfile bfile = LobHelper.createBfile( dir, fileName, attach.getIn(), BfileType.EXTFILE );

			if ( attach.getAttTopic() == null )
				attach.setAttTopic( "" );

			conn.executeUpdate( SQL_INSERT, new Object[] { new Long( attach.getGuid() ),
					new Integer( attach.getDocLibID() ), new Long( attach.getDocID() ),
					attach.getAttTopic(), attach.getAttContent(), fileName, attach.getAttFormat(),
					new Long( attach.getAttSize() ),attach.getAttType(),attach.getUser() }, new int[] { Types.NUMERIC, Types.NUMERIC,
					Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, Types.OTHER, Types.VARCHAR,
					Types.BIGINT,Types.NUMERIC,Types.NUMERIC } );
			return attach.getGuid();
		}
		catch ( Exception e )
		{
			throw new E5Exception( "添加附件失败", e );
		}
		finally
		{
			ResourceMgr.closeQuietly(conn);
		}
	}

	// 方法addAttach的辅助方法，用于解析完善实体对象Attach
	private void assistantAddAttach( Attach attache ) throws Exception
	{
		if ( attache.getGuid() <= 0 )
			attache.setGuid( EUID.getID( ID_IMAGE ) );
	}
	public void deleteAttach( long guid ) throws E5Exception {
		// 1.获取附件，删除实体
		Attach attach = this.getAttach( guid );
		deleteAttach(attach);
	}
	
	public void deleteAttach(Attach attach) throws E5Exception
	{
		DBSession sess = null;
		
		try {
			boolean bigRet = false;
			
			if( attach != null)
			{
				String fileName = attach.getAttPath();
				
				if( !StringUtils.isBlank(fileName) )
				{
					String arrayLarge[] = StringUtils.splitStrictly(fileName, ",");
		        	
					String deviceName = arrayLarge[0];
		        	if(arrayLarge.length == 2)
		        		fileName = arrayLarge[1];
		        	
		        	//获取存贮设备信息
		        	StorageDevice storageDevice = SysFactory.getStorageDeviceReader().getByName( deviceName );
					
					//删除远程设备上的
					bigRet = StorageDeviceHelper.deleteDeviceFile( deviceName, fileName, storageDevice.getDeviceType());
		        	
					if ( !bigRet )
						log.warn( "删除附件实体文件[" + fileName + "]失败,实体文件及附件记录没有清除!" );
				}
			}

			//删除dom_attachment表中记录
			sess = Context.getDBSession();
			Object[] params = new Long[]{ new Long( attach.getGuid() ) };
			sess.executeUpdate( SQL_DELETE, params );
		} catch (Exception e) {
			throw new E5Exception("删除附件:" + attach.getGuid() + " 不成功", e);
		} finally {
			ResourceMgr.closeQuietly(sess);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.founder.e5.edit.dom.AttachManager#updateAttachProperty(com.founder.e5.edit.dom.Attach)
	 */
	public void updateAttachProperty( Attach attach ) throws E5Exception
	{
		StringBuffer sql = new StringBuffer();
		sql.append( "update " ).append( TABLE_DOM_ATTACHES ).append(
				" set ATT_TOPIC=?,ATT_CONTENT=?,DOCLIBID=?,DOCID=?,ATT_FORMAT=?,ATT_SIZE=?,ATT_ATTPATH=?" ).append( " where GUID=? " );
		DBSession conn = null;

		try
		{
			conn = Context.getDBSession();
			conn.executeUpdate( sql.toString(), new Object[]{ 
							attach.getAttTopic(), attach.getAttContent(),
							new Integer( attach.getDocLibID() ), new Long( attach.getDocID() ),
							attach.getAttFormat(), new Long( attach.getAttSize() ),
							attach.getAttPath(), new Long( attach.getGuid() )
							} , new int[]{Types.VARCHAR,Types.VARCHAR,Types.NUMERIC,Types.NUMERIC,Types.VARCHAR,Types.NUMERIC,Types.VARCHAR,Types.NUMERIC }
			);
		}
		catch ( Exception e )
		{
			throw new E5Exception( "修改附件:" + attach.getGuid() + "不成功", e );
		}
		finally
		{
			ResourceMgr.closeQuietly(conn);
		}
	}

	public Attach getAttach( long mediaID ) throws E5Exception
	{
		Attach Attach = null;
		StringBuffer sql = new StringBuffer();
		DBSession conn = null;
		IResultSet rs = null;
		try
		{
			sql.append( "select * from " ).append( TABLE_DOM_ATTACHES )
				.append(" where GUID=?" );
			conn = Context.getDBSession();
			rs = conn.executeQuery( sql.toString(), new Object[] { new Long( mediaID ) } );

			if ( rs.next() )
			{
				Attach = new Attach();
				Attach.setGuid( rs.getLong( "GUID" ) );
				Attach.setDocID( rs.getLong( "DOCID" ) );
				Attach.setDocLibID( rs.getInt( "DOCLIBID" ) );
				Attach.setAttType( rs.getInt( "ATT_TYPE" ) );
				Attach.setAttFormat( rs.getString( "ATT_FORMAT" ) );
				Attach.setAttPath( rs.getString( "ATT_ATTPATH" ) );
				Attach.setAttTopic( rs.getString( "ATT_TOPIC" ) );
				Attach.setAttContent( rs.getString( "ATT_CONTENT" ) );
				Attach.setAttSize( rs.getInt( "ATT_SIZE" ) );
				Attach.setUser(rs.getInt("USERID"));
			}
		}
		catch ( Exception e )
		{
			log.error( "取附件：" + mediaID + "的信息出错！", e );
			throw new E5Exception( e );
		}
		finally
		{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return Attach;
	}

	/**
	 * 根据文档ID取的其对应的所有附件
	 * 
	 * @param documentID
	 * @param docLibID
	 * @return
	 * @throws E5Exception
	 */
	public List<Attach> getAttaches( long documentID, int docLibID ) throws E5Exception
	{
		List<Attach> attaches = new ArrayList<Attach>();
		StringBuffer sql = new StringBuffer();
		DBSession conn = null;
		IResultSet rs = null;
		try {
			sql.append( "select * from DOM_ATTACHMENT where DOCLIBID=? and DOCID=? order by GUID" );
			conn = Context.getDBSession();
			rs = conn.executeQuery( sql.toString(), new Object[] { new Integer( docLibID ),
					new Long( documentID ) } );
			Attach Attach = null;
			while ( rs.next() ) {
				long guid = rs.getLong( "GUID" );
				
				Attach = new Attach();
				Attach.setGuid( guid );
				Attach.setDocID( rs.getLong( "DOCID" ) );
				Attach.setDocLibID( rs.getInt( "DOCLIBID" ) );
				Attach.setAttType( rs.getInt( "ATT_TYPE" ) );
				Attach.setAttFormat( rs.getString( "ATT_FORMAT" ) );
				Attach.setAttPath( rs.getString( "ATT_ATTPATH" ) );
				Attach.setAttTopic( rs.getString( "ATT_TOPIC" ) );
				Attach.setAttContent( rs.getString( "ATT_CONTENT" ) );
				Attach.setAttSize( rs.getInt( "ATT_SIZE" ) );
				Attach.setUser(rs.getInt("USERID"));
				attaches.add( Attach );
			}
		} catch ( Exception e ) {
			log.error( "查询文档ID：" + documentID + "的附件出错！", e );
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return attaches;
	}
	
	public void updateAttach( Attach attach ) throws E5Exception
	{
		DBSession conn = null;
		try
		{
			conn = Context.getDBSession();
			assistantAddAttach( attach );
			// set filename
			String fileName = AttachUtil.getAttachPath() + File.separator
					+ AttachUtil.getAttachName( attach );

			log.debug( fileName );

			// get oracle bfile directory
			String dir = getBfileDir();// BFILE_DIR;
			log.debug( dir );

			// set video thumbnail
//			IBfile bfile = LobHelper.createBfile( dir, fileName, attach.getIn(), BfileType.EXTFILE );

			if ( attach.getAttTopic() == null )
				attach.setAttTopic( "" );

			conn.executeUpdate( SQL_UPDATE, new Object[] { dir, new Long( attach.getAttSize() ),
					new Long( attach.getGuid() ) }, new int[] { Types.VARCHAR, Types.NUMERIC,
					Types.NUMERIC, } );
		}
		catch ( Exception e )
		{
			throw new E5Exception( "修改附件失败", e );
		}
		finally
		{
			ResourceMgr.closeQuietly(conn);
		}

	}
	
	private String getBfileDir() throws E5Exception
	{
//		SysConfigReader sr = SysFactory.getSysConfigReader();
//		String dir = sr.get( 0, APPLICATION_NAME, ATTACH_DEV_NAME );
//		return dir;
		return null;
	}
	
	public long create() {
		
		String initSql = "insert into " + TABLE_DOM_ATTACHES + "(GUID,DOCLIBID,DOCID)"+ " values(?,?,?)";
		DBSession sess = null;
		long refID = 0;
		
		try {
			sess = Context.getDBSession();
			refID = EUID.getID( ID_IMAGE );
			sess.executeUpdate(initSql, 
					new Object[]{new Long(refID), new Integer(0), new Integer(0)});
		} catch (Exception e) {
			e.printStackTrace();
			try{
				sess.rollbackTransaction();
			}catch(SQLException el){
				
			}
		}finally{
			ResourceMgr.closeQuietly(sess);
		}
		
		return refID;
	}

	public int copy(int refID) {
		
		int newRefID = 0;
		
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat();
		format.applyPattern( "yyyy" );
		String year = format.format( now );
		format.applyPattern( "MM" );
		String month = format.format( now );
		
		try {
			Attach attach = getAttach( refID);
			newRefID = (int) create();
			
			String fileName =attach.getAttPath().substring(attach.getAttPath().lastIndexOf( "/" ) + 1 );
			
			InputStream in = getAttachBfile( refID ).openFile();
			
			writeAttach(newRefID,
					LobHelper.createBfile("附件存储", "/" + year + "/" + month + "/" + newRefID + fileName.substring( fileName.lastIndexOf( "." ) ),in )
			);
			if( in != null){
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return newRefID;
	}
	
	public int copy( int refID, String userCode ) 
	{	
		int newRefID = 0;
		
		Calendar calender = Calendar.getInstance();
		calender.setTime( new Date() );
		int year = calender.get(Calendar.YEAR);
		int month = calender.get(Calendar.MONTH)+1;
		
		try 
		{
			Attach attach = getAttach( refID);
			newRefID = (int) create();
			String attachPath = attach.getAttPath();
			
			String fileName = attachPath.substring(attach.getAttPath().lastIndexOf( "/" ) + 1 );
			
			//拼接复制出来的附件的文件名
			StringBuffer newAttachPathSB = new StringBuffer();
			newAttachPathSB.append( userCode )
							.append( year )
							.append( month )
							.append( calender.get(Calendar.DAY_OF_MONTH) )
							.append( calender.get(Calendar.HOUR_OF_DAY) )
							.append( calender.get(Calendar.MINUTE) )
							.append( calender.get(Calendar.SECOND) )
							.append( newRefID );
			
			InputStream in = getAttachBfile( refID ).openFile();
			
			//获取附件的存储设备
			String arrayLarge[] = StringUtils.splitStrictly(attachPath, ",");
			String deviceName= arrayLarge[0];
			
			writeAttach(newRefID,
					LobHelper.createBfile(deviceName , "/" + year + "/" + month + "/" + newAttachPathSB.toString() + fileName.substring( fileName.lastIndexOf( "." ) ),in )
			);
			
			//设置新附件的其他信息()
			writeAttInfo( newRefID, attach );
			
			if( in != null)
			{
				in.close();
			}
		} 
		catch (Exception e) 
		{
			log.error( "复制附件异常,", e );
		}
		finally
		{}
		return newRefID;
	}
	
	/***
	 * 设置新附件的其他信息  附件类型,附件格式,附件大小(字节)
	 * @param guid
	 * @param attach
	 */
	private void writeAttInfo( long guid, Attach attach )
	{
		DBSession sess = null;
		String sql = "update " + TABLE_DOM_ATTACHES + " set ATT_TYPE =?,ATT_FORMAT=?,ATT_SIZE=? where guid=?";
		try
		{
			sess=Context.getDBSession();
			sess.executeUpdate(sql, new Object[]{ new Integer(attach.getAttType()), attach.getAttFormat(), new Long(attach.getAttSize()), new Long(guid)});
		}
		catch(Exception e)
		{
			log.error( e );
		}
		finally
		{
			ResourceMgr.closeQuietly(sess);
		}
	}
	
	public IBfile getAttachBfile(int refID) {
		
		IBfile bfile = null;
		DBSession sess = null;
		IResultSet rs = null;
		String sql = "select ATT_ATTPATH FROM " + TABLE_DOM_ATTACHES + " where guid=?";
		
		try{
			sess = Context.getDBSession();
			rs = sess.executeQuery(sql, new Object[]{ new Integer(refID) });
			if(rs.next()){
				bfile = rs.getBfile( 1 );
			}
		}catch(Exception e){
			e.printStackTrace();
			try{
				sess.rollbackTransaction();
			}catch(SQLException el){
				
			}
		} finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(sess);
		}
		return bfile;
	}

	public void writeAttach(int refID, IBfile attachBfile) {
		
		DBSession sess = null;
		String sql = "update " + TABLE_DOM_ATTACHES + " set ATT_ATTPATH =? where guid=?";
		try{
			sess=Context.getDBSession();
			sess.executeUpdate(sql, new Object[]{attachBfile, new Long(refID)});
		}catch(Exception e){
			
		}finally{
			ResourceMgr.closeQuietly(sess);
		}
	}

}