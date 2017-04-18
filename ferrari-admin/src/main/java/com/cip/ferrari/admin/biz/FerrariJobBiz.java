package com.cip.ferrari.admin.biz;

import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.admin.service.FerrariJobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author haipop Date: 17-4-18 Time: 上午9:38
 */
@Component
public class FerrariJobBiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(FerrariJobBiz.class);

    @Resource
    private FerrariJobLogService ferrariJobLogService;

    @Resource
    private FerrariJobInfoService ferrariJobInfoService;



}