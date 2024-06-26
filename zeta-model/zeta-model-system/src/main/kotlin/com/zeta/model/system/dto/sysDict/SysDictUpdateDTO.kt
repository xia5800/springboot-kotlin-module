package com.zeta.model.system.dto.sysDict

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * 修改 字典
 *
 * @author AutoGenerator
 * @date 2022-04-15 10:38:20
 */
@ApiModel(description = "修改字典")
data class SysDictUpdateDTO(

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @get:NotNull(message = "id不能为空")
    var id: Long? = null,

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true)
    @get:NotEmpty(message = "名称不能为空")
    @get:Size(max = 32, message = "名称长度不能超过32")
    var name: String? = null,

    /** 编码 */
    @ApiModelProperty(value = "编码", required = true)
    @get:NotEmpty(message = "编码不能为空")
    @get:Size(max = 32, message = "编码长度不能超过32")
    var code: String? = null,

    /** 描述 */
    @ApiModelProperty(value = "描述", required = false)
    var describe: String? = null,

    /** 排序 */
    @ApiModelProperty(value = "排序", required = false)
    var sortValue: Int? = null,

)
