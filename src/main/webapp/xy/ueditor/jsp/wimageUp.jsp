<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
  <%@ page import="com.founder.enp.ueditor.*" %>
    <%
    request.setCharacterEncoding("utf-8");
    Uploader up = new Uploader(request); 
    
    String picurl=request.getParameter("picurl");
    String watermarkcontent=request.getParameter("wpimageword");
    String wpimagebgurl=request.getParameter("wpimagebgurl");
    String watermarktype=request.getParameter("wpimagetype");
    String wpimagedest=request.getParameter("wpimagedest");
    String watermarkPosition=request.getParameter("wpimagewz");
    String wpimagebgColor=request.getParameter("wpimagebgColor");
    up.uploadimge(picurl,watermarkcontent,wpimagebgurl,watermarktype,wpimagedest,watermarkPosition,wpimagebgColor);
    %>
