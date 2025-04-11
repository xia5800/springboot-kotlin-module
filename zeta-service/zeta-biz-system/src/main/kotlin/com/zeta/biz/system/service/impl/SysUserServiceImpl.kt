package com.zeta.biz.system.service.impl

import cn.dev33.satoken.secure.BCrypt
import cn.hutool.core.bean.BeanUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.zeta.biz.system.dao.SysUserMapper
import com.zeta.biz.system.service.ISysUserRoleService
import com.zeta.biz.system.service.ISysUserService
import com.zeta.model.system.dto.sysRole.SysRoleDTO
import com.zeta.model.system.dto.sysUser.SysUserDTO
import com.zeta.model.system.dto.sysUser.SysUserSaveDTO
import com.zeta.model.system.dto.sysUser.SysUserUpdateDTO
import com.zeta.model.system.entity.SysRole
import com.zeta.model.system.entity.SysUser
import com.zeta.model.system.enums.UserStateEnum
import com.zeta.model.system.param.SysUserQueryParam
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.zetaframework.base.exception.BusinessException
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.PageResult
import org.zetaframework.core.constants.RedisKeyConstants.USER_PERMISSION_KEY
import org.zetaframework.core.constants.RedisKeyConstants.USER_ROLE_KEY

/**
 * 用户 服务实现类
 *
 * @author AutoGenerator
 * @date 2021-12-30 15:24:03
 */
@Service
class SysUserServiceImpl(
    private val userRoleService: ISysUserRoleService,
): ISysUserService, ServiceImpl<SysUserMapper, SysUser>() {

    /**
     * 自定义分页查询
     *
     * @param param 分页查询参数
     * @return PageResult<SysUserDTO>
     */
    override fun customPage(param: PageParam<SysUserQueryParam>): PageResult<SysUserDTO> {
        // 构造分页page
        var page = param.buildPage<SysUser>()

        // 构造查询条件
        val model = param.model ?: SysUserQueryParam()
        val entity = BeanUtil.toBean(model, SysUser::class.java)

        // 分页查询
        page = this.page(page, KtQueryWrapper(entity))

        // 批量获取用户角色 Map<用户id, 用户角色列表>
        val userIds = page.records.filterNotNull().map { it.id!! }
        val userRoleMap: Map<Long, List<SysRoleDTO>> = if (userIds.isNotEmpty()) {
            this.getUserRoles(userIds)
        } else mutableMapOf()

        // 处理返回结果
        val result = page.records.map { user ->
            // 设置用户角色
            user.roles = userRoleMap.getOrDefault(user.id, mutableListOf())
            // Entity -> EntityDTO
            BeanUtil.toBean(user, SysUserDTO::class.java)
        }

        return PageResult(result, page.total)
    }

    /**
     * 添加用户
     * @param saveDTO SysUserSaveDTO
     * @return Boolean
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun saveUser(saveDTO: SysUserSaveDTO): Boolean {
        // 保存用户
        val user = BeanUtil.toBean(saveDTO, SysUser::class.java)
        user.password = encodePassword(saveDTO.password!!)
        user.readonly = false
        user.state = UserStateEnum.NORMAL.code
        if (!this.save(user)) {
            throw BusinessException("新增用户失败")
        }

        // 删除并重新关联角色
        return userRoleService.saveUserRole(user.id!!, saveDTO.roleIds!!)
    }

    /**
     * 修改用户
     * @param updateDTO SysUserUpdateDTO
     * @return Boolean
     */
    @CacheEvict(value = [USER_PERMISSION_KEY, USER_ROLE_KEY], key = "#updateDTO.id")
    @Transactional(rollbackFor = [Exception::class])
    override fun updateUser(updateDTO: SysUserUpdateDTO): Boolean {
        val user = BeanUtil.toBean(updateDTO, SysUser::class.java)
        if (!this.updateById(user)) {
            throw BusinessException("修改用户失败")
        }

        // 删除并重新关联角色
        return userRoleService.saveUserRole(user.id!!, updateDTO.roleIds)
    }

    /**
     * 修改用户基本信息
     * @param changeUser SysUser 待修改的用户信息
     * @return Boolean
     */
    @CacheEvict(value = [USER_PERMISSION_KEY, USER_ROLE_KEY], key = "#changeUser.id")
    @Transactional(rollbackFor = [Exception::class])
    override fun updateUserBaseInfo(changeUser: SysUser): Boolean {
        if (!this.updateById(changeUser)) {
            throw BusinessException("修改用户失败")
        }

        return true;
    }

    /**
     * 获取用户角色
     *
     * @param userId Long
     * @return List<SysRole?>
     */
    override fun getUserRoles(userId: Long): List<SysRoleDTO> {
        // 根据用户id查询角色
        val roleList: List<SysRole> = userRoleService.listByUserId(userId)
        if (roleList.isEmpty()) return emptyList()

        // List<Entity> -> List<EntityDTO>
        return roleList.map { BeanUtil.toBean(it, SysRoleDTO::class.java) }
    }

    /**
     * 批量获取用户角色
     * @param userIds List<Long>
     * @return Map<Long, List<SysRole?>>
     */
    override fun getUserRoles(userIds: List<Long>): Map<Long, List<SysRoleDTO>> {
        // 批量根据用户id查询角色
        val roleList = userRoleService.listByUserIds(userIds)
        if (roleList.isEmpty()) return emptyMap()

        // 处理返回值, 得到 Map<用户id, 用户角色列表>
        return roleList.filter { it.userId != null }.groupBy { it.userId!! }
    }

    /**
     * 通过账号查询用户 （演示使用xml查询）
     * @param account String
     * @return User
     */
    override fun getByAccount(account: String): SysUser? {
        try {
            return baseMapper.selectByAccount(account)
        }catch (e: Exception) {
            // 可能查询到多个用户
            throw BusinessException("查询到多个用户")
        }
    }

    /**
     * 加密用户密码
     *
     * @param password String 明文
     * @return String   密文
     */
    override fun encodePassword(password: String): String = BCrypt.hashpw(password)

    /**
     * 比较密码
     *
     * @param inputPwd String 用户输入的密码
     * @param dbPwd String    用户数据库中的密码
     * @return Boolean
     */
    override fun comparePassword(inputPwd: String, dbPwd: String): Boolean = BCrypt.checkpw(inputPwd, dbPwd)


    /**
     * 批量导入用户
     *
     * @param userList 待导入的用户列表
     * @return Boolean
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun batchImportUser(userList: List<SysUser>): Boolean {
        // 保存用户
        if (!this.saveBatch(userList)) {
            throw BusinessException("新增用户失败")
        }

        try {
            // 筛选出有角色的用户
            userList.filterNot { it.roles.isNullOrEmpty() }.forEach { user ->
                // 删除并重新关联角色
                val roleIds: List<Long> = user.roles!!.mapNotNull { it.id }
                userRoleService.saveUserRole(user.id!!, roleIds)
            }
        } catch (e: Exception) {
            throw BusinessException("关联用户角色失败")
        }

        return true
    }

}
