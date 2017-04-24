package com.cip.ferrari.commons.template;

public abstract class BizProcessCallBackNoResult {

    public void checkParams() {
    }

    public abstract void process();

    public void succMonitor(long execTime) {
    }

    public void failMonitor() {
    }

    public void afterProcess() {
    }
}
