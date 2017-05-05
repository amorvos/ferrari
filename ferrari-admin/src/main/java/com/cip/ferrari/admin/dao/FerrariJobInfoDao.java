package com.cip.ferrari.admin.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cip.ferrari.admin.core.model.FerrariJobInfo;

/**
 * @author yuantengkai
 *
 */
@Repository
public interface FerrariJobInfoDao {

    int save(FerrariJobInfo ferrariJobInfo);

    List<FerrariJobInfo> pageList(int offset, int pagesize, String jobKey, String jobGroup, String executeName);

    int pageListCount(int offset, int pagesize, String jobKey, String jobGroup, String executeName);

    FerrariJobInfo get(int id);

    FerrariJobInfo getByKey(String triggerKeyName);

    int removeJob(String jobKey);

    int updateJobInfo(FerrariJobInfo jobInfo);

    List<FerrariJobInfo> findJobByExecuteName(String executeName);

}
