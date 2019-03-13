package com.founder.xy.article.trace;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;

@Service("TemplateService")
public class TemplateService {
	// 用作存缓存
	private static Map<String, String> streamMap = new HashMap<String, String>();

	synchronized public String getResultHtml(Pair[] labelExs, String templatefile) {
		Reader reader = null;
		StringBuffer buf = new StringBuffer();
		InputStream in = null;
		try {
			BufferedReader br = null;
			String tmpleteStr = streamMap.get(templatefile);
			if (tmpleteStr == null) {
				in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(templatefile);
				reader = new InputStreamReader(in, "UTF-8");
			} else {
				in = new ByteArrayInputStream(tmpleteStr.getBytes());
				reader = new InputStreamReader(in);
			}

			/**
			 * 需要转成UTF-8编码
			 * 
			 */

			br = new BufferedReader(reader);

			StringBuffer oriStrBuf = new StringBuffer();

			String nextLine = null;
			while ((nextLine = br.readLine()) != null) {
				if (tmpleteStr == null) {
					oriStrBuf.append(nextLine).append("\r\n");
				}
				for (Pair labelEx : labelExs) {
					nextLine = replaceLabel(nextLine, labelEx);
				}
				buf.append(nextLine).append("\r\n");
			}

			if (tmpleteStr == null) {
				streamMap.put(templatefile, oriStrBuf.toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(reader);
			ResourceMgr.closeQuietly(in);

		}
		return buf.toString();

	}

	public String replaceLabel(String nextLine, Pair labelEx) {
		int index = nextLine.indexOf(labelEx.getKey());
		if (index > -1) {
			StringBuffer buf = new StringBuffer();
			buf.append(nextLine.substring(0, index));
			buf.append(labelEx.getValue());
			buf.append(nextLine.substring(index + labelEx.getKey().length()));
			return buf.toString();
		} else {
			return nextLine;
		}
	}

}