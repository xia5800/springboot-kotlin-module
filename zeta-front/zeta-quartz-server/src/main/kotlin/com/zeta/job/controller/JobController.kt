package com.zeta.job.controller

import cn.hutool.core.util.EnumUtil
import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.core.type.TypeReference
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport
import com.github.xiaoymin.knife4j.annotations.ApiSupport
import com.zeta.biz.quartz.service.IQrtzTriggersService
import com.zeta.model.quartz.dto.JobSaveDTO
import com.zeta.model.quartz.dto.JobTriggerUpdateDTO
import com.zeta.model.quartz.entity.QrtzTriggers
import com.zeta.model.quartz.param.JobOperationParam
import com.zeta.model.quartz.param.JobQueryParam
import com.zeta.model.quartz.result.JobClassListResult
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.Trigger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zetaframework.annotation.log.SysLog
import org.zetaframework.annotation.satoken.PreAuth
import org.zetaframework.annotation.satoken.PreCheckPermission
import org.zetaframework.annotation.satoken.PreMode
import org.zetaframework.annotation.xss.NoXss
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.ApiResult
import org.zetaframework.base.result.PageResult
import org.zetaframework.controller.SuperBaseController
import org.zetaframework.core.enums.QuartzJobEnum
import org.zetaframework.core.utils.JSONUtil
import org.zetaframework.quartz.builder.CronTriggerBuilder
import org.zetaframework.quartz.builder.JobDetailBuilder
import org.zetaframework.quartz.module.QuartzJobDetailDTO
import org.zetaframework.quartz.utils.QuartzManager

/**
 * 定时任务 前端控制器
 *
 * @author gcc
 */
