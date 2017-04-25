package com.cip.ferrari.admin.core.router;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuxueli
 * tengkai.yuan
 */
public class CycleJobExecutorRouter extends AbstractExecutorRouter {

	// key-jobId, value-executeCount
	private static final ConcurrentHashMap<Integer, AtomicInteger> jobExecuteCountMap = new ConcurrentHashMap<Integer, AtomicInteger>();

	@Override
	public String doRoute(List<String> addressList, int jobId) {
		int jobExeCount = getNextJobExeCount(jobId, addressList);
		return addressList.get(jobExeCount % addressList.size());
	}

	private static int getNextJobExeCount(int jobId, List<String> addressList) {
		AtomicInteger count = jobExecuteCountMap.get(jobId);
		if (count == null) {
			count = new AtomicInteger(initCount(addressList.size()));
			jobExecuteCountMap.put(jobId, count); // 同一个jobId过来，可以忽略并发情况
			return count.intValue();
		}
		if(count.get() > 10000){
			count.set(initCount(addressList.size()));
		}
		return count.incrementAndGet();
	}

	private static int initCount(int size) {
		return new Random().nextInt(size);
	}

}
