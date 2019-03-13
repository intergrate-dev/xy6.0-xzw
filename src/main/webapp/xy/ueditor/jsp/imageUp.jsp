    <%@ page language="java" contentType="text/html; charset=utf-8"
        pageEncoding="utf-8"%>
    <%@ page import="CMSEditor.WebContent.WEB.xy.ueditor.*" %>
      <%@ page import="java.util.*" %>

    <%
    request.setCharacterEncoding("utf-8");
    String isTitleImg = request.getParameter("isTitleImg");
  // Map  aa = request.getParameterMap();
  // System.out.println(aa);
  // String pictitle = request.getParameter("pictitle");  
    Uploader up = new Uploader(request);
    String articleId = request.getParameter("articleId");
   // System.out.println("上传图片articleId："+articleId);
    if(articleId!=null)
    {
      up.setArticleId(Long.parseLong(articleId)); //设置稿件id
    }
    else
    {
       System.out.println("Error:没有获取articleId："+articleId);
    }
    up.setAttType("1"); //设置附件类型，1:表示内容附件
    up.setSavePath("upload");
    if(request.getParameter("docLibId")!=null && !"".equals(request.getParameter("docLibId"))){
		up.setDocLibId(Integer.parseInt(request.getParameter("docLibId")));
	}
    String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
    up.setAllowFiles(fileType);
    up.setMaxSize(10000); //单位KB
    up.upload();
    //压缩标题图片
    //if("1".equals(isTitleImg)) {
    //	double maxWidth = 500; //最大宽度
    //	double maxHeight = 210; //最大高度
    //	ImageZoomUtil.zoom(up.getUrl(), maxWidth, maxHeight);
    //}
    response.getWriter().print("{'original':'"+up.getOriginalName()+"','url':'"+up.getUrl()+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}");
    %>
