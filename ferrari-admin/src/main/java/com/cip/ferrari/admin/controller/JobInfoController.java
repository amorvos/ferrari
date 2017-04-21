package com.cip.ferrari.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cip.ferrari.admin.biz.FerrariCoreJobBiz;
import com.cip.ferrari.admin.biz.FerrariJobBiz;
import com.cip.ferrari.admin.common.JobGroupEnum;
import com.cip.ferrari.admin.controller.param.FerrariAddJobParam;
import com.cip.ferrari.admin.core.converter.FerrariJobInfoConverter;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.service.DynamicSchedulerService;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.commons.ApiResult;
import com.cip.ferrari.commons.constant.FerrariConstants;
import com.cip.ferrari.core.constant.JobConstants;
import com.google.common.collect.Maps;

/**
 * index controller
 * 
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/job")
public class JobInfoController {

    private static Logger LOGGER = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private FerrariJobBiz ferrariJobBiz;

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;

    @Resource
    private DynamicSchedulerService dynamicSchedulerService;

    @RequestMapping
    public String index(Model model, String jobGroup) {
        if (!StringUtils.isBlank(jobGroup)) {
            model.addAttribute("jobGroup", jobGroup);
        }
        model.addAttribute("groupEnum", JobGroupEnum.values());
        return "job/index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length, String jobGroup, String jobName) {

        String jobKey = null;
        if (StringUtils.isNotBlank(jobGroup) && StringUtils.isNotBlank(jobName)) {
            jobKey = jobGroup.concat(FerrariConstants.JOB_GROUP_NAME_SPLIT).concat(jobName);
        }

        // page list
        List<FerrariJobInfo> list = ferrariJobInfoService.pageList(start, length, jobKey, jobGroup);
        int list_count = ferrariJobInfoService.pageListCount(start, length, jobKey, jobGroup);

        // fill job info
        if (list != null && list.size() > 0) {
            for (FerrariJobInfo jobInfo : list) {
                dynamicSchedulerService.fillJobInfo(jobInfo);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count); // 总记录数
        maps.put("recordsFiltered", list_count); // 过滤后的总记录数
        maps.put("data", list); // 分页列表
        return maps;
    }

    /**
     * ferrali定制版 新增一个任务
     */
    @RequestMapping("/addFerrari")
    @ResponseBody
    public ApiResult<String> addFerrari(@Validated @RequestParam FerrariAddJobParam addJobParam,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ApiResult.fail(bindingResult.getFieldError().getDefaultMessage());
            }
            String errMsg = ferrariJobBiz.addJobParamLogicCheck(addJobParam);
            if (StringUtils.isNotEmpty(errMsg)) {
                return ApiResult.fail(errMsg);
            }
            FerrariJobInfo jobInfo = FerrariJobInfoConverter.jobParam2Info(addJobParam);
            ferrariJobInfoService.save(jobInfo);
            Map<String, Object> jobData = buildJobData(addJobParam, jobInfo);

            boolean result = dynamicSchedulerService.addJob(jobInfo.getJobKey(), addJobParam.getCronExpression(),
                    FerrariCoreJobBiz.class, jobData);
            if (!result) {
                return ApiResult.fail("分组[" + JobGroupEnum.valueOf(jobInfo.getJobGroup()).getDesc() + "]下任务名重复，请更改确认");
            }
            return ApiResult.succ();
        } catch (SchedulerException e) {
            LOGGER.error("新增任务失败,jobParam:{}", addJobParam, e);
            return ApiResult.fail("新增任务失败");
        }
    }

    private Map<String, Object> buildJobData(FerrariAddJobParam jobParam, FerrariJobInfo jobInfo) {
        Map<String, Object> jobData = Maps.newHashMap();
        jobData.put(JobConstants.KEY_JOB_ADDRESS, jobParam.getJob_address());
        jobData.put(JobConstants.KEY_RUN_CLASS, jobParam.getRun_class());
        jobData.put(JobConstants.KEY_RUN_METHOD, jobParam.getRun_method());
        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, jobParam.getRun_method_args());
        jobData.put(FerrariConstants.JOB_INFO_ID, jobInfo.getId());
        return jobData;
    }

    /**
     * 更新任务执行时间
     */
    @RequestMapping("/reschedule")
    @ResponseBody
    public ApiResult<String> reschedule(String triggerKeyName, String cronExpression, String job_desc,
            String job_address, String run_class, String run_method, String run_method_args, String owner,
            String mailReceives, int failAlarmNum) {

        // valid
        if (StringUtils.isBlank(triggerKeyName)) {
            return new ApiResult<String>(500, "请输入“任务key”");
        }
        if (!CronExpression.isValidExpression(cronExpression)) {
            return new ApiResult<String>(500, "“任务cron”不合法");
        }
        if (StringUtils.isBlank(job_desc)) {
            return new ApiResult<String>(500, "请输入“任务描述”");
        }
        if (StringUtils.isBlank(job_address)) {
            return new ApiResult<String>(500, "请输入“执行机器地址”");
        }
        if (StringUtils.isBlank(run_class)) {
            return new ApiResult<String>(500, "请输入“执行类”");
        }
        if (StringUtils.isBlank(run_method)) {
            return new ApiResult<String>(500, "请输入“执行方法”");
        }

        FerrariJobInfo jobInfo = ferrariJobInfoService.getByKey(triggerKeyName);
        if (jobInfo == null) {
            return new ApiResult<String>(500, "系统异常");
        }
        jobInfo.setJobDesc(job_desc);
        jobInfo.setOwner(owner);
        jobInfo.setMailReceives(mailReceives);
        jobInfo.setFailAlarmNum(failAlarmNum);

        Map<String, Object> jobData = new HashMap<String, Object>();
        jobData.put(JobConstants.KEY_JOB_ADDRESS, job_address);
        jobData.put(JobConstants.KEY_RUN_CLASS, run_class);
        jobData.put(JobConstants.KEY_RUN_METHOD, run_method);
        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, run_method_args);
        jobData.put(FerrariConstants.JOB_INFO_ID, jobInfo.getId());

        try {
            boolean result = dynamicSchedulerService.rescheduleJob(triggerKeyName, cronExpression, jobData);
            ferrariJobInfoService.updateJobInfo(jobInfo);
            if (result) {
                return ApiResult.succ();
            }
        } catch (SchedulerException e) {
            LOGGER.error("更新任务执行时间失败,triggerKeyName=" + triggerKeyName, e);
        }
        return ApiResult.fail();
    }

    /**
     * 删除任务
     */
    @RequestMapping("/remove")
    @ResponseBody
    public ApiResult<String> remove(String triggerKeyName) {
        try {
            dynamicSchedulerService.removeJob(triggerKeyName);
            ferrariJobInfoService.removeJob(triggerKeyName);
            return ApiResult.succ();
        } catch (SchedulerException e) {
            LOGGER.error("删除任务失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.fail();
        }
    }

    /**
     * 暂停任务调度
     */
    @RequestMapping("/pause")
    @ResponseBody
    public ApiResult<String> pause(String triggerKeyName) {
        try {
            boolean result = dynamicSchedulerService.pauseJob(triggerKeyName);
            if (result) {
                return ApiResult.succ();
            }
            return ApiResult.fail();
        } catch (SchedulerException e) {
            LOGGER.error("暂停任务调度失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.fail();
        }
    }

    /**
     * 恢复任务调度
     */
    @RequestMapping("/resume")
    @ResponseBody
    public ApiResult<String> resume(String triggerKeyName) {
        try {
            boolean result = dynamicSchedulerService.resumeJob(triggerKeyName);
            if (result) {
                return ApiResult.succ();
            }
            return ApiResult.fail();
        } catch (SchedulerException e) {
            LOGGER.error("恢复任务调度失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.fail();
        }
    }

    /**
     * 手动触发一次任务
     */
    @RequestMapping("/trigger")
    @ResponseBody
    public ApiResult<String> triggerJob(String triggerKeyName) {
        try {
            boolean result = dynamicSchedulerService.triggerJob(triggerKeyName);
            if (result) {
                return ApiResult.succ();
            }
            return ApiResult.fail();
        } catch (SchedulerException e) {
            LOGGER.error("手动触发执行任务失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.fail();
        }
    }

}
