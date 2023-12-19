package org.zetaframework.base.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * id参数
 *
 * 说明：
 * 适用于接口参数只有一个id的情况（用get不方便，只能用post、put啥的）
 *
 * @author gcc
 */
@ApiModel(description = "接口参数")
class IdParam<T> private constructor() {

    /** 主键 */
    @ApiModelProperty(value = "主键", required = true)
    var id: T? = null

    /**
     * id参数 构造方法
     */
    constructor(id: T): this() {
        this.id = id
    }
}
