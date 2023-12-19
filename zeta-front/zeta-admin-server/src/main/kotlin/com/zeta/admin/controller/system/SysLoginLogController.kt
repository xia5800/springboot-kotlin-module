package com.zeta.admin.controller.system

import com.zeta.biz.system.service.ISysLoginLogService
import com.zeta.model.system.entity.SysLoginLog
import com.zeta.model.system.param.SysLoginLogQueryParam
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.controller.SuperSimpleController
import org.zetaframework.controller.curd.QueryController

/**
 * 登录日志 前端控制器
 *
 * @author AutoGenerator
 * @date 2022-03-21 16:33:13
 */
@Api(tags = ["登录日志"])
@PreAuth(replace = "sys:loginLog")
@RestController
@RequestMapping("/api/system/loginLog")
class SysLoginLogController : SuperSimpleController<ISysLoginLogService, SysLoginLog>(),
    QueryController<SysLoginLog, Long, SysLoginLogQueryParam>
{

}
