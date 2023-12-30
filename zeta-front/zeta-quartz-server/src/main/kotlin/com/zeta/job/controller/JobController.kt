package com.zeta.job.controller

import cn.hutool.core.util.EnumUtil
import com.fasterxml.jackson.core.type.TypeReference
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport
import com.github.xiaoymin.knife4j.annotations.ApiSupport
import com.zeta.biz.quartz.service.IQrtzTriggersService
import com.zeta.model.quartz.dto.JobSaveOrUpdateDTO
import com.zeta.model.quartz.entity.QrtzTriggers
import com.zeta.model.quartz.enums.DailyTimeScheduleTypeEnum
import com.zeta.model.quartz.enums.ScheduleTypeEnum
import com.zeta.model.quartz.enums.SimpleScheduleUnitEnum
import com.zeta.model.quartz.param.JobOperationParam
import com.zeta.model.quartz.param.JobQueryParam
import com.zeta.model.quartz.param.JobRunOnceParam
import com.zeta.model.quartz.result.JobClassListResult
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zetaframework.annotation.log.SysLog
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.annotation.satoken.PreCheckPermission
import org.zetaframework.annotation.satoken.PreMode
import org.zetaframework.annotation.xss.NoXss
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.ApiResult
import org.zetaframework.base.result.PageResult
import org.zetaframework.controller.SuperBaseController
import org.zetaframework.core.enums.QuartzJobEnum
import org.zetaframework.core.utils.JSONUtil
import org.zetaframework.quartz.builder.*
import org.zetaframework.quartz.module.QuartzJobDetailDTO
import org.zetaframework.quartz.utils.QuartzManager

/**
 * 定时任务 前端控制器
 *
 * @author gcc
 */
