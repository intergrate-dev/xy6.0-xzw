package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.statistics.interfaces.IStatisticsExport;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Ethan on 2017/2/14.
 */
@Service
public class WorkExportByPerson implements IStatisticsExport {
    @Override
    public Map<String, Object> export(Map<String, Object> inParam) throws E5Exception {
        return null;
    }
}
