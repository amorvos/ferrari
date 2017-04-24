package com.cip.ferrari.admin.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.cip.ferrari.admin.core.model.FerrariJobLog;

@Repository
public interface FerrariJobLogDao {

    int save(FerrariJobLog ferrariJobLog);

    FerrariJobLog load(@Param("id") long id);

    int updateTriggerInfo(FerrariJobLog ferrariJobLog);

    int updateHandleInfo(FerrariJobLog ferrariJobLog);

    List<FerrariJobLog> pageList(@Param("offset") int offset, @Param("pageSize") int pageSize,
            @Param("jobGroup") String jobGroup, @Param("jobName") String jobName,
            @Param("triggerTimeStart") Date triggerTimeStart, @Param("triggerTimeEnd") Date triggerTimeEnd);

    int pageListCount(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("jobGroup") String jobGroup,
            @Param("jobName") String jobName, @Param("triggerTimeStart") Date triggerTimeStart,
            @Param("triggerTimeEnd") Date triggerTimeEnd);

}
