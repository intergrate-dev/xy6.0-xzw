package com.founder.xy.statistics.util;

import java.util.*;

/**
 * Created by Ethan on 2017/2/14.
 */
@SuppressWarnings("rawtypes")
public class ObjectUtil {

	public static List getList(Object object) {
        if (null == object) {
            return null;
        } else if (object instanceof List) {
            List objectList = new ArrayList();
            objectList = (List) object;
            return objectList;
        } else {
            throw new RuntimeException("The type of object cannot be parsed to ArrayList!");
        }
    }

    public static Map getHashMap(Object object) {
        if (null == object) {
            return null;
        } else if (object instanceof Map) {
            Map objectMap = new HashMap();
            objectMap = (Map) object;
            return objectMap;
        } else {
            throw new RuntimeException("The type of object cannot be parsed to HashMap!");
        }
    }

    public static Map getLinkedHashMap(Object object) {
        if (null == object) {
            return null;
        } else if (object instanceof Map) {
            Map objectMap = new LinkedHashMap();
            objectMap = (Map) object;
            return objectMap;
        } else {
            throw new RuntimeException("The type of object cannot be parsed to LinkedHashMap!");
        }
    }
}
