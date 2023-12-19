package com.zeta.biz.system.poi

import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler
import com.zeta.biz.system.service.ISysUserService
import com.zeta.model.system.entity.SysUser
import com.zeta.model.system.poi.SysUserImportPoi
import org.springframework.stereotype.Component
import org.zetaframework.base.param.ExistParam

/**
 * 用户Excel导入校验接口实现类
 *
 * @author gcc
 */
@Component
class SysUserExcelVerifyHandler(private val service: ISysUserService): IExcelVerifyHandler<SysUserImportPoi> {

    /**
     * 导入校验方法
     *
     * @param obj 导入对象
     * @return
     */
    override fun verifyHandler(obj: SysUserImportPoi): ExcelVerifyHandlerResult {
        var message = ""
        var flag = true

        if (obj.account.isNullOrBlank()) {
            message = "账号不能为空"
            flag = false
        } else {
            // 判断是否存在
            if (ExistParam<SysUser, Long>(SysUser::account, obj.account).isExist(service)) {
                message = "账号已存在"
                flag = false
            }
        }

        return ExcelVerifyHandlerResult(flag, message)
    }

}
