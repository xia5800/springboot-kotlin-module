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

    /** JOB名称 */
    @ApiModelProperty(value = "JOB名称")
    var jobName: String? = null,

    /** 触发器描述 */
    @ApiModelProperty(value = "描述")
    var description: String? = null,

    /** 当前触发器状态 */
    @ApiModelProperty(value = "当前触发器状态")
    var triggerState: String? = null,

    /** 任务执行类 */
    @ApiModelProperty(value = "任务执行类")
    var jobClassName: String? = null,

)
