package com.cip.ferrari.admin.dao;

import com.cip.ferrari.admin.core.model.FerrariJobExecutor;

import java.util.List;

/**
 * 任务执行器 dao
 */
public interface IFerrariJobExecutorDao {

    public int save(FerrariJobExecutor ferrariJobExecutor);
    
    public int delete(String executeName);
    
    public int update(FerrariJobExecutor ferrariJobExecutor);

    public List<FerrariJobExecutor> pageList(int offset, int pagesize, String executeName);
    
    public int pageListCount(int offset, int pagesize, String executeName);

    public FerrariJobExecutor getByExecuteName(String executeName);

    public List<FerrariJobExecutor> findAll();

}
