package com.zeta.model.quartz.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

/**
 * 任务调度日志 查询参数
 *
 * @author AutoGenerator
 * @date 2023-12-20 12:44:04
 */
@ApiModel(description = "任务调度日志查询参数")
data class TaskLogQueryParam(

    /** id */
    @ApiModelProperty(value = "id")
    var id: Long? = null,

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间")
    var createTime: LocalDateTime? = null,

    /** 日志类型 */
    @ApiModelProperty(value = "日志类型")
    var type: String? = null,

    /** 触发器名称 */
    @ApiModelProperty(value = "触发器名称")
    var triggerName: String? = null,

    /** 触发器组 */
    @ApiModelProperty(value = "触发器组")
    var triggerGroup: String? = null,

    /** 任务名 */
    @ApiModelProperty(value = "任务名")
    var jobName: String? = null,

    /** 任务描述 */
    @ApiModelProperty(value = "任务描述")
    var jobDescription: String? = null,

    /** 类路径 */
    @ApiModelProperty(value = "类路径")
    var jobClassName: String? = null,

    /** 返回值 */
    @ApiModelProperty(value = "返回值")
    var result: String? = null,

    /** 异常描述 */
    @ApiModelProperty(value = "异常描述")
    var exception: String? = null,

    /** 消耗时间 单位毫秒 */
    @ApiModelProperty(value = "消耗时间 单位毫秒")
    var spendTime: Int? = null,
)
