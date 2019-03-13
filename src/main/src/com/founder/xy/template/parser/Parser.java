package com.founder.xy.template.parser;

import java.util.regex.Pattern;

import com.founder.e5.doc.Document;

public interface Parser {
	
	static final Pattern PARSER_PATTERN = 
			Pattern.compile("<FOUNDER-XY[\\s]*?type=\"([\\s\\S]*?)\"[\\s]*?data=\"([\\s\\S]*?)\">([\\s\\S]*?)</FOUNDER-XY>");
	
	static final String DELETE_COMPONENTOBJS = "delete from xy_componentObj where co_templateID=? and co_templateType=?";
	
	static final String UPDATE_TEMPLATE = "update xy_template set t_colRelated=? where SYS_DOCUMENTID=?";
	
	boolean parse(Document doc);
}
