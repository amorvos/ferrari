package com.cip.ferrari.admin.controller.param;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author haipop Date: 17-4-21 Time: 上午11:55
 */
public class FerrariJobStatusParam implements Serializable {

    private static final long serialVersionUID = -8516657954474919001L;

    @Valid
    @NotNull(message = "请输入任务key")
    private String triggerKeyName;

    private String cronExpression;

    @Valid
    @NotNull(message = "请输入任务描述")
    private String job_desc;

    @Valid
    @NotNull(message = "请输入执行机器地址")
    private String job_address;

    @Valid
    @NotNull(message = "请输入执行类")
    private String run_class;

    @Valid
    @NotNull(message = "请输入执行方法")
    private String run_method;

    private String run_method_args;

    private String owner;

    private String mailReceives;

    private int failAlarmNum;

    public String getTriggerKeyName() {
        return triggerKeyName;
    }

    public void setTriggerKeyName(String triggerKeyName) {
        this.triggerKeyName = triggerKeyName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJob_desc() {
        return job_desc;
    }

    public void setJob_desc(String job_desc) {
        this.job_desc = job_desc;
    }

    public String getJob_address() {
        return job_address;
    }

    public void setJob_address(String job_address) {
        this.job_address = job_address;
    }

    public String getRun_class() {
        return run_class;
    }

    public void setRun_class(String run_class) {
        this.run_class = run_class;
    }

    public String getRun_method() {
        return run_method;
    }

    public void setRun_method(String run_method) {
        this.run_method = run_method;
    }

    public String getRun_method_args() {
        return run_method_args;
    }

    public void setRun_method_args(String run_method_args) {
        this.run_method_args = run_method_args;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMailReceives() {
        return mailReceives;
    }

    public void setMailReceives(String mailReceives) {
        this.mailReceives = mailReceives;
    }

    public int getFailAlarmNum() {
        return failAlarmNum;
    }

    public void setFailAlarmNum(int failAlarmNum) {
        this.failAlarmNum = failAlarmNum;
    }
}