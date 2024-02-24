package com.risirotask.util;

import org.springframework.context.ApplicationContext;

public class SpringContextUtil {
    public static ApplicationContext ac;

    public static <T>  T getBean(String beanName, Class<T> clazz) {
        return ac.getBean(beanName, clazz);
    }

    public static void setAc(ApplicationContext applicationContext){
        ac = applicationContext;
    }
}
