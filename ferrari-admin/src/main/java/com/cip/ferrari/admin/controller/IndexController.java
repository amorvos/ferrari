package com.cip.ferrari.admin.controller;

import com.cip.ferrari.admin.common.JobGroupEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * index controller
 * 
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Model model, String jobGroup) {
        if (!StringUtils.isBlank(jobGroup)) {
            model.addAttribute("jobGroup", jobGroup);
        }
        model.addAttribute("groupEnum", JobGroupEnum.values());
        return "job/index";
    }

    @RequestMapping("/help")
    public String help(Model model) {
        return "help";
    }

}
