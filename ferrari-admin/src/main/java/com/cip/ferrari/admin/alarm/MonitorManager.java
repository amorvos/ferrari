/**
 * 
 */
package com.cip.ferrari.admin.alarm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cip.ferrari.admin.common.FerrariBeanFactory;
import com.cip.ferrari.admin.core.model.FerrariJobInfo;
import com.cip.ferrari.admin.core.model.FerrariJobLog;
import com.cip.ferrari.admin.service.FerrariJobInfoService;
import com.cip.ferrari.admin.service.FerrariJobLogService;
import com.cip.ferrari.admin.service.alarm.AlarmService;
import com.cip.ferrari.admin.service.alarm.DefaultAlarmServiceImpl;

/**
 * @author yuantengkai 监控报警管理类
 */
public class MonitorManager {

    private static final Logger logger = LoggerFactory.getLogger(MonitorManager.class);

    private static final MonitorManager instatnce = new MonitorManager();

    private final BlockingQueue<MonitorEntity> monitorList = new LinkedBlockingQueue<MonitorEntity>(20000);

    private final ConcurrentHashMap<Integer, AtomicInteger> staticsMap = new ConcurrentHashMap<Integer, AtomicInteger>();

    private FerrariJobLogService ferrariJobLogService;

    private FerrariJobInfoService ferrariJobInfoService;

    private AlarmService defaultAlarmService;

    private MonitorManager() {
        startDealAlarm();
    }

    /**
     * 开启报警线程
     */
    private void startDealAlarm() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        MonitorEntity entity = monitorList.take();
                        if (ferrariJobLogService == null) {
                            ferrariJobLogService = (FerrariJobLogService) FerrariBeanFactory
                                    .getBean(FerrariJobLogService.class.getSimpleName());
                        }
                        FerrariJobLog jobLog = ferrariJobLogService.load(entity.getJobLogId());
                        int jobInfoId = jobLog.getJobInfoId();
                        if (entity.isSuccess()) {// 成功
                            staticsMap.remove(jobInfoId);
                            continue;
                        }
                        if (ferrariJobInfoService == null) {
                            ferrariJobInfoService = (FerrariJobInfoService) FerrariBeanFactory
                                    .getBean(FerrariJobInfoService.class.getSimpleName());
                        }
                        FerrariJobInfo jobInfo = ferrariJobInfoService.get(jobInfoId);
                        if (jobInfo == null) {// job已被删除，则无需触发报警
                            continue;
                        }
                        // 失败次数统计
                        int currentFailCount = 1;
                        if (staticsMap.containsKey(jobInfoId)) {
                            currentFailCount = staticsMap.get(jobInfoId).incrementAndGet();
                        } else {
                            staticsMap.put(jobInfoId, new AtomicInteger(1));
                        }
                        if (currentFailCount >= jobInfo.getFailAlarmNum()) {
                            sendAlarm(jobInfo, jobLog);// 发送报警
                            staticsMap.remove(jobInfoId);
                        }
                    } catch (Throwable t) {
                        logger.error("Ferrari job alarm deal exception", t);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }

            }
        });
        t.setDaemon(true);
        t.setName("Ferrari-Alarm-Thread");
        t.start();
    }

    /**
     * 发送报警信息
     * 
     * @param jobInfo
     * @param jobLog
     */
    private void sendAlarm(FerrariJobInfo jobInfo, FerrariJobLog jobLog) {
        if (defaultAlarmService == null) {
            defaultAlarmService = (AlarmService) FerrariBeanFactory
                    .getBean(DefaultAlarmServiceImpl.class.getSimpleName());
        }
        String mailReceives = jobInfo.getMailReceives();
        if (StringUtils.isBlank(mailReceives)) {
            return;
        }
        String[] addressArr = mailReceives.split(",");
        String subject = "Ferrari-Job失败告警";
        StringBuilder sb = new StringBuilder();
        sb.append("任务名:").append(jobLog.getJobName()).append("\n");
        sb.append("任务信息:").append(jobLog.getJobData()).append("\n");
        sb.append("连续失败次数:").append(jobInfo.getFailAlarmNum()).append("\n");
        defaultAlarmService.sendMail(addressArr, subject, sb.toString());
    }

    public static MonitorManager getInstance() {
        return instatnce;
    }

    /**
     * 监控报警处理
     * 
     * @param entity
     */
    public void put2AlarmDeal(MonitorEntity entity) {
        monitorList.offer(entity);
    }

}
