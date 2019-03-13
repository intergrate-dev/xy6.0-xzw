package com.founder.xy.article.trace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html转换工具
 */
public class HTMLUtil {
	/**
	 * 将纯文本转换成html
	 */
	public static String textToHTML(String text) {
		if (text == null || text.trim().length() == 0)
			return text;

		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\"", "&quot;");
		text = text.replaceAll(" ", "&nbsp;");
		text = text.replaceAll("\r\n", "\n");
		text = text.replaceAll("\n    ", "<br/>　　　　");
		text = text.replaceAll("\n  ", "<br/>　　");
		text = text.replaceAll("\n", "<br/>");

		return text;
	}

	/**
	 * 转换XML格式禁止的符号
	 */
	public static String xmlFilter(String text) {
		if (text == null || text.length() == 0)
			return text;

		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\"", "&quot;");
		return text;
	}

	/**
	 * 将XHTML格式转换成纯文本
	 */
	public static String xhtml2Text(String content) {
		if (null == content || "".equals(content.trim()))
			return content;

		StringBuffer sb = new StringBuffer();

		Pattern pattern = Pattern.compile("<[^<|^>]*>");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String group = matcher.group();

			if (group.matches("<[\\s]*>")) {
				matcher.appendReplacement(sb, group);
			} else if (group.matches("<br[^<|^>]*>") || group.matches("<br/>")
					|| group.matches("<p[^<|^>]*>") || group.matches("<p/>")) {
				matcher.appendReplacement(sb, "\r\n");
			} else {
				matcher.appendReplacement(sb, "");
			}
		}
		matcher.appendTail(sb);

		String ret = sb.toString().replaceAll("&#160;", " ")
				.replaceAll("&gt;", ">").replaceAll("&lt;", "<")
				.replaceAll("&amp;", "&");
		return ret;
	}

	public static String textFilter(String text) {
		if (text == null || text.length() == 0)
			return text;
		text = text.replaceAll("&amp;", "&");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&quot;", "\"");
		return text;
	}
}
