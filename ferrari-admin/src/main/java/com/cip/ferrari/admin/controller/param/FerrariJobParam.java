package com.cip.ferrari.admin.controller.param;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author haipop Date: 17-4-21 Time: 上午10:19
 */
public class FerrariJobParam implements Serializable {

    private static final long serialVersionUID = -4222785131069762664L;

    @Valid
    @NotNull(message = "请选择任务组")
    private String jobGroup;

    @Valid
    @NotNull(message = "请选择任务名")
    private String jobName;

    @Valid
    @NotNull(message = "请选择任务组")
    private String cronExpression;

    @Valid
    @NotNull(message = "请选择任务描述")
    private String job_desc;

    @Valid
    @NotNull(message = "请选择执行机器地址")
    private String job_address;

    @Valid
    @NotNull(message = "请选择执行类")
    private String run_class;

    @Valid
    @NotNull(message = "请选择执行方法")
    private String run_method;

    private String run_method_args;

    @Valid
    @NotNull(message = "请选择所属人员")
    private String owner;

    @Valid
    @NotNull(message = "请输入邮件地址")
    private String mailReceives;

    @Valid
    @NotNull(message = "请输入失败次数阈值")
    private Integer failAlarmNum;

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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