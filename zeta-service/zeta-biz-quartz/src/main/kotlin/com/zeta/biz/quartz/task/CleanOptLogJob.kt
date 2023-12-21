package com.zeta.biz.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.zeta.biz.quartz.enums.JobLogTypeEnum
import com.zeta.biz.quartz.service.ITaskLogService
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
 * @author gcc
 */
// 保证上一个任务执行完后，再去执行下一个任务，这里的任务是同一个任务
@DisallowConcurrentExecution
class CleanOptLogJob(
    private val optLogService: ISysOptLogService,
    private val taskLogService: ITaskLogService
) : QuartzJobBean() {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        // 默认提前清理天数
        const val DEFAULT_BEFORE_DAY = 60
    }

    /**
     * 执行指定的任务
     *
     * 说明：
     * 创建任务的时候，可以传入任务参数: `{"beforeDay": 10}`
     *
     * @param context [JobExecutionContext]
     */
    override fun executeInternal(context: JobExecutionContext) {
        logger.info("================ 清理系统操作日志 任务开始执行 ================")
        val startTime = System.currentTimeMillis()
        val jobInfoDTO = printTaskLog(logger, context)

        // 获取任务参数。 从前端传入的参数中获取，取不到就使用默认值
        val beforeDay = jobInfoDTO.jobDataMap?.getInt("beforeDay") ?: DEFAULT_BEFORE_DAY

        // 筛选出符合条件的操作日志
        // SELECT id FROM sys_opt_log  WHERE create_time <= DATE_SUB(NOW(), INTERVAL 60 DAY);
        val batchRemoveList = optLogService.list(
            QueryWrapper<SysOptLog>()
                .select("id")
                .apply("create_time <= DATE_SUB(NOW(), INTERVAL $beforeDay DAY)")
        )

        // 删除
        if (batchRemoveList.isNotEmpty()) {
            optLogService.removeBatchByIds(batchRemoveList.map { it.id })
        }

        // 记录日志
        val spendTime = System.currentTimeMillis() - startTime
        taskLogService.asyncSave(jobInfoDTO, spendTime, JobLogTypeEnum.SUCCESS.name, JobLogTypeEnum.SUCCESS.desc)

        logger.info("================ 清理系统操作日志 任务执行完毕 ================")
    }
}

