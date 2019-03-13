<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.founder.enp.ueditor.*" %>
<%@ page import="com.founder.enp.conf.ConfigManager" %>
<%@ page import="java.awt.Rectangle" %>
<%@ page import="java.util.*" %>

<%
String articleId = request.getParameter("articleId"); //稿件id
String imgTitle = request.getParameter("imgTitle"); //图片title
String imagePath = request.getParameter("imagePath"); //图片源
String imagePumpW =  request.getParameter("imagePumpW"); //小图宽度
String imagePumpH = request.getParameter("imagePumpH"); //小图高度

//获取图片后缀名
String suffixName = imagePath.substring(imagePath.lastIndexOf(".")+1);
String pumpImgName = imagePath.substring(imagePath.lastIndexOf("/")+1, imagePath.lastIndexOf(".")) + "_small." + suffixName;
String relativePath = imagePath.substring(0, imagePath.lastIndexOf("/"));

//加载源图
CropZoomImgUtil imgUtil = CropZoomImgUtil.loadImg(ConfigManager.getEnpData() + imagePath.substring("/data".length()));
//缩放源图
imgUtil.zoomImg((int)Double.parseDouble(imagePumpW), (int)Double.parseDouble(imagePumpH));

//保存压缩后的图片
String dataAfterPath = relativePath.substring("/data".length()) + "/"+ pumpImgName;
imgUtil.saveCropImg(articleId, imgTitle, ConfigManager.getEnpData() + dataAfterPath, suffixName);

//返回压缩后图片的路径
response.getWriter().print("{'imgPath':'/data"+dataAfterPath+"'}");
%>