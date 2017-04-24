/**
 *
 */
package com.cip.ferrari.admin.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.dao.FerrariJobInfoDao;

/**
 * @author yuantengkai
 *
 */
@Service
public class FerrariJobInfoService {

    @Resource
    private FerrariJobInfoDao ferrariJobInfoDao;

    public int save(FerrariJobInfo ferrariJobInfo) {
        return ferrariJobInfoDao.save(ferrariJobInfo);
    }

    public List<FerrariJobInfo> pageList(int offset, int pageSize, String jobKey, String jobGroup) {
        return ferrariJobInfoDao.pageList(offset, pageSize, jobKey, jobGroup);
    }

    public int pageListCount(int offset, int pageSize, String jobKey, String jobGroup) {
        return ferrariJobInfoDao.pageListCount(offset, pageSize, jobKey, jobGroup);
    }

    public FerrariJobInfo get(int id) {
        return ferrariJobInfoDao.queryById(id);
    }

    public FerrariJobInfo getJobByJobKey(String jobKey) {
        return ferrariJobInfoDao.getJobByJobKey(jobKey);
    }

    public int removeJob(String jobKey) {
        return ferrariJobInfoDao.removeJob(jobKey);
    }

    public int updateJobInfo(FerrariJobInfo jobInfo) {
        return ferrariJobInfoDao.updateJobInfo(jobInfo);
    }

}
