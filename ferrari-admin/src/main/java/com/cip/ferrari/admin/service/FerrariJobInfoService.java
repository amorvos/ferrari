/**
 *
 */
package com.cip.ferrari.admin.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.dao.IFerrariJobInfoDao;

/**
 * @author yuantengkai
 *
 */
@Service
public class FerrariJobInfoService {

    @Resource
    private IFerrariJobInfoDao iFerrariJobInfoDao;

    public int save(FerrariJobInfo ferrariJobInfo) {
        return iFerrariJobInfoDao.save(ferrariJobInfo);
    }

    public List<FerrariJobInfo> pageList(int offset, int pageSize, String jobKey, String jobGroup) {
        return iFerrariJobInfoDao.pageList(offset, pageSize, jobKey, jobGroup);
    }

    public int pageListCount(int offset, int pageSize, String jobKey, String jobGroup) {
        return iFerrariJobInfoDao.pageListCount(offset, pageSize, jobKey, jobGroup);
    }

    public FerrariJobInfo get(int id) {
        return iFerrariJobInfoDao.queryById(id);
    }

    public FerrariJobInfo getByKey(String jobKey) {
        return iFerrariJobInfoDao.getByKey(jobKey);
    }

    public int removeJob(String jobKey) {
        return iFerrariJobInfoDao.removeJob(jobKey);
    }

    public int updateJobInfo(FerrariJobInfo jobInfo) {
        return iFerrariJobInfoDao.updateJobInfo(jobInfo);
    }

}
