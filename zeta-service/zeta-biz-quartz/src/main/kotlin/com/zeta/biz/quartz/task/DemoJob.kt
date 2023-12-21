package com.zeta.biz.quartz.task

import cn.hutool.core.util.RandomUtil
import com.zeta.biz.quartz.enums.JobLogTypeEnum
import com.zeta.biz.quartz.service.ITaskLogService
import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.quartz.QuartzJobBean

/**
 * 定时任务 demo
 * @author gcc
 */
// 保证上一个任务执行完后，再去执行下一个任务，这里的任务是同一个任务
@DisallowConcurrentExecution
class DemoJob(private val taskLogService: ITaskLogService): QuartzJobBean() {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 执行指定的任务
     *
     * @param context [JobExecutionContext]
     */
    override fun executeInternal(context: JobExecutionContext) {
        logger.info(">>>> 开始执行test job...")
        val startTime = System.currentTimeMillis()
        val jobInfo = printTaskLog(logger, context)

        // 随机抛出异常
        var exception: Throwable? = null
        if (RandomUtil.randomBoolean()) {
            exception = RuntimeException("随机抛出异常")
        }

        // 记录日志
        val spendTime = System.currentTimeMillis() - startTime
        taskLogService.asyncSave(
            jobInfo,
            spendTime,
            if (exception != null) JobLogTypeEnum.EXCEPTION.name else JobLogTypeEnum.SUCCESS.name,
            if (exception != null) null else JobLogTypeEnum.SUCCESS.desc,
            exception
        )
        logger.info(">>>> 执行test job 结束！！！")
    }
}
