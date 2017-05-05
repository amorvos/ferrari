//package com.cip.ferrari.admin.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Resource;
//
//import com.cip.ferrari.core.constant.JobConstants;
//import org.apache.commons.lang3.StringUtils;
//import org.quartz.CronExpression;
//import org.quartz.SchedulerException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.cip.ferrari.admin.biz.DynamicSchedulerBiz;
//import com.cip.ferrari.admin.biz.FerrariJobInfoBiz;
//import com.cip.ferrari.admin.common.JobGroupEnum;
//import com.cip.ferrari.admin.common.JobRoutePolicyEnum;
//import com.cip.ferrari.admin.controller.param.FerrariJobInfoParam;
//import com.cip.ferrari.admin.core.model.FerrariJobExecutor;
//import com.cip.ferrari.admin.core.model.FerrariJobInfo;
//import com.cip.ferrari.admin.service.FerrariJobInfoService;
//import com.cip.ferrari.admin.service.job.FerrariCoreJobBean;
//import com.cip.ferrari.commons.ApiResult;
//
///**
// * index controller
// *
// * @author xuxueli 2015-12-19 16:13:16
// */
//@Controller
//@RequestMapping("/job")
//public class JobController {
//
//    private static Logger LOGGER = LoggerFactory.getLogger(JobLogController.class);
//
//    @Resource
//    private DynamicSchedulerBiz dynamicSchedulerBiz;
//
//    @Resource
//    private FerrariJobInfoBiz ferrariJobBiz;
//
//    @Resource
//    private FerrariJobInfoService ferrariJobInfoService;
//
//    @Resource
//    private DynamicSchedulerBiz dynamicSchedulerService;
//
//    @RequestMapping
//    public String index(Model model, String jobGroup) {
//        if (!StringUtils.isBlank(jobGroup)) {
//            model.addAttribute("jobGroup", jobGroup);
//        }
//        model.addAttribute("groupEnum", JobGroupEnum.values());
//
//        // 在线 "执行器" 列表
//        List<FerrariJobExecutor> jobExecutorList = ferrariJobExecutorDao.findAll();
//        model.addAttribute("jobExecutorList", jobExecutorList);
//
//        // 执行路由策略
//        model.addAttribute("jobRoutePolicyEnum", JobRoutePolicyEnum.values());
//
//        return "job/index";
//    }
//
//    @RequestMapping("/pageList")
//    @ResponseBody
//    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
//            @RequestParam(required = false, defaultValue = "10") int length, String jobGroup, String jobName,
//            String executeName) {
//
//        String jobKey = null;
//        if (StringUtils.isNotBlank(jobGroup) && StringUtils.isNotBlank(jobName)) {
//            jobKey = jobGroup.concat(FerrariConstantz.job_group_name_split).concat(jobName);
//        }
//
//        // page list
//        List<FerrariJobInfo> list = ferrariJobInfoDao.pageList(start, length, jobKey, jobGroup, executeName);
//        int list_count = ferrariJobInfoDao.pageListCount(start, length, jobKey, jobGroup, executeName);
//
//        // fill job info
//        if (list != null && list.size() > 0) {
//            for (FerrariJobInfo jobInfo : list) {
//                DynamicSchedulerUtil.fillJobInfo(jobInfo);
//            }
//        }
//
//        // package result
//        Map<String, Object> maps = new HashMap<String, Object>();
//        maps.put("recordsTotal", list_count); // 总记录数
//        maps.put("recordsFiltered", list_count); // 过滤后的总记录数
//        maps.put("data", list); // 分页列表
//        return maps;
//    }
//
//    /**
//     * 新增一个任务
//     */
//    @RequestMapping("/addFerrari")
//    @ResponseBody
//    public ApiResult addFerrari(@Validated FerrariJobInfoParam addJobParam, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            return ApiResult.fail(bindingResult.getFieldError().getDefaultMessage());
//        }
//        if (!CronExpression.isValidExpression(addJobParam.getCronExpression())) {
//            return ApiResult.fail("任务cron不合法");
//        }
//        // 执行器校验&address
//        if (StringUtils.isNotBlank(addJobParam.getExecuteName())) {
//            FerrariJobExecutor jobExecutor = ferrariJobExecutorDao.getByExecuteName(executeName);
//            if (jobExecutor == null) {
//                return ApiResult.fail("所选择的执行器不存在");
//            }
//
//            // 路由策略校验
//            if (StringUtils.isBlank(routePolicy)) {
//                return ApiResult.fail("执行器路由策略不可为空");
//            }
//            JobRoutePolicyEnum routePolicyEnum = JobRoutePolicyEnum.match(routePolicy, null);
//            if (routePolicyEnum == null) {
//                return ApiResult.fail("执行器路由策略非法");
//            }
//            job_address = null;
//        } else {
//            routePolicy = null;
//            if (StringUtils.isBlank(job_address)) {
//                return ApiResult.fail("单机模式下地址不可为空");
//            }
//        }
//
//        // jobKey parse
//        String triggerKeyName = DynamicSchedulerUtil.generateTriggerKey(jobGroup, jobName);
//
//        try {
//            boolean hasExists = DynamicSchedulerUtil.hasExistsJob(triggerKeyName);
//            if (hasExists) {
//                return ApiResult.fail("分组[" + JobGroupEnum.valueOf(jobGroup).getDesc() + "]下任务名重复，请更改确认");
//            }
//        } catch (SchedulerException e) {
//            LOGGER.error("新增任务失败,查询任务是否存在发生异常,triggerKeyName=" + triggerKeyName, e);
//            return ApiResult.fail();
//        }
//        // store
//        FerrariJobInfo jobInfo = new FerrariJobInfo();
//        jobInfo.setJobGroup(jobGroup);
//        jobInfo.setJobName(jobName);
//        jobInfo.setJobKey(triggerKeyName);
//        jobInfo.setJobDesc(job_desc);
//        jobInfo.setOwner(owner);
//        jobInfo.setMailReceives(mailReceives);
//        jobInfo.setFailAlarmNum(failAlarmNum);
//        jobInfo.setExecuteName(executeName);
//        jobInfo.setRoutePolicy(routePolicy);
//        ferrariJobInfoDao.save(jobInfo);
//
//        Map<String, Object> jobData = new HashMap<String, Object>();
//        jobData.put(JobConstants.KEY_JOB_ADDRESS, job_address);
//        jobData.put(JobConstants.KEY_RUN_CLASS, run_class);
//        jobData.put(JobConstants.KEY_RUN_METHOD, run_method);
//        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, run_method_args);
//        jobData.put(FerrariConstantz.job_info_id, jobInfo.getId());
//
//        // quartz
//        try {
//            boolean result = DynamicSchedulerUtil.addJob(triggerKeyName, cronExpression, FerrariCoreJobBean.class,
//                    jobData);
//            if (!result) {
//                return ApiResult.fail("分组[" + JobGroupEnum.valueOf(jobGroup).getDesc() + "]下任务名重复，请更改确认");
//            }
//            return ApiResult.succ();
//        } catch (SchedulerException e) {
//            LOGGER.error("新增任务失败,triggerKeyName:{}", triggerKeyName, e);
//        }
//        return ApiResult.fail();
//    }
//
//    /**
//     * 更新任务
//     *
//     * @param triggerKeyName
//     * @param cronExpression
//     * @return
//     */
//    @RequestMapping("/reschedule")
//    @ResponseBody
//    public ApiResult reschedule(String triggerKeyName, String cronExpression, String job_desc, String job_address,
//            String run_class, String run_method, String run_method_args, String owner, String mailReceives,
//            int failAlarmNum, String executeName, String routePolicy) {
//
//        // valid
//        if (StringUtils.isBlank(triggerKeyName)) {
//            return ApiResult.fail("请输入“任务key”");
//        }
//        if (!CronExpression.isValidExpression(cronExpression)) {
//            return ApiResult.fail("“任务cron”不合法");
//        }
//        if (StringUtils.isBlank(job_desc)) {
//            return ApiResult.fail("请输入“任务描述”");
//        }
//        if (StringUtils.isBlank(run_class)) {
//            return ApiResult.fail("请输入“执行类”");
//        }
//        if (StringUtils.isBlank(run_method)) {
//            return ApiResult.fail("请输入“执行方法”");
//        }
//
//        // 执行器校验&address
//        if (StringUtils.isNotBlank(executeName)) {
//            FerrariJobExecutor jobExecutor = ferrariJobExecutorDao.getByExecuteName(executeName);
//            if (jobExecutor == null) {
//                return ApiResult.fail("所选择的执行器不存在");
//            }
//
//            // 路由策略校验
//            if (StringUtils.isBlank(routePolicy)) {
//                return ApiResult.fail("执行器路由策略不可为空");
//            }
//            JobRoutePolicyEnum routePolicyEnum = JobRoutePolicyEnum.match(routePolicy, null);
//            if (routePolicyEnum == null) {
//                return ApiResult.fail("执行器路由策略非法");
//            }
//            job_address = null;
//        } else {
//            routePolicy = null;
//            if (StringUtils.isBlank(job_address)) {
//                return ApiResult.fail("单机模式下地址不可为空");
//            }
//        }
//
//        FerrariJobInfo jobInfo = ferrariJobInfoDao.getByKey(triggerKeyName);
//        if (jobInfo == null) {
//            return ApiResult.fail("系统异常");
//        }
//        jobInfo.setJobDesc(job_desc);
//        jobInfo.setOwner(owner);
//        jobInfo.setMailReceives(mailReceives);
//        jobInfo.setFailAlarmNum(failAlarmNum);
//        jobInfo.setExecuteName(executeName);
//        jobInfo.setRoutePolicy(routePolicy);
//
//        Map<String, Object> jobData = new HashMap<String, Object>();
//        jobData.put(JobConstants.KEY_JOB_ADDRESS, job_address);
//        jobData.put(JobConstants.KEY_RUN_CLASS, run_class);
//        jobData.put(JobConstants.KEY_RUN_METHOD, run_method);
//        jobData.put(JobConstants.KEY_RUN_METHOD_ARGS, run_method_args);
//        jobData.put(FerrariConstantz.job_info_id, jobInfo.getId());
//
//        try {
//            boolean result = DynamicSchedulerUtil.rescheduleJob(triggerKeyName, cronExpression, jobData);
//            ferrariJobInfoDao.updateJobInfo(jobInfo);
//            if (result) {
//                return ApiResult.succ();
//            }
//        } catch (SchedulerException e) {
//            LOGGER.error("更新任务执行时间失败,triggerKeyName=" + triggerKeyName, e);
//            ;
//        }
//        return ApiResult.fail();
//    }
//
//
//    private String judgeExecuteModel(){
//
//    }
//
//    /**
//     * 删除任务
//     *
//     * @param triggerKeyName
//     * @return
//     */
//    @RequestMapping("/remove")
//    @ResponseBody
//    public ApiResult remove(String triggerKeyName) {
//        try {
//            DynamicSchedulerUtil.removeJob(triggerKeyName);
//            ferrariJobInfoDao.removeJob(triggerKeyName);
//            return ApiResult.succ();
//        } catch (SchedulerException e) {
//            LOGGER.error("删除任务失败,triggerKeyName=" + triggerKeyName, e);
//            return ApiResult.fail();
//        }
//    }
//
//    /**
//     * 暂停任务调度
//     *
//     * @param triggerKeyName
//     * @return
//     */
//    @RequestMapping("/pause")
//    @ResponseBody
//    public ApiResult pause(String triggerKeyName) {
//        try {
//            boolean result = DynamicSchedulerUtil.pauseJob(triggerKeyName);
//            if (result) {
//                return ApiResult.succ();
//            }
//            return ApiResult.fail();
//        } catch (SchedulerException e) {
//            LOGGER.error("暂停任务调度失败,triggerKeyName=" + triggerKeyName, e);
//            return ApiResult.fail();
//        }
//    }
//
//    /**
//     * 恢复任务调度
//     *
//     * @param triggerKeyName
//     * @return
//     */
//    @RequestMapping("/resume")
//    @ResponseBody
//    public ApiResult resume(String triggerKeyName) {
//        try {
//            boolean result = DynamicSchedulerUtil.resumeJob(triggerKeyName);
//            if (result) {
//                return ApiResult.succ();
//            }
//            return ApiResult.fail();
//        } catch (SchedulerException e) {
//            LOGGER.error("恢复任务调度失败,triggerKeyName=" + triggerKeyName, e);
//            return ApiResult.fail();
//        }
//    }
//
//    /**
//     * 手动触发一次任务
//     *
//     * @param triggerKeyName
//     * @return
//     */
//    @RequestMapping("/trigger")
//    @ResponseBody
//    public ApiResult triggerJob(String triggerKeyName) {
//        try {
//            boolean result = DynamicSchedulerUtil.triggerJob(triggerKeyName);
//            if (result) {
//                return ApiResult.succ();
//            }
//            return ApiResult.fail();
//        } catch (SchedulerException e) {
//            LOGGER.error("手动触发执行任务失败,triggerKeyName=" + triggerKeyName, e);
//            return ApiResult.fail();
//        }
//    }
//
//}
