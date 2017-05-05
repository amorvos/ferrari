//package com.cip.ferrari.admin.service.job;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.cip.ferrari.commons.constant.FerrariConstants;
//import com.cip.ferrari.commons.utils.JacksonUtil;
//import com.cip.ferrari.commons.utils.PropertiesUtil;
//import com.cip.ferrari.core.constant.JobConstants;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.impl.triggers.CronTriggerImpl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
//import com.cip.ferrari.admin.alarm.MonitorEntity;
//import com.cip.ferrari.admin.alarm.MonitorManager;
//import com.cip.ferrari.admin.common.FerrariBeanFactory;
//import com.cip.ferrari.admin.common.JobGroupEnum;
//import com.cip.ferrari.admin.core.model.FerrariJobExecutor;
//import com.cip.ferrari.admin.core.model.FerrariJobInfo;
//import com.cip.ferrari.admin.core.model.FerrariJobLog;
//import com.cip.ferrari.admin.core.router.AbstractExecutorRouter;
//import com.cip.ferrari.admin.dao.FerrariJobExecutorDao;
//import com.cip.ferrari.admin.dao.FerrariJobInfoDao;
//import com.cip.ferrari.core.job.result.FerrariFeedback;
//
///**
// *
// * @author xuxueli 2015-12-17 18:20:34,多例
// */
//public class FerrariCoreJobBean extends QuartzJobBean {
//	private static Logger logger = LoggerFactory
//			.getLogger(FerrariCoreJobBean.class);
//
//	private final String PORT = "8080";
//
//	private IFerrariJobLogDao ferrariJobLogDao;
//
//	private FerrariJobInfoDao ferrariJobInfoDao;
//
//	private FerrariJobExecutorDao ferrariJobExecutorDao;
//
//	@Override
//	protected void executeInternal(JobExecutionContext context)
//			throws JobExecutionException {
//		String triggerKeyName = context.getTrigger().getJobKey().getName();
//		Map<String, Object> jobDataMap = context.getMergedJobDataMap()
//				.getWrappedMap();
//		//copy
//		Map<String, Object> jobDataMapCopy = new HashMap<String, Object>();
//		jobDataMapCopy.putAll(jobDataMap);
//
//		String jobKey = context.getTrigger().getJobKey().getName();
//		String[] groupAndName = jobKey
//				.split(FerrariConstants.job_group_name_split);
//		String jobGroup = null;
//		String jobName = null;
//		if (groupAndName.length == 2) {
//			jobGroup = groupAndName[0];
//			jobName = groupAndName[1];
//		} else {
//			jobGroup = JobGroupEnum.DEFAULT.name();// 设为默认的
//			jobName = jobKey;
//		}
//
//		if (ferrariJobInfoDao == null) {
//			ferrariJobInfoDao = (FerrariJobInfoDao) FerrariBeanFactory.getBean(FerrariJobInfoDaoImpl.BeanName);
//		}
//		if (ferrariJobLogDao == null) {
//			ferrariJobLogDao = (IFerrariJobLogDao) FerrariBeanFactory.getBean(FerrariJobLogDaoImpl.BeanName);
//		}
//		if (ferrariJobExecutorDao == null) {
//			ferrariJobExecutorDao = (FerrariJobExecutorDao) FerrariBeanFactory.getBean(FerrariJobExecutorDaoImpl.BeanName);
//		}
//
//
//		//执行机器地址选择
//		String job_address = (String) jobDataMapCopy.get(JobConstants.KEY_JOB_ADDRESS);
//		StringBuffer routeMsg = new StringBuffer();
//		if(StringUtils.isBlank(job_address)){ // 如果为空，则说明是执行器模式
//			FerrariJobInfo jobInfo = ferrariJobInfoDao.getByKey(jobKey);
//			FerrariJobExecutor ferrariJobExecutor = ferrariJobExecutorDao.getByExecuteName(jobInfo.getExecuteName());
//			if (ferrariJobExecutor != null && ferrariJobExecutor.getAddress() != null) {
//				job_address = AbstractExecutorRouter.route(Arrays.asList(ferrariJobExecutor.getAddress().split(",")), jobInfo.getRoutePolicy(), jobInfo.getId());
//				jobDataMapCopy.put(JobConstants.KEY_JOB_ADDRESS, job_address);
//
//				routeMsg = new StringBuffer("执行器模式：").append(jobInfo.getExecuteName())
//						.append("<br>机器地址列表：").append(ferrariJobExecutor.getAddress())
//						.append("<br>路由规则：").append(jobInfo.getRoutePolicy())
//						.append("<br>目标机器地址：").append(job_address);
//			}
//		}else{ //说明是单机模式
//			routeMsg.append("单机模式，目标机器地址：").append(job_address);
//		}
//		routeMsg.append("<hr>");
//
//		// save log
//		FerrariJobLog jobLog = new FerrariJobLog();
//		jobLog.setJobGroup(jobGroup);
//		jobLog.setJobName(jobName);
//		Integer jobInfoId = Integer.valueOf(jobDataMapCopy.get(FerrariConstants.JOB_INFO_ID) + "");
//		jobLog.setJobInfoId(jobInfoId);
//
//		jobLog.setJobCron((context.getTrigger() instanceof CronTriggerImpl) ? (((CronTriggerImpl) context
//				.getTrigger()).getCronExpression()) : "");
//		jobLog.setJobClass(FerrariCoreJobBean.class.getName());
//		jobLog.setJobData(JacksonUtil.encode(jobDataMapCopy));
//
//		ferrariJobLogDao.save(jobLog);
//		if (logger.isInfoEnabled()) {
//			logger.info(
//					"############ferrari job trigger starting..., jobLog:{}",
//					jobLog);
//		}
//
//		// request param
//		Map<String, String> params = new HashMap<String, String>();
//		String job_url = "http://" + job_address + PropertiesUtil.getString(FerrariConstants.RECEIVE_SERVLET_PATH);
//
//		params.put(JobConstants.KEY_UUID, jobLog.getId() + "");
//		params.put(JobConstants.KEY_RESULT_URL_LIST, HostUtil.getIP() + ":"
//				+ PORT);
//		params.put(JobConstants.KEY_ACTION, JobConstants.VALUE_ACTION_RUN_JOB);
//		params.put(JobConstants.KEY_JOB_NAME, triggerKeyName);
//		params.put(JobConstants.KEY_RUN_CLASS,
//				String.valueOf(jobDataMapCopy.get(JobConstants.KEY_RUN_CLASS)));
//		params.put(JobConstants.KEY_RUN_METHOD,
//				String.valueOf(jobDataMapCopy.get(JobConstants.KEY_RUN_METHOD)));
//		params.put(JobConstants.KEY_RUN_METHOD_ARGS, String.valueOf(jobDataMapCopy
//				.get(JobConstants.KEY_RUN_METHOD_ARGS)));
//		params.put(JobConstants.KEY_BEGIN_TIME, System.currentTimeMillis() + "");
//
//		// 发起http触发请求
//		String[] postResp = HttpUtil.post(job_url, params);
//		if (logger.isInfoEnabled()) {
//			logger.info(
//					"############ferrari job trigger http response, jobLog.id:{}, response:{}",
//					jobLog.getId(), postResp);
//		}
//
//		// parse trigger response
//		String responseMsg = postResp[0];
//		String exceptionMsg = postResp[1];
//		jobLog.setTriggerTime(new Date());
//		jobLog.setTriggerHost(HostUtil.getIP() + "__" + HostUtil.getHostname());
//		jobLog.setTriggerStatus(HttpUtil.FAIL);
//		jobLog.setTriggerMsg(exceptionMsg);
//		if (StringUtils.isNotBlank(responseMsg)) {
//			FerrariFeedback retVo = null;
//			try {
//				retVo = JacksonUtil.readValue(responseMsg,
//						FerrariFeedback.class);
//			} catch (Exception e) {
//			}
//			if (retVo != null) {
//				jobLog.setTriggerStatus(retVo.isStatus() ? HttpUtil.SUCCESS
//						: HttpUtil.FAIL);
//				jobLog.setTriggerMsg(retVo.getErrormsg());
//			}
//		}
//
//		// update trigger info
//		jobLog.setTriggerMsg(routeMsg.toString() + jobLog.getTriggerMsg());
//		if (jobLog.getTriggerMsg() != null
//				&& jobLog.getTriggerMsg().length() > 1900) {
//			jobLog.setTriggerMsg(jobLog.getTriggerMsg().substring(0, 1880));
//		}
//		ferrariJobLogDao.updateTriggerInfo(jobLog);
//		if (logger.isInfoEnabled()) {
//			logger.info("############ferrari job trigger end, jobLog.id:{}",
//					jobLog.getId());
//		}
//		if (HttpUtil.FAIL.equalsIgnoreCase(jobLog.getTriggerStatus())) {// 发起执行失败，触发报警
//			MonitorManager.getInstance().put2AlarmDeal(
//					new MonitorEntity(jobLog.getId(), false));
//		}
//
//	}
//
//}