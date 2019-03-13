package com.founder.xy.jpublish.magazine;

import java.io.File;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;

/**
 * 期刊发布时，生成日期信息文件period.xml（记录哪些日期有发刊）
 * @author Gong Lijie
 */
public class PeriodMagDealer {
	
	public String createXml(String path, Magazine magazine, String magUrl, int index) throws Exception{
		if (StringUtils.isBlank(path)) return null;
		
		Document document = null;
		Element root = null;
		File file = new File(path);
		if (!file.exists()){
			document = DocumentHelper.createDocument();
			root = document.addElement("periodlist");
		} else {
			//xml文件存在
			document = new SAXReader().read(file);
			root = document.getRootElement();
		}
		
		//记录当前刊期
		if (!exists(magazine, root)) {
			addOneDay(root, magazine, magUrl, index);
			return format(document);
		} else {
			return null;
		}
	}
	
	private void addOneDay(Element root, Magazine magazine, String magUrl, int index) {
		String dateValue = DateUtils.format(magazine.getDate(), "yyyy-MM-dd");

		String picUrl = magazine.getPic() == null ? null : ((index == 0) ? magazine.getPic().getUrl() : magazine.getPic().getUrlPad());
		if (picUrl == null) picUrl = "";
		
		Element element1=root.addElement("period");
		
		element1.addElement("magID").setText(magazine.getId() + "");
		element1.addElement("magDate").setText(dateValue);
		element1.addElement("magUrl").setText(magUrl); //该刊期的url
		element1.addElement("picUrl").setText(picUrl); //该刊期的封面图url
	}
	
	//判断是否已经记录了某报纸的某日期
	private boolean exists(Magazine magazine, Element root) {
		String dateValue = DateUtils.format(magazine.getDate(), "yyyy-MM-dd");
		String magID = String.valueOf(magazine.getId());
		
		for (Object ele : root.elements("period")) {
			Element e = (Element) ele;
			if ((e.elementText("magDate")).equals(dateValue)
					&& e.elementText("magID").equals(magID)) {
				return true;
			}
		}
		return false;
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
}
