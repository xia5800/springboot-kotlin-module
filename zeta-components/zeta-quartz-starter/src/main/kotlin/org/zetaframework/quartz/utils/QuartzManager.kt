package org.zetaframework.quartz.utils

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.RandomUtil
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.zetaframework.base.exception.BusinessException
import org.zetaframework.core.utils.JSONUtil
import org.zetaframework.quartz.builder.CronTriggerBuilder
import org.zetaframework.quartz.builder.JobDetailBuilder
import org.zetaframework.quartz.builder.SimpleTriggerBuilder
import org.zetaframework.quartz.module.QuartzJobDetailDTO
import java.util.*


/**
 * Quartz管理工具类
 *
 * 使用前先搞懂几个概念：
 *   job/jobDetail:
 *      顾名思义，就是任务。
 *      例如：每5秒打印hello world。 打印hello world就是一个任务。 每5秒就是一个任务执行（调度）策略。 触发这个定时任务的叫触发器。
 *   trigger:
 *      顾名思义，就是触发任务的触发器。触发器中包含执行任务用的执行器，可以根据执行器类型来执行定时任务。
 *   schedule:
 *      顾名思义，调度器（执行器）。
 *
 * 常用的几种调度器：
 *   CronSchedule：使用cron表达式的
 *   SimpleSchedule：每隔一段时间执行一次(时、分、秒、毫秒)，可以设置执行总次数
 *   CalendarIntervalSchedule：每隔一段时间执行一次(年、月、日、时、分、秒、毫秒)
 *   DailyTimeIntervalSchedule：指定一周的哪几天、什么时间段、执行间隔。
 *
 * @author gcc
 */
