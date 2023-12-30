package com.zeta.model.quartz.dto

import com.zeta.model.quartz.enums.DailyTimeScheduleTypeEnum
import com.zeta.model.quartz.enums.ScheduleTypeEnum
import com.zeta.model.quartz.enums.SimpleScheduleUnitEnum
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.quartz.DateBuilder
import org.zetaframework.quartz.builder.DailyTimeIntervalTriggerBuilder
import java.time.LocalTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * 新增或修改 任务
 *
 * @author gcc
 */
@ApiModel(description = "新增或修改任务")
data class JobSaveOrUpdateDTO (

    /** 任务名称 */
    @ApiModelProperty(value = "任务名称", required = true)
    @get:NotBlank(message = "任务名称不能为空")
    var jobName: String? = null,

    /** 任务描述 */
    @ApiModelProperty(value = "任务描述", required = false)
    var jobDescription: String? = null,

    /** 任务执行类 */
    @ApiModelProperty(value = "任务执行类", required = true)
    @get:NotBlank(message = "任务执行类不能为空")
    var jobClassName: String? = null,

    /** 任务参数 */
    @ApiModelProperty(value = "任务参数", required = false)
    var jobParam: String? = null,

    /** 触发器优先级 当多个任务具有相同的触发时间时，调度程序将首先触发具有最高优先级的触发 */
    @ApiModelProperty(value = "触发器优先级 当多个任务具有相同的触发时间时，调度程序将首先触发具有最高优先级的触发", required = false)
    var priority: Int? = null,

    /** 调度类型 */
    @ApiModelProperty(value = "调度类型", required = true)
    @get:NotNull(message = "调度类型不能为空")
    var scheduleType: ScheduleTypeEnum? = null,

    /** 调度类型为Cron时的参数 */
    @ApiModelProperty(value = "调度类型为Cron时的参数", required = false)
    var cron: String? = null,

    /** 调度类型为CAL_INT时的参数 */
    @ApiModelProperty(value = "调度类型为CAL_INT时的参数", required = false)
    var calendar: Calendar? = null,

    /** 调度类型为DAILY_I时的参数 */
    @ApiModelProperty(value = "调度类型为DAILY_I时的参数", required = false)
    var dailyTime: DailyTime? = null,

    /** 调度类型为SIMPLE时的参数 */
    @ApiModelProperty(value = "调度类型为SIMPLE时的参数", required = false)
    var simple: Simple? = null
)

/**
 * Calendar类型调度器参数
 *
 * @author gcc
 */
@ApiModel(description = "Calendar类型调度器参数")
data class Calendar (
    /** 触发器应该重复的时间间隔 不能为空 */
    @ApiModelProperty(value = "间隔时间 不能为空", required = false)
    @get:Min(value = 1, message = "间隔时间不能小于1")
    var timeInterval: Int? = null,

    /** 间隔时间的单位 不能为空 */
    @ApiModelProperty(value = "间隔时间的单位 不能为空", required = false)
    var unit: DateBuilder.IntervalUnit? = null,
)

/**
 * DailyTime类型调度器参数
 *
 * @author gcc
 */
@ApiModel(description = "DailyTime类型调度器参数")
data class DailyTime (
    /** 执行类型 不能为空 */
    @ApiModelProperty(value = "执行类型 不能为空", required = false)
    var type: DailyTimeScheduleTypeEnum? = null,

    /** 指定的执行时间 当type为DaysOfTheWeek时，不能为空 */
    @ApiModelProperty(value = "指定的执行时间 当type为DaysOfTheWeek时，不能为空", required = false)
    var daysOfWeek: List<DailyTimeIntervalTriggerBuilder.DaysOfWeek>? = null,

    /** 间隔时间 可为空 */
    @ApiModelProperty(value = "间隔时间 可为空", required = false)
    @get:Min(value = 1, message = "间隔时间不能小于1")
    var timeInterval: Int? = null,

    /** 间隔单位 可为空 */
    @ApiModelProperty(value = "间隔单位 可为空", required = false)
    var unit: DailyTimeIntervalTriggerBuilder.IntervalUnit? = null,

    /** 开始时间 可为空 */
    @ApiModelProperty(value = "开始时间 可为空")
    var startTime: LocalTime? = null,

    /** 结束时间 可为空 */
    @ApiModelProperty(value = "结束时间 可为空")
    var endTime: LocalTime? = null,
)

/**
 * Simple类型调度器参数
 *
 * @author gcc
 */
@ApiModel(description = "Simple类型调度器参数")
data class Simple (
    /** 间隔单位 不能为空 */
    @ApiModelProperty(value = "间隔单位 不能为空", required = false)
    var unit: SimpleScheduleUnitEnum? = null,

    /** 重复间隔时间 不能为空 */
    @ApiModelProperty(value = "重复间隔时间 不能为空", required = false)
    @get:Min(value = 1, message = "间隔时间不能小于1")
    var timeInterval: Long? = null,

    /** 重复次数 可为空 */
    @ApiModelProperty(value = "重复次数 可为空", required = false)
    var repeatCount: Int? = null,

    /** 是否永远重复执行下去 可为空 */
    @ApiModelProperty(value = "是否永远重复执行下去 可为空", required = false)
    var repeatForever: Boolean? = null
)