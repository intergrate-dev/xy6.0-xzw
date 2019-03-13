<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.founder.enp.ueditor.*" %>
<%@ page import="com.founder.enp.conf.ConfigManager" %>
<%@ page import="java.awt.Rectangle" %>
<%@ page import="java.util.*" %>

<%
String articleId = request.getParameter("articleId"); //稿件id
String imgTitle = request.getParameter("imgTitle"); //图片title
String imagePath = request.getParameter("imagePath"); //图片源
String imageW = request.getParameter("imageW"); //图片宽
String imageH = request.getParameter("imageH"); //图片高
String selectorX =  request.getParameter("selectorX"); //选中区位置X
String selectorY = request.getParameter("selectorY"); //选中区位置Y
String selectorW = request.getParameter("selectorW"); //选中区位置宽
String selectorH = request.getParameter("selectorH"); //选中区位置高

//获取图片后缀名
String suffixName = imagePath.substring(imagePath.lastIndexOf(".")+1);
String cropImgName = ""+new Random().nextInt(10000) + System.currentTimeMillis() + "." + suffixName;
String relativePath = imagePath.substring(0, imagePath.lastIndexOf("/"));

//加载源图
CropZoomImgUtil imgUtil = CropZoomImgUtil.loadImg(ConfigManager.getEnpData() + imagePath.substring("/data".length()));
//缩放源图
imgUtil.zoomImg((int)Double.parseDouble(imageW), (int)Double.parseDouble(imageH));
//裁剪图片
Rectangle sourceImgRec = new Rectangle(0,0,(int)Double.parseDouble(imageW),(int)Double.parseDouble(imageH));
Rectangle cropImgRec = new Rectangle((int)Double.parseDouble(selectorX),(int)Double.parseDouble(selectorY),(int)Double.parseDouble(selectorW),(int)Double.parseDouble(selectorH));
imgUtil.cutImg(sourceImgRec, cropImgRec);

//保存裁剪后的图片
String dataAfterPath = relativePath.substring("/data".length()) + "/"+ cropImgName;
imgUtil.saveCropImg(articleId, imgTitle, ConfigManager.getEnpData() + dataAfterPath, suffixName);

//返回裁剪后图片的路径
response.getWriter().print("{'imgPath':'/data"+dataAfterPath+"'}");
%>