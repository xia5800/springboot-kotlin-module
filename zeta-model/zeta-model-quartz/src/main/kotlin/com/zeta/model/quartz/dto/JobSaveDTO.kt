package com.zeta.model.quartz.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

/**
 * 新增 任务
 *
 * @author gcc
 */
@ApiModel(description = "新增任务")
data class JobSaveDTO(

    /** 触发器名称 */
    @ApiModelProperty(value = "触发器名称", required = true)
    @get:NotBlank(message = "触发器名称不能为空")
    var triggerName: String? = null,

    /** 触发器组 */
    @ApiModelProperty(value = "触发器组", required = false)
    var triggerGroup: String? = null,

    /** 触发器描述 */
    @ApiModelProperty(value = "触发器描述", required = false)
    var triggerDescription: String? = null,

    /** 触发器优先级 */
    @ApiModelProperty(value = "触发器优先级", required = false)
    var priority: Int? = null,

    /** 任务名称 */
    @ApiModelProperty(value = "任务名称", required = true)
    @get:NotBlank(message = "任务名称不能为空")
    var jobName: String? = null,

    /** 任务执行类 */
    @ApiModelProperty(value = "任务执行类", required = true)
    @get:NotBlank(message = "任务执行类不能为空")
    var jobClassName: String? = null,

    /** cron表达式 */
    @ApiModelProperty(value = "cron表达式", required = true)
    @get:NotBlank(message = "cron表达式不能为空")
    var cron: String? = null,

    /** 任务组 */
    @ApiModelProperty(value = "任务组", required = false)
    var jobGroup: String? = null,

    /** 任务描述 */
    @ApiModelProperty(value = "任务描述", required = false)
    var jobDescription: String? = null,

    /** 任务参数 */
    @ApiModelProperty(value = "任务参数", required = false)
    var jobParam: String? = null

)
