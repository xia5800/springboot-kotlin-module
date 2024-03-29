package com.zeta.admin.controller.system

import com.zeta.biz.system.service.ISysFileService
import com.zeta.model.system.entity.SysFile
import com.zeta.model.system.param.SysFileQueryParam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.zetaframework.annotation.log.SysLog
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.annotation.satoken.PreCheckPermission
import org.zetaframework.annotation.satoken.PreMode
import org.zetaframework.base.result.ApiResult
import org.zetaframework.controller.SuperSimpleController
import org.zetaframework.controller.curd.DeleteController
import org.zetaframework.controller.curd.QueryController
import javax.servlet.http.HttpServletResponse

/**
 * 系统文件 前端控制器
 *
 * @author AutoGenerator
 * @date 2022-04-11 11:18:44
 */
@Api(tags = ["系统文件"])
@PreAuth(replace = "sys:file")
@RestController
@RequestMapping("/api/system/file")
class SysFileController: SuperSimpleController<ISysFileService, SysFile>(),
    QueryController<SysFile, Long, SysFileQueryParam>,
    DeleteController<SysFile, Long>
{

    /**
     * 上传文件
     *
     * 吐个槽：注解地狱
     * @param file 文件对象
     * @param bizType 业务类型 例如：order、user_avatar等 会影响文件url的值
     */
    @SysLog(request = false)
    @PreCheckPermission(value = ["{}:add", "{}:save"], mode = PreMode.OR)
    @ApiOperation(value = "上传文件")
    @PostMapping("/upload")
    fun upload(
        @RequestParam
        file: MultipartFile,
        @RequestParam(required = false)
        @ApiParam(value = "业务类型 例如：order、user_avatar等 会影响文件url的值", example = "avatar")
        bizType: String? = null
    ): ApiResult<SysFile> {
        return success(service.upload(file, bizType))
    }


    /**
     * 下载文件
     *
     * @param id 文件id
     * @param response
     */
    @SysLog(response = false)
    @PreCheckPermission(value = ["{}:export"])
    @ApiOperation(value = "下载文件")
    @GetMapping(value = ["/download"], produces = ["application/octet-stream"])
    fun download(@RequestParam @ApiParam("文件id") id: Long, response: HttpServletResponse) {
        service.download(id, response)
    }

    /**
     * 自定义单体删除文件
     *
     * @param id Id
     * @return ApiResult<Boolean>
     */
    override fun handlerDelete(id: Long): ApiResult<Boolean> {
        return success(service.delete(id))
    }

    /**
     * 自定义批量删除文件
     *
     * @param ids Id
     * @return ApiResult<Boolean>
     */
    override fun handlerBatchDelete(ids: MutableList<Long>): ApiResult<Boolean> {
        return success(service.batchDelete(ids))
    }
}
