package com.zeta.model.quartz.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

/**
 * 修改 任务触发器
 *
 * @author gcc
 */
@ApiModel(description = "修改任务触发器")
data class JobTriggerUpdateDTO(

    /** 旧触发器名称 */
    @ApiModelProperty(value = "旧触发器名称", required = true)
    @get:NotBlank(message = "旧触发器名称不能为空")
    var oldName: String? = null,

    /** 触发器名称 */
    @ApiModelProperty(value = "新触发器名称", required = true)
    @get:NotBlank(message = "新触发器名称不能为空")
    var triggerName: String? = null,

    /** cron表达式 */
    @ApiModelProperty(value = "cron表达式", required = true)
    @get:NotBlank(message = "cron表达式不能为空")
    var cron: String? = null,


    /** 旧触发器组 */
    @ApiModelProperty(value = "旧触发器组", required = false)
    var oldGroup: String? = null,

    /** 触发器组 */
    @ApiModelProperty(value = "新触发器组", required = false)
    var triggerGroup: String? = null,

    /** 触发器描述 */
    @ApiModelProperty(value = "新触发器描述", required = false)
    var triggerDescription: String? = null,

    /** 触发器优先级 */
    @ApiModelProperty(value = "新触发器优先级", required = false)
    var priority: Int? = null,

)
