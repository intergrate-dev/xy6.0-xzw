package com.founder.xy.column;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import org.springframework.stereotype.Service;

@Service
public class TopicColumnManager {

    public Document[] getRoot(int colLibID, int siteID) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();

        Document[] cols = docManager
                .find(colLibID,
                        "col_siteID=? and col_parentID=0 and SYS_DELETEFLAG=0 order by col_displayOrder",
                        new Object[] { siteID });
        return cols;
    }

    /**
     * 按话题组名称查找。用于栏目树上的查找动作
     */
    public Document[] find(int colLibID, int siteID, String name, boolean flag) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        System.out.println("%" + name + "%");
        if(flag==false){
            Document[] cols = docManager.find(colLibID,
                    "(col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0",
                    new Object[] {"%" + name + "%", "%" + name + "%", siteID});
            return cols;
        }else{
            int sys_documentId=Integer.parseInt(name);
            Document[] cols = docManager.find(colLibID,"SYS_DOCUMENTID like ? or ((col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0 ) ",
                    new Object[] {sys_documentId+"%", "%" + name + "%", "%" + name + "%",  siteID });
            return cols;
        }
    }
}
