package com.cip.ferrari.admin.biz;

import javax.annotation.Resource;

import com.cip.ferrari.admin.service.DynamicSchedulerService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cip.ferrari.admin.common.JobGroupEnum;
import com.cip.ferrari.admin.controller.param.FerrariJobParam;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.admin.service.FerrariJobLogService;
import com.cip.ferrari.commons.utils.QuartzUtil;

/**
 * @author haipop Date: 17-4-18 Time: 上午9:38
 */
@Component
public class FerrariJobBiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(FerrariJobBiz.class);

    @Resource
    private DynamicSchedulerService dynamicSchedulerBiz;

    @Resource
    private FerrariJobLogService ferrariJobLogService;

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;

    public String addFerrariLogicParamCheck(FerrariJobParam jobParam) {
        if (!CronExpression.isValidExpression(jobParam.getCronExpression())) {
            return "任务cron不合法";
        }
        String jobGroup = jobParam.getJobGroup();
        String triggerKeyName = QuartzUtil.generateTriggerKey(jobGroup, jobParam.getJobName());
        try {
            if (dynamicSchedulerBiz.hasExistsJob(triggerKeyName)) {
                return "分组[" + JobGroupEnum.valueOf(jobGroup).getDesc() + "]下任务名重复，请更改确认";
            }
        } catch (SchedulerException e) {
            String msg = "新增任务失败,查询任务是否存在发生异常,triggerKeyName=" + triggerKeyName;
            LOGGER.error(msg, e);
            return msg;
        }
        return StringUtils.EMPTY;
    }

}