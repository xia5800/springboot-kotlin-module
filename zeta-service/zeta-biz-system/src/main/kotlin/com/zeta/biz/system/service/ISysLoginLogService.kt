package com.zeta.biz.system.service

import com.baomidou.mybatisplus.extension.service.IService
import com.zeta.model.system.entity.SysLoginLog
import org.zetaframework.core.model.LoginLogDTO

/**
 * 登录日志 服务类
 *
 * @author AutoGenerator
 * @date 2022-03-21 16:33:13
 */
interface ISysLoginLogService: IService<SysLoginLog> {

    /**
     * 保存用户登录日志
     *
     * @param loginLogDTO [LoginLogDTO]
     */
    fun save(loginLogDTO: LoginLogDTO)
}
