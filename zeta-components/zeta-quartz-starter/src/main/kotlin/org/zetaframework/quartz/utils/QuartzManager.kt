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
     * 创建一个CronJob
     *
     * 说明：
     * 一个job对应一个trigger
     * @param jobDetail: JobDetailBuilder
     * @param cronTrigger: CronTriggerBuilder
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createCronJob(jobDetail: JobDetailBuilder, cronTrigger: CronTriggerBuilder) {
        createJob(jobDetail.build(), cronTrigger.build())
    }

    /**
     * 创建一个只执行一次的job
     *
     * 说明：
     * 创建1s之后开始执行，只执行一次
     * @param jobDetail: JobDetailBuilder
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createOnceJob(jobDetail: JobDetailBuilder, timeInterval: Long? = 1L) {
        val trigger = SimpleTriggerBuilder(
            jobDetail.name,
            timeInterval = timeInterval ?: 1L,
            description = jobDetail.description
        ).withIntervalInSeconds()
        createJob(jobDetail.build(), trigger)
    }

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
     * 修改一个job
     *
     * @param triggerName       原触发器名称
     * @param jobDetail         新任务
     * @param triggerGroup      原触发器组
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateJob(triggerName: String, jobDetail: JobDetailBuilder, triggerGroup: String = Scheduler.DEFAULT_GROUP) {
        updateJob(triggerName, jobDetail.build(), triggerGroup)
    }

    /**
     * 修改一个job
     *
     * @param triggerName       原触发器名称
     * @param jobDetail         新任务
     * @param triggerGroup      原触发器组
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateJob(triggerName: String, jobDetail: JobDetail, triggerGroup: String = Scheduler.DEFAULT_GROUP) {
        try {
            // 获取触发器
            val trigger = scheduler.getTrigger(TriggerKey(triggerName, triggerGroup))

            // 删除旧的job
            deleteJob(trigger.jobKey)

            // 添加一个新的job
            createJob(jobDetail, trigger)
        } catch (e: Exception) {
            logger.error("修改一个job：操作失败", e)
            throw BusinessException("操作失败")
        }
    }

    /**
     * 修改为Cron触发器
     *
     * @param triggerName       原触发器名称
     * @param trigger           新触发器
     * @param triggerGroup      原触发器组
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateCronTrigger(triggerName: String, trigger: CronTriggerBuilder, triggerGroup: String = Scheduler.DEFAULT_GROUP) {
        updateTrigger(triggerName, trigger.build(), triggerGroup)
    }

    /**
     * 修改触发器
     *
     * @param triggerName       原触发器名称
     * @param trigger           新触发器
     * @param triggerGroup      原触发器组
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateTrigger(triggerName: String, trigger: Trigger, triggerGroup: String = Scheduler.DEFAULT_GROUP) {
        updateTrigger(TriggerKey(triggerName, triggerGroup), trigger)
    }

    /**
     * 修改触发器
     *
     * @param triggerKey 触发器key
     * @param trigger    新触发器
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateTrigger(triggerKey: TriggerKey, trigger: Trigger) {
        try {
            scheduler.rescheduleJob(triggerKey, trigger)
        } catch (e: Exception) {
            logger.error("修改触发器：操作失败", e)
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
     * 获取所有计划中的任务列表
     *
     * 说明：
     * 1.适用于前端不分页。或者前端分页、前端排序。
     * 2.如果需要后端分页查询，建议自行编写SQL实现
     *
     * @return List<QuartzJobDTO>
     */
    fun queryAllJob(): MutableList<QuartzJobDetailDTO> {
        val result = mutableListOf<QuartzJobDetailDTO>()

        // 获取所有的触发器key
        val triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup())
        for (triggerKey in triggerKeys) {
            // 根据触发器key查询触发器详情 ps: 查的是QRTZ_TRIGGER表
            val trigger = scheduler.getTrigger(triggerKey) ?: continue
            // 根据任务key查询任务详情 ps: 查的是QRTZ_JOB_DETAILS表
            val jobKey = trigger.jobKey ?: continue
            var jobDetail: JobDetail
            try {
                jobDetail = scheduler.getJobDetail(jobKey)
            }catch (e: Exception) {
                logger.warn("获取任务详情失败", e)
                continue
            }

            // 构造返回结果
            result.add(generatorModule(trigger, jobDetail))
        }

        return result
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

/**
 * 扩展方法：构造Quartz任务详情
 *
 * @param trigger   触发器
 * @param jobDetail 任务
 */
private fun QuartzManager.generatorModule(trigger: Trigger, jobDetail: JobDetail): QuartzJobDetailDTO {
    val triggerKey = trigger.key
    val jobKey = trigger.jobKey

    return QuartzJobDetailDTO().apply {
        // trigger表的数据
        triggerName = triggerKey.name
        triggerGroup = triggerKey.group
        triggerDescription = trigger.description
        nextFireTime = trigger.nextFireTime
        prevFireTime = trigger.previousFireTime
        priority = trigger.priority
        startTime = trigger.startTime
        endTime = trigger.endTime
        triggerState = getTriggerState(triggerKey)
        triggerType = getTriggerType(trigger)


        // job_details表的数据
        jobName = jobKey.name
        jobGroup = jobKey.group
        jobDescription = jobDetail.description
        try {
            jobClassName = jobDetail.jobClass?.name
        }catch (e: Exception) {
            e.printStackTrace()
        }
        jobParam = JSONUtil.toJsonStr(jobDetail.jobDataMap)
        if (triggerType === "CRON") {
            try {
                // 如果触发器是cron类型的触发器，则获取cron表达式的值
                val cronTrigger = trigger as CronTrigger
                cron = cronTrigger.cronExpression
            } catch (e: Exception) { }
        }
    }
}
