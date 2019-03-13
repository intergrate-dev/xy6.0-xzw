package com.founder.xy.set.web;

import com.founder.e5.doc.Document;

/**
 * 文件发布到外网前需要读的信息：是否已配置站点的资源目录、资源目录、文件所属的文档对象
 */
public class ResDir {
	public boolean noSiteDir;
	public Document ownerDoc;
	public String[] dirs;
}
