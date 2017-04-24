package com.cip.ferrari.admin.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cip.ferrari.admin.core.model.FerrariJobLog;
import com.cip.ferrari.admin.dao.FerrariJobLogDao;

@Service
public class FerrariJobLogService {

    @Resource
    private FerrariJobLogDao iFerrariJobLogDao;

    public int save(FerrariJobLog ferrariJobLog) {
        return iFerrariJobLogDao.save(ferrariJobLog);
    }

    public FerrariJobLog load(long id) {
        return iFerrariJobLogDao.load(id);
    }

    public int updateTriggerInfo(FerrariJobLog ferrariJobLog) {
        return iFerrariJobLogDao.updateTriggerInfo(ferrariJobLog);
    }

    public int updateHandleInfo(FerrariJobLog ferrariJobLog) {
        return iFerrariJobLogDao.updateHandleInfo(ferrariJobLog);
    }

    public List<FerrariJobLog> pageList(int offset, int pagesize, String jobGroup, String jobName,
            Date triggerTimeStart, Date triggerTimeEnd) {
        return iFerrariJobLogDao.pageList(offset, pagesize, jobGroup, jobName, triggerTimeStart, triggerTimeEnd);
    }

    public int pageListCount(int offset, int pagesize, String jobGroup, String jobName, Date triggerTimeStart,
            Date triggerTimeEnd) {
        return iFerrariJobLogDao.pageListCount(offset, pagesize, jobGroup, jobName, triggerTimeStart, triggerTimeEnd);
    }

}
