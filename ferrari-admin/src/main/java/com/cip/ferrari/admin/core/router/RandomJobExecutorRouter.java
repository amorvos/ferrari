package com.cip.ferrari.admin.core.router;


import java.util.List;
import java.util.Random;

/**
 * Created by xuxueli on 17/2/17.
 */
public class RandomJobExecutorRouter extends AbstractExecutorRouter {


    @Override
    public String doRoute(List<String> addressList, int jobId) {
    	int index = new Random().nextInt(addressList.size());
        return addressList.get(index);
    }
    
    public static void main(String[] args) {
		System.out.println(new Random().nextInt(10));
		System.out.println(new Random().nextInt(10));
		System.out.println(new Random().nextInt(10));
		System.out.println(new Random().nextInt(10));
		System.out.println(new Random().nextInt(10));
	}

}
