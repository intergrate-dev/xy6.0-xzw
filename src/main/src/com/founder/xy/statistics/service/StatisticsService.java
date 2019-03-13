package com.founder.xy.statistics.service;

import com.founder.e5.context.E5Exception;
import com.founder.xy.statistics.context.StatisticsContext;
import com.founder.xy.statistics.context.StatisticsExportContext;
import com.founder.xy.statistics.dao.StatisticsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/14.
 */

@Service
@Scope("prototype")
public class StatisticsService {
    @Autowired
    StatisticsContext statisticsContext;

    @Autowired
    StatisticsExportContext statisticsExportContext;

    @Autowired
    StatisticsDAO statisticsDAO;

    public Map<String, Object> getStatisticsData(Map<String, Object> inParam) throws E5Exception {
        return statisticsContext.statistics(inParam);
    }

    public Map<String, Object> getExportData(Map<String, Object> inParam) throws E5Exception {
        return statisticsExportContext.export(inParam);
    }

    public List<Map<String,Object>> getColumnTopicData(int siteID) {
        return statisticsDAO.getColumnTopicData(siteID);
    }
}
