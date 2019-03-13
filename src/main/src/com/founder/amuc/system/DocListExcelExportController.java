package com.founder.amuc.system;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.listpage.cache.ListMode;
import com.founder.e5.listpage.cache.ListModeReader;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.param.DocListParam;
import com.founder.e5.workspace.service.DocListService;

@Controller
@RequestMapping("/e5workspace")
public class DocListExcelExportController extends BaseController{

 
    
    @Autowired
    @Qualifier("ListModeReader")
    ListModeReader listModeReader;
  
      //导出个数
    protected int count = 1000;
    private Log log = Context.getLog("xy.amuc");
    
    @SuppressWarnings("rawtypes")
    protected void handle(HttpServletRequest request, HttpServletResponse response, Map model) 
    throws Exception 
    {
  
    }
    
    @RequestMapping("/listExcelExport.do")
    public void listExcelExport(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      
      String docName = "";
      if (null != request.getParameter("DocLibID")) {
        int docLibID= Integer.valueOf(request.getParameter("DocLibID"));
        docName = DomHelper.getDocLibNameByID(docLibID);
      }
      log.info("导出"+docName+"信息开始");
      response.setCharacterEncoding("GBK");
      //取得列表构造对象
      ListMode listMode = getListMode(request);
      //组装参数
      DocListParam param = assembleParamEx(request, listMode);
      String listID = request.getParameter("ListPage");
      //得到列表数据，xml
      String result = getListResult(param);
      String[] results=result.split("\n");
      String str=result.substring(result.indexOf("\n")).replace("\n", "");
      if(!"".equals(str)&&str!=null){
        OutputStream  output=response.getOutputStream();
        response.setContentType("application/vnd.ms-excel; charset=gbk"); //必须是gbk，否则csv文件在excel里打开是乱码
        response.setHeader("Content-Disposition", "attachment;filename=list-export_" + new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(new Date() ) + ".xls");
        HSSFWorkbook wb = new HSSFWorkbook();//建立新HSSFWorkbook对象
        HSSFSheet sheet = wb.createSheet("sheet1");//建立新的sheet对象
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框   
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中    
        for(int i=0;i< results.length;i++){
          
          HSSFRow row = sheet.createRow(i);
          row.setHeight((short) 400);
          
          try{
            //String result2=results[i].substring(0, results[i].lastIndexOf(","));
            //String[] resultArray=result2.split(",");
            String result2=results[i].substring(0, results[i].lastIndexOf(ExcelExportDocListService.DELIMITER));
            String[] resultArray=result2.split(ExcelExportDocListService.DELIMITER);
            
            for(int j=0;j<resultArray.length;j++){
              HSSFCell cell = row.createCell(j);
              sheet.setColumnWidth(j, 4000);
              cell.setCellValue(resultArray[j].replaceAll("\"",""));
              cell.setCellStyle(cellStyle);
            }
          }catch(Exception e){
            HSSFCell cell = row.createCell(0);
            sheet.setColumnWidth(1, 4000);
            cell.setCellValue("数据格式不正确");
            cell.setCellStyle(cellStyle);
            log.info("---------------------导出操作,数据格式不正确---------------------------");
          }
        }
        wb.write(output);
        output.flush();
        output.close();
        log.info("---------------------导出操作成功---------------------------");
      }else{
        PrintWriter out = response.getWriter();
        if("1".equals(listID)){
          out.print("<script>alert('没有导出权限！');window.close();</script>");
          log.info("---------------------导出操作失败,没有导出权限！---------------------------");
        }else {
          out.print("<script>alert('没有数据！');window.close();</script>");
          log.info("---------------------导出操作失败,没有数据！---------------------------");
        }   
        out.close();
      }
      log.info("导出"+docName+"信息结束");
    }
    
    protected DocListParam assembleParamEx(HttpServletRequest request, ListMode pageBuilder)
    throws Exception
    {
      DocListParam param = assembleParam(request, pageBuilder);
      //可以在列表里指定导出的个数
      int curCount=InfoHelper.exportCount();
      if (curCount > 0)
        param.setCount(curCount);
      else
        param.setCount(count);//这是系统初始配置的导出个数
        
      return param;
    }
    
