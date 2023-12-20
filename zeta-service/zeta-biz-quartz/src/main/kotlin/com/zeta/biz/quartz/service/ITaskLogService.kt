package com.zeta.biz.quartz.service

import com.zeta.model.quartz.entity.TaskLog
import com.baomidou.mybatisplus.extension.service.IService
import com.zeta.model.quartz.dto.JobInfoDTO
import com.zeta.model.quartz.dto.taskLog.TaskLogDTO
import com.zeta.model.quartz.param.TaskLogQueryParam
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult

/**
 * 任务调度日志 服务类
 *
 * @author AutoGenerator
 * @date 2023-12-20 12:44:04
 */
interface ITaskLogService: IService<TaskLog> {


    /**
     * 自定义分页查询
     *
     * @param param PageParam<TaskLogQueryParam>
     */
    fun customPage(param: PageParam<TaskLogQueryParam>): PageResult<TaskLogDTO>


    /**
     * 异步保存任务调度日志
     *
     * @param jobInfo 任务信息
     * @param type 日志类型
     * @param spendTime 执行耗时
     * @param result 执行结果
     * @param exception 异常内容
     * @return
     */
    fun asyncSave(jobInfo: JobInfoDTO, type: String, spendTime: Long, result: String?, exception: Throwable? = null)

}
