package com.zeta.admin.controller.system

import cn.hutool.core.collection.CollUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport
import com.zeta.biz.system.service.ISysMenuService
import com.zeta.biz.system.service.ISysRoleMenuService
import com.zeta.model.system.dto.sysRoleMenu.SysRoleMenuHandleDTO
import com.zeta.model.system.entity.SysMenu
import com.zeta.model.system.entity.SysRoleMenu
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.annotation.satoken.PreCheckPermission
import org.zetaframework.annotation.satoken.PreMode
import org.zetaframework.base.result.ApiResult
import org.zetaframework.base.utils.TreeUtil
import org.zetaframework.controller.SuperSimpleController

/**
 * 角色菜单 前端控制器
 *
 * @author AutoGenerator
 * @date 2021-12-30 15:24:03
 */
@Api(tags = ["角色菜单"])
@PreAuth(replace = "sys:role")
@RestController
@RequestMapping("/api/system/roleMenu")
class SysRoleMenuController(private val menuService: ISysMenuService) : SuperSimpleController<ISysRoleMenuService, SysRoleMenu>() {

    /**
     * 查询角色菜单树
     *
     * 说明：
     * 用于前端角色管理查询角色对应的菜单树。
     * @param roleId Long   角色id
     * @return ApiResult<List<SysMenu?>>
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询角色菜单")
    @GetMapping("/{roleId}")
    fun list(@PathVariable("roleId") @ApiParam("角色id") roleId: Long): ApiResult<List<SysMenu?>> {
        val menuList = menuService.list(KtQueryWrapper(SysMenu()).orderByAsc(SysMenu::sortValue)) ?: return success(mutableListOf())
        val roleMenuList = service.list(KtQueryWrapper(SysRoleMenu()).eq(SysRoleMenu::roleId, roleId))
        for (menu in menuList) {
            menu.checked = roleMenuList.any {
                it.menuId == menu.id
            }
        }
        return success(TreeUtil.buildTree(menuList))
    }


    /**
     * 新增或修改
     *
     * @param roleMenuHandleDto SysRoleMenuHandleDTO 批量新增、修改角色菜单关联关系参数
     * @return ApiResult<Boolean>
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "新增或修改")
    @PutMapping
    fun update(@RequestBody @Validated roleMenuHandleDto: SysRoleMenuHandleDTO): ApiResult<Boolean> {
        // 修改前先删除角色所有权限
        service.remove(KtQueryWrapper(SysRoleMenu()).eq(SysRoleMenu::roleId, roleMenuHandleDto.roleId))

        // 重新关联角色权限
        if (CollUtil.isNotEmpty(roleMenuHandleDto.menuIds)) {
            val batchList: List<SysRoleMenu> = roleMenuHandleDto.menuIds!!.map {
                SysRoleMenu(roleMenuHandleDto.roleId, it)
            }
            if (!service.saveBatch(batchList)) return fail("操作失败")
        }

        // 删除用户角色、权限缓存
        service.clearUserCache(roleMenuHandleDto.roleId!!)
        return success(true)
    }

}
