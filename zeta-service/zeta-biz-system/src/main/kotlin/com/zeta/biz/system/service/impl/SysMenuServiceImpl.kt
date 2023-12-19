package com.zeta.biz.system.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.zeta.biz.system.dao.SysMenuMapper
import com.zeta.biz.system.service.ISysMenuService
import com.zeta.model.system.entity.SysMenu
import org.springframework.stereotype.Service

/**
 * 菜单 服务实现类
 *
 * @author AutoGenerator
 * @date 2021-12-30 15:24:03
 */
@Service
class SysMenuServiceImpl: ISysMenuService, ServiceImpl<SysMenuMapper, SysMenu>() {

}
