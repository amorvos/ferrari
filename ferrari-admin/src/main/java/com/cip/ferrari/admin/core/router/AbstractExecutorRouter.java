package com.cip.ferrari.admin.core.router;

import com.cip.ferrari.admin.common.JobRoutePolicyEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 执行器地址路由器
 * Created by xuxueli on 17/2/17.
 */
public abstract class AbstractExecutorRouter {

    public static String route(List<String> addressList, String routePolicy, int jobId){
        if (CollectionUtils.isEmpty(addressList)) {
            return null;
        }

        AbstractExecutorRouter jobExecutorRouter = JobRoutePolicyEnum.match(routePolicy, JobRoutePolicyEnum.CYCLE).getRouter();
        return jobExecutorRouter.doRoute(addressList, jobId);
    }

    public abstract String doRoute(List<String> addressList, int jobId);

    public static void main(String[] args) {
        List<String> list = Arrays.asList("111", "222", "333");
        int jobId = 666;

        System.out.println("----------------");
        for (int i = 0; i < 10; i++) {
            System.out.println(AbstractExecutorRouter.route(list, JobRoutePolicyEnum.CYCLE.name(), jobId));
        }

    }

}
