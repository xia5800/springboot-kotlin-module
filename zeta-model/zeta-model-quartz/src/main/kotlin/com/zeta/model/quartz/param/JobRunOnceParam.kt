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

    @ApiModelProperty(value = "任务参数")
    var dataMap: Map<String, Any>? = null

}
