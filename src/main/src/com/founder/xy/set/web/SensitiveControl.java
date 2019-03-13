package com.founder.xy.set.web;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.set.SourceManager;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词/非法词的相关操作
 * 规则的增加，删除，敏感词/非法词的过滤
 * method:
 *      1.add(添加规则)、
 *      2.delete(根据规则ID、任务类型删除规则)、
 *      3.deleteall(根据任务类型删除该任务类型下所有的规则)、
 *      4.checkSensitive(检查敏感词/非法词);
 *      5.batchAdd(本地批量导入规则)
 * )
 * code:
 *      0:  操作失败,
 *      1:  操作成功,
 *      2:  其他异常：没有找到敏感词/非法词;
 * type:
 *      type==0 表示是写稿,
 *      type==1 表示是评论;
 * data:
 *      是查询敏感词/非法词的时候的敏感词/非法词信息(
 *      SensitiveWord:敏感词
 *      IllegalWord：非法词
 *      keyNum: 命中的规则在文本中对应的命中的敏感词/非法词的个数,
 *      keywords:命中的敏感词/非法词,
 *      ruleId:规则的id,
 *      position:命中的敏感词/非法词在文中的位置,index从0开始,若是单字,则返回单字位置,比如：1-1;
 *  )
 */
@Controller
@RequestMapping("/xy/wordList")
public class SensitiveControl {

    @Autowired
    SourceManager sourceManager;

