package com.zeta.biz.system.dao

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.zeta.model.system.dto.sysDictItem.SysDictItemDTO
import com.zeta.model.system.entity.SysDictItem
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

/**
 * 字典项 Mapper 接口
 *
 * @author AutoGenerator
 * @date 2022-04-15 10:12:10
 */
@Repository
interface SysDictItemMapper: BaseMapper<SysDictItem> {

    /**
     * 根据字典编码查询字典项
     *
     * @param codes
     */
    fun selectByDictCodes(@Param("codes") codes: List<String>): MutableList<SysDictItemDTO>
}
