package com.zeta.biz.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.zeta.biz.system.service.ISysOptLogService
import com.zeta.model.system.entity.SysOptLog
import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.quartz.QuartzJobBean

/**
 * 定时清理系统操作日志
 *
 * 说明：
 * 每天凌晨1点执行一次，清除60天前的操作日志
 *
 * @author gcc
 */
// 保证上一个任务执行完后，再去执行下一个任务，这里的任务是同一个任务
@DisallowConcurrentExecution
class CleanOptLogJob(private val optLogService: ISysOptLogService) : QuartzJobBean() {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        // 提前清理天数
        val BEFORE_DAYS = 60
    }

    /**
     * 执行指定的任务
     *
     * @param context [JobExecutionContext]
     */
    override fun executeInternal(context: JobExecutionContext) {
        logger.info("================ 清理系统操作日志 任务开始执行 ================")
        printTaskLog(logger, context)

        // 筛选出符合条件的操作日志
        // SELECT * FROM sys_opt_log  WHERE create_time <= DATE_SUB(NOW(), INTERVAL 60 DAY);
        val batchRemoveList = optLogService.list(
            QueryWrapper<SysOptLog>()
                .le("create_time", "DATE_SUB(NOW(), INTERVAL $BEFORE_DAYS DAY)")
        )

        // 删除
        if (batchRemoveList.isNotEmpty()) {
            optLogService.removeBatchByIds(batchRemoveList.map { it.id })
        }

        logger.info("================ 清理系统操作日志 任务执行完毕 ================")
    }
}

