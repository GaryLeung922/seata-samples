package io.seata.samples.dubbo;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @Author: Gary
 * @Date: 2023/9/24 16:44
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class IdempotentUtil {

    private static Table<Class<?>, String, String> map = HashBasedTable.create();

    public static void addMarker(Class<?> clazz, String xid, String marker) {
        map.put(clazz, xid, marker);
    }

    public static String getMarker(Class<?> clazz, String xid) {
        return map.get(clazz, xid);
    }

    public static void removeMarker(Class<?> clazz, String xid) {
        map.remove(clazz, xid);
    }
}
