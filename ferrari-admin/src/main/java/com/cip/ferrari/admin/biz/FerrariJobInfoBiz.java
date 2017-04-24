package com.cip.ferrari.admin.biz;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cip.ferrari.admin.controller.param.FerrariJobInfoParam;
import com.cip.ferrari.admin.controller.param.FerrariJobStatusParam;
import com.cip.ferrari.admin.core.converter.FerrariJobInfoConverter;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.admin.service.FerrariJobLogService;
import com.cip.ferrari.commons.constant.FerrariConstants;
import com.cip.ferrari.commons.template.BizHandleTemplate;
import com.cip.ferrari.commons.template.BizProcessCallBack;
import com.cip.ferrari.core.constant.JobConstants;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author haipop Date: 17-4-18 Time: 上午9:38
 */
@Component
@SuppressWarnings("unchecked")
public class FerrariJobInfoBiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(FerrariJobInfoBiz.class);

    @Resource
    private DynamicSchedulerBiz dynamicSchedulerService;

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;

    @Resource
    private FerrariJobLogService ferrariJobLogService;

    @Transactional
    public boolean addJob(final FerrariJobInfoParam addJobParam) {
        return BizHandleTemplate.execute(new BizProcessCallBack<Boolean>() {
            @Override
            public Boolean process() {
                try {
                    FerrariJobInfo jobInfo = FerrariJobInfoConverter.jobParam2Info(addJobParam);
                    ferrariJobInfoService.save(jobInfo);
                    Map<String, Object> jobData = buildJobData(addJobParam, jobInfo);
                    return dynamicSchedulerService.addJob(jobInfo.getJobKey(), addJobParam.getCronExpression(),
                            FerrariCoreJobBiz.class, jobData);
                } catch (Exception e) {
                    LOGGER.error("添加Job出现异常", e);
                    return Boolean.FALSE;
                }
            }
        });
    }

    public FerrariJobInfo fetchJobByTriggerKey(final String triggerKey) {
        return BizHandleTemplate.execute(new BizProcessCallBack<FerrariJobInfo>() {

            @Override
            public void checkParams() {
                Preconditions.checkArgument(StringUtils.isNotEmpty(triggerKey), "triggerKey不能为空");
            }

            @Override
            public FerrariJobInfo process() {
                return ferrariJobInfoService.getJobByJobKey(triggerKey);
            }
        });
    }

    @Transactional
    public boolean updateJob(final FerrariJobInfo jobInfo, final FerrariJobStatusParam jobStatusParam) {
        jobInfo.setJobDesc(jobStatusParam.getJob_desc());
        jobInfo.setOwner(jobStatusParam.getOwner());
        jobInfo.setMailReceives(jobStatusParam.getMailReceives());
        jobInfo.setFailAlarmNum(jobStatusParam.getFailAlarmNum());
        Map<String, Object> jobData = buildJobData(jobStatusParam, jobInfo);
        try {
            if (dynamicSchedulerService.rescheduleJob(jobStatusParam.getTriggerKeyName(),
                    jobStatusParam.getCronExpression(), jobData)) {
                LOGGER.error("更新任务执行时间失败, jobStatusParam:{}, jobData:{}", jobStatusParam, jobData);
                return false;
            }
            return ferrariJobInfoService.updateJobInfo(jobInfo) > 0;
        } catch (SchedulerException e) {
            LOGGER.error("更新任务执行时间失败,triggerKeyName=" + jobStatusParam.getTriggerKeyName(), e);
            return false;
        }
    }

    private Map<String, Object> buildJobData(FerrariJobInfoParam jobInfoParam, FerrariJobInfo jobInfo) {
        Map<String, Object> jobData = Maps.newHashMap();
        jobData.put(JobConstants.KEY_JOB_ADDRESS, jobInfoParam.getJob_address());
        jobData.put(JobConstants.KEY_RUN_CLASS, jobInfoParam.getRun_class());
        jobData.put(JobConstants.KEY_RUN_METHOD, jobInfoParam.getRun_method());
        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, jobInfoParam.getRun_method_args());
        jobData.put(FerrariConstants.JOB_INFO_ID, jobInfo.getId());
        return jobData;
    }

    private Map<String, Object> buildJobData(FerrariJobStatusParam jobStatusParam, FerrariJobInfo jobInfo) {
        Map<String, Object> jobData = Maps.newHashMap();
        jobData.put(JobConstants.KEY_JOB_ADDRESS, jobStatusParam.getJob_address());
        jobData.put(JobConstants.KEY_RUN_CLASS, jobStatusParam.getRun_class());
        jobData.put(JobConstants.KEY_RUN_METHOD, jobStatusParam.getRun_method());
        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, jobStatusParam.getRun_method_args());
        jobData.put(FerrariConstants.JOB_INFO_ID, jobInfo.getId());
        return jobData;
    }

}