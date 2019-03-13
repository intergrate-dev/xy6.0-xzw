package com.founder.amuc.score;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.member.Member;
import com.founder.amuc.tenant.Tenant;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

/**
 * 计算有效的会员积分的处理器
 * @author Gong Lijie
 * 2014-6-10
 */
public class PeriodProcessor {
  private final String SQL_SUM = "select msMember_ID,sum(msScore) from xy_memberscore where msTime>? and msType<=1 group by msMember_ID";
  private final String SQL_MEMBER = "select SYS_DOCUMENTID, mName, mScore from xy_member where mScore>0";
  private final String SQL_MEMBER_UPDATE = "update xy_member set mScore=? where SYS_DOCUMENTID=?";
  
  /**
   * 计算有效期内的会员积分
   * @throws E5Exception
   */
  public void process() throws E5Exception {
    System.out.println("------开始积分有效期计算服务------" + DateUtils.getTimestamp());
    
    TenantManager tManager = (TenantManager)Context.getBean(TenantManager.class);
    List<Tenant> ts = tManager.getAll();
    for (Tenant tenant : ts) {
      //按租户的设置进行处理
      dealPeriod(tenant);
    }
    
    System.out.println("------结束积分有效期计算服务------" + DateUtils.getTimestamp());
  }
  
  /**
   * 处理一个租户的积分有效期
   * @param t
   * @throws E5Exception
   */
  private void dealPeriod(Tenant t) throws E5Exception {
    //为0表示没设置有效期，不计算
    if (t.getScorePeriod() == 0) return;
    
    //计算有效期开始日期，得到每个用户的有效积分
    HashMap<Integer, Integer> scores = sumScore(t);
    
    //一个一个修改会员的积分，差额记在会员积分表里（负分，类型为手工扣减，描述为“过期积分扣减”）
    changeScores(t, scores);
  }
  
  /**
   * 得到每个用户的有效积分
   * @return <memberID, sumScore>
   * @throws E5Exception 
   */
  private HashMap<Integer, Integer> sumScore(Tenant t) throws E5Exception {
    HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
    
    //得到有效期的开始日期
    Date beginDate = _beginDate(t);
    
    //得到租户对应的会员积分汇总sql
    String sql = _sqlSum(t);
    
    DBSession conn = null;
    IResultSet rs = null;
    try {
      conn = Context.getDBSession();
      rs = conn.executeQuery(sql, new Object[]{beginDate});
      while (rs.next()) {
        scores.put(rs.getInt(1), rs.getInt(2));
      }
    } catch (Exception e) {
      throw new E5Exception(e);
    } finally {
      ResourceMgr.closeQuietly(rs);
      ResourceMgr.closeQuietly(conn);
    }
    return scores;
  }
  /**
   * 读会员表，找出每个有积分的会员：
   * 设置积分=min（积分，与有效期内的积分）
   * 差额记录在会员积分表里（负分，类型为手工扣减，描述为“过期积分扣减”）。
   * @throws E5Exception 
   */
  private void changeScores(Tenant t, HashMap<Integer, Integer> scores) throws E5Exception {
    String sql = _sqlMemberUpdate(t);
    
    //取出所有带积分的会员
    List<Member> members = getMembers(t);
    
    for (Member member : members) {
      if (!scores.containsKey(member.getId()))
        continue;
      
      //每个会员进行比较，有效期内积分<目前积分，则修改
      int score = scores.get(member.getId());
      if (score < member.getScore()) {
        int diff = member.getScore() - score;
        
        //修改会员表的积分
        InfoHelper.executeUpdate(sql, new Object[]{score, member.getId()});
        
        //加一条扣减记录
        createConvert(diff, member.getId(), member.getName(), t.getCode());
      }
    }
  }

  /**
   * 按照有效期设置，取出有效期开始日期：
   * 如有效期类型是自然年，则有效期开始日期=（当前年-N+1）年1月1日
   * 如有效期类型是时间间隔，则有效期开始日期=（当前年-N）年当前月当前日
   * @param tenant
   */
  private Date _beginDate(Tenant t) {
    //自然年，=（当前年-N+1）年1月1日
    if (t.getType() == 0) {
      Calendar now = Calendar.getInstance();
      now.set(Calendar.YEAR, now.get(Calendar.YEAR) - t.getScorePeriod() + 1);
      now.set(Calendar.MONTH, 0);
      now.set(Calendar.DATE, 1);
      
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.clear(Calendar.MINUTE);
      now.clear(Calendar.SECOND);
      now.clear(Calendar.MILLISECOND);
      
      return now.getTime();
    } else {
      //时间间隔，=（当前年-N）年当前月当前日
      Calendar now = Calendar.getInstance();
      now.set(Calendar.YEAR, now.get(Calendar.YEAR) - t.getScorePeriod());
      
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.clear(Calendar.MINUTE);
      now.clear(Calendar.SECOND);
      now.clear(Calendar.MILLISECOND);
      
      return now.getTime();
    }
  }

  //租户不同，积分汇总查的表不同
  private String _sqlSum(Tenant t) throws E5Exception {
    return InfoHelper.replaceSQL(t.getCode(), SQL_SUM, Constant.DOCTYPE_MEMBERSCORE, "xy_memberscore");
  }
  //租户不同，会员查的表不同
  private String _sqlMember(Tenant t) throws E5Exception {
    return InfoHelper.replaceSQL(t.getCode(), SQL_MEMBER, Constant.DOCTYPE_MEMBER, "xy_member");
  }
  //租户不同，会员积分修改语句查的表不同
  private String _sqlMemberUpdate(Tenant t) throws E5Exception {
    return InfoHelper.replaceSQL(t.getCode(), SQL_MEMBER_UPDATE, Constant.DOCTYPE_MEMBER, "xy_member");
  }
  
  //取出所有带积分的会员，以备扣减无效积分
  private List<Member> getMembers(Tenant t) throws E5Exception {
    List<Member> members = new ArrayList<Member>();
    
    //取出积分>0的会员（没有积分余额的不可能有过期）
    String sql = _sqlMember(t);
    DBSession conn = null;
    IResultSet rs = null;
    try {
      conn = Context.getDBSession();
      rs = conn.executeQuery(sql, null);
      while (rs.next()) {
        Member b = new Member();
        b.setId(rs.getInt(1));
        b.setName(rs.getString(2));
        b.setScore(rs.getInt(3));
        
        members.add(b);
      }
    } catch (Exception e) {
      throw new E5Exception(e);
    } finally {
      ResourceMgr.closeQuietly(rs);
      ResourceMgr.closeQuietly(conn);
    }
    return members;
  }
  //产生会员积分扣减记录
  private void createConvert(int score, int id, String name, String code) throws E5Exception {
    if (score > 0) score = -1 * score;
    
    DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERSCORE, code);
    
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
    doc.setFolderID(docLib.getFolderID());
    doc.setDeleteFlag(0);
    doc.set("msMember_ID", id);
    doc.set("msMember", name);
    doc.set("msTenantCode", code);
    doc.set("msTime", DateUtils.getTimestamp());
    doc.set("msEvent", "过期积分扣减");
    doc.set("msMemo", "积分有效期检查：扣减过期积分");
    doc.set("msRuleType", -1);
    doc.set("msIsApproved", 1);
    doc.set("msScore", score);
    doc.set("msType", 2); //手工扣减
    
    docManager.save(doc);
  }
}
