package com.cip.ferrari.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cip.ferrari.admin.biz.DynamicSchedulerBiz;
import com.cip.ferrari.admin.biz.FerrariCoreJobBiz;
import com.cip.ferrari.admin.common.FerrariConstantz;
import com.cip.ferrari.admin.common.JobGroupEnum;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.commons.ApiResult;
import com.cip.ferrari.core.common.JobConstants;

/**
 * index controller
 * 
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/job")
public class JobInfoController {

    private static Logger Logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private DynamicSchedulerBiz dynamicSchedulerBiz;

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;

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
            jobKey = jobGroup.concat(FerrariConstantz.job_group_name_split).concat(jobName);
        }

        // page list
        List<FerrariJobInfo> list = ferrariJobInfoService.pageList(start, length, jobKey, jobGroup);
        int list_count = ferrariJobInfoService.pageListCount(start, length, jobKey, jobGroup);

        // fill job info
        if (list != null && list.size() > 0) {
            for (FerrariJobInfo jobInfo : list) {
                dynamicSchedulerBiz.fillJobInfo(jobInfo);
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
    public ApiResult<String> addFerrari(String jobGroup, String jobName, String cronExpression, String job_desc,
            String job_address, String run_class, String run_method, String run_method_args, String owner,
            String mailReceives, int failAlarmNum) {

        // valid
        if (JobGroupEnum.match(jobGroup) == null) {
            return new ApiResult<String>(500, "请选择“任务组”");
        }
        if (StringUtils.isBlank(jobName)) {
            return new ApiResult<String>(500, "请输入“任务名”");
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

        // jobKey parse
        String triggerKeyName = dynamicSchedulerBiz.generateTriggerKey(jobGroup, jobName);

        try {
            boolean hasExists = dynamicSchedulerBiz.hasExistsJob(triggerKeyName);
            if (hasExists) {
                return new ApiResult<String>(500, "分组[" + JobGroupEnum.valueOf(jobGroup).getDesc() + "]下任务名重复，请更改确认");
            }
        } catch (SchedulerException e) {
            Logger.error("新增任务失败,查询任务是否存在发生异常,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.FAIL;
        }
        // store
        FerrariJobInfo jobInfo = new FerrariJobInfo();
        jobInfo.setJobGroup(jobGroup);
        jobInfo.setJobName(jobName);
        jobInfo.setJobKey(triggerKeyName);
        jobInfo.setJobDesc(job_desc);
        jobInfo.setOwner(owner);
        jobInfo.setMailReceives(mailReceives);
        jobInfo.setFailAlarmNum(failAlarmNum);
        ferrariJobInfoService.save(jobInfo);

        Map<String, Object> jobData = new HashMap<String, Object>();
        jobData.put(JobConstants.KEY_JOB_ADDRESS, job_address);
        jobData.put(JobConstants.KEY_RUN_CLASS, run_class);
        jobData.put(JobConstants.KEY_RUN_METHOD, run_method);
        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, run_method_args);
        jobData.put(FerrariConstantz.job_info_id, jobInfo.getId());

        // quartz
        try {
            boolean result = dynamicSchedulerBiz.addJob(triggerKeyName, cronExpression, FerrariCoreJobBiz.class,
                    jobData);
            if (!result) {
                return new ApiResult<String>(500, "分组[" + JobGroupEnum.valueOf(jobGroup).getDesc() + "]下任务名重复，请更改确认");
            }
            return ApiResult.SUCCESS;
        } catch (SchedulerException e) {
            Logger.error("新增任务失败,triggerKeyName=" + triggerKeyName, e);
        }
        return ApiResult.FAIL;
    }

    /**
     * 更新任务执行时间
     * 
     * @param triggerKeyName
     * @param cronExpression
     * @return
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
        jobData.put(FerrariConstantz.job_info_id, jobInfo.getId());

        try {
            boolean result = dynamicSchedulerBiz.rescheduleJob(triggerKeyName, cronExpression, jobData);
            ferrariJobInfoService.updateJobInfo(jobInfo);
            if (result) {
                return ApiResult.SUCCESS;
            }
        } catch (SchedulerException e) {
            Logger.error("更新任务执行时间失败,triggerKeyName=" + triggerKeyName, e);
            ;
        }
        return ApiResult.FAIL;
    }

    /**
     * 删除任务
     * 
     * @param triggerKeyName
     * @return
     */
    @RequestMapping("/remove")
    @ResponseBody
    public ApiResult<String> remove(String triggerKeyName) {
        try {
            dynamicSchedulerBiz.removeJob(triggerKeyName);
            ferrariJobInfoService.removeJob(triggerKeyName);
            return ApiResult.SUCCESS;
        } catch (SchedulerException e) {
            Logger.error("删除任务失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.FAIL;
        }
    }

    /**
     * 暂停任务调度
     * 
     * @param triggerKeyName
     * @return
     */
    @RequestMapping("/pause")
    @ResponseBody
    public ApiResult<String> pause(String triggerKeyName) {
        try {
            boolean result = dynamicSchedulerBiz.pauseJob(triggerKeyName);
            if (result) {
                return ApiResult.SUCCESS;
            }
            return ApiResult.FAIL;
        } catch (SchedulerException e) {
            Logger.error("暂停任务调度失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.FAIL;
        }
    }

    /**
     * 恢复任务调度
     * 
     * @param triggerKeyName
     * @return
     */
    @RequestMapping("/resume")
    @ResponseBody
    public ApiResult<String> resume(String triggerKeyName) {
        try {
            boolean result = dynamicSchedulerBiz.resumeJob(triggerKeyName);
            if (result) {
                return ApiResult.SUCCESS;
            }
            return ApiResult.FAIL;
        } catch (SchedulerException e) {
            Logger.error("恢复任务调度失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.FAIL;
        }
    }

    /**
     * 手动触发一次任务
     * 
     * @param triggerKeyName
     * @return
     */
    @RequestMapping("/trigger")
    @ResponseBody
    public ApiResult<String> triggerJob(String triggerKeyName) {
        try {
            boolean result = dynamicSchedulerBiz.triggerJob(triggerKeyName);
            if (result) {
                return ApiResult.SUCCESS;
            }
            return ApiResult.FAIL;
        } catch (SchedulerException e) {
            Logger.error("手动触发执行任务失败,triggerKeyName=" + triggerKeyName, e);
            return ApiResult.FAIL;
        }
    }

}
