package com.cip.ferrari.admin.dao.impl;

import com.cip.ferrari.admin.core.model.FerrariJobExecutor;
import com.cip.ferrari.admin.dao.IFerrariJobExecutorDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 任务执行器
 * Created by xuxueli on 17/2/16.
 */
@Repository(FerrariJobExecutorDaoImpl.BeanName)
public class FerrariJobExecutorDaoImpl implements IFerrariJobExecutorDao {
	
    public static final String BeanName = "ferrariJobExecutorDao";

    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public int save(FerrariJobExecutor ferrariJobExecutor) {
        return sqlSessionTemplate.insert("FerrariJobExecutorMapper.save", ferrariJobExecutor);
    }

    @Override
    public int delete(String executeName) {
        return sqlSessionTemplate.update("FerrariJobExecutorMapper.delete", executeName);
    }

    @Override
    public int update(FerrariJobExecutor ferrariJobExecutor) {
        return sqlSessionTemplate.update("FerrariJobExecutorMapper.update", ferrariJobExecutor);
    }

    @Override
    public List<FerrariJobExecutor> pageList(int offset, int pagesize, String executeName) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("offset", offset);
        params.put("pagesize", pagesize);
        params.put("executeName", executeName);
        return sqlSessionTemplate.selectList("FerrariJobExecutorMapper.pageList", params);
    }

    @Override
    public int pageListCount(int offset, int pagesize, String executeName) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("offset", offset);
        params.put("pagesize", pagesize);
        params.put("executeName", executeName);
        Integer result = sqlSessionTemplate.selectOne("FerrariJobExecutorMapper.pageListCount", params);
        if(result == null){
            return 0;
        }
        return result;
    }

    @Override
    public FerrariJobExecutor getByExecuteName(String executeName) {
        return sqlSessionTemplate.selectOne("FerrariJobExecutorMapper.getByExecuteName", executeName);
    }

    @Override
    public List<FerrariJobExecutor> findAll() {
        return sqlSessionTemplate.selectList("FerrariJobExecutorMapper.findAll");
    }

}
