package org.zetaframework.controller.curd

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.zetaframework.annotation.log.SysLog
import org.zetaframework.annotation.satoken.PreCheckPermission
import org.zetaframework.annotation.satoken.PreMode
import org.zetaframework.base.result.ApiResult
import org.zetaframework.controller.BaseController
import java.io.Serializable

/**
 * 基础删除 Controller
 *
 * @param <Entity> 实体
 * @param <Id>     主键字段类型
 * @author gcc
 */
interface DeleteController<Entity, Id: Serializable>: BaseController<Entity> {

    /**
     * 单体删除
     *
     * @param id Id
     * @return ApiResult<Boolean>
     */
    @PreCheckPermission(value = ["{}:delete", "{}:remove"], mode = PreMode.OR)
    @ApiOperationSupport(order = 60, author = "AutoGenerate")
    @ApiOperation(value = "单体删除")
    @SysLog
    @DeleteMapping("/{id}")
    fun delete(@PathVariable @ApiParam("主键") id: Id): ApiResult<Boolean> {
        val result = handlerDelete(id)
        if (result.defExec) {
            result.setData(getBaseService().removeById(id))
        }
        return result
    }

    /**
     * 自定义单体删除
     *
     * @param id Id
     * @return ApiResult<Boolean>
     */
    fun handlerDelete(id: Id): ApiResult<Boolean> {
        return ApiResult.successDef()
    }

    /**
     * 批量删除
     *
     * @param ids List<Id>
     * @return ApiResult<Boolean>
     */
    @PreCheckPermission(value = ["{}:delete", "{}:remove"], mode = PreMode.OR)
    @ApiOperationSupport(order = 70, author = "AutoGenerate")
    @ApiOperation(value = "批量删除")
    @SysLog
    @DeleteMapping("/batch")
    fun batchDelete(@RequestBody ids: MutableList<Id>): ApiResult<Boolean> {
        val result = handlerBatchDelete(ids)
        if (result.defExec) {
            result.setData(getBaseService().removeByIds(ids))
        }
        return result
    }

    /**
     * 自定义批量删除
     *
     * @param ids List<Id>
     * @return ApiResult<Boolean>
     */
    fun handlerBatchDelete(ids: MutableList<Id>): ApiResult<Boolean> {
        return ApiResult.successDef()
    }
}
