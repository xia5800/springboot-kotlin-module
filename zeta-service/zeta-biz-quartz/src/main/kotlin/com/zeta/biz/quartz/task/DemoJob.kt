package com.zeta.biz.quartz.task

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
class DemoJob: QuartzJobBean() {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)


    override fun executeInternal(context: JobExecutionContext) {
        logger.info(">>>> 开始执行test job...")
        printTaskLog(logger, context)
        logger.info(">>>> 执行test job 结束！！！")
    }
}
