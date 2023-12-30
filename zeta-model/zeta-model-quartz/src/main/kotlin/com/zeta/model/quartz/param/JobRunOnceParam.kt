package com.zeta.model.quartz.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 运行一次 参数
 *
 * @author gcc
 */
@ApiModel(value = "运行一次参数")
class JobRunOnceParam: JobOperationParam() {

    /** 任务参数 可为空 */
    @ApiModelProperty(value = "任务参数", required = false)
    var jobParam: String? = null

}