@ApiSupport(order = 1)
@Api(tags = ["定时任务"])
@PreAuth(replace = "task")
@RestController
@RequestMapping("/api/job")
class JobController(
    private val quartzManager: QuartzManager,
    private val triggersService: IQrtzTriggersService
): SuperBaseController {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 分页查询定时任务
     *
     * @param param PageParam<JobQueryParam> 任务查询参数
     */
    @PreCheckPermission(value = ["{}:view"])
    @ApiOperationSupport(order = 10)
    @SysLog(response = false)
    @ApiOperation(value = "分页查询", notes = """
    触发器状态值说明：
    null: 获取状态失败
    NONE: 触发器不存在或被删除
    NORMAL: 正常
    PAUSED: 暂停
    COMPLETE: 完成
    ERROR: 错误
    BLOCKED: 阻塞
    """)
    @PostMapping("/page")
    fun page(@RequestBody @Validated param: PageParam<JobQueryParam>): ApiResult<PageResult<QuartzJobDetailDTO>> {
        if (param.sort?.equals("id") == true) {
            param.sort = QrtzTriggers::triggerName.name
        }
        return success(triggersService.customPage(param))
    }

    /**
     * 任务执行类列表
     *
     * @return ApiResult<MutableList<Any>>
     */
    @PreCheckPermission(value = ["{}:view"])
    @ApiOperationSupport(order = 21)
    @ApiOperation(value = "任务执行类列表")
    @GetMapping( "/jobClassList")
    fun jobClassNameList(): ApiResult<MutableList<JobClassListResult>> {
        val result = mutableListOf<JobClassListResult>()

        // 获取枚举字符串值和枚举对象的Map对应，使用LinkedHashMap保证有序
        val enumMap = EnumUtil.getEnumMap(QuartzJobEnum::class.java)
        enumMap.forEach { (_, enum) ->
            // 构造返回值
            if (enum.isShow) {
                result.add(JobClassListResult(value = enum.clazzPath, description = enum.description))
            }
        }
        return success(result)
    }


    /**
     * 新增
     *
     * @param param SaveDTO 保存对象
     * @return ApiResult<Boolean>
     */
    @NoXss // 加这个注解主要是因为，param.jobParam的值是一个json字符串，会被xss过滤掉，导致json字符串转换成map失败
    @PreCheckPermission(value = ["{}:add", "{}:save"], mode = PreMode.OR)
    @ApiOperationSupport(order = 40)
    @ApiOperation(value = "新增", notes = "新增并立即执行")
    @SysLog
    @PostMapping
    fun save(@RequestBody @Validated param: JobSaveOrUpdateDTO): ApiResult<Boolean> {
        val jobClazz: Class<Job>
        try {
            jobClazz = Class.forName(param.jobClassName) as Class<Job>
        }catch (e: Exception) {
            return fail("任务执行类错误", false)
        }

        // 设置默认值
        val priority: Int = if (param.priority == null) Trigger.DEFAULT_PRIORITY else param.priority!!

        // 处理任务参数 string -> Map<string, object>  ps: 转换失败返回null
        val jobParam : MutableMap<String, Any?>? = JSONUtil.parseObject(param.jobParam, object: TypeReference<MutableMap<String, Any?>>(){})

        // 构造任务
        val jobDetail = JobDetailBuilder(
            name = param.jobName!!,
            jobClazz = jobClazz,
            description = param.jobDescription,
            param = jobParam
        ).build()

        // 构造触发器
        val cronTrigger = when(param.scheduleType!!) {
            ScheduleTypeEnum.CRON -> buildCronTrigger(param, priority)
            ScheduleTypeEnum.CAL_INT -> buildCalendarTrigger(param, priority)
            ScheduleTypeEnum.DAILY_I -> buildDailyTimeTrigger(param, priority)
            ScheduleTypeEnum.SIMPLE -> buildSimpleTrigger(param, priority)
        }

        // 创建一个CronJob
        quartzManager.createJob(jobDetail, cronTrigger)
        return success(true)
    }

    /**
     * 修改
     *
     * @param updateDTO UpdateDTO 修改对象
     * @return ApiResult<Boolean>
     */
    @NoXss // 加这个注解主要是因为，param.jobParam的值是一个json字符串，会被xss过滤掉，导致json字符串转换成map失败
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 50)
    @ApiOperation(value = "修改")
    @SysLog
    @PutMapping
    fun update(@RequestBody @Validated param: JobSaveOrUpdateDTO): ApiResult<Boolean> {
        // 删除任务
        delete(JobOperationParam().apply { jobName = param.jobName })

        // 创建任务
        return save(param)
    }

    /**
     * 单体删除
     *
     * @param deleteParam 任务操作参数（暂停、恢复、删除）
     * @return R<Boolean>
     */
    @PreCheckPermission(value = ["{}:delete", "{}:remove"], mode = PreMode.OR)
    @ApiOperationSupport(order = 60)
    @ApiOperation(value = "单体删除")
    @SysLog
    @DeleteMapping
    fun delete(@RequestBody @Validated deleteParam: JobOperationParam): ApiResult<Boolean> {
        quartzManager.deleteJob(deleteParam.jobName!!)
        return success(true)
    }

    /**
     * 暂停任务
     *
     * @param pauseParam 任务操作参数（暂停、恢复、删除）
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 80)
    @ApiOperation(value = "暂停任务")
    @SysLog
    @PostMapping("/pause")
    fun pause(@RequestBody @Validated pauseParam: JobOperationParam): ApiResult<Boolean> {
        quartzManager.pauseJob(pauseParam.jobName!!)
        return success(true)
    }

    /**
     * 恢复任务
     *
     * @param resumeParam 任务操作参数（暂停、恢复、删除）
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 90)
    @ApiOperation(value = "恢复任务")
    @SysLog
    @PostMapping("/resume")
    fun resume(@RequestBody @Validated resumeParam: JobOperationParam): ApiResult<Boolean> {
        quartzManager.resumeJob(resumeParam.jobName!!)
        return success(true)
    }

    /**
     * 立即运行一次
     *
     * @param runParam 运行一次参数
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 100)
    @ApiOperation(value = "立即运行一次", notes = "只会运行一次，方便测试时用")
    @SysLog
    @PostMapping("/runOnce")
    fun runOnce(@RequestBody @Validated runParam: JobRunOnceParam): ApiResult<Boolean> {
        // 处理任务参数 string -> Map<string, object>  ps: 转换失败返回null
        val jobParam : MutableMap<String, Any?>? = JSONUtil.parseObject(runParam.jobParam, object: TypeReference<MutableMap<String, Any?>>(){})
        quartzManager.runAJobNow(runParam.jobName!!, jobDataMap = JobDataMap(jobParam))
        return success(true)
    }

    /**
     * 获取下次触发时间
     *
     * @param cron 表达式
     * @param count 触发次数 可不传。默认为6次
     */
    @ApiOperationSupport(order = 997)
    @ApiOperation(value = "获取下次触发时间")
    @GetMapping("/nextTriggerTime")
    fun nextTriggerTime(
        @ApiParam("cron") @RequestParam("cron")
        cron: String,
        @ApiParam("触发次数") @RequestParam(name = "count", defaultValue = "6", required = false)
        count: Int,
    ): ApiResult<List<String>> {
        return success(quartzManager.nextTriggerTime(cron, count))
    }

    /**
     * 构造Cron类型触发器
     *
     * @param param 保存对象
     * @param priority 优先级
     * @return Trigger
     */
    private fun buildCronTrigger(param: JobSaveOrUpdateDTO, priority: Int): Trigger {
        val cron = param.cron ?: throw IllegalArgumentException("cron表达式不能为空")
        return CronTriggerBuilder(
            name = param.jobName!!,
            cron = cron,
            priority = priority,
            description = param.jobDescription
        ).build()
    }
    /**
     * 构造Calendar类型触发器
     *
     * @param param 保存对象
     * @param priority 优先级
     * @return Trigger
     */
    private fun buildCalendarTrigger(param: JobSaveOrUpdateDTO, priority: Int): Trigger {
        val calendar = param.calendar ?: throw IllegalArgumentException("Calendar类型调度器参数不能为空")
        val timeInterval = calendar.timeInterval ?: throw IllegalArgumentException("间隔时间不能为空")
        val uint = calendar.unit ?: throw IllegalArgumentException("间隔单位不能为空")

        return CalendarIntervalTriggerBuilder(
            name = param.jobName!!,
            timeInterval = timeInterval,
            unit = uint,
            priority = priority,
            description = param.jobDescription
        ).build()
    }
    /**
     * 构造DailyTime类型触发器
     *
     * @param param 保存对象
     * @param priority 优先级
     * @return Trigger
     */
    private fun buildDailyTimeTrigger(param: JobSaveOrUpdateDTO, priority: Int): Trigger {
        val dailyTime = param.dailyTime ?: throw IllegalArgumentException("DailyTime类型调度器参数不能为空")
        val dailyType = dailyTime.type ?: throw IllegalArgumentException("执行类型不能为空")

        val dailyTimeTrigger = DailyTimeIntervalTriggerBuilder(param.jobName!!).apply {
            this.priority = priority
            this.description = param.jobDescription
            this.timeInterval = dailyTime.timeInterval
            this.unit = dailyTime.unit
            dailyTime.startTime?.let { startTime ->
                this.startingDailyAt = TimeOfDay(
                    startTime.hour,
                    startTime.minute,
                    startTime.second
                )
            }
            dailyTime.endTime?.let { endTime ->
                this.endingDailyAt = TimeOfDay(
                    endTime.hour,
                    endTime.minute,
                    endTime.second
                )
            }
        }
        return when(dailyType) {
            DailyTimeScheduleTypeEnum.EveryDay -> {
                dailyTimeTrigger.onEveryDay()
            }
            DailyTimeScheduleTypeEnum.SaturdayAndSunday -> {
                dailyTimeTrigger.onSaturdayAndSunday()
            }
            DailyTimeScheduleTypeEnum.MondayThroughFriday -> {
                dailyTimeTrigger.onMondayThroughFriday()
            }
            DailyTimeScheduleTypeEnum.DaysOfTheWeek -> {
                val daysOfWeek = dailyTime.daysOfWeek ?: throw IllegalArgumentException("daysOfWeek不能为空")
                dailyTimeTrigger.onDaysOfTheWeek(daysOfWeek)
            }
        }
    }
    /**
     * 构造Simple类型触发器
     *
     * @param param 保存对象
     * @param priority 优先级
     * @return Trigger
     */
    private fun buildSimpleTrigger(param: JobSaveOrUpdateDTO, priority: Int): Trigger {
        val simple = param.simple ?: throw IllegalArgumentException("Simple类型调度器参数不能为空")
        val timeInterval = simple.timeInterval?: throw IllegalArgumentException("间隔时间不能为空")
        val unit = simple.unit ?: throw IllegalArgumentException("间隔单位不能为空")

        val simpleTrigger = SimpleTriggerBuilder(param.jobName!!, timeInterval).apply {
            this.priority = priority
            this.description = param.jobDescription
            this.repeatCount = simple.repeatCount
            this.repeatForever = simple.repeatForever
        }
        return when(unit) {
            SimpleScheduleUnitEnum.HOURS -> {
                simpleTrigger.withIntervalInHours()
            }
            SimpleScheduleUnitEnum.MINUTES -> {
                simpleTrigger.withIntervalInMinutes()
            }
            SimpleScheduleUnitEnum.SECONDS -> {
                simpleTrigger.withIntervalInSeconds()
            }
            SimpleScheduleUnitEnum.MILLISECONDS -> {
                simpleTrigger.withIntervalInMilliseconds()
            }
        }
    }

}
