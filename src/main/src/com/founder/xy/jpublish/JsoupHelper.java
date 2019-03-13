package com.founder.xy.jpublish;

import com.founder.e5.commons.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * HTML Parser调用
 * @author Gong Lijie
 */
public class JsoupHelper {

	//对于触屏文章，替换内容中的embed为video
	public static String replaceVideo(String content) {
		Document html = Jsoup.parse(content);
		Elements videos = html.select("embed");
		if (videos.isEmpty()) return content;
		
		for (Element embed : videos) {
			String url = embed.attr("urlApp");
			String video = "<video src=\"" + url 
					+ "\" controls=\"controls\">您的浏览器不支持 video 标签，不是触屏设备吗？</video>";
			embed.after(video);
			embed.remove();
		}
		return html.html();
	}

	//对于预览，替换预览内容中的图片地址：模板中可能是.2，要改成.2.jpg
	public static String replaceImgSuffix(String content) {
		if(StringUtils.isBlank(content))
			return content;
		Document html = Jsoup.parse(content);
		Elements imgs = html.select("img");
		if (imgs.isEmpty()) return content;
		
		for (Element img : imgs) {
			String url = img.attr("src");
			if (url.endsWith(".2") || url.endsWith(".0") || url.endsWith(".1")) {
				url += ".jpg";
				img.attr("src", url);
			}
		}
		return html.html();
	}

	public static String replaceRel(String content) {
		Document html = Jsoup.parse(content);
		Elements rels = html.select(".founder_rel");
		if (rels.isEmpty()) return content;

		for (Element rel : rels) {
			String url = rel.attr("urlpad");
			if(!StringUtils.isBlank(url))
				rel.attr("href",url);
		}
		return html.html();
	}

	public static String replaceNextPage(String content, String nextPageUrl) {
		Document html = Jsoup.parse(content);
		Elements rels = html.select("[classdata=next_page]");
		if (rels.isEmpty()) return content;

		for (Element rel : rels) {
			if(!StringUtils.isBlank(nextPageUrl))
				rel.wrap("<a href='"+ nextPageUrl +"' ></a>");
		}
		return html.html();
	}
}
