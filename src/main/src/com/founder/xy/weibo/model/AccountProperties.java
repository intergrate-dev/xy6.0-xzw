package com.founder.xy.weibo.model;

import net.sf.json.JSONObject;

/**
 * ΢���˻���չ����
 * @author huangjintao
 *
 */
public class AccountProperties
{
	private String uid;//΢���˻���ӦID(Ŀǰ������΢����ȡ�˻���Ϣʹ�ã���Ѷ��ȡ�˻���Ϣ����˲���)
	private long friends;//��ע��
	private long followers;//��˿��
	
	public String getUid()
	{
		return uid;
	}
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	public long getFriends()
	{
		return friends;
	}
	public void setFriends(long friends)
	{
		this.friends = friends;
	}
	public long getFollowers()
	{
		return followers;
	}
	public void setFollowers(long followers)
	{
		this.followers = followers;
	}
	public static String Object2Json(AccountProperties accountProperties) {
		JSONObject jsonObject = JSONObject.fromObject(accountProperties);
		return jsonObject.toString();
	}

	public static AccountProperties Json2Object(String json) {
		AccountProperties accountProperties = null;
		JSONObject jsonObject = JSONObject.fromObject(json);
		accountProperties = (AccountProperties) JSONObject.toBean(jsonObject,
				AccountProperties.class);
		return accountProperties;
	}

}