@Component
class QuartzManager(private val scheduler: Scheduler) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 获取调度器
     *
     * @return [Scheduler]
     */
    fun getScheduler(): Scheduler = scheduler

    /**
     * 创建一个job
     *
     * 说明：
     * 一个job对应一个trigger
     * @param jobDetail
     * @param trigger
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createJob(jobDetail: JobDetail, trigger: Trigger) {
        try {
            scheduler.scheduleJob(jobDetail, trigger)
        } catch (e: Exception) {
            logger.error("创建一个job：操作失败", e)
            throw BusinessException("操作失败")
        }
    }

    /**
     * 获取触发器状态
     *
     * 状态值说明：
     *   null: 获取状态失败
     *   NONE: 触发器不存在或被删除
     *   NORMAL: 正常
     *   PAUSED: 暂停
     *   COMPLETE: 完成
     *   ERROR: 错误
     *   BLOCKED: 阻塞
     *
     * @param triggerName   触发器名称
     * @param triggerGroup  触发器组
     * @return string 触发器状态
     */
    fun getTriggerState(triggerName: String, triggerGroup: String = Scheduler.DEFAULT_GROUP): String? {
        return getTriggerState(TriggerKey(triggerName, triggerGroup))
    }

    /**
     * 获取触发器状态
     *
     * 状态值说明：
     *   null: 获取状态失败
     *   NONE: 触发器不存在或被删除
     *   NORMAL: 正常
     *   PAUSED: 暂停
     *   COMPLETE: 完成
     *   ERROR: 错误
     *   BLOCKED: 阻塞
     *
     * @param triggerName   触发器名称
     * @param triggerGroup  触发器组
     * @return string 触发器状态
     */
    fun getTriggerState(triggerKey: TriggerKey): String? {
        return try {
            val triggerState = scheduler.getTriggerState(triggerKey)
            triggerState.name
        }catch (e: Exception) {
            logger.error("获取触发器状态：操作失败", e)
            null
        }
    }

    /**
     * 获取触发器类型
     *
     * 说明：
     * 实际上Trigger类型不是这样判断的。
     * @see org.quartz.impl.jdbcjobstore.Constants
     * @see org.quartz.impl.jdbcjobstore.StdJDBCDelegate#insertTrigger
     *
     * @param trigger 触发器
     */
    fun getTriggerType(trigger: Trigger): String? {
        return when(trigger) {
            is CronTrigger -> "CRON"
            is CalendarIntervalTrigger -> "CAL_INT"
            is SimpleTrigger -> "SIMPLE"
            is DailyTimeIntervalTrigger -> "DAILY_I"
            else -> "BLOB"
        }
    }

    /**
     * 删除一个job
     *
     * 说明：
     * 删除job和与之关联的trigger
     * @param jobName   任务名称
     * @param groupName 任务组
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteJob(jobName: String, groupName: String = Scheduler.DEFAULT_GROUP) {
        deleteJob(JobKey(jobName, groupName))
    }

    /**
     * 删除一个job
     *
     * 说明：
     * 删除job和与之关联的trigger
     * @param jobKey [JobKey]
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteJob(jobKey: JobKey) {
        try {
            // 删除job和与之关联的trigger
            scheduler.deleteJob(jobKey)
        } catch (e: Exception) {
            logger.error("删除一个job：操作失败", e)
            throw BusinessException("操作失败")
        }
    }

    /**
     * 暂停一个job
     *
     * @param jobName   任务名称
     * @param groupName 任务组
     */
    fun pauseJob(jobName: String, groupName: String = Scheduler.DEFAULT_GROUP) {
        try {
            scheduler.pauseJob(JobKey(jobName, groupName))
        } catch (e: Exception) {
            logger.error("暂停一个job：操作失败", e)
            throw BusinessException("操作失败")
        }
    }

    /**
     * 恢复一个job
     *
     * @param jobName   任务名称
     * @param groupName 任务组
     */
    fun resumeJob(jobName: String, groupName: String = Scheduler.DEFAULT_GROUP) {
        try {
            // 如果该job错过的触发时间，会根据job关联的trigger配置的misfire指令来判断是否执行之前错过的任务
            // 可以看看org.quartz.Trigger接口的MISFIRE_INSTRUCTION_SMART_POLICY、MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY注释说明
            scheduler.resumeJob(JobKey(jobName, groupName))
        } catch (e: Exception) {
            logger.error("恢复一个job：操作失败", e)
            throw BusinessException("操作失败")
        }
    }

    /**
     * 立即运行一次
     *
     * 说明：
     * 只会运行一次，方便测试时用
     * quartz是通过临时生成一个trigger的方式来实现的，这个trigger将在本次任务运行完成之后自动删除
     * @param jobName   任务名称
     * @param groupName 任务组
     */
    fun runAJobNow(jobName: String, groupName: String = Scheduler.DEFAULT_GROUP) {
        runAJobNow(jobName, groupName, null)
    }


    /**
     * 立即运行一次
     *
     * 说明：
     * 只会运行一次，方便测试时用
     * quartz是通过临时生成一个trigger的方式来实现的，这个trigger将在本次任务运行完成之后自动删除
     * @param jobName    任务名称
     * @param groupName  任务组
     * @param jobDataMap 任务参数
     */
    fun runAJobNow(jobName: String, groupName: String = Scheduler.DEFAULT_GROUP, jobDataMap: JobDataMap?) {
        try {
            scheduler.triggerJob(JobKey(jobName, groupName), jobDataMap)
        } catch (e: Exception) {
            logger.error("立即运行一次：操作失败", e)
            throw BusinessException("操作失败")
        }
    }

    /**
     * 获取cron下次触发时间
     *
     * @param cron 表达式
     * @param count 触发次数
     * @return List<String>
     */
    fun nextTriggerTime(cron: String, count: Int): MutableList<String> {
        val result = mutableListOf<String>()
        // 校验cron表达式
        if (!CronExpression.isValidExpression(cron)) return result

        // 构造cron触发器
        val cronTrigger = CronTriggerBuilder(
            name = RandomUtil.randomString(10),
            cron = cron,
            priority = 1,
        ).build()

        var currentTime = Date()
        for (i in 0 until count) {
            // 获取下次触发时间
            val nextFireTime = cronTrigger.getFireTimeAfter(currentTime) ?: break
            result.add(DateUtil.formatDateTime(nextFireTime))

            // 更新当前时间为下次触发时间
            currentTime = nextFireTime
        }

        return result
    }

}
