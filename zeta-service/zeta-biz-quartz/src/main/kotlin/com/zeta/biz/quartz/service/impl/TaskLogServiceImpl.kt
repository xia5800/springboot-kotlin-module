package com.zeta.biz.quartz.service.impl

import cn.hutool.core.exceptions.ExceptionUtil
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.zeta.biz.quartz.dao.TaskLogMapper
import com.zeta.biz.quartz.service.ITaskLogService
import com.zeta.model.quartz.dto.JobInfoDTO
import com.zeta.model.quartz.dto.taskLog.TaskLogDTO
import com.zeta.model.quartz.entity.TaskLog
import com.zeta.model.quartz.param.TaskLogQueryParam
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult

/**
 * 任务调度日志 服务实现类
 *
 * @author AutoGenerator
 * @date 2023-12-20 12:44:04
 */
@Service
class TaskLogServiceImpl: ITaskLogService, ServiceImpl<TaskLogMapper, TaskLog>() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val MAX_LENGTH = 65535
    }

    /**
     * 自定义分页查询
     *
     * @param param PageParam<TaskLogQueryParam>
     */
    override fun customPage(param: PageParam<TaskLogQueryParam>): PageResult<TaskLogDTO> {
        val page = param.buildPage<TaskLogDTO>()
        val taskLogList: List<TaskLogDTO> = baseMapper.customPage(page, param.model ?: TaskLogQueryParam())
        return PageResult(taskLogList, page.total)
    }

    /**
     * 异步保存任务调度日志
     *
     * @param jobInfo 任务信息
     * @param spendTime 执行耗时
     * @param type 日志类型
     * @param result 执行结果
     * @param exception 异常内容
     * @return
     */
    @Async
    override fun asyncSave(jobInfo: JobInfoDTO, spendTime: Long, type: String, result: String?, exception: Throwable?) {
        // 处理异常内容
        val exceptionStr = exception?.let { ExceptionUtil.stacktraceToString(exception, MAX_LENGTH) }

        // 构造日志
        val entity = TaskLog().apply {
            this.type = type
            this.triggerName = jobInfo.triggerName
            this.triggerGroup = jobInfo.triggerGroup
            this.jobName = jobInfo.jobName
            this.jobDescription = jobInfo.jobDescription
            this.jobClassName = jobInfo.jobClassName
            this.jobParam = jobInfo.jobParam
            this.result = result
            this.exception = exceptionStr
            this.spendTime = spendTime
        }

        // 保存
        this.save(entity)
    }

}
