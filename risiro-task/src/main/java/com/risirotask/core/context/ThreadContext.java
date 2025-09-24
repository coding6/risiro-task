package com.risirotask.core.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public class ThreadContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT_HOLDER = ThreadLocal.withInitial(HashMap::new);


    public static void put(String key, Object value) {
        CONTEXT_HOLDER.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT_HOLDER.get().get(key);
    }

    public static void remove(String key) {
        CONTEXT_HOLDER.get().remove(key);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public static Map<String, Object> capture() {
        return new HashMap<>(CONTEXT_HOLDER.get());
    }

    public static void restore(Map<String, Object> context) {
        CONTEXT_HOLDER.get().clear();
        if (context != null) {
            CONTEXT_HOLDER.get().putAll(context);
        }
    }
}
