package com.zeta.admin.controller.system

import cn.afterturn.easypoi.excel.entity.ImportParams
import cn.hutool.core.bean.BeanUtil
import com.zeta.biz.system.poi.SysDictExcelVerifyHandler
import com.zeta.biz.system.service.ISysDictService
import com.zeta.model.system.dto.sysDict.SysDictSaveDTO
import com.zeta.model.system.dto.sysDict.SysDictUpdateDTO
import com.zeta.model.system.entity.SysDict
import com.zeta.model.system.param.SysDictQueryParam
import com.zeta.model.system.poi.SysDictExportPoi
import com.zeta.model.system.poi.SysDictImportPoi
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.base.param.ExistParam
import org.zetaframework.base.result.ApiResult
import org.zetaframework.controller.SuperController
import org.zetaframework.controller.extra.PoiController


/**
 * 字典 前端控制器
 *
 * @author AutoGenerator
 * @date 2022-04-15 10:12:09
 */
@Api(tags = ["字典"])
@PreAuth(replace = "sys:dict")
@RestController
@RequestMapping("/api/system/dict")
class SysDictController(
    private val sysDictExcelVerifyHandler: SysDictExcelVerifyHandler
) : SuperController<ISysDictService, Long, SysDict, SysDictQueryParam, SysDictSaveDTO, SysDictUpdateDTO>(),
    PoiController<SysDictImportPoi, SysDictExportPoi, SysDict, SysDictQueryParam>
{

    /**
     * 自定义新增
     *
     * @param saveDTO SaveDTO 保存对象
     * @return ApiResult<Boolean>
     */
    override fun handlerSave(saveDTO: SysDictSaveDTO): ApiResult<Boolean> {
        // 判断是否存在
        if (ExistParam<SysDict, Long>(SysDict::code, saveDTO.code).isExist(service)) {
            return fail("编码已存在")
        }
        return super.handlerSave(saveDTO)
    }

    /**
     * 自定义修改
     *
     * @param updateDTO UpdateDTO 修改对象
     * @return ApiResult<Boolean>
     */
    override fun handlerUpdate(updateDTO: SysDictUpdateDTO): ApiResult<Boolean> {
        // 判断是否存在
        if (ExistParam<SysDict, Long>(SysDict::code, updateDTO.code, updateDTO.id).isExist(service)) {
            return fail("编码已存在")
        }
        return super.handlerUpdate(updateDTO)
    }

    /**
     * 导入参数增强
     *
     * 说明：
     * 你可以在这里对ImportParams配置进行一些补充
     * 例如设置excel验证规则、校验组、校验处理接口等
     */
    override fun enhanceImportParams(importParams: ImportParams) {
        // 开启：校验上传的Excel数据
        importParams.isNeedVerify = true
        // 校验处理接口：字典编码重复校验
        importParams.verifyHandler = sysDictExcelVerifyHandler
    }

    /**
     * 处理导入数据
     *
     * 说明：
     * 你需要手动实现导入逻辑
     */
    override fun handlerImport(list: MutableList<SysDictImportPoi>): ApiResult<Boolean> {
        val batchList: List<SysDict> = list.map {
            BeanUtil.toBean(it, SysDict::class.java)
        }
        return success(service.saveBatch(batchList))
    }

    /**
     * 获取待导出的数据
     *
     * @param param QueryParam
     * @return MutableList<ExportBean>
     */
    override fun findExportList(param: SysDictQueryParam): MutableList<SysDictExportPoi> {
        // 条件查询Entity数据
        val list = super.handlerBatchQuery(param)
        if (list.isEmpty()) return mutableListOf()

        // Entity -> ExportBean
        return list.map {
            BeanUtil.toBean(it, SysDictExportPoi::class.java)
        }.toMutableList()
    }

}
