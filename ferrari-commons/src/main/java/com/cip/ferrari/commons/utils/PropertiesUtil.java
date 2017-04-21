package com.cip.ferrari.commons.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.Closer;

/**
 * properties util
 * 
 * @author xuxueli 2015-8-28 10:35:53
 */
public final class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final String file_name = "ferrari.properties";

    private static final ConcurrentHashMap<String, String> localcache = new ConcurrentHashMap<String, String>();

    public static String getString(String key) {
        if (localcache.containsKey(key)) {
            return localcache.get(key);
        }
        Properties prop = loadProperties(file_name);
        if (prop != null) {
            String v = prop.getProperty(key);
            localcache.put(key, v);
            return v;
        }
        return null;
    }

    /**
     * load properties
     */
    private static Properties loadProperties(String propertyFileName) {
        Properties prop = new Properties();
        Closer closer = Closer.create();
        InputStreamReader in;
        try {
            URL url;
            ClassLoader loder = Thread.currentThread().getContextClassLoader();
            url = loder.getResource(propertyFileName);
            Preconditions.checkNotNull(url, "未找到该配置文件");
            in = new InputStreamReader(new FileInputStream(url.getPath()), "UTF-8");
            closer.register(in);
            prop.load(in);
            closer.close();
        } catch (IOException e) {
            logger.error("load {} error!", propertyFileName);
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                logger.error("流关闭异常, propertyFileName:{}", propertyFileName);
            }
        }
        return prop;
    }

}
