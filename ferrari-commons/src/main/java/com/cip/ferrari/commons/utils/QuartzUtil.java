package com.cip.ferrari.commons.utils;

import org.apache.commons.lang3.StringUtils;

import com.cip.ferrari.commons.constant.FerrariConstantz;

/**
 * @author haipop Date: 17-4-21 Time: 上午10:33
 */
public final class QuartzUtil {

    public static String generateTriggerKey(String jobGroup, String jobName) {
        if (StringUtils.isBlank(jobGroup) || StringUtils.isBlank(jobName)) {
            throw new IllegalArgumentException("任务参数非法");
        }
        return jobGroup.concat(FerrariConstantz.JOB_GROUP_NAME_SPLIT).concat(jobName);
    }
}