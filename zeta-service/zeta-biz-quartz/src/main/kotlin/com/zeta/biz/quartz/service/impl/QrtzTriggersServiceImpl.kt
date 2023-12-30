package com.zeta.biz.quartz.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.zeta.biz.quartz.dao.QrtzTriggersMapper
import com.zeta.biz.quartz.service.IQrtzTriggersService
import com.zeta.model.quartz.entity.QrtzTriggers
import com.zeta.model.quartz.param.JobQueryParam
import org.quartz.JobKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult
import org.zetaframework.core.utils.JSONUtil
import org.zetaframework.quartz.module.QuartzJobDetailDTO
import org.zetaframework.quartz.utils.QuartzManager
import org.zetaframework.quartz.utils.QuartzUtil

/**
 * <p>
 * 触发器 服务实现类
 * </p>
 *
 * @author AutoGenerator
 * @date 2022-09-05 13:05:14
 */
@Service
class QrtzTriggersServiceImpl(private val quartzManager: QuartzManager): IQrtzTriggersService, ServiceImpl<QrtzTriggersMapper, QrtzTriggers>() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 自定义分页查询
     *
     * @param param PageParam<JobQueryParam> 任务查询参数
     */
    override fun customPage(param: PageParam<JobQueryParam>): PageResult<QuartzJobDetailDTO> {
        // 构造分页对象
        val page = param.buildPage<QuartzJobDetailDTO>()

        // 处理分页查询条件
        val pageParam = param.model ?: JobQueryParam()
        pageParam.triggerStates = pageParam.triggerState?.split(",")?.map {
            it.trim()
        }

        // 分页查询
        val triggerList = baseMapper.customPage(page, pageParam)

        // 返回值处理
        if (triggerList.isNotEmpty()) {
            val schedule = quartzManager.getScheduler()

            // 修改触发器状态
            triggerList.forEach {
                // 将数据库中触发器状态 转换成 Quartz TriggerState枚举对应的状态
                it.triggerState = QuartzUtil.jdbcTriggerTypeConvert(it.triggerState)

                // 获取jobParam
                val jobDetail = schedule.getJobDetail(JobKey(it.jobName))
                it.jobParam = JSONUtil.toJsonStr(jobDetail.jobDataMap)
            }
        }

        // 构造分页结果
        return PageResult(triggerList, page.total)
    }

}
