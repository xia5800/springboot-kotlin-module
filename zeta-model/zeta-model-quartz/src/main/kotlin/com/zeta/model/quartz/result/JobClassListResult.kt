package com.zeta.model.quartz.result

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 任务执行类返回值 返回值
 *
 * @author gcc
 */
@ApiModel(description = "任务执行类返回值")
data class JobClassListResult(

    /** 任务执行类路径 */
    @ApiModelProperty(value = "任务执行类路径", required = true)
    var value: String? = null,

    /** 任务执行类描述 */
    @ApiModelProperty(value = "任务执行类描述", required = true)
    var description: String? = null
)
