package com.zeta.biz.quartz.task

import com.zeta.model.quartz.dto.JobInfoDTO
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.springframework.scheduling.quartz.QuartzJobBean
import org.zetaframework.core.utils.JSONUtil

/**
 * 扩展方法，打印任务日志
 *
 * @param logger slf4j日志对象
 * @param context 任务上下文类
 */
fun QuartzJobBean.printTaskLog(logger: Logger, context: JobExecutionContext): JobInfoDTO {
    val jobKey = context.jobDetail.key
    val jobDescription = context.jobDetail.description
    val jobClazz = context.jobDetail.jobClass.name
    val jobDataMap = context.jobDetail.jobDataMap
    val jobParam = JSONUtil.toJsonStr(jobDataMap)
    val triggerKey = context.trigger.key
    val triggerDescription = context.trigger.description
    logger.info("Job(name=${jobKey.name}, group=${jobKey.group}, description=${jobDescription}, clazz=${jobClazz}, param=${jobParam})")
    logger.info("Trigger(name=${triggerKey.name}, group=${triggerKey.group}, description=${triggerDescription})")

    return JobInfoDTO(
        triggerName = triggerKey.name,
        triggerGroup = triggerKey.group,
        jobName = jobKey.name,
        jobDescription = jobDescription,
        jobClassName = jobClazz,
        jobParam = jobParam,
        jobDataMap = jobDataMap
    )
}
