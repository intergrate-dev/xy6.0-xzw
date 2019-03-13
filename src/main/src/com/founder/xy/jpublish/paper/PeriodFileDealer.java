package com.founder.xy.jpublish.paper;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 数字报发布时，生成日期信息文件period.xml（记录哪些日期有发报）
 */
public class PeriodFileDealer {
	
	public String createXml(String path, PaperLayout paperLayout, int index) throws Exception{
		if (StringUtils.isBlank(path)) return null;
		
		Document document = null;
		Element root = null;
		File file = new File(path);
		if (!file.exists()){
			document = DocumentHelper.createDocument();
			root = document.addElement("periodlist");
			
			//xml文件不存在，重新生成本月以往的记录
			//（注意这里有缺陷，若多个报纸发布目录相同，这里只补充了一个报纸的信息。情况少有，不处理）
			List<PaperLayout> layouts = getLayoutsInMonth(paperLayout.getDate(), paperLayout.getPaperID());
			for (PaperLayout layout0 : layouts) {
				addOneDay(root, layout0, index);
			}
		} else {
			//xml文件存在
			document = new SAXReader().read(file);
			root = document.getRootElement();
		}
		
		//记录当前刊期 无则增加，有则更新
		if (!exists(paperLayout, root)) {
			addOneDay(root, paperLayout, index);
		}
		else {
			updateOneDay(root, paperLayout, index);
		}
		
		return format(document);
	}

	private void updateOneDay(Element root, PaperLayout paperLayout, int index) {
		String layoutFile = _fileName(paperLayout, index);

		String dateValue = DateUtils.format(paperLayout.getDate(), "yyyy-MM-dd");
		String paperID = String.valueOf(paperLayout.getPaperID());

		for (Object ele : root.elements("period")) {
			Element e = (Element) ele;
			if ((e.elementText("period_date")).equals(dateValue)
					&& e.elementText("paper_id").equals(paperID)) {
				Element temp = e.element("period_name");
				temp.setText(dateValue);
				temp = e.element("front_page");
				temp.setText(layoutFile);
			}
		}
	}

	private void addOneDay(Element root, PaperLayout paperLayout, int index) {
		String layoutFile = _fileName(paperLayout, index);

		String dateValue = DateUtils.format(paperLayout.getDate(), "yyyy-MM-dd");
		String periodId = dateValue.substring(8);

		Element element1=root.addElement("period");

		element1.addAttribute("id", periodId);
		element1.addElement("paper_id").setText(paperLayout.getPaperID() + "");
		element1.addElement("period_date").setText(dateValue);
		element1.addElement("period_name").setText(dateValue);
		element1.addElement("front_page").setText(layoutFile); //该日期的第一个版的Url
	}
	
	//取版面的发布文件名
	private String _fileName(PaperLayout layout, int index) {
		String url = layout.getDir();
		if (url == null)
			url = (index == 0) ? layout.getUrl() : layout.getUrlPad();
		return url.substring(url.lastIndexOf("/") + 1);
	}

	//判断是否已经记录了某报纸的某日期
	private boolean exists(PaperLayout paperLayout, Element root) {
		String dateValue = DateUtils.format(paperLayout.getDate(), "yyyy-MM-dd");
		String paperID = String.valueOf(paperLayout.getPaperID());
		
		for (Object ele : root.elements("period")) {
			Element e = (Element) ele;
			if ((e.elementText("period_date")).equals(dateValue)
					&& e.elementText("paper_id").equals(paperID)) {
				return true;
			}
		}
		return false;
	}

	//取某一月内各天的第一版，用于重新生成period.xml
	private List<PaperLayout> getLayoutsInMonth(Date paperDate, int paperID) throws Exception{
		List<PaperLayout> layouts = new ArrayList<PaperLayout>();
		
		Calendar start = Calendar.getInstance();
		start.setTime(paperDate);
		start.set(Calendar.DAY_OF_MONTH,1); //本月1号
		
		Calendar end = Calendar.getInstance();
		end.setTime(paperDate);
		end.add(Calendar.MONTH, 1);
		end.set(Calendar.DAY_OF_MONTH, 1);
		end.add(Calendar.DATE, -1);//本月最后一天
		
		int layoutLibId = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(), Tenant.DEFAULTCODE);
		String conditions = "(pl_date between ? and ?) and pl_paperID=? and pl_status=1 order by pl_date, SYS_DOCUMENTID";
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		com.founder.e5.doc.Document[] docs = docManager.find(layoutLibId, conditions, 
				new Object[] {start.getTime(), end.getTime(), paperID});
		
