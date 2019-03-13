package com.founder.xy.article.trace;

import java.io.InputStream;

import org.dom4j.io.SAXReader;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.IBlob;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;

public class DocTraceUtils {
	
	public static String getVerXml(Document doc) {
		if (doc == null) return null;
		
		IBlob blob = doc.getBlob("a_trace");
		if (blob == null) return null;
		
		String oldVerXml = null;
		InputStream is = null;
		try {
			is = blob.getStream();
			
			SAXReader reader = new SAXReader();
			reader.setEncoding("UTF-16");
			
			oldVerXml = reader.read(is).asXML();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(is);
		}
		return oldVerXml;
	}

	public static String getVerXml(long docID, int docLibID) {
		try {
			Document doc = DocumentManagerFactory.getInstance().get(docLibID,
					docID);
			return getVerXml(doc);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void saveNewXml(String newXml, long docID, int docLibID)
			throws Exception {
		try {
			DocumentManager docMgr = DocumentManagerFactory.getInstance();
			Document doc = docMgr.get(docLibID, docID);
			if (doc == null) {
				doc = docMgr.newDocument(docLibID, docID);
			}
			doc.setBlob("a_trace", newXml.getBytes("UTF-16"));
			docMgr.save(doc);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
}
