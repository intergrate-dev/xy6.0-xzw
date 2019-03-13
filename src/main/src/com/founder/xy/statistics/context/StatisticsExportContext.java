package com.founder.xy.statistics.context;

import com.founder.e5.context.E5Exception;
import com.founder.xy.statistics.interfaces.IStatisticsExport;
import com.founder.xy.statistics.service.impl.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Ethan on 2017/2/14.
 */
@Component
@Scope("prototype")
public class StatisticsExportContext {

    @Autowired
    WorkExportByDepartment workExportByDepartment;

    @Autowired
    WorkExportByPerson workExportByPerson;

    @Autowired
    ArticleExportByDepartment articleExportByDepartment;

    @Autowired
    ArticleExportBySource articleExportBySource;

    @Autowired
    ArticleExportByColumn articleExportByColumn;

    @Autowired
    ArticleRankingExportByColumn articleRankingExportByColumn;

    @Autowired
    ColumnRankingExportByColumn columnRankingExportByColumn;

    @Autowired
    ColumnDetailExportByColumn columnDetailExportByColumn;

    @Autowired
    ArticleExportByUsers articleExportByUsers;

    @Autowired
    ArticleExportByBatman articleExportByBatman;

    @Autowired
    ArticleExportByTopic articleExportByTopic;




    private ThreadLocal<IStatisticsExport> iStatisticsExportThreadLocal = new ThreadLocal<>();

    private IStatisticsExport getiStatisticsExport(String exportType) throws E5Exception {
        IStatisticsExport iStatisticsExport = iStatisticsExportThreadLocal.get();
        if (iStatisticsExport == null) {
            switch (exportType.trim()) {
                case "WorkDepartment": {
                    iStatisticsExport = workExportByDepartment;
                    break;
                }
                case "WorkDepartmentWithPerson": {
                    iStatisticsExport = workExportByPerson;
                    break;
                }
                case "ArticleDepartment": {
                    iStatisticsExport = articleExportByDepartment;
                    break;
                }
                case "ArticleSource": {
                    iStatisticsExport = articleExportBySource;
                    break;
                }
                case "ArticleColumn": {
                    iStatisticsExport = articleExportByColumn;
                    break;
                }
                case "ArticleRanking": {
                    iStatisticsExport = articleRankingExportByColumn;
                    break;
                }
                case "ColumnRanking": {
                    iStatisticsExport = columnRankingExportByColumn;
                    break;
                }
                case "ColumnDetail": {
                    iStatisticsExport = columnDetailExportByColumn;
                    break;
                }
                case "ArticleUsers": {
                	iStatisticsExport = articleExportByUsers;
                    break;
                }
                case "ArticleBatman": {
                    iStatisticsExport = articleExportByBatman;
                    break;
                }
                case "ArticleTopic": {
                    iStatisticsExport = articleExportByTopic;
                    break;
                }
                default: {
                    throw new E5Exception("Wrong ExportType Code!");
                }
            }
            iStatisticsExportThreadLocal.set(iStatisticsExport);
        }
        return iStatisticsExportThreadLocal.get();
    }

    public Map<String, Object> export(Map<String, Object> inParam) throws E5Exception {
        String exportType = MapUtils.getString(inParam, "exportType");
        return getiStatisticsExport(exportType).export(inParam);
    }
}
