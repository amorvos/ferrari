package com.cip.ferrari.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cip.ferrari.admin.core.model.FerrariJobExecutor;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.dao.FerrariJobExecutorDao;
import com.cip.ferrari.admin.dao.FerrariJobInfoDao;
import com.cip.ferrari.commons.ApiResult;

/**
 * 任务执行器 Created by xuxueli on 17/2/16.
 */
@Controller
@RequestMapping("/jobexecutor")
public class JobExecutorController {

    @Resource
    private FerrariJobExecutorDao ferrariJobExecutorDao;
    @Resource
    private FerrariJobInfoDao ferrariJobInfoDao;

    @RequestMapping
    public String index(Model model) {
        return "jobexecutor/jobexecutor.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length, String executeName) {

        // page list
        List<FerrariJobExecutor> list = ferrariJobExecutorDao.pageList(start, length, executeName);
        int list_count = ferrariJobExecutorDao.pageListCount(start, length, executeName);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count); // 总记录数
        maps.put("recordsFiltered", list_count); // 过滤后的总记录数
        maps.put("data", list); // 分页列表
        return maps;
    }

    @RequestMapping("/save")
    @ResponseBody
    public ApiResult save(String executeName, String desc, String address) {

        if (StringUtils.isBlank(executeName)) {
            return ApiResult.fail("请输入“执行器Name”");
        }
        if (StringUtils.isBlank(desc)) {
            return ApiResult.fail("请输入“执行器描述”");
        }
        if (StringUtils.isBlank(address)) {
            return ApiResult.fail("请输入“执行器地址”");
        }

        // executeName 限制唯一
        FerrariJobExecutor existExecutor = ferrariJobExecutorDao.getByExecuteName(executeName);
        if (existExecutor != null) {
            return ApiResult.fail("该执行器Name已存在，不可重复");
        }

        FerrariJobExecutor ferrariJobExecutor = new FerrariJobExecutor();
        ferrariJobExecutor.setExecuteName(executeName);
        ferrariJobExecutor.setDesc(desc);
        ferrariJobExecutor.setAddress(address);
        int num = ferrariJobExecutorDao.save(ferrariJobExecutor);
        return num > 0 ? ApiResult.succ() : ApiResult.fail();
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult delete(String executeName) {
        // executeName 被任务绑定，不允许删除
        List<FerrariJobInfo> jobInfoList = ferrariJobInfoDao.findJobByExecuteName(executeName);
        if (CollectionUtils.isNotEmpty(jobInfoList)) {
            return ApiResult.fail("该执行器已绑定任务，不允许删除");
        }

        int num = ferrariJobExecutorDao.delete(executeName);
        return num > 0 ? ApiResult.succ() : ApiResult.fail();
    }

    @RequestMapping("/update")
    @ResponseBody
    public ApiResult update(String executeName, String desc, String address) {

        if (StringUtils.isBlank(executeName)) {
            return ApiResult.fail("请输入“执行器Name”");
        }
        if (StringUtils.isBlank(desc)) {
            return ApiResult.fail("请输入“执行器描述”");
        }
        if (StringUtils.isBlank(address)) {
            return ApiResult.fail("请输入“执行器地址”");
        }

        // executeName 是否已删除
        FerrariJobExecutor existExecutor = ferrariJobExecutorDao.getByExecuteName(executeName);
        if (existExecutor == null) {
            return ApiResult.fail("该执行器不存在，更新失败");
        }

        existExecutor.setDesc(desc);
        existExecutor.setAddress(address);
        int num = ferrariJobExecutorDao.update(existExecutor);
        return num > 0 ? ApiResult.succ() : ApiResult.fail();
    }

}