@ApiSupport(order = 1)
@Api(tags = ["定时任务"])
@PreAuth(replace = "task")
@RestController
@RequestMapping("/api/job")
class JobController(
    private val quartzManager: QuartzManager,
    private val triggersService: IQrtzTriggersService
): SuperBaseController {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 分页查询定时任务
     *
     * @param param PageParam<JobQueryParam> 任务查询参数
     */
    @PreCheckPermission(value = ["{}:view"])
    @ApiOperationSupport(order = 10)
    @SysLog(response = false)
    @ApiOperation(value = "分页查询", notes = """
    触发器状态值说明：
    null: 获取状态失败
    NONE: 触发器不存在或被删除
    NORMAL: 正常
    PAUSED: 暂停
    COMPLETE: 完成
    ERROR: 错误
    BLOCKED: 阻塞
    """)
    @PostMapping("/page")
    fun page(@RequestBody @Validated param: PageParam<JobQueryParam>): ApiResult<PageResult<QuartzJobDetailDTO>> {
        if (param.sort?.equals("id") == true) {
            param.sort = QrtzTriggers::triggerName.name
        }
        return success(triggersService.customPage(param))
    }

    /**
     * 获取所有定时任务
     *
     * @return ApiResult<List<QuartzJobDetailDTO>>
     */
    @PreCheckPermission(value = ["{}:view"])
    @ApiOperationSupport(order = 20)
    @SysLog
    @ApiOperation(value = "列表查询")
    @GetMapping
    fun list(): ApiResult<List<QuartzJobDetailDTO>> {
        return success(quartzManager.queryAllJob())
    }


    /**
     * 任务执行类列表
     *
     * @return ApiResult<MutableList<Any>>
     */
    @PreCheckPermission(value = ["{}:view"])
    @ApiOperationSupport(order = 21)
    @ApiOperation(value = "任务执行类列表")
    @GetMapping( "/jobClassList")
    fun jobClassNameList(): ApiResult<MutableList<JobClassListResult>> {
        val result = mutableListOf<JobClassListResult>()

        // 获取枚举字符串值和枚举对象的Map对应，使用LinkedHashMap保证有序
        val enumMap = EnumUtil.getEnumMap(QuartzJobEnum::class.java)
        enumMap.forEach { (_, enum) ->
            // 构造返回值
            if (enum.isShow) {
                result.add(JobClassListResult(value = enum.clazzPath, description = enum.description))
            }
        }
        return success(result)
    }


    /**
     * 新增
     *
     * @param saveDTO SaveDTO 保存对象
     * @return ApiResult<Boolean>
     */
    @NoXss // 加这个注解主要是因为，saveDTO.jobParam的值是一个json字符串，会被xss过滤掉，导致json字符串转换成map失败
    @PreCheckPermission(value = ["{}:add", "{}:save"], mode = PreMode.OR)
    @ApiOperationSupport(order = 40)
    @SysLog
    @ApiOperation(value = "新增", notes = "新增并立即执行")
    @PostMapping
    fun save(@RequestBody @Validated saveDTO: JobSaveDTO): ApiResult<Boolean> {
        val jobClazz: Class<Job>
        try {
            jobClazz = Class.forName(saveDTO.jobClassName) as Class<Job>
        }catch (e: Exception) {
            return fail("任务执行类错误", false)
        }

        // 设置默认值
        val jobGroup = if (StrUtil.isBlank(saveDTO.jobGroup)) Scheduler.DEFAULT_GROUP else saveDTO.jobGroup
        val triggerGroup = if (StrUtil.isBlank(saveDTO.triggerGroup)) Scheduler.DEFAULT_GROUP else saveDTO.triggerGroup
        val priority = if (saveDTO.priority == null) Trigger.DEFAULT_PRIORITY else saveDTO.priority

        // 处理任务参数 string -> Map<string, object>  ps: 转换失败返回null
        val jobParam : MutableMap<String, Any?>? = JSONUtil.parseObject(saveDTO.jobParam, object: TypeReference<MutableMap<String, Any?>>(){})

        // 构造任务
        val jobDetail = JobDetailBuilder(
            name = saveDTO.jobName!!,
            jobClazz = jobClazz,
            groupName = jobGroup!!,
            description = saveDTO.jobDescription,
            param = jobParam
        )

        // 构造cron触发器
        val cronTrigger = CronTriggerBuilder(
            name = saveDTO.triggerName!!,
            cron = saveDTO.cron!!,
            priority = priority!!,
            groupName = triggerGroup!!,
            description = saveDTO.triggerDescription
        )

        // 创建一个CronJob
        quartzManager.createCronJob(jobDetail, cronTrigger)
        return success(true)
    }


    /**
     * 修改
     *
     * @param updateDTO UpdateDTO 修改对象
     * @return ApiResult<Boolean>
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 50)
    @ApiOperation(value = "修改")
    @SysLog
    @PutMapping
    fun update(@RequestBody @Validated updateDTO: JobTriggerUpdateDTO): ApiResult<Boolean> {
        // 设置默认值
        val oldGroup = if (StrUtil.isBlank(updateDTO.oldGroup)) Scheduler.DEFAULT_GROUP else updateDTO.oldGroup
        val triggerGroup = if (StrUtil.isBlank(updateDTO.triggerGroup)) Scheduler.DEFAULT_GROUP else updateDTO.triggerGroup
        val priority = if (updateDTO.priority == null) Trigger.DEFAULT_PRIORITY else updateDTO.priority

        // 构建新的触发器
        val cronTrigger = CronTriggerBuilder(
            name = updateDTO.triggerName!!,
            cron = updateDTO.cron!!,
            priority = priority!!,
            groupName = triggerGroup!!,
            description = updateDTO.triggerDescription
        )

        // 修改触发器
        quartzManager.updateCronTrigger(updateDTO.oldName!!, cronTrigger, oldGroup!!)
        return success(true)
    }

    /**
     * 单体删除
     *
     * @param deleteParam 任务操作参数（暂停、恢复、删除）
     * @return R<Boolean>
     */
    @PreCheckPermission(value = ["{}:delete", "{}:remove"], mode = PreMode.OR)
    @ApiOperationSupport(order = 60)
    @ApiOperation(value = "单体删除")
    @SysLog
    @DeleteMapping
    fun delete(@RequestBody @Validated deleteParam: JobOperationParam): ApiResult<Boolean> {
        val jobGroup = if (StrUtil.isBlank(deleteParam.jobGroup)) Scheduler.DEFAULT_GROUP else deleteParam.jobGroup
        quartzManager.deleteJob(deleteParam.jobName!!, jobGroup!!)
        return success(true)
    }

    /**
     * 暂停任务
     *
     * @param pauseParam 任务操作参数（暂停、恢复、删除）
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 80)
    @ApiOperation(value = "暂停任务")
    @SysLog
    @PostMapping("/pause")
    fun pause(@RequestBody @Validated pauseParam: JobOperationParam): ApiResult<Boolean> {
        val jobGroup = if (StrUtil.isBlank(pauseParam.jobGroup)) Scheduler.DEFAULT_GROUP else pauseParam.jobGroup
        quartzManager.pauseJob(pauseParam.jobName!!, jobGroup!!)
        return success(true)
    }

    /**
     * 恢复任务
     *
     * @param resumeParam 任务操作参数（暂停、恢复、删除）
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 90)
    @ApiOperation(value = "恢复任务")
    @SysLog
    @PostMapping("/resume")
    fun resume(@RequestBody @Validated resumeParam: JobOperationParam): ApiResult<Boolean> {
        val jobGroup = if (StrUtil.isBlank(resumeParam.jobGroup)) Scheduler.DEFAULT_GROUP else resumeParam.jobGroup
        quartzManager.resumeJob(resumeParam.jobName!!, jobGroup!!)
        return success(true)
    }

    /**
     * 立即运行一次
     *
     * @param runParam 运行一次参数
     */
    @PreCheckPermission(value = ["{}:edit", "{}:update"], mode = PreMode.OR)
    @ApiOperationSupport(order = 100)
    @ApiOperation(value = "立即运行一次", notes = "只会运行一次，方便测试时用")
    @SysLog
    @PostMapping("/runOnce")
    fun runANow(@RequestBody @Validated runParam: JobOperationParam): ApiResult<Boolean> {
        val jobGroup = if (StrUtil.isBlank(runParam.jobGroup)) Scheduler.DEFAULT_GROUP else runParam.jobGroup
        quartzManager.runAJobNow(runParam.jobName!!, jobGroup!!, null)
        return success(true)
    }

}
