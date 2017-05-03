package com.cip.ferrari.admin.biz;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.cip.ferrari.admin.alarm.MonitorEntity;
import com.cip.ferrari.admin.alarm.MonitorManager;
import com.cip.ferrari.admin.common.JobGroupEnum;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.core.model.FerrariJobLog;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.admin.service.FerrariJobLogService;
import com.cip.ferrari.commons.LocalHost;
import com.cip.ferrari.commons.constant.FerrariConstants;
import com.cip.ferrari.commons.utils.HttpUtil;
import com.cip.ferrari.commons.utils.JacksonUtil;
import com.cip.ferrari.commons.utils.PropertiesUtil;
import com.cip.ferrari.core.constant.JobConstants;
import com.cip.ferrari.core.job.result.FerrariFeedback;
import com.google.common.base.Preconditions;

/**
 * ferrari任务版本，适用于：ferrari-core
 * 
 * @author xuxueli 2015-12-17 18:20:34,多例
 */
@Component
public class FerrariCoreJobBiz extends QuartzJobBean {

    private static Logger LOGGER = LoggerFactory.getLogger(FerrariCoreJobBiz.class);

    private static final String PORT = "8080";

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;

    @Resource
    private FerrariJobLogService ferrariJobLogService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String triggerKeyName = context.getTrigger().getJobKey().getName();
        Map<String, Object> jobDataMap = context.getMergedJobDataMap().getWrappedMap();

        // save log
        FerrariJobLog jobLog = new FerrariJobLog();
        String jobKey = context.getTrigger().getJobKey().getName();
        String[] groupAndName = jobKey.split(FerrariConstants.JOB_GROUP_NAME_SPLIT);
        String jobGroup = null;
        String jobName = null;
        if (groupAndName.length == 2) {
            jobGroup = groupAndName[0];
            jobName = groupAndName[1];
        } else {
            jobGroup = JobGroupEnum.DEFAULT.name();// 设为默认的
            jobName = jobKey;
        }
        jobLog.setJobGroup(jobGroup);
        jobLog.setJobName(jobName);
        jobLog.setJobInfoId(-1);

        try {
            Integer jobInfoId = Integer.valueOf(jobDataMap.get(FerrariConstants.JOB_INFO_ID) + "");
            if (jobInfoId != null) {
                jobLog.setJobInfoId(jobInfoId);
            } else {
                FerrariJobInfo jobInfo = ferrariJobInfoService.getJobByJobKey(jobKey);
                if (jobInfo != null) {
                    jobLog.setJobInfoId(jobInfo.getId());
                }
            }
        } catch (Exception e) {
            FerrariJobInfo jobInfo = ferrariJobInfoService.getJobByJobKey(jobKey);
            if (jobInfo != null) {
                jobLog.setJobInfoId(jobInfo.getId());
            }
        }

        jobLog.setJobCron((context.getTrigger() instanceof CronTriggerImpl)
                ? (((CronTriggerImpl) context.getTrigger()).getCronExpression()) : "");
        jobLog.setJobClass(FerrariCoreJobBiz.class.getName());
        jobLog.setJobData(JacksonUtil.encode(jobDataMap));
        ferrariJobLogService.save(jobLog);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("############ferrari job trigger starting..., jobLog:{}", jobLog);
        }

        // request param
        Map<String, String> params = new HashMap<String, String>();
        String targetIPPort = String.valueOf(jobDataMap.get(JobConstants.KEY_JOB_ADDRESS));
        String job_url = "http://" + targetIPPort + PropertiesUtil.getString(FerrariConstants.RECEIVE_SERVLET_PATH);

        params.put(JobConstants.KEY_UUID, jobLog.getId() + "");
        params.put(JobConstants.KEY_RESULT_URL_LIST, LocalHost.getLoopbackV4() + ":" + PORT);
        params.put(JobConstants.KEY_ACTION, JobConstants.VALUE_ACTION_RUN_JOB);
        params.put(JobConstants.KEY_JOB_NAME, triggerKeyName);
        params.put(JobConstants.KEY_RUN_CLASS, String.valueOf(jobDataMap.get(JobConstants.KEY_RUN_CLASS)));
        params.put(JobConstants.KEY_RUN_METHOD, String.valueOf(jobDataMap.get(JobConstants.KEY_RUN_METHOD)));
        params.put(JobConstants.KEY_RUN_METHOD_ARGS, String.valueOf(jobDataMap.get(JobConstants.KEY_RUN_METHOD_ARGS)));
        params.put(JobConstants.KEY_BEGIN_TIME, System.currentTimeMillis() + "");

        // 发起http触发请求
        String[] postResp = HttpUtil.post(job_url, params);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("############ferrari job trigger http response, jobLog.id:{}, response:{}", jobLog.getId(),
                    postResp);
        }

        // parse trigger response
        String responseMsg = postResp[0];
        String exceptionMsg = postResp[1];
        jobLog.setTriggerTime(new Date());
        jobLog.setTriggerHost(LocalHost.getLoopbackV4() + "__" + LocalHost.getHostName());
        jobLog.setTriggerStatus(HttpUtil.FAIL);
        jobLog.setTriggerMsg(exceptionMsg);
        if (StringUtils.isNotBlank(responseMsg)) {
            FerrariFeedback retVo = null;
            try {
                retVo = JacksonUtil.decode(responseMsg, FerrariFeedback.class);
            } catch (Exception e) {
            }
            if (retVo != null) {
                jobLog.setTriggerStatus(retVo.isStatus() ? HttpUtil.SUCCESS : HttpUtil.FAIL);
                jobLog.setTriggerMsg(retVo.getErrormsg());
            }
        }

        // update trigger info
        if (jobLog.getTriggerMsg() != null && jobLog.getTriggerMsg().length() > 1900) {
            jobLog.setTriggerMsg(jobLog.getTriggerMsg().substring(0, 1880));
        }
        Preconditions.checkArgument(ferrariJobLogService.updateTriggerInfo(jobLog) > 0 , " 更新失败");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("############ferrari job trigger end, jobLog.id:{}", jobLog.getId());
        }
        if (HttpUtil.FAIL.equalsIgnoreCase(jobLog.getTriggerStatus())) {// 发起执行失败，触发报警
            MonitorManager.getInstance().put2AlarmDeal(new MonitorEntity(jobLog.getId(), false));
        }

    }

}