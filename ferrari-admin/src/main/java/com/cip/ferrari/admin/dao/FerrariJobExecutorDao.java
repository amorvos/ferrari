package com.cip.ferrari.admin.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cip.ferrari.admin.core.model.FerrariJobExecutor;

/**
 * 任务执行器 dao
 */
@Repository
public interface FerrariJobExecutorDao {

    int save(FerrariJobExecutor ferrariJobExecutor);

    int delete(String executeName);

    int update(FerrariJobExecutor ferrariJobExecutor);

    List<FerrariJobExecutor> pageList(int offset, int pagesize, String executeName);

    int pageListCount(int offset, int pagesize, String executeName);

    FerrariJobExecutor getByExecuteName(String executeName);

    List<FerrariJobExecutor> findAll();

}
