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

import com.cip.ferrari.admin.biz.DynamicSchedulerBiz;
import com.cip.ferrari.admin.biz.FerrariJobInfoBiz;
import com.cip.ferrari.admin.common.JobGroupEnum;
import com.cip.ferrari.admin.controller.param.FerrariJobInfoParam;
import com.cip.ferrari.admin.controller.param.FerrariJobStatusParam;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.commons.ApiResult;
import com.cip.ferrari.commons.constant.FerrariConstants;
import com.cip.ferrari.commons.utils.QuartzUtil;

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
    private DynamicSchedulerBiz dynamicSchedulerBiz;

    @Resource
    private FerrariJobInfoBiz ferrariJobBiz;

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;

    @Resource
    private DynamicSchedulerBiz dynamicSchedulerService;

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
    public ApiResult<String> addFerrari(@Validated FerrariJobInfoParam addJobParam, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ApiResult.fail(bindingResult.getFieldError().getDefaultMessage());
            }

            if (!CronExpression.isValidExpression(addJobParam.getCronExpression())) {
                return ApiResult.fail("任务cron不合法");
            }
            String jobGroup = addJobParam.getJobGroup();
            String triggerKeyName = QuartzUtil.generateTriggerKey(jobGroup, addJobParam.getJobName());
            try {
                if (dynamicSchedulerBiz.hasExistsJob(triggerKeyName)) {
                    return ApiResult.fail("分组[" + JobGroupEnum.valueOf(jobGroup).getDesc() + "]下任务名重复，请更改确认");
                }
            } catch (SchedulerException e) {
                String msg = "新增任务失败,查询任务是否存在发生异常,triggerKeyName=" + triggerKeyName;
                LOGGER.error(msg, e);
                return ApiResult.fail(msg);
            }
            boolean result = ferrariJobBiz.addJob(addJobParam);
            if (!result) {
                String msg = "分组[" + addJobParam.getJobGroup() + "]下任务名重复，请更改确认";
                LOGGER.warn(msg);
                return ApiResult.fail(msg);
            }
            return ApiResult.succ();
        } catch (Exception e) {
            LOGGER.error("新增任务失败,jobParam:{}", addJobParam, e);
            return ApiResult.fail("新增任务失败");
        }
    }

    /**
     * 更新任务配置
     */
    @RequestMapping("/reschedule")
    @ResponseBody
    public ApiResult<String> reschedule(@Validated FerrariJobStatusParam jobStatusParam, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ApiResult.fail(bindingResult.getFieldError().getDefaultMessage());
            }
            if (!CronExpression.isValidExpression(jobStatusParam.getCronExpression())) {
                return ApiResult.fail("“任务cron”不合法");
            }
            FerrariJobInfo jobInfo = ferrariJobBiz.fetchJobByTriggerKey(jobStatusParam.getTriggerKeyName());
            if (jobInfo == null) {
                return ApiResult.fail("系统异常");
            }
            if (ferrariJobBiz.updateJob(jobInfo, jobStatusParam)) {
                return ApiResult.fail("更新任务失败");
            }
            return ApiResult.succ();
        } catch (Exception e) {
            LOGGER.error("更新任务失败", e);
            return ApiResult.fail("更新任务失败");
        }
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
