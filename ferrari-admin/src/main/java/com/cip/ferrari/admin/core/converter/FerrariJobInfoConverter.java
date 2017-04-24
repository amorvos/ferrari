package com.cip.ferrari.admin.core.converter;

import com.cip.ferrari.admin.controller.param.FerrariJobInfoParam;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.commons.utils.QuartzUtil;

/**
 * @author haipop Date: 17-4-21 Time: 上午10:46
 */
public class FerrariJobInfoConverter {

    public static FerrariJobInfo jobParam2Info(FerrariJobInfoParam jobParam) {
        if (jobParam == null) {
            return null;
        }
        FerrariJobInfo ferrariJobInfo = new FerrariJobInfo();
        ferrariJobInfo.setJobGroup(jobParam.getJobGroup());
        ferrariJobInfo.setJobName(jobParam.getJobName());
        ferrariJobInfo.setJobCron(jobParam.getCronExpression());
        ferrariJobInfo.setJobDesc(jobParam.getJob_desc());
        ferrariJobInfo.setOwner(jobParam.getOwner());
        ferrariJobInfo.setMailReceives(jobParam.getMailReceives());
        ferrariJobInfo.setFailAlarmNum(jobParam.getFailAlarmNum());
        ferrariJobInfo.setJobKey(QuartzUtil.generateTriggerKey(jobParam.getJobGroup(), jobParam.getJobName()));
        return ferrariJobInfo;
    }
}