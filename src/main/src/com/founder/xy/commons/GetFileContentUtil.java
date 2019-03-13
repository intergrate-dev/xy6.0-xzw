package com.founder.xy.commons;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetFileContentUtil {

    @RequestMapping(value = "uploadWord.do", method = RequestMethod.POST)
    @ResponseBody
    public Map uploadWord(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, String> map = new HashMap<>();
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //factory.setSizeThreshold(yourMaxMemorySize);
            //factory.setRepository(yourTempDirectory);
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<?> items = upload.parseRequest(request);

            for (Object item1 : items) {
                FileItem item = (FileItem) item1;
                if (!item.isFormField()) {
                    String format = getformat(item.getName());
                    if ("txt".equalsIgnoreCase(format)) {
                        map = getTxtContent(item.getInputStream());
                    } else if ("doc".equalsIgnoreCase(format)) {
                        map = getWordDocContent(item.getInputStream(), request);
                    } else if ("docx".equalsIgnoreCase(format)) {
                        map = getWordDocxContent(item.getInputStream(), request);
                    }
                }
            }
        } catch (SizeLimitExceededException e) {
            map.put("state", e.getMessage());
            return map;
        }
        return map;
    }

    private String getformat(String name) {
        int pos = name.lastIndexOf('.');
        if (pos == -1) return "";
        else return name.substring(pos + 1, name.length());
    }

    private Map<String, String> getTxtContent(InputStream in) throws IOException {
        String encoding;
        StringBuilder result = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        //判断编码格式
        InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());

        //读内容
        InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());

        byte[] b = new byte[3];
        stream1.read(b);
        stream1.close();
        if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
            encoding = "UTF-8";
        } else {
            encoding = "GBK";
        }
        InputStreamReader read = new InputStreamReader(stream2, encoding);//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            result.append(lineTxt).append("<br/>");
        }
        read.close();
        map.put("result", result.toString().replaceAll(" ", "&nbsp;").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        map.put("state", "SUCCESS");
        return map;

    }

    private Map<String, String> getWordDocContent(InputStream in, HttpServletRequest request)
            throws IOException, ParserConfigurationException, TransformerException {
        StringBuffer text = new StringBuffer();

        Map<String, String> map = new HashMap<>();
        //转换word成html文件
        String path = DocConvertHtml(in, request);
        /*  String textmode = request.getParameter("textmode");
        InputStream input = new FileInputStream(path);
        InputStreamReader read = new InputStreamReader(input, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
          text.append(lineTxt);
        }
        read.close();*/
        File input = new File(path);
        org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8");
        Elements els = doc.body().children();
        for (Element el : els) {
            text = text.append(el);
        }
        text.append(doc.getElementsByTag("style"));
        (new File(path)).delete();//删除临时文件
        map.put("result", replaceStyle(text.toString()));
        map.put("state", "SUCCESS");
        return map;

    }

    private Map<String, String> getWordDocxContent(InputStream in, HttpServletRequest request) throws IOException, ParserConfigurationException, TransformerException {
        StringBuffer text = new StringBuffer();
        Map<String, String> map = new HashMap<>();
        /*  String textmode = request.getParameter("textmode");
        if("1".equals(textmode)){
          XWPFDocument doc = new XWPFDocument(in);
          XWPFWordExtractor extractor2 = new XWPFWordExtractor(doc);
          text.append(extractor2.getText());

        }else{*/
        XWPFDocument document = new XWPFDocument(in);
        String path = DocxConvertHtml(document);
        File input = new File(path);
        org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8");

        List<XWPFPictureData> piclist = document.getAllPictures();
        for (XWPFPictureData aPiclist : piclist) {
            String format = getformat(aPiclist.getFileName());
            //获取图片数据流
            byte[] picbyte = aPiclist.getData();
            InputStream is = new ByteArrayInputStream(picbyte);
            String attUrl = "";
            try {
                //上传图片
                attUrl = uploadFtp(is, format, aPiclist.getFileName().substring(0, aPiclist.getFileName().lastIndexOf(".")), request, picbyte.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //word/media/+图片名字
            for (Element e : doc.getElementsByTag("img")) {
                if (("word/media/" + aPiclist.getFileName()).equals(e.attr("src"))) {
                    e.attr("src", attUrl);
                }
            }
        }

        Elements els = doc.body().children();
        for (Element el : els) {
            text = text.append(el);
        }
        text.append(doc.getElementsByTag("style"));
        // String result=text.toString().replaceAll("font-family[^;]+;", "");
        (new File(path)).delete();//删除临时文件
        //}
        String textStr = text.toString().replaceAll("&middot;","·");//中文特殊字符转换
        textStr = textStr.replaceAll("&times;", "×");
        textStr = textStr.replaceAll(" ", "");
        textStr = textStr.replaceAll("&divide;", "÷");
        textStr = textStr.replaceAll("<img","<img ");
        map.put("result", replaceStyle(textStr));
        map.put("state", "SUCCESS");
        return map;

    }

    private String DocConvertHtml(InputStream in, final HttpServletRequest request) throws IOException, ParserConfigurationException, TransformerException {
        File htmlFile = PicHelper.getTmpFile();
        HWPFDocument wordDocument = new HWPFDocument(in);

        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
                InputStream is = new ByteArrayInputStream(content);
                String attUrl = "";
                try {
                    attUrl = uploadFtp(is, pictureType.getExtension(), suggestedName, request, content.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return attUrl;
            }
        });

        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        OutputStream outStream = new FileOutputStream(htmlFile);
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();
        return htmlFile.getAbsolutePath();
    }

    private String DocxConvertHtml(XWPFDocument document) throws IOException, ParserConfigurationException, TransformerException {
        File htmlFile = PicHelper.getTmpFile();

        XHTMLOptions options = XHTMLOptions.create().indent(4);
        /*File imageFolder = new File("D://images/fileInName");
        options.setExtractor( new FileImageExtractor( imageFolder ) );
        options.URIResolver( new FileURIResolver( imageFolder ) );*/

        OutputStream out = new FileOutputStream(htmlFile);
        XHTMLConverter.getInstance().convert(document, out, options);
        out.close();
        return htmlFile.getAbsolutePath();
    }

    private String uploadFtp(InputStream is, String picSuffix, String fileName, HttpServletRequest request, int size) throws Exception {

        StorageDevice device = InfoHelper.getPicDevice();
        //构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        /* String newFileName="";
        if(fileName.lastIndexOf(".")!=-1)
         newFileName=fileName.substring(fileName.lastIndexOf("."));*/
        String savePath = InfoHelper.getPicSavePath(request) + "." + picSuffix;
        //开始存储到存储设备上
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        int userMaxSize = 0;
        boolean zipFlag = false;
        String userMaxSizeStr = InfoHelper.getConfig("写稿", "上传图片大小限制");
        if (userMaxSizeStr != null && !"".equals(userMaxSizeStr)) {
            userMaxSize = Integer.parseInt(userMaxSizeStr);
            if (userMaxSize > 0)
                zipFlag = true;
        }
//        attUrl = uploadFtp(is, format, aPiclist.getFileName().substring(0, aPiclist.getFileName().lastIndexOf(".")), request, picbyte.length);

        if (zipFlag && !".gif".equals(picSuffix) && size > 1024 * 1024 * userMaxSize) {
            File tmpFile = PicHelper.picZip(is, 800, picSuffix);
            is = new FileInputStream(tmpFile);
        }
        try {
            sdManager.write(device, savePath, is);
        } finally {
            ResourceMgr.closeQuietly(is);
        }

        //加抽图任务
        InfoHelper.prepare4Extract(device, savePath);
        return "../../xy/image.do?path=" + device.getDeviceName() + ";" + savePath;
    }

    private String replaceStyle(String result) {
        Pattern htmlTagPattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
        Matcher htmlTagMathcer = htmlTagPattern.matcher(result);

        while (htmlTagMathcer.find()) {
            String htmlTag = htmlTagMathcer.group();

            result = result.replace(htmlTag, htmlTag.replaceAll("style=\"[^\"]+\"", ""));
        }
        return result;
    }
}
