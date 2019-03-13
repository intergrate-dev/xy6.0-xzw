package com.founder.xy.statistics.util;

import com.founder.e5.context.E5Exception;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2017/2/14.
 */
@SuppressWarnings("unchecked")
public class FileUtil {
    /**
     * 下载文件
     *
     * @param response
     * @param csvFilePath 文件路径
     * @param fileName    文件名称
     * @throws IOException
     */
    public static void downLoadCSVFile(HttpServletResponse response, String fileName, String csvFilePath) throws E5Exception {
        //上传的文件都是保存在/WEB-INF目录下
        try {
            // 得到要下载的文件
            //System.out.println(csvFilePath);
            File file = new File(csvFilePath + File.separator + fileName);
            // 如果文件不存在
            if (!file.exists()) {
                return;
            }
            
            //Properties properties = System.getProperties();
            //String encodingStr = properties.getProperty("file.encoding");
            String encodingStr ="GB2312";//UTF-8
            // 设置响应头，控制浏览器下载该文件
            response.setContentType("application/csv; charset=GB2312");
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName.substring(0,fileName.length()-13), "UTF-8"));

            // 读取要下载的文件，保存到文件输入流
            FileInputStream in = new FileInputStream(file);
            // 创建输出流
            OutputStream out = response.getOutputStream();
            // 创建缓冲区
            byte buffer[] = new byte[1024];
            int len = 0;
            // 循环将输入流中的内容读取到缓冲区当中
            while ((len = in.read(buffer)) > 0) {
                // 输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }
            // 关闭文件输入流
            in.close();
            // 关闭输出流
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static String generateCSVFile(String fileName, String filePath, Map<String, Object> inParam) throws E5Exception {
        String fullFileName = filePath + File.separator + fileName;
        File file = new File(fullFileName);
        //FileWriter fileWriter = null;
        CSVPrinter csvPrinter = null;
        CSVFormat csvFormat;
        System.out.println(file.getAbsolutePath());
        //获取csv表头
        List<String> headArray = ObjectUtil.getList(inParam.get("headParam"));

        String[] headers = headArray.toArray(new String[headArray.size()]);
        csvFormat = CSVFormat.DEFAULT.withHeader(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
        csvFormat = CSVFormat.DEFAULT.withHeader(headers);
        String newFileName = null;
        BufferedWriter csvFileOutputStream = null;
        //Properties properties = System.getProperties();
        //String encodingStr = properties.getProperty("file.encoding");
        String encodingStr ="GB2312";
        try {
            while(file.exists()){
                System.out.println("文件存在");
                fullFileName = fullFileName.substring(0,fileName.length()-17)+ System.currentTimeMillis()+ ".csv" + System.currentTimeMillis();//修改文件名,并创建csv的字符输出流
            }
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encodingStr),1024);//new FileWriter(fullFileName, true);  // 创建csv的字符输出流
            csvPrinter = new CSVPrinter(csvFileOutputStream, csvFormat);
            List<Map<String, Object>> exportList = new ArrayList<>();
            exportList = ObjectUtil.getList(inParam.get("exportData"));
            for(Map<String, Object> row : exportList){
                List<String> exportRow = new ArrayList<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    exportRow.add(entry.getValue().toString());
                }
                csvPrinter.printRecord(exportRow);
            }

            System.out.println("生成.csv文件");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvPrinter.flush();
                //fileWriter.flush();
                //fileWriter.close();
                csvFileOutputStream.flush();
                csvFileOutputStream.close();
                csvPrinter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFileName;
    }

    public static String generateCSVFileWithoutHead(String fileName, String filePath, Map<String, Object> inParam) throws E5Exception {
        String fullFileName = filePath + File.separator + fileName;
        File file = new File(fullFileName);
        //FileWriter fileWriter = null;
        CSVPrinter csvPrinter = null;
        CSVFormat csvFormat;
        System.out.println(file.getAbsolutePath());
        csvFormat = CSVFormat.DEFAULT;
        String newFileName = null;
        BufferedWriter csvFileOutputStream = null;
        //Properties properties = System.getProperties();
        //String encodingStr = properties.getProperty("file.encoding");
        String encodingStr ="GB2312";
        try {
            while(file.exists()){
                System.out.println("文件存在");
                fullFileName = fullFileName.substring(0,fileName.length()-17)+ System.currentTimeMillis()+ ".csv" + System.currentTimeMillis();//修改文件名,并创建csv的字符输出流
            }
            //fileWriter = new FileWriter(fullFileName, true);  // 创建csv的字符输出流
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encodingStr),1024);
            csvPrinter = new CSVPrinter(csvFileOutputStream, csvFormat);
            List<Map<String, Object>> exportList = new ArrayList<>();
            exportList = ObjectUtil.getList(inParam.get("exportData"));
            for(Map<String, Object> row : exportList){
                List<String> exportRow = new ArrayList<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    exportRow.add(entry.getValue().toString());
                }
                csvPrinter.printRecord(exportRow);
            }

            System.out.println("生成.csv文件");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvPrinter.flush();
                //fileWriter.flush();
                //fileWriter.close();
                csvFileOutputStream.flush();
                csvFileOutputStream.close();
                csvPrinter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFileName;
    }

    public static void deleteFile(String fileName, String filePath) throws E5Exception {
        String fullFileName = filePath + File.separator + fileName;
        File file = new File(fullFileName);
        Path path = file.toPath();
        try {
            Files.delete(path);
            System.out.println("删除文件完成！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
