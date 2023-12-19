package com.zeta.biz.quartz.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.zeta.biz.quartz.dao.QrtzTriggersMapper
import com.zeta.biz.quartz.service.IQrtzTriggersService
import com.zeta.model.quartz.entity.QrtzTriggers
import com.zeta.model.quartz.param.JobQueryParam
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult
import org.zetaframework.quartz.module.QuartzJobDetailDTO
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
class QrtzTriggersServiceImpl: IQrtzTriggersService, ServiceImpl<QrtzTriggersMapper, QrtzTriggers>() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 自定义分页查询
     *
     * @param param PageParam<JobQueryParam> 任务查询参数
     */
    override fun customPage(param: PageParam<JobQueryParam>): PageResult<QuartzJobDetailDTO> {
        val page = param.buildPage<QuartzJobDetailDTO>()
        val triggerList = baseMapper.customPage(page, param.model ?: JobQueryParam())
        // 修改触发器状态
        triggerList.forEach { it.triggerState = QuartzUtil.jdbcTriggerTypeConvert(it.triggerState) }
        return PageResult(triggerList, page.total)
    }


}
