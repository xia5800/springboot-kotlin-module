package com.zeta.biz.quartz.service

import com.baomidou.mybatisplus.extension.service.IService
import com.zeta.model.quartz.entity.QrtzTriggers
import com.zeta.model.quartz.param.JobQueryParam
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult
import org.zetaframework.quartz.module.QuartzJobDetailDTO

/**
 * <p>
 * 触发器 服务类
 * </p>
 *
 * @author AutoGenerator
 * @date 2022-09-05 13:05:14
 */
interface IQrtzTriggersService: IService<QrtzTriggers> {

    /**
     * 自定义分页查询
     *
     * @param param PageParam<JobQueryParam> 任务查询参数
     */
    fun customPage(param: PageParam<JobQueryParam>): PageResult<QuartzJobDetailDTO>

}
