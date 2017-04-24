package com.cip.ferrari.commons.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class BizHandleTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizHandleTemplate.class);

    public static void executeNoResult(BizProcessCallBackNoResult action) {

        long startTime = System.currentTimeMillis();

        try {

            // 参数校验
            {
                action.checkParams();
            }

            // 执行业务操作
            {
                action.process();
            }

            // 监控成功结果
            {
                action.succMonitor(System.currentTimeMillis() - startTime);
            }
        } catch (RuntimeException runtimeException) {
            // 监控失败结果
            {
                action.failMonitor();
            }

            LOGGER.error("系统异常! error", runtimeException);
            throw runtimeException;
        } catch (Exception exception) {
            // 监控失败结果
            {
                action.failMonitor();
            }

            LOGGER.error("系统未知异常", exception);
            throw new RuntimeException(exception);
        } finally {
            try {

                {
                    action.afterProcess();
                }

            } catch (Exception e) {
                LOGGER.error("finally中调用方法出现异常！e:" + e.getMessage(), e);
            }
        }
    }

    public static <T> T execute(BizProcessCallBack<T> action) {

        T result = null;

        long startTime = System.currentTimeMillis();

        try {

            // 参数校验
            {
                action.checkParams();
            }

            // 执行业务操作
            {
                result = (T) action.process();
            }

            // 监控成功结果
            {
                action.succMonitor(System.currentTimeMillis() - startTime);
            }
        } catch (RuntimeException runtimeException) {
            // 监控失败结果
            {
                action.failMonitor();
            }

            LOGGER.error("系统异常! error", runtimeException);
            throw runtimeException;
        } catch (Exception exception) {
            // 监控失败结果
            {
                action.failMonitor();
            }

            LOGGER.error("系统未知异常", exception);
            throw new RuntimeException(exception);
        } finally {
            try {

                {
                    action.afterProcess();
                }

            } catch (Exception e) {
                LOGGER.error("finally中调用方法出现异常", e);
            }

        }
        return result;
    }
}