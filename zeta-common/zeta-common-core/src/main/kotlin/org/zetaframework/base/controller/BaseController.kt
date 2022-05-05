package org.zetaframework.base.controller

import com.baomidou.mybatisplus.extension.service.IService
import org.zetaframework.base.result.ApiResult

/**
 * 基础接口
 *
 * @param <Entity> 实体
 * @author gcc
 */
interface BaseController<Entity> {

    /**
     * 获取实体类型
     * @return Class<Entity>
     */
    fun getEntityClass(): Class<Entity>

    /**
     * 获取service
     */
    fun getBaseService(): IService<Entity>


    /**
     * 返回成功
     *
     * @return ApiResult<T>
     */
    fun <T> success(): ApiResult<T> = ApiResult.success()

    /**
     * 返回成功
     *
     * @param message String
     * @return ApiResult<T>
     */
    fun <T> success(message: String): ApiResult<T> = ApiResult.success(message = message)

    /**
     * 返回成功
     *
     * @param data T
     * @return ApiResult<T>
     */
    fun <T> success(data: T): ApiResult<T> = ApiResult.success(data = data)

    /**
     * 返回成功
     *
     * @param message String
     * @param data T
     * @return ApiResult<T>
     */
    fun <T> success(message: String, data: T): ApiResult<T> = ApiResult.success(message = message, data = data)

    /**
     * 返回失败
     *
     * @return ApiResult<T>
     */
    fun <T> fail(): ApiResult<T> = ApiResult.fail()

    /**
     * 返回失败
     *
     * @param message String
     * @return ApiResult<T>
     */
    fun <T> fail(message: String): ApiResult<T> = ApiResult.fail(message = message)

    /**
     * 返回失败
     *
     * @param data T
     * @return ApiResult<T>
     */
    fun <T> fail(data: T): ApiResult<T> = ApiResult.fail(data = data)

    /**
     * 返回失败
     *
     * @param message String
     * @param data T
     * @return ApiResult<T>
     */
    fun <T> fail(message: String, data: T): ApiResult<T> = ApiResult.fail(message = message, data = data)

}
