package com.zeta.system.controller

import com.zeta.system.model.dto.sysRole.SysRoleSaveDTO
import com.zeta.system.model.dto.sysRole.SysRoleUpdateDTO
import com.zeta.system.model.entity.SysRole
import com.zeta.system.model.param.SysRoleQueryParam
import com.zeta.system.service.ISysRoleService
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.zetaframework.base.controller.SuperController
import org.zetaframework.base.param.ExistParam
import org.zetaframework.base.result.ApiResult
import org.zetaframework.core.saToken.annotation.PreAuth

/**
 * 角色 前端控制器
 *
 * @author AutoGenerator
 * @date 2021-12-30 15:24:03
 */
@Api(tags = ["角色管理"])
@PreAuth(replace = "sys:role")
@RestController
@RequestMapping("/api/system/role")
class SysRoleController :
    SuperController<ISysRoleService, Long, SysRole, SysRoleQueryParam, SysRoleSaveDTO, SysRoleUpdateDTO>()
{

    /**
     * 自定义新增
     *
     * @param saveDTO SaveDTO 保存对象
     * @return ApiResult<Entity>
     */
    override fun handlerSave(saveDTO: SysRoleSaveDTO): ApiResult<Boolean> {
        // 判断是否存在
        if(ExistParam<SysRole, Long>("name", saveDTO.name).isExist(service)) {
            return fail("角色名已存在")
        }
        if(ExistParam<SysRole, Long>("code", saveDTO.code).isExist(service)) {
            return fail("角色编码已存在")
        }

        return super.handlerSave(saveDTO)
    }


    /**
     * 自定义修改
     *
     * @param updateDTO UpdateDTO 修改对象
     * @return ApiResult<Entity>
     */
    override fun handlerUpdate(updateDTO: SysRoleUpdateDTO): ApiResult<Boolean> {
        // 判断是否存在
        if(ExistParam<SysRole, Long>("name", updateDTO.name, updateDTO.id).isExist(service)) {
            return fail("角色名已存在")
        }
        if(ExistParam<SysRole, Long>("code", updateDTO.code, updateDTO.id).isExist(service)) {
            return fail("角色编码已存在")
        }

        return super.handlerUpdate(updateDTO)
    }


}
