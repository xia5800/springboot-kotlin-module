package com.zeta.job

import com.zeta.biz.system.service.ISysMenuService
import com.zeta.biz.system.service.ISysRoleMenuService
import com.zeta.model.system.entity.SysMenu
import com.zeta.model.system.entity.SysRoleMenu
import com.zeta.model.system.enums.MenuTypeEnum
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.zetaframework.mybatisplus.generator.UidGenerator

/**
 * 初始化数据库
 *
 * @author gcc
 */
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class InitDatabaseTests(
    private val uidGenerator: UidGenerator,
    private val menuService: ISysMenuService,
    private val roleMenuService: ISysRoleMenuService
) {
   companion object {
       // 超级管理员角色id
       const val SUPER_ADMIN_ROLE_ID: Long = 1645607076937859072L
   }

    /**
     * 初始化数据库
     */
    // @Test // 注释掉，防止maven打包的时候没有跳过测试
    fun initDatabase() {
        // 初始化定时任务菜单、权限
        val menuIds = initMenu()
        // 角色菜单关联
        initRoleMenu(SUPER_ADMIN_ROLE_ID, menuIds)
    }

    /**
     * 初始化定时任务菜单、权限
     */
    fun initMenu(): List<Long> {
        val batchList: MutableList<SysMenu> = mutableListOf()
        var menuSort = 99

        // 任务管理
        var jobManageSort = 1
        val jobManageId = uidGenerator.getUid()
        batchList.add(buildMenu(jobManageId, 0L, menuSort++, "任务管理", "/job", icon = "layui-icon-time").apply {
            this.redirect = "/job/index"
        })

        // 任务管理-任务列表
        val jobId = uidGenerator.getUid()
        val jobIdR = uidGenerator.getUid()
        val jobIdC = uidGenerator.getUid()
        val jobIdU = uidGenerator.getUid()
        val jobIdD = uidGenerator.getUid()
        batchList.add(buildMenu(jobId, jobManageId, jobManageSort++, "任务列表", "/job/index", "job/job"))
        batchList.add(buildButton(jobIdR, jobId, 1, "查看任务", "task:view"))
        batchList.add(buildButton(jobIdC, jobId, 2, "新增任务", "task:save"))
        batchList.add(buildButton(jobIdU, jobId, 3, "修改任务", "task:update"))
        batchList.add(buildButton(jobIdD, jobId, 4, "删除任务", "task:delete"))

        // 任务管理-任务日志
        val jobLogId = uidGenerator.getUid()
        val jobLogIdR = uidGenerator.getUid()
        batchList.add(buildMenu(jobLogId, jobManageId, jobManageSort++, "调度日志", "/job/log", "job/log"))
        batchList.add(buildButton(jobLogIdR, jobLogId, 1, "查看调度日志", "task:log:view"))

        menuService.saveBatch(batchList)

        return mutableListOf(
            jobManageId,
            jobId, jobIdR, jobIdC, jobIdU, jobIdD,
            jobLogId, jobLogIdR
        )
    }


    /**
     * 初始化超级管理员菜单权限
     * @param superAdminRoleId Long     超级管理员角色id
     * @param menuIds List<Long>    菜单id
     */
    fun initRoleMenu(superAdminRoleId: Long, menuIds: List<Long>) {
        val batchList: MutableList<SysRoleMenu> = mutableListOf()
        menuIds.forEach {
            batchList.add(SysRoleMenu().apply { roleId = superAdminRoleId; menuId = it })
        }

        roleMenuService.saveBatch(batchList)
    }

    /**
     * 构造菜单
     *
     * @param id
     * @param parentId
     * @param sortValue
     * @param label
     * @param path
     * @param icon
     */
    private fun buildMenu(id: Long, parentId: Long, sortValue: Int, label: String, path: String, component: String = "", icon: String = ""): SysMenu {
        // 将"/system" => "system";  "/system/user" => "system_user";  "/system/user/123" => "system_user_123"
        val name = path.split("/").filterNot { it.isBlank() }.joinToString("_")
        return SysMenu().apply {
            this.id = id
            this.parentId = parentId
            this.sortValue = sortValue
            this.label = label
            this.path = path
            this.name = name
            this.component = component
            this.icon = icon
            this.type = MenuTypeEnum.MENU
            this.authority = ""
        }
    }

    /**
     * 构造按钮
     *
     * @param id
     * @param parentId
     * @param sortValue
     * @param label
     * @param authority
     */
    private fun buildButton(id: Long, parentId: Long, sortValue: Int, label: String, authority: String): SysMenu {
        return SysMenu().apply {
            this.id = id
            this.parentId = parentId
            this.sortValue = sortValue
            this.label = label
            this.type = MenuTypeEnum.RESOURCE
            this.authority = authority
        }
    }
}