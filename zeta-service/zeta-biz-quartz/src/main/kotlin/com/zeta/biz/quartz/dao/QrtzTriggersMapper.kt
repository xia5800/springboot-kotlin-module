package com.zeta.biz.quartz.dao

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.zeta.model.quartz.entity.QrtzTriggers
import com.zeta.model.quartz.param.JobQueryParam
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository
import org.zetaframework.quartz.module.QuartzJobDetailDTO

/**
 * <p>
 * 触发器 Mapper 接口
 * </p>
 *
 * @author AutoGenerator
 * @date 2022-09-05 13:05:14
 */
@Repository
interface QrtzTriggersMapper: BaseMapper<QrtzTriggers> {

    /**
     * 自定义分页查询
     *
     * @param page
     * @param param
     */
    fun customPage(
        @Param("page") page: IPage<QuartzJobDetailDTO>,
        @Param("param") param: JobQueryParam
    ): List<QuartzJobDetailDTO>

}
