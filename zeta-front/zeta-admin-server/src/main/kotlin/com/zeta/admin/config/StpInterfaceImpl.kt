package com.zeta.admin.config

import cn.dev33.satoken.stp.StpInterface
import cn.hutool.core.collection.CollUtil
import com.zeta.biz.system.service.ISysRoleMenuService
import com.zeta.biz.system.service.ISysUserRoleService
import com.zeta.model.system.entity.SysMenu
import com.zeta.model.system.entity.SysRole
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.zetaframework.core.constants.RedisKeyConstants

/**
 * 权限认证接口 实现类
 *
 * @author gcc
 */
@Component
class StpInterfaceImpl(
    private val userRoleService: ISysUserRoleService,
    private val roleMenuService: ISysRoleMenuService,
): StpInterface {

    /**
     * 返回指定账号id所拥有的权限码集合
     *
     * @param loginId  账号id
     * @param loginType 账号类型
     * @return 该账号id具有的权限码集合
     */
    @Cacheable(value = [RedisKeyConstants.USER_PERMISSION_KEY], key = "#p0")
    override fun getPermissionList(loginId: Any?, loginType: String?): List<String> {
        loginId ?: return emptyList()
        val authorities: List<SysMenu> = roleMenuService.listMenuByUserId(loginId.toString().toLong())
        if(CollUtil.isEmpty(authorities)) {
            return emptyList()
        }
        return authorities.mapNotNull { it.authority }.filterNot { it == "" }
    }

    /**
     * 返回指定账号id所拥有的角色标识集合
     *
     * @param loginId  账号id
     * @param loginType 账号类型
     * @return 该账号id具有的角色标识集合
     */
    @Cacheable(value = [RedisKeyConstants.USER_ROLE_KEY], key = "#p0")
    override fun getRoleList(loginId: Any?, loginType: String?): List<String> {
        loginId ?: return emptyList()
        val roleList: List<SysRole> = userRoleService.listByUserId(loginId.toString().toLong())
        if (CollUtil.isEmpty(roleList)) {
            return emptyList()
        }
        return roleList.mapNotNull { it.code }.filterNot { it == "" }
    }
}
