<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.servlet.ServletContext"%>
<%@ page import="javax.servlet.http.HttpServletRequest"%>
<%@page import="com.founder.xy.commons.StringUtils"%>
<% 
    //仅做示例用，请自行修改
	String path = "watermarkimage";
	String imgStr ="";
	String realpath = getRealPath(request,path)+"/"+path;
	List files = getFiles(realpath,new ArrayList());
	for(int i=0;i<files.size();i++){
		imgStr+=StringUtils.replace(((File)files.get(i)).getPath(),getRealPath(request,path),"")+"ue_separate_ue";
	}
	if(imgStr!=""){
        imgStr = StringUtils.replace(imgStr.substring(0,imgStr.lastIndexOf("ue_separate_ue")),File.separator,"/").trim();
    }
	out.print(imgStr);		
%>
<%!
public List getFiles(String realpath, List files) {
	
	File realFile = new File(realpath);
	if (realFile.isDirectory()) {
		File[] subfiles = realFile.listFiles();
		for(int i=0;i<subfiles.length;i++){
			if(subfiles[i].isDirectory()){
				getFiles(subfiles[i].getAbsolutePath(),files);
			}else{
				if(!getFileType(subfiles[i].getName()).equals("")) {
					files.add(subfiles[i]);
				}
			}
		}
	}
	return files;
}

public String getRealPath(HttpServletRequest request,String path){
	ServletContext application = request.getSession().getServletContext();
	String str = application.getRealPath(request.getServletPath());
	return new File(str).getParent();
}

public String getFileType(String fileName){
	String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
	Iterator type = Arrays.asList(fileType).iterator();
	while(type.hasNext()){
		String t = (String)type.next();
		if(fileName.endsWith(t)){
			return t;
		}
	}
	return "";
}
%>