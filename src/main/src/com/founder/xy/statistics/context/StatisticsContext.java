package com.founder.xy.statistics.context;

import com.founder.e5.context.E5Exception;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.statistics.interfaces.IStatistics;
import com.founder.xy.statistics.service.impl.*;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Ethan on 2016/12/20.
 */

@Component
@Scope("prototype")
public class StatisticsContext {
    @Autowired
    WorkStatisticsByDepartment workStatisticsByDepartment;

    @Autowired
    WorkStatisticsByPerson workStatisticsByPerson;

    @Autowired
    ArticleStatisticsByDepartment articleStatisticsByDepartment;

    @Autowired
    ArticleStatisticsBySource articleStatisticsBySource;

    @Autowired
    ArticleStatisticsByColumn articleStatisticsByColumn;

    @Autowired
    ArticleRankingByColumn articleRankingByColumn;

    @Autowired
    ColumnRankingByColumn columnRankingByColumn;

    @Autowired
    ColumnDetailByColumn columnDetailByColumn;

    @Autowired
    ArticleStatisticsByUsers articleStatisticsByUsers;
    
    @Autowired
    ArticleStatisticsByBatman articleStatisticsByBatman;

    @Autowired
    ArticleStatisticsByTopic articleStatisticsByTopic;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);


    private ThreadLocal<IStatistics> iStatisticsThreadLocal = new ThreadLocal<>();
    //private IStatistics iStatistics;

    private IStatistics getiStatistics(String statisticsType) throws E5Exception {
        IStatistics iStatistics = iStatisticsThreadLocal.get();
        if (iStatistics == null) {
            switch (statisticsType.trim()) {
                case "WorkDepartment": {
                    iStatistics = workStatisticsByDepartment;
                    break;
                }
                case "WorkDepartmentWithPerson": {
                    iStatistics = workStatisticsByPerson;
                    break;
                }
                case "ArticleDepartment": {
                    iStatistics = articleStatisticsByDepartment;
                    break;
                }
                case "ArticleSource": {
                    iStatistics = articleStatisticsBySource;
                    break;
                }
                case "ArticleColumn": {
                    iStatistics = articleStatisticsByColumn;
                    break;
                }
                case "ArticleRanking": {
                    iStatistics = articleRankingByColumn;
                    break;
                }
                case "ColumnRanking": {
                    iStatistics = columnRankingByColumn;
                    break;
                }
                case "ColumnDetail": {
                    iStatistics = columnDetailByColumn;
                    break;
                }
                case "ArticleUsers": {
                    iStatistics = articleStatisticsByUsers;
                    break;
                }
                case "ArticleBatman": {
                    iStatistics = articleStatisticsByBatman;
                    break;
                }
                case "ColumnTopic": {
                    iStatistics = articleStatisticsByTopic;
                    break;
                }
                default: {
                    throw new E5Exception("Wrong StatisticsType Code!");
                }
            }
            iStatisticsThreadLocal.set(iStatistics);
        }
        return iStatisticsThreadLocal.get();
        //return iStatistics;
    }


    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {
        String statisticsType = MapUtils.getString(inParam, "statisticsType");
        log.error("---要统计的模块---"+statisticsType);
        return getiStatistics(statisticsType).statistics(inParam);
    }
}
