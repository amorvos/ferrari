package com.cip.ferrari.commons.template;

public abstract class BizProcessCallBack<T> {

    public void checkParams() {
    }

    public abstract <T> T process();

    public void succMonitor(long execTime) {
    }

    public void failMonitor() {
    }

    public void afterProcess() {
    }

}