		Date one = null;
		for (com.founder.e5.doc.Document doc : docs) {
			Date pl_date = doc.getDate("pl_date");
			if (one == null || !one.equals(pl_date)) {
				PaperLayout layout = new PaperLayout(doc);
				layouts.add(layout);
				
				one = pl_date;
			}
		}
		return  layouts;
	}
	
	//格式化输出xml
	private String format(Document doc) throws Exception {
		// 输出格式
		OutputFormat formater = OutputFormat.createPrettyPrint();
		formater.setEncoding("UTF-8");
		// 输出(目标)
		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, formater);
		// 输出格式化的串到目标中，执行后格式化后的串保存在out中。
		writer.write(doc);
		writer.close();
	
		String result = out.toString();
		out.close();
		
		return result;
	}

	public String updatePapersInfo(String filePath, List<PaperLayout> layouts) throws Exception {
		PaperLayout frontLayout = layouts.get(0);
		int paperID = frontLayout.getPaperID();
		Document document = null;
		Element root = null;
		File file = new File(filePath);
		if (!file.exists()){
			//xml文件不存在，重新生成报纸信息（只生成当前报纸）
			document = DocumentHelper.createDocument();
			root = document.addElement("papers");
			addPaperEle(root,layouts);
		} else {
			//xml文件存在
			document = new SAXReader().read(file);
			root = document.getRootElement();
			String xpath = "//paper[papperID="+paperID+"]";
			Element paperEle = (Element) root.selectSingleNode(xpath);
			if(paperEle!=null) {
				root.remove(paperEle);
				addPaperEle(root, layouts);
			}
			else {
				addPaperEle(root, layouts);
			}
		}
		return document.asXML();
	}

	private void addPaperEle(Element root, List<PaperLayout> layouts) {
		Element paperEle = root.addElement("paper");
		PaperLayout frontLayout = layouts.get(0);
		int paperID = frontLayout.getPaperID();
		Element paperIDEle = paperEle.addElement("papperID");
		paperIDEle.setText(""+paperID);
		String paperName = frontLayout.getPaper();
		Element paperNameEle = paperEle.addElement("paperName");
		paperNameEle.setText(paperName);
		Date paperDate = frontLayout.getDate();
		Element paperDateEle = paperEle.addElement("paperDate");
		paperDateEle.setText(DateUtils.format(paperDate,"yyyy/MM/dd"));
		Element layoutsEle = paperEle.addElement("layouts");
		for (PaperLayout layout:layouts){
			addOneLayouts(layoutsEle,layout);
		}
	}

	private void addOneLayouts(Element layoutsEle, PaperLayout layout) {
		Element layoutEle = layoutsEle.addElement("layout");
		Element layoutID = layoutEle.addElement("layoutID");
		layoutID.setText(""+layout.getId());
		Element edition = layoutEle.addElement("edition");
		//版次
		edition.setText(layout.getLayout());
		Element layoutName = layoutEle.addElement("layoutName");
		layoutName.setText(layout.getLayoutName());
		Element layoutURL = layoutEle.addElement("layoutURL");
		layoutURL.setText(layout.getUrl());
		Element layoutURLPAD = layoutEle.addElement("layoutURLPAD");
		layoutURLPAD.setText(layout.getUrlPad());
	}

    public String deletePeriodNode(PaperLayout paperLayout, String path) throws Exception {
        if (StringUtils.isBlank(path)) return null;

        Document document;
        Element root;
        File file = new File(path);
        if (!file.exists()) {
            return "";
        } else {
            //xml文件存在
            document = new SAXReader().read(file);
            root = document.getRootElement();
        }
        //寻找当前刊期 无则不处理，有则删除
        if (!exists(paperLayout, root)) {
            return "";
        } else {
            deleteOneDay(root, paperLayout);
        }
        return format(document);
    }

	private void deleteOneDay(Element root, PaperLayout paperLayout) {

		String dateValue = DateUtils.format(paperLayout.getDate(), "yyyy-MM-dd");
		String paperID = String.valueOf(paperLayout.getPaperID());

		for (Object ele : root.elements("period")) {
			Element e = (Element) ele;
			if ((e.elementText("period_date")).equals(dateValue)
					&& e.elementText("paper_id").equals(paperID)) {
				root.remove(e);
			}
		}
	}

    String deletePaperinfo(PaperLayout layout, String filePath) throws DocumentException, E5Exception {
        int paperID = layout.getPaperID();
        Document document;
        Element root;
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        } else {
            //xml文件存在
            document = new SAXReader().read(file);
            root = document.getRootElement();
            String xpath = "//paper[papperID=" + paperID + "]";
            Element paperEle = (Element) root.selectSingleNode(xpath);
            if (paperEle == null || !sameDate(layout, paperEle)) {
                return "";
            } else {
				root.remove(paperEle);
			}
        }
        return document.asXML();
    }


    private boolean sameDate(PaperLayout paperLayout, Element paperEle) {
        String dateValue = DateUtils.format(paperLayout.getDate(), "yyyy/MM/dd");
        return (paperEle.elementText("paperDate")).equals(dateValue);
    }
}
