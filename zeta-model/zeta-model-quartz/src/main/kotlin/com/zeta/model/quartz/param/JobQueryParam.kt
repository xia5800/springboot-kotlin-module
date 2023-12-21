package com.zeta.model.quartz.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 触发器 查询参数
 *
 * @author AutoGenerator
 * @date 2022-09-05 13:05:14
 */
@ApiModel(description = "任务查询参数")
data class JobQueryParam(

    /** 触发器名称 */
    @ApiModelProperty(value = "触发器名称")
    var triggerName: String? = null,

    /** 触发器组 */
    @ApiModelProperty(value = "触发器组")
    var triggerGroup: String? = null,

    /** 触发器描述 */
    @ApiModelProperty(value = "新触发器描述")
    var triggerDescription: String? = null,

    /** JOB名称 */
    @ApiModelProperty(value = "JOB名称")
    var jobName: String? = null,

    /** 任务组 */
    @ApiModelProperty(value = "任务组")
    var jobGroup: String? = null,

    /** 任务描述 */
    @ApiModelProperty(value = "任务描述")
    var jobDescription: String? = null,

    /** 当前触发器状态 */
    @ApiModelProperty(value = "当前触发器状态")
    var triggerState: String? = null,

    /** 任务执行类 */
    @ApiModelProperty(value = "任务执行类")
    var jobClassName: String? = null,

)
