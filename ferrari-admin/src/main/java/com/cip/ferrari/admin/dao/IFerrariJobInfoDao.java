package com.cip.ferrari.admin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.cip.ferrari.admin.core.model.FerrariJobInfo;

@Repository
public interface IFerrariJobInfoDao {

    int save(FerrariJobInfo ferrariJobInfo);

    List<FerrariJobInfo> pageList(@Param("offset") int offset, @Param("pageSize") int pageSize,
            @Param("jobKey") String jobKey, @Param("jobGroup") String jobGroup);

    int pageListCount(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("jobKey") String jobKey,
            @Param("jobGroup") String jobGroup);

    FerrariJobInfo queryById(@Param("id") int id);

    FerrariJobInfo getByKey(@Param("triggerKeyName") String triggerKeyName);

    int removeJob(@Param("jobKey") String jobKey);

    int updateJobInfo(FerrariJobInfo jobInfo);

}
