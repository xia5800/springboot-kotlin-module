package com.zeta.model.quartz.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.zetaframework.base.entity.SuperEntity
import javax.validation.constraints.NotBlank

/**
 * 任务调度日志
 *
 * @author AutoGenerator
 * @date 2023-12-20 12:44:04
 */
@ApiModel(description = "任务调度日志")
@TableName(value = "task_log")
class TaskLog: SuperEntity<Long>() {

    /** 日志类型 */
    @ApiModelProperty(value = "日志类型", required = true)
    @TableField(value = "type")
    var type: String? = null

    /** 触发器名称 */
    @ApiModelProperty(value = "触发器名称", required = true)
    @get:NotBlank(message = "触发器名称不能为空")
    @TableField(value = "trigger_name")
    var triggerName: String? = null

    /** 触发器组 */
    @ApiModelProperty(value = "触发器组", required = true)
    @get:NotBlank(message = "触发器组不能为空")
    @TableField(value = "trigger_group")
    var triggerGroup: String? = null

    /** 任务名 */
    @ApiModelProperty(value = "任务名", required = true)
    @get:NotBlank(message = "任务名不能为空")
    @TableField(value = "job_name")
    var jobName: String? = null

    /** 任务描述 */
    @ApiModelProperty(value = "任务描述", required = false)
    @TableField(value = "job_description")
    var jobDescription: String? = null

    /** 类路径 */
    @ApiModelProperty(value = "类路径", required = false)
    @TableField(value = "job_class_name")
    var jobClassName: String? = null

    /** 任务参数 */
    @ApiModelProperty(value = "任务参数", required = false)
    @TableField(value = "job_param")
    var jobParam: String? = null

    /** 返回值 */
    @ApiModelProperty(value = "返回值", required = false)
    @TableField(value = "result")
    var result: String? = null

    /** 异常描述 */
    @ApiModelProperty(value = "异常描述", required = false)
    @TableField(value = "exception")
    var exception: String? = null

    /** 消耗时间 单位毫秒 */
    @ApiModelProperty(value = "消耗时间 单位毫秒", required = false)
    @TableField(value = "spend_time")
    var spendTime: Long? = null


    override fun toString(): String {
        return "TaskLog(id=$id, createTime=$createTime, createdBy=$createdBy, type=$type, triggerName=$triggerName, triggerGroup=$triggerGroup, jobName=$jobName, jobDescription=$jobDescription, jobClassName=$jobClassName, jobParam=$jobParam, result=$result, exception=$exception, spendTime=$spendTime)"
    }

}
