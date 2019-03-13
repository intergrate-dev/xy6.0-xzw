package com.founder.xy.statistics.interfaces;

import com.founder.e5.context.E5Exception;

import java.util.Map;

/**
 * Created by Ethan on 2017/2/14.
 */
public interface IStatisticsExport {
    Map<String, Object> export(Map<String, Object> inParam) throws E5Exception;
}
