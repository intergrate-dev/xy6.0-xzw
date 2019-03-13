package com.founder.xy.system.job;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.util.List;

/**
 * phpStat点击数同步任务
 * phpStat每天会生成一个当天点击数的文件，解析该文件将点击数写入翔宇
 * wenkx_20161018
 */
public class countClickSynJob  extends BaseJob {

    public countClickSynJob() {
        super();
        log = Context.getLog("xy.countClickSynJob");
    }
    @Override
    protected void execute() throws E5Exception {


        log.info("-----phpStat点击数同步任务开始-----");
        String result;
        try {
            result = countClickSyn();
            log.info("-----" + result + "-----");
        } catch (Exception e) {
            log.error("-----" + e.getMessage() + "-----");
            e.printStackTrace();
        }
        log.info("-----phpStat点击数同步任务完毕-----");


    }

    private String countClickSyn() throws Exception {


        String PHPStatPath = InfoHelper.getConfig( "存储设备", "PHPStat统计文件地址");
        if(PHPStatPath == null) return "PHPStat统计文件地址路径为空，请检查后台参数配置";
        DocLib articleLib  = LibHelper.getLib(DocTypes.ARTICLE.typeID());
        if(articleLib == null) return "稿件库为空，请检查数据库";
        String sql = "update " + articleLib.getDocLibTable()
                    + " set  a_countClick = a_countClick + ?"
                    + " where SYS_DOCUMENTID=?";
        Long[] params = {0L, 0L};
        //多站点情况
        SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
        int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
        List<Site> siteList = siteManager.getSites(siteLibID);
        for(Site tempSite:siteList) {
            String PHPFilePath = PHPStatPath + File.separator+ tempSite.getId() + File.separator + "article"+DateUtils.format("yyyyMMdd")+".result";
            System.out.println("PHPFilePath = " + PHPFilePath);
            File PHPFile = new File(PHPFilePath);
            if(!PHPFile.exists()){
                return "PHPStat统计文件"+PHPFile+"不存在，不再统计";
            }
            LineIterator it = FileUtils.lineIterator(PHPFile, "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine().trim();
                    String[] PHPInfo = line.split("\t");
                    if( !"0".equals(PHPInfo[2])){
                        //点击数
                        params[0] = Long.parseLong(PHPInfo[2]);
                        //稿件ID
                        params[1] = Long.parseLong(PHPInfo[0]);
                        InfoHelper.executeUpdate(articleLib.getDocLibID(), sql, params);
                    }
                }
            } finally {
                it.close();
            }
        }

        return "统计结束";
    }
}
