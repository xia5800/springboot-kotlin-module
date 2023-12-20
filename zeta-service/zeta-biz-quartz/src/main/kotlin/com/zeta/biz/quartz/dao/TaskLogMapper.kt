package com.zeta.biz.quartz.dao

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.zeta.model.quartz.dto.taskLog.TaskLogDTO
import com.zeta.model.quartz.entity.TaskLog
import com.zeta.model.quartz.param.TaskLogQueryParam
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

/**
 * 任务调度日志 Mapper 接口
 *
 * @author AutoGenerator
 * @date 2023-12-20 12:44:04
 */
@Repository
interface TaskLogMapper: BaseMapper<TaskLog> {

    /**
     * 分页查询任务调度日志
     *
     * @param page
     * @param param
     */
    fun customPage(
        @Param("page") page: IPage<TaskLogDTO>,
        @Param("param") param: TaskLogQueryParam
    ): List<TaskLogDTO>

}
