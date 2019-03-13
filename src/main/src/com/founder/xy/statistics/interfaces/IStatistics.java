package com.founder.xy.statistics.interfaces;

import com.founder.e5.context.E5Exception;

import java.util.Map;

/**
 * Created by Ethan on 2016/12/19.
 */
public interface IStatistics {
    Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception;
}