    /**
     * 添加敏感词/非法词规则
     *      method 为：add
     */
    @RequestMapping("addSensitiveWord.do")
    public void addSensitiveWord(HttpServletRequest request, HttpServletResponse response, String rule,
                                 String ruleId, String taskType, String method) {
        String result = SensitiveWordControllerHelper.sensitive(method, rule, ruleId, taskType);
        InfoHelper.outputJson(result, response);
    }
    /**
     * 本地批量导入规则
     *      method 为：batchAdd
     */
    @RequestMapping("batchAddSensitiveWord.do")
    public void batchAddSensitiveWord(HttpServletRequest request, HttpServletResponse response, String sensitiveWords) {
        String result = SensitiveWordControllerHelper.sensitive("batchAdd", sensitiveWords);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 删除敏感词/非法词规则
     * method:
     * 有两种删除方式：
     *    1.仅删除指定的单个规则,其方法名method为delete,通过指定ruleId删除
     *    2.删除所有规则,其方法名method为deleteall,即删除所有敏感词/非法词的规则
     */
    @RequestMapping("deleteSensitiveWord.do")
    public void deleteSensitiveWord(HttpServletRequest request, HttpServletResponse response, String ruleId, String taskType){
        String result = SensitiveWordControllerHelper.sensitive("delete", ruleId, taskType);
        InfoHelper.outputJson(result, response);
    }
    @RequestMapping("allDeleteSensitiveWord.do")
    public void allDeleteSensitiveWord(HttpServletRequest request, HttpServletResponse response, String taskType){
        String result = SensitiveWordControllerHelper.sensitive("deleteall", taskType);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 查询敏感词/非法词
     *      method 为：checkSensitive
     */
    @RequestMapping("checkSensitiveWord.do")
    public void checkSensitiveWord(HttpServletRequest request, HttpServletResponse response, String title,
                                   String content, String type) throws Exception {
        String result = SensitiveWordControllerHelper.sensitive("checkSensitive", type, title, content);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping("Sensitive.do")
    public ModelAndView rename(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //1. 取出目标special对象
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String authors = ProcHelper.getUserName(request);
        boolean isNew = (docID == 0); //是否新建

        if (isNew)
            docID = InfoHelper.getNextDocID(DocTypes.SENSITIVE.typeID()); //取ID

        Map<String, Object> model = new HashMap<>();

        String sensitivename="";
        int sentype=0;
        if(!isNew){
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);

            sensitivename = doc.getString("sen_word");
            sentype=doc.getInt("sen_type");
        }


        model.put("docLibID", docLibID);
        model.put("docID", docID);
        model.put("UUID", WebUtil.get(request, "UUID"));

        model.put("sensitivename", sensitivename);
        model.put("sentype",sentype);
        model.put("authors",authors);
        model.put("isNew",(isNew? 0: 1));
        return new ModelAndView("/xy/special/dialog/sensitive", model);
    }

    @RequestMapping("sensiSave.do")
    public String sensiSave(Integer DocLibID, Long DocIDs, String UUID,
        String sensitivename, String sentype, String authors, int isNew
    ) throws Exception{
        String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + DocIDs ;
        try{
            if (!StringUtils.isBlank(sensitivename)) {
                DocumentManager docManager = DocumentManagerFactory.getInstance();
                String[] senList = sensitivename.split(",");
                for (String senName : senList){
                    senName = senName.trim();
                    if(StringUtils.isBlank(senName)){
                        continue;
                    }
                    Document doc;
                    String method="add";
                    int type=Integer.parseInt(sentype);
                    if (isNew == 0) {
                        DocIDs = InfoHelper.getNextDocID(DocTypes.SENSITIVE.typeID()); //取ID
                        doc = docManager.newDocument(DocLibID, DocIDs);
                    }else {
                        doc = docManager.get(DocLibID, DocIDs);
                        method="change";
                    }

                    doc.set("sen_word", senName);
                    doc.set("sen_type", type);
                    doc.set("SYS_AUTHORS", authors);

                    docManager.save(doc);
                    type=0==type?4:5;

                    //通知敏感词服务增加敏感词
                    SensitiveWordControllerHelper.sensitive(method, senName, String.valueOf(DocIDs), String.valueOf(type));
                }
            }
        }catch (Exception e) {
            url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
            return "redirect:" + url;
        }

        return "redirect:" + url;

    }


    @RequestMapping("checkName.do")
    public void checkName(HttpServletRequest request, HttpServletResponse response, Integer docLibId, String sensitiveName,String type) throws Exception{
        JSONObject json = new JSONObject();
        json.put("status", "2");
        for(String senName: sensitiveName.split(",")){
            if(sourceManager.isExist(docLibId, senName,type)){
                json.put("status", "1");
                json.put("senName", senName);
                break;
            }
        }

        InfoHelper.outputJson(json.toString(), response);
    }

    @RequestMapping("delete.do")
    public String sensiDelete(HttpServletRequest request, HttpServletResponse response,Integer DocLibID,String UUID) throws Exception{

        long [] docIDs = StringUtils.getLongArray(WebUtil.get(request, "DocIDs"));
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        StringBuilder DocIDs = new StringBuilder();
        for(long docID : docIDs){
            if(DocIDs.length() > 0) DocIDs.append(",");
            DocIDs.append(docID);
        }
        String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + DocIDs.toString() ;
        List<String> taskTypes= new ArrayList<String>();
        List<Long> ids=new ArrayList<>();

        try{
            for(long docID:docIDs){
                DocumentManager docManager = DocumentManagerFactory.getInstance();
                Document doc = docManager.get(docLibID, docID);
                String sentype="0".equals(doc.getString("sen_type"))?"4":"5";
                taskTypes.add(sentype);
                ids.add(docID);

                docManager.delete(doc);
            }

            //通知敏感词服务删除敏感词
            String result = SensitiveWordControllerHelper.sensitive("delete", ids.toString(), taskTypes.toString());
        }catch (Exception e) {
            url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
            return "redirect:" + url;
        }
        return "redirect:" + url;


    }

   @RequestMapping("ImportXml.do")
    public ModelAndView importXml(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Map<String, Object> model = new HashMap<>();

        model.put("UUID", WebUtil.get(request, "UUID"));

        return new ModelAndView("/xy/special/dialog/import", model);

    }
    @RequestMapping("ImportXmlSave.do")
    public String ImportXmlSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long docID = WebUtil.getLong(request, "DocIDs", 0);

        String xmlName=null;
        String UUID=null;
        String filePath=this.getClass().getResource("/").getFile();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 500);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        upload.setSizeMax(1024 * 1024 * 5);

        List<FileItem> items = upload.parseRequest(request);

        for (FileItem item : items) {
            if (item.isFormField()) {
                if(item.getFieldName().equals("UUID")){
                    UUID = item.getString();
                }
            }
            else {
                String fileName = item.getName();
                if(fileName==null || fileName.trim().equals("")){
                    continue;
                }
                fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
                xmlName=fileName;
                String fileEnd = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
                if(!"xml".equals(fileEnd)){
                    continue;
                }
                File file = new File(filePath+"\\"+fileName);
                item.write(file);

            }
        }


        String url = "/e5workspace/after.do?UUID=" + UUID+ "&&DocIDs=" + docID ;
        try{
            String sensitiveWords=sourceManager.importXmlSave(filePath,xmlName);
            //通知敏感词服务器批量导入
            String result = SensitiveWordControllerHelper.sensitive("batchAdd", sensitiveWords);

        }catch (Exception e) {
            url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
            return "redirect:" + url;
        }
        return "redirect:" + url;

    }
}
