package com.founder.amuc.commons;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.founder.e5.context.E5Exception;

/**
 * Http访问的辅助类
 * @author Yan He
 */
public class HTTPHelper {

	public static void checkValid(ServletRequest req) throws Exception {
	  HttpServletRequest request = (HttpServletRequest) req;
    request.setCharacterEncoding("utf-8");
    Enumeration<String> headers = request.getHeaderNames();
    while(headers.hasMoreElements()){
      String h = headers.nextElement();
    }
    String sign = request.getHeader("program-sign");
    String devid = request.getHeader("devid");
    String version = request.getHeader("version");
    String time = request.getHeader("timestamp");
    long t = time==null?0:Long.valueOf(time);
    String token = request.getHeader("token");
    String random = request.getHeader("random");
    String params = request.getHeader("program-params");
    boolean valid = params==null?false:params.matches("^[a-zA-Z_]+(,[a-zA-Z_]+)*$");
    if(sign==null||devid==null||version==null||random==null||sign==null||token==null||!valid||t<1451577600000l){
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"arguments are not enough");
    }
    StringBuffer buffer = new StringBuffer("devid=").append(devid).append("&random=").append(random)
        .append("&timestamp=").append(time).append("&token=").append(token).append("&version=").append(version);
    StringBuffer sBuffer = new StringBuffer();
    boolean paramsValid = true;
    for(String p:params.split(",")){
      String v = request.getParameter(p);
      if(v==null){
        paramsValid = false;
        break ;
      }
      sBuffer.append(p).append("=").append(v).append("&");
    }
    if(paramsValid){
      sBuffer.append("secret=").append(MD5Util.md5(buffer.toString()));
      String md5 = MD5Util.md5(sBuffer.toString());
      if(!sign.equals(md5))
        paramsValid = false;
    }
    if(!paramsValid){
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "sign is not invalid.");
    }
	}
}