    protected String getListResult(DocListParam param) {
      
      //改成用导出Service
      //DocListService listService = new ExportDocListService();
      DocListService listService = new ExcelExportDocListService();
      listService.init(param);
      String result = listService.getDocList();
      
      result = addParam(result, param);
      
      return result;
    }
    
    /**
     * 设置导出个数，缺省为1000
     * @param count
     */
    public void setCount(int count) {
      this.count = count;
    }
    
    protected ListMode getListMode(HttpServletRequest request) {
      int listPageID = getInt(request, "ListPage", 0);
      int currentPage = getInt(request, "CurrentPage", 1);

      ListMode listMode = null;
      try {
        int userID = 0;
        int roleID = 0;
        SysUser user = getUserInfo(request);
        if (user != null)
        {
          userID = user.getUserID();
          roleID = user.getRoleID();
        }
        listMode = listModeReader.getListMode(listPageID, currentPage, userID, roleID);
      } catch (Exception e) {
        log.error("[DefaultDocListController]Get PageList Builder]", e);
      }
      return listMode;
    }
    
    protected String addParam(String xml, DocListParam param) {
      if (xml == null) return xml;
      int pos = xml.indexOf("<DocItem>");
      if (pos <= 0) return xml;
      
      StringBuffer result = new StringBuffer(xml.length() + 80);
      result.append(xml.substring(0, pos));
      
      result.append("<albumcolumn>").append(param.getAlbumColumn()).append("</albumcolumn>");
      
      result.append(xml.substring(pos));
      return result.toString();
    }
    
    protected DocListParam assembleParam(HttpServletRequest request, ListMode pageBuilder)
        throws Exception
        {

          DocListParam param = new DocListParam();

          param.setCatTypeID(getInt(request, "CatTypeID", 0));
          param.setDocLibID(getInt(request, "DocLibID", 0));
          param.setExtType(getInt(request, "ExtType", 0));

          param.setFilterID(StringUtils.getIntArray(get(request, "FilterID")));
          
          param.setFvID(getInt(request, "FVID", 0));
          param.setRuleFormula(get(request, "RuleFormula"));
          param.setCondition(get(request, "Query"));
          param.setTableName(null);
          
          param.setUser(ProcHelper.getUser(request));
          param.setRequest(request);

        
          int userCountOfPage = getInt(request, "CountOfPage", 0);
          if (userCountOfPage == 0) {
        
            param.setBegin(pageBuilder.getBegin());
            param.setCount(pageBuilder.getTotalCount());
            param.setCountOfPage(pageBuilder.getPageCount());
          }
          else {
            int currentPage = getInt(request, "CurrentPage", 1);
            param.setBegin(userCountOfPage * (currentPage - 1));
            param.setCount(pageBuilder.getCaches()* userCountOfPage);
            param.setCountOfPage(userCountOfPage);
          }
        
          int albumCol = getInt(request, "AlbumColumn", 0);
          if (albumCol == 0) {
            param.setAlbumColumn(pageBuilder.getCols());
          }
          else {
            param.setAlbumColumn(albumCol);
          }
        
          String order = get(request, "OrderBy");
          if (StringUtils.isBlank(order)) {
            setOrderBy(param, pageBuilder.getSortFields(), pageBuilder.getSortTypes());
          }
          else {
            setOrderBy(param, order, get(request, "OrderType"));
          }

          String fields = get(request, "Fields");
          if (StringUtils.isBlank(fields)) {
            param.setFields(pageBuilder.getFields());
          }
          else {
            param.setFields(fields.split(","));
          }
          return param;   
        }
    
    /**
     * 组织Orderby参数到param中
     * @param param
     * @param sortFields
     * @param sortType
     */
    protected void setOrderBy(DocListParam param, String sortFields, String sortType)
    {
      if (sortFields == null || "".equals(sortFields)) return;
      
      String[] fields = sortFields.split(",");
      
      String[] types = null;
      if (sortType != null) types = sortType.split(",");
      
      for (int i = 0; i < fields.length; i++)
      {
        if ((types != null) && "1".equals(types[i]))
          param.addOrderBy(fields[i], false);
        else
          param.addOrderBy(fields[i], true);
      }
    }
}