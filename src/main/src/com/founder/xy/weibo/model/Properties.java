package com.founder.xy.weibo.model;

import net.sf.json.JSONObject;

/**
 * ΢����չ����
 * @author huangjintao
 *
 */
public class Properties
{
private String weiboType;//΢�����ڷ���������(qq/sina)
private String type;//΢������(text/pic/music/video/multi)
private String musicUrl;//����URL
private String musicTitle;//������
private String musicAuthor;//������
private String videoUrl;//��ƵURL
private long reposts;//ת����
private long comments;//������

public String getWeiboType()
{
	return weiboType;
}
public void setWeiboType(String weiboType)
{
	this.weiboType = weiboType;
}
public long getReposts()
{
	return reposts;
}
public void setReposts(long reposts)
{
	this.reposts = reposts;
}
public long getComments()
{
	return comments;
}
public void setComments(long comments)
{
	this.comments = comments;
}
public String getType()
{
	return type;
}
public void setType(String type)
{
	this.type = type;
}
public String getMusicUrl()
{
	return musicUrl;
}
public void setMusicUrl(String musicUrl)
{
	this.musicUrl = musicUrl;
}
public String getMusicTitle()
{
	return musicTitle;
}
public void setMusicTitle(String musicTitle)
{
	this.musicTitle = musicTitle;
}
public String getMusicAuthor()
{
	return musicAuthor;
}
public void setMusicAuthor(String musicAuthor)
{
	this.musicAuthor = musicAuthor;
}
public String getVideoUrl()
{
	return videoUrl;
}
public void setVideoUrl(String videoUrl)
{
	this.videoUrl = videoUrl;
}
public static String Object2Json(Properties properties)
{
	JSONObject jsonObject = JSONObject.fromObject(properties);
	return jsonObject.toString();
}
public static Properties Json2Object(String json)
{
	Properties properties = null;
	JSONObject jsonObject = JSONObject.fromObject(json);
	properties = (Properties) JSONObject.toBean(jsonObject,Properties.class);
	return properties;
}
}
