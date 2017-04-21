package com.cip.ferrari.admin.controller.param;

import java.io.Serializable;

/**
 * @author haipop Date: 17-4-21 Time: 上午11:55
 */
public class FerrariJobStatusParam implements Serializable {

    private static final long serialVersionUID = -8516657954474919001L;

    String triggerKeyName;

    String cronExpression;

    String job_desc;

    String job_address;

    String run_class;

    String run_method;

    String run_method_args;

    String owner;

    String mailReceives;

    int failAlarmNum;

}