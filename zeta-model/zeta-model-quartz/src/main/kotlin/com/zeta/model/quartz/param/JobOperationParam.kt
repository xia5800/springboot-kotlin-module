package com.zeta.model.quartz.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

/**
 * 任务操作（暂停、恢复、删除）
 *
 * @author gcc
 */
@ApiModel(description = "任务操作参数")
open class JobOperationParam {

    @ApiModelProperty(value = "任务名称", required = true)
    @get:NotBlank(message = "任务名称不能为空")
    var jobName: String? = null

    @ApiModelProperty(value = "任务组名称", required = false)
    var jobGroupName: String? = null

}
