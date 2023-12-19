package com.zeta.model.quartz.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * 触发器
 *
 * @author AutoGenerator
 * @date 2022-09-05 13:05:14
 */
@ApiModel(description = "触发器")
@TableName(value = "QRTZ_TRIGGERS")
class QrtzTriggers: Serializable {

    /** 调度器名称 */
    @ApiModelProperty(value = "调度器名称", required = true)
    @get:NotBlank(message = "调度器名称不能为空")
    @get:Size(max = 120, message = "调度器名称长度不能超过120")
    @TableField(value = "SCHED_NAME")
    var schedName: String? = null

    /** 触发器名称 */
    @ApiModelProperty(value = "触发器名称", required = true)
    @get:NotBlank(message = "触发器名称不能为空")
    @get:Size(max = 190, message = "触发器名称长度不能超过190")
    @TableId(value = "TRIGGER_NAME", type = IdType.INPUT)
    var triggerName: String? = null

    /** 触发器组 */
    @ApiModelProperty(value = "触发器组", required = true)
    @get:NotBlank(message = "触发器组不能为空")
    @get:Size(max = 190, message = "触发器组长度不能超过190")
    @TableField(value = "TRIGGER_GROUP")
    var triggerGroup: String? = null

    /** JOB名称 */
    @ApiModelProperty(value = "JOB名称", required = true)
    @get:NotBlank(message = "JOB名称不能为空")
    @get:Size(max = 190, message = "JOB名称长度不能超过190")
    @TableField(value = "JOB_NAME")
    var jobName: String? = null

    /** JOB所属组 */
    @ApiModelProperty(value = "JOB所属组", required = true)
    @get:NotBlank(message = "JOB所属组不能为空")
    @get:Size(max = 190, message = "JOB所属组长度不能超过190")
    @TableField(value = "JOB_GROUP")
    var jobGroup: String? = null

    /** 描述 */
    @ApiModelProperty(value = "描述", required = false)
    @TableField(value = "DESCRIPTION")
    var description: String? = null

    /** 下次触发时间 */
    @ApiModelProperty(value = "下次触发时间", required = false)
    @TableField(value = "NEXT_FIRE_TIME")
    var nextFireTime: Long? = null

    /** 上次触发时间 */
    @ApiModelProperty(value = "上次触发时间", required = false)
    @TableField(value = "PREV_FIRE_TIME")
    var prevFireTime: Long? = null

    /** 优先级 */
    @ApiModelProperty(value = "优先级", required = false)
    @TableField(value = "PRIORITY")
    var priority: Int? = null

    /** 当前触发器状态 设置为ACQUIRED,如果设置为WAITING,则job不会触发 */
    @ApiModelProperty(value = "当前触发器状态 设置为ACQUIRED,如果设置为WAITING,则job不会触发", required = true)
    @get:NotBlank(message = "当前触发器状态不能为空")
    @get:Size(max = 16, message = "当前触发器状态长度不能超过16")
    @TableField(value = "TRIGGER_STATE")
    var triggerState: String? = null

    /** 触发器类型 CRON表达式 */
    @ApiModelProperty(value = "触发器类型 CRON表达式", required = true)
    @get:NotBlank(message = "触发器类型不能为空")
    @get:Size(max = 8, message = "触发器类型长度不能超过8")
    @TableField(value = "TRIGGER_TYPE")
    var triggerType: String? = null

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间", required = true)
    @get:NotNull(message = "开始时间不能为空")
    @TableField(value = "START_TIME")
    var startTime: Long? = null

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间", required = false)
    @TableField(value = "END_TIME")
    var endTime: Long? = null

    /** 日历名称 */
    @ApiModelProperty(value = "日历名称", required = false)
    @TableField(value = "CALENDAR_NAME")
    var calendarName: String? = null

    /** 策略 */
    @ApiModelProperty(value = "策略", required = false)
    @TableField(value = "MISFIRE_INSTR")
    var misfireInstr: Int? = null


    override fun toString(): String {
        return "Triggers(schedName=$schedName, triggerName=$triggerName, triggerGroup=$triggerGroup, jobName=$jobName, jobGroup=$jobGroup, description=$description, nextFireTime=$nextFireTime, prevFireTime=$prevFireTime, priority=$priority, triggerState=$triggerState, triggerType=$triggerType, startTime=$startTime, endTime=$endTime, calendarName=$calendarName, misfireInstr=$misfireInstr)"
    }

}
