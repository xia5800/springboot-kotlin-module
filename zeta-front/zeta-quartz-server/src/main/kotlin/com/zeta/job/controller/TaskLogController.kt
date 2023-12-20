package com.zeta.job.controller

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport
import com.github.xiaoymin.knife4j.annotations.ApiSupport
import com.zeta.biz.quartz.service.ITaskLogService
import com.zeta.model.quartz.dto.taskLog.TaskLogDTO
import com.zeta.model.quartz.entity.TaskLog
import com.zeta.model.quartz.param.TaskLogQueryParam
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*
import org.zetaframework.annotation.log.SysLog
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.annotation.satoken.PreCheckPermission
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.ApiResult
import org.zetaframework.base.result.PageResult
import org.zetaframework.controller.SuperSimpleController

/**
 * 任务调度日志 前端控制器
 *
 * @author AutoGenerator
 * @date 2023-12-20 12:44:04
 */
@ApiSupport(order = 2)
@Api(tags = ["任务调度日志"])
@PreAuth(replace = "task:log")
@RestController
@RequestMapping("/api/taskLog")
class TaskLogController: SuperSimpleController<ITaskLogService, TaskLog>() {

    /**
     * 分页查询
     *
     * 说明：
     * 如果任务日志是“立即运行一次”创建的，没有cron表达式是正常的
     *
     * @param param PageParam<PageQuery> 分页查询参数
     * @return ApiResult<IPage<Entity>>
     */
    @PreCheckPermission(value = ["{}:view"])
    @ApiOperationSupport(order = 10)
    @SysLog(response = false)
    @ApiOperation(value = "分页查询")
    @PostMapping("/page")
    fun page(@RequestBody param: PageParam<TaskLogQueryParam>): ApiResult<PageResult<TaskLogDTO>> {
        return success(service.customPage(param))
    }

}
