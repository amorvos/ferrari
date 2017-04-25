package com.cip.ferrari.admin.common;

import java.util.HashMap;
import java.util.Map;

import com.cip.ferrari.admin.core.router.AbstractExecutorRouter;
import com.cip.ferrari.admin.core.router.CycleJobExecutorRouter;
import com.cip.ferrari.admin.core.router.RandomJobExecutorRouter;

/**
 * 执行器地址路由策略
 * Created by xuxueli on 17/2/17.
 */
public enum JobRoutePolicyEnum {

    CYCLE("循环", new CycleJobExecutorRouter()),
    RAMDOM("随机", new RandomJobExecutorRouter());
    

    private String desc;
    private AbstractExecutorRouter router;
    private JobRoutePolicyEnum(String desc, AbstractExecutorRouter router){
        this.desc = desc;
        this.router = router;
    }
   
    public String getDesc() {
		return desc;
	}

	public AbstractExecutorRouter getRouter() {
        return router;
    }

    public static JobRoutePolicyEnum match(String name, JobRoutePolicyEnum defaultItem){
        for (JobRoutePolicyEnum item: JobRoutePolicyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

    public static void main(String[] args) {
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("1", null);
		System.out.println(param);
		String a = (String) param.get("1");
		System.out.println(a);
		System.out.println(String.valueOf(param.get("1")));
	}
}
