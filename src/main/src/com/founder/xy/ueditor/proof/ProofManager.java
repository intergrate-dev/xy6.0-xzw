package com.founder.xy.ueditor.proof;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.founder.xy.commons.UrlHelper;

import net.sf.json.JSONObject;

@Service
public class ProofManager {
	//获取配置参数
	public CheckExtendInfo getParams(){
		CheckExtendInfo checkExtenInfo = new CheckExtendInfo();
		return checkExtenInfo;
	}
	//其它参数配置:检查句子 领导人排序 标点符号
	public CheckType getOtherParams() {
		CheckType checkType = new CheckType();
		return checkType;
	}
	public String getCheckArticlePID()
	{
		String res = "";
		HTTPRequest request;
		try {
			request = new HTTPRequest(new URL(UrlHelper.proofUrl()));
			request.setTimeout(10000);
			request.setSOAPAction("http://tempuri.org/GetCheckArticlePID");
			request.setPostData("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><GetCheckArticlePID xmlns=\"http://tempuri.org/\" xmlns:a=\"http://schemas.datacontract.org/2004/07/TestWinform.CheckWordsService\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"/></s:Body></s:Envelope>");
			String[] webpage = request.read();
			res = Arrays.toString(webpage);
			res = getParam(res,"PID");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 提交检查内容（同步）
	 * @param pid		授权号
	 * @param content	检查内容
	 * @param info		参数配置
	 * @param type		检查类型
	 * @return			xml结果信息
	 */
	public String CheckArticle(String pid,String content,CheckExtendInfo info,CheckType type){
		content = content.replaceAll("&","&amp;");
		
		content = content.replaceAll("<","&lt;");
		content = content.replaceAll(">","&gt;");
		content = content.replaceAll("\n","\r\n");
		String res = "";
		HTTPRequest request;
		try {			
			request = new HTTPRequest(new URL(UrlHelper.proofUrl()));
			request.setTimeout(10000);
			request.setSOAPAction("http://tempuri.org/UpLoadCheckArticle");
			String postdata = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><UpLoadCheckArticle xmlns=\"http://tempuri.org/\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><article>" + content + "</article><token/><pid>" + pid + "</pid><checkType><CheckLeaderSort>" + type.checkLeaderSort + "</CheckLeaderSort><CheckMatchDots>" + type.checkMatchDots + "</CheckMatchDots><CheckSentence>" + type.checkSentence + "</CheckSentence></checkType><checkExtendInfo><ChkDefault>" + info.chkDefault + "</ChkDefault><ChkEng>" + info.chkEng + "</ChkEng><ChkReturnResultType>" + info.chkReturnResultType + "</ChkReturnResultType><FanTi>" + info.fanTi + "</FanTi><HtmlTag>" + info.htmlTag + "</HtmlTag><Leader>" + info.leader + "</Leader><Needsug>" + info.needsug + "</Needsug><ProfType>" + info.profType + "</ProfType><TaiWan>" + info.taiWan + "</TaiWan><UserErr>" + info.userErr + "</UserErr><Weight>" + info.weight + "</Weight></checkExtendInfo></UpLoadCheckArticle></s:Body></s:Envelope>";
			res = request.sendPost(UrlHelper.proofUrl(), postdata, false);
			while (true) {
				int prcrent = Integer.parseInt(getCheckArticleProgress(pid));
				if (prcrent == 100){
					break;
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			res = getCheckArticleResult(pid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 获取检查进度
	 * @param pid	授权号
	 * @return		xml结果信息
	 */
	public String getCheckArticleProgress(String pid){
		String res = "";
		HTTPRequest request;
		try {
			request = new HTTPRequest(new URL(UrlHelper.proofUrl()));
			request.setTimeout(10000);
			request.setSOAPAction("http://tempuri.org/GetCheckArticleProgress");
			request.setPostData("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><GetCheckArticleProgress xmlns=\"http://tempuri.org/\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><pid>" + pid + "</pid><token/></GetCheckArticleProgress></s:Body></s:Envelope>");
			String[] webpage = request.read();
			res = Arrays.toString(webpage);
			res = getParam(res,"GetCheckArticleProgressResult");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 获取检查结果
	 * @param pid	授权号
	 * @return		xml结果信息
	 */
	public String getCheckArticleResult(String pid) {
		String res = "";
		HTTPRequest request;
		try {
			request = new HTTPRequest(new URL(UrlHelper.proofUrl()));
			request.setTimeout(10000);
			request.setSOAPAction("http://tempuri.org/GetCheckArticleResult");
			String postdata = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><GetCheckArticleResult xmlns=\"http://tempuri.org/\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><pid>" + pid + "</pid><token/></GetCheckArticleResult></s:Body></s:Envelope>";
			res = request.sendPost(UrlHelper.proofUrl(), postdata, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public String getParam(String str,String node) {
		Pattern r = Pattern.compile("<" + node + ">([\\s\\S.]*?)<\\/" + node + ">");
		Matcher m = r.matcher(str);
		if (m.find( )) {
			return m.group(1);
		} else {
			return "";
		}
	}
	/**
	 * 解析xml错误信息
	 * @param res	xml字符串
	 * @return		错误集合
	 */
	public ArrayList<ErrorType> getError(String res) {
		ArrayList<ErrorType> err = new ArrayList<ErrorType>();
		res = res.replaceAll("&amp;", "&");
		res = res.replaceAll("&lt;", "<");
		res = res.replaceAll("&gt;", ">");
		
        String pattern = "<CheckResult>([\\s\\S.]*?)<\\/CheckResult>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(res); 
        while(m.find()) {
          String one = m.group(1);
          String CorWord = getParam(one, "CorWord");
          ArrayList<String> CorWordList = new ArrayList<String>();
          String pattern2 = "<string>([\\s\\S.]*?)<\\/string>";
          Pattern r2 = Pattern.compile(pattern2);
          
          Matcher m2 = r2.matcher(CorWord); 
    
          while(m2.find()) {
        	  String cor = m2.group(1);
        	  CorWordList.add(cor);
          }
          
          String ErrWord = getParam(one, "ErrWord");
          int Pos = Integer.parseInt(getParam(one, "Pos"));
          String level = getParam(one, "level");
          
          ErrorType et = new ErrorType();
          et.CorWord = CorWordList;
          et.ErrWord = ErrWord;
          et.Pos = Pos;
          et.level = level;
          err.add(et);
       }
        return err;
	}
	public String getJson(String strTemp, ArrayList<ErrorType> checkWordsResult) {
		ProofMessage proofMsg = new ProofMessage();
		try {
			if (checkWordsResult.size() <= 0) {
				return getJson(proofMsg);
			}
			initProofMessage(proofMsg,checkWordsResult.size());
			for (int i = 0; i < checkWordsResult.size(); i++) {
				String errWord = checkWordsResult.get(i).ErrWord;
				proofMsg.ErrorWords[i]=errWord;
				ArrayList<String> colWordList = checkWordsResult.get(i).CorWord;
				String tempStr="";
				for (int j = 0; j < colWordList.size(); j++) {
					if (!colWordList.get(j).equals(""))
						tempStr += colWordList.get(j) + ",";
				}
				proofMsg.SuggestWords[i]=tempStr;
				proofMsg.ErrorLevels[i]=checkWordsResult.get(i).level;
				
				int pos = checkWordsResult.get(i).Pos;
				if(pos>0 && proofMsg.ErrorWords[i]!=null) {
					int end = strTemp.indexOf("\n",pos);
					if(end==-1) {
						end=strTemp.length();
					} else {
						end+=1;
					}
					if(end>pos+100) {
						end = pos+15;
					}
					if(end>strTemp.length()) {
						end = strTemp.length();
					}
					String temp1 = strTemp.substring(pos, end);
					proofMsg.SourceSentence[i]=temp1;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getJson(proofMsg);
	}
	private String getJson(ProofMessage proofMsg){
		JSONObject jsonObj = JSONObject.fromObject(proofMsg);
		String jsonStr = jsonObj.toString();
		jsonStr = jsonStr.replaceAll("\"errorCount\"", "\"ErrorCount\"");
		jsonStr = jsonStr.replaceAll("\"errorLevels\"", "\"ErrorLevels\"");
		jsonStr = jsonStr.replaceAll("\"errorWords\"", "\"ErrorWords\"");
		jsonStr = jsonStr.replaceAll("\"sourceSentence\"", "\"SourceSentence\"");
		jsonStr = jsonStr.replaceAll("\"leadErrorCount\"", "\"LeadErrorCount\"");
		jsonStr = jsonStr.replaceAll("\"suggestWords\"", "\"SuggestWords\"");
		jsonStr = jsonStr.replaceAll("\"leaderSortErrors\"", "\"LeaderSortErrors\"");
		jsonStr = jsonStr.replaceAll("\"errorPoses\"", "\"errorPoses\"");
		return jsonStr;
	}
	private void initProofMessage(ProofMessage proofMsg,int count){
		proofMsg.ErrorCount=count;
		proofMsg.ErrorLevels = new String[count];
		proofMsg.ErrorWords = new String[count];
		proofMsg.SourceSentence = new String[count];
		proofMsg.SuggestWords = new String[count];
		proofMsg.LeadErrorCount="0";
	}
}
